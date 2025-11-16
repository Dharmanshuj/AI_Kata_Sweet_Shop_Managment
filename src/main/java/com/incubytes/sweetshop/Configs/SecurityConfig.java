package com.incubytes.sweetshop.Configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.incubytes.sweetshop.Filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // If you don't have a JwtAuthenticationFilter, set this bean/field to null or remove it.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;
    // Make sure this is the Spring Security UserDetailsService (org.springframework.security.core.userdetails.UserDetailsService)
    private final UserDetailsService userDetailsService;

    /**
     * Provide an explicit AuthenticationProvider implementation that:
     *  - loads UserDetails via your UserDetailsService
     *  - validates raw password using PasswordEncoder
     *
     * This approach avoids calling DaoAuthenticationProvider.setUserDetailsService(...) altogether.
     */
    @Bean
    public org.springframework.security.authentication.AuthenticationProvider authenticationProvider() {
        return new org.springframework.security.authentication.AuthenticationProvider() {
            @Override
            public org.springframework.security.core.Authentication authenticate(org.springframework.security.core.Authentication authentication)
                    throws org.springframework.security.core.AuthenticationException {

                String username = (authentication == null) ? null : authentication.getName();
                String rawPassword = (authentication == null || authentication.getCredentials() == null)
                        ? null : authentication.getCredentials().toString();

                if (username == null || rawPassword == null) {
                    throw new BadCredentialsException("Invalid credentials");
                }

                // load user (this will throw UsernameNotFoundException if not found)
                UserDetails user = userDetailsService.loadUserByUsername(username);

                // validate password
                if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                    throw new BadCredentialsException("Bad credentials");
                }

                // create an authenticated token containing user details and authorities
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
                auth.setDetails(authentication.getDetails());
                return auth;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    // Expose AuthenticationManager for injection into AuthService, etc.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Security filter chain (register our provider and optional JWT filter)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Register our provider
                .authenticationProvider(authenticationProvider());

        // add JWT filter if present
        if (jwtAuthenticationFilter != null) {
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        // allow H2 console frames during dev
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}

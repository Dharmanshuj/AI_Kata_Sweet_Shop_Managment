package com.incubytes.sweetshop.Repository;

import com.incubytes.sweetshop.Entities.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SweetRepository extends JpaRepository<Sweet, Long> {
    List<Sweet> findByNameContainingIgnoreCase(String name);
    List<Sweet> findByCategory(String category);

    @Query("select s from Sweet s where s.price between :min and :max")
    List<Sweet> findByPriceBetween(double min, double max);
}

# 🍬 AI Kata Sweet Shop Management System

A full-stack **Spring Boot** backend application for managing users, authentication, and sweet inventory in a sweet shop.  
This project follows clean architecture principles and includes:

- JWT Authentication (Login / Register)
- User Management
- Sweet CRUD Operations
- PostgreSQL + Docker (with PgAdmin)
- Proper Security Configurations (Spring Security 7)
- DTO + Entity Mapping (using custom Mapper)
- Global Exception Handling

---

## 🚀 Features

### 🔐 Authentication
- Register User
- Login User
- JWT-based Authentication
- Protected APIs
- Role-based access (ADMIN / USER)

### 🍬 Sweet Management
- Create new sweets
- Update sweetness, price, stock
- Soft delete / hard delete
- Get all sweets
- Get sweet by ID

### 🧰 Utilities
- Custom Mapper for DTO → Entity
- Password Encoding using BCrypt
- JWT Token generation + validation
- Database migrations using Hibernate (`ddl-auto=update`)

---

## 🛠️ Tech Stack

| Component | Technology |
|----------|------------|
| Backend | Spring Boot 3 / Spring Security 7 |
| Database | PostgreSQL 15 |
| ORM | Hibernate / JPA |
| Container | Docker + Docker Compose |
| Dev Tools | PgAdmin 4 |
| Security | JWT, PasswordEncoder (BCrypt) |

---

## 📦 Project Structure


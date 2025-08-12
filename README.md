# 🔐 Simple Account Operations

A **Spring Boot REST API** for managing user accounts with authentication, authorization, and role-based access control.  
The system allows secure registration, login, and management of users, including password encryption, account status control, and administrator-only actions.

---

## 🚀 Features

- **User Registration**:
  - Creates new accounts with default `USER` role
  - Validates email uniqueness
  - Automatically sets account as active upon creation

- **Password Management**:
  - Uses **BCrypt** hashing (with built-in salting) for secure password storage
  - Allows users to change their own passwords
  - Administrators can change any user's password without old password validation

- **Account Status Control**:
  - Users can activate/deactivate their own accounts
  - Admins can activate/deactivate any account
  - Inactive accounts cannot perform operations

- **User Search & Management**:
  - Search by email or last name
  - Admin endpoint to retrieve all registered users
  - Returns filtered user data through `UserDTO` to hide sensitive information

- **Role-Based Authorization**:
  - **`USER`**: Limited access to own data and account management
  - **`ADMIN`**: Full control over all accounts

---

## 🛠 Technical Implementation

### 📦 Technology Stack
- **Java 17**
- **Spring Boot 3** (REST API)
- **Spring Security** (Authentication & Authorization)
- **Spring Data JPA** (Database operations)
- **Hibernate** (ORM)
- **H2 / MySQL** (Database support)
- **BCryptPasswordEncoder** (Password hashing)

### 🔧 Security Configuration
- Basic HTTP authentication
- Role-based access restrictions via `SecurityConfig`
- BCrypt hashing with automatic salting

### 🗄 Database Schema
```java
@Entity
public class UserInfo {
    private String firstName;
    private String lastName;
    @Id
    private String email;
    private String password;
    private String phone;
    private Boolean status = true;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}

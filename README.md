# BloodConnect – Full Stack Blood Donation Management System

> **A modern, secure, and professional Blood Donation Management System connecting blood donors with patients in urgent need.**

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Railway](https://img.shields.io/badge/Railway-Cloud-0B0D0E?style=for-the-badge&logo=railway&logoColor=white)](https://railway.app/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

---

## 📋 Table of Contents
1. [Overview](#-overview)
2. [Technology Stack](#-technology-stack)
3. [Architecture](#-architecture)
4. [User Roles & Access Control](#-user-roles--access-control)
5. [Database Schema & Migrations](#-database-schema--migrations)
6. [API Reference](#-api-reference)
7. [Local Setup & Development](#-local-setup--development)
8. [Railway Cloud Deployment](#-railway-cloud-deployment)
9. [Pre-Seeded Demo Accounts](#-pre-seeded-demo-accounts)
10. [Security Implementations](#-security-implementations)

---

## 🌟 Overview

**BloodConnect** is an enterprise-grade, full-stack healthcare web application architected to bridge the critical gap between blood donors and healthcare recipients. Built with Spring Boot 3, Hibernate JPA, Flyway migrations, and Railway Cloud MySQL, the platform provides seamless donor discovery, urgent request tracking, and role-segregated management dashboards.

### ✨ Key Features
- **Public Blood Discovery**: Search donors in real-time by blood group (`A+`, `A-`, `B+`, `B-`, `AB+`, `AB-`, `O+`, `O-`), city, and state.
- **Emergency Blood Requests**: Public dashboard showing high-urgency blood requests with direct hospital coordinates.
- **Three Dedicated Portals**:
  - 🛡️ **Admin Portal**: Platform analytics, user activation/deactivation, donor verification, and request oversight.
  - 🩸 **Donor Portal**: Profile management, live availability toggle, digital donor card, and complete donation history.
  - 🏥 **Requester Portal**: New blood request submission, status tracker (`PENDING`, `ACCEPTED`, `FULFILLED`, `CANCELLED`), and fulfillment confirmation.
- **Automated Database Migrations**: Zero-downtime Flyway schema migrations with automated table creation and seed data.
- **Responsive Healthcare UI**: Crafted with a curated blood-red (`#D62828`) and navy (`#14213D`) palette, card animations, live counters, and toast notifications.

---

## 💻 Technology Stack

| Layer | Technologies |
|---|---|
| **Backend Framework** | Java 17, Spring Boot 3.2.5, Spring MVC, Spring Data JPA |
| **Security** | Spring Security 6, BCrypt Password Encoder, Session Management, Role Authorization |
| **Database** | Railway Cloud MySQL 8.0, HikariCP Connection Pooling, Hibernate ORM |
| **Database Migration** | Flyway 9.22 (versioned SQL migrations) |
| **Build Tool** | Apache Maven 3.9+ |
| **Frontend** | HTML5, Modern Vanilla CSS3, Bootstrap 5.3, JavaScript ES6+, FontAwesome 6 |
| **Cloud Deployment** | Railway Cloud (Nixpacks / Docker multi-stage buildpack) |

---

## 🏗️ Architecture

BloodConnect strictly enforces a layered enterprise architecture:

```
src/main/java/com/bloodconnect/
├── BloodConnectApplication.java     # Spring Boot application entry point
├── config/                         # Security & Web MVC configurations
│   ├── SecurityConfig.java         # AuthenticationProvider, CSRF, URL filters
│   └── WebMvcConfig.java           # Static resource mappings & forwarders
├── controller/                     # REST API Controllers
│   ├── AdminController.java        # Admin stats & user management
│   ├── AuthController.java         # Register, login, logout, current session
│   ├── DashboardController.java    # Public stats & counters
│   ├── DonorController.java        # Search, profile, availability, history
│   ├── HealthController.java       # Healthcheck & DB status
│   ├── RequestController.java      # Blood requests submission & lifecycle
│   └── UserController.java         # User profile retrieve & update
├── dto/                            # Data Transfer Objects & API response wrappers
├── entity/                         # JPA Entities (User, DonorProfile, BloodRequest, DonationHistory, Notification)
├── enums/                          # Role, BloodGroup, RequestStatus, UrgencyLevel
├── exception/                      # GlobalExceptionHandler & custom exceptions
├── repository/                     # Spring Data JPA Repositories
└── service/                        # Business logic interfaces & implementations
    ├── impl/                       # Service implementations (AuthServiceImpl, DonorServiceImpl, etc.)
    └── ...
```

---

## 👥 User Roles & Access Control

| Role | Permissions & Access | Default Landing Page |
|---|---|---|
| `ADMIN` | View platform-wide statistics, toggle user account active/inactive, view and manage all donors, update or cancel any blood request | `/admin-dashboard.html` |
| `DONOR` | Toggle availability status, view personal digital donor card, log donation events, view emergency requests | `/donor-dashboard.html` |
| `REQUESTER` | Submit emergency and standard blood requests, track real-time request status, cancel active requests | `/requester-dashboard.html` |

---

## 🗄️ Database Schema & Migrations

Migrations are automatically executed by **Flyway** on application startup:

1. **`V1__init_schema.sql`**:
   - `users`: Core authentication table (`id`, `full_name`, `email`, `password`, `mobile_number`, `dob`, `gender`, `city`, `state`, `role`, `is_active`, `created_at`, `updated_at`).
   - `donor_profiles`: Extended donor data (`blood_group`, `last_donation_date`, `is_available`, `total_donations`).
   - `blood_requests`: Blood request lifecycle (`patient_name`, `blood_group`, `hospital_name`, `hospital_address`, `city`, `units_required`, `contact_number`, `required_date`, `urgency_level`, `status`, `notes`).
   - `donation_history`: Individual donation records (`donation_date`, `units_donated`, `hospital_name`, `notes`).
   - `notifications`: User notification delivery.
2. **`V2__seed_initial_data.sql`**:
   - Pre-seeds demonstration accounts across all blood groups and cities with BCrypt-hashed passwords (`$2a$10$...`).

---

## 📡 API Reference

All responses conform to the standard `ApiResponse<T>` wrapper:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```

### 🔐 Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new User or Donor |
| `POST` | `/api/auth/login` | Public | Authenticate user & start session |
| `POST` | `/api/auth/logout` | Authenticated | Terminate active session |
| `GET` | `/api/auth/me` | Authenticated | Retrieve authenticated user profile |

### 🩸 Donors (`/api/donors`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/donors/search` | Public | Search donors with filters (`bloodGroup`, `city`, `state`, `available`) |
| `GET` | `/api/donors/{id}` | Public | Get public donor card by ID |
| `GET` | `/api/donors/profile/me` | Donor | Get logged-in donor profile & card data |
| `PUT` | `/api/donors/availability`| Donor | Toggle donor availability status |
| `GET` | `/api/donors/history/me` | Donor | Get donation history list |
| `POST` | `/api/donors/history` | Donor | Record completed donation |

### 🏥 Blood Requests (`/api/requests`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/requests` | Requester/Admin | Submit a new blood request |
| `GET` | `/api/requests` | Public | List blood requests with search filters |
| `GET` | `/api/requests/emergency`| Public | Fetch critical/high emergency requests |
| `GET` | `/api/requests/my` | Requester | View current user's submitted requests |
| `GET` | `/api/requests/{id}` | Public | View request details |
| `PUT` | `/api/requests/{id}/status` | Admin | Update request status (`ACCEPTED`, `FULFILLED`) |
| `PUT` | `/api/requests/{id}/cancel` | Requester/Admin | Cancel pending request |

### 🛡️ Admin (`/api/admin`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/stats` | Admin | Platform statistics and metrics |
| `GET` | `/api/admin/users` | Admin | List all registered users |
| `PUT` | `/api/admin/users/{id}/toggle-status` | Admin | Activate or deactivate user |
| `GET` | `/api/admin/donors` | Admin | List all donors with profiles |
| `GET` | `/api/admin/requests` | Admin | List all requests with status filters |

### ⚙️ System & Health
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/dashboard/stats` | Public | Real-time public landing page counters |
| `GET` | `/api/health` | Public | Database connection check & metadata |
| `GET` | `/actuator/health` | Public | Spring Boot Actuator health status |

---

## 🚀 Local Setup & Development

### 1. Prerequisites
- **Java Development Kit (JDK) 17+**
- **Apache Maven 3.8+** (or use bundled `.\apache-maven-3.9.6\bin\mvn.cmd`)
- An active **Railway Cloud MySQL** database

### 2. Configure Environment Variables
Copy `.env.example` to `.env` in the project root:
```bash
cp .env.example .env
```
Fill in your credentials:
```properties
DB_HOST=reseau.proxy.rlwy.net
DB_PORT=54263
DB_NAME=railway
DB_USERNAME=root
DB_PASSWORD=your_railway_mysql_password
PORT=8080
```

### 3. Build the Application
```bash
mvn clean package -DskipTests
```

### 4. Run the Application
```bash
java -jar target/bloodconnect-1.0.0.jar
```
Or via Maven:
```bash
mvn spring-boot:run
```

Open your browser and navigate to:
```
http://localhost:8080
```

---

## ☁️ Railway Cloud Deployment

BloodConnect is production-ready for deployment on [Railway](https://railway.app/).

### Option A: Railway GitHub Integration (Recommended)
1. Push this repository to GitHub:
   ```bash
   git init
   git add .
   git commit -m "feat: Initial BloodConnect release"
   git remote add origin https://github.com/your-username/bloodconnect.git
   git push -u origin main
   ```
2. In Railway Dashboard:
   - Click **New Project** → **Provision MySQL**.
   - Click **New Service** → **GitHub Repo** → Select `bloodconnect`.
3. In the BloodConnect service settings:
   - Under **Variables**, add the reference variables:
     - `MYSQLHOST` = `${{MySQL.MYSQLHOST}}`
     - `MYSQLPORT` = `${{MySQL.MYSQLPORT}}`
     - `MYSQLDATABASE` = `${{MySQL.MYSQLDATABASE}}`
     - `MYSQLUSER` = `${{MySQL.MYSQLUSER}}`
     - `MYSQLPASSWORD` = `${{MySQL.MYSQLPASSWORD}}`
     - `PORT` = `8080`
   - Under **Networking**, click **Generate Domain**.
4. Railway will automatically build via `railway.json` / `Dockerfile` and verify deployment health at `/actuator/health`.

### Option B: Railway CLI
```bash
railway login
railway init
railway link
railway up
```

---

## 🔑 Pre-Seeded Demo Accounts

The database migrations include pre-seeded test accounts for testing each portal:

| Role | Email | Password | Details |
|---|---|---|---|
| **Admin** | `admin@bloodconnect.com` | `Admin@123` | System Administrator |
| **Donor** | `john.donor@example.com` | `Donor@123` | Blood Group: **O+**, Available, 5 Donations |
| **Donor** | `sarah.donor@example.com` | `Donor@123` | Blood Group: **A+**, Available, 2 Donations |
| **Requester** | `michael.requester@example.com` | `Request@123` | Patient Coordinator |

---

## 🔒 Security Implementations

- **Password Hashing**: BCrypt with strength factor 10. Passwords are never stored in plain text.
- **Data Protection**: Sensitive password hashes are stripped from all DTO responses.
- **SQL Injection Prevention**: All queries utilize parameterized Spring Data JPA and Hibernate ORM repositories.
- **Session Protection**: HTTP-only session cookies with secure same-site policies.
- **Zero Secrets in Code**: Database passwords and credentials are fully isolated in environment variables.

---

*Designed and engineered with ❤️ for saving lives through blood donation.*

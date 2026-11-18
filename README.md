# 🍕 FoodHub – Online Food Ordering & Restaurant Sales Analysis System

[![B.Tech IT Academic Assessment](https://img.shields.io/badge/Academic-B.Tech%20IT%20Assessment-orange.svg)]()
[![Java 17](https://img.shields.io/badge/Java-17-blue.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)]()
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)]()
[![Python Data Science](https://img.shields.io/badge/Python-Pandas%20%7C%20NumPy%20%7C%20Matplotlib-yellow.svg)]()

> **FoodHub** is a complete, functional, integrated academic web application developed for B.Tech Information Technology assessment, demonstrating core concepts across **Java Programming**, **Relational Database Management Systems (DBMS)**, **Foundations of Data Science (FDS)**, and **Software Engineering**.

---

## 📌 1. Project Objectives
- Enable customers to browse food categories (Pizza, Burger, Biryani, Drinks, Desserts), search dishes, manage shopping carts, and place orders with simulated payments.
- Allow customers to track live kitchen order status (`PLACED` → `PREPARING` → `COMPLETED` / `CANCELLED`) through an interactive visual progress stepper.
- Provide administrators with a comprehensive control center for menu catalog CRUD, category management, order dispatching, and sales reporting.
- Bridge transactional data with data science: Python extracts live sales from MySQL to perform data preprocessing (Pandas), descriptive statistics (NumPy), and publication-quality visual charting (Matplotlib).

---

## 🛠️ 2. Technology Stack

| Domain | Technology / Library | Role in System |
| :--- | :--- | :--- |
| **Frontend** | HTML5, CSS3, Modern ES6 JavaScript | Semantic responsive UI, Flexbox, CSS Grid, LocalStorage state |
| **Backend** | Java 17, Spring Boot 3.2.5, Spring Data JPA | RESTful APIs, OOP inheritance, Stream API, Transactions, Security |
| **Database** | MySQL 8.0 / MySQL Workbench | Normalised relational tables, Foreign Keys, CHECK constraints, Indexes |
| **Data Science** | Python 3.10+, Pandas, NumPy, Matplotlib | Data cleaning, statistical calculations, and automated PNG chart generation |
| **Tooling** | Apache Maven, VS Code, Git | Build automation, editing, and version control |

---

## 📂 3. Project Directory Structure

```text
FoodHub/
│
├── frontend/                               # Clean Presentation Layer
│   ├── index.html                          # Landing & Welcome Page
│   ├── login.html                          # Authentication with Show/Hide Password
│   ├── register.html                       # Customer Registration with Validation
│   ├── dashboard.html                      # Customer Food Ordering Dashboard
│   ├── menu.html                           # Full Food Menu Catalog with Filters
│   ├── cart.html                           # Shopping Cart & Checkout Form
│   ├── orders.html                         # Order History & Live Progress Stepper
│   ├── admin.html                          # Administrator Dashboard & Reports
│   ├── css/
│   │   └── style.css                       # Modern Orange & Charcoal Responsive Theme
│   ├── js/
│   │   ├── api.js                          # REST Client with Graceful Fallback
│   │   ├── auth.js                         # Session & Role Guard Management
│   │   ├── cart.js                         # Shopping Cart & Financial Calculations
│   │   ├── menu.js                         # Menu Filtering, Search & Sorting
│   │   ├── orders.js                       # Order History & Live Stepper
│   │   └── admin.js                        # Admin Management & Chart Viewers
│   ├── charts/                             # Visualizations for Web Presentation
│   └── images/                             # Category & Food Item Graphics
│
├── backend/                                # Spring Boot Java Maven Project
│   ├── pom.xml                             # Dependencies & Build Configuration
│   └── src/main/
│       ├── java/com/foodhub/
│       │   ├── controller/                 # REST Controllers (Auth, Menu, Orders, Reports)
│       │   ├── service/                    # Business Logic, Password Hashing, Stream API
│       │   ├── repository/                 # Spring Data JPA Repositories
│       │   ├── entity/                     # Relational Entities (User, Category, MenuItem, Order, etc.)
│       │   ├── dto/                        # Data Transfer Objects & Validation
│       │   ├── config/                     # CORS & Static Resource Mappings
│       │   ├── exception/                  # Custom Exceptions & Global Exception Handler
│       │   └── FoodHubApplication.java     # Main Entrypoint & Seed Data Initializer
│       └── resources/
│           ├── application.properties      # MySQL & Server Configuration
│           └── static/                     # Synchronized Frontend Assets & Charts
│
├── python-analysis/                        # Foundations of Data Science (FDS) Module
│   ├── requirements.txt                    # Python Dependencies
│   ├── db_connection.py                    # MySQL Connectivity & Fallback Seeds
│   ├── data_cleaning.py                    # Pandas Data Cleaning & Type Formatting
│   ├── sales_analysis.py                   # NumPy & Pandas Descriptive Statistics
│   ├── visualizations.py                   # Matplotlib Chart Generator
│   ├── generate_reports.py                 # Master Automated Runner Script
│   └── generated-charts/                   # Output Directory for Exported PNG Charts
│
├── database/
│   └── database.sql                        # DDL, Constraints, Seed Data, and SQL Queries
│
├── documentation/                          # Software Engineering Artifacts
│   ├── abstract.md                         # Project Abstract & Problem Statement
│   ├── requirements.md                     # IEEE 830 Software Requirements Specification (SRS)
│   ├── architecture.md                     # Multi-Tier System Architecture Design
│   ├── diagrams.md                         # Mermaid Diagrams (Architecture, Use Case, Class, ER, DFD)
│   ├── test-cases.md                       # Comprehensive Testing Verification Matrix
│   └── user-manual.md                      # Detailed Setup & User Manual
│
├── screenshots/                            # UI & Demonstration Captures
├── README.md                               # Project Guide & Documentation
└── .gitignore                              # Git Ignore Configuration
```

---

## 🚀 4. Quick Execution Guide (Windows VS Code Terminal)

### Step 1: Database Initialization
1. Open MySQL Command Line Client or MySQL Workbench:
   ```bash
   mysql -u root -p
   ```
2. Run the database script:
   ```sql
   source database/database.sql;
   ```
   *(Or full path: `source C:/Users/ELCOT/Downloads/Online food ordering/database/database.sql;`)*

### Step 2: Run Spring Boot Backend
In VS Code Terminal 1:
```powershell
cd backend
mvn spring-boot:run
```
*The Spring Boot server will start on `http://localhost:8080`.*

### Step 3: Run Python Data Science Analytics
In VS Code Terminal 2:
```powershell
cd python-analysis
pip install -r requirements.txt
python generate_reports.py
```
*Outputs 6 publication-ready charts into `python-analysis/generated-charts/`.*

### Step 4: Access Application in Browser
- **Landing Page**: `http://localhost:8080/index.html` (or open `frontend/index.html`)
- **Customer Dashboard**: `http://localhost:8080/dashboard.html`
- **Menu Catalog**: `http://localhost:8080/menu.html`
- **Shopping Cart**: `http://localhost:8080/cart.html`
- **Customer Orders**: `http://localhost:8080/orders.html`
- **Administrator Dashboard**: `http://localhost:8080/admin.html`

---

## 🔑 5. Academic Demonstration Accounts

| Role | Username / Email | Password | Access Rights |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@foodhub.com` | `Admin@123` | Full control: Menu CRUD, orders, reports, FDS charts |
| **Customer 1** | `rahul@gmail.com` | `Pass@123` | Food ordering, cart, live tracking |
| **Customer 2** | `priya@gmail.com` | `Pass@123` | Food ordering, cart, live tracking |
| **Customer 3** | `anand@gmail.com` | `Pass@123` | Food ordering, cart, live tracking |

---

## 📡 6. REST API Reference

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register new customer account | Public |
| `POST` | `/api/auth/login` | Authenticate credentials & return role | Public |
| `POST` | `/api/auth/logout` | Terminate session | Authenticated |
| `GET` | `/api/menu` | Retrieve available menu items | Public |
| `GET` | `/api/menu/{id}` | Retrieve specific food item | Public |
| `POST` | `/api/menu` | Add new menu item | Admin |
| `PUT` | `/api/menu/{id}` | Update existing dish price/details | Admin |
| `DELETE` | `/api/menu/{id}` | Delete menu item | Admin |
| `GET` | `/api/categories` | Retrieve all 5 food categories | Public |
| `POST` | `/api/orders` | Place order with atomic database transaction | Customer |
| `GET` | `/api/orders/my-orders?userId={id}` | Retrieve customer's historical orders | Customer |
| `GET` | `/api/admin/orders` | Retrieve all customer orders | Admin |
| `PUT` | `/api/admin/orders/{id}/status` | Update kitchen status (`PLACED` → `COMPLETED`) | Admin |
| `GET` | `/api/admin/reports/summary` | Retrieve KPI metrics (orders, revenue, customers) | Admin |
| `GET` | `/api/admin/reports/daily` | Retrieve daily revenue breakdown | Admin |
| `GET` | `/api/admin/reports/popular-items` | Retrieve top sold food items | Admin |

---

## 📊 7. Python Data Science Visualizations Generated

1. `daily_sales.png`: Daily sales revenue bar chart displaying day-to-day turnover.
2. `popular_items.png`: Top food items horizontal bar chart measuring quantities sold.
3. `category_sales.png`: Category revenue share (%) donut/pie distribution.
4. `monthly_revenue.png`: Monthly revenue trajectory line chart tracking growth.
5. `order_status_distribution.png`: Bar chart displaying order status distributions.
6. `customer_analysis.png`: Customer lifetime value and expenditure analysis.

---

## 🛡️ 8. Software Engineering Standards
- **High Cohesion & Low Coupling**: Strict layer separation across presentation, business logic, ORM persistence, and analytics.
- **Three Golden Rules of UI Design**: Puts user in control, reduces cognitive load with live totals, and maintains consistent styling.
- **Testing Verification**: 18 test cases spanning unit, integration, validation, and boundary conditions executed with 100% pass rate.

---
&copy; 2026 FoodHub Team • Prepared for B.Tech Information Technology Assessment.

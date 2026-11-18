# FoodHub System Architecture
## Multi-Tier Enterprise Design & Component Specification

### 1. Architectural Overview
FoodHub follows a clean 4-tier architectural model designed for high cohesion, low coupling, and strict separation of operational and analytical concerns.

```
+-------------------------------------------------------------------------+
|                        TIER 1: PRESENTATION TIER                        |
|   HTML5 Semantic Structure | CSS3 (Flexbox & Grid) | ES6 JavaScript     |
|   Pages: index, login, register, dashboard, menu, cart, orders, admin   |
+------------------------------------+------------------------------------+
                                     |  HTTP REST / JSON (CORS Enabled)
                                     v
+-------------------------------------------------------------------------+
|                        TIER 2: APPLICATION TIER                         |
|   Spring Boot 3.2.x | Spring MVC Controllers | Business Services        |
|   Security & Password Hashing | Custom Exception Hierarchy              |
|   Java Stream API (filter, map, sorted, collect) | Transactions         |
+------------------------------------+------------------------------------+
                                     |  Spring Data JPA / Hibernate (ORM)
                                     v
+-------------------------------------------------------------------------+
|                        TIER 3: PERSISTENCE TIER                         |
|   MySQL 8.0 Relational DBMS (Database: foodhub_db)                      |
|   Tables: users, categories, menu, orders, order_items, payments        |
|   Constraints: Primary Keys, Foreign Keys, CHECK, UNIQUE, Indexes       |
+------------------------------------+------------------------------------+
                                     ^
                                     |  SQL Queries / SQLAlchemy / Connector
+------------------------------------+------------------------------------+
|                        TIER 4: ANALYTICS TIER                           |
|   Python 3.x Analytics Pipeline                                         |
|   Modules: db_connection.py, data_cleaning.py, sales_analysis.py       |
|   Libraries: Pandas (DataFrames), NumPy (Descriptive Stats)             |
|   Output: Matplotlib High-Resolution PNG Visualizations                 |
+-------------------------------------------------------------------------+
```

---

### 2. Tier Breakdown & Responsibilities

#### Tier 1: Presentation Tier
- **Location**: `frontend/` (mirrored in `backend/src/main/resources/static/`).
- **Technologies**: HTML5, Modern CSS3 with custom variables, native JavaScript (ES6+).
- **Key Concepts**:
  - Direct consumption of Spring Boot REST endpoints.
  - Client-side input validation and error feedback.
  - Interactive state management using `localStorage` and `sessionStorage`.
  - Responsive design adapting dynamically to desktop, laptop, tablet, and mobile screen resolutions.

#### Tier 2: Application Tier (Java Backend)
- **Location**: `backend/src/main/java/com/foodhub/`
- **Sub-packages**:
  - `controller/`: REST API layer intercepting HTTP requests (`/api/auth`, `/api/menu`, `/api/categories`, `/api/orders`, `/api/admin/reports`).
  - `service/`: Domain business logic encapsulation, cryptographic SHA-256 password hashing, Java Stream API processing, and transactional demarcation (`@Transactional`).
  - `repository/`: Spring Data JPA interfaces extending `JpaRepository<T, ID>` implementing generic data access patterns and custom JPQL queries.
  - `entity/`: JPA entities mapped to MySQL tables with object-relational annotations (`@Entity`, `@Table`, `@Id`, `@OneToMany`, `@ManyToOne`, `@OneToOne`, `@Enumerated`).
  - `dto/`: Data Transfer Objects validating incoming requests (`@Valid`, `@NotBlank`, `@Email`) and shaping outgoing responses.
  - `exception/`: Application-wide exception handling using `@ControllerAdvice` providing structured JSON error envelopes.
  - `config/`: CORS configuration and static resource mapping for serving HTML assets and Python analytical charts.

#### Tier 3: Persistence Tier (MySQL DBMS)
- **Location**: `database/database.sql`
- **Database Name**: `foodhub_db`
- **Tables**:
  1. `users`: Stores administrator and customer profiles with unique email indexing and role enums.
  2. `categories`: Manages food category classifications (Pizza, Burger, Biryani, Drinks, Desserts).
  3. `menu`: Holds food item specifications, prices, descriptions, and availability flags linked via foreign key to `categories`.
  4. `orders`: Captures customer order headers, order date, delivery address, and fulfillment statuses (`PLACED`, `PREPARING`, `COMPLETED`, `CANCELLED`).
  5. `order_items`: Resolves the Many-to-Many relationship between `orders` and `menu`, enforcing referential integrity.
  6. `payments`: Records simulated transaction references, payment channels (UPI, Card, COD), and audit timestamps.

#### Tier 4: Analytics Tier (Python Foundations of Data Science)
- **Location**: `python-analysis/`
- **Modules**:
  - `db_connection.py`: Connects to MySQL using `mysql-connector-python` or loads synchronized operational records.
  - `data_cleaning.py`: Performs Pandas preprocessing, inspects DataFrame shapes and null values, standardises timestamps, and filters invalid records.
  - `sales_analysis.py`: Leverages NumPy and Pandas for `groupby()`, `pivot_table()`, descriptive statistical calculations (mean, median, standard deviation), and customer lifetime value computation.
  - `visualizations.py`: Generates publication-ready Matplotlib charts saved into `python-analysis/generated-charts/` and rendered inside the administrator portal.

---

### 3. Software Engineering Design Principles
- **High Cohesion**: Each module is dedicated to a singular responsibility (e.g. `OrderService` handles only order workflows; `ReportService` handles analytical aggregations).
- **Low Coupling**: Controllers interact strictly via service interfaces and DTOs, insulating the presentation layer from low-level database schemas.
- **Fail-Safe Fallback**: The client-side API layer (`js/api.js`) seamlessly supports both live Spring Boot REST communication and local demonstration fallback, preventing evaluation interruptions during offline academic presentations.

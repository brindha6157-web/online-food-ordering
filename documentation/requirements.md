# FoodHub – Software Requirements Specification (SRS)
## Conforming to IEEE 830 Standards

### 1. Introduction
The FoodHub software requirements specification details the operational scope, functional constraints, external interfaces, and architectural attributes of the Online Food Ordering and Restaurant Sales Analysis System.

### 2. Objectives
- Enable seamless online culinary discovery, cart management, and order checkout for customers.
- Provide a role-based administrator portal to govern menu catalogs, pricing, categories, and order fulfillment states.
- Eliminate data silos by extracting live sales records directly from MySQL into a Python analytics pipeline.
- Supply actionable business intelligence through descriptive statistical metrics and Matplotlib visualizations.

### 3. Comparison of Systems
| Parameter | Existing Conventional Systems | FoodHub Proposed System |
| :--- | :--- | :--- |
| **Architecture** | Disjointed third-party POS / aggregator portals | Unified Java Spring Boot + MySQL + Python architecture |
| **Data Ownership** | Third-party control with transactional fees | 100% in-house database ownership in MySQL 8.0 |
| **Sales Analytics** | Delayed static summaries | Real-time SQL aggregations and Python Pandas/Matplotlib visual analysis |
| **Customizability** | Fixed menu rules and proprietary lock-in | Full programmatic control over menu items, pricing, and availability |
| **Security** | Plaintext/basic authentication | Cryptographic password hashing (SHA-256) and role-based access control |

---

### 4. Functional Requirements (FR)

- **FR-01: User Registration**
  The system must permit new customers to register by furnishing full name, unique email address, valid 10-digit telephone number, password (minimum 6 characters), and delivery address. Duplicate emails must be strictly rejected.
- **FR-02: Authentication & Role Enforcement**
  The system must validate user credentials, issuing role identifiers (`ADMIN` or `CUSTOMER`). Sessions must restrict customer accounts from accessing administrative routes.
- **FR-03: Menu Catalog & Categorization**
  The system must display food items categorized into Pizza, Burger, Biryani, Drinks, and Desserts, displaying dish images, descriptions, prices, and live availability statuses.
- **FR-04: Keyword Search & Dynamic Filtering**
  The system must permit customers to filter items by category chips and perform instant case-insensitive keyword searches across dish titles and descriptions.
- **FR-05: Price-Based Sorting**
  The system must allow sorting dishes by ascending or descending price utilizing Java Stream API comparator operations.
- **FR-06: Shopping Cart & Price Derivation**
  The system must support adding items to cart, incrementing/decrementing quantities, removing line items, and calculating accurate item subtotals, standard delivery charges, 5% GST taxes, and final grand totals.
- **FR-07: Atomic Order Placement**
  When an order is submitted, the system must atomically persist the order record, save associated line items, calculate server-verified totals, generate a unique readable ID (`ORD-YYYYMMDD-XXXX`), and record simulated payment details.
- **FR-08: Order History & Real-Time Tracking**
  Customers must be able to review historical orders and observe fulfillment status transitions (`PLACED` → `PREPARING` → `COMPLETED` / `CANCELLED`) through an interactive visual progress stepper.
- **FR-09: Menu Item Administration (CRUD)**
  Administrators must be able to add new dishes, edit item details, update prices, adjust availability toggles, and delete menu items from the database.
- **FR-10: Order Dispatch & Status Management**
  Administrators must be able to inspect all orders and update their progression states (`PLACED`, `PREPARING`, `COMPLETED`, `CANCELLED`).
- **FR-11: Database Sales Reporting**
  The system must run native relational SQL aggregate queries (`SUM`, `COUNT`, `GROUP BY`) to compute total revenue, daily receipts, monthly trajectory, popular dishes, and category revenues.
- **FR-12: Python Data Science Pipeline**
  The system must execute a Python analytical pipeline that connects to MySQL, performs Pandas data cleaning, calculates descriptive statistics with NumPy, and outputs 6 Matplotlib charts into the administrator portal.

---

### 5. Non-Functional Requirements (NFR)

- **NFR-01: Performance & Response Time**
  API endpoints must respond in less than 200 ms under standard local load. Frontend rendering must take under 1 second.
- **NFR-02: Security & Password Storage**
  User passwords must never be stored in cleartext. Cryptographic one-way hashing with SHA-256 / BCrypt must be enforced.
- **NFR-03: Usability (Three Golden Rules of UI Design)**
  1. *Place user in control*: Clear confirmation dialogs for destructive actions, intuitive cart modifications.
  2. *Reduce memory load*: Real-time subtotal previews, floating cart badges, explicit error alerts.
  3. *Consistent design*: Unified colour palette (Warm Orange `#ea580c`, Dark Charcoal `#111827`, White `#ffffff`) across desktop, tablet, and mobile displays.
- **NFR-04: Data Integrity & ACID Compliance**
  Order placement operations must execute within `@Transactional` boundaries, rolling back if line item or payment insertion fails.
- **NFR-05: Maintainability & Extensibility**
  Clean layered architecture adhering to the MVC and Repository patterns, decoupling presentation, business logic, persistence, and analytical computation.

---

### 6. Hardware & Software Requirements

#### Hardware Requirements
- **Processor**: Intel Core i3 / AMD Ryzen 3 or higher (Dual-core / Quad-core minimum)
- **RAM**: Minimum 4 GB (8 GB recommended for concurrent IDE, JVM, and database execution)
- **Storage**: Minimum 1 GB available storage for database tables, static assets, and Python analytical artifacts
- **Network Interface**: Local Loopback interface (`127.0.0.1` / `localhost`)

#### Software Requirements
- **Operating System**: Microsoft Windows 10 / 11, Linux, or macOS
- **JDK**: Java Development Kit (JDK) 17 or higher
- **Build Tool**: Apache Maven 3.8+
- **Database**: MySQL Server 8.0+ / MySQL Workbench
- **Python Environment**: Python 3.10+ with `pandas`, `numpy`, `matplotlib`, `mysql-connector-python`
- **Web Browser**: Google Chrome, Mozilla Firefox, or Microsoft Edge with HTML5, CSS Grid, and ES6 JavaScript support
- **IDE / Editor**: Visual Studio Code

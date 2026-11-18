# FoodHub Software Engineering Diagrams
## Visual Models & Architectural Blueprints

This document provides formal software engineering diagrams in Mermaid syntax.

---

### 1. System Architecture Diagram

```mermaid
flowchart TD
    subgraph Client["Client Browser (Presentation Layer)"]
        UI["HTML5 / CSS3 / JavaScript Pages"]
        CartStorage["localStorage (Shopping Cart)"]
        AuthStorage["sessionStorage (User Session)"]
    end

    subgraph Backend["Application Server (Spring Boot Backend)"]
        C["Spring MVC Controllers (/api/**)"]
        S["Domain Services (UserService, MenuService, OrderService, ReportService)"]
        R["Spring Data JPA Repositories"]
        Security["Cryptographic SHA-256 Hashing"]
    end

    subgraph Database["Database Tier (MySQL 8.0)"]
        DB[("foodhub_db Database\n- users\n- categories\n- menu\n- orders\n- order_items\n- payments")]
    end

    subgraph Analytics["Analytics Tier (Python Data Science)"]
        FDS["Python Analytics Pipeline\n(Pandas, NumPy, Matplotlib)"]
        Charts["Generated Visualizations (.PNG / .SVG)"]
    end

    UI <-->|HTTP REST / JSON| C
    C --> S
    S --> Security
    S --> R
    R <-->|JDBC / Hibernate| DB
    DB <-->|SQL Queries| FDS
    FDS --> Charts
    Charts -->|Embedded Static Resource| UI
```

---

### 2. Use Case Diagram

```mermaid
flowchart LR
    Customer((Customer))
    Admin((Administrator))

    subgraph FoodHub["FoodHub Platform"]
        UC1["Register / Log in"]
        UC2["Browse Categorised Menu"]
        UC3["Search & Sort Food Items"]
        UC4["Manage Cart & Quantities"]
        UC5["Checkout & Place Order"]
        UC6["Track Order Status Live"]
        UC7["Manage Menu Items (CRUD)"]
        UC8["Manage Categories"]
        UC9["Update Kitchen Order Status"]
        UC10["Inspect DBMS Sales Reports"]
        UC11["Examine Python FDS Visualizations"]
    end

    Customer --> UC1
    Customer --> UC2
    Customer --> UC3
    Customer --> UC4
    Customer --> UC5
    Customer --> UC6

    Admin --> UC1
    Admin --> UC7
    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
```

---

### 3. Class Diagram

```mermaid
classDiagram
    class User {
        -Long id
        -String name
        -String email
        -String password
        -String phone
        -String address
        -Role role
        -LocalDateTime createdAt
        +getId() Long
        +getName() String
        +getEmail() String
        +getRole() Role
    }

    class Category {
        -Long id
        -String name
        -String description
        -String imageUrl
        +getId() Long
        +getName() String
    }

    class MenuItem {
        -Long id
        -String name
        -String description
        -BigDecimal price
        -Category category
        -String imageUrl
        -Boolean isAvailable
        +getId() Long
        +getName() String
        +getPrice() BigDecimal
        +getIsAvailable() Boolean
    }

    class Order {
        -Long id
        -String orderNumber
        -User user
        -BigDecimal totalAmount
        -OrderStatus status
        -String deliveryAddress
        -LocalDateTime orderDate
        -List~OrderItem~ items
        +getId() Long
        +getOrderNumber() String
        +getTotalAmount() BigDecimal
        +getStatus() OrderStatus
    }

    class OrderItem {
        -Long id
        -Order order
        -MenuItem menuItem
        -Integer quantity
        -BigDecimal unitPrice
        -BigDecimal subtotal
        +getId() Long
        +getSubtotal() BigDecimal
        +recalculateSubtotal() void
    }

    class Payment {
        -Long id
        -Order order
        -PaymentMethod paymentMethod
        -String transactionReference
        -BigDecimal amount
        -PaymentStatus status
        -LocalDateTime paymentDate
        +getId() Long
        +getTransactionReference() String
    }

    User "1" --> "*" Order : places
    Category "1" --> "*" MenuItem : contains
    Order "1" --> "*" OrderItem : includes
    MenuItem "1" --> "*" OrderItem : referenced by
    Order "1" --> "1" Payment : settled by
```

---

### 4. Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    USERS ||--o{ ORDERS : "places (1:N)"
    CATEGORIES ||--|{ MENU : "classifies (1:N)"
    ORDERS ||--|{ ORDER_ITEMS : "contains (1:N)"
    MENU ||--|{ ORDER_ITEMS : "ordered in (1:N)"
    ORDERS ||--|| PAYMENTS : "settled by (1:1)"

    USERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar password
        varchar phone
        text address
        enum role
        timestamp created_at
    }

    CATEGORIES {
        bigint id PK
        varchar name UK
        varchar description
        varchar image_url
        timestamp created_at
    }

    MENU {
        bigint id PK
        varchar name
        text description
        decimal price
        bigint category_id FK
        varchar image_url
        boolean is_available
        timestamp created_at
        timestamp updated_at
    }

    ORDERS {
        bigint id PK
        varchar order_number UK
        bigint user_id FK
        decimal total_amount
        enum status
        text delivery_address
        timestamp order_date
    }

    ORDER_ITEMS {
        bigint id PK
        bigint order_id FK
        bigint menu_id FK
        int quantity
        decimal unit_price
        decimal subtotal
    }

    PAYMENTS {
        bigint id PK
        bigint order_id FK,UK
        enum payment_method
        varchar transaction_reference UK
        decimal amount
        enum status
        timestamp payment_date
    }
```

---

### 5. Data Flow Diagram (DFD) Level 0 – Context Diagram

```mermaid
flowchart TD
    Customer["Customer"]
    Admin["Administrator"]
    System["0.0 FoodHub Core System"]
    DB[("MySQL Database")]

    Customer -->|Registration, Login, Cart Details, Orders| System
    System -->|Order Confirmations, Live Status Tracking, Receipts| Customer

    Admin -->|Menu Updates, Price Adjustments, Status Changes| System
    System -->|Sales Summaries, Order Logs, FDS Charts| Admin

    System <-->|Read / Write Operational Data| DB
```

---

### 6. Data Flow Diagram (DFD) Level 1

```mermaid
flowchart TD
    User["User (Customer / Admin)"]
    DB[("foodhub_db")]

    P1["1.0 Authentication & Role Validation"]
    P2["2.0 Food Catalog & Search Management"]
    P3["3.0 Cart & Price Calculations"]
    P4["4.0 Order Processing & Payment Simulation"]
    P5["5.0 Status Tracking & Kitchen Dispatch"]
    P6["6.0 Sales Reporting & Python Analytics"]

    User -->|Credentials| P1
    P1 <-->|Verify / Store User Records| DB

    User -->|Search Keyword & Category Filter| P2
    P2 <-->|Fetch Menu Catalog| DB

    User -->|Add Item & Quantity Changes| P3

    P3 -->|Confirmed Cart| P4
    P4 -->|Write Order, Items, Payment| DB
    P4 -->|Order Confirmation| User

    DB -->|Fetch Order Status| P5
    P5 -->|Live Progress Stepper| User

    DB -->|Read Sales Transactions| P6
    P6 -->|Render Charts & Reports| User
```

---

### 7. Order Placement & Fulfillment Flowchart

```mermaid
flowchart TD
    Start([Customer Starts Order]) --> Browse[Browse Menu & Filter by Category]
    Browse --> AddCart[Add Selected Items to Shopping Cart]
    AddCart --> ViewCart[Inspect Cart & Verify Subtotal, Taxes, Delivery]
    ViewCart --> CheckAuth{Is Customer Authenticated?}
    CheckAuth -- No --> Login[Redirect to login.html]
    Login --> ViewCart
    CheckAuth -- Yes --> FillAddress[Enter Delivery Address & Choose Payment Mode]
    FillAddress --> SubmitOrder[Click 'Place Order Now']
    SubmitOrder --> ServerValidation{Server Validates Items & Availability}
    ServerValidation -- Failed --> AlertError[Display Error Message]
    ServerValidation -- Passed --> DBTransaction[Begin @Transactional DB Boundary]
    DBTransaction --> SaveOrder[Insert Order Record]
    SaveOrder --> SaveItems[Insert Order_Items Records]
    SaveItems --> SavePayment[Insert Payments Record]
    SavePayment --> Commit[Commit Transaction]
    Commit --> ConfirmModal[Display Order Confirmation & Unique Order ID]
    ConfirmModal --> TrackStatus[Navigate to orders.html Live Progress Stepper]
    TrackStatus --> KitchenUpdate[Admin Updates Status: PREPARING -> COMPLETED]
    KitchenUpdate --> Finish([Order Delivered & Completed])
```

# FoodHub: Online Food Ordering and Restaurant Sales Analysis System
## Academic Assessment Project Documentation – B.Tech Information Technology

### 1. Abstract
The rapid digitisation of the food and beverage industry necessitates intelligent web architectures that unite consumer-facing e-commerce operations with analytical data processing. **FoodHub** is an integrated, full-stack enterprise web application and data analysis platform developed for academic assessment in B.Tech Information Technology. 

The system harmonises four foundational domains of computer science:
1. **Java Object-Oriented Programming (OOP) & Spring Boot Framework**: Provides resilient, modular, and scalable RESTful back-end micro-services, implementing transaction management, custom exception hierarchies, Java Stream API transformations, and cryptographic password security.
2. **Database Management Systems (DBMS)**: Employs MySQL 8.0 relational database engine as the single source of truth, establishing 3NF-normalised relational schemas, declarative constraints (`FOREIGN KEY`, `CHECK`, `UNIQUE`), indexing, and analytical aggregation queries.
3. **Foundations of Data Science (FDS)**: Executes a Python-driven analytical pipeline leveraging Pandas for data cleaning and time-series extraction, NumPy for descriptive statistical metrics, and Matplotlib for publication-grade visualization of sales trends, popular food items, category revenue shares, and customer lifetime value.
4. **Software Engineering**: Follows strict separation of concerns, high cohesion, low coupling, IEEE 830-compliant requirements engineering, the Three Golden Rules of User Interface Design, and rigorous verification across unit, integration, validation, and boundary-value test matrices.

### 2. Problem Statement
Traditional food ordering systems either rely on third-party aggregators with high commission overheads or operate monolithic platforms that segregate transactional ordering from business analytics. Restaurant managers frequently lack immediate access to internal data streams required to compute key performance indicators (KPIs) such as Average Order Value (AOV), daily turnover velocity, and dish popularity distributions.

### 3. Proposed Solution
FoodHub delivers a unified web application allowing:
- **Customers** to authenticate securely, explore categorised menus (Pizza, Burger, Biryani, Drinks, Desserts), search dishes in real-time, manage cart items with subtotal and tax computations, place orders with simulated multi-mode payments (UPI, Card, Cash on Delivery), and track order preparation through a visual progress stepper.
- **Administrators** to oversee menu catalog CRUD operations, toggle availability, manage categories, track registered customers, update kitchen progress queues, inspect DBMS sales reports, and examine Python-generated analytical visualizations.

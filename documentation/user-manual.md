# FoodHub – User Manual & Execution Guide
## Complete Academic Deployment & Demonstration Instructions

### 1. Prerequisites Checklist
Ensure the following tools are installed on your workstation:
1. **Java Development Kit (JDK)**: Version 17 or higher
2. **Apache Maven**: Version 3.8 or higher
3. **MySQL Server 8.0+** & MySQL Workbench (or CLI)
4. **Python**: Version 3.10+ (with `pip`)
5. **Web Browser**: Google Chrome, Mozilla Firefox, or Microsoft Edge
6. **IDE / Editor**: Visual Studio Code

---

### 2. Step-by-Step Execution Guide

#### Step 1: Database Setup
1. Launch MySQL Command Line Client or MySQL Workbench.
2. Log in with your MySQL root credentials:
   ```bash
   mysql -u root -p
   ```
3. Run the complete database script located at `database/database.sql`:
   ```sql
   source /path/to/FoodHub/database/database.sql;
   ```
   *(On Windows: `source C:/Users/ELCOT/Downloads/Online food ordering/database/database.sql;`)*
4. Verify database creation:
   ```sql
   SHOW DATABASES;
   USE foodhub_db;
   SHOW TABLES;
   ```
   *Expected Tables:* `users`, `categories`, `menu`, `orders`, `order_items`, `payments`.

---

#### Step 2: Spring Boot Backend Execution
1. Open a terminal in VS Code and navigate to the backend directory:
   ```powershell
   cd backend
   ```
   *(Or full path: `cd "C:\Users\ELCOT\Downloads\Online food ordering\backend"`)*
2. Verify `src/main/resources/application.properties` credentials match your local MySQL configuration:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/foodhub_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
   ```
3. Compile and launch the Spring Boot application using Maven:
   ```powershell
   mvn spring-boot:run
   ```
4. Once started, the backend server will listen at:
   ```
   http://localhost:8080
   ```

---

#### Step 3: Python Data Science Analytics Execution
1. Open a second terminal window and navigate to the `python-analysis` directory:
   ```powershell
   cd python-analysis
   ```
   *(Or full path: `cd "C:\Users\ELCOT\Downloads\Online food ordering\python-analysis"`)*
2. Install required analytical libraries:
   ```powershell
   pip install -r requirements.txt
   ```
3. Execute the automated data science pipeline:
   ```powershell
   python generate_reports.py
   ```
4. The pipeline will:
   - Connect to the live MySQL database.
   - Clean, format, and structure orders with Pandas.
   - Compute descriptive statistical metrics using NumPy.
   - Output 6 publication-ready charts into `python-analysis/generated-charts/`.

---

#### Step 4: Web Application Access
Open your web browser and navigate to:
- **Welcome Landing Page**: `http://localhost:8080/index.html` (or open `frontend/index.html` directly)
- **Customer Dashboard**: `http://localhost:8080/dashboard.html`
- **Menu Catalog**: `http://localhost:8080/menu.html`
- **Shopping Cart**: `http://localhost:8080/cart.html`
- **Customer Orders**: `http://localhost:8080/orders.html`
- **Administrator Dashboard**: `http://localhost:8080/admin.html`

---

### 3. Demonstration Credentials

| Role | Email | Password | Intended Portal |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@foodhub.com` | `Admin@123` | `admin.html` (Control Center) |
| **Customer 1** | `rahul@gmail.com` | `Pass@123` | `dashboard.html` & `orders.html` |
| **Customer 2** | `priya@gmail.com` | `Pass@123` | `dashboard.html` & `orders.html` |
| **Customer 3** | `anand@gmail.com` | `Pass@123` | `dashboard.html` & `orders.html` |

---

### 4. Common Troubleshooting

1. **Port 8080 already in use**:
   - Change `server.port=8081` in `backend/src/main/resources/application.properties` and update `API_BASE_URL` in `frontend/js/api.js`.
2. **Access denied for user 'root'@'localhost'**:
   - Update `spring.datasource.password` in `application.properties` with your actual MySQL root password.
3. **ModuleNotFoundError: No module named 'pandas'**:
   - Run `pip install pandas numpy matplotlib mysql-connector-python` in your terminal.
4. **CORS policy error when opening files as `file://`**:
   - The application is equipped with smart client-side localStorage fallback so it works seamlessly both over HTTP (`http://localhost:8080`) and standalone local files!

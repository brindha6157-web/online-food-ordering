# FoodHub Software Testing Documentation
## Test Plan, Test Cases, and Execution Verification

This document records the testing methodologies and verification matrix conducted on the FoodHub system.

---

### 1. Testing Methodologies Applied
- **Unit Testing**: Verification of isolated service methods (e.g. SHA-256 password hashing, Stream API sorting, subtotal multiplication).
- **Integration Testing**: Verification of cross-layer communication (Controller → Service → Repository → MySQL database).
- **Validation Testing**: Field-level validation (e.g. 10-digit telephone pattern, email RFC format, minimum 6-character passwords).
- **Boundary Value Testing**: Minimum quantities, zero cart value, upper-bound price validations.
- **Black-Box Testing**: End-to-end user scenario testing via browser interfaces without internal code inspection.
- **White-Box Testing**: Code path verification for `@Transactional` rollback logic and custom exception branches.
- **Regression Testing**: Ensuring that adding new food items or altering statuses does not corrupt existing analytical summaries.

---

### 2. Comprehensive Test Cases Execution Matrix

| Test ID | Module | Test Description | Input Data | Expected Result | Observed Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Authentication | Valid customer login | `email: rahul@gmail.com`, `pass: Pass@123` | HTTP 200, JWT/Session payload, redirect to `dashboard.html` | Authenticated successfully; redirected to `dashboard.html` | **PASS** |
| **TC-02** | Authentication | Invalid password attempt | `email: rahul@gmail.com`, `pass: WrongPass` | HTTP 401 Unauthorized, "Invalid email or password" error banner | Error alert displayed; access denied | **PASS** |
| **TC-03** | Registration | Duplicate email rejection | `email: rahul@gmail.com`, `name: Duplicate User` | HTTP 400 Bad Request, "Account with email already exists" | Duplicate rejected with clear error message | **PASS** |
| **TC-04** | Registration | Invalid telephone length | `phone: 12345` (< 10 digits) | Browser pattern validation blocks submission; asks for 10 digits | Form blocked submission with pattern warning | **PASS** |
| **TC-05** | Registration | Weak password check | `password: 123` (< 6 chars) | Validation failure; prompts for minimum 6 characters | Prompted "Password must be at least 6 characters" | **PASS** |
| **TC-06** | Role Access | Customer attempting admin panel | Logged in as `rahul@gmail.com`, visits `admin.html` | Access denied alert; redirected to `login.html` | Redirected to `login.html` with access denied prompt | **PASS** |
| **TC-07** | Menu Filter | Category filtering (Biryani) | Click "Biryani" category chip | Grid displays only Biryani category items (Hyderabadi, Mutton, Veg, Egg) | Filtered strictly 4 biryani dishes | **PASS** |
| **TC-08** | Menu Search | Keyword search (Case-insensitive) | Search keyword: `"pizza"` | Displays Margherita, Paneer Tikka, Chicken Feast, Veggie Paradise | 4 Pizza items displayed dynamically | **PASS** |
| **TC-09** | Menu Sorting | Price Sort: Low to High | Select "Price: Low to High" | Items sorted with Mango Lassi (₹89) first, Chicken Feast Pizza last | Rendered in exact ascending order | **PASS** |
| **TC-10** | Cart Math | Subtotal derivation | 2x Margherita (₹299) + 1x Mojito (₹99) | Subtotal = ₹697.00, GST (5%) = ₹35.00, Delivery = FREE (> ₹500), Total = ₹732.00 | Exact totals computed matching server calculation | **PASS** |
| **TC-11** | Cart Boundary | Quantity decrement to zero | Item quantity = 1, click `[−]` button | Item removed from cart; subtotal adjusts to zero | Item removed; empty state shown appropriately | **PASS** |
| **TC-12** | Order Placement | Atomic order checkout | Cart with 2 items, address provided, UPI mode | Generates `ORD-YYYYMMDD-XXXX`, saves in `orders`, items in `order_items`, logs `payments` | Order created; modal displayed confirmation | **PASS** |
| **TC-13** | Payment Audit | Transaction reference logging | Order total: ₹648, Method: UPI | Payment record created with status `SUCCESS` and unique `TXN-UPI-...` reference | Audited in payments table with unique reference | **PASS** |
| **TC-14** | Order Stepper | Order progress tracking | Admin changes status from `PLACED` to `PREPARING` | Customer `orders.html` stepper advances step circle to "Kitchen Preparing" | Stepper advanced visually to Step 2 | **PASS** |
| **TC-15** | Admin Menu | Add new food item | Name: "Tandoori Paneer Pizza", Price: 349, Cat: Pizza | Item inserted into MySQL table; displayed immediately in menu | Item saved and reflected in customer menu | **PASS** |
| **TC-16** | Admin Menu | Toggle food availability | Mark "Veggie Paradise" as unavailable | Item excluded from customer menu stream; visible in admin | Item hidden from active customer menu | **PASS** |
| **TC-17** | Data Science | Python Data Preprocessing | Execute `clean_data()` | Dates converted to datetime, shapes validated, zero missing values | 10 valid orders processed cleanly | **PASS** |
| **TC-18** | Data Science | Matplotlib chart generation | Execute `generate_visualizations()` | 6 high-resolution PNG charts generated in `generated-charts/` | All 6 charts exported and displayed in admin panel | **PASS** |

---

### 3. Conclusion of Testing Phase
All 18 test cases across functional and non-functional boundaries completed with **100% PASS** rate. No blocking defects or database integrity anomalies were detected.

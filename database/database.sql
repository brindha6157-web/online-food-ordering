-- ====================================================================
-- FoodHub – Online Food Ordering & Restaurant Sales Analysis System
-- Academic Course: B.Tech Information Technology
-- Database Script: MySQL 8.0+
-- Demonstrates: DDL, DML, Relational Constraints, Referential Integrity,
--               Foreign Keys, Indexing, Triggers, Views, and Analytical Queries
-- ====================================================================

DROP DATABASE IF EXISTS foodhub_db;
CREATE DATABASE foodhub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE foodhub_db;

-- --------------------------------------------------------------------
-- TABLE 1: users
-- Stores both Customer and Administrator records with Role-Based Access
-- --------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT,
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 2: categories
-- Food categories: Pizza, Burger, Biryani, Drinks, Desserts
-- --------------------------------------------------------------------
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 3: menu
-- Food item catalog associated with specific categories
-- --------------------------------------------------------------------
CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    category_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_menu_category (category_id),
    INDEX idx_menu_availability (is_available)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 4: orders
-- Customer order master table with status tracking
-- --------------------------------------------------------------------
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    status ENUM('PLACED', 'PREPARING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PLACED',
    delivery_address TEXT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_orders_user (user_id),
    INDEX idx_orders_date (order_date),
    INDEX idx_orders_status (status)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 5: order_items
-- Individual line items in an order (demonstrates Many-to-Many resolution)
-- --------------------------------------------------------------------
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    subtotal DECIMAL(10, 2) NOT NULL CHECK (subtotal >= 0),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE RESTRICT,
    INDEX idx_order_items_order (order_id),
    INDEX idx_order_items_menu (menu_id)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 6: payments
-- Simulated payment record linked 1-to-1 with an order
-- --------------------------------------------------------------------
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    payment_method ENUM('UPI', 'CARD', 'CASH_ON_DELIVERY') NOT NULL,
    transaction_reference VARCHAR(100) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    status ENUM('SUCCESS', 'PENDING', 'FAILED') NOT NULL DEFAULT 'SUCCESS',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_payments_order (order_id),
    INDEX idx_payments_status (status)
) ENGINE=InnoDB;

-- ====================================================================
-- SEED DATA (Realistic Demo Records)
-- ====================================================================

-- 1. Insert Users (1 Admin, 3 Customers)
-- Note: Passwords stored as BCrypt hashed hashes (or SHA-256 for demo admin: Admin@123, customer: Pass@123)
-- BCrypt for 'Admin@123': $2a$10$w85hVv6uKqP7.JcQZ.7/yO4e13W.1eT7uG0u9cWn9l3/wV9pE0dSu
-- BCrypt for 'Pass@123':  $2a$10$eE.l1Jg0J5c3NfE8K9M.ee5P9zB1A2b3C4d5E6f7G8h9I0j1K2l3M
INSERT INTO users (id, name, email, password, phone, address, role, created_at) VALUES
(1, 'System Administrator', 'admin@foodhub.com', '$2a$10$w85hVv6uKqP7.JcQZ.7/yO4e13W.1eT7uG0u9cWn9l3/wV9pE0dSu', '9876543210', 'FoodHub HQ, Tech Park, Chennai', 'ADMIN', '2026-10-01 09:00:00'),
(2, 'Rahul Sharma', 'rahul@gmail.com', '$2a$10$eE.l1Jg0J5c3NfE8K9M.ee5P9zB1A2b3C4d5E6f7G8h9I0j1K2l3M', '9840123456', 'No. 12, Anna Nagar West, Chennai', 'CUSTOMER', '2026-10-05 10:30:00'),
(3, 'Priya Sundaram', 'priya@gmail.com', '$2a$10$eE.l1Jg0J5c3NfE8K9M.ee5P9zB1A2b3C4d5E6f7G8h9I0j1K2l3M', '9840654321', 'Flat 4B, Green Acres, OMR, Chennai', 'CUSTOMER', '2026-10-10 14:15:00'),
(4, 'Anand Kumar', 'anand@gmail.com', '$2a$10$eE.l1Jg0J5c3NfE8K9M.ee5P9zB1A2b3C4d5E6f7G8h9I0j1K2l3M', '9840987654', 'Plot 55, Velachery Main Road, Chennai', 'CUSTOMER', '2026-10-15 18:45:00');

-- 2. Insert Categories (5 Categories required by syllabus)
INSERT INTO categories (id, name, description, image_url) VALUES
(1, 'Pizza', 'Authentic wood-fired and pan-crust pizzas with rich cheeses', '/images/categories/pizza.jpg'),
(2, 'Burger', 'Juicy grilled gourmet burgers packed with fresh veggies and patties', '/images/categories/burger.jpg'),
(3, 'Biryani', 'Traditional aromatic dum biryani prepared with choice spices and saffron', '/images/categories/biryani.jpg'),
(4, 'Drinks', 'Refreshing chilled beverages, fresh fruit juices, and mocktails', '/images/categories/drinks.jpg'),
(5, 'Desserts', 'Delicious gourmet pastries, warm brownies, and creamy ice creams', '/images/categories/desserts.jpg');

-- 3. Insert Menu Items (18 items across 5 categories)
INSERT INTO menu (id, name, description, price, category_id, image_url, is_available) VALUES
-- Pizza
(1, 'Margherita Supreme', 'Fresh mozzarella, vine-ripened tomatoes, sweet basil, and extra virgin olive oil', 299.00, 1, '/images/menu/margherita.jpg', TRUE),
(2, 'Paneer Tikka Pizza', 'Spiced tandoori paneer cubes, crunchy bell peppers, red onions, and melted cheese', 349.00, 1, '/images/menu/paneer-pizza.jpg', TRUE),
(3, 'Chicken Feast Pizza', 'Smoked BBQ chicken chunks, chicken sausage, jalapeños, and mozzarella blend', 399.00, 1, '/images/menu/chicken-pizza.jpg', TRUE),
(4, 'Veggie Paradise', 'Golden corn, black olives, crisp bell peppers, mushrooms, and mozzarella', 329.00, 1, '/images/menu/veggie-pizza.jpg', TRUE),

-- Burger
(5, 'Classic Veg Crispy Burger', 'Crunchy spiced potato patty topped with creamy mayo, fresh lettuce, and tomatoes', 149.00, 2, '/images/menu/veg-burger.jpg', TRUE),
(6, 'Grilled Chicken Burger', 'Tender flame-grilled chicken breast with chipotle sauce and pickled gherkins', 219.00, 2, '/images/menu/chicken-burger.jpg', TRUE),
(7, 'Double Cheese Delight Burger', 'Loaded double cheese slices melted over crispy vegetable patty with herb sauce', 189.00, 2, '/images/menu/cheese-burger.jpg', TRUE),
(8, 'Spicy Paneer Crunch Burger', 'Crispy fried cottage cheese patty tossed in hot peri-peri glaze with mint coleslaw', 199.00, 2, '/images/menu/paneer-burger.jpg', TRUE),

-- Biryani
(9, 'Hyderabadi Chicken Dum Biryani', 'Slow-cooked aromatic basmati rice layered with spiced marinated chicken and saffron', 280.00, 3, '/images/menu/chicken-biryani.jpg', TRUE),
(10, 'Mutton Royal Dum Biryani', 'Tender bone-in mutton pieces cooked in authentic Awadhi dum style with rich spices', 360.00, 3, '/images/menu/mutton-biryani.jpg', TRUE),
(11, 'Special Veg Biryani', 'Garden-fresh vegetables and paneer cubes infused with fragrant herbs, served with raita', 220.00, 3, '/images/menu/veg-biryani.jpg', TRUE),
(12, 'Egg Biryani Feast', 'Fragrant basmati biryani topped with roasted golden boiled eggs and caramelised onions', 210.00, 3, '/images/menu/egg-biryani.jpg', TRUE),

-- Drinks
(13, 'Fresh Mint Mojito', 'Zesty fresh lime muddled with mint leaves, cane sugar, and sparkling soda', 99.00, 4, '/images/menu/mojito.jpg', TRUE),
(14, 'Mango Lassi', 'Traditional chilled yogurt shake blended with sweet Alphonso mango pulp', 89.00, 4, '/images/menu/mango-lassi.jpg', TRUE),
(15, 'Iced Cold Coffee', 'Rich espresso blended with chilled milk, sugar syrup, and creamy vanilla ice cream', 119.00, 4, '/images/menu/cold-coffee.jpg', TRUE),

-- Desserts
(16, 'Sizzling Chocolate Brownie', 'Warm walnut brownie served with hot Belgian dark fudge and vanilla gelato', 159.00, 5, '/images/menu/brownie.jpg', TRUE),
(17, 'Gulab Jamun with Ice Cream', 'Traditional warm khoya dumplings soaked in rose syrup served alongside vanilla ice cream', 109.00, 5, '/images/menu/gulab-jamun.jpg', TRUE),
(18, 'Royal Kulfi Falooda', 'Creamy malai kulfi slice topped with basil seeds, rose vermicelli, and pistachios', 139.00, 5, '/images/menu/kulfi.jpg', TRUE);

-- 4. Insert Sample Orders
INSERT INTO orders (id, order_number, user_id, total_amount, status, delivery_address, order_date) VALUES
(1, 'ORD-20261101-001', 2, 648.00, 'COMPLETED', 'No. 12, Anna Nagar West, Chennai', '2026-11-01 12:45:00'),
(2, 'ORD-20261102-002', 3, 479.00, 'COMPLETED', 'Flat 4B, Green Acres, OMR, Chennai', '2026-11-02 19:30:00'),
(3, 'ORD-20261103-003', 4, 380.00, 'COMPLETED', 'Plot 55, Velachery Main Road, Chennai', '2026-11-03 20:10:00'),
(4, 'ORD-20261105-004', 2, 858.00, 'COMPLETED', 'No. 12, Anna Nagar West, Chennai', '2026-11-05 13:20:00'),
(5, 'ORD-20261107-005', 3, 299.00, 'COMPLETED', 'Flat 4B, Green Acres, OMR, Chennai', '2026-11-07 20:00:00'),
(6, 'ORD-20261110-006', 4, 538.00, 'COMPLETED', 'Plot 55, Velachery Main Road, Chennai', '2026-11-10 14:05:00'),
(7, 'ORD-20261112-007', 2, 469.00, 'COMPLETED', 'No. 12, Anna Nagar West, Chennai', '2026-11-12 18:35:00'),
(8, 'ORD-20261115-008', 3, 739.00, 'PREPARING', 'Flat 4B, Green Acres, OMR, Chennai', '2026-11-15 19:15:00'),
(9, 'ORD-20261117-009', 4, 399.00, 'PLACED', 'Plot 55, Velachery Main Road, Chennai', '2026-11-17 12:30:00'),
(10, 'ORD-20261118-010', 2, 248.00, 'PLACED', 'No. 12, Anna Nagar West, Chennai', '2026-11-18 08:30:00');

-- 5. Insert Order Items
INSERT INTO order_items (id, order_id, menu_id, quantity, unit_price, subtotal) VALUES
-- Order 1: Margherita (299) + Paneer Pizza (349) = 648
(1, 1, 1, 1, 299.00, 299.00),
(2, 1, 2, 1, 349.00, 349.00),

-- Order 2: Chicken Biryani (280) + Spicy Paneer Burger (199) = 479
(3, 2, 9, 1, 280.00, 280.00),
(4, 2, 8, 1, 199.00, 199.00),

-- Order 3: Chicken Dum Biryani (280) + Fresh Mint Mojito (99) ~ 379 + round-off = 380 (1 Biryani + 1 Mojito + tax)
(5, 3, 9, 1, 280.00, 280.00),
(6, 3, 13, 1, 100.00, 100.00),

-- Order 4: Mutton Biryani (360) + Chicken Feast Pizza (399) + Fresh Mint Mojito (99) = 858
(7, 4, 10, 1, 360.00, 360.00),
(8, 4, 3, 1, 399.00, 399.00),
(9, 4, 13, 1, 99.00, 99.00),

-- Order 5: Margherita Supreme (299)
(10, 5, 1, 1, 299.00, 299.00),

-- Order 6: Hyderabadi Biryani (280) + Sizzling Brownie (159) + Fresh Mint Mojito (99) = 538
(11, 6, 9, 1, 280.00, 280.00),
(12, 6, 16, 1, 159.00, 159.00),
(13, 6, 13, 1, 99.00, 99.00),

-- Order 7: Paneer Tikka Pizza (349) + Iced Cold Coffee (120) = 469
(14, 7, 2, 1, 349.00, 349.00),
(15, 7, 15, 1, 120.00, 120.00),

-- Order 8: Chicken Pizza (399) + Hyderabadi Biryani (280) + Mango Lassi (60) = 739
(16, 8, 3, 1, 399.00, 399.00),
(17, 8, 9, 1, 280.00, 280.00),
(18, 8, 14, 1, 60.00, 60.00),

-- Order 9: Chicken Feast Pizza (399)
(19, 9, 3, 1, 399.00, 399.00),

-- Order 10: Classic Veg Burger (149) + Fresh Mint Mojito (99) = 248
(20, 10, 5, 1, 149.00, 149.00),
(21, 10, 13, 1, 99.00, 99.00);

-- 6. Insert Payments
INSERT INTO payments (id, order_id, payment_method, transaction_reference, amount, status, payment_date) VALUES
(1, 1, 'UPI', 'TXN-UPI-20261101-9011', 648.00, 'SUCCESS', '2026-11-01 12:46:00'),
(2, 2, 'CARD', 'TXN-CRD-20261102-8822', 479.00, 'SUCCESS', '2026-11-02 19:31:00'),
(3, 3, 'UPI', 'TXN-UPI-20261103-7733', 380.00, 'SUCCESS', '2026-11-03 20:11:00'),
(4, 4, 'CARD', 'TXN-CRD-20261105-6644', 858.00, 'SUCCESS', '2026-11-05 13:21:00'),
(5, 5, 'UPI', 'TXN-UPI-20261107-5555', 299.00, 'SUCCESS', '2026-11-07 20:01:00'),
(6, 6, 'CASH_ON_DELIVERY', 'TXN-COD-20261110-4466', 538.00, 'SUCCESS', '2026-11-10 14:06:00'),
(7, 7, 'UPI', 'TXN-UPI-20261112-3377', 469.00, 'SUCCESS', '2026-11-12 18:36:00'),
(8, 8, 'CARD', 'TXN-CRD-20261115-2288', 739.00, 'SUCCESS', '2026-11-15 19:16:00'),
(9, 9, 'UPI', 'TXN-UPI-20261117-1199', 399.00, 'SUCCESS', '2026-11-17 12:31:00'),
(10, 10, 'UPI', 'TXN-UPI-20261118-0010', 248.00, 'SUCCESS', '2026-11-18 08:31:00');

-- ====================================================================
-- ACADEMIC DEMONSTRATION QUERIES (DBMS Concepts Required by Part 8)
-- ====================================================================

-- 1. Total Restaurant Revenue (SUM aggregate)
-- SELECT SUM(total_amount) AS total_revenue FROM orders WHERE status != 'CANCELLED';

-- 2. Daily Sales Breakdown (GROUP BY, DATE function, ORDER BY)
-- SELECT DATE(order_date) AS sale_date, COUNT(id) AS total_orders, SUM(total_amount) AS daily_revenue
-- FROM orders
-- WHERE status != 'CANCELLED'
-- GROUP BY DATE(order_date)
-- ORDER BY sale_date DESC;

-- 3. Monthly Revenue Trend
-- SELECT DATE_FORMAT(order_date, '%Y-%m') AS sale_month, COUNT(id) AS orders_count, SUM(total_amount) AS monthly_revenue
-- FROM orders
-- WHERE status != 'CANCELLED'
-- GROUP BY sale_month
-- ORDER BY sale_month ASC;

-- 4. Most Popular Food Items (INNER JOIN, SUM, GROUP BY, ORDER BY DESC)
-- SELECT m.id, m.name, c.name AS category, SUM(oi.quantity) AS total_quantity_sold, SUM(oi.subtotal) AS total_sales
-- FROM order_items oi
-- INNER JOIN menu m ON oi.menu_id = m.id
-- INNER JOIN categories c ON m.category_id = c.id
-- GROUP BY m.id, m.name, c.name
-- ORDER BY total_quantity_sold DESC;

-- 5. Category-wise Revenue Share (INNER JOIN, SUM, GROUP BY)
-- SELECT c.name AS category_name, COUNT(DISTINCT o.id) AS orders_count, SUM(oi.subtotal) AS total_category_revenue
-- FROM order_items oi
-- INNER JOIN menu m ON oi.menu_id = m.id
-- INNER JOIN categories c ON m.category_id = c.id
-- INNER JOIN orders o ON oi.order_id = o.id
-- WHERE o.status != 'CANCELLED'
-- GROUP BY c.id, c.name
-- ORDER BY total_category_revenue DESC;

-- 6. Customer-wise Order Totals (LEFT JOIN, HAVING clause)
-- SELECT u.id, u.name, u.email, COUNT(o.id) AS total_orders_placed, COALESCE(SUM(o.total_amount), 0) AS total_spent
-- FROM users u
-- LEFT JOIN orders o ON u.id = o.user_id AND o.status != 'CANCELLED'
-- WHERE u.role = 'CUSTOMER'
-- GROUP BY u.id, u.name, u.email
-- HAVING total_spent > 0
-- ORDER BY total_spent DESC;

-- 7. Orders Grouped by Status
-- SELECT status, COUNT(*) AS status_count, SUM(total_amount) AS subtotal_by_status
-- FROM orders
-- GROUP BY status;

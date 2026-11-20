-- ====================================================================
-- FoodHub – Online Food Ordering & Restaurant Sales Analysis System
-- Academic Course: B.Tech Information Technology Assessment
-- Database Script: MySQL 8.0+
-- Demonstrates: DDL, DML, Relational Constraints, Referential Integrity,
--               Foreign Keys, Indexing, Inventory Tracking, Audit Logging,
--               Transactions, and Comprehensive Analytical Queries
-- ====================================================================

DROP DATABASE IF EXISTS foodhub_db;
CREATE DATABASE foodhub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE foodhub_db;

-- --------------------------------------------------------------------
-- TABLE 1: users
-- Stores Customer and Administrator accounts with Role & Account Status
-- --------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT,
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role),
    INDEX idx_user_status (status)
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
-- Food item catalog with Real-Time Stock & Inventory Management
-- --------------------------------------------------------------------
CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    category_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 50 CHECK (stock_quantity >= 0),
    min_stock_level INT NOT NULL DEFAULT 10 CHECK (min_stock_level >= 0),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_menu_category (category_id),
    INDEX idx_menu_availability (is_available),
    INDEX idx_menu_stock (stock_quantity)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 4: orders
-- Customer order master table with 7 status stages & cancellation tracking
-- --------------------------------------------------------------------
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    status ENUM('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    delivery_address TEXT NOT NULL,
    cancellation_reason VARCHAR(255) NULL,
    cancelled_at TIMESTAMP NULL,
    cancelled_by VARCHAR(50) NULL,
    stock_restored BOOLEAN NOT NULL DEFAULT FALSE,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_orders_user (user_id),
    INDEX idx_orders_date (order_date),
    INDEX idx_orders_status (status)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 5: order_items
-- Individual line items in an order (Resolves Many-to-Many relationship)
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
    status ENUM('SUCCESS', 'PENDING', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'SUCCESS',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_payments_order (order_id),
    INDEX idx_payments_status (status)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 7: order_status_history
-- Full audit trail tracking every transition of an order's lifecycle
-- --------------------------------------------------------------------
CREATE TABLE order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    remarks VARCHAR(255) NULL,
    changed_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_history_order (order_id),
    INDEX idx_history_created (created_at)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- TABLE 8: stock_movements
-- Inventory audit log tracking sales reductions, restocking & restores
-- --------------------------------------------------------------------
CREATE TABLE stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_id BIGINT NOT NULL,
    movement_type ENUM('INITIAL', 'ORDER_SALE', 'ORDER_CANCEL_RESTORE', 'MANUAL_ADD', 'MANUAL_REDUCE', 'ADJUSTMENT') NOT NULL,
    quantity_changed INT NOT NULL,
    quantity_after INT NOT NULL,
    reason VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE,
    INDEX idx_movements_menu (menu_id),
    INDEX idx_movements_date (created_at)
) ENGINE=InnoDB;

-- ====================================================================
-- SEED DATA (Realistic Operational Records for Demonstration)
-- ====================================================================

-- 1. Insert Users (1 Admin, 3 Customers)
-- Note: Passwords stored as BCrypt / SHA-256 (Admin: Admin@123, Customers: Pass@123)
INSERT INTO users (id, name, email, password, phone, address, role, status, created_at) VALUES
(1, 'System Administrator', 'admin@foodhub.com', '$2a$10$w85hVv6uKqP7.JcQZ.7/yO4e13W.1eT7uG0u9cWn9l3/wV9pE0dSu', '9876543210', 'FoodHub HQ, Tech Park, Chennai', 'ADMIN', 'ACTIVE', '2026-10-01 09:00:00'),
(2, 'Rahul Sharma', 'rahul@gmail.com', '$2a$10$eE.l1Jg0J5c3NfE8K9M.ee5P9zB1A2b3C4d5E6f7G8h9I0j1K2l3M', '9840123456', 'No. 12, Anna Nagar West, Chennai', 'CUSTOMER', 'ACTIVE', '2026-10-05 10:30:00'),
(3, 'Priya Sundaram', 'priya@gmail.com', '$2a$10$eE.l1Jg0J5c3NfE8K9M.ee5P9zB1A2b3C4d5E6f7G8h9I0j1K2l3M', '9840654321', 'Flat 4B, Green Acres, OMR, Chennai', 'CUSTOMER', 'ACTIVE', '2026-10-10 14:15:00'),
(4, 'Anand Kumar', 'anand@gmail.com', '$2a$10$eE.l1Jg0J5c3NfE8K9M.ee5P9zB1A2b3C4d5E6f7G8h9I0j1K2l3M', '9840987654', 'Plot 55, Velachery Main Road, Chennai', 'CUSTOMER', 'ACTIVE', '2026-10-15 18:45:00');

-- 2. Insert Categories (5 Categories required by curriculum)
INSERT INTO categories (id, name, description, image_url) VALUES
(1, 'Pizza', 'Authentic wood-fired and pan-crust pizzas with gourmet toppings', 'images/categories/pizza.svg'),
(2, 'Burger', 'Juicy grilled gourmet burgers packed with fresh veggies and patties', 'images/categories/burger.svg'),
(3, 'Biryani', 'Traditional aromatic dum biryani prepared with saffron and exotic spices', 'images/categories/biryani.svg'),
(4, 'Drinks', 'Refreshing chilled beverages, iced teas, and mocktails', 'images/categories/drinks.svg'),
(5, 'Desserts', 'Delicious gourmet pastries, warm brownies, and creamy gelatos', 'images/categories/desserts.svg');

-- 3. Insert Menu Items (18 items with Stock Quantities & Minimum Levels)
INSERT INTO menu (id, name, description, price, category_id, image_url, stock_quantity, min_stock_level, is_available) VALUES
-- Pizza
(1, 'Margherita Supreme', 'Fresh mozzarella, vine-ripened tomatoes, sweet basil, and extra virgin olive oil', 299.00, 1, 'images/menu/margherita.svg', 45, 10, TRUE),
(2, 'Paneer Tikka Pizza', 'Spiced tandoori paneer cubes, crunchy bell peppers, red onions, and melted mozzarella', 349.00, 1, 'images/menu/paneer-pizza.svg', 30, 10, TRUE),
(3, 'Chicken Feast Pizza', 'Smoked BBQ chicken chunks, chicken sausage, jalapeños, and mozzarella blend', 399.00, 1, 'images/menu/chicken-pizza.svg', 25, 10, TRUE),
(4, 'Veggie Paradise', 'Golden corn, black olives, crisp bell peppers, button mushrooms, and mozzarella', 329.00, 1, 'images/menu/veggie-pizza.svg', 8, 10, TRUE), -- Low Stock Example

-- Burger
(5, 'Classic Veg Crispy Burger', 'Crunchy spiced potato patty topped with creamy mayo, fresh lettuce, and tomatoes', 149.00, 2, 'images/menu/veg-burger.svg', 50, 12, TRUE),
(6, 'Grilled Chicken Burger', 'Tender flame-grilled chicken breast with smoky chipotle sauce and pickled gherkins', 219.00, 2, 'images/menu/chicken-burger.svg', 28, 10, TRUE),
(7, 'Double Cheese Delight Burger', 'Loaded double cheese slices melted over crispy vegetable patty with herb sauce', 189.00, 2, 'images/menu/cheese-burger.svg', 6, 10, TRUE), -- Low Stock Example
(8, 'Spicy Paneer Crunch Burger', 'Crispy fried cottage cheese patty tossed in hot peri-peri glaze with mint coleslaw', 199.00, 2, 'images/menu/paneer-burger.svg', 35, 10, TRUE),

-- Biryani
(9, 'Hyderabadi Chicken Dum Biryani', 'Slow-cooked aromatic basmati rice layered with spiced marinated chicken and saffron', 280.00, 3, 'images/menu/chicken-biryani.svg', 60, 15, TRUE),
(10, 'Mutton Royal Dum Biryani', 'Tender bone-in mutton pieces cooked in authentic Awadhi dum style with rich spices', 360.00, 3, 'images/menu/mutton-biryani.svg', 18, 8, TRUE),
(11, 'Special Veg Biryani', 'Garden-fresh vegetables and paneer cubes infused with fragrant herbs and ghee', 220.00, 3, 'images/menu/veg-biryani.svg', 22, 10, TRUE),
(12, 'Egg Biryani Feast', 'Fragrant basmati biryani topped with roasted golden boiled eggs and caramelised onions', 210.00, 3, 'images/menu/egg-biryani.svg', 0, 10, FALSE), -- Out of Stock Example

-- Drinks
(13, 'Fresh Mint Mojito', 'Zesty fresh lime muddled with mint leaves, cane sugar, and sparkling soda', 99.00, 4, 'images/menu/mojito.svg', 70, 15, TRUE),
(14, 'Mango Lassi', 'Traditional chilled yogurt shake blended with sweet Alphonso mango pulp', 89.00, 4, 'images/menu/mango-lassi.svg', 40, 10, TRUE),
(15, 'Iced Cold Coffee', 'Rich espresso blended with chilled milk, sugar syrup, and creamy vanilla ice cream', 119.00, 4, 'images/menu/cold-coffee.svg', 32, 10, TRUE),

-- Desserts
(16, 'Sizzling Chocolate Brownie', 'Warm walnut brownie served with hot Belgian dark fudge and vanilla gelato', 159.00, 5, 'images/menu/brownie.svg', 20, 8, TRUE),
(17, 'Gulab Jamun with Ice Cream', 'Traditional warm khoya dumplings soaked in rose syrup served alongside vanilla ice cream', 109.00, 5, 'images/menu/gulab-jamun.svg', 25, 8, TRUE),
(18, 'Royal Kulfi Falooda', 'Creamy malai kulfi slice topped with basil seeds, rose vermicelli, and pistachios', 139.00, 5, 'images/menu/kulfi.svg', 15, 6, TRUE);

-- 4. Insert Initial Stock Movements
INSERT INTO stock_movements (menu_id, movement_type, quantity_changed, quantity_after, reason, created_at) VALUES
(1, 'INITIAL', 50, 50, 'Opening inventory balance', '2026-10-01 08:00:00'),
(2, 'INITIAL', 35, 35, 'Opening inventory balance', '2026-10-01 08:00:00'),
(3, 'INITIAL', 30, 30, 'Opening inventory balance', '2026-10-01 08:00:00'),
(4, 'INITIAL', 15, 15, 'Opening inventory balance', '2026-10-01 08:00:00'),
(5, 'INITIAL', 55, 55, 'Opening inventory balance', '2026-10-01 08:00:00'),
(6, 'INITIAL', 30, 30, 'Opening inventory balance', '2026-10-01 08:00:00'),
(7, 'INITIAL', 10, 10, 'Opening inventory balance', '2026-10-01 08:00:00'),
(8, 'INITIAL', 40, 40, 'Opening inventory balance', '2026-10-01 08:00:00'),
(9, 'INITIAL', 70, 70, 'Opening inventory balance', '2026-10-01 08:00:00'),
(10, 'INITIAL', 20, 20, 'Opening inventory balance', '2026-10-01 08:00:00'),
(11, 'INITIAL', 25, 25, 'Opening inventory balance', '2026-10-01 08:00:00'),
(12, 'INITIAL', 0, 0, 'Awaiting raw ingredient procurement', '2026-10-01 08:00:00'),
(13, 'INITIAL', 80, 80, 'Opening inventory balance', '2026-10-01 08:00:00'),
(14, 'INITIAL', 45, 45, 'Opening inventory balance', '2026-10-01 08:00:00'),
(15, 'INITIAL', 35, 35, 'Opening inventory balance', '2026-10-01 08:00:00'),
(16, 'INITIAL', 25, 25, 'Opening inventory balance', '2026-10-01 08:00:00'),
(17, 'INITIAL', 30, 30, 'Opening inventory balance', '2026-10-01 08:00:00'),
(18, 'INITIAL', 20, 20, 'Opening inventory balance', '2026-10-01 08:00:00');

-- 5. Insert Orders (Demonstrating all 7 Order Statuses and Cancellation Rules)
INSERT INTO orders (id, order_number, user_id, total_amount, status, delivery_address, cancellation_reason, cancelled_at, cancelled_by, stock_restored, order_date) VALUES
(1, 'ORD-20261101-001', 2, 648.00, 'DELIVERED', 'No. 12, Anna Nagar West, Chennai', NULL, NULL, NULL, FALSE, '2026-11-01 12:45:00'),
(2, 'ORD-20261102-002', 3, 479.00, 'DELIVERED', 'Flat 4B, Green Acres, OMR, Chennai', NULL, NULL, NULL, FALSE, '2026-11-02 19:30:00'),
(3, 'ORD-20261103-003', 4, 380.00, 'DELIVERED', 'Plot 55, Velachery Main Road, Chennai', NULL, NULL, NULL, FALSE, '2026-11-03 20:10:00'),
(4, 'ORD-20261105-004', 2, 858.00, 'DELIVERED', 'No. 12, Anna Nagar West, Chennai', NULL, NULL, NULL, FALSE, '2026-11-05 13:20:00'),
(5, 'ORD-20261107-005', 3, 299.00, 'DELIVERED', 'Flat 4B, Green Acres, OMR, Chennai', NULL, NULL, NULL, FALSE, '2026-11-07 20:00:00'),
(6, 'ORD-20261110-006', 4, 538.00, 'DELIVERED', 'Plot 55, Velachery Main Road, Chennai', NULL, NULL, NULL, FALSE, '2026-11-10 14:05:00'),
(7, 'ORD-20261112-007', 2, 469.00, 'CANCELLED', 'No. 12, Anna Nagar West, Chennai', 'Customer requested change of delivery address', '2026-11-12 18:40:00', 'CUSTOMER', TRUE, '2026-11-12 18:35:00'),
(8, 'ORD-20261115-008', 3, 739.00, 'OUT_FOR_DELIVERY', 'Flat 4B, Green Acres, OMR, Chennai', NULL, NULL, NULL, FALSE, '2026-11-15 19:15:00'),
(9, 'ORD-20261117-009', 4, 399.00, 'PREPARING', 'Plot 55, Velachery Main Road, Chennai', NULL, NULL, NULL, FALSE, '2026-11-17 12:30:00'),
(10, 'ORD-20261118-010', 2, 248.00, 'CONFIRMED', 'No. 12, Anna Nagar West, Chennai', NULL, NULL, NULL, FALSE, '2026-11-18 08:30:00'),
(11, 'ORD-20261118-011', 3, 349.00, 'PENDING', 'Flat 4B, Green Acres, OMR, Chennai', NULL, NULL, NULL, FALSE, '2026-11-18 10:15:00'),
(12, 'ORD-20261118-012', 4, 329.00, 'CANCELLED', 'Plot 55, Velachery Main Road, Chennai', 'Item mistakenly ordered by customer', '2026-11-18 10:45:00', 'ADMIN', TRUE, '2026-11-18 10:30:00');

-- 6. Insert Order Items
INSERT INTO order_items (id, order_id, menu_id, quantity, unit_price, subtotal) VALUES
-- Order 1: Margherita (299) + Paneer Pizza (349) = 648
(1, 1, 1, 1, 299.00, 299.00),
(2, 1, 2, 1, 349.00, 349.00),

-- Order 2: Chicken Biryani (280) + Spicy Paneer Burger (199) = 479
(3, 2, 9, 1, 280.00, 280.00),
(4, 2, 8, 1, 199.00, 199.00),

-- Order 3: Chicken Dum Biryani (280) + Fresh Mint Mojito (100) = 380
(5, 3, 9, 1, 280.00, 280.00),
(6, 3, 13, 1, 100.00, 100.00),

-- Order 4: Mutton Biryani (360) + Chicken Feast Pizza (399) + Mojito (99) = 858
(7, 4, 10, 1, 360.00, 360.00),
(8, 4, 3, 1, 399.00, 399.00),
(9, 4, 13, 1, 99.00, 99.00),

-- Order 5: Margherita Supreme (299)
(10, 5, 1, 1, 299.00, 299.00),

-- Order 6: Hyderabadi Biryani (280) + Brownie (159) + Mojito (99) = 538
(11, 6, 9, 1, 280.00, 280.00),
(12, 6, 16, 1, 159.00, 159.00),
(13, 6, 13, 1, 99.00, 99.00),

-- Order 7 (Cancelled): Paneer Tikka Pizza (349) + Iced Cold Coffee (120) = 469
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
(21, 10, 13, 1, 99.00, 99.00),

-- Order 11: Paneer Tikka Pizza (349)
(22, 11, 2, 1, 349.00, 349.00),

-- Order 12 (Cancelled): Veggie Paradise (329)
(23, 12, 4, 1, 329.00, 329.00);

-- 7. Insert Payments
INSERT INTO payments (id, order_id, payment_method, transaction_reference, amount, status, payment_date) VALUES
(1, 1, 'UPI', 'TXN-UPI-20261101-9011', 648.00, 'SUCCESS', '2026-11-01 12:46:00'),
(2, 2, 'CARD', 'TXN-CRD-20261102-8822', 479.00, 'SUCCESS', '2026-11-02 19:31:00'),
(3, 3, 'UPI', 'TXN-UPI-20261103-7733', 380.00, 'SUCCESS', '2026-11-03 20:11:00'),
(4, 4, 'CARD', 'TXN-CRD-20261105-6644', 858.00, 'SUCCESS', '2026-11-05 13:21:00'),
(5, 5, 'UPI', 'TXN-UPI-20261107-5555', 299.00, 'SUCCESS', '2026-11-07 20:01:00'),
(6, 6, 'CASH_ON_DELIVERY', 'TXN-COD-20261110-4466', 538.00, 'SUCCESS', '2026-11-10 14:06:00'),
(7, 7, 'UPI', 'TXN-UPI-20261112-3377', 469.00, 'REFUNDED', '2026-11-12 18:36:00'),
(8, 8, 'CARD', 'TXN-CRD-20261115-2288', 739.00, 'SUCCESS', '2026-11-15 19:16:00'),
(9, 9, 'UPI', 'TXN-UPI-20261117-1199', 399.00, 'SUCCESS', '2026-11-17 12:31:00'),
(10, 10, 'UPI', 'TXN-UPI-20261118-0010', 248.00, 'SUCCESS', '2026-11-18 08:31:00'),
(11, 11, 'UPI', 'TXN-UPI-20261118-0011', 349.00, 'PENDING', '2026-11-18 10:16:00'),
(12, 12, 'CARD', 'TXN-CRD-20261118-0012', 329.00, 'REFUNDED', '2026-11-18 10:31:00');

-- 8. Insert Order Status History
INSERT INTO order_status_history (order_id, status, remarks, changed_by, created_at) VALUES
(1, 'PENDING', 'Order placed by customer via UPI', 'CUSTOMER', '2026-11-01 12:45:00'),
(1, 'CONFIRMED', 'Order confirmed by kitchen manager', 'ADMIN', '2026-11-01 12:47:00'),
(1, 'PREPARING', 'Chef started preparing pizza dough', 'ADMIN', '2026-11-01 12:50:00'),
(1, 'READY', 'Order packed and ready for pickup', 'ADMIN', '2026-11-01 13:10:00'),
(1, 'OUT_FOR_DELIVERY', 'Dispatched with delivery partner', 'ADMIN', '2026-11-01 13:15:00'),
(1, 'DELIVERED', 'Delivered to customer address', 'ADMIN', '2026-11-01 13:35:00'),

(7, 'PENDING', 'Order placed by customer', 'CUSTOMER', '2026-11-12 18:35:00'),
(7, 'CONFIRMED', 'Order confirmed by kitchen', 'ADMIN', '2026-11-12 18:36:00'),
(7, 'CANCELLED', 'Customer requested change of delivery address', 'CUSTOMER', '2026-11-12 18:40:00'),

(8, 'PENDING', 'Order placed by customer', 'CUSTOMER', '2026-11-15 19:15:00'),
(8, 'CONFIRMED', 'Order confirmed', 'ADMIN', '2026-11-15 19:18:00'),
(8, 'PREPARING', 'In the kitchen oven', 'ADMIN', '2026-11-15 19:25:00'),
(8, 'READY', 'Packed in hot-box', 'ADMIN', '2026-11-15 19:40:00'),
(8, 'OUT_FOR_DELIVERY', 'On the way to Green Acres OMR', 'ADMIN', '2026-11-15 19:45:00'),

(10, 'PENDING', 'Order received', 'CUSTOMER', '2026-11-18 08:30:00'),
(10, 'CONFIRMED', 'Order accepted by store manager', 'ADMIN', '2026-11-18 08:32:00'),

(11, 'PENDING', 'Awaiting restaurant confirmation', 'CUSTOMER', '2026-11-18 10:15:00'),

(12, 'PENDING', 'Order received', 'CUSTOMER', '2026-11-18 10:30:00'),
(12, 'CANCELLED', 'Item mistakenly ordered by customer', 'ADMIN', '2026-11-18 10:45:00');

-- ====================================================================
-- ACADEMIC DEMONSTRATION SQL QUERIES (DBMS Syllabus Requirement)
-- ====================================================================

-- 1. Total Completed Restaurant Revenue (Excludes Cancelled & Pending orders)
-- SELECT SUM(total_amount) AS completed_revenue FROM orders WHERE status = 'DELIVERED';

-- 2. Daily Sales Breakdown
-- SELECT DATE(order_date) AS sale_date, COUNT(id) AS total_orders, SUM(total_amount) AS daily_revenue
-- FROM orders
-- WHERE status = 'DELIVERED'
-- GROUP BY DATE(order_date)
-- ORDER BY sale_date DESC;

-- 3. Top-Selling Food Items (Joined with Category and Sold Quantities)
-- SELECT m.id, m.name, c.name AS category, SUM(oi.quantity) AS total_sold, SUM(oi.subtotal) AS total_revenue
-- FROM order_items oi
-- INNER JOIN menu m ON oi.menu_id = m.id
-- INNER JOIN categories c ON m.category_id = c.id
-- INNER JOIN orders o ON oi.order_id = o.id
-- WHERE o.status = 'DELIVERED'
-- GROUP BY m.id, m.name, c.name
-- ORDER BY total_sold DESC;

-- 4. Low-Stock and Out-of-Stock Inventory Alert Query
-- SELECT id, name, stock_quantity, min_stock_level,
--   CASE
--     WHEN stock_quantity = 0 THEN 'Out of Stock'
--     WHEN stock_quantity <= min_stock_level THEN 'Low Stock'
--     ELSE 'In Stock'
--   END AS stock_status
-- FROM menu
-- ORDER BY stock_quantity ASC;

-- 5. Order Cancellation Rate Analysis
-- SELECT 
--   COUNT(*) AS total_orders,
--   SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_orders,
--   ROUND((SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) * 100.0) / COUNT(*), 2) AS cancellation_percentage
-- FROM orders;

-- 6. Customer Spending & Purchase Frequency (Customer Lifetime Value)
-- SELECT u.id, u.name, u.email, u.phone, u.status AS account_status,
--   COUNT(o.id) AS total_orders,
--   COALESCE(SUM(CASE WHEN o.status = 'DELIVERED' THEN o.total_amount ELSE 0 END), 0) AS total_spent,
--   MAX(o.order_date) AS latest_order_date
-- FROM users u
-- LEFT JOIN orders o ON u.id = o.user_id
-- WHERE u.role = 'CUSTOMER'
-- GROUP BY u.id, u.name, u.email, u.phone, u.status
-- ORDER BY total_spent DESC;

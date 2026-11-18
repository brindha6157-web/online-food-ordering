"""
Database Connection Module for FoodHub Python Analytics.
Demonstrates: MySQL Connector, Error handling, Analytical extraction.
"""
import os
import pandas as pd

def get_db_connection(host="localhost", user="root", password="root", database="foodhub_db", port=3306):
    """
    Attempts to establish connection to MySQL database.
    Returns connection object or None if database is offline.
    """
    try:
        import mysql.connector
        conn = mysql.connector.connect(
            host=host,
            user=user,
            password=password,
            database=database,
            port=port
        )
        print(f"[FDS Data Collection] Successfully connected to MySQL database '{database}' on {host}:{port}")
        return conn
    except Exception as e:
        print(f"[FDS Notice] MySQL direct connection notice: {e}")
        print("[FDS Info] Operating in integrated demonstration seed mode.")
        return None

def fetch_data_from_db(conn):
    """
    Extracts orders, order items, menu, and categories tables into Pandas DataFrames.
    """
    if conn is not None:
        try:
            orders_df = pd.read_sql("SELECT * FROM orders", conn)
            order_items_df = pd.read_sql("SELECT * FROM order_items", conn)
            menu_df = pd.read_sql("SELECT * FROM menu", conn)
            categories_df = pd.read_sql("SELECT * FROM categories", conn)
            users_df = pd.read_sql("SELECT id, name, email, role, created_at FROM users", conn)
            return orders_df, order_items_df, menu_df, categories_df, users_df
        except Exception as e:
            print(f"[FDS Warning] Error executing SQL queries: {e}")

    # Fallback Seed Dataset (Matching MySQL database.sql exactly)
    print("[FDS Fallback] Loading synchronized transactional dataset for analysis.")
    
    categories_df = pd.DataFrame([
        {"id": 1, "name": "Pizza", "description": "Authentic wood-fired and pan-crust pizzas"},
        {"id": 2, "name": "Burger", "description": "Juicy grilled gourmet burgers"},
        {"id": 3, "name": "Biryani", "description": "Traditional aromatic dum biryani"},
        {"id": 4, "name": "Drinks", "description": "Refreshing chilled beverages and mocktails"},
        {"id": 5, "name": "Desserts", "description": "Gourmet pastries, brownies, and ice creams"}
    ])

    menu_df = pd.DataFrame([
        {"id": 1, "name": "Margherita Supreme", "price": 299.00, "category_id": 1, "is_available": True},
        {"id": 2, "name": "Paneer Tikka Pizza", "price": 349.00, "category_id": 1, "is_available": True},
        {"id": 3, "name": "Chicken Feast Pizza", "price": 399.00, "category_id": 1, "is_available": True},
        {"id": 4, "name": "Veggie Paradise", "price": 329.00, "category_id": 1, "is_available": True},
        {"id": 5, "name": "Classic Veg Crispy Burger", "price": 149.00, "category_id": 2, "is_available": True},
        {"id": 6, "name": "Grilled Chicken Burger", "price": 219.00, "category_id": 2, "is_available": True},
        {"id": 7, "name": "Double Cheese Delight Burger", "price": 189.00, "category_id": 2, "is_available": True},
        {"id": 8, "name": "Spicy Paneer Crunch Burger", "price": 199.00, "category_id": 2, "is_available": True},
        {"id": 9, "name": "Hyderabadi Chicken Dum Biryani", "price": 280.00, "category_id": 3, "is_available": True},
        {"id": 10, "name": "Mutton Royal Dum Biryani", "price": 360.00, "category_id": 3, "is_available": True},
        {"id": 11, "name": "Special Veg Biryani", "price": 220.00, "category_id": 3, "is_available": True},
        {"id": 12, "name": "Egg Biryani Feast", "price": 210.00, "category_id": 3, "is_available": True},
        {"id": 13, "name": "Fresh Mint Mojito", "price": 99.00, "category_id": 4, "is_available": True},
        {"id": 14, "name": "Mango Lassi", "price": 89.00, "category_id": 4, "is_available": True},
        {"id": 15, "name": "Iced Cold Coffee", "price": 119.00, "category_id": 4, "is_available": True},
        {"id": 16, "name": "Sizzling Chocolate Brownie", "price": 159.00, "category_id": 5, "is_available": True},
        {"id": 17, "name": "Gulab Jamun with Ice Cream", "price": 109.00, "category_id": 5, "is_available": True},
        {"id": 18, "name": "Royal Kulfi Falooda", "price": 139.00, "category_id": 5, "is_available": True}
    ])

    users_df = pd.DataFrame([
        {"id": 1, "name": "System Administrator", "email": "admin@foodhub.com", "role": "ADMIN", "created_at": "2026-10-01 09:00:00"},
        {"id": 2, "name": "Rahul Sharma", "email": "rahul@gmail.com", "role": "CUSTOMER", "created_at": "2026-10-05 10:30:00"},
        {"id": 3, "name": "Priya Sundaram", "email": "priya@gmail.com", "role": "CUSTOMER", "created_at": "2026-10-10 14:15:00"},
        {"id": 4, "name": "Anand Kumar", "email": "anand@gmail.com", "role": "CUSTOMER", "created_at": "2026-10-15 18:45:00"}
    ])

    orders_df = pd.DataFrame([
        {"id": 1, "order_number": "ORD-20261101-001", "user_id": 2, "total_amount": 648.00, "status": "COMPLETED", "delivery_address": "Anna Nagar", "order_date": "2026-11-01 12:45:00"},
        {"id": 2, "order_number": "ORD-20261102-002", "user_id": 3, "total_amount": 479.00, "status": "COMPLETED", "delivery_address": "OMR", "order_date": "2026-11-02 19:30:00"},
        {"id": 3, "order_number": "ORD-20261103-003", "user_id": 4, "total_amount": 380.00, "status": "COMPLETED", "delivery_address": "Velachery", "order_date": "2026-11-03 20:10:00"},
        {"id": 4, "order_number": "ORD-20261105-004", "user_id": 2, "total_amount": 858.00, "status": "COMPLETED", "delivery_address": "Anna Nagar", "order_date": "2026-11-05 13:20:00"},
        {"id": 5, "order_number": "ORD-20261107-005", "user_id": 3, "total_amount": 299.00, "status": "COMPLETED", "delivery_address": "OMR", "order_date": "2026-11-07 20:00:00"},
        {"id": 6, "order_number": "ORD-20261110-006", "user_id": 4, "total_amount": 538.00, "status": "COMPLETED", "delivery_address": "Velachery", "order_date": "2026-11-10 14:05:00"},
        {"id": 7, "order_number": "ORD-20261112-007", "user_id": 2, "total_amount": 469.00, "status": "COMPLETED", "delivery_address": "Anna Nagar", "order_date": "2026-11-12 18:35:00"},
        {"id": 8, "order_number": "ORD-20261115-008", "user_id": 3, "total_amount": 739.00, "status": "PREPARING", "delivery_address": "OMR", "order_date": "2026-11-15 19:15:00"},
        {"id": 9, "order_number": "ORD-20261117-009", "user_id": 4, "total_amount": 399.00, "status": "PLACED", "delivery_address": "Velachery", "order_date": "2026-11-17 12:30:00"},
        {"id": 10, "order_number": "ORD-20261118-010", "user_id": 2, "total_amount": 248.00, "status": "PLACED", "delivery_address": "Anna Nagar", "order_date": "2026-11-18 08:30:00"}
    ])

    order_items_df = pd.DataFrame([
        {"id": 1, "order_id": 1, "menu_id": 1, "quantity": 1, "unit_price": 299.00, "subtotal": 299.00},
        {"id": 2, "order_id": 1, "menu_id": 2, "quantity": 1, "unit_price": 349.00, "subtotal": 349.00},
        {"id": 3, "order_id": 2, "menu_id": 9, "quantity": 1, "unit_price": 280.00, "subtotal": 280.00},
        {"id": 4, "order_id": 2, "menu_id": 8, "quantity": 1, "unit_price": 199.00, "subtotal": 199.00},
        {"id": 5, "order_id": 3, "menu_id": 9, "quantity": 1, "unit_price": 280.00, "subtotal": 280.00},
        {"id": 6, "order_id": 3, "menu_id": 13, "quantity": 1, "unit_price": 100.00, "subtotal": 100.00},
        {"id": 7, "order_id": 4, "menu_id": 10, "quantity": 1, "unit_price": 360.00, "subtotal": 360.00},
        {"id": 8, "order_id": 4, "menu_id": 3, "quantity": 1, "unit_price": 399.00, "subtotal": 399.00},
        {"id": 9, "order_id": 4, "menu_id": 13, "quantity": 1, "unit_price": 99.00, "subtotal": 99.00},
        {"id": 10, "order_id": 5, "menu_id": 1, "quantity": 1, "unit_price": 299.00, "subtotal": 299.00},
        {"id": 11, "order_id": 6, "menu_id": 9, "quantity": 1, "unit_price": 280.00, "subtotal": 280.00},
        {"id": 12, "order_id": 6, "menu_id": 16, "quantity": 1, "unit_price": 159.00, "subtotal": 159.00},
        {"id": 13, "order_id": 6, "menu_id": 13, "quantity": 1, "unit_price": 99.00, "subtotal": 99.00},
        {"id": 14, "order_id": 7, "menu_id": 2, "quantity": 1, "unit_price": 349.00, "subtotal": 349.00},
        {"id": 15, "order_id": 7, "menu_id": 15, "quantity": 1, "unit_price": 120.00, "subtotal": 120.00},
        {"id": 16, "order_id": 8, "menu_id": 3, "quantity": 1, "unit_price": 399.00, "subtotal": 399.00},
        {"id": 17, "order_id": 8, "menu_id": 9, "quantity": 1, "unit_price": 280.00, "subtotal": 280.00},
        {"id": 18, "order_id": 8, "menu_id": 14, "quantity": 1, "unit_price": 60.00, "subtotal": 60.00},
        {"id": 19, "order_id": 9, "menu_id": 3, "quantity": 1, "unit_price": 399.00, "subtotal": 399.00},
        {"id": 20, "order_id": 10, "menu_id": 5, "quantity": 1, "unit_price": 149.00, "subtotal": 149.00},
        {"id": 21, "order_id": 10, "menu_id": 13, "quantity": 1, "unit_price": 99.00, "subtotal": 99.00}
    ])

    return orders_df, order_items_df, menu_df, categories_df, users_df

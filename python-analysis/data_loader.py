"""
FoodHub - Data Loader Module
Extracts real database records from MySQL or loads synchronized operational data.
Demonstrates: Pandas SQL extraction, Schema mapping, Graceful fallback.
"""
import pandas as pd
from database import get_db_connection

def load_data(conn=None):
    """
    Retrieves real datasets from the database:
    Returns dict containing: orders, order_items, menu, categories, users, payments, stock_movements
    """
    if conn is None:
        conn = get_db_connection()

    if conn is not None:
        try:
            orders_df = pd.read_sql("SELECT * FROM orders", conn)
            order_items_df = pd.read_sql("SELECT * FROM order_items", conn)
            menu_df = pd.read_sql("SELECT * FROM menu", conn)
            categories_df = pd.read_sql("SELECT * FROM categories", conn)
            users_df = pd.read_sql("SELECT id, name, email, phone, role, status, created_at FROM users", conn)
            payments_df = pd.read_sql("SELECT * FROM payments", conn)
            try:
                stock_movements_df = pd.read_sql("SELECT * FROM stock_movements", conn)
            except Exception:
                stock_movements_df = pd.DataFrame()

            print(f"[FDS DataLoader] Successfully loaded {len(orders_df)} orders and {len(menu_df)} products from MySQL.")
            return {
                "orders": orders_df,
                "order_items": order_items_df,
                "menu": menu_df,
                "categories": categories_df,
                "users": users_df,
                "payments": payments_df,
                "stock_movements": stock_movements_df
            }
        except Exception as e:
            print(f"[FDS DataLoader Warning] SQL query exception: {e}")

    # Synchronized Operational Dataset (Matching database.sql exactly)
    print("[FDS DataLoader] Loading synchronized transactional dataset for analysis.")

    categories_df = pd.DataFrame([
        {"id": 1, "name": "Pizza", "description": "Authentic wood-fired and pan-crust pizzas"},
        {"id": 2, "name": "Burger", "description": "Juicy grilled gourmet burgers"},
        {"id": 3, "name": "Biryani", "description": "Traditional aromatic dum biryani"},
        {"id": 4, "name": "Drinks", "description": "Refreshing chilled beverages and mocktails"},
        {"id": 5, "name": "Desserts", "description": "Gourmet pastries, brownies, and ice creams"}
    ])

    menu_df = pd.DataFrame([
        {"id": 1, "name": "Margherita Supreme", "price": 299.00, "category_id": 1, "stock_quantity": 45, "min_stock_level": 10, "is_available": True},
        {"id": 2, "name": "Paneer Tikka Pizza", "price": 349.00, "category_id": 1, "stock_quantity": 30, "min_stock_level": 10, "is_available": True},
        {"id": 3, "name": "Chicken Feast Pizza", "price": 399.00, "category_id": 1, "stock_quantity": 25, "min_stock_level": 10, "is_available": True},
        {"id": 4, "name": "Veggie Paradise", "price": 329.00, "category_id": 1, "stock_quantity": 8, "min_stock_level": 10, "is_available": True},
        {"id": 5, "name": "Classic Veg Crispy Burger", "price": 149.00, "category_id": 2, "stock_quantity": 50, "min_stock_level": 12, "is_available": True},
        {"id": 6, "name": "Grilled Chicken Burger", "price": 219.00, "category_id": 2, "stock_quantity": 28, "min_stock_level": 10, "is_available": True},
        {"id": 7, "name": "Double Cheese Delight Burger", "price": 189.00, "category_id": 2, "stock_quantity": 6, "min_stock_level": 10, "is_available": True},
        {"id": 8, "name": "Spicy Paneer Crunch Burger", "price": 199.00, "category_id": 2, "stock_quantity": 35, "min_stock_level": 10, "is_available": True},
        {"id": 9, "name": "Hyderabadi Chicken Dum Biryani", "price": 280.00, "category_id": 3, "stock_quantity": 60, "min_stock_level": 15, "is_available": True},
        {"id": 10, "name": "Mutton Royal Dum Biryani", "price": 360.00, "category_id": 3, "stock_quantity": 18, "min_stock_level": 8, "is_available": True},
        {"id": 11, "name": "Special Veg Biryani", "price": 220.00, "category_id": 3, "stock_quantity": 22, "min_stock_level": 10, "is_available": True},
        {"id": 12, "name": "Egg Biryani Feast", "price": 210.00, "category_id": 3, "stock_quantity": 0, "min_stock_level": 10, "is_available": False},
        {"id": 13, "name": "Fresh Mint Mojito", "price": 99.00, "category_id": 4, "stock_quantity": 70, "min_stock_level": 15, "is_available": True},
        {"id": 14, "name": "Mango Lassi", "price": 89.00, "category_id": 4, "stock_quantity": 40, "min_stock_level": 10, "is_available": True},
        {"id": 15, "name": "Iced Cold Coffee", "price": 119.00, "category_id": 4, "stock_quantity": 32, "min_stock_level": 10, "is_available": True},
        {"id": 16, "name": "Sizzling Chocolate Brownie", "price": 159.00, "category_id": 5, "stock_quantity": 20, "min_stock_level": 8, "is_available": True},
        {"id": 17, "name": "Gulab Jamun with Ice Cream", "price": 109.00, "category_id": 5, "stock_quantity": 25, "min_stock_level": 8, "is_available": True},
        {"id": 18, "name": "Royal Kulfi Falooda", "price": 139.00, "category_id": 5, "stock_quantity": 15, "min_stock_level": 6, "is_available": True}
    ])

    users_df = pd.DataFrame([
        {"id": 1, "name": "System Administrator", "email": "admin@foodhub.com", "phone": "9876543210", "role": "ADMIN", "status": "ACTIVE", "created_at": "2026-10-01 09:00:00"},
        {"id": 2, "name": "Rahul Sharma", "email": "rahul@gmail.com", "phone": "9840123456", "role": "CUSTOMER", "status": "ACTIVE", "created_at": "2026-10-05 10:30:00"},
        {"id": 3, "name": "Priya Sundaram", "email": "priya@gmail.com", "phone": "9840654321", "role": "CUSTOMER", "status": "ACTIVE", "created_at": "2026-10-10 14:15:00"},
        {"id": 4, "name": "Anand Kumar", "email": "anand@gmail.com", "phone": "9840987654", "role": "CUSTOMER", "status": "ACTIVE", "created_at": "2026-10-15 18:45:00"}
    ])

    orders_df = pd.DataFrame([
        {"id": 1, "order_number": "ORD-20261101-001", "user_id": 2, "total_amount": 648.00, "status": "DELIVERED", "delivery_address": "No. 12, Anna Nagar West, Chennai", "cancellation_reason": None, "order_date": "2026-11-01 12:45:00"},
        {"id": 2, "order_number": "ORD-20261102-002", "user_id": 3, "total_amount": 479.00, "status": "DELIVERED", "delivery_address": "Flat 4B, Green Acres, OMR, Chennai", "cancellation_reason": None, "order_date": "2026-11-02 19:30:00"},
        {"id": 3, "order_number": "ORD-20261103-003", "user_id": 4, "total_amount": 380.00, "status": "DELIVERED", "delivery_address": "Plot 55, Velachery Main Road, Chennai", "cancellation_reason": None, "order_date": "2026-11-03 20:10:00"},
        {"id": 4, "order_number": "ORD-20261105-004", "user_id": 2, "total_amount": 858.00, "status": "DELIVERED", "delivery_address": "No. 12, Anna Nagar West, Chennai", "cancellation_reason": None, "order_date": "2026-11-05 13:20:00"},
        {"id": 5, "order_number": "ORD-20261107-005", "user_id": 3, "total_amount": 299.00, "status": "DELIVERED", "delivery_address": "Flat 4B, Green Acres, OMR, Chennai", "cancellation_reason": None, "order_date": "2026-11-07 20:00:00"},
        {"id": 6, "order_number": "ORD-20261110-006", "user_id": 4, "total_amount": 538.00, "status": "DELIVERED", "delivery_address": "Plot 55, Velachery Main Road, Chennai", "cancellation_reason": None, "order_date": "2026-11-10 14:05:00"},
        {"id": 7, "order_number": "ORD-20261112-007", "user_id": 2, "total_amount": 469.00, "status": "CANCELLED", "delivery_address": "No. 12, Anna Nagar West, Chennai", "cancellation_reason": "Customer requested change of address", "order_date": "2026-11-12 18:35:00"},
        {"id": 8, "order_number": "ORD-20261115-008", "user_id": 3, "total_amount": 739.00, "status": "OUT_FOR_DELIVERY", "delivery_address": "Flat 4B, Green Acres, OMR, Chennai", "cancellation_reason": None, "order_date": "2026-11-15 19:15:00"},
        {"id": 9, "order_number": "ORD-20261117-009", "user_id": 4, "total_amount": 399.00, "status": "PREPARING", "delivery_address": "Plot 55, Velachery Main Road, Chennai", "cancellation_reason": None, "order_date": "2026-11-17 12:30:00"},
        {"id": 10, "order_number": "ORD-20261118-010", "user_id": 2, "total_amount": 248.00, "status": "CONFIRMED", "delivery_address": "No. 12, Anna Nagar West, Chennai", "cancellation_reason": None, "order_date": "2026-11-18 08:30:00"},
        {"id": 11, "order_number": "ORD-20261118-011", "user_id": 3, "total_amount": 349.00, "status": "PENDING", "delivery_address": "Flat 4B, Green Acres, OMR, Chennai", "cancellation_reason": None, "order_date": "2026-11-18 10:15:00"},
        {"id": 12, "order_number": "ORD-20261118-012", "user_id": 4, "total_amount": 329.00, "status": "CANCELLED", "delivery_address": "Plot 55, Velachery Main Road, Chennai", "cancellation_reason": "Item mistakenly ordered", "order_date": "2026-11-18 10:30:00"}
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
        {"id": 21, "order_id": 10, "menu_id": 13, "quantity": 1, "unit_price": 99.00, "subtotal": 99.00},
        {"id": 22, "order_id": 11, "menu_id": 2, "quantity": 1, "unit_price": 349.00, "subtotal": 349.00},
        {"id": 23, "order_id": 12, "menu_id": 4, "quantity": 1, "unit_price": 329.00, "subtotal": 329.00}
    ])

    payments_df = pd.DataFrame([
        {"id": 1, "order_id": 1, "payment_method": "UPI", "transaction_reference": "TXN-UPI-20261101-9011", "amount": 648.00, "status": "SUCCESS"},
        {"id": 2, "order_id": 2, "payment_method": "CARD", "transaction_reference": "TXN-CRD-20261102-8822", "amount": 479.00, "status": "SUCCESS"},
        {"id": 3, "order_id": 3, "payment_method": "UPI", "transaction_reference": "TXN-UPI-20261103-7733", "amount": 380.00, "status": "SUCCESS"},
        {"id": 4, "order_id": 4, "payment_method": "CARD", "transaction_reference": "TXN-CRD-20261105-6644", "amount": 858.00, "status": "SUCCESS"},
        {"id": 5, "order_id": 5, "payment_method": "UPI", "transaction_reference": "TXN-UPI-20261107-5555", "amount": 299.00, "status": "SUCCESS"},
        {"id": 6, "order_id": 6, "payment_method": "CASH_ON_DELIVERY", "transaction_reference": "TXN-COD-20261110-4466", "amount": 538.00, "status": "SUCCESS"},
        {"id": 7, "order_id": 7, "payment_method": "UPI", "transaction_reference": "TXN-UPI-20261112-3377", "amount": 469.00, "status": "REFUNDED"},
        {"id": 8, "order_id": 8, "payment_method": "CARD", "transaction_reference": "TXN-CRD-20261115-2288", "amount": 739.00, "status": "SUCCESS"},
        {"id": 9, "order_id": 9, "payment_method": "UPI", "transaction_reference": "TXN-UPI-20261117-1199", "amount": 399.00, "status": "SUCCESS"},
        {"id": 10, "order_id": 10, "payment_method": "UPI", "transaction_reference": "TXN-UPI-20261118-0010", "amount": 248.00, "status": "SUCCESS"},
        {"id": 11, "order_id": 11, "payment_method": "UPI", "transaction_reference": "TXN-UPI-20261118-0011", "amount": 349.00, "status": "PENDING"},
        {"id": 12, "order_id": 12, "payment_method": "CARD", "transaction_reference": "TXN-CRD-20261118-0012", "amount": 329.00, "status": "REFUNDED"}
    ])

    stock_movements_df = pd.DataFrame([
        {"id": 1, "menu_id": 1, "movement_type": "INITIAL", "quantity_changed": 50, "quantity_after": 50, "reason": "Opening balance"},
        {"id": 2, "menu_id": 2, "movement_type": "ORDER_CANCEL_RESTORE", "quantity_changed": 1, "quantity_after": 30, "reason": "Restored from cancelled ORD-20261112-007"}
    ])

    return {
        "orders": orders_df,
        "order_items": order_items_df,
        "menu": menu_df,
        "categories": categories_df,
        "users": users_df,
        "payments": payments_df,
        "stock_movements": stock_movements_df
    }

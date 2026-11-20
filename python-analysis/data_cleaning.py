"""
FoodHub - Data Cleaning & Preprocessing Module
Demonstrates: Pandas Data Preprocessing, shape inspection, null handling,
              type conversions, deduplication, anomaly filtering, and feature extraction.
"""
import pandas as pd
import numpy as np

def clean_data(data_dict):
    """
    Cleans and preprocesses relational DataFrames.
    Input: dictionary containing orders, order_items, menu, categories, users, payments, stock_movements
    Returns: cleaned dictionary of DataFrames
    """
    print("\n=======================================================")
    print(" FDS MODULE: DATA CLEANING & PREPROCESSING (Pandas)    ")
    print("=======================================================")

    orders_df = data_dict.get("orders", pd.DataFrame()).copy()
    order_items_df = data_dict.get("order_items", pd.DataFrame()).copy()
    menu_df = data_dict.get("menu", pd.DataFrame()).copy()
    categories_df = data_dict.get("categories", pd.DataFrame()).copy()
    users_df = data_dict.get("users", pd.DataFrame()).copy()
    payments_df = data_dict.get("payments", pd.DataFrame()).copy()
    stock_movements_df = data_dict.get("stock_movements", pd.DataFrame()).copy()

    # 1. Inspect Shapes
    print(f"[*] Raw Orders Shape: {orders_df.shape}")
    print(f"[*] Raw Order Items Shape: {order_items_df.shape}")
    print(f"[*] Raw Menu Shape: {menu_df.shape}")
    print(f"[*] Raw Categories Shape: {categories_df.shape}")

    # 2. Orders Deduplication & Type Casting
    if not orders_df.empty:
        initial_orders = len(orders_df)
        orders_df = orders_df.drop_duplicates(subset=['id'])
        print(f"[*] Deduplicated orders: removed {initial_orders - len(orders_df)} duplicates.")

        orders_df['order_date'] = pd.to_datetime(orders_df['order_date'], errors='coerce')
        orders_df['total_amount'] = pd.to_numeric(orders_df['total_amount'], errors='coerce').fillna(0.0)
        orders_df['status'] = orders_df['status'].astype(str).str.upper().str.strip()
        orders_df['cancellation_reason'] = orders_df.get('cancellation_reason', pd.Series(dtype=object)).fillna("No reason specified")

        # Feature Engineering for temporal analysis
        orders_df['order_day'] = orders_df['order_date'].dt.date
        orders_df['order_month'] = orders_df['order_date'].dt.to_period('M').astype(str)
        orders_df['order_hour'] = orders_df['order_date'].dt.hour
        orders_df['day_name'] = orders_df['order_date'].dt.day_name()

        # Remove negative total anomalies
        orders_df = orders_df[orders_df['total_amount'] >= 0]

    # 3. Order Items Cleaning
    if not order_items_df.empty:
        order_items_df['quantity'] = pd.to_numeric(order_items_df['quantity'], errors='coerce').fillna(1).astype(int)
        order_items_df['unit_price'] = pd.to_numeric(order_items_df['unit_price'], errors='coerce').fillna(0.0)
        order_items_df['subtotal'] = pd.to_numeric(order_items_df['subtotal'], errors='coerce').fillna(0.0)
        order_items_df = order_items_df[order_items_df['quantity'] > 0]

    # 4. Menu Cleaning (with Inventory quantities)
    if not menu_df.empty:
        menu_df['price'] = pd.to_numeric(menu_df['price'], errors='coerce').fillna(0.0)
        menu_df['stock_quantity'] = pd.to_numeric(menu_df.get('stock_quantity', pd.Series(50, index=menu_df.index)), errors='coerce').fillna(0).astype(int)
        menu_df['min_stock_level'] = pd.to_numeric(menu_df.get('min_stock_level', pd.Series(10, index=menu_df.index)), errors='coerce').fillna(10).astype(int)
        
        # Categorize stock status
        def categorize_stock(row):
            qty = row['stock_quantity']
            min_l = row['min_stock_level']
            if qty <= 0:
                return 'Out of Stock'
            elif qty <= min_l:
                return 'Low Stock'
            else:
                return 'In Stock'
        
        menu_df['stock_status'] = menu_df.apply(categorize_stock, axis=1)

    print("[*] Data cleaning and type conversion successfully completed.\n")

    return {
        "orders": orders_df,
        "order_items": order_items_df,
        "menu": menu_df,
        "categories": categories_df,
        "users": users_df,
        "payments": payments_df,
        "stock_movements": stock_movements_df
    }

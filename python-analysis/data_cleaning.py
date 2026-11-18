"""
Data Cleaning Module for FoodHub Python Analytics.
Demonstrates: Pandas Data Preprocessing, head(), shape, isnull(),
              type casting, deduplication, and anomaly filtering.
"""
import pandas as pd
import numpy as np

def clean_data(orders_df, order_items_df, menu_df, categories_df, users_df):
    """
    Cleans, verifies, and transforms relational data into structured analytical DataFrames.
    """
    print("\n=======================================================")
    print(" FDS MODULE 1: DATA INSPECTION & PREPROCESSING         ")
    print("=======================================================")

    # 1. Inspect Shapes and Structures
    print(f"[*] Orders DataFrame Shape: {orders_df.shape}")
    print(f"[*] Order Items DataFrame Shape: {order_items_df.shape}")
    print(f"[*] Menu DataFrame Shape: {menu_df.shape}")
    print(f"[*] Categories DataFrame Shape: {categories_df.shape}")

    # 2. Check Missing / Null Values
    print("\n[Missing Values Check]")
    print("Orders null values:\n", orders_df.isnull().sum())
    print("Order Items null values:\n", order_items_df.isnull().sum())

    # 3. Deduplication
    initial_orders = len(orders_df)
    orders_df = orders_df.drop_duplicates(subset=['id'])
    print(f"[*] Deduplication complete: {initial_orders - len(orders_df)} duplicate orders removed.")

    # 4. Data Type Conversions
    orders_df['order_date'] = pd.to_datetime(orders_df['order_date'])
    orders_df['total_amount'] = pd.to_numeric(orders_df['total_amount'], errors='coerce')
    order_items_df['quantity'] = pd.to_numeric(order_items_df['quantity'], errors='coerce').fillna(1).astype(int)
    order_items_df['unit_price'] = pd.to_numeric(order_items_df['unit_price'], errors='coerce')
    order_items_df['subtotal'] = pd.to_numeric(order_items_df['subtotal'], errors='coerce')
    menu_df['price'] = pd.to_numeric(menu_df['price'], errors='coerce')

    # 5. Anomaly Filtering (Only valid amounts and quantities)
    orders_df = orders_df[orders_df['total_amount'] >= 0]
    order_items_df = order_items_df[order_items_df['quantity'] > 0]

    # 6. Feature Engineering for Time-Series Analysis
    orders_df['order_day'] = orders_df['order_date'].dt.date
    orders_df['order_month'] = orders_df['order_date'].dt.to_period('M')
    orders_df['order_hour'] = orders_df['order_date'].dt.hour
    orders_df['day_name'] = orders_df['order_date'].dt.day_name()

    print("\n[Data Inspection Sample - Orders Head (5 rows)]:")
    print(orders_df[['order_number', 'total_amount', 'status', 'order_day', 'day_name']].head())

    return orders_df, order_items_df, menu_df, categories_df, users_df

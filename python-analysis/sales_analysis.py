"""
FoodHub – Sales Analysis Module (NumPy + Pandas)
Demonstrates: Descriptive statistics, aggregation metrics, business KPIs.
"""
import pandas as pd
import numpy as np

def analyze_sales(data):
    """
    Performs comprehensive sales analysis using NumPy and Pandas.
    Returns a dictionary of calculated metrics.
    """
    orders_df = data.get("orders", pd.DataFrame())
    order_items_df = data.get("order_items", pd.DataFrame())
    menu_df = data.get("menu", pd.DataFrame())
    categories_df = data.get("categories", pd.DataFrame())
    users_df = data.get("users", pd.DataFrame())

    if orders_df.empty:
        print("[Sales Analysis] No order data available.")
        return {}

    print("\n=======================================================")
    print(" FDS MODULE: SALES ANALYSIS (NumPy + Pandas)           ")
    print("=======================================================")

    # --- RULE: Exclude Cancelled orders from completed sales revenue ---
    completed_orders = orders_df[orders_df['status'].isin(['DELIVERED'])]
    active_orders = orders_df[~orders_df['status'].isin(['CANCELLED'])]

    # 1. Total Revenue from DELIVERED orders only
    total_revenue = completed_orders['total_amount'].sum()
    print(f"\n[1] Total Completed Revenue:  ₹{total_revenue:,.2f}")
    print(f"    Calculation: SUM(total_amount) WHERE status = 'DELIVERED'")

    # 2. Total Order Count
    total_orders = len(orders_df)
    completed_count = len(completed_orders)
    cancelled_count = len(orders_df[orders_df['status'] == 'CANCELLED'])
    print(f"\n[2] Total Orders:   {total_orders}")
    print(f"    Completed (DELIVERED): {completed_count}")
    print(f"    Cancelled:             {cancelled_count}")

    # 3. Average Order Value (NumPy mean)
    if not completed_orders.empty:
        order_values = completed_orders['total_amount'].values
        avg_order_value = np.mean(order_values)
        median_order_value = np.median(order_values)
        std_order_value = np.std(order_values)
        print(f"\n[3] Order Value Statistics (DELIVERED orders only):")
        print(f"    Mean:   ₹{avg_order_value:,.2f}  (Calculation: np.mean(total_amount))")
        print(f"    Median: ₹{median_order_value:,.2f}  (Calculation: np.median(total_amount))")
        print(f"    Std Dev:₹{std_order_value:,.2f}  (Calculation: np.std(total_amount))")
    else:
        avg_order_value = 0
        print("[3] No completed orders to analyze.")

    # 4. Order Cancellation Percentage
    cancellation_rate = (cancelled_count / total_orders * 100) if total_orders > 0 else 0
    print(f"\n[4] Cancellation Rate: {cancellation_rate:.2f}%")
    print(f"    Calculation: (cancelled_count / total_orders) * 100")

    # 5. Best-selling and Least-selling Products
    if not order_items_df.empty and not menu_df.empty:
        delivered_order_ids = completed_orders['id'].tolist()
        delivered_items = order_items_df[order_items_df['order_id'].isin(delivered_order_ids)]

        if not delivered_items.empty:
            item_sales = delivered_items.groupby('menu_id')['quantity'].sum()
            item_revenue = delivered_items.groupby('menu_id')['subtotal'].sum()

            # Merge with menu names
            item_sales_df = item_sales.reset_index()
            item_sales_df.columns = ['menu_id', 'total_qty']
            item_sales_df = item_sales_df.merge(menu_df[['id', 'name']], left_on='menu_id', right_on='id', how='left')

            best_seller = item_sales_df.loc[item_sales_df['total_qty'].idxmax()]
            least_seller = item_sales_df.loc[item_sales_df['total_qty'].idxmin()]

            print(f"\n[5] Best-Selling Product:  {best_seller['name']} ({int(best_seller['total_qty'])} units)")
            print(f"    Least-Selling Product: {least_seller['name']} ({int(least_seller['total_qty'])} units)")

    # 6. Highest Revenue-Generating Category
    if not order_items_df.empty and not menu_df.empty and not categories_df.empty:
        delivered_order_ids = completed_orders['id'].tolist()
        delivered_items = order_items_df[order_items_df['order_id'].isin(delivered_order_ids)]

        if not delivered_items.empty:
            merged = delivered_items.merge(menu_df[['id', 'category_id']], left_on='menu_id', right_on='id', how='left')
            merged = merged.merge(categories_df[['id', 'name']], left_on='category_id', right_on='id', how='left')
            merged.rename(columns={'name': 'category_name'}, inplace=True)
            cat_revenue = merged.groupby('category_name')['subtotal'].sum()
            top_category = cat_revenue.idxmax()
            print(f"\n[6] Top Revenue Category: {top_category} (₹{cat_revenue[top_category]:,.2f})")

    # 7. Average Daily Sales (NumPy)
    if not completed_orders.empty and 'order_day' in completed_orders.columns:
        daily_rev = completed_orders.groupby('order_day')['total_amount'].sum()
        avg_daily_sales = np.mean(daily_rev.values)
        print(f"\n[7] Average Daily Sales: ₹{avg_daily_sales:,.2f}")

    # 8. Customer Purchase Frequency
    if not orders_df.empty and not users_df.empty:
        customer_orders = completed_orders.groupby('user_id').size().reset_index(name='order_count')
        avg_frequency = np.mean(customer_orders['order_count'].values) if len(customer_orders) > 0 else 0
        print(f"\n[8] Average Orders per Customer: {avg_frequency:.2f}")

    # 9. Low-Stock product count
    if not menu_df.empty and 'stock_quantity' in menu_df.columns and 'min_stock_level' in menu_df.columns:
        low_stock = menu_df[(menu_df['stock_quantity'] > 0) & (menu_df['stock_quantity'] <= menu_df['min_stock_level'])]
        out_of_stock = menu_df[menu_df['stock_quantity'] == 0]
        print(f"\n[9] Inventory Status:")
        print(f"    Low Stock Products:     {len(low_stock)}")
        print(f"    Out-of-Stock Products:  {len(out_of_stock)}")

    print("\n[Sales Analysis Complete] All metrics calculated from actual FoodHub data.\n")

    return {
        "total_revenue": total_revenue,
        "total_orders": total_orders,
        "completed_orders": completed_count,
        "cancelled_orders": cancelled_count,
        "avg_order_value": avg_order_value,
        "cancellation_rate": cancellation_rate
    }

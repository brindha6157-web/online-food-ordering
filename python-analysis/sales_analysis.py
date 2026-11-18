"""
Sales Analysis Module for FoodHub Python Analytics.
Demonstrates: Pandas groupby(), aggregations, pivot_table(),
              NumPy descriptive statistics, and customer metrics.
"""
import pandas as pd
import numpy as np

def perform_sales_analysis(orders_df, order_items_df, menu_df, categories_df, users_df):
    """
    Performs comprehensive statistical sales analysis.
    """
    print("\n=======================================================")
    print(" FDS MODULE 2: STATISTICAL SALES ANALYSIS & METRICS    ")
    print("=======================================================")

    # Filter out cancelled orders for revenue reporting
    valid_orders = orders_df[orders_df['status'] != 'CANCELLED']

    # 1. High-Level Descriptive Statistics using NumPy & Pandas
    total_revenue = valid_orders['total_amount'].sum()
    total_orders_count = len(valid_orders)
    avg_order_value = valid_orders['total_amount'].mean()
    median_order_value = valid_orders['total_amount'].median()
    std_order_value = valid_orders['total_amount'].std()
    min_order_value = valid_orders['total_amount'].min()
    max_order_value = valid_orders['total_amount'].max()

    print(f"[*] Total Revenue:              INR {total_revenue:,.2f}")
    print(f"[*] Total Valid Orders:         {total_orders_count}")
    print(f"[*] Average Order Value (AOV):  INR {avg_order_value:.2f}")
    print(f"[*] Median Order Value:         INR {median_order_value:.2f}")
    print(f"[*] Standard Deviation:         INR {std_order_value:.2f}")
    print(f"[*] Min / Max Order Value:      INR {min_order_value:.2f} / INR {max_order_value:.2f}")

    # 2. Daily Sales Breakdown (groupby order_day)
    daily_sales = valid_orders.groupby('order_day').agg(
        order_count=('id', 'count'),
        daily_revenue=('total_amount', 'sum'),
        avg_revenue=('total_amount', 'mean')
    ).reset_index()

    print("\n[Daily Sales Summary]:")
    print(daily_sales)

    # 3. Monthly Sales Breakdown (groupby order_month)
    monthly_sales = valid_orders.groupby(valid_orders['order_date'].dt.strftime('%Y-%m')).agg(
        order_count=('id', 'count'),
        monthly_revenue=('total_amount', 'sum')
    ).reset_index()
    monthly_sales.rename(columns={'order_date': 'month'}, inplace=True)

    print("\n[Monthly Revenue Trend]:")
    print(monthly_sales)

    # 4. Most Popular Food Items (merge order_items with menu and categories)
    items_merged = order_items_df.merge(menu_df, left_on='menu_id', right_on='id', suffixes=('', '_menu'))
    items_merged = items_merged.merge(categories_df, left_on='category_id', right_on='id', suffixes=('', '_cat'))

    popular_items = items_merged.groupby(['name', 'name_cat']).agg(
        total_quantity=('quantity', 'sum'),
        total_revenue=('subtotal', 'sum')
    ).reset_index().sort_values(by='total_quantity', ascending=False)
    popular_items.rename(columns={'name_cat': 'category'}, inplace=True)

    print("\n[Top Popular Food Items]:")
    print(popular_items.head(5))

    # 5. Category-wise Sales Analysis
    category_sales = items_merged.groupby('category').agg(
        units_sold=('quantity', 'sum'),
        total_sales=('subtotal', 'sum')
    ).reset_index().sort_values(by='total_sales', ascending=False)

    print("\n[Category-wise Sales Breakdown]:")
    print(category_sales)

    # 6. Pivot Table: Day of Week vs Order Status
    pivot_day_status = pd.pivot_table(
        orders_df,
        values='id',
        index='day_name',
        columns='status',
        aggfunc='count',
        fill_value=0
    )
    print("\n[Pivot Table: Day of Week vs Order Status]:")
    print(pivot_day_status)

    # 7. Customer Order Frequency & Lifetime Value
    customer_summary = valid_orders.groupby('user_id').agg(
        total_orders=('id', 'count'),
        total_spent=('total_amount', 'sum'),
        avg_ticket=('total_amount', 'mean')
    ).reset_index()
    customer_summary = customer_summary.merge(users_df, left_on='user_id', right_on='id')
    customer_summary = customer_summary[['name', 'email', 'total_orders', 'total_spent', 'avg_ticket']]

    print("\n[Customer Lifetime Value Analysis]:")
    print(customer_summary)

    return {
        'total_revenue': total_revenue,
        'total_orders': total_orders_count,
        'avg_order_value': avg_order_value,
        'daily_sales': daily_sales,
        'monthly_sales': monthly_sales,
        'popular_items': popular_items,
        'category_sales': category_sales,
        'customer_summary': customer_summary,
        'orders_df': orders_df
    }

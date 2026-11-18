"""
Visualizations Module for FoodHub Python Analytics.
Demonstrates: Matplotlib chart generation (bar, pie, line, count charts),
              exporting high-resolution PNGs into generated-charts/ directory.
"""
import os
import matplotlib
matplotlib.use('Agg') # Headless backend for web server environment
import matplotlib.pyplot as plt

def generate_visualizations(analysis_results, output_dir=None):
    """
    Generates academic publication-quality Matplotlib charts.
    """
    if output_dir is None:
        output_dir = os.path.join(os.path.dirname(__file__), "generated-charts")
    os.makedirs(output_dir, exist_ok=True)

    print("\n=======================================================")
    print(" FDS MODULE 3: VISUALIZATION & CHART GENERATION        ")
    print(f"[*] Output Directory: {output_dir}")
    print("=======================================================")

    plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')

    # Color Palette: Restaurant Orange & Charcoal Theme
    primary_color = '#e65100'
    secondary_color = '#263238'
    accent_palette = ['#e65100', '#ff9800', '#f57c00', '#ffa726', '#ffb74d', '#ffe0b2']

    # -------------------------------------------------------------
    # CHART 1: Daily Sales Bar Chart
    # -------------------------------------------------------------
    daily_sales = analysis_results['daily_sales']
    fig, ax = plt.subplots(figsize=(10, 5), dpi=150)
    bars = ax.bar([str(d) for d in daily_sales['order_day']], daily_sales['daily_revenue'], color=primary_color, edgecolor='#b23c00', width=0.6)
    ax.set_title('FoodHub - Daily Sales Revenue (INR)', fontsize=14, fontweight='bold', pad=15, color=secondary_color)
    ax.set_xlabel('Date', fontsize=11, fontweight='bold')
    ax.set_ylabel('Revenue in INR', fontsize=11, fontweight='bold')
    ax.tick_params(axis='x', rotation=35)

    # Value labels on top of bars
    for bar in bars:
        height = bar.get_height()
        ax.annotate(f'₹{int(height)}',
                    xy=(bar.get_x() + bar.get_width() / 2, height),
                    xytext=(0, 3),
                    textcoords="offset points",
                    ha='center', va='bottom', fontsize=9, fontweight='bold')

    plt.tight_layout()
    chart1_path = os.path.join(output_dir, 'daily_sales.png')
    plt.savefig(chart1_path)
    plt.close()
    print(f"[+] Generated: {chart1_path}")

    # -------------------------------------------------------------
    # CHART 2: Most Popular Food Items (Horizontal Bar Chart)
    # -------------------------------------------------------------
    popular_items = analysis_results['popular_items'].head(8)
    fig, ax = plt.subplots(figsize=(10, 5), dpi=150)
    bars = ax.barh(popular_items['name'], popular_items['total_quantity'], color='#2e7d32', edgecolor='#1b5e20', height=0.6)
    ax.invert_yaxis() # Top item first
    ax.set_title('FoodHub - Top Most Popular Food Items (Units Sold)', fontsize=14, fontweight='bold', pad=15, color=secondary_color)
    ax.set_xlabel('Quantity Sold', fontsize=11, fontweight='bold')
    ax.set_ylabel('Food Item', fontsize=11, fontweight='bold')

    for bar in bars:
        width = bar.get_width()
        ax.annotate(f'{int(width)} units',
                    xy=(width, bar.get_y() + bar.get_height() / 2),
                    xytext=(5, 0),
                    textcoords="offset points",
                    ha='left', va='center', fontsize=9, fontweight='bold')

    plt.tight_layout()
    chart2_path = os.path.join(output_dir, 'popular_items.png')
    plt.savefig(chart2_path)
    plt.close()
    print(f"[+] Generated: {chart2_path}")

    # -------------------------------------------------------------
    # CHART 3: Category-wise Sales Distribution (Pie Chart)
    # -------------------------------------------------------------
    category_sales = analysis_results['category_sales']
    fig, ax = plt.subplots(figsize=(8, 6), dpi=150)
    wedges, texts, autotexts = ax.pie(
        category_sales['total_sales'],
        labels=category_sales['category'],
        autopct='%1.1f%%',
        startangle=140,
        colors=accent_palette[:len(category_sales)],
        wedgeprops=dict(width=0.7, edgecolor='white', linewidth=2)
    )
    for at in autotexts:
        at.set_color('#212121')
        at.set_fontweight('bold')
    ax.set_title('FoodHub - Category-wise Revenue Share (%)', fontsize=14, fontweight='bold', pad=15, color=secondary_color)

    plt.tight_layout()
    chart3_path = os.path.join(output_dir, 'category_sales.png')
    plt.savefig(chart3_path)
    plt.close()
    print(f"[+] Generated: {chart3_path}")

    # -------------------------------------------------------------
    # CHART 4: Monthly Revenue Trend (Line Chart)
    # -------------------------------------------------------------
    monthly_sales = analysis_results['monthly_sales']
    fig, ax = plt.subplots(figsize=(9, 4.5), dpi=150)
    ax.plot(monthly_sales['month'], monthly_sales['monthly_revenue'], marker='o', color='#d84315', linewidth=3, markersize=8)
    ax.set_title('FoodHub - Monthly Revenue Trajectory (INR)', fontsize=14, fontweight='bold', pad=15, color=secondary_color)
    ax.set_xlabel('Month', fontsize=11, fontweight='bold')
    ax.set_ylabel('Revenue in INR', fontsize=11, fontweight='bold')
    for x, y in zip(monthly_sales['month'], monthly_sales['monthly_revenue']):
        ax.annotate(f'₹{int(y)}', (x, y), textcoords="offset points", xytext=(0, 10), ha='center', fontweight='bold')

    plt.tight_layout()
    chart4_path = os.path.join(output_dir, 'monthly_revenue.png')
    plt.savefig(chart4_path)
    plt.close()
    print(f"[+] Generated: {chart4_path}")

    # -------------------------------------------------------------
    # CHART 5: Order Status Breakdown
    # -------------------------------------------------------------
    orders_df = analysis_results['orders_df']
    status_counts = orders_df['status'].value_counts()
    fig, ax = plt.subplots(figsize=(8, 4.5), dpi=150)
    status_colors = {'COMPLETED': '#4caf50', 'PREPARING': '#ff9800', 'PLACED': '#2196f3', 'CANCELLED': '#f44336'}
    colors = [status_colors.get(s, '#757575') for s in status_counts.index]
    bars = ax.bar(status_counts.index, status_counts.values, color=colors, edgecolor='#37474f', width=0.5)
    ax.set_title('FoodHub - Order Status Distribution', fontsize=14, fontweight='bold', pad=15, color=secondary_color)
    ax.set_xlabel('Status', fontsize=11, fontweight='bold')
    ax.set_ylabel('Number of Orders', fontsize=11, fontweight='bold')

    for bar in bars:
        h = bar.get_height()
        ax.annotate(f'{int(h)}', xy=(bar.get_x() + bar.get_width() / 2, h), xytext=(0, 3), textcoords="offset points", ha='center', fontweight='bold')

    plt.tight_layout()
    chart5_path = os.path.join(output_dir, 'order_status_distribution.png')
    plt.savefig(chart5_path)
    plt.close()
    print(f"[+] Generated: {chart5_path}")

    # -------------------------------------------------------------
    # CHART 6: Customer Purchase Analysis
    # -------------------------------------------------------------
    cust_summary = analysis_results['customer_summary']
    fig, ax = plt.subplots(figsize=(9, 4.5), dpi=150)
    bars = ax.bar(cust_summary['name'], cust_summary['total_spent'], color='#0288d1', edgecolor='#01579b', width=0.5)
    ax.set_title('FoodHub - Customer Lifetime Expenditure (INR)', fontsize=14, fontweight='bold', pad=15, color=secondary_color)
    ax.set_xlabel('Customer Name', fontsize=11, fontweight='bold')
    ax.set_ylabel('Total Spend (INR)', fontsize=11, fontweight='bold')
    ax.tick_params(axis='x', rotation=15)

    for bar in bars:
        h = bar.get_height()
        ax.annotate(f'₹{int(h)}', xy=(bar.get_x() + bar.get_width() / 2, h), xytext=(0, 3), textcoords="offset points", ha='center', fontweight='bold')

    plt.tight_layout()
    chart6_path = os.path.join(output_dir, 'customer_analysis.png')
    plt.savefig(chart6_path)
    plt.close()
    print(f"[+] Generated: {chart6_path}")

    print("\n[+] All 6 Analytical Matplotlib Visualizations Generated Successfully!")
    return [chart1_path, chart2_path, chart3_path, chart4_path, chart5_path, chart6_path]

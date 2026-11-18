"""
FoodHub - Master Python Analytical Runner Script
Foundations of Data Science (FDS) Module Execution
Performs: Database Extraction -> Data Cleaning -> Sales Analysis -> Chart Generation
"""
import sys
import os

# Add local directory to python path
current_dir = os.path.dirname(os.path.abspath(__file__))
if current_dir not in sys.path:
    sys.path.insert(0, current_dir)

from db_connection import get_db_connection, fetch_data_from_db
from data_cleaning import clean_data
from sales_analysis import perform_sales_analysis
from visualizations import generate_visualizations

def run_pipeline():
    print("====================================================================")
    print(" FOODHUB - RESTAURANT SALES DATA SCIENCE & ANALYTICS PIPELINE       ")
    print(" Academic Course: B.Tech Information Technology (FDS & DBMS)        ")
    print("====================================================================")

    # Step 1: Connect to MySQL Database
    conn = get_db_connection()

    # Step 2: Fetch Data
    orders_df, order_items_df, menu_df, categories_df, users_df = fetch_data_from_db(conn)

    # Step 3: Data Cleaning and Preprocessing
    orders_df, order_items_df, menu_df, categories_df, users_df = clean_data(
        orders_df, order_items_df, menu_df, categories_df, users_df
    )

    # Step 4: Statistical Sales Analysis
    analysis_results = perform_sales_analysis(
        orders_df, order_items_df, menu_df, categories_df, users_df
    )

    # Step 5: Visualizations & Chart Generation
    output_dir = os.path.join(current_dir, "generated-charts")
    charts = generate_visualizations(analysis_results, output_dir=output_dir)

    # Close DB connection if open
    if conn:
        try:
            conn.close()
            print("[*] MySQL connection safely closed.")
        except Exception:
            pass

    print("\n====================================================================")
    print(" ANALYTICS PIPELINE EXECUTION COMPLETED SUCCESSFULLY!                ")
    print(f" Charts generated: {len(charts)} in {output_dir}")
    print("====================================================================")

if __name__ == "__main__":
    run_pipeline()

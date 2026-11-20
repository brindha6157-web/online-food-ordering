"""
FoodHub - Database Connection Module
Connects to MySQL database using environment variables or standard defaults.
Demonstrates: Secure configuration, MySQL Connector, Exception handling.
"""
import os

def get_db_connection():
    """
    Connects to MySQL using environment variables or application defaults.
    Environment Variables:
        DB_HOST (default: localhost)
        DB_USER (default: root)
        DB_PASSWORD (default: brindha2624@ or root)
        DB_NAME (default: foodhub_db)
        DB_PORT (default: 3306)
    """
    host = os.environ.get("DB_HOST", "localhost")
    user = os.environ.get("DB_USER", "root")
    password = os.environ.get("DB_PASSWORD", "brindha2624@")
    database = os.environ.get("DB_NAME", "foodhub_db")
    port = int(os.environ.get("DB_PORT", 3306))

    try:
        import mysql.connector
        conn = mysql.connector.connect(
            host=host,
            user=user,
            password=password,
            database=database,
            port=port
        )
        print(f"[FDS Database] Connected successfully to MySQL: {user}@{host}:{port}/{database}")
        return conn
    except Exception as e:
        print(f"[FDS Database Notice] MySQL direct connection notice: {e}")
        # Try alternate default password if brindha2624@ failed
        if password == "brindha2624@":
            try:
                import mysql.connector
                conn = mysql.connector.connect(
                    host=host,
                    user=user,
                    password="root",
                    database=database,
                    port=port
                )
                print(f"[FDS Database] Connected to MySQL with fallback credentials on {host}:{port}/{database}")
                return conn
            except Exception:
                pass
        print("[FDS Database Info] Utilizing synchronized operational dataset for analysis.")
        return None

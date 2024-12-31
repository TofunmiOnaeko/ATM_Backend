#!/bin/bash
# Ensure the SQL Server container starts up properly

# Export SA_PASSWORD if it's not set
export SA_PASSWORD="${SA_PASSWORD:-MyStrongPassword123!}"

# Log the password (for debugging purposes, remove in production)
echo "Using password: $SA_PASSWORD"

# Start SQL Server in the background
/opt/mssql/bin/sqlservr &

# Wait for SQL Server to start up
echo "Waiting for SQL Server to start..."

# Loop to check if SQL Server is up and running
until /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -Q "SELECT 1" &>/dev/null
do
    echo "Waiting for SQL Server to become available..."
    sleep 5s
done

echo "SQL Server is up and running!"

# Run the SQL scripts (if any are present in the ./sql-scripts directory)
if [ -d "/sql-scripts" ]; then
    for sql_file in /sql-scripts/*.sql; do
        echo "Running $sql_file..."
        /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -i "$sql_file"
    done
fi

# Keep the container running (since sqlservr was already started in the background)
wait

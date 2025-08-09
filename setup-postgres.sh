#!/bin/bash

# PostgreSQL Database Setup Script
# Run this script to create the authentication database and user

echo "Setting up PostgreSQL database for Authentication application..."

# Connect to PostgreSQL and create database and user
psql -U postgres -c "CREATE DATABASE auth_db;"
psql -U postgres -c "CREATE USER auth_user WITH ENCRYPTED PASSWORD 'strongpassword';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;"
psql -U postgres -c "GRANT ALL ON SCHEMA public TO auth_user;"

echo "Database setup complete!"
echo "Database: auth_db"
echo "Username: auth_user"
echo "Password: strongpassword"
echo ""
echo "To use PostgreSQL, change application.yml profile to 'postgres'"

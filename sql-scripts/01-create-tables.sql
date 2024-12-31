-- create-tables.sql

-- Check if the database exists, if not, create it
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'MyDatabase')
BEGIN
    CREATE DATABASE MyDatabase;
END
GO

-- Switch to the newly created or existing database
USE MyDatabase;
GO

-- Create the Users table if it does not exist
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'User' AND xtype = 'U')
BEGIN
    CREATE TABLE User (
        UserId INT PRIMARY KEY,
        UserName NVARCHAR(100),
        UserDOB DATE,
        Email NVARCHAR(100),
        Password NVARCHAR(100)
    );
END
GO

-- Create the Balance table if it does not exist
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'Balance' AND xtype = 'U')
BEGIN
    CREATE TABLE Balance (
        UserId INT PRIMARY KEY,
        Balance INT,
        LastUpdated DATETIME2
    );
END
GO

-- Create the Transactions table if it does not exist
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'Transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE Transactions (
        TransactionId INT PRIMARY KEY,
        UserId INT,
        TransactionAmount INT,
        TimeOfTransaction DATETIME2,
        FOREIGN KEY (UserId) REFERENCES Users(UserId)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'Address' AND xtype = 'U')
BEGIN
    CREATE TABLE Address (
        UserId INT,
        AddressLine1 CHAR(100),
        AddressLine2 CHAR(100),
        Locality CHAR(100),
        Town CHAR(100),
        Postcode CHAR(100)
    );
END
GO

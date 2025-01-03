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
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'Users' AND xtype = 'U')
BEGIN
    CREATE TABLE Users (
        UserId CHAR(10) PRIMARY KEY,
        UserName VARCHAR(100),
        UserDOB DATE,
        Email NVARCHAR(100),
        Password NVARCHAR(100),
        LatestLogin DATETIME2,
    );
END
GO

-- Create the Balance table if it does not exist
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'Balance' AND xtype = 'U')
BEGIN
    CREATE TABLE Balance (
        UserId CHAR(10) PRIMARY KEY,
        Balance INT,
        LastUpdated DATETIME2,
        FOREIGN KEY (UserId) REFERENCES Users(UserId),
    );
END
GO

-- Create the Stocks table if it does not exist
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'Stocks' AND xtype = 'U')
BEGIN
    CREATE TABLE Stocks (
        StockId INT PRIMARY KEY,
        StockName CHAR(100),
        Value INT,
        ValueLastUpdated DATETIME2,
        CreatorId CHAR(10),
        FOREIGN KEY (CreatorId) REFERENCES Users(UserId)
    );
END
GO

-- Create the Transactions table if it does not exist
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'Transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE Transactions (
        TransactionId INT IDENTITY(1,1) PRIMARY KEY,
        UserId CHAR(10),
        TransactionAmount INT,
        TimeOfTransaction DATETIME2,
        LocalCurrency VARCHAR(10),
        StockId INT,
        StockValue INT,
        TransactionType VARCHAR(10),
        FOREIGN KEY (UserId) REFERENCES Users(UserId),
        FOREIGN KEY (StockId) REFERENCES Stocks(StockId)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'Address' AND xtype = 'U')
BEGIN
    CREATE TABLE Address (
        UserId CHAR(10) PRIMARY KEY,
        AddressLine1 CHAR(100),
        AddressLine2 CHAR(100),
        Locality CHAR(100),
        Town CHAR(100),
        Postcode CHAR(100)
        FOREIGN KEY (UserId) REFERENCES Users(UserId)
    );
END
GO

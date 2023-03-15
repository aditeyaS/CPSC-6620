-- https://github.com/aditeyaS/6620-project
-- Aditeya Srivastava (aditeys@clemson.edu)

CREATE TABLE topping (
    ToppingID PRIMARY KEY INTEGER NOT NULL AUTO_INCREMENT,
    ToppingName VARCHAR(50) NOT NULL,
    ToppingCP DECIMAL(2, 2) NOT NULL,
    ToppingSP DECIMAL(2, 2) NOT NULL,
    ToppingAmtP INTEGER NOT NULL,
    ToppingAmtM INTEGER NOT NULL,
    ToppingAmtL INTEGER NOT NULL,
    ToppingAmtXL INTEGER NOT NULL,
    ToppingMinimumInventory INTEGER NOT NULL,
    ToppingCurrentInventory INTEGER NOT NULL,
)

CREATE TABLE discount (
    DiscountID INTEGER NOT NULL,
    DiscountName VARCHAR(250) NOT NULL,
    DiscountPercentage DECIMAL(3, 2),
    DiscountAmount DECIMAL(5, 2),
)

CREATE TABLE customer (
    CustomerID PRIMARY KEY INTEGER NOT NULL AUTO_INCREMENT,
    CustomerFName VARCHAR(25) NOT NULL,
    CustomerLName VARCHAR(25) NOT NULL,
    CustomerPhone VARCHAR(10) NOT NULL
)


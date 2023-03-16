-- https://github.com/aditeyaS/6620-project
-- Aditeya Srivastava (aditeys@clemson.edu)

-- Pizza base table
-- Contains information on cost of all pizza combination (size x crust)
CREATE TABLE base (
    BaseSize VARCHAR(15) NOT NULL,
    BaseCrust VARCHAR(15) NOT NULL,
    BaseSP DECIMAL(4, 2) NOT NULL,
    BaseCP DECIMAL(4, 2) NOT NULL,
    PRIMARY KEY (BaseSize, BaseCrust)
);

-- Toppings table
-- Contains details of all the toppings
CREATE TABLE topping (
    ToppingID INTEGER NOT NULL AUTO_INCREMENT,
    ToppingName VARCHAR(50) NOT NULL,
    ToppingSP DECIMAL(3, 2) NOT NULL,
    ToppingCP DECIMAL(3, 2) NOT NULL,
    ToppingCurrentInventory DECIMAL(10,2) NOT NULL,
    ToppingAmtS DECIMAL(3,2) NOT NULL,
    ToppingAmtM DECIMAL(3,2) NOT NULL,
    ToppingAmtL DECIMAL(3,2) NOT NULL,
    ToppingAmtXL DECIMAL(3,2) NOT NULL,
    PRIMARY KEY(ToppingID)
);

CREATE TABLE discount (
    DiscountID INTEGER NOT NULL AUTO_INCREMENT,
    DiscountName VARCHAR(250) NOT NULL,
    DiscountPercentage DECIMAL(4, 2),
    DiscountAmount DECIMAL(5, 2),
    PRIMARY KEY(DiscountID)
);


CREATE TABLE customer (
    CustomerID PRIMARY KEY INTEGER NOT NULL AUTO_INCREMENT,
    CustomerFName VARCHAR(25) NOT NULL,
    CustomerLName VARCHAR(25) NOT NULL,
    CustomerPhone VARCHAR(10) NOT NULL
);









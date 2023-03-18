-- https://github.com/aditeyaS/6620-project
-- Aditeya Srivastava (aditeys@clemson.edu)

-- Pizza base table
-- Contains information on cost of all pizza combination (size x crust)
CREATE TABLE base (
    BaseSize VARCHAR(20) NOT NULL,
    BaseCrust VARCHAR(20) NOT NULL,
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
    CustomerPhone VARCHAR(15) NOT NULL
);

CREATE TABLE order (
    OrderID INTEGER NOT NULL AUTO_INCREMENT,
    OrderType VARCHAR(15) NOT NULL,
    OrderTime DATETIME NOT NULL,
    OrderSP DECIMAL(10, 2) NOT NULL,
    OrderCP DECIMAL(10, 2) NOT NULL,
    OrderCustomerID INTEGER FOREIGN KEY REFERENCES customer(CustomerID),
    PRIMARY KEY(OrderID)
);

CREATE TABLE order_discount (
    OrderDiscountOrderID INTEGER NOT NULL FOREIGN KEY REFERENCES order(OrderID),
    OrderDiscountDiscountID INTEGER NOT NULL FOREIGN KEY REFERENCES discount(DiscountID),
    PRIMARY KEY (OrderDiscountOrderID, OrderDiscountDiscountID)
);

CREATE TABLE pizza (
    PizzaID INTEGER NOT NULL AUTO_INCREMENT,
    PizzaBaseSize VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES base(BaseSize),
    PizzaBaseCrust VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES base(BaseCrust),
    PizzaState VARCHAR(20) NOT NULL,
    PizzaSP DECIMAL(10, 2) NOT NULL,
    PizzaCP DECIMAL(10, 2) NOT NULL,
    PizzaOrderID INTEGER NOT NULL FOREIGN KEY REFERENCES order(OrderID),
    PRIMARY KEY (PizzaID)
);

CREATE TABLE pizza_topping (
    PizzaToppingPizzaID INTEGER NOT NULL FOREIGN KEY REFERENCES pizza(PizzaID),
    PizzaToppingToppingID INTEGER NOT NULL FOREIGN KEY REFERENCES topping(ToppingID),
    PizzaToppingCount INTEGER NOT NULL,
    PRIMARY KEY(PizzaToppingPizzaID, PizzaToppingToppingID)
);

CREATE TABLE pizza_discount (
    PizzaDiscountPizzaID INTEGER NOT NULL FOREIGN KEY REFERENCES pizza(PizzaID),
    PizzaDiscountDiscountID INTEGER NOT NULL FOREIGN KEY REFERENCES discount(DiscountID),
    PRIMARY KEY (PizzaDiscountPizzaID, PizzaDiscountDiscountID)
);









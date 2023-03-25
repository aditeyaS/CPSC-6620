-- https://github.com/aditeyaS/6620-project
-- Aditeya Srivastava (aditeys@clemson.edu)

CREATE TABLE base (
    BaseSize VARCHAR(20) NOT NULL,
    BaseCrust VARCHAR(20) NOT NULL,
    BaseSP DECIMAL(4, 2) NOT NULL,
    BaseCP DECIMAL(4, 2) NOT NULL,
    PRIMARY KEY(BaseSize, BaseCrust)
);

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
    DiscountPercentage DECIMAL(5, 2) NOT NULL DEFAULT 0,
    DiscountAmount DECIMAL(7, 2) NOT NULL DEFAULT 0,
    PRIMARY KEY(DiscountID)
);

CREATE TABLE customer (
    CustomerID INTEGER NOT NULL AUTO_INCREMENT,
    CustomerFName VARCHAR(25) NOT NULL,
    CustomerLName VARCHAR(25) NOT NULL,
    CustomerPhone VARCHAR(15) NOT NULL,
    PRIMARY KEY(CustomerID)
);

CREATE TABLE orders (
    OrderID INTEGER NOT NULL AUTO_INCREMENT,
    OrderType VARCHAR(15) NOT NULL,
    OrderTime DATETIME NOT NULL,
    OrderSP DECIMAL(10, 2) NOT NULL,
    OrderCP DECIMAL(10, 2) NOT NULL,
    OrderCustomerID INTEGER NOT NULL,
    PRIMARY KEY(OrderID),
    FOREIGN KEY(OrderCustomerID) REFERENCES customer(CustomerID)
);

CREATE TABLE dinein (
    DineinOrderID INTEGER NOT NULL,
    DineinSeat INTEGER NOT NULL,
    PRIMARY KEY(DineinOrderID),
    FOREIGN KEY(DineinOrderID) REFERENCES orders(OrderID)
);

CREATE TABLE pickup (
    PickupOrderID INTEGER NOT NULL,
    PickupID VARCHAR(8) NOT NULL DEFAULT 'P0000000',
    PRIMARY KEY(PickupOrderID),
    FOREIGN KEY(PickupOrderID) REFERENCES orders(OrderID)
);

CREATE TABLE delivery (
    DeliveryOrderID INTEGER NOT NULL,
    DeliveryAddress VARCHAR(50) NOT NULL,
    DeliveryCity VARCHAR(25) NOT NULL,
    DeliveryState VARCHAR(25) NOT NULL,
    DeliveryZipCode INTEGER NOT NULL,
    PRIMARY KEY(DeliveryOrderID),
    FOREIGN KEY(DeliveryOrderID) REFERENCES orders(OrderID)
);

CREATE TABLE order_discount (
    OrderDiscountOrderID INTEGER NOT NULL,
    OrderDiscountDiscountID INTEGER NOT NULL,
    PRIMARY KEY(OrderDiscountOrderID, OrderDiscountDiscountID),
    FOREIGN KEY(OrderDiscountOrderID) REFERENCES orders(OrderID),
    FOREIGN KEY(OrderDiscountDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE pizza (
    PizzaID INTEGER NOT NULL AUTO_INCREMENT,
    PizzaBaseSize VARCHAR(20) NOT NULL,
    PizzaBaseCrust VARCHAR(20) NOT NULL,
    PizzaState VARCHAR(20) NOT NULL,
    PizzaSP DECIMAL(10, 2) NOT NULL,
    PizzaCP DECIMAL(10, 2) NOT NULL,
    PizzaOrderID INTEGER NOT NULL,
    PRIMARY KEY(PizzaID),
    FOREIGN KEY(PizzaBaseSize, PizzaBaseCrust) REFERENCES base(BaseSize, BaseCrust),
    FOREIGN KEY(PizzaOrderID) REFERENCES orders(OrderID)
);

CREATE TABLE pizza_topping (
    PizzaToppingPizzaID INTEGER NOT NULL,
    PizzaToppingToppingID INTEGER NOT NULL,
    PizzaToppingCount INTEGER NOT NULL,
    PRIMARY KEY(PizzaToppingPizzaID, PizzaToppingToppingID),
    FOREIGN KEY(PizzaToppingPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY(PizzaToppingToppingID) REFERENCES topping(ToppingID)
);

CREATE TABLE pizza_discount (
    PizzaDiscountPizzaID INTEGER NOT NULL,
    PizzaDiscountDiscountID INTEGER NOT NULL,
    PRIMARY KEY(PizzaDiscountPizzaID, PizzaDiscountDiscountID),
    FOREIGN KEY(PizzaDiscountPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY(PizzaDiscountDiscountID) REFERENCES discount(DiscountID)
);
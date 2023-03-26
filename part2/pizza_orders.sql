-- ORDER 1
-- On March 5th at 12:03 pm there was a dine-in order for a large thin crust pizza with Regular Cheese
-- (extra), Pepperoni, and Sausage (Price: 13.50, Cost: 3.68 ). They used the “Lunch Special Large” discount
-- They sat at Table 14

SELECT @dinein_cid := MIN(CustomerID)
FROM customer;
INSERT INTO orders(OrderType, OrderTime, OrderSP, OrderCP, OrderCustomerID)
VALUES('dinein', '2023-03-05 12:03:00', 13.50, 3.68, @dinein_cid);

SELECT @o_id := LAST_INSERT_ID();
INSERT INTO dinein(DineinOrderID, DineinSeat)
VALUES(@o_id, 14);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('large', 'Thin', 'completed', 13.50, 3.68, @o_id);

SELECT @p_id := LAST_INSERT_ID();

SELECT @t1_id := ToppingID
FROM topping WHERE ToppingName = 'Regular Cheese';

SELECT @t2_id := ToppingID
FROM topping WHERE ToppingName = 'Pepperoni';

SELECT @t3_id := ToppingID
FROM topping WHERE ToppingName = 'Sausage';

INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingCount)
VALUES
    (@p_id, @t1_id, 2),
    (@p_id, @t2_id, 1),
    (@p_id, @t3_id, 1);

SELECT @d_id := DiscountID
FROM discount WHERE DiscountName = 'Lunch Special Large';
INSERT INTO order_discount(OrderDiscountOrderID, OrderDiscountDiscountID)
VALUES (@o_id, @d_id);


-- ORDER 2
-- On March 3rd at 12:05 pm there was a dine-in order At table 4. They ordered a medium pan pizza with
-- Feta Cheese, Black Olives, Roma Tomatoes, Mushrooms and Banana Peppers (P: 10.60, C: 3.23). They get
-- the “Lunch Special Medium”and the “Specialty Pizza” discounts. They also ordered a small original crust
-- pizza with Regular Cheese, Chicken and Banana Peppers (P: 6.75, C: 1.40)

SELECT @dinein_cid:=MIN(CustomerID)
FROM customer;
INSERT INTO orders(OrderType, OrderTime, OrderSP, OrderCP, OrderCustomerID)
VALUES ('dinein', '2023-03-03 12:05:00', 10.60 + 6.75, 3.23 + 1.40, @dinein_cid);

SELECT @o_id := LAST_INSERT_ID();
INSERT INTO dinein(DineinOrderID, DineinSeat)
VALUES(@o_id, 4);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('medium', 'Pan', 'completed', 10.60, 3.23, @o_id);

SELECT @p_id := LAST_INSERT_ID();

INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID)
SELECT
    @p_id 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID'
FROM topping
WHERE ToppingName IN
    ('Feta Cheese', 'Black Olives', 'Roma Tomato', 'Mushrooms', 'Banana Peppers');

INSERT INTO pizza_discount(PizzaDiscountPizzaID, PizzaDiscountDiscountID)
SELECT
    @p_id 'PizzaDiscountPizzaID',
    DiscountID 'PizzaDiscountDiscountID'
FROM
discount
WHERE
DiscountName IN ('Lunch Special Medium', 'Specialty Pizza');

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('small', 'Original', 'completed', 6.75, 1.40, @o_id);

SELECT @p_id := LAST_INSERT_ID();

INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID)
SELECT
    @p_id 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID'
FROM topping
WHERE ToppingName IN
    ('Regular Cheese', 'Chicken', 'Banana Peppers');


-- ORDER 3
-- On March 3rd at 9:30 pm Ellis Beck places an order for pickup of 6 large original crust pizzas with Regular
-- Cheese and Pepperoni (Price: 10.75, Cost:3.30 each). Ellis’ phone number is 864-254-5861.

INSERT INTO customer (CustomerFName, CustomerLName, CustomerPhone)
VALUES ('Ellis', 'Beck', '864-254-5861');

SELECT @c_id := LAST_INSERT_ID();
INSERT INTO orders(OrderType, OrderTime, OrderSP, OrderCP, OrderCustomerID)
VALUES ('pickup', '2023-03-03 21:30:00', 10.75 * 6, 3.30 * 6, @c_id);

SELECT @o_id := LAST_INSERT_ID();
INSERT INTO pickup(PickupOrderID)
VALUES(@o_id);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES
    ('large', 'Original', 'completed', 10.75, 3.30, @o_id),
    ('large', 'Original', 'completed', 10.75, 3.30, @o_id),
    ('large', 'Original', 'completed', 10.75, 3.30, @o_id),
    ('large', 'Original', 'completed', 10.75, 3.30, @o_id),
    ('large', 'Original', 'completed', 10.75, 3.30, @o_id),
    ('large', 'Original', 'completed', 10.75, 3.30, @o_id);

INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID)
SELECT
    PizzaID 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID'
FROM pizza, topping
WHERE PizzaOrderID=@o_id
AND ToppingName IN ('Regular Cheese', 'Pepperoni');


-- ORDER 4
-- On March 5th at 7:11 pm there was a delivery order made by Ellis Beck for 1 x-large pepperoni and
-- Sausage pizza (P 14.50, C 5.59), one x-large pizza with Ham (extra) and Pineapple (extra) pizza (P: 17, C:
-- 5.59), and one x-large Jalapeno and Bacon pizza (P: 14.00, C: 5.68). All the pizzas have the Four Cheese
-- Blend on it and are original crust. The order has the “Gameday Special” discount applied to it, and the
-- ham and pineapple pizza has the “Specialty Pizza” discount applied to it. The pizzas were delivered to 115
-- Party Blvd, Anderson SC 29621. His phone number is the same as before.

SELECT @c_id := CustomerID
FROM customer
WHERE CustomerFName="Ellis" AND CustomerLName="Beck";

INSERT INTO orders(OrderType, OrderTime, OrderSP, OrderCP, OrderCustomerID)
VALUES ('delivery', '2023-03-05 19:11:00', 14.50 + 17 + 14.00, 5.59 + 5.59 + 5.68, @c_id);

SELECT @o_id := LAST_INSERT_ID();

INSERT INTO delivery(DeliveryOrderID, DeliveryAddress, DeliveryCity, DeliveryState, DeliveryZipCode)
VALUES(@o_id, '115 Party Blvd', 'Anderson', 'SC', 29621);

SELECT @d_id := DiscountID
FROM discount WHERE DiscountName = 'Gameday Special';
INSERT INTO order_discount(OrderDiscountOrderID, OrderDiscountDiscountID)
VALUES(@o_id, @d_id);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('x-large', 'Original', 'completed', 14.50, 5.59, @o_id);

SELECT @p_id := LAST_INSERT_ID();

INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID)
SELECT
    @p_id 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID'
FROM topping
WHERE ToppingName IN
    ('Pepperoni', 'Sausage', 'Four Cheese Blend');

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('x-large', 'Original', 'completed', 17, 5.59, @o_id);

SELECT @p_id := LAST_INSERT_ID();

INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingCount)
SELECT
    @p_id 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID',
    2 'PizzaToppingCount'
FROM topping
WHERE ToppingName IN
    ('Ham', 'Pineapple');
INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID)
SELECT
    @p_id 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID'
FROM topping WHERE ToppingName = 'Four Cheese Blend';

SELECT @d_id := DiscountID
FROM discount WHERE DiscountName = 'Specialty Pizza';

INSERT INTO pizza_discount(PizzaDiscountPizzaID, PizzaDiscountDiscountID)
VALUES(@p_id, @d_id);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('x-large', 'Original', 'completed', 14.00, 5.68, @o_id);

SELECT @p_id := LAST_INSERT_ID();

INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID)
SELECT
    @p_id 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID'
FROM topping
WHERE ToppingName IN
    ('Bacon', 'Jalapenos', 'Four Cheese Blend');


-- ORDER 5
-- On March 2nd at 5:30 pm Kurt McKinney placed an order for pickup for an x-large pizza with Green Pepper,
-- Onion, Roma Tomatoes, Mushrooms, and Black Olives on it. He wants the Goat Cheese on it, and a Gluten
-- Free Crust (P: 16.85, C:7.85). The “Specialty Pizza” discount is applied to the pizza. Kurt’s phone number is
-- 864-474-9953.

INSERT INTO customer(CustomerFName, CustomerLName, CustomerPhone)
VALUES ('Kurt', 'McKinney', '864-474-9953');

SELECT @c_id := LAST_INSERT_ID();

INSERT INTO orders(OrderType, OrderTime, OrderSP, OrderCP, OrderCustomerID)
VALUES ('pickup', '2023-03-02 17:30:00', 16.85, 7.85, @c_id);

SELECT @o_id := LAST_INSERT_ID();
INSERT INTO pickup(PickupOrderID)
VALUES(@o_id);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('x-large', 'Gluten-Free', 'completed', 16.85, 7.85, @o_id);

SELECT @p_id := LAST_INSERT_ID();

SELECT @d_id := DiscountID
FROM discount WHERE DiscountName = 'Specialty Pizza';

INSERT INTO pizza_discount(PizzaDiscountPizzaID, PizzaDiscountDiscountID)
VALUES(@p_id, @d_id);

INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID)
SELECT
    @p_id 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID'
FROM topping
WHERE ToppingName IN
    ('Green Pepper', 'Onion', 'Roma Tomato', 'Mushrooms', 'Black Olives', 'Goat Cheese');


-- ORDER 6
-- On March 2nd at 6:17 pm Calvin Sanders places on order for delivery of one large pizza with Chicken,
-- Green Peppers, Onions, and Mushrooms. He wants the Four Cheese Blend (extra) and thin crust (P:
-- 13.25, C: 3.20). The pizza was delivered to 6745 Wessex St Anderson SC 29621. Calvin’s phone number is
-- 864-232-8944.

INSERT INTO customer(CustomerFName, CustomerLName, CustomerPhone)
VALUES('Calvin', 'Sanders', '864-232-8944');

SELECT @c_id := LAST_INSERT_ID();

INSERT INTO orders(OrderType, OrderTime, OrderSP, OrderCP, OrderCustomerID)
VALUES('delivery', '2023-03-02 18:17:00', 13.25, 3.20, @c_id);

SELECT @o_id := LAST_INSERT_ID();

INSERT INTO delivery(DeliveryOrderID, DeliveryAddress, DeliveryCity, DeliveryState, DeliveryZipCode)
VALUES(@o_id, '6745 Wessex St', 'Anderson', 'SC', 29621);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('large', 'Thin', 'completed', 13.25, 3.20, @o_id);

SELECT @p_id := LAST_INSERT_ID();
INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID)
SELECT
    @p_id 'PizzaToppingPizzaID',
    ToppingID 'PizzaToppingToppingID'
FROM topping
WHERE ToppingName IN
    ('Chicken', 'Green Pepper', 'Onion', 'Mushrooms');
SELECT @t_id := ToppingID
FROM topping WHERE ToppingName = 'Four Cheese Blend';
INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingCount)
VALUES(@p_id, @t_id, 2);


-- ORDER 7
-- On March 6th at 8:32 pm Lance Benton ordered two large thin crust pizzas. One had the Four Cheese
-- Blend on it (extra) (P: 12, C: 3.75), the other was Regular Cheese and Pepperoni (extra) (P:12, C: 2.55). He
-- used the “Employee” discount on his order. He had them delivered to 8879 Suburban Home, Anderson,
-- SC 29621. His phone number is 864-878-5679.

INSERT INTO customer (CustomerFName, CustomerLName, CustomerPhone)
VALUES('Lance', 'Benton', '864-878-5679');

SELECT @c_id := LAST_INSERT_ID();

INSERT INTO orders(OrderType, OrderTime, OrderSP, OrderCP, OrderCustomerID)
VALUES('delivery', '2023-03-06 20:32:00', 12 + 12, 3.75 + 2.55, @c_id);

SELECT @o_id := LAST_INSERT_ID();

INSERT INTO delivery(DeliveryOrderID, DeliveryAddress, DeliveryCity, DeliveryState, DeliveryZipCode)
VALUES(@o_id, '8879 Suburban Home', 'Anderson', 'SC', 29621);

SELECT @d_id := DiscountID
FROM discount WHERE DiscountName = 'Employee';
INSERT INTO order_discount(OrderDiscountOrderID, OrderDiscountDiscountID)
VALUES(@o_id, @d_id);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('large', 'Thin', 'completed', 12, 3.75, @o_id);

SELECT @p_id := LAST_INSERT_ID();

SELECT @t_id := ToppingID
FROM topping
WHERE ToppingName = 'Four Cheese Blend';
INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingCount)
VALUES(@p_id, @t_id, 2);

INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID)
VALUES('large', 'Thin', 'completed', 12, 2.55, @o_id);

SELECT @p_id := LAST_INSERT_ID();

SELECT @t1_id := ToppingID
FROM topping
WHERE ToppingName = 'Regular Cheese';
SELECT @t2_id := ToppingID
FROM topping
WHERE ToppingName = 'Pepperoni';
INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingCount)
VALUES
    (@p_id, @t1_id, 1),
    (@p_id, @t2_id, 2);
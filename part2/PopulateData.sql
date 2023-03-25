-- https://github.com/aditeyaS/6620-project
-- Aditeya Srivastava (aditeys@clemson.edu)

INSERT INTO base(BaseSize, BaseCrust, BaseSP, BaseCP)
VALUES
('small', 'Thin', 3, 0.5),
('small', 'Original', 3, 0.75),
('small', 'Pan', 3.5, 1),
('small', 'Gluten-Free', 4, 2),
('medium', 'Thin', 5, 1),
('medium', 'Original', 5, 1.5),
('medium', 'Pan', 6, 2.25),
('medium', 'Gluten-Free', 6.25, 3),
('large', 'Thin', 8, 1.25),
('large', 'Original', 8, 2),
('large', 'Pan', 9, 3),
('large', 'Gluten-Free', 9.5, 4),
('x-large', 'Thin', 10, 2),
('x-large', 'Original', 10, 3),
('x-large', 'Pan', 11.5, 4.5),
('x-large', 'Gluten-Free', 12.5, 6);

INSERT INTO topping(ToppingName, ToppingSP, ToppingCP, ToppingCurrentInventory, ToppingAmtS, ToppingAmtM, ToppingAmtL, ToppingAmtXL)
VALUES
('Pepperoni', 1.25, 0.2, 100, 2, 2.75, 3.5, 4.5),
('Sausage', 1.25, 0.15, 100, 2.5, 3, 3.5, 4.25),
('Ham', 1.5, 0.15, 78, 2, 2.5, 3.25, 4),
('Chicken', 1.75, 0.25, 56, 1.5, 2, 2.25, 3),
('Green Pepper', 0.5, 0.02, 79, 1, 1.5, 2, 2.5),
('Onion', 0.5, 0.02, 85, 1, 1.5, 2, 2.75),
('Roma Tomato', 0.75, 0.03, 86, 2, 3, 3.5, 4.5),
('Mushrooms', 0.75, 0.1, 52, 1.5, 2, 2.5, 3),
('Black Olives', 0.6, 0.1, 39, 0.75, 1, 1.5, 2),
('Pineapple', 1, 0.25, 15, 1, 1.25, 1.75, 2),
('Jalapenos', 0.5, 0.05, 64, 0.5, 0.75, 1.25, 1.75),
('Banana Peppers', 0.5, 0.05, 36, 0.6, 1, 1.3, 1.75),
('Regular Cheese', 1.5, 0.12, 250, 2, 3.5, 5, 7),
('Four Cheese Blend', 2, 0.15, 150, 2, 3.5, 5, 7),
('Feta Cheese', 2, 0.18, 75, 1.75, 3, 4, 5.5),
('Goat Cheese', 2, 0.2, 54, 1.6, 2.75, 4, 5.5),
('Bacon', 1.5, 0.25, 89, 1, 1.5, 2, 3);

INSERT INTO discount(DiscountName, DiscountPercentage, DiscountAmount)
VALUES
('Employee', 15, 0),
('Lunch Special Medium', 0, 1.00),
('Lunch Special Large', 0, 2.00),
('Specialty Pizza', 0, 1.50),
('Gameday Special', 20, 0);

INSERT INTO customer(CustomerFName, CustomerLName, CustomerPhone)
VALUES
('Dinein', 'Customer', '000-000-0000');
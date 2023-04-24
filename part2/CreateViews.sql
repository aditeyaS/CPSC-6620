-- https://github.com/aditeyaS/6620-project
-- Aditeya Srivastava (aditeys@clemson.edu)

USE proj_p2_g1;

-- View 1
-- ToppingPopularity
-- rank order of all the toppings (accounting for extra toppings)
-- from most popular to least popular
CREATE OR REPLACE VIEW ToppingPopularity AS
SELECT
    ToppingName 'Topping',
    SUM(PizzaToppingCount) 'ToppingCount'
FROM pizza_topping pt
JOIN topping t
ON pt.PizzaToppingToppingID = t.ToppingID
GROUP BY pt.PizzaToppingToppingID
ORDER BY ToppingCount DESC;

-- View 2
-- ProfitByPizza
-- a summary of the profit by pizza size and crust type over a selected
-- time period ordered by profit from most profitable to least profitabe
CREATE OR REPLACE VIEW ProfitByPizza AS
SELECT
	PizzaBaseSize 'PizzaSize',
	PizzaBaseCrust 'PizzaCrust',
	DATE_FORMAT(MAX(OrderTime), '%M-%e-%Y') 'LastOrderDate',
	SUM(PizzaSP) - SUM(PizzaCP) 'Profit'
FROM pizza p
JOIN orders o
ON p.PizzaOrderID = o.OrderID
GROUP BY PizzaBaseSize, PizzaBaseCrust
ORDER BY Profit DESC;

-- View 3
-- ProfitByOrderType
-- a summary of the profit for each of the three types of orders
-- by month with a grand total over all the orders at the pizzeria
CREATE OR REPLACE VIEW ProfitByOrderType AS
SELECT
    OrderType 'CustomerType',
    DATE_FORMAT(MAX(OrderTime), "%Y-%M") 'OrderMonth',
    SUM(OrderSP) 'TotalOrderPrice',
    SUM(OrderCP) 'TotalOrderCost',
    SUM(OrderSP) - SUM(OrderCP) 'Profit'
FROM orders
GROUP BY DATE_FORMAT(OrderTime,'%Y-%M'), OrderType
UNION
SELECT
	'',
    'Grand Total',
    SUM(OrderSP),
    SUM(OrderCP),
    SUM(OrderSP) - SUM(OrderCP)
FROM orders
ORDER BY OrderMonth;


SELECT * FROM ToppingPopularity;
SELECT * FROM ProfitByPizza;
SELECT * FROM ProfitByOrderType;
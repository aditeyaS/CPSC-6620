package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "small";
	public final static String size_m = "medium";
	public final static String size_l = "large";
	public final static String size_xl = "x-large";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";

	public final static String state_completed = "completed";
	public final static String state_preparing = "state_preparing";


	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	public static void addOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 */
		int generatedOrderId = -1;

		// adding to orders table
		String query = "INSERT INTO orders(OrderType, OrderTime, OrderSP, OrderCP, OrderCustomerID) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement pStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pStmt.setString(1, o.getOrderType());
		pStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
		pStmt.setDouble(3, o.getCustPrice());
		pStmt.setDouble(4, o.getBusPrice());
		pStmt.setInt(5, o.getCustID());

		try {
			if (pStmt.executeUpdate() != 0) {
				ResultSet generatedKey = pStmt.getGeneratedKeys();
				if (generatedKey.next())
					generatedOrderId = generatedKey.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		o.setOrderID(generatedOrderId);

		// adding to order type table
		if (o instanceof DineinOrder) { // dinein
			DineinOrder dineinO = (DineinOrder)o;
			query = "INSERT INTO dinein(DineinOrderID, DineinSeat) VALUES (?, ?)";
			pStmt = conn.prepareStatement(query);
			pStmt.setInt(1, generatedOrderId);
			pStmt.setInt(2, dineinO.getTableNum());
		} else if (o instanceof DeliveryOrder) { // delivery
			DeliveryOrder deliveryO = (DeliveryOrder)o;
			query = "INSERT INTO delivery(DeliveryOrderID, DeliveryAddress, DeliveryCity, DeliveryState, DeliveryZipCode) VALUES(?, ?, ?, ?, ?)";
			pStmt = conn.prepareStatement(query);
			String[] address = deliveryO.getAddress().split(",");
			pStmt.setInt(1, generatedOrderId);
			pStmt.setString(2, address[0].trim());
			pStmt.setString(3, address[1].trim());
			pStmt.setString(4, address[2].trim());
			pStmt.setInt(5, Integer.parseInt(address[3].trim()));
		} else { // pickup
			PickupOrder pickupO = (PickupOrder)o;
			query = "INSERT INTO pickup(PickupOrderID) VALUES(?)";
			pStmt = conn.prepareStatement(query);
			pStmt.setInt(1, generatedOrderId);
		}
		try {
			pStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}


		// adding pizza
		for (Pizza p: o.getPizzaList()) {
			p.setOrderID(generatedOrderId);
			addPizza(p);
		}

		// adding order discount
		for (Discount d: o.getDiscountList()) {
			useOrderDiscount(o, d);
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pStmt.close();
		conn.close();
	}

	public static void addPizza(Pizza p) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts to that bridge table and 
		 * instance of topping usage to that bridge table if you have't accounted
		 * for that somewhere else.
		 */
		int generatedPizzaId = -1;
		// adding to pizza table
		String query = "INSERT INTO pizza(PizzaBaseSize, PizzaBaseCrust, PizzaState, PizzaSP, PizzaCP, PizzaOrderID) VALUES (?,?,?,?,?,?)";
		PreparedStatement pStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pStmt.setString(1, p.getSize());
		pStmt.setString(2, p.getCrustType());
		pStmt.setString(3, p.getPizzaState());
		pStmt.setDouble(4, p.getCustPrice());
		pStmt.setDouble(5, p.getBusPrice());
		pStmt.setInt(6, p.getOrderID());
		try {
			if (pStmt.executeUpdate() != 0) {
				ResultSet generatedKey = pStmt.getGeneratedKeys();
				if (generatedKey.next())
					generatedPizzaId = generatedKey.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		p.setPizzaID(generatedPizzaId);

		// adding to pizza_topping (bridge) table and updating inventory
		for (int i = 0; i<p.getToppings().size(); i++) {
			boolean isDoubled = p.getIsDoubleArray()[i];
			useTopping(p, p.getToppings().get(i), isDoubled);
		}

		// adding to pizza_discount table
		for (Discount d: p.getDiscounts()) {
			usePizzaDiscount(p, d);
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pStmt.close();
		conn.close();
	}


	public static int getMaxPizzaID() throws SQLException, IOException {
		connect_to_db();
		/*
		 * A function I needed because I forgot to make my pizzas auto increment in my DB.
		 * It goes and fetches the largest PizzaID in the pizza table.
		 * You wont need to implement this function if you didn't forget to do that
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return -1;
	}

	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this function will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		/*
		 * This function should 2 two things.
		 * We need to update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * and we need to add that instance of topping usage to the pizza-topping bridge if we haven't done that elsewhere
		 * Ideally, you should't let toppings go negative. If someone tries to use toppings that you don't have, just print
		 * that you've run out of that topping.
		 */
		double toppingUsed = 0.0;
		if (p.getSize().equals(size_s))
			toppingUsed = t.getPerAMT();
		else if (p.getSize().equals(size_m))
			toppingUsed = t.getMedAMT();
		else if (p.getSize().equals(size_l))
			toppingUsed = t.getLgAMT();
		else
			toppingUsed = t.getXLAMT();

		if (isDoubled) toppingUsed *= 2;
		// updating topping inventory
		AddToInventory(t, toppingUsed * -1.0);

		// adding to pizza_topping bridge table
		connect_to_db();
		String bridgeQuery = "INSERT INTO pizza_topping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingCount) VALUES (?, ?, ?)";
		PreparedStatement pStmt = conn.prepareStatement(bridgeQuery);
		pStmt.setInt(1, p.getPizzaID());
		pStmt.setInt(2, t.getTopID());
		pStmt.setInt(3, isDoubled ? 2 : 1);
		try {
			pStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pStmt.close();
		conn.close();
	}


	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		String bridgeQuery = "INSERT INTO pizza_discount(PizzaDiscountPizzaID, PizzaDiscountDiscountID) VALUES (?, ?)";
		PreparedStatement pStmt = conn.prepareStatement(bridgeQuery);
		pStmt.setInt(1, p.getPizzaID());
		pStmt.setInt(2, d.getDiscountID());
		try {
			pStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pStmt.close();
		conn.close();
	}

	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		String bridgeQuery = "INSERT INTO order_discount(OrderDiscountOrderID, OrderDiscountDiscountID) VALUES(?, ?)";
		PreparedStatement pStmt = conn.prepareStatement(bridgeQuery);
		pStmt.setInt(1, o.getOrderID());
		pStmt.setInt(2, d.getDiscountID());
		try {
			pStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pStmt.close();
		conn.close();
	}


	public static int addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		// if customer already exists it returns it's id (negative)
		// else it returns the newly generated id
		/*
		 * This should add a customer to the database
		 */
		int insertedCustomerId = -1;
		String checkCustomerExistQuery = "SELECT CustomerID FROM customer WHERE CustomerFName=? AND CustomerLName=? AND CustomerPhone=?";
		PreparedStatement pS = conn.prepareStatement(checkCustomerExistQuery);
		pS.setString(1, c.getFName());
		pS.setString(2, c.getLName());
		pS.setString(3, c.getPhone());
		boolean customerExists = false;
		try (ResultSet rSet = pS.executeQuery()) {
			if (rSet.next()) {
				customerExists = true;
				insertedCustomerId = rSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (customerExists) {
			insertedCustomerId *= -1;
		} else {
			String insertCustomerQuery = "INSERT INTO customer(CustomerFName, CustomerLName, CustomerPhone) VALUES(?, ?, ?)";
			pS = conn.prepareStatement(insertCustomerQuery, Statement.RETURN_GENERATED_KEYS);
			pS.setString(1, c.getFName());
			pS.setString(2, c.getLName());
			pS.setString(3, c.getPhone());
			try {
				if (pS.executeUpdate() != 0) {
					ResultSet generatedKey = pS.getGeneratedKeys();
					if (generatedKey.next())
						insertedCustomerId = generatedKey.getInt(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pS.close();
		conn.close();
		return insertedCustomerId;
	}

	public static void CompleteOrder(int orderId) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to mark an order as complete in the DB. You may have a boolean field
		 * for this, or maybe a completed time timestamp. However you have it.
		 */
		String updateQuery = "UPDATE pizza SET PizzaState = ? WHERE PizzaOrderID = ?";
		PreparedStatement pStmt = conn.prepareStatement(updateQuery);
		pStmt.setString(1, state_completed);
		pStmt.setInt(2, orderId);
		try {
			pStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pStmt.close();
		conn.close();
	}


	public static void AddToInventory(Topping t, double toAdd) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Adds toAdd amount of topping to topping t.
		 */
		double newInventory = t.getCurINVT() + toAdd;
		String query = "UPDATE topping SET ToppingCurrentInventory = ? WHERE toppingId = ?";
		PreparedStatement pStmt = conn.prepareStatement(query);
		pStmt.setDouble(1, newInventory);
		pStmt.setInt(2, t.getTopID());
		try {
			pStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pStmt.close();
		conn.close();
	}


	public static void printInventory() throws SQLException, IOException {
		//connect_to_db();

		/*
		 * I used this function to PRINT (not return) the inventory list.
		 * When you print the inventory (either here or somewhere else)
		 * be sure that you print it in a way that is readable.
		 * 
		 * 
		 * 
		 * The topping list should also print in alphabetical order
		 */
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function actually returns the toppings. The toppings
		 * should be returned in alphabetical order if you don't
		 * plan on using a printInventory function
		 */
		ArrayList<Topping> toppingList = new ArrayList<Topping>();
		String selectToppingsQuery = "SELECT * FROM topping ORDER BY ToppingName";
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(selectToppingsQuery)) {
			while (rSet.next()) {
				int id = rSet.getInt("ToppingID");
				String name = rSet.getString("ToppingName");
				double cusPrice = rSet.getDouble("ToppingSP");
				double busPrice = rSet.getDouble("ToppingCP");
				int curI = (int)(rSet.getDouble("ToppingCurrentInventory"));
				int minI = (int)(rSet.getDouble("ToppingMinimumInventory"));
				double pAmt = rSet.getDouble("ToppingAmtS");
				double mAmt = rSet.getDouble("ToppingAmtM");
				double lAmt = rSet.getDouble("ToppingAmtL");
				double xlAmt = rSet.getDouble("ToppingAmtXL");
				toppingList.add(new Topping(id, name, pAmt, mAmt, lAmt, xlAmt, cusPrice, busPrice, minI, curI));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		stmt.close();
		conn.close();
		return toppingList;
	}


	public static ArrayList<Order> getCurrentOrders() throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function should return an arraylist of all of the orders.
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Also, like toppings, whenever we print out the orders using menu function 4 and 5
		 * these orders should print in order from newest to oldest.
		 */
		ArrayList<Order> orderList = new ArrayList<Order>();
		String query = "SELECT * FROM orders ORDER BY OrderTime DESC";
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(query)) {
			while (rSet.next()) {
				int id = rSet.getInt("OrderID");
				String type = rSet.getString("OrderType");
				String date = rSet.getString("OrderTime");
				double sp = rSet.getDouble("OrderSP");
				double cp = rSet.getDouble("OrderCP");
				int cId = rSet.getInt("OrderCustomerID");
				int isComplete = isOrderComplete(id);
				Order order;
				if (type.equals(pickup)) {
					// ASSUMPTION: Pickup order is picked up if it is completed
					order = new PickupOrder(id, cId, date, sp, cp, isComplete, isComplete);
				} else if (type.equals(dine_in)) {
					int tablenum = getTableNumber(id);
					order = new DineinOrder(id, cId, date, sp, cp, isComplete, tablenum);
				} else {
					String address = getDeliveryAddress(id);
					order = new DeliveryOrder(id, cId, date, sp, cp, isComplete, address);
				}
				orderList.add(order);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return orderList;
	}

	private static int isOrderComplete (int orderId) throws SQLException, IOException {
		/*Adi
		* Checks if an order is complete because i have taken complete in the pizza table
		*/
		connect_to_db();
		int isComplete = 0;
		String query = "SELECT PizzaState FROM pizza WHERE PizzaOrderID = " + orderId;
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(query)) {
			if (rSet.next()) {
				if (rSet.getString(1).equalsIgnoreCase(state_completed))
					isComplete = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isComplete;
	}

	private static int getTableNumber (int orderId) throws SQLException, IOException {
		/*
		 * 	returns the table number of a dine in order
		 */
		connect_to_db();
		int tableNumber = -1;
		String query = "SELECT DineinSeat FROM dinein WHERE DineinOrderID = ?";
		PreparedStatement pStmt = conn.prepareStatement(query);
		pStmt.setInt(1, orderId);
		try (ResultSet rSet = pStmt.executeQuery()) {
			if (rSet.next()) tableNumber = rSet.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pStmt.close();
		conn.close();
		return tableNumber;
	}

	private static String getDeliveryAddress(int orderId) throws SQLException, IOException {
		/*
		 * returns address of a delivery order
		 */
		connect_to_db();
		String custAddress = "";
		String query = "SELECT * FROM delivery WHERE DeliveryOrderID = ?";
		PreparedStatement pStmt = conn.prepareStatement(query);
		pStmt.setInt(1, orderId);
		try (ResultSet rSet = pStmt.executeQuery()) {
			if (rSet.next()) {
				String address = rSet.getString("DeliveryAddress");
				String city = rSet.getString("DeliveryCity");
				String state = rSet.getString("DeliveryCity");
				String zip = rSet.getString("DeliveryCity");
				custAddress = address + ", " + city + ", " + state + ", " + zip;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pStmt.close();
		conn.close();
		return custAddress;
	}

	public static ArrayList<Order> sortOrders(ArrayList<Order> list) {
		/*
		 * This was a function that I used to sort my arraylist based on date.
		 * You may or may not need this function depending on how you fetch
		 * your orders from the DB in the getCurrentOrders function.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;

	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder) {
		//Helper function I used to help sort my dates. You likely wont need these


		return false;
	}


	/*
	 * The next 3 private functions help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}

	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}

	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static double[] getSPCPCrust(String size, String crust) throws SQLException, IOException {
		// returns customer and business price at index 0 & 1
		connect_to_db();
		double[] price = new double[2];
		String query = "SELECT BaseSP, BaseCP FROM base WHERE BaseSize = ? AND BaseCrust = ?";
		PreparedStatement pStmt = conn.prepareStatement(query);
		pStmt.setString(1, size);
		pStmt.setString(2, crust);
		try (ResultSet rSet = pStmt.executeQuery()) {
			if (rSet.next()) {
				price[0] = rSet.getDouble("BaseSP");
				price[1] = rSet.getDouble("BaseCP");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pStmt.close();
		conn.close();
		return price;
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base price (for the customer) for that size and crust pizza Depending on how
		// you store size & crust in your database, you may have to do a conversion


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}

	public static String getCustomerName(int CustID) throws SQLException, IOException {
		/*
		 *This is a helper function I used to fetch the name of a customer
		 *based on a customer ID. It actually gets called in the Order class
		 *so I'll keep the implementation here. You're welcome to change
		 *how the order print statements work so that you don't need this function.
		 */
		connect_to_db();
		String customerName = "";
		String query = "SELECT CustomerFName, CustomerLName FROM customer WHERE CustomerID = ?";
		PreparedStatement pStmt = conn.prepareStatement(query);
		pStmt.setInt(1, CustID);
		try (ResultSet rSet = pStmt.executeQuery()) {
			if (rSet.next()) customerName = rSet.getString(1) + " " + rSet.getString(2);
		}
		pStmt.close();
		conn.close();
		return customerName;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base cost (for the business) for that size and crust pizza Depending on how
		// you store size and crust in your database, you may have to do a conversion


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}


	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		ArrayList<Discount> discs = new ArrayList<Discount>();
		connect_to_db();
		//returns a list of all the discounts.
		String query = "SELECT * FROM discount ORDER BY DiscountName";
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(query)) {
			while (rSet.next()) {
				int id = rSet.getInt("DiscountId");
				String name = rSet.getString("DiscountName");
				double percent = rSet.getDouble("DiscountPercentage");
				double amount = rSet.getDouble("DiscountAmount");
				boolean isPercent = amount == 0;
				discs.add(new Discount(id, name, isPercent ? percent : amount, isPercent));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		stmt.close();
		conn.close();
		return discs;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * return an arrayList of all the customers. These customers should
		 *print in alphabetical order, so account for that as you see fit.
		*/
		String query = "SELECT * FROM customer ORDER BY CustomerFName, CustomerLName";
		ArrayList<Customer> customerList = new ArrayList<Customer>();
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(query)) {
			while (rSet.next()) {
				int id = rSet.getInt("CustomerID");
				String fName = rSet.getString("CustomerFName");
				String lName = rSet.getString("CustomerLName");
				String phone = rSet.getString("CustomerPhone");
				customerList.add(new Customer(id, fName, lName, phone));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		stmt.close();
		conn.close();
		return customerList;
	}

	public static int getNextOrderID() throws SQLException, IOException {
		/*
		 * A helper function I had to use because I forgot to make
		 * my OrderID auto increment...You can remove it if you
		 * did not forget to auto increment your orderID.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return -1;
	}

	public static void printToppingPopReport() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print (other than that it should
		 * be in alphabetical order by name), just make sure it's readable.
		 */
		String query = "SELECT * FROM ToppingPopularity ORDER BY Topping";
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(query)) {
			System.out.println("-- TOPPING POPULARITY REPORT --");
			System.out.printf("%-25s%-10s\n", "Topping", "Count");
			while (rSet.next())
				System.out.printf("%-25s%-10d\n", rSet.getString(1), rSet.getInt(2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		stmt.close();
		conn.close();
	}

	public static void printProfitByPizzaReport() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */
		String query = "SELECT * FROM ProfitByPizza";
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(query)) {
			System.out.println("-- PROFIT BY PIZZA REPORT --");
			System.out.printf("%-10s%-20s%-20s%-10s\n", "Size", "Crust", "Date", "Profit");
			while (rSet.next()) {
				String size = rSet.getString(1);
				String crust = rSet.getString(2);
				String date = rSet.getString(3);
				double profit = rSet.getDouble(4);
				System.out.printf("%-10s%-20s%-20s%-10.2f\n", size, crust, date, profit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		stmt.close();
		conn.close();
	}

	public static void printProfitByOrderType() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */
		String query = "SELECT * FROM ProfitByOrderType";
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(query)) {
			System.out.println("-- PROFIT BY ORDER TYPE REPORT --");
			System.out.printf("%-15s%-20s%-10s%-10s%-10s\n", "Type", "Month", "Price", "Cost", "Profit");
			while (rSet.next()) {
				String type = rSet.getString(1);
				String month = rSet.getString(2);
				double price = rSet.getDouble(3);
				double cost = rSet.getDouble(4);
				double profit = rSet.getDouble(5);
				System.out.printf("%-15s%-20s%-10.2f%-10.2f%-10.2f\n", type, month, price, cost, profit);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		stmt.close();
		conn.close();
	}


}
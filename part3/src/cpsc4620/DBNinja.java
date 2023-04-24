package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

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

	public static int addOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 */
		int generatedOrderId = -1;
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

		if (o instanceof DineinOrder) {
			DineinOrder dineinO = (DineinOrder)o;
			query = "INSERT INTO dinein(DineinOrderID, DineinSeat) VALUES (?, ?)";
			pStmt = conn.prepareStatement(query);
			pStmt.setInt(1, generatedOrderId);
			pStmt.setInt(2, dineinO.getTableNum());
		} else if (o instanceof DeliveryOrder) {
			DeliveryOrder deliveryO = (DeliveryOrder)o;
			query = "INSERT INTO delivery(DeliveryOrderID, DeliveryAddress, DeliveryCity, DeliveryState, DeliveryZipCode) VALUES(?, ?, ?, ?, ?)";
			pStmt = conn.prepareStatement(query);
			String[] address = deliveryO.getAddress().split(",");
			pStmt.setInt(1, generatedOrderId);
			pStmt.setString(2, address[0].trim());
			pStmt.setString(3, address[1].trim());
			pStmt.setString(4, address[2].trim());
			pStmt.setInt(5, Integer.parseInt(address[3].trim()));
		} else {
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

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return generatedOrderId;
	}

	public static void addDiscountsToOrder (int orderId, ArrayList<Discount> discountList) throws SQLException, IOException {
		connect_to_db();
		String query = "INSERT INTO order_discount(OrderDiscountOrderID, OrderDiscountDiscountID) VALUES(?, ?)";
		PreparedStatement pStmt = conn.prepareStatement(query);
		pStmt.setInt(1, orderId);
		for (Discount d: discountList) {
			pStmt.setInt(2, d.getDiscountID());
			try {
				pStmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pStmt.close();
		conn.close();
	}

	public static void updateOrderPrice(int orderId) {

	}

	public static void updatePizzaPrice(int pizzaId) {

	}

	public static void addPizza(Pizza p) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts to that bridge table and 
		 * instance of topping usage to that bridge table if you have't accounted
		 * for that somewhere else.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
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
		connect_to_db();
		/*
		 * This function should 2 two things.
		 * We need to update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * and we need to add that instance of topping usage to the pizza-topping bridge if we haven't done that elsewhere
		 * Ideally, you should't let toppings go negative. If someone tries to use toppings that you don't have, just print
		 * that you've run out of that topping.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */



		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static int addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This should add a customer to the database
		 */
		int insertedCustomerId = -1;
		String query = "SELECT * FROM customer WHERE CustomerFName=? AND CustomerLName=? AND CustomerPhone=?";
		PreparedStatement pS = conn.prepareStatement(query);
		pS.setString(1, c.getFName());
		pS.setString(2, c.getLName());
		pS.setString(3, c.getPhone());
		boolean customerExists = false;
		try (ResultSet rSet = pS.executeQuery()) {
			customerExists = rSet.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (customerExists) {
			System.out.println("Customer already exists. Returning to main menu...");
		} else {
			query = "INSERT INTO customer(CustomerFName, CustomerLName, CustomerPhone) VALUES(?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, c.getFName());
			ps.setString(2, c.getLName());
			ps.setString(3, c.getPhone());
			try {
				if (ps.executeUpdate() != 0) {
					ResultSet generatedKey = ps.getGeneratedKeys();
					if (generatedKey.next())
						insertedCustomerId = generatedKey.getInt(1);
				}
				ps.close();
			} catch (Exception e) {
				ps.close();
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		pS.close();
		conn.close();
		return insertedCustomerId;
	}

	public static void CompleteOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to mark an order as complete in the DB. You may have a boolean field
		 * for this, or maybe a completed time timestamp. However you have it.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
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
		String query = "SELECT * FROM topping ORDER BY ToppingName";
		Statement stmt = conn.createStatement();
		try (ResultSet rSet = stmt.executeQuery(query)) {
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
		//toppingList.sort(Comparator.comparing(Topping::getTopName));
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
				if (type.equals(pickup)) {
					PickupOrder o = new PickupOrder(id, cId, date, sp, cp, isComplete, isComplete);
					orderList.add(o);
				} else if (type.equals(dine_in)) {
					int tablenum = getTableNumber(id);
					DineinOrder o = new DineinOrder(id, cId, date, sp, cp, isComplete, tablenum);
					orderList.add(o);
				} else {
					String address = getDeliveryAddress(id);
					DeliveryOrder o = new DeliveryOrder(id, cId, date, sp, cp, isComplete, address);
					orderList.add(o);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return orderList;
	}

	private static int isOrderComplete (int orderId) throws SQLException {
		/*Adi
		* Checks if an order is complete because i have taken complete in the pizza table
		*/
		return 1;
	}

	private static int getTableNumber (int orderId) throws SQLException {
		/*Adi
		 * 	Used in the getCurrentOrders()
		 * Don't need to open/close connection as it's happening in above function
		 */
		int tableNumber = -1;
		String query = "SELECT * FROM dinein WHERE DineinOrderID = ?";
		PreparedStatement pStmt = conn.prepareStatement(query);
		pStmt.setInt(1, orderId);
		try (ResultSet rSet = pStmt.executeQuery()) {
			if (rSet.next()) tableNumber = rSet.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pStmt.close();
		return tableNumber;
	}

	private static String getDeliveryAddress(int orderId) throws SQLException {
		/*Adi
		 * Used in the getCurrentOrders()
		 * Don't need to open/close connection as it's happening in above function
		 */
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
		ArrayList<Customer> customerList = new ArrayList<Customer>();
		connect_to_db();
		/*
		 * return an arrayList of all the customers. These customers should
		 *print in alphabetical order, so account for that as you see fit.
		*/
		String query = "SELECT * FROM customer ORDER BY CustomerFName, CustomerLName";
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
			System.out.println("-----------------------");
			System.out.println("Topping Popularity Report");
			System.out.println("-----------------------");
			while (rSet.next()) {
				System.out.println("Topping=" + rSet.getString(1) + ", Count=" + rSet.getInt(2));
			}
			System.out.println("-----------------------");
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
			System.out.println("-----------------------");
			System.out.println("Profit By Pizza Report");
			System.out.println("-----------------------");
			while (rSet.next()) {
				String size = rSet.getString(1);
				String crust = rSet.getString(2);
				String date = rSet.getString(3);
				double profit = rSet.getDouble(4);
				System.out.println("Size=" + size + ", Crust=" + crust + ", Date=" + date + ", Profit=" + profit);
			}
			System.out.println("-----------------------");
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
			System.out.println("-----------------------");
			System.out.println("Profit By Order Type Report");
			System.out.println("-----------------------");
			while (rSet.next()) {
				String type = rSet.getString(1);
				String month = rSet.getString(2);
				double price = rSet.getDouble(3);
				double cost = rSet.getDouble(4);
				if (!type.isEmpty()) {
					System.out.println("Type=" + type + ", Month=" + month + ", Price=" + price + ", Cost=" + cost);
				} else {
					System.out.println("Grand Total => Price=" + price + ", Cost=" + cost);
				}
			}
			System.out.println("-----------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		stmt.close();
		conn.close();
	}


}
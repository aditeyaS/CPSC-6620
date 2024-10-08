package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import init.DBIniter;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the functionality of each of these menu options' respective functions.
 * 
 * This file should need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove functions as you see necessary. But you MUST have all 8 menu functions (9 including exit)
 * 
 * Simply removing menu functions because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 * 
 */

public class Menu {

	public static final String INVALID_INPUT = "Invalid Input";

	public static void main(String[] args) throws SQLException, IOException {
		System.out.println("Welcome to Taylor's Pizzeria!");

		int menu_option = 0;

		// present a menu of options and take their selection

		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		DBIniter.init();
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
				case 1:// enter order
					EnterOrder();
					break;
				case 2:// view customers
					viewCustomers();
					break;
				case 3:// enter customer
					EnterCustomer(3);
					break;
				case 4:// view order
					// open/closed/date
					ViewOrders();
					break;
				case 5:// mark order as complete
					MarkOrderAsComplete();
					break;
				case 6:// view inventory levels
					ViewInventoryLevels();
					break;
				case 7:// add to inventory
					AddInventory();
					break;
				case 8:// view reports
					PrintReports();
					break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException {
		/*
		 * EnterOrder should do the following:
		 * Ask if the order is for an existing customer -> If yes, select the customer. If no -> create the customer (as if the menu option 2 was selected).
		 * 
		 * Ask if the order is delivery, pickup, or dinein (ask for orderType specific information when needed)
		 * 
		 * Build the pizza (there's a function for this)
		 * 
		 * ask if more pizzas should be be created. if yes, go back to building your pizza. 
		 * 
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * apply the pizza to the order (including to the DB)
		 * 
		 * return to menu
		 */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// customer choice
		System.out.print("Is the order for existing customer? (y/n) :");
		String existingCustomerChoice = "";
		try {
			existingCustomerChoice = br.readLine();
			if (!(existingCustomerChoice.equalsIgnoreCase("y") || existingCustomerChoice.equalsIgnoreCase("n")))
				throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return;
		}
		int customerId = -1;
		if (existingCustomerChoice.equalsIgnoreCase("y")) {
			System.out.println("Here's a list of customers:");
			ArrayList<Customer> customerList = DBNinja.getCustomerList();
			for (Customer c: customerList)
				System.out.println(c.toString());
			System.out.print("Enter customer id?: ");
			try {
				customerId = Integer.parseInt(br.readLine());
				boolean customerFound = false;
				for (Customer c: customerList) {
					if (c.getCustID() == customerId) {
						customerFound = true;
						break;
					}
				}
				if (!customerFound)
					throw new Exception();
			} catch (Exception e) {
				System.out.println(INVALID_INPUT);
				return;
			}
		} else {
			customerId = EnterCustomer(1);
			if (customerId == -1)
				return;
		}

		// order type
		System.out.println("Is this order for:");
		String choice = "1 - " + DBNinja.dine_in + "\n" +
				"2 - " + DBNinja.pickup + "\n" +
				"3 - " + DBNinja.delivery;
		System.out.println(choice);
		System.out.print("Enter order type? :");
		int orderType = -1;
		try {
			orderType = Integer.parseInt(br.readLine());
			if (orderType < 1 || orderType > 3)
				throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return;
		}

		// generating order based on order type
		Order createdOrder;
		if (orderType == 1) { // dinein
			System.out.println("Enter table number");
			int tableNo = -1;
			try {
				tableNo = Integer.parseInt(br.readLine());
				if (tableNo < 0) throw new Exception();
				createdOrder = new DineinOrder(-1,customerId,"", 0.0, 0.0, 0, tableNo);
			} catch (Exception e) {
				System.out.println(INVALID_INPUT);
				return;
			}
		} else if (orderType == 2) { // pick up
			createdOrder = new PickupOrder(-1, customerId, "", 0.0, 0.0, 0, 0);
		} else { // delivery
			System.out.println("Enter address? (street, city, state, zipcode)");
			String address = br.readLine();
			try {
				String[] arr = address.split(",");
				if (arr.length !=4)
					throw new Exception();
				Integer.parseInt(arr[3].trim());
			} catch (Exception e) {
				System.out.println(INVALID_INPUT);
				return;
			}
			createdOrder = new DeliveryOrder(-1, customerId, "", 0.0, 0.0, 0, address);
		}

		// adding pizza to order
		String morePizzaChoice = "";
		double orderSP = 0.0;
		double orderCP = 0.0;
		ArrayList<Pizza> createdPizzaList = new ArrayList<Pizza>();
		do {
			Pizza createdPizza = buildPizza();
			createdPizzaList.add(createdPizza);
			orderSP += createdPizza.getCustPrice();
			orderCP += createdPizza.getBusPrice();
			System.out.print("Do you want to add more pizzas? (y/n): ");
			morePizzaChoice = br.readLine();
		} while (morePizzaChoice.equalsIgnoreCase("y"));
		createdOrder.setCustPrice(orderSP);
		createdOrder.setBusPrice(orderCP);
		createdOrder.setPizzaList(createdPizzaList);


		// adding discounts to order
		System.out.print("Are there any discounts on this order? (y/n): ");
		String discountChoice = br.readLine();
		if (discountChoice.equalsIgnoreCase("y")) {
			ArrayList<Discount> discountList = DBNinja.getDiscountList();
			int discountId = -1;
			do {
				System.out.println("Showing discount list...");
				for (Discount d: discountList)
					System.out.println(d);
				System.out.print("Enter discount id? (-1 to stop): ");
				try {
					discountId = Integer.parseInt(br.readLine());
					boolean isDiscountPresent = false;
					for (Discount d: discountList) {
						if(d.getDiscountID() == discountId) {
							isDiscountPresent = true;
							break;
						}
					}
					if (!isDiscountPresent) {
						System.out.println("INVALID DISCOUNT ID");
					}
				} catch (Exception e) {
					System.out.println(INVALID_INPUT);
					return;
				}
				if (discountId != -1) {
					createdOrder.addDiscount(getDiscountById(discountId, discountList));
				}
			} while(discountId != -1);
		}
		// adding order to db
		DBNinja.addOrder(createdOrder);
		System.out.println("Finished sent to kitchen. CustomerTotal: " + createdOrder.getCustPrice());
		System.out.println("Returning to main menu...");
	}


	public static void viewCustomers() throws SQLException, IOException {
		/*
		 * Simply print out all of the customers from the database. 
		 */
		ArrayList<Customer> customerList = DBNinja.getCustomerList();
		for (Customer c: customerList)
			System.out.println(c.toString());
	}


	// Enter a new customer in the database
	public static int EnterCustomer(int menuOption) throws SQLException, IOException {
		/*
		 * Ask what the name of the customer is. YOU MUST TELL ME (the grader) HOW TO FORMAT THE FIRST NAME, LAST NAME, AND PHONE NUMBER.
		 * If you ask for first and last name one at a time, tell me to insert First name <enter> Last Name (or separate them by different print statements)
		 * If you want them in the same line, tell me (First Name <space> Last Name).
		 * 
		 * same with phone number. If there's hyphens, tell me XXX-XXX-XXXX. For spaces, XXX XXX XXXX. For nothing XXXXXXXXXXXX.
		 * 
		 * I don't care what the format is as long as you tell me what it is, but if I have to guess what your input is I will not be a happy grader
		 * 
		 * Once you get the name and phone number (and anything else your design might have) add it to the DB
		 */

		// menuOption = 3 - Adding customer
		// menuOption = 1 - Adding customer while adding order
		// returns the CustomerID of the existing or newly created customer
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter first name: ");
		String fName = br.readLine();
		System.out.print("Enter last name: ");
		String lName = br.readLine();
		System.out.print("Enter phone number (xxx-xxx-xxxx): ");
		String phone = "";
		try {
			phone = br.readLine();
			String pattern = "\\d{3}-\\d{3}-\\d{4}";
			if (!phone.matches(pattern))
				throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return -1;
		}
		Customer customer = new Customer(0, fName, lName, phone);
		int generatedCustomerId = DBNinja.addCustomer(customer);
		if (menuOption == 3) { // while adding customer through menu option
			String msg = "Customer added successfully. Returning to main menu...";
			if (generatedCustomerId < 0) {
				msg = "Customer already exists. Returning to main menu...";
				generatedCustomerId *= -1;
			}
			System.out.println(msg);
		} else { // while adding customer through order
			if (generatedCustomerId < 0) {
				System.out.println("Customer already exists. Using the existing customer...");
				generatedCustomerId *= -1;
			}
		}
		return generatedCustomerId;
	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException {
		/*
	 * This should be subdivided into two options: print all orders (using simplified view) and print all orders (using simplified view) since a specific date.
	 * 
	 * Once you print the orders (using either sub option) you should then ask which order I want to see in detail
	 * 
	 * When I enter the order, print out all the information about that order, not just the simplified view.
	 * 
	 */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Would you like to:");
		System.out.println("1 - display all orders.");
		System.out.println("2 - Show orders after a specific date.");
		System.out.print("Choose an option? (1,2) ");
		int orderViewOption = 0;
		try {
			orderViewOption = Integer.parseInt(br.readLine());
			if (!(orderViewOption == 1 || orderViewOption == 2))
				throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return;
		}
		ArrayList<Order> orderList = DBNinja.getCurrentOrders();
		if (orderViewOption == 2) {
			System.out.print("Enter date (yyyy-MM-dd): ");
			String givenDate = "";
			try {
				givenDate = br.readLine();
				String pattern = "\\d{4}-\\d{2}-\\d{2}";
				if (!givenDate.matches(pattern))
					throw new Exception();
				Iterator<Order> orderIterator = orderList.iterator();
				while (orderIterator.hasNext()) {
					if (isOrderDateSmallerThatGivenDate(orderIterator.next().getDate(), givenDate)) {
						orderIterator.remove();
					}
				}
			} catch (Exception e) {
				System.out.println(INVALID_INPUT);
				return;
			}
		}
		if (!orderList.isEmpty()) {
			for (Order order: orderList)
				System.out.println(order.toSimplePrint());
		} else {
			System.out.println("There are no orders");
			return;
		}
		System.out.println("Which order would you like to see in detail?");
		System.out.print("Enter order id: ");
		int orderId = -1;
		try {
			orderId = Integer.parseInt(br.readLine());
			boolean orderFound = false;
			for (Order order: orderList) {
				if (order.getOrderID() == orderId) {
					orderFound = true;
					System.out.println(order);
					break;
				}
			}
			if (!orderFound) throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
		}
	}

	public static boolean isOrderDateSmallerThatGivenDate (String orderDate, String givenDate) {
		givenDate += " 00:00:00";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date orderDateObj = null;
		Date givenDateObj = null;
		try {
			orderDateObj = dateFormat.parse(orderDate);
			givenDateObj = dateFormat.parse(givenDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return orderDateObj.compareTo(givenDateObj) < 0;
	}


	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException {
		/*All orders that are created through java (part 3, not the 7 orders from part 2) should start as incomplete
		 * 
		 * When this function is called, you should print all of the orders marked as complete 
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Order> orderList = DBNinja.getCurrentOrders();
		orderList.removeIf(order -> order.getIsComplete() == 1);
		if (orderList.isEmpty()) {
			System.out.println("All orders are complete. Returning to main menu...");
			return;
		} else {
			System.out.println("Displaying all incomplete orders");
		}
		for (Order o: orderList) {
			System.out.println(o.toSimplePrint());
		}
		System.out.print("Enter order id to mark it as complete: ");
		int orderId = -1;
		try {
			orderId = Integer.parseInt(br.readLine());
			boolean isIdPresent = false;
			for (Order o: orderList) {
				if (o.getOrderID() == orderId) {
					isIdPresent = true;
					break;
				}
			}
			if (!isIdPresent) throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return;
		}
		DBNinja.CompleteOrder(orderId);
		System.out.println("Order completed. Returning to main menu...");
	}



	// See the list of inventory and it's current level
	public static void ViewInventoryLevels() throws SQLException, IOException {
		//print the inventory. I am really just concerned with the ID, the name, and the current inventory
		ArrayList<Topping> toppingList = DBNinja.getInventory();
		System.out.printf("%-4s%-20s%-8s", "ID", "Name", "CurrINV\n");
		for (Topping t: toppingList)
			System.out.printf("%-4d%-20s%-8d\n", t.getTopID(), t.getTopName(), t.getCurINVT());
	}

	// Select an inventory item and add more to the inventory level to re-stock the
	// inventory
	public static void AddInventory() throws SQLException, IOException {
		/*
		 * This should print the current inventory and then ask the user which topping they want to add more to and how much to add
		 */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Topping> toppingList = DBNinja.getInventory();
		System.out.println("Showing current inventory...");
		System.out.printf("%-4s%-20s%-8s", "ID", "Name", "CurrINV\n");
		for (Topping t: toppingList)
			System.out.printf("%-4d%-20s%-8d\n", t.getTopID(), t.getTopName(), t.getCurINVT());
		System.out.print("Enter topping id: ");
		int toppingID;
		Topping t = null;
		try {
			toppingID = Integer.parseInt(br.readLine());
			boolean isIdPresent = false;
			for (Topping topping: toppingList) {
				if (topping.getTopID() == toppingID) {
					isIdPresent = true;
					t = topping;
					break;
				}
			}
			if (!isIdPresent) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return;
		}
		System.out.print("Enter amount to be added for " + t.getTopName() + ": ");
		double newTopAmt = 0;
		try {
			newTopAmt = Double.parseDouble(br.readLine());
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return;
		}
		DBNinja.AddToInventory(t, newTopAmt);
		System.out.println("Topping inventory updated. Returning to main menu...");
	}

	// A function that builds a pizza. Used in our add new order function
	public static Pizza buildPizza() throws SQLException, IOException {

		/*
		 * This is a helper function for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Pizza createdPizza = new Pizza(-1, "", "", -1, DBNinja.state_preparing, "", 0, 0);

		// pizza size
		String choice = "1 - " + DBNinja.size_s + "\n" +
				"2 - " + DBNinja.size_m + "\n" +
				"3 - " + DBNinja.size_l + "\n" +
				"4 - " + DBNinja.size_xl + "\n";
		System.out.print(choice);
		System.out.print("Enter pizza size? : ");
		try {
			int size = Integer.parseInt(br.readLine());
			if (size == 1) createdPizza.setSize(DBNinja.size_s);
			else if (size == 2) createdPizza.setSize(DBNinja.size_m);
			else if (size == 3) createdPizza.setSize(DBNinja.size_l);
			else if (size == 4) createdPizza.setSize(DBNinja.size_xl);
			else throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return null;
		}

		// pizza crust
		choice = "1 - " + DBNinja.crust_orig + "\n" +
				"2 - " + DBNinja.crust_pan + "\n" +
				"3 - " + DBNinja.crust_thin + "\n" +
				"4 - " + DBNinja.crust_gf + "\n";
		System.out.print(choice);
		System.out.print("Enter pizza crust? : ");
		try {
			int size = Integer.parseInt(br.readLine());
			if (size == 1) createdPizza.setCrustType(DBNinja.crust_orig);
			else if (size == 2) createdPizza.setCrustType(DBNinja.crust_pan);
			else if (size == 3) createdPizza.setCrustType(DBNinja.crust_thin);
			else if (size == 4) createdPizza.setCrustType(DBNinja.crust_gf);
			else throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return null;
		}

		// getting price for the selected size and crust
		double[] crustSPCP = DBNinja.getSPCPCrust(createdPizza.getSize(), createdPizza.getCrustType());
		createdPizza.setCustPrice(crustSPCP[0]);
		createdPizza.setBusPrice(crustSPCP[1]);
		System.out.println("CrustSP=" +crustSPCP[0]);

		// adding topping
		ArrayList<Topping> inventoryToppingList = DBNinja.getInventory();
		int toppingChoice = 0;
		int toppindIdx = 0;
		do {
			System.out.println("Showing current toppings...");
			System.out.printf("%-4s%-20s%-8s", "ID", "Name", "CurrINV\n");
			for (Topping t: inventoryToppingList) {
				System.out.printf("%-4d%-20s%-8d\n", t.getTopID(), t.getTopName(), t.getCurINVT());
			}
			System.out.print("Enter topping id? (-1 to stop): ");
			toppingChoice = Integer.parseInt(br.readLine());
			if (toppingChoice != -1) {
				System.out.print("Extra topping? (y/n): ");
				String eTChoice = br.readLine();
				if (eTChoice.equalsIgnoreCase("y")) {
					createdPizza.modifyDoubledArray(toppindIdx, true);
					createdPizza.addToppings(getToppingById(toppingChoice, inventoryToppingList), true);
				} else {
					createdPizza.modifyDoubledArray(toppindIdx, false);
					createdPizza.addToppings(getToppingById(toppingChoice, inventoryToppingList), false);
				}
			}
			toppindIdx++;
		} while(toppingChoice != -1);

		// adding discounts
		System.out.print("Are there any discounts on this pizza? (y/n): ");
		String discountChoice = br.readLine();
		ArrayList<Discount> addedDiscountList = new ArrayList<Discount>();
		if (discountChoice.equalsIgnoreCase("y")) {
			ArrayList<Discount> discountList = DBNinja.getDiscountList();
			int discountId = -1;
			do {
				System.out.println("Showing discount list...");
				for (Discount d: discountList)
					System.out.println(d);
				System.out.print("Enter discount id? (-1 to stop): ");
				discountId = Integer.parseInt(br.readLine());
				if (discountId != -1) {
					addedDiscountList.add(getDiscountById(discountId, discountList));
//					createdPizza.addDiscounts(getDiscountById(discountId, discountList));
				}
			} while(discountId != -1);
		}
		// subtracting discount
		double pizzaSP = createdPizza.getCustPrice();

		// I don't think Pizza.addDiscount() works correct in case of percentage - makes the amount CusPrice negative
		// as it's not divided by 100 like in Order.addDiscount()
		for (Discount d: addedDiscountList) {
			if (d.isPercent()) {
				pizzaSP = pizzaSP * (1 - d.getAmount()/100.0);
			} else {
				pizzaSP -= d.getAmount();
			}
		}
		createdPizza.setCustPrice(pizzaSP);
		inventoryToppingList.clear();
		addedDiscountList.clear();
		return createdPizza;
	}

	private static Discount getDiscountById(int discountId, ArrayList<Discount> dList) {
		for (Discount d: dList) {
			if (d.getDiscountID() == discountId)
				return d;
		}
		return null;
	}

	private static Topping getToppingById(int TopID, ArrayList<Topping> tops) {
		for (Topping t: tops) {
			if (t.getTopID() == TopID)
				return t;
		}
		return null;
	}

	private static int getTopIndexFromList(int TopID, ArrayList<Topping> tops) {
		/*
		 * This is a helper function I used to get a topping index from a list of toppings
		 * It's very possible you never need to use a function like this
		 * 
		 */
		int ret = -1;
		for (Topping topping: tops) {
			ret++;
			if (topping.getTopID() == TopID) {
				break;
			}
		}
		return ret;
	}


	public static void PrintReports() throws SQLException, NumberFormatException, IOException {
		/*
		 * This function calls the DBNinja functions to print the three reports.
		 * 
		 * You should ask the user which report to print
		 */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("1 - Topping Popularity\n2 - Profit By Pizza\n3 - Profit By Order Type");
		System.out.print("Select report type? (1,2,3): ");
		int reportType = 0;
		try {
			reportType = Integer.parseInt(br.readLine());
			if (!(reportType == 1 || reportType == 2 || reportType == 3))
				throw new Exception();
		} catch (Exception e) {
			System.out.println(INVALID_INPUT);
			return;
		}
		switch (reportType) {
			case 1:
				DBNinja.printToppingPopReport();
				break;
			case 2:
				DBNinja.printProfitByPizzaReport();
				break;
			case 3:
				DBNinja.printProfitByOrderType();
				break;
		}
	}

}


//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
//DO NOT EDIT ANYTHING BELOW HERE, I NEED IT FOR MY TESTING DIRECTORY. IF YOU EDIT SOMETHING BELOW, IT BREAKS MY TESTER WHICH MEANS YOU DO NOT GET GRADED (0)

/*
CPSC 4620 Project: Part 3 â€“ Java Application Due: Thursday 11/30 @ 11:59 pm 125 pts

For this part of the project you will complete an application that will interact with your database. Much of the code is already completed, you will just need to handle the functionality to retrieve information from your database and save information to the database.
Note, this program does little to no verification of the input that is entered in the interface or passed to the objects in constructors or setters. This means that any junk data you enter will not be caught and will propagate to your database, if it does not cause an exception. Be careful with the data you enter! In the real world, this program would be much more robust and much more complex.

Program Requirements:

Add a new order to the database: You must be able to add a new order with pizzas to the database. The user interface is there to handle all of the input, and it creates the Order object in the code. It then calls DBNinja.addOrder(order) to save the order to the database. You will need to complete addOrder. Remember addOrder will include adding the order as well as the pizzas and their toppings. Since you are adding a new order, the inventory level for any toppings used will need to be updated. You need to check to see if there is inventory available for each topping as it is added to the pizza. You can not let the inventory level go negative for this project. To complete this operation, DBNinja must also be able to return a list of the available toppings and the list of known customers, both of which must be ordered appropropriately.

View Customers: This option will display each customer and their associated information. The customer information must be ordered by last name, first name and phone number. The user interface exists for this, it just needs the functionality in DBNinja

Enter a new customer: The program must be able to add the information for a new customer in the database. Again, the user interface for this exists, and it creates the Customer object and passes it to DBNinja to be saved to the database. You need to write the code to add this customer to the database. You do need to edit the prompt for the user interface in Menu.java to specify the format for the phone number, to make sure it matches the format in your database.

View orders: The program must be able to display orders and be sorted by order date/time from most recent to oldest. The program should be able to display open orders, all the completed orders or just the completed order since a specific date (inclusive) The user interface exists for this, it just needs the functionality in DBNinja

Mark an order as completed: Once the kitchen has finished prepping an order, they need to be able to mark it as completed. When an order is marked as completed, all of the pizzas should be marked as completed in the database. Open orders should be sorted as described above for option #4. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Inventory Levels: This option will display each topping and its current inventory level. The toppings should be sorted in alphabetical order. Again, the user interface exists for this, it just needs the functionality in DBNinja

Add Inventory: When the inventory level of an item runs low, the restaurant will restock that item. When they do so, they need to enter into the inventory how much of that item was added. They will select a topping and then say how many units were added. Note: this is not creating a new topping, just updating the inventory level. Make sure that the inventory list is sorted as described in option #6. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Reports: The program must be able to run the 3 profitability reports using the views you created in Part 2. Again, the user interface exists for this, it just needs the functionality in DBNinja

Modify the package DBConnector to contain your database connection information, this is the same information you use to connect to the database via MySQL Workbench. You will use DBNinja.connect_to_db to open a connection to the database. Be aware of how many open database connections you make and make sure the database is properly closed!
Your code needs to be secure, so any time you are adding any sort of parameter to your query that is a String, you need to use PreparedStatements to prevent against SQL injections attacks. If your query does not involve any parameters, or if your queries parameters are not coming from a String variable, then you can use a regular Statement instead.

The Files: Start by downloading the starter code files from Canvas. You will see that the user interface and the java interfaces and classes that you need for the assignment are already completed. Review all these files to familiarize yourself with them. They contain comments with instructions for what to complete. You should not need to change the user interface except to change prompts to the user to specify data formats (i.e. dashes in phone number) so it matches your database. You also should not need to change the entity object code, unless you want to remove any ID fields that you did not add to your database.

You could also leave the ID fields in place and just ignore them. If you have any data types that donâ€™t match (i.e. string size options as integers instead of strings), make the conversion when you pull the information from the database or add it to the database. You need to handle data type differences at that time anyway, so it makes sense to do it then instead of making changes to all of the files to handle the different data type or format.

The Menu.java class contains the actual user interface. This code will present the user with a menu of options, gather the necessary inputs, create the objects, and call the necessary functions in DBNinja. Again, you will not need to make changes to this file except to change the prompt to tell me what format you expect the phone number in (with or without dashes).

There is also a static class called DBNinja. This will be the actual class that connects to the database. This is where most of the work will be done. You will need to complete the methods to accomplish the tasks specified.

Also in DBNinja, there are several public static strings for different crusts, sizes and order types. By defining these in one place and always using those strings we can ensure consistency in our data and in our comparisons. You donâ€™t want to have â€œSMALLâ€� â€œsmallâ€� â€œSmallâ€� and â€œPersonalâ€� in your database so it is important to stay consistent. These strings will help with that. You can change what these strings say in DBNinja to match your database, as all other code refers to these public static strings.

Start by changing the class attributes in DBConnector that contain the data to connect to the database. You will need to provide your database name, username and password. All of this is available is available in the Chapter 15 lecture materials. Once you have that done, you can begin to build the functions that will interact with the database.

The methods you need to complete are already defined in the DBNinja class and are called by Menu.java, they just need the code. Two functions are completed (getInventory and getTopping), although for a different database design, and are included to show an example of connecting and using a database. You will need to make changes to these methods to get them to work for your database.

Several extra functions are suggested in the DBNinja class. Their functionality will be needed in other methods. By separating them out you can keep your code modular and reduce repeated code. I recommend completing your code with these small individual methods and queries. There are also additional methods suggested in the comments, but without the method template that could be helpful for your program. HINT, make sure you test your SQL queries in MySQL Workbench BEFORE implementing them in codeâ€¦it will save you a lot of debugging time!

If the code in the DBNinja class is completed correctly, then the program should function as intended. Make sure to TEST, to ensure your code works! Remember that you will need to include the MySQL JDBC libraries when building this application. Otherwise you will NOT be able to connect to your database.

Compiling and running your code: The starter code that will compile and â€œrunâ€�, but it will not do anything useful without your additions. Because so much code is being provided, there is no excuse for submitting code that does not compile. Code that does not compile and run will receive a 0, even if the issue is minor and easy to correct.

Help: Use MS Teams to ask questions. Do not wait until the last day to ask questions or get started!

Submission You will submit your assignment on Canvas. Your submission must include: â€¢ Updated DB scripts from Part 2 (all 5 scripts, in a folder, even if some of them are unchanged). â€¢ All of the class code files along with a README file identifying which class files in the starter code you changed. Include the README even if it says â€œI have no special instructions to shareâ€�. â€¢ Zip the DB Scripts, the class files (i.e. the application), and the README file(s) into one compressed ZIP file. No other formats will be accepted. Do not submit the lib directory or an IntellJ or other IDE project, just the code.

Testing your submission Your project will be tested by replacing your DBconnector class with one that connects to a special test server. Then your final SQL files will be run to recreate your database and populate the tables with data. The Java application will then be built with the new DBconnector class and tested.

No late submissions will be accepted for this assignment.*/


import java.sql.SQLException;
import java.util.Scanner;


/*
 * Simulates a Bank which holds all Account information and validates pins.
 */
public class Bank {
	static SqlProcedures sqlProcedures;
	static Scanner read;
	static UserInput userInput;

	/*
	 * Main Parent menu
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		read = new Scanner(System.in);
		sqlProcedures = new SqlProcedures(read);
		userInput = new UserInput(read, sqlProcedures);
		System.out.println("Welcome to Spartan Bank");
		mainMenuOptions option = null;
		while (true) {
			option = userInput.getMainMenuInput();
			switch (option) {
			case C:
				createAccountMenu();
				break;
			case L:
				loginMenu();
				break;
			case Q:
				System.out.println("Thank you for visiting Spartan Bank");
				System.out.println("We hope to see you again");
				read.close();
				System.exit(0);
			}
		}
	}


	/*
	 * Create Account Menu. Asks the user for first name, last name... checks if
	 * username is taken and inserts into DB.
	 */
	private static void createAccountMenu() {
		String firstName, lastName, email, username = null, password = null, confirmPassword;
		boolean usernameTaken = true, passwordConfirmed = false;
		System.out.println("Please fill in the following fields to create your Account");
		System.out.println("First Name:");
		firstName = read.next();
		System.out.println("Last Name:");
		lastName = read.next();
		System.out.println("Email:");
		email = read.next();
		// Default is true, checks if username taken
		while (usernameTaken) {
			System.out.println("Desired username:");
			username = read.next();
			usernameTaken = sqlProcedures.isTaken(username);
		}
		// Default false, checks if passwords are the same
		while (!passwordConfirmed) {
			System.out.println("Desired Password:");
			password = read.next();

			System.out.println("Please re-enter your password:");
			confirmPassword = read.next();
			if (password.equals(confirmPassword)) {
				passwordConfirmed = true;
			} else
				System.out.println("Passwords are not matching. Please try again");
		}
		// Inserts into database and returns the Account_ID
		int checkingNumber = sqlProcedures.insertAccount(firstName, lastName, email, username, password);
	
		System.out.println(
				"Creation of Account is a success! Your checkin number is: " + Integer.toString(checkingNumber));
		System.out.println("Please login to continue using your account");

	}

	/*
	 * Login Menu. Asks for username, password and logs in to Acccount Menu
	 */
	private static void loginMenu() {
		String username, password;
		System.out.println("Username: ");
		username = read.next();
		System.out.println("Password:");
		password = read.next();
		//Admin account is account id = 1
		int account_id = sqlProcedures.validateLogin(username, password);
		if (account_id == 1) {
			System.out.println("Success! You are logging in as admin");
			adminMainMenu();
		}
		//anything else abova 1 is a regular user
		else if (account_id > 1) {
			System.out.println("Success!");
			if (sqlProcedures.checkPremium(account_id))
			{
				System.out.println("You are a premium member. Thank you for trusting us with your money!");
			}
			accountMainMenu();
		} 
	}

	/*
	 * adminMainMenu. Upon login if user is an admin they are directed here so they may reactivate accounts, see total cash in bank, and total customers.
	 */
	private static void adminMainMenu() {

		System.out.println("Hello admin. You may do the following actions.");

		while (true) {
			System.out.println("[R]eactivate account, [V]iew Total amount of customers, View [T]otal cash in Bank, or [Q]uit");
			char action = userInput.getAdminMenuInput();
			switch (action) {
				case 'V':
					totalCustomersMenu();
					break;
				case 'R':
					reactivateAccountMenu();
					break;
				case 'T':
					System.out.println("The total amount of cash in the bank is " + sqlProcedures.totalCash());
					break;
				case 'Q':
					System.out.println("Logging off");
					System.out.println("We hope to see you again");
					return;
			}
		}
	}
	
	/*
	 * Gets how many Active or Non-Active accounts the Bank currently has
	 */
	private static void totalCustomersMenu() {
		System.out.println("Would you like to get the count of all the [A]ctive accounts or [N]on-active?");
		char input = read.next().charAt(0);
		//Only allows two inputs.
		while (input != 'A' && input != 'N') {
			System.out.println("Invalid input.");
			System.out.println("Would you like to get the count of all the [A]ctive accounts or [N]on-active?");
			input = read.next().charAt(0);
		}
		int total = sqlProcedures.getTotalCustomers(input);
		if (input == 'A')
		{
			System.out.println("There is a total of " +total +" Active account" );
		}
		if (input == 'N')
		{
			System.out.println("There is a total of " +total +" Non-Active account" );
		}
	}

	/*
	 * Admin can reactivate account upon contact if need be
	 */
	private static void reactivateAccountMenu() {
		
			System.out.println("Please enter the username of the account you would like to reactivate.");
			String username = read.next();
	
			System.out.println("Please enter the admin password to confirm reactivation.");
			String password = read.next();
			if (!sqlProcedures.reactivateAccount(username, password))
			{
				System.out.println("Please try again");
				reactivateAccountMenu();
			}
		
		}
	

	/*
	 * After logging in Main Menu
	 */
	private static void accountMainMenu() {
		while (true) {
			// Gets user input
			AccountMenuOptions userInput2 = userInput.getAccountMenuInput();
			switch (userInput2) {
			case D:
				depositMenu();
				break;
			case W:
				withdrawalMenu();
				break;
			case T:
				transferMenu();
				break;
			case V:
				viewBalanceMenu();
				break;
			case e:
				if (deleteAccountMenu()) {
					return;
				}
				break;
			case C:
				checkHistoryMenu();
				break;
			case L:
				System.out.println("Have a nice day. Logging off...");
				return;
			}
		}
	}

	


	/*
	 * Delete Menu. Deletes the account of the User
	 */
	private static boolean deleteAccountMenu() {
		System.out.println("Are you sure you want to delete your account?");
		System.out.println("[Y]es or [N]o?");
		char value = read.next().charAt(0);
		boolean validated = false;
		if (value == 'Y') {

			// Prepares to call Stored Procedure
			while (!validated) {
				validated = sqlProcedures.deleteAccount();
				if (!validated) {
					return false;
				}

			}

			System.out.println("Your account has been deactivated!!");
			return true;

		} else if (value == 'N') {
			System.out.println("Your account is still active!!");
			return false;
		}

		System.out.println("Incorrect Input. Please try again!!");
		return deleteAccountMenu();
	}
	
	/*
	 * Check history Menu. Provides the information of transaction history of the User
	 */
	private static void checkHistoryMenu() {
		System.out.println("Please enter the amount of history you would like to see");
		int historyAmount = (int) userInput.getUserDouble("view"); 
		sqlProcedures.checkHistory(historyAmount);
	}
	
	/*
	 * Transfer Menu. Allows the User1 to transfer amount from one account to  User2 account
	 * Checks if the User1 is transfering valid and a non-negative amount.
	 * Sets the new amount in the User1 and User2 account  
	 */
	private static void transferMenu() {
		double amt;
		System.out.println("Would you like to transfer from your [C]heckings or [S]avings account?");
		char accountType = userInput.getUserAccountType();
		System.out.println("Please enter the number of the account you would like to transfer to.");
		int transferAccountID = userInput.getUserTranferAccountID();
		System.out.println("Would you like to transfer to their [C]heckings or [S]avings account?");
		char transferAccountType = userInput.getUserAccountType();
		//Gets current balance
		double currentBalance = sqlProcedures.getCurrentBalance(accountType);
		System.out.println("Please enter the amount you want to transfer: ");
		//Gets userinput and check if its a double.
		amt = userInput.getUserDouble("transfer");
		//Cannot transfer more than current balance
		while (amt > currentBalance) {
			System.out.println("Your transfer amount is greater than your current balance.");
			System.out.println("Your current balance is: $" + currentBalance);
			System.out.println("Please enter the amount you want to transfer: ");
			amt = userInput.getUserDouble("transfer");
		}
		currentBalance = sqlProcedures.transfer(accountType, transferAccountID, transferAccountType, amt);
		if (currentBalance> 0)
		{
			System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);
		}
		
	}

	
	/*
	 * ViewBalance Menu. View current balance of Users checkings and savings account
	 */
	private static void viewBalanceMenu() {

		System.out.println("[C]heckings or [S]avings account?");
		char accountType = userInput.getUserAccountType();
		double currentBalance = sqlProcedures.viewBalance(accountType);
		//Should never be false
		if (currentBalance > -1) {
			System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);
		}
	}

	
	/*
	 * Withdrawal Menu. Allows User to withdraw some amount from its checking/savings account
	 * Validates whether the entered amount is valid and non-negative.
	 * Sets the new amount as the current balance for the User's
	 */
	private static void withdrawalMenu() {
		double amt;
		try {
			System.out.println("[C]heckings or [S]avings account?");
			char accountType = userInput.getUserAccountType();
			double currentBalance = sqlProcedures.getCurrentBalance(accountType);
			System.out.println("Please enter the amount you want to withdraw: ");
			amt = userInput.getUserDouble("withdraw");
			//Cannot withdraw more than currentBalance
			while (amt > currentBalance) {
				System.out.println("Your withdraw amount is greater than your current balance.");
				System.out.println("Your current balance is: $" + currentBalance);
				System.out.println("Please enter the amount you want to withdraw: ");
				amt = userInput.getUserDouble("withdraw");
			}
			currentBalance = sqlProcedures.withdraw(accountType, amt);
			System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);

		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}

	}

	
	/*
	 * Deposit Menu. Allows User to deposit some amount from its checking/savings account
	 * Validates whether the entered amount is valid and non-negative.
	 * Sets the new amount as the current balance for the User's
	 */
	private static void depositMenu() {
		double amt;

		System.out.println("[C]heckings or [S]avings account?");
		char accountType = userInput.getUserAccountType();
		double currentBalance = sqlProcedures.getCurrentBalance(accountType);
		System.out.println("Please enter the amount you want to deposit: ");
		amt = userInput.getUserDouble("deposit");
		currentBalance = sqlProcedures.deposit(accountType, amt);
		System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);

	}



}

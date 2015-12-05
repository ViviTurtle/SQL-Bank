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
		userInput = new UserInput(read);
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
		if (sqlProcedures.validateLogin(username, password)) {
			System.out.println("Success!");
			accountMainMenu();
		}
	}

	/*
	 * Validates a login against DB
	 * 
	 * @param username the username to check
	 * 
	 * @param password the password to check
	 * 
	 * @returns true if valid, else false
	 */

	/*
	 * After logging in Main Menu
	 * Gives options to the user to select a function
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
    *Deletes/deactivates the account of the User
	*/
	private static boolean deleteAccountMenu() {
		System.out.println("Are you sure you want to delete your account?");
		System.out.println("[Y]es or [N]o?");
		char value = read.next().charAt(0);
		boolean validated = false;
<<<<<<< HEAD
		if (value == 'Y') {

			// Prepares to call Stored Procedure
			while (!validated) {
				validated = sqlProcedures.deleteAccount();
				if (!validated) {
					System.out.println("Invalid Account");
=======
		//If the user is sure to delete the account
		if (value == 'Y'){
			try {
				//Prepares to call Stored Procedure
				while (!validated)
				{
					CallableStatement cs = conn.prepareCall("{CALL SP_DELETE_ACCOUNT(?,?)}");
					System.out.println("Please enter your username or press [Q] to [Q]uit: ");
					String username = read.next();
					if (username.equals("Q"))
					{
							return false;
					}
					System.out.println("Please enter your password: ");
					String password = read.next();
				    cs.setString(1, username);
				    cs.setString(2, password);
					//Executes
					 ResultSet rs = cs.executeQuery();
					 rs.next();
					//returns the ACCOUNT_ID column
					validated = rs.getBoolean("result");
					if (!validated)
					{
						System.out.println("Invalid Account");	
					}
>>>>>>> origin/Collaborator-Branch
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

	private static void checkHistoryMenu() {
		// TODO Auto-generated method st ub

	}

	private static void transferMenu() {
		double amt;
		System.out.println("Would you like to transfer from your [C]heckings or [S]avings account?");
		char accountType = userInput.getUserAccountType();
		System.out.println("Please enter the number of the account you would like to transfer to.");
		int transferAccountID = userInput.getUserTranferAccountID();
		System.out.println("Would you like to transfer to their [C]heckings or [S]avings account?");
		char transferAccountType = userInput.getUserAccountType();
		double currentBalance = sqlProcedures.getCurrentBalance(accountType);
		System.out.println("Please enter the amount you want to transfer: ");
		amt = userInput.getUserDouble("transfer");
		while (amt > currentBalance) {
			System.out.println("Your withdraw amount is greater than your current balance.");
			System.out.println("Your current balance is: $" + currentBalance);
			System.out.println("Please enter the amount you want to withdraw: ");
			amt = userInput.getUserDouble("transfer");
		}
		currentBalance = sqlProcedures.transfer(accountType, transferAccountID, transferAccountType, amt);
		System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);
	}

	

	private static void viewBalanceMenu() {

		System.out.println("[C]heckings or [S]avings account?");
		char accountType = userInput.getUserAccountType();
		double currentBalance = sqlProcedures.viewBalance(accountType);
		if (currentBalance > -1) {

			System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);
		}
	}

<<<<<<< HEAD
=======
	/*
	* It provides the current balance available in the Users checking's and saving's
	* On Users request
	 */

	private static void viewBalanceMenu() {
		try{
			System.out.println("[C]heckings or [S]avings account?");
			char accountType = read.next().charAt(0);
			
			if(accountType == 'C'){
				CallableStatement cs = conn.prepareCall("{CALL SP_VIEW_BALANCE(?,?)}");
				cs.setInt(1, account_id);
				cs.setInt(2, 1);
				//Executes
				ResultSet rs = cs.executeQuery();
				//If Results is empty, it means it doesn't exist
				if (!rs.isBeforeFirst()) {
					System.out.println("It doesnt exist!!");
				}
				else{
					rs.next();
					//returns the ACCOUNT_ID column
					int balance = rs.getInt("AMOUNT");
					System.out.println("Your current balance in Checkings Account is: " + balance + "$");
				}
				
			}	
			else if(accountType == 'S'){
				CallableStatement cs = conn.prepareCall("{CALL SP_VIEW_BALANCE(?,?)}");
				cs.setInt(1, account_id);
				cs.setInt(2, 2);
				//Executes
				ResultSet rs = cs.executeQuery();
				//If Results is empty, it means it doesn't exist
				if (!rs.isBeforeFirst()) {
					System.out.println("It doesnt exist!!");
				}
				else{
					rs.next();
					//returns the ACCOUNT_ID column
					int balance = rs.getInt("AMOUNT");
					System.out.println("Your current balance in Savings Account is: " + balance + "$");
				}
			}
			else{
				System.out.println("Invalid Entry!!");
				viewBalanceMenu();
			}
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);

		}

	}

	/*
	* Helper method to check whether the User has entered the correct input for account type
	 */
>>>>>>> origin/Collaborator-Branch
	

	/*
	* WithdrawalMenu helps the user withdraw desired amount from the desired account type
	* Also checks whether the User has valid and non-negative input
	* Also makes sure that the account has valid and non-negative balance
	 */

	private static void withdrawalMenu() {
		double amt;
		try {
			System.out.println("[C]heckings or [S]avings account?");
			char accountType = userInput.getUserAccountType();
			double currentBalance = sqlProcedures.getCurrentBalance(accountType);

			amt = 0.0;
			System.out.println("Please enter the amount you want to withdraw: ");
			amt = userInput.getUserDouble("withdraw");
			while (amt > currentBalance) {
				System.out.println("Your withdraw amount is greater than your current balance.");
				System.out.println("Your current balance is: $" + currentBalance);
				System.out.println("Please enter the amount you want to withdraw: ");
				amt = userInput.getUserDouble("withdraw");
			}
			currentBalance = sqlProcedures.withdraw(accountType, amt);
			System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);
<<<<<<< HEAD

		} catch (Exception ex) {
=======
			
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}

	}

	/*
	* Helper method which checks whether the amount entered by user is valid and non-negative
	 */

	
	private static double getUserDouble(String transanction)
	{
		String amtString = read.next();
		while (!isDouble(amtString))
		{
			System.out.println("Invalid amount");
			System.out.println("Please enter the amount you want to " + transanction +": ");
			amtString = read.next();
		}
		double input = Double.parseDouble(amtString);
		if (input < 0)
		{
			System.out.println("You cannot input a negative number.");
			System.out.println("Please enter the amount you want to " + transanction +": ");
			return getUserDouble(transanction);
		}
		return input;
	}

	/*
	* Helper method which updates the amount in account and database once withdrawal is carried out 
	 */

	
	private static double spWithdraw(char accountType, double amount)
	{
		
		try
		{
			
			CallableStatement cs = conn.prepareCall("{CALL SP_WITHDRAW_AMOUNT(?,?,?)}");
			cs.setInt(1, account_id);
			if (accountType == 'C')
			{
				cs.setInt(2, 1);
			}
			else if (accountType == 'S')
			{
				cs.setInt(2, 2);
			}
			cs.setDouble(3, amount);
			//Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			return  rs.getInt("AMOUNT");
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
			return 0;
		}

	}

	/*
	* Helper method which updates amount in the users account and database
	 */
	
	private static double spGetCurrentBalance(char accountType)
	{
		try
		{
			CallableStatement cs = conn.prepareCall("{CALL SP_VIEW_BALANCE(?,?)}");
			cs.setInt(1, account_id);
			if (accountType == 'C')
			{
				cs.setInt(2, 1);
			}
			else if (accountType == 'S')
			{
				cs.setInt(2, 2);
			}
			//Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			double currentBalance = rs.getInt("AMOUNT");
			System.out.println("Your current balance is: $" + currentBalance);
			return currentBalance;
			
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
			return 0;
		}
		
	}
	/*
	* Helper method which updates the amount in account and database once deposit is carried out 
	 */
	private static double spDespoit(char accountType, double amount)
	{
		try
		{
			CallableStatement cs = conn.prepareCall("{CALL SP_DEPOSIT_AMOUNT(?,?,?)}");
			cs.setInt(1, account_id);
			if (accountType == 'C')
			{
				cs.setInt(2, 1);
			}
			else if (accountType == 'S')
			{
				cs.setInt(2, 2);
			}
			cs.setDouble(3, amount);
			//Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			return  rs.getInt("AMOUNT");
		}
		catch (Exception ex) {
>>>>>>> origin/Collaborator-Branch
			System.out.println(ex.toString());
			System.exit(1);
		}

	}

	/*
	* DepositlMenu helps the user withdraw desired amount from the desired account type
	* Also checks whether the User has valid and non-negative input
	* Also makes sure that the account has valid and non-negative balance
	 */


	

	private static void depositMenu() {
		double amt;
<<<<<<< HEAD

		System.out.println("[C]heckings or [S]avings account?");
		char accountType = userInput.getUserAccountType();
		double currentBalance = sqlProcedures.getCurrentBalance(accountType);
		System.out.println("Please enter the amount you want to deposit: ");
		amt = userInput.getUserDouble("deposit");
		currentBalance = sqlProcedures.deposit(accountType, amt);
		System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);

=======
	
			System.out.println("[C]heckings or [S]avings account?");
			char accountType = getUserAccountType();
			double currentBalance = spGetCurrentBalance(accountType);
			System.out.println("Please enter the amount you want to deposit: ");
			amt = getUserDouble("deposit");
			currentBalance = spDespoit(accountType, amt);
			System.out.println("Success! Your current balance in this Account is: $ " + currentBalance);
			
		}
	
	/*
	*Checks if the input is double.
	 */
	private static boolean isDouble(String s)
	{
		try
		{
			Double.parseDouble(s);
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
		
>>>>>>> origin/Collaborator-Branch
	}



}

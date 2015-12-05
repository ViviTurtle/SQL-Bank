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
					System.out.println("Invalid Account");
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

		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}

	}

	

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

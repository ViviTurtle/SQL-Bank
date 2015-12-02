import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


/*
 * Simulates a Bank which holds all Account information and validates pins.
 */
public class Bank {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/Bank";
	// Database credentials
	static final String USER = "Bank_User";
	static final String PASS = "bank";
	private static Connection conn = null;
	static Scanner read;
	private static int account_id = -1;

	/*
	 * Main Parent menu
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		//initialize JDBC
		Class.forName(JDBC_DRIVER);
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		read = new Scanner(System.in);
		System.out.println("Welcome to Spartan Bank");
		mainMenuOptions userInput = null;
		while (true) {
			userInput = getMainMenuInput();
			switch (userInput) {
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
	 * Gets a valid MainMenu input from user
	 * returns a MainMenuOption
	 */
	protected static mainMenuOptions getMainMenuInput() {
		try {
			System.out.println("Please select an option below.");
			System.out.println("[C]reate an Account, [L]ogin, [Q]uit");
			//Reads input from user and converts in mainMenuOption
			return mainMenuOptions.valueOf(read.next());

		} catch (Exception ex) {
			//Recursive call if invalid mainMenuOption
			System.out.println("Invalid Entry.");
			return getMainMenuInput();

		}
	}

	//Only three main menu Options, [C]reate an Account, [L]ogin, [Q]uit
	protected enum mainMenuOptions {
		C, L, Q
	}
	
	/*
	 * Create Account Menu. Asks the user for first name, last name... checks if username is taken and inserts into DB.
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
		//Default is true, checks if username taken
		while (usernameTaken) {
			System.out.println("Desired username:");
			username = read.next();
			usernameTaken = isTaken(username);
		}
		//Default false, checks if passwords are the same
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
		//Inserts into database and returns the Account_ID
		int checkingNumber = insertAccount(firstName, lastName, email, username, password);
		account_id = checkingNumber;
		System.out.println(
				"Creation of Account is a success! Your checkin number is: " + Integer.toString(checkingNumber));
		System.out.println("Please login to continue using your account");

	}

	/*
	 * Inserts into the actual SQL Database and returns the resulting Account ID
	 * @param firstName the First Name to be inserted
	 * @param lastName the Last Name to be inserted
	 * @param email the Email to be inserted
	 * @param username the Username to be inserted
	 * @param password the Password to be inserted
	 * returns the Account_ID created from the insert
	 */
	private static int insertAccount(String firstName, String lastName, String email, String username,
			String password) {
		try {
			//Prepares to call statement
			CallableStatement cs = conn.prepareCall("{CALL SP_CREATE_ACCOUNT(?,?,?,?,?)}");
			//Sets parameters to use
			cs.setString(1, firstName);
			cs.setString(2, lastName);
			cs.setString(3, email);
			cs.setString(4, username);
			cs.setString(5, password);
			//Executes
			ResultSet rs = cs.executeQuery();
			//Use first row
			rs.next();
			//return the ACCOUNT_ID Column
			return rs.getInt("ACCOUNT_ID");
		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
		return 0;

	}

	/*
	 * Checks if a username has been taken
	 * @param username the username to check against db
	 * @returns true if taken else false
	 */
	private static boolean isTaken(String username) {

		try {
			//Prepares to call Stored Procedure
			CallableStatement cs = conn.prepareCall("{CALL SP_CHECK_USERNAME(?)}");
			//Sets parameters
			cs.setString(1, username);
			//Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			//If result returned 1, then it exists, else returns 0
			if (rs.getInt("result") == 1) {
				System.out.println("Username is taken, Please select a different Username.");
				return true;
			}

		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
		return false;
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
		if (validateLogin(username, password)) {
			System.out.println("Success!");
			accountMainMenu();
		}
	}
	
	/*
	 * Validates a login against DB
	 * @param username the username to check
	 * @param password the password to check
	 * @returns true if valid, else false
	 */
	private static boolean validateLogin(String username, String password) {
		try {
			//Prepares to call Stored Procedure
			CallableStatement cs = conn.prepareCall("{CALL SP_LOGIN(?,?)}");
			//Sets Parameters
			cs.setString(1, username);
			cs.setString(2, password);
			//Executes
			ResultSet rs = cs.executeQuery();
			//If Results is empty, it means it doesn't exist


				rs.next();
				//returns the ACCOUNT_ID column
				account_id = rs.getInt("ACCOUNT_ID");
				if (account_id == -1)
				{
					System.out.println("Account was deactivated. Please contact Vivi Langga at (408) 607-XXXX for any questons.");
					return false;
				}
				else if (account_id == -2)
				{
					System.out.println("Invalid Login. Returning to main menu...");
					return false;
				}
			
		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
		return true;
	}
	
	/*
	 * After logging in Main Menu
	 */
	private static void accountMainMenu() {
		while (true) {
			//Gets user input
			AccountMenuOptions userInput2 = getAccountMenuInput();
			switch (userInput2) {
			case D:
				depositMenu();
				break;
			case W:
				withdrawalMenu();
				break;
			case T:
				transferMenu();
			case V:
				viewBalanceMenu();
				break;
			case e:
				if (deleteAccountMenu())
				{
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
	 * Gets the Account Menu Input and validates it.
	 * returns an AccountMenuOptions user input
	 */
	private static AccountMenuOptions getAccountMenuInput() {
		try {
			System.out.println("Please select an option below.");
			System.out.println(
					"[D]eposit, [W]ithdrawal, [T]ransfer, [V]iew Account Balance, D[e]lete account,[C]heck History, [L]ogout?");
			return AccountMenuOptions.valueOf(read.next());

		} catch (Exception ex) {
			//If not a proper value in AccountMenuOptions it does a recursive call to get another input
			System.out.println("Invalid Entry.");
			return getAccountMenuInput();

		}
	}

	/*
	 * Account Main Menu Options which only allow: [D]eposit, [W]ithdrawal, [T]ransfer, [V]iew Account Balance, D[e]lete account,[C]heck History, [L]ogout?
	 */
	protected enum AccountMenuOptions {
		D, W, T, V, L, e, C
	}

	//------------------------------------TODO-------------------------------------------------------------
	private static boolean deleteAccountMenu() {
		// TODO Auto-generated method stub
		System.out.println("Are you sure you want to delete your account?");
		System.out.println("[Y]es or [N]o?");
		char value = read.next().charAt(0);
		boolean validated = false;
		
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
				}
				
					System.out.println("Your account has been deactivated!!");
					return true;
			} 
			catch (Exception ex) {
				System.out.println("Invalid Input.");
				System.exit(1);
			}	
		}
		else if (value == 'N'){
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
		// TODO Auto-generated method stub

	}

	private static void viewBalanceMenu() {
		// TODO Auto-generated method stub
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

	private static void withdrawalMenu() {
		// TODO Auto-generated method stub
		try{
			System.out.println("[C]heckings or [S]avings account?");
			char accountType = read.next().charAt(0);
			double amt = 0.0;
			if(accountType == 'C'){
				System.out.println("Please enter the amount you want to withdraw: ");
				amt = read.nextDouble();
				CallableStatement cs = conn.prepareCall("{CALL SP_WITHDRAW_AMOUNT(?,?,?)}");
				cs.setInt(1, account_id);
				cs.setInt(2, 1);
				cs.setDouble(3, amt);
				//Executes
				ResultSet rs = cs.executeQuery();
				//If Results is empty, it means it doesn't exist
				if (!rs.isBeforeFirst()) {
					System.out.println("It doesnt exist!!");
				}
				else{
					rs.next();
					int balance = rs.getInt("AMOUNT");
					System.out.println("Your current balance in Checkings Account is: " + balance + "$");
				}

			    //conn.close();

			}
			
			else if(accountType == 'S'){
				System.out.println("Please enter the amount you want to withdraw: ");
				amt = read.nextDouble();
				CallableStatement cs = conn.prepareCall("{CALL SP_WITHDRAW_AMOUNT(?,?,?)}");
				cs.setInt(1, account_id);
				cs.setInt(2, 2);
				cs.setDouble(3, amt);
				//Executes
				ResultSet rs = cs.executeQuery();
				//If Results is empty, it means it doesn't exist
				if (!rs.isBeforeFirst()) {
					System.out.println("It doesnt exist!!");
				}
				else{
					rs.next();
					int balance = rs.getInt("AMOUNT");
					System.out.println("Your current balance in Savings Account is: " + balance + "$");
				}

			    //conn.close();

			}
			
			else{
				System.out.println("Invalid Entry!!");
				withdrawalMenu();
			}
			
			
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}

	}

	private static void depositMenu() {
		// TODO Auto-generated method stub
		try{
			System.out.println("[C]heckings or [S]avings account?");
			char accountType = read.next().charAt(0);
			double amt = 0.0;
			if(accountType == 'C'){
				System.out.println("Please enter the amount you want to withdraw: ");
				amt = read.nextDouble();
				CallableStatement cs = conn.prepareCall("{CALL SP_DEPOSIT_AMOUNT(?,?,?)}");
				cs.setInt(1, account_id);
				cs.setInt(2, 1);
				cs.setDouble(3, amt);
				//Executes
				ResultSet rs = cs.executeQuery();
				//If Results is empty, it means it doesn't exist
				if (!rs.isBeforeFirst()) {
					System.out.println("It doesnt exist!!");
				}
				else{
					rs.next();
					int balance = rs.getInt("AMOUNT");
					System.out.println("Your current balance in Checkings Account is: " + balance + "$");
				}

			    //conn.close();

			}
			
			else if(accountType == 'S'){
				System.out.println("Please enter the amount you want to withdraw: ");
				amt = read.nextDouble();
				CallableStatement cs = conn.prepareCall("{CALL SP_DEPOSIT_AMOUNT(?,?,?)}");
				cs.setInt(1, account_id);
				cs.setInt(2, 2);
				cs.setDouble(3, amt);
				//Executes
				ResultSet rs = cs.executeQuery();
				//If Results is empty, it means it doesn't exist
				if (!rs.isBeforeFirst()) {
					System.out.println("It doesnt exist!!");
				}
				else{
					rs.next();
					int balance = rs.getInt("AMOUNT");
					System.out.println("Your current balance in Savings Account is: " + balance + "$");
				}

			    //conn.close();

			}
			
			else{
				System.out.println("Invalid Entry!!");
				withdrawalMenu();
			}
			
			
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
		
	}
}

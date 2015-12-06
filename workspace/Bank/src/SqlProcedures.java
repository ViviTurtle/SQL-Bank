import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SqlProcedures {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/Bank";
	// Database credentials
	static final String USER = "Bank_User";
	static final String PASS = "bank";
	private static Connection conn = null;
	Scanner read;
	int account_id;

	public SqlProcedures(Scanner read) throws ClassNotFoundException, SQLException {
		// initialize JDBC
		Class.forName(JDBC_DRIVER);
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		this.read = read;

	}

	protected double transfer(char accountType, int transferAccountID, char transferAccountType, double amt) {
		try {

			CallableStatement cs = conn.prepareCall("{CALL SP_TRANSFER_AMOUNT(?,?,?,?,?)}");
			cs.setInt(1, account_id);
			if (accountType == 'C') {
				cs.setInt(2, 1);
			} else if (accountType == 'S') {
				cs.setInt(2, 2);
			}
			cs.setDouble(3, transferAccountID);
			if (transferAccountType == 'C') {
				cs.setInt(4, 1);
			} else if (transferAccountType == 'S') {
				cs.setInt(4, 2);
			}
			cs.setDouble(5, amt);
			// Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			return rs.getDouble("Amount");
		} catch (Exception ex) {
			System.out.println("You cannot transfer to your own account");
			return -1;
		}
	}

	protected boolean validateLogin(String username, String password) {
		try {
			// Prepares to call Stored Procedure
			CallableStatement cs = conn.prepareCall("{CALL SP_LOGIN(?,?)}");
			// Sets Parameters
			cs.setString(1, username);
			cs.setString(2, password);
			// Executes
			ResultSet rs = cs.executeQuery();
			// If Results is empty, it means it doesn't exist

			rs.next();
			// returns the ACCOUNT_ID column
			account_id = rs.getInt("ACCOUNT_ID");
			if (account_id == -1) {
				System.out.println(
						"Account was deactivated. Please contact Vivi Langga at (408) 607-XXXX for any questons.");
				return false;
			} else if (account_id == -2) {
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
	 * Inserts into the actual SQL Database and returns the resulting Account ID
	 * 
	 * @param firstName the First Name to be inserted
	 * 
	 * @param lastName the Last Name to be inserted
	 * 
	 * @param email the Email to be inserted
	 * 
	 * @param username the Username to be inserted
	 * 
	 * @param password the Password to be inserted returns the Account_ID
	 * created from the insert
	 */
	protected int insertAccount(String firstName, String lastName, String email, String username, String password) {
		try {
			// Prepares to call statement
			CallableStatement cs = conn.prepareCall("{CALL SP_CREATE_ACCOUNT(?,?,?,?,?)}");
			// Sets parameters to use
			cs.setString(1, firstName);
			cs.setString(2, lastName);
			cs.setString(3, email);
			cs.setString(4, username);
			cs.setString(5, password);
			// Executes
			ResultSet rs = cs.executeQuery();
			// Use first row
			rs.next();
			// return the ACCOUNT_ID Column
			return rs.getInt("ACCOUNT_ID");
		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
		return 0;

	}

	protected boolean accountExists(int accountID) {

		try {
			// Prepares to call Stored Procedure
			CallableStatement cs = conn.prepareCall("{CALL SP_ACCOUNT_EXISTS(?)}");
			// Sets parameters
			cs.setInt(1, accountID);
			// Executes
			ResultSet rs = cs.executeQuery();

			// If result doesn't have columns return false.
			if (!rs.isBeforeFirst()) {
				return false;
			}

		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
		return true;
	}

	protected double withdraw(char accountType, double amount) {

		try {

			CallableStatement cs = conn.prepareCall("{CALL SP_WITHDRAW_AMOUNT(?,?,?)}");
			cs.setInt(1, account_id);
			if (accountType == 'C') {
				cs.setInt(2, 1);
			} else if (accountType == 'S') {
				cs.setInt(2, 2);
			}
			cs.setDouble(3, amount);
			// Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			return rs.getDouble("Amount");
		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
			return 0;
		}

	}

	protected double getCurrentBalance(char accountType) {
		try {
			CallableStatement cs = conn.prepareCall("{CALL SP_VIEW_BALANCE(?,?)}");
			cs.setInt(1, account_id);
			if (accountType == 'C') {
				cs.setInt(2, 1);
			} else if (accountType == 'S') {
				cs.setInt(2, 2);
			}
			// Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			double currentBalance = rs.getDouble("Amount");
			System.out.println("Your current balance is: $" + currentBalance);
			return currentBalance;

		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
			return 0;
		}

	}

	protected double deposit(char accountType, double amount) {
		try {
			CallableStatement cs = conn.prepareCall("{CALL SP_DEPOSIT_AMOUNT(?,?,?)}");
			cs.setInt(1, account_id);
			if (accountType == 'C') {
				cs.setInt(2, 1);
			} else if (accountType == 'S') {
				cs.setInt(2, 2);
			}
			cs.setDouble(3, amount);
			// Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			return rs.getDouble("AMOUNT");
		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
			return 0;
		}

	}

	/*
	 * Checks if a username has been taken
	 * 
	 * @param username the username to check against db
	 * 
	 * @returns true if taken else false
	 */
	protected boolean isTaken(String username) {

		try {
			// Prepares to call Stored Procedure
			CallableStatement cs = conn.prepareCall("{CALL SP_CHECK_USERNAME(?)}");
			// Sets parameters
			cs.setString(1, username);
			// Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			// If result returned 1, then it exists, else returns 0
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

	protected boolean deleteAccount() {
		try {
			CallableStatement cs = conn.prepareCall("{CALL SP_DELETE_ACCOUNT(?,?)}");
			System.out.println("Please enter your username or press [Q] to [Q]uit: ");
			String username = read.next();
			if (username.equals("Q")) {
				return false;
			}
			System.out.println("Please enter your password: ");
			String password = read.next();
			cs.setString(1, username);
			cs.setString(2, password);
			// Executes
			ResultSet rs = cs.executeQuery();
			rs.next();
			// returns the ACCOUNT_ID column
			return rs.getBoolean("result");

		} catch (Exception ex) {
			System.out.println("Invalid Input.");
			System.exit(1);
			return false;
		}
	}

	protected double viewBalance(char accountType) {
		try {

			CallableStatement cs = conn.prepareCall("{CALL SP_VIEW_BALANCE(?,?)}");
			cs.setInt(1, account_id);
			if (accountType == 'C') {
				cs.setInt(2, 1);
			} else if (accountType == 'S') {
				cs.setInt(2, 2);
			}
			// Executes
			ResultSet rs = cs.executeQuery();
			// If Results is empty, it means it doesn't exist
			if (!rs.isBeforeFirst()) {
				return -1;
			} else {
				rs.next();
				// returns the ACCOUNT_ID column
				return rs.getDouble("Amount");
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
			return -1;
		}
	}
	
	protected void checkHistory(int historyAmount)
	{
		try {

			CallableStatement cs = conn.prepareCall("{CALL SP_CHECK_HISTORY(?,?)}");
			cs.setInt(1, account_id);
			cs.setInt(2, historyAmount);
	
			// Executes
			ResultSet rs = cs.executeQuery();
			// If Results is empty, it means it doesn't exist
			if (!rs.isBeforeFirst()) {
				System.out.println("You have no Transanction History");
			} else {
				while (rs.next())
				{
					double test = rs.getDouble("AMOUNT");
					// Prints the mysql data
					System.out.printf("Account Type:%38s \nTransanction Type:%33s \nAmount: %43f \nDate: %45s ", rs.getString("ACCOUNT_TYPE"), rs.getString("TRANS_TYPE"), rs.getDouble("AMOUNT"),rs.getString("TRANSANCTION_DATE"));
					System.out.println();
					System.out.println();
				}
				
		       } 
			}
		catch (Exception ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
	}

}

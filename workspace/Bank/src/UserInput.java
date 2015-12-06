import java.util.Scanner;

public class UserInput {
	//stores classes
	Scanner read;
	SqlProcedures sqlProcedures;

	/*
	 * Initiates UserInput
	 * @param read the scanner to use
	 * @para sqlProcedures the SQL Class to use
	 */
	public UserInput(Scanner read,SqlProcedures sqlProcedures ) {
		this.read = read;
		this.sqlProcedures = sqlProcedures;
	}

	/*
	 * Gets the Account Menu Input and validates it. returns an
	 * @return AccountMenuOptions user input
	 */
	protected AccountMenuOptions getAccountMenuInput() {
		try {
			System.out.println("Please select an option below.");
			System.out.println(
					"[D]eposit, [W]ithdrawal, [T]ransfer, [V]iew Account Balance, D[e]lete account,[C]heck History, [L]ogout?");
			return AccountMenuOptions.valueOf(read.next());

		} catch (Exception ex) {
			// If not a proper value in AccountMenuOptions it does a recursive
			// call to get another input
			System.out.println("Invalid Entry.");
			return getAccountMenuInput();

		}
	}


	/*
	 * Gets a valid MainMenu input from user returns a MainMenuOption
	 * @return a mainMenuOption which only allows C, L or Q
	 */
	protected mainMenuOptions getMainMenuInput() {
		try {
			System.out.println("Please select an option below.");
			System.out.println("[C]reate an Account, [L]ogin, [Q]uit");
			// Reads input from user and converts in mainMenuOption
			return mainMenuOptions.valueOf(read.next());

		} catch (Exception ex) {
			// Recursive call if invalid mainMenuOption
			System.out.println("Invalid Entry.");
			return getMainMenuInput();

		}
	}

	/*
	 * @Gets from the user a Valid Account Type
	 * @return The valid Account type
	 */
	protected char getUserAccountType() {
		char accountType = read.next().charAt(0);
		while (accountType != 'C' && accountType != 'S') {
			System.out.println("Invalid Input");
			System.out.println("[C]heckings or [S]avings account?");
			accountType = read.next().charAt(0);
		}
		return accountType;
	}

	/*
	 * gets a valid TransferAccountID
	 * @return The AccountID
	 */
	protected int getUserTranferAccountID() {
		String accountIDString = read.next();
		while (!isDouble(accountIDString) || !sqlProcedures.accountExists(Integer.parseInt(accountIDString))) {
			System.out.println("Invalid Account");
			System.out.println("Please enter the number of the account you would like to transfer to.");
			accountIDString = read.next();
		}
		return Integer.parseInt(accountIDString);
	}

	/*
	 * Gets a valid double input from the user
	 * @param transanction The transanction string to modify the Print statement(view, transfer, deposit, withdraw)
	 * @return The double
	 */
	protected double getUserDouble(String transanction) {
		String amtString = read.next();
		while (!isDouble(amtString)) {
			System.out.println("Invalid amount");
			System.out.println("Please enter the amount you want to " + transanction + ": ");
			amtString = read.next();
		}
		double input = Double.parseDouble(amtString);
		//cannot have a negative number
		if (input < 0) {
			System.out.println("You cannot input a negative number.");
			System.out.println("Please enter the amount you want to " + transanction + ": ");
			return getUserDouble(transanction);
		}
		return input;
	}
	
	/*
	 * Gets a valid Admin Input
	 * @return The valid admin input
	 */
	protected char getAdminMenuInput()
	{
		char input = read.next().charAt(0);
		while (input != 'R' && input != 'T' && input != 'V' && input != 'Q') {
			System.out.println("Invalid input.");
			System.out.println("[R]eactivate account, [V]iew Total amount of customers, or View [T]otal cash in Bank, or [Q]uit");
			input = read.next().charAt(0);
		}
		return input;
	}
	
	/*
	 * Checks if a string can be converted to a double
	 * @param s The string to check for
	 * @return True if can be converted
	 */
	protected boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}

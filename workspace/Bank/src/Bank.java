import java.util.Scanner;
/*
 * Simulates a Bank which holds all Account information and validates pins.
 */
public class Bank
{
	static Scanner read;
	static mainMenuOptions userInput;
	
	public static void main(String[] args)
	{
		read = new Scanner(System.in);
		System.out.println("Welcome to Spartan Bank");
		while(true)
		{
			userInput = mainMenuValidator();
			switch(userInput)
			{
				case C:
					createAccount();
					break;
				case L:
					login();
					break;
				case Q:
					System.out.println("Thank you for visiting Spartan Bank");
					System.out.println("We hope to see you again");
					read.close();
					System.exit(0);
			}
		}
	}

	private static void createAccount()
	{
		String firstName, lastName, email, username = null, password = null, confirmPassword;
		boolean usernameTaken = true, passwordConfirmed = false;
		System.out.println("Please fill in the following fields to create your Account");
		System.out.println("First Name:");
		firstName = read.next();
		System.out.println("Last Name:");
		lastName = read.next();
		System.out.println("Email:");
		email = read.next();
		while (usernameTaken)
		{
			System.out.println("Desired username:");
			username = read.next();
			usernameTaken = checkTaken();
		}
		while(!passwordConfirmed)
		{
			System.out.println("Desired Password:");
			password = read.next();
			
			System.out.println("Please re-enter your password:");
			confirmPassword = read.next();
			if (password.equals(confirmPassword))
			{
				passwordConfirmed = true;
			}
			else System.out.println("Passwords are not matching. Please try again");
		}
		int checkingNumber = insertAccount(firstName, lastName, email, username, password);
		System.out.println("Creation of Account is a success! Your checkin number is: " + Integer.toString(checkingNumber));
		System.out.println("Please login to continue using your account");
		
		
	}
	private static AccountMenuOptions accountMainMenuValidator() {
		try
		{
			System.out.println("Please select an option below.");
			System.out.println("[D]eposit, [W]ithdrawal, [T]ransfer, [V]iew Account Balance, [L]ogout?");
			return AccountMenuOptions.valueOf(read.next()); 
			
		}
		catch (Exception ex)
		{
			System.out.println("Invalid Entry.");
			return accountMainMenuValidator();
			
		}
	}
	private static void accountMainMenu()
	{
		while (true)
		{
			AccountMenuOptions userInput2 = accountMainMenuValidator();
			switch(userInput2)
			{
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
				case L:
					System.out.println("Have a nice day. Logging off...");
					return;
			}	
		}
	}

	private static void transferMenu() {
		// TODO Auto-generated method stub
		
	}

	private static void viewBalanceMenu() {
		// TODO Auto-generated method stub
		
	}

	private static void withdrawalMenu() {
		// TODO Auto-generated method stub
		
	}

	private static void depositMenu() {
		// TODO Auto-generated method stub
		
	}

	private static int insertAccount(String firstName, String lastName, String email, String username, String password) {
		// TODO Auto-generated method stub
		return 0;
		
	}

	private static boolean checkTaken() {
		// TODO Auto-generated method stub
		return false;
	}

	private static void login() {
		String username, password; 
		System.out.println("Username: ");
		username = read.next();
		System.out.println("Password:");
		password = read.next();
		if (validLogin(username, password))
		{
			System.out.println("Success!");
			accountMainMenu();
		}
		
	}
	
	private static boolean validLogin(String username, String password) {
		// TODO Auto-generated method stub
		return true;
		
	}

	protected static mainMenuOptions mainMenuValidator()
	{
		try
		{
			System.out.println("Please select an option below.");
			System.out.println("[C]reate an Account, [L]ogin, [Q]uit");
			return mainMenuOptions.valueOf(read.next());
			
		}
		catch (Exception ex)
		{
			System.out.println("Invalid Entry.");
			return mainMenuValidator();
			
		}
	}
	protected enum mainMenuOptions 
	{
		C, L, Q
	}
	protected enum AccountMenuOptions 
	{
		D, W, T, V, L
	}
	
}
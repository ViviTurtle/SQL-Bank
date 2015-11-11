/*
 * Simulates an individuals records of savings or checking
 */
public class Account {
	private long cardNum;
	private String password;
	//NOTE* if Check or Saving balance is -1, it means that person has no account
	//e.g if savings is -1, that person has no saving account
	private long checkingBalance;
	private long savingsBalance;
	/*
	 * Creates an Account
	 * @param cardNum the credit card Number the account is related to
	 * @param password the password to access the account
	 * @param checkingBalance the balance for checking account (if -1, that person has no checking)
	 * @param savingsBalance the balance for savings account (if -1, that person has no saving)
	 */
	public Account(long cardNum, String password, long checkingBalance, long savingsBalance )
	{
		this.cardNum = cardNum;
		this.password = password;
		this.checkingBalance= checkingBalance;
		this.savingsBalance = savingsBalance;
	} 
	/*
	 * Gets the card number the account is related to
	 * @returns the card number
	 */
	public long getCardNum()
	{
		return cardNum;
	}
	/*
	 * Gets the password needed to access the account
	 * @returns the password
	 */
	public String getPassword()
	{
		return password;
	}
	/*
	 * Gets the Savings Balance
	 * @returns the the savings balance
	 */
	public long getSavings()
	{
		return savingsBalance;
	}
	/*
	 * Gets the Checking Balance
	 * @returns checkingBalance
	 */
	public long getChecking()
	{
		return checkingBalance;
	}
	/*
	 * Sets the Savings account after a withdrawal
	 * @param newBalance the balance to set the account with
	 */
	public void setSavings(long newBalance)
	{
		savingsBalance = newBalance;
	}
	/*
	 * Sets the Checking account after a withdrawal
	 * @param newBalance the balance to set the account with
	 */
	public void setChecking(long newBalance)
	{
		checkingBalance = newBalance;
	}
	/*
	 * Checks to see if a person has a Savings Account
	 * @returns false if balance is -1 else that person ahs one
	 */
	public boolean hasSavings()
	{
		if (savingsBalance == -1)
		{
			return false;
		}
		return true;
	}
	/*
	 * Checks to see if a person has a Checking Account
	 * @returns false if balance is -1 else that person has one
	 */
	public boolean hasChecking()
	{
		if (checkingBalance == -1)
		{
			return false;
		}
		return true;
	
}
}
	



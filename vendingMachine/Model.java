//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
//
// Class          : vendingMachine.Model
//
// Author         : Richard E. Pattis
//                  Computer Science Department
//                  Carnegie Mellon University
//                  5000 Forbes Avenue
//                  Pittsburgh, PA 15213-3891
//                  e-mail: pattis@cs.cmu.edu
//
// Maintainer     : Author
//
//
// Description:
//
//   The Model for the VendingMachine package implements the guts of the
// vending machine: it responds to presses of buttons created by the
// Conroller (deposit, cancel, buy), and tells the View when it needs
// to update its display (calling the update in view, which calls the
// accessor methods in this classes)
// 
//   Note that "no access modifier" means that the method is package
// friendly: this means the member is public to all other classes in
// the calculator package, but private elsewhere.
//
// Future Plans   : More Comments
//                  Increase price as stock goes down
//                  Decrease price if being outsold by competition
//                  Allow option to purchase even if full change cannot 
//                    be returned (purchaser pays a premium to quench thirst)
//                  Allow user to enter 2 x money and gamble: 1/2 time
//                    all money returned with product; 1/2 time no money and
//                    no product returned
//
// Program History:
//   9/20/01: R. Pattis - Operational for 15-100
//   2/10/02: R. Pattis - Fixed Bug in change-making method
//
//
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////


package vendingMachine;


import java.lang.Math;
import java.util.Arrays;


public class Model {
	//Define fields (all instance variables)
	
	private View view;         // Model must tell View when to update itself
	private int cokeLeft;
	private int pepsiLeft;
	private String deposited;
	private int intDeposited;
	private int intCokePrice;
	private String cokePrice;
	private int intPepsiPrice;
	private String pepsiPrice;
	private String message;
	private int quartersLeft, dimesLeft, nickelsLeft;
	private String lastAction;
	private String lastDepositMoney;
	private String lastBuyDrink;

	private int[] lastDepositArray = new int[3];
	
	//I defined about 10 more fields
	
	//Define constructor
	public Model(int quarters, int dimes, int nickel, int cokeBottle, int pepsiBottle, int cokeCost, int pepsiCost){
		quartersLeft = quarters;
		nickelsLeft = nickel;
		cokeLeft = cokeBottle;
		pepsiLeft = pepsiBottle;
		intCokePrice = cokeCost;
		cokePrice = " ¢"+intCokePrice;
		intPepsiPrice = pepsiCost;
		pepsiPrice = " ¢"+intPepsiPrice;
	}

	
	//Refer to the view (used to call update after each button press)
	public void addView(View v)
	{view = v;}
	
	//Define required methods: mutators (setters) and accessors (getters)
	public String getDeposited(){
		return deposited;
	}
	public int getCokeLeft(){
		return cokeLeft;
	}

	public int getPepsiLeft(){
		return pepsiLeft;
	}

	public String getMessage(){
		return message;
	}

	public String getPepsiPrice(){
		return pepsiPrice;
	}

	public String getCokePrice(){
		return cokePrice;
	}

	public void deposit(int moneyDeposited){
		intDeposited+=moneyDeposited;
		deposited = "¢"+intDeposited;
		if (moneyDeposited == 25){
			quartersLeft+=1;
			lastDepositMoney = "Quarter";
			message = "Quarter Deposited";
		}
		if (moneyDeposited == 10){
			dimesLeft+=1;
			lastDepositMoney = "Dime";
			message = "Dime Deposited";
		}
		if (moneyDeposited == 5){
			nickelsLeft+=1;
			lastDepositMoney = "Nickel";
			message = "Nickel Deposited";
		}
		lastAction = "Deposit";
		view.update();
	}

	public void buy(String product){
		if (product.equals("Pepsi")){
			if (intDeposited>=intPepsiPrice) {
				pepsiLeft -= 1;
				intDeposited -= intPepsiPrice;
				deposited = "¢" + intDeposited;
				lastBuyDrink = "Pepsi";
				message = "Pepsi Purchased";
				lastDepositArray = new int[3];
				findChange(quartersLeft, dimesLeft, nickelsLeft, intPepsiPrice);
			}
		}
		if (product.equals("Coke")){
			if (intDeposited>=intCokePrice) {
				cokeLeft -= 1;
				intDeposited -= intCokePrice;
				deposited = "¢" + intDeposited;
				lastBuyDrink = "Coke";
				message = "Coke Purchased";
				lastDepositArray = new int[3];
				findChange(quartersLeft, dimesLeft, nickelsLeft, intCokePrice);
			}
		}
		view.update();
		lastAction = "Buy";
	}

	public void cancel(){
		if (!lastAction.equals("Cancel")) {
			if (lastAction.equals("Deposit")) {
				cancelDeposit(lastDepositMoney);
			}
			if (lastAction.equals("Buy")) {
				cancelDrinks(lastBuyDrink);
			}
		}
		lastAction="Cancel";
		view.update();
	}

	//Represent "interesting" state of vending machine
	public String toString()
	{
		return "Vending Machine State: \n" +
			"  Coke     Left      = " + cokeLeft     + "\n" +
			"  Pepsi    Left      = " + pepsiLeft    + "\n" +
			"  Quarters Left      = " + quartersLeft + "\n" +
			"  Dimes    Left      = " + dimesLeft    + "\n" +
			"  Nickels  Left      = " + nickelsLeft  + "\n" +
			"  Array              = " + Arrays.toString(lastDepositArray) + "\n"+
			"  Total    Deposit   = " + deposited    + "\n" +
			"  Last     Action    = " + lastAction   + "\n" +
			"  Last     Deposit   = " + lastDepositMoney + "\n" +
			"  Last     BuyDrink  = " + lastBuyDrink + "\n" +
			"  message            = " + message      + "\n";
		//Display any other instance variables that you declare too
	}
	
	//Define helper methods
	public void findChange(int quartersLeft, int dimesLeft, int nickelsLeft, int totalExpenditure){
		int remainingChange = totalExpenditure;
		if (quartersLeft>0) {
			int quartersNeed = Math.min(quartersLeft, remainingChange / 25);
			remainingChange -= quartersNeed * 25;
			this.quartersLeft -= quartersNeed;
			lastDepositArray[0] = quartersNeed;
		}
		if (remainingChange > 0 && dimesLeft>0) {
			int dimesNeed = Math.min(dimesLeft, remainingChange / 10);
			remainingChange -= dimesNeed * 10;
			this.dimesLeft -= dimesNeed;
			lastDepositArray[1] = dimesNeed;
		}
		if (remainingChange > 0 && nickelsLeft>0) {
			int nickelsNeed = Math.min(nickelsLeft, remainingChange / 5);
			this.nickelsLeft -= nickelsNeed;
			lastDepositArray[2] = nickelsNeed;
		}
	}
	public void cancelDrinks(String drink) {
		if (drink.equals("Coke")) {
			cokeLeft += 1;
			intDeposited += intCokePrice;
			deposited = "¢" + intDeposited;
			message = "Your Last Coke Purchase Canceled";
			quartersLeft+=lastDepositArray[0];
			dimesLeft+=lastDepositArray[1];
			nickelsLeft+=lastDepositArray[2];
		}
		if (drink.equals("Pepsi")) {
			pepsiLeft += 1;
			intDeposited += intPepsiPrice;
			deposited = "¢" + intDeposited;
			message = "Your Last Pepsi Purchase Canceled";
			quartersLeft+=lastDepositArray[0];
			dimesLeft+=lastDepositArray[1];
			nickelsLeft+=lastDepositArray[2];
		}
	}

	public void cancelDeposit(String moneyType){
		if (moneyType.equals("Quarter")) {
			quartersLeft -= 1;
			intDeposited -= 25;
			deposited = "¢" + intDeposited;
			message = "Your Last Quarter Deposit Canceled";
		}
		if (moneyType.equals("Dime")) {
			dimesLeft -= 1;
			intDeposited -= 10;
			deposited = "¢" + intDeposited;
			message = "Your Last Dime Deposit Canceled";
		}
		if (moneyType.equals("Nickel")) {
			nickelsLeft -= 1;
			intDeposited -= 5;
			deposited = "¢" + intDeposited;
			message = "Your Last Nickel Deposit Canceled";
		}
	}
}

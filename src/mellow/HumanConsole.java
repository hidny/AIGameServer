package mellow;

//so far this class only has stubs.

//nad print all relevant variables.
//My plan is to make the client-side do the remembering.

public class HumanConsole implements PlayerDecider {
	
	private String name;
	
	public HumanConsole(String name) {
		this.name = name;
	}
	
	public void start(String names[], int dealerIndex) {
		
	}
	
	public void updateFinalBid(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue) {
		
	}
	
	public void updateCurrentScore(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue) {
		
	}
	
	//asks the current player to play a card.
	//if the player reneges, he/she will get a warning!
	public int getCard(int playerCards[], int currentCardsInFight[], int indexCurrentPlayer) {
		return playerCards[0];
	}
	
	//0=mellow
	//1-13 is the number of tricks the players says he/she could make.
	public int getBid(int playerCards[], int prevBids[], int dealerIndex) {
		return 1;
	}
	
	public String getName() {
		return this.name;
	}
}

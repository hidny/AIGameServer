package euchre;

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
	public int getCard(int playerCards[], int currentCardsInFight[], EuchreCall euchreCall, int dealerIndex, int currentPlayerIndex) {
		return playerCards[0];
	}
	
	public String getCall(int playerCards[], int dealerIndex, String trumpCard, int callingRound, int currentPlayerIndex) {
		return "";
	}
	
	public String getName() {
		return this.name;
	}
}

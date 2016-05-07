package euchre;

//so far this class only has stubs.

//nad print all relevant variables.
//My plan is to make the client-side do the remembering.

import java.util.concurrent.Semaphore;

public class ClientPlayerDecider implements PlayerDecider {
	
	private static String UNENTERED_MOVE = "NAM";
	
	private String currentMove = UNENTERED_MOVE;
	private Semaphore semMoveAvailable = new Semaphore(0);
	
	public void setMove(String move) {
		//this stops ais from spamming moves... that's ok though. :)
		if(currentMove.equals(UNENTERED_MOVE)) {
			
			//TODO: make sure the move is legal!
			
			currentMove = move;
	
			try {
				//June 7th: TODO: did I break it?
				if (semMoveAvailable.availablePermits() < 1) {
					semMoveAvailable.release();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String name;
	
	public ClientPlayerDecider(String name) {
		this.name = name;
	}
	
	public void start(String names[], int dealerIndex) {

		//Not needed: the console will inform the player:
	}
	
	public void updateFinalBid(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue) {

		//Not needed: the console will inform the player:
	}
	
	public void updateCurrentScore(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue) {

		//Not needed: the console will inform the player:
	}
	
	
	//TODO: copy mellow code for bids.
	
	//asks the current player to play a card.
	//if the player renages, he/she will get a warning!
	public int getCard(int playerCards[], int currentCardsInFight[], EuchreCall euchreCall, int dealerIndex, int currentPlayerIndex) {
		
		try {
			semMoveAvailable.acquire();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		currentMove = currentMove.toUpperCase().trim();
		for(int i=0; i<playerCards.length; i++) {
			if(currentMove.equals(random.card.DeckFunctions.getCardString(playerCards[i]) ) ) {
				currentMove = UNENTERED_MOVE;
				return playerCards[i];
			}
		}
		currentMove = UNENTERED_MOVE;
		return playerCards[0];
		
		
	}
	

	//TODO: copy mellow code for bids.
	//0=mellow
	//1-13 is the number of tricks the players says he/she could make.
	public String getCall(int playerCards[], int dealerIndex, String trumpCard, int callingRound, int currentPlayerIndex) {

		try {
			semMoveAvailable.acquire();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String ret = currentMove.toUpperCase().trim();
		
		currentMove = UNENTERED_MOVE;
		
		return ret;
	}
	
	public String getName() {
		return this.name;
	}
}

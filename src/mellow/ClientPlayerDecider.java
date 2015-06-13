package mellow;

//so far this class only has stubs.

//nad print all relevant variables.
//My plan is to make the client-side do the remembering.

import java.util.concurrent.Semaphore;

public class ClientPlayerDecider implements PlayerDecider {
	
	private static String UNENTERED_MOVE = "NAM";
	private static int NOT_A_BID = -1;
	
	private String currentMove = UNENTERED_MOVE;
	private Semaphore semMoveAvailable = new Semaphore(0);
	
	public void setMove(String move) {
		//this stops ais from spamming moves... that's ok though. :)
		if(currentMove.equals(UNENTERED_MOVE)) {
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
	
	//asks the current player to play a card.
	//if the player renages, he/she will get a warning!
	public int getCard(int playerCards[], int currentCardsInFight[], int indexCurrentPlayer) {
		try {
			semMoveAvailable.acquire();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		currentMove = currentMove.toUpperCase().trim();
		for(int i=0; i<playerCards.length; i++) {
			if(currentMove.equals(random.DeckFunctions.getCardString(playerCards[i]) ) ) {
				currentMove = UNENTERED_MOVE;
				return playerCards[i];
			}
		}
		currentMove = UNENTERED_MOVE;
		return playerCards[0];
	}
	
	//0=mellow
	//1-13 is the number of tricks the players says he/she could make.
	public int getBid(int playerCards[], int prevBids[], int dealerIndex) {
		int move;
		try {
			semMoveAvailable.acquire();
			move = Integer.parseInt(currentMove);	
		} catch (Exception e) {
			move = NOT_A_BID;
		}
		
		currentMove = UNENTERED_MOVE;
		return move;
	}
	
	public String getName() {
		return this.name;
	}
}

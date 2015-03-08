package mellow;

//so far this class only has stubs.
//TODO: make this interact with the console
//nad print all relevant variables.
//My plan is to make the client-side do the remembering.

public class ClientPlayerDecider implements PlayerDecider {
	
	private static String UNENTERED_MOVE = "NAM";
	private static int NOT_A_BID = -1;
	
	private String currentMove = UNENTERED_MOVE;
	
	public void setMove(String move) {
		currentMove = move;
	}
	
	private String name;
	
	public ClientPlayerDecider(String name) {
		this.name = name;
	}
	
	public void start(String names[], int dealerIndex) {
		
	}
	
	public void updateFinalBid(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue) {
		
	}
	
	public void updateCurrentScore(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue) {
		
	}
	
	//asks the current player to play a card.
	//if the player renages, he/she will get a warning!
	public int getCard(int playerCards[], int currentCardsInFight[], int indexCurrentPlayer) {
		try {
			while(currentMove.equals(UNENTERED_MOVE) ) {
				//TODO: sleep
				Thread.sleep(100);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		currentMove = currentMove.toUpperCase().trim();
		for(int i=0; i<playerCards.length; i++) {
			if(currentMove.equals(deck.DeckFunctions.getCardString(playerCards[i]) ) ) {
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
			while(currentMove.equals(UNENTERED_MOVE)) {
				//TODO: sleep
				Thread.sleep(100);
			}
			
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

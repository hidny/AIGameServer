package mellow;

public interface PlayerDecider {
	public void start(String names[], int dealerIndex);
	
	//mellow + 0 bid = DOUBLE MELLOW!
	public void updateFinalBid(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue);
	
	public void updateCurrentScore(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue);
	
	//asks the current player to play a card.
	//if the player renages, he/she will get a warning!
	public int getCard(int playerCards[], int currentCardsInFight[], int indexCurrenPlayer);
	
	//0=mellow
	//1-13 is the number of tricks the players says he/she could make.
	public int getBid(int playerCards[], int prevBids[], int dealerIndex);
	
	public String getName();
}

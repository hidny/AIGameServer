package euchre;

public interface PlayerDecider {
	public void start(String names[], int dealerIndex);
	
	//mellow + 0 bid = DOUBLE MELLOW!
	public void updateFinalBid(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue);
	
	public void updateCurrentScore(boolean mellowRed, int bidRed, boolean mellowBlue, int bidBlue);
	
	//asks the current player to play a card.
	//if the player renages, he/she will get a warning!
	public int getCard(int playerCards[], int currentCardsInFight[], EuchreCall euchreCall, int dealerIndex, int currentPlayerIndex);
	
	//pis pass
	//1st round:
	//say the suit of the trump card.
	//ex: "S"
	
	//2nd round:
	//Say the suit of the desired trump.
	//ex: "s"
	
	//For going alone: say "alone" or "a" as the second word.
	//ex: Sa
	//    S a
	
	public String getCall(int playerCards[], int dealerIndex, String trumpCard, int callingRound, int currentPlayerIndex);
	
	public String getName();
}

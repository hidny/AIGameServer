package euchre;

import java.util.Scanner;

public class PlayerReplayer implements PlayerDecider {
	
	public static final String STOP = "STOP";
	Scanner in;
	
	
	private String name;
	
	public PlayerReplayer(Scanner input, String name) {
		this.in = input;
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
	public int getCard(int playerCards[], int currentCardsInFight[], EuchreCall euchreCall, int dealerIndex, int currentPlayerIndex) {
		String ret = "";
		if(in.hasNext()) {
			while(in.hasNext() && random.card.DeckFunctions.getCard(ret) < 0) {
				ret = in.next();
			}
		}
		
		if(ret == null || random.card.DeckFunctions.getCard(ret) < 0) {
			throw new RuntimeException("Game stopped suddenly in playing cards phase.");
		}
		
		return random.card.DeckFunctions.getCard(ret);
		
	}
	
	//0=mellow
	//1-13 is the number of tricks the players says he/she could make.
	public String getCall(int playerCards[], int dealerIndex, String trumpCard, int callingRound, int currentPlayerIndex) {
		String ret = "";
		
		ret = in.next();
		
		return ret;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }
}

package mellow;

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
	public int getCard(int playerCards[], int currentCardsInFight[], int indexCurrenPlayer) {
		String ret = "";
		if(in.hasNext()) {
			while(in.hasNext() && random.DeckFunctions.getCard(ret) < 0) {
				ret = in.next();
			}
		}
		
		if(ret == null || random.DeckFunctions.getCard(ret) < 0) {
			throw new RuntimeException("Game stopped suddenly in playing cards phase.");
		}
		
		return random.DeckFunctions.getCard(ret);
		
	}
	
	//0=mellow
	//1-13 is the number of tricks the players says he/she could make.
	public int getBid(int playerCards[], int prevBids[], int dealerIndex) {
		String ret = "";
		while(in.hasNext() && isInteger(ret) == false) {
			ret = in.next();
		}
		
		if(isInteger(ret)) {
			return Integer.parseInt(ret);
		} else {
			return -1;
		}
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

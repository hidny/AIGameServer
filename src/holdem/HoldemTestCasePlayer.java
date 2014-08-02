package holdem;

import java.util.Scanner;

public class HoldemTestCasePlayer implements HoldemPlayerDecider {
	private String name;
	
	private Scanner inTestRun;
	
	private HoldemServerMiddleMan middleMan;
	
	public HoldemTestCasePlayer(String name, Scanner inTestRun, HoldemServerMiddleMan middleMan) {
		this.name = name;
		this.inTestRun = inTestRun;
		this.middleMan = middleMan;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public void updatePlayer(HoldEmRoundInfoForPlayerDeciders currentRoundState) {
		String message ="";
		message += "*******************************************" + "\n";
		
		message += currentRoundState.getStringForGameState();
		message += "Round of betting: " + currentRoundState.getBettingRound() + "\n";
		
		middleMan.sendMessageToPlayer(name, message);
	}
	
	
	//for holdem ret <0 for a fold, see ret = 0 for a call and return >0 for a raise.
		//note that you can only raise as little as min raise.
	public int queryPlayer(HoldEmRoundInfoForPlayerDeciders currentRoundState) {
		
		String message ="";
		
		message += "*******************************************" + "\n";
		message += "Query for " + this.getName() + "!" + "\n";
		message += currentRoundState.getStringForGameState();
		message += "Round of betting: " + currentRoundState.getBettingRound() + "\n";
		message += this.getName() + ": What do you do? (do something)" + "\n";
		
		middleMan.sendMessageToPlayer(name, message);
		
		int query = Integer.parseInt(inTestRun.nextLine());
	
		middleMan.recordCommand(query + "");

		return query;
		
	}
	
	//Give the details of what happen for the last round.
	public void endRound(HoldEmRoundInfoForPlayerDeciders currentRoundState) {
		String ret = "";
		ret += "*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*" + "\n";
		ret += "End of round info for " + this.getName() + ":" + "\n";
		ret += currentRoundState.getStringForGameState();
		ret += "Do I know what I'm supposed to know?" + "\n";
		
		middleMan.sendMessageToPlayer(name, ret);
	}
}

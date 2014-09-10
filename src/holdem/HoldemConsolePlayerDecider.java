package holdem;


import java.io.PrintWriter;
import java.util.Scanner;

//TODO: extend this class for a player that's just sitting out.

public class HoldemConsolePlayerDecider implements HoldemPlayerDecider {
	private String name;
	
	private Scanner in = new Scanner(System.in);

	private PrintWriter commandRecord;
	
	public HoldemConsolePlayerDecider(String name) {
		 this(name, null);
	}
	public HoldemConsolePlayerDecider(String name, PrintWriter record) {
		if(name.split(" ").length > 1) {
			System.out.println(name);
			System.out.println("ERROR: do not put a space in the name!");
			System.exit(1);
		}
		this.name = name;
		
		this.commandRecord = record;
		
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public void updatePlayer(HoldEmRoundInfoForPlayerDeciders currentRoundState) {
		//No need for this.
	}
	
	
	//for holdem ret <0 for a fold, see ret = 0 for a call and return >0 for a raise.
		//note that you can only raise as little as min raise.
	public int queryPlayer(HoldEmRoundInfoForPlayerDeciders currentRoundState) {
		System.out.println("*******************************************");
		System.out.println("Query for " + name + "!");
		currentRoundState.getStringForGameState();
		System.out.println("Round of betting: " + currentRoundState.getBettingRound());
		System.out.println(name + ": What do you do?");
		
		int query = in.nextInt();
		if(commandRecord != null) {
			commandRecord.println("" + query);
		}
		return query;
	}
	
	
	
	//stub for more sophisticated AIs:
	//Give the details of what happen for the last round.
	public void endRound(HoldEmRoundInfoForPlayerDeciders currentRoundState) {
		System.out.println("*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*");
		System.out.println("End of round info for " + name + ":");
		currentRoundState.getStringForGameState();
		System.out.println("Do I know what I'm supposed to know?");
		
	}
}

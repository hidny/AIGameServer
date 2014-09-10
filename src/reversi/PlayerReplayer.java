package reversi;

import java.util.Scanner;

public class PlayerReplayer implements PlayerDecider {
	
	public static final String STOP = "STOP";
	Scanner in;
	private String name;
	
	public PlayerReplayer(Scanner input, String name) {
		this.in = input;
		this.name = name;
	}
	
	public void start(String names[]) {
		
	}
	
	public void updateBoard(Position pos, int currentPlayerColour) {
		
	}
	
	
	public String getMove(Position pos, int currentPlayerColour) {
		return in.next();
	}
	
	public String getName() {
		return this.name;
	}

	
}

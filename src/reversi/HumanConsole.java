package reversi;

//so far this class only has stubs.
//TODO: make this interact with the console
//nad print all relevant variables.
//My plan is to make the client-side do the remembering.

public class HumanConsole implements PlayerDecider {
	
	private String name;
	
	public HumanConsole(String name) {
		this.name = name;
	}
	
	public void start(String names[]) {
		
	}
	
	public void updateBoard(Position pos, int currentPlayerColour) {
		
	}
	
	public String getMove(Position pos, int currentPlayerColour) {
		//TODO
		return "";
	}
	
	public String getName() {
		return this.name;
	}
}

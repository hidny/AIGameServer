package reversi;

//so far this class only has stubs.
//TODO: make this interact with the console
//nad print all relevant variables.
//My plan is to make the client-side do the remembering.

public class ClientPlayerDecider implements PlayerDecider {
	
	private static String UNENTERED_MOVE = "NAM";
	
	private String currentMove = UNENTERED_MOVE;
	
	public synchronized void setMove(String move) {
		currentMove = move;
	}
	
	private String name;
	
	public ClientPlayerDecider(String name) {
		this.name = name;
	}
	
	public void start(String names[]) {
		
	}
	
	public void updateBoard(Position pos, int currentPlayerColour) {
		
	}
	
	//asks the current player to play a card.
	//if the player renages, he/she will get a warning!
	public String getMove(Position pos, int currentPlayerColour) {
		String ret;
		try {
			while(currentMove.equals(UNENTERED_MOVE) ) {
				//TODO: sleep
				Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		ret = currentMove.toLowerCase().trim();
		
		//TODO:
		//TESTING COMMENT this out to make the program auto-play.
		currentMove = UNENTERED_MOVE;
		
		return ret;
		
	}
	
	
	public String getName() {
		return this.name;
	}
}

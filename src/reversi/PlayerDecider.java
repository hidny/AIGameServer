package reversi;

public interface PlayerDecider {
	public void start(String names[]);
	
	
	public void updateBoard(Position pos, int currentPlayerColour);
	
	//get move:
	public String getMove(Position pos, int currentPlayerColour);
	
	public String getName();
}

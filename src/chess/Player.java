package chess;


public interface Player {
	
	//Note: the reason game is a param is to deal with draws
	
	public String getBestMove(String fenPos);
	
	//sets the depth:
	//applies if the player is a computer.
	public void setDepth(int depth);
	
	//gets the name of the player.
	//Even AIs should have name.
	public String getName();
}

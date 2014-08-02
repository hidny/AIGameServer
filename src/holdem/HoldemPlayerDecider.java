package holdem;

public interface HoldemPlayerDecider {
	
	public String getName();
	
	public String toString();
	
	public void updatePlayer(HoldEmRoundInfoForPlayerDeciders currentRoundState);
	
	public int queryPlayer(HoldEmRoundInfoForPlayerDeciders currentRoundState);
	
	public void endRound(HoldEmRoundInfoForPlayerDeciders currentRoundState);
}

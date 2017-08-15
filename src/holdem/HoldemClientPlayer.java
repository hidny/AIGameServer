package holdem;

import severalClientProject.ProfileInterface;

//TODO: extend this class for a player that's just sitting out.
public class HoldemClientPlayer implements HoldemPlayerDecider {
	
	private ProfileInterface player;
	
	private HoldemServerMiddleMan middleMan;
	
	public HoldemClientPlayer(ProfileInterface player, HoldemServerMiddleMan middleMan) {
		this.player = player;
		this.middleMan = middleMan;
	}
	
	public String getName() {
		return this.player.getClientName();
	}
	
	public String toString() {
		return this.player.getClientName();
	}
	
	
	public void updatePlayer(HoldEmRoundInfoForPlayerDeciders currentRoundState) {
		String message ="";
		message += "*******************************************" + "\n";
		//TODO: who's playng? (A: let the client have logic to figure it out!)
		
		message += currentRoundState.getStringForGameState();
		message += "Round of betting: " + currentRoundState.getBettingRound() + "\n";
		
		middleMan.sendMessageToPlayer(player, message);
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
		
		middleMan.sendMessageToPlayer(player, message);
		
		try {
			String query = HoldemServerMiddleMan.UNENTERED_MOVE;
			
			//spin lock... :(
			while(query.equals(HoldemServerMiddleMan.UNENTERED_MOVE) || isInteger(query) == false) {
				query = middleMan.getNextMove(player);
				middleMan.setMoveTaken(player);
				Thread.sleep(100);
				
			}
			
			middleMan.recordCommand(query);

			return Integer.parseInt(query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	
	
	//stub for more sophisticated AIs:
	//Give the details of what happen for the last round.
	public void endRound(HoldEmRoundInfoForPlayerDeciders currentRoundState) {
		String ret = "";
		ret += "*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*|*" + "\n";
		ret += "End of round info for " + this.getName() + ":" + "\n";
		ret += currentRoundState.getStringForGameState();
		ret += "Do I know what I'm supposed to know?" + "\n";
		
		middleMan.sendMessageToPlayer(player, ret);
		
	}
	
	
	 //determine if string is integer with exceptions.
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

package mellow;

public class BiddingPhase {
	
	public static void getBids(PlayerModel playerModel[], PlayerDecider player[], int dealerIndex, int redScore, int blueScore, MellowServerMiddleMan middleMan) {
		
		int prevBids[] = new int[4];
		
		int currentPreviousBid[];
		
		int currentBid;
		
		int indexPlayerAction;
		int numChances = 0;
		
		for(int i=0; i<4; i++) {
			currentPreviousBid = new int[i];
			for(int j=0; j<i; j++) {
				currentPreviousBid[j] = prevBids[i];
			}
			
			indexPlayerAction = (dealerIndex + 1+ i)%4;
			currentBid = -1;
			numChances = 0;

			middleMan.sendMessageToGroup(playerModel[indexPlayerAction].getPlayerName() +"'s turn to bid:");
			
			while ( true ) {
				
				middleMan.sendMessageToPlayer(playerModel[indexPlayerAction].getPlayerName(), "What's your bid?");
				
				currentBid = player[indexPlayerAction].getBid(playerModel[indexPlayerAction].getHand(), currentPreviousBid, dealerIndex);
				
				
				if(currentBid < 0 || currentBid > 13) {
					numChances++;
					
					if(middleMan.isReadingReplay()) {
						throw new RuntimeException("Game stopped suddenly in bidding phase" );
					} else {
						middleMan.sendMessageToPlayer(player[indexPlayerAction].getName(), "Bid something between 0 and 13 inclusively!");
						
					}
					//TODO
					
				} else if(numChances >= 3) {
					middleMan.sendMessageToPlayer(player[indexPlayerAction].getName(), "to player: You default bid to 1. (idiot)");
					
					currentBid = 1;
				} else {
					break;
				}
			}
			
			middleMan.sendMessageToPlayer(playerModel[indexPlayerAction].getPlayerName(), "Thank you.");
			
			//current Bid should be between 0 and 13 inclusively.
			if(currentBid == 0) {
				playerModel[indexPlayerAction].setMellow();
			} else {
				playerModel[indexPlayerAction].setNumBid(currentBid);
			}
			
			middleMan.sendMessageToGroup(playerModel[indexPlayerAction].getPlayerName() +": " + currentBid);
			middleMan.recordCommand(playerModel[indexPlayerAction].getPlayerName() +": " + currentBid + "\n");
			
		}
		
		
	}
	
}

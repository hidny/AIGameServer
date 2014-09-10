package holdem;



//TODO future: maybe I should separate the table from the tournament.

public class HoldEmTableFunctions {
	
	//post: elimates players that just lost.
	public static HoldEmPlayerLog[] eliminatePlayers(HoldEmPlayerLog playerLog[], int bigBlindPos, int smallBlindPos) {
		for(int i=0; i<playerLog.length; i++) {
			if(playerLog[i].stillInTournament() && playerLog[i].getNumChips() <= 0) {
				playerLog[i].takeOutOfTournament(HoldEmTableFunctions.getNumberOfPeopleStillInTourney(playerLog));
				if(i == bigBlindPos || i == smallBlindPos) {
					playerLog[i].setDeadToken();
				}
			}
		}
		
		return playerLog;
	}
	
	//post: returns the number of people on the table playing poker.
	public static int getNumberOfPeopleStillInTourney(HoldEmPlayerLog player[]) {
		 int numPlayersPlaying = 0;
		for(int i=0; i<player.length; i++) {
			if(player[i].stillInTournament()) {
				numPlayersPlaying++;
			}
		}
		
		return numPlayersPlaying;
	}
	
	//post: returns the number of people that aren't folded.
	public static int getNumberOfPeopleUnfolded(HoldEmPlayerLog player[]) {
		 int numPlayersPlaying = 0;
		for(int i=0; i<player.length; i++) {
			if(player[i].isUnfolded()) {
				numPlayersPlaying++;
			}
		}
		
		return numPlayersPlaying;
	}
	
	

	//post: returns true if everyone had the chance to act during the round.
	//		returns false otherwise.
	public static boolean updateHadChanceToEveryoneBet(int posLastToAct, boolean everyoneHadAChanceToBet, HoldEmPlayerLog currentPlayerCopy[], int indexOfBigBlind, int indexOfDealerPos, int currentRoundOfBetting) {
		
		int posNextToAct = HoldEmPlayerGetters.getIndexOfNextPlayerToAct(currentPlayerCopy, posLastToAct);
		
		if(posNextToAct == HoldEmPlayerGetters.NO_ONE_ELSE_COULD_ACT) {
			return true;
		} else {
			int indexOfLastPosOnTableToAct = getIndexOfLastPosOnTableToAct(indexOfBigBlind, indexOfDealerPos, currentRoundOfBetting);
			
			return updateEveryoneHadAChanceToBetBasedOnSeating(everyoneHadAChanceToBet, posLastToAct, posNextToAct, indexOfLastPosOnTableToAct, currentPlayerCopy.length);
		}
	}
	
	//post Returns the index of the last person to act.
	//Note that the last person to act isn't necessarily unfolded.
	private static int getIndexOfLastPosOnTableToAct(int indexOfBigBlind, int indexOfDealerPos, int currentRoundOfBetting) {
		if(currentRoundOfBetting == 0) {
			return indexOfBigBlind;
		} else {
			return indexOfDealerPos;
		}
	}
	
	//update the boolean EveryoneHadAChanceToBet based on where the last person bet is
	//and the position of big blind or dealer.
	//returns true if EveryoneHadAChanceToBet.
	//returns false otherwise.
	private static boolean updateEveryoneHadAChanceToBetBasedOnSeating(boolean everyoneHadAChanceToBet, int indexOfPersonThatJustActed, int indexOfCurrentPos, int indexOfLastPosOnTableToAct, int playerArrayLength) {
		if(everyoneHadAChanceToBet == true) {
			return true;
		} else {
			int currentDistanceFromLastToAct = (indexOfCurrentPos - indexOfLastPosOnTableToAct + playerArrayLength)%playerArrayLength;
			int prevDistanceFromLastToAct = (indexOfPersonThatJustActed - indexOfLastPosOnTableToAct + playerArrayLength)%playerArrayLength;
			
			//if the current player is the last to act, then let him/her act!
			if(currentDistanceFromLastToAct ==0) {
				return false;
			}
			//if the previous player that acted was the last player to act,
			//	everyone had their chance:
			else if(prevDistanceFromLastToAct == 0) {
				return true;
			}
			//if the previous player can before last pos to act
			//and the next player came after the last player to act:
			else if(currentDistanceFromLastToAct < prevDistanceFromLastToAct) {
				return true;
			
			} else {
				return false;
			}
		}
	}
	
	public static int TOURNEY_OVER = -9;
	//post: ret[0] = new dealer pos
	//		ret[1] = new small blind pos
	//		ret[2] = new big blind pos
	public static int[] preparePositionsForNextRound(HoldEmPlayerLog playerLog[],  int oldDealerPos, int oldSmallBlindPos, int oldBigBlindPos) {
		int ret[] = new int[3];
		
		//Take away dead token if there was a dead dealer.
		if(playerLog[oldDealerPos].isDeadToken()) {
			playerLog[oldDealerPos].unsetDeadToken();
		}
		
		//Move BB to the next person and move small blind and dealer accordingly and unfold all players.
		ret[0] = oldSmallBlindPos;
		ret[1] = oldBigBlindPos;
		
		//Get next player for big blind:
		ret[2] = HoldEmPlayerGetters.getIndexOfNextPlayerInTournament(playerLog, oldBigBlindPos);
		
		//If there's only 2 players, make sure the dealer is not big blind and dealer = small blind:
		if(HoldEmTableFunctions.getNumberOfPeopleStillInTourney(playerLog) == 2) {
			if(ret[0] == ret[2]) {
				ret[0] = HoldEmPlayerGetters.getIndexOfNextPlayerInTournament(playerLog, oldBigBlindPos);
				ret[1] = ret[0];
			}
		
		} else if(HoldEmTableFunctions.getNumberOfPeopleStillInTourney(playerLog) == 1) {
			ret[0] = TOURNEY_OVER;
			ret[1] = TOURNEY_OVER;
			ret[2] = TOURNEY_OVER;
		}
		
		return ret;
	}
}

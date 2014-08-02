package holdem;



public class HoldEmPlayerGetters {
	
	public static int NO_WINNERS = -8;
	//pre: there is only one person left in the tournament.
	//post: returns the index of the last player standing.
		//	returns -1 if no players are left standing and -2 if more than 1 person is left standing.
	public static int getFinalWinner(HoldEmPlayerLog player[]) {
		int ret = -1;
		for(int i=0; i<player.length; i++) {
			if(player[i].stillInTournament()) {
				if(ret == -1) {
					ret = i;
				} else {
					return NO_WINNERS;
				}
			}
		}
		return ret;
	}
		
	public static int ONLY_ONE_PLAYER = -2; 
	public static int getIndexOfNextPlayerInTournament(HoldEmPlayerLog player[], int indexOfCurrentAction) {
		int indexOfCurrentPlayer = indexOfCurrentAction;
		do {
			indexOfCurrentPlayer = (indexOfCurrentPlayer+1)%player.length;
			
		} while( player[indexOfCurrentPlayer].stillInTournament() ==false && indexOfCurrentPlayer != indexOfCurrentAction);
	
		//If no one is next to act:
		if(indexOfCurrentPlayer == indexOfCurrentAction) {
			indexOfCurrentPlayer = ONLY_ONE_PLAYER;
		}
		return indexOfCurrentPlayer;
	}
	
	//pre: the first player[index] is a live player
	//post: gets the next live player after the position of index.
		//If there are no live player after the position of index, it return -2(NO_MORE_PLAYERS).
	
	//NOTE: THIS FUNCTION DOES NOTCARES ABOUT WHETHER A PLAYER IS ALL IN!
	public static int getIndexOfNextUnFoldedPlayerOnTable(HoldEmPlayerLog player[], int indexOfCurrentAction) {
		int indexOfCurrentPlayer = indexOfCurrentAction;
		do {
			indexOfCurrentPlayer = (indexOfCurrentPlayer+1)%player.length;
			
		} while( (player[indexOfCurrentPlayer].isFolded() || player[indexOfCurrentPlayer].stillInTournament() ==false) && indexOfCurrentPlayer != indexOfCurrentAction);
	
		//If no one is next to act:
		if(indexOfCurrentPlayer == indexOfCurrentAction) {
			indexOfCurrentPlayer = ONLY_ONE_PLAYER;
		}
		return indexOfCurrentPlayer;
	}
	
	
	public static int NO_ONE_ELSE_COULD_ACT = -3;
	//post: returns the index of the next person that has to make a decision.
	//		warning: this function doesn't know if the round is over or not.
	//		returns -1 if no one is next to act.
		//NOTE: THIS FUNCTION CARES ABOUT WHETHER A PLAYER IS ALL IN!
	public static int getIndexOfNextPlayerToAct(HoldEmPlayerLog player[], int indexOfCurrentAction) {
		int nextToAct = indexOfCurrentAction;
		do {
			nextToAct = (nextToAct+1)%player.length;
			
		} while( (player[nextToAct].isAllin() == true || player[nextToAct].isFolded() || player[nextToAct].stillInTournament() ==false) && nextToAct != indexOfCurrentAction);
	
		//If no one is next to act:
		if(nextToAct == indexOfCurrentAction) {
			nextToAct = NO_ONE_ELSE_COULD_ACT;
		}
		return nextToAct;
	}
	
	
	//TODO: what about dead small blind or dead big blind!
	//post returns an array length 2 with the 1st cell beig small blind and the 2nd cell being big blind.
	public static int[] getSmallBlindBigBlingPos(HoldEmPlayerLog player[], int dealerPos) {
		int ret[] = new int[2];
		if(HoldEmTableFunctions.getNumberOfPeopleStillInTourney(player)  == 2) {
			ret[0] = dealerPos;
			ret[1] = HoldEmPlayerGetters.getIndexOfNextUnFoldedPlayerOnTable(player, dealerPos);
		} else {
			ret[0] = HoldEmPlayerGetters.getIndexOfSmallBlind(player, dealerPos);
			ret[1] = HoldEmPlayerGetters.getIndexOfNextUnFoldedPlayerOnTable(player, ret[0]);
		}
		return ret;
	}
	
	//post: gets the index of small blind.
	//		This getter is different from the others because it considers the dead token.
	private static int getIndexOfSmallBlind(HoldEmPlayerLog player[], int indexOfCurrentAction) {
		int indexOfCurrentPlayer = indexOfCurrentAction;
		do {
			indexOfCurrentPlayer = (indexOfCurrentPlayer+1)%player.length;
			
		} while( player[indexOfCurrentPlayer].stillInTournament() ==false && player[indexOfCurrentPlayer].isDeadToken() == false && indexOfCurrentPlayer != indexOfCurrentAction);
	
		//If no one is next to act:
		if(indexOfCurrentPlayer == indexOfCurrentAction) {
			indexOfCurrentPlayer = ONLY_ONE_PLAYER;
		}
		return indexOfCurrentPlayer;
	}

}

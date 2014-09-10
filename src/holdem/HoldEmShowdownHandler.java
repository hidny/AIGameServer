package holdem;

import deck.DeckFunctions;

public class HoldEmShowdownHandler {
	
	
	public static int[] handleShowdownNoMoney(HoldEmPlayerLog player[], int communityCards[], HoldemServerMiddleMan middleMan) {
		return handleShowdown(player, communityCards, middleMan);
	}
	
	public static int[] handleShowdown(HoldEmPlayerLog player[], int communityCards[], HoldemServerMiddleMan middleMan) {
		int cardRank[] = getHandValues(player, communityCards, middleMan);
		//SANITY test:
		showdownHandValueSanity(player,cardRank, middleMan);
		//End sanity test
		
		//Get Maximum hand Value
		int maxHandValue = getBiggestHandValues(player, cardRank);
		
		return getWinnersArray(player.length, cardRank, maxHandValue);
	}
	

	//post: Get everyone's hand value:
	public static int[] getHandValues(HoldEmPlayerLog player[], int communityCards[], HoldemServerMiddleMan middleMan) {
		
		int cardRank[] = new int[player.length];
		int best5Cards[];
		
		for(int i=0; i<player.length; i++) {
			if(player[i].stillInTournament() == false) {
				cardRank[i] = -2;
				middleMan.sendMessageToGroup(player[i] + " is out of tournament!");
			} else if(player[i].isFolded() ) {
				cardRank[i] = -1;
				middleMan.sendMessageToGroup(player[i] + " is folded!");
			} else {
				if(communityCardsGotRevealed(communityCards)) {
					//Compare hand strength here:
					best5Cards = HandComparer.getBest5CardComboOutOf7Cards(player[i].getCards(), communityCards);
					cardRank[i] = HandComparer.getHandStrength(best5Cards);
					
				} else {
					//Someone won by default:
					cardRank[i] = 1;
					middleMan.sendMessageToGroup(player[i] + " wins by default! (Don't reveal cards)");
				}
			}
		}
		
		return cardRank;
	}
	
	//post: returns true iff all the community cards got revealed.
	//if not all community cards got revealed, then someone won because everyone else folded.
	public static boolean communityCardsGotRevealed(int communityCards[]) {
		for(int i=0; i<communityCards.length; i++) {
			if(DeckFunctions.isValidCard(communityCards[i]) == false) {
				return false;
			}
		}
		
		return true;
	}
	
	//Get Maximum hand Value
	public static int getBiggestHandValues(HoldEmPlayerLog player[], int cardRank[]) {
		int biggestHandValue = -1;
		for(int i=0; i<player.length; i++) {
			if(cardRank[i] > biggestHandValue) {
				biggestHandValue = cardRank[i];
			}
		}
		return biggestHandValue;
	}
	
	//post:returns an array of the indexes of the winners
	//with the length of the array being the number of winners.
	public static int[] getWinnersArray(int numPlayers, int cardRank[], int maxHandValue) {
		
		//Initialize winners array.
		int winners[] = new int[numPlayers];
		for(int i = 0; i<numPlayers; i++) {
			winners[i] = -1;
		}
	
		//Fill winners array with the number of winners.
		int numWinners = 0;
		for(int i=0; i<numPlayers; i++) {
			if(cardRank[i] == maxHandValue) {
				winners[numWinners] = i;
				numWinners++;
			}
		}
		
		assert(numWinners > 0 && numWinners <= numPlayers);
		
		int ret[] = new int[numWinners];
		
		for(int i=0; i<numWinners; i++) {
			ret[i] = winners[i];
		}
		
		return ret;
	}
	
	//Sanity test:
	public static void showdownHandValueSanity(HoldEmPlayerLog player[], int cardRank[], HoldemServerMiddleMan middleMan) {
		for(int i=0; i<player.length; i++) {
			if(cardRank[i] >= 0 && player[i].isFolded()) {
				middleMan.sendMessageToGroup("ERROR: There's a folder with a hand...");
				System.exit(1);
			}
		}
	}
	
	
}

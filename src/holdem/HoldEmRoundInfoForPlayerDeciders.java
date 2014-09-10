package holdem;

//FUTURE: Maybe I should make this class extend some other class that just
// has information about the game in binary or int arrays.



//Later: make a function that takes this info and turns in into binary or a table of ints.
		// Maybe a general AI will figure out how to play based on it.

//IDEA: have an object encapsulates and (reinterprets?) all this data and call it: latestRoundInfo
//example:
//How much to call?
//How much to raise min?
//How many are in before?
//How many can still potentially go in?
//Is someone allowed to raise? Does he/she have the money + has there been to many raisers already?
//What is the maximum someone can raise without going all in?
//What are the actions that happened before my decision?
//Note: I cleverly forced it to make a hard copy of this info to send to AIs, don't risk this info being currupted by cheaters!


import java.util.ArrayList;

import severalClientProject.MiniServer;
import deck.*;

public class HoldEmRoundInfoForPlayerDeciders {
	

	//TODO: rem
	/*private HoldemServerMiddleMan middleMan = null;
	
	private MiniServer client;
	
	private String name = "";*/
	
	//Gets the amount of money each player has:
	private HoldEmPlayerLog playersInfoForPlayer[];
	
	//gets the amount each player has bet so far. Not that this also includes the antes.
	private int currentAmountBet[];
	
	
	private int bettingRound;
	
	private ArrayList<Integer> ActionsPreFlop = null;
	private ArrayList<Integer> ActionsPreTurn = null;
	private ArrayList<Integer> ActionsPreRiver = null;
	private ArrayList<Integer> ActionsPostRiver = null;
	
	private int communityCards[] = new int[5];
	
	private int numPlayersUnFolded;
	
	//positions of important players:
	private int indexOfDealerPos;
	private int indexOfSmallBlind;
	private int indexOfBigBlind;
	
	
	private int indexOfCurrentPos;
	
	private int currentpot = 0;
	
	//The min amount people could raise.
	private int minraise = 0;
	
	//the total greatest amount of chips put by one player during one hand so far.
	private int TotalCallingAmount = 0;
	
	private int bigBlind;
	private int smallBlind;
	private int ante;
	
	/*public void setMiddleMan(HoldemServerMiddleMan middleMan) {
		this.middleMan = middleMan;
	}
	
	public void setClient(MiniServer client) {
		this.client = client;
	}
	
	public void setName(String name) {
		this.name = name;
	}*/
	
	public HoldEmPlayerLog[] getPlayersInfoForPlayer() {
		return playersInfoForPlayer;
	}
	public void setPlayersInfoForPlayer(HoldEmPlayerLog[] currentPlayerCopy) {
		this.playersInfoForPlayer = currentPlayerCopy;
	}
	public int[] getCurrentAmountBet() {
		return currentAmountBet;
	}
	public void setCurrentAmountBet(int[] currentAmountBet) {
		this.currentAmountBet = currentAmountBet;
	}
	public int getBettingRound() {
		return bettingRound;
	}
	public void setBettingRound(int bettingRound) {
		this.bettingRound = bettingRound;
	}
	public ArrayList<Integer> getActionsPreFlop() {
		return ActionsPreFlop;
	}
	public void setActionsPreFlop(ArrayList<Integer> actionsPreFlop) {
		ActionsPreFlop = actionsPreFlop;
	}
	public ArrayList<Integer> getActionsPreTurn() {
		return ActionsPreTurn;
	}
	public void setActionsPreTurn(ArrayList<Integer> actionsPreTurn) {
		ActionsPreTurn = actionsPreTurn;
	}
	public ArrayList<Integer> getActionsPreRiver() {
		return ActionsPreRiver;
	}
	public void setActionsPreRiver(ArrayList<Integer> actionsPreRiver) {
		ActionsPreRiver = actionsPreRiver;
	}
	public ArrayList<Integer> getActionsPostRiver() {
		return ActionsPostRiver;
	}
	public void setActionsPostRiver(ArrayList<Integer> actionsPostRiver) {
		ActionsPostRiver = actionsPostRiver;
	}
	public int[] getCommunityCards() {
		return communityCards;
	}
	public void setCommunityCards(int[] communityCards) {
		this.communityCards = communityCards;
	}
	public int getNumPlayersUnFolded() {
		return numPlayersUnFolded;
	}
	public void setNumPlayersUnFolded(int numPlayersUnFolded) {
		this.numPlayersUnFolded = numPlayersUnFolded;
	}
	public int getIndexOfDealerPos() {
		return indexOfDealerPos;
	}
	public void setIndexOfDealerPos(int indexOfDealerPos) {
		this.indexOfDealerPos = indexOfDealerPos;
	}
	public int getIndexOfSmallBlind() {
		return indexOfSmallBlind;
	}
	public void setIndexOfSmallBlind(int indexOfSmallBlind) {
		this.indexOfSmallBlind = indexOfSmallBlind;
	}
	public int getIndexOfBigBlind() {
		return indexOfBigBlind;
	}
	public void setIndexOfBigBlind(int indexOfBigBlind) {
		this.indexOfBigBlind = indexOfBigBlind;
	}
	public int getIndexOfCurrentActionPos() {
		return indexOfCurrentPos;
	}
	public void setIndexOfCurrentPlayer(int indexOfCurrentPos) {
		this.indexOfCurrentPos = indexOfCurrentPos;
	}
	public int getCurrentpot() {
		return currentpot;
	}
	public void setCurrentpot(int currentpot) {
		this.currentpot = currentpot;
	}
	public int getMinraise() {
		return minraise;
	}
	public void setMinraise(int minraise) {
		this.minraise = minraise;
	}
	public int getTotalCallingAmount() {
		return TotalCallingAmount;
	}
	public void setTotalCallingAmount(int totalCallingAmount) {
		TotalCallingAmount = totalCallingAmount;
	}
	public int getBigBlind() {
		return bigBlind;
	}
	public void setBigBlind(int bigBlind) {
		this.bigBlind = bigBlind;
	}
	public int getSmallBlind() {
		return smallBlind;
	}
	public void setSmallBlind(int smallBlind) {
		this.smallBlind = smallBlind;
	}
	public int getAnte() {
		return ante;
	}
	public void setAnte(int ante) {
		this.ante = ante;
	}
	
	public String getStringForGameState() {
		String ret = "";
		ret += "Round info for players: " + "\n";
		
		
		//Gets the amount of money each player has:
		for(int i=0; i<playersInfoForPlayer.length; i++) {
			
			
			ret +=  playersInfoForPlayer[i] + ":" + "\n";
			ret += playersInfoForPlayer[i].getCardString() + "\n";
			ret += "Still in to win: " + playersInfoForPlayer[i].playerCouldStillWinPot() + "\n";
			ret += "Amount chips in pocket: " + playersInfoForPlayer[i].getNumChips() + "\n";
			ret += "Amount chips put in pot: " + playersInfoForPlayer[i].getAmountPutInPot() + "\n";
			
		}
		
		
		ret += "Betting Round: " + bettingRound + "\n";
		
		//TODO:
		ret += "Actions pre flop: " + "\n";
		ret += printActions(ActionsPreFlop);
		ret += "Actions after flop: " + "\n";
		ret += printActions(ActionsPreTurn);
		ret +=  "Actions after turn: " + "\n";
		ret += printActions(ActionsPreRiver);
		ret += "Actions after river: " + "\n";
		ret += printActions(ActionsPostRiver);
		//END todo
		
		
		ret += "Community cards:" + "\n";
		String cards = "";
		
		for(int i=0; i<5; i++) {
			if(DeckFunctions.isValidCard(communityCards[i])) {
				cards +=  DeckFunctions.getCardString(communityCards[i]) + " ";
			}
		}
		if(cards.equals("") == false) {
			cards = cards.substring(0, cards.length() - 1);
		}
		
		ret +=  cards + "\n";
		
		
		ret += "Num players contesting Pot: " + numPlayersUnFolded + "\n";
		
		//positions of important players:
		ret += "Index of Dealer Pos: " + indexOfDealerPos + "\n";
		ret += "Index of Small blind: " + indexOfSmallBlind + "\n";
		ret += "Index of big blind: " + indexOfBigBlind + "\n";
		
		
		ret += "Index Of Current Pos: " + indexOfCurrentPos + "\n";
		
		ret += "currentpot: " + currentpot + "\n";
		
		//The min amount people could raise.
		ret += "minraise: " + minraise + "\n";
		
		//the total greatest amount of chips put by one player during one hand so far.
		ret += "Total Calling Amount Still Needed: " + TotalCallingAmount + "\n";
		
		ret += "Big Blind: " + bigBlind + "\n";
		ret += "Small Blind: " + smallBlind + "\n";
		ret += "Ante: " + ante + "\n";
		
		return ret;
	}
	
	public void printLatestStuff() {
		//TODO:
		//don't flood the internet with unnecessary info!
	}
	
	public String printActions( ArrayList<Integer> Actions) {
		String ret = "";
		if(Actions != null) {
			for(int i=0; i<Actions.size(); i++) {
				ret += "" + Actions.get(i);
			}
		} else {
			ret += "No actions yet.";
		}
		
		return ret + "\n";
	}
}

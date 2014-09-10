package holdem;

import deck.*;


public class HoldEmRoundLogic {

	private HoldemServerMiddleMan middleMan;
	
	public static int UNKNOWN = -1;
	public static int PREFLOP = 0;
	public static int POSTFLOP = 1;
	public static int POSTTURN = 2;
	public static int POSTRIVER = 3;
	
//	These variables are there so later on, players can look back at any round
//	and see how everyone was positioned on the table:
/****************************Static variable that get recorded *****/
	private HoldEmPlayerLog initialPlayerCopy[];

	
	//positions of important players:
	private int indexOfDealerPos;
	private int indexOfSmallBlind;
	private int indexOfBigBlind;
		
	//Records all of the actions during a round:
	//I think I might put these numbers on the console or in a text file just for debugging.
	
	
	private int bigBlind;
	private int smallBlind;
	private int ante;
	private int minRaise;
	
/*******End of static Variables********************************************/
	
/****Variables that change throughout the round: *****/
	private HoldEmPlayerLog currentPlayerLog[];
	//private int currentAmountBet[];
	private int indexOfCurrentPos = UNKNOWN;
	
	//the total greatest amount of chips put by one player during one hand so far.
	//private int TotalCallingAmount = 0;
		
	//The min amount people could raise.
	//private int minraise;
	
	private boolean everyoneHadAChanceToBet = false;
	
	private int communityCards[] = new int[5];
	
	
/***End Variables that change throughout the round.********************/
	
	//CurrentRoundOfBetting:
	
	public int getCurrentRoundOfBetting() {
		if(DeckFunctions.isValidCard(communityCards[0]) == false) {
			return PREFLOP;
		} else if(DeckFunctions.isValidCard(communityCards[3]) == false) {
			return POSTFLOP;
		} else if(DeckFunctions.isValidCard(communityCards[4]) == false) {
			return POSTTURN;
		} else {
			return POSTRIVER;
		}
	}
	
/****End of dynamic variables ***********************/
	
	//Overload construtors just for convenience:
	//This construtor assumes that small blind is just half of big blind.
	public HoldEmRoundLogic(HoldEmPlayerLog player[], int dealerPos, int bigBlind, int ante, HoldemServerMiddleMan middleMan) {
		this(player, dealerPos, bigBlind, bigBlind/2, ante, middleMan);
	}
	
	public HoldEmRoundLogic(HoldEmPlayerLog player[], int dealerPos, int bigBlind, int smallBlind, int ante, HoldemServerMiddleMan middleMan) {
		this.middleMan = middleMan;
		
		this.bigBlind= bigBlind;
		this.smallBlind = smallBlind;
		this.ante = ante;
		this.minRaise = this.bigBlind;
		
		initializeArrays(player);
		
		indexOfDealerPos = dealerPos;
		
		int BigBlindSmallBlindpos[] = HoldEmPlayerGetters.getSmallBlindBigBlingPos(currentPlayerLog, indexOfDealerPos);
		indexOfSmallBlind = BigBlindSmallBlindpos[0];
		indexOfBigBlind = BigBlindSmallBlindpos[1];
		
		//minraise = bigBlind;
		
		//force players to post blinds and antes:
		dealWithBuyIns();
		
		initCommunityCards();
		
		//Finds the index of the first position: (on the left of the big blind)
		initializeFirstPlayerToAct();
		
	}
	
	private void initializeArrays(HoldEmPlayerLog player[]) {
		initialPlayerCopy = new HoldEmPlayerLog[player.length];
		currentPlayerLog = new HoldEmPlayerLog[player.length];
		
		for(int i=0; i<player.length; i++) {
			initialPlayerCopy[i] = player[i].makeHardCopy();
			currentPlayerLog[i] = player[i].makeHardCopy();
		}
		
		//initialize community cards to NULL:
		for(int i=0; i<5; i++) {
			communityCards[i] = HoldEmPlayerLog.NULL;
		}
	}
	
	public int getMaxAmountPutIntoPotByOnePlayer() {
		int max = 0;
		for(int i=0; i<currentPlayerLog.length; i++) {
			if(currentPlayerLog[i].playerCouldStillWinPot()) {
				if(currentPlayerLog[i].getAmountPutInPot() > max) {
					max = currentPlayerLog[i].getAmountPutInPot();
				}
			}
		}
		
		return max;
	}
	
	public int getAmountStillNeededForACall(int indexOfPlayer) {
		int amountAlreadyBet = currentPlayerLog[indexOfPlayer].getAmountPutInPot();
		
		int currentAmountNeededToCall = 0;
		
		int amountOtherPlayerBet;
		
		for(int i=0; i<currentPlayerLog.length; i++) {
			if(currentPlayerLog[i].playerCouldStillWinPot()) {
				amountOtherPlayerBet = currentPlayerLog[i].getAmountPutInPot();
				if(amountOtherPlayerBet - amountAlreadyBet > currentAmountNeededToCall) {
					currentAmountNeededToCall = amountOtherPlayerBet - amountAlreadyBet;
				}
			}
		}
		
		return currentAmountNeededToCall;
	}
	
	public boolean allButOnePlayerIsFolded() {
		int unFolded = 0;
		for(int i=0; i<currentPlayerLog.length; i++) {
			if(currentPlayerLog[i].isFolded() == false) {
				unFolded++;
			}
		}
		if(unFolded == 1) {
			return true;
		} else if(unFolded == 0){
			middleMan.sendMessageToGroup("ERROR: 0 players unfolded!");
			System.exit(1);
			return false;
		} else {
			return false;
		}
	}
	
	public int getNumPlayersNotAllinAndNotFolded() {
		int unFoldedAndNotAllin = 0;
		for(int i=0; i<currentPlayerLog.length; i++) {
			if(currentPlayerLog[i].isFolded() == false && currentPlayerLog[i].isAllin() == false) {
				unFoldedAndNotAllin++;
			}
		}
		return unFoldedAndNotAllin;
	}
	
	public void initializeFirstPlayerToAct() {
		if(getCurrentRoundOfBetting() == PREFLOP) {
			indexOfCurrentPos = HoldEmPlayerGetters.getIndexOfNextPlayerToAct(currentPlayerLog, indexOfBigBlind);
		} else {
			indexOfCurrentPos = HoldEmPlayerGetters.getIndexOfNextPlayerToAct(currentPlayerLog, indexOfDealerPos);
		}
	}
	
	public void initCommunityCards() {
		for(int i=0; i<5; i++) {
			communityCards[i] = UNKNOWN;
		}
	}
	
	//I just realized, I don't know the rules... Crap.
	//pre: if num < 0: it's a fold
	// if num = 0, it's a call
	// if num > 0, it's a raise.
	//to call allin: num = 0.
	//to raise allin, just num>= numChips left - amount to call.
	
	//The round log should do a sanity check to make sure that the player doesn't go under a min raise.
	//Too bad I don't know how min raise works lol.
	
	public void enterPlayerActionAndGotoNextPlayer(int num) {
		
		//Sanity check: makes sure no one has goofed up before the action function is called.
		assert(currentPlayerLog[indexOfCurrentPos].isAllin() == false);
		assert(currentPlayerLog[indexOfCurrentPos].isFolded() == false);
		assert(currentPlayerLog[indexOfCurrentPos].stillInTournament());
		assert(isRoundofBettingOver() == false);
		//End Sanity check.
		
		//fold:
		if(num < 0) {
			fold();
		//call:
		} else if(num == 0){
			if(curPlayerHasMoreThanEnoughToCall()) {
				callNormal();
			} else {
				callAllin();
			}
		} else {
		//raise:
			if(curPlayerHasMoreThanEnoughToMakeRaise(num)) {
				if(num < this.minRaise) {
					//TODO: actually send this warning:
					//System.out.println("PLAYERWarning: You didn't raise enough! You are now calling.");
					
					callNormal();
				} else {
					this.minRaise = Math.max(this.minRaise, num);
					raiseNormal(num);
				}
				
			//raise allin:
			} else if(curPlayerHasMoreThanEnoughToCall()) {
				this.minRaise = Math.max(this.minRaise, num);
				raiseAllin();
				
			//call allin:
			} else {
				//TODO: actually send this warning:
				//System.out.println("PLAYERWarning: num > 0 when the player is calling allin!");
				
				callAllin();
			} 
		}
		
		//check if everyone got their chance to bet:
		everyoneHadAChanceToBet = HoldEmTableFunctions.updateHadChanceToEveryoneBet(indexOfCurrentPos, everyoneHadAChanceToBet, currentPlayerLog, indexOfBigBlind, indexOfDealerPos, getCurrentRoundOfBetting());
		
		//get the next player to act:
		indexOfCurrentPos = HoldEmPlayerGetters.getIndexOfNextPlayerToAct(currentPlayerLog, indexOfCurrentPos);
		
	}
	
	private void fold() {
		currentPlayerLog[indexOfCurrentPos].fold();
	}
	
	private void callNormal() {
		assert(getAmountStillNeededForACall(indexOfCurrentPos) < currentPlayerLog[indexOfCurrentPos].getNumChips());
		currentPlayerLog[indexOfCurrentPos].transferChipsToPot(getAmountStillNeededForACall(indexOfCurrentPos));
	}
	
	private void callAllin() {
		assert(getAmountStillNeededForACall(indexOfCurrentPos) >= currentPlayerLog[indexOfCurrentPos].getNumChips());
		currentPlayerLog[indexOfCurrentPos].transferChipsToPot(currentPlayerLog[indexOfCurrentPos].getNumChips());
	}
	
	private void raiseNormal(int num) {
		assert(getAmountStillNeededForACall(indexOfCurrentPos) + num < currentPlayerLog[indexOfCurrentPos].getNumChips());
		currentPlayerLog[indexOfCurrentPos].transferChipsToPot(getAmountStillNeededForACall(indexOfCurrentPos) + num);
	}
	
	private void raiseAllin() {
		assert(getAmountStillNeededForACall(indexOfCurrentPos) < currentPlayerLog[indexOfCurrentPos].getNumChips());
		currentPlayerLog[indexOfCurrentPos].transferChipsToPot(currentPlayerLog[indexOfCurrentPos].getNumChips());
	}
	
	//post: returns true if the current player doesn't have to go allin to call
	//		returns false otherwise.
	private boolean curPlayerHasMoreThanEnoughToCall() {
		if(getAmountStillNeededForACall(indexOfCurrentPos) < currentPlayerLog[indexOfCurrentPos].getNumChips()) {
			return true;
		} else {
			return false;
		}
	
	}
	
	//post: returns true if a raise of num isn't impossible and doesn't put the current player allin.
	//		returns false otherwise.
	private boolean curPlayerHasMoreThanEnoughToMakeRaise(int num) {
		
		int amountToCall = getAmountStillNeededForACall(indexOfCurrentPos);
		
		if(num + amountToCall < currentPlayerLog[indexOfCurrentPos].getNumChips()) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public void getFlopFromDeck(Deck deck) {
		//burn
		deck.getNextCard();
		//get 3 cards:
		int card1 = deck.getNextCard();
		int card2 = deck.getNextCard();
		int card3 = deck.getNextCard();
		flop(card1, card2, card3);
	}
	public void getTurnFromDeck(Deck deck) {
		deck.getNextCard();
		//get 1 card:
		int card4 = deck.getNextCard();
		turn(card4);
	}
	public void getRiverFromDeck(Deck deck) {
		deck.getNextCard();
		//get 1 card:
		int card5 = deck.getNextCard();
		river(card5);
	}
	
	public void flop(int card1, int card2, int card3) {
		communityCards[0] = card1;
		communityCards[1] = card2;
		communityCards[2] = card3;
		
		indexOfCurrentPos = HoldEmPlayerGetters.getIndexOfNextPlayerToAct(currentPlayerLog, this.indexOfDealerPos);
		
	}
	
	public void turn(int card4) {
		communityCards[3] = card4;
		
		indexOfCurrentPos = HoldEmPlayerGetters.getIndexOfNextPlayerToAct(currentPlayerLog, this.indexOfDealerPos);
	}
	
	public void river(int card5) {
		communityCards[4] = card5;
		
		indexOfCurrentPos = HoldEmPlayerGetters.getIndexOfNextPlayerToAct(currentPlayerLog, this.indexOfDealerPos);
	}

	public int getCurrentPlayerIndex() {
		return this.indexOfCurrentPos;
	}
	
	//post: returns a HARD COPY of the player array. (outside modules aren't be able to touch it!)
	public HoldEmPlayerLog[] getCurrentPlayerArray() {
		HoldEmPlayerLog playerCopy[] = new HoldEmPlayerLog[currentPlayerLog.length];
		for(int i=0; i<currentPlayerLog.length; i++) {
			playerCopy[i] = currentPlayerLog[i].makeHardCopy();
		}
		return playerCopy;
	}
	
	//pre: the player on Big Blind Pos is not dead!
	//post: put in Small Blind, Big Blind and Ante
	private void dealWithBuyIns() {
		//DEBUG
		assert(currentPlayerLog[indexOfBigBlind].stillInTournament());
		assert(indexOfBigBlind != indexOfSmallBlind);
	
		for(int i =0; i< currentPlayerLog.length; i++) {
			if(currentPlayerLog[i].getAmountPutInPot() != 0) {
				middleMan.sendMessageToGroup("ERROR: the current Amount Bet hasn't been reset to 0");
			}
		}
		//END DEBUG
		
		
		if(ante > 0) {
			//Take chips from everyone to serve as ante:
			for(int i=0; i<currentPlayerLog.length; i++) {
				currentPlayerLog[i].addAmountPutInPot(currentPlayerLog[i].takeChips(ante));
			}
		}
		
		//Put chips in pot for SB with the logic of Dead token. :)TODO: test dead token logic
		if(currentPlayerLog[indexOfSmallBlind].isDeadToken() == false) {
			currentPlayerLog[indexOfSmallBlind].addAmountPutInPot(currentPlayerLog[indexOfSmallBlind].takeChips(smallBlind));
		}
		
		//Put chips in pot for BB:
		currentPlayerLog[indexOfBigBlind].addAmountPutInPot(currentPlayerLog[indexOfBigBlind].takeChips(bigBlind));
	}
	
	
	
	public boolean isRoundofBettingOver() {
		if(allButOnePlayerIsFolded()) {
			return true;
		}
		
		int maxAmountSomeoneBet = getMaxAmountPutIntoPotByOnePlayer();
		
		//check if we already know that there's no one left to act:
		if(indexOfCurrentPos == HoldEmPlayerGetters.NO_ONE_ELSE_COULD_ACT) {
			return true;
		}
		
		//Check if everyone is allin or folded:
		if(getNumPlayersNotAllinAndNotFolded() == 0) {
			return true;
		}
		//If everyone is allin or folded except 1 person and that person bet enough to be in the pot,
		//the round is over.
		if(getNumPlayersNotAllinAndNotFolded() == 1) {
			for(int i=0; i<currentPlayerLog.length; i++) {
				if(maxAmountSomeoneBet == currentPlayerLog[i].getAmountPutInPot() && currentPlayerLog[i].isAllin() == false) {
					return true;
				}
			}
		}
		
		//check if everyone bet and everyone agrees on an amount to bet
		if(everyoneHadAChanceToBet) {
			for(int i=0; i<currentPlayerLog.length; i++) {
				if(currentPlayerLog[i].playerCouldStillWinPot() == false) {
					continue;
				}
				if(currentPlayerLog[i].isAllin() == false && currentPlayerLog[i].getAmountPutInPot() < maxAmountSomeoneBet) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	
	public HoldEmRoundInfoForPlayerDeciders getRoundInfoForPlayer(int indexOfPlayer) {
		HoldEmRoundInfoForPlayerDeciders ret = new HoldEmRoundInfoForPlayerDeciders();
		
		//ret.setMiddleMan(middleMan);
		//if(middleMan.getClientPlayers() != null) {
		//	ret.setClient(middleMan.getClientPlayers()[indexOfPlayer]);
		//}
		
		//ret.setName(this.currentPlayerLog[indexOfPlayer].getName());
		
		ret.setIndexOfBigBlind(indexOfBigBlind);
		ret.setIndexOfDealerPos(indexOfDealerPos);
		ret.setIndexOfSmallBlind(indexOfSmallBlind);
		
		ret.setCommunityCards(communityCards);
		ret.setNumPlayersUnFolded(HoldEmTableFunctions.getNumberOfPeopleUnfolded(currentPlayerLog));
		ret.setIndexOfCurrentPlayer(indexOfPlayer);
		ret.setPlayersInfoForPlayer(getPlayerLogForAPlayerDecider(indexOfPlayer));
		ret.setCurrentpot(getCurrentPot());
		ret.setAnte(ante);
		ret.setSmallBlind(smallBlind);
		ret.setBigBlind(bigBlind);
		ret.setBettingRound(this.getCurrentRoundOfBetting());
		
		return ret;
	}
	
	//post: makes a hard copy of the player log of each player and hides the cards for the other players.
		//This will make it so the AIs don't know what other people's cards are and they will be 
		//unable to change the facts about the game.
	public HoldEmPlayerLog[] getPlayerLogForAPlayerDecider(int positionOfAI) {
		
		HoldEmPlayerLog playerLogForPlayer[] = new HoldEmPlayerLog[currentPlayerLog.length];
		
		for(int i=0; i<currentPlayerLog.length; i++) {
			if(positionOfAI == i || currentPlayerLog[i].cardsAreRevealed()) {
				playerLogForPlayer[i] = currentPlayerLog[i].makeHardCopy();
			} else {
				playerLogForPlayer[i] = currentPlayerLog[i].makeHardCopyHideCards();
			}
		}
		
		return playerLogForPlayer;
	}
		

	public String toString() {
		String ret = "";
		/*private HoldEmPlayerLog initialPlayerCopy[];
		private HoldEmPlayerLog currentPlayerCopy[];
		
		//TODO: initialize this to ante for everyone except BB, SB
		private int currentAmountBet[];
		
		//Records all of the actions during a round:
		//I think I might put these numbers on the console or in a text file just for debugging.
		ArrayList<Integer> ActionsPreFlop;
		ArrayList<Integer> ActionsPreTurn;
		ArrayList<Integer> ActionsPreRiver;
		ArrayList<Integer> ActionsPostRiver;
		
		
		private int communityCards[] = new int[5];
		*/
		//I'm not sure if I will need this variable... we'll see.
		
		ret += "\n";
		ret += "numPlayersUnFolded:" + HoldEmTableFunctions.getNumberOfPeopleUnfolded(currentPlayerLog);
		ret += "\n";
		//positions of important players:
		ret += "indexOfDealerPos:" + indexOfDealerPos;
		ret += "\n";
		ret += "indexOfSmallBlind:" + indexOfSmallBlind;
		ret += "\n";
		ret += "indexOfBigBlind:" + indexOfBigBlind;
		ret += "\n";
		
		
		ret += "indexOfCurrentPos:" + indexOfCurrentPos;
		ret += "\n";
		
		ret += "currentPot:" + getCurrentPot();
		ret += "\n";
		
		//The min amount people could raise.
		//ret += "minraise:" + minraise;
		ret += "\n";
		
		//the total greatest amount of chips put by one player during one hand so far.
		ret += "TotalCallingAmount:" + getMaxAmountPutIntoPotByOnePlayer();
		ret += "\n";
		
		ret += "bigBlind:" + bigBlind;
		ret += "\n";
		ret += "smallBlind:" + smallBlind;
		ret += "\n";
		ret += "ante:" + ante;
		ret += "\n";
		
		return ret;
	}
	
	//post: returns the current pot by recounting all the bets on the fly.
	public int getCurrentPot() {
		int pot = 0;
		for(int i=0; i<currentPlayerLog.length; i++) {
			pot += currentPlayerLog[i].getAmountPutInPot();
		}
		
		return pot;
	}
	
	public int[] getCommunityCards() {
		return communityCards;
	}
	
	public String getCommunityCardsString() {
		String ret = "";
		for(int i=0; i<5; i++) {
			ret += DeckFunctions.getCardString(communityCards[i]) + " ";
		}
		return ret;
	}
	
	//post: start a round of betting.
	public void startRoundOfBetting(HoldemPlayerDecider playerDecider[]) {
		
		//LatestRoundInfo roundInfo;
		int action = 0;
		
		HoldEmRoundInfoForPlayerDeciders roundInfo;
		
		initializeFirstPlayerToAct();
		
		this.everyoneHadAChanceToBet = false;
		
		while(isRoundofBettingOver() == false) {
			
			//Get information about what the next players gets to know:
			roundInfo = new HoldEmRoundInfoForPlayerDeciders();
			
			//update other players:
			for(int i =0; i<playerDecider.length; i++) {
				roundInfo = getRoundInfoForPlayer(i);
				if(i!= getCurrentPlayerIndex()) {
					playerDecider[i].updatePlayer(roundInfo);
				}
			}
			
			//Ask the player who's turn it is what to do:
			roundInfo = getRoundInfoForPlayer(getCurrentPlayerIndex());
			action = playerDecider[getCurrentPlayerIndex()].queryPlayer(roundInfo);
			
			//Update round log with the player action.
			enterPlayerActionAndGotoNextPlayer(action);
			
		//end loop.
		}
	}
	
	public HoldEmPlayerLog[] playOutRound(HoldemPlayerDecider player[], Deck deck) {
		//Idea: do these steps later:
		this.middleMan.sendMessageToGroup("Time to start first round of betting.");
		startRoundOfBetting(player);
		
		if(allButOnePlayerIsFolded() == false) {
			getFlopFromDeck(deck);
			this.middleMan.sendMessageToGroup("Time to start second round of betting.");
			startRoundOfBetting(player);
		}
		
		if(allButOnePlayerIsFolded() == false) {
			this.middleMan.sendMessageToGroup("Time to start third round of betting.");
			getTurnFromDeck(deck);
			startRoundOfBetting(player);
		}
		
		if(allButOnePlayerIsFolded() == false) {
			this.middleMan.sendMessageToGroup("Time to start fourth round of betting.");
			getRiverFromDeck(deck);
			startRoundOfBetting(player);
		}
		
		printAllCardsAtEndForDebug();
		handleShowdown();

		giveDeciderEndOfRoundInfo(player);
		
		this.middleMan.sendMessageToGroup("Reached end of round!");
		
		return currentPlayerLog;
	}
	
	
	
	public void giveDeciderEndOfRoundInfo(HoldemPlayerDecider player[]) {
		determineRevealedCardAtEndOfRound();
		HoldEmRoundInfoForPlayerDeciders roundInfo;
		
		for(int i=0; i<player.length; i++) {
			roundInfo = getRoundInfoForPlayer(i);
			player[i].endRound(roundInfo);
		}
	}
	
	public void determineRevealedCardAtEndOfRound() {
		int numContestingPot = 0;
		for(int i=0; i<currentPlayerLog.length; i++) {
			if(currentPlayerLog[i].playerCouldStillWinPot()) {
				numContestingPot++;
			}
		}
		
		if(numContestingPot > 1) {
			for(int i=0; i<currentPlayerLog.length; i++) {
				if(currentPlayerLog[i].playerCouldStillWinPot()) {
					currentPlayerLog[i].revealCardsToEveryone();
				}
			}
		}
	}
	
	
	public void handleShowdown() {
		int smallestBet = getSmallestBet();
		int currentPotSize;
		
		//loop until all money in pot has been decided:
		 do {
			currentPotSize = getCurrentShowdownPotSize(smallestBet);
			this.middleMan.sendMessageToGroup("Showdown for pot of " + currentPotSize + "!");
			int winners[] = HoldEmShowdownHandler.handleShowdown(getCurrentPlayerArray(), communityCards, this.middleMan);
			divideWinnings(winners, currentPotSize);
			
			//keep doing this until all pots have been accounted for:
			smallestBet = getSmallestBet();
		} while(smallestBet > 0);
		
		 returnChipsStillInPot();
	}
	
	//Returns the chips of the biggest better that couldn't get called.
	//if this functions finds that there are 2 betters with chips they want back, 
	//something went horribly wrong. FAIL IT!
	public void returnChipsStillInPot() {
		int numStillHaveChipsInPot = 0;
		 for(int i=0; i<currentPlayerLog.length; i++) {
			 if(currentPlayerLog[i].getAmountPutInPot() > 0) {
				 numStillHaveChipsInPot++;
			 }
			currentPlayerLog[i].getChipsBackFromPot();
		}
		 
		if(numStillHaveChipsInPot > 1) {
			this.middleMan.sendMessageToGroup("WTF! The showdown didn't resolve everything! There's still at least 2 people with chips in the pot!");
			System.exit(1);
		}
	}
	
	public int getCurrentShowdownPotSize(int smallestBet) {
		int currentPotSize = 0;
		for(int i=0; i<currentPlayerLog.length; i++) {
			if(currentPlayerLog[i].getAmountPutInPot() >= smallestBet) {
				this.middleMan.sendMessageToGroup("Taking " + smallestBet + " from " + currentPlayerLog[i]);
				currentPotSize += smallestBet;
				
				//SANITY TEST
				if(currentPlayerLog[i].getAmountPutInPot() > 0 && currentPlayerLog[i].getAmountPutInPot() < smallestBet) {
					this.middleMan.sendMessageToGroup("ERROR: in getCurrentShowdownPotSize(smallestBet) player somehow had a bet smaller than current smallest bet! How?");
					System.exit(1);
				}
				//END SANITY TEST
				
				if(currentPlayerLog[i].getAmountPutInPot() > 0) {
					currentPlayerLog[i].takeAmountPutInPot(smallestBet);
				} else {
					currentPlayerLog[i].fold();
				}
			}
		}
		
		return currentPotSize;
	}
	
	public void divideWinnings(int winners[], int currentPotSize) {
		int firstRemainderIndex;
		int remainder;
		
		
		if(winners.length == currentPlayerLog.length) {
			for(int i=0; i<winners.length; i++) {
				this.middleMan.sendMessageToGroup("Everyone won the round!");
			}
		} else if (winners.length > 0) {
			for(int i=0; i<winners.length; i++) {
				this.middleMan.sendMessageToGroup(currentPlayerLog[winners[i]] + " won the round!");
			}
		} else {
			this.middleMan.sendMessageToGroup("No Winners. WTF!");
			System.exit(1);
		}
		
		//Divide the winnings up equally and give to winners:
		for(int i=0; i<winners.length; i++) {
			currentPlayerLog[winners[i]].giveChip(currentPotSize/winners.length);
			this.middleMan.sendMessageToGroup("Giving " +  currentPotSize/winners.length + " to " + currentPlayerLog[winners[i]]);
		}
		
		remainder = currentPotSize%winners.length;
		
		//Give the remainder to people in bad positions first...
		//as long as it's deterministic, I dont care.
		firstRemainderIndex = (indexOfDealerPos+1) % winners.length;
		for(int i=0; i<remainder; i++) {
			currentPlayerLog[winners[(i + firstRemainderIndex) % winners.length]].giveChip(1);
			this.middleMan.sendMessageToGroup("Giving 1 to " + currentPlayerLog[winners[i]]);
		}
	}
	
	
	public void printAllCardsAtEndForDebug() {
		
		for(int i=0; i<currentPlayerLog.length; i++) {
			this.middleMan.sendMessageToGroup(currentPlayerLog[i] + ":");
			this.middleMan.sendMessageToGroup(currentPlayerLog[i].getCardString());
		}
		
		this.middleMan.sendMessageToGroup("Community cards:");
		this.middleMan.sendMessageToGroup(getCommunityCardsString());
		
	}
	
	
	public int getSmallestBet() {
		//-1 means no bet found yet:
		int smallest = -1;
		for(int i=0; i<currentPlayerLog.length; i++) {
			if(currentPlayerLog[i].stillInTournament()) {
				if (currentPlayerLog[i].getAmountPutInPot() > 0 && (currentPlayerLog[i].getAmountPutInPot() < smallest || smallest == -1)) {
					smallest = currentPlayerLog[i].getAmountPutInPot();
				}
			}
		}
		
		if(smallest <=0) {
			smallest = 0;
		}
		
		return smallest;
	}
	
}

package euchre;

import random.card.DeckFunctions;


public class Position {
	//bid goes from 0 to 13.
	//0 = Mellow
	//You can't say 0.
	
	public static String GAME_NAME = "euchre";
	
	public static final int NUM_PLAYERS = 4;
	public static final int NUM_CARDS_PER_HAND = 5;
	
	private int redScore;
	private int blueScore;
	private random.card.Deck currentDeck;
	
	public static int GOAL_SCORE = 10;
	
	public static int UNKNOWN = -3;
	
	private String trumpCard = ""; 
	
	
	public static void main(String args[]) {
		System.out.println("Let's play euchre!");
		
		EuchreVariation variation = new EuchreVariation("ontarian");
		
		EuchreServerMiddleMan middleMan = new EuchreServerMiddleMan();
		
		
		PlayerDecider red[] = new PlayerDecider[2];
		PlayerDecider blue[] = new PlayerDecider[2];
		red[0] = new HumanConsole("Richard");
		red[1] = new HumanConsole("Michael");
		
		blue[0] = new HumanConsole("Doris");
		blue[1] = new HumanConsole("Phil");
		
		
		startEuchre(middleMan, variation, red, blue);
	}
	
	public static void startEuchre(EuchreServerMiddleMan middleMan, EuchreVariation variation, PlayerDecider red[], PlayerDecider blue[]) {
		startEuchre(middleMan, variation, red, blue, UNKNOWN, null);
	}
	
	//When you need it.
	public static void startEuchre(EuchreServerMiddleMan middleMan, EuchreVariation variation, PlayerDecider red[], PlayerDecider blue[], int dealerIndex, random.card.Deck givenDeck) {
		
		Position pos = new Position();
		
		middleMan.sendMessageToGroup("Starting Euchre! " + red[0].getName() + " & " + red[1].getName() + " vs " + blue[0].getName() + " & " + blue[1].getName());
		middleMan.recordCommand(red[0].getName() + " & " + red[1].getName() + " vs " + blue[0].getName() + " & " + blue[1].getName()  + "\n");
		
		middleMan.sendMessageToGroup(variation.getVariation() );
		middleMan.recordCommand(variation.getVariation() +"\n");
		
		
		int result = pos.playGame(red, blue, middleMan, variation, dealerIndex, givenDeck);
		
		if ( result == RED_WINS) {
			middleMan.sendMessageToGroup(red[0].getName() + " & " + red[1].getName() + " win!");
		} else if(result == BLUE_WINS ){
			middleMan.sendMessageToGroup(blue[0].getName() + " & " + blue[1].getName() + " win!");
		} else {
			middleMan.sendMessageToGroup("It's a tie. But that\'s impossible!");
			
		}
	}
	
	public int playGame(PlayerDecider redDecider[], PlayerDecider blueDecider[],  EuchreServerMiddleMan middleMan, EuchreVariation variation) {
		return playGame(redDecider, blueDecider, middleMan, variation, UNKNOWN, null);
	}
	
	//returns false otherwise.
	public int playGame(PlayerDecider redDecider[], PlayerDecider blueDecider[],  EuchreServerMiddleMan middleMan, EuchreVariation variation, int dealerIndex, random.card.Deck givenDeck) {
		PlayerDecider playerDeciders[] = new PlayerDecider[4];
		playerDeciders[0] = redDecider[0];
		playerDeciders[1] = blueDecider[0];
		playerDeciders[2] = redDecider[1];
		playerDeciders[3] = blueDecider[1];
		
		PlayerModel playerModel[] = new PlayerModel[4];
		playerModel[0] = new PlayerModel(playerDeciders[0].getName(), middleMan);
		playerModel[1] = new PlayerModel(playerDeciders[1].getName(), middleMan);
		playerModel[2] = new PlayerModel(playerDeciders[2].getName(), middleMan);
		playerModel[3] = new PlayerModel(playerDeciders[3].getName(), middleMan);
		
		if(dealerIndex == UNKNOWN) {
			dealerIndex = getIndexFirstDealer();
		}
		
		
		middleMan.sendMessageToGroup("First dealer is " + playerDeciders[dealerIndex].getName());
		
		middleMan.recordCommand("First dealer is " + playerDeciders[dealerIndex].getName() + "\n");
		
		//If no deck is given, create a new one:
		if(givenDeck == null) {
			currentDeck = new random.card.RandomEuchreDeck(middleMan.getCommandFile());
		} else {
			currentDeck = givenDeck;
		}
		
		int scoresObtained[];
		
		boolean firstHand  = true;
		
		EuchreCall euchreCall;
		String hand;
		
		while(isGameOver() == false) {
			//update dealer on all rounds except for the first one:
			if(firstHand == false) {
				middleMan.sendMessageToGroup("Current dealer is " + playerDeciders[dealerIndex].getName());
			} else {
				firstHand = false;
			}
			
			this.dealEmUp(playerModel, dealerIndex);
			
			hand = "";
			for(int i=0; i<NUM_PLAYERS; i++) {
				for(int j=0; j<NUM_CARDS_PER_HAND; j++) {
					hand += DeckFunctions.getCardString(playerModel[i].getHand()[j]) + " ";
				}
				middleMan.sendMessageToPlayer(playerModel[i].getPlayerName(),  hand);
				hand = "";
			}
			this.trumpCard = DeckFunctions.getCardString(currentDeck.getNextCard());
			
			middleMan.sendMessageToGroup("Trump card is " + this.trumpCard);
			
			middleMan.sendMessageToGroup("Starting 1st calling round:");
			euchreCall = CallingPhase.getCall(playerModel, playerDeciders, dealerIndex, trumpCard, 1, this.redScore, this.blueScore, middleMan, variation);
			
			if(euchreCall.isPassing()) {
				//2nd calling round:
				middleMan.sendMessageToGroup("Starting 2nd calling round:");
				euchreCall = CallingPhase.getCall(playerModel, playerDeciders, dealerIndex, trumpCard, 2, this.redScore, this.blueScore, middleMan, variation);
			}
			
			if(euchreCall.isPassing()) {
				//Misdeal!
				middleMan.sendMessageToGroup("Misdeal!");
				
			} else {
			
				playRound(dealerIndex, playerModel, playerDeciders, euchreCall, middleMan);
				
				scoresObtained = updateScore(playerModel, euchreCall);
				
				redScore += scoresObtained[0];
				blueScore+= scoresObtained[1];
				
				printUpdate(redScore, blueScore, scoresObtained, middleMan);
				
			}
			
			dealerIndex++;
			dealerIndex = dealerIndex%4;
			
			clearPlayerRoundArgs(playerModel);
		}
		
		return getResult();
	}
	
	public static int getIndexFirstDealer() {
		int index = (int)(4 * Math.random());
		return index;
	}
	
	public void dealEmUp(PlayerModel playerModel[], int dealerIndex) {
		currentDeck.shuffle();
		int temp;
		
		for(int i=1; i<=NUM_PLAYERS * NUM_CARDS_PER_HAND; i++) {
			temp = currentDeck.getNextCard();
			playerModel[(dealerIndex + i)%4].give(temp);
		}
		
	}
	

	public int getRoundScoreRed() {
		return 0;
	}
	
	public int getRoundScoreBlue() {
		return 0;
	}
	
	public boolean isGameOver() {
		if(this.redScore >= GOAL_SCORE || this.blueScore >= GOAL_SCORE) {
			return true;
		}
		
		return false;
		
	}
	
	public static int RED_WINS = -22;
	public static int BLUE_WINS = -42;
	public static int TIE = -67;
	
	public int getResult() {
		if(redScore > blueScore) {
			return RED_WINS;
		} else if(redScore < blueScore) {
			return BLUE_WINS;
		} else {
			return TIE;
		}
	}
	
	public void clearPlayerRoundArgs(PlayerModel playerModel[]) {
		for(int i=0; i<4; i++) {
			playerModel[i].clearRoundVars();
		}
		this.trumpCard = ""; 
	}
	
	
	public void playRound(int dealerIndex, PlayerModel playerModel[], PlayerDecider playerDecider[], EuchreCall euchreCall, EuchreServerMiddleMan middleMan) {
		int CARDS_PER_HAND = NUM_CARDS_PER_HAND;
		
		int prevPlay[] = new int[4];
		
		int initialActionIndex = (dealerIndex + 1) % 4;
		int actionIndex;
		
		int indexWinner;
		
		int indexPlayerNotPlaying = -1;
		if(euchreCall.isAlone()) {
			indexPlayerNotPlaying = (euchreCall.getCallIndex() + 2) % 4;
		}
		
		//If a player isn't playing the round, she can't start the round off:
		if(initialActionIndex == indexPlayerNotPlaying) {
			initialActionIndex = (initialActionIndex + 1 ) % 4;
		}
			
		
		middleMan.sendMessageToGroup("Dealer Index: " + dealerIndex);
		middleMan.sendMessageToGroup("Dealer: " + playerModel[dealerIndex].getPlayerName());
		
		String hand = "";
		for(int i=0; i<CARDS_PER_HAND; i++) {
			
			//TESTING:
			//System.out.println("Current hands:");
			for(int j=0; j<4; j++) {
				prevPlay[j] = PlayerModel.NOT_A_CARD;
				
				//Skip player not playing for the round.
				if(j == indexPlayerNotPlaying) {
					continue;
				}
				int currentHandToPrint[] = playerModel[j].getHand();
				
				//System.out.print(playerModel[j].getPlayerName() + ": ");
				for(int k=0; k<currentHandToPrint.length; k++) {
					//System.out.print(deck.DeckFunctions.getCardString(currentHandToPrint[k]) + " ");
					hand += random.card.DeckFunctions.getCardString(currentHandToPrint[k]) + " ";
				}

				middleMan.sendMessageToPlayer(playerModel[j].getPlayerName(), hand);
				hand = "";
				//System.out.println();
			}
			//END TESTING
			
			middleMan.sendMessageToGroup("Initial Action index: " + initialActionIndex);
			for(int j=0; j<4; j++) {
				prevPlay[j] = PlayerModel.NOT_A_CARD;
			}
			
			for(int j=0; j<4; j++) {
				actionIndex = (initialActionIndex + j)%4;
				
				//Skip player not playing for the round.
				if(actionIndex == indexPlayerNotPlaying) {
					continue;
				}
				
				middleMan.sendMessageToGroup(playerDecider[actionIndex].getName() + "'s turn:");
				
				middleMan.sendMessageToPlayer(playerDecider[actionIndex].getName(), "Play a card!");
				
				prevPlay[actionIndex] = playerDecider[actionIndex].getCard(playerModel[actionIndex].getHand(), prevPlay, euchreCall, dealerIndex, actionIndex);
				
				middleMan.sendMessageToPlayer(playerDecider[actionIndex].getName(), "Thank you!");
				
				if( playerModel[actionIndex].hasCard(prevPlay[actionIndex]) ==false ||
				isReneging(initialActionIndex, actionIndex, prevPlay, playerModel[actionIndex].getHand(), euchreCall.getTrump())) {
					
					//autoinsert card:
					for(int k=0; k<playerModel[actionIndex].getHand().length && isReneging(initialActionIndex, actionIndex, prevPlay, playerModel[actionIndex].getHand(), euchreCall.getTrump()); k++) {
						prevPlay[actionIndex] = playerModel[actionIndex].getHand()[k];
					}
					
					if(isReneging(initialActionIndex, actionIndex, prevPlay, playerModel[actionIndex].getHand(), euchreCall.getTrump())) {
						middleMan.sendMessageToGroup("ERROR: Couldn\'t find a valid card to play!!!");
						System.out.println("ERROR: Couldn\'t find a valid card to play!!!");
						System.exit(1);
					}
					
					if(playerModel[actionIndex].hasCard(prevPlay[actionIndex]) ==false) {
						middleMan.sendMessageToGroup(playerModel[actionIndex].getPlayerName() + " tried to play a card that\'s not in her hand.", false);
					} else {
						middleMan.sendMessageToGroup(playerModel[actionIndex].getPlayerName() + " tried to renege.", false);
					}
				}
				
				middleMan.recordCommand(random.card.DeckFunctions.getCardString(prevPlay[actionIndex]));
				
				middleMan.sendMessageToGroup(playerModel[actionIndex].getPlayerName() + " playing: " + random.card.DeckFunctions.getCardString(prevPlay[actionIndex]));
				
				if(j+1 < 4) {
					if(j == 2 && indexPlayerNotPlaying == (initialActionIndex + 3)%4) {
						//Don't print the last - if the 4th fighter isn't playing the round.
					} else {
						middleMan.recordCommand(" - ");
					}
				}
				
				playerModel[actionIndex].takeCard(prevPlay[actionIndex]);
				
			}
			
			
			indexWinner = fight(initialActionIndex, prevPlay, euchreCall.getTrump());
			middleMan.sendMessageToGroup("Fight Winner: " + playerModel[indexWinner].getPlayerName());
			middleMan.recordCommand("\n");
			
			playerModel[indexWinner].giveTrick();
					
			initialActionIndex = indexWinner;
			
		}
		middleMan.sendMessageToGroup("ALL: END ROUND!");
		for(int i=0; i<4; i++) {
			middleMan.sendMessageToGroup("ALL: " + playerModel[i].getPlayerName() + " got " + playerModel[i].getNumTricks() + " trick(s).");
		}
		
	}
	
	public static int fight(int initialActionIndex, int cardsOnTable[], char trump) {
		int currentWinnerIndex = initialActionIndex;
		char currentBestSuit = Position.getEuchreSuit(cardsOnTable[initialActionIndex], trump);
		
		int currentBestPower = getPowerOfCard(cardsOnTable[initialActionIndex], trump);
		
		//System.out.println("TESTING FIGHT trump: " + trump);
		//System.out.println("TESTING " + random.card.DeckFunctions.getCardString(cardsOnTable[initialActionIndex]) + "FIGHT" + getPowerOfCard(cardsOnTable[initialActionIndex], trump));
		
		char nextSuit;
		int nextPower;
		int currentIndex;
		for(int i=0; i<3; i++) {
			currentIndex = (initialActionIndex + 1 + i)%4;
			
			//If a player is not playing, he didn't put a card on the table:
			if(cardsOnTable[currentIndex] == PlayerModel.NOT_A_CARD) {
				continue;
			}
			
			//System.out.println("TESTING " + "FIGHT");
			//System.out.println("TESTING " + random.card.DeckFunctions.getCardString(cardsOnTable[currentIndex]) + "FIGHT" + getPowerOfCard(cardsOnTable[currentIndex], trump));
			
			
			nextSuit = Position.getEuchreSuit(cardsOnTable[currentIndex], trump);
			
			if(nextSuit == trump && currentBestSuit != trump) {
				//TRUMPING!
				currentBestSuit = trump;
				currentBestPower = getPowerOfCard(cardsOnTable[currentIndex], trump);
				currentWinnerIndex = currentIndex;
				
			} else if(nextSuit == currentBestSuit) {
				//Following suit or trumping over:
				nextPower = getPowerOfCard(cardsOnTable[currentIndex], trump);
				
				if(nextPower > currentBestPower) {
					currentBestPower = nextPower;
					currentWinnerIndex = currentIndex;
				}
				
			}
		}

		return currentWinnerIndex;
	}
	
	public static int getPowerOfCard(int cardNum, char trump) {
		//Handle the case where the cardNum is unknown:
		if(cardNum == PlayerModel.NOT_A_CARD) {
			return -1;
		}
		
		//Handle trump:
		int power = 0;
		
		//trump bonus:
		if(Position.getEuchreSuit(cardNum, trump) == trump) {
			power += 13;
			//Bower bonus:
			if(random.card.DeckFunctions.getBaseNumber(cardNum) == 'J') {
				power += 13;
				//Right Bower bonus:
				if(random.card.DeckFunctions.getSuit(cardNum) == trump) {
					power += 1;
				}
			}
			
		}
		
		char cardBaseNum = random.card.DeckFunctions.getBaseNumber(cardNum);
		
		//Handle normal card:
		if(cardBaseNum >='2' && cardBaseNum<='9') {
			power += (int)(cardBaseNum - '2');
		} else if(cardBaseNum == 'T') {
			power +=  10;
		} else if(cardBaseNum == 'J') {
			power +=  11;
		} else if(cardBaseNum == 'Q') {
			power +=  12;
		} else if(cardBaseNum == 'K') {
			power +=  13;
		} else if(cardBaseNum == 'A') {
			power +=  14;
		} else {
			System.out.println("ERROR: unknown card number in getPowerOfCardNum! ( " + cardBaseNum + ")");
			System.exit(1);
			return -1;
		}
		
		return power;
	}
	
	

	//score could be 0, 1, 2, or 4.
	public int[] updateScore(PlayerModel playerModel[], EuchreCall euchreCall) {
		int callerIndex = euchreCall.getCallIndex();
		int updateCaller = 0;
		int updateNonCaller = 0;
		
		int combinedCalledTricks;
		
		if(callerIndex == 0 || callerIndex == 2) {
			combinedCalledTricks = playerModel[0].getNumTricks() + playerModel[2].getNumTricks();
		} else {
			combinedCalledTricks = playerModel[1].getNumTricks() + playerModel[3].getNumTricks();
		}
		
		if(combinedCalledTricks < 3) {
			//Euchred!
			updateNonCaller = 2;
		} else if(combinedCalledTricks < 5) {
			updateCaller = 1;
		} else if(combinedCalledTricks == 5 && euchreCall.isAlone() == false) {
			updateCaller = 2;
		} else if(combinedCalledTricks == 5 && euchreCall.isAlone() == true) {
			updateCaller = 4;
		} else {
			System.out.println("ERROR: score update has an error because outcome doesn't make sense for red bidder.");
			System.out.println("Euchre call: " + euchreCall);
		}
			
		if(callerIndex == 0 || callerIndex == 2) {
			return new int[] {updateCaller, updateNonCaller};
			
		} else {
			return new int[] {updateNonCaller, updateCaller};
			
		}
	}
	
	
	//Returns true if the player is reneging.
	public boolean isReneging(int initialActionIndex, int actionIndex, int currentPlay[], int cardsInHand[], char trump) {
		
		//The first player to act can't be reneging.
		if(initialActionIndex == actionIndex) {
			return false;
		
		//If the suits are equal, no reneging:
		} else if(getEuchreSuit(currentPlay[initialActionIndex], trump) == getEuchreSuit(currentPlay[actionIndex], trump)) {
			return false;
			
		} else {
			boolean hasSuit = false;
			int leadSuit = getEuchreSuit(currentPlay[initialActionIndex], trump);
			for(int i=0; i<cardsInHand.length; i++) {
				if(getEuchreSuit(cardsInHand[i], trump) == leadSuit) {
					hasSuit = true;
				}
			}
			
			if(hasSuit == false) {
				return  false;
			} else {
				//At this point, we know the player is reneging:
				return true;
			}
		}
		
	}
	
	//pre: trump is upper case.
	public static char getEuchreSuit(int cardNum, char trump) {
		
		if(isLeftBower(cardNum, trump)) {
			return trump;
		} else {
			return random.card.DeckFunctions.getSuit(cardNum);
		}
		
	}
	
	public static boolean isLeftBower(int cardNum, char trump) {

		if(random.card.DeckFunctions.getBaseNumber(cardNum) == 'J') {
			//*********************
			if(trump == 'S' && random.card.DeckFunctions.getSuit(cardNum) == 'C') {
				return true;
			} else if(trump == 'C' && random.card.DeckFunctions.getSuit(cardNum) == 'S') {
				return true;
			} else if(trump == 'D' && random.card.DeckFunctions.getSuit(cardNum) == 'H') {
				return true;
			} else if(trump == 'H' && random.card.DeckFunctions.getSuit(cardNum) == 'D') {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	public void printUpdate(int redScore, int blueScore, int scoresObtained[], EuchreServerMiddleMan middleMan) {
		middleMan.sendMessageToGroup(redScore + "    " + blueScore);
		middleMan.sendMessageToGroup("END PRINT SCORE!");
	
	}
}

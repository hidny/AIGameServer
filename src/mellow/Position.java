package mellow;

import random.card.DeckFunctions;


public class Position {
	//bid goes from 0 to 13.
	//0 = Mellow
	//You can't say 0.
	
	public static String GAME_NAME = "mellow";
	
	private int redScore = 0;
	private int blueScore = 0;
	private random.card.Deck currentDeck;
	
	public static int GOAL_SCORE = 1000;
	public static int OLD_YELLAR_SCORE = -500;
	
	public static int RANDOM_DEALER_POSITION = -3;
	
	public static String TRUMP = "S";
	
	
	public static void main(String args[]) {
		System.out.println("Let's play mellow!");
		
		MellowServerMiddleMan middleMan = new MellowServerMiddleMan(null);
		
		
		PlayerDecider red[] = new PlayerDecider[2];
		PlayerDecider blue[] = new PlayerDecider[2];
		red[0] = new HumanConsole("Richard");
		red[1] = new HumanConsole("Michael");
		
		blue[0] = new HumanConsole("Doris");
		blue[1] = new HumanConsole("Phil");
		
		startMellow(middleMan, red, blue);
	}
	
	public static void startMellow(MellowServerMiddleMan middleMan, PlayerDecider red[], PlayerDecider blue[]) {
			startMellow(middleMan, red, blue, null, 0, 0, RANDOM_DEALER_POSITION);
	}

	public static void startMellow(MellowServerMiddleMan middleMan, PlayerDecider red[], PlayerDecider blue[], int redStartScore, int blueStartScore, int dealerIndex) {
		startMellow(middleMan, red, blue, null, redStartScore, blueStartScore, dealerIndex);
	}
	
	//When you need it.
	public static void startMellow(MellowServerMiddleMan middleMan, PlayerDecider red[], PlayerDecider blue[], random.card.Deck givenDeck, int redStartScore, int blueStartScore, int dealerIndex) {
		
		Position pos = new Position(redStartScore, blueStartScore);
		
		middleMan.recordCommand(red[0].getName() + " & " + red[1].getName() + " vs " + blue[0].getName() + " & " + blue[1].getName()  + "\n");
		
		middleMan.sendMessageToGroup("Starting Mellow! " + red[0].getName() + " & " + red[1].getName() + " vs " + blue[0].getName() + " & " + blue[1].getName());
		if(redStartScore != 0 || blueStartScore != 0) {
			middleMan.sendMessageToGroup(red[0].getName() + " & " + red[1].getName() + " start with " + redStartScore + " points.");
			middleMan.sendMessageToGroup(blue[0].getName() + " & " + blue[1].getName() + " start with " + blueStartScore + " points.");
		}
		
		
		int result = pos.playGame(red, blue, middleMan, dealerIndex, givenDeck);
		
		if ( result == RED_WINS) {
			middleMan.sendMessageToGroup(red[0].getName() + " & " + red[1].getName() + " win!");
		} else if(result == BLUE_WINS ){
			middleMan.sendMessageToGroup(blue[0].getName() + " & " + blue[1].getName() + " win!");
		} else {
			middleMan.sendMessageToGroup("It's a tie. This is obviously rigged!");
			
		}
	}
	
	public Position(int redStartScore, int blueStartScore) {
		this.redScore = redStartScore;
		this.blueScore = blueStartScore;
	}
	public int playGame(PlayerDecider redDecider[], PlayerDecider blueDecider[],  MellowServerMiddleMan middleMan) {
		return playGame(redDecider, blueDecider, middleMan, RANDOM_DEALER_POSITION, null);
	}
	
	//returns false otherwise.
	public int playGame(PlayerDecider redDecider[], PlayerDecider blueDecider[],  MellowServerMiddleMan middleMan, int dealerIndex, random.card.Deck givenDeck) {
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
		
		if(dealerIndex == RANDOM_DEALER_POSITION) {
			dealerIndex = getIndexFirstDealer();
		}
		
		
		middleMan.sendMessageToGroup("First dealer is " + playerDeciders[dealerIndex].getName());
		
		middleMan.recordCommand("Original Dealer: " + playerDeciders[dealerIndex].getName() + "\n");
		
		//If no deck is given, create a new one:
		if(givenDeck == null) {
			currentDeck = new random.card.RandomDeck(middleMan.getCommandFile());
		} else {
			currentDeck = givenDeck;
		}
		
		int scoresDelta[];
		
		boolean firstHand  = true;
		
		String hand = "";
		while(isGameOver() == false) {
			//update dealer on all rounds except for the first one:
			if(firstHand == false) {
				middleMan.sendMessageToGroup("Current dealer is " + playerDeciders[dealerIndex].getName());
			} else {
				firstHand = false;
			}
			
			dealEmUp(playerModel, dealerIndex);
			
			for(int i=0; i<4; i++) {
				for(int j=0; j<13; j++) {
					hand += random.card.DeckFunctions.getCardString(playerModel[i].getHand()[j]) + " ";
				}
				middleMan.sendMessageToPlayer(playerModel[i].getPlayerName(),  hand);
				hand = "";
			}
			BiddingPhase.getBids(playerModel, playerDeciders, dealerIndex, redScore, blueScore, middleMan);
			
			printBids(dealerIndex, playerModel, middleMan);
			
			playRound(dealerIndex, playerModel, playerDeciders, middleMan);
			
			scoresDelta = updateScore(playerModel);
			
			redScore += scoresDelta[0];
			blueScore+= scoresDelta[1];
			
			printUpdate(redScore, blueScore, scoresDelta, middleMan);
			
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
		
		for(int i=1; currentDeck.hasCards(); i++) {
			temp = currentDeck.getNextCard();
			playerModel[(dealerIndex + i)%4].give(temp);
		}
	}

	public static String printGameScore() {
		return "TODO: print it in mom notation.";
	}
	
	public boolean isGameOver() {
		if(this.redScore >= GOAL_SCORE || this.blueScore >= GOAL_SCORE) {
			return true;
		}
		
		if(this.redScore <= OLD_YELLAR_SCORE || this.blueScore <= OLD_YELLAR_SCORE) {
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
	}
	
	
	public void printBids(int dealerIndex, PlayerModel playerModel[], MellowServerMiddleMan middleMan) {
		
		middleMan.sendMessageToGroup("Bids:");
		printBidForTeam(playerModel[0], playerModel[2], middleMan);
		printBidForTeam(playerModel[1], playerModel[3], middleMan);
		
	}
	
	public void printBidForTeam(PlayerModel player, PlayerModel partner, MellowServerMiddleMan middleMan) {
		int combinedBid = player.getNumBid() + partner.getNumBid();
		boolean Mellow1 = player.isMellow();
		boolean Mellow2 = partner.isMellow();
		
		if(Mellow1 && Mellow2) {
			middleMan.sendMessageToGroup("2  ");
		} else if(Mellow1 || Mellow2) {
			middleMan.sendMessageToGroup("1 " + combinedBid);
		} else {
			middleMan.sendMessageToGroup("  " + combinedBid);
		}
	}
	
	
	
	public void playRound(int dealerIndex, PlayerModel playerModel[], PlayerDecider playerDecider[], MellowServerMiddleMan middleMan) {
		int CARDS_PER_HAND = random.card.RandomDeck.STANDARD_DECK_SIZE / 4;
		
		int prevPlay[] = new int[4];
		
		int initialActionIndex = (dealerIndex + 1) % 4;
		int actionIndex;
		
		int indexWinner;
		
		middleMan.sendMessageToGroup("Dealer Index: " + dealerIndex);
		middleMan.sendMessageToGroup("Dealer: " + playerModel[dealerIndex].getPlayerName());
		
		String hand = "";
		for(int i=0; i<CARDS_PER_HAND; i++) {
			
			//TESTING:
			//System.out.println("Current hands:");
			for(int j=0; j<4; j++) {
				prevPlay[j] = PlayerModel.NOT_A_CARD;
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
				
				middleMan.sendMessageToGroup(playerDecider[actionIndex].getName() + "'s turn:");
				
				middleMan.sendMessageToPlayer(playerDecider[actionIndex].getName(), "Play a card!");
				
				prevPlay[actionIndex] = playerDecider[actionIndex].getCard(playerModel[actionIndex].getHand(), prevPlay, actionIndex);
				
				middleMan.sendMessageToPlayer(playerDecider[actionIndex].getName(), "Thank you!");
				
				if( playerModel[actionIndex].hasCard(prevPlay[actionIndex]) ==false ||
						isReneging(initialActionIndex, actionIndex, prevPlay, playerModel[actionIndex].getHand()) ) {
					
					//autoinsert card:
					for(int k=0; k<playerModel[actionIndex].getHand().length && isReneging(initialActionIndex, actionIndex, prevPlay, playerModel[actionIndex].getHand()); k++) {
						prevPlay[actionIndex] = playerModel[actionIndex].getHand()[k];
					}
					
					if(isReneging(initialActionIndex, actionIndex, prevPlay, playerModel[actionIndex].getHand())) {
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
					middleMan.recordCommand(" - ");
				}
				
				playerModel[actionIndex].takeCard(prevPlay[actionIndex]);
				
			}
			
			
			indexWinner = fight(initialActionIndex, prevPlay);
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
	
	public static int fight(int initialActionIndex, int cardsOnTable[]) {
		int currentWinnerIndex = initialActionIndex;
		String currentBestSuit = "" + random.card.DeckFunctions.getSuit(cardsOnTable[initialActionIndex]);
		int currentBestPower = getPowerOfCardNum(DeckFunctions.getBaseNumber(cardsOnTable[initialActionIndex]));
		
		String nextSuit;
		int nextPower;
		int currentIndex;
		for(int i=0; i<3; i++) {
			currentIndex = (initialActionIndex + 1 + i)%4;
			
			nextSuit = "" + random.card.DeckFunctions.getSuit(cardsOnTable[currentIndex]);
			if(nextSuit.equals(TRUMP) && currentBestSuit.equals(TRUMP) == false) {
				//TRUMP!
				currentBestSuit = TRUMP;
				currentBestPower = getPowerOfCardNum(DeckFunctions.getBaseNumber(cardsOnTable[currentIndex]));
				currentWinnerIndex = currentIndex;
			} else if(nextSuit.equals(currentBestSuit)) {
				nextPower = getPowerOfCardNum(DeckFunctions.getBaseNumber(cardsOnTable[currentIndex]));
				
				if(nextPower > currentBestPower) {
					currentBestPower = nextPower;
					currentWinnerIndex = currentIndex;
				}
				
			}
			//cardsOnTable
		}

		//TESING:
		//for(int i=0; i<4; i++) {
		//	System.out.print(deck.DeckFunctions.getCardString(cardsOnTable[i])  +  ", ");
		//}
		//System.out.println("ALL: END FIGHT!");
		//END TESTING
		
		return currentWinnerIndex;
	}
	
	public static int getPowerOfCardNum(char cardNumber) {
		if(cardNumber >='2' && cardNumber<='9') {
			return (int)(cardNumber - '2');
		} else if(cardNumber == 'T') {
			return 10;
		} else if(cardNumber == 'J') {
			return 11;
		} else if(cardNumber == 'Q') {
			return 12;
		} else if(cardNumber == 'K') {
			return 13;
		} else if(cardNumber == 'A') {
			return 14;
		} else {
			System.out.println("ERROR: unknown card number in getPowerOfCardNum! ( " + cardNumber + ")");
			System.exit(1);
			return -1;
		}
	}
	
	public int[] updateScore(PlayerModel playerModel[]) {
		int redScoreDelta = getTeamPointsObtained(playerModel[0], playerModel[2]);
		int blueScoreDelta = getTeamPointsObtained(playerModel[1], playerModel[3]);
		
		return new int[] {redScoreDelta,blueScoreDelta};
	}
	
	private int getTeamPointsObtained(PlayerModel player, PlayerModel partner) {
		int combinedBid = player.getNumBid() + partner.getNumBid();
		int combinedTricks = player.getNumTricks() + partner.getNumTricks();
		boolean BurntMellow1 = player.isBurntMellow();
		boolean BurntMellow2 = partner.isBurntMellow();
		
		int score = 0;
		//if double mellow:
		//I award you no extra points and god have mercy on your soul.
		
		//you get 10*bid + number overbid.
		//(It looks good when logged with pen & paper)
		if(combinedTricks >= combinedBid && combinedBid > 0) {
			score += 10*combinedBid + (combinedTricks-combinedBid);
		} else if(combinedTricks < combinedBid) {
			//Burnt:
			score -= 10 * combinedBid;
		}
		
		//check for mellow for first player
		if(BurntMellow1) {
			score -= 100;
		} else if(BurntMellow1 == false && player.isMellow()) {
			score += 100;
		}
		
		
		if(BurntMellow2) {
			score -= 100;
		} else if(BurntMellow2 && partner.isMellow()) {
			score += 100;
		}
		
		return score;
	}
	
	//Returns true if the player is renaging.
	public boolean isReneging(int initialActionIndex, int actionIndex, int currentPlay[], int cardsInHand[]) {
		//The first player to act can't be renaging.
		if(initialActionIndex == actionIndex) {
			return false;
		//If the suits are equal, no renaging:
		} else if(random.card.DeckFunctions.getSuit(currentPlay[initialActionIndex]) == random.card.DeckFunctions.getSuit(currentPlay[actionIndex])) {
			return false;
		} else {
			boolean hasSuit = false;
			int leadSuit = random.card.DeckFunctions.getSuit(currentPlay[initialActionIndex]);
			for(int i=0; i<cardsInHand.length; i++) {
				if(random.card.DeckFunctions.getSuit(cardsInHand[i]) == leadSuit) {
					hasSuit = true;
				}
			}
			
			if(hasSuit == false) {
				return  false;
			} else {
				//At this point, we know the player is renaging:
				return true;
			}
		}
		
	}
	
	public void printUpdate(int redScore, int blueScore, int scoresObtained[], MellowServerMiddleMan middleMan) {
		middleMan.sendMessageToGroup((redScore-scoresObtained[0]) + "    " + (blueScore-scoresObtained[1]));
		middleMan.sendMessageToGroup(scoresObtained[0] + "    " + scoresObtained[1]);
		middleMan.sendMessageToGroup(redScore + "    " + blueScore);
		middleMan.sendMessageToGroup("END PRINT SCORE!");
	
	}
}

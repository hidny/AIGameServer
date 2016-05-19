package euchre;


public class CallingPhase {
	
	public static int NUMBER_OF_CALLING_ROUNDS = 2;
	
	public static EuchreCall getCall(PlayerModel playerModel[], PlayerDecider player[], int dealerIndex, String trumpCard, int callingRound, int redScore, int blueScore, EuchreServerMiddleMan middleMan, EuchreVariation variation) {
		
		boolean callOnFirstRound = false;
		
		if(callingRound == 1) {
			callOnFirstRound = true;
		}
		

		char trumpCardTrump = trumpCard.toUpperCase().charAt(1);
		
		EuchreCall euchreCall = null;
		
		String currentBid;
		
		int indexPlayerAction;
		int numChances = 0;
		
		boolean exchangeTrumpCard = false;
	
		//1st calling round:
		for(int j=0; j<Position.NUM_PLAYERS && (euchreCall == null || euchreCall.isPassing()); j++) {
			
			indexPlayerAction = (dealerIndex + 1+ j)%4;
			numChances = 0;

			middleMan.sendMessageToGroup(playerModel[indexPlayerAction].getPlayerName() +"'s turn to call:");
			

			euchreCall = null;
			
			while ( euchreCall == null ) {
				
				currentBid = null;
				
				//Default eurchre call:
				if(numChances >= 3) {
					//set default call to some random trump if player is last to make a call:
					//For testing: you could modify this to change the way game goes.
					//AAAH: this is a bad place to put this code. There's no validation here!
					//Warning: don't test illegal bids.
					

					currentBid = "p";
					
					if((dealerIndex+3)%4 == indexPlayerAction && callingRound == 1) {
						
						//Set trump to clubs and go alone.
						currentBid = trumpCardTrump + "A";
						
					} else {
					}
				//End default call
				} else {
				
					//Ask user for the call:
					middleMan.sendMessageToPlayer(playerModel[indexPlayerAction].getPlayerName(), "What's your call?");
				
					currentBid = player[indexPlayerAction].getCall(playerModel[indexPlayerAction].getHand(), dealerIndex, trumpCard, callingRound, indexPlayerAction);

				}
				
				if(currentBid != null && currentBid.length() > 0) {
				
					currentBid = currentBid.toLowerCase().trim();
					
					if(currentBid.startsWith("p")) {
						//player passes:
						euchreCall = new EuchreCall(dealerIndex, indexPlayerAction, "", false, callOnFirstRound, true);
						
					} else {
						char calledTrump = currentBid.toUpperCase().charAt(0);
						
						if(callingRound == 1 && trumpCardTrump == calledTrump) {
							
							if(currentBid.contains("a")) {
								euchreCall = new EuchreCall(dealerIndex, indexPlayerAction, "" + calledTrump, true, callOnFirstRound, false);
								if((indexPlayerAction + 2) % 4 == dealerIndex) {
									exchangeTrumpCard = false;
								} else {
									exchangeTrumpCard = true;
								}
								
							} else {
								
								// VARIATION
								//Ontarian variation doesn't allow this.
								//(I play by this variation too!)
								//Ontarionian variation:
								//http://www.bicyclecards.com/how-to-play/euchre/
								//"Marie-Lyne Ménard
								// February 20, 2016 at 12:08 am
								//Euchre game
								//I’m from Ontario Canada and started playing Euchre 10 years ago. Since I’ve been playing, 
								// the rule has always been that if the dealer is ordered up by his/her partner, that partner must go alone.
								//I know of some people who play other variations but every time I’ve played house games, that’s how we’ve played. 
								//I find that this way makes it more fun and challenging. 
								//There are a lot less 2 and 4 points therefore makes the game more interesting and competitive. 
								//I played STD and under on Apps, but it’s not the same fun.  :)
								//- See more at: http://www.bicyclecards.com/how-to-play/euchre/#sthash.vSsigDXX.dpuf
								//	"
								//Ontarian variation:
								if( variation.isDealerPartnerOrdersUpAloneOrPass() && (indexPlayerAction + 2) % 4 == dealerIndex) {
									middleMan.sendMessageToPlayer(player[indexPlayerAction].getName(), "DISALLOWED! The Ontarian version of Euchre forces you to go alone if you order up your partner.");
								} else {
									euchreCall = new EuchreCall(dealerIndex, indexPlayerAction, "" + calledTrump, false, callOnFirstRound, false);
									exchangeTrumpCard = true;
								}
							}
							
						} else if(callingRound == 2 && trumpCardTrump != calledTrump) {
							if(calledTrump != 'S' && calledTrump != 'C'  && calledTrump != 'H'  && calledTrump != 'D') {
								//unknown trump. Do nothing.
							} else {
								if(currentBid.contains("a")) {
									euchreCall = new EuchreCall(dealerIndex, indexPlayerAction, "" + calledTrump, true, callOnFirstRound, false);
								} else {
									euchreCall = new EuchreCall(dealerIndex, indexPlayerAction, "" + calledTrump, false, callOnFirstRound, false);
								}
								
							}
						}
					}
				}
				
				numChances++;
				
				
			}
			
			middleMan.sendMessageToPlayer(playerModel[indexPlayerAction].getPlayerName(), "Thank you.");
			
			middleMan.sendMessageToGroup(playerModel[indexPlayerAction].getPlayerName() +" " + euchreCall.toString());
			middleMan.recordCommand(euchreCall.getCmdString() + "\n");
			
		}
		
		//Handle case when the dealer exchanges a card with the trump card:
		if(exchangeTrumpCard == true) {

			middleMan.sendMessageToGroup(playerModel[dealerIndex].getPlayerName() +" is exchanging a card with the trump card.");
			
			middleMan.sendMessageToPlayer(playerModel[dealerIndex].getPlayerName(), "Pick a card to exchange with the trump card");
			
			int card = player[dealerIndex].getCard(playerModel[dealerIndex].getHand(), null, euchreCall, dealerIndex, dealerIndex);
			playerModel[dealerIndex].takeCard(card);
			playerModel[dealerIndex].give(random.card.DeckFunctions.getCard(trumpCard));
			middleMan.recordCommand(random.card.DeckFunctions.getCardString(card) + "\n");
			

			middleMan.sendMessageToPlayer(playerModel[dealerIndex].getPlayerName(), "Your new hand: " + playerModel[dealerIndex].getHandString());
			middleMan.sendMessageToPlayer(playerModel[dealerIndex].getPlayerName(), "Thank you.");
			
			middleMan.sendMessageToGroup(playerModel[dealerIndex].getPlayerName() +" is finished exchanging a card with the trump card.");
			
		}
		
		return euchreCall;
	}
}

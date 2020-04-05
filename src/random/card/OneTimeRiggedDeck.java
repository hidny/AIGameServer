package random.card;


import java.io.PrintWriter;
import java.util.ArrayList;



public class OneTimeRiggedDeck implements Deck {

	//This is a first draft. It will get improved later!
		 private int deck[];
		//this variable holds which card is currently on the top on the deck:
		 private int currentCardIndex = 0;

		 
		 
		 public static final int  NUM_SUITS = 4;
		 public static final int  CARDS_PER_SUIT = 13;
		 
		 public static final int STANDARD_DECK_SIZE = 52;
		 public static final int CARD_ASCII_LENGTH = 2;
		
		 private PrintWriter record;
		
		 private String gameArgs[];
		 
		 private boolean isFirstHand;
		//cards are labelled from 1 to 52:
		
		//pre: numDecks > 1
		public OneTimeRiggedDeck(int numStandardDecks, PrintWriter record, String gameArgs[]) {
			deck = new int[STANDARD_DECK_SIZE * numStandardDecks];
			for(int i=0; i<numStandardDecks; i++) {
				for(int j=0; j<STANDARD_DECK_SIZE; j++) {
					deck[j + i * STANDARD_DECK_SIZE] = j + 1;
				}
			}
			this.record = record;

			this.gameArgs = gameArgs;
			this.isFirstHand = true;
			
		}
		public OneTimeRiggedDeck(PrintWriter record, String gameArgs[]) {
			this(1, record, gameArgs);
		}

		public OneTimeRiggedDeck(int numStandardDecks, String gameArgs[]) {
			this(numStandardDecks, null, gameArgs);
		}
		
		public OneTimeRiggedDeck(String gameArgs[]) {
			this(1, null, gameArgs);
		}
		
		
		//DONE: TESTED SHUFFLE!
		private void shuffle(int startPos) {
			for(int i=startPos; i<deck.length; i++) {
				swap(i, i + (int)(Math.random() * (deck.length-i)) );
			}
			currentCardIndex = startPos;
			if(currentCardIndex!= 0) {
				System.out.println("ERROR: WTF!");
				System.exit(1);
			}
			if(record != null) {
				for(int i=0; i<deck.length; i++) {
					record.print(DeckFunctions.getCardString(deck[i]) + " ");
				}
				record.println();
			}
		}
		
		//pre: i and j are indexes of the deck:
		private void swap(int i, int j) {
			int temp = deck[i];
			deck[i] = deck[j];
			deck[j] = temp;
		}
		
		//Functions usable by the outside world:
		public void shuffle() {
			if(isFirstHand) {
				
				rigDeck(gameArgs);
				
				isFirstHand = false;
			} else {
				shuffle(0);
			}
		}
		
		public void shuffleUnUsedDeck() {
			shuffle(currentCardIndex);
		}
		
	//First test: (empty password...)
	// -start server
	// -Start client.java
		
	// /create mellow mellowpy  0 0 0 -fulldeck 3D TD 5D 7C 7S 6S 5C JS 5H JC 8S 2S 8D QH JD KD 8H TS TC TH 7D 2C AC 4C 6D QS QC AS 2H KC AD 9D 6H JH 9C 3S 3C KS QD 5S AH 4H 9S 4D 8C 9H 3H KH 7H 6C 2D 4S 
	// /start
	// It worked the first time OMG!
		
	// Second test: (empty password...)
	// /create mellow mellowpy  0 0 0 -handrig [AS ADKSQSJSKD2C][][3C][2D3D,4D]
	// /start
	// Results: Player left of dealer got 1st list, player on right of dealer got 3C, and dealer gave themselves 2D, 3D and 4D
		
	// Third test: (empty password...)
	// /create mellow mellowpy  0 0 0 -handrig [AS ADKSQSJSKD2C][4C][3C]
	// /start
	// Results: Player left of dealer got 1st list, and player on right of dealer got 3C

		public void rigDeck(String gameArgs[]) {
			for(int i=0; i<gameArgs.length; i++) {
				if(gameArgs[i].toLowerCase().contains("-fulldeck")) {
					rigFullDeck(gameArgs, i);

				} else if(gameArgs[i].toLowerCase().contains("-hand")) {
					rigHands(gameArgs, i);
					
				}
			}
		}
		
		public void rigFullDeck(String gameArgs[], int indexStart) {
			if(gameArgs.length <= indexStart) {
				System.out.println("ERROR: no card deck arg in OneTimeRiggedDeck");
				return;
			}
			//[3D TD 5D 7C 7S 6S 5C JS 5H JC 8S 2S 8D QH JD KD 8H TS TC TH 7D 2C AC 4C 6D QS QC AS 2H KC AD 9D 6H JH 9C 3S 3C KS QD 5S AH 4H 9S 4D 8C 9H 3H KH 7H 6C 2D 4S]
			
			String deckPart = "";
			for(int i=indexStart+1; i<gameArgs.length; i++) {
				deckPart += gameArgs[i] + " ";
			}
			
			//System.out.println(deckPart);
			deckPart = deckPart.replaceAll("[^0-9a-zA-Z]", "");
			
			//System.out.println(deckPart);
			
			if(deckPart.length() != CARD_ASCII_LENGTH*STANDARD_DECK_SIZE) {
				System.out.println("ERROR: didn't get the right number of cards to rig (got deckPart.length() cards)");
				return;
			}
			
			if(deck.length != STANDARD_DECK_SIZE) {
				System.out.println("ERROR: Deck length unexpected in OneTimeRiggedDeck");
				return;
			}
			
			for(int i=0; i<deck.length; i++ ) {
				String card = deckPart.substring(2*i, 2*i+2).toUpperCase();
				
				if(DeckFunctions.getCardNumber(card) == -1) {
					System.out.println("ERROR: Unknown card: " + card);
					System.out.println("Deck: " + deck);
				}
				deck[i] = DeckFunctions.getCardNumber(card);
			}
			
			
		}
		
		public void rigHands(String gameArgs[], int indexStart) {
			if(gameArgs.length <= indexStart) {
				System.out.println("ERROR: no card deck arg in OneTimeRiggedDeck");
				return;
			}
			//[AS KS QS AC ] [] [AH][2C2S2DTD]
			
			//TODO: add a -numPlayers argument, so this function doesn't just have to assume it's 4 players getting cards in future
			String handsToParse = "";
			for(int i=indexStart+1; i<gameArgs.length; i++) {
				handsToParse += gameArgs[i] + " ";
			}
			
			//System.out.println(deckPart);
			handsToParse = handsToParse.replaceAll("[^0-9a-zA-Z\\[\\]]", "");
			
			boolean cardsUsed[] = new boolean[STANDARD_DECK_SIZE];
			for(int i=0; i<cardsUsed.length; i++) {
				cardsUsed[i] = false;
			}
			

			int ASSUMED_NUM_PLAYERS = 4;
			
			
			int handIndex = 0;
			ArrayList<Integer>[] cardsSetToHand = new ArrayList[ASSUMED_NUM_PLAYERS];
	        
			while(handsToParse.contains("[")) {
				if(handsToParse.contains("]") == false) {
					System.out.println("Error: no closing bracket");
					System.exit(1);
				}
				
				cardsSetToHand[handIndex] = new ArrayList<Integer>(); 
				
				String hand = handsToParse.substring(handsToParse.indexOf('[') + 1, handsToParse.indexOf(']'));
				
				handsToParse = handsToParse.substring(handsToParse.indexOf(']'));
				if(handsToParse.contains("[")) {
					handsToParse = handsToParse.substring(handsToParse.indexOf('['));
				}
				
				
				hand = hand.replaceAll("[^0-9a-zA-Z]", "");
				
				for(int i=0; i<hand.length() / CARD_ASCII_LENGTH; i++) {
					String card = hand.substring(CARD_ASCII_LENGTH * i, CARD_ASCII_LENGTH * (i+1)).toUpperCase();
					
					if(DeckFunctions.getCardNumber(card) == -1) {
						System.out.println("ERROR: Unknown card in rigHands: " + card);
						System.exit(1);
					}
					
					int tmpCard = DeckFunctions.getCardNumber(card);
					
					if(cardsUsed[tmpCard - 1] == true) {
						System.out.println("ERROR: card is already used. (in rigHands) (" + card + ")");
						System.exit(1);
					}
					cardsUsed[tmpCard - 1] = true;
					cardsSetToHand[handIndex].add(tmpCard);
					
				}
				
				handIndex++;
			}
			
			//Shuffle cards, then rearrange them according to how it was customized...
			//This is not the optimal way doing it, but I'm lazy... :P
			shuffle(0);
			
			for(handIndex=0; handIndex<ASSUMED_NUM_PLAYERS; handIndex++) {
				
				//if this wasn't declared, skip to the next one:
				if(cardsSetToHand[handIndex] == null) {
					continue;
				}
				
				for(int j=0; j<cardsSetToHand[handIndex].size(); j++) {
					int cardToSwap = cardsSetToHand[handIndex].get(j);
					int indexCardShouldGo = ASSUMED_NUM_PLAYERS*j + handIndex;
					
					int curLocationCard = -1;
					for(int k=0; k<deck.length; k++) {
						if(deck[k] == cardToSwap) {
							curLocationCard = k;
							break;
						}
					}
					if(curLocationCard == -1) {
						System.out.println("ERROR: could not find card! (" + cardToSwap + ") or " + DeckFunctions.getCardString(cardToSwap));
					}
					
					swap(curLocationCard, indexCardShouldGo );
					
				}
			}
			
			
		}
		
		public int getNextCard() {
			assert(currentCardIndex < deck.length);
			
			int card = deck[currentCardIndex];
			currentCardIndex++;
		
			return card;
		}
		
		public void putCardsBackInDeck() {
			currentCardIndex  = 0;
			shuffle();
		}
		
		public boolean hasCards() {
			if( currentCardIndex == deck.length) {
				return false;
			} else {
				return true;
			}
		}
		
		//TESTING: (Copied from random deck)
		//***************************
		//***************************
		//***************************
		
		public static void main(String args[]) {
			
			OneTimeRiggedDeck first = new OneTimeRiggedDeck(null);
			
			System.out.println("Printing deck contents:");
			for(int i=0; i<52; i++) {
				System.out.println("" + DeckFunctions.getCardString(first.deck[i]));
			}
			
			first.shuffle();
			
			System.out.println();
			System.out.println("After a shuffle: ");
			
			for(int i=0; i<52; i++) {
				System.out.println("" + DeckFunctions.getCardString(first.deck[i]));
			}
			
			System.out.println("Test based on frequency analysis:");
			
			//freq analysis: first index for position and second for card value
			int freqAnalysis[][] = new int[52][52];
			
			for(int i =0; i<52; i++) {
				for(int j =0; j<52; j++) {
					freqAnalysis[i][j] = 0;
				}
			}
			
			for(int i=0; i<100000; i++) {
				//first = new Deck();
				first.shuffle();
				for(int j=0; j<52; j++) {
					freqAnalysis[j][first.deck[j] - 1]++;
				}
			}
			
			for(int i =0; i<52; i++) {
				for(int j =0; j<52; j++) {
					if(freqAnalysis[i][j] == 0) {
						System.out.println("Error: no " + DeckFunctions.getCardString(j) + " at position #" + j + "!");
					}
				}
			}
			
			System.out.println();
			System.out.println();
			System.out.println("printing the frequencies:");
			
			for(int i =0; i<52; i++) {
				System.out.println("Index #" + i + ":");
				for(int j =0; j<52; j++) {
					if(j%13 != 12) {
						System.out.print(freqAnalysis[i][j] + ", ");
					} else { 
						System.out.println(freqAnalysis[i][j]);
					}
				}
				System.out.println();
			}
			
		}
		
		
}

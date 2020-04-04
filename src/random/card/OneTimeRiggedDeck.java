package random.card;


import java.io.PrintWriter;



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
		// /create mellow mellowpy  0 0 0 -fulldeck 3D TD 5D 7C 7S 6S 5C JS 5H JC 8S 2S 8D QH JD KD 8H TS TC TH 7D 2C AC 4C 6D QS QC AS 2H KC AD 9D 6H JH 9C 3S 3C KS QD 5S AH 4H 9S 4D 8C 9H 3H KH 7H 6C 2D 4S 
		// /start
		// It worked the first time OMG!
		
		public void rigDeck(String gameArgs[]) {
			for(int i=0; i<gameArgs.length; i++) {
				if(gameArgs[i].toLowerCase().contains("fulldeck")) {
					rigFullDeck(gameArgs, i);

				} else if(gameArgs[i].toLowerCase().contains("hand")) {
					//TODO
					System.out.println("ERROR: this feature isn't ready yet.");
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
		//deck[i] = DeckFunctions.getCardNumber(deckFromFile[i]);
		
		
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
		
		//TESTING:
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

package random.card;


import java.io.PrintWriter;



public class RandomDeck implements Deck {

	//This is a first draft. It will get improved later!
		 private int deck[];
		//this variable holds which card is currently on the top on the deck:
		 private int currentCardIndex = 0;
			
		 public static final int  NUM_SUITS = 4;
		 public static final int  CARDS_PER_SUIT = 13;
		 
		 public static final int STANDARD_DECK_SIZE = 52;
		
		 private PrintWriter record;
		
		//cards are labelled from 1 to 52:
		
		//pre: numDecks > 1
		public RandomDeck(int numStandardDecks, PrintWriter record) {
			deck = new int[STANDARD_DECK_SIZE * numStandardDecks];
			for(int i=0; i<numStandardDecks; i++) {
				for(int j=0; j<STANDARD_DECK_SIZE; j++) {
					deck[j + i * STANDARD_DECK_SIZE] = j + 1;
				}
			}
			this.record = record;
			
		}
		public RandomDeck(PrintWriter record) {
			this(1, record);
		}

		public RandomDeck(int numStandardDecks) {
			this(numStandardDecks, null);
		}
		
		public RandomDeck() {
			this(1, null);
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
			shuffle(0);
		}
		
		public void shuffleUnUsedDeck() {
			shuffle(currentCardIndex);
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
		
		//TESTING:
		//***************************
		//***************************
		//***************************
		
		public static void main(String args[]) {
			
			RandomDeck first = new RandomDeck();
			
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

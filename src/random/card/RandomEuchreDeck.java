package random.card;

import java.io.PrintWriter;

public class RandomEuchreDeck implements Deck  {
	//This is a first draft. It will get improved later!
	 private int deck[];
	//this variable holds which card is currently on the top on the deck:
	 private int currentCardIndex = 0;
	 
	 public static final int STANDARD_DECK_SIZE = 52;
	 
	 public static final int EUCHRE_DECK_SIZE = 24;
	
	 private PrintWriter record;
	
	//cards are labelled from 1 to 52:
	
	//pre: numDecks > 1
	public RandomEuchreDeck(int numStandardDecks, PrintWriter record) {
		deck = new int[EUCHRE_DECK_SIZE * numStandardDecks];
		
		int currentIndex = 0;
		
		for(int i=0; i<numStandardDecks; i++) {
			for(int j=0; j<STANDARD_DECK_SIZE; j++) {
				
				if(DeckFunctions.isValidEuchreCard(j+1)) {
					deck[currentIndex] = j + 1;
					currentIndex++;
				}
				
			}
		}
		
		if(EUCHRE_DECK_SIZE * numStandardDecks != currentIndex) {
			System.out.println("ERROR: (RandomEuchreDeck) expected " + (EUCHRE_DECK_SIZE * numStandardDecks) + " cards but got " + currentIndex + " cards.");
			System.exit(1);
		}
		this.record = record;
		
	}
	public RandomEuchreDeck(PrintWriter record) {
		this(1, record);
	}

	public RandomEuchreDeck(int numStandardDecks) {
		this(numStandardDecks, null);
	}
	
	public RandomEuchreDeck() {
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
		
		RandomEuchreDeck first = new RandomEuchreDeck();
		
		System.out.println("Printing deck contents:");
		System.out.println("Length of Euchre deck: " + first.deck.length);
		
		for(int i=0; i<EUCHRE_DECK_SIZE; i++) {
			System.out.println("" + DeckFunctions.getCardString(first.deck[i]));
		}
		
		first.shuffle();
		
		System.out.println();
		System.out.println("After a shuffle: ");
		
		for(int i=0; i<EUCHRE_DECK_SIZE; i++) {
			System.out.println("" + DeckFunctions.getCardString(first.deck[i]));
		}
		
		System.out.println("Test based on frequency analysis:");
		
		//freq analysis: first index for position and second for card value
		int freqAnalysis[][] = new int[EUCHRE_DECK_SIZE][STANDARD_DECK_SIZE];
		
		for(int i =0; i<EUCHRE_DECK_SIZE; i++) {
			for(int j =0; j<STANDARD_DECK_SIZE; j++) {
				freqAnalysis[i][j] = 0;
			}
		}
		
		for(int i=0; i<100000; i++) {
			//first = new Deck();
			first.shuffle();
			for(int j=0; j<EUCHRE_DECK_SIZE; j++) {
				freqAnalysis[j][first.deck[j] - 1]++;
			}
		}
		
		for(int i =0; i<EUCHRE_DECK_SIZE; i++) {
			for(int j =0; j<STANDARD_DECK_SIZE; j++) {
				if(freqAnalysis[i][j] == 0) {
					System.out.println("No " + DeckFunctions.getCardString(j) + " at position #" + j + "!");
				}
			}
		}
		
		System.out.println();
		System.out.println();
		System.out.println("printing the frequencies:");
		
		for(int i =0; i<EUCHRE_DECK_SIZE; i++) {
			System.out.println("Index #" + i + ":");
			for(int j =0; j<STANDARD_DECK_SIZE; j++) {
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

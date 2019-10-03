package random.card;

import java.util.Scanner;


public class RiggedDeck implements random.card.Deck {

	
	
	 private int deck[];
	//this variable holds which card is currently on the top on the deck:
	 private int currentCardIndex = 0;
		
	 public static final int  NUM_SUITS = 4;
	 public static final int  CARDS_PER_SUIT = 13;
	 
	 public static final int STANDARD_DECK_SIZE = 52;
	 
	 public static final int EUCHRE_TYPE = 1;
	 public static final int STANDARD_EUCHRE_DECK_SIZE = 24;
	
	 public static Scanner inTestRun;
	
	//cards are labelled from 1 to 52:
	
	//pre: numDecks > 1
	public RiggedDeck(Scanner input, int numStandardDecks, int typeOfDeck) {
		inTestRun = input;
		
		if(typeOfDeck == EUCHRE_TYPE) {
			deck = new int[STANDARD_EUCHRE_DECK_SIZE * numStandardDecks];
		} else {
			deck = new int[STANDARD_DECK_SIZE * numStandardDecks];
		}
		
		
		
	}
	 
	//pre: numDecks > 1
	public RiggedDeck(Scanner input, int numStandardDecks) {
		deck = new int[STANDARD_DECK_SIZE * numStandardDecks];
		inTestRun = input;
		
		
		
	}
	
	public RiggedDeck(Scanner in) {
		this(in, 1);
	}
	
	
	private void shuffle(int startPos) {
		String deckFromFile[] = new String[0];
		
		//get line that has a list of cards length the number of cards expected:
		while(deckFromFile.length != deck.length - startPos) {
			deckFromFile = inTestRun.nextLine().split(" ");
		}
		
		for(int i=startPos; i<deckFromFile.length; i++) {
			deck[i] = DeckFunctions.getCardNumber(deckFromFile[i]);
		}
		//reset top of deck:
		currentCardIndex = startPos;
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
	}
	
	public boolean hasCards() {
		if( currentCardIndex == deck.length) {
			return false;
		} else {
			return true;
		}
	}

}

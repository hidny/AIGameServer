package euchre;


public class PlayerModel {
	private String player;
	
	
	public static int NOT_A_CARD = -1;
	public static int MAX_HAND_LENGTH = 5;
	
	private int card[] = new int[MAX_HAND_LENGTH];
	private int currentNumCards;
	
	private int numTricksMade;
	
	private EuchreServerMiddleMan middleMan;
	
	public void clearRoundVars() {
		for(int i=0; i<card.length; i++) {
			card[i] = NOT_A_CARD;
		}
		this.currentNumCards = 0;
		this.numTricksMade = 0;
	}
	public PlayerModel(String player, EuchreServerMiddleMan middle) {
		this.player = player;
		this.middleMan = middle;
	}
	
	public String getPlayerName() {
		return this.player;
	}
	
	public void give5Cards(int card[]) {
		this.currentNumCards = MAX_HAND_LENGTH;
		for(int i=0; i<MAX_HAND_LENGTH; i++) {
			this.card[i] = card[i];
		}
	}
	
	public void give(int card) {
		this.card[this.currentNumCards] = card;
		this.currentNumCards++;
	}
	
	public void play(String card) {
		this.play(random.card.DeckFunctions.getCard(card));
	}
	public void play(int card) {
		for(int i=0; i<this.currentNumCards; i++) {
			if(card == this.card[i]) {
				this.card[i] = this.card[this.currentNumCards];
				this.card[this.currentNumCards] = NOT_A_CARD;
				this.currentNumCards--;
				return;
			}
		}
		
		System.out.println("ERROR: card not found in player play card!");
		System.exit(1);
	}
	
	
	public boolean hasSuit(char suit, char trump) {
		for(int i=0; i<this.currentNumCards; i++) {
			if(Position.getEuchreSuit(this.card[i], trump) == suit ) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasCard(String card) {
		return this.hasCard(random.card.DeckFunctions.getCard(card));
	}
	public boolean hasCard(int card) {
		for(int i=0; i<this.currentNumCards; i++) {
			if(this.card[i] == card) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public int[] getHand() {
		int hand[] = new int[currentNumCards];
		for(int i=0; i<hand.length; i++) {
			hand[i] = card[i];
		}
		return hand;
	}
	
	
	public void giveTrick() {
		this.numTricksMade++;
	}
	
	public int getNumTricks() {
		return this.numTricksMade;
	}
	
	
	public void takeCard(int cardToTake) {
		for(int i=0; i<this.currentNumCards; i++) {
			if(this.card[i] == cardToTake) {
				this.card[i] = this.card[this.currentNumCards - 1];
				this.currentNumCards--;
				return;
			}
		}
		
		if(this.middleMan.isReadingReplay()) {
			//Turn off output file because the game just stopped:
			System.out.println("Couldn't find card to take away. (WTF).");
			System.out.println("Player: " + player);
			for(int i=0; i<this.currentNumCards; i++) {
				System.out.println(random.card.DeckFunctions.getCardString(this.card[i]));
			}
			System.out.println("Looking for: " + random.card.DeckFunctions.getCardString(cardToTake) + " or " + cardToTake);
			System.out.println("DONE AHHH");
			System.exit(1);
			
		} else {
			System.out.println("ERROR: (In PlayerModel) Couldn't find card to take away. (WTF).");
			System.exit(1);
		}
	}
}

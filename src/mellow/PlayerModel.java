package mellow;

public class PlayerModel {
	private String player;
	
	
	public static int NOT_A_CARD = -1;
	public static int MAX_HAND_LENGTH = 13;
	
	private int card[] = new int[MAX_HAND_LENGTH];
	private int currentNumCards;
	
	//private boolean saidMellow;
	//private boolean burntMellow;
	private int numBid;
	private int numTricksMade;
	
	private MellowServerMiddleMan middleMan;
	
	public void clearRoundVars() {
		for(int i=0; i<card.length; i++) {
			card[i] = NOT_A_CARD;
		}
		this.currentNumCards = 0;
		this.numBid = -1;
		this.numTricksMade = 0;
	}
	public PlayerModel(String player, MellowServerMiddleMan middle) {
		this.player = player;
		this.middleMan = middle;
	}
	
	public String getPlayerName() {
		return this.player;
	}
	
	public void give13Cards(int card[]) {
		this.currentNumCards = 13;
		for(int i=0; i<13; i++) {
			this.card[i] = card[i];
		}
	}
	
	public void give(int card) {
		this.card[this.currentNumCards] = card;
		this.currentNumCards++;
	}
	
	public void play(String card) {
		this.play(deck.DeckFunctions.getCard(card));
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
	}
	
	
	public boolean hasSuit(String suit) {
		suit = suit.toUpperCase();
		for(int i=0; i<this.currentNumCards; i++) {
			if(deck.DeckFunctions.getCardString(this.card[i]).substring(1).equals(suit) ) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasCard(String card) {
		return this.hasCard(deck.DeckFunctions.getCard(card));
	}
	public boolean hasCard(int card) {
		for(int i=0; i<this.currentNumCards; i++) {
			if(this.card[i] == card) {
				return true;
			}
		}
		
		return false;
	}
	
	///Only rule is following suit.
	public boolean isAllowedToPlay(String suit, int card) {
		suit = suit.toUpperCase();
		if(hasCard(card)) {
			if(hasSuit(suit)) {
				if(deck.DeckFunctions.getCardString(card).substring(1) == suit.toUpperCase()) {
					return true;
				} else {
					System.out.println("ERROR: player is reneging.");
					return false;
				}
			} else {
				return true;
			}
		} else {
			System.out.println("ERROR: player doesn't even have that card.");
			return false;
		}
	}
	
	public int[] getHand() {
		int hand[] = new int[currentNumCards];
		for(int i=0; i<hand.length; i++) {
			hand[i] = card[i];
		}
		return hand;
	}
	
	public void setMellow() {
		this.numBid = 0;
	}
	
	public void setNumBid(int num) {
		if(num < 0 || num >13) {
			System.out.println("ERROR: you could only bid between 0 and 13.");
			this.numBid = -1;
		} else {
			this.numBid = num;
		}
	}
	
	public int getNumBid() {
		return this.numBid;
	}
	
	public void giveTrick() {
		this.numTricksMade++;
	}
	
	public int getNumTricks() {
		return this.numTricksMade;
	}
	
	public boolean isMellow() {
		if(this.numBid == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isBurntMellow() {
		if(this.numBid == 0 && this.numTricksMade > 0) {
			return true;
		} else {
			return false;
		}
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
			//TODO
			System.out.println("AHHH");
			for(int i=0; i<this.currentNumCards; i++) {
				System.out.println(deck.DeckFunctions.getCardString(this.card[i]));
			}
			System.out.println("DONE AHHH");
			//this.middleMan.setOutputFileWriter(null);
			
		} else {
			System.out.println("ERROR: (In PlayerModel) Couldn't find card to take away. (WTF).");
		}
	}
}

package holdem;

import severalClientProject.MiniServer;
import deck.*;

//This class holds the official data about an AI in texas hold'em.
//This class is here so I don't have to make sure that the AI isn't cheating.

public class HoldEmPlayerLog {
	
	
		public static int NULL = -1;
	
		private HoldemServerMiddleMan middleMan;
		private MiniServer client;
		
		private String name;
	
		private int numCards=0;
		
		private int firstCard;
		private int secondCard;
		
		private int AmountOfChips;
		private int amountPutInPot = 0;
	
		private boolean allIn = false;
		
		//if the finish rank is null, then the player is still in tourney.
		private int finishedRank = NULL;
		
		//TODO: test this
		//This is true if the player's corpse is being used as dead dealer or dead small blind.
		private boolean isDeadToken = false;
		
		//true right after the player folds.
		private boolean folded = false;
		
		private boolean revealCards;
		
		public HoldEmPlayerLog(String name, int AmountOfChips, HoldemServerMiddleMan middleMan, MiniServer client) {
			this.name = name;
			this.AmountOfChips = AmountOfChips;
			this.firstCard = NULL;
			this.secondCard = NULL;
			this.revealCards = false;
			this.middleMan = middleMan;
			this.client = client;
		}
		
		//Returns the client this PlayerLog represents.
		public MiniServer getClient() {
			return this.client;
		}
		
		public String toString() {
			return name;
		}
		
		public String getName() {
			return name;
		}
		
		public void giveCard(int card) {
			if(numCards ==0) {
				firstCard = card;
				numCards++;
			} else if(numCards == 1) {
				secondCard = card;
				numCards++;
			} else {
				middleMan.sendMessageToGroup("Error: Player " + name + " has received " + (numCards+1) + " cards.");
			}
		}
		
		public int getFirstCard() {
			return this.firstCard;
		}
		
		public int getSecondCard() {
			return this.secondCard;
		}
		
		public int[] getCards() {
			int cards[] = {this.firstCard, this.secondCard};
			return cards;
		}
		
		public int getNumChips() {
			return this.AmountOfChips;
		}
		
		//For simplicity: this could only happen at the end of the round.
		private void unSetAllin() {
			allIn = false;
		}
		
		public void setAllin() {
			allIn = true;
		}
		
		public boolean isAllin() {
			return allIn;
		}
		
		//pre: the game is over or the current player has lost.
		public int getFinishRank() {
			return finishedRank;
		}
		
		//post: if the player is still playing, return true.
		public boolean stillInTournament() {
			if(finishedRank == NULL) {
				return true;
			} else {
				return false;
			}
		}
		
		public void endRound() {
			//middleMan.sendMessageToGroup("End round for " + name);
			this.firstCard = NULL;
			this.secondCard = NULL;
			this.revealCards = false;
			this.numCards = 0;
			this.amountPutInPot = 0;
			unSetAllin();
			if(stillInTournament()) {
				this.folded = false;
			} else {
				this.folded = true;
			}
		}
		
		//post: take chips from a player. If the Amount in the param is greater than or equal to
		//the amount the player has, the player is allin.
		public int takeChips(int Amount) {
			//Sanity check:
			assert(Amount >= 0);
			
			int amountTaken = 0;
			if(Amount >= this.AmountOfChips) {
				amountTaken = this.AmountOfChips;
				this.setAllin();
			} else {
				amountTaken = Amount;
			}
			
			this.AmountOfChips -= amountTaken;
			return amountTaken;
		}
		
		public void addAmountPutInPot(int Amount) {
			middleMan.sendMessageToGroup("**Adding " + Amount + " to pot for " + name);
			amountPutInPot += Amount;
		}
		
		public void takeAmountPutInPot(int Amount) {
			middleMan.sendMessageToGroup("**Taking " + Amount + " from amount bet from " + name);
			amountPutInPot -= Amount;
			
			if(amountPutInPot < 0) {
				middleMan.sendMessageToGroup("ERROR: amount put in pot is negative!");
				System.exit(1);
			}
			
		}
		public void getChipsBackFromPot() {
			this.AmountOfChips += this.amountPutInPot;
			this.amountPutInPot = 0;
		}
		
		public void giveChip(int amount) {
			this.AmountOfChips += amount;
		}
		
		
		public void fold() {
			folded = true;
		}
		
		public boolean isFolded() {
			return folded;
		}
		
		
		public void setDeadToken() {
			assert(!this.stillInTournament());
			isDeadToken = true;
		}
		
		public void unsetDeadToken() {
			isDeadToken = false;
		}
		
		public int getAmountPutInPot() {
			return amountPutInPot;
		}
		
		public void transferChipsToPot(int numChips) {
			if(AmountOfChips < numChips) {
				middleMan.sendMessageToGroup("ERROR: trying to transfer more chips than the player " + name + " has.");
				System.exit(1);
			}
			this.addAmountPutInPot(this.takeChips(numChips));
		}
		
		//post: returns true if the player lost but is still acting as a small blind or dealer.
		public boolean isDeadToken() {
			//sanity test:
			assert(!(this.stillInTournament() && isDeadToken));
			return isDeadToken;
		}
		
		public HoldEmPlayerLog makeHardCopy() {
			HoldEmPlayerLog copy = new HoldEmPlayerLog(this.name, this.AmountOfChips, this.middleMan, this.client);
			
			//copy.name = this.name;			
			copy.numCards = this.numCards;
			copy.firstCard = this.firstCard;
			copy.secondCard = this.secondCard;
			copy.revealCards = this.revealCards;
			
			copy.amountPutInPot = this.amountPutInPot;
			//copy.AmountOfChips;
			copy.allIn = this.allIn;
			copy.finishedRank = this.finishedRank;
			copy.isDeadToken = this.isDeadToken;
			copy.folded = this.folded;
			
			return copy;
		}
		
		public HoldEmPlayerLog makeHardCopyHideCards() {
			HoldEmPlayerLog copy = new HoldEmPlayerLog(this.name, this.AmountOfChips, this.middleMan, this.client);
			
			//copy.name = this.name;			
			copy.numCards = this.numCards;
			copy.firstCard = NULL;
			copy.secondCard = NULL;
			
			copy.amountPutInPot = this.amountPutInPot;
			//copy.AmountOfChips;
			copy.allIn = this.allIn;
			copy.finishedRank = this.finishedRank;
			copy.isDeadToken = this.isDeadToken;
			copy.folded = this.folded;
			
			return copy;
		}
		
		public void takeOutOfTournament(int finishRank) {
			//sanity test
			assert(stillInTournament());
			//End sanity test
			
			if(this.AmountOfChips > 0) {
				middleMan.sendMessageToGroup("Warning: player being taken out of tourney has chips.");
			}
			if(this.amountPutInPot > 0) {
				middleMan.sendMessageToGroup("Warning: amount bet in pot is greater than 0 as we are taking someone out of tournament.");
			}
			this.allIn = false;
			this.folded = true;
			this.finishedRank = finishRank;
		}
		
		
		public String getCardString() {
			String ret = DeckFunctions.getCardString(firstCard) + " " + DeckFunctions.getCardString(secondCard);
			return ret;
		}
		
		public boolean playerCouldStillWinPot() {
			if(this.stillInTournament() && this.folded == false) {
				return true;
			} else {
				return false;
			}
		}
		
		public void revealCardsToEveryone() {
			this.revealCards = true;
		}
		
		public boolean cardsAreRevealed() {
			return this.revealCards;
		}
		
		public boolean isUnfolded() {
			if(this.stillInTournament() && this.isFolded() == false) {
				return true;
			} else {
				return false;
			}
		}
}

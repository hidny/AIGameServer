package holdem;

import random.DeckFunctions;

public class HandComparer {

	/**
	 * In the card game poker, a hand consists of five cards and are ranked, from lowest to highest, in the following way:

High Card: Highest value card.
One Pair: Two cards of the same value.
Two Pairs: Two different pairs.
Three of a Kind: Three cards of the same value.
Straight: All cards are consecutive values.
Flush: All cards of the same suit.
Full House: Three of a kind and a pair.
Four of a Kind: Four cards of the same value.
Straight Flush: All cards are consecutive values of same suit.
Royal Flush: Ten, Jack, Queen, King, Ace, in same suit.
The cards are valued in the order:
2, 3, 4, 5, 6, 7, 8, 9, 10, Jack, Queen, King, Ace.

If two players have the same ranked hands then the rank made up of the highest value wins; for example, a pair of eights beats a pair of fives (see example 1 below). But if two ranks tie, for example, both players have a pair of queens, then highest cards in each hand are compared (see example 4 below); if the highest cards tie then the next highest cards are compared, and so on.

Consider the following five hands dealt to two players:

Hand	 	Player 1	 	Player 2	 	Winner
1	 	5H 5C 6S 7S KD
Pair of Fives
 	2C 3S 8S 8D TD
Pair of Eights
 	Player 2
2	 	5D 8C 9S JS AC
Highest card Ace
 	2C 5C 7D 8S QH
Highest card Queen
 	Player 1
3	 	2D 9C AS AH AC
Three Aces
 	3D 6D 7D TD QD
Flush with Diamonds
 	Player 2
4	 	4D 6S 9H QH QC
Pair of Queens
Highest card Nine
 	3D 6D 7H QD QS
Pair of Queens
Highest card Seven
 	Player 1
5	 	2H 2D 4C 4D 4S
Full House
With Three Fours
 	3C 3D 3S 9S 9D
Full House
with Three Threes
 	Player 1
The file, poker.txt, contains one-thousand random hands dealt to two players. Each line of the file contains ten cards (separated by a single space): the first five are Player 1's cards and the last five are Player 2's cards. You can assume that all hands are valid (no invalid characters or repeated cards), each player's hand is in no specific order, and in each hand there is a clear winner.

How many hands does Player 1 win?

	 */
	
	//GREAT NEWS: I checked the number of combo of royal flush, straight flush... to high pair against:
	//http://en.wikipedia.org/wiki/List_of_poker_hands and I got the same number of combinations!
	//That means I can only have made a mistake by comparing two hands of the same type (like 3 of a kind...).
	
	public static void main(String[] args) {
		printPokerCombInOrder(5, 6);
		
	}
	
	public static void printPokerCombInOrder(int minRank, int maxRank) {
		int hand[] = new int[5];
		
		int answerHand[][] = new int[2598960][5];
		
		double maxValue = maxRank * Math.pow(13, 5);
		double minValue = minRank * Math.pow(13, 5);
		
		System.out.println("max Value: " + minValue);
		System.out.println("min Value: " + minValue);
		boolean array[] = new boolean[52];
		
		for(int i=0; i<5; i++) {
			array[i] = true;
		}
		
		int stength;
		
		int numAnswers = 0;
		while(array != null) {
			
			hand = getHand(array);
			
			stength = getHandStrength(hand);
			if(stength >= minValue && stength <= maxValue) {
				answerHand[numAnswers] = hand;
				numAnswers++;
			}
			
			array = PokerUtilityFunctions.getNextCombination(array);
		}
		
		System.out.println("Number of answer: " + numAnswers);
		
		//TODO: faster sort function than O(n^2) please?
		//TODO 2: only calc hand strength once.
		System.out.println("Sort the answers:");
		for(int i=0; i<numAnswers; i++) {
			for(int j=i; j<numAnswers; j++) {
				if(getHandStrength(answerHand[i]) > getHandStrength(answerHand[j])) {
					swap(i, j, answerHand);
				}
			}
		}
		System.out.println("print answers from weakest to strongest:");
		for(int i=0; i<numAnswers; i++) {
			for(int j=0; j<5; j++) {
				System.out.print(DeckFunctions.getCardString(answerHand[i][j]) + " ");
			}
			System.out.println();
		}
	}
	
	public static void swap(int i, int j, int answerHand[][]) {
		int temp[] = answerHand[i];
		answerHand[i] = answerHand[j];
		answerHand[j] = temp;
	}
	
	
	public static int[] getHand(boolean array[]) {
		int card[] = new int[5];
		int numFound = 0;
		for(int i=0; i<52; i++) {
			if(array[i]) {
				card[numFound] = (i+1);
				numFound++;
			}
		}
		
		return card;
	}
	
	
	//get hand value.
	//double from 0 to 10
	//where up to 1 is A high
	//up to 2 is pair
	//up to 3 is 2 pair
	//up to 4 is straight
	//up to 5 is flush
	//up to 6 is 3 of a kind
	//up to 7 is full house
	//up to 8 is 4 of a kind
	//up to 9 is straight flush
	//up to 10 is royal flush. (why not)
	
	//The fractional part of the hand will allow us to compare 2 similar hands. ex A high = 1 but K high = 12/13

	public static int[] getBest5CardComboOutOf7Cards(int hand[], int communityCards[]) {
		int mergedHand[] = new int[7];
		for(int i=0; i<2; i++) {
			mergedHand[i] = hand[i];
		}
		for(int i=0; i<5; i++) {
			mergedHand[2 + i] = communityCards[i];
		}
		
		return getBest5CardComboOutOf7Cards(mergedHand);
	}
	
	//pre: hand length is 7
	public static int[] getBest5CardComboOutOf7Cards(int hand[]) {
		int currentBestvalue = -1;
		boolean combo[] = new boolean[7];
		for(int i=0; i<5; i++) {
			combo[i] = true;
		}
		
		int ret[] = null;
		
		int currentHandStrength;
		while(combo != null) {
			//currentHandStrength = getHandStrength
			currentHandStrength = getHandStrength(getFiveCardHand(hand, combo));
			
			if(currentHandStrength > currentBestvalue) {
				currentBestvalue = currentHandStrength;
				ret = getFiveCardHand(hand, combo);
			}
			combo = PokerUtilityFunctions.getNextCombination(combo);
		}
		
		return ret;
	}
	
	public static int getHandStrength(int hand[]) {
		int value;
		
		hand = sortHand(hand);
		//Testing duplicate cards:
		for(int i=0; i<hand.length; i++) {
			for(int j=i+1; j<hand.length; j++) {
				if(hand[i] == hand[j]) {
					System.out.println("Duplicate cards. Oops!");
					System.exit(1);
				}
			}
		}

		
		
		hand = sortHandStrongerPairsOnRight(hand);
		
		//End testing
		//for(int i=0; i<hand.length; i++) {
		//	System.out.print(getCardString(hand[i]) + " ");
		//}
		//System.out.println();
		
		if(isStraight(hand) && isFlush(hand) && DeckFunctions.getBaseNumber(hand[4]) == 'A' && DeckFunctions.getBaseNumber(hand[3]) == 'K') {
			value = 10;
		} else if(isStraight(hand) && isFlush(hand)) {
			//System.out.println("Straight flush:");
			value =  9;
		} else if(isFourOfAKind(hand)) {
			//System.out.println("Four of a kind:");
			value =  8;
		} else if(isFullHouse(hand)) {
			//System.out.println("Full house:");
			value =  7;
		} else if(isFlush(hand)) {
			//System.out.println("Flush:");
			value =  6;
		} else if(isStraight(hand)) {
			//System.out.println("Straight:");
			value =  5;
		} else if(isThreeOfAKind(hand)) {
			//System.out.println("Three of a kind:");
			value =  4;
		} else if(isTwoPair(hand))  {
			//System.out.println("Two pairs:");
			value =  3;
		} else if(isPair(hand)) {
			//System.out.println("Pair:");
			value = 2;
		} else {
			value = 1;
			//System.out.println(getBaseNumber(hand[4]) + " high.");
		}
		
		
		/*
		for(int i=0; i<hand.length; i++) {
			System.out.print(getCardString(hand[i]) + " ");
		}
		System.out.println();
		*/
		
		//resort cards for ace through 5:
		if(isStraight(hand) && DeckFunctions.getBaseNumber(hand[4]) == 'A' && DeckFunctions.getBaseNumber(hand[3]) == '5') {
			hand = putAceToLeftForLowStraight(hand);
		}
		
		//get final value:
		//get value by knowing the most significant card to least significant goes from right to left:
		for(int i=4; i>=0; i--) {
			value = 13*value + twoLowAHigh(hand[i]);
		}
		
		return value;
	}
	
	private static boolean isFlush(int sortedHand[]) {
		char startSuit = DeckFunctions.getSuit(sortedHand[0]);
		for(int i=1; i<sortedHand.length; i++) {
			if(DeckFunctions.getSuit(sortedHand[i]) != startSuit) {
				return false;
			}
		}
		
		return true;
	}
	
	//pre: hand is sorted with 2 on the left and ace on the right.
	private static boolean isStraight(int sortedHand[]) {
		int firstBaseNum = (sortedHand[0] % 13);
		//A base number = 1
		//step 1: check for A, 2, 3, 4, 5 straight:
		int lastBaseNum = (sortedHand[4] % 13);
		boolean isStraight = true;
		if(firstBaseNum == 2 && lastBaseNum == 1) {
			for(int i=1; i<sortedHand.length - 1; i++) {
				if((sortedHand[i] - i + 13)%13 != firstBaseNum) {
					isStraight = false;
					break;
				}
			}
			if(isStraight) {
				return true;
			}
		}
		
		//step 2 check for every other kind of straight: starting from 2 to 10:
		isStraight = true;
		
		if(firstBaseNum >= 2 && firstBaseNum <=10) {
			for(int i=1; i<sortedHand.length; i++) {
				if((sortedHand[i] - i + 13)%13 != firstBaseNum) {
					isStraight = false;
					break;
				}
			}
			
			if(isStraight) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
	}
	
	private static boolean isFourOfAKind(int sortedHand[]) {
		//check if first 4 cards are 4 of a kind:
		boolean fourOfAKind = true;
		
		for(int i=1; i<4; i++) {
			if(DeckFunctions.getBaseNumber(sortedHand[i]) !=  DeckFunctions.getBaseNumber(sortedHand[i-1])) {
				fourOfAKind = false;
				break;
			}
		}
		
		if(fourOfAKind) {
			return true;
		}
		//check if last 4 cards are 4 of a kind:
		fourOfAKind = true;
			
		for(int i=1; i<4; i++) {
			if(DeckFunctions.getBaseNumber(sortedHand[i]) !=  DeckFunctions.getBaseNumber(sortedHand[i+1])) {
				fourOfAKind = false;
				break;
			}
		}
		
		return fourOfAKind;
		
		
	}
	
	private static boolean isFullHouse(int sortedHand[]) {

		if(DeckFunctions.getBaseNumber(sortedHand[0]) == DeckFunctions.getBaseNumber(sortedHand[1]) && DeckFunctions.getBaseNumber(sortedHand[3]) == DeckFunctions.getBaseNumber(sortedHand[4])) {
			if(DeckFunctions.getBaseNumber(sortedHand[1]) == DeckFunctions.getBaseNumber(sortedHand[2]) || DeckFunctions.getBaseNumber(sortedHand[2]) == DeckFunctions.getBaseNumber(sortedHand[3])) {
				return true;
			}
		}
		
		return false;
	
	}
	
	private static boolean isThreeOfAKind(int sortedHand[]) {
		int numInArow = 1;
		
		for(int i=1; i<5; i++) {
			if(DeckFunctions.getBaseNumber(sortedHand[i]) == DeckFunctions.getBaseNumber(sortedHand[i-1])) {
				numInArow++;
				if(numInArow == 3) {
					return true;
				}
			} else {
				numInArow = 1;
			}
		}
		
		return false;
	
	}
	
	private static boolean isTwoPair(int sortedHand[]) {
		int numPairs = 0;
		
		for(int i=1; i<5; i++) {
			if(DeckFunctions.getBaseNumber(sortedHand[i]) == DeckFunctions.getBaseNumber(sortedHand[i-1])) {
				numPairs++;
				i++;
			}
		}
		
		if(numPairs == 2) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isPair(int sortedHand[]) {
		
		for(int i=1; i<5; i++) {
			if(DeckFunctions.getBaseNumber(sortedHand[i]) == DeckFunctions.getBaseNumber(sortedHand[i-1])) {
				return true;
			}
		}
		return false;
	}
	
	
	private static int[] getFiveCardHand(int hand[], boolean combo[]) {
		int ret[] = new int[5];
		int numCardsFound = 0;
		
		for(int i=0; i<hand.length; i++) {
			if(combo[i]) {
				ret[numCardsFound] = hand[i];
				numCardsFound++;
			}
		}
		
		if(numCardsFound != 5) {
			System.out.println("ALLERROR: get hand doesn't return 5 numbers!");
			System.exit(0);
		}
		return ret;
	}
	
	private static int[] sortHandStrongerPairsOnRight(int sortedHand[]) {
		if(isFourOfAKind(sortedHand)) {
			if(DeckFunctions.getBaseNumber(sortedHand[0]) == DeckFunctions.getBaseNumber(sortedHand[1])) {
				sortedHand = swapCard(sortedHand, 0, 4);
			}
		} else if(isFullHouse(sortedHand)) {
			if(DeckFunctions.getBaseNumber(sortedHand[1]) == DeckFunctions.getBaseNumber(sortedHand[2])) {
				sortedHand = swapCard(sortedHand, 0, 4);
				sortedHand = swapCard(sortedHand, 1, 3);
			}
		} else if(isThreeOfAKind(sortedHand)) {
			if(DeckFunctions.getBaseNumber(sortedHand[0]) == DeckFunctions.getBaseNumber(sortedHand[1])) {
				sortedHand = swapCard(sortedHand, 0, 3);
				sortedHand = swapCard(sortedHand, 1, 4);
			} else if(DeckFunctions.getBaseNumber(sortedHand[1]) == DeckFunctions.getBaseNumber(sortedHand[2])) {
				sortedHand = swapCard(sortedHand, 1, 4);
			}
		} else if(isTwoPair(sortedHand))  {
			if(DeckFunctions.getBaseNumber(sortedHand[3]) != DeckFunctions.getBaseNumber(sortedHand[4])) {
				sortedHand = swapCard(sortedHand, 2, 4);
			}
			
			if(DeckFunctions.getBaseNumber(sortedHand[1]) != DeckFunctions.getBaseNumber(sortedHand[2])) {
				sortedHand = swapCard(sortedHand, 0, 2);
			}
			
		} else if(isPair(sortedHand)) {
			for(int i=0; i<3; i++) {
				if(DeckFunctions.getBaseNumber(sortedHand[i]) == DeckFunctions.getBaseNumber(sortedHand[i+1])) {
					for(int j=i; j<3; j++) {
						sortedHand = swapCard(sortedHand, j, j+2);
					}
				}
			}
		}
		
		
		return sortedHand;
	}
		
	private static int[] swapCard(int card[], int i, int j) {
		int temp = card[i];
		card[i] = card[j];
		card[j] = temp;
		
		return card;
	}
	
	private static int twoLowAHigh(int card) {
		return (card+11)%13;
	}
	
	private static int[] sortHand(int hand[]) {
		int temp;
		
		for(int i=0; i<hand.length; i++) {
			for(int j=i+1; j<hand.length; j++) {
				if(twoLowAHigh(hand[j]) < twoLowAHigh(hand[i])) {
					temp = hand[i];
					hand[i] = hand[j];
					hand[j] = temp;
				}
			}
		}
		
		return hand;
		
	}
	
	private static int[] putAceToLeftForLowStraight(int hand[]) {
		int temp = hand[4];
		hand[4] = hand[3];
		hand[3] = hand[2];
		hand[2] = hand[1];
		hand[1] = hand[0];
		hand[0] = temp;
		
		return hand;
	}
	
	
}

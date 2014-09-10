package deck;

public class DeckFunctions {

	public static char getBaseNumber(int cardNum) {
		int cardBaseNum = (cardNum % 13);
		
		if(cardBaseNum == 0) {
			return 'K';
		} else if(cardBaseNum == 12) {
			return 'Q';
		} else if(cardBaseNum == 11) {
			return 'J';
		} else if(cardBaseNum == 10) {
			return 'T';
		}  else  if(cardBaseNum == 1) {
			return 'A';
		} else {
			return (char) ('0' + cardBaseNum);
		}
	}

	public static char getSuit(int cardNum) {
		if(cardNum <= 13) {
			return 'S';
		} else if(cardNum <= 26) {
			return 'H';
		} else if(cardNum <= 39) {
			return 'C';
		} else {
			return 'D';
		}
	}

	public static String getCardString(int num) {
		if(num>=1 && num <= 52) {
			return "" + getBaseNumber(num) + "" + getSuit(num);
		} else {
			//XX just means unknown.
			return "XX";
		}
	}

	public static int getCard(String card) {
		int ret=0;
		if(card.length() < 2) {
			return -1;
		}
		
		if(card.charAt(0) == 'A') {
			ret = 1;
		} else if(card.charAt(0) == 'K') {
			ret = 13;
		} else if(card.charAt(0) == 'Q') {
			ret = 12;
		} else if(card.charAt(0) == 'J') {
			ret = 11;
		} else if(card.charAt(0) == 'T') {
			ret = 10;
		} else {
			ret = card.charAt(0) - '0';
		}
		
		if(card.charAt(1) == 'S') {
			
		} else if(card.charAt(1) == 'H') {
			ret += 13;
		} else if(card.charAt(1) == 'C') {
			ret += 26;
		} else if(card.charAt(1) == 'D') {
			ret += 39;
		}
		
		return ret;
	}
	

	
	public static boolean isValidCard(int card) {
		if(card < 1 || card > 52) {
			return false;
		} else {
			return true;
		}
	}
}

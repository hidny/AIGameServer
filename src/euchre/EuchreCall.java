package euchre;

public class EuchreCall {

	
	private int callIndex;
	private int dealerIndex;
	private String trump;
	private boolean isAlone;
	private boolean callOnFirstRound;
	private boolean passes;
	
	public EuchreCall(int dealerIndex, int callIndex, String trump, boolean isAlone, boolean callOnFirstRound, boolean passes) {
		
		this.callIndex = callIndex;
		this.trump = trump.toUpperCase();
		this.isAlone = isAlone;
		this.callOnFirstRound = callOnFirstRound;
		this.dealerIndex = dealerIndex;
		this.passes = passes;
	}
	
	public int getCallIndex() {
		return callIndex;
	}

	public int getDealerIndex() {
		return dealerIndex;
	}

	public boolean isPassing() {
		return passes;
	}

	public char getTrump() {
		if(trump.equals("S")) {
			return 'S';
			
		} else if(trump.equals("C")) {
			return 'C';
			
		}else if(trump.equals("H")) {
			return 'H';
			
		}else if(trump.equals("D")) {
			return 'D';
			
		} else {
			System.out.println("ERROR: unknown trump in Euchre Call.");
			System.exit(1);
			return 'X';
		}
	}

	public boolean isAlone() {
		return isAlone;
	}

	public boolean isCallOnFirstRound() {
		return callOnFirstRound;
	}

	//This returns the relevant human readable string
	public String toString() {
		String ret = "";
		if(passes) {
			ret = "passes";
		} else {
			if(callOnFirstRound) {
				if(callIndex != dealerIndex) {
					ret = "orders up the dealer";
				} else {
					ret = "picks up";
				}
			} else {
				ret += "declares ";
				
				if(this.trump.startsWith("S")) {
					ret += "spades";
					
				} else if(this.trump.startsWith("C")) {
					ret += "clubs";
					
				} else if(this.trump.startsWith("H")) {
					ret += "hearts";
					
				} else if(this.trump.startsWith("D")) {
					ret += "diamonds";
					
				} else {
					System.out.println("ERROR: unknown trump declared. Trump string: " + this.trump);
					System.exit(1);
				}
			}
			
			if(isAlone) {
				ret += " and is going alone";
			}
		}
		//Check if the 
		
		ret += ".";
		
		return ret;
	}
	
	//This returns the easily machine readable string.
	public String getCmdString() {
		String ret = "";
		if(passes) {
			ret = "p";
		} else {
			ret = this.trump;
			
			if(isAlone) {
				ret += "A";
			}
		}
		
		return ret;
	}
	
}

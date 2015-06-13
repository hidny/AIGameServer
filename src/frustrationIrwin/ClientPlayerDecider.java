package frustrationIrwin;

//so far this class only has stubs.

//nad print all relevant variables.
//My plan is to make the client-side do the remembering.

import java.util.concurrent.Semaphore;

public class ClientPlayerDecider implements PlayerDecider {
	
	private static int UNENTERED_MOVE = -1;
	
	private int currentMove = UNENTERED_MOVE;
	private Semaphore semMoveAvailable = new Semaphore(0);
	private String name;

	
	//the way I made this: NUM_POS_PER_QUARTER >= NUM_PEGS
	//or else it won't work!
	public static int NUM_POS_PER_QUARTER = 7;
	public static int NUM_PEGS = 4;
	public static int NUM_POS_IN_BOARD = 4 * NUM_POS_PER_QUARTER;
	
	
	public void setMove(String move) {
		//this stops ais from spamming moves... that's ok though. :)
		
		move = move.toLowerCase();
		if(isInteger(move)) {
			currentMove = Integer.parseInt(move);
	
		} else if(move.startsWith("h")) {
			currentMove = Position.HOME;
		} else if(move.startsWith("s")) {
			currentMove = Position.SKIP;
		} else {
			currentMove = Position.ILLEGAL;
		}

		try {
			if (semMoveAvailable.availablePermits() < 1) {
				semMoveAvailable.release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public ClientPlayerDecider(String name) {
		this.name = name;
	}
	@Override
	public void start(String names[], int dealerIndex) {
		
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void updateMove(int roll, int move) {
		
	}

	@Override
	public int getMove(int roll, int[] pos, int playerIndex) {
		try {
			semMoveAvailable.acquire();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return currentMove;
		
	}
	
	
	private static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }
}

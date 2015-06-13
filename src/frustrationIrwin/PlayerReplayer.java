package frustrationIrwin;

import java.util.Scanner;

public class PlayerReplayer implements PlayerDecider {
	
	Scanner in;
	private String name;
	
	public PlayerReplayer(Scanner input, String name) {
		this.in = input;
		this.name = name;
	}
	
	public void start(String names[], int dealerIndex) {
		
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public void updateMove(int roll, int move) {
		
	}

	@Override
	public int getMove(int roll, int[] pos, int dealerIndex) {
		String ret = "";
		if(in.hasNextLine()) {
			ret = in.nextLine().trim();
		}
		
		
		if(ret == null || isInteger(ret) == false) {
			throw new RuntimeException("Couldn't read next move! (" + ret + ")");
		}
		
		return Integer.parseInt(ret);
	}
	

	private static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s.trim()); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }

}

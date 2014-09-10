package connectFour;

import java.io.File;
import java.util.Scanner;

//TODO: generalize this for all games.

public class TESTConnectFourReplayRunner {

	//This runs all the replays of connect four. This makes sure that connect 4 stays the same despite code changes.
	
	public static void main(String[] args) {
		Scanner input;
		int num = 0;
    	
		File dir = new File("connect4Commands");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
		   for (File child : directoryListing) {
			   num = gameUtils.gameFunctionUtils.getFileNum(child);
			   System.out.println("num: " + num);
		    	
			   try {
		    		input = new Scanner(new File("connect4Commands\\connect4Commands" + num + ".txt"));
		    		
		    		TESTConnectFour game = new TESTConnectFour();
			    	
		    		game.startGame(input, num);
			    	
			    	input.close();
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    	
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
			  System.out.println(" you did not find the folder... too bad.");
		  }
		  
		  System.out.println("Done running tests");

	}

}

package rockPaperScissors;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

//TODO: generalize this for all games.
//copied from connect four.

public class TESTRockPaperScissorsReplayRunner {

	//This runs all the replays of RPS. This makes sure that RPS stays the same despite code changes.
	private PrintWriter outputTest;
	
	public static void main(String[] args) {
		Scanner input;
		
		int num = 0;
    	
		File dir = new File("rockPaperScissorsCommands");
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		    	num = gameUtils.gameFunctionUtils.getFileNum(child);
		    	
		    	System.out.println("num: " + num);
		    	
		    	try {
		    		input = new Scanner(new File("rockPaperScissorsCommands\\rockPaperScissorsCommands" + num + ".txt"));
		    		
		    		TESTRockPaperScissors game = new TESTRockPaperScissors();
			    	
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
			  System.out.println("You did not find the folder... too bad.");
		  }
		  System.out.println("Done running tests");
	}
	
	public void recordOutput(String output) {
		outputTest.println(output);
	}
}

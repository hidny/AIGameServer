package frustrationIrwin;

import java.io.File;
import java.util.Scanner;

import gameUtils.*;

public class ReplayRunner {
	public static int NUM_SLOTS = 4;
	
	public static void main(String[] args) {
		PlayerReplayer playerReplayers[] = new PlayerReplayer[NUM_SLOTS];
		
		Scanner input;
		
		int num = 0;
		
		File dir = new File(Position.GAME_NAME + "Commands");
		
		String inputfilename;
		
		
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
		   for (File child : directoryListing) {
			   num = gameUtils.gameFunctionUtils.getFileNum(child);
		    	
			   try {
				   inputfilename = Position.GAME_NAME + "Commands\\" + Position.GAME_NAME + "Commands" + num + ".txt";
		    		input = new Scanner(new File(inputfilename));
		    		System.out.println("Playing: " + inputfilename);
		    		
		    		FrustrationServerMiddleMan middleMan = new FrustrationServerMiddleMan();
		    		
			    	middleMan.setOutputFileWriter( GameReplayPrinter.getNewReplayOuput(Position.GAME_NAME, num) );
		    		
			    	//Frustation Irwin version:
			    	input.nextLine();
			    	for(int i=0; i<NUM_SLOTS; i++) {
			    		playerReplayers[i] = null;
			    	}
			    	
			    	String currentScan = input.nextLine();
			    	//position A: Michael
			    	while(currentScan.startsWith("First") == false) {
			    		String playerArgs[] = currentScan.split(" ");
			    		char posLetter = playerArgs[1].charAt(0);
			    		String playerName = playerArgs[2];
			    		playerReplayers[(int)(posLetter - 'A')] = new PlayerReplayer(input, playerName);
			    		
			    		currentScan = input.nextLine();
			    	}
			    	
			    	String firstRoller = currentScan.split(" ")[2];
			    	random.RiggedDie rigged = new random.RiggedDie(input);
			    	int startingIndex = -1;
			    	for(int i=0; i<NUM_SLOTS; i++) {
			    		if(playerReplayers[i] != null && playerReplayers[i].getName().equals(firstRoller)) {
			    			startingIndex = i;
			    			break;
			    		}
			    	}
			    	
			    	
		    		Position.startFrustration(middleMan, playerReplayers, rigged, startingIndex);
		    		
		    		System.out.println("Finished playing: " + inputfilename);
		    		
			    	input.close();
		    	} catch (Exception e) {
		    		System.out.println("WARNING wtf: " + e.getMessage());
		    		e.printStackTrace();
		    		System.exit(1);
		    	}
		    	
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
			  System.out.println(" you did not find the folder... too bad.");
		  }
		  
		  System.out.println("Done running frustration irwin tests");
		 
	}

}

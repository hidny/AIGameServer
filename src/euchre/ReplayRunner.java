package euchre;

import java.io.File;
import java.util.Scanner;

import gameUtils.*;

public class ReplayRunner {

	
	public static void main(String[] args) {
		PlayerReplayer red[] = new PlayerReplayer[2];
		PlayerReplayer blue[] = new PlayerReplayer[2];
		
		Scanner input;
		int num = 0;

    	String playerNames[] = new String[4];
    	
		
		File dir = new File(Position.GAME_NAME + "Commands");
		
		String firstDealer;
		int indexDealer;
		String inputfilename;
		
		
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
		   for (File child : directoryListing) {
			   num = gameUtils.gameFunctionUtils.getFileNum(child);
		    	
			   try {
				    inputfilename = Position.GAME_NAME + "Commands\\" + Position.GAME_NAME + "Commands" + num + ".txt";
		    		input = new Scanner(new File(inputfilename));
		    		System.out.println("Playing: " + inputfilename);
		    		
		    		EuchreServerMiddleMan middleMan = new EuchreServerMiddleMan();
		    		EuchreVariation variation = null;
		    		
			    	middleMan.setOutputFileWriter( GameReplayPrinter.getNewReplayOuput(Position.GAME_NAME, num) );
		    		
			    	playerNames[0] = input.next();
			    	String check = input.next();
			    	if(check.equals("&") == false) {
			    		System.out.println("ERROR: & expected");
			    	}
			    	
			    	playerNames[1] = input.next();
			    	check = input.next();
			    	if(check.equals("vs") == false) {
			    		System.out.println("ERROR: vs expected");
			    	}
			    	
			    	playerNames[2] = input.next();
			    	check = input.next();
			    	if(check.equals("&") == false) {
			    		System.out.println("ERROR: & expected");
			    	}
			    	
			    	playerNames[3] = input.next();
			    	
			    	

			    	check = input.next();
			    	
			    	//If cmd file mentions a variation:
			    	if(check.equals("Variation:") == false) {
			    		System.out.println("ERROR: Variation: expected");
			    	}
			    	String variationString = input.next();
			    	variation = new EuchreVariation(variationString);
			    	
			    	check = input.next();
			    	
			    	if(check.equals("First") == false) {
			    		System.out.println("ERROR: \"First\" expected");
			    	}
			    	
			    	check = input.next();
			    	if(check.equals("dealer") == false) {
			    		System.out.println("ERROR: \"Dealer\" expected");
			    	}
			    	
			    	check = input.next();
			    	if(check.equals("is") == false) {
			    		System.out.println("ERROR: \"is\" expected");
			    	}
			    	
			    	firstDealer = input.next();
			    	
			    	
			    	red[0] = new PlayerReplayer(input, playerNames[0]);
			    	red[1] = new PlayerReplayer(input, playerNames[1]);
			    	blue[0] = new PlayerReplayer(input, playerNames[2]);
			    	blue[1] = new PlayerReplayer(input, playerNames[3]);
			    	input.nextLine();
			    	
			    	if(red[0].getName().equals(firstDealer)) {
			    		indexDealer = 0;
			    	} else if(red[1].getName().equals(firstDealer)) {
			    		indexDealer = 2;
			    	} else if(blue[0].getName().equals(firstDealer)) {
			    		indexDealer = 1;
			    	} else if(blue[1].getName().equals(firstDealer)) {
			    		indexDealer = 3;
			    	} else {
			    		System.out.println("ERROR: who's the first dealer.");
			    		indexDealer = -1;
			    	}
			    	
		    		Position.startEuchre(middleMan, variation, red, blue, indexDealer, new random.card.RiggedDeck(input));
		    		
		    		System.out.println("Finished playing: " + inputfilename);
		    		
			    	input.close();
		    	} catch (Exception e) {
		    		System.out.println("WARNING: " + e.getMessage());
		    	}
		    	
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
			  System.out.println(" you did not find the folder... too bad.");
		  }
		  
		  System.out.println("Done running mellow tests");
	}

}

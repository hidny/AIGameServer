package reversi;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import gameUtils.*;

public class ReplayRunner {

	
	public static void main(String[] args) {
		
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
		    		
		    		ReversiMiddleMan middleMan = new ReversiMiddleMan();
		    		
			    	middleMan.setOutputFileWriter( GameReplayPrinter.getNewReplayOuput(Position.GAME_NAME, num) );
		    		
			    	//TODO: put in more meaningful metadata.
			    	//also add comments when a line starts with ;
			    	//(For now) First line just says "Reversi:"
			    	input.nextLine();
			    	
			    	String player1String = input.next();
			    	String vs = input.next();
			    	if(vs.equals("vs") == false) {
			    		System.out.println("ERROR: unexpected input in line 1 for reversi.");
			    	}
			    	String player2String = input.next();
			    	
			    	PlayerReplayer dark = new PlayerReplayer(input, player1String);
			    	PlayerReplayer white = new PlayerReplayer(input, player2String);
			    	
			    	middleMan.setOutputFileWriter(new PrintWriter(Position.GAME_NAME + "ReplayOutput\\" + Position.GAME_NAME + "Output" + num + ".txt"));
			    	//TODO
			    	
		    		Position.startReversi(middleMan, dark, white);
		    		
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
		  
		  System.out.println("Done running reversi tests");
	}

}

package chess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChessReplayFolderRunner {

	public static void main(String[] args) {
		File replayFolderName = new File("C:\\Users\\Public\\Documents\\GameServerReplays\\chessCommands");
		
		File chessOutput = new File("C:\\Users\\Public\\Documents\\GameServerReplays\\chessReplayOutput");
		
		List<String> textFiles;
		
		String inputFile;
		String outputFile;
		
		System.out.println("Trying to run replay runner!");
		if(replayFolderName.isDirectory() && chessOutput.isDirectory()) {
			textFiles = getPGNFiles(replayFolderName.getPath());
			
			for(int i=0; i<textFiles.size(); i++) {
				inputFile = replayFolderName.getAbsolutePath() + File.separator + textFiles.get(i);
				outputFile = chessOutput.getAbsolutePath() + File.separator + textFiles.get(i);
				outputFile = outputFile.replace("test", "output");
				outputFile = outputFile.replace(".pgn", ".txt");
				
				System.out.println("Next file:");
				System.out.println(replayFolderName.getAbsolutePath() + File.separator + textFiles.get(i) + "");
				System.out.println(chessOutput.getAbsolutePath() + File.separator + textFiles.get(i) + "");
				
				ChessPGNFilePlayer.playPGNFile(inputFile, outputFile);
				
				System.out.println("In folder runner! Done with file!");
			}
			
			
		} else {
			System.out.println("ERROR: either chess Commands or chessReplayOutput isn\'t a folder.");
		}
	}
	
	
	
	//From stackoverflow:
	public static List<String> getPGNFiles(String directory) {
		  List<String> textFiles = new ArrayList<String>();
		  File dir = new File(directory);
		  for (File file : dir.listFiles()) {
		    if (file.getName().endsWith((".pgn"))) {
		      textFiles.add(file.getName());
		    }
		  }
		  return textFiles;
	}

}

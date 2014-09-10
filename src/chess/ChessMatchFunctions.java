package chess;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class ChessMatchFunctions {
	public static File REPLAY_FOLDER_NAME = new File("chessCommands");
	public static File CHESS_OUTPUT_FOLDER = new File("chessOutput");
	
	public static void startMatch(Player players[], ChessServerMiddleMan middleMan) {
		startMatch(players, "", middleMan);
	}
	
	//TODO: if I keep the output to server just like output to file, make a function that prints to both at once.
	
	//pre: players[0] is white, players[1] is black and the rest are observers.
	//TODO: 1) replace in with server listeners.
	//2 ) put logic of match inside another function...
	public static void startMatch(Player players[], String filename, ChessServerMiddleMan middleMan) {
		Position pos = new Position();
		Scanner in = new Scanner(System.in);
		ArrayList<String> PGNMoveList = new ArrayList<String>();
		String explicitMove;
		
		//For printing replays:
		String filenamePGN;
		String filenameOutput;
		if(filename.equals("")) {
			filenamePGN = getNextFilename(REPLAY_FOLDER_NAME);
		} else {
			filenamePGN = filename;
		}
		filenameOutput = filenamePGN.replace("test", "output");
		filenameOutput = filenameOutput.replace(".pgn", ".txt");
		
		int roundNum = 1;
		String shortHandMove;
		String SPACE = "          ";
		
		try {
			PrintWriter outputPGN = new PrintWriter(new File(REPLAY_FOLDER_NAME.getAbsolutePath() + File.separator + filenamePGN));
			PrintWriter output = new PrintWriter(new File(CHESS_OUTPUT_FOLDER  + File.separator + filenameOutput));
		
			outputPGN.println(getMatchHeaderInfo(players));
			output.println(getMatchHeaderInfo(players));
			
			middleMan.sendMessageToGroup(getMatchHeaderInfo(players));
			
			output.println(pos);
			middleMan.sendMessageToGroup("\n" + pos.toString());
			
			while(isGameOver(pos, PGNMoveList) == false) {
				
				outputPGN.print(roundNum + ". ");
				
				//White turn:
				explicitMove = players[0].getBestMove(pos.getFenString());
				shortHandMove = ChessPGNParser.convertExplicitNotativeToPGN(pos, explicitMove);
				PGNMoveList.add(shortHandMove);
				outputPGN.print(shortHandMove +  "  " + SPACE.substring(shortHandMove.length()));
				
				output.println("--------------------");
				//middleMan.sendMessageToGroup("--------------------");
				
				output.println("Move: " + shortHandMove);
				middleMan.sendMessageToGroup("Move: " + shortHandMove);
						
				pos = ChessMover.moveFromExplicitMoveString(pos, explicitMove);
				
				output.println(pos);
				middleMan.sendMessageToGroup("\n" + pos.toString());
				
				if(isGameOver(pos, PGNMoveList) == true) {
					break;
				}
				
				//Black turn:
				explicitMove = players[1].getBestMove(pos.getFenString());
				shortHandMove = ChessPGNParser.convertExplicitNotativeToPGN(pos, explicitMove);
				PGNMoveList.add(shortHandMove);
				outputPGN.println(shortHandMove +  "  ");
				output.println("--------------------");
				//middleMan.sendMessageToGroup("--------------------");
						
				output.println("Move: " + shortHandMove);
				middleMan.sendMessageToGroup("Move: " + shortHandMove);
				
				pos = ChessMover.moveFromExplicitMoveString(pos, explicitMove);
				
				output.println(pos);
				middleMan.sendMessageToGroup("\n" + pos.toString());
				
				roundNum++;
				outputPGN.flush();
				output.flush();
			}
			
			if(isDraw(pos, PGNMoveList)) {
				System.out.println("It\'s a draw by repetition!");
				middleMan.sendMessageToGroup("It\'s a draw by repetition!");
				
				outputPGN.println("1/2-1/2  ");
			} else if(isCheckMate(pos)) {
				if(pos.isWhiteTurn()) {
					System.out.println(players[1].getName() + " wins!");
					middleMan.sendMessageToGroup(players[1].getName() + " wins!");
					
					outputPGN.println("0-1  ");
				} else {
					System.out.println(players[0].getName() + " wins!");
					middleMan.sendMessageToGroup(players[0].getName() + " wins!");
					
					outputPGN.println("1-0  ");
				}
			} else if(isStalemate(pos)) {
				System.out.println("It\'s a stalemate!");
				middleMan.sendMessageToGroup("It\'s a stalemate!");
				
				outputPGN.println("1/2-1/2  ");
			}
			
			System.out.println("Final position:");
			middleMan.sendMessageToGroup("Final position:");
			
			System.out.println(pos);
			System.out.println(pos.toString());
			middleMan.sendMessageToGroup("\n" + pos.toString());
			
			output.println("--------------------");
			output.println("End of game");
			output.println("--------------------");
			output.println("--------------------");
			output.println("");
			output.println("DONE!");
			
			//middleMan.sendMessageToGroup("--------------------");
			middleMan.sendMessageToGroup("End of game");
			//middleMan.sendMessageToGroup("--------------------");
			//middleMan.sendMessageToGroup("--------------------");
			//middleMan.sendMessageToGroup("");
			//middleMan.sendMessageToGroup("DONE!");
			
			outputPGN.close();
			output.close();

			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static boolean isGameOver(Position pos, ArrayList<String> moveList) {
		if(isCheckMate(pos)  || isStalemate(pos) || isDraw(pos, moveList) )  {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isCheckMate(Position pos) {
		if(LegalMoveFetcher.getLegalMoveListPlusCheckDetails(pos) == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isStalemate(Position pos) {
		if(LegalMoveFetcher.getLegalMoveListPlusCheckDetails(pos).size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isDraw(Position pos, ArrayList<String> PGNMoveList) {
		Position replay = new Position();
		int numRepeats = 0;
		
		if(replay.getFenString().equals(pos.getFenString())) {
			numRepeats++;
		}
		
		for(int i=0; i<PGNMoveList.size(); i++) {
			if(replay.getFenString().equals(pos.getFenString())) {
				numRepeats++;
			}
			replay = ChessPGNParser.moveWithPGNNotation(replay, PGNMoveList.get(i));
			
		}
		
		if(replay.getFenString().equals(pos.getFenString()) == false) {
			System.out.println("WARNING: the board doesn't end up in the correct position.");
		}
		
		if(numRepeats == 3) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getMatchHeaderInfo(Player players[]) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		
		String ret = "[Event \"Internet Project Chess\"]" + "\n" +
					"[Date \""+ dateFormat.format(date) + "\"]" + "\n" +
					"[White \"" + players[0].getName() + "\"]" + "\n" + 
					"[Black \"" + players[1].getName() + "\"]";
		
		return ret;
	}
	
	public static String getNextFilename(File REPLAY_FOLDER_NAME) {
		List<String> pgnFiles = getPGNFiles(REPLAY_FOLDER_NAME.getAbsolutePath());
		File nextTestCase;
		for(int i=0; i<pgnFiles.size(); i++) {
			//replayFolderName.getAbsolutePath() + File.separator + textFiles.get(i);
			nextTestCase = new File(REPLAY_FOLDER_NAME.getAbsoluteFile() + File.separator + "test" + (i+1) + ".pgn");
			if(nextTestCase.exists() == false) {
				return nextTestCase.getName();
			}
		}
		
		return "test" + (pgnFiles.size()+1) + ".pgn";
		
	}
	
	//TODO: this is copied code. Put it in common file
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

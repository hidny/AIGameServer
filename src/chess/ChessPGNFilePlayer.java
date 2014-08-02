package chess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
//NOTE: The relevant rule in the FIDE laws of chess is 9.2, which reads:
/*
The game is drawn, upon a correct claim by the player having the move, when the same position, for at least the third time (not necessarily by sequential repetition of moves)
a. is about to appear, if he first writes his move on his scoresheet and declares to the arbiter his intention to make this move, or
b. has just appeared, and the player claiming the draw has the move.
Positions as in (a) and (b) are considered the same, if the same player has the move, pieces of the same kind and color occupy the same squares, and the possible moves of all the pieces of both players are the same.
Positions are not [considered to be] the same if a pawn that could have been captured en passant can no longer be captured or if the right to castle has been changed. (FIDE 2005, Article 9.2)
*/


//TODO: make sure I don't come up with too many moves... somehow.

public class ChessPGNFilePlayer {
	public static boolean PRINTING_MOVE_LIST = false;
	
	public static void main(String args[]) {
		
		String DEFAULT_FILENAME = "Anand.pgn";
		String filename;
				
		if(args.length == 0) {
			
			//in = new Scanner(new File("samplePGNgameCommentTest.pgn"));
			//annand has some FEN notation games (games that don't start at the very start)
			//I implemented FEN notation handler!
			
			//done: anand, karpov.pgn, Fischer (good one), Carlsen (no #/checkmates)
			filename = DEFAULT_FILENAME;
			
		} else {
			filename = args[0];
		}
		
		
		playPGNFile(filename);
		
		
	}
	public static void playPGNFile(String inputFilename) {
		playPGNFile(inputFilename, "");
	}
	
	public static void playPGNFile(String inputFilename, String outputFilename) {
		Object temp[];
		try {
			PrintStream original = System.out;
			if(outputFilename.equals("") == false) {
				System.setOut(new PrintStream(outputFilename));
			}
			
			Scanner in = new Scanner(new File(inputFilename)); 
			
			boolean currentlyMultiLineComment = false;
			
			int moveNumToExpect = 1;
			
			Scanner lineReader;
			
			String currentLine;
			
			Position currentPos = new Position();
			String currentMove;
			String explicitMove;
			String fenString;
			
			String fenStringTest;
			
			while(in.hasNextLine()) {
				currentLine = in.nextLine();
				currentLine = currentLine.trim();
				if(currentLine.startsWith("[")) {
					System.out.println(currentLine);
					if(currentLine.toLowerCase().contains("fen ")) {
						if(currentLine.indexOf("\"") >= 0 && currentLine.lastIndexOf("\"") > currentLine.indexOf("\"") ) {
							fenString = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\""));
							System.out.println("FEN String: " + fenString);
							currentPos = new Position(fenString);
							moveNumToExpect = 1;
						}
					}
					
				} else {
				
					temp = removeComments(currentLine, currentlyMultiLineComment);
					currentLine = (String)temp[0];
					currentlyMultiLineComment = (boolean)temp[1];
					
					//get rid of good move/bad move comments:
					currentLine = currentLine.replace("?", "");
					currentLine = currentLine.replace("!", "");
					
					while(currentLine.contains(moveNumToExpect + ".")) {
						if(currentLine.endsWith(moveNumToExpect + ".")) {
							currentLine = currentLine.substring(0, currentLine.indexOf((moveNumToExpect + ".")));
							moveNumToExpect++;
						} else {
							//System.out.println(currentLine);
							//System.out.println(currentLine.indexOf((moveNum + ".")));
							currentLine.substring(0, 0);
							//System.out.println("Substring 0,0 works");
							//System.out.println(currentLine.indexOf(moveNum + "."));
							//System.out.println((moveNum + ".").length());
							
							currentLine = currentLine.substring(0, currentLine.indexOf((moveNumToExpect + "."))) + currentLine.substring(currentLine.indexOf(moveNumToExpect + ".") + (moveNumToExpect + ".").length());
							moveNumToExpect++;
						}
					}
					
					//Read each move one by one:
					//System.out.println(currentLine);
					lineReader = new Scanner(currentLine);
					//System.out.println("(DEBUG) Current line: " + currentLine);
					
					while(lineReader.hasNext()) {
						currentMove = lineReader.next();
						System.out.println(currentPos);
						System.out.println("--------------------");
						if(currentMove.equals("O-O-O")) {
							//System.out.println("Time for debug!");
						}
						
						//Check if game is over:
						//for some reason, the games I downloaded sometimes end with an integer.
						if(currentMove.equals("1-0") || currentMove.equals("0-1") || currentMove.equals("1/2-1/2") || isInteger(currentMove) || currentMove.equals("*")) {
							
							System.out.println("End of game");
							System.out.println("--------------------");
							System.out.println("--------------------");
							System.out.println();
							
							//end game.
							currentPos = new Position();
							moveNumToExpect = 1;
						
						//If game not over, keep updating the position:
						} else {
							System.out.println("Move: " + currentMove);
							
							if(LegalMoveFetcher.getLegalMoveList(currentPos) == null) {
								System.out.println("ERROR: my program expected a checkmate!");
							}
							explicitMove = ChessPGNParser.convertPGNToExplicitNotation(currentPos, currentMove);
							
							ChessPGNParser.convertExplicitNotativeToPGN(currentPos, explicitMove);
							
							sanityCheckIfInLegalMoveList(currentPos, explicitMove, inputFilename);
							
							sanityTestGetMoveBack(currentPos, currentMove, explicitMove);
							
							
							fenStringTest = currentPos.getFenString();
							//System.out.println(fenStringTest);
							currentPos = new Position(fenStringTest);
							currentPos = ChessMover.moveFromExplicitMoveString(currentPos, explicitMove);
							
							//System.out.println(currentPos.toString());
							
							
							//Testing the move list:
							if(PRINTING_MOVE_LIST) {
								ArrayList<String> moveList = LegalMoveFetcher.getLegalMoveListPlusCheckDetails(currentPos);
								if(moveList != null) {
									
									if(moveList.size() == 0) {
										System.out.println("Stalemate!");
									}
									System.out.println("List of moves:");
									for(int i=0; i<moveList.size(); i++) {
										System.out.println(moveList.get(i));
									}
								}
							}
							
							
						}
					}
					lineReader.close();
				}
				
			}
			
			//System.out.println(inputFilename + " has been read and all games were playable through my system!");
			System.out.println("DONE!");
			
			System.setOut(original);
			
			in.close();
		} catch(FileNotFoundException e) {
			System.out.println("File not found! (" + inputFilename + ")");
			e.printStackTrace();
		}
		
		
	}
	
	//pre: the comments were removed:
	//post: gets rid of the numbers and just leaves the moves.
	//This makes it easier for the computer to parse.
	public static Object[] removeNumbers(String currentLine, int moveNum) {
		
		while(currentLine.contains(moveNum + ".")) {
			if(currentLine.endsWith(moveNum + ".")) {
				currentLine = currentLine.substring(0, currentLine.indexOf((moveNum + ".")));
				moveNum++;
			} else {
				//System.out.println(currentLine);
				//System.out.println(currentLine.indexOf((moveNum + ".")));
				currentLine.substring(0, 0);
				//System.out.println("Substring 0,0 works");
				//System.out.println(currentLine.indexOf(moveNum + "."));
				//System.out.println((moveNum + ".").length());
				
				currentLine = currentLine.substring(0, currentLine.indexOf((moveNum + "."))) + currentLine.substring(currentLine.indexOf(moveNum + ".") + (moveNum + ".").length());
				moveNum++;
			}
		}
		Object array[] = {currentLine, moveNum};
		
		return array;
	}
	
	public static Object[] removeComments(String currentLine, boolean currentlyMultiLineComment) {
		
		//Dealing with the { comment:
		if(currentlyMultiLineComment) {
			if(currentLine.contains("}")) {
				if(currentLine.indexOf('}') + 1 <= currentLine.length()) {
					currentLine = currentLine.substring(currentLine.indexOf('}') + 1);
					currentLine = currentLine.trim();
				} else {
					currentLine = "";
				}
			} else {
				currentLine = "";
				currentlyMultiLineComment = true;
			}
		}
		
		while(currentLine.contains("{")) {
			if(currentLine.contains("}")) {
				currentlyMultiLineComment = false;
				
				if(currentLine.indexOf('}') + 1 <= currentLine.length()) {
					currentLine = currentLine.substring(0, currentLine.indexOf('{')) + currentLine.substring(currentLine.indexOf('}') + 1);
					currentLine = currentLine.trim();
				} else {
					currentLine = currentLine.substring(0, currentLine.indexOf('{'));
					currentLine = currentLine.trim();
				}
			} else {
				currentLine = currentLine.substring(0, currentLine.indexOf('{'));
				currentLine = currentLine.trim();
				currentlyMultiLineComment = true;
			}
		}
		//End of dealing with the {} comments
		
		if(currentLine.contains(";")) {
			currentLine =  currentLine.substring(0, currentLine.indexOf(';'));
			currentLine = currentLine.trim();
		}
			
		Object array[] = {currentLine, currentlyMultiLineComment};
		
		return array;
	}
	
	//Idea of sanity test:
	//1. check if move is in move list. 
	//2. check if + symbol is correct.
	//3. check if # symbol is correct.
	public static void sanityCheckIfInLegalMoveList(Position currentPos, String explicitMove, String filename) {
		ArrayList<String> moveList = LegalMoveFetcher.getLegalMoveListPlusCheckDetails(currentPos);
		
		String fromMyProg;
		String fromFile;
		for(int i=0; i<moveList.size(); i++) {
			//For Carlsen: (he has no # signs)  (moveList.get(i).contains("#") && moveList.get(i).contains(explicitMove.substring(0, explicitMove.length() - 1))) 
			
			if(moveList.get(i).equals(explicitMove)  ) {
				//good
				//System.out.println("Match found!");
				return;
				
				//Nielsen.pgn has no checkmate symbols.
			} else if(filename.equals("Nielsen.pgn") || filename.substring(filename.lastIndexOf('\\') + 1).equals("Carlsen 1423 games.pgn")) {
				if(moveList.get(i).replace('#', '+').equals(explicitMove)) {
					return;
				}
				
			//Tal gets check wrong too many times to be reliable:
			} else if(filename.equals("Tal.pgn")) {
				fromFile = explicitMove.replace("+", "").replace("#", "");
				fromMyProg = moveList.get(i).replace("+", "").replace("#", "");
				if(fromFile.equals(fromMyProg)) {
					return;
				}
			}
		}
		
		System.out.println("Filename: " + filename);
		System.out.println("ERROR: couldn't find move " + explicitMove);
		System.out.println("List of moves: ");
		for(int i=0; i<moveList.size(); i++) {
			System.out.println(moveList.get(i));
		}
		System.exit(1);
	}
	
	public static void sanityTestGetMoveBack(Position currentPos, String currentMove, String explicitMove) {
		String getMoveBack = ChessPGNParser.convertExplicitNotativeToPGN(currentPos, explicitMove);
		
		//Sanity test:
		//If it fails, CRASH and BURN!
		if(getMoveBack.equals(currentMove) == false) {
			System.out.println("WARNING: failed to convert move from PGN to explicit and back to PGN notation.");
			System.out.println("PGN move: " + currentMove);
			System.out.println("Explicit move: " + explicitMove);
			System.out.println("PGN move after reconversion: " + getMoveBack);
			//System.exit(1);
		} else {
			//System.out.println("DEBUG: TEST PASS");
		}
		
		currentPos = ChessMover.moveFromExplicitMoveString(currentPos, explicitMove);
	}
	
	public static boolean isInteger(String ret) {
		try {
			int i = Integer.parseInt(ret);
			i=i+1;
			return true;
		} catch(Exception e) {
			return false;
		}
	}
}

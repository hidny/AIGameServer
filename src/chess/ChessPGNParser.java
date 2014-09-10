package chess;

import java.util.ArrayList;

public class ChessPGNParser {
	
	public static Position moveWithPGNNotation(Position pos, String move) {
			String explicitMove = convertPGNToExplicitNotation(pos, move);
			
			String getMoveBack = convertExplicitNotativeToPGN(pos, explicitMove);
			
			//Sanity test:
			//If it fails, CRASH and BURN!
			if(getMoveBack.equals(move) == false) {
				System.out.println("ERROR: failed to convert move from PGN to explicit and back to PGN notation.");
				System.out.println("PGN move: " + move);
				System.out.println("Explicit move: " + explicitMove);
				System.out.println("PGN move after reconversion: " + getMoveBack);
				System.exit(1);
			}
			
			
			return ChessMover.moveFromExplicitMoveString(pos, explicitMove);
		}
		
		public static String convertPGNToExplicitNotation(Position pos, String move) {
			
			String end = "";
			String temp = move.trim();
			char piece = ' ';
			boolean isCapture;
			
			String origCoordinate = "";
			
			//get rid of capture symbol and put it back in later for easier parsing.
			if(temp.contains("x")) {
				isCapture = true;
				temp = temp.replace("x", "");
			} else {
				isCapture = false;
			}
			
			//Take care of check or checkmate symbol:
			if(temp.endsWith("+") || temp.endsWith("#")) {
				end = "" + temp.charAt(temp.length() -1);
				temp = temp.substring(0, temp.length() - 1);
			}
			
			//PGN files are a little inconsistent. Some don't have = signs.
			temp = insertEqualSignForPromotionIfApplicable(temp);
			
			//Take care of promotion:
			if(temp.contains("=")) {
				end = temp.substring(temp.indexOf('=')) + end;
				temp = temp.substring(0, temp.indexOf('='));
			}
			
			//handle castling:
			if(temp.startsWith("O-O")) {
				return temp + end;
			} else if(temp.startsWith("R") || temp.startsWith("N") || temp.startsWith("B") || temp.startsWith("Q") || temp.startsWith("K")) {
				piece = temp.charAt(0);
				temp = temp.substring(1);
			} else {
				//Piece defaults to pawn if it's not mentioned:
				piece = 'P';
			}
			
			//at this point we should be left with a 2 or 3 character PGN coordinates:
			
			if(temp.length() == 3 || temp.length() == 2) {
				//first of three characters is either a letter or number to disambiguate original piece.
				origCoordinate = getOriginalCord(pos, piece, temp);
			} else if(temp.length() == 4) {
				//weird pathological case:
				origCoordinate = temp.substring(0, 2);
			} else {
				System.out.println("ERROR: unexpected coordinate length");
				System.out.println("move: " + move);
				System.out.println("Var that's supposed to be a coordinate: " + temp);
				System.exit(1);
			}
			//final coordinate is the last 2 chars of temp:
			String finalCoordinate =  temp.substring(temp.length()-2, temp.length());;
			
			
			String ret;
			if(isCapture) {
				if(piece != 'P') {
					ret = piece + origCoordinate + "x" + finalCoordinate + end;
				} else {
					ret = origCoordinate + "x" + finalCoordinate + end;
				}
			} else {
				if(piece != 'P') {
					ret = piece + origCoordinate + "-" + finalCoordinate + end;
				} else {
					ret = origCoordinate + "-" + finalCoordinate + end;
				}
			}
			
			return ret;
		}
		
		public static ArrayList<String> getListOfMovesPGN(Position pos) {
			ArrayList<String> explicitMoveList = LegalMoveFetcher.getLegalMoveListPlusCheckDetails(pos);
			ArrayList<String> ret = new ArrayList<String>();
			
			for(int i=0; i < explicitMoveList.size(); i++) {
				ret.add(convertExplicitNotativeToPGN(pos, explicitMoveList.get(i)));
			}
			
			return ret;
		}
		
		////TESTED!
		public static String convertExplicitNotativeToPGN(Position pos, String explicitMove) {
			String end = "";
			String temp = explicitMove.trim();
			char piece = ' ';
			
			//if there's a capture, this string will be an x
			// and the x will be added to the return value.
			String captureString ="";
			
			//get rid of capture symbol and put it back in later for easier parsing.
			if(temp.contains("x")) {
				captureString = "x";
			} else {
				captureString = "";
			}
			
			
			//Take care of check or checkmate symbol:
			if(temp.endsWith("+") || temp.endsWith("#")) {
				end = "" + temp.charAt(temp.length() -1);
				temp = temp.substring(0, temp.length() - 1);
			}
			
			//Take care of promotion:
			if(temp.contains("=")) {
				end = temp.substring(temp.indexOf('=')) + end;
				temp = temp.substring(0, temp.indexOf('='));
			}
			
			//handle castling:
			if(temp.startsWith("O-O")) {
				return temp + end;
			} else if(temp.startsWith("R") || temp.startsWith("N") || temp.startsWith("B") || temp.startsWith("Q") || temp.startsWith("K")) {
				piece = temp.charAt(0);
				temp = temp.substring(1);
			} else {
				//Piece defaults to pawn if it's not mentioned:
				piece = 'P';
			}
			
			//get rid of the - or x in the middle of the explicit move string:
			temp = temp.replace("x", "");
			temp = temp.replace("-", "");
			
			
			//at this point we should be left with a 5 character coordinates:
			//like a2-a4 or b1-c3
			if(temp.length() != 4) {
				System.out.println("ERROR in convertExplicitNotationToPGN");
				System.out.println("Expected 4 character string for move details but got " + temp + ".");
				System.exit(1);
			}
			
			String destPos = temp.substring(2);
			String origPos = temp.substring(0, 2);
			
			if(piece == 'P') {
				if(captureString.equals("x")) {
					return  origPos.substring(0, 1) + captureString + destPos + end;
				} else {
					return destPos + end;
				}
				//handle piece coord from the minimal info given in PGN.
			} else {
				if(twoWaysForPieceToGetThere(pos, piece, destPos)) {
					if(twoWaysForPieceToGetThereSameColumn(pos, piece, origPos, destPos)) {
						if(twoWaysForPieceToGetThereSameRow(pos, piece, origPos, destPos)) {
							return piece + origPos.substring(0, 2) + captureString + destPos + end;
						} else {
							return piece + origPos.substring(1, 2) + captureString + destPos + end;
						}
						
					} else {
						return piece + origPos.substring(0, 1) + captureString + destPos + end;
					}
				} else {
					return piece  + captureString +  destPos + end;
				}
			}
		}
		
		//standardize moves from PGN files by forcing promotions to have an equal sign.
		public static String insertEqualSignForPromotionIfApplicable(String temp) {
			//if the move already has an = sign, don't add one:
			if(temp.contains("=")) {
				return temp;
			}
			
			//Check if it needs an equal sign by checking notation for a piece 
			//that isn't at the beginning of the move:
			String temp2 = temp.substring(1);
			
			if(temp2.indexOf('Q') > 0 || temp2.indexOf('B') > 0 || temp2.indexOf('N') > 0 || temp2.indexOf('R') > 0) {
				temp2 = temp2.replace("Q", "=Q");
				temp2 = temp2.replace("B", "=B");
				temp2 = temp2.replace("N", "=N");
				temp2 = temp2.replace("R", "=R");
				
				return temp.substring(0, 1) + temp2;
			} else {
				return temp;
			}
		}
		
		
		//get original cord from pngDesc and Pos. ex: pngDesc= "e4" or "bc3"
		//Important: there is NO special logic needed for en passant here!
		public static String getOriginalCord(Position pos, char piece, String pngDesc) {
			//convert empty to pawn just in case caller didn't specify pawn explicitly:
			if(piece == ' ') {
				piece = 'P';
			}
			char letterDest = pngDesc.charAt(pngDesc.length() - 2);
			int numDest = pngDesc.charAt(pngDesc.length() - 1) - '0';
			
			ArrayList<String> explicitMoveList = LegalMoveFetcher.getLegalMoveListPlusCheckDetails(pos);
			
			if(pngDesc.length() == 3) {
				boolean isLetterHint = isLetterCoord(pngDesc.charAt(0));
				
				for(int i=0; i<explicitMoveList.size(); i++) {
					if(getPieceBeingMoved(explicitMoveList.get(i)) == piece && explicitMoveList.get(i).contains("" +letterDest + "" + numDest) ) {
						if(isLetterHint) {
							char letter = pngDesc.charAt(0);
							for(int num=1; num<=8; num++) {
								//the letter and number of start can't be the same as the destination: (so skip that case!)
								if(letter == letterDest && num == numDest ) {
									continue;
								}
								
								if(explicitMoveList.get(i).contains("" +letter + "" + num)) {
									return "" +letter + "" + num;
								}
							}
						} else {
							int num = pngDesc.charAt(0) - '0';
							for(char letter='a'; letter<='h'; letter++) {
								//the letter and number of start can't be the same as the destination:
								if(letter == letterDest && num == numDest) {
									continue;
								}
								
								if(explicitMoveList.get(i).contains("" +letter + "" + num)) {
									return "" +letter + "" + num;
								}
							}
						}
					}
				}
				
			} else if(pngDesc.length() == 2) {
				for(int i=0; i<explicitMoveList.size(); i++) {
					if(getPieceBeingMoved(explicitMoveList.get(i)) == piece  && explicitMoveList.get(i).contains("" +letterDest + "" + numDest)) {
						if(piece == 'P') {
							return explicitMoveList.get(i).substring(0, 2);
						} else {
							return explicitMoveList.get(i).substring(1, 3);
						}
						
					}
				}
			} else {
				System.out.println("ERROR: the PNG description of the coordinate was expected to be only 2 or 3 characters long.");
				System.out.println("PNG Desc: " + pngDesc);
				System.exit(1);
			}
			
			return null;
		}
		
		
		
		//post: Check if explicit move is a pawn move.
		//If it's a pawn move, then it shouldn't start with R, N, B, Q, or K.
		//it should start with a lower case letter.
		public static boolean isPawnMove(String explicitMove) {
			//NOTE: castling seems to be covered here!
			if(explicitMove.charAt(0) >= 'A' && explicitMove.charAt(0) <= 'Z') {
				return false;
			} else {
				return true;
			}
		}
		
		//pre: move isn't a castling move.
		public static char getPieceBeingMoved(String move) {
			if(move.charAt(0) >= 'a' && move.charAt(0) <= 'h') {
				return 'P';
			} else {
				return move.charAt(0);
			}
		}
		
		public static boolean isLetterCoord(char input) {
			if(input >= '1' && input <= '8') {
				return false;
			} else {
				return true;
			}
			
		}
		
		//pre: if piece is pawn, then piece = 'P'
		public static boolean twoWaysForPieceToGetThere(Position pos, char piece, String destPos) {
			//ArrayList<String> list = LegalMoveFetcher.getLegalMoveList(pos);
			ArrayList<String> list = LegalMoveFetcher.getNaiveMoveList(pos);
			
			
			int numPieces = 0;
			
			for(int i=0; i<list.size(); i++) {
				
				if(list.get(i).startsWith("O-O")) {
					//ignore castling!
					
				} else if(piece == 'P' && isPawnMove(list.get(i)) ) {
					//e2-e4
					if(destPos.equals(list.get(i).substring(3, 5)) ) {
						numPieces++;
					}
				} else if(list.get(i).charAt(0) == piece && isPawnMove(list.get(i)) == false){
					//Nb1-c3
					if(destPos.equals(list.get(i).substring(4, 6))) {
						numPieces++;
					}
				}
			}
			
			if(numPieces > 1) {
				return true;
			} else if(numPieces == 1) {
				return false;
			} else {
				System.out.println("WARNING: no move gets to dest " +destPos +". Are you sure that's what you want.");
				System.exit(1);
				return false;
			}
		}
		
		public static boolean twoWaysForPieceToGetThereSameRow(Position pos, char piece, String origPos, String destPos) {
			//ArrayList<String> list = LegalMoveFetcher.getLegalMoveList(pos);
			ArrayList<String> list = LegalMoveFetcher.getNaiveMoveList(pos);
			
			int numPieces = 0;
			
			for(int i=0; i<list.size(); i++) {
				if(list.get(i).startsWith("O-O")) {
					//ignore castling!
					
				} else if(piece == 'P' && isPawnMove(list.get(i)) ) {
					//e2-e4
					if(destPos.equals(list.get(i).substring(3, 5)) && origPos.charAt(1) == list.get(i).charAt(1)  ) {
						numPieces++;
					}
				} else if(list.get(i).charAt(0) == piece && isPawnMove(list.get(i)) == false){
					//Nb1-c3
					if(destPos.equals(list.get(i).substring(4, 6))  && origPos.charAt(1) == list.get(i).charAt(2) ) {
						numPieces++;
					}
				}
			}
			
			if(numPieces > 1) {
				return true;
			} else if(numPieces == 1) {
				return false;
			} else {
				System.out.println("WARNING: no move gets to dest " +destPos +". Are you sure that's what you want.");
				System.exit(1);
				return false;
			}
		}
		
		public static boolean twoWaysForPieceToGetThereSameColumn(Position pos, char piece, String origPos, String destPos) {
			//ArrayList<String> list = LegalMoveFetcher.getLegalMoveList(pos);
			ArrayList<String> list = LegalMoveFetcher.getNaiveMoveList(pos);
			
			int numPieces = 0;
			
			for(int i=0; i<list.size(); i++) {
				
				if(list.get(i).startsWith("O-O")) {
					//ignore castling!
					
				} else if(piece == 'P' && isPawnMove(list.get(i)) ) {
					//e2-e4
					if(destPos.equals(list.get(i).substring(3, 5)) && origPos.charAt(0) == list.get(i).charAt(0)  ) {
						numPieces++;
					}
				} else if(list.get(i).charAt(0) == piece && isPawnMove(list.get(i)) == false){
					//Nb1-c3
					if(destPos.equals(list.get(i).substring(4, 6))  && origPos.charAt(0) == list.get(i).charAt(1) ) {
						numPieces++;
					}
				}
			}
			
			if(numPieces > 1) {
				return true;
			} else if(numPieces == 1) {
				return false;
			} else {
				System.out.println("WARNING: no move gets to dest " +destPos +". Are you sure that's what you want.");
				System.exit(1);
				return false;
			}
		}
		
		
}

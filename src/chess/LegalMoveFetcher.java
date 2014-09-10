package chess;

import java.util.ArrayList;

public class LegalMoveFetcher {
	
	public static ArrayList<String> getLegalMoveListPlusCheckDetails(Position pos) {
		ArrayList<String> ret = getLegalMoveList(pos);
		//if it's already checkmate, then stop.
		if(ret == null) {
			return null;
		}
		ArrayList<String> nextTurnMoveList;
		
		String currentPiece;
		char opponentColour;
		if(pos.isWhiteTurn()) {
			opponentColour = Position.BLACK;
		} else {
			opponentColour = Position.WHITE;
		}
		
		//check if a plus sign (meaning check) should be added at the end of the move:
		char opponentKingOrigLetterPos = ' ';
		int opponentKingOrigNumPos = -1;
		for(int num=8; num>=1; num--) {
			for(char letter='a'; letter<='h'; letter++) {
				currentPiece = pos.getPiece(letter, num);
				if(currentPiece.charAt(1) == 'K' && currentPiece.charAt(0) == opponentColour) {
					opponentKingOrigLetterPos = letter;
					opponentKingOrigNumPos = num;
					break;
				}
			}
		
		}
		
		//Check if there's a check: (so we can add + sign)
		Position temp;
		for(int i=0; i<ret.size(); i++) {
			temp = ChessMover.moveFromExplicitMoveString(pos, ret.get(i));
			nextTurnMoveList = getNaiveMoveList(ChessMover.moveWeird(temp, Position.NULL));
			
			//TODO: put in it's own function (use isSpaceTargettedByOpponent) 
			for(int j=0; j<nextTurnMoveList.size(); j++) {
				if(nextTurnMoveList.get(j).endsWith((char)opponentKingOrigLetterPos + "" + opponentKingOrigNumPos)) {
					//System.out.println("The move:" + ret.get(i));
					//System.out.println("Caused the opponent to get in check.");
					ret.set(i, ret.get(i) + "+");
					break;
				}
			}
			
		}
	
		//Check if the move causes checkmate:
		for(int i=0; i<ret.size(); i++) {
			//The move first has to cause a check:
			if(ret.get(i).endsWith("+")) {

				//System.out.println(ret.get(i));
				
				temp = ChessMover.moveFromExplicitMoveString(pos, ret.get(i));
				nextTurnMoveList = getLegalMoveList(temp);
				
				//By def of getLegalMoveList, we get a null if there's a checkmate:
				if(nextTurnMoveList == null) {
					ret.set(i, ret.get(i).substring(0, ret.get(i).length() - 1) + "#");
				}
			}
			
		}
		
		return ret;
	}
	
	
	//returns list of legal move the current player could make:
			//returns null on checkmate
			//returns list of 0 on stalemate.
			//doesn't add check details:
			//	Doesn't add a plus sign at the end of a move.
			//  Doesn't add a # sign at the end of a move.
	public static ArrayList<String> getLegalMoveList(Position pos) {
		ArrayList<String> ret = getNaiveMoveList(pos);
		ArrayList<String> nextTurnMoveList;
		
		String currentPiece;
		char kingOrigLetterPos = '0';
		int kingOrigNumPos = -1;
		
		char playerColour;
		if(pos.isWhiteTurn()) {
			playerColour = Position.WHITE;
		} else {
			playerColour = Position.BLACK;
		}
		
		for(int num=8; num>=1; num--) {
			for(char letter='a'; letter<='h'; letter++) {
				currentPiece = pos.getPiece(letter, num);
				if(currentPiece.charAt(1) == 'K' && currentPiece.charAt(0) == playerColour) {
					kingOrigLetterPos = letter;
					kingOrigNumPos = num;
					break;
				}
			}
		
		}
		
		//Sanity check:
		if(kingOrigNumPos == -1) {
			System.out.println("ERROR: WHAT THE HELL?? Where did the king go?");
			System.out.println("??");
			System.exit(1);
		}
		
		
		boolean kingInCheck = isSpaceTargettedByOpponent(pos, kingOrigLetterPos, kingOrigNumPos);
		
		//CASTLING
		ret.addAll(getCastlingMoves(pos, kingInCheck));
		
		//Remove moves because of risk of check
		for(int i=0; i<ret.size(); i++) {
			nextTurnMoveList = getNaiveMoveList(ChessMover.moveFromExplicitMoveString(pos, ret.get(i)));
			for(int j=0; j<nextTurnMoveList.size(); j++) {
				if(ret.get(i).startsWith("K") == false) {
					if(nextTurnMoveList.get(j).endsWith((char)kingOrigLetterPos + "" + kingOrigNumPos)) {
						//System.out.println("The next move:" + nextTurnMoveList.get(j));
						//System.out.println("Caused " + ret.get(i) + " to be removed of the list of legal moves.");
						ret.remove(i);
						i--;
						break;
					}
				} else {
					
					//after a king move, the king's position is changed to wherever it moved to:
					if(nextTurnMoveList.get(j).endsWith(ret.get(i).substring(4))) {
						//System.out.println("The next king move:" + nextTurnMoveList.get(j));
						//System.out.println("Caused " + ret.get(i) + " to be removed of the list of legal moves.");
						ret.remove(i);
						i--;
						break;
					}
				}
			}
		}
		
		//Determine if there's a stalemate or checkmate.
		if(ret.size() == 0) {
			nextTurnMoveList = getNaiveMoveList(ChessMover.moveWeird(pos, Position.NULL));
			
			if(kingInCheck) {
				return null;
			} else {
				System.out.println(playerColour + " can't move! It's a draw!");
			}
		}
		
		//Add legal moves for every type of promotion
		String tempPawnMove;
		for(int i=0; i<ret.size(); i++) {
			//If a pawn move leads to promotion:
			if(ret.get(i).length() == 5 && (ret.get(i).endsWith("8") || ret.get(i).endsWith("1")) ) {
				tempPawnMove = ret.get(i);
				ret.remove(i);
				ret.add(tempPawnMove + "=Q");
				ret.add(tempPawnMove + "=R");
				ret.add(tempPawnMove + "=B");
				ret.add(tempPawnMove + "=N");
				i--;
			}
		}
		
		return ret;
	}
	
	
	//returns true if position could be targeted by opposing player:
	public static boolean isSpaceTargettedByOpponent(Position pos, char letterPos, int numPos) {
		ArrayList<String> nextTurnMoveList = getNaiveMoveList(ChessMover.moveWeird(pos, Position.NULL));
		for(int j=0; j<nextTurnMoveList.size(); j++) {
			if(nextTurnMoveList.get(j).endsWith((char)letterPos + "" + numPos)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	//doesn't handle pins or promotions or castling
	//and only returns moves in the format of Ne3-f1
	//Char for piece, letter, num, dash char for dest piece then num.
	//[R + N + B + Q + K + BLANK] [a-h][1-8][x + -][a-h][1-8]
	public static ArrayList<String> getNaiveMoveList(Position pos) {
		ArrayList<String> ret = new ArrayList<String>(50);
		char playerColour;
		char opponentColour;
		if(pos.isWhiteTurn()) {
			playerColour = Position.WHITE;
			opponentColour = Position.BLACK;
		} else {
			playerColour = Position.BLACK;
			opponentColour = Position.WHITE;
		}
		
		String currentPiece;
		
		for(int num=8; num>=1; num--) {
			for(char letter='a'; letter<='h'; letter++) {
				currentPiece = pos.getPiece(letter, num);
				if(currentPiece.charAt(0) == playerColour) {
					//System.out.println(letter + "" + num + ": " + getPiece(letter, num) + ":");
					
					//at this point, the piece should be movable
					if(currentPiece.charAt(1) == 'N') {
						//System.out.println("kNight!");
						//make L shape
						//Top right 1:
						if(num+2 <=8 && letter+1<='h' && pos.getPiece((char)(letter+1), num+2).charAt(0) != playerColour) {
							//System.out.println("Could move top right!");
							ret.add(ChessMover.createMove(pos, 'N', (char)letter, num, (char)(letter+1), num+2));
						}
						
						if(num+1 <=8 && letter+2<='h' && pos.getPiece((char)(letter+2), num+1).charAt(0) != playerColour) {
							//System.out.println("Could move top right 2!");
							ret.add(ChessMover.createMove(pos, 'N', (char)letter, num, (char)(letter+2), num+1));
						}
						
						if(num-1 >=1 && letter+2<='h' && pos.getPiece((char)(letter+2), num-1).charAt(0) != playerColour) {
							//System.out.println("Could move bottom right 1!");
							ret.add(ChessMover.createMove(pos, 'N', (char)letter, num, (char)(letter+2), num-1));
						}
						
						if(num-2 >=1 && letter+1<='h' && pos.getPiece((char)(letter+1), num-2).charAt(0) != playerColour) {
							//System.out.println("Could move bottom right 2!");
							ret.add(ChessMover.createMove(pos, 'N', (char)letter, num, (char)(letter+1), num-2));
						}
						
						if(num+2 <=8 && letter-1>='a' && pos.getPiece((char)(letter-1), num+2).charAt(0) != playerColour) {
							//System.out.println("Could move top left!");
							ret.add(ChessMover.createMove(pos, 'N', (char)letter, num, (char)(letter-1), num+2));
						}
						
						if(num+1 <=8 && letter-2>='a' && pos.getPiece((char)(letter-2), num+1).charAt(0) != playerColour) {
							//System.out.println("Could move top left 2!");
							ret.add(ChessMover.createMove(pos, 'N', (char)letter, num, (char)(letter-2), num+1));
						}
						
						if(num-1 >=1 && letter-2>='a' && pos.getPiece((char)(letter-2), num-1).charAt(0) != playerColour) {
							//System.out.println("Could move bottom left 1!");
							ret.add(ChessMover.createMove(pos, 'N', (char)letter, num, (char)(letter-2), num-1));
						}
						
						if(num-2 >=1 && letter-1>='a' && pos.getPiece((char)(letter-1), num-2).charAt(0) != playerColour) {
							//System.out.println("Could move bottom left 2!");
							ret.add(ChessMover.createMove(pos, 'N', (char)letter, num, (char)(letter-1), num-2));
						}
						
					}
					
					if(currentPiece.charAt(1) == 'P') {
						int direction = 1;
						if(playerColour == Position.WHITE) {
							direction = 1;
						} else {
							direction = -1;
						}
						
						//check if pawn could capture:
						if(letter+1<='h' && pos.getPiece((char)(letter+1), num+direction).charAt(0) == opponentColour) {
							//System.out.println("could capture with pawn!");
							ret.add(ChessMover.createMove((char)letter, num, (char)(letter+1), num+direction, true));
						}
						
						if(letter-1>='a' && pos.getPiece((char)(letter-1), num+direction).charAt(0) == opponentColour) {
							//System.out.println("could capture with pawn!");
							ret.add(ChessMover.createMove((char)letter, num, (char)(letter-1), num+direction, true));
						}
						
						if(pos.getEnPassantCol() != Position.NULL) {
							if(num == 5 && playerColour == Position.WHITE) {
								if(letter + 1 == pos.getEnPassantCol() ) {
									//System.out.println("White could enpassent!");
									ret.add(ChessMover.createMove((char)letter, num, (char)(letter+1), num+direction, true));
								} else if(letter - 1 == pos.getEnPassantCol()) {
									//System.out.println("White could enpassent!");
									ret.add(ChessMover.createMove((char)letter, num, (char)(letter-1), num+direction, true));
								}
							} else if(num == 4 && playerColour == Position.BLACK) {
								if(letter + 1 == pos.getEnPassantCol() ) {
									//System.out.println("Black could enpassent!");
									ret.add(ChessMover.createMove((char)letter, num, (char)(letter+1), num+direction, true));
								} else if(letter - 1 == pos.getEnPassantCol()) {
									//System.out.println("Black could enpassent!");
									ret.add(ChessMover.createMove((char)letter, num, (char)(letter-1), num+direction, true));
								}
							}
						}
						
						//
						if(pos.getPiece(letter, num+direction).charAt(0) == ' ') {
							//System.out.println("Can move pawn");
							ret.add(ChessMover.createMove((char)letter, num, (char)(letter), num+direction, false));
							if(num == 2 && playerColour == Position.WHITE) {
								if(pos.getPiece(letter, num+2*direction).charAt(0) == ' ') {
									//System.out.println("White could move 2");
									ret.add(ChessMover.createMove((char)letter, num, (char)(letter), num+2*direction, false));
								}
							} else if(num == 7 && playerColour == Position.BLACK) {
								if(pos.getPiece(letter, num+2*direction).charAt(0) == ' ') {
									//System.out.println("Black could move 2");
									ret.add(ChessMover.createMove((char)letter, num, (char)(letter), num+2*direction, false));
								}
							}
						}
					}

					if(currentPiece.charAt(1) == 'B' || currentPiece.charAt(1) == 'Q') {
						
						//  /
						for(int i=1; letter + i<= 'h' && num + i<= 8; i++) {
							if(pos.getPiece((char)(letter + i), num + i).equals(Position.EMPTY)) {
								//System.out.println("Bishop/Queen could move up right " + i);
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter+i), (num+i), false));
								
							} else if(pos.getPiece((char)(letter + i), num + i).charAt(0) == opponentColour ){
								//System.out.println("Bishop/Queen could move up right " + i + " and capture");
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter+i), (num+i), true));
								break;
							} else {
								break;
							}
						}
						
						for(int i=1; letter - i>= 'a' && num - i >= 1; i++) {
							if(pos.getPiece((char)(letter - i), num - i).equals(Position.EMPTY)) {
								//System.out.println("Bishop/Queen could move down left " + i);
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter-i), (num-i), false));
							} else if(pos.getPiece((char)(letter - i), num - i).charAt(0) == opponentColour ){
								//System.out.println("Bishop/Queen could move down left " + i + " and capture");
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter-i), (num-i), true));
								break;
							} else {
								break;
							}
						}
						
						for(int i=1; letter - i>= 'a' && num + i<= 8; i++) {
							if(pos.getPiece((char)(letter - i), num + i).equals(Position.EMPTY)) {
								//System.out.println("Bishop/Queen could move up left " + i);
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter-i), (num+i), false));
							} else if(pos.getPiece((char)(letter - i), num + i).charAt(0) == opponentColour ){
								//System.out.println("Bishop/Queen could move up left " + i + " and capture");
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter-i), (num+i), true));
								break;
							} else {
								break;
							}
						}
						
						for(int i=1; letter + i<= 'h' && num - i >= 1; i++) {
							if(pos.getPiece((char)(letter + i), num - i).equals(Position.EMPTY)) {
								//System.out.println("Bishop/Queen could move down right " + i);
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter+i), (num-i), false));
							} else if(pos.getPiece((char)(letter + i), num - i).charAt(0) == opponentColour ){
								//System.out.println("Bishop/Queen could move down right " + i + " and capture");
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter+i), (num-i), true));
								break;
							} else {
								break;
							}
						}
					}
					
					if(currentPiece.charAt(1) == 'R' || currentPiece.charAt(1) == 'Q') {
						//up
						for(int i=1; num+i<=8; i++) {
							if(pos.getPiece(letter, num+i).equals(Position.EMPTY)) {
								//System.out.println("Rock/Queen could move up " + i);
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter), num+i, false));
							} else if(pos.getPiece(letter, num+i).charAt(0) == opponentColour ){
								//System.out.println("Rock/Queen could move up " + i + " and capture");
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter), num+i, true));
								break;
							} else {
								break;
							}
						}
						//down
						for(int i=1; num-i>=1; i++) {
							if(pos.getPiece(letter, num-i).equals(Position.EMPTY)) {
								//System.out.println("Rock/Queen could down up " + i);
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter), num-i, false));
							} else if(pos.getPiece(letter, num-i).charAt(0) == opponentColour ){
								//System.out.println("Rock/Queen could down up " + i + " and capture");
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter), num-i, true));
								break;
							} else {
								break;
							}
						}
						
						//right
						for(int i=1; letter+i<='h'; i++) {
							if(pos.getPiece((char)(letter+i), num).equals(Position.EMPTY)) {
								//System.out.println("Rock/Queen could right " + i);
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter+i), num, false));
								
							} else if(pos.getPiece((char)(letter+i), num).charAt(0) == opponentColour ){
								//System.out.println("Rock/Queen could right " + i + " and capture");
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter+i), num, true));
								break;
							} else {
								break;
							}
						}
						//left
						for(int i=1; letter-i>='a'; i++) {
							if(pos.getPiece((char)(letter-i), num).equals(Position.EMPTY)) {
								//System.out.println("Rock/Queen could left " + i);
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter-i), num, false));
							} else if(pos.getPiece((char)(letter-i), num).charAt(0) == opponentColour ){
								//System.out.println("Rock/Queen could left " + i + " and capture");
								ret.add(ChessMover.createMove(currentPiece.charAt(1), (char)letter, num, (char)(letter-i), num, true));
								break;
							} else {
								break;
							}
						}
						
					}
					
					if(currentPiece.charAt(1) == 'K') {
						//System.out.println("King!");
						//make L shape
						//Top right 1:
						if(num+1 <=8 && letter+1<='h' && pos.getPiece((char)(letter+1), num+1).charAt(0) != playerColour) {
							//System.out.println("Could move top right!");
							ret.add(ChessMover.createMove(pos, 'K', (char)letter, num, (char)(letter+1), num+1));
						}
						
						if(letter+1<='h' && pos.getPiece((char)(letter+1), num).charAt(0) != playerColour) {
							//System.out.println("Could move right!");
							ret.add(ChessMover.createMove(pos, 'K', (char)letter, num, (char)(letter+1), num));
						}
						
						if(num-1 >=1 && letter+1<='h' && pos.getPiece((char)(letter+1), num-1).charAt(0) != playerColour) {
							//System.out.println("Could move bottom right!");
							ret.add(ChessMover.createMove(pos, 'K', (char)letter, num, (char)(letter+1), num-1));
						}
						
						if(num-1 >=1  && pos.getPiece(letter, num-1).charAt(0) != playerColour) {
							//System.out.println("Could move bottom!");
							ret.add(ChessMover.createMove(pos, 'K', (char)letter, num, (char)(letter), num-1));
						}
						
						if(num-1 >=1 && letter-1>='a' && pos.getPiece((char)(letter-1), num-1).charAt(0) != playerColour) {
							//System.out.println("Could move bottom left!");
							ret.add(ChessMover.createMove(pos, 'K', (char)letter, num, (char)(letter-1), num-1));
						}
						
						if(letter-1>='a' && pos.getPiece((char)(letter-1), num).charAt(0) != playerColour) {
							//System.out.println("Could move left!");
							ret.add(ChessMover.createMove(pos, 'K', (char)letter, num, (char)(letter-1), num));
						}
						
						if(num+1<=8 && letter-1>='a' && pos.getPiece((char)(letter-1), num+1).charAt(0) != playerColour) {
							//System.out.println("Could move top left!");
							ret.add(ChessMover.createMove(pos, 'K', (char)letter, num, (char)(letter-1), num+1));
						}
						
						if(num+1<=8 && pos.getPiece(letter, num+1).charAt(0) != playerColour) {
							//System.out.println("Could move top!");
							ret.add(ChessMover.createMove(pos, 'K', (char)letter, num, (char)(letter), num+1));
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	public static ArrayList<String> getCastlingMoves(Position pos, boolean kingInCheck) {
		ArrayList<String> ret = new ArrayList<String>();
		char playerColour;
		if(pos.isWhiteTurn()) {
			playerColour = Position.WHITE;
		} else {
			playerColour = Position.BLACK;
		}
		//handle castle possibilities here:
		/*
		 * From website:
		 * When are you not allowed to castle?

				There are a number of cases when castling is not permitted.
				Your king has been moved earlier in the game.
				The rook that castles has been moved earlier in the game.
				There are pieces standing between your king and rook.
				The king is in check.
				The king moves through a square that is attacked by a piece of the opponent.
				The king would be in check after castling.
		 */
		
		if(kingInCheck == false) {
			if(playerColour == Position.WHITE) {
				//king side for white:
				if(pos.isWhiteCastleKing()) {
					if(pos.getPiece('f', 1).equals(Position.EMPTY) && pos.getPiece('g', 1).equals(Position.EMPTY)) {
						if(isSpaceTargettedByOpponent(pos, 'f', 1) == false && isSpaceTargettedByOpponent(pos, 'g', 1) == false) {
							ret.add("O-O");
						}
						
					}
				}
				
				//Queen side for white:
				if(pos.isWhiteCastleQueen()) {
					if(pos.getPiece('d', 1).equals(Position.EMPTY) && pos.getPiece('c', 1).equals(Position.EMPTY) && pos.getPiece('b', 1).equals(Position.EMPTY)) {
						if(isSpaceTargettedByOpponent(pos, 'd', 1) == false && isSpaceTargettedByOpponent(pos, 'c', 1) == false) {
							ret.add("O-O-O");
						}
					}
				}
			} else {
				//king side for white:
				if(pos.isBlackCastleKing()) {
					if(pos.getPiece('f', 8).equals(Position.EMPTY) && pos.getPiece('g', 8).equals(Position.EMPTY)) {
						if(isSpaceTargettedByOpponent(pos, 'f', 8) == false && isSpaceTargettedByOpponent(pos, 'g', 8) == false) {
							ret.add("O-O");
						}
					}
				}
				
				//Queen side for white:
				if(pos.isBlackCastleQueen()) {
					if(pos.getPiece('d', 8).equals(Position.EMPTY) && pos.getPiece('c', 8).equals(Position.EMPTY) && pos.getPiece('b', 8).equals(Position.EMPTY)) {
						if(isSpaceTargettedByOpponent(pos, 'd', 8) == false && isSpaceTargettedByOpponent(pos, 'c', 8) == false) {
							ret.add("O-O-O");
						}
					}
				}
			}
		}
		
		return ret;
	}
}

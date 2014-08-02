package chess;


public class ChessMover {
	
	//accepts: "a1-b1" or "Nc1-f3" or "O-O" or "O-O-O" or "Ke1-e2" or "Ke2xf3".
	public static Position moveFromExplicitMoveString(Position pos, String move) {
		//System.out.println("Get move from explicit: " + move);
		//handle king side castle:
		if(move.equals("O-O") || move.equals("O-O+")) {
			return ChessMover.moveWeird(pos, Position.KING_SIDE_CASTLE);
			
		//handle queen side castle:
		} else if(move.equals("O-O-O") || move.equals("O-O-O+")) {
			return ChessMover.moveWeird(pos, Position.QUEEN_SIDE_CASTLE);
		
		//Handle promotion:
		} else if(move.contains("=")) {
			String piece = move.substring(move.indexOf("=") + 1, move.indexOf("=") + 2);
			if(pos.isWhiteTurn()) {
				piece = Position.WHITE + piece;
			} else {
				piece = Position.BLACK + piece;
			}
			return ChessMover.movePromote(pos, move.charAt(0), move.charAt(1) - '0', move.charAt(3), move.charAt(4) - '0', piece);
		
		//handle normal move:
		} else {
			
			int startPosofCord = 0;
			for(startPosofCord=0; startPosofCord<move.length(); startPosofCord++) {
				if(move.charAt(startPosofCord) >='a' && move.charAt(startPosofCord) <='h') {
					break;
				}
			}
			return ChessMover.moveNormal(pos, move.charAt(startPosofCord), move.charAt(startPosofCord + 1) - '0', move.charAt(startPosofCord + 3), move.charAt(startPosofCord + 4) - '0');
		}
	}
	
	//pre: castling is possible if it is requested.
			//moveWeird means null move or castling.
	public static Position moveWeird(Position pos, int weirdType) {
		
		Position newPos  = pos.hardCopyPosition();
		if(weirdType == Position.NULL) {
			//System.out.println("NULL move time!");
			//Do a null move: (for check-mate checks and Alpha-beta pruning.)
			
			
		} else if(weirdType == Position.KING_SIDE_CASTLE) {
			//System.out.println("KING SIDE CASTLE!");
			
			if(pos.isWhiteTurn()) {
				newPos.setWhiteCastleQueen(false);
				newPos.setWhiteCastleKing(false);
				
				//take away orig king position:
				newPos.removePiece('e', 1);
				//take away orig rock position
				newPos.removePiece('h', 1);
				
				//put rock in new location:
				newPos.placePieceUnSafe(Position.convertStringPieceToNum("WR"), 'f', 1);
				//put king in new location:
				newPos.placePieceUnSafe(Position.convertStringPieceToNum("WK"), 'g', 1);
				
			} else {
				newPos.setBlackCastleQueen(false);
				newPos.setBlackCastleKing(false);
				

				//take away orig king position:
				newPos.removePiece('e', 8);
				//take away orig rock position
				newPos.removePiece('h', 8);
				
				//put rock in new location:
				newPos.placePieceUnSafe(Position.convertStringPieceToNum("BR"), 'f', 8);
				//put king in new location:
				newPos.placePieceUnSafe(Position.convertStringPieceToNum("BK"), 'g', 8);
			}
			
			
		} else if(weirdType == Position.QUEEN_SIDE_CASTLE) {
			//System.out.println("QUEEN SIDE CASTLE!");
			
			if(pos.isWhiteTurn()) {
				newPos.setWhiteCastleQueen(false);
				newPos.setWhiteCastleKing(false);
				
				//take away orig king position:
				newPos.removePiece('e', 1);
				//take away orig rock position
				newPos.removePiece('a', 1);
				
				//put rock in new location:
				newPos.placePieceUnSafe(Position.convertStringPieceToNum("WR"), 'd', 1);
				//put king in new location:
				newPos.placePieceUnSafe(Position.convertStringPieceToNum("WK"), 'c', 1);
				
			} else {
				newPos.setBlackCastleQueen(false);
				newPos.setBlackCastleKing(false);
				

				//take away orig king position:
				newPos.removePiece('e', 8);
				//take away orig rock position
				newPos.removePiece('a', 8);
				
				//put rock in new location:
				newPos.placePieceUnSafe(Position.convertStringPieceToNum("BR"), 'd', 8);
				//put king in new location:
				newPos.placePieceUnSafe(Position.convertStringPieceToNum("BK"), 'c', 8);
			}
			
		}
		
		newPos.setEnPassantCol(Position.NULL);
		newPos.setWhiteTurn(!newPos.isWhiteTurn());
		return newPos;
	}
	
	public static Position movePromote(Position pos, char xi, int yi, char xf, int yf, String piece) {
		//System.out.println((char)xi + "" + yi + "" + (char)xf + "" + yf + "  PROMOTION TO " + piece);
		//System.out.println("Promotion to "  + piece);
		Position newPos = moveNormal(pos, xi, yi, xf, yf);
		//Promote to the piece the user wants:
		newPos.removePiece(xf, yf);
		newPos.placePieceUnSafe(Position.convertStringPieceToNum(piece), (char)xf, yf);
		return newPos;	
	}
	
	//pre: handles all legal moves EXCEPT castling, promotion and NULL moves!
	public static Position moveNormal(Position pos, char xi, int yi, char xf, int yf) {
		//System.out.println((char)xi + "" + yi + "" + (char)xf + "" + yf);
		
		Position newPos = pos.hardCopyPosition();
		newPos.setEnPassantCol(Position.NULL);
		
		String piece = pos.getPiece(xi, yi);
		String piece2 = pos.getPiece(xf, yf);
		
		newPos.removePiece(xi, yi);
		newPos.removePiece(xf, yf);
		newPos.placePieceUnSafe(Position.convertStringPieceToNum(piece), (char)xf, yf);
		
		if(piece.endsWith("P")){
				//check for en passent:
			if(xi != xf && piece2.equals(Position.EMPTY)) {
				newPos.removePiece(xf, yi);
				
				//check if a pawn moves by 2: (makes en-passent possible)
			} else if(moveExposesEnPassant(pos, piece, xi, yi, xf, yf) ){
				newPos.setEnPassantCol(xi);
				
				//check if there's a promotion:
			} else if(yf == 8 || yf == 1) {
				//If there is one, do nothing!
				//promotions are done in another function
			}
			
		//Handle cases where castling becomes unavailable:
		//If king moves, no more castling:
		} else if(piece.endsWith("K")){
			if(pos.isWhiteTurn()) {
				newPos.setWhiteCastleQueen(false);
				newPos.setWhiteCastleKing(false);
			} else {
				newPos.setBlackCastleQueen(false);
				newPos.setBlackCastleKing(false);
			}
			
		//If a rock moves or gets captured, no more castling on that side:
		} else if((xi == 'a' && yi == 1) || (xf == 'a' && yf == 1)) {
			newPos.setWhiteCastleQueen(false);
		} else if((xi == 'a' && yi == 8) || (xf == 'a' && yf == 8)) {
			newPos.setBlackCastleQueen(false);
		} else if((xi == 'h' && yi == 1) || (xf == 'h' && yf == 1)) {
			newPos.setWhiteCastleKing(false);
		} else if((xi == 'h' && yi == 8) || (xf == 'h' && yf == 8)) {
			newPos.setBlackCastleKing(false);
		}
		
		//switch the song:
		newPos.setWhiteTurn(!newPos.isWhiteTurn());
		return newPos;

	}
	

	
	
	
	
	//get move string functions.
	public static String createMove(char piece, char letter1, int num1, char letter2, int num2, boolean capture) {
		if(capture) {
			return "" +((char)piece) + "" + ((char)letter1) + "" + num1 + "x" + ((char)(letter2)) + (num2);
		} else {
			return "" +((char)piece) + "" + ((char)letter1) + "" + num1 + "-" + ((char)(letter2)) + (num2);
		}
	}
	
	public static String createMove(char letter1, int num1, char letter2, int num2, boolean capture) {
		if(capture) {
			return "" +((char)letter1) + "" + num1 + "x" + ((char)(letter2)) + (num2);
		} else {
			return "" +((char)letter1) + "" + num1 + "-" + ((char)(letter2)) + (num2);
		}
	}
	
	public static String createMove(Position pos, char piece, char letter1, int num1, char letter2, int num2) {
		if(pos.isSpaceOccupied(letter2, num2)) {
			return createMove(piece, letter1, num1, letter2, num2, true);
		} else {
			return createMove(piece, letter1, num1, letter2, num2, false);
		}
	}
	
	public static String createMove(Position pos, char letter1, int num1, char letter2, int num2) {
		if(pos.isSpaceOccupied(letter2, num2)) {
			return createMove(letter1, num1, letter2, num2, true);
		} else {
			return createMove(letter1, num1, letter2, num2, false);
			
		}
	}
	
	//enPassantPossible(pos, piece, yi, xf, yf)
	//pre: pawn moves up 2 to xf and yf.
	private static boolean moveExposesEnPassant(Position pos, String piece, int xi, int yi, char xf, int yf) {
		if(piece.endsWith("WP") && Math.abs(yi - yf) > 1 ){
			if(xf-1 >= 'a' &&  pos.getPiece((char)(xf-1), yf).equals("BP")) {
				return true;
			} else if(xf+1 <= 'h' && pos.getPiece((char)(xf+1), yf).equals("BP")) {
				return true;
			}
		} else if(piece.endsWith("BP") && Math.abs(yi - yf) > 1 ){
			if(xf-1 >= 'a' &&  pos.getPiece((char)(xf-1), yf).equals("WP")) {
				return true;
			} else if(xf+1 <= 'h' && pos.getPiece((char)(xf+1), yf).equals("WP")) {
				return true;
			}
		}
		
			return false;
		
	}
}
	//end 4 get move string functions.

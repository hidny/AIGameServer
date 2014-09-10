package chess;

import java.util.Scanner;


public class Position {

	//TODO: make a faster version of this class that is just as correct. (Test the faster version by comparing it to this simplified version)
	
	//DONE: reminder en-passant pawns disappear after 1 move.
	

	//DONE: make list of moves use a function to make it cleaner.
	//DONE: check for capture with knight and king.
	
	//DONE: castling king side ad castling queen side.
	//DONE: convert to  Portable Game Notation (PGN),
	
	
	//Castling[edit]
	//Castling is indicated by the special notations 0-0 (for kingside castling) and 0-0-0 (queenside castling).
	//While the FIDE Handbook, appendix C.13[6] uses the digit zero (0-0 and 0-0-0), PGN requires the uppercase letter O (O-O and O-O-O).
	
	public static final int NULL = -1;

	public static final int WIDTH = 8;
	

	public static final int KING_SIDE_CASTLE = 1;
	
	public static final int QUEEN_SIDE_CASTLE = 2;
	
	public static final char WHITE = 'W';
	public static final char BLACK = 'B';
	public static final String EMPTY = "  ";
	
	
	public Position() {
		this.whiteTurn = true;
		whiteCastleQueen = true;
		blackCastleQueen = true;

		whiteCastleKing = true;
		blackCastleKing = true;
		
		this.enPassantCol = NULL;
		
		this.pos = getInitialPosition();
		
		
	}

	
	//FEN string is the offical way to describe a game.
	//Example FEN string:
	//rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1
	public Position(String fenString) {
		for(int i=0; i<8; i++) {
			this.pos[i] = 0;
		}
		Scanner in = new Scanner(fenString);
		String piecePos = in.next();

		char upperCasePiece;
		
		int currentIndex = 0;
		String currentLine ="";
		int tempNum=0;
		for(int num=8; num>=1; num--) {
			for(char letter='a'; letter<='h'; letter++, currentIndex++) {
				if(piecePos.charAt(currentIndex) >= '1' && piecePos.charAt(currentIndex) <= '8') {
					tempNum = (int)(piecePos.charAt(currentIndex) - '0');
					letter += tempNum - 1;
					
					for(int i=0; i<tempNum; i++) {
						currentLine = currentLine + EMPTY;
					}
						
				//Add Black piece:
				} else if(piecePos.charAt(currentIndex) > 'a' && piecePos.charAt(currentIndex) <= 'z') {
					upperCasePiece = (char)(piecePos.charAt(currentIndex) + 'A' - 'a');
					currentLine = currentLine + "B" + upperCasePiece;
					
				//Add white piece:
				} else if(piecePos.charAt(currentIndex) > 'A' && piecePos.charAt(currentIndex) <= 'Z') {
					currentLine = currentLine + "W" + piecePos.charAt(currentIndex);
				
				//Slash problems:
				} else if(piecePos.charAt(currentIndex) == '/') {

					System.out.println("Unexpected slash input while reading piece position of FEN string.");
					System.exit(1);
				//Can't compute:
				} else {
					System.out.println("ERROR: unexpected input while reading piece position of FEN string.");
					System.exit(1);
					
				}
			}
			
			//if there's more lines to read:
			if(currentIndex < piecePos.length() && piecePos.charAt(currentIndex) == '/') {
				//remove first instance of / once the currentIndex has gone past it:
				currentIndex++;
				
			//After last line is read, do nothing.
			} else if(piecePos.length() == currentIndex ) {
				//Do nothing
				
				//Can't compute:
			} else {
				System.out.println("ERROR: expected slash input while reading piece position of FEN string.");
				System.exit(1);
			}
			
			//Sanity test: (current line length should be 16)
			if(currentLine.length() != 16) {
				//Sanity test:
				System.out.println("ERROR: expected FEN notation to complete a whole row but it didn't");
				System.out.println("Line so far: " + currentLine);
				System.exit(1);
			} else {
				
				pos[num - 1] = convertStringLineToNum(currentLine);
				currentLine = "";
			}
		}
		
		//get turn:
		String turn = in.next();
		if(turn.toUpperCase().equals("W")) {
			this.whiteTurn = true;
		} else {
			this.whiteTurn = false;
		}
		
		//TODO(optional):  logically figure out is castle info is inconsistent with the board position.
		//		Let the board position take precedence.
		//Get castle information:
		String castle = in.next();
		
		if(castle.contains("K")) {
			this.whiteCastleKing = true;
		} else {
			this.whiteCastleKing = false;
		}
		
		if(castle.contains("Q")) {
			this.whiteCastleQueen = true;
		} else {
			this.whiteCastleQueen = false;
		}
		
		if(castle.contains("k")) {
			this.blackCastleKing = true;
		} else {
			this.blackCastleKing = false;
		}
		
		if(castle.contains("q")) {
			this.blackCastleQueen = true;
		} else {
			this.blackCastleQueen = false;
		}
		
		//Get en passant information:
		String enPassant = in.next().toLowerCase();
		
		if(enPassant.charAt(0) >= 'a' && enPassant.charAt(0) <= 'h') {
			this.enPassantCol = enPassant.charAt(0);
		}

		//System.out.println(this.toString());
		//TODO: This class actually doensn't care about the last 2 numbers, (for how long the game has gone)
		// if those numbers matter, some class that cares should read them.
		in.close();
	}
	
	
	public String getFenString() {
		String ret = "";
		
		int space;
		for(int num=8; num>=1; num--) {
			space = 0;
			for(char letter='a'; letter<='h'; letter++) {
				if(this.getPiece(letter, num) != EMPTY) {
					if(space > 0) {
						ret += "" + space;
					}
					ret += getFenLetterFromPiece(this.getPiece(letter, num));
					space = 0;
					
				} else {
					space++;
				}
			}
			
			if(space > 0) {
				ret += "" + space;
			}
			ret += "/";
		}
		
		ret = ret.substring(0, ret.length() - 1);
		
		ret += " ";
		
		if(this.isWhiteTurn()) {
			ret += "w";
		} else {
			ret += "b";
		}
		
		ret += " ";
		if(this.whiteCastleKing) {
			ret+= "K";
		}
		
		if(this.whiteCastleQueen) {
			ret += "Q";
		}
		
		if(this.blackCastleKing) {
			ret += "k";
		}
		
		if(this.blackCastleQueen) {
			ret += "q";
		}

		if((this.whiteCastleKing || this.whiteCastleQueen || this.blackCastleKing || this.blackCastleQueen) == false) {
			ret += "-";
		}
		
		ret += " ";
		
		if(this.enPassantCol != NULL) {
			if(this.whiteTurn) {
				ret += this.enPassantCol + "" + 3;
			} else {
				ret += this.enPassantCol + "" + 6;
			}
		} else {
			ret += "-";
		}
		ret += " ";
		
		return ret;
	}
	
	private char getFenLetterFromPiece(String piece) {
		if(piece.charAt(0) == 'B') {
			return (char)(piece.charAt(1) +'a' - 'A');
		} else {
			return piece.charAt(1);
		}
	}
	
	public Position hardCopyPosition() {
		Position newPos = new Position();
		
		for(int i=0; i<pos.length; i++) {
			newPos.pos[i] = this.pos[i];
		}
		newPos.blackCastleKing = this.blackCastleKing;
		newPos.blackCastleQueen = this.blackCastleQueen;
		
		newPos.enPassantCol = this.enPassantCol;

		newPos.whiteCastleKing = this.whiteCastleKing;
		newPos.whiteCastleQueen = this.whiteCastleQueen;
		
		newPos.whiteTurn = this.whiteTurn;
		
		return newPos;
	}
	
	//returns the chess board at the initial position:
	public int[] getInitialPosition() {
		String firstLine =   "BRBNBBBQBKBBBNBR";
		String secondLine =  "BPBPBPBPBPBPBPBP";
		
		String seventhLine = "WPWPWPWPWPWPWPWP";
		String eigthLine =   "WRWNWBWQWKWBWNWR";
		
		int ret[] = new int[8];
		ret[7] = convertStringLineToNum(firstLine);
		ret[6] = convertStringLineToNum(secondLine);
		ret[1] = convertStringLineToNum(seventhLine);
		ret[0] = convertStringLineToNum(eigthLine);
		
		for(int i = 2; i<6; i++) {
			ret[i] = 0;
		}
		
		return ret;
	}
	
	//Default to the settings at the beginning of the game:

	private boolean whiteTurn = true;
	
	private int pos[] = new int[8];
	
	private boolean whiteCastleQueen = true;
	private boolean blackCastleQueen = true;
	
	private boolean whiteCastleKing = true;
	private boolean blackCastleKing = true;
	
	//Ascii character # of an enpassant col if there is the potential
	//to do an enpassant in the current position.
	private int enPassantCol = NULL;
	
	public boolean isWhiteTurn() {
		return whiteTurn;
	}

	public int[] getPos() {
		return pos;
	}

	public void setPos(int[] pos) {
		this.pos = pos;
	}

	public boolean isWhiteCastleQueen() {
		return whiteCastleQueen;
	}


	public boolean isBlackCastleQueen() {
		return blackCastleQueen;
	}

	public boolean isWhiteCastleKing() {
		return whiteCastleKing;
	}


	public boolean isBlackCastleKing() {
		return blackCastleKing;
	}


	public int getEnPassantCol() {
		return enPassantCol;
	}
	
	public void setWhiteTurn(boolean whiteTurn) {
		this.whiteTurn = whiteTurn;
	}

	public void setWhiteCastleQueen(boolean whiteCastleQueen) {
		this.whiteCastleQueen = whiteCastleQueen;
	}

	public void setBlackCastleQueen(boolean blackCastleQueen) {
		this.blackCastleQueen = blackCastleQueen;
	}

	public void setWhiteCastleKing(boolean whiteCastleKing) {
		this.whiteCastleKing = whiteCastleKing;
	}

	public void setBlackCastleKing(boolean blackCastleKing) {
		this.blackCastleKing = blackCastleKing;
	}

	public void setEnPassantCol(int enPassantCol) {
		this.enPassantCol = enPassantCol;
	}
	
	public String toString() {
		String ret = "-------------------------" + '\n';
		
		for(int i=7; i>=0; i--) {
			ret += "|";
			for(int j=0; j<8; j++) {
				ret += convertBitsToPiece(pos[i]>>(28-j*4)) + "|";
			}
			ret += '\n';
			
			ret += "-------------------------" + '\n';
		}
		if(whiteTurn) {
			ret += "White to move" + '\n';
		} else {
			ret += "Black to move" + '\n';
		}
		
		if(this.enPassantCol != NULL) {
			ret += "En passant for column " + (char)this.enPassantCol  + "\n";
		}
		ret += "FEN string: " + getFenString();
		return ret.substring(0, ret.length() - 1);
	}
	
	//converts numbers to strings:
	//WT: white en-passant target.
	//BT: black en-passant target.
	public String convertBitsToPiece(int pos) {
		int positivePosition = ((pos%16) + 16)%16;
		switch (positivePosition) {
			case 0:  return EMPTY;
			case 1:  return "BP";
	        case 2:  return "BR";
	        case 3:  return "BN";
	        case 4:  return "BB";
	        case 5:  return "BQ";
	        case 6:  return "BK";
	        case 7:  return "BS";
	        case 8:  return "??";
	        case 9:  return "WS";
	        case 10: return "WK";
	        case 11: return "WQ";
	        case 12: return "WB";
	        case 13: return "WN";
	        case 14: return "WR";
	        case 15: return "WP";
	       
    }
		return EMPTY;
	}
	
	 	public static int convertStringLineToNum(String line) {
	    	int ret = 0;
	 		for(int i=0; 2*i<line.length(); i++) {
	    		ret ^= convertStringPieceToNum(line.substring(2*i, 2*(i+1))) << (4*7-4*i);
	    	}
	 		
	 		return ret;
	    }
	
	    public static int convertStringPieceToNum(String piece) {
	    	return convertCharPieceToBit(piece.charAt(0), piece.charAt(1));
	    }
	    
	//converts numbers to strings:
	    //S is for shit.
		public static int convertCharPieceToBit(char colour, char piece) {
			if(colour == BLACK) {
				switch(piece) {
					case 'P': return 1;
					case 'R': return 2;
					case 'N': return 3;
					case 'B': return 4;
					case 'Q': return 5;
					case 'K': return 6;
					case 'S': return 7;
				}
				return 8;
			} else if(colour == WHITE) {
				switch(piece) {
				case 'P': return 15;
				case 'R': return 14;
				case 'N': return 13;
				case 'B': return 12;
				case 'Q': return 11;
				case 'K': return 10;
				case 'S': return 9;
			}
				return 8;
			} else {
				return 0;
			}
		}
		
		//I'm using standard chess coord:
		public String getPiece(char letter, int num) {
			return convertBitsToPiece(pos[num - 1] >> (4*(7 - (letter - 'a'))) );
		}
		
		//pre: there's no piece there in the first place!
		public void placePieceUnSafe(int piece, char letter, int num) {
			if(convertStringPieceToNum(getPiece(letter, num)) != 0) {
				System.out.println("ERROR: trying to place a piece on a piece.");
				System.out.println("The pre-condition is abused");
				System.exit(1);
			}
			
			int temp = piece << (4*(7 - (letter - 'a')));
			
			pos[num - 1] ^= temp;
		}
		
		public void removePiece(char letter, int num) {
			int temp = (pos[num - 1] >> (4*(7 - (letter - 'a')))) %16 ;
			if(temp< 0) {
				temp += 16;
			}
			pos[num - 1] ^= temp << (4*(7 - (letter - 'a')));
		}
		
		
		public boolean isSpaceOccupied(char letter, int num) {
			return getPiece(letter, num).charAt(0) != ' ';
		}
}

package reversi;


public class Position {

	/* Coord:
	 * 
	 *      0 1 2 3 4 5 6 7 8
	 *   0                    
	 *   1                    
	 *   2                    
	 *   3                    
	 *   4                     
	 *   5                    
	 *   6                    
	 *   7                    
	 *   8                    
	 *   
	 *   Translate to: (see wikipedia)
	 *   
	 *   1
	 *   2
	 *   3
	 *   4
	 *   5
	 *   6
	 *   7
	 *   8
	 *      a  b  c  d  e  f  g  h
	 */
	public static String GAME_NAME = "reversi";
	
	public static int OUT_OF_BOUNDS = -1;
	public static int EMPTY = 0;
	public static int DARK = 1;
	public static int WHITE = 2;
	
	public static int SIZE = 8;
	
	public static boolean DEBUGGINGAUTOPLAY = false;
	private int position[][] = new int[SIZE][SIZE];
	
	private boolean possibleMovesCalculated = false;
	private boolean possibleMove[][] = new boolean[SIZE][SIZE];
	
	//Dark
	private int playerTurn = DARK;
	
	public static void main(String args[]) {
		System.out.println("Let's play reversi!");
		
		ReversiMiddleMan middleMan = new ReversiMiddleMan();
		
		
		PlayerDecider dark = new HumanConsole("Mike");
		PlayerDecider white = new HumanConsole("Rich");
		
		startReversi(middleMan, dark, white);
		
	}
	
	
	//TODO: print commands and output.
	//TODO 2: create console players.
	public static void startReversi(ReversiMiddleMan middleMan, PlayerDecider dark, PlayerDecider white) {
		
		Position pos = new Position();
		Position posTest;
		
		String queryMove;
		
		int nextMove;
		
		middleMan.recordCommand("Reversi:" + "\n");
		middleMan.recordCommand(dark.getName() + " vs " + white.getName() + "\n");
		middleMan.sendMessageToGroup(dark.getName() + " vs " + white.getName() + "\n", true);
		
		
		//System.out.println(pos.getPositionString());
		middleMan.sendMessageToGroup(pos.getPositionString(), true);
		
		while(true) {
			nextMove = pos.getNextPossibleMove();
			
			//see if the game is over:
			if(nextMove == OUT_OF_BOUNDS) {
				posTest = pos.makeHardCopy();
				if(posTest.playerTurn == DARK) {
					posTest.playerTurn = WHITE;
				} else {
					posTest.playerTurn = DARK;
				}
				
				posTest.possibleMovesCalculated = false;
				
				nextMove  = posTest.getNextPossibleMove();
				
				if(nextMove == OUT_OF_BOUNDS) {
					
					//It's over!
					break;
				} else {

					pos = posTest;
					
					//System.out.println("Can't move! Next player's turn");
					middleMan.sendMessageToGroup("Can't move! Next player's turn", true);
					//next player's turn
					
					if(pos.playerTurn == DARK) {
						middleMan.recordCommand("\n");
					} else {
						middleMan.recordCommand("    ");
					}
					
					
				}
			} else {
				//Ask Player Decider to move here!
				if(DEBUGGINGAUTOPLAY) {
					//System.out.println("Moving: (" + nextMove/SIZE + ", " + nextMove%SIZE+")");
					//TODO: make sre the logs are standardized.
					middleMan.sendMessageToGroup("Moving: (" + nextMove/SIZE + ", " + nextMove%SIZE+")", true);
					
					pos = pos.move(nextMove/SIZE, nextMove%SIZE);
				} else {
					boolean moveMade = false;
					int numTrials = 0;
					do {
						if(pos.playerTurn == DARK) {
							middleMan.sendMessageToPlayer(dark.getName(), "Your turn dark. What do you do?:");
							queryMove = dark.getMove(pos, pos.playerTurn);
						} else {
							middleMan.sendMessageToPlayer(white.getName(), "Your turn white. What do you do?:");
							queryMove = white.getMove(pos, pos.playerTurn);
						}
						
						queryMove = queryMove.toLowerCase();
						
						int posi = -1;
						int posj = -1;
						if(queryMove.length()>=2) {
							posi = (queryMove.charAt(1) - '1');
							posj = (queryMove.charAt(0) - 'a');
							
						}
						
						
						if(posi >= 0 && posi<SIZE && posj>=0 && posj<SIZE && pos.checkPositionPlayable(posi, posj)) {
							
							pos = pos.move(posi, posj);
							moveMade = true;
						} else {
							System.out.println("NOPE!");
							System.out.println("(i , j) = ( " + posi + ", " + posj + ")");
							numTrials++;
							if(numTrials>=3) {
								//just do the default move:
								
								queryMove = "" + (char)(nextMove%SIZE + 'a');
								queryMove += "" + (char)(nextMove/SIZE + '1');
								
								pos = pos.move(nextMove/SIZE, nextMove%SIZE);
								moveMade = true;
								
							}
						}
						
						if(moveMade) {
							if(pos.playerTurn != DARK) {
								middleMan.recordCommand(queryMove + "  ");
							} else {
								middleMan.recordCommand(queryMove + "\n");
							}
							middleMan.sendMessageToGroup("Moving: " + queryMove + "\n", true);
						}
						
					} while(moveMade == false);
				}
			}
			
			//System.out.println(pos.getPositionString());
			
			middleMan.sendMessageToGroup(pos.getPositionString(), true);
		}
		
		//Check who wins:
		int numDark = 0;
		int numWhite = 0;
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				if(pos.position[i][j] == DARK) {
					numDark++;
				} else if(pos.position[i][j] == WHITE) {
					numWhite++;
				}
			}
		}
		
		//TODO: tie breakers are complicated look it up on wikipedia
		if(numDark > numWhite) {
			//System.out.println("DARK WINS!");
			//System.out.println(numDark + " - " + numWhite);
			middleMan.sendMessageToGroup(dark.getName() + " WINS!", true);
			middleMan.sendMessageToGroup(numDark + " - " + numWhite, true);
		} else if (numWhite > numDark) {
			//System.out.println("WHITE WINS!");
			//System.out.println(numDark + " - " + numWhite);
			//TODO
			middleMan.sendMessageToGroup(white.getName() + " WINS!", true);
			middleMan.sendMessageToGroup(numDark + " - " + numWhite, true);
		} else {
			//System.out.println("It's a tie!");
			//System.out.println(numDark + " - " + numWhite);
			middleMan.sendMessageToGroup("It is a tie!", true);
			middleMan.sendMessageToGroup(numDark + " - " + numWhite, true);
		}
		//print final score because why not?
		middleMan.recordCommand(numDark + " - " + numWhite);
		
	}
	
	
	public int getNextPossibleMove() {
		if(this.possibleMovesCalculated == false) {
			this.getPossibleMoves();
		}
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				if(this.getPossibleMoves()[i][j]) {
					return SIZE*i + j;
				}
			}
		}
		return OUT_OF_BOUNDS;
	}
	
	public Position() {
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				position[i][j] = EMPTY;
			}
		}
		
		position[SIZE/2-1][SIZE/2-1] = WHITE;
		position[SIZE/2][SIZE/2] = WHITE;
		position[SIZE/2-1][SIZE/2] = DARK;
		position[SIZE/2][SIZE/2-1] = DARK;
	}
	
	
	
	public void setPosition(char letter, int num, int value) {
		if(letter >= 'A' || letter <='Z') {
			int temp = 'a' - 'A' + letter;
			letter = (char)temp;
		}
		position[num - 1][(int)(letter - 'a')] = value;
	}
	
	public int getPosition(char letter, int num) {
		return position[num - 1][(int)(letter - 'a')];
	}
	
	
	public int[][] getPosition() {
		return position;
	}
	public boolean[][] getPossibleMoves() {
		if(possibleMovesCalculated) {
			return possibleMove;
		}
		
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				possibleMove[i][j] = checkPositionPlayable(i, j);
			}
		}
		
		possibleMovesCalculated = true;
		
		return possibleMove;
		
	}
	
	
	public boolean checkPositionPlayable(int i, int j) {
		if(position[i][j] != EMPTY) {
			return false;
		}
		
		int x;
		int y;
		
		boolean enemiesInBetween;
		int enemyTurn;
		if(playerTurn == DARK) {
			enemyTurn = WHITE;
		} else {
			enemyTurn = DARK;
		}
		
		for(int dirx=-1; dirx<=1; dirx++) {
			for(int diry=-1; diry<=1; diry++) {
				if(dirx == 0 && diry==0) {
					continue;
				} else{
					enemiesInBetween = false;
					
					for(int k=1; k<SIZE; k++) {
						if(j+k*dirx >= 0 && j+k*dirx < SIZE && i+k*diry >= 0 && i+k*diry < SIZE) {
							x = j+k*dirx;
							y = i+k*diry;
							
							//you got x and y reversed!
							if(position[y][x] == enemyTurn) {
								enemiesInBetween = true;
							} else if(position[y][x] == playerTurn && enemiesInBetween) {
								return true;
								
							//check if the space is adjacent to the same colour:
							} else if(position[y][x] == playerTurn && enemiesInBetween == false) {
								break;
							} else if(position[y][x] == EMPTY) {
								break;
							} else {
								System.out.println("POSITION UNKNOWN! (In check position playable)");
								System.exit(1);
							}
						}
					}
				}
			}
		}
		return false;
		
	}
	

	public Position move(int i, int j) {
		
		//STEP 0: check if the move is possible:
		Position newPos = null;
		boolean possible[][] = getPossibleMoves();
		if(possible[i][j] == false) {
			return null;
		}
		
		/*
		 * STEP 1: hard copy position
		 * 
		 */
		newPos = this.makeHardCopy();
		
		/* step 2:
		 * flip the pieces on the board appropriately.
		 */
		int x;
		int y;
		
		int xFlip;
		int yFlip;
		
		boolean enemiesInBetween = false;
		int enemyTurn;
		if(newPos.playerTurn == DARK) {
			enemyTurn = WHITE;
		} else {
			enemyTurn = DARK;
		}
		
		for(int dirx=-1; dirx<=1; dirx++) {
			for(int diry=-1; diry<=1; diry++) {
				if(dirx == 0 && diry==0) {
					continue;
				} else{
					enemiesInBetween = false;
					
					for(int k=1; k<SIZE; k++) {
						if(j+k*dirx >= 0 && j+k*dirx < SIZE && i+k*diry >= 0 && i+k*diry < SIZE) {
							x = j+k*dirx;
							y = i+k*diry;
							
							//you got x and y reversed!
							if(newPos.position[y][x] == enemyTurn) {
								enemiesInBetween = true;
							} else if(newPos.position[y][x] == newPos.playerTurn && enemiesInBetween) {
								//FLIPPY TIME!
								if(k==1) {
									System.out.println("ERROR: (In Position.move()) I'm not flipping anything wtf?");
									System.exit(1);
								}
								for(int l=1; l<k; l++) {
									xFlip = j+l*dirx;
									yFlip = i+l*diry;
									if(newPos.position[yFlip][xFlip] == DARK) {
										newPos.position[yFlip][xFlip] = WHITE;
									} else if(newPos.position[yFlip][xFlip] == WHITE) {
										newPos.position[yFlip][xFlip] = DARK;
									} else {
										System.out.println("ERROR: (In Position.move()) I'm flipping an empty square wtf?");
										System.exit(1);
									}
								}
								break;
								
							//check if the space is adjacent to the same colour:
							} else if(newPos.position[y][x] == newPos.playerTurn && enemiesInBetween == false) {
								break;
							
							} else if(newPos.position[y][x] == EMPTY) {
								break;
							} else {
								System.out.println("POSITION UNKNOWN! (In Position.move() playable)");
								System.exit(1);
							}
						}
					}
				}
			}
		}
		
		if(newPos.position[i][j] != EMPTY) {
			System.out.println("MOVING into a filled space (wtf?) UNKNOWN! (In Position.move())");
			System.exit(1);
		}
		if(newPos.playerTurn == WHITE) {
			newPos.position[i][j] = WHITE;
		} else if(newPos.playerTurn == DARK) {
			newPos.position[i][j] = DARK;
		}
		
		//step 3: make the switch
		
		if(this.playerTurn == DARK) {
			newPos.playerTurn = WHITE;
			
		} else {
			newPos.playerTurn = DARK;
			
		}
		
		newPos.possibleMovesCalculated = false;
		
		return newPos;
	}
	
	public String getPositionString() {
		boolean possible[][] = getPossibleMoves();
		
		String ret = "";
		if(playerTurn == DARK) {
			ret += "Dark to move:" + '\n';
		} else {
			ret += "White to move:" + '\n';
		}
		
		ret += "-----------------" + '\n';
		
		for(int i=0; i<SIZE; i++) {
			ret += "|";
			for(int j=0; j<SIZE; j++) {
				
				if(position[i][j] == DARK) {
					ret += "D";
				} else if(position[i][j] == WHITE) {
					ret += "W";
				} else if(position[i][j] == EMPTY) {
					if(possible[i][j]) {
						ret += "*";
					} else {
						ret += " ";
					}
				}
				ret += "|";
			}
			ret += '\n';
			ret += "-----------------" + '\n';
		}
		
		//take away the last new line character:
		ret = ret.substring(0, ret.length() -1);
		
		return ret;
	}
	
	public Position makeHardCopy() {
		Position newPos = new Position();
		
		newPos.possibleMovesCalculated = possibleMovesCalculated;
		newPos.playerTurn = playerTurn;
		
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE;j++) {
				newPos.position[i][j] = position[i][j];
				newPos.possibleMove[i][j] = possibleMove[i][j];
			}
		}
		
		return newPos;
	}

}
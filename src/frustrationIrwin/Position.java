package frustrationIrwin;

//TODO: for AI: print the array in a more readable way! (Not the ascii board)

//TODO: Make a HASBRO version:
//1 is not special
//6 means you could (take out peg or play 6) AND then play again.
public class Position {
	//TODO: make this changeable:
	//irwin vs (matel/hasbro?)
	public static String GAME_NAME = "frustrationIrwin";
	
	private random.dice.Die currentDie;
	
	public static int NUM_POS_PER_QUARTER = 7;
	public static int NUM_PEGS_PER_PLAYER = 4;
	public static int NUM_POS_IN_CIRCLE = 4 * NUM_POS_PER_QUARTER;
	private static int LENGTH_POS = 2 * NUM_POS_IN_CIRCLE;
	
	public static int NUM_PLAYER_SLOTS = 4;
	public static int ILLEGAL = -1;
	public static int EMPTY = -2;
	public static int HOME = -3;
	public static int SKIP = -4;
	public static int DID_NOT_MOVE = -5;
	public static int RAND = -6;
	//index of pegs "in-game":
		// 0(1st landing) 1  2  3   4 5 6 7(2nd landing) 8 9 10 11 12 13 14(3rd landing) 15 16 17 18 19 20 21 (3rd landing) 22 23 24 25 26 27
		// 28 29  30  31                  35  36  37  38                 42   43  44  45                   49  50  51  52
	
	private int[] pos;
	private int turnIndex;
	
	private String prevMoveDetails = "";
	
	
	private static final String asciiBoard = "\n" +
"                      (C4) (C3) (C2) (C1)" + "\n" + 
"                      (13)       (14)" + "\n" + 
"                (12)       (42)        (15)" + "\n" + 
"              (11)         (43)          (16)" + "\n" + 
"            (10)           (44)            (17)" + "\n" + 
"          (9)              (45)              (18)" + "\n" + 
"        (8)                                   (19)" + "\n" + 
" (B1)                                                  (D4)" + "\n" + 
" (B2)  (7)                                       (20)  (D3)" + "\n" + 
"         (35)(36)(37)(38)  (L)  (52)(51)(50)(49)" + "\n" + 
" (B3)  (6)                                       (21)  (D2)" + "\n" + 
" (B4)                                                  (D1)" + "\n" + 
"        (5)                                   (22)" + "\n" + 
"          (4)              (31)              (23)" + "\n" + 
"            (3)            (30)            (24)" + "\n" + 
"              (2)          (29)          (25)" + "\n" + 
"                (1)        (28)        (26)" + "\n" + 
"                      (0)       (27)" + "\n" + 
"                     (A1) (A2) (A3) (A4)";
	
	public static void startFrustration(FrustrationServerMiddleMan middleMan, PlayerDecider players[]) {
		startFrustration(middleMan, players, null, RAND);
	}
	
	//When you need it.
	public static void startFrustration(FrustrationServerMiddleMan middleMan, PlayerDecider players[], random.dice.Die givenDie, int startingIndex) {
		
		Position pos = new Position();
		middleMan.sendMessageToGroup("Starting Frustation IRWIN!"+ "\n");
		middleMan.recordCommand("Frustation Irwin version:" + "\n");
		
		for(int i=0; i<players.length; i++) {
			if(players[i] != null) {
				middleMan.recordCommand("position " + ((char) ('A' + i)) + ": " + players[i].getName() + "\n");
				middleMan.sendMessageToGroup("position " + ((char) ('A' + i)) + ": " + players[i].getName()+ "\n");
			}
		}
		
		int result = pos.playGame(players, middleMan, givenDie, startingIndex);
		
		if ( result >= 0 && result < NUM_PLAYER_SLOTS) {
			middleMan.sendMessageToGroup(players[result].getName() + " wins!");
		} else {
			middleMan.sendMessageToGroup("ERROR: I don't know who wins!");
			System.exit(1);
			
		}
	}
	
	public int playGame(PlayerDecider players[],  FrustrationServerMiddleMan middleMan) {
		return playGame(players, middleMan, null, RAND);
	}
	
	public static int getNumPlayers(PlayerDecider players[]) {
		int ret = 0;
		for(int i=0; i<players.length; i++) {
			if(players[i] != null) {
				ret++;
			}
		}
		return ret;
	}
	
	public static int getNextRoller(PlayerDecider players[], int currentRollerIndex) {
		for(int i=currentRollerIndex + 1; i%4 != currentRollerIndex; i++) {
			if(players[i%4] != null) {
				return i%4;
			}
		}
		System.out.println("ERROR: could not find next roller!");
		System.exit(1);
		return -1;
	}
	
	public static int getIndexStarting(PlayerDecider players[]) {
		int index = (int)(getNumPlayers(players) * Math.random());
		int numFound = 0;
		for(int i=0; i<players.length; i++) {
			if(players[i] != null) {
				if(numFound == index) {
					return i;
				} else {
					numFound++;
				}
			}
		}
		System.out.println("ERROR: getIndexStarting failed (could not find index)");
		System.exit(1);
		return -1;
	}
	
	//returns false otherwise.
	public int playGame(PlayerDecider playerDeciders[],  FrustrationServerMiddleMan middleMan, random.dice.Die givenDie, int startingIndex) {
		if(getNumPlayers(playerDeciders) <= 1) {
			System.out.println("ERROR: this is a multiplayer game!");
			System.exit(1);
		}
		
		if(startingIndex < 0) {
			startingIndex = getIndexStarting(playerDeciders);
		}
		
		middleMan.sendMessageToGroup("First roller is " + playerDeciders[startingIndex].getName());
		middleMan.recordCommand("First Roller: " + playerDeciders[startingIndex].getName() + "\n");
		
		//If no deck is given, create a new one:
		if(givenDie == null) {
			currentDie = new random.dice.RandomDie(middleMan.getCommandFile());
		} else {
			currentDie = givenDie;
		}
		
		setupInitialPos(playerDeciders, startingIndex);
		
		turnIndex = startingIndex;
		
		int move;
		int currentRoll = -1;
		
		while(isGameOver(pos) == false) {
			//update dealer on all rounds except for the first one:
			middleMan.sendMessageToGroup("Current roller is " + playerDeciders[turnIndex].getName());
			
			currentRoll = Integer.parseInt(currentDie.getRoll());
			middleMan.recordCommand(currentRoll + "\n");

			int currentTurn = this.turnIndex;
			
			move = DID_NOT_MOVE;
			do {
				//IRWIN: (play again condition:)
				if(move != DID_NOT_MOVE) {
					System.out.println("TO ALL: " + playerDeciders[turnIndex].getName() + " gets to roll again.");
					middleMan.sendMessageToGroup(playerDeciders[turnIndex].getName() + " gets to roll again.");
				}
				//END IRWIN

				System.out.println("TO ALL: " + getPositionString(pos, playerDeciders, currentRoll));
				middleMan.sendMessageToGroup(getPositionString(pos, playerDeciders, currentRoll));
				System.out.println("TO ALL: " + playerDeciders[turnIndex].getName() + " rolled a " + currentRoll);
				middleMan.sendMessageToGroup(playerDeciders[turnIndex].getName() + " rolled a " + currentRoll);
				
				System.out.println("TO PLAYER: " + getListOfChoicesString(pos, turnIndex, currentRoll));
				middleMan.sendMessageToPlayer(playerDeciders[turnIndex].getName(), "Your turn.");
				middleMan.sendMessageToPlayer(playerDeciders[turnIndex].getName(), "Your choices: " + getListOfChoicesString(pos, turnIndex, currentRoll));
				
				//MOVE:
				int numTries = 0;
				do {
					if(numTries > 0) {
						System.out.println("TO PLAYER: MOVE PROPERLY!");
						middleMan.sendMessageToPlayer(playerDeciders[turnIndex].getName(),  "MOVE PROPERLY!");
					}
					
					if(numTries < 3) {
						move = playerDeciders[turnIndex].getMove(currentRoll, pos, turnIndex);
					} else {
						//After 3 tries, just play any legal move:
						move = DID_NOT_MOVE;
						for(int i=0; i<pos.length; i++) {
							if(isLegalMove(pos, turnIndex, i, currentRoll)) {
								move = i;
								break;
							}
						}
						if(move == DID_NOT_MOVE) {
							move = SKIP;
						}
					}
					
					numTries++;
				} while(isLegalMove(pos, turnIndex, move, currentRoll) == false);
				
				middleMan.recordCommand(" " + move + "\n");
				this.move(playerDeciders, move, currentRoll);
				
				System.out.println("TO ALL: " +  getMoveDetails());
				middleMan.sendMessageToGroup(getMoveDetails());
				
			} while(currentTurn == this.turnIndex);
		}
			
		
		System.out.println("TO ALL: " + getPositionString(pos, playerDeciders, currentRoll));
		middleMan.sendMessageToGroup(getPositionString(pos, playerDeciders, currentRoll));
		
		return getResult(pos);
	}
	
	public void setupInitialPos(PlayerDecider playerDeciders[], int startingIndex) {
		pos = new int[LENGTH_POS];
		for(int i=0; i<LENGTH_POS; i++) {
			pos[i] = EMPTY;
		}
		turnIndex = startingIndex;
	}
	
	//pre: players[turnIndex] is not null.
	public String getMoveDetails() {
		return prevMoveDetails;
	}

	public void move(PlayerDecider players[], int move, int roll) {
		boolean getsToMoveAgain = false;
		
		if(isLegalMove(pos, turnIndex, move, roll) == false) {
			System.out.println("ERROR: illegal move!");
			System.exit(1);
		}
		
		if(move == SKIP) {
			prevMoveDetails = players[turnIndex].getName() + " is skipping his/her turn.";
			//Do nothing!
			
		} else if(move >= 0 && getLandingPos(pos, turnIndex, move, roll) != ILLEGAL) {
			int landingPos = getLandingPos(pos, turnIndex, move, roll);
			pos[landingPos] = turnIndex;
			if(landingPos != HOME) {
				pos[move] = EMPTY;
			}
			prevMoveDetails =  players[turnIndex].getName() + " is moving peg from " + move + " to " + landingPos + ".";
		
		
		} else if(move == HOME || move == getStartIndex(turnIndex) ) {
			if(getLandingPos(pos, turnIndex, HOME, roll) == getStartIndex(turnIndex) && getNumPegsHome(pos, turnIndex) > 0 && pos[getStartIndex(turnIndex)] != turnIndex) {
				pos[getStartIndex(turnIndex)] = turnIndex;
				
				prevMoveDetails =  players[turnIndex].getName() + " is moving peg from HOME to " + pos[getStartIndex(turnIndex)] + ".";
				getsToMoveAgain = true;
				
			} else {
				System.out.println("ERROR: can't move to/from index " + getStartIndex(turnIndex) );
				System.exit(1);
			}
		} else {
			boolean hasMoved = false;
			for(int i=0; i<pos.length; i++) {
				if(pos[i] == turnIndex) {
					if(getLandingPos(pos, turnIndex, i, roll) == move) {
						pos[i] = EMPTY;
						if(move >= 0) {
							pos[move] = turnIndex;
						}
						hasMoved = true;
						prevMoveDetails =  players[turnIndex].getName() + " is moving peg from index  " + i +  " to " + move + ".";
						break;
					}
				}
			}
			
			if(hasMoved == false) {
				System.out.println("ERROR: could not find a way to move.");
				System.exit(1);
			}
		}
		
		if(getsToMoveAgain == false) {
			turnIndex = getNextRoller(players, turnIndex);
		}
	}
	
	
	

	//Print the board in ascii:
	public static String getPositionString(int pos[], PlayerDecider players[], int currentRoll) {
		//hard copy asciiBoard:
		String ret = asciiBoard + "";
		
		int numPegsToEraseAtHome;
		for(int i=0; i<NUM_PLAYER_SLOTS; i++) {
			if(players[i] == null) {
				numPegsToEraseAtHome = NUM_PLAYER_SLOTS;
			} else {
				numPegsToEraseAtHome = NUM_PEGS_PER_PLAYER - getNumPegsHome(pos, i);
			}
			for(int j=0; j<numPegsToEraseAtHome; j++) {
				ret = ret.replace("(" + ((char)('A' + i)) + "" + (j+1) + ")", "( )");
			}
			for(int j=numPegsToEraseAtHome; j<NUM_PEGS_PER_PLAYER; j++) {
				ret = ret.replace("(" + ((char)('A' + i)) + "" + (j+1) + ")", "("+ ((char)('A' + i)) +")");
			}
		}
		
		for(int i=0; i<pos.length; i++) {
			if(pos[i] != EMPTY) {
				ret = ret.replace("(" + i + ")", "(" + ((char)('A' + pos[i])) + ")");
			}
		}
		
		ret = ret.replace("(L)", "(" + currentRoll + ")");
		
		
		
		return ret;
	}
	
	
	
	
	
	//pre: game is over
	public static int getResult(int pos[]) {
		
		for(int i=0; i<NUM_PLAYER_SLOTS; i++) {
			boolean isOverForIndexI = true;
			int indexFirstHomer = NUM_POS_PER_QUARTER * i + NUM_POS_IN_CIRCLE;
			for(int j=0; j<NUM_PEGS_PER_PLAYER; j++) {
				if(pos[indexFirstHomer + j] != i) {
					isOverForIndexI = false;
				}
			}
			if(isOverForIndexI) {
				return i;
			}
		}
		
		return -1;
	}
	
	public static boolean isGameOver(int pos[]) {
		if(getResult(pos) >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	public static String getListOfChoicesString(int pos[], int turnIndex, int roll) {
		String ret = "skip";
		
		boolean landingSites[] = new boolean[pos.length];
		for(int i=0; i<landingSites.length; i++) {
			landingSites[i] = false;
		}
		
		
		if(isLegalMove(pos, turnIndex, HOME, roll)) {
			ret += ", HOME to " + getLandingPos(pos, turnIndex, HOME, roll);
			landingSites[getStartIndex(turnIndex)] = true;
		}
		
		
		for(int i=0; i<pos.length; i++) {
			if(getLandingPos(pos, turnIndex, i, roll) != ILLEGAL) {
				ret += ", " + i + " to " + getLandingPos(pos, turnIndex, i, roll);
				if(landingSites[ getLandingPos(pos, turnIndex, i, roll)] == true) {
					System.out.println("ERROR: 2 ways to land on the same spot.");
					System.exit(1);
				} else {
					landingSites[ getLandingPos(pos, turnIndex, i, roll)] = true;
				}
			}
		}
		
		for(int i=0; i<pos.length; i++) {
			if(landingSites[i] == true && isLegalMove(pos, turnIndex, i, roll) == false && getNumPegsHome(pos, turnIndex) > 0) {
				System.out.println("ERROR: didn't count legal move index " + i);
				System.exit(1);
			} else if(landingSites[i] == false && isLegalMove(pos, turnIndex, i, roll) == true && getLandingPos(pos, turnIndex, i, roll) == ILLEGAL) {
				System.out.println("ERROR: why is  " + i + " a legal move?");
				System.exit(1);
			}
		}
		
		return ret;
	}
	
	//move could either denote the landing index or starting index.
		//(I want it to be flexible)	
	//Returns true if the player is an illegal move.
	public static boolean isLegalMove(int pos[], int turnIndex, int move, int roll) {
		if(move == SKIP) {
			return true;
		
		//move == index of peg to move
		} else if(getLandingPos(pos, turnIndex, move, roll) != ILLEGAL && pos[getLandingPos(pos, turnIndex, move, roll)] != turnIndex) {
			return true;
			
		//getting a peg out of HOME: (For IRWIN: roll has to be 1 to get out.)
		} else if(roll == 1 && move == getStartIndex(turnIndex) ){
			if(getNumPegsHome(pos, turnIndex) > 0 && pos[getStartIndex(turnIndex)] != turnIndex) {
				return true;
			} else {
				return false;
			}
		//move == index of peg after move and peg was somewhere outside of home:
		} else if(move >= 0){
			for(int i=0; i<pos.length; i++) {
				if(pos[i] == turnIndex) {
					if(getLandingPos(pos, turnIndex, i, roll) == move && pos[move] != turnIndex) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
		
	
	
	
	//returns -1 if it's illegal to move that peg.
	//
	public static int getLandingPos(int pos[], int turnIndex, int locationIndex, int roll) {
		if(locationIndex == HOME) {
			//IRWIN VERSION:
				if(roll == 1 && pos[getStartIndex(turnIndex)] != turnIndex && getNumPegsHome(pos, turnIndex) > 0) {
					return getStartIndex(turnIndex);
				} else {
					return ILLEGAL;
				}
			//END IRWIN VERSION
		} else {
			if(isLegalStartingIndex(pos, turnIndex, locationIndex)) {
				if(isInFinishLocation(locationIndex) == false) {
					int dist1 = getDistToFinish(turnIndex, locationIndex);
					int dist2 = getDistToFinish(turnIndex, (locationIndex + roll) % NUM_POS_IN_CIRCLE);

					if(dist1 > dist2) {
						//GOING TO AROUND:
						int landingPos = (locationIndex + roll) % NUM_POS_IN_CIRCLE;
						if(pos[landingPos] == turnIndex) {
							return ILLEGAL;
						} else {
							return landingPos;
						}
					} else {
						//GOING TO FINISH:
						int landingPos = (locationIndex + roll) % NUM_POS_IN_CIRCLE  + NUM_POS_IN_CIRCLE;
						if(landingPos - (getStartIndex(turnIndex) + NUM_POS_IN_CIRCLE) >= NUM_PEGS_PER_PLAYER) {
							return ILLEGAL;
						} else if(pos[landingPos] == turnIndex) {
							return ILLEGAL;
						} else {
							return landingPos;
						}
					}
				} else {
					int landingPos = locationIndex + roll;
					if(landingPos - (getStartIndex(turnIndex) + NUM_POS_IN_CIRCLE) >= NUM_PEGS_PER_PLAYER) {
						return ILLEGAL;
					} else if(pos[landingPos] == turnIndex) {
						return ILLEGAL;
					} else {
						return landingPos;
					}
				}
			} else {
				return ILLEGAL;
			}
		}
	}
	

	
	public static boolean isLegalStartingIndex(int pos[], int turnIndex, int move) {
		if(move >= 0 && move < pos.length && pos[move] == turnIndex) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isInFinishLocation(int locationIndex) {
		if(locationIndex >= NUM_POS_IN_CIRCLE) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int getDistToFinish(int playerIndex, int locationIndex) {
		if(locationIndex < NUM_POS_IN_CIRCLE) {

			int endPos = NUM_POS_PER_QUARTER * playerIndex;
			int ret=  (endPos - locationIndex + NUM_POS_IN_CIRCLE) % NUM_POS_IN_CIRCLE;
			if(ret == 0) {
				ret += NUM_POS_IN_CIRCLE;
			}
			return ret;
			
		} else {
			System.out.println("WARNING: you are asking for the distance to finish when the peg is already passed the finish line.");
			return 0;
		}
	}
	
	public static int getStartIndex(int playerIndex) {
		return NUM_POS_PER_QUARTER * playerIndex;
	}
	
	public static int getNumPegsHome(int pos[], int playerIndex) {
		int ret = NUM_PEGS_PER_PLAYER;
		for(int i=0; i<pos.length; i++) {
			if(pos[i] == playerIndex) {
				ret--;
			}
		}
		return ret;
	}
}

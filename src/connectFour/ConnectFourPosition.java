package connectFour;

import severalClientProject.ProfileInterface;

public class ConnectFourPosition {
	
	public static int HEIGHT = 6;
	public static int WIDTH = 7;
	
	private short pos[][] = new short[HEIGHT][WIDTH];
	private boolean isRedTurn = true;
	private ProfileInterface playersWatchingOrPlaying[];
	
	
	public static short EMPTY = 0;
	public static short RED = 1;
	public static short BLACK = 2;
	
	private gameUtils.GameReplayPrinter recorder;
	
	
	//if WINNING_UTILITY >= 10000 or <= -10000
	public static int WINNING_UTILITY = 10000;
	
	
	public ConnectFourPosition(short pos[][], boolean isRedTurn, ProfileInterface playersWatching[], gameUtils.GameReplayPrinter recorder) {
		for(int i=0; i<pos.length; i++) {
			for(int j=0; j<pos[0].length; j++) {
				this.pos[i][j] = pos[i][j];
			}
		}
		this.isRedTurn = isRedTurn;
		this.playersWatchingOrPlaying = playersWatching;

		this.recorder = recorder;
		
	}

	public ConnectFourPosition(ProfileInterface playersWatching[], gameUtils.GameReplayPrinter recorder) {
		for(int i=0; i<HEIGHT; i++) {
			for(int j=0; j<WIDTH; j++) {
				pos[i][j] = 0;
			}
		}
		isRedTurn = true;
		this.playersWatchingOrPlaying = playersWatching;
		
		this.recorder = recorder;
		
	}
	
	
	public void printPos() {
		String message ="\n";
		for(int i=0; i<HEIGHT; i++) {
			message += "|";
			for(int j=0; j<WIDTH; j++) {
				if(pos[i][j] == EMPTY) {
					message += " ";
				} else if(pos[i][j] == RED) {
					message += "R";
				} else if(pos[i][j] == BLACK) {
					message += "B";
				}
				message += "|";
			}
			message += '\n';
		}
		message += "-------------------------";
		
		recordOutput(message);
		sendMessageToGroup(this.playersWatchingOrPlaying, message);
		
		if(getRedUtility() >= WINNING_UTILITY) {
			sendMessageToGroup(this.playersWatchingOrPlaying, "Red wins!");
			this.recordOutput("Red wins!");
		} else if(getRedUtility() <= -WINNING_UTILITY) {
			sendMessageToGroup(this.playersWatchingOrPlaying, "Black wins!");
			this.recordOutput("Black wins!");
		} else {
			if(this.isRedTurn) {
				sendMessageToGroup(this.playersWatchingOrPlaying, "Red to play");
			} else {
				sendMessageToGroup(this.playersWatchingOrPlaying, "Black to play");
			}
		}

	}
	
	public ConnectFourPosition playLegalTurn(int column) {
		if(couldPlayColumn(column)) {
			int heightInsert = 0;
			for(int i=0; i<HEIGHT; i++) {
				if(pos[i][column] == EMPTY) {
					heightInsert = i;
				}
			}
			
			recordOutput("Move to column " + column + ":");
			recordCommandMsg("Move to column " + column + ":");
			
			
			return getPositionAfterInsert(heightInsert, column);
			
		}
		
		sendMessageToGroup(this.playersWatchingOrPlaying, "WARNING: you can't play there!");
		return null;
	}
	
	
	public boolean couldPlayColumn(int column) {
		if(column >=0 && column < WIDTH) {
			
			if(pos[0][column] == EMPTY) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}
	
	private ConnectFourPosition getPositionAfterInsert(int height, int column) {
		ConnectFourPosition next = this.makeHardCopy();
		if(next.isRedTurn == true) {
			next.pos[height][column] = RED;
		} else {
			next.pos[height][column] = BLACK;
		}
		
		//switch the turn:
		next.isRedTurn = !next.isRedTurn;
		
		return next;
	}
	
	private ConnectFourPosition makeHardCopy() {
		ConnectFourPosition hardCopy = new ConnectFourPosition(this.pos, this.isRedTurn, playersWatchingOrPlaying, this.recorder);
		
		
		return hardCopy;
	}
	
	
	public int getRedUtility() {
		
		boolean redWins;
		boolean blackWins;
		
		
		for(int i=0; i<this.pos.length - 3; i++) {
			for(int j=0; j<this.pos[0].length; j++) {
				redWins = true;
				blackWins = true;
				for(int k=0; k<4; k++) {
					if(this.pos[i + k][j] != BLACK) {
						blackWins = false;
					}
					
					if(this.pos[i + k][j] != RED) {
						redWins = false;
					}
				}
				
				if(redWins) {
					return WINNING_UTILITY;
				} else if(blackWins) {
					return -WINNING_UTILITY;
				}
			}
		}
		
		
		for(int i=0; i<this.pos.length; i++) {
			for(int j=0; j<this.pos[0].length - 3; j++) {
				redWins = true;
				blackWins = true;
				for(int k=0; k<4; k++) {
					if(this.pos[i][j + k] != BLACK) {
						blackWins = false;
					}
					
					if(this.pos[i][j + k] != RED) {
						redWins = false;
					}
				}
				
				if(redWins) {
					return WINNING_UTILITY;
				} else if(blackWins) {
					return -WINNING_UTILITY;
				}
			}
		}
		
		
		
		for(int i=0; i<this.pos.length - 3; i++) {
			for(int j=0; j<this.pos[0].length - 3; j++) {
				redWins = true;
				blackWins = true;
				for(int k=0; k<4; k++) {
					if(this.pos[i + k][j + k] != BLACK) {
						blackWins = false;
					}
					
					if(this.pos[i + k][j + k] != RED) {
						redWins = false;
					}
				}
				
				if(redWins) {
					return WINNING_UTILITY;
				} else if(blackWins) {
					return -WINNING_UTILITY;
				}
			}
		}
		
		
		
		for(int i=0; i<this.pos.length - 3; i++) {
			for(int j=3; j<this.pos[0].length; j++) {
				redWins = true;
				blackWins = true;
				for(int k=0; k<4; k++) {
					if(this.pos[i + k][j - k] != BLACK) {
						blackWins = false;
					}
					
					if(this.pos[i + k][j - k] != RED) {
						redWins = false;
					}
				}
				
				if(redWins) {
					return WINNING_UTILITY;
				} else if(blackWins) {
					return -WINNING_UTILITY;
				}
			}
		}
		
		
		
		return 0;
	}
	
	public boolean isGameOver() {
		if(this.getRedUtility() <= -WINNING_UTILITY || this.getRedUtility() >= WINNING_UTILITY) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isTie() {
		for(int j=0; j<pos[0].length; j++) {
			if(pos[0][j] == EMPTY) {
				return false;
			}
		}
		
		return true;
	}
	
	public short[][] getHardCopyPos() {
		short ret[][] = new short[pos.length][pos[0].length];
		for(int i=0; i<pos.length; i++) {
			for(int j=0; j<pos[0].length; j++) {
				ret[i][j] = pos[i][j];
			}
		}
		
		return ret;
	}
	
	public boolean isRedTurn() {
		return isRedTurn;
	}
	
	
	//ends the game and destroys the recorder:
	public void endGame() {
		recordCommandMsg("GAME OVER!");
		recordOutput("GAME OVER!");
		recorder.close();
	}
	
	
	public void recordCommandMsg(String command) {
		recorder.printCommand(command);
	}
	
	public void recordOutput(String output) {
		recorder.printOutput(output);
	}
	
	//TODO: create a connect 4 middle-man class.
	public static void sendMessageToGroup(ProfileInterface players[], String message) {
		try {
			for(int i=0; i<players.length; i++) {
				if(players[i] != null) {
					players[i].sendMessageToClient("From connect 4: " + message);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO: create a connect 4 middle-man class.
	public static void sendMessageToPlayer(ProfileInterface player, String message) {
		try {
			player.sendMessageToClient("From connect 4: " + message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
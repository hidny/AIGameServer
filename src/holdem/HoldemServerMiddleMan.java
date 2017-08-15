package holdem;

import java.io.PrintWriter;

import severalClientProject.ProfileInterface;

public class HoldemServerMiddleMan implements severalClientProject.Game {

	private ProfileInterface clientPlayersPlaying[];
	private String playerMoves[];
	
	public static String UNENTERED_MOVE = "";
	
	private PrintWriter commandFile = null;
	private PrintWriter outputFile = null;
	
	private int numberOfPlayers = 0;
	
	
	public void setCommandFileWriter(PrintWriter commandFile) {
		//if this is null, then don't record the commands.
		//This should be null when playing a replay.
		// and create a record command function.
		this.commandFile = commandFile;
	}
	
	public void setOutputFileWriter(PrintWriter outputFile) {
		this.outputFile = outputFile;
	}
	
	//getter for deck class.
	public PrintWriter getCommandFile() {
		return commandFile;
	}
	
	public void startGameForClients(ProfileInterface player[]) {
		this.clientPlayersPlaying = player;
		
	
		//Disallow the player array from having nulls in between players:
		//TODO: test this loop
		this.numberOfPlayers = 0;
		
		for(int i=0; i<this.clientPlayersPlaying.length; i++) {
			if(this.clientPlayersPlaying[i] == null) {
				for(int j=i+1; j<this.clientPlayersPlaying.length; j++) {
					if(this.clientPlayersPlaying[j] != null) {
						this.clientPlayersPlaying[i] = this.clientPlayersPlaying[j];
						this.clientPlayersPlaying[j] = null;
						break;
					}
				}
			} else {
				numberOfPlayers++;
			}
		}
		//end todo.
		
		this.playerMoves = new String[player.length];
		for(int i=0; i<this.playerMoves.length; i++) {
			this.playerMoves[i] = "";
		}
		
		this.playGame();
		
		if(this.commandFile != null) {
			this.commandFile.close();
		}
		if(this.outputFile != null) {
			this.outputFile.close();
		}
		
	}
	
	
	
	public void playGame() {
		HeadsUpMain.startHeadsUp(("-p " + this.numberOfPlayers + " -recordNext").split(" "), this);
	}
	
	public boolean isInList(ProfileInterface player) {
		if(clientPlayersPlaying != null) {
			for(int i=0; i<clientPlayersPlaying.length; i++) {
				//TODO: check player name in the future:
				if(clientPlayersPlaying[i] == player) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public ProfileInterface[] getClientPlayers() {
		return clientPlayersPlaying;
	}
	
	
	public void submitClientQuery(ProfileInterface player, String query) {
		String move = UNENTERED_MOVE;
		boolean clientRecognized = false;
		
		if(query.toLowerCase().startsWith("/move") && isInList(player) ) {
			try {
				move = query.split(" ")[1];
				
			} catch (Exception e) {
				move = UNENTERED_MOVE;
			}
		} else {
			
			sendMessageToGroup(player.getClientName() + ": " + query);
		}
		
		if(move.equals(UNENTERED_MOVE) == false) {
			for(int i=0; i<clientPlayersPlaying.length; i++) {
				if(clientPlayersPlaying[i] == player) {
					playerMoves[i] = move;
					clientRecognized = true;
					break;
				}
			}
			
			
			if(clientRecognized == false) {
				sendMessageToGroup("Error: unknown player trying to make a move.");
			}
		}
	}
	
	public String getNextMove(ProfileInterface player) {
		
		if(this.isInList(player)) {
			for(int i=0; i<clientPlayersPlaying.length; i++) {
				if(clientPlayersPlaying[i] == player) {
					return playerMoves[i];
				}
			}
			sendMessageToGroup("Error: unknown player trying to get a move.");
		}
		
		return UNENTERED_MOVE;
		
	}
	
	//This tells the server the game acknowledged the move request and is now waiting for another move.
	public void setMoveTaken(ProfileInterface player) {
		
		boolean clientRecognized = false;
		
		if(clientPlayersPlaying != null) {
			for(int i=0; i<clientPlayersPlaying.length; i++) {
				if(clientPlayersPlaying[i] == player) {
					playerMoves[i] = UNENTERED_MOVE;
					clientRecognized = true;
				}
			}
		}
		
		if(clientRecognized == false) {
			sendMessageToGroup("Error: move taken by whom?");
		}
	}
	
	
	public void recordCommand(String command) {
		if(commandFile != null) {
			commandFile.println(command);
		}
	}
	
	public void sendMessageToGroup(String message) {
		
		try {
			if(clientPlayersPlaying != null) {
				for(int i=0; i<clientPlayersPlaying.length; i++) {
					if(clientPlayersPlaying[i] != null) {
						clientPlayersPlaying[i].sendMessageToClient("From holdem(public): " + message);
						
					}
				}
			}
			
			if(outputFile != null) {
				outputFile.println(message);
				outputFile.flush();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//pre: MiniServer player is NOT NULL
	public void sendMessageToPlayer(ProfileInterface player, String message) {
		sendMessageToPlayer(player, player.getClientName(), message);
		
	}
	
	//pre: MiniServer player is NOT NULL
	public void sendMessageToPlayer(String name, String message) {
		sendMessageToPlayer(null, name, message);
		
	}
	
	public void sendMessageToPlayer(ProfileInterface player, String name, String message) {
		try {
			if(player != null) {
				player.sendMessageToClient("From holdem(private): " + message);
			}
			
			if(outputFile != null) {
				outputFile.println(name + ": " + message);
				outputFile.flush();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

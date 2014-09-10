package mellow;

import gameUtils.GameReplayPrinter;

import java.io.PrintWriter;

import severalClientProject.MiniServer;

public class MellowServerMiddleMan implements severalClientProject.Game {

	private MiniServer clientPlayersPlaying[];
	private ClientPlayerDecider clientPlayer[];
	
	int NUM_PLAYERS_IN_MELLOW = 4;
	
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
	
	public void startGameForClients(MiniServer player[]) {
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
		//end TODO
		
		if(numberOfPlayers	>=	NUM_PLAYERS_IN_MELLOW) {
			this.clientPlayer = new ClientPlayerDecider[player.length];
			for(int i=0; i<NUM_PLAYERS_IN_MELLOW; i++) {
				this.clientPlayer[i] = new ClientPlayerDecider( player[i].getClientName() );
			}
			
			//TODO: create output files here!
			//TODO: create cmd output file here!
			
			this.playGame(this.clientPlayer);
			
			if(this.commandFile != null) {
				this.commandFile.close();
			}
			if(this.outputFile != null) {
				this.outputFile.close();
			}
		} else {
			sendMessageToGroup("ERROR: not enough players to player mellow. You need 4 players!");
		}
	}
	
	public void playGame(PlayerDecider player[]) {
		
		PlayerDecider red[] = new PlayerDecider[2];
		PlayerDecider blue[] = new PlayerDecider[2];
		red[0] = player[0];
		red[1] = player[2];
		
		blue[0] = player[1];
		blue[1] = player[3];
		
		int num = GameReplayPrinter.getTestCaseNumber(Position.GAME_NAME);
		
		//TODO: check if replay runner!
		this.setCommandFileWriter( GameReplayPrinter.getNewCommandWriter(Position.GAME_NAME, num) );
		this.setOutputFileWriter( GameReplayPrinter.getNewOuput(Position.GAME_NAME, num) );
		
		Position.startMellow(this, red, blue);
		
	}
	
	public boolean isReadingReplay() {
		if(this.commandFile == null) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean isInList(MiniServer player) {
		if(clientPlayersPlaying != null) {
			for(int i=0; i<clientPlayersPlaying.length; i++) {
				if(clientPlayersPlaying[i].getClientName().equals(player.getClientName())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public MiniServer[] getClientPlayers() {
		return clientPlayersPlaying;
	}
	
	
	public void submitClientQuery(MiniServer player, String query) {
		String move = UNENTERED_MOVE;
		boolean clientRecognized = false;
		
		if(query.toLowerCase().startsWith("/move") && isInList(player) ) {
			try {
				move = query.split(" ")[1];
				
			} catch (Exception e) {
				move = UNENTERED_MOVE;
			}
		} else {
			
			sendMessageToGroup(player.getClientName() + ": " + query, false);
		}
		
		if(move.equals(UNENTERED_MOVE) == false) {
			for(int i=0; i<clientPlayer.length; i++) {
				if(clientPlayer[i].getName().equals(player.getClientName())) {
					clientPlayer[i].setMove(move);
					clientRecognized = true;
				}
			}
			
			if(clientRecognized == false) {
				sendMessageToGroup("Error: unknown player trying to make a move.");
			}
		}
	}
	
	public void recordCommand(String command) {
		if(commandFile != null) {
			commandFile.print(command);
			commandFile.flush();
		}
	}
	
	public void sendMessageToGroup(String message) {
		sendMessageToGroup(message, true);
		
	}
	public void sendMessageToGroup(String message, boolean record) {
		
		try {
			if(clientPlayersPlaying != null) {
				for(int i=0; i<clientPlayersPlaying.length; i++) {
					if(clientPlayersPlaying[i] != null) {
						clientPlayersPlaying[i].sendMessageToClient("From Mellow(public): " + message);
						
					}
				}
			}
			
			if(outputFile != null && record) {
				outputFile.println(message);
				outputFile.flush();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//pre: MiniServer player is NOT NULL
	public void sendMessageToPlayer(MiniServer player, String message) {
		sendMessageToPlayer(player, player.getClientName(), message);
		
	}
	
	public void sendMessageToPlayer(String name, String message) {
		//Check if the player is a client:
		MiniServer playerToSendTo = null;
		if(clientPlayersPlaying != null) {
			for(int i=0; i<clientPlayersPlaying.length; i++) {
				if(name.equals(clientPlayersPlaying[i].getClientName())) {
					playerToSendTo = clientPlayersPlaying[i];
					break;
				}
			}
		}
		//END check.
		sendMessageToPlayer(playerToSendTo, name, message);
		
	}
	
	public void sendMessageToPlayer(MiniServer player, String name, String message) {
		try {
			if(player != null) {
				player.sendMessageToClient("From Mellow(private): " + message);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

package gameUtils;
import java.io.PrintWriter;

import severalClientProject.ProfileInterface;

public abstract class ServerGameMiddleMan implements severalClientProject.Game {

	protected ProfileInterface clientPlayersPlaying[];
	protected PrintWriter commandFile = null;
	protected PrintWriter outputFile = null;
	
	
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
	

	//TO BE overridden.
	public void startGameForClients(ProfileInterface player[]) {
		startGameForClients(player, "");
	}
	
	public void startGameForClients(ProfileInterface player[], String variation) {
		for(int i=0; i<player.length; i++) {
			if(player[i] != null) {
				try {
					player[i].sendMessageToClient("ERROR: No game specified!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public boolean isReadingReplay() {
		if(this.commandFile == null) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean isInList(ProfileInterface player) {
		if(clientPlayersPlaying != null) {
			for(int i=0; i<clientPlayersPlaying.length; i++) {
				if(clientPlayersPlaying[i] != null && clientPlayersPlaying[i].getClientName().toLowerCase().equals(player.getClientName().toLowerCase())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public ProfileInterface[] getClientPlayers() {
		return clientPlayersPlaying;
	}
	
	
	//TO BE overwritten.
	public void submitClientQuery(ProfileInterface player, String query) {
		sendMessageToGroup(player.getClientName() + ": " + query, false);
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
			if(clientPlayersPlaying != null && clientPlayersPlaying.length > 0) {
				clientPlayersPlaying[0].sendMessageToAllClientsInGame("From Game(public): " + message, clientPlayersPlaying);
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
	public void sendMessageToPlayer(ProfileInterface player, String message) {
		sendMessageToPlayer(player, player.getClientName(), message);
		
	}
	
	public void sendMessageToPlayer(String name, String message) {
		//Check if the player is a client:
		ProfileInterface playerToSendTo = null;
		if(clientPlayersPlaying != null) {
			for(int i=0; i<clientPlayersPlaying.length; i++) {
				if(clientPlayersPlaying[i] != null && name.equals(clientPlayersPlaying[i].getClientName())) {
					playerToSendTo = clientPlayersPlaying[i];
					break;
				}
			}
		}
		//END check.
		sendMessageToPlayer(playerToSendTo, name, message);
		
	}
	
	public void sendMessageToPlayer(ProfileInterface player, String name, String message) {
		try {
			if(player != null) {
				player.sendMessageToClient("From Game(private): " + message);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

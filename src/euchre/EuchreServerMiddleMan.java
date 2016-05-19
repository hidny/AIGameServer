package euchre;

import gameUtils.GameReplayPrinter;
import severalClientProject.MiniServer;
import gameUtils.ServerGameMiddleMan;

public class EuchreServerMiddleMan extends ServerGameMiddleMan {
	
	private ClientPlayerDecider clientPlayer[];
	
	int NUM_PLAYERS_IN_EUCHRE = 4;
	
	public static String UNENTERED_MOVE = "";
	
	
	private int numberOfPlayers = 0;
	
	
	public void startGameForClients(MiniServer player[], String variation) {
		
		EuchreVariation euchreVariation = new EuchreVariation(variation);
		
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
		
		if(numberOfPlayers	>=	NUM_PLAYERS_IN_EUCHRE) {
			this.clientPlayer = new ClientPlayerDecider[player.length];
			for(int i=0; i<NUM_PLAYERS_IN_EUCHRE; i++) {
				this.clientPlayer[i] = new ClientPlayerDecider( player[i].getClientName() );
			}
			
			
			this.playGame(this.clientPlayer, euchreVariation);
			
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
	public void playGame(PlayerDecider player[], EuchreVariation variation) {

		PlayerDecider red[] = new PlayerDecider[2];
		PlayerDecider blue[] = new PlayerDecider[2];
		red[0] = player[0];
		red[1] = player[2];
		
		blue[0] = player[1];
		blue[1] = player[3];
		
		int num = GameReplayPrinter.getTestCaseNumber(Position.GAME_NAME);
		
		this.setCommandFileWriter( GameReplayPrinter.getNewCommandWriter(Position.GAME_NAME, num) );
		this.setOutputFileWriter( GameReplayPrinter.getNewOuput(Position.GAME_NAME, num) );
		
		Position.startEuchre(this, variation, red, blue);
	}
	
	
	//pre: it's a move and the client is recognizable.
	public void submitClientQuery(MiniServer player, String query) {
		String move = UNENTERED_MOVE;
		boolean clientRecognized = false;
		
		if(query.toLowerCase().startsWith("/move") && isInList(player) ) {
			try {
				move = "";
				for(int i=1; i<query.split(" ").length; i++) {
					move += query.split(" ")[i] + " ";
					
				}
				
			} catch (Exception e) {
				move = UNENTERED_MOVE;
			}
		} else {
			if(isInList(player) == false) {
				System.out.println("ERROR: " + player.getClientName() + " is not in list");
				System.exit(1);
			}
			//System.out.println("Query from " + player.getClientName() + ": " + query);
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
	
}

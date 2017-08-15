package frustrationIrwin;

import gameUtils.GameReplayPrinter;

import severalClientProject.ProfileInterface;
import gameUtils.ServerGameMiddleMan;

public class FrustrationServerMiddleMan extends ServerGameMiddleMan {
	
	private ClientPlayerDecider clientPlayer[];
	
	int MIN_NUM_PLAYERS_FRUSTRATION = 2;
	int MAX_NUM_PLAYERS_FRUSTRATION = 4;
	
	public static String UNENTERED_MOVE = "";
	
	
	private int numberOfPlayers = 0;
	
	
	public void startGameForClients(ProfileInterface player[]) {
		this.clientPlayersPlaying = player;
		
		//Disallow the player array from having nulls in between players:
		this.numberOfPlayers = 0;
		
		for(int i=0; i<this.clientPlayersPlaying.length; i++) {
			if(this.clientPlayersPlaying[i] != null) {
				numberOfPlayers++;
			}
		}
		
		if(numberOfPlayers	>=	MIN_NUM_PLAYERS_FRUSTRATION && numberOfPlayers <= MAX_NUM_PLAYERS_FRUSTRATION) {
			this.clientPlayer = new ClientPlayerDecider[player.length];
			for(int i=0; i<MAX_NUM_PLAYERS_FRUSTRATION; i++) {
				if(player[i] != null) {
					this.clientPlayer[i] = new ClientPlayerDecider( player[i].getClientName() );
				} else {
					player[i] = null;
				}
			}
			
			this.playGame(this.clientPlayer);
			
			if(this.commandFile != null) {
				this.commandFile.close();
			}
			if(this.outputFile != null) {
				this.outputFile.close();
			}
		} else {
			sendMessageToGroup("ERROR: there should be 2 to 4 players, but counted " + numberOfPlayers + "!");
			System.exit(1);
		}
	}
	public void playGame(PlayerDecider player[]) {
		
		int num = GameReplayPrinter.getTestCaseNumber(Position.GAME_NAME);
		
		this.setCommandFileWriter( GameReplayPrinter.getNewCommandWriter(Position.GAME_NAME, num) );
		this.setOutputFileWriter( GameReplayPrinter.getNewOuput(Position.GAME_NAME, num) );
		
		Position.startFrustration(this, player);
	}
	
	
	//pre: it's a move and the client is recognizable.
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
			if(isInList(player) == false) {
				System.out.println("ERROR: in submitClientQuery for frustration: " + player.getClientName() + " is not in list");
				System.exit(1);
			}
			//System.out.println("Query from " + player.getClientName() + ": " + query);
			sendMessageToGroup(player.getClientName() + ": " + query, false);
		}
		
		if(move.equals(UNENTERED_MOVE) == false) {
			for(int i=0; i<clientPlayer.length; i++) {
				if(clientPlayer[i] != null && clientPlayer[i].getName().equals(player.getClientName())) {
					clientPlayer[i].setMove(move);
					clientRecognized = true;
				}
			}
			
			if(clientRecognized == false) {
				sendMessageToGroup("Error: unknown player trying to make a move.");
				System.exit(1);
			}
		}
	}
	
}

package mellow;

import gameUtils.GameReplayPrinter;

import severalClientProject.ProfileInterface;
import gameUtils.ServerGameMiddleMan;
import random.card.Deck;

public class MellowServerMiddleMan extends ServerGameMiddleMan {
	
	private ClientPlayerDecider clientPlayer[];
	
	int NUM_PLAYERS_IN_MELLOW = 4;
	
	public static String UNENTERED_MOVE = "";
	
	
	private int numberOfPlayers = 0;
	
	private String gameArgs[] = null;

	public MellowServerMiddleMan(String gameArgs[]) {
		this.gameArgs = gameArgs;
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
		//end TODO
		
		if(numberOfPlayers	>=	NUM_PLAYERS_IN_MELLOW) {
			this.clientPlayer = new ClientPlayerDecider[player.length];
			for(int i=0; i<NUM_PLAYERS_IN_MELLOW; i++) {
				this.clientPlayer[i] = new ClientPlayerDecider( player[i].getClientName() );
			}
			
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
		
		this.setCommandFileWriter( GameReplayPrinter.getNewCommandWriter(Position.GAME_NAME, num) );
		this.setOutputFileWriter( GameReplayPrinter.getNewOuput(Position.GAME_NAME, num) );
		
		if(gameArgs != null) {
			//First 2 args will be the initial score
			//Third arg is the dealer index
			
			//start with intial score:
			if(gameArgs.length < 3) {
				Position.startMellow(this, red, blue, Integer.parseInt(gameArgs[0]), Integer.parseInt(gameArgs[1]), Position.RANDOM_DEALER_POSITION);
				
			//start with a set dealer position:
			} else if(gameArgs.length < 4) {
				Position.startMellow(this, red, blue, Integer.parseInt(gameArgs[0]), Integer.parseInt(gameArgs[1]), Integer.parseInt(gameArgs[2]));
				
			//start with a rigged deck:
			} else {
				
				Deck firstDeckRigged = new random.card.OneTimeRiggedDeck(this.getCommandFile(), gameArgs);

				//Method 1: (Actual deck)
				//-fulldeck
				//-rigFirstDeck [3D TD 5D 7C 7S 6S 5C JS 5H JC 8S 2S 8D QH JD KD 8H TS TC TH 7D 2C AC 4C 6D QS QC AS 2H KC AD 9D 6H JH 9C 3S 3C KS QD 5S AH 4H 9S 4D 8C 9H 3H KH 7H 6C 2D 4S] 

				//or Method 2: give cards to players
				// sample: /create mellow mellowpy  0 0 0 -handrig [AS ADKSQSJSKD2C][][3C][2D3D,4D]
								//Deck to rig
				Position.startMellow(this, red, blue, firstDeckRigged, Integer.parseInt(gameArgs[0]), Integer.parseInt(gameArgs[1]), Integer.parseInt(gameArgs[2]));

			}
		} else {
			Position.startMellow(this, red, blue);
			
		}
	}
	
	
	//pre: it's a move and the client is recognizable.
	public void submitClientQuery(ProfileInterface player, String query) {
		String move = UNENTERED_MOVE;
		boolean clientRecognized = false;
		
		if(query.toLowerCase().startsWith("/move") && isInList(player) ) {
			try {
				move = query.split(" ")[1].trim();
				
				if(move.equals("")) {
					move = UNENTERED_MOVE;
				}
			} catch (Exception e) {
				move = UNENTERED_MOVE;
			}
		} else {
			if(isInList(player) == false) {
				System.out.println(player.getClientName() + " is not in list");
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

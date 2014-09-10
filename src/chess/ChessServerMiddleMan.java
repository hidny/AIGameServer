package chess;

import severalClientProject.MiniServer;

//TODO:
public class ChessServerMiddleMan implements severalClientProject.Game {

	private MiniServer playersWatchingOrPlaying[];
	
	public static String UNENTERED_MOVE = "";
	
	private MiniServer player1 = null;
	private MiniServer player2 = null;
	
	
	public String player1CurrentMove = UNENTERED_MOVE;
	public String player2CurrentMove = UNENTERED_MOVE;
	
	
	//TODO
	public void startGameForClients(MiniServer player[]) {
		this.playersWatchingOrPlaying = player;
		
		this.getPlayerNamesOfCompetitors(player);
		this.playGame();
	}
	
	
	public void playGame() {
		//TODO:
		Player white = new ClientPlayer(player1, this);
		Player black = new ClientPlayer(player2, this);
		
		Player competitors[] = new Player[]{white, black};
		ChessMatchFunctions.startMatch(competitors, this);
	}
	
	//TODO: put in util function
	public void getPlayerNamesOfCompetitors(MiniServer player[]) {
		for(int i=0; i<player.length; i++) {
			if(player[i] != null && player1 == null) {
				player1 = player[i];
			} else if(player[i] != null && player2 == null) {
				player2 = player[i];
			}
		}
		
		if(player2 == null) {
			sendMessageToGroup("ERROR: couldn't find 2 players.");
		}
	}
	
	//TODO: put in util function interface
	public void submitClientQuery(MiniServer player, String query) {
		String move = UNENTERED_MOVE;
		if(query.toLowerCase().startsWith("/move") && (player == player1 || player == player2) ) {
			try {
				move = query.split(" ")[1];
				
			} catch (Exception e) {
				move = UNENTERED_MOVE;
			}
		} else {
			
			sendMessageToGroup(player.getClientName() + ": " + query);
		}
		
		if(move.equals(UNENTERED_MOVE) == false) {
			if(player == player1) {
				//TODO: wake up player 1 listener.
				player1CurrentMove = move;
				//if(this.connect4Waiter != null ) {
				//	this.connect4Waiter.notify();
				//}
				
			} else if(player == player2) {
				//TODO: wake up player 2 listener.
				player2CurrentMove = move;
				//if(this.connect4Waiter != null ) {
				//	this.connect4Waiter.notify();
				//}
				
			} else {
				sendMessageToGroup("Error: unknown player trying to make a move.");
			}
		}
	}
	
	public String getNextMove(MiniServer player) {
		if(player == player1) {
			return player1CurrentMove;
			
		} else if(player == player2) {
			return player2CurrentMove;
			
		} else {
			sendMessageToGroup("Error: unknown player trying to get a move.");
			return UNENTERED_MOVE;
		}
	}
	
	public void setMoveTaken(MiniServer player) {
		if(player == player1) {
			player1CurrentMove = UNENTERED_MOVE;
			
		} else if(player == player2) {
			player2CurrentMove = UNENTERED_MOVE;
			
		} else {
			sendMessageToGroup("Error: move taken by whom?");
		}
	}
		
	//TODO: put in util function interface
	public void sendMessageToGroup(String message) {
		try {
			for(int i=0; i<playersWatchingOrPlaying.length; i++) {
				if(playersWatchingOrPlaying[i] != null) {
					playersWatchingOrPlaying[i].sendMessageToClient("From chess: " + message);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO: put in util function interface
	public void sendMessageToPlayer(MiniServer player, String message) {
		try {
			player.sendMessageToClient("From chess: " + message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

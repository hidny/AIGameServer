package connectFour;

import severalClientProject.MiniServer;

public class ConnectFour implements severalClientProject.Game {
	
	
	//TODO: allow for the possibility that players leave
	private MiniServer playersWatchingOrPlaying[];
	
	private MiniServer player1 = null;
	private MiniServer player2 = null;
	
	private static int UNENTERED_MOVE = -1;
	
	private int player1CurrentMove = UNENTERED_MOVE;
	private int player2CurrentMove = UNENTERED_MOVE;
	
	private gameUtils.GameReplayPrinter recorder;
	
	
	//TODO: figure it out:
	//private Thread connect4Waiter = null;
	
	public void startGameForClients(MiniServer player[]) {
		this.playersWatchingOrPlaying = player;
		
		this.getPlayerNamesOfCompetitors(player);
		this.playGame();
	}
	
	
	public void getPlayerNamesOfCompetitors(MiniServer player[]) {
		for(int i=0; i<player.length; i++) {
			if(player[i] != null && player1 == null) {
				player1 = player[i];
			} else if(player[i] != null && player2 == null) {
				player2 = player[i];
			}
		}
		
		if(player2 == null) {
			ConnectFourPosition.sendMessageToGroup(this.playersWatchingOrPlaying, "ERROR: couldn't find 2 players.");
		}
	}
	
	public void playGame() {
		this.recorder = new gameUtils.GameReplayPrinter("connect4");
		
		ConnectFourPosition pos = new ConnectFourPosition(this.playersWatchingOrPlaying, this.recorder);
		
		
		pos.recordCommandMsg(player1.getClientName() + " vs " + player2.getClientName());
		pos.recordOutput(player1.getClientName() + " vs " + player2.getClientName());
		
		
		while(pos.isGameOver() == false && pos.isTie() == false) {
			pos = playTurn(player1, pos);
			
			if(pos.isGameOver() == true) {
				break;
			}
			
			pos = playTurn(player2, pos);
			
		}
		
		ConnectFourPosition.sendMessageToGroup(this.playersWatchingOrPlaying, "Final position:");
		pos.recordOutput("Final position:");
		
		if(pos.isTie()) {
			pos.recordOutput("Tie Game!");
		}
		pos.printPos();
		pos.endGame();
	}
	
	
	public ConnectFourPosition playTurn(MiniServer player, ConnectFourPosition pos) {
		pos.printPos();
		String message = "\n";
		message += player.getClientName() + ": which column?" + "\n";
		message += "0 or 1 or 2 or 3 or 4 or 5 or 6?";
		ConnectFourPosition.sendMessageToPlayer(player, message);
		
		
		int moveNum = UNENTERED_MOVE;
		
		do {
			try {
				while(moveNum == UNENTERED_MOVE) {

					//TODO: sleep
					if(player == player1) {
						//connect4Waiter = Thread.currentThread();
						//connect4Waiter.wait();

						Thread.sleep(1000);
						moveNum = player1CurrentMove;
						player1CurrentMove = UNENTERED_MOVE;
					} else {
						//connect4Waiter = Thread.currentThread();
						//connect4Waiter.wait();
						Thread.sleep(1000);
						
						moveNum = player2CurrentMove;
						player2CurrentMove = UNENTERED_MOVE;
					}
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(pos.couldPlayColumn(moveNum) == false) {
				ConnectFourPosition.sendMessageToPlayer(player, "Play properly!");
				moveNum = UNENTERED_MOVE;
			}
			
		} while(pos.couldPlayColumn(moveNum) == false);
		
		//NOTE: always play the move BEFORE sending the message!
		pos = pos.playLegalTurn(moveNum);
		ConnectFourPosition.sendMessageToGroup(playersWatchingOrPlaying, player.getClientName() + " has entered: " + moveNum);
		
		return pos;
	}
	
	
	//TODO: create a lock for this method.
	public synchronized void submitClientQuery(MiniServer player, String query) {
		int move = UNENTERED_MOVE;
		if(query.toLowerCase().startsWith("/move")) {
			try {
				move = Integer.parseInt(query.split(" ")[1]);
				
			} catch (Exception e) {
				move = UNENTERED_MOVE;
			}
		} else {
			
			ConnectFourPosition.sendMessageToGroup(playersWatchingOrPlaying, player.getClientName() + ": " + query);
		}
		
		if(move >=0 && move <=6) {
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
				ConnectFourPosition.sendMessageToGroup(this.playersWatchingOrPlaying, "Error: unknown player.");
			}
		} else {
			ConnectFourPosition.sendMessageToPlayer(player, "please choose a column between 0 and 6!");
		}
		
	}

}

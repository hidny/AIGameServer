package rockPaperScissors;


import severalClientProject.MiniServer;

public class RockPaperScissors  implements severalClientProject.Game {
	private MiniServer playersWatchingOrPlaying[];
	
	private MiniServer player1 = null;
	private MiniServer player2 = null;
	
	private static String UNENTERED_MOVE = "";
	
	private String player1Answer = "";
	private String player2Answer = "";
	
	private int player1Wins = 0;
	private int player2Wins = 0;
	
	//TODO: make this changeable:
	private static int NUM_WINS_TO_FINISH = 5;
	
	private gameUtils.GameReplayPrinter recorder;
	
<<<<<<< HEAD
	public void startGameForClients(MiniServer player[]) {
=======
	public void startGame(MiniServer player[]) {
>>>>>>> 5d2572ce9e421e991da951240033a84d5e2a9fb6
		this.playersWatchingOrPlaying = player;
		this.getPlayerNamesOfCompetitors(player);
		this.playGame();
	}
	
	
	
	//Copied from connectFour:
	public void getPlayerNamesOfCompetitors(MiniServer player[]) {
		for(int i=0; i<player.length; i++) {
			if(player[i] != null && player1 == null) {
				player1 = player[i];
			} else if(player[i] != null && player2 == null) {
				player2 = player[i];
			}
		}
		
		if(player2 == null) {
			sendMessageToGroup(this.playersWatchingOrPlaying, "ERROR: couldn't find 2 players.");
		}
	}
	
	public void playGame() {
		this.recorder = new gameUtils.GameReplayPrinter("rockPaperScissors");
		this.recordCommandMsg(player1.getClientName() + " vs " + player2.getClientName());
		this.recordOutput(player1.getClientName() + " vs " + player2.getClientName());
		sendMessageToGroup(playersWatchingOrPlaying, player1.getClientName() + " vs " + player2.getClientName());
		
		this.recordCommandMsg("First to " + NUM_WINS_TO_FINISH + " wins wins the game");
		this.recordOutput("First to " + NUM_WINS_TO_FINISH + " wins wins the game");
		sendMessageToGroup(playersWatchingOrPlaying, "First to " + NUM_WINS_TO_FINISH + " wins wins the game");
		
		
		int roundNum = 0;
		
		
		while(player1Wins < NUM_WINS_TO_FINISH && player2Wins < NUM_WINS_TO_FINISH) {
			roundNum++;
			sendMessageToGroup(playersWatchingOrPlaying, "Score: " + player1Wins + "-" + player2Wins);
			this.recordOutput("Score: " + player1Wins + "-" + player2Wins);
			
			sendMessageToGroup(playersWatchingOrPlaying, "Round " + roundNum + ":");
			this.recordOutput("Round " + roundNum + ":");
			
			sendMessageToPlayer(player1, player1.getClientName() + ", (R)ock, (P)aper, or (S)cissors?");
			sendMessageToPlayer(player2, player2.getClientName() + ", (R)ock, (P)aper, or (S)cissors?");
			
			while( (player1Answer.equals("R") || player1Answer.equals("P") || player1Answer.equals("S") ) == false || (player2Answer.equals("R") || player2Answer.equals("P") || player2Answer.equals("S") ) == false) {
				//Waiting.
				//TODO: create listeners!
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			sendMessageToGroup(playersWatchingOrPlaying, player1.getClientName() + ": " + player1Answer + " vs " + player2.getClientName() + ": " + player2Answer);
			this.recordCommandMsg(player1Answer + " " + player2Answer);

			if(player1Answer.equals(player2Answer)) {
				sendMessageToGroup(playersWatchingOrPlaying, "Tie");
				this.recordOutput(player1Answer + " = " + player2Answer);
			} else if((player1Answer.equals("R") && player2Answer.equals("S")) || (player1Answer.equals("S") && player2Answer.equals("P"))  || (player1Answer.equals("P") && player2Answer.equals("R"))) {
				this.recordOutput(player1Answer + " > " + player2Answer);
				player1Wins++;
				if(player1Wins < NUM_WINS_TO_FINISH) {
					sendMessageToGroup(playersWatchingOrPlaying, player1.getClientName() + " wins this round!");
					this.recordOutput(player1.getClientName() + " wins this round!");
				}
			} else if((player2Answer.equals("R") && player1Answer.equals("S")) || (player2Answer.equals("S") && player1Answer.equals("P"))  || (player2Answer.equals("P") && player1Answer.equals("R"))){
				this.recordOutput(player1Answer + " < " + player2Answer);
				player2Wins++;
				if(player2Wins < NUM_WINS_TO_FINISH) {
					sendMessageToGroup(playersWatchingOrPlaying, player2.getClientName() + " wins this round!");
					this.recordOutput(player2.getClientName() + " wins this round!");
				}
			} else {
				sendMessageToGroup(playersWatchingOrPlaying, "WTF! Shit!");
			}
			
			player1Answer = UNENTERED_MOVE;
			player2Answer = UNENTERED_MOVE;
			
		}
		
		if(player1Wins >= NUM_WINS_TO_FINISH) {
			sendMessageToGroup(playersWatchingOrPlaying, player1.getClientName() + " wins the game!");
			this.recordOutput(player1.getClientName() + " wins the game!");
		} else {

			sendMessageToGroup(playersWatchingOrPlaying, player2.getClientName() + " wins the game!");
			this.recordOutput(player1.getClientName() + " wins the game!");
		}
		
		this.recorder.close();
	}
	
	
	public synchronized void submitClientQuery(MiniServer player, String query) {
		String move = UNENTERED_MOVE;
		if(query.toLowerCase().startsWith("/move")) {
			try {
				move = query.split(" ")[1];
				move = move.toUpperCase();
				
			} catch (Exception e) {
				move = UNENTERED_MOVE;
			}
			
			if(move.equals("R") ||  move.equals("P") || move.equals("S")) {
				if(player == player1 && player1Answer.equals(UNENTERED_MOVE)) {
					//TODO: wake up player 1 listener.
					player1Answer = move;
					
					sendMessageToGroup(playersWatchingOrPlaying, player.getClientName() + " submitted query.");
					
				} else if(player == player2 && player2Answer.equals(UNENTERED_MOVE)) {
					//TODO: wake up player 2 listener.
					player2Answer = move;
					
					sendMessageToGroup(playersWatchingOrPlaying, player.getClientName() + " submitted query.");
					
					
				} else if(player == player1 || player == player2 ){
					sendMessageToPlayer(player, "You already submitted an answer!");
				} else {
					sendMessageToPlayer(player, "Are you even playing this game?");
				}
			} else {
				sendMessageToPlayer(player, "please choose 'R', 'P', or 'S'!");
			}
			
		} else {
			
			sendMessageToGroup(playersWatchingOrPlaying, player.getClientName() + ": " + query);
		}
		
		
		
	}
	
	public void recordCommandMsg(String command) {
		recorder.printCommand(command);
	}
	
	public void recordOutput(String output) {
		recorder.printOutput(output);
	}
	
	//TODO: move this to Server class.
	public static void sendMessageToGroup(MiniServer players[], String message) {
		try {
			for(int i=0; i<players.length; i++) {
				if(players[i] != null) {
					players[i].sendMessageToClient("From Rock Paper Scissors: " + message);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO: just get rid of this middle-man class.
	public static void sendMessageToPlayer(MiniServer player, String message) {
		try {
			player.sendMessageToClient("From Rock Paper Scissors: " + message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

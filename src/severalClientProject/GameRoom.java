package severalClientProject;

import java.io.IOException;
import java.util.ArrayList;

//This class extends Thread because when it starts a new game, it starts a new thread.
public class GameRoom extends Thread implements Runnable {
	private String gameName;
	private String roomName;
	private String password;
	
	//TODO: maybe allow for some spots reserved for a specific player?
	
	private MiniServer players[];
	private int minPlayers;
	private int maxPlayers;
	
	private MiniServer host;
	
	private boolean openSlot[];
	
	private boolean gameStarted = false;
	
	//TODO: add a condition where the game gets cancelled... like everyone leaving the room... but that's no fun!
	private boolean gameCancelled = false;
	private boolean gameOver = false;
	
	
	private ArrayList<String> bannedPlayerList = new ArrayList<String>();
	
	private String refreshMessage;
	private int numPlayers;
	
	severalClientProject.Game game = null;
<<<<<<< HEAD

	public static int NUM_SECONDS_COUNTDOWN = 5;
=======
>>>>>>> 5d2572ce9e421e991da951240033a84d5e2a9fb6
	
	public severalClientProject.Game getGame() {
		return game;
	}

	public GameRoom(String gameName, String roomName, MiniServer host) {
		this(gameName, roomName, "", host, new String[0]);
	}
	
	public GameRoom(String gameName, String roomName, MiniServer host, String gameArgs[]) {
		this(gameName, roomName, "", host, gameArgs);
	}
	
	public GameRoom(String gameName, String roomName, String password, MiniServer host) {
		this(gameName, roomName, password, host, new String[0]);
	}
	

	public String getStateFromInsideRoom() {
		return refreshMessage;
	}
	
	public boolean isGameStarted() {
		return gameStarted;
	}
	
	public boolean isCancelled() {
		return gameCancelled;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	public MiniServer getHost() {
		return this.host;
	}
	
	public String getGameName() {
		return this.gameName;
	}
	
	public String getRoomName() {
		return this.roomName;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	//TODO: game args should specify the details of the game. This is there because there 
	//are too many variants of how poker gets played out.
	public GameRoom(String gameName, String roomName, String password, MiniServer host, String gameArgs[]) {
		this.gameName = gameName;
		this.roomName = roomName;
		this.password = password;
		
		 //TODO: create a file that lists all the game names and handles this function:
		//***********************************************
		if(gameName.equals(ServerGameReference.HOLD_EM)) {
			minPlayers = 2;
			maxPlayers = 10;
			
			setupBasicRoomVars(host);
			
		
		} else if (gameName.equals(ServerGameReference.CONNECT_FOUR)) {
			
			//TODO: allow observers?
			minPlayers = 2;
			maxPlayers = 2;
			
			setupBasicRoomVars(host);
		
		} else if (gameName.equals(ServerGameReference.RPS)) {
			minPlayers = 2;
			maxPlayers = 2;
			
			setupBasicRoomVars(host);
			
		} else if (gameName.equals(ServerGameReference.CHESS)) {
			minPlayers = 2;
			maxPlayers = 2;
			
			setupBasicRoomVars(host);
			
<<<<<<< HEAD
		} else if (gameName.equals(ServerGameReference.MELLOW)) {
			minPlayers = 4;
			maxPlayers = 4;
			
			setupBasicRoomVars(host);
		} else if (gameName.equals(ServerGameReference.REVERSI)) {
			minPlayers = 2;
			maxPlayers = 2;
			
			setupBasicRoomVars(host);
=======
>>>>>>> 5d2572ce9e421e991da951240033a84d5e2a9fb6
		} else {
			System.err.println("ERROR: Unknown game \"" + gameName + "\"");
		}
		//***************************

		renewRefreshMessage();
	}
	
	public void setupBasicRoomVars(MiniServer host) {
		players = new MiniServer[maxPlayers];
		openSlot = new boolean[maxPlayers];
		
		for(int i=0; i<maxPlayers; i++) {
			openSlot[i] = true;
		}
		
		players[0] = host;
		this.host = host;
		this.numPlayers = 1;
		
	}
	
	public String getStateFromOutsideRoom() {
		String ret = "";
		ret += this.gameName + ": " + this.roomName + " (" + this.numPlayers + "/" + this.maxPlayers + ") host: " + this.host.getClientName();
		return ret;
	}
	
	
	public int getNumPlayers() {
		return this.numPlayers;
	}
	
	
	
	public synchronized String join(MiniServer player) {
		int slot = getNextEmptySlot();
		if(slot >= 0) {
			String errorMsg = checkIfPlayerBanned(player.getClientName());
			
			if(errorMsg.equals("")) {
				players[slot] = player;
				this.numPlayers++;
				renewRefreshMessage();
				return "";
			} else {
				return errorMsg;
			}
		} else {
			return "ERROR: couldn\'t find an empty slot!";
		}
	}
	
	private String checkIfPlayerBanned(String playerName) {
		playerName = playerName.toLowerCase();
		for(int i=0; i<bannedPlayerList.size(); i++) {
			if(playerName.equals(bannedPlayerList.get(i).toLowerCase())) {
				return "ERROR: you have been banned from that game!";
			}
		}
		
		return "";
	}
	
	//leave: If host leaves, this just makes the guy under the host the host.
	public synchronized String leave(MiniServer player) {
		for(int i=0; i<players.length; i++) {
			if(players[i] == player) {
				players[i] = null;
				this.numPlayers--;
				
				//change host if host leaves:
				if(player == this.host) {
					for(int j=1; j<players.length; j++) {
						if(players[(i+j) % players.length] != null) {
							this.host = players[(i+j) % players.length];
						}
					}
				}
				
				System.out.println("TESTING: " + player.getClientName() + " has left the room");
				renewRefreshMessage();
				System.out.println("TESTING: " + player.getClientName() + " has left the room2");
				
				return "";
			}
		}
		
		return "ERROR: couldn't leave the game!";
	}
	
	//leave: If host leaves, this just makes the guy under the host the host.
	public synchronized String kick(String player) {
		player = player.toLowerCase();
		MiniServer bannedPlayer = null;
		
		for(int i=0; i<players.length; i++) {
			if(players[i] != null && players[i].getClientName().toLowerCase().equals(player)) {
				bannedPlayer = players[i];
				players[i] = null;
				try {
					bannedPlayer.sendMessageToClient("You have been banned from the game.");
					bannedPlayer.receiveBan();
					bannedPlayerList.add(bannedPlayer.getClientName().toLowerCase());
				} catch (IOException e) {
					System.err.println("Couldn\'t ban player...");
					e.printStackTrace();
				}
				
				this.numPlayers--;
				
				//change host if host leaves:
				if(player == this.host.getClientName().toLowerCase()) {
					for(int j=1; j<players.length; j++) {
						if(players[i+j] != null) {
							this.host = players[i+j];
						}
					}
				}
				renewRefreshMessage();
				return "";
			}
		}
		
		return "ERROR: couldn't find player to ban!";
	}
	
	public synchronized String moveSlot(MiniServer player, int newPos) {
		int posIndex = newPos - 1;
		
		for(int i=0; i<players.length; i++) {
			if(players[i] == player) {
				if(posIndex < maxPlayers && posIndex > 0) {
					if(players[posIndex] == null) {
						if(openSlot[posIndex]) {
							players[posIndex] = players[i];
							players[i] = null;
							renewRefreshMessage();
							return "";
						} else {
							return "WARNING: slot is not open.";
						}
					} else {
						return "WARNING: another player is in that position";
					}
				} else {
					return "WARNING: index of new position is out of bounds.";
				}
			}
		}
		
		return "WARNING: you aren't in the game anymore!";
	}
	
	public synchronized String openSlot(int pos) {
		int posIndex = pos - 1;
		if(players[posIndex] == null) {
			openSlot[posIndex] = true;
			renewRefreshMessage();
			return "";
		} else {
			return "WARNING: player already occupies that slot.";
		}
		
	}
	
	public synchronized String closeSlot(int pos) {
		int posIndex = pos - 1;
		if(players[posIndex] == null) {
			openSlot[posIndex] = false;
			renewRefreshMessage();
			return "";
		} else {
			return "WARNING: player already occupies that slot.";
		}
		
	}
	
	public synchronized void sendChatMessageToRoom(MiniServer client, String message) {
    	//OPTIONAL: create send message(listofclients) functions. (Make the order of what's displayed on the screen consistant between clients...)
    	sendGameRoomPlayersMessage(client.getClientName(), message);
    }
    
	public int getNextEmptySlot() {
		for(int i=0; i<players.length; i++) {
			if(players[i] == null) {
				return i;
			}
		}
		
		return -1;
	}
	
    
	//pre: The below methods are private and are only called within synchronized function:
	public synchronized void sendGameRoomPlayersMessage(String from, String msg) {
			System.out.println("DEBUG: trying to send msg: " + msg);
			if(from.equals("") == false) {
				msg = from + ": " + msg;
			}
			
			for(int i=0; i<players.length; i++) {
				try {
					if(players[i] != null) {
						System.out.println("player " + (i+1) + " is not null!");
						System.out.println(players[i].getClientName());
						players[i].sendMessageToClient(msg);
						System.out.println("player " + (i+1) + " didn\'t throw an exception!");
					}
				} catch (IOException e) {
					System.err.println("Couldn't send refresh message from game room.");
					e.printStackTrace();
				}
			}
			
			
		
	}
	
	private synchronized void renewRefreshMessage() {
		String newMessage = "";
		newMessage += getStateFromOutsideRoom() + ":" + "\n";
		for(int i=0; i<players.length; i++) {
			newMessage += "Slot #" + (i+1) + ": ";
			if(players[i] != null) {
				newMessage += players[i].getClientName() + "\n";
				
			} else {
				if(openSlot[i]) {
					newMessage += "Open" + "\n";
				} else {
					newMessage += "Closed" + "\n";
				}
			}
		}
		
		refreshMessage = newMessage;
		
		System.out.println("Sending game room players message:");
		sendGameRoomPlayersMessage("", refreshMessage);
		System.out.println("End sending game room players message:");
	}
		
<<<<<<< HEAD
=======
	public static int NUM_SECONDS_COUNTDOWN = 10;
>>>>>>> 5d2572ce9e421e991da951240033a84d5e2a9fb6
	
	//WARNING: this function cannot be synchronized (I think)
	// start: checks if conditions allow for game to start then does a 5 second countdown then actually starts\]
	//(if conditions are still ok) (Count-downs are cool! :P)
	public synchronized String startCountdown() {
		//if( game allows player array to word)
		if(gameAllowsPlayersToGo()) {
			System.out.println("Start count down:");
			GameStarter gameStarter = new GameStarter(this);
			Thread t = new Thread(gameStarter);
	        t.start();
			
			return "";
		} else {
			return "ERROR: game conditions haven\'t been meet.";
		}
	}
	
	
	public synchronized String startGame() {
		if(gameAllowsPlayersToGo()) {
			this.gameStarted = true;
			
			System.out.println("Starting " + gameName + "!");
			
			this.game = ServerGameReference.createGame(gameName);
<<<<<<< HEAD
			this.game.startGameForClients(players);
=======
			this.game.startGame(players);
>>>>>>> 5d2572ce9e421e991da951240033a84d5e2a9fb6
			
			//At this point, we've reached the end of the game:
			this.endGameRoom();
			return "";
		} else {
			return "ERROR: game conditions haven\'t been meet after countdown.";
		}
	}
	
	private synchronized void endGameRoom() {
		game = null;
		this.gameStarted = false;
		this.gameOver = true;
		
		for(int i=0; i<players.length; i++) {
			if(players[i] != null) {
				players[i].forcePlayerOutOfGameRoom();
			}
		}
	}
	
	//TODO: improve on this for other games:
	private synchronized boolean gameAllowsPlayersToGo() {
		if(numPlayers >= this.minPlayers && numPlayers <= this.maxPlayers) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
package severalClientProject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
//TODO: ahh... this class is horribly inefficient.
	//I need to just make refresh messages refresh based on changes to the state of the server.
		//NOT: every time a client requests them.
		//This will reduce the amount of overhead.
	
	//TODO: syncronized doesn't save me from starvation...
		//think about using queues... later
	
	//Main server that handles client requests and the player/game threads.
	//This is a static class and there should only ever be one instance of this class per computer.
	
	
	//TODO: sort these so we could do a binary search: (or hash map?)
	private static ArrayList<MiniServer> clientConnection = new ArrayList<MiniServer>();
	private static ArrayList<GameRoom> gameRoom = new ArrayList<GameRoom>();
	
	
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        int PORT = 6789;
        boolean listeningSocket = true;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
        }

        while(listeningSocket){
            Socket clientSocket = serverSocket.accept();
            clientConnection.add(new MiniServer(clientSocket));
            clientConnection.get(clientConnection.size() - 1).start();
            
            updateCountNumPeopleOnline();
            
        }
        serverSocket.close();       
    }

    
    
  //returns the gameRoom the client wants to create or an error message.
   public static synchronized Object createGame(String gameName, String roomName, String password, MiniServer host, String gameArgs[]) {
    	Object ret = "ERROR: ret shouldn\'t became something.";
    	
    	removeOldGames();
    	
    	boolean alreadyExists = false;
    	
    	for(int i=0; i<gameRoom.size(); i++) {
    		if(gameRoom.get(i).equals(roomName)) {
    			ret = "ERROR: a game called " + gameName + " already exists.";
    			alreadyExists = true;
    			break;
    		}
    	}
    	if(alreadyExists == false) {
	    	gameRoom.add(new GameRoom(gameName, roomName, password, host, gameArgs));
	    	gameRoom.get(gameRoom.size() - 1).start();
	    	
	    	return gameRoom.get(gameRoom.size() - 1);
    	} else {
    		return ret;
    	}
    }
    
    //returns the gameRoom the client wants to join or an error message.
    public static synchronized Object joinGame(String roomName, String password, MiniServer client) {
    	removeOldGames();
    	
    	boolean foundGame = false;
    	Object ret = "ERROR: ret shouldn\'t became something.";
    	
    	String temp;
    	
    	for(int i=0; i<gameRoom.size(); i++) {
    		if(gameRoom.get(i).getRoomName().equals(roomName)) {
    			foundGame = true;
    			if(gameRoom.get(i).getPassword().equals(password)) {
    				temp = gameRoom.get(i).join(client);
    				
    				if(temp.equals("")) {
    					ret =  gameRoom.get(i);
    				} else {
    					ret = temp;
    				}
    				break;
    			} else {
    				ret =  "ERROR: Bad password.";
    				break;
    			}
    		}
    	}
    	if(foundGame == false) {
    		ret = "ERROR: couldn\'t find game.";
    	}
    	
    	return ret;
    }
    
    //TODO: run this at intelligent times.
    public static synchronized void removeOldGames() {
	    for(int i=0; i<gameRoom.size(); i++) {
			if(gameRoom.get(i).isGameStarted() || gameRoom.get(i).isCancelled() || gameRoom.get(i).isGameOver() == true) {
				gameRoom.remove(i);
			}
		}
	}

    
    //counting number of people online BNET style!
    //TODO: change to poker style where your state is still there after you disconnect.
    //TODO: Don't recalculate every time, just refer to the string that has the latest update
    private static synchronized void updateCountNumPeopleOnline() {
    	for(int i=0; i<clientConnection.size(); i++) {
        	if(clientConnection.get(i).isAlive() == false) {
        		clientConnection.remove(i);
        	}
        }
        System.out.println("Number of clients logged in: " + clientConnection.size());
    }
    
    //TODO: Don't recalculate every time, just refer to the string that has the latest update    
    public static synchronized String getOpenGames() {
    	removeOldGames();
    	
    	String ret = "";
    	int numRooms = 0;
    	ret += "number of game rooms: " + gameRoom.size() + "\n";
    	for(int i=0; i<gameRoom.size(); i++) {
			if(gameRoom.get(i).isGameStarted() == false && gameRoom.get(i).isCancelled() == false || gameRoom.get(i).getNextEmptySlot() < 0) {
				ret += gameRoom.get(i).getStateFromOutsideRoom() + "\n";
				numRooms++;
			} else if(gameRoom.get(i).isGameStarted() == true) {
				ret += "DEBUG started: " + gameRoom.get(i).getStateFromOutsideRoom() + "\n";
			}
		}
    	if(numRooms == 0) {
    		ret += "(no game rooms)" + "\n";
    	}
    	
    	return ret;
    }
    
    private static String playersOusideMessage;
    
    public  static synchronized void updateGetPlayersOuside() {
    	playersOusideMessage = "";
    	
    	playersOusideMessage += "players in channel:" + "\n";
    	for(int i=0; i<clientConnection.size(); i++) {
    		if(clientConnection.get(i).isInGame() == false) {
    			playersOusideMessage += clientConnection.get(i).getClientName() + "\n";
    		}
    	}
    	
    }
    
    //TODO: actually make channels to minimize the of interference/waiting. (Channels in the Bnet sense of the word)
    //TODO: Don't recalculate every time, just refer to the string that has the latest update
    public  static String getPlayersOuside() {
    	updateGetPlayersOuside();
    	return playersOusideMessage;
    }
    
    public static synchronized String givePlayerMessage(String sender, String receiver, String message) {
    	String ret ="";
    	for(int i=0; i<clientConnection.size(); i++) {
    		if(clientConnection.get(i).getClientName().toLowerCase().equals(receiver.toLowerCase())) {
    			try {
    				clientConnection.get(i).sendMessageFromOtherClientToClient(sender, message);
    				ret =  sender + " whispers to " + receiver + ": " + message;
    			} catch(IOException e) {
    				ret = receiver + " is no longer online.";
    			}
    			
    		}
    	}
    	
    	if(ret.equals("")) {
    		ret = "could\'t find user";
    	}
    	
    	return ret;
    }
    
   public static synchronized String sendInvite(MiniServer sender, String receiver, GameRoom room) {
    	String ret = "";
    	MiniServer receiverConnection;
    	if(room == null) {
    		ret = "Congratulations. You somehow invited someone while outside the game.\n";
    		ret += "I was hoping this was logically impossible.";
    	} else {
    		Object temp = getUser(receiver);
    		if(temp instanceof MiniServer) {
    			receiverConnection = (MiniServer)temp;
    			ret = receiverConnection.sendInvite(sender, room);
    		} else {
    			ret = (String) temp;
    		}
    	}
    	
    	return ret;
    }
    
	public static synchronized Object getUser(String username) {
    	username = username.toLowerCase();
    	for(int i=0; i<clientConnection.size(); i++) {
    		if(clientConnection.get(i).getClientName().toLowerCase().equals(username)) {
    			return clientConnection.get(i);
    			
    		}
    	}
    	
    	return "couldn\'t find user.";
    }
    
	//TODO: put lock here!
    public synchronized static void sendChatMessageToChannel(MiniServer client, String message) {
    	//OPTIONAL: create send message(listofclients) functions. (Make the order of what's displayed on the screen consistant between clients...)
    	
    	for(int i=0; i<clientConnection.size(); i++) {
    		if(clientConnection.get(i).isInGame() == false) {
    			try {
    				clientConnection.get(i).sendMessageToClient(client.getClientName() + ": " + message);
    			} catch(IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    public static synchronized String getRefreshMessageFromChannel() {
    	String reply = Server.getOpenGames();
    	reply += "\n";
    	reply += Server.getPlayersOuside();
    	return reply;
    }
   
}

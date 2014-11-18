package severalClientProject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//TODO: punish someone if they spam / requests.

//Tuesday at hacklab:
//	1) Make more functions
//	2) get rid of immidiateResponse as an instance var.
// 3) Add chat so I can actually impress.

//TODO: way more functions!
//handle leaving in the middle of a game for later.
//(I want to add the ability to reconnect)
//Add ability to start holdem!
//keep going though code and adding functions.

public class MiniServer extends Thread{

	public static final String EOT = "**end of transmission**";
	public static final String EOC = "Goodbye.";
	
    private Socket socket = null;

    private GameRoom currentGameRoom;
    
    
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    
    //private String immediateResponse;
    private String clientName = "";
    
    private boolean isConnected = true;
    
    // if this is not empty, the current client using this mini server has been invited to go somewhere.
    public String invitation = "";
    public MiniServer inviter = null;
    
    public MiniServer(Socket socket) {
        super("MiniServer");
        this.socket = socket;
    }

    public void run() {
    	try {
    		System.out.println("Running mini server!");
	    	
    		//Read input and process here
	    	inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToClient = new DataOutputStream( socket.getOutputStream());
			String clientSentence = "";
			
			while(isConnectionStillOpen() == true) {
				
				System.out.println("trying to get something from client.");
				
				clientSentence = inFromClient.readLine();
				
				System.out.println("got something from client.");
				
				submitClientQuery(clientSentence);
				
			}
			
			System.out.println("No longer listening for requests from " + this.clientName);
			outToClient.close();
			inFromClient.close();
			socket.close();
    	
    	} catch(java.net.SocketException e) {
    		System.out.println("Client is a quitter!");
			return;
    	
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    }
    
    public boolean isConnectionStillOpen() {
    	return isConnected;
    }
    
    //NOTE: if I synchronize this function, it will cause a deadlock with the send msg function.
    // TODO: Maybe I should just use a lock because this function has NOTHING to do with the 
    // send message function.
    // For now, I'm lazy. ;)
    private void submitClientQuery(String query) {
    	if(clientName == "") {
    		clientName = beAJerkToFriends(query);
    		sendImmediateResponseToClient("Hello, " + clientName + ". Type /help or /h for help.");
    	} else {
    		
    		//if the query is a command, take away the capitals.
    		if(query.trim().startsWith("/")) {
    			cleanCommandQuery(query);
    		}
	    	
    		System.out.println("Query from " + this.clientName + ": " + query);
    		
    		
	    	if(isInGame() == false) {
	    		submitClientQueryFromChannels(query);
	    	} else if(currentGameRoom.isGameStarted() == false){
	    		submitClientQueryFromWaitingRoom(query);
	    	} else {
	    		System.out.println("Submit query to game!!");
	    		submitClientQueryToGame(query);
	    	}
    	}
    	
    }
    
    private static String cleanCommandQuery(String query) {
    	String cleanQuery = query.toLowerCase().trim();
		cleanQuery = getRidOfExtraSpaces(cleanQuery);
		System.out.println("clean Query: " + cleanQuery);
		
		//TODO: query = keepMessageAreaUnClean(query, cleanQuery);
		
		return query;
    }
    
    //TODO: This function allows users to type with multiple space and rolling caps.
    public static String keepMessageAreaUnClean(String query, String cleanQuery) {
    	String args[] = cleanQuery.split(" ");
    	if(args.length > 0) {
    		if(args[0].equals("/m") || args[0].equals("/w")) {
    			cleanQuery = query.substring(query.indexOf("/"));
    		}
    	} else if(args.length > 1) {
    		if(args[0].equals("/f") || args[1].equals("m")) {
    			cleanQuery = query.substring(query.indexOf("/"));
    		}
    	}
    	return cleanQuery;
    }
    //post: cleans the input of a / command by making the space between the arguments seperated by only 1 space.
    //ex: /create  holdem	   hello     wolrd
    //becomes: /create holdem hello world
    private static String getRidOfExtraSpaces(String query) {
    	query = query.replace('\t', ' ');
    	
    	int prevlength;
    	
    	do {
    		prevlength = query.length();
    		query = query.replace("  ", " ");
    	} while (prevlength > query.length());
    	
    	query = query.trim();
    	
    	return query;
    }
    
    //pre: query is lowercase and trimmed
    private void submitClientQueryFromChannels(String query) {
    	
    	if(query.startsWith("/")) {
    		if(query.startsWith("/create")) {
    			sendCreateGameRequest(query);
    			
    		} else if(query.startsWith("/join")) {
    			joinGame(query);
    			
    		} else if(query.startsWith("/gamerooms")) {
    			sendImmediateResponseToClient(Server.getOpenGames());
    			
    		} else if(query.startsWith("/refresh")) {
    			sendImmediateResponseToClient(Server.getRefreshMessageFromChannel());
    			
    		} else if(query.startsWith("/profile")) {
    			sendImmediateResponseToClient("TODO: show profile stuff here!");
    			
    		} else if(query.startsWith("/help") || query.equals("/h")) {
    			sendImmediateResponseToClient("I didn\'t create a message for this yet. (sorry)");
    		
    		} else if(query.startsWith("/disc")) {
    			sendImmediateResponseToClient("Goodbye!\nEnter any key to exit.\n" + EOC);
    			
    			//if user was invited, cancel invitation:
    			if(inviter != null) {
    				submitGenericServerQuery("/n");
    			}
    			intentionallyDisconnect();
    			
    			//if user is in game, say:   SERVER: username has disconnected. to everyone else in room
    			
    			//Set miniserver to disconnected:
    			isConnected = false;
    			
    		} else {
    			submitGenericServerQuery(query);
    		}
    		
		} else {
			//just send message within the chat channel (or something...)
			Server.sendChatMessageToChannel(this, query);
		}
    }
    
 
    public void intentionallyDisconnect() {
    	 //TODO: fill in the blanks!!!
    	//this.destroy.
    	//Also remmove invitation and currentGame.
    }
    
    
  //pre: query is lowercase and trimmed
    private  void submitClientQueryToGame(String query) {
    	currentGameRoom.getGame().submitClientQuery(this, query);
		
    }
    
  //pre: query is lowercase and trimmed
    private  void submitClientQueryFromWaitingRoom(String query) {
    	String args[] = query.split(" ");
    	
    	if(query.startsWith("/")) {
    		if(currentGameRoom.isGameStarted() == false) {
    			if(query.startsWith("/kick") || query.startsWith("/ban")) {
    				if(currentGameRoom.getHost() == this) {
    					if(args.length > 1 && isInteger(args[1]) == false) {
    						kick(args[1]);
    					} else {
    						sendImmediateResponseToClient("Warning: argument expected player name.");
    					}
    				} else {
    					sendImmediateResponseToClient("Only the host could do that!");
    				}
    				
	    		} else if(query.startsWith("/open")) {
	    			if(currentGameRoom.getHost() == this) {
	    				if(args.length > 1 && isInteger(args[1])) {
		    				openSlot(Integer.parseInt(args[1]));
		    			}
    				} else {
    					sendImmediateResponseToClient("Only the host could do that!");
    				}
	    			
	    		} else if(query.startsWith("/close")) {
	    			if(currentGameRoom.getHost() == this) {
	    				if(args.length > 1 && isInteger(args[1])) {
		    				closeSlot(Integer.parseInt(args[1]));
		    			} else {
		    				sendImmediateResponseToClient("Warning: argument expected number.");
		    			}
    				} else {
    					sendImmediateResponseToClient("Only the host could do that!");
    				}
	    			
	    		} else if(query.startsWith("/invite")) {
	    			
	    			if(args.length > 1) {
	    				//TODO: don't send refresh if all goes well. (The room will send the message)
	    				String errorMsg = Server.sendInvite(this, args[1], currentGameRoom);
	    				if(errorMsg.equals("") == false) {
	    					sendImmediateResponseToClient(errorMsg);
	    				}
	    				
	    			} else {
	    				sendImmediateResponseToClient("Please specify who you will invite.");
	    			}
    				
	    			
	    		} else if(query.startsWith("/start")) {
	    			if(currentGameRoom.getHost() == this) {
		    			String retMessage = currentGameRoom.startCountdown();
	    				if(retMessage.equals("") == false) {
	    					sendImmediateResponseToClient(retMessage);
	    				} else {
	    					sendImmediateResponseToClient("Count down started.");
	    				}
	    				
    				} else {
    					sendImmediateResponseToClient("Only the host could do that!");
    				}
	    			
	    		} else if(query.startsWith("/movePlayer")) {
	    			if(currentGameRoom.getHost() == this) {
	    				sendImmediateResponseToClient("Move player hasn\'t been implemented yet.");
    				} else {
    					sendImmediateResponseToClient("Only the host could do that!");
    				}
	    			
	    		} else if(query.startsWith("/leave")) {
	    			leaveGame();
	    			
	    		} else if(query.startsWith("/move")) {
	    			if(args.length > 1 && isInteger(args[1])) {
	    				moveSlots(Integer.parseInt(args[1]));
	    			}
	    			
	    		} else if(query.startsWith("/refresh")) {
	    			sendImmediateResponseToClient(currentGameRoom.getStateFromInsideRoom());
	    		
	    		} else if(query.startsWith("/help") || query.equals("/h")) {//TODO: send help message
	    			sendImmediateResponseToClient("I didn\'t create a message for this inside game room yet. (sorry)");
	    			
	    		} else {
	    			submitGenericServerQuery(query);
	    		}
    			
    		} else {
    			//TODO send request to game:
    			sendImmediateResponseToClient("Game is started. TODO: make the game figure it out!");
    		}
    	} else {
    		currentGameRoom.sendChatMessageToRoom(this, query);
    	}
    	
    }
    
    private void submitGenericServerQuery(String query)  {
    	String args[] = query.split(" ");
    	String player;
    	String message;
    	
    	if(query.startsWith("/f")) {
    		String immediateResponse = Server.getOpenGames();
 			immediateResponse += "TODO: create friends list system like the battle.net friends list.";
 			sendImmediateResponseToClient(immediateResponse);
 			
 		} else if(query.startsWith("/m")) {
 			if(args.length > 1) {
 				if(args.length > 2) {
 					player = args[1];
 					if(query.startsWith("/m " + player)) {
 						message = query.substring(("/m " + player).length() + 1);
 					} else {
 						//this isn't supposed to happen...
 						message = "ERROR! This /m condition wasn\'t supposed to happen!" + args[2];
 					}
 					sendImmediateResponseToClient(Server.givePlayerMessage(this.clientName, player, message));
 				} else {
 					sendImmediateResponseToClient("Please specify a message to give to the user.");
 				}
 		    	
 			} else {
 				sendImmediateResponseToClient("Please specify the username of the person you want to message.");
 			}
 			 			
 		} else if(query.startsWith("/y")) {
 			if(invitation.equals("") == false) {
 				try {
	 				inviter.sendMessageToClient(this.clientName + " accepted your invitation.");
 				} catch(IOException e) {
 					//if the client/user who invited us got disconnected:
 					String immediateResponse = "sent acceptance of invitation."  + "\n";
					immediateResponse += inviter.getClientName() + "disconnected.";
					sendImmediateResponseToClient(immediateResponse);
				}
 				submitClientQueryFromChannels(invitation);
 				
 			} else {
 				sendImmediateResponseToClient("you weren\'t invited to a game.");

 			}
 			invitation = "";
			inviter = null;
 				
 		} else if(query.startsWith("/n")) {
 			if(inviter == null) {
 				String immediateResponse = "Congratz: we somehow forget who invited us." + "\n";
				immediateResponse += "I was hoping this was logically impossible.";
				sendImmediateResponseToClient(immediateResponse);
				
			} else {
				try {
					inviter.sendMessageToClient(this.clientName + " declined your invitation.");
					sendImmediateResponseToClient("sent decline of invitation.");
				} catch(IOException e) {
					String immediateResponse = "sent decline of invitation."  + "\n";
					immediateResponse += inviter.getClientName() + "disconnected.";
					sendImmediateResponseToClient(immediateResponse);
				}
			}
 			invitation = "";
			inviter = null;
 			 
 			
 		} else {
 			sendImmediateResponseToClient("ERROR: unknown command. Type /h or /help for help.");
 		}
    }

    private void sendImmediateResponseToClient(String immediateResponse) {
    	try {
    		sendMessageToClient(immediateResponse);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public void sendGameQueryToClient(String query) throws IOException {
    	currentGameRoom.getGame().submitClientQuery(this, query);
    }
    
    
    public String sendInvite(MiniServer sender, GameRoom room) {
    	String ret = "";
    	try {
    		
    		if(invitation.equals("") && currentGameRoom == null) {
    			sendMessageToClient(sender.getClientName() + " invites you to play " + room.getStateFromOutsideRoom() + "\n" + "Do you accept? (Type /y for yes and /n for no.)");
    	    	this.inviter = sender;
    			this.invitation = "/join " + room.getRoomName() + " " + room.getPassword();
    	    	ret += "Invite sent.";
    		} else if(invitation.equals("") == false){
    			ret += this.clientName + " is already invited to another game.";
    		} else {
    			ret += this.clientName + " is in a game.";
    		}
	    	 
    	} catch(IOException e) {
    		ret += this.clientName + " disconnected.";
    	}
    	
    	return ret;
    }
    
    public void sendMessageFromOtherClientToClient(String sender, String message) throws IOException {
    	sendMessageToClient(sender + " whispers: " + message);
    }
    
    //TODO: put a lock on sendMessageToClient.
    public void sendMessageToClient(String message)  throws IOException {
    	System.out.println("Trying to send: " + message + '\n' + EOT);
    	
    	//sanitize output just in case:
    	while(message.endsWith("\n")) {
    		message = message.substring(0, message.length() - 1);
    	}
    	
    	
    	//outToClient.writeBytes(message /*+ '\n'*/ + EOT /*+ '\n'*/ + typeOfRequest);
    	outToClient.writeBytes(message + '\n');
    	outToClient.writeBytes(EOT + '\n');
    	System.out.println("End of write bytes!");
    }
    
    //TODO: make sure the name isn't already taken somehow... 
    //A very important function, that renames my friend's names to their 'real' name. :P
    private static String beAJerkToFriends(String name) {
    	name = name.trim();
    	
    	if(name.toLowerCase().startsWith("rich")) {
    		name = "Dick";
    	} else if(name.toLowerCase().startsWith("mar")) {
    		name = "Daisy";
    	} else if(name.toLowerCase().startsWith("des ")) {
    		name = "Daisy";
    	} else if(name.toLowerCase().startsWith("doris")) {
    		name = "Mom";
    	} else if(name.toLowerCase().startsWith("phil")) {
    		name = "Dad";
    	} else if(name.toLowerCase().startsWith("mel")) {
    		name = "Melissa(heart)";
    	} else if(name.toLowerCase().startsWith("ben")) {
    		name = "Franklin";
    	} else if(name.equals("")) {
    		name = "YOUR_NAME_HERE";
    	} else if(name.toLowerCase().contains("open")) {
    		name = "TEN_YEAR_OLD_BOY";
    	} else if(name.toLowerCase().contains("closed")) {
    		name = "SOME_NOBODY";
    	}
    	
    	return name;
    }
    
    private void sendCreateGameRequest(String query) {
    	String args[] = query.split(" ");
    	String roomName;
    	String password = "";
    	
    	String immediateResponse;
    	Object temp = "";
    	
    	if(args.length > 1 ) {
    		//TODO: put this logic in another file...
    		//TODO: get rid of redundancy.
			if(args[1].startsWith(ServerGameReference.HOLD_EM) || args[1].startsWith("texas")) {
				//TODO: put below in it's own function!
				if(args.length > 2 ) {
					roomName = args[2];
					if(args.length > 3 ) {
    					password = args[3];
    				}
					
					
					temp = Server.createGame(ServerGameReference.HOLD_EM, roomName, password, this, null);
					
					if(temp instanceof GameRoom) {
						this.currentGameRoom  = (GameRoom)temp;
						immediateResponse = "Game created:";
    					immediateResponse += this.currentGameRoom.getStateFromInsideRoom();
					} else {
						immediateResponse = (String)temp;
					}
					
					
				} else {
					immediateResponse = "ERROR: please add game name. Create game in the form: \"/create holdem roomname [password]\"";
				}
			} else if(args[1].startsWith(ServerGameReference.CONNECT_FOUR)) {
				//TODO: put below in it's own function!
				if(args.length > 2 ) {
					roomName = args[2];
					if(args.length > 3 ) {
    					password = args[3];
    				}
					
					
					temp = Server.createGame(ServerGameReference.CONNECT_FOUR, roomName, password, this, null);
					
					if(temp instanceof GameRoom) {
						this.currentGameRoom  = (GameRoom)temp;
						immediateResponse = "Game created:";
    					immediateResponse += this.currentGameRoom.getStateFromInsideRoom();
					} else {
						immediateResponse = (String)temp;
					}
					
					
				} else {
					immediateResponse = "ERROR: please add game name. Create game in the form: \"/create holdem roomname [password]\"";
				}
    		} else if(args[1].startsWith(ServerGameReference.RPS)) {
				//TODO: put below in it's own function!
				if(args.length > 2 ) {
					roomName = args[2];
					if(args.length > 3 ) {
    					password = args[3];
    				}
					
					
					temp = Server.createGame(ServerGameReference.RPS, roomName, password, this, null);
					
					if(temp instanceof GameRoom) {
						this.currentGameRoom  = (GameRoom)temp;
						immediateResponse = "Game created:";
    					immediateResponse += this.currentGameRoom.getStateFromInsideRoom();
					} else {
						immediateResponse = (String)temp;
					}
					
					
				} else {
					immediateResponse = "ERROR: please add game name. Create game in the form: \"/create rock_paper_scissors roomname [password]\"";
				}
				
    		}  else if(args[1].startsWith(ServerGameReference.CHESS)) {
				//TODO: put below in it's own function!
				if(args.length > 2 ) {
					roomName = args[2];
					if(args.length > 3 ) {
    					password = args[3];
    				}
					
					
					temp = Server.createGame(ServerGameReference.CHESS, roomName, password, this, null);
					
					if(temp instanceof GameRoom) {
						this.currentGameRoom  = (GameRoom)temp;
						immediateResponse = "Game created:";
    					immediateResponse += this.currentGameRoom.getStateFromInsideRoom();
					} else {
						immediateResponse = (String)temp;
					}
					
					
				} else {
					immediateResponse = "ERROR: please add game name. Create game in the form: \"/create rock_paper_scissors roomname [password]\"";
				}
				//***END TODO
				//********************************************************************************************************************
    		
    		}  else if(args[1].startsWith(ServerGameReference.MELLOW)) {
				//TODO: put below in it's own function!
				if(args.length > 2 ) {
					roomName = args[2];
					if(args.length > 3 ) {
    					password = args[3];
    				}
					
					
					temp = Server.createGame(ServerGameReference.MELLOW, roomName, password, this, null);
					
					if(temp instanceof GameRoom) {
						this.currentGameRoom  = (GameRoom)temp;
						immediateResponse = "Game created:";
    					immediateResponse += this.currentGameRoom.getStateFromInsideRoom();
					} else {
						immediateResponse = (String)temp;
					}
					
					
				} else {
					immediateResponse = "ERROR: please add game name. Create game in the form: \"/create rock_paper_scissors roomname [password]\"";
				}
				//***END TODO
				//********************************************************************************************************************
    		}  else if(args[1].startsWith(ServerGameReference.REVERSI)) {
				//TODO: put below in it's own function!
				if(args.length > 2 ) {
					roomName = args[2];
					if(args.length > 3 ) {
    					password = args[3];
    				}
					
					
					temp = Server.createGame(ServerGameReference.REVERSI, roomName, password, this, null);
					
					if(temp instanceof GameRoom) {
						this.currentGameRoom  = (GameRoom)temp;
						immediateResponse = "Game created:";
    					immediateResponse += this.currentGameRoom.getStateFromInsideRoom();
					} else {
						immediateResponse = (String)temp;
					}
					
					
				} else {
					immediateResponse = "ERROR: please add game name. Create game in the form: \"/create rock_paper_scissors roomname [password]\"";
				}
				//***END TODO
				//********************************************************************************************************************
    		} else {
				immediateResponse = "ERROR: can\'t identify game name.";
			}
		} else {
			immediateResponse = "ERROR: please create game in the form: \"/create gamename roomname\"";
		}
    	
    	sendImmediateResponseToClient(immediateResponse);
    	
    }
    
    private void joinGame(String query) {
    	String args[] = query.split(" ");
    	String roomName;
    	String password = "";
    	Object temp;
    	String immediateResponse = "";
    	
    	if(args.length > 1 ) {
			roomName = args[1];
			if(args.length > 2 ) {
				password = args[2];
			}
			
			temp = Server.joinGame(roomName, password, this);

			if(temp instanceof GameRoom) {
				this.currentGameRoom  = (GameRoom)temp;
				immediateResponse = "Game joined:" + "\n";
				immediateResponse += this.currentGameRoom.getStateFromInsideRoom();
			} else {
				immediateResponse = (String)temp;
			}
			
		} else {
			immediateResponse = "ERROR: please specify game name. Create game in the form: \"/join roomname [password]\"";
		}
    	
    	sendImmediateResponseToClient(immediateResponse);
    }
    
    //TODO: syncronize this with the banning feature.
     private void leaveGame() {
    	if(isInGame()) {
    		
    		String msg = currentGameRoom.leave(this);
    		currentGameRoom = null;
    		if(msg.equals("") == false) {
    			sendImmediateResponseToClient(msg);
    		} else {
    			sendImmediateResponseToClient(Server.getRefreshMessageFromChannel());
    		}
    		
    	} else {
    		sendImmediateResponseToClient("ERROR: you have already left the game.");
    	}
    }
    
    //after a game is finished, the game room get destroyed:
    public void forcePlayerOutOfGameRoom() {
    	if(isInGame()) {
    		
    		currentGameRoom.leave(this);
    		currentGameRoom = null;
    		
    		sendImmediateResponseToClient(Server.getRefreshMessageFromChannel());
    		
    	} else {
    		sendImmediateResponseToClient("ERROR: you have already left the game.");
    	}
    }
    
    private void kick(String player) {
    	if(isInGame()) {
    		
    		String msg = currentGameRoom.kick(player);
       		if(msg.equals("") == false) {
    			sendImmediateResponseToClient(msg);
    		}
    		
    	} else {
    		sendImmediateResponseToClient("ERROR: you have already left the game.");
    	}
    }
    
    public void receiveBan() {
    	currentGameRoom = null;
    	sendImmediateResponseToClient(Server.getRefreshMessageFromChannel());
    }
    
   private void moveSlots(int newPos) {
	   if(isInGame()) {
   		
   		String msg = currentGameRoom.moveSlot(this, newPos);
   		if(msg.equals("") == false) {
			sendImmediateResponseToClient(msg);
		}
   		
   	} else {
   		sendImmediateResponseToClient("ERROR: you're no longer in the game already left the game.");
   	}
   }
   
   private void openSlot(int newPos) {
	   if(isInGame()) {
   		String msg = currentGameRoom.openSlot(newPos);
   		if(msg.equals("") == false) {
			sendImmediateResponseToClient(msg);
		}
   		
   	} else {
   		sendImmediateResponseToClient("ERROR: you're no longer in the game already left the game.");
   	}
   }
   
   private void closeSlot(int newPos) {
	   if(isInGame()) {
   		String msg = currentGameRoom.closeSlot(newPos);
   		if(msg.equals("") == false) {
			sendImmediateResponseToClient(msg);
		}
   		
   	} else {
   		sendImmediateResponseToClient("ERROR: you're no longer in the game already left the game.");
   	}
   }
    
    public boolean isInGame() {
    	if(currentGameRoom != null) {
    		return true;
    	} else {
    		return false;
    	
    	}
    }
    
    public String getClientName() {
    	return clientName;
    }
    
    //determine if string is integer with exceptions.
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }
}

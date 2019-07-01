package severalClientProject;

import java.io.IOException;
import java.util.ArrayList;

public class Profile implements ProfileInterface {

	private MiniServer connection;
	
	public static final String EOT = "**end of transmission**";
	 
	private GameRoom currentGameRoom;
	private String clientName = "";


    // if this is not empty, the current client using this mini server has been invited to go somewhere.
    public String invitation = "";
    public Profile inviter = null;
	


    private Object lockSubmitQueryFromClient = new Object();
    private Object lockSendMessageToClient = new Object();
	
    public Profile(MiniServer connection, String clientName) {
    	this.connection = connection;
    	this.clientName = clientName;
    }
    
    
    //https://stackoverflow.com/questions/367706/how-do-i-parse-command-line-arguments-in-java
    
    
    //TODO: maybe you should defend against spam by setting this to synchronized...
    // but then there's a risk of dead lock.... :(
    //I don't know
    //SOLUTION: make a critical section!
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
					

					//TODO: maybe use real command line options later:
					//https://stackoverflow.com/questions/367706/how-do-i-parse-command-line-arguments-in-java
						
					//TODO: generalize and make args a constant
					
					System.out.println("TEST" + args.length);
					for(int i=0; i<args.length; i++) {
						System.out.println(args[i]);
					}
					
					String gameArgs[] = null;
					if(args.length > 4) {
						gameArgs = new String[args.length - 4];
						for(int i=0; i<gameArgs.length; i++) {
							gameArgs[i] = args[i+4];
						}
					}
					
					temp = Server.createGame(ServerGameReference.MELLOW, roomName, password, this, gameArgs);
					
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
			
			}  else if(args[1].startsWith(ServerGameReference.EUCHRE)) {
				//TODO: put below in it's own function!
				if(args.length > 2 ) {
					roomName = args[2];
					if(args.length > 3 ) {
    					password = args[3];
    				}
					
					
					temp = Server.createGame(ServerGameReference.EUCHRE, roomName, password, this, null);
					
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
					
			}  else if(args[1].startsWith(ServerGameReference.FRUSTRATION)) {
				//TODO: put below in it's own function!
				if(args.length > 2 ) {
					roomName = args[2];
					if(args.length > 3 ) {
    					password = args[3];
    				}
					
					
					temp = Server.createGame(ServerGameReference.FRUSTRATION, roomName, password, this, null);
					
					if(temp instanceof GameRoom) {
						this.currentGameRoom  = (GameRoom)temp;
						immediateResponse = "Game created:";
    					immediateResponse += this.currentGameRoom.getStateFromInsideRoom();
					} else {
						immediateResponse = (String)temp;
					}
				} else {
					immediateResponse = "ERROR: please add game name. Create game in the form: \"/create frustration roomname [password]\"";
				}
					
				//***END TODO
				//********************************************************************************************************************
    		} else {
				immediateResponse = "ERROR: can\'t identify game name. " + "( " + args[1] + ")";
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
    		
    		if(currentGameRoom.getHost() == this) {
    			//When the host leaves, the game should stop.
    			currentGameRoom.endGameRoom();
    		} else {
    		
	    		String msg = currentGameRoom.leave(this);
	    		currentGameRoom = null;
	    		if(msg.equals("") == false) {
	    			sendImmediateResponseToClient(msg);
	    		} else {
	    			sendImmediateResponseToClient(Server.getRefreshMessageFromChannel());
	    		}
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
   
   private void swapSlots(String player2) {
	   if(player2.toLowerCase().trim().equals(this.getClientName().toLowerCase().trim())) {
		   sendImmediateResponseToClient("You can't swap with yourself!");
		   return;
	   }
	   
	   
	   Object player2Obj = Server.getUser(player2);
	   if(player2Obj instanceof String) {
		   sendImmediateResponseToClient("ERROR: that's not a user's name");
		   return;
	   }

	   ProfileInterface player2Prof = (ProfileInterface)Server.getUser(player2);
	   
	   if(isInGame()) {
	   		
		   String msg = currentGameRoom.swapSlots(this, player2Prof);
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
	
    
    
    //NOTE: if I synchronize this function, it will cause a deadlock with the send msg function.
    // TODO: Maybe I should just use a lock because this function has NOTHING to do with the 
    // send message function.
    // For now, I'm lazy. ;)
    public void submitClientQuery(String query) {
    	synchronized(lockSubmitQueryFromClient) {
			
	    	
			System.out.println("Query from " + this.clientName + ": " + query);
			
			if(query.startsWith("/disc")) {			
    			//if user was invited, cancel invitation:
    			if(inviter != null) {
    				submitGenericServerQuery("/n");
    			}
    			sendImmediateResponseToClient("/disc");
    			//TODO
    			//if user is in game, say:   SERVER: username has disconnected. to everyone else in room
    		} else if(isInGame() == false) {
	    		submitClientQueryFromChannels(query);
	    	} else if(currentGameRoom.isGameStarted() == false){
	    		submitClientQueryFromWaitingRoom(query);
	    	} else {
	    		System.out.println("Submit query to game!!");
	    		submitClientQueryToGame(query);
	    	}
	    }
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
    /*private static String getRidOfExtraSpaces(String query) {
    	query = query.replace('\t', ' ');
    	
    	int prevlength;
    	
    	do {
    		prevlength = query.length();
    		query = query.replace("  ", " ");
    	} while (prevlength > query.length());
    	
    	query = query.trim();
    	
    	return query;
    }*/
    
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
    		
    		} else {
    			submitGenericServerQuery(query);
    		}
    		
		} else {
			//just send message within the chat channel (or something...)
			Server.sendChatMessageToChannel(this, query);
		}
    }

    
    
  //pre: query is lowercase and trimmed
    private  void submitClientQueryToGame(String query) {
    	if (currentGameRoom == null) {
    		System.out.println("Current game room is null WTF");
    	}
    	if (currentGameRoom.getGame() == null) {
    		//this is here because of a race condition. (currentGameRoom.getGame() is not set yet)
    		//When this happens, I think we should just do nothing.
    	} else {
    		currentGameRoom.getGame().submitClientQuery(this, query);
    	}
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
	    					sendImmediateResponseToClient("Countdown started.");
	    					//TODO: Added My 18th 2015: does this work?
	    					Server.removeOldGames();
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
	    			
	    		} else if(query.startsWith("/swap")) {
	    			if(args.length > 1) {
	    				swapSlots(args[1]);
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
    			sendImmediateResponseToClient("Game is started. TODO: make the game figure it out! (I think you have to resend the request so the game could figure it out)");
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
 				
	 			inviter.sendMessageToClient(this.clientName + " accepted your invitation.");
 				
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
				inviter.sendMessageToClient(this.clientName + " declined your invitation.");
				sendImmediateResponseToClient("sent decline of invitation.");
				
			}
 			invitation = "";
			inviter = null;
 			 
 			
 		} else {
 			sendImmediateResponseToClient("ERROR: unknown command. Type /h or /help for help.");
 		}
    }

    private void sendImmediateResponseToClient(String immediateResponse) {
    	sendMessageToClient(immediateResponse);
    }

    public void sendGameQueryToClient(String query) throws IOException {
    	currentGameRoom.getGame().submitClientQuery(this, query);
    }
    
    
    public String sendInvite(Profile sender, GameRoom room) {
    	String ret = "";
    	
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
    	
    	return ret;
    }
    
    public void sendMessageFromOtherClientToClient(String sender, String message) throws IOException {
    	sendMessageToClient(sender + " whispers: " + message);
    }
    


    public boolean isMiniServerConnectionOpen() {
    	return connection.isConnectionStillOpen();
    }
    
    //I might want to synchronize these functions... but I have to be careful about deadlocks...
    ArrayList <String>msgSendBuffer = new ArrayList<String>();
    public void sendMessageToClient(String message)  {
    	synchronized(lockSendMessageToClient) {
	    	try {
	    		this.connection.sendMessageToClient(message);
			} catch (IOException e) {
				msgSendBuffer.add(message);
			}
    	}
    }
    
    //pre: current connection is broken
    public Profile setNewConnection(MiniServer newConnection) {
    	
    	//see: https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html
    	synchronized(lockSendMessageToClient) {
	    	boolean allMessagesReceived = true;
	    	
	    	for(int i=0; i<msgSendBuffer.size(); i++) {
	    		try {
	    			
	    			if(msgSendBuffer.get(i).equals("/disc") == false) {
	    				newConnection.sendMessageToClient(msgSendBuffer.get(i));
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    			allMessagesReceived = false;
	    		}
	    	}
	    	if(allMessagesReceived) {
	    		msgSendBuffer = new ArrayList<String>();
	    	}
	    	this.connection = newConnection;
	    	
	    	return this;
    	}
    }

}

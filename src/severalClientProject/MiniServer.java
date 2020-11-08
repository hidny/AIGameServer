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

public class MiniServer extends Thread {

	public static final String EOC = "Goodbye.";

	private Object lockSubmitQueryFromClientMiniServ = new Object();
	private Object lockSendMessageToClientMiniServ = new Object();

	public static final String EOT = "**end of transmission**";

	private Socket socket = null;

	private Profile clientProfile = null;

	private BufferedReader inFromClient;
	private DataOutputStream outToClient;

	// private String immediateResponse;

	private boolean isConnected = true;

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
				
				System.out.println("TEST Before read in miniserver!");
				clientSentence = inFromClient.readLine();
				
				if(clientSentence == null) {
					System.out.println("ERROR: Found a null when getting message from client!");
					break;
				}
				System.out.println(clientSentence);
				System.out.println("TEST After read in miniserver!");
				
				if(clientSentence != null) {
					submitClientQuery(clientSentence);
				}
				
			}
			
			if(clientProfile != null) {
				System.out.println("No longer listening for requests from " + clientProfile.getClientName());
				sendMessageToClient(EOC + "\n");
			} else {
				System.out.println("Login failed");
				
			}
			
			
			outToClient.close();
			inFromClient.close();
			socket.close();
    	
    	} catch(java.net.SocketException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	isConnected = false;
    	
    }

	public void sendMessageToClient(String message) throws IOException {
		synchronized (lockSendMessageToClientMiniServ) {
			System.out.println("Trying to send: " + message + '\n' + EOT);

			if (message.equals("/disc")) {
				isConnected = false;
				return;
			}
			// sanitize output just in case:
			while (message.endsWith("\n")) {
				message = message.substring(0, message.length() - 1);
			}

			// outToClient.writeBytes(message /*+ '\n'*/ + EOT /*+ '\n'*/ +
			// typeOfRequest);
			outToClient.writeBytes(message + '\n');
			outToClient.writeBytes(EOT + '\n');
			System.out.println("End of write bytes!");
		}
	}

	public boolean isConnectionStillOpen() {
		return isConnected;
	}

	private void submitClientQuery(String query) {
		synchronized (lockSubmitQueryFromClientMiniServ) {
			if (clientProfile == null) {
				String clientName = query.trim();

				if (Server.isClientNameTaken(clientName) == false) {
					System.out.println("TEST: Creating new profile: ");
					this.clientProfile = Server.createNewProfile(this, clientName);
				} else if (Server.isClientNameWaitingForConnection(clientName) == true) {
					this.clientProfile = Server.reconnectToProfile(this, clientName);
				} else {
					try {
						sendMessageToClient("ERROR: " + clientName + " is already online.");
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}

				// TODO: block until profile allows the sending of msgs back to
				// client...
				while (this.clientProfile.isMiniServerConnectionOpen() == false) {
					// TODO: just do a thread sleep because you're lazy...
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(":(");
				}

				try {
					sendMessageToClient("Hello, " + clientName + ". Type /help or /h for help.");
				} catch (IOException e) {
					System.err.println("USER that isn't logged in quit the game.");
					e.printStackTrace();
				}

			} else {
				clientProfile.submitClientQuery(query);
			}
		}

	}

}

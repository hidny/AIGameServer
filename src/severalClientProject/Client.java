package severalClientProject;

import java.io.*;
import java.net.*;

class Client {
	
	//Chat program:
	//http://www.java-gaming.org/index.php?topic=22911.0
	
	//I read http://stackoverflow.com/questions/6672413/in-what-way-is-java-net-socket-threadsafe
	//to make sure I can read and write on the same socket 
	
	public static final String EOT = "**end of transmission**";
	public static final String EOC = "Goodbye";
	
    public static void main(String argv[]) throws Exception {
    	
    	ClientListener serverListener = null;
		
		//AIArena is taken... :( GameArena instead.
		try {
			Socket clientSocket = new Socket("localhost", 6789);
			
			
			serverListener = new ClientListener(clientSocket);
			
			serverListener.start();
			
			handleClientCommands(serverListener, clientSocket);
			
			clientSocket.close();
		 } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
		 } catch(java.net.SocketException e) {
			 
			System.err.println("ERROR socket closed unexpectedly: " + e);
			e.printStackTrace();
			System.exit(1);
			 
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
    public static void handleClientCommands(ClientListener listener, Socket clientSocket) throws IOException {
    	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    	DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
    	
    	boolean nameGiven = false;
    	String clientQuery = "";
		
    	while(listener.isConnected()) {
			
			if(nameGiven == false) {
				System.out.println("What is your name? GIT HUB TEST!");
				System.out.print("> ");
			}
			clientQuery = inFromUser.readLine();
			
			if(listener.isConnected()) {
				outToServer.writeBytes(clientQuery + '\n');
			}
			
			//TODO: actually put in some registration logic here. (Also in server)
			if(nameGiven == false) {
				nameGiven = true;
			}
		
		}
    	
    	inFromUser.close();
		outToServer.close();
    }
    
    
    
}
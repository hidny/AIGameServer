package severalClientProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientListener extends Thread {

	public static final String EOT = "**end of transmission**";
	public static final String EOC = "Goodbye.";
	
    private Socket socket = null;

   
    private BufferedReader inFromServer;
    
    private boolean connected = true;
     
    
   
    public ClientListener(Socket socket) {
        super("ClientListener");
        this.socket = socket;
    }
    
    public boolean isConnected() {
    	return connected;
    }

    public void run() {
    	String serverResponse;
    	String currentServerLine;
    	
    	try {
    		
    		//Read input and process here
    		inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			
			while(this.connected) {
				
				
				serverResponse = "";
				while(this.connected) {
					currentServerLine = inFromServer.readLine();
					if(currentServerLine.equals(EOT)) {
						break;
					} else if(currentServerLine.equals(EOC)) {
						this.connected = false;
						break;
					}
					
					serverResponse = serverResponse + currentServerLine + '\n';
				}
				
				
				System.out.println();
				System.out.println("From server: ");
				System.out.print(serverResponse);
				System.out.print("> ");
			}
			
			inFromServer.close();
			socket.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    }

}

package severalClientProject;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataOutputStreamWrapper {

	private DataOutputStream outToClient;

	public static final String EOT = "**end of transmission**";
	
	public DataOutputStreamWrapper(DataOutputStream outputStream) {
		this.outToClient = outputStream;
	}
	
	public synchronized void sendMessageToClient(String message)  throws IOException {
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
	
	public void close() throws IOException {
		this.outToClient.close();
	}
	

}

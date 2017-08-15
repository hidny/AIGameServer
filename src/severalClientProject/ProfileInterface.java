package severalClientProject;

public interface ProfileInterface {

	public String getClientName();
	
	
	//TODO: this doesn't have any throws exception, therefore:
	//handle exceptions created by MiniServer inside profile.
	public void sendMessageToClient(String message);
	
	
	public void receiveBan();
	
	public void forcePlayerOutOfGameRoom();
}

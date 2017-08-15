package severalClientProject;

public interface Game {
	public void startGameForClients(ProfileInterface player[]);
	
	public void submitClientQuery(ProfileInterface player, String query);
}

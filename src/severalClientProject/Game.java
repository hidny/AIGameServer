package severalClientProject;

public interface Game {
	public void startGameForClients(MiniServer player[]);
	
	public void submitClientQuery(MiniServer player, String query);
}

package severalClientProject;

public interface Game {
	public void startGame(MiniServer player[]);
	
	public void submitClientQuery(MiniServer player, String query);
}

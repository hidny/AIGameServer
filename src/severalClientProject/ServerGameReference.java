package severalClientProject;

//TODO: Add more games

//TODO: put more game specific logic here.
public class ServerGameReference {

	public static String HOLD_EM = "holdem";
	public static String CONNECT_FOUR = "connect_four";
	public static String RPS = "rock_paper_scissors";
	public static String CHESS = "chess";
	
	public static severalClientProject.Game createGame(String gameName) {
		return createGame(gameName, null);
	}
			
	public static severalClientProject.Game createGame(String gameName, String args[]) {
		if(gameName.toLowerCase().equals(HOLD_EM)) {
			return new holdem.HoldemServerMiddleMan();
		} else if(gameName.toLowerCase().equals(CONNECT_FOUR)) {
			return new connectFour.ConnectFour();
		} else if(gameName.toLowerCase().equals(RPS)) {
			return new rockPaperScissors.RockPaperScissors();
		} else if(gameName.toLowerCase().equals(CHESS)) {
			//TODO: host shouldn't always have to be white...
			return new chess.ChessServerMiddleMan();
		} else {
			return new connectFour.ConnectFour();
		}
	}
}

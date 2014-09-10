package severalClientProject;

//TODO: Add more games

//TODO: put more game specific logic here.
public class ServerGameReference {

	public static String HOLD_EM = "holdem";
	public static String CONNECT_FOUR = "connect_four";
	public static String RPS = "rock_paper_scissors";
	public static String CHESS = "chess";
	public static String MELLOW = "mellow";
	public static String REVERSI = "reversi";
	
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
		} else if(gameName.toLowerCase().equals(MELLOW)) {
			//TODO: host shouldn't always have to be white...
			//Or make extra slots to play with the ordering.
			return new mellow.MellowServerMiddleMan();
		} else if(gameName.toLowerCase().equals(REVERSI)) {
			//TODO: host shouldn't always have to be dark...
			return new reversi.ReversiMiddleMan();
		} else {
			return new connectFour.ConnectFour();
		}
	}
}

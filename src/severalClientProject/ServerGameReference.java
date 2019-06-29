package severalClientProject;

public class ServerGameReference {
	

	public static String HOLD_EM = "holdem";
	public static String CONNECT_FOUR = "connect_four";
	public static String RPS = "rock_paper_scissors";
	public static String CHESS = "chess";
	public static String MELLOW = "mellow";
	public static String REVERSI = "reversi";
	public static String FRUSTRATION = "frustration";
	public static String EUCHRE = "euchre";
	
			
	public static severalClientProject.Game createGame(String gameName, String args[]) {
		if(gameName.toLowerCase().equals(HOLD_EM)) {
			return new holdem.HoldemServerMiddleMan();
		} else if(gameName.toLowerCase().equals(CONNECT_FOUR)) {
			return new connectFour.ConnectFour();
		} else if(gameName.toLowerCase().equals(RPS)) {
			return new rockPaperScissors.RockPaperScissors();
		} else if(gameName.toLowerCase().equals(CHESS)) {
			return new chess.ChessServerMiddleMan();
		} else if(gameName.toLowerCase().equals(MELLOW)) {
			//Or make extra slots to play with the ordering.
			return new mellow.MellowServerMiddleMan(args);
		} else if(gameName.toLowerCase().equals(EUCHRE)) {
			//Or make extra slots to play with the ordering.
			return new euchre.EuchreServerMiddleMan();
		} else if(gameName.toLowerCase().equals(REVERSI)) {
			return new reversi.ReversiMiddleMan();
		} else if(gameName.toLowerCase().equals(FRUSTRATION)) {
			return new frustrationIrwin.FrustrationServerMiddleMan();
		} else {
			return new connectFour.ConnectFour();
		}
	}
}

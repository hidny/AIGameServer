package chess;

import java.util.ArrayList;

import severalClientProject.ProfileInterface;


public class ClientPlayer implements Player {
	
	private ProfileInterface player;
	private ChessServerMiddleMan middleMan;
	
	public ClientPlayer(ProfileInterface player, ChessServerMiddleMan middleMan) {
		this.player = player;
		this.middleMan = middleMan;
	}
	
	public String getBestMove(String fenPos) {
		Position pos = new Position(fenPos);
		ArrayList<String> choiceList = new ArrayList<String>();
		String move;
		String explicitMove;
		
		choiceList = ChessPGNParser.getListOfMovesPGN(pos);
		
		move = getClientChoice(pos, choiceList);
		
		explicitMove = ChessPGNParser.convertPGNToExplicitNotation(pos, move);
		
		return explicitMove;
	}
	
	//sets the depth:
	//applies if the player is a computer.
	public void setDepth(int depth) {
		//lol do nothing.
		System.out.println("I haven\'t implemented brain enhancement functions yet...");
	}
	
	//gets the name of the player.
	//Even AIs should have name.
	public String getName() {
		return player.getClientName();
	}
	
	
	public String getClientChoice(Position pos, ArrayList<String> choiceList) {
		middleMan.sendMessageToPlayer(player, pos.toString());
		middleMan.sendMessageToPlayer(player, this.getName() + ": which move?");
		String choices = "";
		for(int i=0; i<choiceList.size(); i++) {
			choices += choiceList.get(i);
			if(i<choiceList.size() - 1) {
				choices += ", ";
			}
		}
		choices += "." + "\n";
		middleMan.sendMessageToPlayer(player, choices);
				
		middleMan.sendMessageToPlayer(player, "length: " + choiceList.size());
		
		String ret = ChessServerMiddleMan.UNENTERED_MOVE;
		try {
			do {
				if(ret.equals(ChessServerMiddleMan.UNENTERED_MOVE) == false) {
					//At this point, we know the client didn't punch in a legal move:
					middleMan.sendMessageToPlayer(player, "Please choose an actual move.");
				}
				
				ret = ChessServerMiddleMan.UNENTERED_MOVE;
				
				while(ret.equals(ChessServerMiddleMan.UNENTERED_MOVE)) {
					ret = middleMan.getNextMove(player);
					Thread.sleep(1000);
					//TODO:
				}
				
				middleMan.setMoveTaken(player);
				
				ret = getMorePreciseDescriptionOfMove(choiceList, ret);
				
			} while(isInList(choiceList, ret) == false);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return ret;
	}
	
	public static boolean isInList(ArrayList<String> choiceList, String ret) {
		for(int i=0; i<choiceList.size(); i++) {
			if(choiceList.get(i).equals(ret)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String getMorePreciseDescriptionOfMove(ArrayList<String> choiceList, String ret) {
		ret = ChessPGNParser.insertEqualSignForPromotionIfApplicable(ret);
		ret = ret.trim().toLowerCase();
		
		String temp;
		for(int i=0; i<choiceList.size(); i++) {
			temp = choiceList.get(i).toLowerCase();
			if(temp.equals(ret) || temp.equals(ret + "+") || temp.equals(ret + "#")) {
				return choiceList.get(i);
			}
			
		}
		
		return "nope";
	}
	
	

}

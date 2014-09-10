package connectFour;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class TESTConnectFour {
	
	
	
	private String player1 = null;
	private String player2 = null;
	
	PrintWriter outputTest;
	
	Scanner in;
	//TODO: don't copy/paste code in future!
	//the test logic should be the same as the real logic.
	
	//TODO: figure it out:
	//private Thread connect4Waiter = null;
	
	public void startGame(Scanner in, int num) {
		
		player1 = in.next();
		//vs:
		in.next();
		player2 = in.next();
		
		this.in = in;
		
		
		try {
			outputTest = new PrintWriter(new File("connect4ReplayOutput\\connect4Output" + num + ".txt"));

			this.playGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputTest.close();
	}
	
	public void playGame() {
		
		TESTConnectFourPosition pos = new TESTConnectFourPosition(outputTest);
		
		
		pos.recordOutput(player1 + " vs " + player2);
		//read past the first line:
		in.nextLine();
				
		
		while(pos.isGameOver() == false && pos.isTie() == false) {
			pos = playTurn(player1, pos);
			
			if(pos.isGameOver() == true) {
				break;
			}
			
			pos = playTurn(player2, pos);
			
		}
		
		//ConnectFourPosition.sendMessageToGroup(this.playersWatchingOrPlaying, "Final position:");
		pos.recordOutput("Final position:");
		
		if(pos.isTie()) {
			pos.recordOutput("Tie Game!");
		}
		pos.printPos();
		pos.endGame();
	}
	
	
	
	
	
	public TESTConnectFourPosition playTurn(String player, TESTConnectFourPosition pos) {
		pos.printPos();
		
		String temp = in.nextLine();
		System.out.println(temp);
		int moveNum = (temp.charAt(temp.length() - 2) - '0');
		
		pos = pos.playLegalTurn(moveNum);
		
		return pos;
	}
	

}

package rockPaperScissors;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;


public class TESTRockPaperScissors {
	
	
	
	private String player1 = null;
	private String player2 = null;
	
	PrintWriter outputTest;
	
	Scanner in;
	
	//TODO: make this changeable
	private static int NUM_WINS_TO_FINISH = 5;
	
	
	private static String UNENTERED_MOVE = "";
	
	private String player1Answer = "";
	private String player2Answer = "";
	
	private int player1Wins = 0;
	private int player2Wins = 0;
	
	//TODO: figure it out:
	//private Thread connect4Waiter = null;
	
	public void startGame(Scanner in, int num) {
		
		player1 = in.next();
		//vs:
		in.next();
		
		player2 = in.next();
		
		this.in = in;
		
		
		try {
			outputTest = new PrintWriter(new File("rockPaperScissorsReplayOutput\\rockPaperScissorsOutput" + num + ".txt"));

			this.playGame();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		outputTest.close();
	}
	
	public void playGame() {
		
		String temp;
		
		
		this.recordOutput(player1 + " vs " + player2);
		//read past the first 2 line:
		in.nextLine();
		in.nextLine();
		
		//this.recordCommandMsg("First to " + NUM_WINS_TO_FINISH + " wins wins the game");
		this.recordOutput("First to " + NUM_WINS_TO_FINISH + " wins wins the game");
		//sendMessageToGroup(playersWatchingOrPlaying, "First to " + NUM_WINS_TO_FINISH + " wins wins the game");
		
		int roundNum = 0;
		
		while(player1Wins < NUM_WINS_TO_FINISH && player2Wins < NUM_WINS_TO_FINISH) {
			roundNum++;
			this.recordOutput("Score: " + player1Wins + "-" + player2Wins);
			
			this.recordOutput("Round " + roundNum + ":");
			
			
			temp = in.nextLine();
			
			//this.recordCommandMsg(player1Answer + " " + player2Answer);

			player1Answer = temp.split(" ")[0];
			player2Answer = temp.split(" ")[1];
			
			
			if(player1Answer.equals(player2Answer)) {
				//sendMessageToGroup(playersWatchingOrPlaying, "Tie");
				this.recordOutput(player1Answer + " = " + player2Answer);
			} else if((player1Answer.equals("R") && player2Answer.equals("S")) || (player1Answer.equals("S") && player2Answer.equals("P"))  || (player1Answer.equals("P") && player2Answer.equals("R"))) {
				this.recordOutput(player1Answer + " > " + player2Answer);
				player1Wins++;
				if(player1Wins < NUM_WINS_TO_FINISH) {
					//sendMessageToGroup(playersWatchingOrPlaying, player1 + " wins this round!");
					this.recordOutput(player1 + " wins this round!");
				}
			} else if((player2Answer.equals("R") && player1Answer.equals("S")) || (player2Answer.equals("S") && player1Answer.equals("P"))  || (player2Answer.equals("P") && player1Answer.equals("R"))){
				this.recordOutput(player1Answer + " < " + player2Answer);
				player2Wins++;
				if(player2Wins < NUM_WINS_TO_FINISH) {
					//sendMessageToGroup(playersWatchingOrPlaying, player2 + " wins this round!");
					this.recordOutput(player2 + " wins this round!");
				}
			} else {
				//sendMessageToGroup(playersWatchingOrPlaying, "WTF! Shit!");
			}
			player1Answer = UNENTERED_MOVE;
			player2Answer = UNENTERED_MOVE;
		}
		
		if(player1Wins >= NUM_WINS_TO_FINISH) {
			this.recordOutput(player1 + " wins the game!");
		} else {
			this.recordOutput(player1 + " wins the game!");
		}
		
	}
	
	
	
	public void recordOutput(String output) {
		outputTest.println(output);
	}

}

package gameUtils;

import java.io.File;
import java.io.PrintWriter;

//TODO: generalize this for all games.

public class GameReplayPrinter {
	
	private PrintWriter commandFile = null;
	private PrintWriter commandOutput = null;
	
	
	public GameReplayPrinter(String gameName) {
		int num = getTestCaseNumber(gameName);
		
		commandFile = getNewCommandWriter(gameName, num);
		
		commandOutput = getNewOuput(gameName, num);
		
	}
	
	public synchronized void printCommand(String msg) {
		commandFile.println(msg);
		commandFile.flush();
		
	}
	
	public synchronized void printOutput(String msg) {
		commandOutput.println(msg);
		commandOutput.flush();
	}
	
	public synchronized void close() {
		commandFile.close();
		commandOutput.close();
	}
	
	public synchronized static int getTestCaseNumber(String gameName) {
		int num = 0;
		File f;
		try {
			do {
				num++;
				f = new File("C:\\Users\\Public\\Documents\\GameServerReplays\\" + gameName + "Commands\\" + gameName + "Commands" + num + ".txt");
			} while(f.exists());
			
			
		} catch( Exception e) {
			num = -1;
			e.printStackTrace();
		}
		
		return num;
		
	}
	
	public static PrintWriter getNewCommandWriter(String gameName, int num) {
		PrintWriter ret = null;
		try {
			
			ret = new PrintWriter(new File("C:\\Users\\Public\\Documents\\GameServerReplays\\" + gameName + "Commands\\" + gameName + "Commands" + num + ".txt"));
			
		} catch( Exception e) {
			e.printStackTrace();
		}
		return ret;
		
	}
	
	public static PrintWriter getNewOuput(String gameName, int num) {
		PrintWriter ret = null;
		try {
			ret = new PrintWriter(new File("C:\\Users\\Public\\Documents\\GameServerReplays\\" + gameName + "Output\\" + gameName + "Output" + num + ".txt"));
			
		} catch( Exception e) {
			e.printStackTrace();
		}
		return ret;
		
	}
	
	//TODO: use this.
	public static PrintWriter getNewReplayOuput(String gameName, int num) {
		PrintWriter ret = null;
		try {
			ret = new PrintWriter(new File("C:\\Users\\Public\\Documents\\GameServerReplays\\" + gameName + "ReplayOutput\\" + gameName + "Output" + num + ".txt"));
			
		} catch( Exception e) {
			e.printStackTrace();
		}
		return ret;
		
	}
}

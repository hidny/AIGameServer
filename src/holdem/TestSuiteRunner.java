package holdem;

import java.io.File;

public class TestSuiteRunner {

	public static void main(String[] args) {
		//plan: for all files of the form testX.txt, create a file outputX.txt in outputText by calling
		//main of headsUpMain with args: "-testrun testX.txt -o outputX.txt"
		int limit = 1000;
		
		if(args.length >= 1) {
			limit = Integer.parseInt(args[0]);
		}
		File nextTestCase;
		for(int i=0; i<limit; i++) {
			nextTestCase = new File("holdemCommands\\test" + (i+1) + ".txt");
			if(nextTestCase.exists()) {
				HeadsUpMain.main(new String[]{"-testrun",  "test" + (i+1) + ".txt", "-o", "holdemoutput" + (i+1) + ".txt", "-console"});
			}
		}
		
		//NOTE: i don't know how to print to console once I set out...
		
	}

}

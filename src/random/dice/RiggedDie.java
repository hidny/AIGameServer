package random.dice;

import java.util.Scanner;

public class RiggedDie implements Die {
	public static Scanner inTestRun;
	 
	public RiggedDie(Scanner in) {
		this(in, 1);
	}
	
	public RiggedDie(Scanner input, int numSides) {
		 inTestRun = input;
	}
	 
	public RiggedDie(Scanner input, String symbols[]) {
		 inTestRun = input;
	}
	 
	public String getRoll() {
		 return inTestRun.nextLine().trim();
	}
}

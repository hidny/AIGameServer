package random.dice;

import java.io.PrintWriter;

public class RandomDie implements Die {
	private int numSides;
	private String symbols[];

	 private PrintWriter record;
	 
	//Default die is 6 sided:
	public RandomDie() {
		this(6, null);
	}
	
	public RandomDie(int numSides) {
		this(numSides, null);
	}
	
	public RandomDie(String symbols[]) {
		this(symbols, null);
	}
	
	public RandomDie(PrintWriter record) {
		this(6);
	}
	
	public RandomDie(int numSides, PrintWriter record) {
		this.record = record;
		this.numSides = numSides;
		this.symbols = new String[this.numSides];
		for(int i=0; i<this.numSides; i++) {
			this.symbols[i] = (i+1) + "";
		}
	}
	
	public RandomDie(String symbols[], PrintWriter record) {
		this.record = record;
		this.numSides = symbols.length;
		this.symbols = new String[this.numSides];
		for(int i=0; i<this.numSides; i++) {
			this.symbols[i] = symbols[i];
		}
	}
	
	public String getRoll() {
		int rollIndex = (int)(numSides * Math.random());
		
		if(record != null) {
			record.println(symbols[rollIndex]);
		}
		return symbols[rollIndex];
	}
}

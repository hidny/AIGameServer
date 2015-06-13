package frustrationIrwin;

public interface PlayerDecider {
	public void start(String names[], int startIndex);
	
	public void updateMove(int roll, int move);
	
	public int getMove(int roll, int pos[], int startIndex);
	
	public String getName();
}

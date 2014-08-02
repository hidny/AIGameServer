package severalClientProject;

public class GameStarter implements Runnable {
	private GameRoom gameRoom;

    public GameStarter(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public void run() {
        // code in the other thread, can reference "var" variable
    	countDown();
    }
    

	private String countDown() {
		this.gameRoom.sendGameRoomPlayersMessage("", "Starting game in:");
		for(int i=0; i<GameRoom.NUM_SECONDS_COUNTDOWN; i++) {
			this.gameRoom.sendGameRoomPlayersMessage("", "" + (GameRoom.NUM_SECONDS_COUNTDOWN - i));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		String errorMsg = this.gameRoom.startGame();
		
		return errorMsg;
		//TODO: use error message.
	}
}

package holdem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import severalClientProject.MiniServer;
import deck.*;

public class HeadsUpMain {

	
	//To add test cases:
	//-record test3.txt -o output3.txt
	public static void main(String[] args) {
		if(args.length == 0){
			startHeadsUp(new String[] {"-p", "2", "-recordNext", "-console"}, new HoldemServerMiddleMan());
		} else {
			startHeadsUp(args, new HoldemServerMiddleMan());
		}
	}
	
	public static void startHeadsUp(String[] args, HoldemServerMiddleMan middleman) {
		int initStack = 1000;
		int initBB = 2;
		
		int NUM_INITIAL_PLAYERS = 2;
		int NUM_GAMES = 1;
		
		boolean isTestRun = false;
		boolean recordRun = false;
		Scanner inTestRun = null;
		
		PrintWriter commandRecord = null;
		PrintWriter output = null;
		boolean useSingleConsoleForTesting = false;
		
		try {
			for(int i=0; i<args.length; i++) {
				if(args[i].equals("-p")) {
					NUM_INITIAL_PLAYERS = Integer.parseInt(args[i+1]);
				
				} else if(args[i].equals("-testrun")) {
					isTestRun = true;
					inTestRun = new Scanner(new File("holdemCommands\\" + args[i+1]));
				
				} else if(args[i].equals("-o")) {
					output = new PrintWriter("holdemReplayOutput\\" + args[i+1]);
				
				} else if(args[i].equals("-record")) {
					recordRun = true;
					if(new File("holdemCommands\\" + args[i+1]).exists() || new File("holdemOutput\\" + args[i+2]).exists()) {
						System.out.println("Do not overwrite previous records!");
						System.exit(1);
					}
					commandRecord = new PrintWriter("holdemCommands\\" + args[i+1]);
					output = new PrintWriter("holdemOutput\\" + args[i+2]);
				
				} else if(args[i].equals("-recordNext")) {
					recordRun = true;
					int numInName=1;
					while(new File("holdemCommands\\test" + numInName + ".txt").exists()) {
						numInName++;
					}
					
					commandRecord = new PrintWriter("holdemCommands\\test" + numInName + ".txt");
					output = new PrintWriter("holdemOutput\\output" + numInName + ".txt");
				} else if(args[i].equals("-console")) {
					useSingleConsoleForTesting = true;
				}
			}
			
			if(recordRun &&  isTestRun) {
				System.out.println("You aren't allowed to record a testrun!");
				System.exit(1);
			}
			
			HoldemPlayerDecider player[];
			
			if(useSingleConsoleForTesting) {
				player = setupDefaultConsolePlayers(NUM_INITIAL_PLAYERS, commandRecord);
			} else {
				player = setupDefaultClientPlayers(NUM_INITIAL_PLAYERS, middleman);
			}
			
			//set middleman output stream files:
			middleman.setCommandFileWriter(commandRecord);
			middleman.setOutputFileWriter(output);
			
			for(int i=0; i<NUM_GAMES; i++) {
				if(isTestRun == false) {
					runNormal(player, initStack, initBB, middleman);
				} else {
					runTestRun(inTestRun, initStack, initBB, middleman);
				}
			}
			
			if(commandRecord != null) {
				commandRecord.close();
			}
			
			if(inTestRun != null) {
				inTestRun.close();
			}
			
		} catch(FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
		}
	}
	
	/*public static void test2Players() {
		int initStack = 1000;
		int initBB = 2;
		
		
		HoldemPlayerDecider player[] = setupDefaultPlayers(2);
		
		for(int i=0; i<1; i++) {
			runNormal(player, initStack, initBB);
		}
	}*/
	
	public static void runTestRun(Scanner inTestRun, int initialStackPerPlayer, int initBB, HoldemServerMiddleMan middleMan) {
		HoldemPlayerDecider player[] = initTestPlayers(inTestRun, inTestRun.nextLine(), middleMan);
		Deck deck = new RiggedDeck(inTestRun);
		run(deck, player, initialStackPerPlayer, initBB, middleMan);
	}
	
	
	public static void runNormal(HoldemPlayerDecider player[], int initialStackPerPlayer, int initBB, HoldemServerMiddleMan middleMan) {
		initRandSeating(player, middleMan);
		Deck deck = new DeckRandom(middleMan.getCommandFile());
		run(deck, player, initialStackPerPlayer, initBB, middleMan);
	}
	
	public static void run(Deck deck, HoldemPlayerDecider player[], int initialStackPerPlayer, int initBB, HoldemServerMiddleMan middleMan) {
		int playersStillCompeting = player.length;
		
		HoldEmPlayerLog[] playerLog = initMoney(player, initialStackPerPlayer, middleMan);
		
		HoldEmRoundLogic roundLogic;
		
		//HoldEmRoundInfo roundInfo = HoldEmRoundInfo.setupFirstRound(playerLog, initBB, 0);
		checkNamesAreAllDiferent(player, middleMan);
		
		int dealerPos = 0;
		int ante = 100;
		int bigBlind = initBB;
		
		int roundNum = 0;
		// loop until someone wins:
		while(HoldEmTableFunctions.getNumberOfPeopleStillInTourney(playerLog) > 1) {
			roundNum++;
			
			dealOut(playerLog, dealerPos, deck);
			
			roundLogic = new HoldEmRoundLogic(playerLog, dealerPos, bigBlind, bigBlind/2, ante, middleMan);
			
			printStartOfRoundInfo(bigBlind, bigBlind/2, ante, dealerPos, middleMan);
			
			playerLog = roundLogic.playOutRound(player, deck);
			
			//TODO:
			//updateBigBlindAnte and positions as the game progresses based on how game was setup.
			int ret[] = updateBigBlindAnteAndPos(playerLog, bigBlind, bigBlind/2, ante, dealerPos, middleMan);
			dealerPos = ret[0];
			bigBlind = ret[1];
			ante = ret[3];
			

			//TODO: deal with dead token here!
			
			playersStillCompeting = endRound(playerLog, playersStillCompeting, dealerPos, middleMan);
			
			
			middleMan.sendMessageToGroup("End of Round #" + roundNum + "!");
			middleMan.sendMessageToGroup("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
			middleMan.sendMessageToGroup("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
		}
		
		
		middleMan.sendMessageToGroup("End of game. Goodbye!");
	}
	
	
	public static int endRound(HoldEmPlayerLog[] playerLog, int numPlayersLeft, int newDealerPos, HoldemServerMiddleMan middleMan) {
		for(int i=0; i<playerLog.length; i++) {
			playerLog[i].endRound();
			middleMan.sendMessageToGroup(playerLog[i].getNumChips() + " for " + playerLog[i].toString());
			
			//Take players with no chips out of tournament. (note: round logic already does this.)
			if( playerLog[i].getNumChips() == 0 && playerLog[i].stillInTournament()) {
				playerLog[i].takeOutOfTournament(numPlayersLeft);
			}
		}
		
		return numPlayersLeft;
	}
	
	public static HoldemPlayerDecider[] setupDefaultClientPlayers(int numPlayers, HoldemServerMiddleMan middleMan) {
		HoldemPlayerDecider player[] = new HoldemPlayerDecider[numPlayers];
		
		MiniServer playerClient[] = middleMan.getClientPlayers();
		
		int playersAdded = 0;
		
		for(int i=0; i<playerClient.length && playersAdded < numPlayers; i++) {
			if(playerClient[i] != null) {
				player[i] = new HoldemClientPlayer(playerClient[i], middleMan);
				playersAdded++;
			}
		}
		
		if(playersAdded < numPlayers) {
			System.out.println("ERROR: number of players added wasn't enough in setup default clent Players. DEBUG!");
			System.exit(1);
		}
		
		return player;
	}
	
	public static HoldemPlayerDecider[] setupDefaultConsolePlayers(int numPlayers, PrintWriter record) {
		HoldemPlayerDecider player[] = new HoldemPlayerDecider[numPlayers];
		
		for(int i=0; i<player.length; i++) {
			player[i] = new HoldemConsolePlayerDecider("Player_" + (i+1), record);
		}
		
		return player;
	}
	
	
	//an O(n^2) algo that makes sure that all the names are different.
	public static void checkNamesAreAllDiferent(HoldemPlayerDecider players[], HoldemServerMiddleMan middleMan) {
		for(int i=0; i<players.length; i++) {
			for(int j=i+1; j<players.length; j++) {
				if( (players[i].getName()).equals( players[j].getName() ) ) {
					middleMan.sendMessageToGroup("ERROR: 2 of the player names are the same!");
					middleMan.sendMessageToGroup(players[i].getName());
					System.exit(0);
				}
			}
		}
	}
	
	//post: randomly initializes seats:
	public static void initRandSeating(HoldemPlayerDecider players[], HoldemServerMiddleMan middleMan) {
		int randIndex;
		for(int i=0; i<players.length; i++) {
			randIndex = (int) (Math.random() * players.length);
			
			swapPlayersSeat(players, i, randIndex);
		}
		
		String ret = "";
		for(int i=0; i<players.length; i++) {
				ret+= (players[i].getName() + " ");
		}
		
		middleMan.recordCommand(ret);
		
	}
	
	//post: put players in according to the script:
	public static HoldemPlayerDecider[] initTestPlayers(Scanner inTestRun, String line, HoldemServerMiddleMan middleMan) {
		String players[] = line.split(" ");
		HoldemPlayerDecider playerDecider[] = new HoldemPlayerDecider[players.length];
		for(int i=0; i<players.length; i++) {
			playerDecider[i] = new HoldemTestCasePlayer(players[i], inTestRun, middleMan);
		}
		
		return playerDecider;
	}
	
	
	public static void swapPlayersSeat(HoldemPlayerDecider players[], int i, int j) {
		HoldemPlayerDecider temp = players[i];
		players[i] = players[j];
		players[j] = temp;
	}
	
	//post: init money
	public static HoldEmPlayerLog[] initMoney(HoldemPlayerDecider players[], int amount, HoldemServerMiddleMan middleMan) {
		HoldEmPlayerLog playerLog[] = new HoldEmPlayerLog[players.length];
		for(int i=0; i<players.length; i++) {
			if(middleMan.getClientPlayers() != null) {
				playerLog[i] = new HoldEmPlayerLog(players[i].getName(), amount, middleMan, middleMan.getClientPlayers()[i]);
			} else {
				playerLog[i] = new HoldEmPlayerLog(players[i].getName(), amount, middleMan, null);
			}
		}
		return playerLog;
	}
	
	
	//pre: players doesn't contain any null indexes.
	//if the player is done but was supposed to be sb or bb, they become dead small & dead big...
	public static void dealOut(HoldEmPlayerLog players[], int dealerPos, Deck deck) {
		deck.shuffle();
		int indexToGive;
		
		for(int i=0; i<2; i++) {
			for(int j=0; j<players.length; j++) {
				
				//get index to give. Note: There's really dumb rules when it's heads up.
				if(players.length > 2) {
					indexToGive = (dealerPos + 1 + j) % players.length;
				} else {
					indexToGive = (dealerPos + j) % players.length;
				}
				
				if(players[indexToGive].stillInTournament()) {
					players[indexToGive].giveCard(deck.getNextCard());
				}
			}
		}
	}
	
	//TODO: test next dealer logic!
	//TODO: update this to something more realistic...
	//like updating big blinds!
	public static int[] updateBigBlindAnteAndPos(HoldEmPlayerLog[] playerLog, int bigBlind, int smallBlind, int ante, int indexOfDealer, HoldemServerMiddleMan middleMan) {
		middleMan.sendMessageToGroup("Im supossed to update BB and SB here...");
		int ret[] = new int[4];
		
		ret[0] = indexOfDealer;
		do {
			if(playerLog[ret[0]].stillInTournament() == false && playerLog[ret[0]].isDeadToken()) {
				playerLog[ret[0]].unsetDeadToken();
			}
			ret[0] = (ret[0] + 1)%playerLog.length;
		} while(playerLog[ret[0]].stillInTournament() == false && playerLog[ret[0]].isDeadToken()==false);
		
		
		ret[1] = bigBlind;
		ret[2] = smallBlind;
		ret[3] = ante;
		
		return ret;
	}
	
	public static void printStartOfRoundInfo(int bigBlind, int smallBlind, int ante, int indexOfDealer, HoldemServerMiddleMan middleMan) {
		middleMan.sendMessageToGroup("Start of Round!");
		middleMan.sendMessageToGroup("BB: " + bigBlind);
		middleMan.sendMessageToGroup("SB: " + smallBlind);
		middleMan.sendMessageToGroup("Ante: " + ante);
		middleMan.sendMessageToGroup("dealer position: " + indexOfDealer);
	}
	
}

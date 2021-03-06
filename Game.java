import java.util.*;

/**
 * FINAL GIRL Application - main class
 * Created by: Johnnalee Kutzke, Sam Siner, Ted Brockly, Tyler Arndt and Xin Su
 * The main class of the 'Final Girl' application, a text-based
 * adventure game. Users must navigate through rooms, search
 * for items and fight monsters before proceeding through one
 * of three levels.
 *
 *Sets the player stats such as name, location, and weapon.
 */
public class Game{

	private static boolean gameIsOver = false;

	public static void main(String[] args) {

		String[] itemDataList = {"level_1_master.txt", "level_2_master.txt", "level_3_master.txt"};
		Game game;

		String playerName = UI.promptLine("Enter a name for your player: ").trim();
		Item currentWeapon = new Item("slingshot", "weapon", 10);
		int health = 100;

		for (String dataFile: itemDataList){
			game = new Game(dataFile, playerName, currentWeapon, health);
			game.play();
			if (gameIsOver){
				System.out.println("The game is over!");
				return;
			}

			currentWeapon = game.getPlayer().getCurrentWeapon();
			health += 50;
		}

		System.out.println("Congratulations, you've escaped the haunted house!\nBut unfortunately, the princess is in another house...");

	}

	private Room currentRoom;
	private HashMap<String, Room> rooms; // allow all rooms to be found by name
	private Player currentPlayer;
	private String welcomeString;

	public Room getCurrentRoom()
	{
		return currentRoom;
	}

	public void setCurrentRoom(Room newRoom)
	{
		this.currentRoom = newRoom;
	}

	public Player getPlayer(){
		return currentPlayer;
	}

	public Game(String txt, String name, Item currentWeapon, int health)
	{
		Scanner levelScanner = ResourceUtil.openFileScanner(txt);
		welcomeString = FileUtil.readParagraph(levelScanner);
		rooms = Room.createRooms(levelScanner);
		currentRoom = rooms.get("hallway");

		// player
		currentPlayer = Player.createPlayer(this, name, currentWeapon, health);

		CommandMapper.init(this);

	}

	private boolean processCommand()
	{
		String line = UI.promptLine("> ").trim().toUpperCase(); // get user input

		if (line.length() == 0 || !CommandMapper.isCommand(line))
		{
			System.out.println("Please enter a command.");
			return false;
		}

		Action action = CommandMapper.getAction(line);
		return action.execute();
	}

	private void play()
	{
		System.out.println(welcomeString);
		currentRoom = new Room("hallway", null, "in the hallway", null);
		System.out.println("You are " + currentRoom.getDescription());
		currentPlayer.checkStatus();
		System.out.println("Please select an action to take. [INSPECT, GO, HELP, QUIT]");

		while (!processCommand()) {
			if (currentRoom.getName().equals("exit") && currentRoom.getMonster() == null){
				break;
			}
			System.out.println("Please select an action to take. [INSPECT, GO, HELP, QUIT]");
		}
	}

	public HashMap<String,Room> getRooms(){
	    return rooms;
	}

	public static void setGameIsOver(){
		gameIsOver = true;
	}
}

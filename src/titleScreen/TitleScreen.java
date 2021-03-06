package titleScreen;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import engine.GameCode;
import engine.GameObject;
import engine.ObjectHandler;
import engine.RenderLoop;
import engine.Sprite;
import engine.SpriteParser;
import map.Roome;
import menu.CompositeComponite;
import menu.Menu;
import menu.MenuComponite;
import menu.ObjectComponite;
import menu.TextComponite;
import network.Client;
import network.NetworkHandler;
import network.Server;
import npcs.ControlsTxt;
import npcs.NPC;
import npcs.PopcornMachine;
import npcs.SettingsTxt;
import npcs.TalkableNPC;
import npcs.TutorialNPC;
import resources.SoundPlayer;
import resources.Textbox;

public class TitleScreen extends GameObject {
	

	public static Sprite infographic = new Sprite ("resources/sprites/game infographic.png");
	
	private String ip;
	
	public static int perkNum = 15;
	
	private boolean typeIp = false;
	
	private static Textbox ipBox;
	private static Textbox infoBox;
	private static volatile int numPlayers = 0;
	
	private Button hostButton;
	private Button joinButton;
	private Button rulesButton;
	private Button perksButton;
	private Button settingsButton;
	
	private TitleCodeWalls walls = new TitleCodeWalls();
	
	private static boolean connected = false;
	
	private static Sprite lobbySprite = new Sprite ("resources/sprites/lobby.png");
	
	
	boolean ipMode = false;
	boolean isHost = false;
	boolean failedMode = false;
	boolean connectedMode = false;
	boolean waitMode = false;
	
	public static boolean titleClosed = false;
	
	static Server server;
	static Client client;
	
	public String mapLoadPath = null; //Set this to a filepath before closing the title screen to load a map
	public static boolean doMapSave = false; //Internal use
	public static String[] initialData = null;
	
	private TitleBit titleBit;
	private TitleRegister titleReg;
	private TitleSlot startGameSlot;
	private TitleSlot joinSlot;
	private TitleSlot perksSlot;
	private TitleSlot settingsSlot;
	private TitleSlot helpSlot;
	
	private PerkStation blastStation;
	private PerkStation gripStation;
	private PerkStation navStation;
	private PerkStation dupeStation;
	private PerkStation powerStation;
	private PerkStation dualStation;
	private PerkStation gamblerStation;
	
	private SettingsTxt settingsBot;
	private ControlsTxt controlMenu;
	private TutorialNPC tutorial;
	
	private static Scene perkScene;
	

	public static final int OBJECT_SPAWN_RING_PADDING = 140;

	private static HintMessage hintMessage;
	
	public static boolean tutorialMode = false;

	@Override
	public void onDeclare () {
		
		if (!NetworkHandler.isServer ()) {
			//Set stuff
			ip = "127.0.0.1:41881"; //Default IP
			
			//Make the buttons
			initMainMenu();
			
			//Make the textboxes
			ipBox = new Textbox ("");
			ipBox.declare (0, 32);
			ipBox.changeWidth (128);
			ipBox.changeHeight (128);
			ipBox.setFont ("text (red)");
			ipBox.changeBoxVisability ();
			
			ipBox.setRenderPriority(99);
			
			infoBox = new Textbox ("");
			infoBox.declare (0, 48);
			infoBox.changeWidth (128);
			infoBox.changeHeight (128);
			infoBox.setFont ("text (red)");
			infoBox.changeBoxVisability ();
			
			infoBox.setRenderPriority(99);
		}
		
	}
	
	public static void clearTitleScreen () {
		ObjectHandler.getObjectsByName("TitleBit").get(0).forget();
		ObjectHandler.getObjectsByName("TitleRegister").get(0).forget();
		ObjectHandler.getObjectsByName("PerkStation").get(0).forget();
		ObjectHandler.getObjectsByName("PerkStation").get(0).forget();
		ObjectHandler.getObjectsByName("PerkStation").get(0).forget();
		ObjectHandler.getObjectsByName("PerkStation").get(0).forget();
		ObjectHandler.getObjectsByName("PerkStation").get(0).forget();
		ObjectHandler.getObjectsByName("PerkStation").get(0).forget();
		ObjectHandler.getObjectsByName("PerkStation").get(0).forget();
		ObjectHandler.getObjectsByName("TitleSlot").get(0).forget();
		ObjectHandler.getObjectsByName("TutorialNPC").get(0).forget();
		ObjectHandler.getObjectsByName("SettingsTxt").get(0).forget();
		ObjectHandler.getObjectsByName("ControlsTxt").get(0).forget();
		ObjectHandler.getObjectsByName("TitleCodeWalls").get(0).forget();
		
	}
	
	@Override
	public void frameEvent () {
		
		if (keyPressed ('T')) {
			tutorialMode = true;
		}
		
		//Center the title screen
		Point centeringPt = calculateCenteringPoint ();
		GameCode.setView ((int)-centeringPt.getX (), (int)-centeringPt.getY ());
		
		//Handle typing with the IP
		if (ipMode && !isHost) {
			ArrayList<KeyEvent> events = getKeyEvents ();
			for (int i = 0; i < events.size (); i++) {
				KeyEvent currEvent = events.get (i);
				if (currEvent.getID () == KeyEvent.KEY_TYPED) {
					char c = currEvent.getKeyChar ();
					if ((c >= '0' && c <= '9') || (c == '.') || (c == ':')) {
						ip += c;
					}
				}
				if (currEvent.getID () == KeyEvent.KEY_PRESSED) {
					if (currEvent.getKeyCode () == KeyEvent.VK_BACK_SPACE || currEvent.getKeyCode () == KeyEvent.VK_DELETE) {
						if (ip.length () > 0) {
							ip = ip.substring (0, ip.length () - 1);
						}
					} else if (currEvent.getKeyCode () == KeyEvent.VK_ENTER) {
						doConnect ();
					} else if (currEvent.getKeyCode () == KeyEvent.VK_ESCAPE) {
						System.exit (0);
					}
				}
			}
		}
		
		if (startGameSlot != null && startGameSlot.isSelected ()) {
			
			if (tutorialMode) {
				mapLoadPath = "resources/maps/tutorial_map.txt";
				exitMainMenu ();
				enterHostMode ();
				doConnect ();
			} else {	
				exitMainMenu ();
				new GameMenu ().declare (0, 0);
			}
			
		}
		
		if (/*joinSlot.isSelected ()*/false) {
			//joinButton.reset ();
			
			//Remove the buttons

		}
		if (/*helpSlot.isSelected ()*/false) {
			exitMainMenu ();
			
			this.setSprite(infographic);
		
		}
		if (getSprite () == infographic && keyPressed (KeyEvent.VK_ESCAPE)) {
			
			ip = "";
			
			this.initMainMenu();
			//rulesButton.pressed = false;
		}
		if (/*perksSlot.isSelected ()*/false) {
			exitMainMenu ();
			
		}
		if (/*settingsSlot.isSelected ()*/false) {
			exitMainMenu ();
			//settingsButton.reset();
			SettingMenu menu = new SettingMenu (this);
			if (ObjectHandler.getObjectsByName("SettingMenu") == null || ObjectHandler.getObjectsByName("SettingMenu").size() == 0) {
				menu.declare();
			}
		}
		if (ipMode && !failedMode && !connectedMode && !waitMode) {
			
			//Change box contents for host
			if (isHost) {
				setupHostMode ();
			} else {
				//Change box contents for join
				setupJoinMode ();
			}
			
			if (keyPressed (KeyEvent.VK_ENTER)) {
				doConnect ();
			}
			
		}
		
	}
	
	public void doConnect () {
		if (isHost) {
			System.out.println ("STARTING");
			closeTitleScreen ();
			if (!NetworkHandler.isServer ()) {
				ipBox.forget ();
				infoBox.forget();
			}
			forget ();
		} else {
			if (!failedMode && !connected) {
				try {
					client = new Client (ip);
					client.start ();
					NetworkHandler.setClient (client);
				} catch (Exception e) {
					failedMode = true;
					ipBox.changeText ("CONNECTION FAILED. TRY USING A DIFFERENT IP OR CHECK YOUR FIREWALL SETTINGS.");
					infoBox.changeText ("");
					return;
				}
				//Connected, try to ping host
				waitMode = true;
				ipBox.changeText ("WAITING FOR HOST...");
				infoBox.changeText ("(IF THIS TAKES A LONG TIME, THERE MAY BE A CONNECTION ERROR)");
				client.joinServer ();
				
			} else {
				failedMode = false;
			}
		}
	}
	
	public void setupHostMode () {
		//Change box contents for host
		if (!NetworkHandler.isServer ()) {
			ipBox.changeText ("CONNECT USING IP " + server.getIp () + " (" + numPlayers + "/4 PLAYERS JOINED)");
			infoBox.changeText ("PRESS ENTER ONCE ALL PLAYERS HAVE JOINED TO START THE GAME");
		}
	}
	
	public void setupJoinMode () {
		ipBox.changeText ("ENTER THE CONNECTION IP: " + ip);
		infoBox.changeText ("(PRESS ENTER AFTER TYPING THE IP TO JOIN)");
		if (hintMessage != null) {
			hintMessage = new HintMessage ();
		}
	}
	
	public Point calculateCenteringPoint () {
		int resX = GameCode.getSettings ().getResolutionX ();
		int resY = GameCode.getSettings ().getResolutionY ();
		int centerX = 100;
		int centerY = 0;
		if (resX > 1280) {
			centerX = (resX - 1080) / 2;
			centerY = (resY - 720) / 2;
		}
		return new Point (centerX, centerY);
	}
	
	private void enterMainMenu () {
		
		
		titleBit = new TitleBit ();
		titleReg = new TitleRegister ();
		startGameSlot = new TitleSlot (TitleSlot.titleStartGame);
//		joinSlot = new TitleSlot (TitleSlot.titleJoin);
//		perksSlot = new TitleSlot (TitleSlot.titlePerks);
//		settingsSlot = new TitleSlot (TitleSlot.titleSettings);
//		helpSlot = new TitleSlot (TitleSlot.titleHelp);

		settingsBot = new SettingsTxt (150,200);
		controlMenu = new ControlsTxt(450,200);
		tutorial = new TutorialNPC (550, 400);
		
		settingsBot.setRenderPriority(101);
		controlMenu.setRenderPriority(101);
		tutorial.setRenderPriority(101);
	
		blastStation = new PerkStation (1);
		gripStation = new PerkStation (2);
		navStation = new PerkStation (3);
		dupeStation = new PerkStation (4);
		powerStation = new PerkStation (5);
		dualStation = new PerkStation (6);
		gamblerStation = new PerkStation (7);
		

		titleBit.declare (410 + OBJECT_SPAWN_RING_PADDING , 580);
		titleReg.declare (380 + OBJECT_SPAWN_RING_PADDING , 491);
		startGameSlot.declare (385 + OBJECT_SPAWN_RING_PADDING , 339);
//		joinSlot.declare (1150, 180);
//		helpSlot.declare (1150, 322);
//		perksSlot.declare (1150, 460);
//		settingsSlot.declare (1150, 600);
		settingsBot.declare(290 + OBJECT_SPAWN_RING_PADDING ,350);
		controlMenu.declare(470 + OBJECT_SPAWN_RING_PADDING ,350);
		tutorial.declare(550 + OBJECT_SPAWN_RING_PADDING, 400);
		

		blastStation.declare (90 + OBJECT_SPAWN_RING_PADDING , 416);
		gripStation.declare (146 + OBJECT_SPAWN_RING_PADDING , 314);
		navStation.declare (228 + OBJECT_SPAWN_RING_PADDING , 220);
		dupeStation.declare (390 + OBJECT_SPAWN_RING_PADDING , 168);
		powerStation.declare (558 + OBJECT_SPAWN_RING_PADDING , 225);
		dualStation.declare (636 + OBJECT_SPAWN_RING_PADDING , 314);
		gamblerStation.declare (700 + OBJECT_SPAWN_RING_PADDING , 416);
		
		walls.declare();
		
	}
	
	private void exitMainMenu () {
		
		titleBit.forget ();
		titleReg.forget ();
		startGameSlot.forget ();
//		joinSlot.forget ();
//		perksSlot.forget ();
//		settingsSlot.forget ();
//		helpSlot.forget ();
		settingsBot.forget();
		controlMenu.forget();
		tutorial.forget();
		
		walls.forget();
		/*hostButton.forget();
		joinButton.forget();
		rulesButton.forget();
		perksButton.forget();
		settingsButton.forget();*/
		

		blastStation.forget ();
		gripStation.forget ();
		navStation.forget ();
		dupeStation.forget ();
		powerStation.forget ();
		dualStation.forget ();
		gamblerStation.forget ();
		
	}
	
	private void initMainMenu () {
		
		enterMainMenu ();
		
		/*hostButton = new Button (new Sprite ("resources/sprites/host red.png"));
		joinButton = new Button (new Sprite ("resources/sprites/join.png"));
		rulesButton = new Button (new Sprite ("resources/sprites/story red.png"));
		perksButton = new Button (new Sprite ("resources/sprites/perks red.png"));
		settingsButton = new Button (new Sprite ("resources/sprites/setup red.png"));
		
		
		hostButton.setGreen(new Sprite ("resources/sprites/host.png"));
		joinButton.setGreen(new Sprite ("resources/sprites/join green.png"));
		rulesButton.setGreen(new Sprite ("resources/sprites/story green.png"));
		perksButton.setGreen(new Sprite ("resources/sprites/perks green.png"));
		settingsButton.setGreen(new Sprite ("resources/sprites/setup green.png"));
		
		
		hostButton.declare (700, 32);
		joinButton.declare (680, 167);
		rulesButton.declare(680, 322);
		perksButton.declare(720, 480);
		settingsButton.declare(720, 600);
		
		hostButton.setRenderPriority(69);
		joinButton.setRenderPriority(69);
		rulesButton.setRenderPriority(69);
		perksButton.setRenderPriority(69);
		settingsButton.setRenderPriority(69);*/
	
	}
	
	public static void closeTitleScreen () {
		titleClosed = true;
		if (hintMessage != null) {
			hintMessage.forget ();
		}
	}
	
	public static Scene playScene (String path, int x, int y) {
		perkScene = new Scene (path);
		return perkScene;
	}
	
	public static boolean scenePlaying () {
		return perkScene != null && perkScene.isPlaying ();
	}
	
	@Override
	public void draw () {
		//clearScreen ();
		if (!NetworkHandler.isServer()) {
			super.draw ();
		}
		if (this.getSprite() != null && this.getSprite() == lobbySprite) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					
					Textbox perk = null;
					
					ArrayList <String> parserQuantitys = new ArrayList<String> ();
					parserQuantitys.add("grid 168 128");
					
					Sprite bits = null;
			
					switch (GameCode.perks[(i*2) + j]) {
					case 0:
						perk = new Textbox ("BLAST PROCESSING");
						perk.setFont("text (blue)");
						bits = new Sprite ("resources/sprites/bits blue big.png", new SpriteParser (parserQuantitys));
						break;
					case 1:
						perk = new Textbox ("GRIP STRENTH");
						perk.setFont("text (lime green)");
						bits = new Sprite ("resources/sprites/bits green big.png", new SpriteParser (parserQuantitys));
						break;
					case 2:
						perk = new Textbox ("NAVIGATION BIT");
						perk.setFont("text (yellow)");
						bits = new Sprite ("resources/sprites/bits yellow big.png", new SpriteParser (parserQuantitys));
						break;
					case 3:
						perk = new Textbox ("POWERHOUSE");
						perk.setFont("text (red)");
						bits = new Sprite ("resources/sprites/bits red big.png", new SpriteParser (parserQuantitys));
						break;
					case 4:
						perk = new Textbox ("DUPLICATION BIT");
						perk.setFont("text (purple)");
						bits = new Sprite ("resources/sprites/bits purple big.png", new SpriteParser (parserQuantitys));
						break;
					case 5:
						perk = new Textbox ("DUEL CORE");
						perk.setFont("text (red)");
						bits = new Sprite ("resources/sprites/bits duel 1 big.png", new SpriteParser (parserQuantitys));
						break;
					case 15:
						perk = new Textbox ("NO PERK");
						bits = new Sprite ("resources/sprites/bits big.png", new SpriteParser (parserQuantitys));
						switch ((i*2) + j) {
						case 0:
							perk.setFont("text (lime green)");
							break;
						case 1:
							perk.setFont("text (red)");
							break;
						case 2: 
							perk.setFont("text (blue)");
							break;
						case 3: 
							perk.setFont("text (purple)");
							break;
						}
						break;
					}
					if (perk != null) {
						
						String str = perk.getText();
						
						int middleNum = str.length()/2;
						
						int middlePos = (middleNum * 16) + 8;
						
						int middlePosTarget = bits.getWidth()/2;
						
						int displace = middlePosTarget - middlePos;
						
						
						perk.changeBoxVisability();
						perk.setRenderPriority(72);
						perk.setX((i * 500) + 165 + displace);
						perk.setY((j * 300) + 75);
						perk.draw();
						//TODO FOR SOME REASON, INCREASING THE RESOLUTION CAUSES THESE TO NOT RENDER
						bits.draw((i * 500) + 165 - GameCode.getViewX (), (j * 300) + 130, (i*2) + j - GameCode.getViewY ());
					}
				}
			}
		}
	}
	
	public void enterHostMode () {
		
		isHost = true;
		
		NetworkHandler.setHost (true);
		if (NetworkHandler.isHostAPlayer()) {
			GameCode.setPerk(perkNum, 0);
		}
		
		this.setSprite(new Sprite ("resources/sprites/now loading.png"));
		
		RenderLoop.pause();
		
		if (!NetworkHandler.isServer ()) {
			this.draw();
			RenderLoop.wind.refresh();
		}
		enterIpMode ();
		
		//If there is a map to load
		if (mapLoadPath != null) {
			System.out.println(mapLoadPath);
			//Load the map
			File f = new File (mapLoadPath);
			try {
				Scanner s = new Scanner (f);
				//Load the room data
				String mapStr = "INITIAL";
				while (!mapStr.substring (0, 5).equals ("START")) {
					mapStr = s.nextLine ();
					//WARNING this is a big copy-paste, be sure to update in both places if applicable
					if (mapStr.length () >= 3 && mapStr.substring (0,4).equals ("NPC ")) {
						String[] args = mapStr.split (" ");
						if (args[1].equals ("CREATE")) {
							NPC npc = NPC.getInstance (args[2]);
							npc.declare ();
						} else if (args[1].equals ("UPDATE")) {
							String[] data = args[2].split (":");
							try {
								NPC npc = NPC.getNpcById (Integer.parseInt (data[1]));
								npc.updateNpc (args[2]);
							} catch (NullPointerException e) {
								System.out.println ("WARNING: NPC " + data[1] + " WAS NULL");
							}
						} else if (args[1].equals ("FORGET")) {
							NPC.getNpcById (Integer.parseInt (args[2])).forget ();
						}
					}
				}
				String roomsStr = mapStr.split (":")[1];
				Roome.loadMap (roomsStr);
				//Load the object data
				String dataStr = s.nextLine ();
				initialData = dataStr.split (":");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			//Arcade mode, generate a map
			Roome.generateMap();
		}
		
		RenderLoop.unPause();
		this.setSprite(lobbySprite);

	}
	
	public void enterJoinMode () {

		enterIpMode ();
		
	}
	
	public void enterIpMode () {
		
		//Setup the server if hosting
		if (isHost) {
			server = new Server ();
			server.start ();
			NetworkHandler.setServer (server);
		}
		
		ipMode = true;
		System.out.println (isHost);
		
	}
	
	public static void connectSuccess () {
		ipBox.changeText("CONNECTED: WAITING FOR HOST TO START GAME");
		TitleScreen screen = GameCode.getTitleScreen();
		screen.setSprite(lobbySprite);
		connected = true;
		
	}
	
	public static void playerJoin () {
		numPlayers++;
	}
	
	public void clearScreen () {
		Graphics g = RenderLoop.wind.getBufferGraphics ();
		g.setColor (Color.BLACK);
		g.fillRect (0, 0, GameCode.getSettings ().getResolutionX (), GameCode.getSettings ().getResolutionY ());
	}
	
	public static class Button extends GameObject {
		
		private boolean pressed = false;
		private Sprite green;
		private Sprite red;
		
	
		boolean mouseInside;
		
		public boolean hidden = false;
		
		public boolean isMouseInside() {
			return mouseInside;
		}
		public void setMouseInside(boolean mouseInside) {
			this.mouseInside = mouseInside;
		}
		public Button (Sprite sprite) {
			pressed = false;
			red = sprite;
			setSprite (sprite);
		}
		public void setGreen (Sprite green) {
			this.green = green;
		}
		@Override
		public void frameEvent () {
			if (this.isVisable()) {
				int mouseX = getCursorX () + GameCode.getViewX ();
				int mouseY = getCursorY () + GameCode.getViewY ();
				if (mouseX > getX () && mouseY > getY () && mouseX < getX () + getSprite ().getWidth () && mouseY < getY () + getSprite ().getHeight ()) {
					if (green != null) {
						this.setSprite(green);
					}
					mouseInside = true;
					if (this.mouseButtonReleased (0)) {
						pressed = true;
					}
				} else {
					mouseInside = false;
					this.setSprite(red);
				}
			}
		}
		public boolean isPressed () {
			return pressed;
		}
		
		public void reset () {
			pressed = false;
		}
		public boolean isHidden() {
			return hidden;
		}
		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}
		@Override
		public void setSprite (Sprite src) {
			super.setSprite(src);
			if (green == null) {
				red = src;
			} else if (!green.equals(src)) {
				red = src;
			}
		}
		
	}
	
	public static class TextButton extends Button {

		String text;
		
		public TextButton() {
			super(new Sprite ("resources/sprites/text button border.png"));
			
		}
		
		public void setText (String text) {
			this.text = text;
		}
		
		@Override
		public void draw () {
			if (this.isVisable()) {
				super.draw();
				Graphics g = RenderLoop.wind.getBufferGraphics();
				g.setColor(new Color (0x000000));
				g.drawString(text, (int)this.getX() + 1 - GameCode.getViewX(), (int)this.getY() + 14 - GameCode.getViewY());
			}
		}
		
		
	}
	
	public static class ArrowButtons extends GameObject {
		Button leftButton;
		Button rightButton;
		
		boolean leftSelectable = false;
		boolean rightSelectable = false;
		
		boolean toggled = false;
		
		String [] stringList;
		int selectedIndex = 0;
		
		public ArrowButtons (String [] strings) {
			stringList = strings;
			leftButton = new Button (new Sprite ("resources/sprites/left arrow green.png"));
			rightButton = new Button (new Sprite ("resources/sprites/right arrow green.png"));
		}
		
		public String getSelectedString () {
			return stringList[selectedIndex];
		}
		
		public boolean wasToggled () {
			boolean wasToggled = toggled;
			toggled = false;
			return wasToggled;
		}
		
		public void setIndex (int index) {
			selectedIndex = index;
		}
		public int getIndex () {
			return selectedIndex;
		}
		/**
		 * sets the selected index to the place where the value of index of is does nothing if index of is not in the list
		 */
		public void setTo (String indexOf) {
			for (int i = 0; i < stringList.length; i++) {
				if (stringList[i].equals(indexOf)) {
					selectedIndex = i;
					break;
				}
			}
		}
		@Override
		public void frameEvent () {
			if (selectedIndex == stringList.length -1) {
				rightButton.setSprite(new Sprite ("resources/sprites/right arrow red.png"));
				rightSelectable = false;
			} else {
				rightButton.setSprite(new Sprite ("resources/sprites/right arrow green.png"));
				rightSelectable = true;
			}
			if (selectedIndex == 0) {
				leftButton.setSprite(new Sprite ("resources/sprites/left arrow red.png"));
				leftSelectable = false;
			} else {
				leftButton.setSprite(new Sprite ("resources/sprites/left arrow green.png"));
				leftSelectable = true;
			}
			
			leftButton.frameEvent();
			rightButton.frameEvent();
			
			if (leftButton.isPressed() && leftSelectable) {
				selectedIndex = selectedIndex - 1;
				leftButton.reset();
				toggled = true;
			}
			if (rightButton.isPressed() && rightSelectable) {
				selectedIndex = selectedIndex + 1;
				rightButton.reset();
				toggled = true;
			}
		}
		@Override
		public void onDeclare () {
			leftButton.setX(this.getX());
			leftButton.setY(this.getY());
			rightButton.setY(this.getY());
		
			
			Graphics g = RenderLoop.wind.getBufferGraphics();
			FontMetrics fm = g.getFontMetrics();
			
			
			int width = leftButton.getSprite().getWidth() + 20;
			
			int cpy = width;
			
			for (int j = 0; j < stringList.length; j++) {
				int tempWidth = cpy;
				for (int i = 0; i < stringList[j].length(); i++) {
					tempWidth = tempWidth + fm.charWidth(stringList[j].charAt(i));
				}
				if (tempWidth > width) {
					width = tempWidth;
				}
			}
			
			
//			for (int i = 0; i < this.getSelectedString().length(); i++) {
//				width = width + fm.charWidth(this.getSelectedString().charAt(i));
//		}

			rightButton.setX(this.getX() + width);
			
			this.setHitboxAttributes(width + rightButton.getSprite().getWidth(), 32);
			
		}
		@Override
		public void draw () {
			this.onDeclare();
			
			if (this.isVisable()) {
				leftButton.draw();
				rightButton.draw();
				Graphics g = RenderLoop.wind.getBufferGraphics();
	
				g.setColor(new Color (0x000000));
				g.drawString(getSelectedString(), (int) (this.getDrawX() + leftButton.getSprite().getWidth()) + 10, (int)this.getDrawY() + 20);
				
			}
		}
		
	}
	public class SettingMenu extends GameObject {
		
		
		
		Button controllsButton = new Button (new Sprite ("resources/sprites/config button.png"));
		
		TitleScreen screen;
		
		Sprite resolutionsText = new Sprite ("resources/sprites/resolutions.png");
		
		boolean showResText = true;
		
		Button backButton;
		
		public SettingMenu (TitleScreen screen) {
//			this.screen = screen;
//			
//			this.setSprite(new Sprite ("resources/sprites/settings menu.png"));
//			
//			this.setRenderPriority(70);
//			
//			volume.setRenderPriority(71);
//			resolutions.setRenderPriority(71);
//			width.setRenderPriority(71);
//			height.setRenderPriority(71);
//			controllsButton.setRenderPriority(71);
//			displayMode.setRenderPriority(71);
//			
//			backButton = new Button (new Sprite ("resources/sprites/back.png"));
//			
//			
//			volume.declare(140,260);
//			resolutions.declare(180,175);
//			width.declare(140,460);
//			height.declare(140,520);
//			controllsButton.declare(150, 345);
//			backButton.declare(300, 512);
//			displayMode.declare(550, 165);
//			
//			
//			backButton.setRenderPriority(71);
			
			
		}
		
		@Override
		public void frameEvent () {
		
			
//			if (controllsButton.isPressed()) {
//				ControlMenu menu = new ControlMenu (screen);
//				
//				menu.declare();
//				
//				controllsButton.reset();
//				this.forgetStuff();
//				
//				
//			}
//		
//			if (backButton.isPressed()) {
//				screen.initMainMenu();
//				forgetStuff();
//			}
			
		}
		private void forgetStuff () {

//			volume.forget();
//			resolutions.forget();
//			controllsButton.forget();
//			width.forget();
//			height.forget();
//			displayMode.forget();
//			
//			
//			backButton.forget();
//			
//			this.forget();
		}
		
		@Override
		public void draw () {			
			super.draw();
			if (showResText) {
				resolutionsText.draw(15,160);
			}
		}
		
	}
//	public class ControlMenu extends GameObject {
//		
//		
//	
//		
//		Button backButton;
//		
//		TitleScreen screen;
//		
//		int selectedButton = -1;
//		
//		
//		public ControlMenu (TitleScreen screen) {
//		
//			this.screen = screen;
//			this.setSprite(new Sprite ("resources/sprites/controls menu.png"));
//			this.setRenderPriority(72);
//			
//	
//			
//		}
//		
////		@Override
////		public void onDeclare () {
////			
////			
////			
////			buttons[0].declare(120, 125);
////			buttons[1].declare(180, 170);
////			buttons[2].declare(140, 210);
////			buttons[3].declare(170, 260);
////			buttons[4].declare(160, 340);
////			buttons[5].declare(240, 385);
////			buttons[6].declare(400, 430);
////			buttons[7].declare(480, 520);
////			buttons[8].declare(690, 135);
////			buttons[9].declare(750, 180);
////			buttons[10].declare(710, 220);
////			buttons[11].declare(730, 265);
////			buttons[12].declare(940, 350);
////			
////			backButton = new Button (new Sprite ("resources/sprites/back.png"));
////			
////			backButton.declare(550, 452);
////			
////			backButton.setRenderPriority(73);
////			
////			
////		
////			
////		}
//		
//		
//		@Override
//		public void frameEvent () {
////			if (backButton.isPressed()) {
////				this.forgetStuff();
////				this.forget();
////				
////				SettingMenu menu = new SettingMenu (screen);
////				menu.declare();
////			}
//			
//			for (int i = 0; i < buttons.length; i++) {
//				if (buttons[i].isPressed()) {
//					if (i != selectedButton) {
//						if (selectedButton != -1) {
//							buttons[selectedButton].reset();
//						}
//						selectedButton = i;
//					} else {
//						if (getKeysDown().length != 0) {
//							int [] oldControls = GameCode.getSettings().getControls();
//							oldControls[i] = getKeysDown()[0];
//							
//							
//							
//							GameCode.getSettings().setControls(oldControls);
//							GameCode.getSettings().updateControlFile();
//							
//							buttons[i].setText(KeyEvent.getKeyText(GameCode.getSettings().getControls()[i]));
//							selectedButton = -1;
//						}
//					}
//				}
//			}
//			if (defaultButton.isPressed()) {
//				File oldControls = new File ("resources/saves/controls.txt");
//				oldControls.delete();
//				GameCode.initControls();
//				
//				
//				for (int i = 0; i < buttons.length; i++) {
//					buttons[i].setText(KeyEvent.getKeyText(GameCode.getSettings().getControls()[i]));
//				}
//			}
//		}
//		
//		
//		private void forgetStuff () {
//
//			for (int i = 0; i < buttons.length; i++) {
//				buttons[i].forget();
//			}
//			
//			backButton.forget();
//			
//			defaultButton.forget();
//			
//			this.forget();
//		}
//	}
//	public class perkMenu extends GameObject {
//		//Make the buttons
//			
//			Button blastButton;
//			Button haulerButton;
//			Button naviationButton;
//			Button duplicatieButton;
//			Button dualButton;
//			Button powerButton;
//			
//			Button backButton;
//			
//			TitleScreen screen;
//			
//			Sprite sideImage;
//			
//			Sprite check;
//			
//			@Override
//			public void onDeclare () {
//				blastButton = new Button (new Sprite ("resources/sprites/blast processsing  red.png"));
//				haulerButton = new Button (new Sprite ("resources/sprites/grip strength.png"));
//				naviationButton = new Button (new Sprite ("resources/sprites/navigation bit green.png"));
//				duplicatieButton = new Button (new Sprite ("resources/sprites/duplication red.png"));
//				powerButton = new Button (new Sprite ("resources/sprites/power house red.png"));
//				dualButton = new Button (new Sprite ("resources/sprites/duel core red.png"));
//				
//				backButton = new Button (new Sprite ("resources/sprites/back.png"));
//				
//				sideImage = new Sprite ("resources/sprites/blast processsing explanation.png");
//				
//				blastButton.declare (100, 32);
//				haulerButton.declare (470, 32);
//				naviationButton.declare(100, 212);
//				duplicatieButton.declare(470, 212);
//				powerButton.declare(100, 412);
//				dualButton.declare(470, 412);
//				
//				backButton.declare(300, 512);
//				
//				check = new Sprite ("resources/sprites/check.png");
//				
//				blastButton.setRenderPriority(71);
//				haulerButton.setRenderPriority(71);
//				naviationButton.setRenderPriority(71);
//				duplicatieButton.setRenderPriority(71);
//				powerButton.setRenderPriority(71);
//				dualButton.setRenderPriority(71);
//				
//				backButton.setRenderPriority(71);
//	}
//			
//				@Override
//				public void draw () {
//					super.draw();
//					if (sideImage != null) {
//						sideImage.draw(790,0);
//					}
//						switch (perkNum) {
//							case 0:
//								check.draw(170, 62);
//								break;
//							
//							case 1:
//								check.draw(540, 62);
//								break;
//							
//							case 2:
//								check.draw(190, 242);
//								break;
//								
//							case 3:
//								check.draw(190, 442);
//								break;
//								
//							case 4:
//								check.draw(540, 242);
//								break;
//							
//							case 5:
//								check.draw(540, 442);
//								break;
//	
//						}
//				}
//				public perkMenu (TitleScreen screen) {
//					this.screen = screen;
//					this.setSprite (new Sprite ("resources/sprites/perk Menu.png"));
//					this.setRenderPriority(70);
//				}
//				
//				@Override
//				public void frameEvent () {
//					if (blastButton.isPressed ()) {	
//						blastButton.pressed = false;
//						perkNum = 0;
//					}
//					
//					if (haulerButton.isPressed ()) {
//						haulerButton.pressed = false;
//						perkNum = 1;
//					}
//					if (naviationButton.isPressed ()) {
//						naviationButton.pressed = false;
//						perkNum = 2;
//					}
//					
//					if (duplicatieButton.isPressed ()) {
//						duplicatieButton.pressed = false;
//						perkNum = 4;
//					}
//					if (powerButton.isPressed ()) {
//						powerButton.pressed = false;
//						perkNum = 3;
//						
//					}
//					if (dualButton.isPressed ()) {
//						dualButton.pressed = false;
//						perkNum = 5;
//					}
//					
//					if (backButton.isPressed()) {
//						screen.initMainMenu();
//						//screen.perksButton.pressed = false;
//						forgetStuff();
//					}
//					
//					if (blastButton.mouseInside) {
//						sideImage = new Sprite ("resources/sprites/blast processsing explanation.png");
//					}
//					if (haulerButton.mouseInside) {
//						sideImage = new Sprite ("resources/sprites/grip strength explination.png");
//					}
//					if (naviationButton.mouseInside) {
//						sideImage = new Sprite ("resources/sprites/navigation bit explanation.png");
//					}
//					if (duplicatieButton.mouseInside) {
//						sideImage = new Sprite ("resources/sprites/duplication explination.png");
//					}
//					if (powerButton.mouseInside) {
//						sideImage = new Sprite ("resources/sprites/powerhouse explination.png");
//					}
//					if (dualButton.mouseInside) {
//						sideImage = new Sprite ("resources/sprites/duel core explination.png");
//					}
//					
//				}	
//				private void forgetStuff () {
//
//					blastButton.forget();
//					haulerButton.forget();
//					naviationButton.forget();
//					duplicatieButton.forget();
//					powerButton.forget();
//					dualButton.forget();
//					
//					backButton.forget();
//					
//					this.forget();
//				}
//		}
	
	public class GameMenu extends GameObject {
		
		private Button gmHostButton;
		private Button gmJoinButton;
		
		public Sprite joinDescSprite = new Sprite ("resources/sprites/join_description.png");
		public Sprite hostDescSprite = new Sprite ("resources/sprites/host_description.png");
		
		@Override
		public void onDeclare () {
			
			//Host button coords: 146 257
			//Join button coords: 697 252
			//Host desc coords: 58 355
			//Join desc corods: 586 354
			
			//Render this above the title screen
			this.setSprite (new Sprite ("resources/sprites/game_menu.png"));
			this.setRenderPriority (70);
			
			//Initialize the buttons
			gmHostButton = new Button (new Sprite ("resources/sprites/host red.png"));
			gmJoinButton = new Button (new Sprite ("resources/sprites/join.png"));
			gmHostButton.declare (146, 257);
			gmJoinButton.declare (697, 252);
			gmHostButton.setGreen (new Sprite ("resources/sprites/host.png"));
			gmJoinButton.setGreen (new Sprite ("resources/sprites/join green.png"));
			gmHostButton.setRenderPriority (71);
			gmJoinButton.setRenderPriority (71);
			
		}
		
		@Override
		public void frameEvent () {
			if (gmHostButton.isPressed ()) {
				forgetStuff ();
				enterHostMode ();
				gmHostButton.reset ();
			}
			if (this.gmJoinButton.isPressed ()) {
				forgetStuff ();
				enterJoinMode ();
				gmJoinButton.reset ();
			}
		}
		
		@Override
		public void draw () {
			
			super.draw ();
			
			if (gmHostButton != null && gmHostButton.isMouseInside ()) {
				hostDescSprite.draw (58 - GameCode.getViewX (), 355 - GameCode.getViewY ());
			}
			if (gmJoinButton != null && gmJoinButton.isMouseInside ()) {
				joinDescSprite.draw (586 - GameCode.getViewX (), 354 - GameCode.getViewY ());
			}
			
		}
		
		public void forgetStuff () { 
			gmHostButton.forget ();
			gmJoinButton.forget ();
			forget ();
		}
		
	}
	
		public static int getNumberOfPlayers () {
			return numPlayers;
		}
}

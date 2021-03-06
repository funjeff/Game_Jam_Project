package items;

import engine.GameCode;
import engine.Sprite;
import gameObjects.DataSlot;
import gameObjects.Register;
import map.Roome;
import network.NetworkHandler;
import npcs.Basketball;
import players.Bit;
import resources.SoundPlayer;
import util.DummyCollider;

public class DataScrambler extends Item {
	
	public DataScrambler () {
		this.setSprite(new Sprite ("resources/sprites/Data scrambler.png"));
		this.setHitboxAttributes(32, 32);
	}

	/**
	 * returns true if item use was succsefull false otherwise
	 */
	@Override
	public boolean useItem (Bit user) {
		/*if (user.regestersBeingCarried == null) {
			return false;
		} else {
			if (NetworkHandler.isHost()) {
				SoundPlayer play = new SoundPlayer ();
				play.playSoundEffect(GameCode.volume,"resources/sounds/effects/scrambler.wav");
			} else {
				NetworkHandler.getServer().sendMessage("SOUND:"  + user.playerNum + ":resources/sounds/effects/scrambler.wav");
			}
			Register reg = (Register)user.regestersBeingCarried.get(0);
			reg.scramble();
			return true;
		}*/
		DummyCollider dc = new DummyCollider ((int)user.getX () - Bit.HIGHLIGHT_RADIUS, (int)user.getY () - Bit.HIGHLIGHT_RADIUS, Bit.HIGHLIGHT_RADIUS * 2, Bit.HIGHLIGHT_RADIUS * 2);
		if (dc.isColliding ("DataSlot")) {
			DataSlot ds = (DataSlot)dc.getCollisionInfo ().getCollidingObjects ().get (0);
			ds.scramble ();
			return true;
		}
		return false;
	}
	
	public String getName () {
		return "Data Scrambler";
	}
	
	public String getDesc () {
		return "Use this item on a data slot\n"
				+ "to scramble it. Scrambled\n"
				+ "data slots will not deduct\n"
				+ "lives at the end of a wave.";
	}
	
	public String getLongDescription () {
		return "default_long_desc";
	}
	
	public String getItemFlavor () {
		return "should we be doing this?";
	}
}

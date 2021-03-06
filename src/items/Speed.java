package items;

import java.util.ArrayList;

import engine.GameCode;
import engine.GameObject;
import engine.ObjectHandler;
import engine.Sprite;
import network.NetworkHandler;
import players.Bit;
import resources.SoundPlayer;

public class Speed extends Item {
	
	public Speed () {
		this.setSprite(new Sprite ("resources/sprites/speed.png"));	
		this.setHitboxAttributes(32, 32);
	}
	/**
	 * returns true if item use was succsefull false otherwise
	 */
	@Override
	public boolean useItem (Bit user) {
		if (NetworkHandler.isHost()) {
			SoundPlayer play = new SoundPlayer ();
			play.playSoundEffect(GameCode.volume,"resources/sounds/effects/speed.wav");
		} else {
			NetworkHandler.getServer().sendMessage("SOUND:"  + user.playerNum + ":resources/sounds/effects/speed.wav");
		}
		user.speedUpTemporarly();
		return true;
	}
	
	public String getName () {
		return "Speed Boost";
	}
	
	public String getDesc () {
		return "Using this item will allow\n"
				+ "you to move faster temporarily.";
	}
	
	public String getLongDescription () {
		return "default_long_desc";
	}
	
	public String getItemFlavor () {
		return "contains 20 ccs of milk";
	}
}

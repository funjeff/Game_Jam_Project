package items;

import java.util.ArrayList;

import engine.GameCode;
import engine.GameObject;
import engine.ObjectHandler;
import engine.Sprite;
import network.NetworkHandler;
import players.Bit;
import resources.SoundPlayer;

public class Egg extends Item {
	
	public Egg () {
		this.setSprite(new Sprite ("resources/sprites/egg.png"));	
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
		user.powerUpTemporarly();
		return true;
	}
}

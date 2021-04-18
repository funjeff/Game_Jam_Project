package items;

import engine.Sprite;
import gameObjects.Register;
import players.Bit;

public class Glue extends Item {
	
	public Glue () {
		this.setSprite(new Sprite ("resources/sprites/glue kinda.png"));
		this.setHitboxAttributes(32, 32);
	}
	/**
	 * returns true if item use was succsefull false otherwise
	 */
	@Override
	public boolean useItem (Bit user) {
		if (user.regestersBeingCarried == null) {
			return false;
		} else {
			if (user.regestersBeingCarried.size() < 2) {
				return false;
			} else {
				Register reg1 = (Register) user.regestersBeingCarried.get(0);
				Register reg2 = (Register) user.regestersBeingCarried.get(1);
				reg1.combine(reg2);
				return true;
			}
		}
	}
	
	
}
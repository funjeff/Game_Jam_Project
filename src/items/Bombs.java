package items;

import java.awt.Rectangle;

import engine.GameCode;
import engine.Sprite;
import map.Roome;
import network.NetworkHandler;
import players.Bit;
import resources.SoundPlayer;

public class Bombs extends Item {

	boolean thrown = false;
	
	Bit user;
	
	int direction = 0;
	public Bombs () {
		this.setSprite(new Sprite ("resources/sprites/bombs.png"));
		this.setHitboxAttributes(32, 32);
	}
	
	/**
	 * returns true if item use was succsefull false otherwise
	 */
	@Override
	public boolean useItem (Bit user) {
		this.declare((int)user.getX(),(int)user.getY());
		thrown = true;
		this.user = user;
		direction = user.lastMove;
		pickupability = false;
		return true;
	}
	
	//made so you can throw a bomb in the tutorial
	public void useItemAlternate (int direction,int x, int y) {
		this.declare(x, y);
		thrown = true;
		this.direction = direction;
		pickupability = false;
	}
	
	
	@Override
	public void frameEvent () {
		if (thrown) {
			switch (direction) {
			case 0:
				if (!this.goY(this.getY() - 6)) {
					this.setY(this.getY() - 6);
					Rectangle rect9 = new Rectangle ((int)(432 + Roome.getRoom(this.getX(),this.getY()).getX()), (int)(Roome.getRoom(this.getX(), this.getY()).getY()),216,144);
					if (this.hitbox().intersects(rect9)) {
						Roome.getRoom(this.getX(), this.getY()).destroyTopWall();
					}
					this.forget();
					if (NetworkHandler.isHost()) {
						SoundPlayer play = new SoundPlayer ();
						play.playSoundEffect(GameCode.volume,"resources/sounds/effects/bomb.wav");
					} else {
						NetworkHandler.getServer().sendMessage("SOUND:"  + user.playerNum + ":resources/sounds/effects/bomb.wav");
					}
				}
				break;
			case 1:
				if (!this.goY(this.getY() + 6)) {
					this.setY(this.getY() + 6);
					Rectangle rect9 = new Rectangle ((int)(432 + Roome.getRoom(this.getX(),this.getY()).getX()), (int)(576 + Roome.getRoom(this.getX(), this.getY()).getY()),216,144);
					if (this.hitbox().intersects(rect9)) {
						Roome.getRoom(this.getX(), this.getY()).destroyBottomWall();
					}
					this.forget();
					if (NetworkHandler.isHost()) {
						SoundPlayer play = new SoundPlayer ();
						play.playSoundEffect(GameCode.volume,"resources/sounds/effects/bomb.wav");
					} else {
						NetworkHandler.getServer().sendMessage("SOUND:"  + user.playerNum + ":resources/sounds/effects/bomb.wav");
					}
				}
				break;
			case 2:
				if (!this.goX(this.getX() + 6)) {
					this.setX(this.getX() + 6);
					Rectangle rect9 = new Rectangle ((int)(864 + Roome.getRoom(this.getX(),this.getY()).getX()), (int)(252 + Roome.getRoom(this.getX(), this.getY()).getY()),216,144);
					if (this.hitbox().intersects(rect9)) {
						Roome.getRoom(this.getX(), this.getY()).destroyRightWall();
					}
					this.forget();
					if (NetworkHandler.isHost()) {
						SoundPlayer play = new SoundPlayer ();
						play.playSoundEffect(GameCode.volume,"resources/sounds/effects/bomb.wav");
					} else {
						NetworkHandler.getServer().sendMessage("SOUND:"  + user.playerNum + ":resources/sounds/effects/bomb.wav");
					}
				}
				break;
			case 3:
				if (!this.goX(this.getX() - 6)) {
					this.setX(this.getX() - 6);
					Rectangle rect9 = new Rectangle ((int)(Roome.getRoom(this.getX(),this.getY()).getX()), (int)(252 + Roome.getRoom(this.getX(), this.getY()).getY()),216,144);
					if (this.hitbox().intersects(rect9)) {
						Roome.getRoom(this.getX(), this.getY()).destroyLeftWall();
					}
					this.forget();
					if (NetworkHandler.isHost()) {
						SoundPlayer play = new SoundPlayer ();
						play.playSoundEffect(GameCode.volume,"resources/sounds/effects/bomb.wav");
					} else {
						NetworkHandler.getServer().sendMessage("SOUND:"  + user.playerNum + ":resources/sounds/effects/bomb.wav");
					}
				}
				break;
			}
		} else {
			super.frameEvent ();
		}
	}
	
	public String getName () {
		return "Bomb";
	}
	
	public String getDesc () {
		return "Approach a wall and\nthrow the bomb to destroy it.\nDoes not work on the\nedges of the map.";
	}
	
	public String getLongDescription () {
		return "default_long_desc";
	}
	
	public String getItemFlavor () {
		//You know back in the day they used to call me the bomb master
		return "edible until proven otherwise";
	}
	
}

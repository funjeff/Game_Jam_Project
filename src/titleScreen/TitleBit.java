package titleScreen;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import engine.GameCode;
import engine.GameObject;
import engine.Sprite;
import gameObjects.Register;
import resources.SoundPlayer;

public class TitleBit extends GameObject {

	public static final int HINT_TIME = 200;
	
	Sprite sprite = new Sprite ("resources/sprites/config/bits.txt");
	
	private MoveHint hint;
	
	private TitleRegister carried = null;
	
	private int stillTime = 0;
	private boolean moved = false;

	boolean frozen = false;
	
	public TitleBit () {
		setSprite (sprite);
		getAnimationHandler ().setAnimationFrame ((int)(Math.random () * 4));
		setRenderPriority (100);
		setHitboxAttributes (21, 16);
	}
	
	@Override
	public void frameEvent () {
		
		//Make the move hint if needed
		if (!moved) {
			stillTime++;
		} else {
			stillTime = Integer.MIN_VALUE;
		}
		if (stillTime > HINT_TIME && hint == null) {
			hint = new MoveHint ();
			hint.declare ((int)getX () - 44, (int)getY () - 92);
		}
		if (hint != null && moved) {
			hint.forget ();
			hint = null;
			stillTime = Integer.MIN_VALUE;
		}
		moved = false;
		
		//Assert the default speed
		double speed = 5;
		
		//Check for the start carrying condition
		this.setX (getX () - speed);
		this.setY (getY () - speed);
		setHitboxAttributes (21 + speed * 2, 16 + speed * 2);
		if (carried == null && this.isColliding ("TitleRegister")) {
			if (keyDown (KeyEvent.VK_SHIFT)) {
				carried = (TitleRegister)this.getCollisionInfo ().getCollidingObjects ().get (0);
				SoundPlayer play = new SoundPlayer ();
				play.playSoundEffect(GameCode.volume,"resources/sounds/effects/pickup.wav");
			}
		}
		this.setX (getX () + speed);
		this.setY (getY () + speed);
		setHitboxAttributes (21, 16);
		
		//Stop carrying if shift is not held
		if (!keyDown (KeyEvent.VK_SHIFT)) {
			carried = null;
		}
		
		//Slow down the player if they're carrying the TitleRegister
		if (carried != null) {
			speed /= 2;
		}
		
		//Handle movement
		double startX = getX ();
		double startY = getY ();
		if (!frozen) {
			if (keyDown (GameCode.getSettings().getControls()[0])) {
				setY (getY () - speed);
			}
			if (keyDown (GameCode.getSettings().getControls()[2])) {
				setX (getX () - speed);
			}
			if (keyDown (GameCode.getSettings().getControls()[1])) {
				setY (getY () + speed);
			}
			if (keyDown (GameCode.getSettings().getControls()[3])) {
				setX (getX () + speed);
			}
		}
		//Handle the pushing and pulling of the TitleRegister
		double diffX = getX () - startX;
		double diffY = getY () - startY;
		if (diffX != 0 || diffY != 0) {
			moved = true;
		}
		if (carried != null) {
			if (!carried.goX (carried.getX () + diffX)) {
				setX (startX);
			}
			if (!carried.goY (carried.getY () + diffY)) {
				setY (startY);
			}
		} else {
			double finalX = startX + diffX;
			double finalY = startY + diffY;
			setX (startX + diffX);
			setY (startY);
			if (this.isColliding ("TitleRegister")) {
				TitleRegister r = (TitleRegister)this.getCollisionInfo ().getCollidingObjects ().get (0);
				if (!r.goX (r.getX () + diffX)) {
					finalX = startX;
				}
			}
			setX (startX);
			setY (startY + diffY);
			if (this.isColliding ("TitleRegister")) {
				TitleRegister r = (TitleRegister)this.getCollisionInfo ().getCollidingObjects ().get (0);
				if (!r.goY (r.getY () + diffY)) {
					finalY = startY;
				}
			}
			setX (finalX);
			setY (finalY);
		}
		
		//Check for OOB
		Rectangle r = new Rectangle (0, 0, GameCode.getSettings ().getResolutionX (), GameCode.getSettings ().getResolutionY ());
		if (!r.contains (hitbox ())) {
			setX (startX);
			setY (startY);
		}
		
	}
	public void freeze () {
		frozen = true;
	}
	public void unfreeze () {
		frozen = false;
	}
}

package engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import gameObjects.Catalogable;
import gameObjects.DataSlot;
import gameObjects.Register;
import items.Bombs;
import map.Roome;
import npcs.Basketball;
import npcs.CarKey;
import npcs.Dirt;
import npcs.Popcorn;
import npcs.Shovel;
import npcs.Trash;
import players.Bit;

/**
 * Represents an in-game object that can be interacted with in some way
 * @author nathan
 *
 */
public abstract class GameObject extends GameAPI implements Catalogable {
	
	/**
	 * x-coordinate of this GameObject
	 */
	private double x;
	/**
	 * y-coordinate of this GameObject
	 */
	private double y;
	/**
	 * Previous x-coordinate of this GameObject
	 */
	private double xprevious;
	/**
	 * Previous y-coordinate of this GameObject
	 */
	private double yprevious;
	/**
	 * The width of this GameObject's hitbox
	 */
	private double hitboxWidth;
	/**
	 * The height of this GameObject's hitbox
	 */
	private double hitboxHeight;
	/**
	 * The AnimationHandler object used to render this GameObject
	 */
	private AnimationHandler animationHandler = new AnimationHandler (null);
	/**
	 * The variant of this GameObject
	 */
	private Variant variant;
	/**
	 * The CollisionInfo object generated by the most recent collision check done by this GameObject
	 */
	private CollisionInfo lastCollision;
	
	/**
	 * lol yes = pixel collisions my guy
	 */
	private boolean pixelCollisions = false;
	
	private boolean declared = false;
	
	int gamelogicPriority = 0;
	
	int renderPriority = 0; 
	
	public int id;
	
	static int lastID;
	
	private boolean visable = true;
	
	private boolean hitboxBorders = false;
	
	private static HashMap<Class<?>, Dimension> hitboxDimensions;
	
	/**
	 * Container and utility class for GameObject variants
	 * @author nathan
	 *
	 */
	public class Variant {
		
		/**
		 * Contains the list of attributes for this variant
		 */
		private LinkedList<Attribute> attributes;
		
		/**
		 * Container and utility class for mapping variant names to values
		 * @author nathan
		 *
		 */
		private class Attribute {
			
			private String name;
			
			private String value;
			
			public Attribute (String name, String value) {
				this.name = name;
				this.value = value;
			}
			
			public String getName () {
				return name;
			}
			
			public boolean isNamed (String name) {
				return this.name.equals (name);
			}
			
			public String getValue () {
				return value;
			}
			
			public void setValue (String value) {
				this.value = value;
			}
			
			@Override
			public String toString () {
				return name + ":" + value;
			}
		}
		
		/**
		 * No-arg constructor which constructs an empty variant
		 */
		public Variant () {
			attributes = new LinkedList<Attribute> ();
		}
		
		/**
		 * Constructs a new variant using the given data string.
		 * @param attributeData A variant data string formatted according to the specification of the setAttributes method
		 */
		public Variant (String attributeData) {
			this ();
			setAttributes (attributeData);
		}
		
		/**
		 * Sets the attribute with the given name to the given value.
		 * @param name The name of the attribute to assign
		 * @param value The new value of the attribute
		 */
		public void setAttribute (String name, String value) {
			Attribute workingAttribute = null;
			Iterator<Attribute> iter = attributes.iterator ();
			while (iter.hasNext ()) {
				Attribute working = iter.next ();
				if (working.isNamed (name)) {
					workingAttribute = working;
					break;
				}
			}
			if (workingAttribute == null) {
				attributes.add (new Attribute (name, value));
			} else {
				workingAttribute.setValue (value);
			}
		}
		
		/**
		 * Sets the attributes to the values indicated in the data string attributeData
		 * @param attributeData TODO
		 */
		public void setAttributes (String attributeData) {
			String[] variants = attributeData.split ("&");
			for (int i = 0; i < variants.length; i ++) {
				String[] attribute = variants [i].split (":");
				if (attribute.length == 2) {
					setVariantAttribute (attribute [0], attribute [1]);
				}
			}
		}
		
		/**
		 * Gets the value of the attribute with the given name.
		 * @param name The name of the attribute
		 * @return The value mapped to the given name
		 */
		public String getAttribute (String name) {
			Iterator<Attribute> iter = attributes.iterator ();
			while (iter.hasNext ()) {
				Attribute working = iter.next ();
				if (working.isNamed (name)) {
					return working.getValue ();
				}
			}
			return null;
		}
		
		@Override
		public String toString () {
			String data = "";
			Iterator<Attribute> iter = attributes.iterator ();
			while (iter.hasNext ()) {
				data += iter.next ().toString ();
				if (iter.hasNext ()) {
					data += "&";
				}
			}
			return data;
		}
	}
	
	/**
	 * No-argument constructor for ease of inheretence
	 */
	public GameObject () {
		Dimension d = getHitboxDimensions (this.getClass());
		if (d != null) {
			this.setHitboxAttributes (d.getWidth (), d.getHeight ());
		}
	}
	
	/**
	 * Constructs a new GameObject at the given x and y coordinates.
	 * @param x The x-coordinate to use
	 * @param y The y-coordinate to use
	 */
	public GameObject (double x, double y) {
		this.x = x;
		this.y = y;
		xprevious = x;
		yprevious = y;
	}
	
	/**
	 * Inserts this object into the static instance of ObjectHandler, effectively scheduling it for calls to frameEvent and draw, in addition to allowing collision detection with it.
	 */
	public void declare () {
		this.declare((int)getX (),(int)getY ());
	}
	public void declare (int x, int y) {
		ObjectHandler.insert (this);
		declared = true;
		this.x = x;
		this.y = y;
		id = lastID;
		lastID = lastID + 1;
		this.onDeclare();
	}
	
	public void onDeclare() {
		
	}
	/**
	 * Whether or not this GameObject is currently declared.
	 * @return true if declared; false otherwise
	 */
	public boolean declared () {
		return declared;
	}
	/**
	 * Removes this object from the static instance of ObjectHandler.
	 */
	public void forget () {
		declared = false;
		ObjectHandler.remove (this);
	}
	
	public static void initHitboxDimensions () {
		
		//Make the hitbox dimensions
		hitboxDimensions = new HashMap<Class<?>, Dimension> ();
		
		//Populate the hitbox dimensions
		hitboxDimensions.put (Dirt.class, new Dimension (30, 31));
		hitboxDimensions.put (Basketball.class, new Dimension (48, 48));
		hitboxDimensions.put (Shovel.class, new Dimension (22, 48));
		hitboxDimensions.put (DataSlot.class, new Dimension (84, 90));
		hitboxDimensions.put (Register.class, new Dimension (98, 42));
		hitboxDimensions.put (Popcorn.class, new Dimension (16, 16));
		hitboxDimensions.put (Trash.class, new Dimension (64, 64));
		hitboxDimensions.put (CarKey.class, new Dimension (16, 36));
		hitboxDimensions.put(Bombs.class, new Dimension(16,16));
		
	}
	
	public int getGamelogicPriority() {
		return gamelogicPriority;
	}

	public void setGamelogicPriority(int gamelogicPriority) {
		this.gamelogicPriority = gamelogicPriority;
	}

	public int getRenderPriority() {
		return renderPriority;
	}

	public void setRenderPriority(int renderPriority) {
		this.renderPriority = renderPriority;
	}
	
	public void adjustHitboxBorders () {
		hitboxBorders = !hitboxBorders;
	}
	
	/**
	 * turns on pixel collisions
	 */
	protected void enablePixelCollisions () {
		pixelCollisions = true;
	}
	/**
	 * turns off pixel collisions
	 */
	protected void disablePixelCollisions () {
		pixelCollisions = false;
	}

	/**
	 * Draws this GameObject at its x and y coordinates relative to the room view.
	 */
	public void draw () {
		//TODO wtf?
		if (this.getSprite() != null) {
			Rectangle thisRect = new Rectangle ((int)this.getX(), (int)this.getY(), this.getSprite().getFrame(0).getWidth(), this.getSprite().getFrame(0).getHeight());
			
			Rectangle veiwport = new Rectangle ((int) GameCode.getViewX(), (int) GameCode.getViewY(), GameCode.getSettings ().getResolutionX (), GameCode.getSettings ().getResolutionY ());
		
			
			if (thisRect.intersects(veiwport) && visable) {	
				animationHandler.draw (x - GameCode.getViewX(), y - GameCode.getViewY());	
				
			}
		}
		if (this.hitboxBorders) {
			Graphics g = RenderLoop.wind.getBufferGraphics();
			
			g.drawRect(this.hitbox().x - GameCode.getViewX(), this.hitbox().y - GameCode.getViewY(), this.hitbox().width,this.hitbox().height);
		}
	}
	
	/**
	 */
	public void drawAbsolute () {
		if (visable) {
			animationHandler.draw (x, y);
		}
	}
	public void frameEvent () {
		
	}
	
	public void pausedEvent () {
		
	}
	
	/**
	 * Runs a collision check between this GameObject and another GameObject. Does not generate a CollisionInfo object.
	 * @param obj The object to check for collision with
	 * @return True if the objects collide; false otherwise
	 */
	public boolean isColliding (GameObject obj) {
		Rectangle thisHitbox = hitbox ();
		Rectangle objHitbox = obj.hitbox ();
		
		if (thisHitbox == null || objHitbox == null) {
			return false;
		}
				if (thisHitbox.intersects (objHitbox)) {
					boolean pixelCollisionsEnabled = obj.pixelCollisions;
					if ((!pixelCollisionsEnabled && !pixelCollisions)) {
						return true;
					} else if (!pixelCollisionsEnabled && pixelCollisions) {
						if (this.runPixelCollsions(this, objHitbox)) {
							return true;
						}
					} else if (pixelCollisionsEnabled && !pixelCollisions) {
						if (this.runPixelCollsions(obj, thisHitbox)) {
							return true;
						}
					} else {
						return this.runMultipulePixelCollsions(obj, this,objHitbox,thisHitbox);
					}
				}
		return false;
	}
	public boolean isColliding (Rectangle hitbox) {
		Rectangle thisHitbox = hitbox ();
		Rectangle objHitbox = hitbox;
		if (thisHitbox == null || objHitbox == null) {
			return false;
		}
			if (thisHitbox.intersects (objHitbox)) {
				if (this.pixelCollisions) {
					return this.runPixelCollsions(this, hitbox);
				}
				return true;
			}
		return false;
	}
	
	/**
	 * Checks for collision with all GameObjects of the given type.
	 * @param objectType The type of GameObject to check for, as given by calling getClass.getSimpleName() on the object
	 * @return True if a collision was detected; false otherwise
	 */
	public boolean isColliding (String objectType) {
		lastCollision = ObjectHandler.checkCollision (objectType, this);
		return lastCollision.collisionOccured ();
	}
	
	/**
	 * Checks for collision with all GameObjects that are children of the given type.
	 * @param parentType The type of the parent GameObject, as given by calling getClass.getSimpleName() on the object
	 * @return True if a collision was detected; false otherwise
	 */
	public boolean isCollidingChildren (String parentType) {
		lastCollision = ObjectHandler.checkCollisionChildren (parentType, this);
		return lastCollision.collisionOccured ();
	}
	private boolean runPixelCollsions (GameObject pixelObject, Rectangle hitboxObject) {
		Raster mask;
		mask = pixelObject.getAnimationHandler().getImage().getFrame(pixelObject.getAnimationHandler().getFrame()).getAlphaRaster();
		
		int [] sample = new int [1];
		Rectangle working = pixelObject.hitbox().intersection(hitboxObject);
		int startPosX = (int) (working.x - pixelObject.hitbox().getX());
		int startPosY =(int) (working.y - pixelObject.hitbox().getY());
		if (startPosX < 0) {
			startPosX = startPosX*-1;
		}
		if (startPosY < 0) {
			startPosY = startPosY*-1;
		}
		for (int wy = 0; wy < working.height; wy++) {
			for (int wx = 0; wx < working.width; wx++){
				try {
				mask.getPixel (startPosX + wx, startPosY + wy,sample);	
				if (sample[0] != 0) {
					return true;
				}
				} catch (IndexOutOfBoundsException e) {
				}
			}
		}
		return false;
	}
	private boolean runMultipulePixelCollsions (GameObject pixelObject, GameObject hitboxObject, Rectangle pixelHitbox, Rectangle hitboxHitbox) {
		Raster mask1;
		Raster mask3;
		mask1 = pixelObject.getAnimationHandler().getImage().getFrame(pixelObject.getAnimationHandler().getFrame()).getAlphaRaster();
		mask3 = hitboxObject.getAnimationHandler().getImage().getFrame(hitboxObject.getAnimationHandler().getFrame()).getAlphaRaster();
		int [] sample = new int [1];
		Rectangle working = pixelHitbox.intersection(hitboxHitbox);
		int startPosX = (int) (pixelHitbox.getX() - working.x);
		int startPosY =(int) (pixelHitbox.getY() - working.y);
		int startPos_1 = (int)(hitboxHitbox.getX() - working.x);
		int startPosbee = (int)(hitboxHitbox.getY() - working.y);
		if (startPosX < 0) {
			startPosX = startPosX*-1;
		}
		if (startPosY < 0) {
			startPosY = startPosY*-1;
		}
		if (startPos_1 < 0) {
			startPos_1 = startPos_1*-1;
		}
		if (startPosbee < 0) {
			startPosbee = startPosbee*-1;
		}
		for (int wy = 0; wy < working.height; wy++) {
			for (int wx = 0; wx < working.width; wx++){
				try {
				mask1.getPixel (wx + startPosX,wy + startPosY,sample);
				if (sample[0] != 0) {
					mask3.getPixel (wx + startPos_1,wy + startPosbee,sample);
						if (sample[0] != 0) {
							return true;
					}
				}
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets the CollisionInfo object generated by the last collision check performed by this object.
	 * @return The most recently generated CollisionInfo object
	 */
	public CollisionInfo getCollisionInfo () {
		return lastCollision;
	}
	
	/**
	 * Gets the x component of this GameObject's position.
	 * @return The x coordinate of this GameObject
	 */
	public double getX () {
		return x;
	}
	
	/**
	 * Gets the y component of this GameObject's position.
	 * @return The y coordinate of this GameObject
	 */
	public double getY () {
		return y;
	}
	
	/**
	 * Gets the x coordinate this GameObject would be drawn at on the screen, accounting for scrolling
	 * @return the x coordinate this GameObject will be drawn at
	 */
	public int getDrawX () {
		return (int)(getX () - GameCode.getViewX ());
	}
	
	/**
	 * Gets the x coordinate this GameObject would be drawn at on the screen, accounting for scrolling
	 * @return the y coordinate this GameObject will be drawn at
	 */
	public int getDrawY () {
		return (int)(getY () - GameCode.getViewY ());
	}
	
	/**
	 * Gets the x-coordinate of the center of this GameObject, based on its position and hitbox attributes
	 * @return the x-coordinate of this GameObject's center point
	 */
	public double getCenterX () {
		return hitbox ().getX () + hitbox ().getWidth () / 2;
	}
	
	/**
	 * Gets the y-coordinate of the center of this GameObject, based on its position and hitbox attributes
	 * @return the y-coordinate of this GameObject's center point
	 */
	public double getCenterY () {
		return hitbox ().getY () + hitbox ().getHeight () / 2;
	}
	
	/**
	 * Gets the distance between the center point of this GameObject and that of the specified GameObject obj
	 * @param obj the object to calculate the distance from
	 * @return the distance between the two objects
	 */
	public double getDistance (GameObject obj) {
		double diffX = getCenterX () - obj.getCenterX ();
		double diffY = getCenterY () - obj.getCenterY ();
		return Math.sqrt (diffX * diffX + diffY * diffY);
	}
	
	/**
	 * Gets the x component of this GameObject's previous position.
	 * @return The x coordinate of this GameObject
	 */
	public double getXPrevious () {
		return x;
	}
	
	/**
	 * Gets the y component of this GameObject's previous position.
	 * @return The y coordinate of this GameObject
	 */
	public double getYPrevious () {
		return y;
	}
	
	/**
	 * Gets the sprite used to render this GameObject
	 * @return The sprite associated with this GameObject
	 */
	public Sprite getSprite () {
		return animationHandler.getImage ();
	}
	
	/**
	 * Gets the value associated with the given variant name from this GameObject's variant.
	 * @param attributeName The name of the attribute
	 * @return The value of the attribute; null if not found
	 */
	public String getVariantAttribute (String attributeName) {
		if (variant != null) {
			return variant.getAttribute (attributeName);
		}
		return null;
	}
	
	/**
	 * Returns this GameObject's hitbox. Constructs a new Rectangle object each call.
	 * @return A Rectangle object representing this GameObject's hitbox
	 */
	public Rectangle hitbox () {
		if (hitboxWidth == 0 || hitboxHeight == 0) {
			return null;
		}
		return new Rectangle ((int)x, (int)y, (int)hitboxWidth, (int)hitboxHeight);
	}
	public void setHitboxAttributes(double hitboxWidth, double hitboxHeight) {
		this.hitboxWidth = hitboxWidth;
		this.hitboxHeight = hitboxHeight;
	}
	public static Dimension getHitboxDimensions (Class<?> c) {
		
		if (hitboxDimensions == null) {
			initHitboxDimensions ();
		}
		
		if (!hitboxDimensions.containsKey (c)) {
			return new Dimension (32, 32);
		} else {
			return hitboxDimensions.get (c);
		}
		
	}
	/**
	 * Gets the variant object representing this GameObject's variant.
	 * @return This GameObject's variant
	 */
	public Variant variant () {
		return variant;
	}
	
	/**
	 * Gets the AnimationHandler object associated with this GameObject.
	 * @return This GameObject's AnimationHandler
	 */
	public AnimationHandler getAnimationHandler () {
		return animationHandler;
	}
	
	/**
	 * Updates the x component of this GameObject's position.
	 * @param val The new value to use
	 */
	public void setX (double val) {
		x = val;
	}
	/**
	 * trys to move to a new x pos but doesen't if it would hit a wall
	 * @param val the new x pos to go too
	 * @return true if the move was sucessfull false otherwise 
	 */
	public boolean goX(double val) {
		double x = this.getX();
		Roome currentRoom = Roome.getRoom(this.getX(), this.getY());
		this.setX(val);
		try {
			if (currentRoom.isColliding(this)) {
				this.setX(x);
				return false;
			}
		} catch (NullPointerException e) {
			
		}
		return true;
	}
	
	
	
	/**
	 * Updates the y component of this GameObject's position.
	 * @param val The new value to use
	 */
	public void setY (double val) {
		y = val;
	}
	
	/**
	 * trys to move to a new y pos but doesen't if it would hit a wall
	 * @param val the new y pos to go too
	 * @return true if the move was sucessfull false otherwise 
	 */
	public boolean goY(double val) {
		double y = this.getY();
		this.setY(val);
		Roome currentRoom = Roome.getRoom(this.getX(), this.getY());
		try {
			if (currentRoom.isColliding(this)) {
				this.setY(y);
				return false;
			}
		} catch (NullPointerException e) {
			
		}
		return true;
	}
	
	
	
	/**
	 * Sets the sprite of this GameObject to the given sprite.
	 * @param sprite The sprite to use
	 */
	public void setSprite (Sprite sprite) {
		animationHandler.resetImage (sprite);
	}
	
	/**
	 * Sets the given variant attribute to the specified value.
	 * @param name The name of the attribute
	 * @param value The new value to assign
	 */
	public void setVariantAttribute (String name, String value) {
		if (variant == null) {
			variant = new Variant ();
		}
		variant.setAttribute (name, value);
	}
	
	/**
	 * Sets the variants specified in attributeData to the respective values.
	 * @param attributeData The attributes as a formatted String, as specified by the setAttributes method in the class GameObject.Variant
	 */
	public void setVariantAttributes (String attributeData) {
		if (variant == null) {
			variant = new Variant (attributeData);
		} else {
			variant.setAttributes (attributeData);
		}
	}
	
	public int getId () {
		return id;
	}

	public boolean isVisable() {
		return visable;
	}

	public void setVisability(boolean visable) {
	
		this.visable = visable;
	}
	
	public String getName () {
		return "default_name";
	}
	
	public String getDesc () {
		return "default_short_desc";
	}
	
	public String getLongDescription () {
		return "default_long_desc";
	}
	
	public String getItemFlavor () {
		return "default_flavor";
	}
}

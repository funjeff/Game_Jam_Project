package engine;

import java.util.ArrayList;

public class CollisionInfo {
	
	/**
	 * Whether a collision occured for this given object
	 */
	private boolean collisionOccured;
	/**
	 * A list of the objects involved in this collision
	 */
	private ArrayList<GameObject> collisions;
	
	/**
	 * Constructs a new CollisionInfo object with the given GameObjects.
	 * @param collisions A Linked List of objects for which collisions were detected
	 */
	public CollisionInfo (ArrayList<GameObject> collisions) {
		this.collisions = collisions;
		if (!this.collisions.isEmpty()) {
			this.collisionOccured = true;
		}
	}
	
	/**
	 * Constructs a new CollisionInfo object storing only whether a collision occured or not.
	 * @param collisionOccured Whether a collision occured
	 */
	public CollisionInfo (boolean collisionOccured) {
		this.collisionOccured = collisionOccured;
		this.collisions = null;
	}
	
	/**
	 * Returns true if this object records that a collision was detected; false otherwise.
	 * @return Whether a collision occured
	 */
	public boolean collisionOccured () {
		return collisionOccured;
	}
	
	/**
	 * Gets the list of objects involved in the collision.
	 * @return A ArrayList of the relevant objects.
	 */
	public ArrayList<GameObject> getCollidingObjects () {
		return collisions;
	}
}

package com.mina86.snake;



/**
 * Exception thrown by {@link Snake#step(byte)} method when snake steps on
 * a bomb, a wall or itself.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
 */
public final class SnakeDied extends Exception {
	/** Point wher snake tried to move. */
	protected Point p;
	/** State of map cell. */
	protected byte s;


	/**
	 * Creates new exception with given point and map cell's state.
	 *
	 * @param _p cell snake tried to move to.
	 * @param _s state of cell at given position.
	 */
	SnakeDied(Point _p, byte _s) {
		p = _p;
		s = _s;
	}


	/**
	 * Returns position where snake tried to move.
	 *
	 * @return position where snake tried to move.
	 */
	public final Point head() { return p; }
	/**
	 * Returns state of cell where snake tried to move.
	 *
	 * @return state of map cell.
	 */
	public final byte state() { return s; }



}

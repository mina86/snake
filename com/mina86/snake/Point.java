/*
 * Snake, the game.
 * Copyright (c) Michal Nazarewicz (mina86/AT/mina86.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mina86.snake;



/**
 * Class representing read-only x, y coordinates.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class Point {
	/** The x coordinate. */
	private short x;
	/** The y coordinate. */
	private short y;


	/**
	 * Creates new Point with given coordinates.
	 *
	 * @param _x x coordinate.
	 * @param _y y coordinate.
	 */
	public Point(short _x, short _y) {
		x = _x;
		y = _y;
	}

	/**
	 * Creates new Point with given coordinates.
	 *
	 * @param _x x coordinate.
	 * @param _y y coordinate.
	 */
	public Point(int _x, int _y) {
		x = (short)_x;
		y = (short)_y;
	}

	/**
	 * Creates new Point with given coordinates wrapped in given
	 * rectangle.
	 *
	 * Wraping along given axis works according to the following
	 * formula: <tt>exist k such that p' = p + k*d and p' is in [0,
	 * d)</tt> where p is coordinate on given axis and d dimension.
	 * In particular, if moving up makes you stap aoutside of the
	 * rectangle you get put on the bottom, etc.
	 *
	 * @param _x x coordinate.
	 * @param _y y coordinate.
	 * @param width width of rectangle to wrap pointo into.
	 * @param height height of rectangle to wrap pointo into.
	 */
	public Point(short _x, short _y, short width, short height) {
		_x %= width;
		_y %= height;
		x = (short)(_x<0 ? _x + width  : _x);
		y = (short)(_y<0 ? _y + height : _y);
	}


	/**
	 * Returns x coordinate.
	 *
	 * @return x coordinate.
	 */
	public short getX() { return x; }

	/**
	 * Returns x coordinate.
	 *
	 * @return x coordinate.
	 */
	public short getY() { return y; }


	/**
	 * Wraps point around.
	 *
	 * @param width width of space to wrap point into.
	 * @param height height of space to wrap point into.
	 * @return new cycled point.
	 * @see #Point(short, short, short, short)
	 */
	public Point wrap(short width, short height) {
		return new Point(x, y, width, height);
	}


	/**
	 * Modifies coordinates by given differences.
	 *
	 * @param dx difference along x axis.
	 * @param dy difference along y axis.
	 * @return new Point with modified coordinates.
	 */
	public Point transform(short dx, short dy) {
		return new Point((short)(x + dx), (short)(y + dy));
	}

	/**
	 * Modifies coordinates by given differences and then wraps it
	 * around.
	 *
	 * @param dx difference along x axis.
	 * @param dy difference along y axis.
	 * @param width width of space to wrap point into.
	 * @param height height of space to wrap point into.
	 * @return new Point with modified coordinates.
	 */
	public Point transform(short dx, short dy, short width, short height) {
		return new Point((short)(x + dx), (short)(y + dy), width, height);
	}

	/**
	 * Moves point in given direction and then wraps it around.
	 * Differences along x and y axis are obtained using
	 * Direction.dx() and Direction.dy() methods.
	 *
	 * @param dir direction to move.
	 * @param width width of space to wrap point into.
	 * @param height height of space to wrap point into.
	 * @return new Point with modified coordinates.
	 */
	public Point transform(byte dir, short width, short height) {
		return new Point((short)(x + Direction.dx(dir)),
		                 (short)(y + Direction.dy(dir)), width, height);
	}

	/**
	 * Moves point in given direction.  Differences along x and y axis
	 * are obtained using Direction.dx() and Direction.dy() methods.
	 *
	 * @param dir direction to move.
	 * @return new Point with modified coordinates.
	 */
	public Point transform(byte dir) {
		return new Point((short)(x + Direction.dx(dir)),
		                 (short)(y + Direction.dy(dir)));
	}


	public String toString() {
		return "(" + x + ", " + y + ")";
	}


	public boolean equals(Object obj) {
		return obj!=null && getClass() == obj.getClass() &&
			x == ((Point)obj).x && y == ((Point)obj).y;
	}


	public int hashCode() {
		int key = x | (y << 16);
		/* http://www.concentric.net/~Ttwang/tech/inthash.htm */
		key  = ~key + (key << 15);  /* key = (key << 15) - key - 1; */
		key ^= key >>> 12;
		key += key <<   2;
		key ^= key >>>  4;
		key *=       2057;          /* key += (key << 3) + (key << 11); */
		key ^= key >>> 16;
		return key;
	}
}

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
 * A static class containing Direction and Shape enumerations as well
 * as methods used to calculate direction or shapes from given points.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class Direction {
	private Direction() { };


	/** The lowest number used in directions enumeration. */
	public final static byte DIR_MIN = 0;
	/** The biggest number used in directions enumeration. */
	public final static byte DIR_MAX = 3;
	/** Value representing direction up. */
	public final static byte UP      = 0;
	/** Value representing direction Down. */
	public final static byte DOWN    = 1;
	/** Value representing direction Left. */
	public final static byte LEFT    = 2;
	/** Value representing direction Right. */
	public final static byte RIGHT   = 3;


	/**
	 * Array holding x difference for a movement in direction being
	 * array's index.
	 */
	private static short dxs[] = {  0,  0, -1,  1 };

	/**
	 * Array holding y difference for a movement in direction being
	 * array's index.
	 */
	private static short dys[] = { -1,  1,  0,  0 };


	/**
	 * Returns direction which is oposite to the given one.
	 *
	 * @param dir a direction.
	 * @return direction oposite to dir.
	 */
	public static byte oposite(byte dir) { return (byte)(dir ^ 1); }

	/**
	 * Returns true if d1 is oposite direction to d2, false otherwise.
	 *
	 * @param d1 first direction.
	 * @param d2 second direction.
	 * @return true if d1 is oposite direction to d2, false otherwise.
	 */
	public static boolean areOposites(byte d1, byte d2) { return (d1^d2) == 1; }


	/**
	 * Returns difference along x axis for a movement in a given direction.
	 *
	 * @param dir movement's direction.
	 * @return direction's x difference.
	 * @see #dy(byte)
	 */
	public static short dx(byte dir) { return dxs[dir]; }

	/**
	 * Returns difference along y axis for a movement in a given direction.
	 *
	 * @param dir movement's direction.
	 * @return direction's y difference.
	 * @see #dx(byte)
	 */
	public static short dy(byte dir) { return dys[dir]; }


	/**
	 * Calculates direction from given two points.  Returns a
	 * direction one has to move from point p1 to get to point p2.  If
	 * points are not on one line result is unspecified.  If absolute
	 * value of difference between coordinates of the points on a
	 * given axis is greater then 1, method treats it as if the
	 * movement caused tho wrap around the edge of map.
	 *
	 * @param p1 point to move from.
	 * @param p2 point to move to.
	 * @return direction from point p1 to point p2.
	 */
	public static byte calculate(Point p1, Point p2) {
		int diff = p2.getX() - p1.getX();
		byte ret = UP;
		if (diff<0) {
			ret = LEFT;
		} else if (diff>0) {
			ret = RIGHT;
		} else if ((diff = p2.getY() - p1.getY())<0) {
			ret = UP;
		} else {
			ret = DOWN;
		}
		return (diff * diff > 1) ? (byte)(ret ^ 1) : ret;
	}


	/**
	 * Returns random direction.
	 *
	 * @return random direction.
	 */
	public static byte random() {
		return (byte)(Math.random() * 4);
	}



	/** The lowest number used in shapes enumeration. */
	public static final byte SHAPE_MIN  = 0;
	/** The biggest number used in shapes enumeration. */
	public static final byte SHAPE_MAX  = 5;
	/** Value representing vertical shape. */
	public static final byte TOP_DOWN   = 0;
	/** Value representing horizontal shape. */
	public static final byte LEFT_RIGHT = 1;
	/** Value representing top-left shape. */
	public static final byte TOP_LEFT   = 2;
	/** Value representing top-right shape. */
	public static final byte TOP_RIGHT  = 3;
	/** Value representing down-left shape. */
	public static final byte DOWN_LEFT  = 4;
	/** Value representing down-right shape. */
	public static final byte DOWN_RIGHT = 5;


	/**
	 * Array holding shapes according to two directions.
	 */
	private final static byte shapes[][] = {
		{ TOP_DOWN,   TOP_DOWN,   DOWN_LEFT,  DOWN_RIGHT },
		{ TOP_DOWN,   TOP_DOWN,   TOP_LEFT,   TOP_RIGHT  },
		{ TOP_RIGHT,  DOWN_RIGHT, LEFT_RIGHT, LEFT_RIGHT },
		{ TOP_LEFT,   DOWN_LEFT,  LEFT_RIGHT, LEFT_RIGHT }
	};


	/**
	 * Calculates shape based on two directions.  See description of
	 * calculate(Point, Point, Point) for info.
	 *
	 * @param d1 direction from 1st pointo to 2nd point.
	 * @param d2 direction from 2nd pointo to 3rd point.
	 * @return shape defined by given given directions.
	 */
	public static byte calculate(byte d1, byte d2) {
		return shapes[d1][d2];
	}

	/**
	 * Calculates shape based on three points.  Shape of item at
	 * second point is defined by all neighbours that is point
	 * proceeding given point (the first point) and succesing the
	 * point (the third point).
	 *
	 * @param p1 shape's 1st.
	 * @param p2 shape's 2nd.
	 * @param p3 shape's 3rd.
	 * @return shape defined by given given points.
	 */
	public static byte calculate(Point p1, Point p2, Point p3) {
		return shapes[calculate(p1, p2)][calculate(p2, p3)];
	}
}

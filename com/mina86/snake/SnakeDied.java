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
 * Exception thrown by {@link Snake#step(byte)} method when snake steps on
 * a bomb, a wall or itself.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
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

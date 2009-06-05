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
 * Exception thrown by a SkinLoader or MapLoader when either
 * input/output exception occures or there is an error in
 * configuration file (such as missing key or invalid value).
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class LoaderException extends Exception {
	/** Creats new exception with null message and no cause. */
	public LoaderException() { }

	/**
	 * Creates new exception with given message and no cause.
	 *
	 * @param message exception message.
	 */
	public LoaderException(String message) {
		super(message);
	}

	/**
	 * Creates new exception with given message and cause.
	 *
	 * @param message exception message.
	 * @param cause what caused the exception.
	 */
	public LoaderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates new exception with null message and given cause.
	 *
	 * @param cause what caused the exception.
	 */
	public LoaderException(Throwable cause) {
		super(cause);
	}


}

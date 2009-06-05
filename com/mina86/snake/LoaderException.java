package com.mina86.snake;



/**
 * Exception thrown by a SkinLoader or MapLoader when either
 * input/output exception occures or there is an error in
 * configuration file (such as missing key or invalid value).
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
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

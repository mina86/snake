package com.mina86.snake;


/**
 * Exception thrown by {@link Map} class when user tried to put an item, a
 * snake or a wall on cell which is already occupied.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
 */
public final class CellOccupiedException extends RuntimeException {
	/** Position of the cell. */
	private Point point;


	/**
	 * Creates instance of exception with given point and null message.
	 *
	 * @param p cell's coordinates.
	 */
	public CellOccupiedException(Point p) {
		super("Cell " + p + " is already occupied");
		point = p;
	}

	/**
	 * Creates instance of exception with given point and message.
	 *
	 * @param p cell's coordinates.
	 * @param msg exception message.
	 */
	public CellOccupiedException(Point p, String msg) {
		super(msg);
		point = p;
	}



	/**
	 * Returns x coordinate of the cell.
	 *
	 * @return cell's x coordinate.
	 * @see #getY()
	 * @see #getPoint()
	 */
	public short getX() { return point.getX(); }

	/**
	 * Returns y coordinate of the cell.
	 *
	 * @return cell's y coordinate.
	 * @see #getX()
	 * @see #getPoint()
	 */
	public short getY() { return point.getY(); }


	/**
	 * Returns coordinates of the cell.
	 *
	 * @return cell's coordinates.
	 * @see #getX()
	 * @see #getY()
	 */
	public Point getPoint() { return point; }



}

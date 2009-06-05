package com.mina86.snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;



/**
 * Abstract class representing skin.  Contains methods which allow
 * drawing images on graphics buffer and doing animations.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
 */
public abstract class Skin {
	/** Single skin's icon width. */
	protected short width;
	/** Single skin's icon height. */
	protected short height;
	/** Background color defined by skin. */
	protected Color backgroundColor;



	/**
	 * Returns skin's icon width.
	 *
	 * @return skin's icon width.
	 */
	public final short getWidth () { return width; }

	/**
	 * Returns skin's icon height.
	 *
	 * @return skin's icon height.
	 */
	public final short getHeight() { return height; }


	/**
	 * Sets basic skin's values.
	 *
	 * @param w skin's icon width.
	 * @param h skin's icon height.
	 * @param bgColor background color.
	 * @throws IllegalArgumentException if w or h is lower then 1.
	 */
	protected Skin(short w, short h, Color bgColor) {
		if (w<1 || h<1) {
			throw new IllegalArgumentException();
		}
		width = w;
		height = h;
		backgroundColor = bgColor;
	}


	/**
	 * Draws given skin icon at given coordinates on given Graphics2D.
	 *
	 * Skin icon is defined by two integers.  To get apropriate
	 * identificator one uses one of imgHead(), imgTail(), imgBody(),
	 * imgOnlyHead(), imgFree(), imgWall() and imgItem() functions.
	 *
	 * @param gr Graphics2D to draw on.
	 * @param x x coordinate where to draw icon.
	 * @param y y coordinate where to draw icon.
	 * @param r first identificator of skin's icon.
	 * @param c second identificator of skin's icon.
	 */
	public abstract void draw(Graphics2D gr, short x, short y, short r, short c);

	/**
	 * Draws given skin icon at given coordinates on given Graphics2D.
	 *
	 * @param gr Graphics2D to draw on.
	 * @param x x coordinate where to draw icon.
	 * @param y y coordinate where to draw icon.
	 * @param img skin's icon identificator
	 * @see #draw(Graphics2D, short, short, short, short)
	 */
	public final void draw(Graphics2D gr, short x, short y, Point img) {
		draw(gr, x, y, img.getX(), img.getY());
	}

	/**
	 * Draws given skin icon at given coordinates on given Graphics2D.
	 *
	 * @param gr Graphics2D to draw on.
	 * @param point x, y coordinates where to draw icon.
	 * @param img skin's icon identificator
	 * @see #draw(Graphics2D, short, short, short, short)
	 */
	public final void draw(Graphics2D gr, Point point, Point img) {
		draw(gr, point.getX(), point.getY(), img.getX(), img.getY());
	}

	/**
	 * Draws given skin icon at given coordinates on given Graphics2D.
	 *
	 * @param gr Graphics2D to draw on.
	 * @param point x, y coordinates where to draw icon.
	 * @param r first identificator of skin's icon.
	 * @param c second identificator of skin's icon.
	 * @see #draw(Graphics2D, short, short, short, short)
	 */
	public final void draw(Graphics2D gr, Point point, short r, short c) {
		draw(gr, point.getX(), point.getY(), r, c);
	}


	/**
	 * Clears cell at given position.
	 *
	 * @param gr Graphics2D to draw on.
	 * @param x x coordinate of cell to clear.
	 * @param y y coordinate of cell to clear.
	 */
	public final void clear(Graphics2D gr, short x, short y) {
		draw(gr, x, y, (short)1, (short)7);
	}

	/**
	 * Clears cell at given position.
	 *
	 * @param gr Graphics2D to draw on.
	 * @param point x, y coordinates of cell to clear.
	 */
	public final void clear(Graphics2D gr, Point point) {
		clear(gr, point.getX(), point.getY());
	}


	/**
	 * Fills specified area in Graphics with background color.
	 *
	 * @param gr Graphics to draw on.
	 * @param x x coordinate of upper left corner of rectangle.
	 * @param y y coordinate of upper left corner of rectangle.
	 * @param w width of rectangle.
	 * @param h height of rectangle.
	 */
	public void clearGraphics(Graphics gr, int x, int y, int w, int h) {
		if (backgroundColor!=null) {
			gr.setColor(backgroundColor);
			gr.fillRect(x, y, w, h);
		}
	}

	/**
	 * Fills specified area in Graphics with background color.  Area
	 * to clear is a rectangle with upper left corner in point (0, 0)
	 * and given dimensions.
	 *
	 * @param gr Graphics to draw on.
	 * @param w width of rectangle.
	 * @param h height of rectangle.
	 */
	public final void clearGraphics(Graphics gr, int w, int h) {
		clearGraphics(gr, 0, 0, w, h);
	}

	/**
	 * Fills specified area in Graphics with background color.  Area
	 * to clear is a rectangle with upper left corner in point (0, 0)
	 * and given dimensions.
	 *
	 * @param gr Graphics to draw on.
	 * @param dim rectangle dimensions.
	 */
	public final void clearGraphics(Graphics gr, Dimension dim) {
		clearGraphics(gr, 0, 0, (int)dim.getWidth(), (int)dim.getHeight());
	}



	/**
	 * Returns skin's icon identificator of snake's head facing given
	 * direction.
	 *
	 * @param dir direction snake is facing.
	 * @return skin's icon identificator of snake's head facing given
	 *         direction.
	 */
	public  static Point imgHead(byte dir) { return new Point(0, dir  ); }

	/**
	 * Returns skin's icon identificator of snake's tail facing given
	 * direction.
	 *
	 * @param dir direction snake is facing.
	 * @return skin's icon identificator of snake's tail facing given
	 *         direction.
	 */
	public  static Point imgTail(byte dir) { return new Point(0, dir+4); }

	/**
	 * Returns skin's icon identificator of snake's body element in
	 * given shape.
	 *
	 * @param shp snake's body shape.
	 * @return skin's icon identificator of snake's body element in
	 *         given shape.
	 */
	public  static Point imgBody(byte shp) { return new Point(1, shp  ); }

	/**
	 * Returns skin's icon identificator of snake's head when it has
	 * no body (ie. at the very beginning of game).
	 *
	 * @return skin's icon identificator of snake's head when it has
	 *         no body.
	 */
	public  static Point imgOnlyHead()     { return new Point(1, 6); }

	/**
	 * Returns skin's icon identificator of free cell.  It can be used
	 * to clear cell on buffer however clear(Graphics, int, int) or
	 * clear(Graphics, Point) are preffered.
	 *
	 * @return skin's icon identificator of free cell.
	 */
	private static Point imgFree()         { return new Point(1, 7); }

	/**
	 * Returns skin's icon identificator of a wall with given
	 * definition of neighbouring walls.
	 *
	 * @param ngb definition of walls around given wall.
	 * @return skin's icon identificator of a wall.
	 */
	public  static Point imgWall(byte ngb) {
		return new Point(2 + (ngb>>3), ngb & 7);
	}

	/**
	 * Returns skin's icon identificator of an item.
	 *
	 * @param id icon main identificator.
	 * @param frame frame number.
	 * @return skin's icon identificator of an item.
	 */
	public  static Point imgItem(byte id, byte frame) {
		return new Point(4 + id, frame);
	}




	/**
	 * Returns number of food item icons available in skin.
	 *
	 * @return number of food item icons available in skin.
	 */
	public abstract byte countFoodTypes();

	/**
	 * Returns number of bomb icons available in skin.
	 *
	 * @return number of bomb icons available in skin.
	 */
	public abstract byte countBombTypes();

	/**
	 * Returns number of frames in given icon.
	 *
	 * @param id icon main identificator.
	 * @return number of frames in given icon.
	 */
	public abstract byte countFrames(byte id);

	/**
	 * Returns random food item main icon identificator.
	 *
	 * @return random food item main icon identificator.
	 */
	public abstract byte randomFood();

	/**
	 * Returns random bomb main icon identificator.
	 *
	 * @return random bomb main icon identificator.
	 */
	public abstract byte randomBomb();
}

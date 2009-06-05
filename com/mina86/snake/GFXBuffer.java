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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;



/**
 * A graphical buffer for holding rendered map.  This class extends
 * BufferedImage so it can by used in Graphics' drawImage() method.
 * The way cells are cleard and images are painted depends on Skin
 * object associated with buffer.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class GFXBuffer extends BufferedImage {
	/** Skin associated with graphical buffer. */
	private Skin  skin;


	/**
	 * Creates buffer with given skin, number of columns and number of
	 * rows.  Accual width and height of the buffer is number of
	 * columns multiplied by skin's cell width and number of rows
	 * multiplied by skin's cell height.
	 *
	 * @param skin skin used to paint on buffer.
	 * @param cols number of columns.
	 * @param rows number of rows.
	 */
	public GFXBuffer(Skin skin, short cols, short rows) {
		super((int)skin.getWidth() * cols, (int)skin.getHeight() * rows,
		      TYPE_INT_ARGB);
		if (cols<5 || rows<5) {
			throw new IllegalArgumentException("Invalid number of cols or rows");
		}
		this.skin = skin;
	}


	/**
	 * Returns Skin associated with buffer.
	 *
	 * @return Skin associated with buffer.
	 * @see #setSkin(Skin)
	 */
	public Skin  getSkin() { return skin; }

	/**
	 * Sets Skin associated with buffer.  It's caller's responsibility
	 * to guarantee that size of skin's cell will match size of map.
	 *
	 * @param skin Skin to associated with buffer.
	 * @see #getSkin()
	 */
	public void  setSkin(Skin skin) { this.skin = skin; }


	/**
	 * An interface of items which can be drown on the buffer.
	 *
	 * @version 0.1
	 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
	 */
	public static interface Drawable {
		/**
		 * Draws item on given buffer.
		 *
		 * @param buf buffer to draw on.
		 * @see #redraw(GFXBuffer)
		 */
		public void   draw(GFXBuffer buf);

		/**
		 * Reraws item on given buffer.  For instance, this method can
		 * first clear area used by the element and then call draw()
		 * method.
		 *
		 * @param buf buffer to draw on.
		 * @see #draw(GFXBuffer)
		 */
		public void redraw(GFXBuffer buf);
	}



	/**
	 * Draws skin's image donated by img at position donated by point.
	 *
	 * @param point coordinates of the cell to draw on.
	 * @param img skin's image identification.
	 * @see #redraw(Point, Point)
	 * @see Skin#draw(Graphics2D, Point, Point)
	 */
	public void   draw(Point point, Point img) {
		skin.draw(createGraphics(), point, img);
	}

	/**
	 * Redraw skin's image donated by img at position donated by
	 * point.  This method first clears given cell and then performs
	 * the same action draw() method would perform.
	 *
	 * @param point coordinates of the cell to draw on.
	 * @param img skin's image identification.
	 * @see #draw(Point, Point)
	 * @see #clear(Point)
	 * @see Skin#draw(Graphics2D, Point, Point)
	 */
	public void redraw(Point point, Point img) {
		Graphics2D gr = createGraphics();
		skin.clear(gr, point);
		skin.draw(gr, point, img);
	}

	/**
	 * Clears given cell.
	 *
	 * @param point coordinates of the cell to clear.
	 * @see #draw(Point, Point)
	 * @see #redraw(Point, Point)
	 * @see Skin#clear(Graphics2D, Point)
	 */
	public void clear (Point point)   {
		skin.clear(createGraphics(), point);
	}
}

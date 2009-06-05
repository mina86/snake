package com.mina86.snake;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import javax.swing.JComponent;



/**
 * Class representing map on which snake's is moving.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
 */
public final class Map {
	/** Value donating map cell is free. */
	public static final byte FREE = 0;
	/** Value donating map cell contains snake's body. */
	public static final byte BODY = 1;
	/** Value donating map cell contains food. */
	public static final byte FOOD = 2;
	/** Value donating map cell contains bomb. */
	public static final byte BOMB = 3;
	/** Value donating map cell contains wall. */
	public static final byte WALL = 4;


	/**
	 * Class representing item which can be put on map.  That is
	 * either food or bomb.  It implements GFXBuffer.Drawable
	 * interface so it can be direcccctly painted on GFXBuffer using
	 * it's draw() method.
	 *
	 * @version 0.1
	 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
	 */
	public final static class Item implements GFXBuffer.Drawable {
		/** Position on map item is located. */
		private Point point;
		/** Item's duration (ie. how long will it be on map). */
		private int duration;
		/** Image identification available from Skin used to paint
		 * this item. */
		private byte imgId;
		/** Frame number that is currently painted. */
		private byte frame = 0;
		/** Either Map.FOOD or Map.BOMB. */
		private byte state;


		/**
		 * Creates new item object at given position, with given
		 * duration and image id.
		 *
		 * @param point position on map item is located at.
		 * @param state either Map.FOOD donating this is food item or
		 *        Map.BOMB dnating this is a bomb.
		 * @param duration number of moves this item will stay on map
		 *        or negative number donating it will stay on map
		 *        forever.
		 * @param id image identificator used to paint this item.
		 * @see Skin#randomFood()
		 * @see Skin#randomBomb()
		 */
		public Item(Point point, byte state, int duration, byte id) {
			if (state!=FOOD && state!=BOMB) {
				throw new IllegalArgumentException();
			}
			this.point = point;
			this.state = state;
			this.duration = duration;
			this.imgId = id;
		}

		/**
		 * Returns position on map item is located.
		 *
		 * @return position on map item is located.
		 */
		public Point getPoint() { return point; }
		/**
		 * Returns Map.FOOD if it's food item or Map.BOMB if it's a
		 * bomb.
		 *
		 * @return Map.FOOD if it's food item or Map.BOMB if it's a
		 *         bomb.
		 */
		public byte getMapState() { return state; }
		/**
		 * Returns true if it's a food item.
		 *
		 * @return true if it's a food item.
		 */
		public boolean isFood() { return state == FOOD; }
		/**
		 * Returns true if it's a bomb.
		 *
		 * @return true if it's a bomb.
		 */
		public boolean isBomb() { return state == BOMB; }

		/**
		 * Decrements duration and returns true if item's duration has
		 * reached zero.  If item's duration is zero when method is
		 * called it does not decrement it and returns true.  If
		 * item's duration is less then zero it does not decrement it
		 * and returns false (that is, a negative duration donates
		 * that item never disappear).
		 *
		 * @return true if item should disappear.
		 */
		public final boolean getsOlder() {
			if (duration<0) return false;
			return duration==0 || --duration==0;
		}

		/**
		 * Sets item's image ID used to paint this item.  It's shall
		 * be used whenever skin is changed as some ID's valid in one
		 * skin may become invalid in other skins or even a food item
		 * can get an image of a bomb.  This method also sets frame to
		 * 0.
		 *
		 * @param id new image ID.
		 * @see Skin#randomFood()
		 * @see Skin#randomBomb()
		 */
		public final void setImgId(byte id) {
			imgId = id;
			frame = 0;
		}

		/**
		 * Draws item on buffer.
		 *
		 * @param buf buffer to draw on.
		 * @see #redraw(GFXBuffer)
		 * @see #redrawNextFrame(GFXBuffer)
		 * @see GFXBuffer#draw(Point, Point)
		 */
		public void draw(GFXBuffer buf) {
			buf.draw(point, buf.getSkin().imgItem(imgId, frame));
		}

		/**
		 * Redraws item on buffer.  It just clears cell used by item
		 * and then calls draw() method.
		 *
		 * @param buf buffer to draw on.
		 * @see #draw(GFXBuffer)
		 * @see #redrawNextFrame(GFXBuffer)
		 * @see GFXBuffer#clear(Point)
		 * @see GFXBuffer#draw(Point, Point)
		 */
		public void redraw(GFXBuffer buf) {
			buf.clear(point);
			buf.draw(point, buf.getSkin().imgItem(imgId, frame));
		}

		/**
		 * Draws next frame of animation.  If current image is not
		 * animated method only returns false.  Otherwise method
		 * increments frame number (with wrap around) and redraws
		 * image then returns true.
		 *
		 * @param buf buffer to draw on.
		 * @return whether item is animated and buffer was realy
		 *         changed.
		 * @see #redraw(GFXBuffer)
		 */
		public boolean redrawNextFrame(GFXBuffer buf) {
			final int frames = buf.getSkin().countFrames(imgId);
			if (frames<2) return false;
			frame = (byte)((frame + 1) % frames);
			redraw(buf);
			return true;
		}
	}


	/** State of each map cell.  First index is X coordinate, second Y. */
	private byte[][] map;
	/** How many cells are free. */
	private int available;
	/** How many food items there are on the map. */
	private short food;
	/** How many bombs there are on the map. */
	private short bombs;

	/** List of items on the map. */
	private LinkedHashMap<Point, Item> items =
		new LinkedHashMap<Point, Item>();

	/** Buffer used to paint this map. */
	private GFXBuffer buffer;


	/**
	 * Creates new Map object with given Skin used to paint it, given
	 * dimensions, and cells where walls are located.  If walls is
	 * null map has no walls.
	 *
	 * @param skin Skin used to paint map on buffer.
	 * @param width number of columns on the map.
	 * @param height number of rows on the map.
	 * @param walls positions of walls.
	 * @throws IllegalArgumentException if width or height is less then
	 *        5 or there are less then 10% of cells free (ie. w/o wall).
	 */
	protected Map(Skin skin, short width, short height, boolean walls[][]) {
		if (width<5 || height<5) {
			throw new IllegalArgumentException();
		}
		buffer    = new GFXBuffer(skin, width, height);
		map       = new byte[width][height];
		available = width * height;
		if (walls!=null) {
			for (short x = 0; x < width; ++x) {
				for (short y = 0; y < height; ++y) {
					if (walls[x][y]) {
						map[x][y] = WALL;
						--available;
					}
				}
			}
		}
		if (available * 10 / width / height == 0) {
			throw new IllegalArgumentException();
		}
		drawMap();
	}

	/**
	 * Creates new Map object with given Skin used to paint it, gicen
	 * dimensions and no walls.
	 *
	 * @param skin Skin used to paint map on buffer.
	 * @param width number of columns on the map.
	 * @throws IllegalArgumentException if width or height is less then 5.
	 * @param height number of rows on the map.
	 */
	public Map(Skin skin, short width, short height) {
		this(skin, width, height, null);
	}

	/**
	 * Creates new Map object with given Skin used to paint it, and
	 * given positions of walls.  At each position walls 2D array
	 * contains true value wall is put.
	 *
	 * @param skin Skin used to paint map on buffer.
	 * @param walls positions of walls.
	 * @throws IllegalArgumentException if width or height is less then
	 *        5 or there are less then 10% of cells free (ie. w/o wall).
	 */
	public Map(Skin skin, boolean[][] walls) {
		this(skin, (short)walls.length, (short)walls[0].length, walls);
	}




	/**
	 * Removes all items from the map.  All positions are marked
	 * either FREE or WALL (if there was WALL already) and items list
	 * is cleard.  It is caller's responsibility to clear
	 * (ie. destroy) Snake object as well.
	 */
	public void reset() {
		items.clear();
		food = bombs = 0;
		final short width = getWidth(), height = getHeight();
		available = width * height;
		for (short x = 0; x < width; ++x) {
			for (short y = 0; y < height; ++y) {
				map[x][y] = map[x][y]==WALL ? WALL : FREE;
				if (map[x][y]==WALL) --available;
			}
		}
		drawMap();
	}


	/**
	 * Initializes a new plain map with given dimensions.  It is
	 * caller's responsibility to clear (ie. destroy) Snake object as
	 * well.  After calling this method caller should use getBuffer()
	 * method to obtain new GFXBuffer if it uses it in any components
	 * as the old one may become invalid.
	 *
	 * @param width number of columns on the map.
	 * @param height number of rows on the map.
	 * @throws IllegalArgumentException if width or height is less then 5.
	 */
	public void reset(short width, short height) {
		items.clear();
		food = bombs = 0;
		if (width!=getWidth() || height!=getHeight()) {
			if (width<5 || height<5) {
				throw new IllegalArgumentException();
			}
			map = new byte[width][height];
			buffer = new GFXBuffer(buffer.getSkin(), width, height);
		}
		available = width * height;
		for (short x = 0; x < width; ++x) {
			for (short y = 0; y < height; ++y) {
				map[x][y] = FREE;
			}
		}
		drawMap();
	}


	/**
	 * Initializes a new map as defined by walls argument.  All
	 * positions are marked either FREE or WALL (if coresponding walls
	 * array cell is true) and items list is cleard.  It is caller's
	 * responsibility to clear (ie. destroy) Snake object as well.
	 * After calling this method caller should use getBuffer() method
	 * to obtain new GFXBuffer if it uses it in any components as the
	 * old one may become invalid.
	 *
	 * @param walls positions of walls; if null method behaves like
	 *        reset().
	 * @throws IllegalArgumentException if width or height is less then
	 *        5 or there are less then 10% of cells free (ie. w/o wall).
	 * @see #reset()
	 */
	public void reset(boolean walls[][]) {
		if (walls==null) {
			reset();
			return;
		}
		items.clear();
		food = bombs = 0;
		final short width  = (short)walls.length;
		final short height = (short)walls[0].length;
		if (width<5 || height<5) {
			throw new IllegalArgumentException();
		}
		available = width * height;
		if (width!=map.length || height!=map[0].length) {
			map = new byte[width][height];
			buffer = new GFXBuffer(buffer.getSkin(), width, height);
		}
		for (short x = 0; x < width; ++x) {
			for (short y = 0; y < height; ++y) {
				map[x][y] = walls[x][y] ? WALL : FREE;
				if (walls[x][y]) --available;
			}
		}
		if (available * 10 / width / height == 0) {
			throw new IllegalArgumentException();
		}
		drawMap();
	}

	/**
	 * Initializes a new map as defined by walls argument and changes
	 * skin.  All positions are marked either FREE or WALL (if
	 * coresponding walls array cell is true) and items list is
	 * cleard.  It is caller's responsibility to clear (ie. destroy)
	 * Snake object as well.  After calling this method caller should
	 * use getBuffer() method to obtain new GFXBuffer if it uses it in
	 * any components as the old one may become invalid.
	 *
	 * @param walls positions of walls; if null method behaves like
	 *        reset().
	 * @param skin a new skin; if null skin is not changes.
	 * @throws IllegalArgumentException if width or height is less then
	 *        5 or there are less then 10% of cells free (ie. w/o wall).
	 * @see #reset()
	 * @see #reset(boolean[][])
	 * @see #changeSkin(Skin, Collection)
	 */
	public void reset(boolean walls[][], Skin skin) {
		if (skin==null || skin==buffer.getSkin()) {
			reset(walls);
			return;
		}

		short width = (walls==null ? getWidth() : (short)walls.length);
		short height = (walls==null ? getHeight() : (short)walls[0].length);
		if (width * skin.getWidth() != buffer.getWidth() ||
			height * skin.getHeight() != buffer.getHeight()) {
			buffer = new GFXBuffer(skin, width, height);
		} else {
			buffer.setSkin(skin);
		}

		if (width!=getWidth() || height!=getHeight()) {
			if (width<5 || height<5) {
				throw new IllegalArgumentException();
			}
			map = new byte[width][height];
		}

		if (walls==null) {
			reset(width, height);
		} else {
			reset(walls);
		}
	}



	/**
	 * Changes skin and repaints all items.  It is caller's
	 * responsibility to clear (ie. destroy) Snake object as well.
	 * After calling this method caller should use getBuffer() method
	 * to obtain new GFXBuffer if it uses it in any components as the
	 * old one may become invalid.
	 *
	 * @param skin new Skin.
	 * @param snake snake body elements to redraw.
	 */
	public void changeSkin(Skin skin, Collection<Snake.Body> snake) {
		if (skin==null) return;
		Skin oldSkin = buffer.getSkin();
		if (skin==oldSkin) return;
		if (oldSkin.getHeight() != skin.getHeight() ||
			oldSkin.getWidth() != skin.getWidth()) {
			buffer = new GFXBuffer(skin, getWidth(), getHeight());
		}

		buffer.setSkin(skin);
		drawMap(true);
		for (Item item : items.values()) {
			item.setImgId(item.isFood()?skin.randomFood():skin.randomBomb());
			item.redraw(buffer);
		}
		for (GFXBuffer.Drawable item : snake) {
			item.redraw(buffer);
		}
	}


	/**
	 * Returns map's width.
	 *
	 * @return map's width.
	 */
	public short getWidth  () { return (short)map.length; }
	/**
	 * Retrns map's height.
	 *
	 * @return map's height.
	 */
	public short getHeight () { return (short)map[0].length; }
	/** R
	 * eturns number of free cells on the map.
	 *
	 * @return number of free cells on the map.
	 */
	public int   countFree () { return available; }
	/**
	 * Retruns number of food items on the map.
	 *
	 * @return number of food items on the map.
	 */
	public short countFood () { return food; }
	/**
	 * Returns number of bombs on the map.
	 *
	 * @return number of bombs on the map.
	 */
	public short countBombs() { return bombs; }

	/**
	 * Returns new point that wrapps around the edge of the map.
	 * Ie. if x or y coordinate is negative or greater then or equal
	 * to widht or height od the map it is changed to the range [0,
	 * width) or [0, height).
	 *
	 * @param p point to transform.
	 * @return new point that wrapps around the edge of the map.
	 */
	public Point wrapPoint(Point p) {
		return p.wrap(getWidth(), getHeight());
	}


	/**
	 * Returns state of map at the point.  Point is wrappedd using
	 * wrapPoint() method.
	 *
	 * @param point position of cell in question.
	 * @return state of map cell at given position.
	 */
	public byte get(Point point) {
		point = wrapPoint(point);
		return map[point.getX()][point.getY()];
	}

	/**
	 * Returns item at given position or null if there is no such
	 * item.  Point is wrappedd using wrapPoint() method.
	 *
	 * @param point position of cell in question.
	 * @return item at given position.
	 */
	public Item getItem(Point point) {
		return items.get(point.wrap(getWidth(), getHeight()));
	}


	/**
	 * Returns true if given cell is ccupied (ie. is not FREE).  Point
	 * is wrappedd using wrapPoint() method.
	 *
	 * @param point position of cell in question.
	 * @return whether map cell at given position is occupied.
	 */
	public boolean isOccupied(Point point) {
		return get(point) != FREE;
	}

	/**
	 * Returns true if given cell is safe for snake to move on
	 * (ie. it's FREE or contains FOOD).  Point is wrappedd using
	 * wrappedPoint() method.
	 *
	 * @param point position of cell in question.
	 * @return whether it's safe for snake to move on map cell at
	 * given position.
	 */
	public boolean isSafe(Point point) {
		byte b = get(point);
		return b == FREE || b == FOOD;
	}



	/**
	 * Sets cells at given position to given state.  If cell is already
	 * occupied method throws an exception.
	 *
	 * @param point position of cell in question.
	 * @param st new cell's state
	 * @throws CellOccupiedException if cell is already occupied.
	 */
	protected void occupy(Point point, byte st) {
		point = wrapPoint(point);
		if (map[point.getX()][point.getY()]!=FREE) {
			throw new CellOccupiedException(point);
		}
		map[point.getX()][point.getY()] = st;
		--available;
	}

	/**
	 * Sets cells at given position to WALL.  If cell is already
	 * occupied method throws an exception.  Wall is *not* being
	 * painted on the buffer.  Caller has to do it manualy by calling
	 * (for instance) drawWall() method.
	 *
	 * @param point position of cell in question.
	 * @throws CellOccupiedException if cell is already occupied.
	 * @see #drawWall(Point)
	 * @see #drawWalls().
	 */
	public void putWall(Point point) {
		/* Wall need drawing *after* all walls are put */
		occupy(point, WALL);
	}

	/**
	 * Draw walls at the point.
	 *
	 * @param point position on map in question.
	 * @throws NoSuchElementException if there is no wall at given
	 *        position.
	 * @see #drawWalls()
	 * @see #drawWallsAround(Point)
	 */
	public void drawWall(Point point) {
		if (get(point)!=WALL) {
			throw new NoSuchElementException();
		}
		byte ngb = 0;
		for (byte dir = Direction.DIR_MIN; dir <= Direction.DIR_MAX; ++dir) {
			ngb <<= 1;
			ngb |= (byte)(get(point.transform(dir))==WALL ? 1 : 0);
		}
		buffer.redraw(wrapPoint(point), buffer.getSkin().imgWall(ngb));
	}

	/**
	 * Draw all walls on the map.
	 *
	 * @see #drawWall(Point)
	 * @see #drawWallsAround(Point)
	 */
	public void drawWalls() {
		drawMap(false);
	}

	/**
	 * Draw all walls around given position.  Around means above,
	 * belowe, on the left and on the right.
	 *
	 * @param point position on map in question.
	 * @see #drawWall(Point)
	 * @see #drawWalls()
	 */
	public void drawWallsAround(Point point) {
		for (byte dir = Direction.DIR_MIN; dir <= Direction.DIR_MAX; ++dir) {
			Point p = point.transform(dir, getWidth(), getHeight());
			if (map[p.getX()][p.getY()]==WALL) {
				drawWall(p);
			}
		}
	}

	/**
	 * Draws all walls and free cells on the map.  This method does
	 * not draw items and snake.
	 *
	 * @see #drawMap(boolean)
	 */
	protected void drawMap() {
		drawMap(true);
	}


	/**
	 * Draws all walls and (if argument is true) free cells on the
	 * map.  This method does not draw items and snake.
	 *
	 * @param drawFree whether to draw free cells.
	 */
	protected void drawMap(boolean drawFree) {
		final short width = getWidth(), height = getHeight();
		for (short x = 0; x < width; ++x) {
			for (short y = 0; y < width; ++y) {
				switch (map[x][y]) {
				case FREE: if (drawFree) buffer.clear(new Point(x, y)); break;
				case WALL: drawWall(new Point(x, y));
				}
			}
		}
	}


	/**
	 * Sets cells at given position to BODY.  Position is obtained by
	 * calling Snake.Body.getPoint() method. If cell is already
	 * occupied method throws an exception.  Snake's body is then
	 * painted on the buffer by calling
	 * GFXBuffer.draw(GFXBuffer.Drawable) method.  This method also
	 * redraws previous and next body elements.
	 *
	 * @param body snake's body element
	 * @throws CellOccupiedException if cell is already occupied.
	 */
	public void putSnake(Snake.Body body) {
		occupy(body.getPoint(), BODY);
		body.draw(buffer);
		if (body.getPrev()!=null) {
			body.getPrev().redraw(buffer);
		}
		if (body.getNext()!=null) {
			body.getNext().redraw(buffer);
		}
	}

	/**
	 * Sets cells at given position to FOOD or BOMB depending on
	 * item's type.  Position is obtained by calling Item.getPoint()
	 * method. If cell is already occupied method throws an exception.
	 * Item is then painted on the buffer by calling
	 * GFXBuffer.draw(GFXBuffer.Drawable) method.  Item is also added
	 * to item list held by Map object.
	 *
	 * @param item item to add to map.
	 * @throws CellOccupiedException if cell is already occupied.
	 */
	public void putItem(Item item) {
		Point point = wrapPoint(item.getPoint());
		occupy(point, item.getMapState());
		items.put(item.getPoint(), item);
		item.draw(buffer);
		if (item.isFood()) ++food; else ++bombs;
	}


	/**
	 * Sets given position on map to FREE.  If cell's state is not
	 * what was expected exception is thrown.  This point is also
	 * cleared on buffer.
	 *
	 * @param point position on map in question.
	 * @param expected expected state of the cell.
	 * @throws NoSuchElementException if cell's state does not equal
	 *        expected.
	 */
	protected void clear(Point point, byte expected) {
		point = wrapPoint(point);
		if (map[point.getX()][point.getY()]!=expected) {
			throw new NoSuchElementException();
		}
		map[point.getX()][point.getY()] = FREE;
		++available;
		buffer.clear(point);
	}

	/**
	 * Removes wall from given point.  It's caller's responsibility to
	 * redraw all walls around the point by (for instance) calling
	 * drawWallsAround().
	 *
	 * @param point position on map in question.
	 * @throws NoSuchElementException if cell's state is not WALL.
	 * @see #drawWallsAround(Point)
	 */
	public void clearWall(Point point) {
		clear(point, WALL);
	}

	/**
	 * Removes snake body from given point.  It's caller's
	 * responsibility to redraw any neighbour body elements if
	 * neccesery.  It's also his responsibility to accualy remove the
	 * element from the snake.
	 *
	 * @param point position on map in question.
	 * @throws NoSuchElementException if cell's state is not BODY.
	 */
	public void clearSnake(Point point) {
		clear(point, BODY);
	}

	/**
	 * Removes item from the map.  Position is obtained by calling
	 * Item.getPoint() method.
	 *
	 * @param item item to remove.
	 * @throws NoSuchElementException if cell's state does not
	 *        correspond to item's type.
	 */
	public void removeItem(Item item) {
		clear(item.getPoint(), item.getMapState());
		items.remove(item.getPoint());
		if (item.isFood()) --food; else --bombs;
	}

	/**
	 * Removes item from the map.
	 *
	 * @param point position on map in question.
	 * @throws NoSuchElementException if cell's state does not
	 *        correspond to item's type or there is no item on given
	 *        position.
	 */
	public void removeItem(Point point) {
		removeItem(getItem(point));
	}



	/**
	 * Returns random free position from the map.
	 *
	 * @return position of random free cell.
	 * @throws NoSuchElementException if all cells are occupied.
	 */
	public Point random() {
		if (available==0) throw new NoSuchElementException();

		final int width  = getWidth ();
		final int height = getHeight();

		/* There are many available cells (at least 20%) */
		if (5 * available >= width * height) {
			int x, y;
			do {
				x = (int)(Math.random() * width);
				y = (int)(Math.random() * height);
			} while (map[x][y]!=FREE);
			return new Point(x, y);
		}

		/* Get available cells */
		Point[] points = new Point[available];
		int a = available, x = 0, y = 0;
		while (a>0) {
			if (map[x][y]==FREE) points[--a] = new Point(x, y);
			if (++x == width) {
				x = 0;
				++y;
			}
		}

		return points[(int)(Math.random() * available)];
	}



	/**
	 * Returns collection of all items on the map.  This list is
	 * sorted in the same order items were inserted into the list, but
	 * you should not relay on that as this behaviour may change
	 * without notice.
	 *
	 * @return collection of items on map.
	 */
	public Collection<Item> getItems() {
		return items.values();
	}

	/**
	 * Traverses through all items on map and redraws their next
	 * frames.
	 *
	 * @return true if any item was repainted any buffer was changed.
	 */
	public boolean nextFrame() {
		boolean ret = false;
		for (Item i : items.values()) {
			ret |= i.redrawNextFrame(buffer);
		}
		return ret;
	}


	/**
	 * Returns GFXBuffer used by this object to paint map.
	 *
	 * @return graphical buffer associated with this object.
	 */
	public GFXBuffer getBuffer() {
		return buffer;
	}



	/**
	 * Map Component which gets a GFXBuffer and draws it's content
	 * centered whenever it's repainted.  Used to display the map.
	 *
	 * @version 0.1
	 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
	 */
	public final static class Component extends JComponent {
		/** Graphical buffer. */
		private GFXBuffer buffer;

		/**
		 * Constructs new compontn and associates buffer to it.
		 *
		 * @param buf buffer which will be drawn in component.
		 */
		public Component(GFXBuffer buf) {
			buffer = buf;
		}


		/**
		 * Changes buffer asociated with component.
		 *
		 * @param buf buffer which will be drawn in component.
		 */
		public void setBuffer(GFXBuffer buf) {
			if (buffer==buf) return;
			buffer = buf;
			setSize(buf.getWidth(), buf.getHeight());
			repaint();
		}

		public void paint(Graphics g) {
			Dimension size = getSize();
			if (size.equals(getPreferredSize())) {
				g.drawImage(buffer, 0, 0, null);
				return;
			}
			buffer.getSkin().clearGraphics(g, size);
			int x = (int)(size.getWidth () - buffer.getWidth ()) >> 1;
			int y = (int)(size.getHeight() - buffer.getHeight()) >> 1;
			g.drawImage(buffer, x, y, null);
		}

		public void update(Graphics g) {
			paint(g);
		}

		public Dimension getMinimumSize() {
			return new Dimension(buffer.getWidth(), buffer.getHeight());
		}

		public Dimension getPreferredSize() {
			return new Dimension(buffer.getWidth(), buffer.getHeight());
		}


	}
}

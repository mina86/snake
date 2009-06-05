package com.mina86.snake;

import java.awt.Graphics2D;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Class holding information about snake.  This class extends
 * AbstractCollection therefore it can be used as a read-only
 * collection of snake's body elements.  A body element is part of
 * snake that occupy exacly one cell on map.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
 */
public final class Snake extends AbstractCollection<Snake.Body> {
	/**
	 * Class represents single snake's body element which occupy a
	 * single cell on map.  It is a d-linked list node as it contains
	 * pointers to previous and next body elements.
	 *
	 * @version 0.1
	 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
	 */
	public static final class Body implements GFXBuffer.Drawable {
		/** Position of the body element. */
		protected Point point;
		/** Previous body element (ie. towards the head). */
		protected Body prev;
		/** Next body element (ie. towards the tail). */
		protected Body next;

		/**
		 * Creates new body element with all elements set.
		 *
		 * @param point location of the body element.
		 * @param prev previous body element.
		 * @param next next body element.
		 */
		public Body(Point point, Body prev, Body next) {
			this.point = point;
			this.prev = prev;
			this.next = next;
		}

		/**
		 * Creates new body element at given position and with no
		 * previous/next elements.
		 *
		 * @param point location of the body element.
		 */
		public Body(Point point) {
			this(point, null, null);
		}


		/**
		 * Returns location of the body element.
		 *
		 * @return location of the body element.
		 */
		public Point getPoint() { return point; }

		/**
		 * Returns next body element (ie. the one towards tail).
		 *
		 * @return next body element or null if none.
		 */
		public Body getNext() { return next; }
		/**
		 * Returns previous body element (ie. the one towards head).
		 *
		 * @return previus body element or null if none.
		 */
		public Body getPrev() { return prev; }

		/**
		 * Returns whether given body element is head (ie. has no
		 * previous element).
		 *
		 * @return whether given body element is head.
		 */
		public boolean isHead() { return prev == null; }
		/**
		 * Returns whether given body element is tail (ie. has no
		 * next element).
		 *
		 * @return whether given body element is tail.
		 */
		public boolean isTail() { return next == null; }
		/**
		 * Returns whether given body element is the only part of
		 * snake's body (ie. has no next or previous element, that is
		 * snake's length is 1).
		 *
		 * @return whether given body element is the only part of
		 *         snake's body
		 */
		public boolean isOnly() { return isHead() && isTail(); }


		/**
		 * Returns direction snake's head is facing.
		 *
		 * @return direction snake's head is facing.
		 */
		public byte getHeadDirection() {
			return Direction.calculate(next.point, point);
		}

		/**
		 * Returns direction snake's tail is facing.
		 *
		 * @return direction snake's tail is facing.
		 */
		public byte getTailDirection() {
			return Direction.calculate(point, prev.point);
		}

		/**
		 * Returns snake's body element shape.
		 *
		 * @return snake's body element shape.
		 */
		public byte getBodyShape() {
			return Direction.calculate(prev.point, point, next.point);
		}

		public void draw(GFXBuffer buf) {
			Point img;
			if      (isOnly()) img = Skin.imgOnlyHead();
			else if (isHead()) img = Skin.imgHead(getHeadDirection());
			else if (isTail()) img = Skin.imgTail(getTailDirection());
			else               img = Skin.imgBody(getBodyShape    ());
			buf.draw(point, img);
		}

		public void redraw(GFXBuffer buf) {
			buf.clear(point);
			draw(buf);
		}
	}


	/** Snake's head. */
	private Body head;
	/** Snake's tail. */
	private Body tail;
	/** Snake's length. */
	private int size = 0;
	/** How much more snake shall grow. */
	private int enlarge_by = 1;
	/** Map snake's is walking on. */
	private Map map;


	/**
	 * Creates new snake with head on random position on given map.
	 *
	 * @param map map snak will move on.
	 */
	public Snake(Map map) {
		this(map, null);
	}


	/**
	 * Creates new snake with head on given position on given map.
	 *
	 * @param map map snak will move on.
	 * @param point position where snak's head should be located; if
	 *              null random location will be chose.
	 * @throws CellOccupiedException if given position is occupied.
	 */
	public Snake(Map map, Point point) {
		this.map = map;
		if (point==null) {
			point = map.random();
		}
		tail = head = new Body(point);
		map.putSnake(head);
		++size;
	}


	/**
	 * Returns snake's head.
	 *
	 * @return snake's head.
	 */
	public Body getHead() { return head; }
	/**
	 * Returns snake's tail.
	 *
	 * @return snake's tail.
	 *.
	public Body getTail() { return tail; }
	/**
	* Returns snake's length.
	*
	* @return snake's length.
	*/
	public int size() { return size; }
	/**
	 * Makes snake grow by given value.
	 *
	 * @param by how much more snake should grow.
	 */
	public void grow(int by) { enlarge_by += by; }


	/**
	 * Returns iterator allowing to iterate through all snake's body
	 * elements starting with head.
	 *
	 * @return snake's body elements iterator.
	 */
	public Iterator<Body> iterator() {
		return new Iterator<Body>() {
			private Body part = head;
			public boolean hasNext() { return part != null; }
			public Body next() {
				if (part==null) throw new NoSuchElementException();
				Body tmp = part;
				part = part.getNext();
				return tmp;
			}
			public void remove() { throw new UnsupportedOperationException(); }
		};
	}



	/**
	 * Moves snake one step in given direction.  This method moves
	 * snake and also enlarges or shrinks it depending on {@link
	 * #enlarge_by} field.  If snake finds food it will be removed
	 * from map and method will return true (so return value indicates
	 * whether snake ate something).  If snake dies (because it step
	 * on a bomb, a wall or itself) SnakeDied exception is thrown.
	 *
	 * If snake eat something it's caller's responsibility to call
	 * {@link #grow(int)} method to enlarge (or shrink) snake.  This
	 * is because Snak object does not have access to
	 * GameConfiguration object and has no mean of determining value
	 * of eaten food.
	 *
	 * @param dir direction to move snake to.
	 * @return whether snake found any food.
	 * @throws SnakeDied if snake step on a bomb, a wall or itself.
	 */
	public boolean step(byte dir) throws SnakeDied {
		boolean ateSomething = false;

		Point nextStep = map.wrapPoint(head.getPoint().transform(dir));
		byte state = map.get(nextStep);
		switch (state) {
		case Map.WALL:
		case Map.BOMB:
		case Map.BODY:
			throw new SnakeDied(nextStep, state);

		case Map.FOOD:
			ateSomething = true;
			map.removeItem(nextStep);
			break;

		case Map.FREE:
			break;

		default:
			throw new AssertionError();
		}

		head = new Body(nextStep, null, head);
		head.next.prev = head;
		++size;
		map.putSnake(head);

		if (enlarge_by<0) {
			removeTail(2);
			++enlarge_by;
		} else if (enlarge_by==0) {
			removeTail(1);
		} else {
			--enlarge_by;
		}
		return ateSomething;
	}


	/**
	 * Removes snake's tail (that is shrinks it by one element).  This
	 * method is equivalent to calling {@link #removeTail(int)} with
	 * argument <tt>1</tt>.
	 */
	protected void removeTail() {
		removeTail(1);
	}

	/**
	 * Removes given number of elements from the end of the snake.
	 * However, this method never makes snake shorter then 2 elements.
	 *
	 * @param count number of elements to remove.
	 */
	protected void removeTail(int count) {
		for (count = Math.min(size - 2, count); count > 0; --count) {
			Body tmp = tail;
			tail = tail.prev;
			tail.next = null;
			tmp.prev = null;
			--size;
			map.clearSnake(tmp.getPoint());
			map.getBuffer().clear(tmp.getPoint());
		}
		tail.redraw(map.getBuffer());
	}
}

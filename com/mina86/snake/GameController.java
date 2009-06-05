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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JComponent;



/**
 * Class controling the game.  When game is started it creates new
 * threads for moving the snake and animating items on the map.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class GameController implements Runnable {
	/** List of action listeners. */
	private LinkedList<ActionListener> listeners =
		new LinkedList<ActionListener>();
	/** Game's configuration. */
	private GameConfiguration config;
	/** Map. */
	private Map map;
	/** Snake. */
	private Snake snake;


	/**
	 * Returns game's configuration.
	 *
	 * @return game's configuration.
	 */
	public GameConfiguration getConfig() { return config; }
	/**
	 * Returns map.
	 *
	 * @return map.
	 */
	public Map       getMap()    { return map; }
	/**
	 * Returns snake.
	 *
	 * @return snake.
	 */
	public Snake     getSnake()  { return snake; }
	/**
	 * Returns graphic buffer used to paint the map.
	 *
	 * @return graphic buffer used to paint the map.
	 */
	public GFXBuffer getBuffer() { return map.getBuffer(); }
	/**
	 * Returns skin used to paint the map.
	 *
	 * @return skin used to paint the map.
	 */
	public Skin      getSkin()   { return map.getBuffer().getSkin(); }



	/**
	 * Adds action listener to given game contrler.  Game controler
	 * performs the fllowing actions (donated by acion command):
	 *
	 * <ul>
	 *  <li><tt>GAME FINISHED</tt> - when game has been finished,
	 *                               ie. snake died,
	 *  <li><tt>GAME PAUSED</tt>   - when game has been paused,
	 *  <li><tt>GAME RESUMING</tt> - when game is going to be resumed,
	 *  <li><tt>GAME STARTING</tt> - when game is going to start,
	 *  <li><tt>GAME STOPPED</tt>  - when game stopped and all threads
	 *                               stopped,
	 *  <li><tt>GAME UPDATED</tt>  - when map's state changed and needs
	 *                               repainting.
	 * </ul>
	 *
	 * @param listener listener to add
	 * @see #removeActionListener(ActionListener)
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes action listener from given game contrler.
	 *
	 * @param listener listener to add
	 * @see #addActionListener(ActionListener)
	 */
	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Informs all action listeners that action has been performed.
	 *
	 * @param action action command.
	 */
	protected void doAction(String action) {
		ActionEvent event = new ActionEvent(this, 0, action);
		for (ActionListener l : listeners) {
			l.actionPerformed(event);
		}
	}



	/**
	 * Creates new Game Controler.
	 *
	 * @param config game's configuration.
	 * @param skin skin used to draw map.
	 * @param walls initial map.
	 */
	public GameController(GameConfiguration config,
	                     Skin skin, boolean walls[][]) {
		this.config = config;
		map = new Map(skin, walls);
		snake = new Snake(map);
	}



	/**
	 * Sets snake's movement direction.  This should be used to allow
	 * user controll snake's movements for instance by calling this
	 * method from a KeyListener.  If direction is illegal (snake
	 * cannot tourn 180 degrees) method won't do anything.
	 *
	 * @param dir new direction.
	 */
	public synchronized void setDirection(byte dir) {
		if (snake.size()==1 ||
			!Direction.areOposites(dir, headDirection)) {
			moveingDirection = dir;
		}
	}



	/**
	 * Randomizes snake's movement direction.  This method is used
	 * when automatic game is enabled (ie. snake is controled by a
	 * computer not human).  If there are any available directions
	 * this method will choose one of it at random so the snake will
	 * die only when theres really nowhere to move.
	 *
	 * @param point position of snake's head
	 * @param headDir current direction.
	 * @return direction snake shall move.
	 */
	protected byte randDirection(Point point, byte headDir) {
		if (Math.random()<0.8 && map.isSafe(point.transform(headDir))) {
			return headDir;
		}

		byte dirs[] = new byte[4];
		int count = 0;
		for (byte dir = Direction.DIR_MIN; dir <= Direction.DIR_MAX; ++dir) {
			if ((snake.size()==1 || !Direction.areOposites(dir, headDir))
			    && map.isSafe(point.transform(dir))) {
				dirs[count++] = dir;
			}
		}
		return count == 0 ? headDir : dirs[(int)(Math.random() * count)];
	}



	/**
	 * Performs a single step of a snake.  If it's automatic game and
	 * snake dies map is reset to it's previous state and game
	 * contiues.  If it's not automatic game and snake dies method
	 * returns false which indicates that the game has finished.
	 *
	 * @return whether snake is still alive.
	 * @see #run()
	 */
	private synchronized boolean step() {
		/* Randomize direction if random game */
		if (autoGame) {
			moveingDirection =
				randDirection(snake.getHead().getPoint(), headDirection);
		}

		/* Move snake */
		boolean ate = false;
		try {
			ate = snake.step(moveingDirection);
		}
		catch (SnakeDied died) {
			if (autoGame) {
				resetGame();
				return true;
			}
			return false;
		}
		headDirection = moveingDirection;

		/* It ate something */
		if (ate) {
			snake.grow(config.foodValue());
		}

		/* Items get older */
		LinkedList<Map.Item> old = new LinkedList<Map.Item>();
		for (Map.Item i : map.getItems()) {
			if (i.getsOlder()) {
				old.add(i);
			}
		}
		for (Map.Item i : old) {
			map.removeItem(i);
		}

		/* Put new items */
		if (map.countFree()>3) {
			if (config.putFood(map.countFood())) {
				map.putItem(new Map.Item(map.random(), Map.FOOD,
				                         config.foodDuration(),
				                         getSkin().randomFood()));
			} else if (config.putBomb(map.countBombs())) {
				map.putItem(new Map.Item(map.random(), Map.BOMB,
				                         config.bombDuration(),
				                         getSkin().randomBomb()));
			}
		}

		/* Finish */
		doAction("GAME UPDATED");
		return true;
	}



	/** Current moveing direction. */
	private byte moveingDirection = Direction.UP;
	/** Current head direction. */
	private byte headDirection    = Direction.UP;
	/** Main thread controlling snake. */
	private Thread mainThread = null;
	/** Thread performing animations. */
	private Thread animThread = null;
	/** Whether this is an auto game (ie. controlled by computer). */
	private boolean autoGame  = false;

	/**
	 * Returns true if current game is an automatic game, that is a
	 * game controlled by computer.  Such games can be used as a demo.
	 *
	 * @return whether current game is an auto game.
	 */
	public boolean isAutoGame() { return autoGame; }


	/** Move number. */
	private int move = 0;

	/**
	 * Main thread's body controlling snake's movements.  This method
	 * runs while game's state is changed to STOPPED.  If it's changed
	 * to FINISHED it performs a <tt>GAME FINISHED</tt> action and
	 * waits till it's RUNNING again.  Also, during the game, if it's
	 * PAUSED it waits till it's RUNNING.
	 */
	public void run() {
		try {
			do {
				do {
					Thread.sleep((long)(1000/config.speed(++move,snake.size())));
					requireRunning();
				} while (isRunning() && step());
				state = FINISHED;
				doAction("GAME FINISHED");
				requireRunning();
			} while (!isStopped());
		}
		catch (InterruptedException e) { }
	}

	/**
	 * Paints next frame of item's animation.  If any item needed
	 * update performs <tt>GAME UPDATE</tt> action.
	 */
	protected synchronized void nextFrame() {
		if (map.nextFrame()) {
			doAction("GAME UPDATED");
		}
	}


	/**
	 * Returns score.  At the moment, score is snake's length minus 1.
	 *
	 * @return score.
	 */
	public int getPoints() { return snake.size() - 1;}

	/**
	 * Returns move number.
	 *
	 * @return move number.
	 */
	public int getMove() { return move; }

	/**
	 * Returns snake's speed.
	 *
	 * @return snake's speed.
	 */
	public double getSpeed() {
		return config.speed(move, snake.size());
	}



	/** Value donating that game is stopped (there are no running
	 * threads). */
	public final byte STOPPED  = 0;
	/** Value donating that game is running (snake is moving etc) */
	public final byte RUNNING  = 1;
	/** Value donating that game is paused (there are threads running
	 * but snake is not moving). */
	public final byte PAUSED   = 2;
	/** Value donating that game is finished (there are threads
	 * running but snake is dead). */
	public final byte FINISHED = 3;


	/** Game's state. */
	private byte state = 0;
	/** Whether animations are on. */
	private Bool animate = new Bool();

	/**
	 * Class representing synchronized value which can be toogled
	 * between false and true values.
	 *
	 * @version 0.1
	 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
	 */
	public final static class Bool {
		/** The value. */
		private boolean value;

		/** Creates new object with true value. */
		public Bool() { this(true); }
		/**
		 * Creates new object with given value.
		 *
		 * @param v the value.
		 */
		public Bool(boolean v) { value = v; }

		/** Sets value to true and notifies all waiting objects. */
		public synchronized void on () { value = true; notifyAll(); }
		/** Sets value to false. */
		public void off() { value = false; }
		/** Returns whether value is true.
		 * @return the value. */
		public boolean isOn() { return value; }
		/** Waits till value is true.
		 * @throws InterruptedException if thread was interrupted
		 */
		public synchronized void waitForOn() throws InterruptedException {
			while (!value) wait();
		}
	}

	/**
	 * Returns Bool object holding whether animations are turned on.
	 * Animations should be turned off whenever window is being
	 * iconified so that they do not waste CPU time.
	 *
	 * @return animatins state
	 */
	public Bool animations() { return animate; }

	/**
	 * Returns whether game is stopped.
	 *
	 * @return whether game is stopped.
	 */
	public synchronized boolean isStopped () { return state == STOPPED ; }
	/**
	 * Returns whether game is running.
	 *
	 * @return whether game is running.
	 */
	public synchronized boolean isRunning () { return state == RUNNING ; }
	/**
	 * Returns whether game is paused.
	 *
	 * @return whether game is paused.
	 */
	public synchronized boolean isPaused  () { return state == PAUSED  ; }
	/**
	 * Returns whether game is finished.
	 *
	 * @return whether game is finished.
	 */
	public synchronized boolean isFinished() { return state == FINISHED; }



	/**
	 * Restarts game.  It resets map state and starts the game
	 * creating threads if game is stopped.
	 *
	 * @param auto whether that should be an auto game.
	 */
	public synchronized void restartGame(boolean auto) {
		resetGame();

		if (isStopped()) {
			startGame(auto);
			return;
		}

		autoGame = auto;
		doAction("GAME UPDATED");
		doAction("GAME STARTING");
		state = RUNNING;
		notifyAll();
	}


	/**
	 * Starts a new game.
	 *
	 * @param auto whether that should be an auto game.
	 * @throws IllegalStateException if game is not stopped
	 */
	public synchronized void startGame(boolean auto) {
		if (!isStopped()) {
			throw new IllegalStateException();
		}

		autoGame = auto;
		mainThread = new Thread(this);
		animThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (!isStopped()) {
						Thread.sleep(100);
						animate.waitForOn();
						nextFrame();
					}
				}
				catch (InterruptedException e) { }
			}
			});

		try {
			animThread.setPriority(Math.max(animThread.getPriority() - 3,
			                                Thread.MIN_PRIORITY));
		}
		catch (SecurityException e) { }

		doAction("GAME STARTING");
		state = RUNNING;
		mainThread.start();
		if (animThread!=null) {
			animThread.start();
		}
	}



	/**
	 * Pauses the game.
	 *
	 * @throws IllegalStateException if game is not running.
	 */
	public synchronized void pauseGame() {
		if (!isRunning()) {
			throw new IllegalStateException();
		}
		state = PAUSED;
		doAction("GAME PAUSED");
	}

	/**
	 * Resumes the game.
	 *
	 * @throws IllegalStateException if game is not paused.
	 */
	public synchronized void resumeGame() {
		if (!isPaused()) {
			throw new IllegalStateException();
		}
		doAction("GAME RESUMING");
		state = RUNNING;
		notifyAll();
	}

	/**
	 * Pauses the game if it's running or resumes it if it's paused.
	 *
	 * @throws IllegalStateException if game is not running nor paused.
	 */
	public synchronized void togglePausedGame() {
		if (isRunning()) {
			state = PAUSED;
			doAction("GAME PAUSED");
		} else if (isPaused()) {
			doAction("GAME RESUMING");
			state = RUNNING;
			notifyAll();
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Set paused state of game to given value.  This method throws no
	 * exceptions, but in some circumstances does nothing.
	 *
	 * @param paused whether game should be paused.
	 */
	public synchronized void setPaused(boolean paused) {
		if (paused && isRunning()) {
			state = PAUSED;
			doAction("GAME PAUSED");
		} else if (!paused && isPaused()) {
			doAction("GAME RESUMING");
			state = RUNNING;
			notifyAll();
		}
	}


	/**
	 * Waits till game's state changes to running.
	 *
	 * @throws InterruptedException if thread was interrupted.
	 */
	protected synchronized void requireRunning() throws InterruptedException {
		while (state==PAUSED || state==FINISHED) wait();
	}

	/**
	 * Stops game (changing state to STOPPED) and stopping all
	 * threads.
	 */
	public synchronized void stopGame() {
		if (isStopped()) return;
		state = STOPPED;
		notifyAll();
		mainThread.interrupt();
		animThread.interrupt();
		try { mainThread.join(1000); } catch (InterruptedException e) { };
		try { animThread.join(1000); } catch (InterruptedException e) { };
		/* stop is deprecated but it's here only in case interrupt()
		 * won't work */
		//mainThread.stop();
		//animThread.stop();
		doAction("GAME STOPPED");
	}


	/**
	 * Resets game, ie. map.
	 *
	 * @see #resetGame(GameConfiguration, boolean[][], Skin)
	 */
	public void resetGame() {
		resetGame(null, null, null);
	}

	/**
	 * Resets game and changes map.  After calling this method caller
	 * should update all it's components that uses GFXBuffer returned
	 * by getBuffer() method.
	 *
	 * @param walls a new map; if null method behaves like resetGame().
	 * @see #resetGame(GameConfiguration, boolean[][], Skin)
	 * @see #resetGame()
	 */
	public void resetGame(boolean walls[][]) {
		resetGame(null, walls, null);
	}

	/**
	 * Resets game and changes map and skin.  After calling this
	 * method caller should update all it's components that uses
	 * GFXBuffer returned by getBuffer() method.
	 *
	 * @param walls a new map; if null method behaves like resetGame()
	 *        plus changes skin.
	 * @param skin a new skin, can be null indicating no change; if
	 *        null method behaves like resetGame(boolean[][]).
	 * @see #resetGame(GameConfiguration, boolean[][], Skin)
	 * @see #resetGame(boolean[][])
	 * @see #resetGame()
	 * @see Map#changeSkin(Skin, Collection)
	 */
	public void resetGame(boolean walls[][], Skin skin) {
		resetGame(null, walls, skin);
	}

	/**
	 * Resets game and changes game's configuration.
	 *
	 * @param config new configuration; if null method behaves like
	 *        resetGame().
	 * @see #resetGame(GameConfiguration, boolean[][], Skin)
	 * @see #resetGame()
	 */
	public void resetGame(GameConfiguration config) {
		resetGame(config, null, null);
	}


	/**
	 * Resets game, changes map, skin and configuration. Each argument
	 * can be null indicating no change.  After calling this method
	 * caller should update all it's components that uses GFXBuffer
	 * returned by getBuffer() method.
	 *
	 * @param config new configuration; if null configuration is not
	 *        changes.
	 * @param walls new map; if null map is reset to it's initial
	 *        state (ie. no items only walls and snake's body).
	 * @param skin new skin; if null skin is not chaged.
	 * @see Map#reset()
	 * @see Map#reset(boolean[][])
	 * @see Map#changeSkin(Skin, Collection)
	 */
	public synchronized void resetGame(GameConfiguration config,
	                                   boolean walls[][], Skin skin) {
		if (config!=null) {
			this.config = config;
		}
		map.reset(walls, skin);
		snake = new Snake(map);
		move = 0;
		moveingDirection = Direction.UP;
	}
}

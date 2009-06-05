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
 * Holds game configuration such as numbe of food items on the map,
 * items duration, etc.  It also provides several methods for deciding
 * whether a new item should be put on the map and what speed snake
 * has.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class GameConfiguration {
	/** Index of minimal number of food items. */
	public final static int FOOD_MIN          =  0;
	/** Index of maximal number of food items. */
	public final static int FOOD_MAX          =  1;
	/** Index of minimal food duration. */
	public final static int FOOD_DURATION_MIN =  2;
	/** Index of maximal food duration. */
	public final static int FOOD_DURATION_MAX =  3;
	/** Index of minimal food value. */
	public final static int FOOD_VALUE_MIN    =  4;
	/** Index of maximal food value. */
	public final static int FOOD_VALUE_MAX    =  5;

	/** Index of minimal number of bombs. */
	public final static int BOMB_MIN          =  6;
	/** Index of maximal number of bombs. */
	public final static int BOMB_MAX          =  7;
	/** Index of minimal bombs duration. */
	public final static int BOMB_DURATION_MIN =  8;
	/** Index of maximal bombs duration. */
	public final static int BOMB_DURATION_MAX =  9;

	/** Number of config entries. */
	public final static int CFG_COUNT         = 10;


	/**
	 * Array with configration presets.
	 *
	 * @see #configNames
	 * @see #DEFAULT_CONFIG
	 */
	public final static int configs[][] = {
		{
			  2,  10, /* food */
			 -1,  -1, /* food duration */
			  1,  10, /* food value */
			  0,   0, /* bombs */
			200, 300  /* bombs duration */
		},
		{
			  2,  10, /* food */
			200, 500, /* food duration */
			  0,  10, /* food value */
			  0,   2, /* bombs */
			200, 300  /* bombs duration */
		},
		{
			  1,   5, /* food */
			 50, 250, /* food duration */
			  0,   5, /* food value */
			  0,   3, /* bombs */
			 50, 200  /* bombs duration */
		},
		{
			  1,   3, /* food */
			 50, 150, /* food duration */
			 -1,   5, /* food value */
			  1,   5, /* bombs */
			 50, 200  /* bombs duration */
		},
		{
			  1,   3, /* food */
			 50, 150, /* food duration */
			 -1,   5, /* food value */
			  1,  20, /* bombs */
			 10,  50  /* bombs duration */
		}
	};

	/**
	 * Names of available presets.
	 *
	 * @see #configs
	 */
	public final static String configNames[] = {
		"Piece of cake",
		"Easy",
		"Medium",
		"Hard",
		"Nightmare"
	};

	/** Default preset. */
	public final static int DEFAULT_CONFIG = 2;


	/**
	 * Randomizes a number between min and max including both values.
	 *
	 * @param min minimal possible value.
	 * @param max maximal possible value.
	 * @return pseudo random integer from set [min, max]
	 */
	protected static int rand(int min, int max) {
		return min != max ? min + (int)(Math.random() * (max-min)) : min;
	}

	/**
	 * Randomizes whether a new item should be put on the map.
	 *
	 * @param count current number of items on map.
	 * @param min minimal number of items on map.
	 * @param max maximal number of items on map.
	 * @return whether new item should be put.
	 */
	protected static boolean putItem(int count, int min, int max) {
		return count < max &&
			(Math.random() < Math.hypot(min-count, (max-count))
			 / 1.25 / (max-min));
	}


	/** Configuration values. */
	private int values[];

	/**
	 * Creates new configuration from given values.
	 *
	 * @param values configuration values.
	 */
	public GameConfiguration(int values[]) {
		this.values = values;
	}

	/**
	 * Creates new default configuration.
	 */
	public GameConfiguration() {
		values = configs[DEFAULT_CONFIG];
	}

	/**
	 * Creates new configuration from given preset.
	 *
	 * @param preset preset number to use.
	 * @see #configs
	 */
	public GameConfiguration(int preset) {
		values = configs[preset];
	}


	/**
	 * Returns value at geiven index.
	 *
	 * @param idx value's index.
	 * @return value at given index.
	 */
	public int get(int idx) { return values[idx]; }

	/**
	 * Returns all values.
	 *
	 * @return array with all values.
	 */
	public int[] get() { return values; }


	/**
	 * Returns whether new food item should be put on map.
	 *
	 * @param count current number of food items on map.
	 * @return whether new food item should be put.
	 * @see #putItem(int, int, int)
	 * @see #putBomb(int)
	 */
	public boolean putFood(int count) {
		return putItem(count, values[FOOD_MIN], values[FOOD_MAX]);
	}

	/**
	 * Returns whether new bomb should be put on map.
	 *
	 * @param count current number of bombs on map.
	 * @return whether new bomb should be put.
	 * @see #putItem(int, int, int)
	 * @see #putFood(int)
	 */
	public boolean putBomb(int count) {
		return putItem(count, values[BOMB_MIN], values[BOMB_MAX]);
	}

	/**
	 * Randomizes food duration as donated by configuration values.
	 *
	 * @return pseudo random food duration.
	 * @see #bombDuration()
	 * @see #foodValue()
	 */
	public int foodDuration() {
		return rand(values[FOOD_DURATION_MIN], values[FOOD_DURATION_MAX]);
	}

	/**
	 * Randomizes bomb duration as donated by configuration values.
	 *
	 * @return pseudo random bomb duration.
	 * @see #foodDuration()
	 */
	public int bombDuration() {
		return rand(values[BOMB_DURATION_MIN], values[BOMB_DURATION_MAX]);
	}

	/**
	 * Randomizes food value as donated by configuration values.
	 *
	 * @return pseudo random food valiue.
	 * @see #foodDuration()
	 */
	public int foodValue() {
		return rand(values[FOOD_VALUE_MIN], values[FOOD_VALUE_MAX]);
	}


	/**
	 * Returns snake's speed.  At the moment snake's speed is defined
	 * to be <tt>2.5 * move^(1/4)</tt> moves per second however in
	 * future versions it can be coded to be configurable and to
	 * depend on snake's length as well.
	 *
	 * @param move move number (starting with 1).
	 * @param length snake's langth.
	 * @return snake's speed in moves per second.
	 */
	public double speed(int move, int length) {
		return 2.5 * Math.pow(move, 0.25);
	}
}

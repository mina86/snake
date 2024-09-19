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

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;



/**
 * Static class containing methods for loading maps from map files.
 * It uses {@link ConfigFile} class to load map.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 * @see ConfigFile
 * @see SkinLoader
 */
public final class MapLoader {
	/**
	 * Loads map from given path.
	 *
	 * @param path map file's path.
	 * @return 2D boolean array representing map - free cells are
	 *         donated by {@code false} values, and walls by
	 *         {@code true}.
	 * @throws LoaderException if file could not be read or it has
	 *        invalid format.
	 */
	public static boolean[][] load(String path) throws LoaderException {
		try {
			return load(new ConfigFile(path));
		}
		catch (IOException e) {
			throw new LoaderException("Error reading file", e);
		}
	}


	/**
	 * Loads map from given file.
	 *
	 * @param file map file.
	 * @return 2D boolean array representing map - free cells are
	 *         donated by {@code false} values, and walls by
	 *         {@code true}.
	 * @throws LoaderException if file could not be read or it has
	 *        invalid format.
	 */
	public static boolean[][] load(File file) throws LoaderException {
		try {
			return load(new ConfigFile(file));
		}
		catch (IOException e) {
			throw new LoaderException("Error reading file", e);
		}
	}


	/**
	 * Loads map from given config file.
	 *
	 * @param map loaded config file.
	 * @return 2D boolean array representing map - free cells are
	 *         donated by {@code false} values, and walls by
	 *         {@code true}.
	 * @throws LoaderException if file has invalid format.
	 */
	public static boolean[][] load(ConfigFile map) throws LoaderException {
		short width, height;
		String data;
		try {
			width  = (short)map.getInt("width");
			height = (short)map.getInt("height");
			data   = map.getString("map");
		}
		catch (NumberFormatException e) {
			throw new LoaderException("Invalid number fomat in config file", e);
		}
		catch (NoSuchElementException e) {
			throw new LoaderException("Missing key in config file", e);
		}

		if (width<5 || height<5) {
			throw new LoaderException("Invalid map size");
		}
		boolean walls[][] = new boolean[width][height];
		String rows[] = data.split("\n");

		int limit = height <= rows.length ? height : rows.length;
		for (int i = 0; i<limit; ++i) {
			String row = rows[i].trim();
			int l = width <= row.length() ? width : row.length();
			for (int j = 0; j<l; ++j) {
				walls[j][i] = row.charAt(j) == '#';
			}
		}

		return walls;
	}



	/**
	 * Generates new plain map with given size.
	 *
	 * @param size map's width and height.
	 * @return 2D array representing map with no walls.
	 */
	public static boolean[][] plain(short size) {
		return plain(size, size);
	}

	/**
	 * Generates new plain map with given size.
	 *
	 * @param width map's width.
	 * @param height map's height.
	 * @return 2D array representing map with no walls.
	 */
	public static boolean[][] plain(short width, short height) {
		return new boolean[width][height];
	}


	/**
	 * Generates new map with walls on the top and left edge with
	 * given size.
	 *
	 * @param size map's width and height.
	 * @return 2D array representing map with walls on edges.
	 */
	public static boolean[][] walls(short size) {
		return walls(size, size);
	}

	/**
	 * Generates new map with walls on the top and left edge with
	 * given size.
	 *
	 * @param width map's width.
	 * @param height map's height.
	 * @return 2D array representing map with walls on edges.
	 */
	public static boolean[][] walls(short width, short height) {
		boolean map[][] = new boolean[width][height];
		for (int i = 0; i < width; ++i) {
			map[i][0] = true;
		}
		for (int i = 0; i < height; ++i) {
			map[0][i] = true;
		}
		return map;
	}



	/** File chooser used when loading maps. */
	private static JFileChooser fileChooser = null;

	/**
	 * Shows modal dialog for choosing map file to open and then loads
	 * map.  If user selected map file which method was able to parse
	 * it returns this map.  If user canceled action null is returned.
	 * On error error message is displayed and null is returned.
	 *
	 * @param parent parent component.
	 * @return loaded map or null.
	 */
	public static boolean[][] showDialog(Component parent) {
		if (fileChooser==null) {
			fileChooser = Main.createFileChooser("Map files", "smp");
		}

		if (fileChooser.showOpenDialog(parent)!=JFileChooser.APPROVE_OPTION) {
			return null;
		}

		try {
			return load(fileChooser.getSelectedFile());
		}
		catch (LoaderException exc) {
			String message = exc.getMessage();
			if (message==null) message = "Error loading map";

			JOptionPane.showMessageDialog(parent, message, "Map Loading Error",
			                              JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
}

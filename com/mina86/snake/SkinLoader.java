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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;



/**
 * Astract class containing static methods for loading skins.  It uses
 * {@link ConfigFile} for loading text files provided with image
 * files.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 * @see ConfigFile
 * @see MapLoader
 */
public final class SkinLoader {
	/**
	 * Loads skin from given path.  Skin usually refer to an image
	 * file which name is given in text configuration file.  Given
	 * path is resolved relative to the parent directory of the
	 * configuration file.
	 *
	 * @param path skin file's path.
	 * @return loaded skin.
	 * @throws LoaderException if file could not be read or it has
	 *        invalid format.
	 */
	public static Skin load(String path) throws LoaderException {
		try {
			return load(new ConfigFile(path));
		}
		catch (IOException e) {
			throw new LoaderException("Error reading file", e);
		}
	}


	/**
	 * Loads skin from given file.  Skin usually refer to an image
	 * file which name is given in text configuration file.  Given
	 * path is resolved relative to the parent directory of the
	 * configuration file.
	 *
	 * @param file skin file.
	 * @return loaded skin.
	 * @throws LoaderException if file could not be read or it has
	 *        invalid format.
	 */
	public static Skin load(File file) throws LoaderException {
		try {
			return load(new ConfigFile(file));
		}
		catch (IOException e) {
			throw new LoaderException("Error reading file", e);
		}
	}


	/**
	 * Loads skin from given config file.  Skin usually refer to an
	 * image file which name is given in text configuration file.
	 * Given path is resolved relative to the parent directory of the
	 * configuration file.
	 *
	 * @param map loaded config file.
	 * @return loaded skin.
	 * @throws LoaderException if file has invalid format or image
	 *         could not be read.
	 */
	public static Skin load(ConfigFile map) throws LoaderException {
		try {
			short width     = (short)map.getInt("width");
			short height    = (short)map.getInt("height");
			if (width<1 || height<1) {
				throw new LoaderException("Invalid cell width or height");
			}

			if (map.get("plain-skin")!=null) {
				int vals[] = map.getInts("plain-skin");
				if (vals.length==0) {
					return new PlainSkin(width, height);
				}
				Color colors[] = new Color[PlainSkin.defaultColors.length];
				int num = Math.min(vals.length, colors.length), i;
				for (i = 0; i < num; ++i) {
					colors[i] = new Color(vals[i]);
				}
				for (; i < colors.length; ++i) {
					colors[i] = PlainSkin.defaultColors[i];
				}
				return new PlainSkin(width, height, colors);
			}

			File file = map.getFile();

			String skinFile = map.getString("file");
			Color bgColor   = map.getColor("background");

			byte foodTypes  = (byte)map.getInt("food-types");
			byte bombTypes  = (byte)map.getInt("bomb-types");
			if (foodTypes < 1 || bombTypes < 1) {
				throw new LoaderException("Invalid number of food or bomb types");
			}

			byte frames[]   = map.getBytes("frames");
			if (foodTypes + bombTypes != frames.length) {
				throw new LoaderException("Invalid number of frames counts");
			}

			BufferedImage skinImg;
			skinImg = ImageIO.read(new File(file.getParent(), skinFile));
			byte max = 8;
			for (byte b : frames) if (b>max) max = b;
			if (skinImg.getWidth() < max * width ||
				skinImg.getHeight() < (frames.length + 4) * height) {
				throw new LoaderException("Skin image too small");
			}

			return new SkinFile(skinImg, width, height, frames, foodTypes,
			                    bgColor);
		}
		catch (IOException e) {
			throw new LoaderException("Error reading file", e);
		}
		catch (NumberFormatException e) {
			throw new LoaderException("Invalid number fomat in config file", e);
		}
		catch (NoSuchElementException e) {
			throw new LoaderException("Missing key in config file", e);
		}
	}


	/** File chooser used when loading skins. */
	private static JFileChooser fileChooser = null;

	/**
	 * Shows modal dialog for choosing skin file to open and then
	 * loads skin.  If user selected a skin file which method was able
	 * to load it returns loaded skin.  If user canceled action null
	 * is returned.  On error error message is displayed and null is
	 * returned.
	 *
	 * @param parent parent component.
	 * @return loaded skin or null.
	 */
	public static Skin showDialog(Component parent) {
		if (fileChooser==null) {
			fileChooser = Main.createFileChooser("Skin files", "ssk");
		}

		if (fileChooser.showOpenDialog(parent)!=JFileChooser.APPROVE_OPTION) {
			return null;
		}

		try {
			return load(fileChooser.getSelectedFile());
		}
		catch (LoaderException exc) {
			String message = exc.getMessage();
			if (message==null) message = "Error loading skin";

			JOptionPane.showMessageDialog(parent, message, "Skin Loading Error",
			                              JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
}

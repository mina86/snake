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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



/**
 * Class representing a text configuration file.  It allows read only
 * access to the configuration file.  It also supports files
 * compressed using GZip algorithm.  Constructors automatically check
 * if two file starts with the GZip magic number as donated by
 * GZIPInputStream.GZIP_MAGIC.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class ConfigFile extends HashMap<String, String> {
	/** File class representing loaded configuration file. */
	private File file;


	/**
	 * Returns File class representing loaded configuration file.
	 *
	 * @return configuration file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets File class accosiated with configuration.
	 *
	 * @param f File class.
	 */
	public void setFile(File f) {
		file = f;
	}


	/**
	 * Creates new instance with no file associated to it and no
	 * contetn.
	 */
	public ConfigFile() { }

	/**
	 * Loads and parses configuration file.
	 *
	 * @param path configuration file's path.
	 */
	public ConfigFile(String path) throws IOException {
		this(new File(path));
	}

	/**
	 * Loads and parses configuration file.
	 *
	 * @param file configuration file.
	 * @throws IOException if input/output error occurs.
	 */
	public ConfigFile(File file) throws IOException {
		this.file = file;
		BufferedReader in;

		FileInputStream fis = new FileInputStream(file);
		int magick = fis.read() | (fis.read() << 8);
		fis.close();

		in = magick == GZIPInputStream.GZIP_MAGIC
			? new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"))
			: new BufferedReader(new FileReader(file));

		String line;
		while ((line = in.readLine())!=null) {
			line = line.trim();
			if (line.length()==0) continue;
			if (line.charAt(0)=='#' || line.charAt(0)==';') continue;

			int pos = line.indexOf(':');
			if (pos<1) continue;

			String key = line.substring(0, pos).trim().toLowerCase();
			String value = line.substring(pos+1).trim();
			if (key.length()==0 || value.length()==0) continue;
			String old = get(key);
			put(key, old == null ? value : (old + "\n" + value));
		}

		in.close();
	}



	/**
	 * Returns value mapped to given key.  If key is not mapped method
	 * throws exception.
	 *
	 * @param key key
	 * @return value mapped to given key.
	 * @throws NoSuchElementException if given key is not mapped to
	 *         any value.
	 */
	public String getString(String key) throws NoSuchElementException {
		key = get(key);
		if (key==null) {
			throw new NoSuchElementException();
		}
		return key;
	}

	/**
	 * Returns int value mapped to given key.  If key is not mapped
	 * method throws exception.
	 *
	 * @param key key
	 * @return integer value mapped to given key.
	 * @throws NoSuchElementException if given key is not mapped to
	 *         any value.
	 * @throws NumberFormatException if given key is mapped to a value
	 *         which does not represent a valid integer.
	 */
	public int getInt(String key)
		throws NoSuchElementException, NumberFormatException {
		return Integer.decode(getString(key).trim());
	}

	/**
	 * Returns Color value mapped to given key or null if key is not
	 * mapped to any value or mapped to a value which is not a valid
	 * color.
	 *
	 * @param key key
	 * @return color value mapped to given key or null.
	 */
	public Color getColor(String key) {
		try {
			return new Color(getInt(key));
		}
		catch (Exception exc) {
			return null;
		}
	}



	/**
	 * Returns array of integers mapped to given key.  Array is saved
	 * in a string in such a way that each item is separated by a
	 * comma.
	 *
	 * @param key key
	 * @return array of integers mapped to given key.
	 * @throws NoSuchElementException if given key is not mapped to
	 *         any value.
	 * @throws NumberFormatException if any of values does not
	 *         represent a valid integer.
	 */
	public int[] getInts(String key)
		throws NoSuchElementException, NumberFormatException {
		String strs[] = getString(key).trim().split(",");
		int ret[] = new int[strs.length];
		for (int i = 0; i<strs.length; ++i) {
			ret[i] = Integer.decode(strs[i].trim());
		}
		return ret;
	}

	/**
	 * Returns array of shorts mapped to given key.  Array is saved in
	 * a string in such a way that each item is separated by a comma.
	 *
	 * @param key key
	 * @return array of shorts mapped to given key.
	 * @throws NoSuchElementException if given key is not mapped to
	 *         any value.
	 * @throws NumberFormatException if any of values does not
	 *         represent a valid integer.
	 */
	public short[] getShortss(String key)
		throws NoSuchElementException, NumberFormatException {
		int vals[] = getInts(key);
		short ret[] = new short[vals.length];
		for (int i = 0; i<vals.length; ++i) {
			ret[i] = (short)vals[i];
		}
		return ret;
	}

	/**
	 * Returns array of bytes mapped to given key.  Array is saved in
	 * a string in such a way that each item is separated by a comma.
	 *
	 * @param key key
	 * @return array of bytes mapped to given key.
	 * @throws NoSuchElementException if given key is not mapped to
	 *         any value.
	 * @throws NumberFormatException if any of values does not
	 *         represent a valid integer.
	 */
	public byte[] getBytes(String key)
		throws NoSuchElementException, NumberFormatException {
		int vals[] = getInts(key);
		byte ret[] = new byte[vals.length];
		for (int i = 0; i<vals.length; ++i) {
			ret[i] = (byte)vals[i];
		}
		return ret;
	}



	/**
	 * Saves config file into the same file it was loaded from.  If
	 * file name ends with <tt>.gz</tt> it will be compressed.
	 *
	 * @throws NullPointerException if there is no file asociated with
	 *         this instance.
	 * @throws IOException if I/O exception occurs.
	 */
	public void save() throws IOException {
		save(file);
	}

	/**
	 * Saves config file into the same file it was loaded from.
	 *
	 * @param compress whether to compress the file.
	 * @throws NullPointerException if there is no file asociated with
	 *         this instance.
	 * @throws IOException if I/O exception occurs.
	 */
	public void save(boolean compress) throws IOException {
		save(file, compress);
	}

	/**
	 * Saves config file into given file.  If file name ends with
	 * <tt>.gz</tt> it will be compressed.
	 *
	 * @param path file's path
	 * @throws NullPointerException if there is no file asociated with
	 *         this instance.
	 * @throws IOException if I/O exception occurs.
	 */
	public void save(String path) throws IOException {
		save(new File(path), path.endsWith(".gz"));
	}

	/**
	 * Saves config file into given file.  If file name ends with
	 * <tt>.gz</tt> it will be compressed.
	 *
	 * @param file file to save to
	 * @throws NullPointerException if there is no file asociated with
	 *         this instance.
	 * @throws IOException if I/O exception occurs.
	 */
	public void save(File file) throws IOException {
		save(file, file.getName().endsWith(".gz"));
	}

	/**
	 * Saves config file into given file.
	 *
	 * @param file file to save to
	 * @param compress whether to compress the file.
	 * @throws NullPointerException if there is no file asociated with
	 *         this instance.
	 * @throws IOException if I/O exception occurs.
	 */
	public void save(File file, boolean compress) throws IOException {
		OutputStream out = new FileOutputStream(file);
		if (compress) {
			out = new GZIPOutputStream(out);
		}
		OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");

		Set<Map.Entry<String, String>> entries = entrySet();
		for (Map.Entry<String, String> entry : entries) {
			writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
		}

		writer.close();
	}
}

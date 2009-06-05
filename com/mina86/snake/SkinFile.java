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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;



/**
 * Class represents a skin which is basicly loaded from an image file.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class SkinFile extends Skin {
	/** Image with icons. */
	private BufferedImage image;

	/** Number of frames each item icon has. */
	private byte frames[];
	/** Number of food item icons. */
	private byte foodTypes;
	/** Number of bomb item icons. */
	private byte bombTypes;


	/**
	 * Creates new skin.
	 *
	 * @param img image containing skin icons.
	 * @param w single icon width.
	 * @param h single icon height.
	 * @param fr array with number of frames for each item icon.
	 * @param ft number of food item icons.
	 * @param bgColor background color.
	 */
	public SkinFile(BufferedImage img, short w, short h,
	                byte fr[], byte ft, Color bgColor) {
		super(w, h, bgColor);

		if (fr.length==0 || ft<1 || ft >= fr.length) {
			throw new IllegalArgumentException();
		}

		byte max = 8;
		for (byte m : fr) { if (max < m) max = m; }
		if (img.getWidth() < w * max || img.getHeight() < h * 4 + fr.length) {
			throw new IllegalArgumentException();
		}

		image = img;
		frames = fr;
		foodTypes = ft;
		bombTypes = (byte)(frames.length - ft);
	}




	public void draw(Graphics2D gr, short x, short y, short r, short c) {
		gr.drawImage(image,
		             x * width, y * height, (x+1) * width, (y+1) * width,
		             c * width, r * height, (c+1) * width, (r+1) * width,
		             null);
	}


	public byte countFoodTypes() { return foodTypes; }
	public byte countBombTypes() { return bombTypes; }
	public byte countFrames(byte id) { return frames[id]; }
	public byte randomFood() {
		return foodTypes==1 ? 0 : (byte)(Math.random() * foodTypes);
	}
	public byte randomBomb() {
		return (byte)(foodTypes+(bombTypes==1?0:(Math.random()*bombTypes)));
	}
}

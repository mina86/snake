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
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/tlen.pl)
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

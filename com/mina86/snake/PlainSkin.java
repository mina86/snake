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



/**
 * Represents a plain skin (ie. items are just rectangles).
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class PlainSkin extends Skin {
	/** Names of colors used in plain skin. */
	public static final String colorNames[] = {
		"Snake's body", "Grass", "Wall", "Food items", "Bombs", "Grid",
		"Background"
	};
	/** Default values for colors. */
	public static final Color defaultColors[] = {
		new Color(0x0000FF), new Color(0xCCFFCC), new Color(0xCCCCCC),
		new Color(0x00FF00), new Color(0xFF0000), new Color(0xCCEECC),
		new Color(0x99FF99)
	};


	/** Skin colors. */
	protected Color colors[];
	/**
	 * Returns skin colors.
	 *
	 * @return skin colors.
	 */
	public Color[] getColors() {
		return colors;
	}


	/**
	 * Construct new skin with default size and default colors.
	 */
	public PlainSkin() {
		this((short)10, (short)10, defaultColors);
	}

	/**
	 * Construct new skin with given size and default colors.
	 *
	 * @param size skin's single item widht and height.
	 */
	public PlainSkin(short size) {
		this(size, size, defaultColors);
	}

	/**
	 * Construct new skin with given size and default colors.
	 *
	 * @param width skin's single item widht.
	 * @param height skin's single item height.
	 */
	public PlainSkin(short width, short height) {
		this(width, height, defaultColors);
	}


	/**
	 * Construct new skin with given size and given colors.
	 *
	 * @param size skin's single item widht and height.
	 * @param colors skin's colors.
	 */
	public PlainSkin(short size, Color... colors) {
		this(size, size, colors);
	}


	/**
	 * Construct new skin with given size and given colors.
	 *
	 * @param width skin's single item widht.
	 * @param height skin's single item height.
	 * @param colors skin's colors.
	 */
	public PlainSkin(short width, short height, Color... colors) {
		super(width, height, null);

		if (colors.length < defaultColors.length) {
			Color tmp[] = new Color[defaultColors.length];
			int i = 0;
			for (; i < colors.length; ++i) tmp[i] = colors[i];
			for (; i < defaultColors.length; ++i) tmp[i] = defaultColors[i];
			colors = tmp;
		}

		this.colors = colors;
		this.backgroundColor = colors[6];
	}


	public void draw(Graphics2D gr, short x, short y, short r, short c) {
		short idx = 0;
		if (r == 0 || (r==1 && c!=7)) {
			idx = 0;
		} else if (r==1) {
			idx = 1;
		} else if (r<4) {
			idx = 2;
		} else if (r==4) {
			idx = 3;
		} else {
			idx = 4;
		}

		gr.setColor(colors[idx]);
		gr.fillRect(x * width, y * height, width, height);

		if (colors[5]==null) return;
		if (width >= 15 && height >= 15) {
			gr.setColor(colors[5]);
			gr.drawRect(x * width, y * height, width-1, height-1);
		} else if (width >= 5 && height >= 5) {
			gr.setColor(colors[5]);
			gr.drawLine(x * width, y * height, (x+1) * width - 1, y * height);
			gr.drawLine(x * width, y * height, x * width, (y+1) * height - 1);
		}
	}


	public byte countFoodTypes() { return 1; }
	public byte countBombTypes() { return 1; }
	public byte countFrames(byte id) { return 1; }
	public byte randomFood() { return 0; }
	public byte randomBomb() { return 1; }
}

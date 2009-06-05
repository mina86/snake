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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;



public final class HighScoreDialog extends JDialog implements ActionListener {
	private JLabel labels[];
	private MComboBox comboBox;
	private Main main;

	public void actionPerformed(ActionEvent e) {
		setVisible(false);
	}


	private HighScoreDialog(Main parent) {
		super(parent, "High Score", true);

		main = parent;

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 2;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;

		final int len = GameConfiguration.configs.length;
		String options[] = new String[len * 2];
		for (int i = 0; i < len; ++i) {
			options[i] = GameConfiguration.configNames[i];
			options[i + len] = GameConfiguration.configNames[i] + " plus walls";
		}
		comboBox = new MComboBox(options);
		gridbag.setConstraints(comboBox, c);
		add(comboBox);
		comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					set(comboBox.getSelectedIndex());
				}
			});

		labels = new JLabel[5];
		for (int i = 0; i < 5; ++i) {
			labels[i] = new JLabel(" ", JLabel.CENTER);
			gridbag.setConstraints(labels[i], c);
			add(labels[i]);
		}

		JButton button = new JButton("OK");
		getRootPane().setDefaultButton(button);
		button.addActionListener(this);
		gridbag.setConstraints(button, c);
		add(button);

		pack();
	}


	private void set(String map, int level) {
		int i = level;
		i += (map.equals("plain") ? 0 : GameConfiguration.configs.length);
		comboBox.setSelectedIndex(i);
		setLabels(main.getHighScore(map, level));
	}

	private void set(int idx) {
		int level = idx % GameConfiguration.configs.length;
		set(idx == level ? "plain" : "wall", level);
	}

	private void setLabels(LinkedList<Main.HSEntry> hs) {
		int i = 0;
		for (Main.HSEntry entry : hs) {
			labels[i++].setText(entry.name + " :: " + entry.points);
		}
		while (i<5) {
			labels[i++].setText(" ");
		}
	}


	public void display() {
		set(comboBox.getSelectedIndex());
		setVisible(true);
	}

	public void display(String map, int level) {
		set(map, level);
		setVisible(true);
	}


	private static HighScoreDialog dialog = null;

	public static void display(Main main) {
		if (dialog==null) {
			dialog = new HighScoreDialog(main);
		}
		dialog.display();
	}

	public static void display(Main main, String map, int level) {
		if (dialog==null) {
			dialog = new HighScoreDialog(main);
		}
		dialog.display(map, level);
	}
}

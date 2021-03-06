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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * Dialog used to configure a PlainSkin.  It has a slider which allows
 * setting skin's size and color chooser which allow customizing
 * colors.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
@SuppressWarnings("serial")
public final class PlainSkinDialog extends JDialog implements ActionListener {
	/** Slider for setting skin's size. */
	private JSlider size;
	/** Indictes whether action was canceled. */
	private boolean canceled = false;
	/** Color chooser for setting colors. */
	private JColorChooser chooser;
	/** Combo box for selecting color to set. */
	private MComboBox comboBox;
	/** Initial value of colors. */
	private Color resetColor;
	/** All configured colors. */
	private Color colors[] = new Color[PlainSkin.defaultColors.length];


	/**
	 * Creates new modal dialog with given owner.
	 *
	 * @param owner dialog's owner.
	 */
	public PlainSkinDialog(Frame owner) {
		super(owner, "Customize Skin", true);
		setLayout(new BorderLayout());


		/* Size */
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setBorder(BorderFactory.createTitledBorder("Skin size"));

		size = new JSlider(JSlider.HORIZONTAL, 1, 40, 10);
		panel.add(size);
		size.setMajorTickSpacing(5);
		size.setMinorTickSpacing(1);
		size.setPaintTicks(true);
		size.setPaintLabels(true);
		Hashtable<Integer, JLabel> hash = new Hashtable<Integer, JLabel>();
		hash.put(10, new JLabel("10"));
		size.setLabelTable(hash);

		size.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = size.getValue();
				Hashtable<Integer, JLabel> hash = new Hashtable<Integer, JLabel>();
				hash.put(val, new JLabel("" + val));
				size.setLabelTable(hash);
			}
			});


		/* Colors */
		panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Colors"));
		panel.setLayout(new BorderLayout());
		add(panel);

		chooser = new JColorChooser();
		chooser.getSelectionModel().addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					colors[comboBox.getSelectedIndex()] = chooser.getColor();
				}
			});
		panel.add(chooser);

		panel.add(panel = new JPanel(), BorderLayout.NORTH);

		comboBox = new MComboBox(PlainSkin.colorNames);
		comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					resetColor = colors[comboBox.getSelectedIndex()];
					chooser.setColor(resetColor);
				}
			});
		panel.add(comboBox);

		makeButton(panel, "Reset", new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					colors[comboBox.getSelectedIndex()] = resetColor;
					chooser.setColor(resetColor);
				}
			});


		/* Controls */
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		makeButton(panel, "Defaults", new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					set(PlainSkin.defaultColors);
				}
			});
		getRootPane().setDefaultButton(makeButton(panel, "OK"));
		makeButton(panel, "Cancel");

		set(PlainSkin.defaultColors);
		pack();
	}



	/**
	 * Sets all colors to given.
	 *
	 * @param c skin color array.
	 */
	public void set(Color c[]) {
		for (int i = 0; i<colors.length && i<c.length; ++i) {
			colors[i] = c[i];
		}
		chooser.setColor(colors[comboBox.getSelectedIndex()]);
	}

	/**
	 * Sets color at given index.
	 *
	 * @param i color's index.
	 * @param color color's new value.
	 */
	public void set(int i, Color color) {
		colors[i] = color;
		if (i==comboBox.getSelectedIndex()) {
			chooser.setColor(color);
		}
	}



	/**
	 * Creates new JButton and adds it to given panel.  The dialog is
	 * set as actin listener.  Action command is set to upper case
	 * version of text.
	 *
	 * @param panel panel to add button to.
	 * @param text text on the pannel.
	 * @return created button.
	 */
	private JButton makeButton(JPanel panel, String text) {
		return makeButton(panel, text, this);
	}

	/**
	 * Creates new JButton and adds it to given panel.  Action command
	 * is set to upper case version of text.
	 *
	 * @param panel panel to add button to.
	 * @param text text on the pannel.
	 * @param al button's action listener.
	 * @return created button.
	 */
	private JButton makeButton(JPanel panel, String text, ActionListener al) {
		JButton button = new JButton(text);
		panel.add(button);
		button.setActionCommand(text.toUpperCase());
		button.addActionListener(al);
		return button;
	}


	/**
	 * Handles action performed events.  Only OK anc Cancel button
	 * have ths dialog set as action listener so only action which
	 * close window are handled here.
	 *
	 * @param e action event.
	 */
	public void actionPerformed(ActionEvent e) {
		canceled = e.getActionCommand().equals("CANCEL");
		setVisible(false);
	}




	/**
	 * Returns whether user canceled action.
	 *
	 * @return whether user canceled action.
	 */
	public boolean isCancled() { return canceled; }

	/**
	 * Returns configured colors.
	 *
	 * @return configured colors.
	 */
	public Color[] getColors() { return colors; }

	/**
	 * Returns configured skin size.
	 *
	 * @return configured skin size.
	 */
	public short getSkinSize() { return (short)size.getValue(); }

	/**
	 * Returns configured skin or null if action was canceled.
	 *
	 * @return configured skin or null if action was canceled.
	 */
	public PlainSkin getSkin() {
		return canceled ? null : new PlainSkin((short)size.getValue(), colors);
	}



}

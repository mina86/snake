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

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * A dialog window used to customise a difficulty level.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
@SuppressWarnings("serial")
public final class DifficultyDialog extends JDialog
	implements ActionListener, ChangeListener {
	/** Check box showing whether food items never disappear. */
	private JCheckBox fNeverDisappear;
	/** Ranges for a given property. */
	private Range fCount, fDur, fVal, bCount, bDur;
	/** Combo box for choosing preset. */
	private JComboBox<String> presets;
	/** Difficulty level values accessible after dialog is closed. */
	private int values[] = null;
	/** Identification of chosen preset or -1 if it's 'Custom'
	 * difficulty level. */
	private int preset;
	/** A temporary variable set to true during loading preset so that
	 * dialog ignore a state changed events. */
	private boolean ignoreChange = false;


	/**
	 * Creates a modal difficulty customisation dialog and fills
	 * values with values of given preset.
	 *
	 * @param owner dialog's owner.
	 * @param preset preset's id to load.
	 */
	public DifficultyDialog(Frame owner, int preset) {
		this(owner, GameConfiguration.configs[preset], preset + 1);
	}

	/**
	 * Creates a modal difficulty customisation difficulty and fills
	 * values with values from given configuration.
	 *
	 * @param owner dialog's owner.
	 * @param config difficulty configuration.
	 */
	public DifficultyDialog(Frame owner, GameConfiguration config) {
		this(owner, config.get(), 0);
	}

	/**
	 * Creates a modal difficulty customisation difficulty and fills
	 * values with values from given configuration.
	 *
	 * @param owner dialog's owner.
	 * @param config difficulty configuration.
	 */
	public DifficultyDialog(Frame owner, int config[]) {
		this(owner, config, 0);
	}

	/**
	 * Creates a modal difficulty customisation difficulty and fills
	 * values with values from given configuration and sets presets
	 * combo box to given index.
	 *
	 * @param owner dialog's owner.
	 * @param config difficulty configuration.
	 * @param idx index of item to be selected in presets combo box.
	 */
	private DifficultyDialog(Frame owner, int config[], int idx) {
		super(owner, "Custom Difficulty", true);
		JLabel label;

		Font font = getFont().deriveFont(Font.BOLD, 2.0f + getFont().getSize());

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 2;
		c.fill = GridBagConstraints.BOTH;

		label = makeLabel(1, "", gridbag, c);
		label = makeLabel(1, "Food Items", gridbag, c);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(font);
		makeLabel(1, "  ", gridbag, c);
		label = makeLabel(0, "Bombs", gridbag, c);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(font);

		makeLabel(1, "Count", gridbag, c);
		fCount = makeRange(1, gridbag, c, 1, 500);
		makeLabel(1, "  ", gridbag, c);
		bCount = makeRange(0, gridbag, c, 0, 500);

		makeLabel(1, "Duration", gridbag, c);
		fDur = makeRange(1, gridbag, c, 10, null, 10);
		makeLabel(1, "  ", gridbag, c);
		bDur = makeRange(0, gridbag, c, 10, null, 10);

		makeLabel(1, "", gridbag, c);
		fNeverDisappear = new JCheckBox("Never disappear");
		fNeverDisappear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fDur.setEnabled(!fNeverDisappear.isSelected());
				}
			});
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(fNeverDisappear, c);
		add(fNeverDisappear);
		fNeverDisappear.addChangeListener(this);

		makeLabel(1, "Value", gridbag, c);
		fVal = makeRange(1, gridbag, c, -100, 100);
		makeLabel(0, "", gridbag, c);


		JPanel panel = new JPanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(panel, c);
		add(panel);

		String names[] = new String[GameConfiguration.configNames.length+1];
		names[0] = "Custom";
		for (int i = 0; i<GameConfiguration.configNames.length; ++i) {
			names[i+1] = GameConfiguration.configNames[i];
		}
		presets = new JComboBox<>(names);
		panel.add(presets);

		getRootPane().setDefaultButton(makeButton(panel, "OK", this));
		makeButton(panel, "Cancel", this);

		setConfig(config, idx);
		pack();
	}



	/**
	 * Fills dialog with given preset and sets combo box
	 * appropriately.
	 *
	 * @param i preset number.
	 * @see #setConfig(GameConfiguration)
	 * @see #setConfig(int[])
	 * @see #setConfig(int[], int)
	 */
	public void setPreset(int i) {
		setConfig(GameConfiguration.configs[i], i+1);
	}

	/**
	 * Fills dialog with given values and sets presets combo box to
	 * 'Custom'.
	 *
	 * @param config difficulty configuration.
	 * @see #setPreset(int)
	 * @see #setConfig(int[])
	 * @see #setConfig(int[], int)
	 */
	public void setConfig(GameConfiguration config) {
		setConfig(config.get(), 0);
	}

	/**
	 * Fills dialog with given values and sets presets combo box to
	 * 'Custom'.
	 *
	 * @param vals difficulty configuration.
	 * @see #setPreset(int)
	 * @see #setConfig(GameConfiguration)
	 * @see #setConfig(int[], int)
	 */
	public void setConfig(int vals[]) {
		setConfig(vals, 0);
	}

	/**
	 * Fills dialog with given values and sets presets to given index.
	 *
	 * @param vals difficulty configuration.
	 * @param idx index of item to be selected in presets combo box.
	 * @see #setPreset(int)
	 * @see #setConfig(GameConfiguration)
	 * @see #setConfig(int[])
	 */
	private void setConfig(int vals[], int idx) {
		ignoreChange = true;

		fCount.set(vals[GameConfiguration.FOOD_MIN],
		           vals[GameConfiguration.FOOD_MAX]);
		if (vals[GameConfiguration.FOOD_DURATION_MIN]==-1) {
			fDur.setEnabled(false);
			fNeverDisappear.setSelected(true);
		} else {
			fDur.set(vals[GameConfiguration.FOOD_DURATION_MIN],
			         vals[GameConfiguration.FOOD_DURATION_MAX]);
			fDur.setEnabled(true);
			fNeverDisappear.setSelected(false);
		}
		fVal.set(vals[GameConfiguration.FOOD_VALUE_MIN],
		         vals[GameConfiguration.FOOD_VALUE_MAX]);

		bCount.set(vals[GameConfiguration.BOMB_MIN],
		           vals[GameConfiguration.BOMB_MAX]);
		bDur.set(vals[GameConfiguration.BOMB_DURATION_MIN],
		         vals[GameConfiguration.BOMB_DURATION_MAX]);

		if (idx!=-1) {
			presets.setSelectedIndex(idx);
		}

		ignoreChange = false;
	}



	/*
	 * Creates JLabel and adds it to dialog.
	 *
	 * @param gw widths of label or 0 meaning
	 *           GridBagConstraints.REMAINDER.
	 * @param text label's text.
	 * @param gd GridBagLayout manager used by the dialog.
	 * @param c GridBagConstraints used to add component.
	 * @return created label.
	 */
	private JLabel makeLabel(int gw, String text, GridBagLayout gd,
	                         GridBagConstraints c) {
		JLabel label = new JLabel(text);
		c.gridwidth = gw == 0 ? GridBagConstraints.REMAINDER : gw;
		gd.setConstraints(label, c);
		add(label);
		return label;
	}



	/*
	 * Creates Range with step equal one component and adds it to
	 * dialog.
	 *
	 * @param gw widths of label or 0 meaning
	 *           GridBagConstraints.REMAINDER.
	 * @param gd GridBagLayout manager used by the dialog.
	 * @param c GridBagConstraints used to add component.
	 * @param min minimal value of Range component.
	 * @param max maximal value of Range component.
	 * @return created Range component.
	 * @see makeRange(int, GridBagLayout, GridBagConstraints, int, Integer, int)
	 */
	private Range makeRange(int gw, GridBagLayout gd,
	                        GridBagConstraints c, int min, int max) {
		return makeRange(gw, gd, c, min, max, 1);
	}

	/*
	 * Creates Range component and adds it to dialog.
	 *
	 * @param gw widths of label or 0 meaning
	 *           GridBagConstraints.REMAINDER.
	 * @param gd GridBagLayout manager used by the dialog.
	 * @param c GridBagConstraints used to add component.
	 * @param min minimal value of Range component.
	 * @param max maximal value of Range component.
	 * @param step step value of Range component.
	 * @return created Range component.
	 * @see makeRange(int, GridBagLayout, GridBagConstraints, int, int)
	 */
	private Range makeRange(int gw, GridBagLayout gd,
	                        GridBagConstraints c,
	                        int min, Integer max, int step) {
		Range range = new Range(min, max, step);
		c.gridwidth = gw == 0 ? GridBagConstraints.REMAINDER : gw;
		gd.setConstraints(range, c);
		add(range);
		range.addChangeListener(this);
		return range;
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


	public void actionPerformed(ActionEvent e) {
		values = null;
		if (e.getActionCommand().charAt(0) == 'C') {
			preset = -1;
			setVisible(false);
		}
		preset = presets.getSelectedIndex() - 1;
		if (preset==-1) {
			values = new int[GameConfiguration.CFG_COUNT];
			values[GameConfiguration.FOOD_MIN] = fCount.getFrom();
			values[GameConfiguration.FOOD_MAX] = fCount.getTo();
			if (fNeverDisappear.isSelected()) {
				values[GameConfiguration.FOOD_DURATION_MIN] = -1;
				values[GameConfiguration.FOOD_DURATION_MAX] = -1;
			} else {
				values[GameConfiguration.FOOD_DURATION_MIN] = fDur.getFrom();
				values[GameConfiguration.FOOD_DURATION_MAX] = fDur.getTo();
			}
			values[GameConfiguration.FOOD_VALUE_MIN] = fVal.getFrom();
			values[GameConfiguration.FOOD_VALUE_MAX] = fVal.getTo();

			values[GameConfiguration.BOMB_MIN] = bCount.getFrom();
			values[GameConfiguration.BOMB_MAX] = bCount.getTo();
			values[GameConfiguration.BOMB_DURATION_MIN] = bDur.getFrom();
			values[GameConfiguration.BOMB_DURATION_MAX] = bDur.getTo();
		}
		setVisible(false);
	}


	public void stateChanged(ChangeEvent e) {
		if (!ignoreChange) {
			presets.setSelectedIndex(0);
		}
	}


	/**
	 * Returns whether operation was canceled (ie. by clicking Cancel
	 * button).
	 *
	 * @return true if dialog was canceled or false otherwise.
	 */
	public boolean isCancled() {
		return preset==-1 && values==null;
	}

	/**
	 * Returns whether user has chosen one of the presets.
	 *
	 * @return true if user has chosen one of the presets, false
	 *         otherwise.
	 * @see #getPreset()
	 */
	public boolean isPreset() {
		return preset!=-1;
	}

	/**
	 * Returns preset number user chose or -1.
	 *
	 * @return preset number user chose or -1.
	 * @see #isPreset()
	 * @see #getValues()
	 */
	public int getPreset() {
		return preset;
	}

	/**
	 * Returns configuration values uses has chosen.  They can be used
	 * to construct a new GameConfiguration object.
	 *
	 * @return configuration values uses has chosen.
	 * @see #getPreset()
	 */
	public int[] getValues() {
		return preset==-1 ? values : GameConfiguration.configs[preset];
	}


}

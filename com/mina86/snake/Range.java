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

import java.awt.GridLayout;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * Component containing two fields for entering numbers such that
 * <tt>min &lt;= first_field &lt;= second_field &lt;= max</tt>, where
 * <tt>min</tt> and <tt>max</tt> are values defined when creating
 * component.  Component uses two JSpinners components for allowing
 * users to enter numbers and modifies their SpinnerNumberModel when
 * any value is changed.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
@SuppressWarnings("serial")
public final class Range extends JPanel implements ChangeListener {
	/** Number model for the first field. */
	private SpinnerNumberModel fromModel;
	/** Number model for the second field. */
	private SpinnerNumberModel toModel;
	/** The first field. */
	private JSpinner from;
	/** The second fields. */
	private JSpinner to;
	/** Change listeners. */
	private LinkedList<ChangeListener> listeners =
		new LinkedList<ChangeListener>();
	private static Integer ZERO = Integer.valueOf(0);

	/**
	 * Creates new component with given limits and step equal 1.
	 *
	 * @param min lower limit.
	 * @param max upper limit.
	 */
	public Range(Integer min, Integer max) {
		this(min, max, 1);
	}

	/**
	 * Creates new component with given limits and step.  If limit is
	 * null it's treated as unlimited.
	 *
	 * @param min lower limit.
	 * @param max upper limit.
	 * @param step step for spinners.
	 */
	public Range(Integer min, Integer max, Integer step) {
		setLayout(new GridLayout(1, 2));

		fromModel = new SpinnerNumberModel(
			min == null ? ZERO : min, min, max, step);
		from = new JSpinner(fromModel);
		from.addChangeListener(this);
		add(from);

		toModel = new SpinnerNumberModel(
			max == null ? min == null ? ZERO : min : max,
			min, max, step);
		to = new JSpinner(toModel);
		to.addChangeListener(this);
		add(to);
	}

	public void set(int f, int t) {
		if (f>t) {
			throw new IllegalArgumentException();
		}
		from.setValue(f);
		to.setValue(t);
	}


	/**
	 * Returns lower limit entered by user.
	 *
	 * @return lower limit entered by user.
	 */
	public int getFrom() { return ((Integer)from.getValue()).intValue(); }

	/**
	 * Returns upper limit entered by user.
	 *
	 * @return upper limit entered by user.
	 */
	public int getTo() { return ((Integer)to.getValue()).intValue(); }


	public void stateChanged(ChangeEvent e) {
		JSpinner source = (JSpinner)e.getSource();
		if (source==from) {
			toModel.setMinimum((Integer)source.getValue());
		} else if (source==to) {
			fromModel.setMaximum((Integer)source.getValue());
		} else {
			return;
		}

		if (listeners.size()==0) return;
		e = new ChangeEvent(this);
		for (ChangeListener listener : listeners) {
			listener.stateChanged(e);
		}
	}


	/**
	 * Adds new change listener which is notified any time range is
	 * being changed.
	 *
	 * @param listener change listener to add.
	 */
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes change listener.
	 *
	 * @param listener change listener to remove.
	 */
	public void removeChangeListener(ChangeListener listener) {
		listeners.remove(listener);
	}


	public void setEnabled(boolean enabled) {
		from.setEnabled(enabled);
		to.setEnabled(enabled);
	}
}

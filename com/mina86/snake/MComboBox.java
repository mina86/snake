package com.mina86.snake;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;



/**
 * A wrapper around JComboBox class.  Has identical functionality but
 * supports changing selected item whith mouse wheel.
 */
public class MComboBox extends JComboBox {
	/**
	 * Creates a MComboBox with a default data model.  The default
	 * data model is an empty list of objects. Use addItem to add
	 * items. By default the first item in the data model becomes
	 * selected.
	 */
	public MComboBox() {
		initMouseWheelListener();
	}

	/**
	 * Creates a JComboBox that takes it's items from an existing
	 * ComboBoxModel. Since the ComboBoxModel is provided, a combo box
	 * created using this constructor does not create a default combo
	 * box model and may impact how the insert, remove and add methods
	 * behave.
	 *
	 * @param aModel the ComboBoxModel that provides the displayed list of items
	 */
	public MComboBox(ComboBoxModel aModel) {
		super(aModel);
		initMouseWheelListener();
	}

	/**
	 * Creates a JComboBox that contains the elements in the specified
	 * array. By default the first item in the array (and therefore
	 * the data model) becomes selected.
	 *
	 * @param items an array of objects to insert into the combo box
	 */
	public MComboBox(Object[] items) {
		super(items);
		initMouseWheelListener();
	}

	/**
	 * Creates a JComboBox that contains the elements in the specified
	 * Vector. By default the first item in the vector and therefore
	 * the data model) becomes selected.
	 *
	 * @param items an array of vectors to insert into the combo box
	 */
	public MComboBox(Vector<?> items) {
		super(items);
		initMouseWheelListener();
	}


	/**
	 * Adds a MouseWheelListener which will change selected index when
	 * mouse wheel is rotated.
	 */
	private void initMouseWheelListener() {
		addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					if (getItemCount()<2) return;
					int idx = getSelectedIndex() + e.getWheelRotation();
					if (idx < 0) idx = 0;
					else if (idx >= getItemCount()) idx = getItemCount() - 1;
					setSelectedIndex(idx);
				}
			});
	}
}

/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.ifc;

public interface CrudRowChangeListener<E>
{
	interface Callback
	{
		void allow();

		void disallow();
	}

	/**
	 * Called when a user attempts to change the current row. Return false to stop the user selecting a new row.
	 * 
	 * @param variables
	 * @param source
	 * @param rowChangeCallback
	 * @return
	 */
	void allowRowChange(E currentSelection, E newSelection, Callback allow);

	/**
	 * Called to allow the listener to enact the row change.
	 * 
	 * @param current
	 * @param newSelection
	 */
	void rowChanged(E current, E newSelection);

}

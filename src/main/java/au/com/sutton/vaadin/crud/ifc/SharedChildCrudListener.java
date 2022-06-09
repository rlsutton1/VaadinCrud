/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.ifc;

public interface SharedChildCrudListener<P extends CrudEntity>
{

	/**
	 * this method is invoked when the parent saves, signaling the children that they too should save.
	 */
	public boolean save();

	/**
	 * called by the parent when the parent changes row, allowing the child to change the set of records it is
	 * displaying to match the parent
	 * 
	 * @param item
	 */
	public void refresh(final P parentEntity);

	/**
	 * the parent crud calls this method to check if the child has changes
	 * 
	 * @return
	 */
	public boolean isDirty();

	/**
	 * Overload this method to check the edit layout is ready to be save before saving the data.
	 * 
	 * @param saveProvider
	 * @return true to confirm that the edit layout should be saved.
	 */
	public boolean confirmSave();

	public void cancel();

	public void _postSave();

}
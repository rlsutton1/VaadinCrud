/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.ifc;

import au.com.sutton.vaadin.crud.editor.FormValidationResults;

public interface ChildCrudListener<E>
{

	/**
	 * this method is invoked when the parent saves, signalling the children that they too should save. The parent
	 * entity is provided so that the child crud can retrieve the parent key (for new records)
	 * 
	 * @throws Exception
	 */
	public void save(E parentEntity) throws Exception;

	/**
	 * called by the parent when the parent changes row, allowing the child to change the set of records it is
	 * displaying to match the parent
	 * 
	 * @param item
	 */
	public void selectedParentRowChanged(E parentEntity);

	/**
	 * the parent crud calls this method to check if the child has changes
	 * 
	 * @return
	 */
	public boolean isDirty();

	/**
	 * Used to trigger a validation of the form. Any cross field errors should be returned in the set of
	 * FormValidationErrors. Field level errors should be displayed within the form.
	 * 
	 * @return
	 */
	public FormValidationResults validateForm(E parentEntity);

	/*
	 * overload this method to intercept the user clicking save. return false to cancel the save action.
	 */
	public boolean preSave();

	public void cancel();

	/*
	 * overload this method to do work after the entity has been saved but before the transaction is committed.
	 */
	public void postSave();
}
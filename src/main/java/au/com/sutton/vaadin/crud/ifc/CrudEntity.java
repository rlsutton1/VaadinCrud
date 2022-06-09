/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.ifc;

public interface CrudEntity
{

	/**
	 * Return a short description of the record that can be used in crud filters and some grid columns.
	 * 
	 * @return
	 */
	public String getName();

	public Long getId();

}

/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.example;

import au.com.sutton.vaadin.crud.ifc.CrudEntity;

public class ExampleEntity implements CrudEntity
{

	String name = "fred";

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Long getId()
	{
		return 1L;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}

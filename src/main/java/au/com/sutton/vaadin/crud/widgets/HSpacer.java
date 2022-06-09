/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.widgets;

import com.vaadin.flow.component.html.Span;

public class HSpacer extends Span
{
	private static final long serialVersionUID = 1L;

	public HSpacer()
	{
		super(" ");
		setWidth("10px");
		setId("HSpacer");
	}
}
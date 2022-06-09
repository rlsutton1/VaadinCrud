/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.widgets;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VLayout extends VerticalLayout implements Layout
{
	private static final long serialVersionUID = 1L;

	public VLayout(String id)
	{
		setId(id);
		setMargin(false);
		setPadding(false);
		setSpacing(false);
		/// setSizeFull();

		// setAlignContent(ContentAlignment.START);
		// setFlexDirection(FlexDirection.COLUMN);
	}

	public VLayout(String id, String width)
	{
		this(id);
		setWidth(width);
	}

	public void addAndExpand(Component... components)
	{
		super.add(components);
		super.expand(components);
	}

	public void add(Component... components)
	{
		super.add(components);

	}
}

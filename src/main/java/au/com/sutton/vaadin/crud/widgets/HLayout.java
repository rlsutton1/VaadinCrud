/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.widgets;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HLayout extends HorizontalLayout implements Layout
{
	private static final long serialVersionUID = 1L;

	public HLayout(String id)
	{
		setId(id);
		setMargin(false);
		setPadding(false);
		setSpacing(false);
		// setSizeFull();
		// setAlignContent(ContentAlignment.START);
		// setFlexDirection(FlexDirection.ROW);
	}

	public HLayout(String id, String width)
	{
		this(id);
		setWidth(width);
	}

	public HLayout withComponent(Component component)
	{
		this.add(component);
		return this;
	}

	public HLayout withExpanded(Component component)
	{
		this.expand(component);
		return this;
	}

	@Override
	public void addAndExpand(Component... components)
	{
		super.add(components);
		super.expand(components);
	}

	@Override
	public void add(Component... components)
	{
		super.add(components);
	}
}

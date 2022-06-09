/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited 
 * Proprietary and confidential 
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022 
 */
package au.com.sutton.vaadin.crud.widgets;

import java.util.stream.Stream;

import com.vaadin.flow.component.Component;

public interface Layout
{
	void add(Component... component);

	default void add(Stream<Component> components)
	{
		components.forEach(component -> add(component));
	}

	default void addLayouts(Layout... layouts)
	{
		for (var layout : layouts)
		{
			add((Component) layout);
		}
	}

	Stream<Component> getChildren();
}

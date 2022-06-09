/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.splitlayout.SplitLayout;

public class HorizontalSplitPanelImproved extends SplitLayout
{
	private static final long serialVersionUID = 1L;

	public HorizontalSplitPanelImproved(final String uniqueId, final Integer initialSize)
	{
		this(uniqueId, Optional.ofNullable(initialSize).map(Integer::floatValue).orElse(null));
	}

	public HorizontalSplitPanelImproved(final String uniqueId, final Float initialSize)
	{
		final SplitPanelHelper helper = new SplitPanelHelper(this);
		helper.configureSaveSplitSize(uniqueId, initialSize);

		setId("HorizontalSplitPanelImproved");

	}

	public void setComponents(final Component firstComponent, final Component secondComponent)
	{
		addToPrimary(firstComponent);
		addToSecondary(secondComponent);

	}
}
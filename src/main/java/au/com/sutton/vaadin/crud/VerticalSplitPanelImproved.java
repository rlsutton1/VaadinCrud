/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import java.util.Optional;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.splitlayout.SplitLayout;

public class VerticalSplitPanelImproved extends SplitLayout
{
	private static final long serialVersionUID = 1L;

	public VerticalSplitPanelImproved(final String uniqueId, final Integer initialSize, final Unit initialUnit)
	{
		this(uniqueId, Optional.ofNullable(initialSize).map(Integer::floatValue).orElse(null), initialUnit);
	}

	public VerticalSplitPanelImproved(final String uniqueId, final Float initialSize, final Unit initialUnit)
	{
		final SplitPanelHelper helper = new SplitPanelHelper(this);
		helper.configureSaveSplitSize(uniqueId, initialSize);
	}

	//	public void setComponents(final Component firstComponent, final Component secondComponent)
	//	{
	//		super.setComponents(firstComponent, secondComponent);
	//	}
}
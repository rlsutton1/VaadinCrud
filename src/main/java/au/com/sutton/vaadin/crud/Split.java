/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;

import au.com.sutton.vaadin.crud.ifc.CrudPanelPair;

public class Split extends VerticalSplitPanelImproved implements CrudPanelPair
{
	private static final long serialVersionUID = 1L;

	public Split(String uniqueId, Float initialSize, final Unit initialUnit)
	{
		super(uniqueId, initialSize, initialUnit);
	}

	@Override
	public Component getPanel()
	{
		return this;
	}

	@Override
	public void setSplitterPosition(float pos)
	{
		super.setSplitterPosition(pos);
	}

	@Override
	public void setLocked(boolean locked)
	{
		// super.setLocked(locked);
	}

	@Override
	public void setMinSplitPosition(float pos, Unit unit)
	{
		// super.setMinSplitPosition(pos, unit);
	}

	@Override
	public void setMaxSplitPosition(float pos, Unit unit)
	{
		// super.setMaxSplitPosition(pos, unit);
	}

	@Override
	public void setComponents(Component firstComponent, Component secondComponent)
	{
		addToPrimary(firstComponent);
		addToSecondary(secondComponent);

	}
}
/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;

import au.com.sutton.vaadin.crud.ifc.CrudPanelPair;

public class CrudPanelPairHorizontal extends HorizontalSplitPanelImproved implements CrudPanelPair
{
	private static final long serialVersionUID = 1L;

	public CrudPanelPairHorizontal(String uniqueId, Float initialSize)
	{
		super(uniqueId, initialSize);
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
}
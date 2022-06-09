/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.ifc;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;

public interface CrudPanelPair
{

	public Component getPanel();

	public void setSplitterPosition(float pos);

	void setLocked(boolean locked);

	public void setMinSplitPosition(float pos, Unit unit);

	public void setMaxSplitPosition(float pos, Unit unit);

	public void setComponents(Component selectionLayout, Component navigationLayout);
}
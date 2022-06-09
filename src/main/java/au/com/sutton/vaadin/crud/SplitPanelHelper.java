/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.flow.component.splitlayout.SplitLayout;

class SplitPanelHelper
{
	private Logger logger = LogManager.getLogger();

	private final int defaultPosition = 50;

	private SplitLayout splitPanel;

	SplitPanelHelper(final SplitLayout splitPanel)
	{
		this.splitPanel = splitPanel;
		splitPanel.setSizeFull();
	}

	void configureSaveSplitSize(final String uniqueId, final Float initialSize)
	{
		final String sizeKey = uniqueId + "-splitSize";

		final Float savedSize = getSavedSize(sizeKey);
		if (savedSize != null)
		{
			splitPanel.setSplitterPosition(defaultPosition);
		}
		else
		{
			if (initialSize != null)
			{
				splitPanel.setSplitterPosition(initialSize);
			}
		}
		splitPanel.addSplitterDragendListener((event) ->
			{
				final float newSize = defaultPosition;

				MemberSettingsStorageFactory.getMemberSettingsStorage().put(sizeKey, "" + newSize);
			});
	}

	private Float getSavedSize(final String setting)
	{
		final String savedSize = MemberSettingsStorageFactory.getMemberSettingsStorage().get(setting);
		Float size = (float) defaultPosition;
		try
		{
			size = Float.parseFloat(savedSize);
		}
		catch (NullPointerException e)
		{
			logger.debug("Invalid split size setting for " + setting);
		}
		catch (NumberFormatException e)
		{
			logger.debug("Invalid split size setting for " + setting);
		}

		return size;
	}

}
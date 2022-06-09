/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import com.vaadin.flow.component.formlayout.FormLayout;

public class AdvancedSearchLayout extends FormLayout
{
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_COLS = 3;

	public AdvancedSearchLayout()
	{
		setResponsiveSteps(
				// Use one column by default
				new ResponsiveStep("0", DEFAULT_COLS)
		// // Use two columns, if layout's width exceeds 500px
		// new ResponsiveStep("500px", 2)
		);
	}

}

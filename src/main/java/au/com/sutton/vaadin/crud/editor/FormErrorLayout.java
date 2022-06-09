/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.editor;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;

import au.com.sutton.vaadin.crud.editor.FormValidationResults.FormError;
import au.com.sutton.vaadin.crud.widgets.VLayout;

/*
 * Used to display cross field validation errors.
 */
public class FormErrorLayout extends VLayout
{
	private static final long serialVersionUID = 1L;

	public FormErrorLayout(FormValidationResults formErrors)
	{
		super("FormError");

		final Grid<FormError> grid = new Grid<>();
		grid.setSizeFull();
		grid.addColumn(error -> error.title, "field");

		grid.setItems(formErrors.formErrors);

		setSizeFull();
		add(new H3("Fix the following errors and try again."));
		add(grid);
		expand(grid);

	}
}

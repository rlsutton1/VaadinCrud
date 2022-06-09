/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.editor;

import java.util.ArrayList;
import java.util.List;

/*
 * Used to return the results of a form validation. If the validation succeeds then [success] will be true. If the
 * validation failed and their were cross form validation errors then tehy will be returned in [formErrors] Used to hold
 * cross form validation errors.
 */
public class FormValidationResults
{

	public boolean success = true;
	public List<FormError> formErrors = new ArrayList<>();

	public boolean hasFormErrors()
	{
		return formErrors.isEmpty();
	}

	public void addAllErrors(List<FormError> errors)
	{
		formErrors.addAll(errors);
		success = false;
	}

	public void addError(FormError error)
	{
		formErrors.add(error);
		success = false;
	}

	static class FormError
	{
		public FormError(String title)
		{
			this.title = title;
		}

		String title;
	}

}

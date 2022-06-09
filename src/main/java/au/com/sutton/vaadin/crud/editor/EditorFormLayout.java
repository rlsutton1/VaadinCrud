/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.editor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.BinderValidationStatus;

import au.com.sutton.vaadin.crud.editor.FormValidationResults.FormError;
import au.com.sutton.vaadin.crud.ifc.CrudEntity;

public class EditorFormLayout<E extends CrudEntity> extends FormLayout
{
	private static Logger logger = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	private CrudFieldBinder<E> binder;

	// the field to set focus to when the editor
	// is first displayed.
	Focusable<?> focusField;

	public EditorFormLayout(BindFieldProvider<E> bindProvider, EditFormBuildProvider<E> buildProvider)
	{
		setId("EditorFormLayout");

		this.binder = new CrudFieldBinder<>();
		this.bindProvider = bindProvider;
		this.buildProvider = buildProvider;
		setResponsiveSteps(
				// Use one column by default
				new ResponsiveStep("0", 1)
		// // Use two columns, if layout's width exceeds 500px
		// new ResponsiveStep("500px", 2)
		);

		build();
		bind();
	}

	private BindFieldProvider<E> bindProvider;
	private EditFormBuildProvider<E> buildProvider;

	private void bind()
	{
		bindProvider.bindFields(binder);

	}

	private void build()
	{
		buildProvider.buildEditForm(this);
	}

	public static interface BindFieldProvider<E extends CrudEntity>
	{
		void bindFields(CrudFieldBinder<E> binder);
	}

	public static interface EditFormBuildProvider<E extends CrudEntity>
	{
		void buildEditForm(EditorFormLayout<E> layout);
	}

	/*
	 * If validation fails the entity will not be updated.
	 */
	protected FormValidationResults saveEdits(E currentEntity)
	{
		final var formValidationErrors = new FormValidationResults();

		BinderValidationStatus<E> status = binder.validate();
		if (status.isOk())
		{
			boolean outcome = binder.writeBeanIfValid(currentEntity);
			assert (outcome == true);
			return formValidationErrors;
		}
		else
		{
			formValidationErrors.success = false;
		}

		final var formErrors = status.getBeanValidationErrors();
		if (!formErrors.isEmpty())
		{
			formValidationErrors.success = false;
		}
		formErrors.forEach(error -> formValidationErrors.addError(new FormError(error.getErrorMessage())));

		return formValidationErrors;
	}

	boolean dirty = false;

	public void addValueChangeListener(
			ValueChangeListener<? super ValueChangeEvent<?>> listener)
	{
		binder.addValueChangeListener(e ->
			{
				listener.valueChanged(e);
				dirty = true;
			});

	}

	public boolean isDirty()
	{
		return dirty;
	}

	public void readBean(E entity)
	{
		binder.readBean(entity);
		dirty = false;

	}

	// Defines the field that should recieve focus when the
	// editor opens.
	public void setFirstFocus(Focusable<?> focusField)
	{
		this.focusField = focusField;

	}

	public void setFocus()
	{
		if (focusField != null)
		{
			focusField.focus();
			// if (focusField instanceof TextField)
			// {
			// ((TextField) focusField).
			// }
		}
		else
		{
			logger.warn("You need to call setFirstFocus for %s", this.getClass().getName());
		}

	}

	protected CrudFieldBinder<E> getBinder()
	{
		return binder;
	}

}

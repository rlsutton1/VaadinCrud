/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.editor;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;

import au.com.sutton.vaadin.crud.editor.EditorFormLayout.BindFieldProvider;
import au.com.sutton.vaadin.crud.editor.EditorFormLayout.EditFormBuildProvider;
import au.com.sutton.vaadin.crud.ifc.CrudEntity;
import au.com.sutton.vaadin.crud.widgets.VLayout;

public class EditorLayout<E extends CrudEntity> extends VLayout
{

	private static final long serialVersionUID = 1L;

	private boolean allowEdits = false;

	private EditButtonLayout buttonLayout;

	// true if the editor is the current content.
	boolean editorShowing = false;
	// to handle setup
	boolean nothingShowing = true;
	VLayout editorContent;
	String singularNoun;

	private EditorFormLayout<E> editorForm;

	BindFieldProvider<E> bindProvider;
	EditFormBuildProvider<E> buildProvider;

	VLayout body = new VLayout("Content");
	VLayout formHolder = new VLayout("FormHolder");

	public EditorLayout(BindFieldProvider<E> bindProvider, EditFormBuildProvider<E> buildProvider, String singularNoun)
	{
		super("EditorLayout");
		this.bindProvider = bindProvider;
		this.buildProvider = buildProvider;
		this.singularNoun = singularNoun;
		buttonLayout = new EditButtonLayout();
		this.getStyle().set("padding-top", "0px");

		initLayout();
	}

	private void initLayout()
	{
		setPadding(allowEdits);
		setPadding(true);
		add(body);
		body.add(new H4(singularNoun));
		body.add(formHolder);

		if (editorContent == null)
		{
			editorContent = new VLayout("EditorContent");
			editorForm = new EditorFormLayout<E>(bindProvider, buildProvider);
			editorContent.add(editorForm);
			editorContent.add(buttonLayout);
			editorForm.addValueChangeListener((e) ->
				{
					buttonLayout.enableButtons(true);
				});
		}
	}

	public void showEditor(boolean setFocus)
	{

		if (!editorShowing)
		{
			setContent(editorContent);
			editorShowing = true;
		}
		if (setFocus)
		{
			editorForm.setFocus();
		}
		buttonLayout.enableButtons(allowEdits);
	}

	public void setContent(final Component content)
	{
		formHolder.removeAll();
		formHolder.add(content);
	}

	public void showNoSelectionMessage()
	{
		final VLayout layout = new VLayout("Content");
		layout.setSizeFull();
		final var label = new H3("No selection");
		label.setWidth("300");

		layout.add(label);
		layout.setAlignSelf(Alignment.CENTER, label);
		if (editorShowing || nothingShowing)
		{
			setContent(layout);
			editorShowing = false;
			nothingShowing = false;
		}
	}

	public boolean isDirty()
	{
		// the editorForm won't exists if no records are currenty displayed.
		return editorForm != null && editorForm.isDirty();
	}

	public void startEdit(E entity)
	{
		if (this.allowEdits && editorForm != null)
		{
			editorForm.readBean(entity);
			buttonLayout.enableButtons(allowEdits);
		}
	}

	/*
	 * Call this method to write the form fields back into the entity. The form fields will first be validated. If
	 * validation fails
	 */
	public FormValidationResults saveEdits(E entity)
	{
		// After a button is click we disable it
		// setBean is called on completion of
		// a save
		buttonLayout.enableButtons(allowEdits);
		return editorForm.saveEdits(entity);
	}

	// Called when the user clicked the cancel button
	// but then changed there mind.
	/// We need to re-enable the button as the on-click-disable
	// option leaves it disabled.
	public void cancelComplete()
	{
		buttonLayout.enableButtons(allowEdits);
	}

	public void setAllowEdit(final boolean allowEdits)
	{

		if (buttonLayout != null)
		{
			buttonLayout.setVisible(allowEdits);
		}
		this.allowEdits = allowEdits;
	}

	public boolean isButtonsVisible()
	{
		return buttonLayout.isVisible();
	}

	public void addPrimaryClickListener(final PrimaryClickListener clickListener)
	{
		buttonLayout.addPrimaryClickListener(clickListener);
	}

	public void addSecondaryClickListener(final SecondaryClickListener clickListener)
	{
		buttonLayout.addSecondaryClickListener(clickListener);
	}

	public interface PrimaryClickListener
	{
		public void click(final ClickEvent<?> event);
	}

	public interface SecondaryClickListener
	{
		public void click(final ClickEvent<?> event);
	}

	public void setPrimaryLabel(String primaryButtonLabel)
	{
		this.buttonLayout.setPrimaryLabel(primaryButtonLabel);
	}

	public void setSecondaryLabel(String secondaryButtonLabel)
	{
		this.buttonLayout.setSecondaryLabel(secondaryButtonLabel);
	}

	// Used by the child crud as they don't have save/cancel buttons.
	public void setButtonsVisible(boolean visible)
	{
		buttonLayout.setVisible(visible);

	}

	public CrudFieldBinder<E> getBinder()
	{
		return editorForm.getBinder();
	}

	public void disableEnterShortcut()
	{
		buttonLayout.disableEnterShortcut();
	}

	public void enableEnterShortcut()
	{
		buttonLayout.enableEnterShortcut();

	}

}
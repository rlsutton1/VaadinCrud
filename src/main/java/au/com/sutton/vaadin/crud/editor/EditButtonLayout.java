/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.editor;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;

import au.com.sutton.vaadin.crud.editor.EditorLayout.PrimaryClickListener;
import au.com.sutton.vaadin.crud.editor.EditorLayout.SecondaryClickListener;
import au.com.sutton.vaadin.crud.widgets.HLayout;

public class EditButtonLayout extends HLayout
{

	private static final long serialVersionUID = 1L;
	private Button infoIcon = new Button(FontAwesome.Solid.INFO.create());
	private Button primaryButton = new Button("Primary");
	private Button secondaryButton = new Button("Secondary");

	private ShortcutRegistration primaryShortCut;

	public EditButtonLayout()
	{
		super("EditButtons");
		setSpacing(true);

		infoIcon.setVisible(false);
		add(infoIcon);
		expand(infoIcon);
		setAlignSelf(Alignment.END, infoIcon);

		// add(new Spacer());
		add(secondaryButton);
		// add(new Spacer());
		add(primaryButton);
		primaryShortCut = primaryButton.addClickShortcut(Key.ENTER);
		// 		primaryButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);
		setAlignSelf(Alignment.END, secondaryButton);

		setAlignSelf(Alignment.END, primaryButton);
		primaryButton.setDisableOnClick(true);
		secondaryButton.setDisableOnClick(true);
	}

	public void setDefaultButtonState()
	{
		primaryButton.setEnabled(true);
		secondaryButton.setEnabled(true);
	}

	public void addPrimaryClickListener(final PrimaryClickListener clickListener)
	{
		if (clickListener != null)
		{
			primaryButton.addClickListener(event ->
				{
					clickListener.click(event);

				});
		}
	}

	public void addSecondaryClickListener(final SecondaryClickListener clickListener)
	{
		if (clickListener != null)
		{
			secondaryButton.addClickListener(event ->
				{
					clickListener.click(event);
				});
		}
	}

	public void enableButtons(boolean enable)
	{
		primaryButton.setEnabled(enable);
		secondaryButton.setEnabled(enable);
	}

	public void setPrimaryLabel(String primaryButtonLabel)
	{
		this.primaryButton.setText(primaryButtonLabel);
	}

	public void setSecondaryLabel(String secondaryButtonLabel)
	{
		this.secondaryButton.setText(secondaryButtonLabel);
	}

	public void disableEnterShortcut()
	{
		primaryShortCut.remove();
	}

	public void enableEnterShortcut()
	{
		primaryShortCut = primaryButton.addClickShortcut(Key.ENTER);

	}

}
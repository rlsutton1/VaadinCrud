/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.H3;

import au.com.sutton.vaadin.crud.widgets.VLayout;

public class DialogConfirm extends DialogBase<Void>
{

	private static final long serialVersionUID = 1L;
	private String message;

	public DialogConfirm(String title, String message, OKListener<Void> okListener)
	{
		super(title, false);
		this.message = message;

		super.setOKListener(dialog ->
			{
				okListener.ok(null);
			});
		open();
	}

	boolean html = false;

	public void enableHtml()
	{
		html = true;
	}

	public DialogConfirm(String title, String message, OKListener<Void> okListener, CancelListener cancelListener)
	{
		super(title, false);
		this.message = message;

		super.setOKListener(dialog ->
			{
				okListener.ok(null);
			});
		super.setCancelListener(cancelListener);
		open();
	}

	@Override
	protected Component buildContent()
	{
		VLayout layout = new VLayout("Content");

		Component label;
		if (html == true)
		{
			label = new Html("<span>%s</span>".formatted(message));
		}
		else
		{
			label = new H3(message);
		}

		layout.add(label);
		return layout;
	}

}

/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;

import au.com.sutton.vaadin.crud.widgets.HLayout;
import au.com.sutton.vaadin.crud.widgets.VLayout;

/**
 * @author bsutton
 *
 * @param <R> the return type from the dialog when the user clicks OK.
 */
public abstract class DialogBase<R> extends Dialog
{
	private static final long serialVersionUID = 1L;

	protected VLayout standardContent = new VLayout("StandardContent");
	private VLayout customContent = new VLayout("CustomContent");

	private String title;
	private Button okButton;

	private Button cancelButton;

	private BaseOKListener<R> okListener = null;

	private CancelListener cancelListener = null;

	protected DialogBase(String title, boolean autoOpen)
	{
		this.title = title;
		buildBody();
		if (autoOpen)
			open();
	}

	public void open()
	{
		customContent.add(buildContent());
		super.open();
	}

	private void buildBody()
	{
		this.setCloseOnEsc(true);
		this.setCloseOnOutsideClick(false);

		super.add(standardContent);

		standardContent.add(new H2(title));

		standardContent.setSizeFull();
		standardContent.add(customContent);
		standardContent.expand(customContent);
		standardContent.setFlexGrow(1, customContent);

		HLayout buttons = new HLayout("DialogButtons");
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		buttons.setSpacing(true);
		standardContent.add(buttons);

		buttons.setWidth("100%");

		cancelButton = new Button("Cancel");
		cancelButton.addClassName("button-cancel");
		buttons.add(cancelButton);
		buttons.setAlignSelf(Alignment.START, cancelButton);
		cancelButton.addClickListener(l -> cancel());
		cancelButton.setDisableOnClick(true);

		okButton = new Button("OK");
		okButton.addClassName("button-ok");
		buttons.add(okButton);
		okButton.addThemeName("badge");
		buttons.setAlignSelf(Alignment.END, okButton);

		okButton.addClickShortcut(Key.ENTER);
		okButton.setDisableOnClick(true);

		okButton.addClickListener(l -> ok());

	}

	protected abstract Component buildContent();

	public void setOkCaption(String okCaption)
	{
		// i believe setText has replaced setHeader although I'm dubious
		this.okButton.setText(okCaption);
	}

	public void setCancelCaption(String cancelCaption)
	{
		this.cancelButton.setText(cancelCaption);
	}

	protected void cancel()
	{
		this.close();
		if (cancelListener != null)
			this.cancelListener.cancel();
	}

	protected void ok()
	{
		waitForValidation((valid) ->
			{
				if (valid)
				{
					this.close();
					if (this.okListener != null)
						this.okListener.ok(DialogBase.this);
				}
				else
				{
					this.okButton.setEnabled(true);
				}
			});

	}

	/**
	 * Over load this method (rather than validate) to implement a validator
	 * that needs to run a background process that notifies
	 * the validation state when complete.
	 * @param listener
	 */
	protected void waitForValidation(ValidationComplete listener)
	{
		listener.validate(validate());
	}

	public interface ValidationComplete
	{
		void validate(boolean valid);
	}

	/**
	 * Override this method if you need to validate the user input before the dialog is allowed to close.
	 * 
	 * @return
	 */
	protected boolean validate()
	{
		return true;
	}

	/** 
	 * Call this from your derived class so you can provide
	 * the actual value to the listener.
	 * void setOKListener(OKListener<CreditCardHolder> okListener)
	 * {
	 * 		super.setOKListener(dialog -> okListener.ok(getCardHolder()));
	 * }
	 */
	protected void setOKListener(BaseOKListener<R> okListener)
	{
		this.okListener = okListener;
	}

	public void setCancelListener(CancelListener cancelListener)
	{
		this.cancelListener = cancelListener;
	}

	protected interface BaseOKListener<R>
	{

		void ok(DialogBase<R> dialog);
	}

	public interface OKListener<R>
	{

		void ok(R result);
	}

	public interface CancelListener
	{
		void cancel();
	}

	public void hideCancelButton()
	{
		this.cancelButton.setVisible(false);

	}

	public void hideOKButton()
	{
		this.okButton.setVisible(false);
	}

}

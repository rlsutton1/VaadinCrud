/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.editor;

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;

import au.com.sutton.vaadin.crud.ifc.CrudEntity;

public class CrudFieldBinder<E extends CrudEntity> extends Binder<E>
{

	private static final long serialVersionUID = 1L;

	public CrudFieldBinder()
	{

	}

	/**
	 * Apparently when doing cross field validation by calling
	 * binder.withValidator there is no way to the list of errors back
	 * due to the following issue.
	 *   https://github.com/vaadin/platform/issues/2868
	 *   
	 *   Which essentially says that unless you are using setBean you can't
	 *   do cross form validation.
	 *   
	 *   This code caches the bean sent to the binder when readBean is called
	 *   and then uses that bean to fake a bound bean during validation.
	 *   
	 */
	private boolean validating = false;
	private E bean;

	@Override
	public void readBean(E bean)
	{
		this.bean = bean;
		super.readBean(bean);
	}

	@Override
	public void setBean(E bean)
	{
		throw new RuntimeException("The CrudFieldBinder only works with read/writeBean");
	}

	@Override
	public E getBean()
	{
		if (validating)
		{
			return bean;
		}
		/// this call should always return null as setBean hasn't been
		// called but we do this try to reduce the likelihood of this overload
		// causing problems if someone accicentially uses this class
		// when using setBean.
		return super.getBean();
	}

	@Override
	public BinderValidationStatus<E> validate()
	{
		try
		{
			validating = true;
			return super.validate();
		}
		finally
		{
			validating = false;
		}

	}

}
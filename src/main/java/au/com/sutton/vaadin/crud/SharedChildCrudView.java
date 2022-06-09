/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import au.com.sutton.vaadin.crud.ifc.CrudEntity;
import au.com.sutton.vaadin.crud.ifc.EventListener;
import au.com.sutton.vaadin.crud.ifc.SharedChildCrudListener;

public abstract class SharedChildCrudView<P extends CrudEntity, E extends CrudEntity> extends ParentCrudView<E>
		implements SharedChildCrudListener<P>
{

	private static final long serialVersionUID = 1L;
	private ParentCrudView<P> parentCrud;
	// private ParentAssociationProvider<P> parentAssociationProvider;
	private CrudEntity parentEntity;
	// Used to track whether save was called and therefore postSave should also
	// be called
	private boolean inSave;

	public SharedChildCrudView(final ParentCrudView<P> parentCrud)
	{
		this(parentCrud, CrudType.FULL);
	}

	public SharedChildCrudView(final ParentCrudView<P> parentCrud, final CrudType crudType)
	{
		super(CrudDisplayMode.VERTICAL, crudType);
		this.parentCrud = parentCrud;
		parentCrud.addChildCrudListener(this);
		// this.parentAssociationProvider = parentAssociationProvider;
	}

	public abstract void init();

	@Override
	public void init(final Class<E> entityClass, String singularNoun, String pluralNoun)
	{
		super.init(entityClass, singularNoun, pluralNoun);
		postProcessChildCrud();
	}

	private void postProcessChildCrud()
	{
		// Child cruds don't have save/cancel buttons
		if (editorLayout != null)
		{
			editorLayout.setButtonsVisible(false);
		}

		// If the child was initialized during a row change cycle, then leave it
		// up to the parent to refresh the child, otherwise force a refresh now
		if (!parentCrud.isRowChanging())
		{
			// Simulate a parent row change in case one happened while the child
			// was lazy-not-loaded

			refresh(parentCrud.getCurrentEntity());
		}
	}

	@Override
	public void refresh(final P parentEntity)
	{
		this.parentEntity = parentEntity;

		setSearchText("", false);
		clearAdvancedSearch();

		// Refresh entities filtered by new parent entity
		refreshRows();

		// TODO: select selectedEntity
		// Try to select first entity
		if (currentEntity == null)
			editorLayout.showNoSelectionMessage();
		else
			selectRow(currentEntity);

	}

	/**
	 * Parent checks whether child is dirty before allowing a row change
	 */
	@Override
	public boolean isDirty()
	{
		if (editorLayout == null)
		{
			return false;
		}
		return editorLayout.isDirty() || isNewEntity;
	}

	@Override
	protected void newEntity()
	{
		parentCrud.setSearchEnabled(false);
		super.newEntity();
	}

	@Override
	public void cancel()
	{
		final boolean newEntity = this.isNewEntity;

		if (newEntity)
		{
			rowChanged(null, null);
		}
		else
		{
			super.cancel();
		}

		setDefaultState();
	}

	@Override
	public boolean save()
	{
		if (validateForm())
		{
			preSave(currentEntity);
			currentEntity = getDao().merge(currentEntity);
			associateParent(parentCrud.getCurrentEntity(), currentEntity);
			inSave = true;
			return true;
		}
		return false;
	};

	@Override
	boolean validateForm()
	{
		var formErrors = this.editorLayout.saveEdits(currentEntity);
		return formErrors.success;
	}

	@Override
	public void _postSave()
	{
		if (inSave)
		{
			refreshEntity();
			isNewEntity = false;
			refreshRows();
			setDefaultState();
			inSave = false;
		}
	}

	protected abstract void associateParent(final P parentEntity, final E entity);

	@Override
	public String getTitleText()
	{
		// No title text on children by default
		return null;
	}

	public ParentCrudView<P> getParentCrud()
	{
		return parentCrud;
	}

	public CrudEntity getParentEntity()
	{
		return parentEntity;
	}

	@Override
	public void addEventListener(final EventType eventType, final Long userId, final EventListener eventListener)
	{
		parentCrud.addEventListener(eventType, userId, eventListener);
	}

	@Override
	public void publishEvent(final EventType eventType, final Long userId, final Object entity)
	{
		parentCrud.publishEvent(eventType, userId, entity);
	}

	// public P getParentAssociation()
	// {
	// return parentAssociationProvider.getAssociation();
	// }

	@Override
	protected String getUniqueId()
	{
		return parentCrud.getClass().getSimpleName() + "-" + getClass().getSimpleName();
	}
}
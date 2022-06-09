/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.textfield.TextField;

import au.com.sutton.vaadin.crud.SearchableGrid.ActionProvider;
import au.com.sutton.vaadin.crud.SearchableGrid.AdvancedSearchLayoutProvider;
import au.com.sutton.vaadin.crud.SearchableGrid.ResetAdvancedSearchProvider;
import au.com.sutton.vaadin.crud.ifc.CrudEntity;
import au.com.sutton.vaadin.crud.ifc.DataProviderIfc;

public class SearchableGridBuilder<E extends CrudEntity>
{
	protected Class<E> entityClass;

	protected Integer pageSize;
	private String title;
	private String singularNoun;
	private String pluralNoun;
	private String uniqueId;
	AdvancedSearchLayoutProvider<E> advancedSearchProvider;
	private ResetAdvancedSearchProvider resetAdvancedSearchProvider;
	private TextField searchField;
	private boolean newAllowed = true;
	private ActionProvider<E> newAction;

	// private GetFilterBuilder<E> getFilterBuilder;

	private boolean refreshVisible = false;

	private ContextMenu actionMenu;

	DataProviderIfc<E> dataProvider;

	public SearchableGridBuilder(final Class<E> entityClass)
	{
		this.entityClass = entityClass;
	}

	public SearchableGrid<E> build()
	{

		if (dataProvider == null)
		{
			throw new RuntimeException("You must provide a DaoProvider");
		}
		return new SearchableGrid<E>(this);
	}

	public SearchableGridBuilder<E> setDaoDataProvider(DataProviderIfc<E> dataProvider)
	{
		this.dataProvider = dataProvider;
		return this;
	}

	public SearchableGridBuilder<E> setPageSize(final Integer pageSize)
	{
		this.pageSize = pageSize;
		return this;
	}

	public Integer getPageSize()
	{
		return pageSize;
	}

	public SearchableGridBuilder<E> setTitle(final String title)
	{
		this.title = title;
		return this;
	}

	public String getTitle()
	{
		return title;
	}

	public SearchableGridBuilder<E> setUniqueId(final String uniqueId)
	{
		this.uniqueId = uniqueId;
		return this;
	}

	public String getUniqueId()
	{
		return uniqueId;
	}

	public SearchableGridBuilder<E> setAdvancedSearchProvider(
			final AdvancedSearchLayoutProvider<E> advancedSearchProvider)
	{
		this.advancedSearchProvider = advancedSearchProvider;
		return this;
	}

	public AdvancedSearchLayoutProvider<E> getAdvancedSearchProvider()
	{
		return advancedSearchProvider;
	}

	public SearchableGridBuilder<E> setResetAdvancedSearch(
			final ResetAdvancedSearchProvider resetAdvancedSearchProvider)
	{
		this.resetAdvancedSearchProvider = resetAdvancedSearchProvider;
		return this;
	}

	public ResetAdvancedSearchProvider getResetAdvancedSearchProvider()
	{
		return resetAdvancedSearchProvider;
	}

	public SearchableGridBuilder<E> setSearchField(final TextField searchField)
	{
		this.searchField = searchField;
		return this;
	}

	public TextField getSearchField()
	{
		return searchField;
	}

	public SearchableGridBuilder<E> setNewAllowed(final boolean newAllowed)
	{
		this.newAllowed = newAllowed;
		return this;
	}

	public boolean isNewAllowed()
	{
		return newAllowed;
	}

	public SearchableGridBuilder<E> setRefreshVisible(final boolean refreshVisible)
	{
		this.refreshVisible = refreshVisible;
		return this;
	}

	public boolean isRefreshVisible()
	{
		return refreshVisible;
	}

	public SearchableGridBuilder<E> setNewAction(final ActionProvider<E> newAction)
	{
		this.newAction = newAction;
		return this;
	}

	public ActionProvider<E> getNewAction()
	{
		return newAction;
	}

	public SearchableGridBuilder<E> setActionMenu(ContextMenu actionMenu)
	{
		this.actionMenu = actionMenu;
		return this;

	}

	public ContextMenu getActionMenu()
	{
		return actionMenu;
	}

	public void setSingularNone(String noun)
	{
		this.singularNoun = noun;
	}

	public String getSingularNoun()
	{
		return this.singularNoun;
	}

	public void setPluralNone(String noun)
	{
		this.pluralNoun = noun;
	}

	public String getPluralNoun()
	{
		return this.pluralNoun;
	}

}
/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import java.util.Collection;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;

import au.com.sutton.vaadin.crud.ifc.CrudEntity;
import au.com.sutton.vaadin.crud.ifc.CrudRowChangeListener;
import au.com.sutton.vaadin.crud.ifc.DataProviderIfc;
import au.com.sutton.vaadin.crud.widgets.HLayout;
import au.com.sutton.vaadin.crud.widgets.HSpacer;
import au.com.sutton.vaadin.crud.widgets.VLayout;

public class SearchableGrid<E extends CrudEntity> extends VLayout
{

	private static final long serialVersionUID = 1L;

	private Grid<E> entityGrid = new Grid<>();
	private H4 titleLabel;
	private TextField searchField;
	private String searchFieldText = "";
	private AdvancedSearchLayout advancedSearchLayout;
	private ResetAdvancedSearchProvider resetAdvancedSearchProvider;
	private Button advancedSearchButton;
	private boolean advancedSearchOn = false;

	private Button clearButton = new Button(FontAwesome.Solid.TIMES.create());
	private Button searchButton = new Button(FontAwesome.Solid.SEARCH.create());
	private Button newButton;
	private boolean instantSearch = true;
	private int minSearchTextLength = 0;
	private Button actionButton;

	private Runnable postSearchAction;
	private boolean refreshingEntities;

	private DataProviderIfc<E> daoDataProvider;

	private GridLazyDataView<E> view;

	public SearchableGrid(SearchableGridBuilder<E> builder)
	{
		super("SearchableGrid");
		initLayout(builder);

		buildGrid(builder);

	}

	public SearchableGrid(SearchableGridBuilder<E> builder, HLayout titleLayout)
	{
		super("SearchableGrid");
		initLayoutWithTitle(titleLayout, builder);

		buildGrid(builder);
	}

	private void buildGrid(SearchableGridBuilder<E> builder)
	{
		daoDataProvider = builder.dataProvider;
		view = entityGrid.setItems(query ->
			{

				// add the filter to the query
				var q = new Query<E, String>(query.getOffset(), query.getLimit(), query.getSortOrders(), null,
						getSearchField().getValue());

				return daoDataProvider.fetchFromBackEnd(q);

			});
		view.setItemCountCallback(query ->
			{
				// add the filter to the query
				var q = new Query<E, String>(query.getOffset(), query.getLimit(), query.getSortOrders(), null,
						getSearchField().getValue());

				return daoDataProvider.sizeInBackEnd(q);

			});

		view.setIdentifierProvider(E::getId);
	}

	public static <E extends CrudEntity> SearchableGridBuilder<E> getBuilder(Class<E> entityClass)
	{
		return new SearchableGridBuilder<>(entityClass);
	}

	GridContextMenu<E> createActionMenu()
	{
		return entityGrid.addContextMenu();
	}

	public interface AdvancedSearchLayoutProvider<E extends CrudEntity>
	{
		public AdvancedSearchLayout getLayout();
	}

	public interface ResetAdvancedSearchProvider
	{
		public void reset();
	}

	public interface ActionProvider<E>
	{
		public void runAction();
	}

	private void initLayoutWithTitle(Component titleLayout, final SearchableGridBuilder<E> builder)
	{
		add(titleLayout);
		add(buildSearchBar(builder));
		add(entityGrid);
		expand(entityGrid);
	}

	private void initLayout(final SearchableGridBuilder<E> builder)
	{
		buildTitle(builder);
		add(buildSearchBar(builder));
		add(entityGrid);
		expand(entityGrid);
	}

	private void buildTitle(final SearchableGridBuilder<E> builder)
	{
		final String titleText = builder.getTitle();
		if (titleText != null && !titleText.isEmpty())
		{
			titleLabel = new H4(titleText);
			titleLabel.setId("Title");
			add(titleLabel);
		}
	}

	public H4 getTitleLabel()
	{
		return titleLabel;
	}

	private VLayout buildSearchBar(final SearchableGridBuilder<E> builder)
	{
		// Try to add a custom search field, but if none was provided then just
		// add a simple TextField
		searchField = builder.getSearchField();
		if (searchField == null)
		{
			searchField = new TextField();
			searchField.getElement().setProperty("title", "Enter a search filter");
		}

		searchField.setId("CrudSearchField");
		searchField.setPlaceholder("Search");

		searchField.focus();
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event ->
			{
				searchFieldText = searchField.getValue();
				if (instantSearch)
				{
					// Also refresh rows if search field is blank, effectively
					// ignoring the minimum search text length setting.
					if (searchFieldText.length() >= minSearchTextLength || searchFieldText.length() == 0)
					{
						refreshRows();
						// selectFirstRow();
						if (postSearchAction != null)
						{
							postSearchAction.run();
						}
					}
				}
			});

		searchButton.addClickListener(event ->
			{
				if (!searchField.isInvalid())
				{
					refreshRows();
					if (postSearchAction != null)
					{
						postSearchAction.run();
					}
				}
			});

		setButtonToolTip(searchButton, "Filter the list of %s".formatted(builder.getPluralNoun()));

		searchField.addKeyPressListener(Key.ENTER, (event) ->
			{
				if (!instantSearch)
				{
					searchButton.click();
				}
			});

		searchField.addKeyUpListener(Key.ESCAPE, (event) ->
			{
				searchField.clear();
			});

		VLayout searchBar = new VLayout("SearchBar");

		var searchLayout = new HLayout("SearchLayout");
		searchLayout.setWidth("100%");
		searchBar.add(searchLayout);

		clearButton.addClassName("search-clear");
		searchButton.addClassName("button-search");
		if (builder.isRefreshVisible())
		{
			final HSpacer spacerLabel = new HSpacer();
			spacerLabel.setSizeUndefined();
			final Button refreshButton = new Button(FontAwesome.Solid.RECYCLE.create());
			searchLayout.add(refreshButton, spacerLabel);
			refreshButton.addClickListener(listener -> refreshRows());
		}

		clearButton.addClickListener((e) ->
			{
				searchField.clear();
				refreshRows();
			});
		setButtonToolTip(clearButton, "Clear the search filter  (ESC)");

		final ActionProvider<E> newAction = builder.getNewAction();
		if (newAction != null && builder.isNewAllowed())
		{
			newButton = new Button("New %s".formatted(builder.getSingularNoun()));
			setButtonToolTip(newButton, "Create a new %s".formatted(builder.getSingularNoun()));

			newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
			newButton.addClickListener(listener -> newAction.runAction());
		}

		ContextMenu actionMenu = ParentCrudView.safe(builder, SearchableGridBuilder::getActionMenu);
		if (actionMenu != null)
		{
			actionButton = new Button(FontAwesome.Solid.ELLIPSIS_H.create());
			// actionButton.setStyleName(ValoTheme.BUTTON_QUIET);
			setButtonToolTip(actionButton, "Additional Actions");

			actionMenu.setTarget(actionButton);

			actionMenu.setOpenOnClick(true);

		}

		searchLayout
				.withComponent(clearButton)
				.withComponent(searchField)
				.withExpanded(searchField)
				.withComponent(searchButton);

		advancedSearchLayout = buildAdvancedSearch(builder, searchBar);
		if (advancedSearchLayout != null)
		{
			advancedSearchButton = buildAdvancedSearchButton();
			searchBar.add(advancedSearchLayout);
			searchLayout.add(advancedSearchButton);
		}

		if (actionButton != null)
		{
			searchLayout
					.withComponent(actionButton);
		}
		if (newButton != null)
		{
			searchLayout.withComponent(new HSpacer())
					.withComponent(newButton)
					.withComponent(new HSpacer());
		}

		return searchBar;

	}

	private AdvancedSearchLayout buildAdvancedSearch(final SearchableGridBuilder<E> builder, VLayout searchBar)
	{
		var advancedSearchLayout = ParentCrudView.safe(builder.advancedSearchProvider,
				(provider) -> provider.getLayout());
		if (advancedSearchLayout != null)
		{
			resetAdvancedSearchProvider = builder.getResetAdvancedSearchProvider();
			advancedSearchLayout.setVisible(false);

		}
		return advancedSearchLayout;
	}

	Button buildAdvancedSearchButton()
	{
		var advancedSearchButton = new Button(getAdvancedCaption());
		setButtonToolTip(advancedSearchButton, "Show advanced search options");

		advancedSearchButton.setWidth("60px");

		advancedSearchButton.addClickListener(event ->
			{
				advancedSearchOn = !advancedSearchOn;
				advancedSearchLayout.setVisible(advancedSearchOn);
				resetAdvancedSearch();

				if (!advancedSearchOn)
				{
					refreshRows();
					if (postSearchAction != null)
					{
						postSearchAction.run();
					}
				}

				if (!advancedSearchOn)
				{
					advancedSearchButton.setText(getAdvancedCaption());
					setButtonToolTip(advancedSearchButton, "Show advanced search options");
				}
				else
				{
					advancedSearchButton.setText(getBasicCaption());
					setButtonToolTip(advancedSearchButton, "Hide advanced search options");
				}
			});
		return advancedSearchButton;
	}

	private void setButtonToolTip(Button button, String description)
	{
		button.getElement().setProperty("title", description);
	}

	/**
	 * Show advanced search and, if lockAdvancedSearch is true, lock it into place
	 *
	 * @param lockAdvancedSearch lock advanced search into place
	 */
	public void showAdvancedSearch(boolean lockAdvancedSearch)
	{
		advancedSearchOn = true;
		advancedSearchLayout.setVisible(true);
		advancedSearchButton.setText(getBasicCaption());

		if (lockAdvancedSearch)
		{
			advancedSearchButton.setVisible(false);
		}
	}

	public void closeAdvancedSearch()
	{
		resetAdvancedSearch();
		advancedSearchOn = false;
		advancedSearchLayout.setVisible(false);
		advancedSearchButton.setText(getAdvancedCaption());
	}

	public void resetAdvancedSearch()
	{
		if (resetAdvancedSearchProvider != null)
		{
			resetAdvancedSearchProvider.reset();
		}
	}

	public boolean isAdvancedSearchOn()
	{
		return advancedSearchOn;
	}

	public void setSearchText(final String text, final boolean refresh)
	{
		if (!searchField.getValue().equals(text))
		{
			searchField.setValue(text);
			searchFieldText = text;
		}

		if (refresh)
		{
			refreshRows();
		}
	}

	public int getMinSearchTextLength()
	{
		return minSearchTextLength;
	}

	public void setMinSearchTextLength(int minSearchTextLength)
	{
		this.minSearchTextLength = minSearchTextLength;
	}

	/**
	 * Sets whether search should happen as text is typed into the search field. This does not apply to advanced search
	 * criteria.
	 *
	 * @param enabled set instant search
	 */
	public void setInstantSearch(final boolean enabled)
	{
		instantSearch = enabled;
		searchButton.setVisible(!instantSearch);
	}

	public void addSearchClickListener(final ComponentEventListener<ClickEvent<Button>> listener)
	{
		searchButton.addClickListener(listener);
	}

	public void searchClick()
	{
		searchButton.click();
	}

	public void refreshRows()
	{
		view.refreshAll();
	}

	public void refreshRow(final E row)
	{
		view.refreshItem(row);

		// // Don't try to reselect the row if the number of entities is greater
		// // than the default page size. This is a temporary fix to get around
		// // scenarios where there is a heavy query being run by the
		// // PagingProvider and the previously selected entity may be several
		// // pages down or may not even exist any more in the container.
		// // TODO: The best solution I can think of is to only scan the first x
		// // pages to see whether the existing row exists, otherwise just select
		// // the first row. Unfortunately caching happens several layers down in
		// // LazyList and the cache is private.
		// try
		// {
		// if (!selectedRows.isEmpty())
		// {
		// selectRow(selectedRows.iterator().next());
		// }
		// }
		// catch (IllegalArgumentException e)
		// {
		// // Entity doesn't exist in grid any more
		// selectFirstRow();
		// }
	}

	public void selectRow(final E entity)
	{
		try
		{
			entityGrid.select(entity);
		}
		catch (IllegalArgumentException e)
		{
			// The provided entity may be out of date compared to the one in the
			// grid, so try to select based on the persisted entity
			if (entity instanceof CrudEntity)
			{
				final E persistedEntity = daoDataProvider.getDao().tryById(((CrudEntity) entity).getId());
				entityGrid.select(persistedEntity);
			}
			else
			{
				throw e;
			}
		}
	}

	public E selectFirstRow()
	{
		return selectRowAtIndex(0);
	}

	public E selectRowAtIndex(int index)
	{

		E entity = getItemAtIndex(index);
		if (entity != null)
		{
			entityGrid.select(entity);
		}
		else
		{
			entityGrid.deselectAll();
		}

		return entity;
	}

	// returns the entity at [index] or null
	// if the index is out of bounds.
	public E getItemAtIndex(int index)
	{
		E entity = null;

		if (view.getItemCountEstimate() > index)
		{
			try
			{
				entity = view.getItem(index);
			}
			catch (IndexOutOfBoundsException e)
			{
				// the item may have been deleted
				// under us.
			}
		}
		return entity;
	}

	public void deselectAll()
	{
		entityGrid.deselectAll();
	}

	public GridSelectionModel<E> getSelectionModel()
	{
		return entityGrid.getSelectionModel();
	}

	public GridSelectionModel<E> setSelectionMode(SelectionMode selectionMode)
	{
		return entityGrid.setSelectionMode(selectionMode);
	}

	public Collection<E> getSelectedRows()
	{
		return entityGrid.getSelectedItems();
	}

	// returns the index of the first selected item.
	public int getSelectedIndex()
	{
		var items = entityGrid.getSelectedItems();

		var selected = items.stream().findFirst();
		int index = -1;
		if (selected.isPresent())
		{
			index = daoDataProvider.getIndexOf(selected.get(), entityGrid.getPageSize());
		}
		return index;
	}

	// returns the selected entity or null of no row is selected
	public E getSelectedRow()
	{
		var items = entityGrid.getSelectedItems();

		return items.stream().findFirst().orElse(null);
	}

	public void addItemClickListener(ComponentEventListener<ItemClickEvent<E>> listener)
	{
		entityGrid.addItemClickListener(listener);

	}

	/*
	 * We need to debounce the selection listener and the focus listener as we can get both events for the one action.
	 * So if the event is for the same target we ignore it.
	 */
	E lastSelectionTarget;
	boolean inRowChange = false;

	public void addRowChangeListener(CrudRowChangeListener<E> listener)
	{
		/// handle mouse click selection
		entityGrid.addSelectionListener(e ->
			{
				if (e.isFromClient())
				{
					var newSelection = e.getFirstSelectedItem().orElse(null);
					if (newSelection != lastSelectionTarget)
					{
						lastSelectionTarget = newSelection;
						rowChangeHandler(listener, newSelection, e.isFromClient(), false);
					}
				}

			});

		// handle keyboard navigation up and down the list.
		entityGrid.addCellFocusListener(
				e ->
					{
						if (e.isFromClient())
						{
							var newSelection = e.getItem().orElse(null);
							if (newSelection != lastSelectionTarget)
							{
								lastSelectionTarget = newSelection;
								rowChangeHandler(listener, newSelection, e.isFromClient(), true);
							}
						}

					});

	}

	private void rowChangeHandler(CrudRowChangeListener<E> listener, E newSelection, boolean isFromClient,
			boolean isFocus)
	{
		if (!inRowChange)
		{
			inRowChange = true;
			var current = getSelectedRow();

			if (isFromClient)
			{
				// If its a client action we need to check if the form is dirty and give
				// the user a chance to change thier mind.
				listener.allowRowChange(current, newSelection, new CrudRowChangeListener.Callback()
				{
					@Override
					public void allow()
					{
						listener.rowChanged(current, newSelection);
						if (isFocus)
						{
							// entityGrid.deselectAll();
							entityGrid.select(newSelection);
						}
						inRowChange = false;
					}

					@Override
					public void disallow()
					{
						// force selection back to the the original row.
						entityGrid.select(current);
						inRowChange = false;
					}

				});
			}
			else
			{
				listener.rowChanged(current, newSelection);
				inRowChange = false;
			}
		}

	}

	public void addThemeName(String style)
	{
		entityGrid.addThemeName(style);
	}

	public Grid<E> getGrid()
	{
		return entityGrid;
	}

	public TextField getSearchField()
	{
		return searchField;
	}

	public void setColumnReorderingAllowed(boolean columnReorderingAllowed)
	{
		entityGrid.setColumnReorderingAllowed(columnReorderingAllowed);
	}

	public boolean isColumnReorderingAllowed()
	{
		return entityGrid.isColumnReorderingAllowed();
	}

	protected String getAdvancedCaption()
	{
		return "More";
	}

	protected String getBasicCaption()
	{
		return "Less";
	}

	public void sort(final List<GridSortOrder<E>> sort)
	{
		entityGrid.sort(sort);
	}

	public void setSearchEnabled(final boolean enabled)
	{
		searchField.setEnabled(enabled);
		searchButton.setEnabled(enabled);
		clearButton.setEnabled(enabled);
		if (advancedSearchButton != null)
		{
			advancedSearchButton.setEnabled(enabled);
		}
	}

	public void setNewEnabled(final boolean enabled)
	{
		if (newButton != null)
		{
			newButton.setEnabled(enabled);
		}
	}

	public void setPostSearchAction(final Runnable postSearchAction)
	{
		this.postSearchAction = postSearchAction;
	}

	public boolean isRefreshingEntities()
	{
		return refreshingEntities;
	}

}
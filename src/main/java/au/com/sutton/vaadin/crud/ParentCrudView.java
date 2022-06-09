/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;

import au.com.sutton.vaadin.crud.editor.CrudFieldBinder;
import au.com.sutton.vaadin.crud.editor.EditorFormLayout;
import au.com.sutton.vaadin.crud.editor.EditorFormLayout.BindFieldProvider;
import au.com.sutton.vaadin.crud.editor.EditorFormLayout.EditFormBuildProvider;
import au.com.sutton.vaadin.crud.editor.EditorLayout;
import au.com.sutton.vaadin.crud.editor.FormErrorLayout;
import au.com.sutton.vaadin.crud.editor.PopupEditorLayout;
import au.com.sutton.vaadin.crud.ifc.CrudEntity;
import au.com.sutton.vaadin.crud.ifc.CrudPanelPair;
import au.com.sutton.vaadin.crud.ifc.CrudRowChangeListener;
import au.com.sutton.vaadin.crud.ifc.DataProviderIfc;
import au.com.sutton.vaadin.crud.ifc.EventListener;
import au.com.sutton.vaadin.crud.ifc.GenericDao;
import au.com.sutton.vaadin.crud.ifc.RowChangedListener;
import au.com.sutton.vaadin.crud.ifc.SharedChildCrudListener;
import au.com.sutton.vaadin.crud.widgets.VLayout;
import au.com.sutton.vaadin.dialogs.DialogConfirm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;

public abstract class ParentCrudView<E extends CrudEntity> extends VLayout
		implements CrudRowChangeListener<E>, BindFieldProvider<E>, EditFormBuildProvider<E>
{

	private static final long serialVersionUID = 1L;

	protected Class<E> entityClass;
	protected String singularNoun;
	protected String pluralNoun;

	protected E currentEntity;

	// used when new is cancelled so we can get back to the
	// prior selected entity.
	protected E priorEntity;

	// Used to track whether we are editing a new entity
	protected boolean isNewEntity;
	private boolean rowChanging;

	// Layout
	private Component selectionLayout;

	private CrudDisplayMode displayMode = CrudDisplayMode.HORIZONTAL;
	protected CrudType crudType = CrudType.FULL;
	private boolean useNewEntityPopup;
	private CrudPanelPair splitPanel;
	private SearchableGrid<E> searchableGrid;
	protected EditorLayout<E> editorLayout;
	private Float initialSplitSize = null;

	// Listeners
	private Set<RowChangedListener<E>> rowChangedListeners = new LinkedHashSet<>();
	private Set<SharedChildCrudListener<E>> sharedChildCrudListeners = new LinkedHashSet<>();

	// Events
	private EventDistributor eventDistributor = new EventDistributor();

	Logger logger = LogManager.getLogger();

	protected ParentCrudView()
	{
		super("ParentCrudView");
		addClassName("container");
	}

	public ParentCrudView(final CrudDisplayMode displayMode, final CrudType crudType)
	{
		super("ParentCrudView");
		addClassName("container");
		this.displayMode = displayMode;
		this.crudType = crudType;
	}

	public void init(final Class<E> entityClass, String singularNoun, String pluralNoun)
	{
		try
		{
			this.entityClass = entityClass;
			this.singularNoun = singularNoun;
			this.pluralNoun = pluralNoun;

			initSearchableGrid();
			initLayout();

			// A refresh and select happens in
			// SharedChildCrudView.postProcessChildCrud
			// TODO: Think of a cleaner way to select the first row only in
			// ParentCrudView once ChildCrudView is no longer used.
			if (!(this instanceof SharedChildCrudView))
			{
				var e = selectFirstRow();
				if (e == null)
				{
					editorLayout.showNoSelectionMessage();
				}
			}
		}
		catch (Exception e)
		{
			showErrorWindow(e);
		}
	}

	/**
	 * show a severe, unrecoverable error
	 * @param e
	 */
	protected abstract void showErrorWindow(Exception e);

	private void initSearchableGrid()
	{
		final SearchableGridBuilder<E> gridBuilder = SearchableGrid.getBuilder(entityClass);

		gridBuilder.setTitle(pluralNoun);
		gridBuilder.setAdvancedSearchProvider(() ->
			{
				return getAdvancedSearchLayout();
			});
		gridBuilder.setResetAdvancedSearch(this::resetAdvancedSearch);
		gridBuilder.setSearchField(getCustomSearchField());
		gridBuilder.setNewAction(() -> newClicked());
		gridBuilder.setActionMenu(getActionMenu());
		gridBuilder.setUniqueId(getUniqueId());
		gridBuilder.setNewAllowed(isNewAllowed());
		gridBuilder.setSingularNone(singularNoun);
		gridBuilder.setPluralNone(pluralNoun);
		gridBuilder
				.setDaoDataProvider(getDataProvider());

		searchableGrid = gridBuilder.build();
		searchableGrid.setSizeFull();

		searchableGrid.addRowChangeListener(this);

		searchableGrid.setColumnReorderingAllowed(true);
		getEntityGrid().setSelectionMode(SelectionMode.SINGLE);
		addColumnsToGrid(searchableGrid.getGrid());
		setSizeFull();
	}

	protected abstract DataProviderIfc<E> getDataProvider();

	boolean inSelectionListener = false;

	private ContextMenu getActionMenu()
	{
		ContextMenu menu = null;
		//DaoMember.isAllowedDelete(MemberSession.getMemberFromSession(), this.getClass())
		if (isDeleteAllowed())
		{
			menu = new ContextMenu();
			menu.addItem("Delete", (e) ->
				{
					deleteClicked();
				});

			buildActionMenu(menu);
		}
		return menu;
	}

	protected boolean isDeleteAllowed()
	{
		return true;
	}

	private AdvancedSearchLayout getAdvancedSearchLayout()
	{
		final var layout = new AdvancedSearchLayout();

		final var heading = new H4("Advanced Search");
		layout.add(heading, AdvancedSearchLayout.DEFAULT_COLS);

		buildAdvancedSearchLayout(layout);

		return layout.getChildren().count() == 1 ? null : layout;
	}

	protected abstract void addColumnsToGrid(Grid<E> grid);

	private void initLayout()
	{
		this.setSizeFull();

		splitPanel = displayMode.getSplitPair(getUniqueId(), initialSplitSize);
		add(splitPanel.getPanel());

		selectionLayout = createSelectionLayout(searchableGrid);

		editorLayout = new EditorLayout<E>(this, this, singularNoun);
		editorLayout.setSizeFull();
		editorLayout.setPrimaryLabel(getPrimaryButtonLabel());
		editorLayout.setSecondaryLabel(getSecondaryButtonLabel());
		editorLayout.setAllowEdit(isEditAllowed() || isNewAllowed());
		editorLayout.addPrimaryClickListener(e -> saveClicked());
		editorLayout.addSecondaryClickListener(e -> cancelClicked());

		splitPanel.setComponents(selectionLayout, editorLayout);

		if (crudType == CrudType.LIST)
		{
			setSplitPosition(100, true);
			editorLayout.setVisible(false);
		}
		else if (crudType == CrudType.EDITOR)
		{
			setSplitPosition(0, true);
		}

		if (crudType != CrudType.EDITOR)
		{
			if (displayMode == CrudDisplayMode.HORIZONTAL)
			{
				splitPanel.setMinSplitPosition(40, Unit.PIXELS);
			}
			else if (displayMode == CrudDisplayMode.VERTICAL)
			{
				splitPanel.setMinSplitPosition(70, Unit.PIXELS);
				splitPanel.setMaxSplitPosition(95, Unit.PERCENTAGE);
			}
		}
	}

	/**
	 * Overload this method to change the label of the secondary button on the edit panel. Defaults to Cancel.
	 * 
	 * @return
	 */
	protected String getSecondaryButtonLabel()
	{
		return "Cancel";
	}

	/**
	 * Overload this method to change the label of the primary button on the edit panel. Defaults to Save.
	 * 
	 * @return
	 */
	protected String getPrimaryButtonLabel()
	{
		return "Save";
	}

	@Override
	public abstract void bindFields(CrudFieldBinder<E> binder);

	protected CrudFieldBinder<E> getBinder()
	{
		return editorLayout.getBinder();
	}

	@Override
	public abstract void buildEditForm(EditorFormLayout<E> layout);

	protected Component createSelectionLayout(final SearchableGrid<E> searchableGrid)
	{
		VLayout selectionLayout = new VLayout("selectionLayout");
		selectionLayout.add(searchableGrid);
		selectionLayout.expand(searchableGrid);
		selectionLayout.setSizeFull();

		return selectionLayout;
	}

	protected String getTitleText()
	{
		var title = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(this.getClass().getSimpleName()), ' ');

		/// Strip view of the name.
		if (title.endsWith("View"))
		{
			title = title.substring(0, title.length() - 4);
		}
		return title;
	}

	public boolean isDirty()
	{
		boolean dirtyChild = false;
		for (SharedChildCrudListener<E> commitListener : sharedChildCrudListeners)
		{
			if (commitListener.isDirty())
			{
				dirtyChild = true;
				break;
			}
		}

		return dirtyChild || editorLayout.isDirty() || isNewEntity;
	}

	@Override
	public void allowRowChange(E current, E selected, Callback callback)
	{
		// If the entity is new then rowChanged gets called manually
		// with the new entity object
		if (isNewEntity)
		{
			requestDiscard(callback);
			return;
		}

		if (isDirty())
		{
			requestDiscard(callback);
			return;
		}

		callback.allow();

	}

	private void requestDiscard(Callback callback)
	{
		DialogConfirm dialog = new DialogConfirm("Discard changes?",
				"You have unsaved changes for this record. Continuing will result in those changes being discarded.",
				(ok) ->
					{
						cancel();
						sharedChildCrudListeners.forEach(child -> child.cancel());
						setDefaultState();
						callback.allow();

					},
				() ->
					{
						callback.disallow();
					});

		dialog.setOkCaption("Discard");
	}

	@Override
	public void rowChanged(E existingEntity, E selectedEntity)
	{
		rowChanging = true;
		if (crudType != CrudType.LIST)
		{
			if (selectedEntity != null)
			{
				editorLayout.showEditor(isNewEntity);
			}
			else
			{
				editorLayout.showNoSelectionMessage();
			}
		}

		this.currentEntity = selectedEntity;

		if (currentEntity != null)
		{
			editorLayout.startEdit(currentEntity);
		}

		sharedChildCrudListeners.forEach(listener -> listener.refresh(currentEntity));

		notifyRowChangedListeners(currentEntity);

		afterRowChanged(currentEntity);
		rowChanging = false;
	}

	/**
	 * Over load this method to be informed each time a row changes. [entity] will be null if the row change resulted in
	 * no entity being selected. Note that when no entity is selected then the normal editorLayout won't be displayed.
	 * 
	 * @param entity
	 */
	protected void afterRowChanged(final CrudEntity entity)
	{

	}

	public void refreshChildren()
	{
		sharedChildCrudListeners.forEach(listener -> listener.refresh(currentEntity));
	}

	protected void notifyRowChangedListeners(E entity)
	{
		rowChangedListeners.forEach(listener -> listener.rowChanged(entity));
	}

	public void setInitialSplitPosition(final float initialSplitSize, final Unit initialSplitUnit)
	{
		if (editorLayout != null)
		{
			throw new IllegalStateException("You must call setInitialSplitPosition before init()");
		}
		this.initialSplitSize = initialSplitSize;

	}

	public void setSplitPosition(final int position)
	{
		setSplitPosition(position, false);
	}

	public void setSplitPosition(final int position, final boolean locked)
	{
		splitPanel.setSplitterPosition(position);
		splitPanel.setLocked(locked);
	}

	public void addRowChangedListener(RowChangedListener<E> listener)
	{
		rowChangedListeners.add(listener);
	}

	public void addChildCrudListener(SharedChildCrudListener<E> listener)
	{
		sharedChildCrudListeners.add(listener);
	}

	public void setInstantSearch(final boolean enabled)
	{
		searchableGrid.setInstantSearch(enabled);
	}

	public void showAdvancedSearch(boolean lockAdvancedSearch)
	{
		searchableGrid.showAdvancedSearch(lockAdvancedSearch);
	}

	protected void clearAdvancedSearch()
	{
		// searchableGrid.clearAdvancedSearch();
	}

	public void closeAdvancedSearch()
	{
		searchableGrid.closeAdvancedSearch();
	}

	/**
	 * Gets the current entity object being used by the crud. Note the object will be unmanaged. You should probably use
	 * {@link #getSelected()} instead.
	 *
	 * @return the current entity
	 */
	public E getCurrentEntity()
	{
		return currentEntity;
	}

	public abstract boolean isEditAllowed();

	public abstract boolean isNewAllowed();

	public void newClicked()
	{

		if (isDirty())
		{
			showNotificationError("Save your changes first.");
			return;
		}

		priorEntity = currentEntity;
		if (useNewEntityPopup)
		{
			new PopupEditorLayout<E>(ParentCrudView.this, ParentCrudView.this, singularNoun).show(currentEntity);
		}
		newEntity();
		// editorLayout.setButtonsEnabled(true);
	}

	/**
	 * show a message to the user about something they did wrong.
	 * @param string
	 */
	protected abstract void showNotificationError(String string);

	protected void newEntity()
	{
		setNewEnabled(false);

		if (isAdvancedSearchOn())
		{
			closeAdvancedSearch();
		}

		final E newEntity = preNew(currentEntity, createNewEntity());

		rowChanged(priorEntity, newEntity);
		postNew(newEntity);
		searchableGrid.deselectAll();
		setSearchEnabled(false);
	}

	private E createNewEntity()
	{
		try
		{
			currentEntity = entityClass.getConstructor().newInstance();
			isNewEntity = true;
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException
				| NoSuchMethodException | SecurityException e)
		{
			showErrorWindow(e);
		}

		return currentEntity;
	}

	/**
	 * If add any newEntity related entities to newEntity you must first detach them using EntityManagerProvider.detach
	 * Otherwise when the transaction commits we will get an error. MemberView.preNew has an example.
	 */
	protected E preNew(E previousEntity, E newEntity)
	{
		return newEntity;
	}

	protected void postNew(E newEntity)
	{
	}

	/**
	 * Initiate a delete of the selected entity. The subclass is responsible for implementing a way to initiate a
	 * delete.
	 */
	public void deleteClicked()
	{
		if (currentEntity == null)
		{
			showNotificationError("No %s selected to delete.".formatted(pluralNoun));
			return;
		}
		try
		{
			preDeleteValidation(currentEntity);
			DialogConfirm dialog = new DialogConfirm("Confirm deletion",
					"Are you sure you want to delete '" + currentEntity.getName() + "'?", (ok) ->
						{

							delete(currentEntity);

						});
			dialog.setOkCaption("Delete");
		}
		catch (ValidationException e)
		{
			showNotificationError(e.getMessage());
		}

	}

	/**
	 * Check whether the entity can be deleted. If it can't then a ValidationException should be thrown.
	 */
	protected void preDeleteValidation(E entity) throws ValidationException
	{

	}

	private void delete(E entity)
	{
		try
		{
			final EntityManager em = getDao().getEntityManager();
			entity = em.getReference(entityClass, entity.getId());

			searchableGrid.getSelectedRows();
			int index = searchableGrid.getSelectedIndex();
			var nextItem = getItemToSelectPostDelete(index);

			deleteAction(entity);
			em.getTransaction().commit();
			em.getTransaction().begin();

			// searchableGrid.
			searchableGrid.refreshRows();
			searchableGrid.selectRow(nextItem);
			rowChanged(null, nextItem);

		}
		catch (RollbackException e)
		{

			var cause = safe(e.getCause(), Throwable::getCause);

			if (cause != null &&
					cause instanceof SQLIntegrityConstraintViolationException)
			{
				// Cannot delete or update a parent row: a foreign key constraint
				if (((SQLIntegrityConstraintViolationException) cause).getErrorCode() == 1451)
				{
					showNotificationError("This %s is in use, so can't be deleted."
							.formatted(entity.getClass().getSimpleName()));
				}

			}
		}
		catch (Exception e)
		{
			showErrorWindow(e);
		}
	}

	public static <E, R> R safe(E object, Function<E, R> method)
	{
		if (object == null)
			return null;
		else
			return method.apply(object);
	}

	private E getItemToSelectPostDelete(int index)
	{
		var nextItem = searchableGrid.getItemAtIndex(index + 1);

		if (nextItem == null)
		{
			nextItem = searchableGrid.getItemAtIndex(index - 1);
		}
		return nextItem;
	}

	/**
	 * Override this method if an alternate action should be taken when deleting an entity, eg. inactivate instead of
	 * remove from the database
	 *
	 * @param entity the entity to be deleted
	 */
	public void deleteAction(E entity)
	{
		getDao().remove(entity);
	}

	public void saveClicked()
	{
		if (!confirmSave())
		{

			return;
		}

		for (var listener : sharedChildCrudListeners)
		{
			if (!listener.confirmSave())
			{
				return;
			}
		}

		if (save())
		{
			Notification.show("Changes Saved");
		}
	}

	public interface SaveProvider
	{
		public boolean save();
	}

	public void cancelClicked()
	{
		if (isDirty())
		{
			final var dialog = new DialogConfirm("Unsaved changes",
					"Continuing will discarded any edits you have made.", (ok) ->
						{
							doCancel();
							Notification.show("Changes Discarded");
						},
					() -> editorLayout.cancelComplete()

			);
			dialog.setOkCaption("Discard");
		}
		else
			doCancel();

	}

	private void doCancel()
	{
		final boolean newEntity = this.isNewEntity;

		cancel();
		if (newEntity)
		{
			this.isNewEntity = false;
			// no need to cancel the edit
			// as a row change will reset the editor.
			rowChanged(null, priorEntity);
			searchableGrid.selectRow(priorEntity);
			priorEntity = null;
		}
		else
		{
			sharedChildCrudListeners.forEach(listener -> listener.cancel());
		}
		setDefaultState();
	}

	/**
	 * Override this method to intercept the save process after clicking the save button. Return true if you would like
	 * the save action to proceed otherwise return false if you want to halt the save process. When suppressing the
	 * action you should display a notification as to why you suppressed it.
	 *
	 * @return whether to continue saving
	 */
	public boolean confirmSave()
	{
		return true;
	}

	/**
	 * An opportunity to modify the entity being saved
	 *
	 * @param entity
	 * @throws Exception
	 */
	protected void preSave(E entity)
	{
	}

	public boolean save()
	{
		boolean saved = false;

		var dao = getDao();
		EntityTransaction transaction = dao.getTransaction();
		try
		{

			if (!validateForm())
			{
				return saved;
			}

			preSave(currentEntity);

			if (isNewEntity)
			{
				dao.persist(currentEntity);
			}
			else
			{
				currentEntity = dao.merge(currentEntity);
			}

			getDao().flush();
			// refreshEntity();

			for (var commitListener : sharedChildCrudListeners)
			{
				if (commitListener.isDirty())
				{
					commitListener.save();
				}
			}

			getDao().flush();
			_postSave(currentEntity);
			sharedChildCrudListeners.forEach(listener -> listener._postSave());
			searchableGrid.selectRow(currentEntity);

			saved = true;
			getDao().commitAndContinue();
			// editorLayout.setButtonsEnabled(false);

		}
		catch (PersistenceException e)
		{
			logger.error(e, e);
			showNotificationError("There was an error saving, check the data you have entered and try again.");
			transaction.rollback();
			transaction.begin();
		}
		catch (Exception e)
		{
			showErrorWindow(e);
			transaction.rollback();
			transaction.begin();
		}

		return saved;
	}

	boolean validateForm()
	{
		var formErrors = this.editorLayout.saveEdits(currentEntity);

		//		sharedChildCrudListeners.forEach(listener ->
		//			{
		//				if (listener.isDirty())
		//				{
		//					final FormValidationResults childValidationErrors = listener.validateForm(currentEntity);
		//					if (childValidationErrors != null)
		//					{
		//						formErrors.addAllErrors(childValidationErrors.formErrors);
		//					}
		//				}
		//			});

		if (!formErrors.hasFormErrors())
		{
			var formErrorLayout = new FormErrorLayout(formErrors);
			editorLayout.add(formErrorLayout);
		}

		return formErrors.success;
	}

	private void _postSave(E currentEntity)
	{
		refreshEntity();
		isNewEntity = false;
		searchableGrid.refreshRows();
		setDefaultState();
		postSave(currentEntity);
	}

	/*
	 * Overload this method to do post save activities within the transaction
	 */
	protected void postSave(E currentEntity)
	{

	}

	void cancel()
	{
		// The user is discarding any edits so
		// restart the editor with the unadulterated
		// entity.
		editorLayout.startEdit(currentEntity);
		isNewEntity = false;
		// editorLayout.setButtonsEnabled(false);

	}

	// After an entity has been saved, reload it from the database to ensure
	// that we have an up to date copy following any changes from computed
	// columns, triggers, etc. When a new entity is persisted, lazy fields are
	// initialised with empty collections and therefore stop being lazy. Upon
	// refreshing the entity in this state, all lazy fields are also eagerly
	// refreshed. For this reason the entity is detached and reloaded with unset
	// lazy fields.
	protected E refreshEntity()
	{
		if (isNewEntity)
		{
			currentEntity = getDao().tryById(currentEntity.getId());
		}
		getDao().refresh(currentEntity);
		editorLayout.startEdit(currentEntity);

		// editorLayout.setButtonsEnabled(false);

		return currentEntity;
	}

	protected abstract GenericDao<E> getDao();

	protected void setDefaultState()
	{
		setNewEnabled(true);
		setSearchEnabled(true);
	}

	public void setNewEnabled(final boolean enabled)
	{
		searchableGrid.setNewEnabled(enabled);
	}

	public void setSearchEnabled(final boolean enabled)
	{
		searchableGrid.setSearchEnabled(enabled);
	}

	public Grid<E> getEntityGrid()
	{
		return searchableGrid.getGrid();
	}

	protected Component getLeftLayout()
	{
		return selectionLayout;
	}

	public int getMinSearchTextLength()
	{
		return searchableGrid.getMinSearchTextLength();
	}

	public void setMinSearchTextLength(int minSearchTextLength)
	{
		searchableGrid.setMinSearchTextLength(minSearchTextLength);
	}

	public boolean isNewEntity()
	{
		return isNewEntity;
	}

	protected boolean isAdvancedSearchOn()
	{
		return searchableGrid.isAdvancedSearchOn();
	}

	public void selectRow(final E entity)
	{
		try
		{
			searchableGrid.selectRow(entity);
		}
		catch (IllegalArgumentException e)
		{
			// The provided entity may be out of date compared to the one in the
			// grid, so try to select based on the persisted entity
			final E persistedEntity = getDao().tryById(entity.getId());
			searchableGrid.selectRow(persistedEntity);
		}
	}

	/**
	 * Read the entity from the db (or from JPA's cache) and return it. This ensures that any changes to this, or
	 * associated entities, that have occurred are present. Note that a managed entity is returned.
	 *
	 * @return the selected entity
	 */
	public E getSelected()
	{
		if (currentEntity == null || isNewEntity)
		{
			return currentEntity;
		}
		else
		{
			return getDao().tryById(currentEntity.getId());
		}
	}

	//
	// public String text
	// {
	// return searchableGrid.text;
	// }
	//
	public void setSearchText(final String text, final boolean refresh)
	{
		searchableGrid.setSearchText(text, refresh);
	}

	public TextField getSearchField()
	{
		return searchableGrid.getSearchField();
	}

	public void refreshRows()
	{
		searchableGrid.refreshRows();
		editorLayout.showNoSelectionMessage();
	}

	public void refreshRow(final E entity)
	{
		searchableGrid.refreshRow(entity);
	}

	public E selectFirstRow()
	{
		var e = searchableGrid.selectFirstRow();
		rowChanged(priorEntity, e);
		return e;
	}

	public void setPostSearchAction(final Runnable postSearchAction)
	{
		searchableGrid.setPostSearchAction(postSearchAction);
	}

	public void addSearchClickListener(final ComponentEventListener<ClickEvent<Button>> listener)
	{
		searchableGrid.addSearchClickListener(listener);
	}

	public void searchClick()
	{
		searchableGrid.searchClick();
	}

	public void addEventListener(final EventType eventType, final Long userId, final EventListener eventListener)
	{
		eventDistributor.addEventListener(eventType, userId, eventListener);
	}

	public void addEventListeners(final List<? extends EventType> eventTypes, final Long userId,
			final EventListener eventListener)
	{
		eventDistributor.addEventListeners(eventTypes, userId, eventListener);
	}

	public void publishEvent(final EventType eventType, final Long userId, final Object entity)
	{
		eventDistributor.publishEvent(eventType, userId, entity);
	}

	/**
	 * Override this method to set a custom search field
	 *
	 * @return the custom search field
	 */
	public TextField getCustomSearchField()
	{
		return null;
	}

	/**
	 * Override this method to set an advanced search layout by add fields to the passed layout.
	 * 
	 * You need to include some mechanism that calls refreshRows() when 
	 * you want to have the filters applied. Add an 'Search' button or value change listeners:
	 * 
	 * ```java
	 * showDeactivatedSearchField.addValueChangeListener((v) ->
	 * 	{
	 * 		refreshRows();
	 * 	});
	 * 	```
	 *
	 * @return the advanced search layout
	 */
	public void buildAdvancedSearchLayout(AdvancedSearchLayout layout)
	{
	}

	/**
	 * Override this method to define default values for advanced search fields
	 */
	public void resetAdvancedSearch()
	{
	}

	/**
	 * Override this method to add custom actions items to the contextMenu.
	 */
	protected void buildActionMenu(ContextMenu contextMenu)
	{
	}

	public void setUseNewEntityPopup(final boolean useNewEntityPopup)
	{
		this.useNewEntityPopup = useNewEntityPopup;
	}

	public boolean isUseNewEntityPopup()
	{
		return useNewEntityPopup;
	}

	protected String getUniqueId()
	{
		return getClass().getSimpleName();
	}

	public boolean isRefreshingEntities()
	{
		return searchableGrid.isRefreshingEntities();
	}

	public boolean isRowChanging()
	{
		return rowChanging;
	}

}

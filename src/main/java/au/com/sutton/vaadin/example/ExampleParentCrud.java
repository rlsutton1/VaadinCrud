/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.example;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;

import au.com.sutton.vaadin.crud.AdvancedSearchLayout;
import au.com.sutton.vaadin.crud.CrudDisplayMode;
import au.com.sutton.vaadin.crud.CrudType;
import au.com.sutton.vaadin.crud.ParentCrudView;
import au.com.sutton.vaadin.crud.editor.CrudFieldBinder;
import au.com.sutton.vaadin.crud.editor.EditorFormLayout;
import au.com.sutton.vaadin.crud.ifc.DataProviderIfc;
import au.com.sutton.vaadin.crud.ifc.GenericDao;

public class ExampleParentCrud extends ParentCrudView<ExampleEntity> implements ExampleQueryFilterBuilder<ExampleEntity>
{
	private static final long serialVersionUID = 1L;
	public static String SINGULAR_NOUN = "CLI Log";
	public static String PLURARL_NOUN = "CLI Logs";

	public ExampleParentCrud()
	{
		super(CrudDisplayMode.VERTICAL, CrudType.LIST);

		init(ExampleEntity.class, SINGULAR_NOUN, PLURARL_NOUN);

		setSizeFull();
	}

	DatePicker onDateFilter;
	private TextField nameField;

	@Override
	protected void addColumnsToGrid(Grid<ExampleEntity> grid)
	{

		grid.addColumn(ExampleEntity::getName).setHeader("Name");

	}

	@Override
	public void buildAdvancedSearchLayout(AdvancedSearchLayout layout)
	{
		onDateFilter = new DatePicker("On Date");
		onDateFilter.addValueChangeListener((v) ->
			{
				refreshRows();
			});

		layout.add(onDateFilter);
	}

	@Override
	public void buildEditForm(EditorFormLayout<ExampleEntity> layout)
	{

		nameField = new TextField("Name");
		layout.add(nameField);

	}

	@Override
	public void bindFields(CrudFieldBinder<ExampleEntity> fieldGroup)
	{
		fieldGroup.forField(this.nameField)
				.bind(ExampleEntity::getName, ExampleEntity::setName);

	}

	@Override
	protected GenericDao<ExampleEntity> getDao()
	{
		return new ExampleDao();
	}

	@Override
	public void addFilters(ExampleFakeQuery<ExampleEntity> jpaQuery, Query<ExampleEntity, String> query)
	{
		// add any extra required filters to jpaQuery
	}

	@Override
	protected void showErrorWindow(Exception e)
	{
		Notification.show(e.getMessage());

	}

	@Override
	protected DataProviderIfc<ExampleEntity> getDaoDataProvider()
	{
		return new ExampleCrudDataProvider<ExampleEntity>(getDao(), this);
	}

	@Override
	public boolean isEditAllowed()
	{
		return false;
	}

	@Override
	public boolean isNewAllowed()
	{
		return false;
	}

	@Override
	protected void showNotificationError(String string)
	{
		Notification.show(string);
	}

}

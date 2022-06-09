/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.editor;

import au.com.sutton.vaadin.crud.editor.EditorFormLayout.BindFieldProvider;
import au.com.sutton.vaadin.crud.editor.EditorFormLayout.EditFormBuildProvider;
import au.com.sutton.vaadin.crud.ifc.CrudEntity;

public class PopupEditorLayout<E extends CrudEntity>
{
	final EditorLayout<E> editorLayout;

	public PopupEditorLayout(BindFieldProvider<E> bindProvider, EditFormBuildProvider<E> buildProvider,
			String singularNoun)
	{
		editorLayout = new EditorLayout<E>(bindProvider, buildProvider, singularNoun);
	}

	public void show(E entity)
	{
		// DialogForm dialog;

		editorLayout.setSizeFull();
		// editorLayout.onSave(() -> dialog.close());
		// editorLayout.onCancel(() -> dialog.close());

		// DialogForm dialog = new DialogForm("Title", editorLayout)
		// {
		//
		// /**
		// *
		// */
		// private static final long serialVersionUID = 1L;
		//
		// @Override
		// protected Component buildContent()
		// {
		// return editorLayout;
		// }
		// };
		//
		// dialog.addDialogCloseActionListener(new ComponentEventListener<Dialog.DialogCloseActionEvent>()
		// {
		//
		// /**
		// *
		// */
		// private static final long serialVersionUID = 1L;
		//
		// @Override
		// public void onComponentEvent(DialogCloseActionEvent event)
		// {
		// editorLayout.setContent(null);
		// formLayout.setContent(navigationLayoutPreviousContent);
		// setDefaultState();
		// if (ParentCrudView.this instanceof SharedChildCrudView)
		// {
		// ((SharedChildCrudView) ParentCrudView.this).getParentCrud().setDefaultState();
		// }
		// }
		// });
		// dialog.show();

	}
}

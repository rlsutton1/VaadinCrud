/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.example;

import au.com.sutton.vaadin.crud.ifc.GenericDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ExampleDao implements GenericDao<ExampleEntity>
{

	@Override
	public void persist(ExampleEntity entity)
	{
		// EntityManagerProvider.getEntitymanager().persist(entity);

	}

	@Override
	public ExampleEntity tryById(Long id)
	{
		// // EntityManagerProvider.getEntitymanager().findById(id);
		return null;
	}

	@Override
	public void refresh(ExampleEntity entity)
	{
		//EntityManagerProvider.getEntitymanager().refresh(entity);
	}

	@Override
	public void commitAndContinue()
	{
		// EntityManagerProvider.getEntitymanager().getTransaction().commit();

		// EntityManagerProvider.getEntitymanager().getTransaction().begin();

	}

	@Override
	public void flush()
	{
		// EntityManagerProvider.getEntitymanager().flush();

	}

	@Override
	public ExampleEntity merge(ExampleEntity entity)
	{
		// EntityManagerProvider.getEntitymanager().merge(entity);
		return null;
	}

	@Override
	public EntityTransaction getTransaction()
	{
		// EntityManagerProvider.getEntitymanager().getTransaction();
		return null;
	}

	@Override
	public void remove(ExampleEntity entity)
	{
		// EntityManagerProvider.getEntitymanager().remove(entity);

	}

	@Override
	public EntityManager getEntityManager()
	{
		// EntityManagerProvider.getEntitymanager();
		return null;
	}

}

/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.ifc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public interface GenericDao<E>
{

	void persist(E currentEntity);

	E tryById(Long id);

	void refresh(E currentEntity);

	void commitAndContinue();

	void flush();

	E merge(E currentEntity);

	EntityTransaction getTransaction();

	void remove(E entity);

	EntityManager getEntityManager();

}

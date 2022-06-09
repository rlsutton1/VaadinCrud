/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud.ifc;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

public interface DataProviderIfc<E>
{

	Stream<E> fetchFromBackEnd(Query<E, String> q);

	int sizeInBackEnd(Query<E, String> q);

	GenericDao<E> getDao();

	int getIndexOf(E e, int pageSize);

}

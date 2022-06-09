/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.example;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import au.com.sutton.vaadin.crud.ifc.CrudEntity;
import au.com.sutton.vaadin.crud.ifc.DataProviderIfc;
import au.com.sutton.vaadin.crud.ifc.GenericDao;

public class ExampleCrudDataProvider<E extends CrudEntity>
		extends AbstractBackEndDataProvider<E, String> implements DataProviderIfc<E>
{
	private static final long serialVersionUID = 1L;

	public GenericDao<E> dao;
	ExampleQueryFilterBuilder<E> filterBuilder;

	/// records the last page that was requested
	int currentPage;

	// we cache the last three pages of entities
	// so we can get the index of an entity that
	// is currently being displayed
	Cache<Integer, Page> pageCache;

	/**
	 * Creates a lazy provider.
	 * 
	 * ```java
	 * new DaoDataProvider(new DaoOrganisation(), MemberSession.escalatedPriviges(), (q, query) -> {
	 * 		q.where(q.eq(Member_.organisation, organisation));
	 * });
	 * ```
	 * 
	 * @param dao
	 * @param memberSession
	 * @param getFilterBuilder
	 */
	public ExampleCrudDataProvider(GenericDao<E> dao, ExampleQueryFilterBuilder<E> filterBuilder)
	{
		this.dao = dao;
		this.filterBuilder = filterBuilder;
		buildCache();
	}

	@Override
	public int sizeInBackEnd(Query<E, String> query)
	{
		// build a JPA Query for the supplied query parameters.
		ExampleFakeQuery<E> jpaQuery = new ExampleFakeQuery<>();

		// allow the Crud Implementation to modify the query adding extra filters
		filterBuilder.addFilters(jpaQuery, query);

		return jpaQuery.getResults().size();
	}

	@Override
	public Stream<E> fetchFromBackEnd(Query<E, String> query)
	{
		// build a JPA Query for the supplied query parameters.
		ExampleFakeQuery<E> jpaQuery = new ExampleFakeQuery<>();

		// allow the Crud Implementation to modify the query adding extra filters
		filterBuilder.addFilters(jpaQuery, query);

		List<E> items = jpaQuery.getResults();
		pageCache.put(query.getPage(), new Page(items));
		return items.stream();

	}

	void buildCache()
	{
		pageCache = CacheBuilder.newBuilder().maximumSize(3).build();
	}

	class Page
	{
		List<Long> ids;

		Page(List<E> list)
		{
			this.ids = list.stream().map(e -> e.getId()).toList();
		}
	}

	// Looks for item in the page cache and returns its
	// index.
	@Override
	public int getIndexOf(E item, int pageSize)
	{
		var map = pageCache.asMap();

		long id = item.getId();
		for (var pageNo : map.keySet())
		{
			var page = map.get(pageNo);
			for (int i = 0; i < page.ids.size(); i++)
			{
				if (page.ids.get(i) == id)
				{
					return pageNo * pageSize + i;
				}
			}
		}
		return -1;
	}

	@Override
	public GenericDao<E> getDao()
	{
		return dao;
	}

}

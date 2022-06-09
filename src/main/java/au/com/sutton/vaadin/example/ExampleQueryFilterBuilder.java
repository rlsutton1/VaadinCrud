/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.example;

import com.vaadin.flow.data.provider.Query;

public interface ExampleQueryFilterBuilder<T>
{

	void addFilters(ExampleFakeQuery<T> jpaQuery, Query<T, String> query);

}

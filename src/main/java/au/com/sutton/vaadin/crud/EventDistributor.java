/* Copyright (C) OnePub IP Pty Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Brett Sutton <bsutton@onepub.dev>, Jan 2022
 */

package au.com.sutton.vaadin.crud;

import java.util.List;

import au.com.sutton.vaadin.crud.ifc.EventListener;

public class EventDistributor
{

	public void publishEvent(EventType eventType, Long userId, Object entity)
	{
		// TODO Auto-generated method stub

	}

	public void addEventListener(EventType eventType, Long userId, EventListener eventListener)
	{
		// TODO Auto-generated method stub

	}

	public void addEventListeners(List<? extends EventType> eventTypes, Long userId, EventListener eventListener)
	{
		// TODO Auto-generated method stub

	}

}

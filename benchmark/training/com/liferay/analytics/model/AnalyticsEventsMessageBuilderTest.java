/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.analytics.model;


import AnalyticsEventsMessage.Event;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jeyvison Nascimento
 * @author Marcellus Tavares
 */
public class AnalyticsEventsMessageBuilderTest {
    @Test
    public void testCreateEvent() {
        String expectedApplicationId = randomString();
        String expectedEventId = randomString();
        Map<String, String> expectedProperties = new HashMap<>();
        expectedProperties.put(randomString(), randomString());
        expectedProperties.put(randomString(), randomString());
        expectedProperties.put(randomString(), randomString());
        AnalyticsEventsMessage.Event actualEvent = createEvent(expectedApplicationId, expectedEventId, expectedProperties);
        assertEvent(expectedApplicationId, expectedEventId, expectedProperties, actualEvent);
    }

    @Test
    public void testCreateMessage() {
        // Context
        Map<String, String> expectedContext = createContext(randomLong(), randomString(), randomString(), randomLong());
        // Events
        List<AnalyticsEventsMessage.Event> expectedEvents = new ArrayList<>();
        String expectedApplicationId = randomString();
        String expectedEventId = randomString();
        Map<String, String> expectedProperties = new HashMap<>();
        expectedProperties.put(randomString(), randomString());
        expectedEvents.add(createEvent(expectedApplicationId, expectedEventId, expectedProperties));
        // Message
        String expectedDataSourceId = randomString();
        String expectedUserId = randomString();
        String expectedProtocolVersion = randomString();
        AnalyticsEventsMessage actualAnalyticsEventsMessage = createAnalyticsEventsMessage(expectedDataSourceId, expectedUserId, expectedContext, expectedEvents, expectedProtocolVersion);
        Assert.assertEquals(expectedDataSourceId, actualAnalyticsEventsMessage.getDataSourceId());
        Assert.assertEquals(expectedUserId, actualAnalyticsEventsMessage.getUserId());
        Assert.assertEquals(expectedContext, actualAnalyticsEventsMessage.getContext());
        List<AnalyticsEventsMessage.Event> actualEvents = actualAnalyticsEventsMessage.getEvents();
        Assert.assertEquals(expectedEvents.toString(), expectedEvents.size(), actualEvents.size());
        int i = 0;
        for (AnalyticsEventsMessage.Event expectedEvent : expectedEvents) {
            assertEvent(expectedEvent.getApplicationId(), expectedEvent.getEventId(), expectedEvent.getProperties(), actualEvents.get((i++)));
        }
        Assert.assertEquals(expectedProtocolVersion, actualAnalyticsEventsMessage.getProtocolVersion());
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateMessageWithoutEvent() {
        createAnalyticsEventsMessage(randomString(), randomString(), new HashMap<>(), new ArrayList<>(), randomString());
    }
}


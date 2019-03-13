/**
 * Copyright 2012 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.ambrose.service.impl;


import com.twitter.ambrose.model.Event;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author billg
 */
public class InMemoryStatsServiceTest {
    private InMemoryStatsService service;

    private final String workflowId = "id1";

    private final Event[] testEvents = new Event[]{ new Event.JobStartedEvent(new com.twitter.ambrose.model.DAGNode<com.twitter.ambrose.model.Job>("some name", null)), new Event.JobProgressEvent(new com.twitter.ambrose.model.DAGNode<com.twitter.ambrose.model.Job>("50", null)), new Event.JobFinishedEvent(new com.twitter.ambrose.model.DAGNode<com.twitter.ambrose.model.Job>("done", null)), new Event.JobProgressEvent(new com.twitter.ambrose.model.DAGNode<com.twitter.ambrose.model.Job>("75", null)), new Event.JobProgressEvent(new com.twitter.ambrose.model.DAGNode<com.twitter.ambrose.model.Job>("100", null)) };

    @Test
    public void testGetAllEvents() throws IOException {
        for (Event event : testEvents) {
            service.pushEvent(workflowId, event);
        }
        Collection<Event> events = service.getEventsSinceId(workflowId, (-1));
        Iterator<Event> foundEvents = events.iterator();
        Assert.assertTrue("No events returned", foundEvents.hasNext());
        for (Event sentEvent : testEvents) {
            assertEqualWorkflows(sentEvent, foundEvents.next());
        }
        Assert.assertFalse("Wrong number of events returned", foundEvents.hasNext());
    }

    @Test
    public void testGetEventsSince() throws IOException {
        for (Event event : testEvents) {
            service.pushEvent(workflowId, event);
        }
        // first, peek at the first eventId
        Collection<Event> allEvents = service.getEventsSinceId(workflowId, (-1));
        int sinceId = allEvents.iterator().next().getId();
        // get all events since the first
        Collection<Event> events = service.getEventsSinceId(workflowId, sinceId);
        Iterator<Event> foundEvents = events.iterator();
        Assert.assertEquals("Wrong number of events returned", ((testEvents.length) - 1), events.size());
        for (Event sentEvent : testEvents) {
            if ((sentEvent.getId()) <= sinceId) {
                continue;
            }
            assertEqualWorkflows(sentEvent, foundEvents.next());
        }
        Assert.assertFalse("Wrong number of events returned", foundEvents.hasNext());
    }

    @Test
    public void testGetEventsMax() throws IOException {
        for (Event event : testEvents) {
            service.pushEvent(workflowId, event);
        }
        int sinceId = -1;
        Event foundEvent;
        for (Event event : testEvents) {
            Iterator<Event> foundEvents = service.getEventsSinceId(workflowId, sinceId, 1).iterator();
            foundEvent = foundEvents.next();
            assertEqualWorkflows(event, foundEvent);
            Assert.assertFalse("Wrong number of events returned", foundEvents.hasNext());
            sinceId = foundEvent.getId();
        }
    }
}


/**
 * Copyright (C) 2007 The Guava Authors
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
package com.squareup.otto;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

import static ThreadEnforcer.ANY;


/**
 * Validate that {@link Bus} behaves carefully when listeners publish
 * their own events.
 *
 * @author Jesse Wilson
 */
public class ReentrantEventsTest {
    static final String FIRST = "one";

    static final Double SECOND = 2.0;

    final Bus bus = new Bus(ANY);

    @Test
    public void noReentrantEvents() {
        ReentrantEventsTest.ReentrantEventsHater hater = new ReentrantEventsTest.ReentrantEventsHater();
        bus.register(hater);
        bus.post(ReentrantEventsTest.FIRST);
        Assert.assertEquals("ReentrantEventHater expected 2 events", Arrays.<Object>asList(ReentrantEventsTest.FIRST, ReentrantEventsTest.SECOND), hater.eventsReceived);
    }

    public class ReentrantEventsHater {
        boolean ready = true;

        List<Object> eventsReceived = new ArrayList<Object>();

        @Subscribe
        public void listenForStrings(String event) {
            eventsReceived.add(event);
            ready = false;
            try {
                bus.post(ReentrantEventsTest.SECOND);
            } finally {
                ready = true;
            }
        }

        @Subscribe
        public void listenForDoubles(Double event) {
            Assert.assertTrue("I received an event when I wasn't ready!", ready);
            eventsReceived.add(event);
        }
    }

    @Test
    public void eventOrderingIsPredictable() {
        ReentrantEventsTest.EventProcessor processor = new ReentrantEventsTest.EventProcessor();
        bus.register(processor);
        ReentrantEventsTest.EventRecorder recorder = new ReentrantEventsTest.EventRecorder();
        bus.register(recorder);
        bus.post(ReentrantEventsTest.FIRST);
        Assert.assertEquals("EventRecorder expected events in order", Arrays.<Object>asList(ReentrantEventsTest.FIRST, ReentrantEventsTest.SECOND), recorder.eventsReceived);
    }

    public class EventProcessor {
        @Subscribe
        public void listenForStrings(String event) {
            bus.post(ReentrantEventsTest.SECOND);
        }
    }

    public class EventRecorder {
        List<Object> eventsReceived = new ArrayList<Object>();

        @Subscribe
        public void listenForEverything(Object event) {
            eventsReceived.add(event);
        }
    }
}


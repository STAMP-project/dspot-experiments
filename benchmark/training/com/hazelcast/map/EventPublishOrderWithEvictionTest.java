/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.map;


import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class EventPublishOrderWithEvictionTest extends HazelcastTestSupport {
    @Test
    public void testEntryEvictEventsEmitted_afterAddEvents() throws Exception {
        final int maxSize = 10;
        IMap<Integer, Integer> map = createMap(maxSize);
        EventPublishOrderWithEvictionTest.EventOrderAwareEntryListener entryListener = new EventPublishOrderWithEvictionTest.EventOrderAwareEntryListener();
        map.addEntryListener(entryListener, true);
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < maxSize; i++) {
                map.put(i, i);
            }
        }
        HazelcastTestSupport.sleepMillis(3456);
        assertEmittedEventsOrder(entryListener);
    }

    private static final class EventOrderAwareEntryListener extends EntryAdapter {
        private final List<EntryEvent> orderedEvents = new CopyOnWriteArrayList<EntryEvent>();

        @Override
        public void entryEvicted(EntryEvent event) {
            orderedEvents.add(event);
        }

        @Override
        public void entryAdded(EntryEvent event) {
            orderedEvents.add(event);
        }

        List<EntryEvent> getOrderedEvents() {
            return orderedEvents;
        }
    }
}


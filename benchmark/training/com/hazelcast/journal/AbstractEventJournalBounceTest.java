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
package com.hazelcast.journal;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.journal.EventJournalReader;
import com.hazelcast.test.bounce.BounceMemberRule;
import com.hazelcast.test.jitter.JitterRule;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Base class for an bouncing event journal read test. The test will fill
 * the data structure after which it will start gracefully shutting down
 * instances and starting new instances while concurrently reading from
 * the event journal.
 */
public abstract class AbstractEventJournalBounceTest {
    private static final int TEST_PARTITION_COUNT = 10;

    private static final int CONCURRENCY = 10;

    @Rule
    public BounceMemberRule bounceMemberRule = BounceMemberRule.with(getConfig()).clusterSize(4).driverCount(4).build();

    @Rule
    public JitterRule jitterRule = new JitterRule();

    private LinkedList<Object> expectedEvents;

    @Test
    public void testBouncingEventJournal() {
        AbstractEventJournalBounceTest.EventJournalReadRunnable[] testTasks = new AbstractEventJournalBounceTest.EventJournalReadRunnable[AbstractEventJournalBounceTest.CONCURRENCY];
        for (int i = 0; i < (AbstractEventJournalBounceTest.CONCURRENCY); i++) {
            testTasks[i] = new AbstractEventJournalBounceTest.EventJournalReadRunnable(bounceMemberRule.getNextTestDriver(), expectedEvents);
        }
        bounceMemberRule.testRepeatedly(testTasks, TimeUnit.MINUTES.toSeconds(3));
    }

    @SuppressWarnings("unchecked")
    class EventJournalReadRunnable<T> implements Runnable {
        private final HazelcastInstance hazelcastInstance;

        private final LinkedList<T> expected;

        private EventJournalReader<T> reader;

        EventJournalReadRunnable(HazelcastInstance hazelcastInstance, LinkedList<T> expected) {
            this.hazelcastInstance = hazelcastInstance;
            this.expected = expected;
        }

        @Override
        public void run() {
            if ((reader) == null) {
                reader = getEventJournalReader(hazelcastInstance);
            }
            final LinkedList<T> actual = getEventJournalEvents(reader);
            Assert.assertEquals(expected, actual);
        }
    }
}


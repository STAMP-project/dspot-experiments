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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Offloadable;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.test.bounce.BounceMemberRule;
import java.io.Serializable;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class EntryProcessorOffloadableBouncingNodesTest extends HazelcastTestSupport {
    public static final String MAP_NAME = "EntryProcessorOffloadableTest";

    public static final int COUNT_ENTRIES = 1000;

    private static final int CONCURRENCY = RuntimeAvailableProcessors.get();

    @Rule
    public BounceMemberRule bounceMemberRule = BounceMemberRule.with(getBouncingTestConfig()).build();

    @Test
    public void testOffloadableEntryProcessor() {
        populateMap(getMap());
        testForKey(0);
    }

    public static class EntryProcessorRunnable implements Runnable {
        private final IMap map;

        private final EntryProcessor ep;

        private final int key;

        public EntryProcessorRunnable(HazelcastInstance hz, EntryProcessor ep, int key) {
            this.map = hz.getMap(EntryProcessorOffloadableBouncingNodesTest.MAP_NAME);
            this.ep = ep;
            this.key = key;
        }

        @Override
        public void run() {
            map.executeOnKey(key, ep);
        }
    }

    private static class EntryIncOffloadable implements Offloadable , EntryBackupProcessor<Integer, EntryProcessorOffloadableBouncingNodesTest.SimpleValue> , EntryProcessor<Integer, EntryProcessorOffloadableBouncingNodesTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<Integer, EntryProcessorOffloadableBouncingNodesTest.SimpleValue> entry) {
            final EntryProcessorOffloadableBouncingNodesTest.SimpleValue value = entry.getValue();
            int result = value.i;
            (value.i)++;
            entry.setValue(value);
            HazelcastTestSupport.sleepAtLeastMillis(10);
            return result;
        }

        @Override
        public EntryBackupProcessor<Integer, EntryProcessorOffloadableBouncingNodesTest.SimpleValue> getBackupProcessor() {
            return this;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public void processBackup(Map.Entry<Integer, EntryProcessorOffloadableBouncingNodesTest.SimpleValue> entry) {
            process(entry);
        }
    }

    private static class EntryReadOnlyOffloadable implements Offloadable , ReadOnly , EntryProcessor<Integer, EntryProcessorOffloadableBouncingNodesTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<Integer, EntryProcessorOffloadableBouncingNodesTest.SimpleValue> entry) {
            return null;
        }

        @Override
        public EntryBackupProcessor<Integer, EntryProcessorOffloadableBouncingNodesTest.SimpleValue> getBackupProcessor() {
            return null;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }
    }

    private static class SimpleValue implements Serializable {
        public int i;

        SimpleValue() {
        }

        SimpleValue(final int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            EntryProcessorOffloadableBouncingNodesTest.SimpleValue that = ((EntryProcessorOffloadableBouncingNodesTest.SimpleValue) (o));
            if ((i) != (that.i)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "value: " + (i);
        }
    }
}


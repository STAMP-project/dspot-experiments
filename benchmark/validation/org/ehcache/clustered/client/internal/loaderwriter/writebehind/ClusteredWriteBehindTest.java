/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.client.internal.loaderwriter.writebehind;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import org.ehcache.clustered.common.internal.store.operations.Operation;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.ehcache.core.spi.time.TimeSource;
import org.junit.Test;


public class ClusteredWriteBehindTest {
    private static final TimeSource TIME_SOURCE = SystemTimeSource.INSTANCE;

    @Test
    public void testPutWithWriter() throws Exception {
        List<ClusteredWriteBehindTest.EventInfo> eventInfoList = new ArrayList<>();
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(1L, "The one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The one", true));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(1L, "The one one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The one one", true));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(2L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(2L, "The two", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The two", true));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(2L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(2L, "The two two", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The two two", true));
        HashMap<Long, String> result = new HashMap<>();
        result.put(1L, "The one one");
        result.put(2L, "The two two");
        verifyEvents(eventInfoList, result);
    }

    @Test
    public void testRemoves() throws Exception {
        List<ClusteredWriteBehindTest.EventInfo> eventInfoList = new ArrayList<>();
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(1L, "The one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The one", true));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(1L, "The one one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The one one", true));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.RemoveOperation(1L, ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), null, true));
        verifyEvents(eventInfoList, Collections.emptyMap());
    }

    @Test
    public void testCAS() throws Exception {
        List<ClusteredWriteBehindTest.EventInfo> eventInfoList = new ArrayList<>();
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.PutIfAbsentOperation(1L, "The one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The one", true));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.PutIfAbsentOperation(1L, "The one one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "none", false));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.ConditionalRemoveOperation(1L, "The one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), null, true));
        verifyEvents(eventInfoList, Collections.emptyMap());
    }

    @Test
    public void testPuts() throws Exception {
        List<ClusteredWriteBehindTest.EventInfo> eventInfoList = new ArrayList<>();
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.PutOperation(1L, "The one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The one", false));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(1L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(1L, "The one one", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The one one", true));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(2L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(2L, "The two", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The two", true));
        eventInfoList.add(new ClusteredWriteBehindTest.EventInfo(4L, new org.ehcache.clustered.common.internal.store.operations.PutWithWriterOperation(4L, "The four", ClusteredWriteBehindTest.TIME_SOURCE.getTimeMillis()), "The four", true));
        HashMap<Long, String> result = new HashMap<>();
        result.put(1L, "The one one");
        result.put(2L, "The two");
        result.put(4L, "The four");
        verifyEvents(eventInfoList, result);
    }

    class TestExecutorService extends AbstractExecutorService {
        @Override
        public void shutdown() {
        }

        @Override
        public List<Runnable> shutdownNow() {
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    private class EventInfo {
        private final Long key;

        private final Operation<Long, String> operation;

        private final String expectedValue;

        private final boolean track;

        private EventInfo(Long key, Operation<Long, String> operation, String expectedValue, boolean track) {
            this.key = key;
            this.operation = operation;
            this.expectedValue = expectedValue;
            this.track = track;
        }
    }
}


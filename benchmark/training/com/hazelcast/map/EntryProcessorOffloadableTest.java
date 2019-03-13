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


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Offloadable;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EntryProcessorOffloadableTest extends HazelcastTestSupport {
    public static final String MAP_NAME = "EntryProcessorOffloadableTest";

    private static final int HEARTBEATS_INTERVAL_SEC = 2;

    private HazelcastInstance[] instances;

    @Parameterized.Parameter(0)
    public InMemoryFormat inMemoryFormat;

    @Parameterized.Parameter(1)
    public int syncBackupCount;

    @Parameterized.Parameter(2)
    public int asyncBackupCount;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testEntryProcessorWithKey_offloadable_setValue() {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        EntryProcessorOffloadableTest.SimpleValue expectedValue = new EntryProcessorOffloadableTest.SimpleValue(2);
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        Object result = map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryIncOffloadable());
        Assert.assertEquals(expectedValue, map.get(key));
        assertBackupEventually(instances[1], EntryProcessorOffloadableTest.MAP_NAME, key, (isBackup() ? expectedValue : null));
        Assert.assertEquals(givenValue.i, result);
        instances[0].shutdown();
        Assert.assertEquals(expectedValue, map.get(key));
        Assert.assertEquals(givenValue.i, result);
    }

    private static class EntryIncOffloadable implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            final EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
            int result = value.i;
            (value.i)++;
            entry.setValue(value);
            return result;
        }

        @Override
        public EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> getBackupProcessor() {
            return this;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadable_withoutSetValue() {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        EntryProcessorOffloadableTest.SimpleValue expectedValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        Object result = map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryIncOffloadableNoSetValue());
        Assert.assertEquals(expectedValue, map.get(key));
        if (inMemoryFormat.equals(InMemoryFormat.OBJECT)) {
            assertBackupEventually(instances[1], EntryProcessorOffloadableTest.MAP_NAME, key, (isBackup() ? new EntryProcessorOffloadableTest.SimpleValue(2) : null));
        } else {
            assertBackupEventually(instances[1], EntryProcessorOffloadableTest.MAP_NAME, key, (isBackup() ? expectedValue : null));
        }
        Assert.assertEquals(givenValue.i, result);
        instances[0].shutdown();
        Assert.assertEquals(expectedValue, map.get(key));
    }

    private static class EntryIncOffloadableNoSetValue implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            final EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
            int result = value.i;
            (value.i)++;
            return result;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return this;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadableReadOnly_setValue() {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        expectedException.expect(UnsupportedOperationException.class);
        map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryIncOffloadableReadOnly());
    }

    private static class EntryIncOffloadableReadOnly implements Offloadable , ReadOnly , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            final EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
            int result = value.i;
            (value.i)++;
            entry.setValue(value);
            return result;
        }

        @Override
        public EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> getBackupProcessor() {
            return null;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadableReadOnly_withoutSetValue() {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        EntryProcessorOffloadableTest.SimpleValue expectedValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        Object result = map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryIncOffloadableReadOnlyNoSetValue());
        Assert.assertEquals(expectedValue, map.get(key));
        assertBackupEventually(instances[1], EntryProcessorOffloadableTest.MAP_NAME, key, (isBackup() ? expectedValue : null));
        Assert.assertEquals(givenValue.i, result);
        instances[0].shutdown();
        Assert.assertEquals(expectedValue, map.get(key));
    }

    private static class EntryIncOffloadableReadOnlyNoSetValue implements Offloadable , ReadOnly , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            final EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
            int result = value.i;
            (value.i)++;
            return result;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadableReadOnly_returnValue() {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        EntryProcessorOffloadableTest.SimpleValue expectedValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        Object result = map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryIncOffloadableReadOnlyReturnValue());
        Assert.assertEquals(expectedValue, map.get(key));
        assertBackupEventually(instances[1], EntryProcessorOffloadableTest.MAP_NAME, key, (isBackup() ? expectedValue : null));
        Assert.assertEquals(givenValue.i, result);
        instances[0].shutdown();
        Assert.assertEquals(expectedValue, map.get(key));
    }

    private static class EntryIncOffloadableReadOnlyReturnValue implements Offloadable , ReadOnly , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            final EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
            return value.i;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadableReadOnly_noReturnValue() {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        EntryProcessorOffloadableTest.SimpleValue expectedValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        Object result = map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryIncOffloadableReadOnlyNoReturnValue());
        Assert.assertEquals(expectedValue, map.get(key));
        assertBackupEventually(instances[1], EntryProcessorOffloadableTest.MAP_NAME, key, (isBackup() ? expectedValue : null));
        Assert.assertNull(result);
        instances[0].shutdown();
        Assert.assertEquals(expectedValue, map.get(key));
    }

    private static class EntryIncOffloadableReadOnlyNoReturnValue implements Offloadable , ReadOnly , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            return null;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadableReadOnly_throwsException() {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        expectedException.expect(RuntimeException.class);
        map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryIncOffloadableReadOnlyException());
        Assert.assertFalse(map.isLocked(key));
    }

    private static class EntryIncOffloadableReadOnlyException implements Offloadable , ReadOnly , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            throw new RuntimeException("EP exception");
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadableModifying_throwsException_keyNotLocked() {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        expectedException.expect(RuntimeException.class);
        map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryIncOffloadableException());
        Assert.assertFalse(map.isLocked(key));
    }

    @Test
    public void puts_multiple_entry_into_empty_map() {
        IMap map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        Set keySet = new HashSet(Arrays.asList(1, 2, 3, 4, 5));
        Object value = -1;
        map.executeOnKeys(keySet, new EntryProcessorOffloadableTest.EntryAdderOffloadable(value));
        for (Object key : keySet) {
            Assert.assertEquals(value, map.get(key));
        }
    }

    @Test
    public void puts_entry_into_empty_map() {
        IMap map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        Object key = 1;
        Object value = -1;
        map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryAdderOffloadable(value));
        Assert.assertEquals(value, map.get(key));
    }

    private static class EntryIncOffloadableException implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            throw new RuntimeException("EP exception");
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    private static class EntryAdderOffloadable extends AbstractEntryProcessor implements Offloadable {
        private final Object value;

        public EntryAdderOffloadable(Object value) {
            this.value = value;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public Object process(Map.Entry entry) {
            entry.setValue(value);
            return null;
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadable_otherModifyingWillWait() throws InterruptedException {
        final String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        EntryProcessorOffloadableTest.SimpleValue expectedValue = new EntryProcessorOffloadableTest.SimpleValue(4);
        final IMap<Object, Object> map = instances[0].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        final CountDownLatch epStarted = new CountDownLatch(1);
        final CountDownLatch epStopped = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryLatchModifying(epStarted, epStopped));
            }
        }.start();
        epStarted.await();
        map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryLatchVerifying(epStarted, epStopped, 4));
        // verified EPs not out-of-order, and not at the same time
        HazelcastTestSupport.assertEqualsEventually(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return map.get(key);
            }
        }, expectedValue);
    }

    private static class EntryLatchModifying implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        private final CountDownLatch start;

        private final CountDownLatch stop;

        public EntryLatchModifying(CountDownLatch start, CountDownLatch stop) {
            this.start = start;
            this.stop = stop;
        }

        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            start.countDown();
            try {
                final EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
                (value.i)++;
                entry.setValue(value);
                return null;
            } finally {
                stop.countDown();
            }
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }
    }

    private static class EntryLatchVerifying implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        private final CountDownLatch otherStarted;

        private final CountDownLatch otherStopped;

        private final int valueToSet;

        public EntryLatchVerifying(CountDownLatch otherStarted, CountDownLatch otherStopped, final int value) {
            this.otherStarted = otherStarted;
            this.otherStopped = otherStopped;
            this.valueToSet = value;
        }

        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            if (((otherStarted.getCount()) != 0) || ((otherStopped.getCount()) != 0)) {
                throw new RuntimeException("Wrong threading order");
            }
            final EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
            value.i = valueToSet;
            entry.setValue(value);
            return null;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }
    }

    @Test
    public void testEntryProcessorWithKey_offloadable_otherReadingWillNotWait() throws InterruptedException {
        final String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        EntryProcessorOffloadableTest.SimpleValue expectedValue = new EntryProcessorOffloadableTest.SimpleValue(2);
        final IMap<Object, Object> map = instances[0].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        final CountDownLatch epStarted = new CountDownLatch(1);
        final CountDownLatch epWaitToProceed = new CountDownLatch(1);
        final CountDownLatch epStopped = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryLatchModifyingOtherReading(epStarted, epWaitToProceed, epStopped));
            }
        }.start();
        epStarted.await();
        map.executeOnKey(key, new EntryProcessorOffloadableTest.EntryLatchReadOnlyVerifyingWhileOtherWriting(epStarted, epWaitToProceed, epStopped));
        epStopped.await();
        // verified EPs not out-of-order, and not at the same time
        HazelcastTestSupport.assertEqualsEventually(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return map.get(key);
            }
        }, expectedValue);
    }

    private static class EntryLatchModifyingOtherReading implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        private final CountDownLatch start;

        private final CountDownLatch stop;

        private final CountDownLatch waitToProceed;

        public EntryLatchModifyingOtherReading(CountDownLatch start, CountDownLatch waitToProceed, CountDownLatch stop) {
            this.start = start;
            this.stop = stop;
            this.waitToProceed = waitToProceed;
        }

        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            start.countDown();
            try {
                waitToProceed.await();
                final EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
                (value.i)++;
                entry.setValue(value);
                return null;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                stop.countDown();
            }
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }
    }

    private static class EntryLatchReadOnlyVerifyingWhileOtherWriting implements Offloadable , ReadOnly , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        private final CountDownLatch otherStarted;

        private final CountDownLatch otherWaitingToProceed;

        private final CountDownLatch otherStopped;

        public EntryLatchReadOnlyVerifyingWhileOtherWriting(CountDownLatch otherStarted, CountDownLatch otherWaitingToProceed, CountDownLatch otherStopped) {
            this.otherStarted = otherStarted;
            this.otherWaitingToProceed = otherWaitingToProceed;
            this.otherStopped = otherStopped;
        }

        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            if (((otherStarted.getCount()) != 0) || ((otherStopped.getCount()) != 1)) {
                throw new RuntimeException("Wrong threading order");
            }
            otherWaitingToProceed.countDown();
            return null;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }
    }

    @Test
    public void testEntryProcessorWithKey_lockedVsUnlocked() {
        String key = init();
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        // not locked -> will offload
        String thread = ((String) (map.executeOnKey(key, new EntryProcessorOffloadableTest.ThreadSneakingOffloadableEntryProcessor())));
        Assert.assertTrue(thread.contains("cached.thread"));
        // locked -> won't offload
        map.lock(key);
        thread = ((String) (map.executeOnKey(key, new EntryProcessorOffloadableTest.ThreadSneakingOffloadableEntryProcessor())));
        Assert.assertTrue(thread.contains("partition-operation.thread"));
    }

    @Test
    public void testEntryProcessorWithKey_lockedVsUnlocked_ReadOnly() {
        String key = init();
        IMap<Object, Object> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        // not locked -> will offload
        String thread = ((String) (map.executeOnKey(key, new EntryProcessorOffloadableTest.ThreadSneakingOffloadableReadOnlyEntryProcessor())));
        Assert.assertTrue(thread.contains("cached.thread"));
        // locked -> will offload
        map.lock(key);
        thread = ((String) (map.executeOnKey(key, new EntryProcessorOffloadableTest.ThreadSneakingOffloadableReadOnlyEntryProcessor())));
        Assert.assertTrue(thread.contains("cached.thread"));
    }

    private static class ThreadSneakingOffloadableEntryProcessor extends AbstractEntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> implements Offloadable {
        @Override
        public Object process(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            // returns the name of thread it runs on
            return Thread.currentThread().getName();
        }

        @Override
        public String getExecutorName() {
            return OFFLOADABLE_EXECUTOR;
        }
    }

    private static class ThreadSneakingOffloadableReadOnlyEntryProcessor implements Offloadable , ReadOnly , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            // returns the name of thread it runs on
            return Thread.currentThread().getName();
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public String getExecutorName() {
            return OFFLOADABLE_EXECUTOR;
        }
    }

    @Test
    public void testEntryProcessorWithKey_localNotReentrant() throws InterruptedException, ExecutionException {
        String key = init();
        IMap<Object, EntryProcessorOffloadableTest.SimpleValue> map = instances[1].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        int count = 100;
        // when
        List<ICompletableFuture> futures = new ArrayList<ICompletableFuture>();
        for (int i = 0; i < count; i++) {
            futures.add(map.submitToKey(key, new EntryProcessorOffloadableTest.IncrementingOffloadableEP()));
        }
        for (ICompletableFuture future : futures) {
            future.get();
        }
        // then
        Assert.assertEquals((count + 1), map.get(key).i);
    }

    private static class IncrementingOffloadableEP implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            EntryProcessorOffloadableTest.SimpleValue value = entry.getValue();
            (value.i)++;
            entry.setValue(value);
            return null;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return this;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }
    }

    @Test
    public void testEntryProcessorWithKey_localNotReentrant_latchTest() throws InterruptedException, ExecutionException {
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        EntryProcessorOffloadableTest.SimpleValue givenValue = new EntryProcessorOffloadableTest.SimpleValue(1);
        IMap<Object, Object> map = instances[0].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        CountDownLatch mayStart = new CountDownLatch(1);
        CountDownLatch stopped = new CountDownLatch(1);
        ICompletableFuture first = map.submitToKey(key, new EntryProcessorOffloadableTest.EntryLatchAwaitingModifying(mayStart, stopped));
        mayStart.countDown();
        ICompletableFuture second = map.submitToKey(key, new EntryProcessorOffloadableTest.EntryOtherStoppedVerifying(stopped));
        while (!((first.isDone()) && (second.isDone()))) {
            HazelcastTestSupport.sleepAtLeastMillis(1);
        } 
        // verifies that the other has stopped before the first one started
        Assert.assertEquals(0L, second.get());
    }

    private static class EntryLatchAwaitingModifying implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.SimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        private final CountDownLatch mayStart;

        private final CountDownLatch stop;

        public EntryLatchAwaitingModifying(CountDownLatch mayStart, CountDownLatch stop) {
            this.mayStart = mayStart;
            this.stop = stop;
        }

        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            try {
                mayStart.await();
            } catch (InterruptedException e) {
            }
            try {
                entry.setValue(entry.getValue());
                return null;
            } finally {
                stop.countDown();
            }
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            process(entry);
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }
    }

    private static class EntryOtherStoppedVerifying implements Offloadable , EntryProcessor<String, EntryProcessorOffloadableTest.SimpleValue> {
        private final CountDownLatch otherStopped;

        public EntryOtherStoppedVerifying(CountDownLatch otherStopped) {
            this.otherStopped = otherStopped;
        }

        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.SimpleValue> entry) {
            return otherStopped.getCount();
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }
    }

    /**
     * <pre>
     * Given: Heart beats are configured to come each few seconds (i.e. one quarter of hazelcast.operation.call.timeout.millis
     *        - set in the {@code getConfig()} method)
     * When: An offloaded EntryProcessor takes a long time to run.
     * Then: Heart beats are still coming during the task is offloaded.
     * </pre>
     *
     * @see #getConfig()
     * @see #HEARTBEATS_INTERVAL_SEC
     */
    @Test
    public void testHeartBeatsComingWhenEntryPropcessorOffloaded() throws Exception {
        final String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        EntryProcessorOffloadableTest.TimestampedSimpleValue givenValue = new EntryProcessorOffloadableTest.TimestampedSimpleValue(1);
        final IMap<Object, Object> map = instances[0].getMap(EntryProcessorOffloadableTest.MAP_NAME);
        map.put(key, givenValue);
        final Address instance1Address = instances[1].getCluster().getLocalMember().getAddress();
        final List<Long> heartBeatTimestamps = new LinkedList<Long>();
        Thread hbMonitorThread = new Thread() {
            public void run() {
                NodeEngine nodeEngine = HazelcastTestSupport.getNodeEngineImpl(instances[0]);
                OperationServiceImpl osImpl = ((OperationServiceImpl) (nodeEngine.getOperationService()));
                Map<Address, AtomicLong> heartBeats = osImpl.getInvocationMonitor().getHeartbeatPerMember();
                long lastbeat = Long.MIN_VALUE;
                while (!(isInterrupted())) {
                    AtomicLong timestamp = heartBeats.get(instance1Address);
                    if (timestamp != null) {
                        long newlastbeat = timestamp.get();
                        if (lastbeat != newlastbeat) {
                            lastbeat = newlastbeat;
                            heartBeatTimestamps.add(newlastbeat);
                        }
                    }
                    HazelcastTestSupport.sleepMillis(100);
                } 
            }
        };
        final int secondsToRun = 8;
        try {
            hbMonitorThread.start();
            map.executeOnKey(key, new EntryProcessorOffloadableTest.TimeConsumingOffloadableTask(secondsToRun));
        } finally {
            hbMonitorThread.interrupt();
        }
        int heartBeatCount = 0;
        EntryProcessorOffloadableTest.TimestampedSimpleValue updatedValue = ((EntryProcessorOffloadableTest.TimestampedSimpleValue) (map.get(key)));
        for (long heartBeatTimestamp : heartBeatTimestamps) {
            if ((heartBeatTimestamp > (updatedValue.processStart)) && (heartBeatTimestamp < (updatedValue.processEnd))) {
                heartBeatCount++;
            }
        }
        Assert.assertTrue("Heartbeats should be received while offloadable entry processor is running", (heartBeatCount > 0));
    }

    private static class TimeConsumingOffloadableTask implements Offloadable , EntryBackupProcessor<String, EntryProcessorOffloadableTest.TimestampedSimpleValue> , EntryProcessor<String, EntryProcessorOffloadableTest.TimestampedSimpleValue> , Serializable {
        private final int secondsToWork;

        private TimeConsumingOffloadableTask(int secondsToWork) {
            this.secondsToWork = secondsToWork;
        }

        @Override
        public Object process(final Map.Entry<String, EntryProcessorOffloadableTest.TimestampedSimpleValue> entry) {
            final EntryProcessorOffloadableTest.TimestampedSimpleValue value = entry.getValue();
            value.processStart = System.currentTimeMillis();
            long endTime = ((TimeUnit.SECONDS.toMillis(secondsToWork)) + (System.currentTimeMillis())) + 500L;
            do {
                HazelcastTestSupport.sleepMillis(200);
                (value.i)++;
                entry.setValue(value);
            } while ((System.currentTimeMillis()) < endTime );
            value.i = 1;
            entry.setValue(value);
            value.processEnd = System.currentTimeMillis();
            return null;
        }

        @Override
        public EntryBackupProcessor<String, EntryProcessorOffloadableTest.TimestampedSimpleValue> getBackupProcessor() {
            return this;
        }

        @Override
        public void processBackup(Map.Entry<String, EntryProcessorOffloadableTest.TimestampedSimpleValue> entry) {
            process(entry);
        }

        @Override
        public String getExecutorName() {
            return Offloadable.OFFLOADABLE_EXECUTOR;
        }
    }

    private static class SimpleValue implements Serializable {
        int i;

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
            EntryProcessorOffloadableTest.SimpleValue that = ((EntryProcessorOffloadableTest.SimpleValue) (o));
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

    private static class TimestampedSimpleValue extends EntryProcessorOffloadableTest.SimpleValue {
        private long processStart;

        private long processEnd;

        public TimestampedSimpleValue() {
            super();
        }

        public TimestampedSimpleValue(int i) {
            super(i);
        }
    }
}


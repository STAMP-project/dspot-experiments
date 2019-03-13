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


import TruePredicate.INSTANCE;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Offloadable;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.LockAwareLazyMapEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EntryProcessorLockTest extends HazelcastTestSupport {
    public static final String MAP_NAME = "EntryProcessorLockTest";

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Test
    public void test_executeOnEntries() {
        IMap<String, String> map = getInitializedMap();
        map.lock("key1");
        Map<String, Object> result = map.executeOnEntries(new EntryProcessorLockTest.TestNonOffloadableEntryProcessor());
        Assert.assertTrue(((Boolean) (result.get("key1"))));
        Assert.assertFalse(((Boolean) (result.get("key2"))));
    }

    @Test
    public void test_executeOnEntries_withPredicate() {
        IMap<String, String> map = getInitializedMap();
        map.lock("key1");
        Map<String, Object> result = map.executeOnEntries(new EntryProcessorLockTest.TestNonOffloadableEntryProcessor(), INSTANCE);
        Assert.assertTrue(((Boolean) (result.get("key1"))));
        Assert.assertFalse(((Boolean) (result.get("key2"))));
    }

    @Test
    public void test_executeOnKeys() {
        IMap<String, String> map = getInitializedMap();
        map.lock("key1");
        Map<String, Object> result = map.executeOnKeys(new HashSet<String>(Arrays.asList("key1", "key2")), new EntryProcessorLockTest.TestNonOffloadableEntryProcessor());
        Assert.assertTrue(((Boolean) (result.get("key1"))));
        Assert.assertFalse(((Boolean) (result.get("key2"))));
    }

    @Test
    public void test_executeOnKey_notOffloadable() {
        IMap<String, String> map = getInitializedMap();
        Boolean result = ((Boolean) (map.executeOnKey("key1", new EntryProcessorLockTest.TestNonOffloadableEntryProcessor())));
        Assert.assertFalse(result);
    }

    @Test
    public void test_executeOnKey_Offloadable() {
        IMap<String, String> map = getInitializedMap();
        Boolean result = ((Boolean) (map.executeOnKey("key1", new EntryProcessorLockTest.TestOffloadableEntryProcessor())));
        Assert.assertNull(result);
    }

    @Test
    public void test_executeOnKey_Offloadable_ReadOnly() {
        IMap<String, String> map = getInitializedMap();
        Boolean result = ((Boolean) (map.executeOnKey("key1", new EntryProcessorLockTest.TestOffloadableReadOnlyEntryProcessor())));
        Assert.assertNull(result);
    }

    @Test
    public void test_submitToKey_notOffloadable() throws InterruptedException, ExecutionException {
        IMap<String, String> map = getInitializedMap();
        Boolean result = ((Boolean) (map.submitToKey("key1", new EntryProcessorLockTest.TestNonOffloadableEntryProcessor()).get()));
        Assert.assertFalse(result);
    }

    @Test
    public void test_submitToKey_Offloadable() throws InterruptedException, ExecutionException {
        IMap<String, String> map = getInitializedMap();
        Boolean result = ((Boolean) (map.submitToKey("key1", new EntryProcessorLockTest.TestOffloadableEntryProcessor()).get()));
        Assert.assertNull(result);
    }

    @Test
    public void test_submitToKey_Offloadable_ReadOnly() throws InterruptedException, ExecutionException {
        IMap<String, String> map = getInitializedMap();
        Boolean result = ((Boolean) (map.submitToKey("key1", new EntryProcessorLockTest.TestOffloadableReadOnlyEntryProcessor()).get()));
        Assert.assertNull(result);
    }

    @Test
    public void test_Serialization_LockAwareLazyMapEntry_deserializesAs_LazyMapEntry() throws InterruptedException, ExecutionException {
        InternalSerializationService ss = HazelcastTestSupport.getSerializationService(createHazelcastInstance(getConfig()));
        LockAwareLazyMapEntry entry = new LockAwareLazyMapEntry(ss.toData("key"), "value", ss, Extractors.newBuilder(ss).build(), false);
        LockAwareLazyMapEntry deserializedEntry = ss.toObject(ss.toData(entry));
        Assert.assertEquals(LockAwareLazyMapEntry.class, deserializedEntry.getClass());
        Assert.assertEquals("key", deserializedEntry.getKey());
        Assert.assertEquals("value", deserializedEntry.getValue());
        Assert.assertNull(deserializedEntry.isLocked());
    }

    private static class TestNonOffloadableEntryProcessor implements EntryProcessor {
        @Override
        public Object process(Map.Entry entry) {
            return isLocked();
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }
    }

    private static class TestOffloadableEntryProcessor implements Offloadable , EntryProcessor {
        @Override
        public String getExecutorName() {
            return OFFLOADABLE_EXECUTOR;
        }

        @Override
        public Object process(Map.Entry entry) {
            return isLocked();
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }
    }

    private static class TestOffloadableReadOnlyEntryProcessor implements Offloadable , ReadOnly , EntryProcessor {
        @Override
        public String getExecutorName() {
            return OFFLOADABLE_EXECUTOR;
        }

        @Override
        public Object process(Map.Entry entry) {
            return isLocked();
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }
    }
}


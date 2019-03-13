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
package com.hazelcast.client.map;


import FalsePredicate.INSTANCE;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientEntryProcessorTest extends AbstractClientMapTest {
    @Test
    public void test_executeOnEntries_updatesValue_onOwnerAndBackupPartition() {
        String mapName = "test_executeOnEntries_updatesValue_onOwnerAndBackupPartition";
        String member1Key = generateKeyOwnedBy(AbstractClientMapTest.member1);
        IMap<String, String> clientMap = AbstractClientMapTest.client.getMap(mapName);
        clientMap.put(member1Key, "value");
        clientMap.executeOnEntries(new ClientEntryProcessorTest.ValueUpdater("newValue"));
        IMap<String, String> member1Map = AbstractClientMapTest.member1.getMap(mapName);
        ClientEntryProcessorTest.OwnerBackupValueCollector ep = new ClientEntryProcessorTest.OwnerBackupValueCollector();
        member1Map.executeOnKey(member1Key, ep);
        ConcurrentLinkedQueue<String> values = ClientEntryProcessorTest.OwnerBackupValueCollector.getValues();
        Assert.assertEquals(2, values.size());
        String value1 = values.poll();
        String value2 = values.poll();
        Assert.assertEquals(value1, value2);
    }

    public static class OwnerBackupValueCollector extends AbstractEntryProcessor<String, String> {
        private static final ConcurrentLinkedQueue<String> values = new ConcurrentLinkedQueue<String>();

        @Override
        public Object process(Map.Entry<String, String> entry) {
            ClientEntryProcessorTest.OwnerBackupValueCollector.values.add(entry.getValue());
            return null;
        }

        public static ConcurrentLinkedQueue<String> getValues() {
            return ClientEntryProcessorTest.OwnerBackupValueCollector.values;
        }
    }

    @Test
    public void test_executeOnEntries_notUpdatesValue_with_FalsePredicate() {
        String mapName = "test_executeOnEntries_notUpdatesValue_with_FalsePredicate";
        String member1Key = generateKeyOwnedBy(AbstractClientMapTest.member1);
        IMap<String, String> clientMap = AbstractClientMapTest.client.getMap(mapName);
        clientMap.put(member1Key, "value");
        clientMap.executeOnEntries(new ClientEntryProcessorTest.ValueUpdater("newValue"), INSTANCE);
        IMap<String, String> member1Map = AbstractClientMapTest.member1.getMap(mapName);
        String member1Value = member1Map.get(member1Key);
        Assert.assertEquals("value", member1Value);
    }

    @Test
    public void test_executeOnEntries_updatesValue_with_TruePredicate() {
        String mapName = "test_executeOnEntries_updatesValue_with_TruePredicate";
        String member1Key = generateKeyOwnedBy(AbstractClientMapTest.member1);
        IMap<String, String> clientMap = AbstractClientMapTest.client.getMap(mapName);
        clientMap.put(member1Key, "value");
        clientMap.executeOnEntries(new ClientEntryProcessorTest.ValueUpdater("newValue"), TruePredicate.INSTANCE);
        IMap<String, String> member1Map = AbstractClientMapTest.member1.getMap(mapName);
        String member1Value = member1Map.get(member1Key);
        Assert.assertEquals("newValue", member1Value);
    }

    @Test
    public void test_executeOnEntriesWithPredicate_usesIndexes_whenIndexesAvailable() {
        String mapName = "test_executeOnEntriesWithPredicate_usesIndexes_whenIndexesAvailable";
        IMap<Integer, Integer> map = AbstractClientMapTest.client.getMap(mapName);
        map.addIndex("__key", true);
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }
        ClientEntryProcessorTest.IndexedTestPredicate predicate = new ClientEntryProcessorTest.IndexedTestPredicate();
        map.executeOnEntries(new ClientEntryProcessorTest.EP(), predicate);
        Assert.assertTrue("isIndexed method of IndexAwarePredicate should be called", ClientEntryProcessorTest.IndexedTestPredicate.INDEX_CALLED.get());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_executeOnKey_readOnly_setValue() {
        String mapName = "test_executeOnKey_readOnly_setValue";
        String member1Key = generateKeyOwnedBy(AbstractClientMapTest.member1);
        IMap<String, String> clientMap = AbstractClientMapTest.client.getMap(mapName);
        clientMap.put(member1Key, "value");
        clientMap.executeOnKey(member1Key, new ClientEntryProcessorTest.ValueUpdaterReadOnly("newValue"));
    }

    public static final class EP extends AbstractEntryProcessor {
        @Override
        public Object process(Map.Entry entry) {
            return null;
        }
    }

    /**
     * This predicate is used to check whether or not {@link IndexAwarePredicate#isIndexed} method is called.
     */
    private static class IndexedTestPredicate implements IndexAwarePredicate {
        static final AtomicBoolean INDEX_CALLED = new AtomicBoolean(false);

        @Override
        public Set<QueryableEntry> filter(QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean isIndexed(QueryContext queryContext) {
            ClientEntryProcessorTest.IndexedTestPredicate.INDEX_CALLED.set(true);
            return true;
        }

        @Override
        public boolean apply(Map.Entry mapEntry) {
            return false;
        }
    }

    public static class ValueUpdater extends AbstractEntryProcessor {
        private final String newValue;

        ValueUpdater(String newValue) {
            this.newValue = newValue;
        }

        @Override
        public Object process(Map.Entry entry) {
            entry.setValue(newValue);
            return null;
        }
    }

    public static class ValueUpdaterReadOnly implements ReadOnly , EntryProcessor {
        private final String newValue;

        ValueUpdaterReadOnly(String newValue) {
            this.newValue = newValue;
        }

        @Override
        public Object process(Map.Entry entry) {
            entry.setValue(newValue);
            return null;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }
    }
}


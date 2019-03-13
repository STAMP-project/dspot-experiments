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


import MapService.SERVICE_NAME;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BackupExpirationTest extends HazelcastTestSupport {
    private static final String MAP_NAME = "test";

    private static final int NODE_COUNT = 3;

    private static final int REPLICA_COUNT = BackupExpirationTest.NODE_COUNT;

    private HazelcastInstance[] nodes;

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Test
    public void all_backups_should_be_empty_eventually() throws Exception {
        configureAndStartNodes(3, 271, 5);
        IMap map = nodes[0].getMap(BackupExpirationTest.MAP_NAME);
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }
        HazelcastTestSupport.sleepSeconds(5);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance node : nodes) {
                    Assert.assertEquals(0, BackupExpirationTest.getTotalEntryCount(node.getMap(BackupExpirationTest.MAP_NAME).getLocalMapStats()));
                }
            }
        });
    }

    @Test
    public void updates_on_same_key_prevents_expiration_on_backups() {
        configureAndStartNodes(10, 1, 1);
        long waitTimeInMillis = 1000;
        IMap map = nodes[0].getMap(BackupExpirationTest.MAP_NAME);
        map.put(1, 1);
        final BackupExpirationTest.BackupExpiryTimeReader backupExpiryTimeReader = new BackupExpirationTest.BackupExpiryTimeReader(BackupExpirationTest.MAP_NAME);
        // First call to read expiry time
        map.executeOnKey(1, backupExpiryTimeReader);
        HazelcastTestSupport.sleepAtLeastMillis(waitTimeInMillis);
        map.put(1, 1);
        // Second call to read expiry time
        map.executeOnKey(1, backupExpiryTimeReader);
        final int backupCount = (BackupExpirationTest.NODE_COUNT) - 1;
        final int executeOnKeyCallCount = 2;
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals((executeOnKeyCallCount * backupCount), backupExpiryTimeReader.TIMES_QUEUE.size());
            }
        });
        long expiryFoundAt1stCall = -1;
        for (int i = 0; i < backupCount; i++) {
            expiryFoundAt1stCall = backupExpiryTimeReader.TIMES_QUEUE.poll();
        }
        long expiryFoundAt2ndCall = -1;
        for (int i = 0; i < backupCount; i++) {
            expiryFoundAt2ndCall = backupExpiryTimeReader.TIMES_QUEUE.poll();
        }
        Assert.assertTrue(((expiryFoundAt2ndCall + "-") + expiryFoundAt1stCall), (expiryFoundAt2ndCall >= (expiryFoundAt1stCall + waitTimeInMillis)));
    }

    public static class BackupExpiryTimeReader implements HazelcastInstanceAware , EntryBackupProcessor<Integer, Integer> , EntryProcessor<Integer, Integer> , Serializable {
        public static final ConcurrentLinkedQueue<Long> TIMES_QUEUE = new ConcurrentLinkedQueue<Long>();

        private transient HazelcastInstance instance;

        private String mapName;

        public BackupExpiryTimeReader(String mapName) {
            this.mapName = mapName;
        }

        @Override
        public Object process(Map.Entry<Integer, Integer> entry) {
            return null;
        }

        @Override
        public EntryBackupProcessor<Integer, Integer> getBackupProcessor() {
            return this;
        }

        @Override
        public void processBackup(Map.Entry<Integer, Integer> entry) {
            EntryView entryView = instance.getMap(mapName).getEntryView(entry.getKey());
            BackupExpirationTest.BackupExpiryTimeReader.TIMES_QUEUE.add(entryView.getExpirationTime());
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.instance = hazelcastInstance;
        }
    }

    @Test
    public void dont_collect_expired_keys_if_expiration_reason_is_TTL() throws Exception {
        configureAndStartNodes(30, 1, 5);
        IMap map = nodes[0].getMap(BackupExpirationTest.MAP_NAME);
        Object expirationQueueLength = map.executeOnKey("1", new BackupExpirationTest.BackupExpirationQueueLengthFinder());
        Assert.assertEquals(0, ((Integer) (expirationQueueLength)).intValue());
    }

    // This EP mimics TTL expiration process by creating a record
    // which expires after 100 millis. TTL expired key should not be added to the expiration queue
    // after `recordStore.get`.
    @SuppressFBWarnings("SE_NO_SERIALVERSIONID")
    public static final class BackupExpirationQueueLengthFinder extends AbstractEntryProcessor implements HazelcastInstanceAware {
        private transient HazelcastInstance node;

        @Override
        public Object process(Map.Entry entry) {
            NodeEngineImpl nodeEngineImpl = HazelcastTestSupport.getNodeEngineImpl(node);
            SerializationService ss = nodeEngineImpl.getSerializationService();
            MapService mapService = nodeEngineImpl.getService(SERVICE_NAME);
            MapServiceContext mapServiceContext = mapService.getMapServiceContext();
            RecordStore recordStore = mapServiceContext.getRecordStore(0, BackupExpirationTest.MAP_NAME);
            Data dataKey = ss.toData(1);
            recordStore.put(dataKey, "value", 100, (-1));
            HazelcastTestSupport.sleepSeconds(1);
            recordStore.get(dataKey, false, null);
            InvalidationQueue expiredKeys = recordStore.getExpiredKeysQueue();
            return expiredKeys.size();
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.node = hazelcastInstance;
        }
    }
}


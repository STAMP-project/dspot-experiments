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
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.config.Config;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.sequence.CallIdSequence;
import com.hazelcast.spi.impl.sequence.CallIdSequenceWithBackpressure;
import com.hazelcast.spi.impl.sequence.CallIdSequenceWithoutBackpressure;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BackpressureRegulatorTest extends HazelcastTestSupport {
    private static final int SYNC_WINDOW = 100;

    private ILogger logger;

    @Test
    public void testBackPressureDisabledByDefault() {
        Config config = new Config();
        HazelcastProperties hazelcastProperties = new HazelcastProperties(config);
        BackpressureRegulator regulator = new BackpressureRegulator(hazelcastProperties, logger);
        Assert.assertFalse(regulator.isEnabled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_invalidSyncWindow() {
        Config config = new Config();
        config.setProperty(GroupProperty.BACKPRESSURE_ENABLED.getName(), "true");
        config.setProperty(GroupProperty.BACKPRESSURE_SYNCWINDOW.getName(), "0");
        HazelcastProperties hazelcastProperties = new HazelcastProperties(config);
        new BackpressureRegulator(hazelcastProperties, logger);
    }

    @Test
    public void testConstruction_OneSyncWindow_syncOnEveryCall() {
        Config config = new Config();
        config.setProperty(GroupProperty.BACKPRESSURE_ENABLED.getName(), "true");
        config.setProperty(GroupProperty.BACKPRESSURE_SYNCWINDOW.getName(), "1");
        HazelcastProperties hazelcastProperties = new HazelcastProperties(config);
        BackpressureRegulator regulator = new BackpressureRegulator(hazelcastProperties, logger);
        for (int k = 0; k < 1000; k++) {
            BackpressureRegulatorTest.PartitionSpecificOperation op = new BackpressureRegulatorTest.PartitionSpecificOperation(10);
            Assert.assertTrue(regulator.isSyncForced(op));
        }
    }

    // ========================== newCallIdSequence =================
    @Test
    public void newCallIdSequence_whenBackPressureEnabled() {
        Config config = new Config();
        config.setProperty(GroupProperty.BACKPRESSURE_ENABLED.getName(), "true");
        HazelcastProperties hazelcastProperties = new HazelcastProperties(config);
        BackpressureRegulator backpressureRegulator = new BackpressureRegulator(hazelcastProperties, logger);
        CallIdSequence callIdSequence = backpressureRegulator.newCallIdSequence();
        HazelcastTestSupport.assertInstanceOf(CallIdSequenceWithBackpressure.class, callIdSequence);
        Assert.assertEquals(backpressureRegulator.getMaxConcurrentInvocations(), callIdSequence.getMaxConcurrentInvocations());
    }

    @Test
    public void newCallIdSequence_whenBackPressureDisabled() {
        Config config = new Config();
        config.setProperty(GroupProperty.BACKPRESSURE_ENABLED.getName(), "false");
        HazelcastProperties hazelcastProperties = new HazelcastProperties(config);
        BackpressureRegulator backpressureRegulator = new BackpressureRegulator(hazelcastProperties, logger);
        CallIdSequence callIdSequence = backpressureRegulator.newCallIdSequence();
        HazelcastTestSupport.assertInstanceOf(CallIdSequenceWithoutBackpressure.class, callIdSequence);
    }

    // ========================== isSyncForced =================
    @Test
    public void isSyncForced_whenUrgentOperation_thenFalse() {
        BackpressureRegulator regulator = newEnabledBackPressureService();
        BackpressureRegulatorTest.UrgentOperation operation = new BackpressureRegulatorTest.UrgentOperation();
        setPartitionId(1);
        boolean result = regulator.isSyncForced(operation);
        Assert.assertFalse(result);
    }

    @Test
    public void isSyncForced_whenDisabled_thenFalse() {
        BackpressureRegulator regulator = newDisabledBackPressureService();
        BackpressureRegulatorTest.PartitionSpecificOperation op = new BackpressureRegulatorTest.PartitionSpecificOperation(10);
        int oldSyncDelay = regulator.syncCountDown();
        boolean result = regulator.isSyncForced(op);
        Assert.assertFalse(result);
        Assert.assertEquals(oldSyncDelay, regulator.syncCountDown());
    }

    @Test
    public void isSyncForced_whenNoAsyncBackups_thenFalse() {
        BackpressureRegulator regulator = newEnabledBackPressureService();
        BackpressureRegulatorTest.PartitionSpecificOperation op = new BackpressureRegulatorTest.PartitionSpecificOperation(10) {
            @Override
            public int getAsyncBackupCount() {
                return 0;
            }
        };
        int oldSyncDelay = regulator.syncCountDown();
        boolean result = regulator.isSyncForced(op);
        Assert.assertFalse(result);
        Assert.assertEquals(oldSyncDelay, regulator.syncCountDown());
    }

    @Test
    public void isSyncForced_whenPartitionSpecific() {
        BackpressureRegulator regulator = newEnabledBackPressureService();
        BackupAwareOperation op = new BackpressureRegulatorTest.PartitionSpecificOperation(10);
        for (int iteration = 0; iteration < 10; iteration++) {
            int initialSyncDelay = regulator.syncCountDown();
            int remainingSyncDelay = initialSyncDelay - 1;
            for (int k = 0; k < (initialSyncDelay - 1); k++) {
                boolean result = regulator.isSyncForced(op);
                Assert.assertFalse("no sync force expected", result);
                int syncDelay = regulator.syncCountDown();
                Assert.assertEquals(remainingSyncDelay, syncDelay);
                remainingSyncDelay--;
            }
            boolean result = regulator.isSyncForced(op);
            Assert.assertTrue("sync force expected", result);
            int syncDelay = regulator.syncCountDown();
            assertValidSyncDelay(syncDelay);
        }
    }

    private class UrgentOperation extends Operation implements BackupAwareOperation , UrgentSystemOperation {
        @Override
        public void run() throws Exception {
        }

        @Override
        public boolean shouldBackup() {
            return false;
        }

        @Override
        public int getSyncBackupCount() {
            return 0;
        }

        @Override
        public int getAsyncBackupCount() {
            return 1;
        }

        @Override
        public Operation getBackupOperation() {
            return null;
        }
    }

    private class PartitionSpecificOperation extends Operation implements BackupAwareOperation , PartitionAwareOperation {
        public PartitionSpecificOperation(int partitionId) {
            setPartitionId(partitionId);
        }

        @Override
        public void run() throws Exception {
        }

        @Override
        public boolean shouldBackup() {
            return true;
        }

        @Override
        public int getSyncBackupCount() {
            return 0;
        }

        @Override
        public int getAsyncBackupCount() {
            return 1;
        }

        @Override
        public Operation getBackupOperation() {
            return null;
        }
    }

    private class GenericOperation extends Operation implements BackupAwareOperation {
        public GenericOperation() {
            setPartitionId((-1));
        }

        @Override
        public void run() throws Exception {
        }

        @Override
        public boolean shouldBackup() {
            return true;
        }

        @Override
        public int getSyncBackupCount() {
            return 0;
        }

        @Override
        public int getAsyncBackupCount() {
            return 1;
        }

        @Override
        public Operation getBackupOperation() {
            return null;
        }
    }
}


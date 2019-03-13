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


import com.hazelcast.internal.partition.TestPartitionUtils;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationOutOfOrderBackupTest extends HazelcastTestSupport {
    private final OperationOutOfOrderBackupTest.ValueHolderService service = new OperationOutOfOrderBackupTest.ValueHolderService();

    private int partitionId;

    private NodeEngineImpl nodeEngine1;

    private NodeEngineImpl nodeEngine2;

    @Test
    public void test() throws InterruptedException {
        // set 1st value
        int oldValue = 111;
        setValue(nodeEngine1, partitionId, oldValue);
        long[] initialReplicaVersions = TestPartitionUtils.getDefaultReplicaVersions(nodeEngine1.getNode(), partitionId);
        assertBackupReplicaVersions(nodeEngine2.getNode(), partitionId, initialReplicaVersions);
        // set 2nd value
        int newValue = 222;
        setValue(nodeEngine1, partitionId, newValue);
        long[] lastReplicaVersions = TestPartitionUtils.getDefaultReplicaVersions(nodeEngine1.getNode(), partitionId);
        assertBackupReplicaVersions(nodeEngine2.getNode(), partitionId, lastReplicaVersions);
        // run a stale backup
        runBackup(nodeEngine2, oldValue, initialReplicaVersions, nodeEngine1.getThisAddress());
        long[] backupReplicaVersions = TestPartitionUtils.getDefaultReplicaVersions(nodeEngine2.getNode(), partitionId);
        Assert.assertArrayEquals(lastReplicaVersions, backupReplicaVersions);
        Assert.assertEquals(newValue, service.value.get());
    }

    private static class ValueHolderService {
        static final String NAME = "value-holder-service";

        final AtomicLong value = new AtomicLong();
    }

    private static class SampleBackupAwareOperation extends Operation implements BackupAwareOperation {
        long value;

        public SampleBackupAwareOperation() {
        }

        public SampleBackupAwareOperation(long value) {
            this.value = value;
        }

        @Override
        public void run() throws Exception {
            NodeEngineImpl nodeEngine = ((NodeEngineImpl) (getNodeEngine()));
            OperationOutOfOrderBackupTest.ValueHolderService service = nodeEngine.getService(OperationOutOfOrderBackupTest.ValueHolderService.NAME);
            service.value.set(value);
        }

        @Override
        public boolean shouldBackup() {
            return true;
        }

        @Override
        public int getSyncBackupCount() {
            return 1;
        }

        @Override
        public int getAsyncBackupCount() {
            return 0;
        }

        @Override
        public Operation getBackupOperation() {
            return new OperationOutOfOrderBackupTest.SampleBackupOperation(value);
        }

        @Override
        protected void writeInternal(ObjectDataOutput out) throws IOException {
            super.writeInternal(out);
            out.writeLong(value);
        }

        @Override
        protected void readInternal(ObjectDataInput in) throws IOException {
            super.readInternal(in);
            value = in.readLong();
        }
    }

    private static class SampleBackupOperation extends Operation implements BackupOperation {
        final CountDownLatch latch = new CountDownLatch(1);

        long value;

        public SampleBackupOperation() {
        }

        public SampleBackupOperation(long value) {
            this.value = value;
        }

        @Override
        public void run() throws Exception {
            try {
                NodeEngineImpl nodeEngine = ((NodeEngineImpl) (getNodeEngine()));
                OperationOutOfOrderBackupTest.ValueHolderService service = nodeEngine.getService(OperationOutOfOrderBackupTest.ValueHolderService.NAME);
                service.value.set(value);
            } finally {
                latch.countDown();
            }
        }

        @Override
        protected void writeInternal(ObjectDataOutput out) throws IOException {
            super.writeInternal(out);
            out.writeLong(value);
        }

        @Override
        protected void readInternal(ObjectDataInput in) throws IOException {
            super.readInternal(in);
            value = in.readLong();
        }
    }

    private static class LatchOperation extends Operation {
        final CountDownLatch latch;

        private LatchOperation(int count) {
            latch = new CountDownLatch(count);
        }

        @Override
        public void run() throws Exception {
            latch.countDown();
        }

        @Override
        public boolean returnsResponse() {
            return false;
        }

        @Override
        public boolean validatesTarget() {
            return false;
        }
    }
}


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
package com.hazelcast.util;


import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CollectionTxnUtilTest extends HazelcastTestSupport {
    private NodeEngineImpl nodeEngine = Mockito.mock(NodeEngineImpl.class);

    private RemoteService remoteService = Mockito.mock(RemoteService.class);

    private String serviceName;

    private String callerUuid;

    private int partitionId;

    private List<Operation> operationList;

    private Operation wrapper;

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(CollectionTxnUtil.class);
    }

    @Test
    public void testBefore() throws Exception {
        CollectionTxnUtil.before(operationList, wrapper);
        for (Operation operation : operationList) {
            CollectionTxnUtilTest.TestOperation op = ((CollectionTxnUtilTest.TestOperation) (operation));
            Assert.assertTrue(op.beforeCalled);
            Assert.assertEquals(remoteService, getService());
            Assert.assertEquals(serviceName, getServiceName());
            Assert.assertEquals(callerUuid, getCallerUuid());
            Assert.assertEquals(nodeEngine, getNodeEngine());
            Assert.assertEquals(partitionId, op.getPartitionId());
        }
    }

    @Test
    public void testRun() throws Exception {
        List<Operation> backupList = CollectionTxnUtil.run(operationList);
        Assert.assertEquals(1, backupList.size());
        CollectionTxnUtilTest.TestOperation operation = ((CollectionTxnUtilTest.TestOperation) (backupList.get(0)));
        Assert.assertEquals((-3), operation.i);
    }

    @Test
    public void testAfter() throws Exception {
        CollectionTxnUtil.after(operationList);
        for (Operation operation : operationList) {
            CollectionTxnUtilTest.TestOperation op = ((CollectionTxnUtilTest.TestOperation) (operation));
            Assert.assertTrue(op.afterCalled);
        }
    }

    @Test
    public void testWriteRead() throws IOException {
        InternalSerializationService ss = new DefaultSerializationServiceBuilder().build();
        BufferObjectDataOutput out = ss.createObjectDataOutput();
        CollectionTxnUtil.write(out, operationList);
        BufferObjectDataInput in = ss.createObjectDataInput(out.toByteArray());
        List<Operation> resultList = CollectionTxnUtil.read(in);
        Assert.assertEquals(operationList.size(), resultList.size());
        for (int i = 0; i < (operationList.size()); i++) {
            Assert.assertEquals(operationList.get(i), resultList.get(i));
        }
    }

    static class TestOperation extends Operation implements BackupAwareOperation {
        int i;

        transient boolean beforeCalled;

        transient boolean runCalled;

        transient boolean afterCalled;

        public TestOperation() {
        }

        public TestOperation(int i) {
            this.i = i;
        }

        @Override
        public void beforeRun() throws Exception {
            beforeCalled = true;
        }

        @Override
        public void run() throws Exception {
            runCalled = true;
        }

        @Override
        public void afterRun() throws Exception {
            afterCalled = true;
        }

        @Override
        public boolean returnsResponse() {
            return false;
        }

        @Override
        public boolean shouldBackup() {
            return (i) == 3;
        }

        @Override
        public int getSyncBackupCount() {
            return 0;
        }

        @Override
        public int getAsyncBackupCount() {
            return 0;
        }

        @Override
        public Operation getBackupOperation() {
            return new CollectionTxnUtilTest.TestOperation((-(i)));
        }

        @Override
        protected void writeInternal(ObjectDataOutput out) throws IOException {
            out.writeInt(i);
        }

        @Override
        protected void readInternal(ObjectDataInput in) throws IOException {
            i = in.readInt();
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof CollectionTxnUtilTest.TestOperation)) {
                return false;
            }
            CollectionTxnUtilTest.TestOperation that = ((CollectionTxnUtilTest.TestOperation) (o));
            return (i) == (that.i);
        }

        @Override
        public int hashCode() {
            return i;
        }
    }
}


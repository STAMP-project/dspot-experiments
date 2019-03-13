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
package com.hazelcast.transaction.impl;


import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.net.InetAddress;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class TransactionLogTest {
    @Test
    public void add_whenKeyAware() {
        TransactionLog log = new TransactionLog();
        TransactionLogRecord record = Mockito.mock(TransactionLogRecord.class);
        String key = "foo";
        Mockito.when(record.getKey()).thenReturn(key);
        log.add(record);
        Assert.assertSame(record, log.get(key));
        Assert.assertEquals(1, log.size());
    }

    @Test
    public void add_whenNotKeyAware() {
        TransactionLog log = new TransactionLog();
        TransactionLogRecord record = Mockito.mock(TransactionLogRecord.class);
        log.add(record);
        Assert.assertEquals(1, log.size());
        Assert.assertEquals(Arrays.asList(record), log.getRecordList());
    }

    @Test
    public void add_whenOverwrite() {
        TransactionLog log = new TransactionLog();
        String key = "foo";
        // first we insert the old record
        TransactionLogRecord oldRecord = Mockito.mock(TransactionLogRecord.class);
        Mockito.when(oldRecord.getKey()).thenReturn(key);
        log.add(oldRecord);
        // then we insert the old record
        TransactionLogRecord newRecord = Mockito.mock(TransactionLogRecord.class);
        Mockito.when(newRecord.getKey()).thenReturn(key);
        log.add(newRecord);
        Assert.assertSame(newRecord, log.get(key));
        Assert.assertEquals(1, log.size());
    }

    @Test
    public void remove_whenNotExist_thenCallIgnored() {
        TransactionLog log = new TransactionLog();
        log.remove("not exist");
    }

    @Test
    public void remove_whenExist_thenRemoved() {
        TransactionLog log = new TransactionLog();
        TransactionLogRecord record = Mockito.mock(TransactionLogRecord.class);
        String key = "foo";
        Mockito.when(record.getKey()).thenReturn(key);
        log.add(record);
        log.remove(key);
        Assert.assertNull(log.get(key));
    }

    @Test
    public void prepare_partitionSpecificRecord() throws Exception {
        OperationService operationService = Mockito.mock(OperationService.class);
        NodeEngine nodeEngine = Mockito.mock(NodeEngine.class);
        Mockito.when(nodeEngine.getOperationService()).thenReturn(operationService);
        TransactionLog log = new TransactionLog();
        TransactionLogRecord partitionRecord = Mockito.mock(TransactionLogRecord.class);
        Operation partitionOperation = new TransactionLogTest.DummyPartitionOperation();
        Mockito.when(partitionRecord.newPrepareOperation()).thenReturn(partitionOperation);
        log.add(partitionRecord);
        log.prepare(nodeEngine);
        Mockito.verify(operationService, Mockito.times(1)).invokeOnPartition(partitionOperation.getServiceName(), partitionOperation, partitionOperation.getPartitionId());
    }

    @Test
    public void rollback_partitionSpecificRecord() throws Exception {
        OperationService operationService = Mockito.mock(OperationService.class);
        NodeEngine nodeEngine = Mockito.mock(NodeEngine.class);
        Mockito.when(nodeEngine.getOperationService()).thenReturn(operationService);
        TransactionLog log = new TransactionLog();
        TransactionLogRecord partitionRecord = Mockito.mock(TransactionLogRecord.class);
        Operation partitionOperation = new TransactionLogTest.DummyPartitionOperation();
        Mockito.when(partitionRecord.newRollbackOperation()).thenReturn(partitionOperation);
        log.add(partitionRecord);
        log.rollback(nodeEngine);
        Mockito.verify(operationService, Mockito.times(1)).invokeOnPartition(partitionOperation.getServiceName(), partitionOperation, partitionOperation.getPartitionId());
    }

    @Test
    public void commit_partitionSpecificRecord() throws Exception {
        OperationService operationService = Mockito.mock(OperationService.class);
        NodeEngine nodeEngine = Mockito.mock(NodeEngine.class);
        Mockito.when(nodeEngine.getOperationService()).thenReturn(operationService);
        TransactionLog log = new TransactionLog();
        TransactionLogRecord partitionRecord = Mockito.mock(TransactionLogRecord.class);
        Operation partitionOperation = new TransactionLogTest.DummyPartitionOperation();
        Mockito.when(partitionRecord.newCommitOperation()).thenReturn(partitionOperation);
        log.add(partitionRecord);
        log.commit(nodeEngine);
        Mockito.verify(operationService, Mockito.times(1)).invokeOnPartition(partitionOperation.getServiceName(), partitionOperation, partitionOperation.getPartitionId());
    }

    @Test
    public void prepare_targetAwareRecord() throws Exception {
        OperationService operationService = Mockito.mock(OperationService.class);
        NodeEngine nodeEngine = Mockito.mock(NodeEngine.class);
        Mockito.when(nodeEngine.getOperationService()).thenReturn(operationService);
        TransactionLog log = new TransactionLog();
        Address target = new Address(InetAddress.getLocalHost(), 5000);
        TargetAwareTransactionLogRecord targetRecord = Mockito.mock(TargetAwareTransactionLogRecord.class);
        Mockito.when(targetRecord.getTarget()).thenReturn(target);
        TransactionLogTest.DummyTargetOperation targetOperation = new TransactionLogTest.DummyTargetOperation();
        Mockito.when(targetRecord.newPrepareOperation()).thenReturn(targetOperation);
        log.add(targetRecord);
        log.prepare(nodeEngine);
        Mockito.verify(operationService, Mockito.times(1)).invokeOnTarget(targetOperation.getServiceName(), targetOperation, target);
    }

    @Test
    public void rollback_targetAwareRecord() throws Exception {
        OperationService operationService = Mockito.mock(OperationService.class);
        NodeEngine nodeEngine = Mockito.mock(NodeEngine.class);
        Mockito.when(nodeEngine.getOperationService()).thenReturn(operationService);
        TransactionLog log = new TransactionLog();
        Address target = new Address(InetAddress.getLocalHost(), 5000);
        TargetAwareTransactionLogRecord targetRecord = Mockito.mock(TargetAwareTransactionLogRecord.class);
        Mockito.when(targetRecord.getTarget()).thenReturn(target);
        TransactionLogTest.DummyTargetOperation targetOperation = new TransactionLogTest.DummyTargetOperation();
        Mockito.when(targetRecord.newRollbackOperation()).thenReturn(targetOperation);
        log.add(targetRecord);
        log.rollback(nodeEngine);
        Mockito.verify(operationService, Mockito.times(1)).invokeOnTarget(targetOperation.getServiceName(), targetOperation, target);
    }

    @Test
    public void commit_targetAwareRecord() throws Exception {
        OperationService operationService = Mockito.mock(OperationService.class);
        NodeEngine nodeEngine = Mockito.mock(NodeEngine.class);
        Mockito.when(nodeEngine.getOperationService()).thenReturn(operationService);
        TransactionLog log = new TransactionLog();
        Address target = new Address(InetAddress.getLocalHost(), 5000);
        TargetAwareTransactionLogRecord targetRecord = Mockito.mock(TargetAwareTransactionLogRecord.class);
        Mockito.when(targetRecord.getTarget()).thenReturn(target);
        TransactionLogTest.DummyTargetOperation targetOperation = new TransactionLogTest.DummyTargetOperation();
        Mockito.when(targetRecord.newCommitOperation()).thenReturn(targetOperation);
        log.add(targetRecord);
        log.commit(nodeEngine);
        Mockito.verify(operationService, Mockito.times(1)).invokeOnTarget(targetOperation.getServiceName(), targetOperation, target);
    }

    private static class DummyPartitionOperation extends Operation {
        {
            setPartitionId(0);
        }

        @Override
        public void run() throws Exception {
        }

        @Override
        public String getServiceName() {
            return "dummy";
        }
    }

    private static class DummyTargetOperation extends Operation {
        @Override
        public void run() throws Exception {
        }

        @Override
        public String getServiceName() {
            return "dummy";
        }
    }
}


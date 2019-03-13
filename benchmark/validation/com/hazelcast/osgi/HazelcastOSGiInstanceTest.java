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
package com.hazelcast.osgi;


import com.hazelcast.config.Config;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Endpoint;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.osgi.impl.HazelcastOSGiTestUtil;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class HazelcastOSGiInstanceTest {
    @Test
    @SuppressWarnings("EqualsWithItself")
    public void equalsReturnsTrueForSameOSGiInstances() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Assert.assertTrue(hazelcastOSGiInstance.equals(hazelcastOSGiInstance));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void equalsReturnsFalseForNullObject() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Assert.assertFalse(hazelcastOSGiInstance.equals(null));
    }

    @Test
    public void equalsReturnsFalseForDifferentTypedObject() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Assert.assertFalse(hazelcastOSGiInstance.equals(new Object()));
    }

    @Test
    public void equalsReturnsFalseForDifferentOSGiInstancesWithDifferentDelegatedInstanceAndSameService() {
        HazelcastInstance mockHazelcastInstance1 = Mockito.mock(HazelcastInstance.class);
        HazelcastInstance mockHazelcastInstance2 = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiService mockService = Mockito.mock(HazelcastOSGiService.class);
        HazelcastOSGiInstance hazelcastOSGiInstance1 = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance1, mockService);
        HazelcastOSGiInstance hazelcastOSGiInstance2 = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance2, mockService);
        Assert.assertFalse(hazelcastOSGiInstance1.equals(hazelcastOSGiInstance2));
    }

    @Test
    public void equalsReturnsFalseForDifferentOSGiInstancesWithSameDelegatedInstanceAndDifferentService() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiService mockService1 = Mockito.mock(HazelcastOSGiService.class);
        HazelcastOSGiService mockService2 = Mockito.mock(HazelcastOSGiService.class);
        HazelcastOSGiInstance hazelcastOSGiInstance1 = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance, mockService1);
        HazelcastOSGiInstance hazelcastOSGiInstance2 = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance, mockService2);
        Assert.assertFalse(hazelcastOSGiInstance1.equals(hazelcastOSGiInstance2));
    }

    @Test
    public void equalsReturnsFalseForDifferentOSGiInstancesWithDifferentDelegatedInstanceAndDifferentService() {
        HazelcastInstance mockHazelcastInstance1 = Mockito.mock(HazelcastInstance.class);
        HazelcastInstance mockHazelcastInstance2 = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiService mockService1 = Mockito.mock(HazelcastOSGiService.class);
        HazelcastOSGiService mockService2 = Mockito.mock(HazelcastOSGiService.class);
        HazelcastOSGiInstance hazelcastOSGiInstance1 = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance1, mockService1);
        HazelcastOSGiInstance hazelcastOSGiInstance2 = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance2, mockService2);
        Assert.assertFalse(hazelcastOSGiInstance1.equals(hazelcastOSGiInstance2));
    }

    @Test
    public void equalsReturnsTrueForDifferentOSGiInstancesWithSameDelegatedInstanceAndSameService() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiService mockService = Mockito.mock(HazelcastOSGiService.class);
        HazelcastOSGiInstance hazelcastOSGiInstance1 = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance, mockService);
        HazelcastOSGiInstance hazelcastOSGiInstance2 = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance, mockService);
        Assert.assertTrue(hazelcastOSGiInstance1.equals(hazelcastOSGiInstance2));
    }

    @Test
    public void getDelegatedInstanceCalledSuccessfullyOverOSGiInstance() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Assert.assertEquals(mockHazelcastInstance, hazelcastOSGiInstance.getDelegatedInstance());
    }

    @Test
    public void getOwnerServiceCalledSuccessfullyOverOSGiInstance() {
        HazelcastOSGiService mockHazelcastOSGiService = Mockito.mock(HazelcastOSGiService.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastOSGiService);
        Assert.assertEquals(mockHazelcastOSGiService, hazelcastOSGiInstance.getOwnerService());
    }

    @Test
    public void getNameCalledSuccessfullyOverOSGiInstance() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getName()).thenReturn("my-name");
        Assert.assertEquals("my-name", hazelcastOSGiInstance.getName());
        Mockito.verify(mockHazelcastInstance).getName();
    }

    @Test
    public void getQueueCalledSuccessfullyOverOSGiInstance() {
        IQueue<Object> mockQueue = Mockito.mock(IQueue.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getQueue("my-queue")).thenReturn(mockQueue);
        Assert.assertEquals(mockQueue, hazelcastOSGiInstance.getQueue("my-queue"));
        Mockito.verify(mockHazelcastInstance).getQueue("my-queue");
    }

    @Test
    public void getTopicCalledSuccessfullyOverOSGiInstance() {
        ITopic<Object> mockTopic = Mockito.mock(ITopic.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getTopic("my-topic")).thenReturn(mockTopic);
        Assert.assertEquals(mockTopic, hazelcastOSGiInstance.getTopic("my-topic"));
        Mockito.verify(mockHazelcastInstance).getTopic("my-topic");
    }

    @Test
    public void getSetCalledSuccessfullyOverOSGiInstance() {
        ISet<Object> mockSet = Mockito.mock(ISet.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getSet("my-set")).thenReturn(mockSet);
        Assert.assertEquals(mockSet, hazelcastOSGiInstance.getSet("my-set"));
        Mockito.verify(mockHazelcastInstance).getSet("my-set");
    }

    @Test
    public void getListCalledSuccessfullyOverOSGiInstance() {
        IList<Object> mockList = Mockito.mock(IList.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getList("my-list")).thenReturn(mockList);
        Assert.assertEquals(mockList, hazelcastOSGiInstance.getList("my-list"));
        Mockito.verify(mockHazelcastInstance).getList("my-list");
    }

    @Test
    public void getMapCalledSuccessfullyOverOSGiInstance() {
        IMap<Object, Object> mockMap = Mockito.mock(IMap.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getMap("my-map")).thenReturn(mockMap);
        Assert.assertEquals(mockMap, hazelcastOSGiInstance.getMap("my-map"));
        Mockito.verify(mockHazelcastInstance).getMap("my-map");
    }

    @Test
    public void getReplicatedMapCalledSuccessfullyOverOSGiInstance() {
        ReplicatedMap<Object, Object> mockReplicatedMap = Mockito.mock(ReplicatedMap.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getReplicatedMap("my-replicatedmap")).thenReturn(mockReplicatedMap);
        Assert.assertEquals(mockReplicatedMap, hazelcastOSGiInstance.getReplicatedMap("my-replicatedmap"));
        Mockito.verify(mockHazelcastInstance).getReplicatedMap("my-replicatedmap");
    }

    @Test
    public void getJobTrackerMapCalledSuccessfullyOverOSGiInstance() {
        JobTracker mockJobTracker = Mockito.mock(JobTracker.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getJobTracker("my-jobtracker")).thenReturn(mockJobTracker);
        Assert.assertEquals(mockJobTracker, hazelcastOSGiInstance.getJobTracker("my-jobtracker"));
        Mockito.verify(mockHazelcastInstance).getJobTracker("my-jobtracker");
    }

    @Test
    public void getMultiMapCalledSuccessfullyOverOSGiInstance() {
        MultiMap<Object, Object> mockMultiMap = Mockito.mock(MultiMap.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getMultiMap("my-multimap")).thenReturn(mockMultiMap);
        Assert.assertEquals(mockMultiMap, hazelcastOSGiInstance.getMultiMap("my-multimap"));
        Mockito.verify(mockHazelcastInstance).getMultiMap("my-multimap");
    }

    @Test
    public void getLockCalledSuccessfullyOverOSGiInstance() {
        ILock mockLock = Mockito.mock(ILock.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getLock("my-lock")).thenReturn(mockLock);
        Assert.assertEquals(mockLock, hazelcastOSGiInstance.getLock("my-lock"));
        Mockito.verify(mockHazelcastInstance).getLock("my-lock");
    }

    @Test
    public void getRingbufferCalledSuccessfullyOverOSGiInstance() {
        Ringbuffer<Object> mockRingbuffer = Mockito.mock(Ringbuffer.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getRingbuffer("my-ringbuffer")).thenReturn(mockRingbuffer);
        Assert.assertEquals(mockRingbuffer, hazelcastOSGiInstance.getRingbuffer("my-ringbuffer"));
        Mockito.verify(mockHazelcastInstance).getRingbuffer("my-ringbuffer");
    }

    @Test
    public void getReliableTopicCalledSuccessfullyOverOSGiInstance() {
        ITopic<Object> mockReliableTopic = Mockito.mock(ITopic.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getReliableTopic("my-reliabletopic")).thenReturn(mockReliableTopic);
        Assert.assertEquals(mockReliableTopic, hazelcastOSGiInstance.getReliableTopic("my-reliabletopic"));
        Mockito.verify(mockHazelcastInstance).getReliableTopic("my-reliabletopic");
    }

    @Test
    public void getClusterCalledSuccessfullyOverOSGiInstance() {
        Cluster mockCluster = Mockito.mock(Cluster.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getCluster()).thenReturn(mockCluster);
        Assert.assertEquals(mockCluster, hazelcastOSGiInstance.getCluster());
        Mockito.verify(mockHazelcastInstance).getCluster();
    }

    @Test
    public void getLocalEndpointCalledSuccessfullyOverOSGiInstance() {
        Endpoint mockEndpoint = Mockito.mock(Endpoint.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getLocalEndpoint()).thenReturn(mockEndpoint);
        Assert.assertEquals(mockEndpoint, hazelcastOSGiInstance.getLocalEndpoint());
        Mockito.verify(mockHazelcastInstance).getLocalEndpoint();
    }

    @Test
    public void getExecutorServiceCalledSuccessfullyOverOSGiInstance() {
        IExecutorService mockExecutorService = Mockito.mock(IExecutorService.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getExecutorService("my-executorservice")).thenReturn(mockExecutorService);
        Assert.assertEquals(mockExecutorService, hazelcastOSGiInstance.getExecutorService("my-executorservice"));
        Mockito.verify(mockHazelcastInstance).getExecutorService("my-executorservice");
    }

    @Test
    public void executeTransactionCalledSuccessfullyOverOSGiInstance() {
        Object result = new Object();
        TransactionalTask mockTransactionalTask = Mockito.mock(TransactionalTask.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.executeTransaction(mockTransactionalTask)).thenReturn(result);
        Assert.assertEquals(result, hazelcastOSGiInstance.executeTransaction(mockTransactionalTask));
        Mockito.verify(mockHazelcastInstance).executeTransaction(mockTransactionalTask);
    }

    @Test
    public void executeTransactionWithOptionsCalledSuccessfullyOverOSGiInstance() {
        Object result = new Object();
        TransactionOptions transactionOptions = new TransactionOptions();
        TransactionalTask mockTransactionalTask = Mockito.mock(TransactionalTask.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.executeTransaction(transactionOptions, mockTransactionalTask)).thenReturn(result);
        Assert.assertEquals(result, hazelcastOSGiInstance.executeTransaction(transactionOptions, mockTransactionalTask));
        Mockito.verify(mockHazelcastInstance).executeTransaction(transactionOptions, mockTransactionalTask);
    }

    @Test
    public void newTransactionContextCalledSuccessfullyOverOSGiInstance() {
        TransactionContext mockTransactionContext = Mockito.mock(TransactionContext.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.newTransactionContext()).thenReturn(mockTransactionContext);
        Assert.assertEquals(mockTransactionContext, hazelcastOSGiInstance.newTransactionContext());
        Mockito.verify(mockHazelcastInstance).newTransactionContext();
    }

    @Test
    public void newTransactionContextWithOptionsCalledSuccessfullyOverOSGiInstance() {
        TransactionOptions transactionOptions = new TransactionOptions();
        TransactionContext mockTransactionContext = Mockito.mock(TransactionContext.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.newTransactionContext(transactionOptions)).thenReturn(mockTransactionContext);
        Assert.assertEquals(mockTransactionContext, hazelcastOSGiInstance.newTransactionContext(transactionOptions));
        Mockito.verify(mockHazelcastInstance).newTransactionContext(transactionOptions);
    }

    @Test
    public void getIdGeneratorCalledSuccessfullyOverOSGiInstance() {
        IdGenerator mockIdGenerator = Mockito.mock(IdGenerator.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getIdGenerator("my-idgenerator")).thenReturn(mockIdGenerator);
        Assert.assertEquals(mockIdGenerator, hazelcastOSGiInstance.getIdGenerator("my-idgenerator"));
        Mockito.verify(mockHazelcastInstance).getIdGenerator("my-idgenerator");
    }

    @Test
    public void getAtomicLongCalledSuccessfullyOverOSGiInstance() {
        IAtomicLong mockAtomicLong = Mockito.mock(IAtomicLong.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getAtomicLong("my-atomiclong")).thenReturn(mockAtomicLong);
        Assert.assertEquals(mockAtomicLong, hazelcastOSGiInstance.getAtomicLong("my-atomiclong"));
        Mockito.verify(mockHazelcastInstance).getAtomicLong("my-atomiclong");
    }

    @Test
    public void getAtomicReferenceCalledSuccessfullyOverOSGiInstance() {
        IAtomicReference<Object> mockAtomicReference = Mockito.mock(IAtomicReference.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getAtomicReference("my-atomicreference")).thenReturn(mockAtomicReference);
        Assert.assertEquals(mockAtomicReference, hazelcastOSGiInstance.getAtomicReference("my-atomicreference"));
        Mockito.verify(mockHazelcastInstance).getAtomicReference("my-atomicreference");
    }

    @Test
    public void getCountDownLatchCalledSuccessfullyOverOSGiInstance() {
        ICountDownLatch mockCountDownLatch = Mockito.mock(ICountDownLatch.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getCountDownLatch("my-countdownlatch")).thenReturn(mockCountDownLatch);
        Assert.assertEquals(mockCountDownLatch, hazelcastOSGiInstance.getCountDownLatch("my-countdownlatch"));
        Mockito.verify(mockHazelcastInstance).getCountDownLatch("my-countdownlatch");
    }

    @Test
    public void getSemaphoreCalledSuccessfullyOverOSGiInstance() {
        ISemaphore mockSemaphore = Mockito.mock(ISemaphore.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getSemaphore("my-semaphore")).thenReturn(mockSemaphore);
        Assert.assertEquals(mockSemaphore, hazelcastOSGiInstance.getSemaphore("my-semaphore"));
        Mockito.verify(mockHazelcastInstance).getSemaphore("my-semaphore");
    }

    @Test
    public void getDistributedObjectsCalledSuccessfullyOverOSGiInstance() {
        Collection<DistributedObject> mockDistributedObjects = Mockito.mock(Collection.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getDistributedObjects()).thenReturn(mockDistributedObjects);
        Assert.assertEquals(mockDistributedObjects, hazelcastOSGiInstance.getDistributedObjects());
        Mockito.verify(mockHazelcastInstance).getDistributedObjects();
    }

    @Test
    public void getDistributedObjectCalledSuccessfullyOverOSGiInstance() {
        DistributedObject mockDistributedObject = Mockito.mock(DistributedObject.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getDistributedObject("my-service", "my-name")).thenReturn(mockDistributedObject);
        Assert.assertEquals(mockDistributedObject, hazelcastOSGiInstance.getDistributedObject("my-service", "my-name"));
        Mockito.verify(mockHazelcastInstance).getDistributedObject("my-service", "my-name");
    }

    @Test
    public void addDistributedObjectListenerCalledSuccessfullyOverOSGiInstance() {
        DistributedObjectListener mockDistributedObjectListener = Mockito.mock(DistributedObjectListener.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.addDistributedObjectListener(mockDistributedObjectListener)).thenReturn("my-registration-id");
        Assert.assertEquals("my-registration-id", hazelcastOSGiInstance.addDistributedObjectListener(mockDistributedObjectListener));
        Mockito.verify(mockHazelcastInstance).addDistributedObjectListener(mockDistributedObjectListener);
    }

    @Test
    public void removeDistributedObjectListenerCalledSuccessfullyOverOSGiInstance() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.removeDistributedObjectListener("my-registration-id")).thenReturn(true);
        Assert.assertTrue(hazelcastOSGiInstance.removeDistributedObjectListener("my-registration-id"));
        Mockito.verify(mockHazelcastInstance).removeDistributedObjectListener("my-registration-id");
    }

    @Test
    public void getPartitionServiceCalledSuccessfullyOverOSGiInstance() {
        PartitionService mockPartitionService = Mockito.mock(PartitionService.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getPartitionService()).thenReturn(mockPartitionService);
        Assert.assertEquals(mockPartitionService, hazelcastOSGiInstance.getPartitionService());
        Mockito.verify(mockHazelcastInstance).getPartitionService();
    }

    @Test
    public void getQuorumServiceCalledSuccessfullyOverOSGiInstance() {
        QuorumService mockQuorumService = Mockito.mock(QuorumService.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getQuorumService()).thenReturn(mockQuorumService);
        Assert.assertEquals(mockQuorumService, hazelcastOSGiInstance.getQuorumService());
        Mockito.verify(mockHazelcastInstance).getQuorumService();
    }

    @Test
    public void getClientServiceCalledSuccessfullyOverOSGiInstance() {
        ClientService mockClientService = Mockito.mock(ClientService.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getClientService()).thenReturn(mockClientService);
        Assert.assertEquals(mockClientService, hazelcastOSGiInstance.getClientService());
        Mockito.verify(mockHazelcastInstance).getClientService();
    }

    @Test
    public void getLoggingServiceCalledSuccessfullyOverOSGiInstance() {
        LoggingService mockLoggingService = Mockito.mock(LoggingService.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getLoggingService()).thenReturn(mockLoggingService);
        Assert.assertEquals(mockLoggingService, hazelcastOSGiInstance.getLoggingService());
        Mockito.verify(mockHazelcastInstance).getLoggingService();
    }

    @Test
    public void getLifecycleServiceCalledSuccessfullyOverOSGiInstance() {
        LifecycleService mockLifecycleService = Mockito.mock(LifecycleService.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getLifecycleService()).thenReturn(mockLifecycleService);
        Assert.assertEquals(mockLifecycleService, hazelcastOSGiInstance.getLifecycleService());
        Mockito.verify(mockHazelcastInstance).getLifecycleService();
    }

    @Test
    public void getUserContextCalledSuccessfullyOverOSGiInstance() {
        ConcurrentMap<String, Object> mockUserContext = Mockito.mock(ConcurrentMap.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getUserContext()).thenReturn(mockUserContext);
        Assert.assertEquals(mockUserContext, hazelcastOSGiInstance.getUserContext());
        Mockito.verify(mockHazelcastInstance).getUserContext();
    }

    @Test
    public void getXAResourceCalledSuccessfullyOverOSGiInstance() {
        HazelcastXAResource mockXAResource = Mockito.mock(HazelcastXAResource.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getXAResource()).thenReturn(mockXAResource);
        Assert.assertEquals(mockXAResource, hazelcastOSGiInstance.getXAResource());
        Mockito.verify(mockHazelcastInstance).getXAResource();
    }

    @Test
    public void getConfigCalledSuccessfullyOverOSGiInstance() {
        Config mockConfig = Mockito.mock(Config.class);
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        Mockito.when(mockHazelcastInstance.getConfig()).thenReturn(mockConfig);
        Assert.assertEquals(mockConfig, hazelcastOSGiInstance.getConfig());
        Mockito.verify(mockHazelcastInstance).getConfig();
    }

    @Test
    public void shutdownCalledSuccessfullyOverOSGiInstance() {
        HazelcastInstance mockHazelcastInstance = Mockito.mock(HazelcastInstance.class);
        HazelcastOSGiInstance hazelcastOSGiInstance = HazelcastOSGiTestUtil.createHazelcastOSGiInstance(mockHazelcastInstance);
        hazelcastOSGiInstance.shutdown();
        Mockito.verify(mockHazelcastInstance).shutdown();
    }
}


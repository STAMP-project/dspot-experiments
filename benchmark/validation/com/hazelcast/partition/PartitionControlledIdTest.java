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
package com.hazelcast.partition;


import LockServiceImpl.SERVICE_NAME;
import StringPartitioningStrategy.INSTANCE;
import com.hazelcast.collection.impl.list.ListService;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.collection.impl.set.SetService;
import com.hazelcast.concurrent.atomiclong.AtomicLongService;
import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.concurrent.lock.LockStore;
import com.hazelcast.concurrent.semaphore.SemaphoreService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.Member;
import com.hazelcast.core.Partition;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionControlledIdTest extends HazelcastTestSupport {
    private static HazelcastInstance[] instances;

    @Test
    public void testLock() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        ILock lock = hz.getLock(("lock@" + partitionKey));
        lock.lock();
        Assert.assertEquals(("lock@" + partitionKey), lock.getName());
        Assert.assertEquals(partitionKey, lock.getPartitionKey());
        Node node = HazelcastTestSupport.getNode(hz);
        LockServiceImpl lockService = node.nodeEngine.getService(SERVICE_NAME);
        Partition partition = PartitionControlledIdTest.instances[0].getPartitionService().getPartition(partitionKey);
        LockStore lockStore = lockService.getLockStore(partition.getPartitionId(), new com.hazelcast.concurrent.lock.InternalLockNamespace(lock.getName()));
        Data key = node.getSerializationService().toData(lock.getName(), INSTANCE);
        Assert.assertTrue(lockStore.isLocked(key));
    }

    @Test
    public void testSemaphore() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        ISemaphore semaphore = hz.getSemaphore(("semaphore@" + partitionKey));
        semaphore.release();
        Assert.assertEquals(("semaphore@" + partitionKey), semaphore.getName());
        Assert.assertEquals(partitionKey, semaphore.getPartitionKey());
        SemaphoreService service = getNodeEngine(hz).getService(SemaphoreService.SERVICE_NAME);
        Assert.assertTrue(service.containsSemaphore(semaphore.getName()));
    }

    @Test
    public void testRingbuffer() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        Ringbuffer<String> ringbuffer = hz.getRingbuffer(("ringbuffer@" + partitionKey));
        ringbuffer.add("foo");
        Assert.assertEquals(("ringbuffer@" + partitionKey), ringbuffer.getName());
        Assert.assertEquals(partitionKey, ringbuffer.getPartitionKey());
        RingbufferService service = getNodeEngine(hz).getService(RingbufferService.SERVICE_NAME);
        final Map<ObjectNamespace, RingbufferContainer> partitionContainers = service.getContainers().get(service.getRingbufferPartitionId(ringbuffer.getName()));
        Assert.assertNotNull(partitionContainers);
        Assert.assertTrue(partitionContainers.containsKey(RingbufferService.getRingbufferNamespace(ringbuffer.getName())));
    }

    @Test
    public void testIdGenerator() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        IdGenerator idGenerator = hz.getIdGenerator(("idgenerator@" + partitionKey));
        idGenerator.newId();
        Assert.assertEquals(("idgenerator@" + partitionKey), idGenerator.getName());
        Assert.assertEquals(partitionKey, idGenerator.getPartitionKey());
        AtomicLongService service = getNodeEngine(hz).getService(AtomicLongService.SERVICE_NAME);
        Assert.assertTrue(service.containsAtomicLong(("hz:atomic:idGenerator:" + (idGenerator.getName()))));
    }

    @Test
    public void testAtomicLong() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        IAtomicLong atomicLong = hz.getAtomicLong(("atomiclong@" + partitionKey));
        atomicLong.incrementAndGet();
        Assert.assertEquals(("atomiclong@" + partitionKey), atomicLong.getName());
        Assert.assertEquals(partitionKey, atomicLong.getPartitionKey());
        AtomicLongService service = getNodeEngine(hz).getService(AtomicLongService.SERVICE_NAME);
        Assert.assertTrue(service.containsAtomicLong(atomicLong.getName()));
    }

    @Test
    public void testQueue() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        IQueue<String> queue = hz.getQueue(("queue@" + partitionKey));
        queue.add("");
        Assert.assertEquals(("queue@" + partitionKey), queue.getName());
        Assert.assertEquals(partitionKey, queue.getPartitionKey());
        QueueService service = getNodeEngine(hz).getService(QueueService.SERVICE_NAME);
        Assert.assertTrue(service.containsQueue(queue.getName()));
    }

    @Test
    public void testList() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        IList<String> list = hz.getList(("list@" + partitionKey));
        list.add("");
        Assert.assertEquals(("list@" + partitionKey), list.getName());
        Assert.assertEquals(partitionKey, list.getPartitionKey());
        ListService service = getNodeEngine(hz).getService(ListService.SERVICE_NAME);
        Assert.assertTrue(service.getContainerMap().containsKey(list.getName()));
    }

    @Test
    public void testSet() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        ISet<String> set = hz.getSet(("set@" + partitionKey));
        set.add("");
        Assert.assertEquals(("set@" + partitionKey), set.getName());
        Assert.assertEquals(partitionKey, set.getPartitionKey());
        SetService service = getNodeEngine(hz).getService(SetService.SERVICE_NAME);
        Assert.assertTrue(service.getContainerMap().containsKey(set.getName()));
    }

    @Test
    public void testCountDownLatch() {
        String partitionKey = "hazelcast";
        HazelcastInstance hz = getHazelcastInstance(partitionKey);
        ICountDownLatch countDownLatch = hz.getCountDownLatch(("countDownLatch@" + partitionKey));
        countDownLatch.trySetCount(1);
        Assert.assertEquals(("countDownLatch@" + partitionKey), countDownLatch.getName());
        Assert.assertEquals(partitionKey, countDownLatch.getPartitionKey());
        CountDownLatchService service = getNodeEngine(hz).getService(CountDownLatchService.SERVICE_NAME);
        Assert.assertTrue(service.containsLatch(countDownLatch.getName()));
    }

    @Test
    public void testObjectWithPartitionKeyAndTask() throws Exception {
        HazelcastInstance instance = PartitionControlledIdTest.instances[0];
        IExecutorService executorServices = instance.getExecutorService("executor");
        String partitionKey = "hazelcast";
        ISemaphore semaphore = instance.getSemaphore(("foobar@" + partitionKey));
        semaphore.release();
        PartitionControlledIdTest.ContainsSemaphoreTask task = new PartitionControlledIdTest.ContainsSemaphoreTask(semaphore.getName());
        Future<Boolean> future = executorServices.submitToKeyOwner(task, semaphore.getPartitionKey());
        Assert.assertTrue(future.get());
    }

    private static class ContainsSemaphoreTask implements HazelcastInstanceAware , Serializable , Callable<Boolean> {
        private final String semaphoreName;

        private transient HazelcastInstance hz;

        private ContainsSemaphoreTask(String semaphoreName) {
            this.semaphoreName = semaphoreName;
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.hz = hazelcastInstance;
        }

        @Override
        public Boolean call() {
            NodeEngineImpl nodeEngine = HazelcastTestSupport.getNode(hz).nodeEngine;
            SemaphoreService service = nodeEngine.getService(SemaphoreService.SERVICE_NAME);
            return service.containsSemaphore(semaphoreName);
        }
    }

    @Test
    public void testObjectWithPartitionKeyAndMap() throws Exception {
        HazelcastInstance instance = PartitionControlledIdTest.instances[0];
        IExecutorService executorServices = instance.getExecutorService("executor");
        String partitionKey = "hazelcast";
        String mapKey = "key@" + partitionKey;
        IMap<String, String> map = instance.getMap("map");
        map.put(mapKey, "foobar");
        ISemaphore semaphore = instance.getSemaphore(("s@" + partitionKey));
        semaphore.release();
        PartitionControlledIdTest.ContainsSemaphoreAndMapEntryTask task = new PartitionControlledIdTest.ContainsSemaphoreAndMapEntryTask(semaphore.getName(), mapKey);
        Map<Member, Future<Boolean>> futures = executorServices.submitToAllMembers(task);
        int count = 0;
        for (Future<Boolean> future : futures.values()) {
            count += (future.get()) ? 1 : 0;
        }
        Assert.assertEquals(1, count);
    }

    private static class ContainsSemaphoreAndMapEntryTask implements HazelcastInstanceAware , Serializable , Callable<Boolean> {
        private final String semaphoreName;

        private final String mapKey;

        private transient HazelcastInstance hz;

        private ContainsSemaphoreAndMapEntryTask(String semaphoreName, String mapKey) {
            this.semaphoreName = semaphoreName;
            this.mapKey = mapKey;
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.hz = hazelcastInstance;
        }

        @Override
        public Boolean call() {
            NodeEngineImpl nodeEngine = HazelcastTestSupport.getNode(hz).nodeEngine;
            SemaphoreService service = nodeEngine.getService(SemaphoreService.SERVICE_NAME);
            IMap map = hz.getMap("map");
            return (map.localKeySet().contains(mapKey)) && (service.containsSemaphore(semaphoreName));
        }
    }
}


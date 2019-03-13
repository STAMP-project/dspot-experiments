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
package com.hazelcast.cp.internal.datastructures.atomiclong;


import CPGroupStatus.DESTROYED;
import QueryPolicy.ANY_LOCAL;
import QueryPolicy.LEADER_LOCAL;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.datastructures.atomiclong.proxy.RaftAtomicLongProxy;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftAtomicLongBasicTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    private IAtomicLong atomicLong;

    private String name = "long1@group1";

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProxyOnMetadataCPGroup() {
        instances[0].getCPSubsystem().getAtomicLong("long1@metadata");
    }

    @Test
    public void testSet() {
        atomicLong.set(271);
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testGet() {
        Assert.assertEquals(0, atomicLong.get());
    }

    @Test
    public void testDecrementAndGet() {
        Assert.assertEquals((-1), atomicLong.decrementAndGet());
        Assert.assertEquals((-2), atomicLong.decrementAndGet());
    }

    @Test
    public void testIncrementAndGet() {
        Assert.assertEquals(1, atomicLong.incrementAndGet());
        Assert.assertEquals(2, atomicLong.incrementAndGet());
    }

    @Test
    public void testGetAndSet() {
        Assert.assertEquals(0, atomicLong.getAndSet(271));
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testAddAndGet() {
        Assert.assertEquals(271, atomicLong.addAndGet(271));
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testGetAndAdd() {
        Assert.assertEquals(0, atomicLong.getAndAdd(271));
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testCompareAndSet_whenSuccess() {
        Assert.assertTrue(atomicLong.compareAndSet(0, 271));
        Assert.assertEquals(271, atomicLong.get());
    }

    @Test
    public void testCompareAndSet_whenNotSuccess() {
        Assert.assertFalse(atomicLong.compareAndSet(172, 0));
        Assert.assertEquals(0, atomicLong.get());
    }

    @Test
    public void testAlter() {
        atomicLong.set(2);
        atomicLong.alter(new RaftAtomicLongBasicTest.MultiplyByTwo());
        Assert.assertEquals(4, atomicLong.get());
    }

    @Test
    public void testAlterAndGet() {
        atomicLong.set(2);
        long result = atomicLong.alterAndGet(new RaftAtomicLongBasicTest.MultiplyByTwo());
        Assert.assertEquals(4, result);
    }

    @Test
    public void testGetAndAlter() {
        atomicLong.set(2);
        long result = atomicLong.getAndAlter(new RaftAtomicLongBasicTest.MultiplyByTwo());
        Assert.assertEquals(2, result);
        Assert.assertEquals(4, atomicLong.get());
    }

    @Test
    public void testAlterAsync() throws InterruptedException, ExecutionException {
        atomicLong.set(2);
        ICompletableFuture<Void> f = atomicLong.alterAsync(new RaftAtomicLongBasicTest.MultiplyByTwo());
        f.get();
        Assert.assertEquals(4, atomicLong.get());
    }

    @Test
    public void testAlterAndGetAsync() throws InterruptedException, ExecutionException {
        atomicLong.set(2);
        ICompletableFuture<Long> f = atomicLong.alterAndGetAsync(new RaftAtomicLongBasicTest.MultiplyByTwo());
        long result = f.get();
        Assert.assertEquals(4, result);
    }

    @Test
    public void testGetAndAlterAsync() throws InterruptedException, ExecutionException {
        atomicLong.set(2);
        ICompletableFuture<Long> f = atomicLong.getAndAlterAsync(new RaftAtomicLongBasicTest.MultiplyByTwo());
        long result = f.get();
        Assert.assertEquals(2, result);
        Assert.assertEquals(4, atomicLong.get());
    }

    @Test
    public void testApply() {
        atomicLong.set(2);
        long result = atomicLong.apply(new RaftAtomicLongBasicTest.MultiplyByTwo());
        Assert.assertEquals(4, result);
        Assert.assertEquals(2, atomicLong.get());
    }

    @Test
    public void testApplyAsync() throws InterruptedException, ExecutionException {
        atomicLong.set(2);
        Future<Long> f = atomicLong.applyAsync(new RaftAtomicLongBasicTest.MultiplyByTwo());
        long result = f.get();
        Assert.assertEquals(4, result);
        Assert.assertEquals(2, atomicLong.get());
    }

    @Test
    public void testLocalGet_withLeaderLocalPolicy() {
        atomicLong.set(3);
        RaftAtomicLongProxy atomicLongProxy = ((RaftAtomicLongProxy) (atomicLong));
        long v = atomicLongProxy.localGet(LEADER_LOCAL);
        Assert.assertEquals(3, v);
    }

    @Test
    public void testLocalGet_withAnyLocalPolicy() {
        atomicLong.set(3);
        // I may not be the leader...
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftAtomicLongProxy atomicLongProxy = ((RaftAtomicLongProxy) (atomicLong));
                final long v = atomicLongProxy.localGet(ANY_LOCAL);
                Assert.assertEquals(3, v);
            }
        });
    }

    @Test
    public void testCreate_withDefaultGroup() {
        IAtomicLong atomicLong = createAtomicLong(HazelcastTestSupport.randomName());
        Assert.assertEquals(CPGroup.DEFAULT_GROUP_NAME, getGroupId(atomicLong).name());
    }

    @Test(expected = DistributedObjectDestroyedException.class)
    public void testUse_afterDestroy() {
        atomicLong.destroy();
        atomicLong.incrementAndGet();
    }

    @Test(expected = DistributedObjectDestroyedException.class)
    public void testCreate_afterDestroy() {
        atomicLong.destroy();
        atomicLong = createAtomicLong(name);
        atomicLong.incrementAndGet();
    }

    @Test
    public void testMultipleDestroy() {
        atomicLong.destroy();
        atomicLong.destroy();
    }

    @Test
    public void testRecreate_afterGroupDestroy() throws Exception {
        atomicLong.destroy();
        final CPGroupId groupId = getGroupId(atomicLong);
        final RaftInvocationManager invocationManager = getRaftInvocationManager(instances[0]);
        invocationManager.invoke(HazelcastRaftTestSupport.getRaftService(instances[0]).getMetadataGroupId(), new com.hazelcast.cp.internal.raftop.metadata.TriggerDestroyRaftGroupOp(groupId)).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                CPGroup group = invocationManager.<CPGroup>invoke(HazelcastRaftTestSupport.getMetadataGroupId(instances[0]), new com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp(groupId)).join();
                Assert.assertEquals(DESTROYED, group.status());
            }
        });
        try {
            atomicLong.incrementAndGet();
            Assert.fail();
        } catch (CPGroupDestroyedException ignored) {
        }
        atomicLong = createAtomicLong(name);
        CPGroupId newGroupId = getGroupId(atomicLong);
        Assert.assertNotEquals(groupId, newGroupId);
        atomicLong.incrementAndGet();
    }

    public static class MultiplyByTwo implements IFunction<Long, Long> {
        @Override
        public Long apply(Long input) {
            return input * 2;
        }
    }
}


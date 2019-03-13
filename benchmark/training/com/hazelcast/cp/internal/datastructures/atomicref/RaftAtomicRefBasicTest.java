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
package com.hazelcast.cp.internal.datastructures.atomicref;


import CPGroupStatus.DESTROYED;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IFunction;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftAtomicRefBasicTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    private IAtomicReference<String> atomicRef;

    private String name = "ref@group1";

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProxyOnMetadataCPGroup() {
        instances[0].getCPSubsystem().getAtomicReference("ref@metadata");
    }

    @Test
    public void test_compareAndSet() {
        Assert.assertTrue(atomicRef.compareAndSet(null, "str1"));
        Assert.assertEquals("str1", atomicRef.get());
        Assert.assertFalse(atomicRef.compareAndSet(null, "str1"));
        Assert.assertTrue(atomicRef.compareAndSet("str1", "str2"));
        Assert.assertEquals("str2", atomicRef.get());
        Assert.assertFalse(atomicRef.compareAndSet("str1", "str2"));
        Assert.assertTrue(atomicRef.compareAndSet("str2", null));
        Assert.assertNull(atomicRef.get());
        Assert.assertFalse(atomicRef.compareAndSet("str2", null));
    }

    @Test
    public void test_compareAndSetAsync() throws InterruptedException, ExecutionException {
        Assert.assertTrue(atomicRef.compareAndSetAsync(null, "str1").get());
        Assert.assertEquals("str1", atomicRef.getAsync().get());
        Assert.assertFalse(atomicRef.compareAndSetAsync(null, "str1").get());
        Assert.assertTrue(atomicRef.compareAndSetAsync("str1", "str2").get());
        Assert.assertEquals("str2", atomicRef.getAsync().get());
        Assert.assertFalse(atomicRef.compareAndSetAsync("str1", "str2").get());
        Assert.assertTrue(atomicRef.compareAndSetAsync("str2", null).get());
        Assert.assertNull(atomicRef.getAsync().get());
        Assert.assertFalse(atomicRef.compareAndSetAsync("str2", null).get());
    }

    @Test
    public void test_set() {
        atomicRef.set("str1");
        Assert.assertEquals("str1", atomicRef.get());
        Assert.assertEquals("str1", atomicRef.getAndSet("str2"));
        Assert.assertEquals("str2", atomicRef.get());
    }

    @Test
    public void test_setAsync() throws InterruptedException, ExecutionException {
        atomicRef.setAsync("str1").get();
        Assert.assertEquals("str1", atomicRef.get());
        Assert.assertEquals("str1", atomicRef.getAndSetAsync("str2").get());
        Assert.assertEquals("str2", atomicRef.get());
    }

    @Test
    public void test_isNull() throws InterruptedException, ExecutionException {
        Assert.assertTrue(atomicRef.isNull());
        Assert.assertTrue(atomicRef.isNullAsync().get());
        atomicRef.set("str1");
        Assert.assertFalse(atomicRef.isNull());
        Assert.assertFalse(atomicRef.isNullAsync().get());
    }

    @Test
    public void test_clear() {
        atomicRef.set("str1");
        atomicRef.clear();
        Assert.assertTrue(atomicRef.isNull());
    }

    @Test
    public void test_clearAsync() throws InterruptedException, ExecutionException {
        atomicRef.set("str1");
        atomicRef.clearAsync().get();
        Assert.assertTrue(atomicRef.isNull());
    }

    @Test
    public void test_contains() throws InterruptedException, ExecutionException {
        Assert.assertTrue(atomicRef.contains(null));
        Assert.assertTrue(atomicRef.containsAsync(null).get());
        Assert.assertFalse(atomicRef.contains("str1"));
        Assert.assertFalse(atomicRef.containsAsync("str1").get());
        atomicRef.set("str1");
        Assert.assertFalse(atomicRef.contains(null));
        Assert.assertFalse(atomicRef.containsAsync(null).get());
        Assert.assertTrue(atomicRef.contains("str1"));
        Assert.assertTrue(atomicRef.containsAsync("str1").get());
    }

    @Test
    public void test_alter() {
        atomicRef.set("str1");
        atomicRef.alter(new RaftAtomicRefBasicTest.AppendStringFunction("str2"));
        String val = atomicRef.get();
        Assert.assertEquals("str1 str2", val);
        val = atomicRef.alterAndGet(new RaftAtomicRefBasicTest.AppendStringFunction("str3"));
        Assert.assertEquals("str1 str2 str3", val);
        val = atomicRef.getAndAlter(new RaftAtomicRefBasicTest.AppendStringFunction("str4"));
        Assert.assertEquals("str1 str2 str3", val);
        Assert.assertEquals("str1 str2 str3 str4", atomicRef.get());
    }

    @Test
    public void test_alterAsync() throws InterruptedException, ExecutionException {
        atomicRef.set("str1");
        atomicRef.alterAsync(new RaftAtomicRefBasicTest.AppendStringFunction("str2")).get();
        String val = atomicRef.get();
        Assert.assertEquals("str1 str2", val);
        val = atomicRef.alterAndGetAsync(new RaftAtomicRefBasicTest.AppendStringFunction("str3")).get();
        Assert.assertEquals("str1 str2 str3", val);
        val = atomicRef.getAndAlterAsync(new RaftAtomicRefBasicTest.AppendStringFunction("str4")).get();
        Assert.assertEquals("str1 str2 str3", val);
        Assert.assertEquals("str1 str2 str3 str4", atomicRef.get());
    }

    @Test
    public void test_apply() throws InterruptedException, ExecutionException {
        atomicRef.set("str1");
        String val = atomicRef.apply(new RaftAtomicRefBasicTest.AppendStringFunction("str2"));
        Assert.assertEquals("str1 str2", val);
        Assert.assertEquals("str1", atomicRef.get());
        val = atomicRef.applyAsync(new RaftAtomicRefBasicTest.AppendStringFunction("str2")).get();
        Assert.assertEquals("str1 str2", val);
        Assert.assertEquals("str1", atomicRef.get());
    }

    @Test
    public void testCreate_withDefaultGroup() {
        IAtomicReference<String> atomicRef = createAtomicRef(HazelcastTestSupport.randomName());
        Assert.assertEquals(CPGroup.DEFAULT_GROUP_NAME, getGroupId(atomicRef).name());
    }

    @Test(expected = DistributedObjectDestroyedException.class)
    public void testUse_afterDestroy() {
        atomicRef.destroy();
        atomicRef.set("str1");
    }

    @Test(expected = DistributedObjectDestroyedException.class)
    public void testCreate_afterDestroy() {
        atomicRef.destroy();
        atomicRef = createAtomicRef(name);
        atomicRef.set("str1");
    }

    @Test
    public void testMultipleDestroy() {
        atomicRef.destroy();
        atomicRef.destroy();
    }

    @Test
    public void testRecreate_afterGroupDestroy() throws Exception {
        atomicRef.destroy();
        final CPGroupId groupId = getGroupId(atomicRef);
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
            atomicRef.get();
            Assert.fail();
        } catch (CPGroupDestroyedException ignored) {
        }
        atomicRef = createAtomicRef(name);
        Assert.assertNotEquals(groupId, getGroupId(atomicRef));
        atomicRef.set("str1");
    }

    public static class AppendStringFunction implements IFunction<String, String> {
        private String suffix;

        public AppendStringFunction(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public String apply(String input) {
            return (input + " ") + (suffix);
        }
    }
}


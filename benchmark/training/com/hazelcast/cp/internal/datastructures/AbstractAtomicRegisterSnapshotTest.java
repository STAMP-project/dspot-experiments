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
package com.hazelcast.cp.internal.datastructures;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.exception.CPSubsystemException;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractAtomicRegisterSnapshotTest<T> extends HazelcastRaftTestSupport {
    private static final int SNAPSHOT_THRESHOLD = 100;

    private HazelcastInstance[] instances;

    @Test
    public void test_snapshot() throws Exception {
        final T initialValue = setAndGetInitialValue();
        // force snapshot
        for (int i = 0; i < (AbstractAtomicRegisterSnapshotTest.SNAPSHOT_THRESHOLD); i++) {
            T v = readValue();
            Assert.assertEquals(initialValue, v);
        }
        // shutdown the last instance
        instances[((instances.length) - 1)].shutdown();
        final HazelcastInstance instance = factory.newHazelcastInstance(createConfig(3, 3));
        instance.getCPSubsystem().getCPSubsystemManagementService().promoteToCPMember().get();
        // Read from local CP member, which should install snapshot after promotion.
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                InternalCompletableFuture<Object> future = queryLocally(instance);
                try {
                    T value = getValue(future);
                    Assert.assertEquals(initialValue, value);
                } catch (CPSubsystemException e) {
                    // Raft node may not be created yet...
                    throw new AssertionError(e);
                }
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                InternalCompletableFuture<Object> future = queryLocally(instance);
                T value = getValue(future);
                Assert.assertEquals(initialValue, value);
            }
        }, 5);
    }
}


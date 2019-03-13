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
package com.hazelcast.client.cp.internal.datastructures.lock;


import com.hazelcast.client.cp.internal.session.ClientProxySessionManager;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.datastructures.lock.FencedLockBasicTest;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FencedLockClientBasicTest extends FencedLockBasicTest {
    @Test
    public void test_lockAutoRelease_onClientShutdown() {
        String proxyName = lock.getName();
        lock.lock();
        lockInstance.shutdown();
        Assert.assertFalse(instances[0].getCPSubsystem().getLock(proxyName).isLocked());
    }

    @Test
    public void test_sessionIsClosedOnCPSubsystemReset() throws Exception {
        lock.lock();
        instances[0].getCPSubsystem().getCPSubsystemManagementService().restart().get();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                HazelcastClientProxy clientProxy = ((HazelcastClientProxy) (lockInstance));
                ClientProxySessionManager proxySessionManager = clientProxy.client.getProxySessionManager();
                Assert.assertEquals(NO_SESSION_ID, proxySessionManager.getSession(((RaftGroupId) (lock.getGroupId()))));
            }
        });
    }
}


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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.Address;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InvocationMonitor_GetLastMemberHeartbeatMillisTest extends HazelcastTestSupport {
    public static final int CALL_TIMEOUT = 4000;

    private HazelcastInstance local;

    private HazelcastInstance remote;

    private InvocationMonitor invocationMonitor;

    private Address localAddress;

    private Address remoteAddress;

    @Test
    public void whenNullAddress() {
        long result = invocationMonitor.getLastMemberHeartbeatMillis(null);
        Assert.assertEquals(0, result);
    }

    @Test
    public void whenNonExistingAddress() throws Exception {
        Address address = new Address(localAddress.getHost(), ((localAddress.getPort()) - 1));
        long result = invocationMonitor.getLastMemberHeartbeatMillis(address);
        Assert.assertEquals(0, result);
    }

    @Test
    public void whenLocal() {
        final long startMillis = System.currentTimeMillis();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((startMillis + (TimeUnit.SECONDS.toMillis(5))) < (invocationMonitor.getLastMemberHeartbeatMillis(localAddress))));
            }
        });
    }

    @Test
    public void whenRemote() {
        final long startMillis = System.currentTimeMillis();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((startMillis + (TimeUnit.SECONDS.toMillis(5))) < (invocationMonitor.getLastMemberHeartbeatMillis(remoteAddress))));
            }
        });
    }

    @Test
    public void whenMemberDies_lastHeartbeatRemoved() {
        // trigger the sending of heartbeats
        DummyOperation op = new DummyOperation().setDelayMillis(((InvocationMonitor_GetLastMemberHeartbeatMillisTest.CALL_TIMEOUT) * 2));
        remote.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(0, invocationMonitor.getLastMemberHeartbeatMillis(remoteAddress));
            }
        });
    }
}


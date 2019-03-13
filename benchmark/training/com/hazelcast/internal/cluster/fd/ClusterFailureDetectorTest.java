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
package com.hazelcast.internal.cluster.fd;


import com.hazelcast.core.Member;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.Clock;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterFailureDetectorTest {
    private static final long HEARTBEAT_TIMEOUT = TimeUnit.SECONDS.toMillis(1);

    @Parameterized.Parameter
    public ClusterFailureDetectorType failureDetectorType;

    private ClusterFailureDetector failureDetector;

    @Test
    public void member_isNotAlive_whenNoHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        Assert.assertFalse(failureDetector.isAlive(member, Clock.currentTimeMillis()));
    }

    @Test
    public void member_isAlive_whenHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        Assert.assertTrue(failureDetector.isAlive(member, timestamp));
    }

    @Test
    public void member_isAlive_beforeHeartbeatTimeout() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        Assert.assertTrue(failureDetector.isAlive(member, (timestamp + ((ClusterFailureDetectorTest.HEARTBEAT_TIMEOUT) / 2))));
    }

    @Test
    public void member_isNotAlive_afterHeartbeatTimeout() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        long ts = timestamp + ((ClusterFailureDetectorTest.HEARTBEAT_TIMEOUT) * 2);
        Assert.assertFalse(("Suspicion level: " + (failureDetector.suspicionLevel(member, ts))), failureDetector.isAlive(member, ts));
    }

    @Test
    public void lastHeartbeat_whenNoHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long lastHeartbeat = failureDetector.lastHeartbeat(member);
        Assert.assertEquals(0L, lastHeartbeat);
    }

    @Test
    public void lastHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        long lastHeartbeat = failureDetector.lastHeartbeat(member);
        Assert.assertEquals(timestamp, lastHeartbeat);
    }

    @Test
    public void suspicionLevel_whenNoHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        double suspicionLevel = failureDetector.suspicionLevel(member, Clock.currentTimeMillis());
        double failureLevel = ClusterFailureDetectorTest.getFailureSuspicionLevel(failureDetector);
        Assert.assertEquals(failureLevel, suspicionLevel, 0.0);
    }

    @Test
    public void suspicionLevel_whenHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        double suspicionLevel = failureDetector.suspicionLevel(member, timestamp);
        Assert.assertEquals(0, suspicionLevel, 0.0);
    }

    @Test
    public void suspicionLevel_beforeHeartbeatTimeout() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        double suspicionLevel = failureDetector.suspicionLevel(member, (timestamp + ((ClusterFailureDetectorTest.HEARTBEAT_TIMEOUT) / 2)));
        double failureLevel = ClusterFailureDetectorTest.getFailureSuspicionLevel(failureDetector);
        Assert.assertThat(suspicionLevel, Matchers.lessThan(failureLevel));
    }

    @Test
    public void suspicionLevel_afterHeartbeatTimeout() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        double suspicionLevel = failureDetector.suspicionLevel(member, (timestamp + ((ClusterFailureDetectorTest.HEARTBEAT_TIMEOUT) * 2)));
        double failureLevel = ClusterFailureDetectorTest.getFailureSuspicionLevel(failureDetector);
        Assert.assertThat(suspicionLevel, Matchers.greaterThanOrEqualTo(failureLevel));
    }

    @Test
    public void remove_whenNoHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        failureDetector.remove(member);
        Assert.assertFalse(failureDetector.isAlive(member, Clock.currentTimeMillis()));
    }

    @Test
    public void remove_afterHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        failureDetector.remove(member);
        Assert.assertFalse(failureDetector.isAlive(member, Clock.currentTimeMillis()));
    }

    @Test
    public void reset_whenNoHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        failureDetector.reset();
        Assert.assertFalse(failureDetector.isAlive(member, Clock.currentTimeMillis()));
    }

    @Test
    public void reset_afterHeartbeat() throws Exception {
        Member member = ClusterFailureDetectorTest.newMember(5000);
        long timestamp = Clock.currentTimeMillis();
        failureDetector.heartbeat(member, timestamp);
        failureDetector.reset();
        Assert.assertFalse(failureDetector.isAlive(member, Clock.currentTimeMillis()));
    }
}


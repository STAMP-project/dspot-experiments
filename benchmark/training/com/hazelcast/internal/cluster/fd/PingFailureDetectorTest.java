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
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PingFailureDetectorTest {
    private PingFailureDetector failureDetector;

    @Test
    public void member_isNotAlive_whenNoHeartbeat() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        Assert.assertFalse(failureDetector.isAlive(member));
    }

    @Test
    public void member_isNotAlive_afterThreeAttempts() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        failureDetector.logAttempt(member);
        failureDetector.logAttempt(member);
        failureDetector.logAttempt(member);
        Assert.assertFalse(failureDetector.isAlive(member));
    }

    @Test
    public void member_isAlive_afterThreeAttempts_afterHeartbeat() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        failureDetector.logAttempt(member);
        failureDetector.logAttempt(member);
        failureDetector.logAttempt(member);
        failureDetector.heartbeat(member);
        Assert.assertTrue(failureDetector.isAlive(member));
    }

    @Test
    public void member_isAlive_whenHeartbeat() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        failureDetector.heartbeat(member);
        Assert.assertTrue(failureDetector.isAlive(member));
    }

    @Test
    public void member_isAlive_beforeHeartbeatTimeout() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        failureDetector.heartbeat(member);
        Assert.assertTrue(failureDetector.isAlive(member));
    }

    @Test
    public void remove_whenNoHeartbeat() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        failureDetector.remove(member);
        Assert.assertFalse(failureDetector.isAlive(member));
    }

    @Test
    public void remove_afterHeartbeat() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        failureDetector.heartbeat(member);
        failureDetector.remove(member);
        Assert.assertFalse(failureDetector.isAlive(member));
    }

    @Test
    public void reset_whenNoHeartbeat() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        failureDetector.reset();
        Assert.assertFalse(failureDetector.isAlive(member));
    }

    @Test
    public void reset_afterHeartbeat() throws Exception {
        Member member = PingFailureDetectorTest.newMember(5000);
        failureDetector.heartbeat(member);
        failureDetector.reset();
        Assert.assertFalse(failureDetector.isAlive(member));
    }
}


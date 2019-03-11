/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.highavailability.nonha.leaderelection;


import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.leaderelection.LeaderContender;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalListener;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the {@link SingleLeaderElectionService}.
 */
public class SingleLeaderElectionServiceTest {
    private static final Random RND = new Random();

    private final Executor executor = Executors.directExecutor();

    // ------------------------------------------------------------------------
    @Test
    public void testStartStopAssignLeadership() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final SingleLeaderElectionService service = new SingleLeaderElectionService(executor, uuid);
        final LeaderContender contender = SingleLeaderElectionServiceTest.mockContender(service);
        final LeaderContender otherContender = SingleLeaderElectionServiceTest.mockContender(service);
        service.start(contender);
        Mockito.verify(contender, Mockito.times(1)).grantLeadership(uuid);
        service.stop();
        Mockito.verify(contender, Mockito.times(1)).revokeLeadership();
        // start with a new contender - the old contender must not gain another leadership
        service.start(otherContender);
        Mockito.verify(otherContender, Mockito.times(1)).grantLeadership(uuid);
        Mockito.verify(contender, Mockito.times(1)).grantLeadership(uuid);
        Mockito.verify(contender, Mockito.times(1)).revokeLeadership();
    }

    @Test
    public void testStopBeforeConfirmingLeadership() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final SingleLeaderElectionService service = new SingleLeaderElectionService(executor, uuid);
        final LeaderContender contender = Mockito.mock(LeaderContender.class);
        service.start(contender);
        Mockito.verify(contender, Mockito.times(1)).grantLeadership(uuid);
        service.stop();
        // because the leadership was never confirmed, there is no "revoke" call
        Mockito.verifyNoMoreInteractions(contender);
    }

    @Test
    public void testStartOnlyOnce() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final SingleLeaderElectionService service = new SingleLeaderElectionService(executor, uuid);
        final LeaderContender contender = Mockito.mock(LeaderContender.class);
        final LeaderContender otherContender = Mockito.mock(LeaderContender.class);
        service.start(contender);
        Mockito.verify(contender, Mockito.times(1)).grantLeadership(uuid);
        // should not be possible to start again this with another contender
        try {
            service.start(otherContender);
            Assert.fail("should fail with an exception");
        } catch (IllegalStateException e) {
            // expected
        }
        // should not be possible to start this again with the same contender
        try {
            service.start(contender);
            Assert.fail("should fail with an exception");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testShutdown() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final SingleLeaderElectionService service = new SingleLeaderElectionService(executor, uuid);
        // create a leader contender and let it grab leadership
        final LeaderContender contender = SingleLeaderElectionServiceTest.mockContender(service);
        service.start(contender);
        Mockito.verify(contender, Mockito.times(1)).grantLeadership(uuid);
        // some leader listeners
        final LeaderRetrievalListener listener1 = Mockito.mock(LeaderRetrievalListener.class);
        final LeaderRetrievalListener listener2 = Mockito.mock(LeaderRetrievalListener.class);
        LeaderRetrievalService listenerService1 = service.createLeaderRetrievalService();
        LeaderRetrievalService listenerService2 = service.createLeaderRetrievalService();
        listenerService1.start(listener1);
        listenerService2.start(listener2);
        // one listener stops
        listenerService1.stop();
        // shut down the service
        service.shutdown();
        // the leader contender and running listener should get error notifications
        Mockito.verify(contender, Mockito.times(1)).handleError(ArgumentMatchers.any(Exception.class));
        Mockito.verify(listener2, Mockito.times(1)).handleError(ArgumentMatchers.any(Exception.class));
        // the stopped listener gets no notification
        Mockito.verify(listener1, Mockito.times(0)).handleError(ArgumentMatchers.any(Exception.class));
        // should not be possible to start again after shutdown
        try {
            service.start(contender);
            Assert.fail("should fail with an exception");
        } catch (IllegalStateException e) {
            // expected
        }
        // no additional leadership grant
        Mockito.verify(contender, Mockito.times(1)).grantLeadership(ArgumentMatchers.any(UUID.class));
    }

    @Test
    public void testImmediateShutdown() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final SingleLeaderElectionService service = new SingleLeaderElectionService(executor, uuid);
        service.shutdown();
        final LeaderContender contender = Mockito.mock(LeaderContender.class);
        // should not be possible to start
        try {
            service.start(contender);
            Assert.fail("should fail with an exception");
        } catch (IllegalStateException e) {
            // expected
        }
        // no additional leadership grant
        Mockito.verify(contender, Mockito.times(0)).grantLeadership(ArgumentMatchers.any(UUID.class));
    }
}


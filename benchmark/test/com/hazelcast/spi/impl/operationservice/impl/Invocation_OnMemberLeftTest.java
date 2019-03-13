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


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.StaticMemberNodeContext;
import com.hazelcast.spi.Operation;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_OnMemberLeftTest extends HazelcastTestSupport {
    private OperationServiceImpl localOperationService;

    private InvocationMonitor localInvocationMonitor;

    private HazelcastInstance remote;

    private MemberImpl remoteMember;

    private TestHazelcastInstanceFactory instanceFactory;

    @Test
    public void whenMemberLeaves() throws Exception {
        Future<Object> future = localOperationService.invokeOnTarget(null, new Invocation_OnMemberLeftTest.UnresponsiveTargetOperation(), remoteMember.getAddress());
        // Unresponsive operation should be executed before shutting down the node
        assertUnresponsiveOperationStarted();
        remote.getLifecycleService().terminate();
        try {
            future.get();
            Assert.fail("Invocation should have failed with MemberLeftException!");
        } catch (MemberLeftException e) {
            HazelcastTestSupport.ignore(e);
        }
    }

    @Test
    public void whenMemberRestarts_withSameAddress() throws Exception {
        whenMemberRestarts(new Runnable() {
            @Override
            public void run() {
                remote = instanceFactory.newHazelcastInstance(remoteMember.getAddress());
            }
        });
    }

    @Test
    public void whenMemberRestarts_withSameIdentity() throws Exception {
        whenMemberRestarts(new Runnable() {
            @Override
            public void run() {
                StaticMemberNodeContext nodeContext = new StaticMemberNodeContext(instanceFactory, remoteMember);
                remote = HazelcastInstanceFactory.newHazelcastInstance(new Config(), remoteMember.toString(), nodeContext);
            }
        });
    }

    private static class UnresponsiveTargetOperation extends Operation {
        static final String COMPLETION_FLAG = Invocation_OnMemberLeftTest.UnresponsiveTargetOperation.class.getName();

        @Override
        public void run() throws Exception {
            getNodeEngine().getHazelcastInstance().getUserContext().put(Invocation_OnMemberLeftTest.UnresponsiveTargetOperation.COMPLETION_FLAG, new Object());
        }

        @Override
        public boolean returnsResponse() {
            return false;
        }
    }
}


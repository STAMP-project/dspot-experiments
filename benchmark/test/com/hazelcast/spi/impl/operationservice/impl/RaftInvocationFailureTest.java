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
import com.hazelcast.core.IndeterminateOperationStateException;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.StaleAppendRequestException;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.exception.CallerNotMemberException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.MemberVersion;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftInvocationFailureTest extends HazelcastRaftTestSupport {
    private static final AtomicInteger COMMIT_COUNT = new AtomicInteger();

    private HazelcastInstance[] instances;

    private String groupName = "group";

    private int groupSize = 3;

    @Test
    public void test_invocationFailsOnMemberLeftException() throws InterruptedException, ExecutionException {
        CPGroupId groupId = getRaftInvocationManager(instances[0]).createRaftGroup(groupName).get();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        Future f = invoke();
        try {
            f.get(60, TimeUnit.SECONDS);
            Assert.fail();
        } catch (Exception e) {
            HazelcastTestSupport.assertInstanceOf(IndeterminateOperationStateException.class, e.getCause());
        }
        Assert.assertTrue(((RaftInvocationFailureTest.COMMIT_COUNT.get()) <= (groupSize)));
    }

    @Test
    public void test_invocationFailsWithMemberLeftException_when_thereAreRetryableExceptionsAfterwards() throws InterruptedException, ExecutionException {
        CPGroupId groupId = getRaftInvocationManager(instances[0]).createRaftGroup(groupName).get();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        Future f = invoke();
        try {
            f.get(60, TimeUnit.SECONDS);
            Assert.fail();
        } catch (Exception e) {
            HazelcastTestSupport.assertInstanceOf(IndeterminateOperationStateException.class, e.getCause());
        }
        Assert.assertTrue(((RaftInvocationFailureTest.COMMIT_COUNT.get()) > (groupSize)));
    }

    @Test
    public void test_invocationFailsWithStaleAppendRequestException_when_thereAreRetryableExceptionsAfterwards() throws InterruptedException, ExecutionException {
        CPGroupId groupId = getRaftInvocationManager(instances[0]).createRaftGroup(groupName).get();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        Future f = invoke();
        try {
            f.get(60, TimeUnit.SECONDS);
            Assert.fail();
        } catch (Exception e) {
            HazelcastTestSupport.assertInstanceOf(IndeterminateOperationStateException.class, e.getCause());
        }
        Assert.assertTrue(((RaftInvocationFailureTest.COMMIT_COUNT.get()) > (groupSize)));
    }

    @Test
    public void test_invocationFailsWithFirstMemberLeftException_when_thereAreIndeterminateOperationStateExceptionsAfterwards() throws InterruptedException, ExecutionException {
        CPGroupId groupId = getRaftInvocationManager(instances[0]).createRaftGroup(groupName).get();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        Future f = invoke();
        try {
            f.get(60, TimeUnit.SECONDS);
            Assert.fail();
        } catch (Exception e) {
            HazelcastTestSupport.assertInstanceOf(IndeterminateOperationStateException.class, e.getCause());
        }
        Assert.assertTrue(((RaftInvocationFailureTest.COMMIT_COUNT.get()) > (groupSize)));
    }

    @Test
    public void test_invocationFailsWitNonRetryableException_when_thereAreRetryableExceptionsAfterIndeterminateOperationState() throws InterruptedException, ExecutionException {
        CPGroupId groupId = getRaftInvocationManager(instances[0]).createRaftGroup(groupName).get();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        Future f = invoke();
        try {
            f.get(60, TimeUnit.SECONDS);
            Assert.fail();
        } catch (Exception e) {
            HazelcastTestSupport.assertInstanceOf(IllegalStateException.class, e.getCause());
        }
        Assert.assertTrue(((RaftInvocationFailureTest.COMMIT_COUNT.get()) > (groupSize)));
    }

    public static class CustomResponseOp extends RaftOp {
        @Override
        public Object run(CPGroupId groupId, long commitIndex) throws Exception {
            if ((RaftInvocationFailureTest.COMMIT_COUNT.incrementAndGet()) <= 3) {
                MemberImpl member = new MemberImpl(new Address("localhost", 1111), MemberVersion.UNKNOWN, false);
                throw new com.hazelcast.core.MemberLeftException(member);
            }
            throw new CallerNotMemberException("");
        }

        @Override
        protected String getServiceName() {
            return RaftService.SERVICE_NAME;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    public static class CustomResponseOp2 extends RaftOp implements IndeterminateOperationStateAware {
        @Override
        public Object run(CPGroupId groupId, long commitIndex) throws Exception {
            if ((RaftInvocationFailureTest.COMMIT_COUNT.incrementAndGet()) <= 3) {
                MemberImpl member = new MemberImpl(new Address("localhost", 1111), MemberVersion.UNKNOWN, false);
                throw new com.hazelcast.core.MemberLeftException(member);
            }
            throw new CallerNotMemberException("");
        }

        @Override
        public boolean isRetryableOnIndeterminateOperationState() {
            return true;
        }

        @Override
        protected String getServiceName() {
            return RaftService.SERVICE_NAME;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    public static class CustomResponseOp3 extends RaftOp implements IndeterminateOperationStateAware {
        @Override
        public Object run(CPGroupId groupId, long commitIndex) {
            if ((RaftInvocationFailureTest.COMMIT_COUNT.incrementAndGet()) <= 3) {
                throw new StaleAppendRequestException(null);
            }
            throw new CallerNotMemberException("");
        }

        @Override
        public boolean isRetryableOnIndeterminateOperationState() {
            return true;
        }

        @Override
        protected String getServiceName() {
            return RaftService.SERVICE_NAME;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    public static class CustomResponseOp4 extends RaftOp implements IndeterminateOperationStateAware {
        @Override
        public Object run(CPGroupId groupId, long commitIndex) throws Exception {
            RaftInvocationFailureTest.COMMIT_COUNT.incrementAndGet();
            MemberImpl member = new MemberImpl(new Address("localhost", 1111), MemberVersion.UNKNOWN, false);
            throw new com.hazelcast.core.MemberLeftException(member);
        }

        @Override
        public boolean isRetryableOnIndeterminateOperationState() {
            return true;
        }

        @Override
        protected String getServiceName() {
            return RaftService.SERVICE_NAME;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    public static class CustomResponseOp5 extends RaftOp implements IndeterminateOperationStateAware {
        @Override
        public Object run(CPGroupId groupId, long commitIndex) {
            if ((RaftInvocationFailureTest.COMMIT_COUNT.incrementAndGet()) <= 3) {
                throw new StaleAppendRequestException(null);
            }
            throw new IllegalStateException("");
        }

        @Override
        public boolean isRetryableOnIndeterminateOperationState() {
            return true;
        }

        @Override
        protected String getServiceName() {
            return RaftService.SERVICE_NAME;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }
}


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
package com.hazelcast.quorum.scheduledexecutor;


import com.hazelcast.core.Member;
import com.hazelcast.quorum.AbstractQuorumTest;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.test.HazelcastSerialParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastSerialParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ScheduledExecutorQuorumWriteTest extends AbstractQuorumTest {
    @Parameterized.Parameter
    public static QuorumType quorumType;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void schedule_runnable_quorum() throws Exception {
        exec(0).schedule(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test(expected = QuorumException.class)
    public void schedule_runnable_noQuorum() throws Exception {
        exec(3).schedule(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void schedule_callable_quorum() throws Exception {
        exec(0).schedule(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test(expected = QuorumException.class)
    public void schedule_callable_noQuorum() throws Exception {
        exec(3).schedule(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void scheduleAtFixedRate_callable_quorum() {
        exec(0).scheduleAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), 10, 10, TimeUnit.MILLISECONDS).cancel(false);
    }

    @Test(expected = QuorumException.class)
    public void scheduleAtFixedRate_callable_noQuorum() throws Exception {
        exec(3).scheduleAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), 10, 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void scheduleOnMember_runnable_quorum() throws Exception {
        exec(0).scheduleOnMember(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), member(0), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnMember_runnable_noQuorum() throws Exception {
        exec(3).scheduleOnMember(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), member(3), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void scheduleOnMember_callable_quorum() throws Exception {
        exec(0).scheduleOnMember(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), member(0), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnMember_callable_noQuorum() throws Exception {
        exec(3).scheduleOnMember(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), member(3), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void scheduleOnMemberAtFixedRate_runnable_quorum() {
        exec(0).scheduleOnMemberAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), member(0), 10, 10, TimeUnit.MILLISECONDS).cancel(false);
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnMemberAtFixedRate_runnable_noQuorum() throws Exception {
        exec(3).scheduleOnMemberAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), member(3), 10, 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void scheduleOnKeyOwner_runnable_quorum() throws Exception {
        exec(0).scheduleOnKeyOwner(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), key(0), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnKeyOwner_runnable_noQuorum() throws Exception {
        exec(3).scheduleOnKeyOwner(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), key(3), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void scheduleOnKeyOwner_callable_quorum() throws Exception {
        exec(0).scheduleOnKeyOwner(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), key(0), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnKeyOwner_callable_noQuorum() throws Exception {
        exec(3).scheduleOnKeyOwner(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), key(3), 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void scheduleOnKeyOwnerAtFixedRate_runnable_quorum() {
        exec(0).scheduleOnKeyOwnerAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), key(0), 10, 10, TimeUnit.MILLISECONDS).cancel(false);
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnKeyOwnerAtFixedRate_runnable_noQuorum() throws Exception {
        exec(3).scheduleOnKeyOwnerAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), key(3), 10, 10, TimeUnit.MILLISECONDS).get();
    }

    @Test
    public void scheduleOnAllMembers_runnable_quorum() throws Exception {
        wait(exec(0).scheduleOnAllMembers(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), 10, TimeUnit.MILLISECONDS));
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnAllMembers_runnable_noQuorum() throws Exception {
        wait(exec(3).scheduleOnAllMembers(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), 10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void scheduleOnAllMembers_callable_quorum() throws Exception {
        wait(exec(0).scheduleOnAllMembers(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), 10, TimeUnit.MILLISECONDS));
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnAllMembers_callable_noQuorum() throws Exception {
        wait(exec(3).scheduleOnAllMembers(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), 10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void scheduleOnAllMembersAtFixedRate_runnable_quorum() {
        cancel(exec(0).scheduleOnAllMembersAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), 10, 10, TimeUnit.MILLISECONDS));
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnAllMembersAtFixedRate_runnable_noQuorum() throws Exception {
        wait(exec(3).scheduleOnAllMembersAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), 10, 10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void scheduleOnMembers_runnable_quorum() throws Exception {
        wait(exec(0).scheduleOnMembers(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), Arrays.asList(member(0)), 10, TimeUnit.MILLISECONDS));
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnMembers_runnable_noQuorum() throws Exception {
        wait(exec(3).scheduleOnMembers(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), Arrays.asList(member(3)), 10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void scheduleOnMembers_callable_quorum() throws Exception {
        Map<Member, IScheduledFuture<?>> futures = ((Map<Member, IScheduledFuture<?>>) (exec(0).scheduleOnMembers(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), Arrays.asList(member(0)), 10, TimeUnit.MILLISECONDS)));
        wait(futures);
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnMembers_callable_noQuorum() throws Exception {
        Map<Member, IScheduledFuture<?>> futures = ((Map<Member, IScheduledFuture<?>>) (exec(3).scheduleOnMembers(ScheduledExecutorQuorumWriteTest.ExecRunnable.callable(), Arrays.asList(member(3)), 10, TimeUnit.MILLISECONDS)));
        wait(futures);
    }

    @Test
    public void scheduleOnMembersAtFixedRate_runnable_quorum() {
        cancel(exec(0).scheduleOnMembersAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), Arrays.asList(member(0)), 10, 10, TimeUnit.MILLISECONDS));
    }

    @Test(expected = QuorumException.class)
    public void scheduleOnMembersAtFixedRate_runnable_noQuorum() throws Exception {
        wait(exec(3).scheduleOnMembersAtFixedRate(ScheduledExecutorQuorumWriteTest.ExecRunnable.runnable(), Arrays.asList(member(3)), 10, 10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shutdown_quorum() {
        exec(0, "shutdown").shutdown();
    }

    @Test(expected = QuorumException.class)
    public void shutdown_noQuorum() {
        exec(3, "shutdown").shutdown();
    }

    static class ExecRunnable implements Serializable , Runnable , Callable {
        @Override
        public Object call() throws Exception {
            return "response";
        }

        public void run() {
        }

        public static Runnable runnable() {
            return new ScheduledExecutorQuorumWriteTest.ExecRunnable();
        }

        public static Callable callable() {
            return new ScheduledExecutorQuorumWriteTest.ExecRunnable();
        }
    }
}


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
package com.hazelcast.quorum.durableexecutor;


import com.hazelcast.durableexecutor.StaleTaskIdException;
import com.hazelcast.quorum.AbstractQuorumTest;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.test.HazelcastSerialParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastSerialParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DurableExecutorQuorumWriteTest extends AbstractQuorumTest {
    @Parameterized.Parameter
    public static QuorumType quorumType;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void disposeResult_quorum() {
        try {
            exec(0).disposeResult(123L);
        } catch (StaleTaskIdException ex) {
            // expected & meaningless since not a real taskId
        }
    }

    @Test
    public void disposeResult_noQuorum() {
        expectedException.expect(CoreMatchers.isA(QuorumException.class));
        exec(3).disposeResult(125L);
    }

    @Test
    public void retrieveAndDisposeResult_quorum() throws Exception {
        try {
            exec(0).retrieveAndDisposeResult(123L).get();
        } catch (ExecutionException ex) {
            if ((ex.getCause()) instanceof StaleTaskIdException) {
                // expected & meaningless since not a real taskId
            }
        }
    }

    @Test
    public void retrieveAndDisposeResult_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).retrieveAndDisposeResult(125L).get();
    }

    @Test
    public void executeOnKeyOwner_quorum() {
        exec(0).executeOnKeyOwner(DurableExecutorQuorumWriteTest.ExecRunnable.runnable(), key(0));
    }

    @Test
    public void executeOnKeyOwner_noQuorum() {
        // fire and forget operation, no quorum exception propagation
        // expectedException.expectCause(isA(QuorumException.class));
        exec(3).executeOnKeyOwner(DurableExecutorQuorumWriteTest.ExecRunnable.runnable(), key(3));
    }

    @Test
    public void execute_quorum() {
        exec(0).execute(DurableExecutorQuorumWriteTest.ExecRunnable.runnable());
    }

    @Test
    public void execute_noQuorum() {
        // fire and forget operation, no quorum exception propagation
        // expectedException.expectCause(isA(QuorumException.class));
        exec(3).execute(DurableExecutorQuorumWriteTest.ExecRunnable.runnable());
    }

    @Test
    public void submit_runnable_quorum() throws Exception {
        exec(0).submit(DurableExecutorQuorumWriteTest.ExecRunnable.runnable()).get();
    }

    @Test
    public void submit_runnable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submit(DurableExecutorQuorumWriteTest.ExecRunnable.runnable()).get();
    }

    @Test
    public void submit_runnable_result_quorum() throws Exception {
        exec(0).submit(DurableExecutorQuorumWriteTest.ExecRunnable.runnable(), "result").get();
    }

    @Test
    public void submit_runnable_result_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submit(DurableExecutorQuorumWriteTest.ExecRunnable.runnable(), "result").get();
    }

    @Test
    public void submit_callable_quorum() throws Exception {
        exec(0).submit(DurableExecutorQuorumWriteTest.ExecRunnable.callable()).get();
    }

    @Test
    public void submit_callable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submit(DurableExecutorQuorumWriteTest.ExecRunnable.callable()).get();
    }

    @Test
    public void submitToKeyOwner_callable_quorum() throws Exception {
        exec(0).submitToKeyOwner(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), key(0)).get();
    }

    @Test
    public void submitToKeyOwner_callable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submitToKeyOwner(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), key(3)).get();
    }

    @Test
    public void submitToKeyOwner_runnable_quorum() throws Exception {
        exec(0).submitToKeyOwner(DurableExecutorQuorumWriteTest.ExecRunnable.runnable(), key(0)).get();
    }

    @Test
    public void submitToKeyOwner_runnable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submitToKeyOwner(DurableExecutorQuorumWriteTest.ExecRunnable.runnable(), key(3)).get();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAll_quorum() throws Exception {
        wait(exec(0).invokeAll(Arrays.<Callable<Object>>asList(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), DurableExecutorQuorumWriteTest.ExecRunnable.callable())));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAll_noQuorum() throws Exception {
        wait(exec(3).invokeAll(Arrays.<Callable<Object>>asList(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), DurableExecutorQuorumWriteTest.ExecRunnable.callable())));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAll_timeout_quorum() throws Exception {
        wait(exec(0).invokeAll(Arrays.<Callable<Object>>asList(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), DurableExecutorQuorumWriteTest.ExecRunnable.callable()), 10L, TimeUnit.SECONDS));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAll_timeout_noQuorum() throws Exception {
        wait(exec(3).invokeAll(Arrays.<Callable<Object>>asList(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), DurableExecutorQuorumWriteTest.ExecRunnable.callable()), 10L, TimeUnit.SECONDS));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAny_quorum() throws Exception {
        exec(0).invokeAny(Arrays.<Callable<Object>>asList(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), DurableExecutorQuorumWriteTest.ExecRunnable.callable()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAny_noQuorum() throws Exception {
        exec(3).invokeAny(Arrays.<Callable<Object>>asList(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), DurableExecutorQuorumWriteTest.ExecRunnable.callable()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAny_timeout_quorum() throws Exception {
        exec(0).invokeAny(Arrays.<Callable<Object>>asList(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), DurableExecutorQuorumWriteTest.ExecRunnable.callable()), 10L, TimeUnit.SECONDS);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAny_timeout_noQuorum() throws Exception {
        exec(3).invokeAny(Arrays.<Callable<Object>>asList(DurableExecutorQuorumWriteTest.ExecRunnable.callable(), DurableExecutorQuorumWriteTest.ExecRunnable.callable()), 10L, TimeUnit.SECONDS);
    }

    @Test
    public void shutdown_quorum() {
        exec(0, "shutdown").shutdown();
    }

    @Test(expected = QuorumException.class)
    public void shutdown_noQuorum() {
        exec(3, "shutdown").shutdown();
    }

    @Test
    public void shutdownNow_quorum() {
        exec(0, "shutdownNow").shutdownNow();
    }

    @Test(expected = QuorumException.class)
    public void shutdownNow_noQuorum() {
        exec(3, "shutdownNow").shutdownNow();
    }

    static class ExecRunnable implements Serializable , Runnable , Callable {
        @Override
        public Object call() throws Exception {
            return "response";
        }

        public void run() {
        }

        public static Runnable runnable() {
            return new DurableExecutorQuorumWriteTest.ExecRunnable();
        }

        public static Callable callable() {
            return new DurableExecutorQuorumWriteTest.ExecRunnable();
        }
    }
}


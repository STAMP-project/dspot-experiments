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
package com.hazelcast.quorum.executor;


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.core.MultiExecutionCallback;
import com.hazelcast.quorum.AbstractQuorumTest;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.test.HazelcastSerialParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ExceptionUtil;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
public class ExecutorQuorumWriteTest extends AbstractQuorumTest {
    @Parameterized.Parameter
    public static QuorumType quorumType;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void executeOnAllMembers_quorum() {
        exec(0).executeOnAllMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable());
    }

    @Test
    public void executeOnAllMembers_noQuorum() {
        // fire and forget operation, no quorum exception propagation
        // expectedException.expectCause(isA(QuorumException.class));
        exec(3).executeOnAllMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable());
    }

    @Test
    public void executeOnKeyOwner_quorum() {
        exec(0).executeOnKeyOwner(ExecutorQuorumWriteTest.ExecRunnable.runnable(), key(0));
    }

    @Test
    public void executeOnKeyOwner_noQuorum() {
        // fire and forget operation, no quorum exception propagation
        // expectedException.expectCause(isA(QuorumException.class));
        exec(3).executeOnKeyOwner(ExecutorQuorumWriteTest.ExecRunnable.runnable(), key(3));
    }

    @Test
    public void executeOnMember_quorum() {
        exec(0).executeOnMember(ExecutorQuorumWriteTest.ExecRunnable.runnable(), member(0));
    }

    @Test
    public void executeOnMember_noQuorum() {
        // fire and forget operation, no quorum exception propagation
        // expectedException.expectCause(isA(QuorumException.class));
        exec(3).executeOnMember(ExecutorQuorumWriteTest.ExecRunnable.runnable(), member(3));
    }

    @Test
    public void executeOnMembers_collection_quorum() {
        exec(0).executeOnMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), Arrays.asList(member(0)));
    }

    @Test
    public void executeOnMembers_collection_noQuorum() {
        // fire and forget operation, no quorum exception propagation
        // expectedException.expectCause(isA(QuorumException.class));
        exec(3).executeOnMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), Arrays.asList(member(3)));
    }

    @Test
    public void executeOnMembers_selector_quorum() {
        exec(0).executeOnMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), ExecutorQuorumWriteTest.Selector.selector(0));
    }

    @Test
    public void executeOnMembers_selector_noQuorum() {
        // fire and forget operation, no quorum exception propagation
        // expectedException.expectCause(isA(QuorumException.class));
        exec(3).executeOnMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), ExecutorQuorumWriteTest.Selector.selector(3));
    }

    @Test
    public void execute_quorum() {
        exec(0).execute(ExecutorQuorumWriteTest.ExecRunnable.runnable());
    }

    @Test
    public void execute_noQuorum() {
        // fire and forget operation, no quorum exception propagation
        // expectedException.expectCause(isA(QuorumException.class));
        exec(3).execute(ExecutorQuorumWriteTest.ExecRunnable.runnable());
    }

    @Test
    public void submit_runnable_quorum() throws Exception {
        exec(0).submit(ExecutorQuorumWriteTest.ExecRunnable.runnable()).get();
    }

    @Test
    public void submit_runnable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submit(ExecutorQuorumWriteTest.ExecRunnable.runnable()).get();
    }

    @Test
    public void submit_runnable_result_quorum() throws Exception {
        exec(0).submit(ExecutorQuorumWriteTest.ExecRunnable.runnable(), "result").get();
    }

    @Test
    public void submit_runnable_result_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submit(ExecutorQuorumWriteTest.ExecRunnable.runnable(), "result").get();
    }

    @Test
    public void submit_runnable_selector_quorum() throws Exception {
        exec(0).submit(ExecutorQuorumWriteTest.ExecRunnable.runnable(), ExecutorQuorumWriteTest.Selector.selector(0)).get();
    }

    @Test
    public void submit_runnable_selector_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submit(ExecutorQuorumWriteTest.ExecRunnable.runnable(), ExecutorQuorumWriteTest.Selector.selector(3)).get();
    }

    @Test
    public void submit_runnable_selector_callback_quorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(0).submit(ExecutorQuorumWriteTest.ExecRunnable.runnable(), ExecutorQuorumWriteTest.Selector.selector(0), callback);
        callback.get();
    }

    @Test
    public void submit_runnable_selector_callback_noQuorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(3).submit(ExecutorQuorumWriteTest.ExecRunnable.runnable(), ExecutorQuorumWriteTest.Selector.selector(3), ExecutorQuorumWriteTest.Callback.callback());
        expectQuorumException(callback);
    }

    @Test
    public void submit_callable_quorum() throws Exception {
        exec(0).submit(ExecutorQuorumWriteTest.ExecRunnable.callable()).get();
    }

    @Test
    public void submit_callable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submit(ExecutorQuorumWriteTest.ExecRunnable.callable()).get();
    }

    @Test
    public void submit_callable_selector_quorum() throws Exception {
        exec(0).submit(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.Selector.selector(0)).get();
    }

    @Test
    public void submit_callable_selector_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submit(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.Selector.selector(3)).get();
    }

    @Test
    public void submit_callable_selector_callback_quorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(0).submit(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.Selector.selector(0), callback);
        callback.get();
    }

    @Test
    public void submit_callable_selector_callback_noQuorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(3).submit(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.Selector.selector(3), ExecutorQuorumWriteTest.Callback.callback());
        expectQuorumException(callback);
    }

    @Test
    public void submitToAllMembers_callable_quorum() throws Exception {
        wait(exec(0).submitToAllMembers(ExecutorQuorumWriteTest.ExecRunnable.callable()));
    }

    @Test
    public void submitToAllMembers_callable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        wait(exec(3).submitToAllMembers(ExecutorQuorumWriteTest.ExecRunnable.callable()));
    }

    @Test
    public void submitToAllMembers_callable_multiCallback_quorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(0).submitToAllMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), multiCallback);
        multiCallback.get();
    }

    @Test
    public void submitToAllMembers_callable_multiCallback_noQuorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(3).submitToAllMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), multiCallback);
        expectQuorumException(multiCallback);
    }

    @Test
    public void submitToAllMembers_runnable_multiCallback_quorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(0).submitToAllMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), multiCallback);
        multiCallback.get();
    }

    @Test
    public void submitToAllMembers_runnable_multiCallback_noQuorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(3).submitToAllMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), multiCallback);
        expectQuorumException(multiCallback);
    }

    @Test
    public void submitToKeyOwner_callable_quorum() throws Exception {
        exec(0).submitToKeyOwner(ExecutorQuorumWriteTest.ExecRunnable.callable(), key(0)).get();
    }

    @Test
    public void submitToKeyOwner_callable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submitToKeyOwner(ExecutorQuorumWriteTest.ExecRunnable.callable(), key(3)).get();
    }

    @Test
    public void submitToKeyOwner_runnable_callback_quorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(0).submitToKeyOwner(ExecutorQuorumWriteTest.ExecRunnable.runnable(), key(0), callback);
        callback.get();
    }

    @Test
    public void submitToKeyOwner_runnable_callback_noQuorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(3).submitToKeyOwner(ExecutorQuorumWriteTest.ExecRunnable.runnable(), key(3), callback);
        expectQuorumException(callback);
    }

    @Test
    public void submitToKeyOwner_callable_callback_quorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(0).submitToKeyOwner(ExecutorQuorumWriteTest.ExecRunnable.callable(), key(0), callback);
        callback.get();
    }

    @Test
    public void submitToKeyOwner_callable_callback_noQuorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(3).submitToKeyOwner(ExecutorQuorumWriteTest.ExecRunnable.callable(), key(3), callback);
        expectQuorumException(callback);
    }

    @Test
    public void submitToMember_callable_quorum() throws Exception {
        exec(0).submitToMember(ExecutorQuorumWriteTest.ExecRunnable.callable(), member(0)).get();
    }

    @Test
    public void submitToMember_callable_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        exec(3).submitToMember(ExecutorQuorumWriteTest.ExecRunnable.callable(), member(3)).get();
    }

    @Test
    public void submitToMember_runnable_callback_quorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(0).submitToMember(ExecutorQuorumWriteTest.ExecRunnable.runnable(), member(0), callback);
        callback.get();
    }

    @Test
    public void submitToMember_runnable_callback_noQuorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(3).submitToMember(ExecutorQuorumWriteTest.ExecRunnable.runnable(), member(3), callback);
        expectQuorumException(callback);
    }

    @Test
    public void submitToMember_callable_callback_quorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(0).submitToMember(ExecutorQuorumWriteTest.ExecRunnable.callable(), member(0), callback);
        callback.get();
    }

    @Test
    public void submitToMember_callable_callback_noQuorum() {
        ExecutorQuorumWriteTest.Callback callback = ExecutorQuorumWriteTest.Callback.callback();
        exec(3).submitToMember(ExecutorQuorumWriteTest.ExecRunnable.callable(), member(3), callback);
        expectQuorumException(callback);
    }

    @Test
    public void submitToMembers_callable_member_quorum() throws Exception {
        wait(exec(0).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), Arrays.asList(member(0))));
    }

    @Test
    public void submitToMembers_callable_member_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        wait(exec(3).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), Arrays.asList(member(3))));
    }

    @Test
    public void submitToMembers_callable_member_callback_quorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(0).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), Arrays.asList(member(0)), multiCallback);
        multiCallback.get();
    }

    @Test
    public void submitToMembers_callable_member_callback_noQuorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(3).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), Arrays.asList(member(3)), multiCallback);
        expectQuorumException(multiCallback);
    }

    @Test
    public void submitToMembers_callable_selector_quorum() throws Exception {
        wait(exec(0).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.Selector.selector(0)));
    }

    @Test
    public void submitToMembers_callable_selector_noQuorum() throws Exception {
        expectedException.expectCause(CoreMatchers.isA(QuorumException.class));
        wait(exec(3).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.Selector.selector(3)));
    }

    @Test
    public void submitToMembers_callable_selector_callback_quorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(0).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.Selector.selector(0), multiCallback);
        multiCallback.get();
    }

    @Test
    public void submitToMembers_callable_selector_callback_noQuorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(3).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.Selector.selector(3), multiCallback);
        expectQuorumException(multiCallback);
    }

    @Test
    public void submitToMembers_runnable_selector_callback_quorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(0).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), ExecutorQuorumWriteTest.Selector.selector(0), multiCallback);
        multiCallback.get();
    }

    @Test
    public void submitToMembers_runnable_selector_callback_noQuorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(3).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), ExecutorQuorumWriteTest.Selector.selector(3), multiCallback);
        expectQuorumException(multiCallback);
    }

    @Test
    public void submitToMembers_runnable_member_callback_quorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(0).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), Arrays.asList(member(0)), multiCallback);
        multiCallback.get();
    }

    @Test
    public void submitToMembers_runnable_member_callback_noQuorum() {
        ExecutorQuorumWriteTest.MultiCallback multiCallback = ExecutorQuorumWriteTest.MultiCallback.multiCallback();
        exec(3).submitToMembers(ExecutorQuorumWriteTest.ExecRunnable.runnable(), Arrays.asList(member(3)), multiCallback);
        expectQuorumException(multiCallback);
    }

    @Test
    public void invokeAll_quorum() throws Exception {
        wait(exec(0).invokeAll(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable())));
    }

    @Test
    public void invokeAll_noQuorum() throws Exception {
        expectQuorumException(exec(3).invokeAll(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable())));
    }

    @Test
    public void invokeAll_timeout_quorum_short_timeout() throws Exception {
        List<? extends Future<?>> futures = exec(0).invokeAll(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable()), 10L, TimeUnit.SECONDS);
        // 10s is relatively short timeout -> there is some chance the task will be cancelled before it
        // had a chance to be executed. especially in slow environments -> we have to tolerate the CancellationException
        // see the test bellow for a scenario where the timeout is sufficiently long
        assertAllowedException(futures, CancellationException.class);
    }

    @Test
    public void invokeAll_timeout_quorum_long_timeout() throws Exception {
        // 30s is a long enough timeout - the task should never be cancelled -> any exception means a test failure
        wait(exec(0).invokeAll(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable()), 30L, TimeUnit.SECONDS));
    }

    @Test
    public void invokeAll_timeout_noQuorum() throws Exception {
        expectQuorumException(exec(3).invokeAll(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable()), 10L, TimeUnit.SECONDS));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAny_quorum() throws Exception {
        exec(0).invokeAny(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAny_noQuorum() throws Exception {
        exec(3).invokeAny(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAny_timeout_quorum() throws Exception {
        exec(0).invokeAny(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable()), 10L, TimeUnit.SECONDS);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeAny_timeout_noQuorum() throws Exception {
        exec(3).invokeAny(Arrays.<Callable<Object>>asList(ExecutorQuorumWriteTest.ExecRunnable.callable(), ExecutorQuorumWriteTest.ExecRunnable.callable()), 10L, TimeUnit.SECONDS);
    }

    @Test
    public void shutdown_quorum() {
        exec(0, "shutdown").shutdown();
    }

    @Test
    public void shutdown_noQuorum() {
        try {
            exec(3, "shutdown").shutdown();
        } catch (QuorumException ex) {
            // best effort - server will throw with best effort basis, client will never throw due to API
        }
    }

    @Test
    public void shutdownNow_quorum() {
        exec(0, "shutdownNow").shutdownNow();
    }

    @Test
    public void shutdownNow_noQuorum() {
        try {
            exec(3, "shutdownNow").shutdownNow();
        } catch (QuorumException ex) {
            // best effort - server will throw with best effort basis, client will never throw due to API
        }
    }

    public static class ExecRunnable implements Serializable , Runnable , Callable {
        @Override
        public Object call() throws Exception {
            return "response";
        }

        public void run() {
        }

        public static Runnable runnable() {
            return new ExecutorQuorumWriteTest.ExecRunnable();
        }

        public static Callable callable() {
            return new ExecutorQuorumWriteTest.ExecRunnable();
        }
    }

    static class Selector implements MemberSelector {
        private int index;

        Selector(int index) {
            this.index = index;
        }

        @Override
        public boolean select(Member member) {
            return ((member.getAddress().getPort()) % ((HazelcastTestSupport.getNode(AbstractQuorumTest.cluster.getInstance(0)).getThisAddress().getPort()) + (index))) == 0;
        }

        public static MemberSelector selector(int index) {
            return new ExecutorQuorumWriteTest.Selector(index);
        }
    }

    static class Callback implements ExecutionCallback {
        static Semaphore finished;

        static Throwable throwable;

        Callback() {
            ExecutorQuorumWriteTest.Callback.finished = new Semaphore(0);
            ExecutorQuorumWriteTest.Callback.throwable = null;
        }

        @Override
        public void onResponse(Object response) {
            ExecutorQuorumWriteTest.Callback.finished.release();
        }

        @Override
        public void onFailure(Throwable t) {
            ExecutorQuorumWriteTest.Callback.finished.release();
            ExecutorQuorumWriteTest.Callback.throwable = t;
        }

        public void get() {
            while (true) {
                try {
                    ExecutorQuorumWriteTest.Callback.finished.tryAcquire(5, TimeUnit.SECONDS);
                    if ((ExecutorQuorumWriteTest.Callback.throwable) != null) {
                        ExceptionUtil.sneakyThrow(ExecutorQuorumWriteTest.Callback.throwable);
                    }
                    return;
                } catch (InterruptedException ignored) {
                }
            } 
        }

        public static ExecutorQuorumWriteTest.Callback callback() {
            return new ExecutorQuorumWriteTest.Callback();
        }
    }

    static class MultiCallback implements MultiExecutionCallback {
        Semaphore finished = new Semaphore(0);

        Throwable throwable;

        @Override
        public void onResponse(Member member, Object response) {
            if (response instanceof Throwable) {
                throwable = ((Throwable) (response));
            }
        }

        @Override
        public void onComplete(Map<Member, Object> values) {
            finished.release();
        }

        public void get() {
            while (true) {
                try {
                    if (!(finished.tryAcquire(5, TimeUnit.SECONDS))) {
                        ExceptionUtil.sneakyThrow(new TimeoutException());
                    }
                    if ((throwable) != null) {
                        ExceptionUtil.sneakyThrow(throwable);
                    }
                    return;
                } catch (InterruptedException ignored) {
                }
            } 
        }

        static ExecutorQuorumWriteTest.MultiCallback multiCallback() {
            return new ExecutorQuorumWriteTest.MultiCallback();
        }
    }
}


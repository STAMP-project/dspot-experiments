/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.ConnectivityState;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ConnectivityStateManager}.
 */
@RunWith(JUnit4.class)
public class ConnectivityStateManagerTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final FakeClock executor = new FakeClock();

    private final ConnectivityStateManager state = new ConnectivityStateManager();

    private final LinkedList<ConnectivityState> sink = new LinkedList<>();

    @Test
    public void noCallback() {
        state.gotoState(ConnectivityState.CONNECTING);
        Assert.assertEquals(ConnectivityState.CONNECTING, state.getState());
        state.gotoState(ConnectivityState.TRANSIENT_FAILURE);
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, state.getState());
    }

    @Test
    public void registerCallbackBeforeStateChanged() {
        state.gotoState(ConnectivityState.CONNECTING);
        Assert.assertEquals(ConnectivityState.CONNECTING, state.getState());
        state.notifyWhenStateChanged(new Runnable() {
            @Override
            public void run() {
                sink.add(state.getState());
            }
        }, executor.getScheduledExecutorService(), ConnectivityState.CONNECTING);
        Assert.assertEquals(0, executor.numPendingTasks());
        state.gotoState(ConnectivityState.TRANSIENT_FAILURE);
        // Make sure the callback is run in the executor
        Assert.assertEquals(0, sink.size());
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertEquals(0, executor.numPendingTasks());
        Assert.assertEquals(1, sink.size());
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, sink.poll());
    }

    @Test
    public void registerCallbackAfterStateChanged() {
        state.gotoState(ConnectivityState.CONNECTING);
        Assert.assertEquals(ConnectivityState.CONNECTING, state.getState());
        state.notifyWhenStateChanged(new Runnable() {
            @Override
            public void run() {
                sink.add(state.getState());
            }
        }, executor.getScheduledExecutorService(), ConnectivityState.IDLE);
        // Make sure the callback is run in the executor
        Assert.assertEquals(0, sink.size());
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertEquals(0, executor.numPendingTasks());
        Assert.assertEquals(1, sink.size());
        Assert.assertEquals(ConnectivityState.CONNECTING, sink.poll());
    }

    @Test
    public void callbackOnlyCalledOnTransition() {
        state.notifyWhenStateChanged(new Runnable() {
            @Override
            public void run() {
                sink.add(state.getState());
            }
        }, executor.getScheduledExecutorService(), ConnectivityState.IDLE);
        state.gotoState(ConnectivityState.IDLE);
        Assert.assertEquals(0, executor.numPendingTasks());
        Assert.assertEquals(0, sink.size());
    }

    @Test
    public void callbacksAreOneShot() {
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                sink.add(state.getState());
            }
        };
        state.notifyWhenStateChanged(callback, executor.getScheduledExecutorService(), ConnectivityState.IDLE);
        // First transition triggers the callback
        state.gotoState(ConnectivityState.CONNECTING);
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertEquals(1, sink.size());
        Assert.assertEquals(ConnectivityState.CONNECTING, sink.poll());
        Assert.assertEquals(0, executor.numPendingTasks());
        // Second transition doesn't trigger the callback
        state.gotoState(ConnectivityState.TRANSIENT_FAILURE);
        Assert.assertEquals(0, sink.size());
        Assert.assertEquals(0, executor.numPendingTasks());
        // Register another callback
        state.notifyWhenStateChanged(callback, executor.getScheduledExecutorService(), ConnectivityState.TRANSIENT_FAILURE);
        state.gotoState(ConnectivityState.READY);
        state.gotoState(ConnectivityState.IDLE);
        // Callback loses the race with the second stage change
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertEquals(1, sink.size());
        // It will see the second state
        Assert.assertEquals(ConnectivityState.IDLE, sink.poll());
        Assert.assertEquals(0, executor.numPendingTasks());
    }

    @Test
    public void multipleCallbacks() {
        final LinkedList<String> callbackRuns = new LinkedList<>();
        state.notifyWhenStateChanged(new Runnable() {
            @Override
            public void run() {
                sink.add(state.getState());
                callbackRuns.add("callback1");
            }
        }, executor.getScheduledExecutorService(), ConnectivityState.IDLE);
        state.notifyWhenStateChanged(new Runnable() {
            @Override
            public void run() {
                sink.add(state.getState());
                callbackRuns.add("callback2");
            }
        }, executor.getScheduledExecutorService(), ConnectivityState.IDLE);
        state.notifyWhenStateChanged(new Runnable() {
            @Override
            public void run() {
                sink.add(state.getState());
                callbackRuns.add("callback3");
            }
        }, executor.getScheduledExecutorService(), ConnectivityState.READY);
        // callback3 is run immediately because the source state is already different from the current
        // state.
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertEquals(1, callbackRuns.size());
        Assert.assertEquals("callback3", callbackRuns.poll());
        Assert.assertEquals(1, sink.size());
        Assert.assertEquals(ConnectivityState.IDLE, sink.poll());
        // Now change the state.
        state.gotoState(ConnectivityState.CONNECTING);
        Assert.assertEquals(2, executor.runDueTasks());
        Assert.assertEquals(2, callbackRuns.size());
        Assert.assertEquals("callback1", callbackRuns.poll());
        Assert.assertEquals("callback2", callbackRuns.poll());
        Assert.assertEquals(2, sink.size());
        Assert.assertEquals(ConnectivityState.CONNECTING, sink.poll());
        Assert.assertEquals(ConnectivityState.CONNECTING, sink.poll());
        Assert.assertEquals(0, executor.numPendingTasks());
    }

    @Test
    public void registerCallbackFromCallback() {
        state.notifyWhenStateChanged(newRecursiveCallback(executor.getScheduledExecutorService()), executor.getScheduledExecutorService(), state.getState());
        state.gotoState(ConnectivityState.CONNECTING);
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertEquals(0, executor.numPendingTasks());
        Assert.assertEquals(1, sink.size());
        Assert.assertEquals(ConnectivityState.CONNECTING, sink.poll());
        state.gotoState(ConnectivityState.READY);
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertEquals(0, executor.numPendingTasks());
        Assert.assertEquals(1, sink.size());
        Assert.assertEquals(ConnectivityState.READY, sink.poll());
    }

    @Test
    public void registerCallbackFromCallbackDirectExecutor() {
        state.notifyWhenStateChanged(newRecursiveCallback(MoreExecutors.directExecutor()), MoreExecutors.directExecutor(), state.getState());
        state.gotoState(ConnectivityState.CONNECTING);
        Assert.assertEquals(1, sink.size());
        Assert.assertEquals(ConnectivityState.CONNECTING, sink.poll());
        state.gotoState(ConnectivityState.READY);
        Assert.assertEquals(1, sink.size());
        Assert.assertEquals(ConnectivityState.READY, sink.poll());
    }

    @Test
    public void shutdownThenReady() {
        state.gotoState(ConnectivityState.SHUTDOWN);
        Assert.assertEquals(ConnectivityState.SHUTDOWN, state.getState());
        state.gotoState(ConnectivityState.READY);
        Assert.assertEquals(ConnectivityState.SHUTDOWN, state.getState());
    }
}


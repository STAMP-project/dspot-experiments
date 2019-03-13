/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.grpclb;


import Attributes.Key;
import ConnectivityState.READY;
import FakeClock.TaskFilter;
import Status.UNAVAILABLE;
import io.grpc.Attributes;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancer.Subchannel;
import io.grpc.SynchronizationContext;
import io.grpc.grpclb.CachedSubchannelPool.ShutdownSubchannelTask;
import io.grpc.internal.FakeClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link CachedSubchannelPool}.
 */
@RunWith(JUnit4.class)
public class CachedSubchannelPoolTest {
    private static final EquivalentAddressGroup EAG1 = new EquivalentAddressGroup(new FakeSocketAddress("fake-address-1"), Attributes.EMPTY);

    private static final EquivalentAddressGroup EAG2 = new EquivalentAddressGroup(new FakeSocketAddress("fake-address-2"), Attributes.EMPTY);

    private static final Attributes.Key<String> ATTR_KEY = Key.create("test-attr");

    private static final Attributes ATTRS1 = Attributes.newBuilder().set(io.grpc.grpclb.ATTR_KEY, "1").build();

    private static final Attributes ATTRS2 = Attributes.newBuilder().set(io.grpc.grpclb.ATTR_KEY, "2").build();

    private static final ConnectivityStateInfo READY_STATE = ConnectivityStateInfo.forNonError(READY);

    private static final ConnectivityStateInfo TRANSIENT_FAILURE_STATE = ConnectivityStateInfo.forTransientFailure(UNAVAILABLE.withDescription("Simulated"));

    private static final TaskFilter SHUTDOWN_TASK_FILTER = new FakeClock.TaskFilter() {
        @Override
        public boolean shouldAccept(Runnable command) {
            // The task is wrapped by SynchronizationContext, so we can't compare the type
            // directly.
            return command.toString().contains(ShutdownSubchannelTask.class.getSimpleName());
        }
    };

    private final Helper helper = Mockito.mock(Helper.class);

    private final LoadBalancer balancer = Mockito.mock(LoadBalancer.class);

    private final FakeClock clock = new FakeClock();

    private final SynchronizationContext syncContext = new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            throw new AssertionError(e);
        }
    });

    private final CachedSubchannelPool pool = new CachedSubchannelPool();

    private final ArrayList<Subchannel> mockSubchannels = new ArrayList<>();

    @Test
    public void subchannelExpireAfterReturned() {
        Subchannel subchannel1 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1);
        assertThat(subchannel1).isNotNull();
        Mockito.verify(helper).createSubchannel(ArgumentMatchers.eq(Arrays.asList(CachedSubchannelPoolTest.EAG1)), ArgumentMatchers.same(CachedSubchannelPoolTest.ATTRS1));
        Subchannel subchannel2 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS2);
        assertThat(subchannel2).isNotNull();
        assertThat(subchannel2).isNotSameAs(subchannel1);
        Mockito.verify(helper).createSubchannel(ArgumentMatchers.eq(Arrays.asList(CachedSubchannelPoolTest.EAG2)), ArgumentMatchers.same(CachedSubchannelPoolTest.ATTRS2));
        pool.returnSubchannel(subchannel1, CachedSubchannelPoolTest.READY_STATE);
        // subchannel1 is 1ms away from expiration.
        clock.forwardTime(((CachedSubchannelPool.SHUTDOWN_TIMEOUT_MS) - 1), TimeUnit.MILLISECONDS);
        Mockito.verify(subchannel1, Mockito.never()).shutdown();
        pool.returnSubchannel(subchannel2, CachedSubchannelPoolTest.READY_STATE);
        // subchannel1 expires. subchannel2 is (SHUTDOWN_TIMEOUT_MS - 1) away from expiration.
        clock.forwardTime(1, TimeUnit.MILLISECONDS);
        Mockito.verify(subchannel1).shutdown();
        // subchanne2 expires.
        clock.forwardTime(((CachedSubchannelPool.SHUTDOWN_TIMEOUT_MS) - 1), TimeUnit.MILLISECONDS);
        Mockito.verify(subchannel2).shutdown();
        assertThat(clock.numPendingTasks()).isEqualTo(0);
    }

    @Test
    public void subchannelReused() {
        Subchannel subchannel1 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1);
        assertThat(subchannel1).isNotNull();
        Mockito.verify(helper).createSubchannel(ArgumentMatchers.eq(Arrays.asList(CachedSubchannelPoolTest.EAG1)), ArgumentMatchers.same(CachedSubchannelPoolTest.ATTRS1));
        Subchannel subchannel2 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS2);
        assertThat(subchannel2).isNotNull();
        assertThat(subchannel2).isNotSameAs(subchannel1);
        Mockito.verify(helper).createSubchannel(ArgumentMatchers.eq(Arrays.asList(CachedSubchannelPoolTest.EAG2)), ArgumentMatchers.same(CachedSubchannelPoolTest.ATTRS2));
        pool.returnSubchannel(subchannel1, CachedSubchannelPoolTest.READY_STATE);
        // subchannel1 is 1ms away from expiration.
        clock.forwardTime(((CachedSubchannelPool.SHUTDOWN_TIMEOUT_MS) - 1), TimeUnit.MILLISECONDS);
        // This will cancel the shutdown timer for subchannel1
        Subchannel subchannel1a = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1);
        assertThat(subchannel1a).isSameAs(subchannel1);
        pool.returnSubchannel(subchannel2, CachedSubchannelPoolTest.READY_STATE);
        // subchannel2 expires SHUTDOWN_TIMEOUT_MS after being returned
        clock.forwardTime(((CachedSubchannelPool.SHUTDOWN_TIMEOUT_MS) - 1), TimeUnit.MILLISECONDS);
        Mockito.verify(subchannel2, Mockito.never()).shutdown();
        clock.forwardTime(1, TimeUnit.MILLISECONDS);
        Mockito.verify(subchannel2).shutdown();
        // pool will create a new channel for EAG2 when requested
        Subchannel subchannel2a = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS2);
        assertThat(subchannel2a).isNotSameAs(subchannel2);
        Mockito.verify(helper, Mockito.times(2)).createSubchannel(ArgumentMatchers.eq(Arrays.asList(CachedSubchannelPoolTest.EAG2)), ArgumentMatchers.same(CachedSubchannelPoolTest.ATTRS2));
        // subchannel1 expires SHUTDOWN_TIMEOUT_MS after being returned
        pool.returnSubchannel(subchannel1a, CachedSubchannelPoolTest.READY_STATE);
        clock.forwardTime(((CachedSubchannelPool.SHUTDOWN_TIMEOUT_MS) - 1), TimeUnit.MILLISECONDS);
        Mockito.verify(subchannel1a, Mockito.never()).shutdown();
        clock.forwardTime(1, TimeUnit.MILLISECONDS);
        Mockito.verify(subchannel1a).shutdown();
        assertThat(clock.numPendingTasks()).isEqualTo(0);
    }

    @Test
    public void updateStateWhileInPool() {
        Subchannel subchannel1 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1);
        Subchannel subchannel2 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS2);
        pool.returnSubchannel(subchannel1, CachedSubchannelPoolTest.READY_STATE);
        pool.returnSubchannel(subchannel2, CachedSubchannelPoolTest.TRANSIENT_FAILURE_STATE);
        ConnectivityStateInfo anotherFailureState = ConnectivityStateInfo.forTransientFailure(UNAVAILABLE.withDescription("Another"));
        pool.handleSubchannelState(subchannel1, anotherFailureState);
        Mockito.verify(balancer, Mockito.never()).handleSubchannelState(ArgumentMatchers.any(Subchannel.class), ArgumentMatchers.any(ConnectivityStateInfo.class));
        assertThat(pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1)).isSameAs(subchannel1);
        Mockito.verify(balancer).handleSubchannelState(ArgumentMatchers.same(subchannel1), ArgumentMatchers.same(anotherFailureState));
        Mockito.verifyNoMoreInteractions(balancer);
        assertThat(pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS2)).isSameAs(subchannel2);
        Mockito.verify(balancer).handleSubchannelState(ArgumentMatchers.same(subchannel2), ArgumentMatchers.same(CachedSubchannelPoolTest.TRANSIENT_FAILURE_STATE));
        Mockito.verifyNoMoreInteractions(balancer);
    }

    @Test
    public void updateStateWhileInPool_notSameObject() {
        Subchannel subchannel1 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1);
        pool.returnSubchannel(subchannel1, CachedSubchannelPoolTest.READY_STATE);
        Subchannel subchannel2 = helper.createSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1);
        Subchannel subchannel3 = helper.createSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS2);
        // subchannel2 is not in the pool, although with the same address
        pool.handleSubchannelState(subchannel2, CachedSubchannelPoolTest.TRANSIENT_FAILURE_STATE);
        // subchannel3 is not in the pool.  In fact its address is not in the pool
        pool.handleSubchannelState(subchannel3, CachedSubchannelPoolTest.TRANSIENT_FAILURE_STATE);
        assertThat(pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1)).isSameAs(subchannel1);
        // subchannel1's state is unchanged
        Mockito.verify(balancer).handleSubchannelState(ArgumentMatchers.same(subchannel1), ArgumentMatchers.same(CachedSubchannelPoolTest.READY_STATE));
        Mockito.verifyNoMoreInteractions(balancer);
    }

    @Test
    public void returnDuplicateAddressSubchannel() {
        Subchannel subchannel1 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1);
        Subchannel subchannel2 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS2);
        Subchannel subchannel3 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS1);
        assertThat(subchannel1).isNotSameAs(subchannel2);
        assertThat(clock.getPendingTasks(CachedSubchannelPoolTest.SHUTDOWN_TASK_FILTER)).isEmpty();
        pool.returnSubchannel(subchannel2, CachedSubchannelPoolTest.READY_STATE);
        assertThat(clock.getPendingTasks(CachedSubchannelPoolTest.SHUTDOWN_TASK_FILTER)).hasSize(1);
        // If the subchannel being returned has an address that is the same as a subchannel in the pool,
        // the returned subchannel will be shut down.
        Mockito.verify(subchannel1, Mockito.never()).shutdown();
        pool.returnSubchannel(subchannel1, CachedSubchannelPoolTest.READY_STATE);
        assertThat(clock.getPendingTasks(CachedSubchannelPoolTest.SHUTDOWN_TASK_FILTER)).hasSize(1);
        Mockito.verify(subchannel1).shutdown();
        pool.returnSubchannel(subchannel3, CachedSubchannelPoolTest.READY_STATE);
        assertThat(clock.getPendingTasks(CachedSubchannelPoolTest.SHUTDOWN_TASK_FILTER)).hasSize(2);
        // Returning the same subchannel twice has no effect.
        pool.returnSubchannel(subchannel3, CachedSubchannelPoolTest.READY_STATE);
        assertThat(clock.getPendingTasks(CachedSubchannelPoolTest.SHUTDOWN_TASK_FILTER)).hasSize(2);
        Mockito.verify(subchannel2, Mockito.never()).shutdown();
        Mockito.verify(subchannel3, Mockito.never()).shutdown();
    }

    @Test
    public void clear() {
        Subchannel subchannel1 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG1, CachedSubchannelPoolTest.ATTRS1);
        Subchannel subchannel2 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS2);
        Subchannel subchannel3 = pool.takeOrCreateSubchannel(CachedSubchannelPoolTest.EAG2, CachedSubchannelPoolTest.ATTRS2);
        pool.returnSubchannel(subchannel1, CachedSubchannelPoolTest.READY_STATE);
        pool.returnSubchannel(subchannel2, CachedSubchannelPoolTest.READY_STATE);
        Mockito.verify(subchannel1, Mockito.never()).shutdown();
        Mockito.verify(subchannel2, Mockito.never()).shutdown();
        pool.clear();
        Mockito.verify(subchannel1).shutdown();
        Mockito.verify(subchannel2).shutdown();
        Mockito.verify(subchannel3, Mockito.never()).shutdown();
        assertThat(clock.numPendingTasks()).isEqualTo(0);
    }
}


/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.ehcache.StateTransitionException;
import org.ehcache.Status;
import org.ehcache.core.events.StateChangeListener;
import org.ehcache.core.spi.LifeCycled;
import org.ehcache.core.spi.LifeCycledAdapter;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;


public class StatusTransitionerTest {
    @Test
    public void testTransitionsToLowestStateOnFailure() {
        StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.UNINITIALIZED));
        transitioner.init().failed(new Throwable());
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.UNINITIALIZED));
        transitioner.init().succeeded();
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.AVAILABLE));
        transitioner.close().failed(new Throwable());
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.UNINITIALIZED));
    }

    @Test
    public void testFiresListeners() {
        StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        final StateChangeListener listener = Mockito.mock(StateChangeListener.class);
        transitioner.registerListener(listener);
        transitioner.init().succeeded();
        Mockito.verify(listener).stateTransition(Status.UNINITIALIZED, Status.AVAILABLE);
        Mockito.reset(listener);
        transitioner.deregisterListener(listener);
        transitioner.close().succeeded();
        Mockito.verify(listener, Mockito.never()).stateTransition(Status.AVAILABLE, Status.UNINITIALIZED);
    }

    @Test
    public void testFinishesTransitionOnListenerThrowing() {
        StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        final StateChangeListener listener = Mockito.mock(StateChangeListener.class);
        final RuntimeException runtimeException = new RuntimeException();
        Mockito.doThrow(runtimeException).when(listener).stateTransition(Status.UNINITIALIZED, Status.AVAILABLE);
        transitioner.registerListener(listener);
        try {
            transitioner.init().succeeded();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertThat(e, CoreMatchers.is(runtimeException));
        }
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.AVAILABLE));
    }

    @Test
    public void testMaintenanceOnlyLetsTheOwningThreadInteract() throws InterruptedException {
        final StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        transitioner.maintenance().succeeded();
        transitioner.checkMaintenance();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    transitioner.checkMaintenance();
                    Assert.fail();
                } catch (IllegalStateException e) {
                    Assert.assertThat(e.getMessage().contains(Status.MAINTENANCE.name()), CoreMatchers.is(true));
                    Assert.assertThat(e.getMessage().contains("own"), CoreMatchers.is(true));
                }
            }
        };
        thread.start();
        thread.join();
    }

    @Test
    public void testMaintenanceOnlyOwningThreadCanClose() throws InterruptedException {
        final StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        transitioner.maintenance().succeeded();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    transitioner.close();
                    Assert.fail();
                } catch (IllegalStateException e) {
                    Assert.assertThat(e.getMessage().contains(Status.MAINTENANCE.name()), CoreMatchers.is(true));
                    Assert.assertThat(e.getMessage().contains("own"), CoreMatchers.is(true));
                }
            }
        };
        thread.start();
        thread.join();
        transitioner.close();
    }

    @Test
    public void testCheckAvailableAccountsForThreadLease() throws InterruptedException {
        final StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        transitioner.maintenance().succeeded();
        transitioner.checkAvailable();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    transitioner.checkAvailable();
                    Assert.fail();
                } catch (IllegalStateException e) {
                    Assert.assertThat(e.getMessage().contains(Status.MAINTENANCE.name()), CoreMatchers.is(true));
                    Assert.assertThat(e.getMessage().contains("own"), CoreMatchers.is(true));
                }
            }
        };
        thread.start();
        thread.join();
        transitioner.close();
    }

    @Test
    public void testHookThrowingVetosTransition() throws Exception {
        final StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        final LifeCycled mock = Mockito.mock(LifeCycled.class);
        transitioner.addHook(mock);
        final Exception toBeThrown = new Exception();
        Mockito.doThrow(toBeThrown).when(mock).init();
        try {
            transitioner.init().succeeded();
            Assert.fail();
        } catch (StateTransitionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.<Throwable>sameInstance(toBeThrown));
        }
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.UNINITIALIZED));
        Mockito.reset(mock);
        Mockito.doThrow(toBeThrown).when(mock).close();
        transitioner.init().succeeded();
        try {
            transitioner.close().succeeded();
            Assert.fail();
        } catch (StateTransitionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.<Throwable>sameInstance(toBeThrown));
        }
    }

    @Test
    public void testRepectRegistrationOrder() {
        final List<LifeCycled> order = new ArrayList<>();
        final StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        final StatusTransitionerTest.Recorder first = new StatusTransitionerTest.Recorder(order, "first");
        final StatusTransitionerTest.Recorder second = new StatusTransitionerTest.Recorder(order, "second");
        transitioner.addHook(first);
        transitioner.addHook(second);
        transitioner.init().succeeded();
        Assert.assertThat(order.get(0), CoreMatchers.<LifeCycled>sameInstance(first));
        Assert.assertThat(order.get(1), CoreMatchers.<LifeCycled>sameInstance(second));
        order.clear();
        transitioner.close().succeeded();
        Assert.assertThat(order.get(0), CoreMatchers.<LifeCycled>sameInstance(second));
        Assert.assertThat(order.get(1), CoreMatchers.<LifeCycled>sameInstance(first));
    }

    @Test
    public void testStopsInitedHooksOnFailure() throws Exception {
        final StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        final LifeCycled first = Mockito.mock(LifeCycled.class);
        final LifeCycled second = Mockito.mock(LifeCycled.class);
        transitioner.addHook(first);
        transitioner.addHook(second);
        final Exception toBeThrown = new Exception();
        Mockito.doThrow(toBeThrown).when(second).init();
        try {
            transitioner.init().succeeded();
            Assert.fail();
        } catch (StateTransitionException e) {
            // expected
        }
        Mockito.verify(first).init();
        Mockito.verify(first).close();
    }

    @Test
    public void testDoesNoReInitsClosedHooksOnFailure() throws Exception {
        final StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        final LifeCycled first = Mockito.mock(LifeCycled.class);
        final LifeCycled second = Mockito.mock(LifeCycled.class);
        transitioner.addHook(first);
        transitioner.addHook(second);
        transitioner.init().succeeded();
        Mockito.reset(first);
        Mockito.reset(second);
        final Exception toBeThrown = new Exception();
        Mockito.doThrow(toBeThrown).when(first).close();
        try {
            transitioner.close().succeeded();
            Assert.fail();
        } catch (StateTransitionException e) {
            // expected
        }
        Mockito.verify(second).close();
        Mockito.verify(second, Mockito.never()).init();
    }

    @Test
    public void testClosesAllHooksOnFailure() throws Exception {
        final StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        final LifeCycled first = Mockito.mock(LifeCycled.class);
        final LifeCycled second = Mockito.mock(LifeCycled.class);
        transitioner.addHook(first);
        transitioner.addHook(second);
        transitioner.init().succeeded();
        Mockito.reset(first);
        Mockito.reset(second);
        final Exception toBeThrown = new Exception();
        Mockito.doThrow(toBeThrown).when(second).close();
        try {
            transitioner.close().succeeded();
            Assert.fail();
        } catch (StateTransitionException e) {
            // expected
        }
        Mockito.verify(first).close();
    }

    @Test
    public void testLifeCycledAdapterCanBeUsedInsteadOfLifeCycled() {
        StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        final List<String> calls = new LinkedList<>();
        LifeCycledAdapter adapter1 = new LifeCycledAdapter() {};
        LifeCycledAdapter adapter2 = new LifeCycledAdapter() {
            @Override
            public void init() throws Exception {
                calls.add("adapter2-init");
            }
        };
        LifeCycledAdapter adapter3 = new LifeCycledAdapter() {
            @Override
            public void close() throws Exception {
                calls.add("adapter3-close");
            }
        };
        LifeCycledAdapter adapter4 = new LifeCycledAdapter() {
            @Override
            public void init() throws Exception {
                calls.add("adapter4-init");
            }

            @Override
            public void close() throws Exception {
                calls.add("adapter4-close");
            }
        };
        transitioner.addHook(adapter1);
        transitioner.addHook(adapter2);
        transitioner.addHook(adapter3);
        transitioner.addHook(adapter4);
        transitioner.init().succeeded();
        Assert.assertThat(calls, Matchers.equalTo(Arrays.asList("adapter2-init", "adapter4-init")));
        transitioner.close().succeeded();
        Assert.assertThat(calls, Matchers.equalTo(Arrays.asList("adapter2-init", "adapter4-init", "adapter4-close", "adapter3-close")));
    }

    @Test
    public void testTransitionDuringFailures() {
        StatusTransitioner transitioner = new StatusTransitioner(LoggerFactory.getLogger(StatusTransitionerTest.class));
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.UNINITIALIZED));
        StatusTransitioner.Transition st = transitioner.init();
        st.failed(new Throwable());
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.UNINITIALIZED));
        try {
            st.failed(new Throwable());
            Assert.fail();
        } catch (AssertionError err) {
            Assert.assertThat(err.getMessage(), CoreMatchers.is("Throwable cannot be thrown if Transition is done."));
        }
        st.failed(null);
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.UNINITIALIZED));
        StatusTransitioner.Transition st1 = transitioner.init();
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.AVAILABLE));
        st1.failed(null);
        Assert.assertThat(transitioner.currentStatus(), CoreMatchers.is(Status.UNINITIALIZED));
    }

    private static class Recorder implements LifeCycled {
        private final List<LifeCycled> order;

        private final String name;

        public Recorder(final List<LifeCycled> order, final String name) {
            this.order = order;
            this.name = name;
        }

        @Override
        public void init() throws Exception {
            order.add(this);
        }

        @Override
        public void close() throws Exception {
            order.add(this);
        }

        @Override
        public String toString() {
            return ("Recorder{" + (name)) + '}';
        }
    }
}


/**
 * Copyright 2015 Netflix, Inc.
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
package io.reactivex.netty.events;


import io.reactivex.netty.test.util.MockEventListener;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rx.Subscription;


public class ListenersHolderTest {
    @Rule
    public final ListenersHolderRule holderRule = new ListenersHolderRule();

    @Test(timeout = 60000)
    public void testSubscribe() throws Exception {
        ListenersHolderRule.ListenerWithSub l = holderRule.addAListener();
        holderRule.assertListenerAdded(l.listener);
        l.subscription.unsubscribe();
        Assert.assertThat("Listener not removed on unsubscribe.", holderRule.getHolder().getAllListeners(), is(empty()));
    }

    @Test(timeout = 60000)
    public void testMultipleListeners() throws Exception {
        final MockEventListener listener1 = new MockEventListener();
        final MockEventListener listener2 = new MockEventListener();
        Subscription subscription1 = holderRule.getHolder().subscribe(listener1);
        Subscription subscription2 = holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        subscription1.unsubscribe();
        Assert.assertThat("Listener not removed on unsubscribe.", holderRule.getHolder().getAllListeners(), hasSize(1));
        Assert.assertThat("Listener not removed on unsubscribe.", holderRule.getHolder().getAllListeners(), not(contains(listener1)));
        Assert.assertThat("Listener not removed on unsubscribe.", holderRule.getHolder().getAllListeners(), contains(listener2));
        subscription2.unsubscribe();
        Assert.assertThat("Listener not removed on unsubscribe.", holderRule.getHolder().getAllListeners(), is(empty()));
    }

    @Test(timeout = 60000)
    public void testPublishingEnabled() throws Exception {
        Assert.assertThat("Publishing enabled with no listeners", holderRule.getHolder().publishingEnabled(), is(false));
        ListenersHolderRule.ListenerWithSub l1 = holderRule.addAListener();
        Assert.assertThat("Publishing disabled with a listener", holderRule.getHolder().publishingEnabled(), is(true));
        l1.subscription.unsubscribe();
        Assert.assertThat("Listener not removed on unsubscribe.", holderRule.getHolder().getAllListeners(), is(empty()));
        Assert.assertThat("Publishing enabled post listener unsubscribe", holderRule.getHolder().publishingEnabled(), is(false));
    }

    @Test(timeout = 60000)
    public void testDispose() throws Exception {
        ListenersHolderRule.ListenerWithSub l = holderRule.addAListener();
        holderRule.getHolder().dispose();
        Assert.assertThat("On complete not called on dispose.", l.listener.getOnCompletedCount(), is(1));
        Assert.assertThat("Listener not unsubscribed on dispose.", l.subscription.isUnsubscribed(), is(true));
        Assert.assertThat("Listener not removed on dispose.", holderRule.getHolder().getAllListeners(), not(contains(l.listener)));
    }

    @Test(timeout = 60000)
    public void testDisposeWithExceptions() throws Exception {
        final MockEventListener listener1 = new MockEventListener(true);
        final MockEventListener listener2 = new MockEventListener();
        Subscription subscription1 = holderRule.getHolder().subscribe(listener1);
        Subscription subscription2 = holderRule.getHolder().subscribe(listener2);
        Assert.assertThat("Listeners not added.", holderRule.getHolder().getAllListeners(), hasSize(2));
        Assert.assertThat("Listeners not added.", holderRule.getHolder().getAllListeners(), contains(listener1, listener2));
        try {
            holderRule.getHolder().dispose();
            throw new AssertionError("Error not thrown on dispose.");
        } catch (Exception e) {
            // Expected.
        }
        Assert.assertThat("First listener not completed.", listener1.getOnCompletedCount(), is(1));
        Assert.assertThat("Second listener not completed.", listener2.getOnCompletedCount(), is(1));
        Assert.assertThat("First listener not unsubscribed.", subscription1.isUnsubscribed(), is(true));
        Assert.assertThat("Second listener not unsubscribed.", subscription2.isUnsubscribed(), is(true));
        Assert.assertThat("Listeners not removed post dispose.", holderRule.getHolder().getAllListeners(), is(empty()));
    }

    @Test(timeout = 60000)
    public void testInvokeListeners() throws Exception {
        final MockEventListener listener = new MockEventListener();
        holderRule.getHolder().subscribe(listener);
        holderRule.assertListenerAdded(listener);
        holderRule.getHolder().invokeListeners(new rx.functions.Action1<MockEventListener>() {
            @Override
            public void call(MockEventListener mockEventListener) {
                mockEventListener.anEvent();
            }
        });
        Assert.assertThat("Listener not invoked.", listener.getEventInvocationCount(), is(1));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersMulti() throws Exception {
        final MockEventListener listener1 = new MockEventListener();
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        holderRule.getHolder().invokeListeners(new rx.functions.Action1<MockEventListener>() {
            @Override
            public void call(MockEventListener mockEventListener) {
                mockEventListener.anEvent();
            }
        });
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked.", listener2.getEventInvocationCount(), is(1));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersRaiseException() throws Exception {
        final MockEventListener listener1 = new MockEventListener(true);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener1);
        holderRule.getHolder().subscribe(listener2);
        Assert.assertThat("Listeners not added.", holderRule.getHolder().getAllListeners(), hasSize(2));
        Assert.assertThat("Listeners not added.", holderRule.getHolder().getAllListeners(), contains(listener1, listener2));
        holderRule.getHolder().invokeListeners(new rx.functions.Action1<MockEventListener>() {
            @Override
            public void call(MockEventListener mockEventListener) {
                mockEventListener.anEvent();
            }
        });
        Assert.assertThat("First listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithDuration() throws Exception {
        final MockEventListener listener = new MockEventListener();
        holderRule.getHolder().subscribe(listener);
        holderRule.assertListenerAdded(listener);
        holderRule.getHolder().invokeListeners(new rx.functions.Action3<MockEventListener, Long, TimeUnit>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit) {
                mockEventListener.anEventWithDuration(duration, timeUnit);
            }
        }, 1, TimeUnit.MICROSECONDS);
        Assert.assertThat("Listener not invoked.", listener.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener.getTimeUnit(), is(TimeUnit.MICROSECONDS));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersMultiWithDuration() throws Exception {
        final MockEventListener listener1 = new MockEventListener();
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        holderRule.getHolder().invokeListeners(new rx.functions.Action3<MockEventListener, Long, TimeUnit>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit) {
                mockEventListener.anEventWithDuration(duration, timeUnit);
            }
        }, 1, TimeUnit.MICROSECONDS);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener1.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener1.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with duration.", listener2.getDuration(), is(1L));
        Assert.assertThat("Second listener not invoked with time unit.", listener2.getTimeUnit(), is(TimeUnit.MICROSECONDS));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithDurationRaiseException() throws Exception {
        final MockEventListener listener1 = new MockEventListener(true);
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        holderRule.getHolder().invokeListeners(new rx.functions.Action3<MockEventListener, Long, TimeUnit>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit) {
                mockEventListener.anEventWithDuration(duration, timeUnit);
            }
        }, 1, TimeUnit.MICROSECONDS);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener1.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener1.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with duration.", listener2.getDuration(), is(1L));
        Assert.assertThat("Second listener not invoked with time unit.", listener2.getTimeUnit(), is(TimeUnit.MICROSECONDS));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithDurationAndError() throws Exception {
        final MockEventListener listener = new MockEventListener();
        holderRule.getHolder().subscribe(listener);
        holderRule.assertListenerAdded(listener);
        final Throwable expected = new NullPointerException();
        holderRule.getHolder().invokeListeners(new rx.functions.Action4<MockEventListener, Long, TimeUnit, Throwable>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit, Throwable t) {
                mockEventListener.anEventWithDurationAndError(duration, timeUnit, t);
            }
        }, 1, TimeUnit.MICROSECONDS, expected);
        Assert.assertThat("Listener not invoked.", listener.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Listener not invoked with error.", listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersMultiWithDurationAndError() throws Exception {
        final MockEventListener listener1 = new MockEventListener();
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        final Throwable expected = new NullPointerException();
        holderRule.getHolder().invokeListeners(new rx.functions.Action4<MockEventListener, Long, TimeUnit, Throwable>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit, Throwable t) {
                mockEventListener.anEventWithDurationAndError(duration, timeUnit, t);
            }
        }, 1, TimeUnit.MICROSECONDS, expected);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener1.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener1.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Listener not invoked with error.", listener1.getRecievedError(), is(expected));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with duration.", listener2.getDuration(), is(1L));
        Assert.assertThat("Second listener not invoked with time unit.", listener2.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Second listener not invoked with error.", listener2.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithDurationAndErrorRaiseException() throws Exception {
        final MockEventListener listener1 = new MockEventListener(true);
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        final Throwable expected = new NullPointerException();
        holderRule.getHolder().invokeListeners(new rx.functions.Action4<MockEventListener, Long, TimeUnit, Throwable>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit, Throwable t) {
                mockEventListener.anEventWithDurationAndError(duration, timeUnit, t);
            }
        }, 1, TimeUnit.MICROSECONDS, expected);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener1.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener1.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Listener not invoked with error.", listener1.getRecievedError(), is(expected));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with duration.", listener2.getDuration(), is(1L));
        Assert.assertThat("Second listener not invoked with time unit.", listener2.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Second listener not invoked with error.", listener2.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithDurationAndArg() throws Exception {
        final MockEventListener listener = new MockEventListener();
        holderRule.getHolder().subscribe(listener);
        holderRule.assertListenerAdded(listener);
        final String arg = "doom";
        holderRule.getHolder().invokeListeners(new rx.functions.Action4<MockEventListener, Long, TimeUnit, String>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit, String arg) {
                mockEventListener.anEventWithDurationAndArg(duration, timeUnit, arg);
            }
        }, 1, TimeUnit.MICROSECONDS, arg);
        Assert.assertThat("Listener not invoked.", listener.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Listener not invoked with argument.", listener.getArg(), is(arg));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersMultiWithDurationAndArg() throws Exception {
        final MockEventListener listener1 = new MockEventListener();
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        final String arg = "doom";
        holderRule.getHolder().invokeListeners(new rx.functions.Action4<MockEventListener, Long, TimeUnit, String>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit, String arg) {
                mockEventListener.anEventWithDurationAndArg(duration, timeUnit, arg);
            }
        }, 1, TimeUnit.MICROSECONDS, arg);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener1.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener1.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Listener not invoked with argument.", listener1.getArg(), is(arg));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with duration.", listener2.getDuration(), is(1L));
        Assert.assertThat("Second listener not invoked with time unit.", listener2.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Second listener not invoked with argument.", listener2.getArg(), is(arg));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersMultiWithDurationErrorAndArg() throws Exception {
        final MockEventListener listener1 = new MockEventListener();
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        final Object event = "doom";
        final Throwable expected = new NullPointerException();
        holderRule.getHolder().invokeListeners(new rx.functions.Action5<MockEventListener, Long, TimeUnit, Throwable, Object>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit, Throwable throwable, Object event) {
                mockEventListener.onCustomEvent(event, duration, timeUnit, throwable);
            }
        }, 1, TimeUnit.MICROSECONDS, expected, event);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener1.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener1.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Listener not invoked with error.", listener1.getRecievedError(), is(expected));
        Assert.assertThat("Listener not invoked with argument.", listener1.getCustomEvent(), is(event));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with duration.", listener2.getDuration(), is(1L));
        Assert.assertThat("Second listener not invoked with time unit.", listener2.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Second listener not invoked with error.", listener2.getRecievedError(), is(expected));
        Assert.assertThat("Second listener not invoked with argument.", listener2.getCustomEvent(), is(event));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithDurationArgRaiseException() throws Exception {
        final MockEventListener listener1 = new MockEventListener(true);
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        final String arg = "doom";
        holderRule.getHolder().invokeListeners(new rx.functions.Action4<MockEventListener, Long, TimeUnit, String>() {
            @Override
            public void call(MockEventListener mockEventListener, Long duration, TimeUnit timeUnit, String arg) {
                mockEventListener.anEventWithDurationAndArg(duration, timeUnit, arg);
            }
        }, 1, TimeUnit.MICROSECONDS, arg);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with duration.", listener1.getDuration(), is(1L));
        Assert.assertThat("Listener not invoked with time unit.", listener1.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Listener not invoked with argument.", listener1.getArg(), is(arg));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with duration.", listener2.getDuration(), is(1L));
        Assert.assertThat("Second listener not invoked with time unit.", listener2.getTimeUnit(), is(TimeUnit.MICROSECONDS));
        Assert.assertThat("Second listener not invoked with argument.", listener2.getArg(), is(arg));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithArg() throws Exception {
        final MockEventListener listener = new MockEventListener();
        holderRule.getHolder().subscribe(listener);
        holderRule.assertListenerAdded(listener);
        final String arg = "doom";
        holderRule.getHolder().invokeListeners(new rx.functions.Action2<MockEventListener, String>() {
            @Override
            public void call(MockEventListener mockEventListener, String arg) {
                mockEventListener.anEventWithArg(arg);
            }
        }, arg);
        Assert.assertThat("Listener not invoked.", listener.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with argument.", listener.getArg(), is(arg));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithExceptionAndArg() throws Exception {
        final MockEventListener listener = new MockEventListener();
        holderRule.getHolder().subscribe(listener);
        holderRule.assertListenerAdded(listener);
        final Object event = "doom";
        final Throwable expected = new NullPointerException();
        holderRule.getHolder().invokeListeners(new rx.functions.Action3<MockEventListener, Throwable, Object>() {
            @Override
            public void call(MockEventListener mockEventListener, Throwable throwable, Object arg) {
                mockEventListener.onCustomEvent(arg, throwable);
            }
        }, expected, event);
        Assert.assertThat("Listener not invoked.", listener.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with argument.", listener.getCustomEvent(), equalTo(event));
        Assert.assertThat("Listener not invoked with exception.", listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersMultiWithArg() throws Exception {
        final MockEventListener listener1 = new MockEventListener();
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        final String arg = "doom";
        holderRule.getHolder().invokeListeners(new rx.functions.Action2<MockEventListener, String>() {
            @Override
            public void call(MockEventListener mockEventListener, String arg) {
                mockEventListener.anEventWithArg(arg);
            }
        }, arg);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with argument.", listener1.getArg(), is(arg));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with argument.", listener2.getArg(), is(arg));
    }

    @Test(timeout = 60000)
    public void testInvokeListenersWithArgAndRaiseException() throws Exception {
        final MockEventListener listener1 = new MockEventListener(true);
        holderRule.getHolder().subscribe(listener1);
        final MockEventListener listener2 = new MockEventListener();
        holderRule.getHolder().subscribe(listener2);
        holderRule.assertListenerAdded(listener1, listener2);
        final String arg = "doom";
        holderRule.getHolder().invokeListeners(new rx.functions.Action2<MockEventListener, String>() {
            @Override
            public void call(MockEventListener mockEventListener, String arg) {
                mockEventListener.anEventWithArg(arg);
            }
        }, arg);
        Assert.assertThat("Listener not invoked.", listener1.getEventInvocationCount(), is(1));
        Assert.assertThat("Listener not invoked with argument.", listener1.getArg(), is(arg));
        Assert.assertThat("Second listener not invoked.", listener2.getEventInvocationCount(), is(1));
        Assert.assertThat("Second listener not invoked with argument.", listener2.getArg(), is(arg));
    }

    @Test(timeout = 60000)
    public void testDuplicateListeners() throws Exception {
        ListenersHolderRule.ListenerWithSub l = holderRule.addAListener();
        holderRule.assertListenerAdded(l.listener);
        holderRule.getHolder().subscribe(l.listener);
        Assert.assertThat("Duplicate listener added.", holderRule.getHolder().getActualListenersList(), hasSize(1));
        l.subscription.unsubscribe();
        Assert.assertThat("Listener not removed on unsubscribe.", holderRule.getHolder().getAllListeners(), is(empty()));
    }

    @Test(timeout = 60000)
    public void testCopy() throws Exception {
        final MockEventListener listener = new MockEventListener();
        Subscription subscription = holderRule.getHolder().subscribe(listener);
        holderRule.assertListenerAdded(listener);
        final ListenersHolder<MockEventListener> copy = holderRule.getHolder().copy();
        Assert.assertThat("Holder not copied", copy, is(not(holderRule.getHolder())));
        Assert.assertThat("Listeners list not copied", copy.getActualListenersList(), not(sameInstance(holderRule.getHolder().getActualListenersList())));
        final Collection<MockEventListener> allListenersCopied = copy.getAllListeners();
        Assert.assertThat("Registered listeners not copied", allListenersCopied, contains(listener));
        subscription.unsubscribe();
        Assert.assertThat("Not removed from copy on unsubscribe.", copy.getAllListeners(), not(contains(listener)));
    }
}


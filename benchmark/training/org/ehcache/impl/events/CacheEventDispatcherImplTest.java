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
package org.ehcache.impl.events;


import java.util.EnumSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.ehcache.core.spi.store.events.StoreEventListener;
import org.ehcache.core.spi.store.events.StoreEventSource;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class CacheEventDispatcherImplTest {
    private CacheEventDispatcherImpl<Number, String> eventService;

    private CacheEventListener<Number, String> listener;

    private ExecutorService orderedExecutor;

    private ExecutorService unorderedExecutor;

    private StoreEventSource storeEventDispatcher;

    @Test
    public void testAsyncEventFiring() throws Exception {
        eventService = new CacheEventDispatcherImpl<>(Executors.newCachedThreadPool(), orderedExecutor);
        eventService.setStoreEventSource(storeEventDispatcher);
        final CountDownLatch signal = new CountDownLatch(1);
        final CountDownLatch signal2 = new CountDownLatch(1);
        Mockito.doAnswer(( invocation) -> {
            if (!(signal.await(2, TimeUnit.SECONDS))) {
                return null;
            } else {
                signal2.countDown();
                return null;
            }
        }).when(listener).onEvent(ArgumentMatchers.any(CacheEvent.class));
        eventService.registerCacheEventListener(listener, EventOrdering.UNORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED));
        final CacheEvent<Number, String> create = CacheEventDispatcherImplTest.eventOfType(EventType.CREATED);
        eventService.onEvent(create);
        signal.countDown();
        if (!(signal2.await(2, TimeUnit.SECONDS))) {
            Assert.fail("event handler never triggered latch - are we synchronous?");
        }
    }

    @Test
    public void testCheckEventType() {
        eventService.registerCacheEventListener(listener, EventOrdering.UNORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.EVICTED));
        CacheEvent<Number, String> create = CacheEventDispatcherImplTest.eventOfType(EventType.CREATED);
        eventService.onEvent(create);
        Mockito.verify(listener, Mockito.never()).onEvent(ArgumentMatchers.any(CacheEvent.class));
        CacheEvent<Number, String> evict = CacheEventDispatcherImplTest.eventOfType(EventType.EVICTED);
        eventService.onEvent(evict);
        Mockito.verify(listener).onEvent(evict);
    }

    @Test
    public void testListenerRegistrationEnablesStoreEvents() {
        eventService.registerCacheEventListener(listener, EventOrdering.UNORDERED, EventFiring.ASYNCHRONOUS, EnumSet.allOf(EventType.class));
        Mockito.verify(storeEventDispatcher).addEventListener(ArgumentMatchers.any(StoreEventListener.class));
    }

    @Test
    public void testOrderedListenerRegistrationTogglesOrderedOnStoreEvents() {
        eventService.registerCacheEventListener(listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.allOf(EventType.class));
        Mockito.verify(storeEventDispatcher).setEventOrdering(true);
    }

    @Test(expected = IllegalStateException.class)
    public void testDuplicateRegistration() {
        eventService.registerCacheEventListener(listener, EventOrdering.UNORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.EVICTED));
        eventService.registerCacheEventListener(listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.EXPIRED));
    }

    @Test(expected = IllegalStateException.class)
    public void testUnknownListenerDeregistration() {
        eventService.deregisterCacheEventListener(listener);
    }

    @Test
    public void testDeregisterLastListenerStopsStoreEvents() {
        eventService.registerCacheEventListener(listener, EventOrdering.UNORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.EVICTED));
        eventService.deregisterCacheEventListener(listener);
        Mockito.verify(storeEventDispatcher).removeEventListener(ArgumentMatchers.any(StoreEventListener.class));
    }

    @Test
    public void testDeregisterLastOrderedListenerTogglesOffOrderedStoreEvents() {
        eventService.registerCacheEventListener(listener, EventOrdering.ORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.EVICTED));
        eventService.deregisterCacheEventListener(listener);
        Mockito.verify(storeEventDispatcher).setEventOrdering(false);
    }

    @Test
    public void testDeregisterNotLastOrderedListenerDoesNotToggleOffOrderedStoreEvents() {
        eventService.registerCacheEventListener(listener, EventOrdering.ORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.EVICTED));
        eventService.registerCacheEventListener(Mockito.mock(CacheEventListener.class), EventOrdering.ORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.EVICTED));
        eventService.deregisterCacheEventListener(listener);
        Mockito.verify(storeEventDispatcher, Mockito.never()).setEventOrdering(false);
    }

    @Test
    public void testDeregisterNotLastListenerDoesNotStopStoreEvents() {
        eventService.registerCacheEventListener(listener, EventOrdering.UNORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.EVICTED));
        eventService.registerCacheEventListener(Mockito.mock(CacheEventListener.class), EventOrdering.UNORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.EVICTED));
        eventService.deregisterCacheEventListener(listener);
        Mockito.verify(storeEventDispatcher, Mockito.never()).removeEventListener(ArgumentMatchers.any(StoreEventListener.class));
    }

    @Test
    public void testShutdownDisableStoreEventsAndShutsDownOrderedExecutor() {
        eventService.registerCacheEventListener(listener, EventOrdering.UNORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.CREATED));
        CacheEventListener<Number, String> otherLsnr = Mockito.mock(CacheEventListener.class);
        eventService.registerCacheEventListener(otherLsnr, EventOrdering.ORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.REMOVED));
        eventService.shutdown();
        InOrder inOrder = Mockito.inOrder(storeEventDispatcher, orderedExecutor);
        inOrder.verify(storeEventDispatcher).removeEventListener(ArgumentMatchers.any(StoreEventListener.class));
        inOrder.verify(storeEventDispatcher).setEventOrdering(false);
        inOrder.verify(orderedExecutor).shutdown();
        inOrder.verifyNoMoreInteractions();
    }
}


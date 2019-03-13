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
package org.ehcache.impl.internal.events;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.ehcache.core.events.StoreEventSink;
import org.ehcache.core.spi.store.events.StoreEvent;
import org.ehcache.core.spi.store.events.StoreEventFilter;
import org.ehcache.core.spi.store.events.StoreEventListener;
import org.ehcache.event.EventType;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ehcache.impl.internal.util.Matchers.eventOfType;


/**
 * ScopedStoreEventDispatcherTest
 */
public class ScopedStoreEventDispatcherTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopedStoreEventDispatcherTest.class);

    @Test
    public void testRegistersOrderingChange() {
        ScopedStoreEventDispatcher<Object, Object> dispatcher = new ScopedStoreEventDispatcher<>(1);
        Assert.assertThat(dispatcher.isEventOrdering(), Matchers.is(false));
        dispatcher.setEventOrdering(true);
        Assert.assertThat(dispatcher.isEventOrdering(), Matchers.is(true));
        dispatcher.setEventOrdering(false);
        Assert.assertThat(dispatcher.isEventOrdering(), Matchers.is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListenerNotifiedUnordered() {
        ScopedStoreEventDispatcher<String, String> dispatcher = new ScopedStoreEventDispatcher<>(1);
        @SuppressWarnings("unchecked")
        StoreEventListener<String, String> listener = Mockito.mock(StoreEventListener.class);
        dispatcher.addEventListener(listener);
        StoreEventSink<String, String> sink = dispatcher.eventSink();
        sink.created("test", "test");
        dispatcher.releaseEventSink(sink);
        Mockito.verify(listener).onEvent(ArgumentMatchers.any(StoreEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListenerNotifiedOrdered() {
        ScopedStoreEventDispatcher<String, String> dispatcher = new ScopedStoreEventDispatcher<>(1);
        @SuppressWarnings("unchecked")
        StoreEventListener<String, String> listener = Mockito.mock(StoreEventListener.class);
        dispatcher.addEventListener(listener);
        dispatcher.setEventOrdering(true);
        StoreEventSink<String, String> sink = dispatcher.eventSink();
        sink.created("test", "test");
        dispatcher.releaseEventSink(sink);
        Mockito.verify(listener).onEvent(ArgumentMatchers.any(StoreEvent.class));
    }

    @Test
    public void testEventFiltering() {
        ScopedStoreEventDispatcher<String, String> dispatcher = new ScopedStoreEventDispatcher<>(1);
        @SuppressWarnings("unchecked")
        StoreEventListener<String, String> listener = Mockito.mock(StoreEventListener.class, Mockito.withSettings().verboseLogging());
        dispatcher.addEventListener(listener);
        @SuppressWarnings("unchecked")
        StoreEventFilter<String, String> filter = Mockito.mock(StoreEventFilter.class);
        Mockito.when(filter.acceptEvent(ArgumentMatchers.eq(EventType.CREATED), ArgumentMatchers.anyString(), ArgumentMatchers.<String>isNull(), ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(filter.acceptEvent(ArgumentMatchers.eq(EventType.REMOVED), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(false);
        dispatcher.addEventFilter(filter);
        StoreEventSink<String, String> sink = dispatcher.eventSink();
        sink.removed("gone", () -> "really gone");
        sink.created("new", "and shiny");
        dispatcher.releaseEventSink(sink);
        Matcher<StoreEvent<String, String>> matcher = eventOfType(EventType.CREATED);
        Mockito.verify(listener).onEvent(MockitoHamcrest.argThat(matcher));
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testOrderedEventDelivery() throws Exception {
        final ScopedStoreEventDispatcher<Long, Boolean> dispatcher = new ScopedStoreEventDispatcher<>(4);
        dispatcher.setEventOrdering(true);
        final ConcurrentHashMap<Long, Long> map = new ConcurrentHashMap<>();
        final long[] keys = new long[]{ 1L, 42L, 256L };
        map.put(keys[0], 125L);
        map.put(keys[1], (42 * 125L));
        map.put(keys[2], (256 * 125L));
        final ConcurrentHashMap<Long, Long> resultMap = new ConcurrentHashMap<>(map);
        dispatcher.addEventListener(( event) -> {
            if (event.getNewValue()) {
                resultMap.compute(event.getKey(), ( key, value) -> value + 10L);
            } else {
                resultMap.compute(event.getKey(), ( key, value) -> 7L - value);
            }
        });
        final long seed = new Random().nextLong();
        ScopedStoreEventDispatcherTest.LOGGER.info("Starting test with seed {}", seed);
        int workers = (Runtime.getRuntime().availableProcessors()) + 2;
        final CountDownLatch latch = new CountDownLatch(workers);
        for (int i = 0; i < workers; i++) {
            final int index = i;
            new Thread(() -> {
                Random random = new Random((seed * index));
                for (int j = 0; j < 10000; j++) {
                    int keyIndex = random.nextInt(3);
                    final StoreEventSink<Long, Boolean> sink = dispatcher.eventSink();
                    if (random.nextBoolean()) {
                        map.compute(keys[keyIndex], ( key, value) -> {
                            long newValue = value + 10L;
                            sink.created(key, true);
                            return newValue;
                        });
                    } else {
                        map.compute(keys[keyIndex], ( key, value) -> {
                            long newValue = 7L - value;
                            sink.created(key, false);
                            return newValue;
                        });
                    }
                    dispatcher.releaseEventSink(sink);
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        ScopedStoreEventDispatcherTest.LOGGER.info("\n\tResult map {} \n\tWork map {}", resultMap, map);
        Assert.assertThat(resultMap, Matchers.is(map));
    }
}


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


import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.ehcache.core.spi.store.events.StoreEvent;
import org.ehcache.core.spi.store.events.StoreEventListener;
import org.ehcache.event.EventType;
import org.ehcache.impl.internal.store.offheap.AbstractOffHeapStoreTest;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * InvocationScopedEventSinkTest
 */
public class InvocationScopedEventSinkTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private StoreEventListener<String, String> listener;

    private InvocationScopedEventSink<String, String> eventSink;

    private BlockingQueue<FireableStoreEventHolder<String, String>> blockingQueue;

    private Set<StoreEventListener<String, String>> storeEventListeners;

    @Test
    public void testReset() {
        eventSink = createEventSink(false);
        eventSink.created("k1", "v1");
        eventSink.evicted("k1", () -> "v2");
        eventSink.reset();
        eventSink.created("k1", "v1");
        eventSink.updated("k1", () -> "v1", "v2");
        eventSink.evicted("k1", () -> "v2");
        eventSink.close();
        InOrder inOrder = Mockito.inOrder(listener);
        Matcher<StoreEvent<String, String>> createdMatcher = AbstractOffHeapStoreTest.eventType(EventType.CREATED);
        inOrder.verify(listener).onEvent(MockitoHamcrest.argThat(createdMatcher));
        Matcher<StoreEvent<String, String>> updatedMatcher = AbstractOffHeapStoreTest.eventType(EventType.UPDATED);
        inOrder.verify(listener).onEvent(MockitoHamcrest.argThat(updatedMatcher));
        Matcher<StoreEvent<String, String>> evictedMatcher = AbstractOffHeapStoreTest.eventType(EventType.EVICTED);
        inOrder.verify(listener).onEvent(MockitoHamcrest.argThat(evictedMatcher));
        Mockito.verifyNoMoreInteractions(listener);
    }

    /**
     * Make sure an interrupted sink sets the interrupted flag and keep both event queues in the state
     * as of before the event that was interrupted.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testInterruption() throws InterruptedException {
        eventSink = createEventSink(true);
        // Add enough elements to fill the queue
        IntStream.range(0, 10).forEachOrdered(( i) -> eventSink.created(("k" + i), ("v" + i)));
        AtomicBoolean wasInterrupted = new AtomicBoolean(false);
        Thread t = new Thread(() -> {
            // add one element that will block on the full queue
            eventSink.created("k", "v");
            wasInterrupted.set(Thread.currentThread().isInterrupted());
        });
        t.start();
        while ((blockingQueue.remainingCapacity()) != 0) {
            System.out.println(blockingQueue.remainingCapacity());
        } 
        t.interrupt();
        t.join();
        assertThat(wasInterrupted).isTrue();
        assertThat(blockingQueue).hasSize(10);
        IntStream.range(0, 10).forEachOrdered(( i) -> {
            try {
                assertThat(blockingQueue.take().getEvent().getKey()).isEqualTo(("k" + i));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        assertThat(eventSink.getEvents()).hasSize(10);
        assertThat(eventSink.getEvents().getLast().getEvent().getKey()).isEqualTo("k9");
    }
}


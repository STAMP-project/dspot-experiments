/**
 * Copyright 2011 LMAX Ltd.
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
package com.lmax.disruptor.dsl;


import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.stubs.DelayedEventHandler;
import com.lmax.disruptor.dsl.stubs.EventHandlerStub;
import com.lmax.disruptor.dsl.stubs.EvilEqualsEventHandler;
import com.lmax.disruptor.dsl.stubs.ExceptionThrowingEventHandler;
import com.lmax.disruptor.dsl.stubs.SleepingEventHandler;
import com.lmax.disruptor.dsl.stubs.StubExceptionHandler;
import com.lmax.disruptor.dsl.stubs.StubPublisher;
import com.lmax.disruptor.dsl.stubs.StubThreadFactory;
import com.lmax.disruptor.dsl.stubs.TestWorkHandler;
import com.lmax.disruptor.support.TestEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings({ "unchecked" })
public class DisruptorTest {
    private static final int TIMEOUT_IN_SECONDS = 2;

    private final Collection<DelayedEventHandler> delayedEventHandlers = new ArrayList<DelayedEventHandler>();

    private final Collection<TestWorkHandler> testWorkHandlers = new ArrayList<TestWorkHandler>();

    private Disruptor<TestEvent> disruptor;

    private StubThreadFactory executor;

    private RingBuffer<TestEvent> ringBuffer;

    private TestEvent lastPublishedEvent;

    @Test
    public void shouldProcessMessagesPublishedBeforeStartIsCalled() throws Exception {
        final CountDownLatch eventCounter = new CountDownLatch(2);
        disruptor.handleEventsWith(new com.lmax.disruptor.EventHandler<TestEvent>() {
            @Override
            public void onEvent(final TestEvent event, final long sequence, final boolean endOfBatch) throws Exception {
                eventCounter.countDown();
            }
        });
        disruptor.publishEvent(new com.lmax.disruptor.EventTranslator<TestEvent>() {
            @Override
            public void translateTo(final TestEvent event, final long sequence) {
                lastPublishedEvent = event;
            }
        });
        disruptor.start();
        disruptor.publishEvent(new com.lmax.disruptor.EventTranslator<TestEvent>() {
            @Override
            public void translateTo(final TestEvent event, final long sequence) {
                lastPublishedEvent = event;
            }
        });
        if (!(eventCounter.await(5, TimeUnit.SECONDS))) {
            Assert.fail(("Did not process event published before start was called. Missed events: " + (eventCounter.getCount())));
        }
    }

    @Test
    public void shouldBatchOfEvents() throws Exception {
        final CountDownLatch eventCounter = new CountDownLatch(2);
        disruptor.handleEventsWith(new com.lmax.disruptor.EventHandler<TestEvent>() {
            @Override
            public void onEvent(final TestEvent event, final long sequence, final boolean endOfBatch) throws Exception {
                eventCounter.countDown();
            }
        });
        disruptor.start();
        disruptor.publishEvents(new com.lmax.disruptor.EventTranslatorOneArg<TestEvent, Object>() {
            @Override
            public void translateTo(final TestEvent event, final long sequence, Object arg) {
                lastPublishedEvent = event;
            }
        }, new Object[]{ "a", "b" });
        if (!(eventCounter.await(5, TimeUnit.SECONDS))) {
            Assert.fail(("Did not process event published before start was called. Missed events: " + (eventCounter.getCount())));
        }
    }

    @Test
    public void shouldAddEventProcessorsAfterPublishing() throws Exception {
        RingBuffer<TestEvent> rb = disruptor.getRingBuffer();
        BatchEventProcessor<TestEvent> b1 = new BatchEventProcessor<TestEvent>(rb, rb.newBarrier(), new SleepingEventHandler());
        BatchEventProcessor<TestEvent> b2 = new BatchEventProcessor<TestEvent>(rb, rb.newBarrier(b1.getSequence()), new SleepingEventHandler());
        BatchEventProcessor<TestEvent> b3 = new BatchEventProcessor<TestEvent>(rb, rb.newBarrier(b2.getSequence()), new SleepingEventHandler());
        Assert.assertThat(b1.getSequence().get(), CoreMatchers.is((-1L)));
        Assert.assertThat(b2.getSequence().get(), CoreMatchers.is((-1L)));
        Assert.assertThat(b3.getSequence().get(), CoreMatchers.is((-1L)));
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        disruptor.handleEventsWith(b1, b2, b3);
        Assert.assertThat(b1.getSequence().get(), CoreMatchers.is(5L));
        Assert.assertThat(b2.getSequence().get(), CoreMatchers.is(5L));
        Assert.assertThat(b3.getSequence().get(), CoreMatchers.is(5L));
    }

    @Test
    public void shouldSetSequenceForHandlerIfAddedAfterPublish() throws Exception {
        RingBuffer<TestEvent> rb = disruptor.getRingBuffer();
        com.lmax.disruptor.EventHandler<TestEvent> b1 = new SleepingEventHandler();
        com.lmax.disruptor.EventHandler<TestEvent> b2 = new SleepingEventHandler();
        com.lmax.disruptor.EventHandler<TestEvent> b3 = new SleepingEventHandler();
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        disruptor.handleEventsWith(b1, b2, b3);
        Assert.assertThat(disruptor.getSequenceValueFor(b1), CoreMatchers.is(5L));
        Assert.assertThat(disruptor.getSequenceValueFor(b2), CoreMatchers.is(5L));
        Assert.assertThat(disruptor.getSequenceValueFor(b3), CoreMatchers.is(5L));
    }

    @Test
    public void shouldSetSequenceForWorkProcessorIfAddedAfterPublish() throws Exception {
        RingBuffer<TestEvent> rb = disruptor.getRingBuffer();
        TestWorkHandler wh1 = createTestWorkHandler();
        TestWorkHandler wh2 = createTestWorkHandler();
        TestWorkHandler wh3 = createTestWorkHandler();
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        rb.publish(rb.next());
        disruptor.handleEventsWithWorkerPool(wh1, wh2, wh3);
        Assert.assertThat(disruptor.getRingBuffer().getMinimumGatingSequence(), CoreMatchers.is(5L));
    }

    @Test
    public void shouldCreateEventProcessorGroupForFirstEventProcessors() throws Exception {
        executor.ignoreExecutions();
        final com.lmax.disruptor.EventHandler<TestEvent> eventHandler1 = new SleepingEventHandler();
        com.lmax.disruptor.EventHandler<TestEvent> eventHandler2 = new SleepingEventHandler();
        final EventHandlerGroup<TestEvent> eventHandlerGroup = disruptor.handleEventsWith(eventHandler1, eventHandler2);
        disruptor.start();
        Assert.assertNotNull(eventHandlerGroup);
        Assert.assertThat(Integer.valueOf(executor.getExecutionCount()), CoreMatchers.equalTo(Integer.valueOf(2)));
    }

    @Test
    public void shouldMakeEntriesAvailableToFirstHandlersImmediately() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        com.lmax.disruptor.EventHandler<TestEvent> eventHandler = new EventHandlerStub<TestEvent>(countDownLatch);
        disruptor.handleEventsWith(createDelayedEventHandler(), eventHandler);
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch);
    }

    @Test
    public void shouldWaitUntilAllFirstEventProcessorsProcessEventBeforeMakingItAvailableToDependentEventProcessors() throws Exception {
        DelayedEventHandler eventHandler1 = createDelayedEventHandler();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        com.lmax.disruptor.EventHandler<TestEvent> eventHandler2 = new EventHandlerStub<TestEvent>(countDownLatch);
        disruptor.handleEventsWith(eventHandler1).then(eventHandler2);
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch, eventHandler1);
    }

    @Test
    public void should() throws Exception {
        RingBuffer<TestEvent> rb = disruptor.getRingBuffer();
        BatchEventProcessor<TestEvent> b1 = new BatchEventProcessor<TestEvent>(rb, rb.newBarrier(), new SleepingEventHandler());
        EventProcessorFactory<TestEvent> b2 = new EventProcessorFactory<TestEvent>() {
            @Override
            public EventProcessor createEventProcessor(RingBuffer<TestEvent> ringBuffer, Sequence[] barrierSequences) {
                return new BatchEventProcessor<TestEvent>(ringBuffer, ringBuffer.newBarrier(barrierSequences), new SleepingEventHandler());
            }
        };
        disruptor.handleEventsWith(b1).then(b2);
        disruptor.start();
    }

    @Test
    public void shouldAllowSpecifyingSpecificEventProcessorsToWaitFor() throws Exception {
        DelayedEventHandler handler1 = createDelayedEventHandler();
        DelayedEventHandler handler2 = createDelayedEventHandler();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        com.lmax.disruptor.EventHandler<TestEvent> handlerWithBarrier = new EventHandlerStub<TestEvent>(countDownLatch);
        disruptor.handleEventsWith(handler1, handler2);
        disruptor.after(handler1, handler2).handleEventsWith(handlerWithBarrier);
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch, handler1, handler2);
    }

    @Test
    public void shouldWaitOnAllProducersJoinedByAnd() throws Exception {
        DelayedEventHandler handler1 = createDelayedEventHandler();
        DelayedEventHandler handler2 = createDelayedEventHandler();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        com.lmax.disruptor.EventHandler<TestEvent> handlerWithBarrier = new EventHandlerStub<TestEvent>(countDownLatch);
        disruptor.handleEventsWith(handler1);
        final EventHandlerGroup<TestEvent> handler2Group = disruptor.handleEventsWith(handler2);
        disruptor.after(handler1).and(handler2Group).handleEventsWith(handlerWithBarrier);
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch, handler1, handler2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfHandlerIsNotAlreadyConsuming() throws Exception {
        disruptor.after(createDelayedEventHandler()).handleEventsWith(createDelayedEventHandler());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldTrackEventHandlersByIdentityNotEquality() throws Exception {
        EvilEqualsEventHandler handler1 = new EvilEqualsEventHandler();
        EvilEqualsEventHandler handler2 = new EvilEqualsEventHandler();
        disruptor.handleEventsWith(handler1);
        // handler2.equals(handler1) but it hasn't yet been registered so should throw exception.
        disruptor.after(handler2);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldSupportSpecifyingAExceptionHandlerForEventProcessors() throws Exception {
        AtomicReference<Throwable> eventHandled = new AtomicReference<Throwable>();
        ExceptionHandler exceptionHandler = new StubExceptionHandler(eventHandled);
        RuntimeException testException = new RuntimeException();
        ExceptionThrowingEventHandler handler = new ExceptionThrowingEventHandler(testException);
        disruptor.handleExceptionsWith(exceptionHandler);
        disruptor.handleEventsWith(handler);
        publishEvent();
        final Throwable actualException = waitFor(eventHandled);
        Assert.assertSame(testException, actualException);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldOnlyApplyExceptionsHandlersSpecifiedViaHandleExceptionsWithOnNewEventProcessors() throws Exception {
        AtomicReference<Throwable> eventHandled = new AtomicReference<Throwable>();
        ExceptionHandler exceptionHandler = new StubExceptionHandler(eventHandled);
        RuntimeException testException = new RuntimeException();
        ExceptionThrowingEventHandler handler = new ExceptionThrowingEventHandler(testException);
        disruptor.handleExceptionsWith(exceptionHandler);
        disruptor.handleEventsWith(handler);
        disruptor.handleExceptionsWith(new FatalExceptionHandler());
        publishEvent();
        final Throwable actualException = waitFor(eventHandled);
        Assert.assertSame(testException, actualException);
    }

    @Test
    public void shouldSupportSpecifyingADefaultExceptionHandlerForEventProcessors() throws Exception {
        AtomicReference<Throwable> eventHandled = new AtomicReference<Throwable>();
        ExceptionHandler exceptionHandler = new StubExceptionHandler(eventHandled);
        RuntimeException testException = new RuntimeException();
        ExceptionThrowingEventHandler handler = new ExceptionThrowingEventHandler(testException);
        disruptor.setDefaultExceptionHandler(exceptionHandler);
        disruptor.handleEventsWith(handler);
        publishEvent();
        final Throwable actualException = waitFor(eventHandled);
        Assert.assertSame(testException, actualException);
    }

    @Test
    public void shouldApplyDefaultExceptionHandlerToExistingEventProcessors() throws Exception {
        AtomicReference<Throwable> eventHandled = new AtomicReference<Throwable>();
        ExceptionHandler exceptionHandler = new StubExceptionHandler(eventHandled);
        RuntimeException testException = new RuntimeException();
        ExceptionThrowingEventHandler handler = new ExceptionThrowingEventHandler(testException);
        disruptor.handleEventsWith(handler);
        disruptor.setDefaultExceptionHandler(exceptionHandler);
        publishEvent();
        final Throwable actualException = waitFor(eventHandled);
        Assert.assertSame(testException, actualException);
    }

    @Test
    public void shouldBlockProducerUntilAllEventProcessorsHaveAdvanced() throws Exception {
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        disruptor.handleEventsWith(delayedEventHandler);
        final RingBuffer<TestEvent> ringBuffer = disruptor.start();
        delayedEventHandler.awaitStart();
        final StubPublisher stubPublisher = new StubPublisher(ringBuffer);
        try {
            executor.newThread(stubPublisher).start();
            assertProducerReaches(stubPublisher, 4, true);
            delayedEventHandler.processEvent();
            delayedEventHandler.processEvent();
            delayedEventHandler.processEvent();
            delayedEventHandler.processEvent();
            delayedEventHandler.processEvent();
            assertProducerReaches(stubPublisher, 5, false);
        } finally {
            stubPublisher.halt();
        }
    }

    @Test
    public void shouldBeAbleToOverrideTheExceptionHandlerForAEventProcessor() throws Exception {
        final RuntimeException testException = new RuntimeException();
        final ExceptionThrowingEventHandler eventHandler = new ExceptionThrowingEventHandler(testException);
        disruptor.handleEventsWith(eventHandler);
        AtomicReference<Throwable> reference = new AtomicReference<Throwable>();
        StubExceptionHandler exceptionHandler = new StubExceptionHandler(reference);
        disruptor.handleExceptionsFor(eventHandler).with(exceptionHandler);
        publishEvent();
        waitFor(reference);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenAddingEventProcessorsAfterTheProducerBarrierHasBeenCreated() throws Exception {
        executor.ignoreExecutions();
        disruptor.handleEventsWith(new SleepingEventHandler());
        disruptor.start();
        disruptor.handleEventsWith(new SleepingEventHandler());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfStartIsCalledTwice() throws Exception {
        executor.ignoreExecutions();
        disruptor.handleEventsWith(new SleepingEventHandler());
        disruptor.start();
        disruptor.start();
    }

    @Test
    public void shouldSupportCustomProcessorsAsDependencies() throws Exception {
        RingBuffer<TestEvent> ringBuffer = disruptor.getRingBuffer();
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        com.lmax.disruptor.EventHandler<TestEvent> handlerWithBarrier = new EventHandlerStub<TestEvent>(countDownLatch);
        final BatchEventProcessor<TestEvent> processor = new BatchEventProcessor<TestEvent>(ringBuffer, ringBuffer.newBarrier(), delayedEventHandler);
        disruptor.handleEventsWith(processor).then(handlerWithBarrier);
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch, delayedEventHandler);
    }

    @Test
    public void shouldSupportHandlersAsDependenciesToCustomProcessors() throws Exception {
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        disruptor.handleEventsWith(delayedEventHandler);
        RingBuffer<TestEvent> ringBuffer = disruptor.getRingBuffer();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        com.lmax.disruptor.EventHandler<TestEvent> handlerWithBarrier = new EventHandlerStub<TestEvent>(countDownLatch);
        final SequenceBarrier sequenceBarrier = disruptor.after(delayedEventHandler).asSequenceBarrier();
        final BatchEventProcessor<TestEvent> processor = new BatchEventProcessor<TestEvent>(ringBuffer, sequenceBarrier, handlerWithBarrier);
        disruptor.handleEventsWith(processor);
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch, delayedEventHandler);
    }

    @Test
    public void shouldSupportCustomProcessorsAndHandlersAsDependencies() throws Exception {
        final DelayedEventHandler delayedEventHandler1 = createDelayedEventHandler();
        final DelayedEventHandler delayedEventHandler2 = createDelayedEventHandler();
        disruptor.handleEventsWith(delayedEventHandler1);
        RingBuffer<TestEvent> ringBuffer = disruptor.getRingBuffer();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        com.lmax.disruptor.EventHandler<TestEvent> handlerWithBarrier = new EventHandlerStub<TestEvent>(countDownLatch);
        final SequenceBarrier sequenceBarrier = disruptor.after(delayedEventHandler1).asSequenceBarrier();
        final BatchEventProcessor<TestEvent> processor = new BatchEventProcessor<TestEvent>(ringBuffer, sequenceBarrier, delayedEventHandler2);
        disruptor.after(delayedEventHandler1).and(processor).handleEventsWith(handlerWithBarrier);
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch, delayedEventHandler1, delayedEventHandler2);
    }

    @Test
    public void shouldProvideEventsToWorkHandlers() throws Exception {
        final TestWorkHandler workHandler1 = createTestWorkHandler();
        final TestWorkHandler workHandler2 = createTestWorkHandler();
        disruptor.handleEventsWithWorkerPool(workHandler1, workHandler2);
        publishEvent();
        publishEvent();
        workHandler1.processEvent();
        workHandler2.processEvent();
    }

    @Test
    public void shouldProvideEventsMultipleWorkHandlers() throws Exception {
        final TestWorkHandler workHandler1 = createTestWorkHandler();
        final TestWorkHandler workHandler2 = createTestWorkHandler();
        final TestWorkHandler workHandler3 = createTestWorkHandler();
        final TestWorkHandler workHandler4 = createTestWorkHandler();
        final TestWorkHandler workHandler5 = createTestWorkHandler();
        final TestWorkHandler workHandler6 = createTestWorkHandler();
        final TestWorkHandler workHandler7 = createTestWorkHandler();
        final TestWorkHandler workHandler8 = createTestWorkHandler();
        disruptor.handleEventsWithWorkerPool(workHandler1, workHandler2).thenHandleEventsWithWorkerPool(workHandler3, workHandler4);
        disruptor.handleEventsWithWorkerPool(workHandler5, workHandler6).thenHandleEventsWithWorkerPool(workHandler7, workHandler8);
    }

    @Test
    public void shouldSupportUsingWorkerPoolAsDependency() throws Exception {
        final TestWorkHandler workHandler1 = createTestWorkHandler();
        final TestWorkHandler workHandler2 = createTestWorkHandler();
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        disruptor.handleEventsWithWorkerPool(workHandler1, workHandler2).then(delayedEventHandler);
        publishEvent();
        publishEvent();
        Assert.assertThat(disruptor.getBarrierFor(delayedEventHandler).getCursor(), CoreMatchers.equalTo((-1L)));
        workHandler2.processEvent();
        workHandler1.processEvent();
        delayedEventHandler.processEvent();
    }

    @Test
    public void shouldSupportUsingWorkerPoolAsDependencyAndProcessFirstEventAsSoonAsItIsAvailable() throws Exception {
        final TestWorkHandler workHandler1 = createTestWorkHandler();
        final TestWorkHandler workHandler2 = createTestWorkHandler();
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        disruptor.handleEventsWithWorkerPool(workHandler1, workHandler2).then(delayedEventHandler);
        publishEvent();
        publishEvent();
        workHandler1.processEvent();
        delayedEventHandler.processEvent();
        workHandler2.processEvent();
        delayedEventHandler.processEvent();
    }

    @Test
    public void shouldSupportUsingWorkerPoolWithADependency() throws Exception {
        final TestWorkHandler workHandler1 = createTestWorkHandler();
        final TestWorkHandler workHandler2 = createTestWorkHandler();
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        disruptor.handleEventsWith(delayedEventHandler).thenHandleEventsWithWorkerPool(workHandler1, workHandler2);
        publishEvent();
        publishEvent();
        delayedEventHandler.processEvent();
        delayedEventHandler.processEvent();
        workHandler1.processEvent();
        workHandler2.processEvent();
    }

    @Test
    public void shouldSupportCombiningWorkerPoolWithEventHandlerAsDependencyWhenNotPreviouslyRegistered() throws Exception {
        final TestWorkHandler workHandler1 = createTestWorkHandler();
        final DelayedEventHandler delayedEventHandler1 = createDelayedEventHandler();
        final DelayedEventHandler delayedEventHandler2 = createDelayedEventHandler();
        disruptor.handleEventsWith(delayedEventHandler1).and(disruptor.handleEventsWithWorkerPool(workHandler1)).then(delayedEventHandler2);
        publishEvent();
        publishEvent();
        delayedEventHandler1.processEvent();
        delayedEventHandler1.processEvent();
        workHandler1.processEvent();
        delayedEventHandler2.processEvent();
        workHandler1.processEvent();
        delayedEventHandler2.processEvent();
    }

    @Test(expected = TimeoutException.class, timeout = 2000)
    public void shouldThrowTimeoutExceptionIfShutdownDoesNotCompleteNormally() throws Exception {
        // Given
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        disruptor.handleEventsWith(delayedEventHandler);
        publishEvent();
        // When
        disruptor.shutdown(1, TimeUnit.SECONDS);
        // Then
    }

    @Test(timeout = 1000)
    public void shouldTrackRemainingCapacity() throws Exception {
        final long[] remainingCapacity = new long[]{ -1 };
        // Given
        final com.lmax.disruptor.EventHandler<TestEvent> eventHandler = new com.lmax.disruptor.EventHandler<TestEvent>() {
            @Override
            public void onEvent(final TestEvent event, final long sequence, final boolean endOfBatch) throws Exception {
                remainingCapacity[0] = disruptor.getRingBuffer().remainingCapacity();
            }
        };
        disruptor.handleEventsWith(eventHandler);
        // When
        publishEvent();
        // Then
        while ((remainingCapacity[0]) == (-1)) {
            Thread.sleep(100);
        } 
        Assert.assertThat(remainingCapacity[0], CoreMatchers.is(((ringBuffer.getBufferSize()) - 1L)));
        Assert.assertThat(disruptor.getRingBuffer().remainingCapacity(), CoreMatchers.is(((ringBuffer.getBufferSize()) - 0L)));
    }

    @Test
    public void shouldAllowEventHandlerWithSuperType() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        final com.lmax.disruptor.EventHandler<Object> objectHandler = new EventHandlerStub<Object>(latch);
        disruptor.handleEventsWith(objectHandler);
        ensureTwoEventsProcessedAccordingToDependencies(latch);
    }

    @Test
    public void shouldAllowChainingEventHandlersWithSuperType() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        final com.lmax.disruptor.EventHandler<Object> objectHandler = new EventHandlerStub<Object>(latch);
        disruptor.handleEventsWith(delayedEventHandler).then(objectHandler);
        ensureTwoEventsProcessedAccordingToDependencies(latch, delayedEventHandler);
    }

    @Test
    public void shouldMakeEntriesAvailableToFirstCustomProcessorsImmediately() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final com.lmax.disruptor.EventHandler<TestEvent> eventHandler = new EventHandlerStub<TestEvent>(countDownLatch);
        disruptor.handleEventsWith(new EventProcessorFactory<TestEvent>() {
            @Override
            public EventProcessor createEventProcessor(final RingBuffer<TestEvent> ringBuffer, final Sequence[] barrierSequences) {
                Assert.assertEquals("Should not have had any barrier sequences", 0, barrierSequences.length);
                return new BatchEventProcessor<TestEvent>(disruptor.getRingBuffer(), ringBuffer.newBarrier(barrierSequences), eventHandler);
            }
        });
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch);
    }

    @Test
    public void shouldHonourDependenciesForCustomProcessors() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final com.lmax.disruptor.EventHandler<TestEvent> eventHandler = new EventHandlerStub<TestEvent>(countDownLatch);
        final DelayedEventHandler delayedEventHandler = createDelayedEventHandler();
        disruptor.handleEventsWith(delayedEventHandler).then(new EventProcessorFactory<TestEvent>() {
            @Override
            public EventProcessor createEventProcessor(final RingBuffer<TestEvent> ringBuffer, final Sequence[] barrierSequences) {
                Assert.assertSame("Should have had a barrier sequence", 1, barrierSequences.length);
                return new BatchEventProcessor<TestEvent>(disruptor.getRingBuffer(), ringBuffer.newBarrier(barrierSequences), eventHandler);
            }
        });
        ensureTwoEventsProcessedAccordingToDependencies(countDownLatch, delayedEventHandler);
    }
}


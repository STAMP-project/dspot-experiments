package com.lmax.disruptor;


import DaemonThreadFactory.INSTANCE;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.support.DummyWaitStrategy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static Sequencer.INITIAL_CURSOR_VALUE;


@RunWith(Parameterized.class)
public class SequencerTest {
    private static final int BUFFER_SIZE = 16;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(INSTANCE);

    private final Sequencer sequencer;

    private final Sequence gatingSequence = new Sequence();

    private final ProducerType producerType;

    public SequencerTest(ProducerType producerType, WaitStrategy waitStrategy) {
        this.producerType = producerType;
        this.sequencer = newProducer(producerType, SequencerTest.BUFFER_SIZE, waitStrategy);
    }

    @Test
    public void shouldStartWithInitialValue() {
        Assert.assertEquals(0, sequencer.next());
    }

    @Test
    public void shouldBatchClaim() {
        Assert.assertEquals(3, sequencer.next(4));
    }

    @Test
    public void shouldIndicateHasAvailableCapacity() {
        sequencer.addGatingSequences(gatingSequence);
        Assert.assertTrue(sequencer.hasAvailableCapacity(1));
        Assert.assertTrue(sequencer.hasAvailableCapacity(SequencerTest.BUFFER_SIZE));
        Assert.assertFalse(sequencer.hasAvailableCapacity(((SequencerTest.BUFFER_SIZE) + 1)));
        sequencer.publish(sequencer.next());
        Assert.assertTrue(sequencer.hasAvailableCapacity(((SequencerTest.BUFFER_SIZE) - 1)));
        Assert.assertFalse(sequencer.hasAvailableCapacity(SequencerTest.BUFFER_SIZE));
    }

    @Test
    public void shouldIndicateNoAvailableCapacity() {
        sequencer.addGatingSequences(gatingSequence);
        long sequence = sequencer.next(SequencerTest.BUFFER_SIZE);
        sequencer.publish((sequence - ((SequencerTest.BUFFER_SIZE) - 1)), sequence);
        Assert.assertFalse(sequencer.hasAvailableCapacity(1));
    }

    @Test
    public void shouldHoldUpPublisherWhenBufferIsFull() throws InterruptedException {
        sequencer.addGatingSequences(gatingSequence);
        long sequence = sequencer.next(SequencerTest.BUFFER_SIZE);
        sequencer.publish((sequence - ((SequencerTest.BUFFER_SIZE) - 1)), sequence);
        final CountDownLatch waitingLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(1);
        final long expectedFullSequence = (INITIAL_CURSOR_VALUE) + (sequencer.getBufferSize());
        Assert.assertThat(sequencer.getCursor(), CoreMatchers.is(expectedFullSequence));
        executor.submit(new Runnable() {
            @Override
            public void run() {
                waitingLatch.countDown();
                long next = sequencer.next();
                sequencer.publish(next);
                doneLatch.countDown();
            }
        });
        waitingLatch.await();
        Assert.assertThat(sequencer.getCursor(), CoreMatchers.is(expectedFullSequence));
        gatingSequence.set(((INITIAL_CURSOR_VALUE) + 1L));
        doneLatch.await();
        Assert.assertThat(sequencer.getCursor(), CoreMatchers.is((expectedFullSequence + 1L)));
    }

    @Test(expected = InsufficientCapacityException.class)
    public void shouldThrowInsufficientCapacityExceptionWhenSequencerIsFull() throws Exception {
        sequencer.addGatingSequences(gatingSequence);
        for (int i = 0; i < (SequencerTest.BUFFER_SIZE); i++) {
            sequencer.next();
        }
        sequencer.tryNext();
    }

    @Test
    public void shouldCalculateRemainingCapacity() throws Exception {
        sequencer.addGatingSequences(gatingSequence);
        Assert.assertThat(sequencer.remainingCapacity(), CoreMatchers.is(((long) (SequencerTest.BUFFER_SIZE))));
        for (int i = 1; i < (SequencerTest.BUFFER_SIZE); i++) {
            sequencer.next();
            Assert.assertThat(sequencer.remainingCapacity(), CoreMatchers.is((((long) (SequencerTest.BUFFER_SIZE)) - i)));
        }
    }

    @Test
    public void shouldNotBeAvailableUntilPublished() throws Exception {
        long next = sequencer.next(6);
        for (int i = 0; i <= 5; i++) {
            Assert.assertThat(sequencer.isAvailable(i), CoreMatchers.is(false));
        }
        sequencer.publish((next - (6 - 1)), next);
        for (int i = 0; i <= 5; i++) {
            Assert.assertThat(sequencer.isAvailable(i), CoreMatchers.is(true));
        }
        Assert.assertThat(sequencer.isAvailable(6), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotifyWaitStrategyOnPublish() throws Exception {
        final DummyWaitStrategy waitStrategy = new DummyWaitStrategy();
        final Sequenced sequencer = newProducer(producerType, SequencerTest.BUFFER_SIZE, waitStrategy);
        sequencer.publish(sequencer.next());
        Assert.assertThat(waitStrategy.signalAllWhenBlockingCalls, CoreMatchers.is(1));
    }

    @Test
    public void shouldNotifyWaitStrategyOnPublishBatch() throws Exception {
        final DummyWaitStrategy waitStrategy = new DummyWaitStrategy();
        final Sequenced sequencer = newProducer(producerType, SequencerTest.BUFFER_SIZE, waitStrategy);
        long next = sequencer.next(4);
        sequencer.publish((next - (4 - 1)), next);
        Assert.assertThat(waitStrategy.signalAllWhenBlockingCalls, CoreMatchers.is(1));
    }

    @Test
    public void shouldWaitOnPublication() throws Exception {
        SequenceBarrier barrier = sequencer.newBarrier();
        long next = sequencer.next(10);
        long lo = next - (10 - 1);
        long mid = next - 5;
        for (long l = lo; l < mid; l++) {
            sequencer.publish(l);
        }
        Assert.assertThat(barrier.waitFor((-1)), CoreMatchers.is((mid - 1)));
        for (long l = mid; l <= next; l++) {
            sequencer.publish(l);
        }
        Assert.assertThat(barrier.waitFor((-1)), CoreMatchers.is(next));
    }

    @Test
    public void shouldTryNext() throws Exception {
        sequencer.addGatingSequences(gatingSequence);
        for (int i = 0; i < (SequencerTest.BUFFER_SIZE); i++) {
            sequencer.publish(sequencer.tryNext());
        }
        try {
            sequencer.tryNext();
            Assert.fail(("Should of thrown: " + (InsufficientCapacityException.class.getSimpleName())));
        } catch (InsufficientCapacityException e) {
            // No-op
        }
    }

    @Test
    public void shouldClaimSpecificSequence() throws Exception {
        long sequence = 14L;
        sequencer.claim(sequence);
        sequencer.publish(sequence);
        Assert.assertThat(sequencer.next(), CoreMatchers.is((sequence + 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowBulkNextLessThanZero() throws Exception {
        sequencer.next((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowBulkNextOfZero() throws Exception {
        sequencer.next(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowBulkTryNextLessThanZero() throws Exception {
        sequencer.tryNext((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowBulkTryNextOfZero() throws Exception {
        sequencer.tryNext(0);
    }
}


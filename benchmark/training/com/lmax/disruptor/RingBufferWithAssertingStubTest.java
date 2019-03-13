package com.lmax.disruptor;


import com.lmax.disruptor.support.StubEvent;
import java.util.concurrent.ThreadLocalRandom;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class RingBufferWithAssertingStubTest {
    private RingBuffer<StubEvent> ringBuffer;

    private Sequencer sequencer;

    @Test
    public void shouldDelegateNextAndPublish() {
        ringBuffer.publish(ringBuffer.next());
    }

    @Test
    public void shouldDelegateTryNextAndPublish() throws Exception {
        ringBuffer.publish(ringBuffer.tryNext());
    }

    @Test
    public void shouldDelegateNextNAndPublish() throws Exception {
        long hi = ringBuffer.next(10);
        ringBuffer.publish((hi - 9), hi);
    }

    @Test
    public void shouldDelegateTryNextNAndPublish() throws Exception {
        long hi = ringBuffer.tryNext(10);
        ringBuffer.publish((hi - 9), hi);
    }

    private static final class AssertingSequencer implements Sequencer {
        private final int size;

        private long lastBatchSize = -1;

        private long lastValue = -1;

        private AssertingSequencer(int size) {
            this.size = size;
        }

        @Override
        public int getBufferSize() {
            return size;
        }

        @Override
        public boolean hasAvailableCapacity(int requiredCapacity) {
            return requiredCapacity <= (size);
        }

        @Override
        public long remainingCapacity() {
            return size;
        }

        @Override
        public long next() {
            lastValue = ThreadLocalRandom.current().nextLong(0, 1000000);
            lastBatchSize = 1;
            return lastValue;
        }

        @Override
        public long next(int n) {
            lastValue = ThreadLocalRandom.current().nextLong(n, 1000000);
            lastBatchSize = n;
            return lastValue;
        }

        @Override
        public long tryNext() throws InsufficientCapacityException {
            return next();
        }

        @Override
        public long tryNext(int n) throws InsufficientCapacityException {
            return next(n);
        }

        @Override
        public void publish(long sequence) {
            Assert.assertThat(sequence, Is.is(lastValue));
            Assert.assertThat(lastBatchSize, Is.is(1L));
        }

        @Override
        public void publish(long lo, long hi) {
            Assert.assertThat(hi, Is.is(lastValue));
            Assert.assertThat(((hi - lo) + 1), Is.is(lastBatchSize));
        }

        @Override
        public long getCursor() {
            return lastValue;
        }

        @Override
        public void claim(long sequence) {
        }

        @Override
        public boolean isAvailable(long sequence) {
            return false;
        }

        @Override
        public void addGatingSequences(Sequence... gatingSequences) {
        }

        @Override
        public boolean removeGatingSequence(Sequence sequence) {
            return false;
        }

        @Override
        public SequenceBarrier newBarrier(Sequence... sequencesToTrack) {
            return null;
        }

        @Override
        public long getMinimumSequence() {
            return 0;
        }

        @Override
        public long getHighestPublishedSequence(long nextSequence, long availableSequence) {
            return 0;
        }

        @Override
        public <T> EventPoller<T> newPoller(DataProvider<T> provider, Sequence... gatingSequences) {
            return null;
        }
    }
}


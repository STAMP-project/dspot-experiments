package com.lmax.disruptor;


import PollState.GATING;
import PollState.IDLE;
import PollState.PROCESSING;
import java.util.ArrayList;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class EventPollerTest {
    @Test
    @SuppressWarnings("unchecked")
    public void shouldPollForEvents() throws Exception {
        final Sequence gatingSequence = new Sequence();
        final SingleProducerSequencer sequencer = new SingleProducerSequencer(16, new BusySpinWaitStrategy());
        final EventPoller.Handler<Object> handler = new EventPoller.Handler<Object>() {
            public boolean onEvent(Object event, long sequence, boolean endOfBatch) throws Exception {
                return false;
            }
        };
        final Object[] data = new Object[16];
        final DataProvider<Object> provider = new DataProvider<Object>() {
            public Object get(long sequence) {
                return data[((int) (sequence))];
            }
        };
        final EventPoller<Object> poller = sequencer.newPoller(provider, gatingSequence);
        final Object event = new Object();
        data[0] = event;
        Assert.assertThat(poller.poll(handler), Is.is(IDLE));
        // Publish Event.
        sequencer.publish(sequencer.next());
        Assert.assertThat(poller.poll(handler), Is.is(GATING));
        gatingSequence.incrementAndGet();
        Assert.assertThat(poller.poll(handler), Is.is(PROCESSING));
    }

    @Test
    public void shouldSuccessfullyPollWhenBufferIsFull() throws Exception {
        final ArrayList<byte[]> events = new ArrayList<byte[]>();
        final EventPoller.Handler<byte[]> handler = new EventPoller.Handler<byte[]>() {
            public boolean onEvent(byte[] event, long sequence, boolean endOfBatch) throws Exception {
                events.add(event);
                return !endOfBatch;
            }
        };
        EventFactory<byte[]> factory = new EventFactory<byte[]>() {
            @Override
            public byte[] newInstance() {
                return new byte[1];
            }
        };
        final RingBuffer<byte[]> ringBuffer = RingBuffer.createMultiProducer(factory, 4, new SleepingWaitStrategy());
        final EventPoller<byte[]> poller = ringBuffer.newPoller();
        ringBuffer.addGatingSequences(poller.getSequence());
        int count = 4;
        for (byte i = 1; i <= count; ++i) {
            long next = ringBuffer.next();
            ringBuffer.get(next)[0] = i;
            ringBuffer.publish(next);
        }
        // think of another thread
        poller.poll(handler);
        Assert.assertThat(events.size(), Is.is(4));
    }
}


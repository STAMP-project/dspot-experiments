package com.lmax.disruptor;


import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class SingleProducerSequencerTest {
    @Test
    public void shouldNotUpdateCursorDuringHasAvailableCapacity() throws Exception {
        SingleProducerSequencer sequencer = new SingleProducerSequencer(16, new BusySpinWaitStrategy());
        for (int i = 0; i < 32; i++) {
            long next = sequencer.next();
            Assert.assertThat(sequencer.cursor.get(), IsNot.not(next));
            sequencer.hasAvailableCapacity(13);
            Assert.assertThat(sequencer.cursor.get(), IsNot.not(next));
            sequencer.publish(next);
        }
    }
}


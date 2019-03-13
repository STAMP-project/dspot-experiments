package com.vip.vjtools.vjkit.concurrent;


import com.vip.vjtools.vjkit.concurrent.jsr166e.LongAdder;
import org.junit.Test;


public class ConcurrentsTest {
    @Test
    public void longAdder() {
        LongAdder counter = Concurrents.longAdder();
        counter.increment();
        counter.add(2);
        assertThat(counter.longValue()).isEqualTo(3L);
    }
}


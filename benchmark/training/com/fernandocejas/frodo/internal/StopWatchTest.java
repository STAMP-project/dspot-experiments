package com.fernandocejas.frodo.internal;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StopWatchTest {
    private StopWatch stopWatch;

    @Test
    public void mustResetStopWatch() {
        stopWatch.reset();
        assertThat(stopWatch.getTotalTimeMillis()).isZero();
    }

    @Test
    public void mustStartStopWatch() throws InterruptedException {
        stopWatch.start();
        Thread.sleep(10);
        stopWatch.stop();
        assertThat(stopWatch.getTotalTimeMillis()).isGreaterThan(0L);
    }
}


package brave.internal.recorder;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


// Added to declutter console: tells power mock not to mess with implicit classes we aren't testing
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.logging.*", "javax.script.*" })
@PrepareForTest(TickClock.class)
public class TickClockTest {
    @Test
    public void relativeTimestamp_incrementsAccordingToNanoTick() {
        mockStatic(System.class);
        TickClock clock = /* 1ms */
        /* 0ns */
        new TickClock(1000L, 0L);
        when(System.nanoTime()).thenReturn(1000L);// 1 microsecond = 1000 nanoseconds

        assertThat(clock.currentTimeMicroseconds()).isEqualTo(1001L);// 1ms + 1us

    }
}


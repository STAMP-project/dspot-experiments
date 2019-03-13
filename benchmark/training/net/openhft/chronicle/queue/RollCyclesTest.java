package net.openhft.chronicle.queue;


import DefaultCycleCalculator.INSTANCE;
import java.util.concurrent.atomic.AtomicLong;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.core.time.TimeProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@RequiredForClient
public class RollCyclesTest {
    private static final long NO_EPOCH_OFFSET = 0L;

    private static final long SOME_EPOCH_OFFSET = 17L * 37L;

    private static final RollCycles[] TEST_DATA = RollCycles.values();

    private final RollCycles cycle;

    private final AtomicLong clock = new AtomicLong();

    private final TimeProvider timeProvider = clock::get;

    public RollCyclesTest(final String cycleName, final RollCycles cycle) {
        this.cycle = cycle;
    }

    @Test
    public void shouldDetermineCurrentCycle() throws Exception {
        assertCycleRollTimes(RollCyclesTest.NO_EPOCH_OFFSET, RollCyclesTest.withDelta(timeProvider, RollCyclesTest.NO_EPOCH_OFFSET));
    }

    @Test
    public void shouldTakeEpochIntoAccoutWhenCalculatingCurrentCycle() throws Exception {
        assertCycleRollTimes(RollCyclesTest.SOME_EPOCH_OFFSET, RollCyclesTest.withDelta(timeProvider, RollCyclesTest.SOME_EPOCH_OFFSET));
    }

    @Test
    public void shouldHandleReasonableDateRange() throws Exception {
        final int currentCycle = INSTANCE.currentCycle(cycle, timeProvider, 0);
        // ~ 14 Jul 2017 to 18 May 2033
        for (long nowMillis = 1500000000000L; nowMillis < 2000000000000L; nowMillis += 3.0E10) {
            clock.set(nowMillis);
            long index = cycle.toIndex(currentCycle, 0);
            Assert.assertEquals(currentCycle, cycle.toCycle(index));
        }
    }
}


package net.openhft.chronicle.queue;


import RollCycles.DAILY;
import RollCycles.HOURLY;
import net.openhft.chronicle.core.time.TimeProvider;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Assert;
import org.junit.Test;

import static RollCycles.TEST_SECONDLY;


public class RollCycleDefaultingTest {
    @Test
    public void correctConfigGetsLoaded() {
        String aClass = HOURLY.getClass().getName();
        String configuredCycle = aClass + ":HOURLY";
        System.setProperty(SingleChronicleQueueBuilder.DEFAULT_ROLL_CYCLE_PROPERTY, configuredCycle);
        SingleChronicleQueueBuilder builder = SingleChronicleQueueBuilder.binary("test");
        Assert.assertEquals(HOURLY, builder.rollCycle());
    }

    @Test
    public void customDefinitionGetsLoaded() {
        String configuredCycle = RollCycleDefaultingTest.MyRollcycle.class.getName();
        System.setProperty(SingleChronicleQueueBuilder.DEFAULT_ROLL_CYCLE_PROPERTY, configuredCycle);
        SingleChronicleQueueBuilder builder = SingleChronicleQueueBuilder.binary("test");
        Assert.assertTrue(((builder.rollCycle()) instanceof RollCycleDefaultingTest.MyRollcycle));
    }

    @Test
    public void unknownClassDefaultsToDaily() {
        String configuredCycle = "foobarblah";
        System.setProperty(SingleChronicleQueueBuilder.DEFAULT_ROLL_CYCLE_PROPERTY, configuredCycle);
        SingleChronicleQueueBuilder builder = SingleChronicleQueueBuilder.binary("test");
        Assert.assertEquals(DAILY, builder.rollCycle());
    }

    @Test
    public void nonRollCycleDefaultsToDaily() {
        String configuredCycle = String.class.getName();
        System.setProperty(SingleChronicleQueueBuilder.DEFAULT_ROLL_CYCLE_PROPERTY, configuredCycle);
        SingleChronicleQueueBuilder builder = SingleChronicleQueueBuilder.binary("test");
        Assert.assertEquals(DAILY, builder.rollCycle());
    }

    public static class MyRollcycle implements RollCycle {
        private final RollCycle delegate = TEST_SECONDLY;

        @Override
        public String format() {
            return "xyz";
        }

        @Override
        public int length() {
            return delegate.length();
        }

        @Override
        public int defaultIndexCount() {
            return delegate.defaultIndexCount();
        }

        @Override
        public int defaultIndexSpacing() {
            return delegate.defaultIndexSpacing();
        }

        @Override
        public int current(TimeProvider time, long epoch) {
            return delegate.current(time, epoch);
        }

        @Override
        public long toIndex(int cycle, long sequenceNumber) {
            return delegate.toIndex(cycle, sequenceNumber);
        }

        @Override
        public long toSequenceNumber(long index) {
            return delegate.toSequenceNumber(index);
        }

        @Override
        public int toCycle(long index) {
            return delegate.toCycle(index);
        }
    }
}


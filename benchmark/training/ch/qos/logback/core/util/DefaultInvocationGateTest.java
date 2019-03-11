package ch.qos.logback.core.util;


import org.junit.Assert;
import org.junit.Test;


public class DefaultInvocationGateTest {
    @Test
    public void smoke() {
        long currentTime = 0;
        long minDelayThreshold = 4;
        long maxDelayThreshold = 8;
        DefaultInvocationGate gate = new DefaultInvocationGate(minDelayThreshold, maxDelayThreshold, currentTime);
        Assert.assertTrue(gate.isTooSoon(0));
    }

    @Test
    public void closelyRepeatedCallsShouldCauseMaskToIncrease() {
        long currentTime = 0;
        long minDelayThreshold = 4;
        long maxDelayThreshold = 8;
        DefaultInvocationGate gate = new DefaultInvocationGate(minDelayThreshold, maxDelayThreshold, currentTime);
        for (int i = 0; i < (DefaultInvocationGate.DEFAULT_MASK); i++) {
            Assert.assertTrue(gate.isTooSoon(0));
        }
        Assert.assertFalse(gate.isTooSoon(0));
        Assert.assertTrue(((gate.getMask()) > (DefaultInvocationGate.DEFAULT_MASK)));
    }

    @Test
    public void stableAtSteadyRate() {
        long currentTime = 0;
        long minDelayThreshold = DefaultInvocationGate.DEFAULT_MASK;
        long maxDelayThreshold = (DefaultInvocationGate.DEFAULT_MASK) * 2;
        DefaultInvocationGate gate = new DefaultInvocationGate(minDelayThreshold, maxDelayThreshold, currentTime);
        for (int t = 0; t < (4 * minDelayThreshold); t++) {
            gate.isTooSoon((currentTime++));
            Assert.assertEquals(DefaultInvocationGate.DEFAULT_MASK, gate.getMask());
        }
    }

    @Test
    public void intermittentCallsShouldCauseMaskToDecrease() {
        long currentTime = 0;
        long minDelayThreshold = 4;
        long maxDelayThreshold = 8;
        DefaultInvocationGate gate = new DefaultInvocationGate(minDelayThreshold, maxDelayThreshold, currentTime);
        int currentMask = DefaultInvocationGate.DEFAULT_MASK;
        currentTime += maxDelayThreshold + 1;
        Assert.assertFalse(gate.isTooSoon(currentTime));
        Assert.assertTrue(((gate.getMask()) < currentMask));
    }

    @Test
    public void maskCanDropToZeroForInfrequentInvocations() {
        long currentTime = 0;
        long minDelayThreshold = 4;
        long maxDelayThreshold = 8;
        DefaultInvocationGate gate = new DefaultInvocationGate(minDelayThreshold, maxDelayThreshold, currentTime);
        int currentMask = DefaultInvocationGate.DEFAULT_MASK;
        do {
            currentTime += maxDelayThreshold + 1;
            Assert.assertFalse(gate.isTooSoon(currentTime));
            Assert.assertTrue(((gate.getMask()) < currentMask));
            currentMask = currentMask >> (DefaultInvocationGate.MASK_DECREASE_RIGHT_SHIFT_COUNT);
        } while (currentMask > 0 );
        Assert.assertEquals(0, gate.getMask());
        Assert.assertFalse(gate.isTooSoon(currentTime));
    }
}


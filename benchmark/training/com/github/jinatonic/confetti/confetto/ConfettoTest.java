package com.github.jinatonic.confetti.confetto;


import org.junit.Assert;
import org.junit.Test;


public class ConfettoTest {
    @Test
    public void test_computeMillisToReachTarget() {
        Long time = Confetto.computeMillisToReachTarget(null, 0.0F, 0.0F);
        Assert.assertNull(time);
        time = Confetto.computeMillisToReachTarget(0.0F, 10.0F, 10.0F);
        Assert.assertEquals(0, time.longValue());
        time = Confetto.computeMillisToReachTarget(20.0F, 10.0F, 10.0F);
        Assert.assertEquals(1, time.longValue());
        time = Confetto.computeMillisToReachTarget(30.0F, 0.0F, 10.0F);
        Assert.assertEquals(3, time.longValue());
        time = Confetto.computeMillisToReachTarget(20.0F, 10.0F, 0.0F);
        Assert.assertNull(time);
    }

    @Test
    public void test_computeBound_noAcceleration() {
        // Normal velocity
        long time = Confetto.computeBound(0.0F, 0.01F, 0.0F, null, null, (-10000), 100);
        Assert.assertEquals(10000, time);
        time = Confetto.computeBound(0.0F, (-0.01F), 0.0F, null, null, (-100), 10000);
        Assert.assertEquals(10000, time);
        time = Confetto.computeBound(10.0F, 0.01F, 0.0F, null, null, (-10000), 100);
        Assert.assertEquals(9000, time);
        time = Confetto.computeBound(10.0F, (-0.01F), 0.0F, null, null, (-100), 10000);
        Assert.assertEquals(11000, time);
        // Normal velocity with non-null unreachable target velocity
        time = Confetto.computeBound(0.0F, 0.01F, 0.0F, null, 0.02F, (-10000), 100);
        Assert.assertEquals(10000, time);
        time = Confetto.computeBound(0.0F, (-0.01F), 0.0F, null, 0.02F, (-100), 10000);
        Assert.assertEquals(10000, time);
        // Normal velocity with non-null already-reached target velocity
        time = Confetto.computeBound(0.0F, 0.01F, 0.0F, 0L, (-0.01F), (-100), 10000);
        Assert.assertEquals(10000, time);
        // Normal velocity with the initial position past bound
        time = Confetto.computeBound((-100.0F), 0.01F, 0.0F, null, null, (-50), 100);
        Assert.assertEquals(20000, time);
    }

    @Test
    public void test_computeBound_withAcceleration() {
        // 100 = 0.5 * 0.01 * t * t, t = sqrt(20000) or 141
        long time = Confetto.computeBound(0.0F, 0.0F, 0.01F, null, null, (-10000), 100);
        Assert.assertEquals(141, time);
        time = Confetto.computeBound(0.0F, 0.0F, (-0.01F), null, null, (-100), 10000);
        Assert.assertEquals(141, time);
        // 100 = 10 + 0.01 * t + 0.5 * 0.01 * t * t, t 3.358
        time = Confetto.computeBound(10.0F, 0.01F, 0.01F, null, null, (-10000), 100);
        Assert.assertEquals(133, time);
        time = Confetto.computeBound((-10.0F), (-0.01F), (-0.01F), null, null, (-100), 10000);
        Assert.assertEquals(133, time);
    }

    @Test
    public void test_computeBound_withAccelerationAndTargetVelocity() {
        // 100 = 0.5 * 0.01 * 3 * 3 + 0.03 * (t - 3)
        long time = Confetto.computeBound(0.0F, 0.0F, 0.01F, 3L, 0.03F, (-10000), 100);
        Assert.assertEquals(3334, time);
        time = Confetto.computeBound(0.0F, 0.0F, (-0.01F), 3L, (-0.03F), (-100), 10000);
        Assert.assertEquals(3334, time);
        // 100 = 10 + 0.01 * 3 + 0.5 * 0.01 * 3 * 3 + 0.04 * (t - 3)
        time = Confetto.computeBound(10.0F, 0.01F, 0.01F, 3L, 0.04F, (-10000), 100);
        Assert.assertEquals(2251, time);
        time = Confetto.computeBound(10.0F, (-0.01F), (-0.01F), 3L, (-0.04F), (-100), 10000);
        Assert.assertEquals(2251, time);
    }
}


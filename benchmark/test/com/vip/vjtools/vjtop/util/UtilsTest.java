package com.vip.vjtools.vjtop.util;


import org.junit.Assert;
import org.junit.Test;


public class UtilsTest {
    @Test
    public void calcLoadInputNegativeZeroZeroOutputZero() {
        // Arrange
        final Long deltaCpuTime = -4194304L;
        final long deltaUptime = 0L;
        final long factor = 0L;
        // Act
        final double retval = Utils.calcLoad(deltaCpuTime, deltaUptime, factor);
        // Assert result
        Assert.assertEquals(0.0, retval, 0.0);
    }

    @Test
    public void calcLoadInputPositivePositiveOutputPositive() {
        // Arrange
        final long deltaCpuTime = 558348370L;
        final long deltaUptime = 1L;
        // Act
        final double retval = Utils.calcLoad(deltaCpuTime, deltaUptime);
        // Assert result
        /* 5.58348e+10 */
        Assert.assertEquals(5.5834837E10, retval, 0.0);
    }

    @Test
    public void calcLoadInputPositivePositiveZeroOutputPositiveInfinity() {
        // Arrange
        final Long deltaCpuTime = 4194304L;
        final long deltaUptime = 4611686018427387904L;
        final long factor = 0L;
        // Act
        final double retval = Utils.calcLoad(deltaCpuTime, deltaUptime, factor);
        // Assert result
        Assert.assertEquals(Double.POSITIVE_INFINITY, retval, 0.0);
    }

    @Test
    public void calcLoadInputPositiveZeroOutputZero() {
        // Arrange
        final long deltaCpuTime = 558348370L;
        final long deltaUptime = 0L;
        // Act
        final double retval = Utils.calcLoad(deltaCpuTime, deltaUptime);
        // Assert result
        Assert.assertEquals(0.0, retval, 0.0);
    }

    @Test
    public void calcLoadInputPositiveZeroZeroOutputZero() {
        // Arrange
        final Long deltaCpuTime = 4194304L;
        final long deltaUptime = 0L;
        final long factor = 0L;
        // Act
        final double retval = Utils.calcLoad(deltaCpuTime, deltaUptime, factor);
        // Assert result
        Assert.assertEquals(0.0, retval, 0.0);
    }

    @Test
    public void calcLoadInputZeroZeroOutputZero() {
        // Arrange
        final long deltaCpuTime = 0L;
        final long deltaUptime = 0L;
        // Act
        final double retval = Utils.calcLoad(deltaCpuTime, deltaUptime);
        // Assert result
        Assert.assertEquals(0.0, retval, 0.0);
    }

    @Test
    public void calcMemoryUtilizationInputNegativePositiveOutputNegative() {
        // Arrange
        final Long threadBytes = -507680768073012547L;
        final long totalBytes = 1147301170758571992L;
        // Act
        final double retval = Utils.calcMemoryUtilization(threadBytes, totalBytes);
        // Assert result
        /* -44.25 */
        Assert.assertEquals((-44.250000001075946), retval, 0.0);
    }

    @Test
    public void calcMemoryUtilizationInputPositiveZeroOutputZero() {
        // Arrange
        final Long threadBytes = 1L;
        final long totalBytes = 0L;
        // Act
        final double retval = Utils.calcMemoryUtilization(threadBytes, totalBytes);
        // Assert result
        Assert.assertEquals(0.0, retval, 0.0);
    }
}


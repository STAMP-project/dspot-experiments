package com.baeldung.java.math;


import org.junit.Assert;
import org.junit.Test;


public class MathUnitTest {
    @Test
    public void whenAbsInteger_thenReturnAbsoluteValue() {
        Assert.assertEquals(5, Math.abs((-5)));
    }

    @Test
    public void whenMaxBetweenTwoValue_thenReturnMaximum() {
        Assert.assertEquals(10, Math.max(5, 10));
    }

    @Test
    public void whenMinBetweenTwoValue_thenReturnMinimum() {
        Assert.assertEquals(5, Math.min(5, 10));
    }

    @Test
    public void whenSignumWithNegativeNumber_thenReturnMinusOne() {
        Assert.assertEquals((-1), Math.signum((-5)), 0);
    }

    @Test
    public void whenCopySignWithNegativeSign_thenReturnNegativeArgument() {
        Assert.assertEquals((-5), Math.copySign(5, (-1)), 0);
    }

    @Test
    public void whenPow_thenReturnPoweredValue() {
        Assert.assertEquals(25, Math.pow(5, 2), 0);
    }

    @Test
    public void whenSqrt_thenReturnSquareRoot() {
        Assert.assertEquals(5, Math.sqrt(25), 0);
    }

    @Test
    public void whenCbrt_thenReturnCubeRoot() {
        Assert.assertEquals(5, Math.cbrt(125), 0);
    }

    @Test
    public void whenExp_thenReturnEulerNumberRaised() {
        Assert.assertEquals(2.718, Math.exp(1), 0.1);
    }

    @Test
    public void whenExpm1_thenReturnEulerNumberMinusOne() {
        Assert.assertEquals(1.718, Math.expm1(1), 0.1);
    }

    @Test
    public void whenGetExponent_thenReturnUnbiasedExponent() {
        Assert.assertEquals(8, Math.getExponent(333.3), 0);
        Assert.assertEquals(7, Math.getExponent(222.2F), 0);
    }

    @Test
    public void whenLog_thenReturnValue() {
        Assert.assertEquals(1, Math.log(Math.E), 0);
    }

    @Test
    public void whenLog10_thenReturnValue() {
        Assert.assertEquals(1, Math.log10(10), 0);
    }

    @Test
    public void whenLog1p_thenReturnValue() {
        Assert.assertEquals(1.31, Math.log1p(Math.E), 0.1);
    }

    @Test
    public void whenSin_thenReturnValue() {
        Assert.assertEquals(1, Math.sin(((Math.PI) / 2)), 0);
    }

    @Test
    public void whenCos_thenReturnValue() {
        Assert.assertEquals(1, Math.cos(0), 0);
    }

    @Test
    public void whenTan_thenReturnValue() {
        Assert.assertEquals(1, Math.tan(((Math.PI) / 4)), 0.1);
    }

    @Test
    public void whenAsin_thenReturnValue() {
        Assert.assertEquals(((Math.PI) / 2), Math.asin(1), 0);
    }

    @Test
    public void whenAcos_thenReturnValue() {
        Assert.assertEquals(((Math.PI) / 2), Math.acos(0), 0);
    }

    @Test
    public void whenAtan_thenReturnValue() {
        Assert.assertEquals(((Math.PI) / 4), Math.atan(1), 0);
    }

    @Test
    public void whenAtan2_thenReturnValue() {
        Assert.assertEquals(((Math.PI) / 4), Math.atan2(1, 1), 0);
    }

    @Test
    public void whenToDegrees_thenReturnValue() {
        Assert.assertEquals(180, Math.toDegrees(Math.PI), 0);
    }

    @Test
    public void whenToRadians_thenReturnValue() {
        Assert.assertEquals(Math.PI, Math.toRadians(180), 0);
    }

    @Test
    public void whenCeil_thenReturnValue() {
        Assert.assertEquals(4, Math.ceil(Math.PI), 0);
    }

    @Test
    public void whenFloor_thenReturnValue() {
        Assert.assertEquals(3, Math.floor(Math.PI), 0);
    }

    @Test
    public void whenGetExponent_thenReturnValue() {
        Assert.assertEquals(8, Math.getExponent(333.3), 0);
    }

    @Test
    public void whenIEEERemainder_thenReturnValue() {
        Assert.assertEquals(1.0, Math.IEEEremainder(5, 2), 0);
    }

    @Test
    public void whenNextAfter_thenReturnValue() {
        Assert.assertEquals(1.9499999284744263, Math.nextAfter(1.95F, 1), 1.0E-7);
    }

    @Test
    public void whenNextUp_thenReturnValue() {
        Assert.assertEquals(1.9500002, Math.nextUp(1.95F), 1.0E-7);
    }

    @Test
    public void whenRint_thenReturnValue() {
        Assert.assertEquals(2.0, Math.rint(1.95F), 0.0);
    }

    @Test
    public void whenRound_thenReturnValue() {
        Assert.assertEquals(2.0, Math.round(1.95F), 0.0);
    }

    @Test
    public void whenScalb_thenReturnValue() {
        Assert.assertEquals(48, Math.scalb(3, 4), 0.0);
    }

    @Test
    public void whenHypot_thenReturnValue() {
        Assert.assertEquals(5, Math.hypot(4, 3), 0.0);
    }
}


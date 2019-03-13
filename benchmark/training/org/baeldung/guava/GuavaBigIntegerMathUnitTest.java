package org.baeldung.guava;


import com.google.common.math.BigIntegerMath;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.junit.Assert;
import org.junit.Test;


public class GuavaBigIntegerMathUnitTest {
    @Test
    public void whenPerformBinomialOnTwoIntValues_shouldReturnResult() {
        BigInteger result = BigIntegerMath.binomial(6, 3);
        Assert.assertEquals(new BigInteger("20"), result);
    }

    @Test
    public void whenProformCeilPowOfTwoBigIntegerValues_shouldReturnResult() {
        BigInteger result = ceilingPowerOfTwo(new BigInteger("20"));
        Assert.assertEquals(new BigInteger("32"), result);
    }

    @Test
    public void whenDivideTwoBigIntegerValues_shouldDivideThemAndReturnTheResultForCeilingRounding() {
        BigInteger result = BigIntegerMath.divide(new BigInteger("10"), new BigInteger("3"), RoundingMode.CEILING);
        Assert.assertEquals(new BigInteger("4"), result);
    }

    @Test
    public void whenDivideTwoBigIntegerValues_shouldDivideThemAndReturnTheResultForFloorRounding() {
        BigInteger result = BigIntegerMath.divide(new BigInteger("10"), new BigInteger("3"), RoundingMode.FLOOR);
        Assert.assertEquals(new BigInteger("3"), result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenDivideTwoBigIntegerValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        BigIntegerMath.divide(new BigInteger("10"), new BigInteger("3"), RoundingMode.UNNECESSARY);
    }

    @Test
    public void whenFactorailInteger_shouldFactorialThemAndReturnTheResultIfInIntRange() {
        BigInteger result = BigIntegerMath.factorial(5);
        Assert.assertEquals(new BigInteger("120"), result);
    }

    @Test
    public void whenFloorPowerOfInteger_shouldReturnValue() {
        BigInteger result = floorPowerOfTwo(new BigInteger("30"));
        Assert.assertEquals(new BigInteger("16"), result);
    }

    @Test
    public void whenIsPowOfInteger_shouldReturnTrueIfPowerOfTwo() {
        boolean result = BigIntegerMath.isPowerOfTwo(new BigInteger("16"));
        Assert.assertTrue(result);
    }

    @Test
    public void whenLog10BigIntegerValues_shouldLog10ThemAndReturnTheResultForCeilingRounding() {
        int result = BigIntegerMath.log10(new BigInteger("30"), RoundingMode.CEILING);
        Assert.assertEquals(2, result);
    }

    @Test
    public void whenLog10BigIntegerValues_shouldog10ThemAndReturnTheResultForFloorRounding() {
        int result = BigIntegerMath.log10(new BigInteger("30"), RoundingMode.FLOOR);
        Assert.assertEquals(1, result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenLog10BigIntegerValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        BigIntegerMath.log10(new BigInteger("30"), RoundingMode.UNNECESSARY);
    }

    @Test
    public void whenLog2BigIntegerValues_shouldLog2ThemAndReturnTheResultForCeilingRounding() {
        int result = BigIntegerMath.log2(new BigInteger("30"), RoundingMode.CEILING);
        Assert.assertEquals(5, result);
    }

    @Test
    public void whenLog2BigIntegerValues_shouldog2ThemAndReturnTheResultForFloorRounding() {
        int result = BigIntegerMath.log2(new BigInteger("30"), RoundingMode.FLOOR);
        Assert.assertEquals(4, result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenLog2BigIntegerValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        BigIntegerMath.log2(new BigInteger("30"), RoundingMode.UNNECESSARY);
    }

    @Test
    public void whenSqrtBigIntegerValues_shouldSqrtThemAndReturnTheResultForCeilingRounding() {
        BigInteger result = BigIntegerMath.sqrt(new BigInteger("30"), RoundingMode.CEILING);
        Assert.assertEquals(new BigInteger("6"), result);
    }

    @Test
    public void whenSqrtBigIntegerValues_shouldSqrtThemAndReturnTheResultForFloorRounding() {
        BigInteger result = BigIntegerMath.sqrt(new BigInteger("30"), RoundingMode.FLOOR);
        Assert.assertEquals(new BigInteger("5"), result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenSqrtBigIntegerValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        BigIntegerMath.sqrt(new BigInteger("30"), RoundingMode.UNNECESSARY);
    }
}


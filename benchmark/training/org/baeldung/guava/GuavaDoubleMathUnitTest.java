package org.baeldung.guava;


import com.google.common.math.DoubleMath;
import java.math.RoundingMode;
import org.junit.Assert;
import org.junit.Test;


public class GuavaDoubleMathUnitTest {
    @Test
    public void whenFactorailDouble_shouldFactorialThemAndReturnTheResultIfInDoubleRange() {
        double result = DoubleMath.factorial(5);
        Assert.assertEquals(120, result, 0);
    }

    @Test
    public void whenFactorailDouble_shouldFactorialThemAndReturnDoubkeInfIfNotInDoubletRange() {
        double result = DoubleMath.factorial(Integer.MAX_VALUE);
        Assert.assertEquals(Double.POSITIVE_INFINITY, result, 0);
    }

    @Test
    public void whenFuzzyCompareDouble_shouldReturnZeroIfInRange() {
        int result = DoubleMath.fuzzyCompare(4, 4.05, 0.6);
        Assert.assertEquals(0, result);
    }

    @Test
    public void whenFuzzyCompareDouble_shouldReturnNonZeroIfNotInRange() {
        int result = DoubleMath.fuzzyCompare(4, 5, 0.1);
        Assert.assertEquals((-1), result);
    }

    @Test
    public void whenFuzzyEqualDouble_shouldReturnZeroIfInRange() {
        boolean result = DoubleMath.fuzzyEquals(4, 4.05, 0.6);
        Assert.assertTrue(result);
    }

    @Test
    public void whenFuzzyEqualDouble_shouldReturnNonZeroIfNotInRange() {
        boolean result = DoubleMath.fuzzyEquals(4, 5, 0.1);
        Assert.assertFalse(result);
    }

    @Test
    public void whenMathematicalIntDouble_shouldReturnTrueIfInRange() {
        boolean result = DoubleMath.isMathematicalInteger(5);
        Assert.assertTrue(result);
    }

    @Test
    public void whenMathematicalIntDouble_shouldReturnFalseIfNotInRange() {
        boolean result = DoubleMath.isMathematicalInteger(5.2);
        Assert.assertFalse(result);
    }

    @Test
    public void whenIsPowerOfTwoDouble_shouldReturnTrueIfIsPowerOfTwo() {
        boolean result = DoubleMath.isMathematicalInteger(4);
        Assert.assertTrue(result);
    }

    @Test
    public void whenIsPowerOfTwoDouble_shouldReturnFalseIsNotPowerOfTwoe() {
        boolean result = DoubleMath.isMathematicalInteger(5.2);
        Assert.assertFalse(result);
    }

    @Test
    public void whenLog2Double_shouldReturnResult() {
        double result = DoubleMath.log2(4);
        Assert.assertEquals(2, result, 0);
    }

    @Test
    public void whenLog2DoubleValues_shouldLog2ThemAndReturnTheResultForCeilingRounding() {
        int result = DoubleMath.log2(30, RoundingMode.CEILING);
        Assert.assertEquals(5, result);
    }

    @Test
    public void whenLog2DoubleValues_shouldog2ThemAndReturnTheResultForFloorRounding() {
        int result = DoubleMath.log2(30, RoundingMode.FLOOR);
        Assert.assertEquals(4, result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenLog2DoubleValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        DoubleMath.log2(30, RoundingMode.UNNECESSARY);
    }
}


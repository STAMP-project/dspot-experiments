package org.baeldung.guava;


import com.google.common.math.IntMath;
import java.math.RoundingMode;
import org.junit.Assert;
import org.junit.Test;


public class GuavaIntMathUnitTest {
    @Test
    public void whenPerformBinomialOnTwoIntegerValues_shouldReturnResultIfUnderInt() {
        int result = IntMath.binomial(6, 3);
        Assert.assertEquals(20, result);
    }

    @Test
    public void whenPerformBinomialOnTwoIntegerValues_shouldReturnIntMaxIfUnderInt() {
        int result = IntMath.binomial(Integer.MAX_VALUE, 3);
        Assert.assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void whenProformCeilPowOfTwoIntegerValues_shouldReturnResult() {
        int result = ceilingPowerOfTwo(20);
        Assert.assertEquals(32, result);
    }

    @Test
    public void whenCheckedAddTwoIntegerValues_shouldAddThemAndReturnTheSumIfNotOverflow() {
        int result = IntMath.checkedAdd(1, 2);
        Assert.assertEquals(3, result);
    }

    @Test(expected = ArithmeticException.class)
    public void gwhenCheckedAddTwoIntegerValues_shouldThrowArithmeticExceptionIfOverflow() {
        IntMath.checkedAdd(Integer.MAX_VALUE, 100);
    }

    @Test
    public void whenCheckedMultiplyTwoIntegerValues_shouldMultiplyThemAndReturnTheResultIfNotOverflow() {
        int result = IntMath.checkedMultiply(1, 2);
        Assert.assertEquals(2, result);
    }

    @Test(expected = ArithmeticException.class)
    public void gwhenCheckedMultiplyTwoIntegerValues_shouldThrowArithmeticExceptionIfOverflow() {
        IntMath.checkedMultiply(Integer.MAX_VALUE, 100);
    }

    @Test
    public void whenCheckedPowTwoIntegerValues_shouldPowThemAndReturnTheResultIfNotOverflow() {
        int result = IntMath.checkedPow(2, 3);
        Assert.assertEquals(8, result);
    }

    @Test(expected = ArithmeticException.class)
    public void gwhenCheckedPowTwoIntegerValues_shouldThrowArithmeticExceptionIfOverflow() {
        IntMath.checkedPow(Integer.MAX_VALUE, 100);
    }

    @Test
    public void whenCheckedSubstractTwoIntegerValues_shouldSubstractThemAndReturnTheResultIfNotOverflow() {
        int result = IntMath.checkedSubtract(4, 1);
        Assert.assertEquals(3, result);
    }

    @Test(expected = ArithmeticException.class)
    public void gwhenCheckedSubstractTwoIntegerValues_shouldThrowArithmeticExceptionIfOverflow() {
        IntMath.checkedSubtract(Integer.MAX_VALUE, (-100));
    }

    @Test
    public void whenDivideTwoIntegerValues_shouldDivideThemAndReturnTheResultForCeilingRounding() {
        int result = IntMath.divide(10, 3, RoundingMode.CEILING);
        Assert.assertEquals(4, result);
    }

    @Test
    public void whenDivideTwoIntegerValues_shouldDivideThemAndReturnTheResultForFloorRounding() {
        int result = IntMath.divide(10, 3, RoundingMode.FLOOR);
        Assert.assertEquals(3, result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenDivideTwoIntegerValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        IntMath.divide(10, 3, RoundingMode.UNNECESSARY);
    }

    @Test
    public void whenFactorailInteger_shouldFactorialThemAndReturnTheResultIfInIntRange() {
        int result = IntMath.factorial(5);
        Assert.assertEquals(120, result);
    }

    @Test
    public void whenFactorailInteger_shouldFactorialThemAndReturnIntMaxIfNotInIntRange() {
        int result = IntMath.factorial(Integer.MAX_VALUE);
        Assert.assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void whenFloorPowerOfInteger_shouldReturnValue() {
        int result = floorPowerOfTwo(30);
        Assert.assertEquals(16, result);
    }

    @Test
    public void whenGcdOfTwoIntegers_shouldReturnValue() {
        int result = IntMath.gcd(30, 40);
        Assert.assertEquals(10, result);
    }

    @Test
    public void whenIsPowOfInteger_shouldReturnTrueIfPowerOfTwo() {
        boolean result = IntMath.isPowerOfTwo(16);
        Assert.assertTrue(result);
    }

    @Test
    public void whenIsPowOfInteger_shouldReturnFalseeIfNotPowerOfTwo() {
        boolean result = IntMath.isPowerOfTwo(20);
        Assert.assertFalse(result);
    }

    @Test
    public void whenIsPrineOfInteger_shouldReturnFalseeIfNotPrime() {
        boolean result = isPrime(20);
        Assert.assertFalse(result);
    }

    @Test
    public void whenLog10IntegerValues_shouldLog10ThemAndReturnTheResultForCeilingRounding() {
        int result = IntMath.log10(30, RoundingMode.CEILING);
        Assert.assertEquals(2, result);
    }

    @Test
    public void whenLog10IntegerValues_shouldog10ThemAndReturnTheResultForFloorRounding() {
        int result = IntMath.log10(30, RoundingMode.FLOOR);
        Assert.assertEquals(1, result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenLog10IntegerValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        IntMath.log10(30, RoundingMode.UNNECESSARY);
    }

    @Test
    public void whenLog2IntegerValues_shouldLog2ThemAndReturnTheResultForCeilingRounding() {
        int result = IntMath.log2(30, RoundingMode.CEILING);
        Assert.assertEquals(5, result);
    }

    @Test
    public void whenLog2IntegerValues_shouldog2ThemAndReturnTheResultForFloorRounding() {
        int result = IntMath.log2(30, RoundingMode.FLOOR);
        Assert.assertEquals(4, result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenLog2IntegerValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        IntMath.log2(30, RoundingMode.UNNECESSARY);
    }

    @Test
    public void whenMeanTwoIntegerValues_shouldMeanThemAndReturnTheResult() {
        int result = IntMath.mean(30, 20);
        Assert.assertEquals(25, result);
    }

    @Test
    public void whenModTwoIntegerValues_shouldModThemAndReturnTheResult() {
        int result = IntMath.mod(30, 4);
        Assert.assertEquals(2, result);
    }

    @Test
    public void whenPowTwoIntegerValues_shouldPowThemAndReturnTheResult() {
        int result = IntMath.pow(6, 4);
        Assert.assertEquals(1296, result);
    }

    @Test
    public void whenSaturatedAddTwoIntegerValues_shouldAddThemAndReturnTheResult() {
        int result = saturatedAdd(6, 4);
        Assert.assertEquals(10, result);
    }

    @Test
    public void whenSaturatedAddTwoIntegerValues_shouldAddThemAndReturnIntMaxIfOverflow() {
        int result = saturatedAdd(Integer.MAX_VALUE, 1000);
        Assert.assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void whenSaturatedAddTwoIntegerValues_shouldAddThemAndReturnIntMinIfUnderflow() {
        int result = saturatedAdd(Integer.MIN_VALUE, (-1000));
        Assert.assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    public void whenSaturatedMultiplyTwoIntegerValues_shouldMultiplyThemAndReturnTheResult() {
        int result = saturatedMultiply(6, 4);
        Assert.assertEquals(24, result);
    }

    @Test
    public void whenSaturatedMultiplyTwoIntegerValues_shouldMultiplyThemAndReturnIntMaxIfOverflow() {
        int result = saturatedMultiply(Integer.MAX_VALUE, 1000);
        Assert.assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void whenSaturatedMultiplyTwoIntegerValues_shouldMultiplyThemAndReturnIntMinIfUnderflow() {
        int result = saturatedMultiply(Integer.MIN_VALUE, 1000);
        Assert.assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    public void whenSaturatedPowTwoIntegerValues_shouldPowThemAndReturnTheResult() {
        int result = saturatedPow(6, 2);
        Assert.assertEquals(36, result);
    }

    @Test
    public void whenSaturatedPowTwoIntegerValues_shouldPowThemAndReturnIntMaxIfOverflow() {
        int result = saturatedPow(Integer.MAX_VALUE, 2);
        Assert.assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void whenSaturatedPowTwoIntegerValues_shouldPowThemAndReturnIntMinIfUnderflow() {
        int result = saturatedPow(Integer.MIN_VALUE, 3);
        Assert.assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    public void whenSaturatedSubstractTwoIntegerValues_shouldSubstractThemAndReturnTheResult() {
        int result = saturatedSubtract(6, 2);
        Assert.assertEquals(4, result);
    }

    @Test
    public void whenSaturatedSubstractTwoIntegerValues_shouldSubstractwThemAndReturnIntMaxIfOverflow() {
        int result = saturatedSubtract(Integer.MAX_VALUE, (-2));
        Assert.assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void whenSaturatedSubstractTwoIntegerValues_shouldSubstractThemAndReturnIntMinIfUnderflow() {
        int result = saturatedSubtract(Integer.MIN_VALUE, 3);
        Assert.assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    public void whenSqrtIntegerValues_shouldSqrtThemAndReturnTheResultForCeilingRounding() {
        int result = IntMath.sqrt(30, RoundingMode.CEILING);
        Assert.assertEquals(6, result);
    }

    @Test
    public void whenSqrtIntegerValues_shouldSqrtThemAndReturnTheResultForFloorRounding() {
        int result = IntMath.sqrt(30, RoundingMode.FLOOR);
        Assert.assertEquals(5, result);
    }

    @Test(expected = ArithmeticException.class)
    public void whenSqrtIntegerValues_shouldThrowArithmeticExceptionIfRoundingNotDefinedButNecessary() {
        IntMath.sqrt(30, RoundingMode.UNNECESSARY);
    }
}


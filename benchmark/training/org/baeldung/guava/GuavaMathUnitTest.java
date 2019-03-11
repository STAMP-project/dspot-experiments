package org.baeldung.guava;


import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class GuavaMathUnitTest {
    @Test(expected = ArithmeticException.class)
    public void whenSumOverflow_thenThrowException() {
        IntMath.checkedAdd(Integer.MAX_VALUE, 1);
    }

    @Test(expected = ArithmeticException.class)
    public void whenSumUnderflow_thenThrowException() {
        IntMath.checkedAdd(Integer.MIN_VALUE, (-1));
    }

    @Test
    public void should_calculate_sum() {
        int result = IntMath.checkedAdd(2, 1);
        Assert.assertThat(result, IsEqual.equalTo(3));
    }

    @Test
    public void whenSumOverflow_thenReturnMaxInteger() {
        int result = saturatedAdd(Integer.MAX_VALUE, 100);
        Assert.assertThat(result, IsEqual.equalTo(Integer.MAX_VALUE));
    }

    @Test
    public void whenSumUnderflow_thenReturnMinInteger() {
        int result = saturatedAdd(Integer.MIN_VALUE, (-100));
        Assert.assertThat(result, IsEqual.equalTo(Integer.MIN_VALUE));
    }

    @Test(expected = ArithmeticException.class)
    public void whenDifferenceOverflow_thenThrowException() {
        IntMath.checkedSubtract(Integer.MAX_VALUE, (-1));
    }

    @Test(expected = ArithmeticException.class)
    public void whenDifferenceUnderflow_thenThrowException() {
        IntMath.checkedSubtract(Integer.MIN_VALUE, 1);
    }

    @Test
    public void should_calculate_difference() {
        int result = IntMath.checkedSubtract(200, 100);
        Assert.assertThat(result, IsEqual.equalTo(100));
    }

    @Test
    public void whenDifferenceOverflow_thenReturnMaxInteger() {
        int result = saturatedSubtract(Integer.MAX_VALUE, (-1));
        Assert.assertThat(result, IsEqual.equalTo(Integer.MAX_VALUE));
    }

    @Test
    public void whenDifferenceUnderflow_thenReturnMinInteger() {
        int result = saturatedSubtract(Integer.MIN_VALUE, 1);
        Assert.assertThat(result, IsEqual.equalTo(Integer.MIN_VALUE));
    }

    @Test(expected = ArithmeticException.class)
    public void whenProductOverflow_thenThrowException() {
        IntMath.checkedMultiply(Integer.MAX_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void whenProductUnderflow_thenThrowException() {
        IntMath.checkedMultiply(Integer.MIN_VALUE, 2);
    }

    @Test
    public void should_calculate_product() {
        int result = IntMath.checkedMultiply(21, 3);
        Assert.assertThat(result, IsEqual.equalTo(63));
    }

    @Test
    public void whenProductOverflow_thenReturnMaxInteger() {
        int result = saturatedMultiply(Integer.MAX_VALUE, 2);
        Assert.assertThat(result, IsEqual.equalTo(Integer.MAX_VALUE));
    }

    @Test
    public void whenProductUnderflow_thenReturnMinInteger() {
        int result = saturatedMultiply(Integer.MIN_VALUE, 2);
        Assert.assertThat(result, IsEqual.equalTo(Integer.MIN_VALUE));
    }

    @Test(expected = ArithmeticException.class)
    public void whenPowerOverflow_thenThrowException() {
        IntMath.checkedPow(Integer.MAX_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void whenPowerUnderflow_thenThrowException() {
        IntMath.checkedPow(Integer.MIN_VALUE, 3);
    }

    @Test
    public void should_calculate_power() {
        int result = saturatedPow(3, 3);
        Assert.assertThat(result, IsEqual.equalTo(27));
    }

    @Test
    public void whenPowerOverflow_thenReturnMaxInteger() {
        int result = saturatedPow(Integer.MAX_VALUE, 2);
        Assert.assertThat(result, IsEqual.equalTo(Integer.MAX_VALUE));
    }

    @Test
    public void whenPowerUnderflow_thenReturnMinInteger() {
        int result = saturatedPow(Integer.MIN_VALUE, 3);
        Assert.assertThat(result, IsEqual.equalTo(Integer.MIN_VALUE));
    }

    @Test
    public void should_round_divide_result() {
        int result1 = IntMath.divide(3, 2, RoundingMode.DOWN);
        Assert.assertThat(result1, IsEqual.equalTo(1));
        int result2 = IntMath.divide(3, 2, RoundingMode.UP);
        Assert.assertThat(result2, IsEqual.equalTo(2));
    }

    @Test
    public void should_round_log2_result() {
        int result1 = IntMath.log2(5, RoundingMode.FLOOR);
        Assert.assertThat(result1, IsEqual.equalTo(2));
        int result2 = IntMath.log2(5, RoundingMode.CEILING);
        Assert.assertThat(result2, IsEqual.equalTo(3));
    }

    @Test
    public void should_round_log10_result() {
        int result = IntMath.log10(11, RoundingMode.HALF_UP);
        Assert.assertThat(result, IsEqual.equalTo(1));
    }

    @Test
    public void should_round_sqrt_result() {
        int result = IntMath.sqrt(4, RoundingMode.UNNECESSARY);
        Assert.assertThat(result, IsEqual.equalTo(2));
    }

    @Test(expected = ArithmeticException.class)
    public void whenNeedRounding_thenThrowException() {
        IntMath.sqrt(5, RoundingMode.UNNECESSARY);
    }

    @Test
    public void should_calculate_gcd() {
        int result = IntMath.gcd(15, 20);
        Assert.assertThat(result, IsEqual.equalTo(5));
    }

    @Test
    public void should_calculate_mod() {
        int result = IntMath.mod(8, 3);
        Assert.assertThat(result, IsEqual.equalTo(2));
    }

    @Test
    public void should_test_if_is_power_of_two() {
        boolean result1 = IntMath.isPowerOfTwo(8);
        Assert.assertTrue(result1);
        boolean result2 = IntMath.isPowerOfTwo(9);
        Assert.assertFalse(result2);
    }

    @Test
    public void should_calculate_factorial() {
        int result = IntMath.factorial(4);
        Assert.assertThat(result, IsEqual.equalTo(24));
    }

    @Test
    public void should_calculate_binomial() {
        int result = IntMath.binomial(7, 3);
        Assert.assertThat(result, IsEqual.equalTo(35));
    }

    @Test
    public void should_detect_integer() {
        boolean result1 = DoubleMath.isMathematicalInteger(2.0);
        Assert.assertThat(result1, IsEqual.equalTo(true));
        boolean result2 = DoubleMath.isMathematicalInteger(2.1);
        Assert.assertThat(result2, IsEqual.equalTo(false));
    }

    @Test
    public void should_round_to_integer_types() {
        int result3 = DoubleMath.roundToInt(2.5, RoundingMode.DOWN);
        Assert.assertThat(result3, IsEqual.equalTo(2));
        long result4 = DoubleMath.roundToLong(2.5, RoundingMode.HALF_UP);
        Assert.assertThat(result4, IsEqual.equalTo(3L));
        BigInteger result5 = DoubleMath.roundToBigInteger(2.5, RoundingMode.UP);
        Assert.assertThat(result5, IsEqual.equalTo(new BigInteger("3")));
    }

    @Test
    public void should_calculate_log_2() {
        int result6 = DoubleMath.log2(10, RoundingMode.UP);
        Assert.assertThat(result6, IsEqual.equalTo(4));
    }
}


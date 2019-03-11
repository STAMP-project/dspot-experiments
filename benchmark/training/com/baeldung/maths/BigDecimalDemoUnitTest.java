package com.baeldung.maths;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BigDecimalDemoUnitTest {
    @Test
    public void whenBigDecimalCreated_thenValueMatches() {
        BigDecimal bdFromString = new BigDecimal("0.1");
        BigDecimal bdFromCharArray = new BigDecimal(new char[]{ '3', '.', '1', '6', '1', '5' });
        BigDecimal bdlFromInt = new BigDecimal(42);
        BigDecimal bdFromLong = new BigDecimal(123412345678901L);
        BigInteger bigInteger = BigInteger.probablePrime(100, new Random());
        BigDecimal bdFromBigInteger = new BigDecimal(bigInteger);
        Assertions.assertEquals("0.1", bdFromString.toString());
        Assertions.assertEquals("3.1615", bdFromCharArray.toString());
        Assertions.assertEquals("42", bdlFromInt.toString());
        Assertions.assertEquals("123412345678901", bdFromLong.toString());
        Assertions.assertEquals(bigInteger.toString(), bdFromBigInteger.toString());
    }

    @Test
    public void whenBigDecimalCreatedFromDouble_thenValueMayNotMatch() {
        BigDecimal bdFromDouble = new BigDecimal(0.1);
        Assert.assertNotEquals("0.1", bdFromDouble.toString());
    }

    @Test
    public void whenBigDecimalCreatedUsingValueOf_thenValueMatches() {
        BigDecimal bdFromLong1 = BigDecimal.valueOf(123412345678901L);
        BigDecimal bdFromLong2 = BigDecimal.valueOf(123412345678901L, 2);
        BigDecimal bdFromDouble = BigDecimal.valueOf(0.1);
        Assertions.assertEquals("123412345678901", bdFromLong1.toString());
        Assertions.assertEquals("1234123456789.01", bdFromLong2.toString());
        Assertions.assertEquals("0.1", bdFromDouble.toString());
    }

    @Test
    public void whenEqualsCalled_thenSizeAndScaleMatched() {
        BigDecimal bd1 = new BigDecimal("1.0");
        BigDecimal bd2 = new BigDecimal("1.00");
        Assert.assertFalse(bd1.equals(bd2));
    }

    @Test
    public void whenComparingBigDecimals_thenExpectedResult() {
        BigDecimal bd1 = new BigDecimal("1.0");
        BigDecimal bd2 = new BigDecimal("1.00");
        BigDecimal bd3 = new BigDecimal("2.0");
        Assert.assertTrue(((bd1.compareTo(bd3)) < 0));
        Assert.assertTrue(((bd3.compareTo(bd1)) > 0));
        Assert.assertTrue(((bd1.compareTo(bd2)) == 0));
        Assert.assertTrue(((bd1.compareTo(bd3)) <= 0));
        Assert.assertTrue(((bd1.compareTo(bd2)) >= 0));
        Assert.assertTrue(((bd1.compareTo(bd3)) != 0));
    }

    @Test
    public void whenPerformingArithmetic_thenExpectedResult() {
        BigDecimal bd1 = new BigDecimal("4.0");
        BigDecimal bd2 = new BigDecimal("2.0");
        BigDecimal sum = bd1.add(bd2);
        BigDecimal difference = bd1.subtract(bd2);
        BigDecimal quotient = bd1.divide(bd2);
        BigDecimal product = bd1.multiply(bd2);
        Assert.assertTrue(((sum.compareTo(new BigDecimal("6.0"))) == 0));
        Assert.assertTrue(((difference.compareTo(new BigDecimal("2.0"))) == 0));
        Assert.assertTrue(((quotient.compareTo(new BigDecimal("2.0"))) == 0));
        Assert.assertTrue(((product.compareTo(new BigDecimal("8.0"))) == 0));
    }

    @Test
    public void whenGettingAttributes_thenExpectedResult() {
        BigDecimal bd = new BigDecimal("-12345.6789");
        Assertions.assertEquals(9, bd.precision());
        Assertions.assertEquals(4, bd.scale());
        Assertions.assertEquals((-1), bd.signum());
    }

    @Test
    public void whenRoundingDecimal_thenExpectedResult() {
        BigDecimal bd = new BigDecimal("2.5");
        // Round to 1 digit using HALF_EVEN
        BigDecimal rounded = bd.round(new MathContext(1, RoundingMode.HALF_EVEN));
        Assertions.assertEquals("2", rounded.toString());
    }

    @Test
    public void givenPurchaseTxn_whenCalculatingTotalAmount_thenExpectedResult() {
        BigDecimal quantity = new BigDecimal("4.5");
        BigDecimal unitPrice = new BigDecimal("2.69");
        BigDecimal discountRate = new BigDecimal("0.10");
        BigDecimal taxRate = new BigDecimal("0.0725");
        BigDecimal amountToBePaid = BigDecimalDemo.calculateTotalAmount(quantity, unitPrice, discountRate, taxRate);
        Assertions.assertEquals("11.68", amountToBePaid.toString());
    }
}


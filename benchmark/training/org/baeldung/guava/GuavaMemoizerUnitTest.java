package org.baeldung.guava;


import com.google.common.base.Suppliers;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.baeldung.guava.memoizer.Factorial;
import org.baeldung.guava.memoizer.FibonacciSequence;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class GuavaMemoizerUnitTest {
    @Test
    public void givenInteger_whenGetFibonacciNumber_thenShouldCalculateFibonacciNumber() {
        // given
        int n = 95;
        // when
        BigInteger fibonacciNumber = FibonacciSequence.getFibonacciNumber(n);
        // then
        BigInteger expectedFibonacciNumber = new BigInteger("31940434634990099905");
        Assert.assertThat(fibonacciNumber, Is.is(IsEqual.equalTo(expectedFibonacciNumber)));
    }

    @Test
    public void givenInteger_whenGetFactorial_thenShouldCalculateFactorial() {
        // given
        int n = 95;
        // when
        BigInteger factorial = Factorial.getFactorial(n);
        // then
        BigInteger expectedFactorial = new BigInteger("10329978488239059262599702099394727095397746340117372869212250571234293987594703124871765375385424468563282236864226607350415360000000000000000000000");
        Assert.assertThat(factorial, Is.is(IsEqual.equalTo(expectedFactorial)));
    }

    @Test
    public void givenMemoizedSupplier_whenGet_thenSubsequentGetsAreFast() {
        // given
        Supplier<BigInteger> memoizedSupplier;
        memoizedSupplier = Suppliers.memoize(CostlySupplier::generateBigNumber);
        // when
        BigInteger expectedValue = new BigInteger("12345");
        assertSupplierGetExecutionResultAndDuration(memoizedSupplier, expectedValue, 2000.0);
        // then
        assertSupplierGetExecutionResultAndDuration(memoizedSupplier, expectedValue, 0.0);
        assertSupplierGetExecutionResultAndDuration(memoizedSupplier, expectedValue, 0.0);
    }

    @Test
    public void givenMemoizedSupplierWithExpiration_whenGet_thenSubsequentGetsBeforeExpiredAreFast() throws InterruptedException {
        // given
        Supplier<BigInteger> memoizedSupplier;
        memoizedSupplier = Suppliers.memoizeWithExpiration(CostlySupplier::generateBigNumber, 3, TimeUnit.SECONDS);
        // when
        BigInteger expectedValue = new BigInteger("12345");
        assertSupplierGetExecutionResultAndDuration(memoizedSupplier, expectedValue, 2000.0);
        // then
        assertSupplierGetExecutionResultAndDuration(memoizedSupplier, expectedValue, 0.0);
        // add one more second until memoized Supplier is evicted from memory
        TimeUnit.SECONDS.sleep(3);
        assertSupplierGetExecutionResultAndDuration(memoizedSupplier, expectedValue, 2000.0);
    }
}


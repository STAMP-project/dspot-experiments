package com.baeldung.unsafe;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import sun.misc.Unsafe;


public class UnsafeUnitTest {
    private Unsafe unsafe;

    @Test
    public void givenClass_whenInitializeIt_thenShouldHaveDifferentStateWhenUseUnsafe() throws IllegalAccessException, InstantiationException {
        // when
        UnsafeUnitTest.InitializationOrdering o1 = new UnsafeUnitTest.InitializationOrdering();
        TestCase.assertEquals(o1.getA(), 1);
        // when
        UnsafeUnitTest.InitializationOrdering o3 = ((UnsafeUnitTest.InitializationOrdering) (unsafe.allocateInstance(UnsafeUnitTest.InitializationOrdering.class)));
        TestCase.assertEquals(o3.getA(), 0);
    }

    @Test
    public void givenPrivateMethod_whenUsingUnsafe_thenCanModifyPrivateField() throws NoSuchFieldException {
        // given
        UnsafeUnitTest.SecretHolder secretHolder = new UnsafeUnitTest.SecretHolder();
        // when
        Field f = secretHolder.getClass().getDeclaredField("SECRET_VALUE");
        unsafe.putInt(secretHolder, unsafe.objectFieldOffset(f), 1);
        // then
        Assert.assertTrue(secretHolder.secretIsDisclosed());
    }

    @Test(expected = IOException.class)
    public void givenUnsafeThrowException_whenThrowCheckedException_thenNotNeedToCatchIt() {
        unsafe.throwException(new IOException());
    }

    @Test
    public void givenUnsafeCompareAndSwap_whenUseIt_thenCounterYildCorrectLockFreeResults() throws Exception {
        // given
        int NUM_OF_THREADS = 1000;
        int NUM_OF_INCREMENTS = 10000;
        ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
        CASCounter casCounter = new CASCounter();
        // when
        IntStream.rangeClosed(0, (NUM_OF_THREADS - 1)).forEach(( i) -> service.submit(() -> IntStream.rangeClosed(0, (NUM_OF_INCREMENTS - 1)).forEach(( j) -> casCounter.increment())));
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
        // then
        TestCase.assertEquals((NUM_OF_INCREMENTS * NUM_OF_THREADS), casCounter.getCounter());
    }

    class InitializationOrdering {
        private long a;

        public InitializationOrdering() {
            this.a = 1;
        }

        public long getA() {
            return this.a;
        }
    }

    class SecretHolder {
        private int SECRET_VALUE = 0;

        public boolean secretIsDisclosed() {
            return (SECRET_VALUE) == 1;
        }
    }
}


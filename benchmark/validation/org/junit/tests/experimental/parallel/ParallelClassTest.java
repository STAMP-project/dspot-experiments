package org.junit.tests.experimental.parallel;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


public class ParallelClassTest {
    private static final long TIMEOUT = 15;

    private static volatile Thread fExample1One = null;

    private static volatile Thread fExample1Two = null;

    private static volatile Thread fExample2One = null;

    private static volatile Thread fExample2Two = null;

    private static volatile CountDownLatch fSynchronizer;

    public static class Example1 {
        @Test
        public void one() throws InterruptedException {
            ParallelClassTest.fSynchronizer.countDown();
            Assert.assertTrue(ParallelClassTest.fSynchronizer.await(ParallelClassTest.TIMEOUT, TimeUnit.SECONDS));
            ParallelClassTest.fExample1One = Thread.currentThread();
        }

        @Test
        public void two() throws InterruptedException {
            ParallelClassTest.fSynchronizer.countDown();
            Assert.assertTrue(ParallelClassTest.fSynchronizer.await(ParallelClassTest.TIMEOUT, TimeUnit.SECONDS));
            ParallelClassTest.fExample1Two = Thread.currentThread();
        }
    }

    public static class Example2 {
        @Test
        public void one() throws InterruptedException {
            ParallelClassTest.fSynchronizer.countDown();
            Assert.assertTrue(ParallelClassTest.fSynchronizer.await(ParallelClassTest.TIMEOUT, TimeUnit.SECONDS));
            ParallelClassTest.fExample2One = Thread.currentThread();
        }

        @Test
        public void two() throws InterruptedException {
            ParallelClassTest.fSynchronizer.countDown();
            Assert.assertTrue(ParallelClassTest.fSynchronizer.await(ParallelClassTest.TIMEOUT, TimeUnit.SECONDS));
            ParallelClassTest.fExample2Two = Thread.currentThread();
        }
    }

    @Test
    public void testsRunInParallel() {
        Result result = JUnitCore.runClasses(ParallelComputer.classes(), ParallelClassTest.Example1.class, ParallelClassTest.Example2.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertNotNull(ParallelClassTest.fExample1One);
        Assert.assertNotNull(ParallelClassTest.fExample1Two);
        Assert.assertNotNull(ParallelClassTest.fExample2One);
        Assert.assertNotNull(ParallelClassTest.fExample2Two);
        Assert.assertThat(ParallelClassTest.fExample1One, Is.is(ParallelClassTest.fExample1Two));
        Assert.assertThat(ParallelClassTest.fExample2One, Is.is(ParallelClassTest.fExample2Two));
        Assert.assertThat(ParallelClassTest.fExample1One, Is.is(IsNot.not(ParallelClassTest.fExample2One)));
    }
}


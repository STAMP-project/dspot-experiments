package org.junit.tests.experimental.parallel;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


public class ParallelMethodTest {
    private static final long TIMEOUT = 15;

    private static volatile Thread fOne = null;

    private static volatile Thread fTwo = null;

    public static class Example {
        private static volatile CountDownLatch fSynchronizer;

        @BeforeClass
        public static void init() {
            ParallelMethodTest.Example.fSynchronizer = new CountDownLatch(2);
        }

        @Test
        public void one() throws InterruptedException {
            ParallelMethodTest.Example.fSynchronizer.countDown();
            Assert.assertTrue(ParallelMethodTest.Example.fSynchronizer.await(ParallelMethodTest.TIMEOUT, TimeUnit.SECONDS));
            ParallelMethodTest.fOne = Thread.currentThread();
        }

        @Test
        public void two() throws InterruptedException {
            ParallelMethodTest.Example.fSynchronizer.countDown();
            Assert.assertTrue(ParallelMethodTest.Example.fSynchronizer.await(ParallelMethodTest.TIMEOUT, TimeUnit.SECONDS));
            ParallelMethodTest.fTwo = Thread.currentThread();
        }
    }

    @Test
    public void testsRunInParallel() {
        Result result = JUnitCore.runClasses(ParallelComputer.methods(), ParallelMethodTest.Example.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertNotNull(ParallelMethodTest.fOne);
        Assert.assertNotNull(ParallelMethodTest.fTwo);
        Assert.assertThat(ParallelMethodTest.fOne, Is.is(IsNot.not(ParallelMethodTest.fTwo)));
    }
}


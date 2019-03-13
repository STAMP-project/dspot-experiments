package com.baeldung.thread.join;


import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Demonstrates Thread.join behavior.
 */
public class ThreadJoinUnitTest {
    static final Logger LOGGER = Logger.getLogger(ThreadJoinUnitTest.class.getName());

    class SampleThread extends Thread {
        public int processingCount = 0;

        SampleThread(int processingCount) {
            this.processingCount = processingCount;
            ThreadJoinUnitTest.LOGGER.info((("Thread " + (this.getName())) + " created"));
        }

        @Override
        public void run() {
            ThreadJoinUnitTest.LOGGER.info((("Thread " + (this.getName())) + " started"));
            while ((processingCount) > 0) {
                try {
                    Thread.sleep(1000);// Simulate some work being done by thread

                } catch (InterruptedException e) {
                    ThreadJoinUnitTest.LOGGER.info((("Thread " + (this.getName())) + " interrupted."));
                }
                (processingCount)--;
                ThreadJoinUnitTest.LOGGER.info(((("Inside Thread " + (this.getName())) + ", processingCount = ") + (processingCount)));
            } 
            ThreadJoinUnitTest.LOGGER.info((("Thread " + (this.getName())) + " exiting"));
        }
    }

    @Test
    public void givenNewThread_whenJoinCalled_returnsImmediately() throws InterruptedException {
        Thread t1 = new ThreadJoinUnitTest.SampleThread(0);
        ThreadJoinUnitTest.LOGGER.info("Invoking join.");
        t1.join();
        ThreadJoinUnitTest.LOGGER.info("Returned from join");
        ThreadJoinUnitTest.LOGGER.info(("Thread state is" + (t1.getState())));
        Assert.assertFalse(t1.isAlive());
    }

    @Test
    public void givenStartedThread_whenJoinCalled_waitsTillCompletion() throws InterruptedException {
        Thread t2 = new ThreadJoinUnitTest.SampleThread(1);
        t2.start();
        ThreadJoinUnitTest.LOGGER.info("Invoking join.");
        t2.join();
        ThreadJoinUnitTest.LOGGER.info("Returned from join");
        Assert.assertFalse(t2.isAlive());
    }

    @Test
    public void givenStartedThread_whenTimedJoinCalled_waitsUntilTimedout() throws InterruptedException {
        Thread t3 = new ThreadJoinUnitTest.SampleThread(10);
        t3.start();
        t3.join(1000);
        Assert.assertTrue(t3.isAlive());
    }

    @Test
    public void givenJoinWithTerminatedThread_checkForEffect_guaranteed() throws InterruptedException {
        ThreadJoinUnitTest.SampleThread t4 = new ThreadJoinUnitTest.SampleThread(10);
        t4.start();
        do {
            t4.join(100);
        } while ((t4.processingCount) > 0 );
    }
}


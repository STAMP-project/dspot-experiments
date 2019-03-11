/**
 * Copyright (c) 2004-2016 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.slf4j.nop;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MultithreadedInitializationTest {
    static final int THREAD_COUNT = 4 + ((Runtime.getRuntime().availableProcessors()) * 2);

    private static AtomicLong EVENT_COUNT = new AtomicLong(0);

    final CyclicBarrier barrier = new CyclicBarrier(((MultithreadedInitializationTest.THREAD_COUNT) + 1));

    int diff = new Random().nextInt(10000);

    String loggerName = "org.slf4j.impl.MultithreadedInitializationTest";

    private final PrintStream oldErr = System.err;

    MultithreadedInitializationTest.StringPrintStream sps = new MultithreadedInitializationTest.StringPrintStream(oldErr);

    @Test
    public void multiThreadedInitialization() throws InterruptedException, BrokenBarrierException {
        System.out.println(("THREAD_COUNT=" + (MultithreadedInitializationTest.THREAD_COUNT)));
        MultithreadedInitializationTest.LoggerAccessingThread[] accessors = MultithreadedInitializationTest.harness();
        for (MultithreadedInitializationTest.LoggerAccessingThread accessor : accessors) {
            MultithreadedInitializationTest.EVENT_COUNT.getAndIncrement();
            accessor.logger.info("post harness");
        }
        Logger logger = LoggerFactory.getLogger((((loggerName) + ".slowInitialization-") + (diff)));
        logger.info("hello");
        MultithreadedInitializationTest.EVENT_COUNT.getAndIncrement();
        Assert.assertEquals(0, sps.stringList.size());
    }

    static class LoggerAccessingThread extends Thread {
        final CyclicBarrier barrier;

        Logger logger;

        int count;

        LoggerAccessingThread(CyclicBarrier barrier, int count) {
            this.barrier = barrier;
            this.count = count;
        }

        public void run() {
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger = LoggerFactory.getLogger((((this.getClass().getName()) + "-") + (count)));
            logger.info("in run method");
            MultithreadedInitializationTest.EVENT_COUNT.getAndIncrement();
        }
    }

    public static class StringPrintStream extends PrintStream {
        public static final String LINE_SEP = System.getProperty("line.separator");

        PrintStream other;

        List<String> stringList = new ArrayList<String>();

        public StringPrintStream(PrintStream ps) {
            super(ps);
            other = ps;
        }

        public void print(String s) {
            other.print(s);
            stringList.add(s);
        }

        public void println(String s) {
            other.println(s);
            stringList.add(s);
        }

        public void println(Object o) {
            other.println(o);
            stringList.add(o.toString());
        }
    }
}


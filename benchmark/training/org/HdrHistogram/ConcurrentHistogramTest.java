/**
 * HistogramTest.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */
package org.HdrHistogram;


import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;


/**
 * JUnit test for {@link Histogram}
 */
public class ConcurrentHistogramTest {
    static final long highestTrackableValue = ((3600L * 1000) * 1000) * 1000;// e.g. for 1 hr in usec units


    volatile boolean doRun = true;

    volatile boolean waitToGo = true;

    @Test
    public void testConcurrentAutoSizedRecording() throws Exception {
        doConcurrentRecordValues();
    }

    static AtomicLong valueRecorderId = new AtomicLong(42);

    class ValueRecorder extends Thread {
        ConcurrentHistogram histogram;

        long count = 0;

        Semaphore readySem = new Semaphore(0);

        Semaphore setSem = new Semaphore(0);

        long id = ConcurrentHistogramTest.valueRecorderId.getAndIncrement();

        Random random = new Random(id);

        ValueRecorder(ConcurrentHistogram histogram) {
            this.histogram = histogram;
        }

        public void run() {
            try {
                long nextValue = 0;
                for (int i = 0; i < (id); i++) {
                    nextValue = ((long) ((ConcurrentHistogramTest.highestTrackableValue) * (random.nextDouble())));
                }
                while (doRun) {
                    readySem.release();
                    setSem.acquire();
                    while (waitToGo) {
                        // wait for doRun to be set.
                    } 
                    histogram.resize(nextValue);
                    histogram.recordValue(nextValue);
                    (count)++;
                } 
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


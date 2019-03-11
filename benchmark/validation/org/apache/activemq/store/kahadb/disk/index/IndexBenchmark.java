/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.kahadb.disk.index;


import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.apache.activemq.store.kahadb.disk.page.Transaction;


/**
 *
 *
 * @author chirino
 */
public abstract class IndexBenchmark extends TestCase {
    // Slower machines might need to make this bigger.
    private static final long SAMPLE_DURATION = Integer.parseInt(System.getProperty("SAMPLES_DURATION", ("" + (1000 * 5))));

    // How many times do we sample?
    private static final long SAMPLES = Integer.parseInt(System.getProperty("SAMPLES", ("" + ((60 * 1000) / (IndexBenchmark.SAMPLE_DURATION)))));

    // How many indexes will we be benchmarking concurrently?
    private static final int INDEX_COUNT = Integer.parseInt(System.getProperty("INDEX_COUNT", ("" + 1)));

    // Indexes tend to perform worse when they get big.. so how many items
    // should we put into the index before we start sampling.
    private static final int INDEX_PRE_LOAD_COUNT = Integer.parseInt(System.getProperty("INDEX_PRE_LOAD_COUNT", ("" + (10000 / (IndexBenchmark.INDEX_COUNT)))));

    protected File ROOT_DIR;

    protected final HashMap<String, Index<String, Long>> indexes = new HashMap<String, Index<String, Long>>();

    protected PageFile pf;

    class Producer extends Thread {
        private final String name;

        AtomicBoolean shutdown = new AtomicBoolean();

        public Producer(String name) {
            super(("Producer: " + name));
            this.name = name;
        }

        public void shutdown() {
            shutdown.set(true);
        }

        @Override
        public void run() {
            try {
                Transaction tx = pf.tx();
                Index<String, Long> index = openIndex(name);
                long counter = 0;
                while (!(shutdown.get())) {
                    long c = counter;
                    String key = key(c);
                    index.put(tx, key, c);
                    tx.commit();
                    Thread.yield();// This avoids consumer starvation..

                    onProduced((counter++));
                } 
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public void onProduced(long counter) {
        }
    }

    class Consumer extends Thread {
        private final String name;

        AtomicBoolean shutdown = new AtomicBoolean();

        public Consumer(String name) {
            super(("Consumer: " + name));
            this.name = name;
        }

        public void shutdown() {
            shutdown.set(true);
        }

        @Override
        public void run() {
            try {
                Transaction tx = pf.tx();
                Index<String, Long> index = openIndex(name);
                long counter = 0;
                while (!(shutdown.get())) {
                    long c = counter;
                    String key = key(c);
                    Long record = index.get(tx, key);
                    if (record != null) {
                        if ((index.remove(tx, key)) == null) {
                            System.out.print("Remove failed...");
                        }
                        tx.commit();
                        onConsumed((counter++));
                    }
                } 
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public void onConsumed(long counter) {
        }
    }

    public void testLoad() throws Exception {
        final IndexBenchmark.Producer[] producers = new IndexBenchmark.Producer[IndexBenchmark.INDEX_COUNT];
        final IndexBenchmark.Consumer[] consumers = new IndexBenchmark.Consumer[IndexBenchmark.INDEX_COUNT];
        final CountDownLatch preloadCountDown = new CountDownLatch(IndexBenchmark.INDEX_COUNT);
        final AtomicLong producedRecords = new AtomicLong();
        final AtomicLong consumedRecords = new AtomicLong();
        System.out.println((("Starting: " + (IndexBenchmark.INDEX_COUNT)) + " producers"));
        for (int i = 0; i < (IndexBenchmark.INDEX_COUNT); i++) {
            producers[i] = new IndexBenchmark.Producer(("test-" + i)) {
                private boolean prelaodDone;

                public void onProduced(long counter) {
                    if ((!(prelaodDone)) && (counter >= (IndexBenchmark.INDEX_PRE_LOAD_COUNT))) {
                        prelaodDone = true;
                        preloadCountDown.countDown();
                    }
                    producedRecords.incrementAndGet();
                }
            };
            producers[i].start();
        }
        long start = System.currentTimeMillis();
        System.out.println((("Waiting for each producer create " + (IndexBenchmark.INDEX_PRE_LOAD_COUNT)) + " records before starting the consumers."));
        preloadCountDown.await();
        long end = System.currentTimeMillis();
        System.out.println((((("Preloaded " + ((IndexBenchmark.INDEX_PRE_LOAD_COUNT) * (IndexBenchmark.INDEX_COUNT))) + " records at ") + ((((IndexBenchmark.INDEX_PRE_LOAD_COUNT) * (IndexBenchmark.INDEX_COUNT)) * 1000.0F) / (end - start))) + " records/sec"));
        System.out.println((("Starting: " + (IndexBenchmark.INDEX_COUNT)) + " consumers"));
        for (int i = 0; i < (IndexBenchmark.INDEX_COUNT); i++) {
            consumers[i] = new IndexBenchmark.Consumer(("test-" + i)) {
                public void onConsumed(long counter) {
                    consumedRecords.incrementAndGet();
                }
            };
            consumers[i].start();
        }
        long sample_start = System.currentTimeMillis();
        System.out.println((((("Taking " + (IndexBenchmark.SAMPLES)) + " performance samples every ") + (IndexBenchmark.SAMPLE_DURATION)) + " ms"));
        System.out.println("time (s), produced, produce rate (r/s), consumed, consume rate (r/s), used memory (k)");
        producedRecords.set(0);
        consumedRecords.set(0);
        for (int i = 0; i < (IndexBenchmark.SAMPLES); i++) {
            start = System.currentTimeMillis();
            Thread.sleep(IndexBenchmark.SAMPLE_DURATION);
            end = System.currentTimeMillis();
            long p = producedRecords.getAndSet(0);
            long c = consumedRecords.getAndSet(0);
            long usedMemory = (Runtime.getRuntime().totalMemory()) - (Runtime.getRuntime().freeMemory());
            System.out.println(((((((((((((end - sample_start) / 1000.0F) + ", ") + p) + ", ") + ((p * 1000.0F) / (end - start))) + ", ") + c) + ", ") + ((c * 1000.0F) / (end - start))) + ", ") + (usedMemory / 1024)));
        }
        System.out.println("Samples done... Shutting down the producers and consumers...");
        for (int i = 0; i < (IndexBenchmark.INDEX_COUNT); i++) {
            producers[i].shutdown();
            consumers[i].shutdown();
        }
        for (int i = 0; i < (IndexBenchmark.INDEX_COUNT); i++) {
            producers[i].join((1000 * 5));
            consumers[i].join((1000 * 5));
        }
        System.out.println("Shutdown.");
    }
}


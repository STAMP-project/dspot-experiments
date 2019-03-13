/**
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.support;


import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;


/**
 *
 *
 * @author Matthew Ouyang
 */
public class SynchronizedItemStreamReaderTests {
    /**
     * A simple class used to test the SynchronizedItemStreamReader.  It simply returns
     * the number of times the read method has been called, manages some state variables
     * and updates an ExecutionContext.
     *
     * @author Matthew Ouyang
     */
    private class TestItemReader extends AbstractItemStreamItemReader<Integer> implements ItemStreamReader<Integer> {
        private int cursor = 0;

        private boolean isClosed = false;

        public static final String HAS_BEEN_OPENED = "hasBeenOpened";

        public static final String UPDATE_COUNT_KEY = "updateCount";

        public Integer read() throws Exception, NonTransientResourceException, ParseException {
            cursor = (cursor) + 1;
            return cursor;
        }

        public void close() {
            this.isClosed = true;
        }

        public void open(ExecutionContext executionContext) {
            this.isClosed = false;
            executionContext.put(SynchronizedItemStreamReaderTests.TestItemReader.HAS_BEEN_OPENED, true);
            executionContext.remove(SynchronizedItemStreamReaderTests.TestItemReader.UPDATE_COUNT_KEY);
        }

        public void update(ExecutionContext executionContext) {
            if (!(executionContext.containsKey(SynchronizedItemStreamReaderTests.TestItemReader.UPDATE_COUNT_KEY))) {
                executionContext.putInt(SynchronizedItemStreamReaderTests.TestItemReader.UPDATE_COUNT_KEY, 0);
            }
            executionContext.putInt(SynchronizedItemStreamReaderTests.TestItemReader.UPDATE_COUNT_KEY, ((executionContext.getInt(SynchronizedItemStreamReaderTests.TestItemReader.UPDATE_COUNT_KEY)) + 1));
        }

        public boolean isClosed() {
            return this.isClosed;
        }
    }

    @Test
    public void testMultipleThreads() throws Exception {
        // Initialized an ExecutionContext and a SynchronizedItemStreamReader to test.
        final ExecutionContext executionContext = new ExecutionContext();
        final SynchronizedItemStreamReaderTests.TestItemReader testItemReader = new SynchronizedItemStreamReaderTests.TestItemReader();
        final SynchronizedItemStreamReader<Integer> synchronizedItemStreamReader = new SynchronizedItemStreamReader();
        synchronizedItemStreamReader.setDelegate(testItemReader);
        // Open the ItemReader and make sure it's initialized properly.
        synchronizedItemStreamReader.open(executionContext);
        Assert.assertEquals(true, executionContext.get(SynchronizedItemStreamReaderTests.TestItemReader.HAS_BEEN_OPENED));
        Assert.assertFalse(testItemReader.isClosed());
        /* Set up SIZE threads that read from the reader and updates the execution 
        context.
         */
        final Set<Integer> ecSet = new HashSet<>();
        final int SIZE = 20;
        Thread[] threads = new Thread[SIZE];
        for (int i = 0; i < SIZE; i++) {
            threads[i] = new Thread() {
                public void run() {
                    try {
                        ecSet.add(synchronizedItemStreamReader.read());
                        synchronizedItemStreamReader.update(executionContext);
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }
            };
        }
        // Start the threads and block until all threads are done.
        for (Thread thread : threads) {
            thread.run();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        testItemReader.close();
        /* Ensure cleanup happens as expected: status variable is set correctly and 
        ExecutionContext variable is set properly.  Lastly, the Set<Integer> should 
        have 1 to 20 which may not always be the case if the read is not synchronized.
         */
        for (int i = 1; i <= SIZE; i++) {
            Assert.assertTrue(ecSet.contains(i));
        }
        Assert.assertTrue(testItemReader.isClosed());
        Assert.assertEquals(SIZE, executionContext.getInt(SynchronizedItemStreamReaderTests.TestItemReader.UPDATE_COUNT_KEY));
    }
}


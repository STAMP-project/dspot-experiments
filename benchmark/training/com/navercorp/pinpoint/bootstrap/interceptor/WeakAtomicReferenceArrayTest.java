/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.interceptor;


import com.navercorp.pinpoint.bootstrap.interceptor.registry.WeakAtomicReferenceArray;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * raceCondition generate fail.~~~~ hmm
 */
@Ignore
public class WeakAtomicReferenceArrayTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int arraySize = 1024 * 2000;

    private final int testMapSize = 1;

    private final Map<Integer, WeakAtomicReferenceArrayTest.AtomicReferenceTest> map = new HashMap<Integer, WeakAtomicReferenceArrayTest.AtomicReferenceTest>();

    private final AtomicInteger nextMapId = new AtomicInteger();

    private int writerThreadSize = 1;

    private ThreadPoolExecutor writer;

    private int readThreadSize = 2;

    private ThreadPoolExecutor reader;

    private final AtomicInteger failCounter = new AtomicInteger();

    static final class Cell {
        volatile long p0;

        volatile long p1;

        volatile long p2;

        volatile long p3;

        volatile long p4;

        volatile long p5;

        volatile long p6;

        volatile WeakAtomicReferenceArray<Integer> weakAtomicReferenceArray;

        volatile long q0;

        volatile long q1;

        volatile long q2;

        volatile long q3;

        volatile long q4;

        volatile long q5;

        volatile long q6;
    }

    private static class ChangedValue {
        private int index;

        private int value;

        public ChangedValue(int index, int value) {
            this.index = index;
            this.value = value;
        }
    }

    private class AtomicReferenceTest {
        private final WeakAtomicReferenceArrayTest.Cell cell = new WeakAtomicReferenceArrayTest.Cell();

        private final AtomicInteger nextId = new AtomicInteger(0);

        private final AtomicMaxUpdater maxIndex = new AtomicMaxUpdater();

        // private final ConcurrentLinkedQueue<Integer> updateIndex = new ConcurrentLinkedQueue<Integer>();
        private final WeakAtomicReferenceArray<Integer> ref;

        private final AtomicInteger afterLast = new AtomicInteger((-1));

        private final AtomicReference<WeakAtomicReferenceArrayTest.ChangedValue> lastChangeValue = new AtomicReference<WeakAtomicReferenceArrayTest.ChangedValue>();

        private final Random random = new Random();

        public AtomicReferenceTest() {
            cell.weakAtomicReferenceArray = new WeakAtomicReferenceArray<Integer>(arraySize, Integer.class);
            ref = cell.weakAtomicReferenceArray;
        }

        public boolean nextId() {
            int nextId = this.nextId.getAndIncrement();
            if (nextId < (arraySize)) {
                ref.set(nextId, nextId);
                afterLast.set(nextId);
                // maxIndex.updateMax(nextId);
                // updateIndex.offer(nextId);
                // logger.debug("nextId:{}", nextId);
                return true;
            } else {
                return false;
            }
        }

        public boolean changeId(int index, int value) {
            if (index < (arraySize)) {
                ref.set(index, value);
                lastChangeValue.set(new WeakAtomicReferenceArrayTest.ChangedValue(index, value));
                // maxIndex.updateMax(nextId);
                // updateIndex.offer(nextId);
                // logger.debug("nextId:{}", nextId);
                return true;
            } else {
                return false;
            }
        }

        public boolean get(final int findIndex) {
            if (findIndex == (-1)) {
                return true;
            }
            Integer findResult = ref.get(findIndex);
            return checkInteger(findIndex, findResult);
        }

        public boolean checkChangeId() {
            WeakAtomicReferenceArrayTest.ChangedValue changedValue = lastChangeValue.get();
            if (changedValue == null) {
                return true;
            }
            Integer findResult = ref.get(changedValue.index);
            return checkInteger(changedValue.value, findResult);
        }

        private boolean checkInteger(int findIndex, Integer findResult) {
            if (findResult == null) {
                logger.debug("null find:{} result:{}", findIndex, nextId.get());
                return false;
            }
            final boolean result = findResult == findIndex;
            if (!result) {
                logger.debug("not equals findResult:{}, findIndex:{}", findResult, findIndex);
            }
            return result;
        }

        // public boolean testInsert() {
        // final Integer findIndex = updateIndex.poll();
        // if (findIndex == null) {
        // return true;
        // }
        // Integer findResult = cell.weakAtomicReferenceArray.get(findIndex);
        // if (random.nextInt(3) == 0) {
        // updateIndex.offer(findIndex);
        // }
        // return checkInteger(findIndex, findResult);
        // }
        public boolean randomGet() {
            if ((writerThreadSize) != 1) {
                return true;
            }
            // final int maxIndex = getMaxIndex();
            final int maxIndex = afterLast.get();
            if (maxIndex == (-1)) {
                return true;
            }
            int randomIndex;
            if (maxIndex == 0) {
                randomIndex = 0;
            } else {
                randomIndex = (Math.abs(random.nextInt())) % maxIndex;
            }
            return get(randomIndex);
        }

        public boolean lastGet() {
            return get(getMaxIndex());
        }

        private int getMaxIndex() {
            return maxIndex.getIndex();
        }
    }

    @Test
    public void testLastGet() {
        WeakAtomicReferenceArrayTest.AtomicReferenceTest mock = new WeakAtomicReferenceArrayTest.AtomicReferenceTest();
        Assert.assertTrue(mock.lastGet());
        mock.nextId();
        Assert.assertTrue(mock.lastGet());
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(mock.lastGet());
        }
    }

    @Test
    public void testRandomGet() {
        WeakAtomicReferenceArrayTest.AtomicReferenceTest mock = new WeakAtomicReferenceArrayTest.AtomicReferenceTest();
        Assert.assertTrue(mock.randomGet());
        mock.nextId();
        mock.nextId();
        mock.nextId();
        Assert.assertTrue(mock.randomGet());
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(mock.randomGet());
        }
    }

    @Test
    public void testTestMock3() {
        WeakAtomicReferenceArrayTest.AtomicReferenceTest mock = new WeakAtomicReferenceArrayTest.AtomicReferenceTest();
        mock.nextId();
        mock.nextId();
        mock.nextId();
        Assert.assertTrue(mock.randomGet());
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(mock.randomGet());
        }
    }

    @Test
    public void test() throws Exception {
        final AtomicBoolean start = new AtomicBoolean(true);
        final Runnable writeJob = new Runnable() {
            @Override
            public void run() {
                logger.debug("WriteJob-start");
                int i = 0;
                while (start.get()) {
                    WeakAtomicReferenceArrayTest.AtomicReferenceTest referenceTest = getTestMock();
                    referenceTest.nextId();
                    // referenceTest.changeId(0, i);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignore) {
                    }
                    i++;
                } 
                logger.debug("WriteJob-end");
            }
        };
        final Runnable readJob = new Runnable() {
            @Override
            public void run() {
                logger.debug("ReaderJob-start");
                while (start.get()) {
                    WeakAtomicReferenceArrayTest.AtomicReferenceTest atomicReferenceTest = getTestMock();
                    if (!(atomicReferenceTest.lastGet())) {
                        failCounter.getAndIncrement();
                    }
                    // if (!atomicReferenceTest.checkChangeId()) {
                    // failCounter.getAndIncrement();
                    // }
                    // if (!testMock.testInsert()) {
                    // failCounter.getAndIncrement();
                    // }
                    // if (!testMock.randomGet()) {
                    // failCounter.getAndIncrement();
                    // }
                    // try {
                    // Thread.sleep(10);
                    // } catch (InterruptedException e) {
                    // e.printStackTrace();
                    // }
                } 
                logger.debug("ReaderJob-end");
            }
        };
        for (int i = 0; i < (readThreadSize); i++) {
            reader.execute(readJob);
        }
        for (int i = 0; i < (writerThreadSize); i++) {
            writer.execute(writeJob);
        }
        logger.debug("start");
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            logger.debug("failCounter:{}", failCounter.get());
        }
        start.set(false);
        Thread.sleep(1000);
        Assert.assertEquals("raceCondition test", failCounter.get(), 0);
        writer.shutdown();
        reader.shutdown();
    }
}


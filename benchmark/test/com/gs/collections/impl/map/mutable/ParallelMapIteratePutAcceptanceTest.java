/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.impl.map.mutable;


import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;


public class ParallelMapIteratePutAcceptanceTest {
    private static final long SEED = 20015998348237L;

    private static final long PUT_REPEAT = 100;

    private static final int CHUNK_SIZE = 16000;

    private static final int MAX_THREADS = 48;

    @Test
    public void testMapIteratePut() {
        int constSize = 100000;
        int size = 10000000;
        Integer[] contents = new Integer[size];
        Integer[] constContents = new Integer[constSize];
        for (int i = 0; i < size; i++) {
            contents[i] = i;
            if (i < (constSize / 2)) {
                constContents[i] = i;
            } else
                if (i < constSize) {
                    constContents[i] = size - i;
                }

        }
        Collections.shuffle(Arrays.asList(contents), new Random(ParallelMapIteratePutAcceptanceTest.SEED));
        this.runAllPutTests(contents, constContents);
    }

    private static final class PutRunner1 implements Runnable {
        private final Map<Integer, Integer> map;

        private final Integer[] contents;

        private long total;

        private final AtomicInteger queuePosition;

        private PutRunner1(Map<Integer, Integer> map, Integer[] contents, AtomicInteger queuePosition) {
            this.map = map;
            this.contents = contents;
            this.queuePosition = queuePosition;
        }

        @Override
        public void run() {
            while ((this.queuePosition.get()) < (this.contents.length)) {
                int end = this.queuePosition.addAndGet(ParallelMapIteratePutAcceptanceTest.CHUNK_SIZE);
                int start = end - (ParallelMapIteratePutAcceptanceTest.CHUNK_SIZE);
                if (start < (this.contents.length)) {
                    if (end > (this.contents.length)) {
                        end = this.contents.length;
                    }
                    for (int i = start; i < end; i++) {
                        if ((this.map.put(this.contents[i], this.contents[i])) != null) {
                            (this.total)++;
                        }
                    }
                }
            } 
            if ((this.total) < 0) {
                throw new AssertionError("never gets here, but it can't be optimized away");
            }
        }
    }
}


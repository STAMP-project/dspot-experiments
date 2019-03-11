/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.groupby.epinephelinae;


import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.java.util.common.parsers.CloseableIterator;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.groupby.epinephelinae.Grouper.Entry;
import org.apache.druid.query.groupby.epinephelinae.Grouper.KeySerdeFactory;
import org.junit.Assert;
import org.junit.Test;


public class ParallelCombinerTest {
    private static final int THREAD_NUM = 8;

    private static final ExecutorService SERVICE = Execs.multiThreaded(ParallelCombinerTest.THREAD_NUM, "parallel-combiner-test-%d");

    private static final ConcurrentGrouperTest.TestResourceHolder TEST_RESOURCE_HOLDER = new ConcurrentGrouperTest.TestResourceHolder(512);

    private static final KeySerdeFactory<Long> KEY_SERDE_FACTORY = new ConcurrentGrouperTest.TestKeySerdeFactory();

    private static final class TestIterator implements CloseableIterator<Entry<Long>> {
        private final Iterator<Entry<Long>> innerIterator;

        private boolean closed;

        TestIterator(Iterator<Entry<Long>> innerIterator) {
            this.innerIterator = innerIterator;
        }

        @Override
        public boolean hasNext() {
            return innerIterator.hasNext();
        }

        @Override
        public Entry<Long> next() {
            return innerIterator.next();
        }

        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() {
            if (!(closed)) {
                closed = true;
            }
        }
    }

    @Test
    public void testCombine() throws IOException {
        final ParallelCombiner<Long> combiner = // default priority
        // default timeout
        new ParallelCombiner(ParallelCombinerTest.TEST_RESOURCE_HOLDER, new AggregatorFactory[]{ new CountAggregatorFactory("cnt").getCombiningFactory() }, ParallelCombinerTest.KEY_SERDE_FACTORY, MoreExecutors.listeningDecorator(ParallelCombinerTest.SERVICE), false, ParallelCombinerTest.THREAD_NUM, 0, 0, 4);
        final int numRows = 1000;
        final List<Entry<Long>> baseIterator = new ArrayList<>(numRows);
        for (long i = 0; i < numRows; i++) {
            baseIterator.add(new Entry(i, new Object[]{ i * 10 }));
        }
        final int leafNum = 8;
        final List<ParallelCombinerTest.TestIterator> iterators = new ArrayList<>(leafNum);
        for (int i = 0; i < leafNum; i++) {
            iterators.add(new ParallelCombinerTest.TestIterator(baseIterator.iterator()));
        }
        try (final CloseableIterator<Entry<Long>> iterator = combiner.combine(iterators, new ArrayList())) {
            long expectedKey = 0;
            while (iterator.hasNext()) {
                Assert.assertEquals(new Entry(expectedKey, new Object[]{ ((expectedKey++) * leafNum) * 10 }), iterator.next());
            } 
        }
        iterators.forEach(( it) -> Assert.assertTrue(it.isClosed()));
    }
}


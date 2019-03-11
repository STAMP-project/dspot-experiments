/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.spark.io;


import java.io.IOException;
import java.util.NoSuchElementException;
import org.apache.beam.runners.core.metrics.MetricsContainerImpl;
import org.apache.beam.sdk.io.Source;
import org.hamcrest.core.Is;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test for {@link SourceRDD.Bounded.ReaderToIteratorAdapter}.
 */
public class ReaderToIteratorAdapterTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static class TestReader extends Source.Reader<Integer> {
        static final int LIMIT = 4;

        static final int START = 1;

        private Integer current = (ReaderToIteratorAdapterTest.TestReader.START) - 1;

        private boolean closed = false;

        private boolean drained = false;

        boolean isClosed() {
            return closed;
        }

        @Override
        public boolean start() throws IOException {
            return advance();
        }

        @Override
        public boolean advance() throws IOException {
            checkState(((!(drained)) && (!(closed))));
            drained = (++(current)) >= (ReaderToIteratorAdapterTest.TestReader.LIMIT);
            return !(drained);
        }

        @Override
        public Integer getCurrent() throws NoSuchElementException {
            checkState(((!(drained)) && (!(closed))));
            return current;
        }

        @Override
        public Instant getCurrentTimestamp() throws NoSuchElementException {
            checkState(((!(drained)) && (!(closed))));
            return Instant.now();
        }

        @Override
        public void close() throws IOException {
            checkState((!(closed)));
            closed = true;
        }

        @Override
        public Source<Integer> getCurrentSource() {
            return null;
        }
    }

    private final ReaderToIteratorAdapterTest.TestReader testReader = new ReaderToIteratorAdapterTest.TestReader();

    private final SourceRDD.Bounded.ReaderToIteratorAdapter<Integer> readerIterator = new SourceRDD.Bounded.ReaderToIteratorAdapter<>(new MetricsContainerImpl(""), testReader);

    @Test
    public void testReaderIsClosedAfterDrainage() throws Exception {
        assertReaderRange(ReaderToIteratorAdapterTest.TestReader.START, ReaderToIteratorAdapterTest.TestReader.LIMIT);
        Assert.assertThat(readerIterator.hasNext(), Is.is(false));
        // reader is closed only after hasNext realises there are no more elements
        Assert.assertThat(testReader.isClosed(), Is.is(true));
    }

    @Test
    public void testNextWhenDrainedThrows() throws Exception {
        assertReaderRange(ReaderToIteratorAdapterTest.TestReader.START, ReaderToIteratorAdapterTest.TestReader.LIMIT);
        exception.expect(NoSuchElementException.class);
        readerIterator.next();
    }

    @Test
    public void testHasNextIdempotencyCombo() throws Exception {
        Assert.assertThat(readerIterator.hasNext(), Is.is(true));
        Assert.assertThat(readerIterator.hasNext(), Is.is(true));
        Assert.assertThat(readerIterator.next().getValue(), Is.is(1));
        Assert.assertThat(readerIterator.hasNext(), Is.is(true));
        Assert.assertThat(readerIterator.hasNext(), Is.is(true));
        Assert.assertThat(readerIterator.hasNext(), Is.is(true));
        Assert.assertThat(readerIterator.next().getValue(), Is.is(2));
        Assert.assertThat(readerIterator.next().getValue(), Is.is(3));
        // drained
        Assert.assertThat(readerIterator.hasNext(), Is.is(false));
        Assert.assertThat(readerIterator.hasNext(), Is.is(false));
        // no next to give
        exception.expect(NoSuchElementException.class);
        readerIterator.next();
    }
}


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
package org.apache.beam.runners.dataflow.worker;


import com.google.api.services.dataflow.model.Source;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.DynamicSplitResult;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@code ConcatReader}.
 */
@RunWith(JUnit4.class)
public class ConcatReaderTest {
    private static final String READER_OBJECT = "reader_object";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private List<ConcatReaderTest.TestReader<?>> recordedReaders = new ArrayList<>();

    private ReaderRegistry registry;

    /**
     * A {@link NativeReader} used for testing purposes. Delegates functionality to an underlying
     * {@link InMemoryReader}.
     */
    public class TestReader<T> extends NativeReader<T> {
        private final long recordToFailAt;

        private final boolean failWhenClosing;

        private ConcatReaderTest.TestReader<T>.TestIterator lastIterator = null;

        private final NativeReader<T> readerDelegator;

        /**
         * Create a TestReader.
         *
         * @param encodedElements
         * 		list of elements read by the {@code Reader}
         * @param coder
         * 		{@code Coder} to by used by the underlying {@code Reader}
         * @param recordToFailAt
         * 		if non-negative, a {@code TestIterator} will fail throwing an {@code IOException} when trying to read the element at this index
         * @param failWhenClosing
         * 		if {@code true}, a {@code TestIterator} will fail throwing an {@code IOException} when {@link TestIterator#close()} is invoked
         */
        public TestReader(List<String> encodedElements, Coder<T> coder, long recordToFailAt, boolean failWhenClosing) {
            this.recordToFailAt = recordToFailAt;
            this.failWhenClosing = failWhenClosing;
            readerDelegator = new InMemoryReader(encodedElements, 0, encodedElements.size(), coder);
            recordedReaders.add(this);
        }

        public boolean isClosedOrUnopened() {
            if ((lastIterator) != null) {
                return lastIterator.isClosed;
            }
            // A reader was not created
            return true;
        }

        @Override
        public NativeReaderIterator<T> iterator() throws IOException {
            lastIterator = new TestIterator(readerDelegator.iterator());
            return lastIterator;
        }

        private class TestIterator extends NativeReaderIterator<T> {
            private final NativeReaderIterator<T> iteratorImpl;

            private long currentIndex = -1;

            private boolean isClosed = false;

            private TestIterator(NativeReaderIterator<T> iteratorImpl) {
                this.iteratorImpl = iteratorImpl;
            }

            @Override
            public boolean start() throws IOException {
                (currentIndex)++;
                if ((currentIndex) == (recordToFailAt)) {
                    throw new IOException(("Failing at record " + (currentIndex)));
                }
                return iteratorImpl.start();
            }

            @Override
            public boolean advance() throws IOException {
                (currentIndex)++;
                if ((currentIndex) == (recordToFailAt)) {
                    throw new IOException(("Failing at record " + (currentIndex)));
                }
                return iteratorImpl.advance();
            }

            @Override
            public T getCurrent() throws NoSuchElementException {
                return iteratorImpl.getCurrent();
            }

            @Override
            public void close() throws IOException {
                isClosed = true;
                if (failWhenClosing) {
                    throw new IOException("Failing when closing");
                }
                iteratorImpl.close();
            }

            @Override
            public Progress getProgress() {
                return iteratorImpl.getProgress();
            }

            @Override
            public DynamicSplitResult requestDynamicSplit(DynamicSplitRequest request) {
                throw new UnsupportedOperationException();
            }

            @Override
            public double getRemainingParallelism() {
                return Double.NaN;
            }
        }
    }

    private static class TestReaderFactory implements ReaderFactory {
        @Override
        public NativeReader<?> create(CloudObject spec, @Nullable
        Coder<?> coder, @Nullable
        PipelineOptions options, @Nullable
        DataflowExecutionContext executionContext, DataflowOperationContext operationContext) throws Exception {
            return ((NativeReader<?>) (spec.get(ConcatReaderTest.READER_OBJECT)));
        }
    }

    @Test
    public void testCreateFromNull() throws Exception {
        expectedException.expect(NullPointerException.class);
        /* options */
        /* executionContext */
        /* sources */
        new ConcatReader<String>(registry, null, null, TestOperationContext.create(), null);
    }

    @Test
    public void testReadEmptyList() throws Exception {
        ConcatReader<String> concat = /* options */
        /* executionContext */
        new ConcatReader(registry, null, null, TestOperationContext.create(), new ArrayList<Source>());
        ConcatReader.ConcatIterator<String> iterator = concat.iterator();
        Assert.assertNotNull(iterator);
        Assert.assertFalse(concat.iterator().start());
        expectedException.expect(NoSuchElementException.class);
        iterator.getCurrent();
    }

    @Test
    public void testReadOne() throws Exception {
        testReadersOfSizes(100);
    }

    @Test
    public void testReadMulti() throws Exception {
        testReadersOfSizes(10, 5, 20, 40);
    }

    @Test
    public void testReadFirstReaderEmpty() throws Exception {
        testReadersOfSizes(0, 5, 20, 40);
    }

    @Test
    public void testReadLastReaderEmpty() throws Exception {
        testReadersOfSizes(10, 5, 20, 0);
    }

    @Test
    public void testEmptyReaderBeforeNonEmptyReader() throws Exception {
        testReadersOfSizes(10, 0, 20, 30);
    }

    @Test
    public void testMultipleReadersAreEmpty() throws Exception {
        testReadersOfSizes(10, 0, 20, 0, 30, 0, 40);
    }

    @Test
    public void testAReaderFailsAtClose() throws Exception {
        List<String> expected = new ArrayList<>();
        List<Source> sources = Arrays.asList(createSourceForTestReader(/* recordsPerReader */
        /* recordToFailAt */
        /* failWhenClosing */
        createTestReader(10, (-1), false, expected)), createSourceForTestReader(/* recordsPerReader */
        /* recordToFailAt */
        /* failWhenClosing */
        createTestReader(10, (-1), true, expected)), createSourceForTestReader(/* recordsPerReader */
        /* recordToFailAt */
        /* failWhenClosing */
        createTestReader(10, (-1), false, new ArrayList<String>())));
        ConcatReader<String> concatReader = /* options */
        /* executionContext */
        new ConcatReader(registry, null, null, TestOperationContext.create(), sources);
        try {
            ReaderUtils.readAllFromReader(concatReader);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals(3, recordedReaders.size());
            ConcatReaderTest.assertAllOpenReadersClosed(recordedReaders);
        }
    }

    @Test
    public void testReaderFailsAtRead() throws Exception {
        List<String> expected = new ArrayList<>();
        List<Source> sources = Arrays.asList(createSourceForTestReader(/* recordsPerReader */
        /* recordToFailAt */
        /* failWhenClosing */
        createTestReader(10, (-1), false, expected)), createSourceForTestReader(/* recordsPerReader */
        /* recordToFailAt */
        /* failWhenClosing */
        createTestReader(10, 6, false, expected)), createSourceForTestReader(/* recordsPerReader */
        /* recordToFailAt */
        /* failWhenClosing */
        createTestReader(10, (-1), false, expected)));
        expected = expected.subList(0, 16);
        Assert.assertEquals(16, expected.size());
        ConcatReader<String> concatReader = /* options */
        /* executionContext */
        new ConcatReader(registry, null, null, TestOperationContext.create(), sources);
        try {
            ReaderUtils.readAllFromReader(concatReader);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals(3, recordedReaders.size());
            ConcatReaderTest.assertAllOpenReadersClosed(recordedReaders);
        }
    }

    @Test
    public void testGetProgressSingle() throws Exception {
        runProgressTest(10);
    }

    @Test
    public void testGetProgressSameSize() throws Exception {
        runProgressTest(10, 10, 10);
    }

    @Test
    public void testGetProgressDifferentSizes() throws Exception {
        runProgressTest(10, 30, 20, 15, 7);
    }

    @Test
    public void testUpdateStopPositionSingle() throws Exception {
        runUpdateStopPositionTest(10);
    }

    @Test
    public void testUpdateStopPositionSameSize() throws Exception {
        runUpdateStopPositionTest(10, 10, 10);
    }

    @Test
    public void testUpdateStopPositionDifferentSizes() throws Exception {
        runUpdateStopPositionTest(10, 30, 20, 15, 7);
    }
}


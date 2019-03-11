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
package org.apache.druid.data.input.impl.prefetch;


import RetryUtils.MAX_SLEEP_MILLIS;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.druid.data.input.FiniteFirehoseFactory;
import org.apache.druid.data.input.Firehose;
import org.apache.druid.data.input.InputSplit;
import org.apache.druid.data.input.Row;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.StringInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.java.util.common.RetryUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class PrefetchableTextFilesFirehoseFactoryTest {
    private static long FILE_SIZE = -1;

    private static final StringInputRowParser parser = new StringInputRowParser(new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("timestamp", "a", "b")), new ArrayList(), new ArrayList()), ",", Arrays.asList("timestamp", "a", "b"), false, 0), StandardCharsets.UTF_8.name());

    @ClassRule
    public static TemporaryFolder tempDir = new TemporaryFolder();

    private static File TEST_DIR;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWithoutCacheAndFetch() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.with(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 0, 0);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testWithoutCacheAndFetch");
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        Assert.assertEquals(0, getCacheManager().getTotalCachedBytes());
        PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
        PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 0);
    }

    @Test
    public void testWithoutCacheAndFetchAgainstConnectionReset() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.withConnectionResets(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 0, 0, 2);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testWithoutCacheAndFetch");
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        Assert.assertEquals(0, getCacheManager().getTotalCachedBytes());
        PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
        PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 0);
    }

    @Test
    public void testWithoutCache() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.with(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 0, 2048);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testWithoutCache");
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        Assert.assertEquals(0, getCacheManager().getTotalCachedBytes());
        PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
        PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 0);
    }

    @Test
    public void testWithZeroFetchCapacity() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.with(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 2048, 0);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testWithZeroFetchCapacity");
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
        PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 2);
    }

    @Test
    public void testWithCacheAndFetch() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.of(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testWithCacheAndFetch");
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
        PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 2);
    }

    @Test
    public void testWithLargeCacheAndSmallFetch() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.with(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 2048, 1024);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testWithLargeCacheAndSmallFetch");
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
        PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 2);
    }

    @Test
    public void testWithSmallCacheAndLargeFetch() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.with(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 1024, 2048);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testWithSmallCacheAndLargeFetch");
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
        PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 1);
    }

    @Test
    public void testRetry() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.withOpenExceptions(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 1);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testRetry");
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
        PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 2);
    }

    @Test
    public void testMaxRetry() throws IOException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(ExecutionException.class));
        expectedException.expectMessage("Exception for retry test");
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.withOpenExceptions(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 5);
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testMaxRetry"))) {
            while (firehose.hasMore()) {
                firehose.nextRow();
            } 
        }
    }

    @Test
    public void testTimeout() throws IOException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(TimeoutException.class));
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.withSleepMillis(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 1000);
        try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testTimeout"))) {
            while (firehose.hasMore()) {
                firehose.nextRow();
            } 
        }
    }

    @Test
    public void testReconnectWithCacheAndPrefetch() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.of(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR);
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testReconnectWithCacheAndPrefetch");
        for (int i = 0; i < 5; i++) {
            final List<Row> rows = new ArrayList<>();
            try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
                if (i > 0) {
                    Assert.assertEquals(((PrefetchableTextFilesFirehoseFactoryTest.FILE_SIZE) * 2), getCacheManager().getTotalCachedBytes());
                }
                while (firehose.hasMore()) {
                    rows.add(firehose.nextRow());
                } 
            }
            PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
            PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 2);
        }
    }

    @Test
    public void testReconnectWithCache() throws IOException {
        final PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory factory = PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.with(PrefetchableTextFilesFirehoseFactoryTest.TEST_DIR, 2048, 0);
        final File firehoseTmpDir = PrefetchableTextFilesFirehoseFactoryTest.createFirehoseTmpDir("testReconnectWithCache");
        for (int i = 0; i < 5; i++) {
            final List<Row> rows = new ArrayList<>();
            try (Firehose firehose = factory.connect(PrefetchableTextFilesFirehoseFactoryTest.parser, firehoseTmpDir)) {
                if (i > 0) {
                    Assert.assertEquals(((PrefetchableTextFilesFirehoseFactoryTest.FILE_SIZE) * 2), getCacheManager().getTotalCachedBytes());
                }
                while (firehose.hasMore()) {
                    rows.add(firehose.nextRow());
                } 
            }
            PrefetchableTextFilesFirehoseFactoryTest.assertResult(rows);
            PrefetchableTextFilesFirehoseFactoryTest.assertNumRemainingCacheFiles(firehoseTmpDir, 2);
        }
    }

    static class TestPrefetchableTextFilesFirehoseFactory extends PrefetchableTextFilesFirehoseFactory<File> {
        private final long sleepMillis;

        private final File baseDir;

        private int numOpenExceptions;

        private int maxConnectionResets;

        static PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory with(File baseDir, long cacheCapacity, long fetchCapacity) {
            return // fetch timeout
            new PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory(baseDir, 1024, cacheCapacity, fetchCapacity, 60000, 3, 0, 0, 0);
        }

        static PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory of(File baseDir) {
            return new PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory(baseDir, 1024, 2048, 2048, 3, 0, 0, 0);
        }

        static PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory withOpenExceptions(File baseDir, int count) {
            return new PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory(baseDir, 1024, 2048, 2048, 3, count, 0, 0);
        }

        static PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory withConnectionResets(File baseDir, long cacheCapacity, long fetchCapacity, int numConnectionResets) {
            return new PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory(baseDir, (fetchCapacity / 2), cacheCapacity, fetchCapacity, 3, 0, numConnectionResets, 0);
        }

        static PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory withSleepMillis(File baseDir, long ms) {
            return new PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory(baseDir, 1024, 2048, 2048, 100, 3, 0, 0, ms);
        }

        private static long computeTimeout(int maxRetry) {
            // See RetryUtils.nextRetrySleepMillis()
            final double maxFuzzyMultiplier = 2.0;
            return ((long) (Math.min(MAX_SLEEP_MILLIS, (((RetryUtils.BASE_SLEEP_MILLIS) * (Math.pow(2, (maxRetry - 1)))) * maxFuzzyMultiplier))));
        }

        TestPrefetchableTextFilesFirehoseFactory(File baseDir, long prefetchTriggerThreshold, long maxCacheCapacityBytes, long maxFetchCapacityBytes, int maxRetry, int numOpenExceptions, int numConnectionResets, long sleepMillis) {
            this(baseDir, prefetchTriggerThreshold, maxCacheCapacityBytes, maxFetchCapacityBytes, PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.computeTimeout(maxRetry), maxRetry, numOpenExceptions, numConnectionResets, sleepMillis);
        }

        TestPrefetchableTextFilesFirehoseFactory(File baseDir, long prefetchTriggerThreshold, long maxCacheCapacityBytes, long maxFetchCapacityBytes, long fetchTimeout, int maxRetry, int numOpenExceptions, int maxConnectionResets, long sleepMillis) {
            super(maxCacheCapacityBytes, maxFetchCapacityBytes, prefetchTriggerThreshold, fetchTimeout, maxRetry);
            this.numOpenExceptions = numOpenExceptions;
            this.maxConnectionResets = maxConnectionResets;
            this.sleepMillis = sleepMillis;
            this.baseDir = baseDir;
        }

        @Override
        protected Collection<File> initObjects() {
            return FileUtils.listFiles(Preconditions.checkNotNull(baseDir).getAbsoluteFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        }

        @Override
        protected InputStream openObjectStream(File object) throws IOException {
            if ((numOpenExceptions) > 0) {
                (numOpenExceptions)--;
                throw new IOException("Exception for retry test");
            }
            if ((sleepMillis) > 0) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return (maxConnectionResets) > 0 ? new PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.TestInputStream(FileUtils.openInputStream(object), maxConnectionResets) : FileUtils.openInputStream(object);
        }

        @Override
        protected InputStream wrapObjectStream(File object, InputStream stream) {
            return stream;
        }

        @Override
        protected Predicate<Throwable> getRetryCondition() {
            return ( e) -> e instanceof IOException;
        }

        @Override
        protected InputStream openObjectStream(File object, long start) throws IOException {
            if ((numOpenExceptions) > 0) {
                (numOpenExceptions)--;
                throw new IOException("Exception for retry test");
            }
            if ((sleepMillis) > 0) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            final InputStream in = FileUtils.openInputStream(object);
            in.skip(start);
            return (maxConnectionResets) > 0 ? new PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.TestInputStream(in, maxConnectionResets) : in;
        }

        private int readCount;

        private int numConnectionResets;

        @Override
        public FiniteFirehoseFactory<StringInputRowParser, File> withSplit(InputSplit<File> split) {
            throw new UnsupportedOperationException();
        }

        private class TestInputStream extends InputStream {
            private static final int NUM_READ_COUNTS_BEFORE_ERROR = 10;

            private final InputStream delegate;

            private final int maxConnectionResets;

            TestInputStream(InputStream delegate, int maxConnectionResets) {
                this.delegate = delegate;
                this.maxConnectionResets = maxConnectionResets;
            }

            @Override
            public int read() throws IOException {
                if ((((readCount)++) % (PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.TestInputStream.NUM_READ_COUNTS_BEFORE_ERROR)) == 0) {
                    if (((numConnectionResets)++) < (maxConnectionResets)) {
                        // Simulate connection reset
                        throw new SocketException("Test Connection reset");
                    }
                }
                return delegate.read();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if ((((readCount)++) % (PrefetchableTextFilesFirehoseFactoryTest.TestPrefetchableTextFilesFirehoseFactory.TestInputStream.NUM_READ_COUNTS_BEFORE_ERROR)) == 0) {
                    if (((numConnectionResets)++) < (maxConnectionResets)) {
                        // Simulate connection reset
                        throw new SocketException("Test Connection reset");
                    }
                }
                return delegate.read(b, off, len);
            }
        }
    }
}


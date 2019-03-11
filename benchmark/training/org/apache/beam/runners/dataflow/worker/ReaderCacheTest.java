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


import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.beam.sdk.io.UnboundedSource;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Stopwatch;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link ReaderCache}.
 */
@RunWith(JUnit4.class)
public class ReaderCacheTest {
    private static final String C_ID = "computationId";

    private static final String C_ID_1 = "computationId_1";

    private static final ByteString KEY_1 = ByteString.copyFromUtf8("0000000000000001");

    private static final ByteString KEY_2 = ByteString.copyFromUtf8("0000000000000002");

    private static final ByteString KEY_3 = ByteString.copyFromUtf8("0000000000000003");

    private ReaderCache readerCache = null;

    @Mock
    private UnboundedSource.UnboundedReader<?> reader1;

    @Mock
    private UnboundedSource.UnboundedReader<?> reader2;

    @Mock
    private UnboundedSource.UnboundedReader<?> reader3;

    @Test
    public void testReaderCache() throws IOException {
        // Test basic caching expectations
        readerCache.cacheReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_1, 1, reader1);
        readerCache.cacheReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_2, 2, reader2);
        Assert.assertEquals(reader1, readerCache.acquireReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_1, 1));
        // Trying to override existing reader should throw
        try {
            readerCache.cacheReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_2, 2, reader1);
            Assert.fail("Exception should have been thrown");
        } catch (RuntimeException expected) {
            // expected
        }
        // And it should not have overwritten the old value
        Assert.assertEquals(reader2, readerCache.acquireReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_2, 2));
        Assert.assertNull("acquireReader() should remove matching entry", readerCache.acquireReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_2, 2));
        // Make sure computationId is part of the key
        readerCache.cacheReader(ReaderCacheTest.C_ID_1, ReaderCacheTest.KEY_1, 1, reader2);
        Assert.assertEquals(reader2, readerCache.acquireReader(ReaderCacheTest.C_ID_1, ReaderCacheTest.KEY_1, 1));
    }

    @Test
    public void testInvalidation() throws IOException {
        readerCache.cacheReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_1, 1, reader1);
        readerCache.cacheReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_2, 2, reader2);
        readerCache.cacheReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_3, 2, reader3);
        readerCache.invalidateReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_1);
        Mockito.verify(reader1).close();
        // Verify IOException during close is not thrown (it is logged).
        Mockito.doThrow(new IOException("swallowed")).when(reader2).close();
        readerCache.invalidateReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_2);
        // Make sure a runtime exception is not swallowed
        Mockito.doThrow(new RuntimeException("expected")).when(reader3).close();
        try {
            readerCache.invalidateReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_3);
            Assert.fail("Exception should have been thrown");
        } catch (RuntimeException e) {
            Assert.assertEquals("expected", e.getMessage());
        }
    }

    @Test
    public void testReaderCacheExpiration() throws IOException, InterruptedException {
        // Make sure cache closes the reader soon after expiry (as long as readerCache is accessed).
        Duration cacheDuration = Duration.millis(10);
        // Create a cache with short expiry period.
        ReaderCache readerCache = new ReaderCache(cacheDuration);
        readerCache.cacheReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_1, 1, reader1);
        readerCache.cacheReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_2, 2, reader2);
        Stopwatch stopwatch = Stopwatch.createStarted();
        while ((stopwatch.elapsed(TimeUnit.MILLISECONDS)) < (2 * (cacheDuration.getMillis()))) {
            Thread.sleep(1);
        } 
        // Trigger clean up with a lookup, non-existing key is ok.
        readerCache.acquireReader(ReaderCacheTest.C_ID, ReaderCacheTest.KEY_3, 3);
        Mockito.verify(reader1).close();
    }
}


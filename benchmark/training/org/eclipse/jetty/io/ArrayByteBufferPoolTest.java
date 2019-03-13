/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.io;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import org.eclipse.jetty.io.ByteBufferPool.Bucket;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ArrayByteBufferPoolTest {
    @Test
    public void testMinimumRelease() {
        ArrayByteBufferPool bufferPool = new ArrayByteBufferPool(10, 100, 1000);
        ByteBufferPool[] buckets = bufferPool.bucketsFor(true);
        for (int size = 1; size <= 9; size++) {
            ByteBuffer buffer = bufferPool.acquire(size, true);
            Assertions.assertTrue(buffer.isDirect());
            Assertions.assertEquals(size, buffer.capacity());
            for (ByteBufferPool.Bucket bucket : buckets) {
                if (bucket != null)
                    Assertions.assertTrue(bucket.isEmpty());

            }
            bufferPool.release(buffer);
            for (ByteBufferPool.Bucket bucket : buckets) {
                if (bucket != null)
                    Assertions.assertTrue(bucket.isEmpty());

            }
        }
    }

    @Test
    public void testMaxRelease() {
        ArrayByteBufferPool bufferPool = new ArrayByteBufferPool(10, 100, 1000);
        ByteBufferPool[] buckets = bufferPool.bucketsFor(true);
        for (int size = 999; size <= 1001; size++) {
            bufferPool.clear();
            ByteBuffer buffer = bufferPool.acquire(size, true);
            Assertions.assertTrue(buffer.isDirect());
            MatcherAssert.assertThat(buffer.capacity(), Matchers.greaterThanOrEqualTo(size));
            for (ByteBufferPool.Bucket bucket : buckets) {
                if (bucket != null)
                    Assertions.assertTrue(bucket.isEmpty());

            }
            bufferPool.release(buffer);
            int pooled = Arrays.stream(buckets).filter(Objects::nonNull).mapToInt(Bucket::size).sum();
            Assertions.assertEquals((size <= 1000), (1 == pooled));
        }
    }

    @Test
    public void testAcquireRelease() {
        ArrayByteBufferPool bufferPool = new ArrayByteBufferPool(10, 100, 1000);
        ByteBufferPool[] buckets = bufferPool.bucketsFor(true);
        for (int size = 390; size <= 510; size++) {
            bufferPool.clear();
            ByteBuffer buffer = bufferPool.acquire(size, true);
            Assertions.assertTrue(buffer.isDirect());
            MatcherAssert.assertThat(buffer.capacity(), Matchers.greaterThanOrEqualTo(size));
            for (ByteBufferPool.Bucket bucket : buckets) {
                if (bucket != null)
                    Assertions.assertTrue(bucket.isEmpty());

            }
            bufferPool.release(buffer);
            int pooled = Arrays.stream(buckets).filter(Objects::nonNull).mapToInt(Bucket::size).sum();
            Assertions.assertEquals(1, pooled);
        }
    }

    @Test
    public void testAcquireReleaseAcquire() {
        ArrayByteBufferPool bufferPool = new ArrayByteBufferPool(10, 100, 1000);
        ByteBufferPool[] buckets = bufferPool.bucketsFor(true);
        for (int size = 390; size <= 510; size++) {
            bufferPool.clear();
            ByteBuffer buffer1 = bufferPool.acquire(size, true);
            bufferPool.release(buffer1);
            ByteBuffer buffer2 = bufferPool.acquire(size, true);
            bufferPool.release(buffer2);
            ByteBuffer buffer3 = bufferPool.acquire(size, false);
            bufferPool.release(buffer3);
            int pooled = Arrays.stream(buckets).filter(Objects::nonNull).mapToInt(Bucket::size).sum();
            Assertions.assertEquals(1, pooled);
            Assertions.assertSame(buffer1, buffer2);
            Assertions.assertNotSame(buffer1, buffer3);
        }
    }

    @Test
    public void testMaxQueue() {
        ArrayByteBufferPool bufferPool = new ArrayByteBufferPool((-1), (-1), (-1), 2);
        ByteBuffer buffer1 = bufferPool.acquire(512, false);
        ByteBuffer buffer2 = bufferPool.acquire(512, false);
        ByteBuffer buffer3 = bufferPool.acquire(512, false);
        Bucket[] buckets = bufferPool.bucketsFor(false);
        Arrays.stream(buckets).filter(Objects::nonNull).forEach(( b) -> assertEquals(0, b.size()));
        bufferPool.release(buffer1);
        Bucket bucket = Arrays.stream(buckets).filter(Objects::nonNull).filter(( b) -> (b.size()) > 0).findFirst().orElseThrow(AssertionError::new);
        Assertions.assertEquals(1, bucket.size());
        bufferPool.release(buffer2);
        Assertions.assertEquals(2, bucket.size());
        bufferPool.release(buffer3);
        Assertions.assertEquals(2, bucket.size());
    }

    @Test
    public void testMaxMemory() {
        int factor = 1024;
        int maxMemory = 11 * 1024;
        ArrayByteBufferPool bufferPool = new ArrayByteBufferPool((-1), factor, (-1), (-1), (-1), maxMemory);
        Bucket[] buckets = bufferPool.bucketsFor(true);
        // Create the buckets - the oldest is the larger.
        // 1+2+3+4=10 / maxMemory=11.
        for (int i = 4; i >= 1; --i) {
            int capacity = factor * i;
            ByteBuffer buffer = bufferPool.acquire(capacity, true);
            bufferPool.release(buffer);
        }
        // Create and release a buffer to exceed the max memory.
        ByteBuffer buffer = bufferPool.newByteBuffer((2 * factor), true);
        bufferPool.release(buffer);
        // Now the oldest buffer should be gone and we have: 1+2x2+3=8
        long memory = bufferPool.getMemory(true);
        MatcherAssert.assertThat(memory, Matchers.lessThan(((long) (maxMemory))));
        Assertions.assertNull(buckets[3]);
        // Create and release a large buffer.
        // Max memory is exceeded and buckets 3 and 1 are cleared.
        // We will have 2x2+7=11.
        buffer = bufferPool.newByteBuffer((7 * factor), true);
        bufferPool.release(buffer);
        memory = bufferPool.getMemory(true);
        MatcherAssert.assertThat(memory, Matchers.lessThanOrEqualTo(((long) (maxMemory))));
        Assertions.assertNull(buckets[0]);
        Assertions.assertNull(buckets[2]);
    }
}


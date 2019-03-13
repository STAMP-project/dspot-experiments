/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state.internals;


import java.nio.ByteBuffer;
import org.apache.kafka.common.utils.Bytes;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


// TODO: this test coverage does not consider session serde yet
public class SegmentedCacheFunctionTest {
    private static final int SEGMENT_INTERVAL = 17;

    private static final int TIMESTAMP = 736213517;

    private static final Bytes THE_KEY = WindowKeySchema.toStoreKeyBinary(new byte[]{ 10, 11, 12 }, SegmentedCacheFunctionTest.TIMESTAMP, 42);

    private static final Bytes THE_CACHE_KEY = Bytes.wrap(ByteBuffer.allocate((8 + (SegmentedCacheFunctionTest.THE_KEY.get().length))).putLong(((SegmentedCacheFunctionTest.TIMESTAMP) / (SegmentedCacheFunctionTest.SEGMENT_INTERVAL))).put(SegmentedCacheFunctionTest.THE_KEY.get()).array());

    private final SegmentedCacheFunction cacheFunction = new SegmentedCacheFunction(new WindowKeySchema(), SegmentedCacheFunctionTest.SEGMENT_INTERVAL);

    @Test
    public void key() {
        MatcherAssert.assertThat(cacheFunction.key(SegmentedCacheFunctionTest.THE_CACHE_KEY), IsEqual.equalTo(SegmentedCacheFunctionTest.THE_KEY));
    }

    @Test
    public void cacheKey() {
        final long segmentId = (SegmentedCacheFunctionTest.TIMESTAMP) / (SegmentedCacheFunctionTest.SEGMENT_INTERVAL);
        final Bytes actualCacheKey = cacheFunction.cacheKey(SegmentedCacheFunctionTest.THE_KEY);
        final ByteBuffer buffer = ByteBuffer.wrap(actualCacheKey.get());
        MatcherAssert.assertThat(buffer.getLong(), IsEqual.equalTo(segmentId));
        final byte[] actualKey = new byte[buffer.remaining()];
        buffer.get(actualKey);
        MatcherAssert.assertThat(Bytes.wrap(actualKey), IsEqual.equalTo(SegmentedCacheFunctionTest.THE_KEY));
    }

    @Test
    public void testRoundTripping() {
        MatcherAssert.assertThat(cacheFunction.key(cacheFunction.cacheKey(SegmentedCacheFunctionTest.THE_KEY)), IsEqual.equalTo(SegmentedCacheFunctionTest.THE_KEY));
        MatcherAssert.assertThat(cacheFunction.cacheKey(cacheFunction.key(SegmentedCacheFunctionTest.THE_CACHE_KEY)), IsEqual.equalTo(SegmentedCacheFunctionTest.THE_CACHE_KEY));
    }

    @Test
    public void compareSegmentedKeys() {
        MatcherAssert.assertThat("same key in same segment should be ranked the same", ((cacheFunction.compareSegmentedKeys(cacheFunction.cacheKey(SegmentedCacheFunctionTest.THE_KEY), SegmentedCacheFunctionTest.THE_KEY)) == 0));
        final Bytes sameKeyInPriorSegment = WindowKeySchema.toStoreKeyBinary(new byte[]{ 10, 11, 12 }, 1234, 42);
        MatcherAssert.assertThat("same keys in different segments should be ordered according to segment", ((cacheFunction.compareSegmentedKeys(cacheFunction.cacheKey(sameKeyInPriorSegment), SegmentedCacheFunctionTest.THE_KEY)) < 0));
        MatcherAssert.assertThat("same keys in different segments should be ordered according to segment", ((cacheFunction.compareSegmentedKeys(cacheFunction.cacheKey(SegmentedCacheFunctionTest.THE_KEY), sameKeyInPriorSegment)) > 0));
        final Bytes lowerKeyInSameSegment = WindowKeySchema.toStoreKeyBinary(new byte[]{ 10, 11, 11 }, ((SegmentedCacheFunctionTest.TIMESTAMP) - 1), 0);
        MatcherAssert.assertThat("different keys in same segments should be ordered according to key", ((cacheFunction.compareSegmentedKeys(cacheFunction.cacheKey(SegmentedCacheFunctionTest.THE_KEY), lowerKeyInSameSegment)) > 0));
        MatcherAssert.assertThat("different keys in same segments should be ordered according to key", ((cacheFunction.compareSegmentedKeys(cacheFunction.cacheKey(lowerKeyInSameSegment), SegmentedCacheFunctionTest.THE_KEY)) < 0));
    }
}


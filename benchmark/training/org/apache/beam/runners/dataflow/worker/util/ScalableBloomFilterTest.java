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
package org.apache.beam.runners.dataflow.worker.util;


import java.nio.ByteBuffer;
import org.apache.beam.runners.dataflow.worker.util.ScalableBloomFilter.Builder;
import org.apache.beam.runners.dataflow.worker.util.ScalableBloomFilter.ScalableBloomFilterCoder;
import org.apache.beam.sdk.testing.CoderProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ScalableBloomFilter}.
 */
@RunWith(JUnit4.class)
public class ScalableBloomFilterTest {
    private static final int MAX_SIZE = 10000;

    private static final ByteBuffer BUFFER = ByteBuffer.wrap(new byte[]{ 1, 2 });

    @Test
    public void testBuilder() throws Exception {
        Builder builder = ScalableBloomFilter.withMaximumSizeBytes(ScalableBloomFilterTest.MAX_SIZE);
        Assert.assertTrue("Expected Bloom filter to have been modified.", builder.put(ScalableBloomFilterTest.BUFFER));
        // Re-adding should skip and not record the insertion.
        Assert.assertFalse("Expected Bloom filter to not have been modified.", builder.put(ScalableBloomFilterTest.BUFFER));
        // Verify insertion
        int maxValue = insertAndVerifyContents(builder, 31);
        // Verify that the decoded value contains all the values and that it is much smaller
        // than the maximum size.
        verifyCoder(builder.build(), maxValue, ((ScalableBloomFilterTest.MAX_SIZE) / 50));
    }

    @Test
    public void testBuilderWithMaxSize() throws Exception {
        Builder builder = ScalableBloomFilter.withMaximumSizeBytes(ScalableBloomFilterTest.MAX_SIZE);
        int maxValue = insertAndVerifyContents(builder, ((int) ((ScalableBloomFilterTest.MAX_SIZE) * 1.1)));
        ScalableBloomFilter bloomFilter = builder.build();
        // Verify that the decoded value contains all the values and that it is much smaller
        // than the maximum size.
        verifyCoder(bloomFilter, maxValue, ScalableBloomFilterTest.MAX_SIZE);
    }

    @Test
    public void testScalableBloomFilterCoder() throws Exception {
        Builder builderA = ScalableBloomFilter.withMaximumNumberOfInsertionsForOptimalBloomFilter(16);
        builderA.put(ScalableBloomFilterTest.BUFFER);
        ScalableBloomFilter filterA = builderA.build();
        Builder builderB = ScalableBloomFilter.withMaximumNumberOfInsertionsForOptimalBloomFilter(16);
        builderB.put(ScalableBloomFilterTest.BUFFER);
        ScalableBloomFilter filterB = builderB.build();
        CoderProperties.coderDecodeEncodeEqual(ScalableBloomFilterCoder.of(), filterA);
        CoderProperties.coderDeterministic(ScalableBloomFilterCoder.of(), filterA, filterB);
        CoderProperties.coderConsistentWithEquals(ScalableBloomFilterCoder.of(), filterA, filterB);
        CoderProperties.coderSerializable(ScalableBloomFilterCoder.of());
        CoderProperties.structuralValueConsistentWithEquals(ScalableBloomFilterCoder.of(), filterA, filterB);
    }
}


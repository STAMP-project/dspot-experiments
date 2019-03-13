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
package org.apache.beam.sdk.testing;


import java.util.List;
import java.util.Set;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.BoundedSource.BoundedReader;
import org.apache.beam.sdk.io.CountingSource;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SourceTestUtils}.
 */
@RunWith(JUnit4.class)
public class SourceTestUtilsTest {
    @Test
    public void testToUnsplittableSource() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        BoundedSource<Long> baseSource = CountingSource.upTo(100);
        BoundedSource<Long> unsplittableSource = SourceTestUtils.toUnsplittableSource(baseSource);
        List<?> splits = unsplittableSource.split(1, options);
        Assert.assertEquals(1, splits.size());
        Assert.assertEquals(unsplittableSource, splits.get(0));
        BoundedReader<Long> unsplittableReader = unsplittableSource.createReader(options);
        Assert.assertEquals(0, unsplittableReader.getFractionConsumed(), 1.0E-15);
        Set<Long> expected = Sets.newHashSet(SourceTestUtils.readFromSource(baseSource, options));
        Set<Long> actual = Sets.newHashSet();
        actual.addAll(SourceTestUtils.readNItemsFromUnstartedReader(unsplittableReader, 40));
        Assert.assertNull(unsplittableReader.splitAtFraction(0.5));
        actual.addAll(/* started */
        SourceTestUtils.readRemainingFromReader(unsplittableReader, true));
        Assert.assertEquals(1, unsplittableReader.getFractionConsumed(), 1.0E-15);
        Assert.assertEquals(100, actual.size());
        Assert.assertEquals(Sets.newHashSet(expected), Sets.newHashSet(actual));
    }
}


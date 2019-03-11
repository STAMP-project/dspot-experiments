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
package org.apache.flink.api.java.summarize.aggregation;


import org.apache.flink.api.java.summarize.StringColumnSummary;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link StringSummaryAggregator}.
 */
public class StringSummaryAggregatorTest {
    @Test
    public void testMixedGroup() {
        StringColumnSummary summary = summarize("abc", "", null, "  ", "defghi", "foo", null, null, "", " ");
        Assert.assertEquals(10, summary.getTotalCount());
        Assert.assertEquals(3, summary.getNullCount());
        Assert.assertEquals(7, summary.getNonNullCount());
        Assert.assertEquals(2, summary.getEmptyCount());
        Assert.assertEquals(0, summary.getMinLength().intValue());
        Assert.assertEquals(6, summary.getMaxLength().intValue());
        Assert.assertEquals(2.142857, summary.getMeanLength().doubleValue(), 0.001);
    }

    @Test
    public void testAllNullStrings() {
        StringColumnSummary summary = summarize(null, null, null, null);
        Assert.assertEquals(4, summary.getTotalCount());
        Assert.assertEquals(4, summary.getNullCount());
        Assert.assertEquals(0, summary.getNonNullCount());
        Assert.assertEquals(0, summary.getEmptyCount());
        Assert.assertNull(summary.getMinLength());
        Assert.assertNull(summary.getMaxLength());
        Assert.assertNull(summary.getMeanLength());
    }

    @Test
    public void testAllWithValues() {
        StringColumnSummary summary = summarize("cat", "hat", "dog", "frog");
        Assert.assertEquals(4, summary.getTotalCount());
        Assert.assertEquals(0, summary.getNullCount());
        Assert.assertEquals(4, summary.getNonNullCount());
        Assert.assertEquals(0, summary.getEmptyCount());
        Assert.assertEquals(3, summary.getMinLength().intValue());
        Assert.assertEquals(4, summary.getMaxLength().intValue());
        Assert.assertEquals(3.25, summary.getMeanLength().doubleValue(), 0.0);
    }
}


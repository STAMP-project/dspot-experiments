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
package org.apache.druid.query.aggregation;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class HistogramAggregatorTest {
    @Test
    public void testSerde() throws Exception {
        final ObjectMapper objectMapper = TestHelper.makeJsonMapper();
        String json0 = "{\"type\": \"histogram\", \"name\": \"billy\", \"fieldName\": \"nilly\"}";
        HistogramAggregatorFactory agg0 = objectMapper.readValue(json0, HistogramAggregatorFactory.class);
        Assert.assertEquals(ImmutableList.of(), agg0.getBreaks());
        String aggSpecJson = "{\"type\": \"histogram\", \"name\": \"billy\", \"fieldName\": \"nilly\", \"breaks\": [ -1, 2, 3.0 ]}";
        HistogramAggregatorFactory agg = objectMapper.readValue(aggSpecJson, HistogramAggregatorFactory.class);
        Assert.assertEquals(new HistogramAggregatorFactory("billy", "nilly", Arrays.asList((-1.0F), 2.0F, 3.0F)), agg);
        Assert.assertEquals(agg, objectMapper.readValue(objectMapper.writeValueAsBytes(agg), HistogramAggregatorFactory.class));
    }

    @Test
    public void testAggregate() {
        final float[] values = new float[]{ 0.55F, 0.27F, -0.3F, -0.1F, -0.8F, -0.7F, -0.5F, 0.25F, 0.1F, 2.0F, -3.0F };
        final float[] breaks = new float[]{ -1.0F, -0.5F, 0.0F, 0.5F, 1.0F };
        final TestFloatColumnSelector selector = new TestFloatColumnSelector(values);
        HistogramAggregator agg = new HistogramAggregator(selector, breaks);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 0, 0 }, ((Histogram) (agg.get())).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 0, 0 }, ((Histogram) (agg.get())).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 0, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 1, 0 }, ((Histogram) (agg.get())).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 1, 0 }, ((Histogram) (agg.get())).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 1, 1, 0 }, ((Histogram) (agg.get())).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 1, 1, 0 }, ((Histogram) (agg.get())).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 1, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 0, 1, 1, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 0, 2, 1, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 1, 2, 1, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 2, 2, 1, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 3, 2, 1, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 3, 2, 2, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 3, 2, 3, 1, 0 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 0, 3, 2, 3, 1, 1 }, ((Histogram) (agg.get())).bins);
        aggregate(selector, agg);
        Assert.assertArrayEquals(new long[]{ 1, 3, 2, 3, 1, 1 }, ((Histogram) (agg.get())).bins);
    }

    @Test
    public void testBufferAggregate() {
        final float[] values = new float[]{ 0.55F, 0.27F, -0.3F, -0.1F, -0.8F, -0.7F, -0.5F, 0.25F, 0.1F, 2.0F, -3.0F };
        final float[] breaks = new float[]{ -1.0F, -0.5F, 0.0F, 0.5F, 1.0F };
        final TestFloatColumnSelector selector = new TestFloatColumnSelector(values);
        HistogramAggregatorFactory factory = new HistogramAggregatorFactory("billy", "billy", Floats.asList(breaks));
        HistogramBufferAggregator agg = new HistogramBufferAggregator(selector, breaks);
        ByteBuffer buf = ByteBuffer.allocateDirect(factory.getMaxIntermediateSizeWithNulls());
        int position = 0;
        agg.init(buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 0, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 0, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 0, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 1, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 1, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 1, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 0, 1, 1, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 0, 2, 1, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 1, 2, 1, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 2, 2, 1, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 3, 2, 1, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 3, 2, 2, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 3, 2, 3, 1, 0 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 0, 3, 2, 3, 1, 1 }, ((Histogram) (agg.get(buf, position))).bins);
        aggregateBuffer(selector, agg, buf, position);
        Assert.assertArrayEquals(new long[]{ 1, 3, 2, 3, 1, 1 }, ((Histogram) (agg.get(buf, position))).bins);
    }
}


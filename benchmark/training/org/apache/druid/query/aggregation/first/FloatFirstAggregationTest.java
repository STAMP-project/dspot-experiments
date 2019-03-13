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
package org.apache.druid.query.aggregation.first;


import java.nio.ByteBuffer;
import org.apache.druid.collections.SerializablePair;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.query.aggregation.Aggregator;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.BufferAggregator;
import org.apache.druid.query.aggregation.TestFloatColumnSelector;
import org.apache.druid.query.aggregation.TestLongColumnSelector;
import org.apache.druid.query.aggregation.TestObjectColumnSelector;
import org.apache.druid.segment.ColumnSelectorFactory;
import org.junit.Assert;
import org.junit.Test;


public class FloatFirstAggregationTest {
    private FloatFirstAggregatorFactory floatFirstAggregatorFactory;

    private FloatFirstAggregatorFactory combiningAggFactory;

    private ColumnSelectorFactory colSelectorFactory;

    private TestLongColumnSelector timeSelector;

    private TestFloatColumnSelector valueSelector;

    private TestObjectColumnSelector objectSelector;

    private float[] floats = new float[]{ 1.1F, 2.7F, 3.5F, 1.3F };

    private long[] times = new long[]{ 12, 10, 5344, 7899999 };

    private SerializablePair[] pairs = new SerializablePair[]{ new SerializablePair(1467225096L, 134.3F), new SerializablePair(23163L, 1232.212F), new SerializablePair(742L, 18.0F), new SerializablePair(111111L, 233.5232F) };

    @Test
    public void testDoubleFirstAggregator() {
        Aggregator agg = floatFirstAggregatorFactory.factorize(colSelectorFactory);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        Pair<Long, Float> result = ((Pair<Long, Float>) (agg.get()));
        Assert.assertEquals(times[1], result.lhs.longValue());
        Assert.assertEquals(floats[1], result.rhs, 1.0E-4);
        Assert.assertEquals(((long) (floats[1])), agg.getLong());
        Assert.assertEquals(floats[1], agg.getFloat(), 1.0E-4);
    }

    @Test
    public void testDoubleFirstBufferAggregator() {
        BufferAggregator agg = floatFirstAggregatorFactory.factorizeBuffered(colSelectorFactory);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[floatFirstAggregatorFactory.getMaxIntermediateSizeWithNulls()]);
        agg.init(buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        Pair<Long, Float> result = ((Pair<Long, Float>) (agg.get(buffer, 0)));
        Assert.assertEquals(times[1], result.lhs.longValue());
        Assert.assertEquals(floats[1], result.rhs, 1.0E-4);
        Assert.assertEquals(((long) (floats[1])), agg.getLong(buffer, 0));
        Assert.assertEquals(floats[1], agg.getFloat(buffer, 0), 1.0E-4);
    }

    @Test
    public void testCombine() {
        SerializablePair pair1 = new SerializablePair(1467225000L, 3.621);
        SerializablePair pair2 = new SerializablePair(1467240000L, 785.4);
        Assert.assertEquals(pair1, floatFirstAggregatorFactory.combine(pair1, pair2));
    }

    @Test
    public void testDoubleFirstCombiningAggregator() {
        Aggregator agg = combiningAggFactory.factorize(colSelectorFactory);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        Pair<Long, Float> result = ((Pair<Long, Float>) (agg.get()));
        Pair<Long, Float> expected = ((Pair<Long, Float>) (pairs[2]));
        Assert.assertEquals(expected.lhs, result.lhs);
        Assert.assertEquals(expected.rhs, result.rhs, 1.0E-4);
        Assert.assertEquals(expected.rhs.longValue(), agg.getLong());
        Assert.assertEquals(expected.rhs, agg.getFloat(), 1.0E-4);
    }

    @Test
    public void testDoubleFirstCombiningBufferAggregator() {
        BufferAggregator agg = combiningAggFactory.factorizeBuffered(colSelectorFactory);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[floatFirstAggregatorFactory.getMaxIntermediateSizeWithNulls()]);
        agg.init(buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        Pair<Long, Float> result = ((Pair<Long, Float>) (agg.get(buffer, 0)));
        Pair<Long, Float> expected = ((Pair<Long, Float>) (pairs[2]));
        Assert.assertEquals(expected.lhs, result.lhs);
        Assert.assertEquals(expected.rhs, result.rhs, 1.0E-4);
        Assert.assertEquals(expected.rhs.longValue(), agg.getLong(buffer, 0));
        Assert.assertEquals(expected.rhs, agg.getFloat(buffer, 0), 1.0E-4);
    }

    @Test
    public void testSerde() throws Exception {
        DefaultObjectMapper mapper = new DefaultObjectMapper();
        String doubleSpecJson = "{\"type\":\"floatFirst\",\"name\":\"billy\",\"fieldName\":\"nilly\"}";
        Assert.assertEquals(floatFirstAggregatorFactory, mapper.readValue(doubleSpecJson, AggregatorFactory.class));
    }
}


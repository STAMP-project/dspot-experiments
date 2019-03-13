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
package org.apache.druid.query.aggregation.last;


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


public class FloatLastAggregationTest {
    private FloatLastAggregatorFactory floatLastAggregatorFactory;

    private FloatLastAggregatorFactory combiningAggFactory;

    private ColumnSelectorFactory colSelectorFactory;

    private TestLongColumnSelector timeSelector;

    private TestFloatColumnSelector valueSelector;

    private TestObjectColumnSelector objectSelector;

    private float[] floats = new float[]{ 1.1897F, 0.001F, 86.23F, 166.228F };

    private long[] times = new long[]{ 8224, 6879, 2436, 7888 };

    private SerializablePair[] pairs = new SerializablePair[]{ new SerializablePair(52782L, 134.3F), new SerializablePair(65492L, 1232.212F), new SerializablePair(69134L, 18.1233F), new SerializablePair(11111L, 233.5232F) };

    @Test
    public void testDoubleLastAggregator() {
        Aggregator agg = floatLastAggregatorFactory.factorize(colSelectorFactory);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        aggregate(agg);
        Pair<Long, Float> result = ((Pair<Long, Float>) (agg.get()));
        Assert.assertEquals(times[0], result.lhs.longValue());
        Assert.assertEquals(floats[0], result.rhs, 1.0E-4);
        Assert.assertEquals(((long) (floats[0])), agg.getLong());
        Assert.assertEquals(floats[0], agg.getFloat(), 1.0E-4);
    }

    @Test
    public void testDoubleLastBufferAggregator() {
        BufferAggregator agg = floatLastAggregatorFactory.factorizeBuffered(colSelectorFactory);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[floatLastAggregatorFactory.getMaxIntermediateSizeWithNulls()]);
        agg.init(buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        aggregate(agg, buffer, 0);
        Pair<Long, Float> result = ((Pair<Long, Float>) (agg.get(buffer, 0)));
        Assert.assertEquals(times[0], result.lhs.longValue());
        Assert.assertEquals(floats[0], result.rhs, 1.0E-4);
        Assert.assertEquals(((long) (floats[0])), agg.getLong(buffer, 0));
        Assert.assertEquals(floats[0], agg.getFloat(buffer, 0), 1.0E-4);
    }

    @Test
    public void testCombine() {
        SerializablePair pair1 = new SerializablePair(1467225000L, 3.621);
        SerializablePair pair2 = new SerializablePair(1467240000L, 785.4);
        Assert.assertEquals(pair2, floatLastAggregatorFactory.combine(pair1, pair2));
    }

    @Test
    public void testDoubleLastCombiningAggregator() {
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
    public void testDoubleLastCombiningBufferAggregator() {
        BufferAggregator agg = combiningAggFactory.factorizeBuffered(colSelectorFactory);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[floatLastAggregatorFactory.getMaxIntermediateSizeWithNulls()]);
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
        String doubleSpecJson = "{\"type\":\"floatLast\",\"name\":\"billy\",\"fieldName\":\"nilly\"}";
        Assert.assertEquals(floatLastAggregatorFactory, mapper.readValue(doubleSpecJson, AggregatorFactory.class));
    }
}


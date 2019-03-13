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
package org.apache.druid.query.aggregation.variance;


import java.nio.ByteBuffer;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.query.aggregation.TestFloatColumnSelector;
import org.apache.druid.segment.ColumnSelectorFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class VarianceAggregatorTest {
    private VarianceAggregatorFactory aggFactory;

    private ColumnSelectorFactory colSelectorFactory;

    private TestFloatColumnSelector selector;

    private final float[] values = new float[]{ 1.1F, 2.7F, 3.5F, 1.3F };

    private final double[] variances_pop = new double[values.length];// calculated


    private final double[] variances_samp = new double[values.length];// calculated


    public VarianceAggregatorTest() throws Exception {
        String aggSpecJson = "{\"type\": \"variance\", \"name\": \"billy\", \"fieldName\": \"nilly\"}";
        aggFactory = new DefaultObjectMapper().readValue(aggSpecJson, VarianceAggregatorFactory.class);
        double sum = 0;
        for (int i = 0; i < (values.length); i++) {
            sum += values[i];
            if (i > 0) {
                double mean = sum / (i + 1);
                double temp = 0;
                for (int j = 0; j <= i; j++) {
                    temp += Math.pow(((values[j]) - mean), 2);
                }
                variances_pop[i] = temp / (i + 1);
                variances_samp[i] = temp / i;
            }
        }
    }

    @Test
    public void testDoubleVarianceAggregator() {
        VarianceAggregator agg = ((VarianceAggregator) (aggFactory.factorize(colSelectorFactory)));
        assertValues(((VarianceAggregatorCollector) (agg.get())), 0, 0.0, 0.0);
        aggregate(selector, agg);
        assertValues(((VarianceAggregatorCollector) (agg.get())), 1, 1.1, 0.0);
        aggregate(selector, agg);
        assertValues(((VarianceAggregatorCollector) (agg.get())), 2, 3.8, 1.28);
        aggregate(selector, agg);
        assertValues(((VarianceAggregatorCollector) (agg.get())), 3, 7.3, 2.9866);
        aggregate(selector, agg);
        assertValues(((VarianceAggregatorCollector) (agg.get())), 4, 8.6, 3.95);
    }

    @Test
    public void testDoubleVarianceBufferAggregator() {
        VarianceBufferAggregator agg = ((VarianceBufferAggregator) (aggFactory.factorizeBuffered(colSelectorFactory)));
        ByteBuffer buffer = ByteBuffer.wrap(new byte[aggFactory.getMaxIntermediateSizeWithNulls()]);
        agg.init(buffer, 0);
        assertValues(((VarianceAggregatorCollector) (agg.get(buffer, 0))), 0, 0.0, 0.0);
        aggregate(selector, agg, buffer, 0);
        assertValues(((VarianceAggregatorCollector) (agg.get(buffer, 0))), 1, 1.1, 0.0);
        aggregate(selector, agg, buffer, 0);
        assertValues(((VarianceAggregatorCollector) (agg.get(buffer, 0))), 2, 3.8, 1.28);
        aggregate(selector, agg, buffer, 0);
        assertValues(((VarianceAggregatorCollector) (agg.get(buffer, 0))), 3, 7.3, 2.9866);
        aggregate(selector, agg, buffer, 0);
        assertValues(((VarianceAggregatorCollector) (agg.get(buffer, 0))), 4, 8.6, 3.95);
    }

    @Test
    public void testCombine() {
        VarianceAggregatorCollector holder1 = new VarianceAggregatorCollector().add(1.1F).add(2.7F);
        VarianceAggregatorCollector holder2 = new VarianceAggregatorCollector().add(3.5F).add(1.3F);
        VarianceAggregatorCollector expected = new VarianceAggregatorCollector(4, 8.6, 3.95);
        Assert.assertTrue(expected.equalsWithEpsilon(((VarianceAggregatorCollector) (aggFactory.combine(holder1, holder2))), 1.0E-5));
    }

    @Test
    public void testEqualsAndHashCode() {
        VarianceAggregatorFactory one = new VarianceAggregatorFactory("name1", "fieldName1");
        VarianceAggregatorFactory oneMore = new VarianceAggregatorFactory("name1", "fieldName1");
        VarianceAggregatorFactory two = new VarianceAggregatorFactory("name2", "fieldName2");
        Assert.assertEquals(one.hashCode(), oneMore.hashCode());
        Assert.assertTrue(one.equals(oneMore));
        Assert.assertFalse(one.equals(two));
    }
}


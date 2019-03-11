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
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import org.apache.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class HistogramTest {
    @Test
    public void testOffer() {
        final float[] values = new float[]{ 0.55F, 0.27F, -0.3F, -0.1F, -0.8F, -0.7F, -0.5F, 0.25F, 0.1F, 2.0F, -3.0F };
        final float[] breaks = new float[]{ -1.0F, -0.5F, 0.0F, 0.5F, 1.0F };
        Histogram hExpected = new Histogram(breaks, new long[]{ 1, 3, 2, 3, 1, 1 }, (-3.0F), 2.0F);
        Histogram h = new Histogram(breaks);
        for (float v : values) {
            h.offer(v);
        }
        Assert.assertEquals("histogram matches expected histogram", hExpected, h);
    }

    /**
     * This test differs from {@link #testOffer()} only in that it offers only negative values into Histogram. It's to
     * expose the issue of using Float's MIN_VALUE that is actually positive as initial value for {@link Histogram#max}.
     */
    @Test
    public void testOfferOnlyNegative() {
        final float[] values = new float[]{ -0.3F, -0.1F, -0.8F, -0.7F, -0.5F, -3.0F };
        final float[] breaks = new float[]{ -1.0F, -0.5F, 0.0F, 0.5F, 1.0F };
        Histogram hExpected = new Histogram(breaks, new long[]{ 1, 3, 2, 0, 0, 0 }, (-3.0F), (-0.1F));
        Histogram h = new Histogram(breaks);
        for (float v : values) {
            h.offer(v);
        }
        Assert.assertEquals("histogram matches expected histogram", hExpected, h);
    }

    @Test
    public void testToFromBytes() {
        float[] breaks = new float[]{ -1.0F, -0.5F, 0.0F, 0.5F, 1.0F };
        long[] bins = new long[]{ 23, 123, 4, 56, 7, 493210 };
        Histogram h = new Histogram(breaks, bins, (-1.0F), 1.0F);
        Assert.assertEquals(Histogram.fromBytes(h.toBytes()), h);
    }

    @Test
    public void testAsVisual() throws Exception {
        float[] breaks = new float[]{ -1.0F, -0.5F, 0.0F, 0.5F, 1.0F };
        long[] bins = new long[]{ 23, 123, 4, 56, 7, 493210 };
        Histogram h = new Histogram(breaks, bins, (-1.0F), 1.0F);
        Double[] visualBreaks = new Double[]{ -1.0, -0.5, 0.0, 0.5, 1.0 };
        Double[] visualCounts = new Double[]{ 123.0, 4.0, 56.0, 7.0 };
        ObjectMapper objectMapper = TestHelper.makeJsonMapper();
        String json = objectMapper.writeValueAsString(h.asVisual());
        Map<String, Object> expectedObj = Maps.newLinkedHashMap();
        expectedObj.put("breaks", Arrays.asList(visualBreaks));
        expectedObj.put("counts", Arrays.asList(visualCounts));
        expectedObj.put("quantiles", Arrays.asList(new Double[]{ -1.0, 1.0 }));
        Map<String, Object> obj = ((Map<String, Object>) (objectMapper.readValue(json, Object.class)));
        Assert.assertEquals(expectedObj, obj);
    }
}


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
package org.apache.druid.query.groupby.having;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.druid.data.input.MapBasedInputRow;
import org.apache.druid.data.input.Row;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import static HavingSpec.ALWAYS;
import static HavingSpec.NEVER;


public class HavingSpecTest {
    private static final Row ROW = new MapBasedInputRow(0, new ArrayList(), ImmutableMap.of("metric", Float.valueOf(10)));

    @Test
    public void testHavingClauseSerde() {
        List<HavingSpec> havings = Arrays.asList(new GreaterThanHavingSpec("agg", Double.valueOf(1.3)), new OrHavingSpec(Arrays.asList(new LessThanHavingSpec("lessAgg", Long.valueOf(1L)), new NotHavingSpec(new EqualToHavingSpec("equalAgg", Double.valueOf(2))))));
        HavingSpec andHavingSpec = new AndHavingSpec(havings);
        Map<String, Object> notMap = ImmutableMap.of("type", "not", "havingSpec", ImmutableMap.of("type", "equalTo", "aggregation", "equalAgg", "value", 2.0));
        Map<String, Object> lessMap = ImmutableMap.of("type", "lessThan", "aggregation", "lessAgg", "value", 1);
        Map<String, Object> greaterMap = ImmutableMap.of("type", "greaterThan", "aggregation", "agg", "value", 1.3);
        Map<String, Object> orMap = ImmutableMap.of("type", "or", "havingSpecs", ImmutableList.of(lessMap, notMap));
        Map<String, Object> payloadMap = ImmutableMap.of("type", "and", "havingSpecs", ImmutableList.of(greaterMap, orMap));
        ObjectMapper mapper = new DefaultObjectMapper();
        Assert.assertEquals(andHavingSpec, mapper.convertValue(payloadMap, AndHavingSpec.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeTypo() {
        Map<String, Object> greaterMap = ImmutableMap.of("type", "nonExistingType", "aggregation", "agg", "value", 1.3);
        ObjectMapper mapper = new DefaultObjectMapper();
        // noinspection unused
        HavingSpec spec = mapper.convertValue(greaterMap, HavingSpec.class);
    }

    @Test
    public void testGreaterThanHavingSpec() {
        GreaterThanHavingSpec spec = new GreaterThanHavingSpec("metric", Long.valueOf(((Long.MAX_VALUE) - 10)));
        Assert.assertFalse(spec.eval(getTestRow(Long.valueOf(((Long.MAX_VALUE) - 10)))));
        Assert.assertFalse(spec.eval(getTestRow(Long.valueOf(((Long.MAX_VALUE) - 15)))));
        Assert.assertTrue(spec.eval(getTestRow(Long.valueOf(((Long.MAX_VALUE) - 5)))));
        Assert.assertTrue(spec.eval(getTestRow(String.valueOf(((Long.MAX_VALUE) - 5)))));
        Assert.assertFalse(spec.eval(getTestRow(100.05F)));
        spec = new GreaterThanHavingSpec("metric", 100.56F);
        Assert.assertFalse(spec.eval(getTestRow(100.56F)));
        Assert.assertFalse(spec.eval(getTestRow(90.53F)));
        Assert.assertFalse(spec.eval(getTestRow("90.53f")));
        Assert.assertTrue(spec.eval(getTestRow(101.34F)));
        Assert.assertTrue(spec.eval(getTestRow(Long.MAX_VALUE)));
    }

    @Test
    public void testLessThanHavingSpec() {
        LessThanHavingSpec spec = new LessThanHavingSpec("metric", Long.valueOf(((Long.MAX_VALUE) - 10)));
        Assert.assertFalse(spec.eval(getTestRow(Long.valueOf(((Long.MAX_VALUE) - 10)))));
        Assert.assertTrue(spec.eval(getTestRow(Long.valueOf(((Long.MAX_VALUE) - 15)))));
        Assert.assertTrue(spec.eval(getTestRow(String.valueOf(((Long.MAX_VALUE) - 15)))));
        Assert.assertFalse(spec.eval(getTestRow(Long.valueOf(((Long.MAX_VALUE) - 5)))));
        Assert.assertTrue(spec.eval(getTestRow(100.05F)));
        spec = new LessThanHavingSpec("metric", 100.56F);
        Assert.assertFalse(spec.eval(getTestRow(100.56F)));
        Assert.assertTrue(spec.eval(getTestRow(90.53F)));
        Assert.assertFalse(spec.eval(getTestRow(101.34F)));
        Assert.assertFalse(spec.eval(getTestRow("101.34f")));
        Assert.assertFalse(spec.eval(getTestRow(Long.MAX_VALUE)));
    }

    @Test
    public void testEqualHavingSpec() {
        EqualToHavingSpec spec = new EqualToHavingSpec("metric", Long.valueOf(((Long.MAX_VALUE) - 10)));
        Assert.assertTrue(spec.eval(getTestRow(Long.valueOf(((Long.MAX_VALUE) - 10)))));
        Assert.assertFalse(spec.eval(getTestRow(Long.valueOf(((Long.MAX_VALUE) - 5)))));
        Assert.assertFalse(spec.eval(getTestRow(100.05F)));
        spec = new EqualToHavingSpec("metric", 100.56F);
        Assert.assertFalse(spec.eval(getTestRow(100L)));
        Assert.assertFalse(spec.eval(getTestRow(100.0)));
        Assert.assertFalse(spec.eval(getTestRow(100.0)));
        Assert.assertFalse(spec.eval(getTestRow(100.56)));// False since 100.56d != (double) 100.56f

        Assert.assertFalse(spec.eval(getTestRow(90.53)));
        Assert.assertTrue(spec.eval(getTestRow(100.56F)));
        Assert.assertFalse(spec.eval(getTestRow(90.53F)));
        Assert.assertFalse(spec.eval(getTestRow(Long.MAX_VALUE)));
        spec = new EqualToHavingSpec("metric", 100.56);
        Assert.assertFalse(spec.eval(getTestRow(100L)));
        Assert.assertFalse(spec.eval(getTestRow(100.0)));
        Assert.assertFalse(spec.eval(getTestRow(100.0)));
        Assert.assertTrue(spec.eval(getTestRow(100.56)));
        Assert.assertFalse(spec.eval(getTestRow(90.53)));
        Assert.assertFalse(spec.eval(getTestRow(100.56F)));// False since 100.56d != (double) 100.56f

        Assert.assertFalse(spec.eval(getTestRow(90.53F)));
        Assert.assertFalse(spec.eval(getTestRow(Long.MAX_VALUE)));
        spec = new EqualToHavingSpec("metric", 100.0F);
        Assert.assertTrue(spec.eval(getTestRow(100L)));
        Assert.assertTrue(spec.eval(getTestRow(100.0)));
        Assert.assertTrue(spec.eval(getTestRow(100.0)));
        Assert.assertFalse(spec.eval(getTestRow(100.56)));
        Assert.assertFalse(spec.eval(getTestRow(90.53)));
        Assert.assertFalse(spec.eval(getTestRow(100.56F)));
        Assert.assertFalse(spec.eval(getTestRow(90.53F)));
        Assert.assertFalse(spec.eval(getTestRow(Long.MAX_VALUE)));
        spec = new EqualToHavingSpec("metric", 100.0);
        Assert.assertTrue(spec.eval(getTestRow(100L)));
        Assert.assertTrue(spec.eval(getTestRow(100.0)));
        Assert.assertTrue(spec.eval(getTestRow(100.0)));
        Assert.assertFalse(spec.eval(getTestRow(100.56)));
        Assert.assertFalse(spec.eval(getTestRow(90.53)));
        Assert.assertFalse(spec.eval(getTestRow(100.56F)));
        Assert.assertFalse(spec.eval(getTestRow(90.53F)));
        Assert.assertFalse(spec.eval(getTestRow(Long.MAX_VALUE)));
        spec = new EqualToHavingSpec("metric", 100);
        Assert.assertTrue(spec.eval(getTestRow(100L)));
        Assert.assertTrue(spec.eval(getTestRow(100.0)));
        Assert.assertTrue(spec.eval(getTestRow(100.0)));
        Assert.assertFalse(spec.eval(getTestRow(100.56)));
        Assert.assertFalse(spec.eval(getTestRow(90.53)));
        Assert.assertFalse(spec.eval(getTestRow(100.56F)));
        Assert.assertFalse(spec.eval(getTestRow(90.53F)));
        Assert.assertFalse(spec.eval(getTestRow(Long.MAX_VALUE)));
        spec = new EqualToHavingSpec("metric", 100L);
        Assert.assertTrue(spec.eval(getTestRow(100L)));
        Assert.assertTrue(spec.eval(getTestRow(100.0)));
        Assert.assertTrue(spec.eval(getTestRow(100.0)));
        Assert.assertFalse(spec.eval(getTestRow(100.56)));
        Assert.assertFalse(spec.eval(getTestRow(90.53)));
        Assert.assertFalse(spec.eval(getTestRow(100.56F)));
        Assert.assertFalse(spec.eval(getTestRow(90.53F)));
        Assert.assertFalse(spec.eval(getTestRow(Long.MAX_VALUE)));
    }

    private static class CountingHavingSpec extends BaseHavingSpec {
        private final AtomicInteger counter;

        private final boolean value;

        private CountingHavingSpec(AtomicInteger counter, boolean value) {
            this.counter = counter;
            this.value = value;
        }

        @Override
        public boolean eval(Row row) {
            counter.incrementAndGet();
            return value;
        }
    }

    @Test
    public void testAndHavingSpecShouldSupportShortcutEvaluation() {
        AtomicInteger counter = new AtomicInteger(0);
        AndHavingSpec spec = new AndHavingSpec(ImmutableList.of(((HavingSpec) (new HavingSpecTest.CountingHavingSpec(counter, true))), new HavingSpecTest.CountingHavingSpec(counter, false), new HavingSpecTest.CountingHavingSpec(counter, true), new HavingSpecTest.CountingHavingSpec(counter, false)));
        spec.eval(HavingSpecTest.ROW);
        Assert.assertEquals(2, counter.get());
    }

    @Test
    public void testAndHavingSpec() {
        AtomicInteger counter = new AtomicInteger(0);
        AndHavingSpec spec = new AndHavingSpec(ImmutableList.of(((HavingSpec) (new HavingSpecTest.CountingHavingSpec(counter, true))), new HavingSpecTest.CountingHavingSpec(counter, true), new HavingSpecTest.CountingHavingSpec(counter, true), new HavingSpecTest.CountingHavingSpec(counter, true)));
        spec.eval(HavingSpecTest.ROW);
        Assert.assertEquals(4, counter.get());
        counter.set(0);
        spec = new AndHavingSpec(ImmutableList.of(((HavingSpec) (new HavingSpecTest.CountingHavingSpec(counter, false))), new HavingSpecTest.CountingHavingSpec(counter, true), new HavingSpecTest.CountingHavingSpec(counter, true), new HavingSpecTest.CountingHavingSpec(counter, true)));
        spec.eval(HavingSpecTest.ROW);
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testOrHavingSpecSupportsShortcutEvaluation() {
        AtomicInteger counter = new AtomicInteger(0);
        OrHavingSpec spec = new OrHavingSpec(ImmutableList.of(((HavingSpec) (new HavingSpecTest.CountingHavingSpec(counter, true))), new HavingSpecTest.CountingHavingSpec(counter, true), new HavingSpecTest.CountingHavingSpec(counter, true), new HavingSpecTest.CountingHavingSpec(counter, false)));
        spec.eval(HavingSpecTest.ROW);
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testOrHavingSpec() {
        AtomicInteger counter = new AtomicInteger(0);
        OrHavingSpec spec = new OrHavingSpec(ImmutableList.of(((HavingSpec) (new HavingSpecTest.CountingHavingSpec(counter, false))), new HavingSpecTest.CountingHavingSpec(counter, false), new HavingSpecTest.CountingHavingSpec(counter, false), new HavingSpecTest.CountingHavingSpec(counter, false)));
        spec.eval(HavingSpecTest.ROW);
        Assert.assertEquals(4, counter.get());
        counter.set(0);
        spec = new OrHavingSpec(ImmutableList.of(((HavingSpec) (new HavingSpecTest.CountingHavingSpec(counter, false))), new HavingSpecTest.CountingHavingSpec(counter, false), new HavingSpecTest.CountingHavingSpec(counter, false), new HavingSpecTest.CountingHavingSpec(counter, true)));
        spec.eval(HavingSpecTest.ROW);
        Assert.assertEquals(4, counter.get());
    }

    @Test
    public void testNotHavingSepc() {
        NotHavingSpec spec = new NotHavingSpec(NEVER);
        Assert.assertTrue(spec.eval(HavingSpecTest.ROW));
        spec = new NotHavingSpec(ALWAYS);
        Assert.assertFalse(spec.eval(HavingSpecTest.ROW));
    }
}


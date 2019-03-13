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


import com.google.common.collect.ImmutableList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.druid.js.JavaScriptConfig;
import org.apache.druid.query.dimension.DimensionSpec;
import org.apache.druid.segment.ColumnSelectorFactory;
import org.apache.druid.segment.ColumnValueSelector;
import org.apache.druid.segment.DimensionSelector;
import org.apache.druid.segment.column.ColumnCapabilities;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JavaScriptAggregatorTest {
    protected static final Map<String, String> sumLogATimesBPlusTen = new HashMap<>();

    protected static final Map<String, String> scriptDoubleSum = new HashMap<>();

    final ColumnSelectorFactory DUMMY_COLUMN_SELECTOR_FACTORY = new ColumnSelectorFactory() {
        @Override
        public DimensionSelector makeDimensionSelector(DimensionSpec dimensionSpec) {
            return null;
        }

        @Override
        public ColumnValueSelector<?> makeColumnValueSelector(String columnName) {
            return null;
        }

        @Override
        public ColumnCapabilities getColumnCapabilities(String columnName) {
            return null;
        }
    };

    static {
        JavaScriptAggregatorTest.sumLogATimesBPlusTen.put("fnAggregate", "function aggregate(current, a, b) { return current + (Math.log(a) * b) }");
        JavaScriptAggregatorTest.sumLogATimesBPlusTen.put("fnReset", "function reset()                  { return 10 }");
        JavaScriptAggregatorTest.sumLogATimesBPlusTen.put("fnCombine", "function combine(a,b)             { return a + b }");
        JavaScriptAggregatorTest.scriptDoubleSum.put("fnAggregate", "function aggregate(current, a) { return current + a }");
        JavaScriptAggregatorTest.scriptDoubleSum.put("fnReset", "function reset()               { return 0 }");
        JavaScriptAggregatorTest.scriptDoubleSum.put("fnCombine", "function combine(a,b)          { return a + b }");
    }

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testAggregate() {
        final TestDoubleColumnSelectorImpl selector1 = new TestDoubleColumnSelectorImpl(new double[]{ 42.12, 9.0 });
        final TestDoubleColumnSelectorImpl selector2 = new TestDoubleColumnSelectorImpl(new double[]{ 2.0, 3.0 });
        Map<String, String> script = JavaScriptAggregatorTest.sumLogATimesBPlusTen;
        JavaScriptAggregator agg = new JavaScriptAggregator(Arrays.asList(selector1, selector2), JavaScriptAggregatorFactory.compileScript(script.get("fnAggregate"), script.get("fnReset"), script.get("fnCombine")));
        double val = 10.0;
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        JavaScriptAggregatorTest.aggregate(selector1, selector2, agg);
        val += (Math.log(42.12)) * 2.0;
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        JavaScriptAggregatorTest.aggregate(selector1, selector2, agg);
        val += (Math.log(9.0)) * 3.0;
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
    }

    @Test
    public void testBufferAggregate() {
        final TestFloatColumnSelector selector1 = new TestFloatColumnSelector(new float[]{ 42.12F, 9.0F });
        final TestFloatColumnSelector selector2 = new TestFloatColumnSelector(new float[]{ 2.0F, 3.0F });
        Map<String, String> script = JavaScriptAggregatorTest.sumLogATimesBPlusTen;
        JavaScriptBufferAggregator agg = new JavaScriptBufferAggregator(Arrays.asList(selector1, selector2), JavaScriptAggregatorFactory.compileScript(script.get("fnAggregate"), script.get("fnReset"), script.get("fnCombine")));
        ByteBuffer buf = ByteBuffer.allocateDirect(32);
        final int position = 4;
        agg.init(buf, position);
        double val = 10.0;
        Assert.assertEquals(val, agg.get(buf, position));
        Assert.assertEquals(val, agg.get(buf, position));
        Assert.assertEquals(val, agg.get(buf, position));
        aggregateBuffer(selector1, selector2, agg, buf, position);
        val += (Math.log(42.12F)) * 2.0F;
        Assert.assertEquals(val, agg.get(buf, position));
        Assert.assertEquals(val, agg.get(buf, position));
        Assert.assertEquals(val, agg.get(buf, position));
        aggregateBuffer(selector1, selector2, agg, buf, position);
        val += (Math.log(9.0F)) * 3.0F;
        Assert.assertEquals(val, agg.get(buf, position));
        Assert.assertEquals(val, agg.get(buf, position));
        Assert.assertEquals(val, agg.get(buf, position));
    }

    @Test
    public void testAggregateMissingColumn() {
        Map<String, String> script = JavaScriptAggregatorTest.scriptDoubleSum;
        JavaScriptAggregator agg = new JavaScriptAggregator(Collections.singletonList(null), JavaScriptAggregatorFactory.compileScript(script.get("fnAggregate"), script.get("fnReset"), script.get("fnCombine")));
        final double val = 0;
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        agg.aggregate();
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        agg.aggregate();
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
    }

    @Test
    public void testAggregateStrings() {
        final TestObjectColumnSelector ocs = new TestObjectColumnSelector<>(new Object[]{ "what", null, new String[]{ "hey", "there" } });
        final JavaScriptAggregator agg = new JavaScriptAggregator(Collections.singletonList(ocs), JavaScriptAggregatorFactory.compileScript("function aggregate(current, a) { if (Array.isArray(a)) { return current + a.length; } else if (typeof a === 'string') { return current + 1; } else { return current; } }", JavaScriptAggregatorTest.scriptDoubleSum.get("fnReset"), JavaScriptAggregatorTest.scriptDoubleSum.get("fnCombine")));
        double val = 0.0;
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        JavaScriptAggregatorTest.aggregate(ocs, agg);
        val += 1;
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        JavaScriptAggregatorTest.aggregate(ocs, agg);
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        JavaScriptAggregatorTest.aggregate(ocs, agg);
        val += 2;
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
        Assert.assertEquals(val, agg.get());
    }

    @Test
    public void testJavaScriptDisabledFactorize() {
        final JavaScriptAggregatorFactory factory = new JavaScriptAggregatorFactory("foo", ImmutableList.of("foo"), JavaScriptAggregatorTest.scriptDoubleSum.get("fnAggregate"), JavaScriptAggregatorTest.scriptDoubleSum.get("fnReset"), JavaScriptAggregatorTest.scriptDoubleSum.get("fnCombine"), new JavaScriptConfig(false));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("JavaScript is disabled");
        factory.factorize(DUMMY_COLUMN_SELECTOR_FACTORY);
        Assert.assertTrue(false);
    }

    @Test
    public void testJavaScriptDisabledFactorizeBuffered() {
        final JavaScriptAggregatorFactory factory = new JavaScriptAggregatorFactory("foo", ImmutableList.of("foo"), JavaScriptAggregatorTest.scriptDoubleSum.get("fnAggregate"), JavaScriptAggregatorTest.scriptDoubleSum.get("fnReset"), JavaScriptAggregatorTest.scriptDoubleSum.get("fnCombine"), new JavaScriptConfig(false));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("JavaScript is disabled");
        factory.factorizeBuffered(DUMMY_COLUMN_SELECTOR_FACTORY);
        Assert.assertTrue(false);
    }
}


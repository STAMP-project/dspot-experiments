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


import com.google.common.collect.Lists;
import java.util.Arrays;
import org.apache.druid.js.JavaScriptConfig;
import org.apache.druid.query.extraction.ExtractionFn;
import org.apache.druid.query.filter.RegexDimFilter;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.query.search.ContainsSearchQuerySpec;
import org.junit.Test;


public class FilteredAggregatorTest {
    @Test
    public void testAggregate() {
        final float[] values = new float[]{ 0.15F, 0.27F };
        final TestFloatColumnSelector selector = new TestFloatColumnSelector(values);
        FilteredAggregatorFactory factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new SelectorDimFilter("dim", "a", null));
        FilteredAggregator agg = ((FilteredAggregator) (factory.factorize(makeColumnSelector(selector))));
        double expectedFirst = new Float(values[0]).doubleValue();
        double expectedSecond = (new Float(values[1]).doubleValue()) + expectedFirst;
        double expectedThird = expectedSecond;
        assertValues(agg, selector, expectedFirst, expectedSecond, expectedThird);
    }

    @Test
    public void testAggregateWithNotFilter() {
        final float[] values = new float[]{ 0.15F, 0.27F };
        final TestFloatColumnSelector selector = new TestFloatColumnSelector(values);
        FilteredAggregatorFactory factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.NotDimFilter(new SelectorDimFilter("dim", "b", null)));
        validateFilteredAggs(factory, values, selector);
    }

    @Test
    public void testAggregateWithOrFilter() {
        final float[] values = new float[]{ 0.15F, 0.27F, 0.14F };
        final TestFloatColumnSelector selector = new TestFloatColumnSelector(values);
        FilteredAggregatorFactory factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.OrDimFilter(Lists.newArrayList(new SelectorDimFilter("dim", "a", null), new SelectorDimFilter("dim", "b", null))));
        FilteredAggregator agg = ((FilteredAggregator) (factory.factorize(makeColumnSelector(selector))));
        double expectedFirst = new Float(values[0]).doubleValue();
        double expectedSecond = (new Float(values[1]).doubleValue()) + expectedFirst;
        double expectedThird = expectedSecond + (new Float(values[2]).doubleValue());
        assertValues(agg, selector, expectedFirst, expectedSecond, expectedThird);
    }

    @Test
    public void testAggregateWithAndFilter() {
        final float[] values = new float[]{ 0.15F, 0.27F };
        final TestFloatColumnSelector selector = new TestFloatColumnSelector(values);
        FilteredAggregatorFactory factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.AndDimFilter(Lists.newArrayList(new org.apache.druid.query.filter.NotDimFilter(new SelectorDimFilter("dim", "b", null)), new SelectorDimFilter("dim", "a", null))));
        validateFilteredAggs(factory, values, selector);
    }

    @Test
    public void testAggregateWithPredicateFilters() {
        final float[] values = new float[]{ 0.15F, 0.27F };
        TestFloatColumnSelector selector;
        FilteredAggregatorFactory factory;
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.BoundDimFilter("dim", "a", "a", false, false, true, null, StringComparators.ALPHANUMERIC));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new RegexDimFilter("dim", "a", null));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.SearchQueryDimFilter("dim", new ContainsSearchQuerySpec("a", true), null));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
        String jsFn = "function(x) { return(x === 'a') }";
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.JavaScriptDimFilter("dim", jsFn, null, JavaScriptConfig.getEnabledInstance()));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
    }

    @Test
    public void testAggregateWithExtractionFns() {
        final float[] values = new float[]{ 0.15F, 0.27F };
        TestFloatColumnSelector selector;
        FilteredAggregatorFactory factory;
        String extractionJsFn = "function(str) { return str + 'AARDVARK'; }";
        ExtractionFn extractionFn = new org.apache.druid.query.extraction.JavaScriptExtractionFn(extractionJsFn, false, JavaScriptConfig.getEnabledInstance());
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new SelectorDimFilter("dim", "aAARDVARK", extractionFn));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.InDimFilter("dim", Arrays.asList("NOT-aAARDVARK", "FOOBAR", "aAARDVARK"), extractionFn));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.BoundDimFilter("dim", "aAARDVARK", "aAARDVARK", false, false, true, extractionFn, StringComparators.ALPHANUMERIC));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new RegexDimFilter("dim", "aAARDVARK", extractionFn));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.SearchQueryDimFilter("dim", new ContainsSearchQuerySpec("aAARDVARK", true), extractionFn));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
        String jsFn = "function(x) { return(x === 'aAARDVARK') }";
        factory = new FilteredAggregatorFactory(new DoubleSumAggregatorFactory("billy", "value"), new org.apache.druid.query.filter.JavaScriptDimFilter("dim", jsFn, extractionFn, JavaScriptConfig.getEnabledInstance()));
        selector = new TestFloatColumnSelector(values);
        validateFilteredAggs(factory, values, selector);
    }
}


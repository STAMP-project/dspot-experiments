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
package org.apache.beam.runners.dataflow;


import Combine.CombineFn;
import CombineWithContext.CombineFnWithContext;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.runners.PTransformMatcher;
import org.apache.beam.sdk.transforms.CombineWithContext.Context;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DataflowPTransformMatchers}.
 */
@RunWith(JUnit4.class)
public class DataflowPTransformMatchersTest {
    /**
     * Test the cases that the matcher should successfully match against. In this case, it should
     * match against {@link Combine.GroupedValues} on their own and as part of an expanded {@link Combine.PerKey} transform.
     */
    @Test
    public void combineValuesWithoutSideInputsSuccessfulMatches() {
        PTransformMatcher matcher = new DataflowPTransformMatchers.CombineValuesWithoutSideInputsPTransformMatcher();
        AppliedPTransform<?, ?, ?> groupedValues;
        groupedValues = DataflowPTransformMatchersTest.getCombineGroupedValuesFrom(DataflowPTransformMatchersTest.createCombineGroupedValuesPipeline());
        Assert.assertThat(matcher.matches(groupedValues), Matchers.is(true));
        groupedValues = DataflowPTransformMatchersTest.getCombineGroupedValuesFrom(DataflowPTransformMatchersTest.createCombinePerKeyPipeline());
        Assert.assertThat(matcher.matches(groupedValues), Matchers.is(true));
    }

    /**
     * Test significant cases that the matcher should not match against. In this case, this explicitly
     * tests that any {@link Combine.GroupedValues} with side inputs should not match.
     */
    @Test
    public void combineValuesWithoutSideInputsSkipsNonmatching() {
        PTransformMatcher matcher = new DataflowPTransformMatchers.CombineValuesWithoutSideInputsPTransformMatcher();
        AppliedPTransform<?, ?, ?> groupedValues;
        groupedValues = DataflowPTransformMatchersTest.getCombineGroupedValuesFrom(DataflowPTransformMatchersTest.createCombineGroupedValuesWithSideInputsPipeline());
        Assert.assertThat(matcher.matches(groupedValues), Matchers.is(false));
        groupedValues = DataflowPTransformMatchersTest.getCombineGroupedValuesFrom(DataflowPTransformMatchersTest.createCombinePerKeyWithSideInputsPipeline());
        Assert.assertThat(matcher.matches(groupedValues), Matchers.is(false));
    }

    /**
     * Test the cases that the matcher should successfully match against. In this case, it should
     * match against {@link Combine.GroupedValues} that are part of an expanded {@link Combine.PerKey}
     * transform.
     */
    @Test
    public void combineValuesWithParentCheckSuccessfulMatches() {
        PTransformMatcher matcher = new DataflowPTransformMatchers.CombineValuesWithParentCheckPTransformMatcher();
        AppliedPTransform<?, ?, ?> groupedValues;
        groupedValues = DataflowPTransformMatchersTest.getCombineGroupedValuesFrom(DataflowPTransformMatchersTest.createCombinePerKeyPipeline());
        Assert.assertThat(matcher.matches(groupedValues), Matchers.is(true));
    }

    /**
     * Test significant cases that the matcher should not match against. In this case, this tests that
     * any {@link Combine.GroupedValues} with side inputs should not match, and that a {@link Combine.GroupedValues} without an encompassing {@link Combine.PerKey} will not match.
     */
    @Test
    public void combineValuesWithParentCheckSkipsNonmatching() {
        PTransformMatcher matcher = new DataflowPTransformMatchers.CombineValuesWithParentCheckPTransformMatcher();
        AppliedPTransform<?, ?, ?> groupedValues;
        groupedValues = DataflowPTransformMatchersTest.getCombineGroupedValuesFrom(DataflowPTransformMatchersTest.createCombineGroupedValuesPipeline());
        Assert.assertThat(matcher.matches(groupedValues), Matchers.is(false));
        groupedValues = DataflowPTransformMatchersTest.getCombineGroupedValuesFrom(DataflowPTransformMatchersTest.createCombineGroupedValuesWithSideInputsPipeline());
        Assert.assertThat(matcher.matches(groupedValues), Matchers.is(false));
        groupedValues = DataflowPTransformMatchersTest.getCombineGroupedValuesFrom(DataflowPTransformMatchersTest.createCombinePerKeyWithSideInputsPipeline());
        Assert.assertThat(matcher.matches(groupedValues), Matchers.is(false));
    }

    private static class SumCombineFn extends CombineFn<Integer, Integer, Integer> {
        @Override
        public Integer createAccumulator() {
            return 0;
        }

        @Override
        public Integer addInput(Integer accum, Integer input) {
            return accum + input;
        }

        @Override
        public Integer mergeAccumulators(Iterable<Integer> accumulators) {
            Integer sum = 0;
            for (Integer accum : accumulators) {
                sum += accum;
            }
            return sum;
        }

        @Override
        public Integer extractOutput(Integer accumulator) {
            return accumulator;
        }
    }

    private static class SumCombineFnWithContext extends CombineFnWithContext<Integer, Integer, Integer> {
        DataflowPTransformMatchersTest.SumCombineFn delegate;

        @Override
        public Integer createAccumulator(Context c) {
            return delegate.createAccumulator();
        }

        @Override
        public Integer addInput(Integer accum, Integer input, Context c) {
            return delegate.addInput(accum, input);
        }

        @Override
        public Integer mergeAccumulators(Iterable<Integer> accumulators, Context c) {
            return delegate.mergeAccumulators(accumulators);
        }

        @Override
        public Integer extractOutput(Integer accumulator, Context c) {
            return delegate.extractOutput(accumulator);
        }
    }
}


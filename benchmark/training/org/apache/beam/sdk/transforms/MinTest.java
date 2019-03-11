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
package org.apache.beam.sdk.transforms;


import Combine.Globally;
import org.apache.beam.sdk.testing.CombineFnTester;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Min.
 */
@RunWith(JUnit4.class)
public class MinTest {
    @Test
    public void testMinGetNames() {
        Assert.assertEquals("Combine.globally(MinInteger)", Min.integersGlobally().getName());
        Assert.assertEquals("Combine.globally(MinDouble)", Min.doublesGlobally().getName());
        Assert.assertEquals("Combine.globally(MinLong)", Min.longsGlobally().getName());
        Assert.assertEquals("Combine.perKey(MinInteger)", Min.integersPerKey().getName());
        Assert.assertEquals("Combine.perKey(MinDouble)", Min.doublesPerKey().getName());
        Assert.assertEquals("Combine.perKey(MinLong)", Min.longsPerKey().getName());
    }

    @Test
    public void testMinIntegerFn() {
        CombineFnTester.testCombineFn(Min.ofIntegers(), Lists.newArrayList(1, 2, 3, 4), 1);
    }

    @Test
    public void testMinLongFn() {
        CombineFnTester.testCombineFn(Min.ofLongs(), Lists.newArrayList(1L, 2L, 3L, 4L), 1L);
    }

    @Test
    public void testMinDoubleFn() {
        CombineFnTester.testCombineFn(Min.ofDoubles(), Lists.newArrayList(1.0, 2.0, 3.0, 4.0), 1.0);
    }

    @Test
    public void testDisplayData() {
        Top.Reversed<Integer> comparer = new Top.Reversed<>();
        Globally<Integer, Integer> min = Min.globally(comparer);
        MatcherAssert.assertThat(DisplayData.from(min), DisplayDataMatchers.hasDisplayItem("comparer", comparer.getClass()));
    }
}


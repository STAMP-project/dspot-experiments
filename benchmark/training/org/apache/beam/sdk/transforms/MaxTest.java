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
 * Tests for Max.
 */
@RunWith(JUnit4.class)
public class MaxTest {
    @Test
    public void testMaxGetNames() {
        Assert.assertEquals("Combine.globally(MaxInteger)", Max.integersGlobally().getName());
        Assert.assertEquals("Combine.globally(MaxDouble)", Max.doublesGlobally().getName());
        Assert.assertEquals("Combine.globally(MaxLong)", Max.longsGlobally().getName());
        Assert.assertEquals("Combine.perKey(MaxInteger)", Max.integersPerKey().getName());
        Assert.assertEquals("Combine.perKey(MaxDouble)", Max.doublesPerKey().getName());
        Assert.assertEquals("Combine.perKey(MaxLong)", Max.longsPerKey().getName());
    }

    @Test
    public void testMaxIntegerFn() {
        CombineFnTester.testCombineFn(Max.ofIntegers(), Lists.newArrayList(1, 2, 3, 4), 4);
    }

    @Test
    public void testMaxLongFn() {
        CombineFnTester.testCombineFn(Max.ofLongs(), Lists.newArrayList(1L, 2L, 3L, 4L), 4L);
    }

    @Test
    public void testMaxDoubleFn() {
        CombineFnTester.testCombineFn(Max.ofDoubles(), Lists.newArrayList(1.0, 2.0, 3.0, 4.0), 4.0);
    }

    @Test
    public void testDisplayData() {
        Top.Natural<Integer> comparer = new Top.Natural<>();
        Globally<Integer, Integer> max = Max.globally(comparer);
        MatcherAssert.assertThat(DisplayData.from(max), DisplayDataMatchers.hasDisplayItem("comparer", comparer.getClass()));
    }
}


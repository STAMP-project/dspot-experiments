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
package org.apache.beam.sdk.extensions.euphoria.core.client.lib;


import org.apache.beam.sdk.extensions.euphoria.core.client.functional.UnaryPredicate;
import org.apache.beam.sdk.extensions.euphoria.core.client.operator.Filter;
import org.apache.beam.sdk.extensions.euphoria.core.client.operator.TestUtils;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.junit.Assert;
import org.junit.Test;

import static Split.DEFAULT_NAME;
import static Split.NEGATIVE_FILTER_SUFFIX;
import static Split.POSITIVE_FILTER_SUFFIX;


/**
 * Test suite for {@link Split} library.
 */
public class SplitTest {
    @Test
    public void testBuild() {
        final String opName = "split";
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final Split.Output<String> split = Split.named(opName).of(dataset).using(((UnaryPredicate<String>) (( what) -> true))).output();
        final Filter positive = ((Filter) (TestUtils.getProducer(split.positive())));
        Assert.assertNotNull(positive.getPredicate());
        final Filter negative = ((Filter) (TestUtils.getProducer(split.negative())));
        Assert.assertNotNull(negative.getPredicate());
    }

    @Test
    public void testBuild_ImplicitName() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final Split.Output<String> split = Split.of(dataset).using(((UnaryPredicate<String>) (( what) -> true))).output();
        final Filter positive = ((Filter) (TestUtils.getProducer(split.positive())));
        Assert.assertTrue(positive.getName().isPresent());
        Assert.assertEquals(((DEFAULT_NAME) + (POSITIVE_FILTER_SUFFIX)), positive.getName().get());
        final Filter negative = ((Filter) (TestUtils.getProducer(split.negative())));
        Assert.assertTrue(negative.getName().isPresent());
        Assert.assertEquals(((DEFAULT_NAME) + (NEGATIVE_FILTER_SUFFIX)), negative.getName().get());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBuild_NegatedPredicate() {
        final PCollection<Integer> dataset = TestUtils.createMockDataset(TypeDescriptors.integers());
        final Split.Output<Integer> split = Split.of(dataset).using(((UnaryPredicate<Integer>) (( what) -> (what % 2) == 0))).output();
        final Filter<Integer> oddNumbers = ((Filter) (TestUtils.getProducer(split.negative())));
        Assert.assertFalse(oddNumbers.getPredicate().apply(0));
        Assert.assertFalse(oddNumbers.getPredicate().apply(2));
        Assert.assertFalse(oddNumbers.getPredicate().apply(4));
        Assert.assertTrue(oddNumbers.getPredicate().apply(1));
        Assert.assertTrue(oddNumbers.getPredicate().apply(3));
        Assert.assertTrue(oddNumbers.getPredicate().apply(5));
    }
}


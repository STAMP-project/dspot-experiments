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
package org.apache.beam.sdk.extensions.sql.impl.transform.agg;


import VarianceAccumulator.EMPTY;
import java.math.BigDecimal;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit tests for {@link VarianceFnTest}.
 */
@RunWith(Parameterized.class)
public class VarianceFnTest {
    private static final BigDecimal FIFTEEN = new BigDecimal(15);

    private static final BigDecimal THREE = new BigDecimal(3);

    private static final BigDecimal FOUR = new BigDecimal(4);

    private VarianceFn varianceFn;

    private VarianceAccumulator testAccumulatorInput;

    private int expectedExtractedResult;

    public VarianceFnTest(VarianceFn varianceFn, VarianceAccumulator testAccumulatorInput, int expectedExtractedResult) {
        this.varianceFn = varianceFn;
        this.testAccumulatorInput = testAccumulatorInput;
        this.expectedExtractedResult = expectedExtractedResult;
    }

    @Test
    public void testCreatesEmptyAccumulator() {
        Assert.assertEquals(EMPTY, varianceFn.createAccumulator());
    }

    @Test
    public void testReturnsAccumulatorUnchangedForNullInput() {
        VarianceAccumulator accumulator = VarianceAccumulator.newVarianceAccumulator(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN);
        Assert.assertEquals(accumulator, varianceFn.addInput(accumulator, null));
    }

    @Test
    public void testAddsInputToAccumulator() {
        VarianceAccumulator expectedAccumulator = VarianceAccumulator.newVarianceAccumulator(BigDecimal.ZERO, BigDecimal.ONE, new BigDecimal(2));
        Assert.assertEquals(expectedAccumulator, varianceFn.addInput(varianceFn.createAccumulator(), new BigDecimal(2)));
    }

    @Test
    public void testCeatesAccumulatorCoder() {
        Assert.assertNotNull(varianceFn.getAccumulatorCoder(CoderRegistry.createDefault(), VarIntCoder.of()));
    }

    @Test
    public void testReturnsOutput() {
        Assert.assertEquals(expectedExtractedResult, varianceFn.extractOutput(testAccumulatorInput));
    }
}


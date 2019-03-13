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
package org.apache.beam.sdk.extensions.sketching;


import com.tdunning.math.stats.MergingDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.beam.sdk.extensions.sketching.TDigestQuantiles.TDigestQuantilesFn;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link TDigestQuantiles}.
 */
public class TDigestQuantilesTest {
    @Rule
    public final transient TestPipeline tp = TestPipeline.create();

    private static final List<Double> stream = TDigestQuantilesTest.generateStream();

    private static final int size = 999;

    private static final int compression = 100;

    private static final double[] quantiles = new double[]{ 0.25, 0.5, 0.75, 0.99 };

    @Test
    public void globally() {
        PCollection<KV<Double, Double>> col = tp.apply(Create.of(TDigestQuantilesTest.stream)).apply(TDigestQuantiles.globally().withCompression(TDigestQuantilesTest.compression)).apply(ParDo.of(new TDigestQuantilesTest.RetrieveQuantiles(TDigestQuantilesTest.quantiles)));
        PAssert.that("Verify Accuracy", col).satisfies(new TDigestQuantilesTest.VerifyAccuracy());
        tp.run();
    }

    @Test
    public void perKey() {
        PCollection<KV<Double, Double>> col = tp.apply(Create.of(TDigestQuantilesTest.stream)).apply(WithKeys.of(1)).apply(TDigestQuantiles.<Integer>perKey().withCompression(TDigestQuantilesTest.compression)).apply(Values.create()).apply(ParDo.of(new TDigestQuantilesTest.RetrieveQuantiles(TDigestQuantilesTest.quantiles)));
        PAssert.that("Verify Accuracy", col).satisfies(new TDigestQuantilesTest.VerifyAccuracy());
        tp.run();
    }

    @Test
    public void testCoder() throws Exception {
        MergingDigest tDigest = new MergingDigest(1000);
        for (int i = 0; i < 10; i++) {
            tDigest.add((2.4 + i));
        }
        Assert.assertTrue("Encode and Decode", encodeDecodeEquals(tDigest));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testMergeAccum() {
        Random rd = new Random(1234);
        List<MergingDigest> accums = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MergingDigest std = new MergingDigest(100);
            for (int j = 0; j < 1000; j++) {
                std.add(rd.nextDouble());
            }
            accums.add(std);
        }
        TDigestQuantilesFn fn = TDigestQuantilesFn.create(100);
        MergingDigest res = fn.mergeAccumulators(accums);
    }

    @Test
    public void testDisplayData() {
        final TDigestQuantilesFn fn = TDigestQuantilesFn.create(155.0);
        Assert.assertThat(DisplayData.from(fn), hasDisplayItem("compression", 155.0));
    }

    static class RetrieveQuantiles extends DoFn<MergingDigest, KV<Double, Double>> {
        private final double[] quantiles;

        RetrieveQuantiles(double[] quantiles) {
            this.quantiles = quantiles;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            for (double q : quantiles) {
                c.output(KV.of(q, c.element().quantile(q)));
            }
        }
    }

    static class VerifyAccuracy implements SerializableFunction<Iterable<KV<Double, Double>>, Void> {
        final double expectedError = 3.0 / (TDigestQuantilesTest.compression);

        @Override
        public Void apply(Iterable<KV<Double, Double>> input) {
            for (KV<Double, Double> pair : input) {
                double expectedValue = (pair.getKey()) * ((TDigestQuantilesTest.size) + 1);
                boolean isAccurate = ((Math.abs(((pair.getValue()) - expectedValue))) / (TDigestQuantilesTest.size)) <= (expectedError);
                Assert.assertTrue(((((("not accurate enough : \nQuantile " + (pair.getKey())) + " is ") + (pair.getValue())) + " and not ") + expectedValue), isAccurate);
            }
            return null;
        }
    }
}


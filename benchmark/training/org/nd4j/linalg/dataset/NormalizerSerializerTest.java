/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.linalg.dataset;


import NormalizerStats.Builder;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.CustomSerializerStrategy;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerType;
import org.nd4j.linalg.dataset.api.preprocessor.stats.MinMaxStats;
import org.nd4j.linalg.dataset.api.preprocessor.stats.NormalizerStats;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author Ede Meijer
 */
@RunWith(Parameterized.class)
public class NormalizerSerializerTest extends BaseNd4jTest {
    private File tmpFile;

    private NormalizerSerializer SUT;

    public NormalizerSerializerTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testImagePreProcessingScaler() throws Exception {
        ImagePreProcessingScaler imagePreProcessingScaler = new ImagePreProcessingScaler(0, 1);
        SUT.write(imagePreProcessingScaler, tmpFile);
        ImagePreProcessingScaler restored = SUT.restore(tmpFile);
        Assert.assertEquals(imagePreProcessingScaler, restored);
    }

    @Test
    public void testNormalizerStandardizeNotFitLabels() throws Exception {
        NormalizerStandardize original = new NormalizerStandardize(Nd4j.create(new double[]{ 0.5, 1.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 2.5, 3.5 }).reshape(1, (-1)));
        SUT.write(original, tmpFile);
        NormalizerStandardize restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testNormalizerStandardizeFitLabels() throws Exception {
        NormalizerStandardize original = new NormalizerStandardize(Nd4j.create(new double[]{ 0.5, 1.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 2.5, 3.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 4.5, 5.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 6.5, 7.5 }).reshape(1, (-1)));
        original.fitLabel(true);
        SUT.write(original, tmpFile);
        NormalizerStandardize restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testNormalizerMinMaxScalerNotFitLabels() throws Exception {
        NormalizerMinMaxScaler original = new NormalizerMinMaxScaler(0.1, 0.9);
        original.setFeatureStats(Nd4j.create(new double[]{ 0.5, 1.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 2.5, 3.5 }).reshape(1, (-1)));
        SUT.write(original, tmpFile);
        NormalizerMinMaxScaler restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testNormalizerMinMaxScalerFitLabels() throws Exception {
        NormalizerMinMaxScaler original = new NormalizerMinMaxScaler(0.1, 0.9);
        original.setFeatureStats(Nd4j.create(new double[]{ 0.5, 1.5 }), Nd4j.create(new double[]{ 2.5, 3.5 }));
        original.setLabelStats(Nd4j.create(new double[]{ 4.5, 5.5 }), Nd4j.create(new double[]{ 6.5, 7.5 }));
        original.fitLabel(true);
        SUT.write(original, tmpFile);
        NormalizerMinMaxScaler restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testMultiNormalizerStandardizeNotFitLabels() throws Exception {
        MultiNormalizerStandardize original = new MultiNormalizerStandardize();
        original.setFeatureStats(Arrays.asList(new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new double[]{ 0.5, 1.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 2.5, 3.5 }).reshape(1, (-1))), new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new double[]{ 4.5, 5.5, 6.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 7.5, 8.5, 9.5 }).reshape(1, (-1)))));
        SUT.write(original, tmpFile);
        MultiNormalizerStandardize restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testMultiNormalizerStandardizeFitLabels() throws Exception {
        MultiNormalizerStandardize original = new MultiNormalizerStandardize();
        original.setFeatureStats(Arrays.asList(new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new double[]{ 0.5, 1.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 2.5, 3.5 }).reshape(1, (-1))), new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new double[]{ 4.5, 5.5, 6.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 7.5, 8.5, 9.5 }).reshape(1, (-1)))));
        original.setLabelStats(Arrays.asList(new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new double[]{ 0.5, 1.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 2.5, 3.5 }).reshape(1, (-1))), new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new double[]{ 4.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 7.5 }).reshape(1, (-1))), new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new double[]{ 4.5, 5.5, 6.5 }).reshape(1, (-1)), Nd4j.create(new double[]{ 7.5, 8.5, 9.5 }).reshape(1, (-1)))));
        original.fitLabel(true);
        SUT.write(original, tmpFile);
        MultiNormalizerStandardize restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testMultiNormalizerMinMaxScalerNotFitLabels() throws Exception {
        MultiNormalizerMinMaxScaler original = new MultiNormalizerMinMaxScaler(0.1, 0.9);
        original.setFeatureStats(Arrays.asList(new MinMaxStats(Nd4j.create(new double[]{ 0.5, 1.5 }), Nd4j.create(new double[]{ 2.5, 3.5 })), new MinMaxStats(Nd4j.create(new double[]{ 4.5, 5.5, 6.5 }), Nd4j.create(new double[]{ 7.5, 8.5, 9.5 }))));
        SUT.write(original, tmpFile);
        MultiNormalizerMinMaxScaler restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testMultiNormalizerMinMaxScalerFitLabels() throws Exception {
        MultiNormalizerMinMaxScaler original = new MultiNormalizerMinMaxScaler(0.1, 0.9);
        original.setFeatureStats(Arrays.asList(new MinMaxStats(Nd4j.create(new double[]{ 0.5, 1.5 }), Nd4j.create(new double[]{ 2.5, 3.5 })), new MinMaxStats(Nd4j.create(new double[]{ 4.5, 5.5, 6.5 }), Nd4j.create(new double[]{ 7.5, 8.5, 9.5 }))));
        original.setLabelStats(Arrays.asList(new MinMaxStats(Nd4j.create(new double[]{ 0.5, 1.5 }), Nd4j.create(new double[]{ 2.5, 3.5 })), new MinMaxStats(Nd4j.create(new double[]{ 4.5 }), Nd4j.create(new double[]{ 7.5 })), new MinMaxStats(Nd4j.create(new double[]{ 4.5, 5.5, 6.5 }), Nd4j.create(new double[]{ 7.5, 8.5, 9.5 }))));
        original.fitLabel(true);
        SUT.write(original, tmpFile);
        MultiNormalizerMinMaxScaler restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testMultiNormalizerHybridEmpty() throws Exception {
        MultiNormalizerHybrid original = new MultiNormalizerHybrid();
        original.setInputStats(new HashMap<Integer, NormalizerStats>());
        original.setOutputStats(new HashMap<Integer, NormalizerStats>());
        SUT.write(original, tmpFile);
        MultiNormalizerHybrid restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testMultiNormalizerHybridGlobalStats() throws Exception {
        MultiNormalizerHybrid original = new MultiNormalizerHybrid().minMaxScaleAllInputs().standardizeAllOutputs();
        Map<Integer, NormalizerStats> inputStats = new HashMap<>();
        inputStats.put(0, new MinMaxStats(Nd4j.create(new float[]{ 1, 2 }).reshape(1, (-1)), Nd4j.create(new float[]{ 3, 4 }).reshape(1, (-1))));
        inputStats.put(0, new MinMaxStats(Nd4j.create(new float[]{ 5, 6 }).reshape(1, (-1)), Nd4j.create(new float[]{ 7, 8 }).reshape(1, (-1))));
        Map<Integer, NormalizerStats> outputStats = new HashMap<>();
        outputStats.put(0, new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new float[]{ 9, 10 }).reshape(1, (-1)), Nd4j.create(new float[]{ 11, 12 }).reshape(1, (-1))));
        outputStats.put(0, new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new float[]{ 13, 14 }).reshape(1, (-1)), Nd4j.create(new float[]{ 15, 16 }).reshape(1, (-1))));
        original.setInputStats(inputStats);
        original.setOutputStats(outputStats);
        SUT.write(original, tmpFile);
        MultiNormalizerHybrid restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test
    public void testMultiNormalizerHybridGlobalAndSpecificStats() throws Exception {
        MultiNormalizerHybrid original = new MultiNormalizerHybrid().standardizeAllInputs().minMaxScaleInput(0, (-5), 5).minMaxScaleAllOutputs((-10), 10).standardizeOutput(1);
        Map<Integer, NormalizerStats> inputStats = new HashMap<>();
        inputStats.put(0, new MinMaxStats(Nd4j.create(new float[]{ 1, 2 }).reshape(1, (-1)), Nd4j.create(new float[]{ 3, 4 }).reshape(1, (-1))));
        inputStats.put(1, new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new float[]{ 5, 6 }).reshape(1, (-1)), Nd4j.create(new float[]{ 7, 8 }).reshape(1, (-1))));
        Map<Integer, NormalizerStats> outputStats = new HashMap<>();
        outputStats.put(0, new MinMaxStats(Nd4j.create(new float[]{ 9, 10 }).reshape(1, (-1)), Nd4j.create(new float[]{ 11, 12 }).reshape(1, (-1))));
        outputStats.put(1, new org.nd4j.linalg.dataset.api.preprocessor.stats.DistributionStats(Nd4j.create(new float[]{ 13, 14 }).reshape(1, (-1)), Nd4j.create(new float[]{ 15, 16 }).reshape(1, (-1))));
        original.setInputStats(inputStats);
        original.setOutputStats(outputStats);
        SUT.write(original, tmpFile);
        MultiNormalizerHybrid restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    @Test(expected = RuntimeException.class)
    public void testCustomNormalizerWithoutRegisteredStrategy() throws Exception {
        SUT.write(new NormalizerSerializerTest.MyNormalizer(123), tmpFile);
    }

    @Test
    public void testCustomNormalizer() throws Exception {
        NormalizerSerializerTest.MyNormalizer original = new NormalizerSerializerTest.MyNormalizer(42);
        SUT.addStrategy(new NormalizerSerializerTest.MyNormalizerSerializerStrategy());
        SUT.write(original, tmpFile);
        NormalizerSerializerTest.MyNormalizer restored = SUT.restore(tmpFile);
        Assert.assertEquals(original, restored);
    }

    public static class MyNormalizer extends AbstractDataSetNormalizer<MinMaxStats> {
        @Getter
        private final int foo;

        public MyNormalizer(int foo) {
            super(new MinMaxStrategy());
            this.foo = foo;
            setFeatureStats(new MinMaxStats(Nd4j.zeros(1), Nd4j.ones(1)));
        }

        @Override
        public NormalizerType getType() {
            return NormalizerType.CUSTOM;
        }

        @Override
        protected Builder newBuilder() {
            return new MinMaxStats.Builder();
        }
    }

    public static class MyNormalizerSerializerStrategy extends CustomSerializerStrategy<NormalizerSerializerTest.MyNormalizer> {
        @Override
        public Class<NormalizerSerializerTest.MyNormalizer> getSupportedClass() {
            return NormalizerSerializerTest.MyNormalizer.class;
        }

        @Override
        public void write(NormalizerSerializerTest.MyNormalizer normalizer, OutputStream stream) throws IOException {
            new DataOutputStream(stream).writeInt(getFoo());
        }

        @Override
        public NormalizerSerializerTest.MyNormalizer restore(InputStream stream) throws IOException {
            return new NormalizerSerializerTest.MyNormalizer(new DataInputStream(stream).readInt());
        }
    }
}


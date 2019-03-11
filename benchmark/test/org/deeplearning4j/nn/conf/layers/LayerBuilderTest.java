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
package org.deeplearning4j.nn.conf.layers;


import Convolution.Type;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.weights.WeightInit;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.activations.impl.ActivationSoftmax;
import org.nd4j.linalg.activations.impl.ActivationTanH;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import static PoolingType.MAX;


/**
 *
 *
 * @author Jeffrey Tang.
 */
public class LayerBuilderTest extends BaseDL4JTest {
    final double DELTA = 1.0E-15;

    int numIn = 10;

    int numOut = 5;

    double drop = 0.3;

    IActivation act = new ActivationSoftmax();

    PoolingType poolType = MAX;

    int[] kernelSize = new int[]{ 2, 2 };

    int[] stride = new int[]{ 2, 2 };

    int[] padding = new int[]{ 1, 1 };

    int k = 1;

    Type convType = Type.VALID;

    LossFunction loss = LossFunction.MCXENT;

    WeightInit weight = WeightInit.XAVIER;

    double corrupt = 0.4;

    double sparsity = 0.3;

    double corruptionLevel = 0.5;

    double dropOut = 0.1;

    IUpdater updater = new AdaGrad();

    GradientNormalization gradNorm = GradientNormalization.ClipL2PerParamType;

    double gradNormThreshold = 8;

    @Test
    public void testLayer() throws Exception {
        DenseLayer layer = new DenseLayer.Builder().activation(act).weightInit(weight).dropOut(dropOut).updater(updater).gradientNormalization(gradNorm).gradientNormalizationThreshold(gradNormThreshold).build();
        checkSerialization(layer);
        Assert.assertEquals(act, layer.getActivationFn());
        Assert.assertEquals(weight.getWeightInitFunction(), layer.getWeightInitFn());
        Assert.assertEquals(new Dropout(dropOut), layer.getIDropout());
        Assert.assertEquals(updater, layer.getIUpdater());
        Assert.assertEquals(gradNorm, layer.getGradientNormalization());
        Assert.assertEquals(gradNormThreshold, layer.getGradientNormalizationThreshold(), 0.0);
    }

    @Test
    public void testFeedForwardLayer() throws Exception {
        DenseLayer ff = new DenseLayer.Builder().nIn(numIn).nOut(numOut).build();
        checkSerialization(ff);
        Assert.assertEquals(numIn, ff.getNIn());
        Assert.assertEquals(numOut, ff.getNOut());
    }

    @Test
    public void testConvolutionLayer() throws Exception {
        ConvolutionLayer conv = new ConvolutionLayer.Builder(kernelSize, stride, padding).build();
        checkSerialization(conv);
        // assertEquals(convType, conv.getConvolutionType());
        Assert.assertArrayEquals(kernelSize, conv.getKernelSize());
        Assert.assertArrayEquals(stride, conv.getStride());
        Assert.assertArrayEquals(padding, conv.getPadding());
    }

    @Test
    public void testSubsamplingLayer() throws Exception {
        SubsamplingLayer sample = kernelSize(kernelSize).padding(padding).build();
        checkSerialization(sample);
        Assert.assertArrayEquals(padding, sample.getPadding());
        Assert.assertArrayEquals(kernelSize, sample.getKernelSize());
        Assert.assertEquals(poolType, sample.getPoolingType());
        Assert.assertArrayEquals(stride, sample.getStride());
    }

    @Test
    public void testOutputLayer() throws Exception {
        OutputLayer out = build();
        checkSerialization(out);
    }

    @Test
    public void testRnnOutputLayer() throws Exception {
        RnnOutputLayer out = new RnnOutputLayer.Builder(loss).build();
        checkSerialization(out);
    }

    @Test
    public void testAutoEncoder() throws Exception {
        AutoEncoder enc = new AutoEncoder.Builder().corruptionLevel(corruptionLevel).sparsity(sparsity).build();
        checkSerialization(enc);
        Assert.assertEquals(corruptionLevel, enc.getCorruptionLevel(), DELTA);
        Assert.assertEquals(sparsity, enc.getSparsity(), DELTA);
    }

    @Test
    public void testGravesLSTM() throws Exception {
        GravesLSTM glstm = new GravesLSTM.Builder().forgetGateBiasInit(1.5).activation(Activation.TANH).nIn(numIn).nOut(numOut).build();
        checkSerialization(glstm);
        Assert.assertEquals(glstm.getForgetGateBiasInit(), 1.5, 0.0);
        Assert.assertEquals(glstm.nIn, numIn);
        Assert.assertEquals(glstm.nOut, numOut);
        Assert.assertTrue(((glstm.getActivationFn()) instanceof ActivationTanH));
    }

    @Test
    public void testGravesBidirectionalLSTM() throws Exception {
        final GravesBidirectionalLSTM glstm = new GravesBidirectionalLSTM.Builder().forgetGateBiasInit(1.5).activation(Activation.TANH).nIn(numIn).nOut(numOut).build();
        checkSerialization(glstm);
        Assert.assertEquals(glstm.getForgetGateBiasInit(), 1.5, 0.0);
        Assert.assertEquals(glstm.nIn, numIn);
        Assert.assertEquals(glstm.nOut, numOut);
        Assert.assertTrue(((glstm.getActivationFn()) instanceof ActivationTanH));
    }

    @Test
    public void testEmbeddingLayer() throws Exception {
        EmbeddingLayer el = new EmbeddingLayer.Builder().nIn(10).nOut(5).build();
        checkSerialization(el);
        Assert.assertEquals(10, el.getNIn());
        Assert.assertEquals(5, el.getNOut());
    }

    @Test
    public void testBatchNormLayer() throws Exception {
        BatchNormalization bN = new BatchNormalization.Builder().nIn(numIn).nOut(numOut).gamma(2).beta(1).decay(0.5).lockGammaBeta(true).build();
        checkSerialization(bN);
        Assert.assertEquals(numIn, bN.nIn);
        Assert.assertEquals(numOut, bN.nOut);
        Assert.assertEquals(true, bN.isLockGammaBeta());
        Assert.assertEquals(0.5, bN.decay, 1.0E-4);
        Assert.assertEquals(2, bN.gamma, 1.0E-4);
        Assert.assertEquals(1, bN.beta, 1.0E-4);
    }

    @Test
    public void testActivationLayer() throws Exception {
        ActivationLayer activationLayer = new ActivationLayer.Builder().activation(act).build();
        checkSerialization(activationLayer);
        Assert.assertEquals(act, activationLayer.activationFn);
    }
}


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
package org.deeplearning4j.nn.updater;


import DefaultParamInitializer.BIAS_KEY;
import DefaultParamInitializer.WEIGHT_KEY;
import GradientNormalization.ClipElementWiseAbsoluteValue;
import GradientNormalization.ClipL2PerLayer;
import GradientNormalization.ClipL2PerParamType;
import GradientNormalization.RenormalizeL2PerLayer;
import GradientNormalization.RenormalizeL2PerParamType;
import lombok.val;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.Updater;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.NoOp;


public class TestGradientNormalization extends BaseDL4JTest {
    @Test
    public void testRenormalizatonPerLayer() {
        Nd4j.getRandom().setSeed(12345);
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new DenseLayer.Builder().nIn(10).nOut(20).updater(new NoOp()).gradientNormalization(RenormalizeL2PerLayer).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        Layer layer = conf.getLayer().instantiate(conf, null, 0, params, true);
        INDArray gradArray = Nd4j.rand(1, 220).muli(10).subi(5);
        layer.setBackpropGradientsViewArray(gradArray);
        INDArray weightGrad = Shape.newShapeNoCopy(gradArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 200)), new int[]{ 10, 20 }, true);
        INDArray biasGrad = gradArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(200, 220));
        INDArray weightGradCopy = weightGrad.dup();
        INDArray biasGradCopy = biasGrad.dup();
        Gradient gradient = new DefaultGradient(gradArray);
        gradient.setGradientFor(WEIGHT_KEY, weightGrad);
        gradient.setGradientFor(BIAS_KEY, biasGrad);
        Updater updater = UpdaterCreator.getUpdater(layer);
        updater.update(layer, gradient, 0, 0, 1, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertNotEquals(weightGradCopy, weightGrad);
        Assert.assertNotEquals(biasGradCopy, biasGrad);
        double sumSquaresWeight = weightGradCopy.mul(weightGradCopy).sumNumber().doubleValue();
        double sumSquaresBias = biasGradCopy.mul(biasGradCopy).sumNumber().doubleValue();
        double sumSquares = sumSquaresWeight + sumSquaresBias;
        double l2Layer = Math.sqrt(sumSquares);
        INDArray normWeightsExpected = weightGradCopy.div(l2Layer);
        INDArray normBiasExpected = biasGradCopy.div(l2Layer);
        double l2Weight = gradient.getGradientFor(WEIGHT_KEY).norm2Number().doubleValue();
        double l2Bias = gradient.getGradientFor(BIAS_KEY).norm2Number().doubleValue();
        Assert.assertTrue(((!(Double.isNaN(l2Weight))) && (l2Weight > 0.0)));
        Assert.assertTrue(((!(Double.isNaN(l2Bias))) && (l2Bias > 0.0)));
        Assert.assertEquals(normWeightsExpected, gradient.getGradientFor(WEIGHT_KEY));
        Assert.assertEquals(normBiasExpected, gradient.getGradientFor(BIAS_KEY));
    }

    @Test
    public void testRenormalizationPerParamType() {
        Nd4j.getRandom().setSeed(12345);
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new DenseLayer.Builder().nIn(10).nOut(20).updater(new NoOp()).gradientNormalization(RenormalizeL2PerParamType).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        Layer layer = conf.getLayer().instantiate(conf, null, 0, params, true);
        layer.setBackpropGradientsViewArray(Nd4j.create(params.shape()));
        Updater updater = UpdaterCreator.getUpdater(layer);
        INDArray weightGrad = Nd4j.rand(10, 20);
        INDArray biasGrad = Nd4j.rand(1, 10);
        INDArray weightGradCopy = weightGrad.dup();
        INDArray biasGradCopy = biasGrad.dup();
        Gradient gradient = new DefaultGradient();
        gradient.setGradientFor(WEIGHT_KEY, weightGrad);
        gradient.setGradientFor(BIAS_KEY, biasGrad);
        updater.update(layer, gradient, 0, 0, 1, LayerWorkspaceMgr.noWorkspaces());
        INDArray normWeightsExpected = weightGradCopy.div(weightGradCopy.norm2Number());
        INDArray normBiasExpected = biasGradCopy.div(biasGradCopy.norm2Number());
        Assert.assertEquals(normWeightsExpected, gradient.getGradientFor(WEIGHT_KEY));
        Assert.assertEquals(normBiasExpected, gradient.getGradientFor(BIAS_KEY));
    }

    @Test
    public void testAbsValueClippingPerElement() {
        Nd4j.getRandom().setSeed(12345);
        double threshold = 3;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new DenseLayer.Builder().nIn(10).nOut(20).updater(new NoOp()).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(threshold).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        Layer layer = conf.getLayer().instantiate(conf, null, 0, params, true);
        INDArray gradArray = Nd4j.rand(1, 220).muli(10).subi(5);
        layer.setBackpropGradientsViewArray(gradArray);
        INDArray weightGrad = Shape.newShapeNoCopy(gradArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 200)), new int[]{ 10, 20 }, true);
        INDArray biasGrad = gradArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(200, 220));
        INDArray weightGradCopy = weightGrad.dup();
        INDArray biasGradCopy = biasGrad.dup();
        Gradient gradient = new DefaultGradient(gradArray);
        gradient.setGradientFor(WEIGHT_KEY, weightGrad);
        gradient.setGradientFor(BIAS_KEY, biasGrad);
        Updater updater = UpdaterCreator.getUpdater(layer);
        updater.update(layer, gradient, 0, 0, 1, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertNotEquals(weightGradCopy, weightGrad);
        Assert.assertNotEquals(biasGradCopy, biasGrad);
        INDArray expectedWeightGrad = weightGradCopy.dup();
        for (int i = 0; i < (expectedWeightGrad.length()); i++) {
            double d = expectedWeightGrad.getDouble(i);
            if (d > threshold)
                expectedWeightGrad.putScalar(i, threshold);
            else
                if (d < (-threshold))
                    expectedWeightGrad.putScalar(i, (-threshold));


        }
        INDArray expectedBiasGrad = biasGradCopy.dup();
        for (int i = 0; i < (expectedBiasGrad.length()); i++) {
            double d = expectedBiasGrad.getDouble(i);
            if (d > threshold)
                expectedBiasGrad.putScalar(i, threshold);
            else
                if (d < (-threshold))
                    expectedBiasGrad.putScalar(i, (-threshold));


        }
        Assert.assertEquals(expectedWeightGrad, gradient.getGradientFor(WEIGHT_KEY));
        Assert.assertEquals(expectedBiasGrad, gradient.getGradientFor(BIAS_KEY));
    }

    @Test
    public void testL2ClippingPerLayer() {
        Nd4j.getRandom().setSeed(12345);
        double threshold = 3;
        for (int t = 0; t < 2; t++) {
            // t=0: small -> no clipping
            // t=1: large -> clipping
            NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new DenseLayer.Builder().nIn(10).nOut(20).updater(new NoOp()).gradientNormalization(ClipL2PerLayer).gradientNormalizationThreshold(threshold).build()).build();
            val numParams = conf.getLayer().initializer().numParams(conf);
            INDArray params = Nd4j.create(1, numParams);
            Layer layer = conf.getLayer().instantiate(conf, null, 0, params, true);
            INDArray gradArray = Nd4j.rand(1, 220).muli((t == 0 ? 0.05 : 10)).subi((t == 0 ? 0 : 5));
            layer.setBackpropGradientsViewArray(gradArray);
            INDArray weightGrad = Shape.newShapeNoCopy(gradArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 200)), new int[]{ 10, 20 }, true);
            INDArray biasGrad = gradArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(200, 220));
            INDArray weightGradCopy = weightGrad.dup();
            INDArray biasGradCopy = biasGrad.dup();
            Gradient gradient = new DefaultGradient(gradArray);
            gradient.setGradientFor(WEIGHT_KEY, weightGrad);
            gradient.setGradientFor(BIAS_KEY, biasGrad);
            double layerGradL2 = gradient.gradient().norm2Number().doubleValue();
            if (t == 0)
                Assert.assertTrue((layerGradL2 < threshold));
            else
                Assert.assertTrue((layerGradL2 > threshold));

            Updater updater = UpdaterCreator.getUpdater(layer);
            updater.update(layer, gradient, 0, 0, 1, LayerWorkspaceMgr.noWorkspaces());
            if (t == 0) {
                // norm2 < threshold -> no change
                Assert.assertEquals(weightGradCopy, weightGrad);
                Assert.assertEquals(biasGradCopy, biasGrad);
                continue;
            } else {
                // norm2 > threshold -> rescale
                Assert.assertNotEquals(weightGradCopy, weightGrad);
                Assert.assertNotEquals(biasGradCopy, biasGrad);
            }
            // for above threshold only...
            double scalingFactor = threshold / layerGradL2;
            INDArray expectedWeightGrad = weightGradCopy.mul(scalingFactor);
            INDArray expectedBiasGrad = biasGradCopy.mul(scalingFactor);
            Assert.assertEquals(expectedWeightGrad, gradient.getGradientFor(WEIGHT_KEY));
            Assert.assertEquals(expectedBiasGrad, gradient.getGradientFor(BIAS_KEY));
        }
    }

    @Test
    public void testL2ClippingPerParamType() {
        Nd4j.getRandom().setSeed(12345);
        double threshold = 3;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new DenseLayer.Builder().nIn(10).nOut(20).updater(new NoOp()).gradientNormalization(ClipL2PerParamType).gradientNormalizationThreshold(threshold).build()).build();
        val numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        Layer layer = conf.getLayer().instantiate(conf, null, 0, params, true);
        layer.setBackpropGradientsViewArray(Nd4j.create(params.shape()));
        Updater updater = UpdaterCreator.getUpdater(layer);
        INDArray weightGrad = Nd4j.rand(10, 20).muli(0.05);
        INDArray biasGrad = Nd4j.rand(1, 10).muli(10);
        INDArray weightGradCopy = weightGrad.dup();
        INDArray biasGradCopy = biasGrad.dup();
        Gradient gradient = new DefaultGradient();
        gradient.setGradientFor(WEIGHT_KEY, weightGrad);
        gradient.setGradientFor(BIAS_KEY, biasGrad);
        double weightL2 = weightGrad.norm2Number().doubleValue();
        double biasL2 = biasGrad.norm2Number().doubleValue();
        Assert.assertTrue((weightL2 < threshold));
        Assert.assertTrue((biasL2 > threshold));
        updater.update(layer, gradient, 0, 0, 1, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertEquals(weightGradCopy, weightGrad);// weight norm2 < threshold -> no change

        Assert.assertNotEquals(biasGradCopy, biasGrad);// bias norm2 > threshold -> rescale

        double biasScalingFactor = threshold / biasL2;
        INDArray expectedBiasGrad = biasGradCopy.mul(biasScalingFactor);
        Assert.assertEquals(expectedBiasGrad, gradient.getGradientFor(BIAS_KEY));
    }
}


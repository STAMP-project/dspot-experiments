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


import Activation.SOFTMAX;
import Activation.TANH;
import DefaultParamInitializer.BIAS_KEY;
import DefaultParamInitializer.WEIGHT_KEY;
import LossFunctions.LossFunction.MSE;
import Nd4j.EPS_THRESHOLD;
import PretrainParamInitializer.VISIBLE_BIAS_KEY;
import UpdaterBlock.ParamState;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.math3.util.FastMath;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.Updater;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater.ADAGRAD;
import org.deeplearning4j.nn.conf.Updater.NESTEROVS;
import org.deeplearning4j.nn.conf.Updater.SGD;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.BaseLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.params.DefaultParamInitializer;
import org.deeplearning4j.nn.updater.graph.ComputationGraphUpdater;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.NoOpUpdater;
import org.nd4j.linalg.ops.transforms.Transforms;

import static AdaGrad.DEFAULT_ADAGRAD_EPSILON;
import static AdaMax.DEFAULT_ADAMAX_EPSILON;
import static Adam.DEFAULT_ADAM_EPSILON;
import static Nadam.DEFAULT_NADAM_EPSILON;
import static RmsProp.DEFAULT_RMSPROP_EPSILON;
import static org.nd4j.linalg.factory.Nd4j.EPS_THRESHOLD;


public class TestUpdaters extends BaseDL4JTest {
    protected int nIn = 3;

    protected int nOut = 2;

    // protected double epsilon = 1e-8;
    protected INDArray gradients;

    protected INDArray weightGradient;

    protected INDArray biasGradient;

    protected DefaultGradient gradient = new DefaultGradient();

    protected INDArray val;

    protected INDArray gradExpected;

    protected String key;

    @Test
    public void testAdaDeltaUpdate() {
        // Here: test updaters manually vs. using updater
        INDArray dxSquared;
        Map<String, INDArray> msg = new HashMap<>();
        Map<String, INDArray> msdx = new HashMap<>();
        double rho = 0.85;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).updater(new AdaDelta(rho, EPS_THRESHOLD)).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        int updaterStateSize = ((int) (layer.layerConf().getIUpdater().stateSize(numParams)));
        INDArray updaterState = Nd4j.create(1, updaterStateSize);
        updater.setStateViewArray(layer, updaterState, true);
        Gradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        int count = 0;
        for (int i = 0; i < 2; i++) {
            updater.update(layer, gradient, i, 0, 1, LayerWorkspaceMgr.noWorkspaces());
            // calculations for one iteration / update
            for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
                key = entry.getKey();
                val = entry.getValue();
                INDArray msgTmp = msg.get(key);
                INDArray msdxTmp = msdx.get(key);
                if (msgTmp == null) {
                    msgTmp = Nd4j.zeros(val.shape());
                    msdxTmp = Nd4j.zeros(val.shape());
                }
                msgTmp.muli(rho);
                msgTmp.addi(val.mul(val).muli((1 - rho)));
                gradExpected = Transforms.sqrt(msdxTmp.add(EPS_THRESHOLD)).divi(Transforms.sqrt(msgTmp.add(EPS_THRESHOLD))).muli(val);
                gradientCopyPreUpdate.setGradientFor(key, gradExpected);
                Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
                msdxTmp.muli(rho);
                dxSquared = gradExpected.mul(gradExpected);
                msdxTmp.addi(dxSquared.muli((1 - rho)));
                msg.put(key, msgTmp);
                msdx.put(key, msdxTmp);
                count++;
            }
            Assert.assertEquals(rho, getRho(), 1.0E-4);
        }
        Assert.assertEquals(4, count);
    }

    @Test
    public void testAdaGradUpdater() {
        double lr = 0.01;
        double epsilon = DEFAULT_ADAGRAD_EPSILON;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().updater(new AdaGrad(lr)).layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        int updaterStateSize = ((int) (layer.layerConf().getIUpdater().stateSize(numParams)));
        INDArray updaterState = Nd4j.create(1, updaterStateSize);
        updater.setStateViewArray(layer, updaterState, true);
        Gradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        updater.update(layer, gradient, (-1), 0, 1, LayerWorkspaceMgr.noWorkspaces());
        int count = 0;
        for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
            val = entry.getValue();
            gradExpected = Transforms.sqrt(val.mul(val).add(epsilon)).rdiv(lr).mul(val);
            Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
            count++;
        }
        Assert.assertEquals(lr, getLearningRate(), 1.0E-4);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testAdamUpdater() {
        INDArray m;
        INDArray v;
        double lr = 0.01;
        int iteration = 0;
        double beta1 = 0.8;
        double beta2 = 0.888;
        double epsilon = DEFAULT_ADAM_EPSILON;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Adam(lr, beta1, beta2, DEFAULT_ADAM_EPSILON)).layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        int updaterStateSize = ((int) (layer.layerConf().getIUpdater().stateSize(numParams)));
        INDArray updaterState = Nd4j.create(1, updaterStateSize);
        updater.setStateViewArray(layer, updaterState, true);
        updater.update(layer, gradient, iteration, 0, 1, LayerWorkspaceMgr.noWorkspaces());
        double beta1t = FastMath.pow(beta1, (iteration + 1));
        double beta2t = FastMath.pow(beta2, (iteration + 1));
        double alphat = (lr * (FastMath.sqrt((1 - beta2t)))) / (1 - beta1t);
        if ((Double.isNaN(alphat)) || (alphat == 0.0))
            alphat = epsilon;

        Gradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        int count = 0;
        for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
            val = entry.getValue();
            m = Nd4j.zeros(val.shape());
            v = Nd4j.zeros(val.shape());
            m.muli(beta1).addi(val.mul((1.0 - beta1)));
            v.muli(beta2).addi(val.mul(val).mul((1.0 - beta2)));
            gradExpected = m.mul(alphat).divi(Transforms.sqrt(v).addi(epsilon));
            if (!(gradExpected.equals(gradient.getGradientFor(entry.getKey())))) {
                System.out.println(Arrays.toString(gradExpected.dup().data().asFloat()));
                System.out.println(Arrays.toString(gradient.getGradientFor(entry.getKey()).dup().data().asFloat()));
            }
            Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
            count++;
        }
        Assert.assertEquals(beta1, getBeta1(), 1.0E-4);
        Assert.assertEquals(beta2, getBeta2(), 1.0E-4);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testNadamUpdater() {
        INDArray m;
        INDArray v;
        double lr = 0.01;
        int iteration = 0;
        double beta1 = 0.8;
        double beta2 = 0.888;
        double epsilon = DEFAULT_NADAM_EPSILON;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).updater(Nadam.builder().learningRate(lr).beta1(beta1).beta2(beta2).epsilon(epsilon).build()).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        int updaterStateSize = ((int) (layer.layerConf().getIUpdater().stateSize(numParams)));
        INDArray updaterState = Nd4j.create(1, updaterStateSize);
        updater.setStateViewArray(layer, updaterState, true);
        /* Making update for layer */
        updater.update(layer, gradient, iteration, 0, 1, LayerWorkspaceMgr.noWorkspaces());
        double beta1t = FastMath.pow(beta1, (iteration + 1));
        Gradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        int count = 0;
        for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
            val = entry.getValue();
            m = Nd4j.zeros(val.shape());
            v = Nd4j.zeros(val.shape());
            INDArray oneMinusBeta1Grad = val.mul((1.0 - beta1));
            m.muli(beta1).addi(oneMinusBeta1Grad);
            INDArray oneMinusBeta2GradSquared = val.mul(val).muli((1.0 - beta2));
            v.muli(beta2).addi(oneMinusBeta2GradSquared);
            INDArray biasCorrectedEstimateOfMomentum = m.mul(beta1).divi((1.0 - beta1t));
            INDArray secondTerm = oneMinusBeta1Grad.divi((1.0 - beta1t));
            INDArray alphat = biasCorrectedEstimateOfMomentum.add(secondTerm).muli(lr);
            INDArray sqrtV = Transforms.sqrt(v, false).addi(epsilon);
            gradExpected = val.assign(alphat).divi(sqrtV);
            if (!(gradExpected.equals(gradient.getGradientFor(entry.getKey())))) {
                System.out.println(Arrays.toString(gradExpected.dup().data().asFloat()));
                System.out.println(Arrays.toString(gradient.getGradientFor(entry.getKey()).dup().data().asFloat()));
            }
            Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
            count++;
        }
        Assert.assertEquals("Count should be equal to 2, one for weight gradient and one for bias gradient", 2, count);
        /* Check that we are not erroneously mutating moving avg gradient while calculating
        `biasCorrectedEstimateOfMomentum = m * beta1 /(1.0 - beta1t);`
         */
        BaseMultiLayerUpdater baseUpdater = ((BaseMultiLayerUpdater) (updater));
        UpdaterBlock ub = ((UpdaterBlock) (baseUpdater.getUpdaterBlocks().get(0)));
        NadamUpdater nadamUpdater = ((NadamUpdater) (ub.getGradientUpdater()));
        // Calculated for following setup: initialWeights are all equal to 1, beta1 = 0.8, beta2 = 0.888, learning rate = 0.01
        double calculatedByHandMScalar = 0.2;
        double[] expectedM = Nd4j.ones(1, numParams).mul(calculatedByHandMScalar).data().asDouble();
        // FIXME: int cast
        double[] actualM = Arrays.copyOfRange(nadamUpdater.getM().data().asDouble(), 0, ((int) (numParams)));
        for (int i = 0; i < (actualM.length); i++) {
            actualM[i] = (Math.round(((actualM[i]) * 100.0))) / 100.0;
        }
        Assert.assertEquals("Wrong weight gradient after first iteration's update", Arrays.equals(actualM, expectedM), true);
    }

    @Test
    public void testAdaMaxUpdater() {
        INDArray m;
        INDArray v;
        double lr = 0.01;
        int iteration = 0;
        double beta1 = 0.8;
        double beta2 = 0.888;
        double epsilon = DEFAULT_ADAMAX_EPSILON;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().updater(new AdaMax(lr, beta1, beta2, DEFAULT_ADAMAX_EPSILON)).layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        int updaterStateSize = ((int) (layer.layerConf().getIUpdater().stateSize(numParams)));
        INDArray updaterState = Nd4j.create(1, updaterStateSize);
        updater.setStateViewArray(layer, updaterState, true);
        updater.update(layer, gradient, iteration, 0, 1, LayerWorkspaceMgr.noWorkspaces());
        double beta1t = FastMath.pow(beta1, (iteration + 1));
        double beta2t = FastMath.pow(beta2, (iteration + 1));
        double alphat = (lr * (FastMath.sqrt((1 - beta2t)))) / (1 - beta1t);
        if ((Double.isNaN(alphat)) || (alphat == 0.0))
            alphat = epsilon;

        Gradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        int count = 0;
        for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
            val = entry.getValue();
            m = Nd4j.zeros(val.shape());
            v = Nd4j.zeros(val.shape());
            m.muli(beta1).addi(val.mul((1.0 - beta1)));
            v.muli(beta2).addi(val.mul(val).mul((1.0 - beta2)));
            gradExpected = m.mul(alphat).divi(Transforms.sqrt(v).addi(epsilon));
            if (!(gradExpected.equals(gradient.getGradientFor(entry.getKey())))) {
                System.out.println(Arrays.toString(gradExpected.dup().data().asFloat()));
                System.out.println(Arrays.toString(gradient.getGradientFor(entry.getKey()).dup().data().asFloat()));
            }
            Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
            count++;
        }
        Assert.assertEquals(beta1, getBeta1(), 1.0E-4);
        Assert.assertEquals(beta2, getBeta2(), 1.0E-4);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testNestorovsUpdater() {
        double lr = 0.01;
        double mu = 0.6;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Nesterovs(lr, mu)).layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        int updaterStateSize = ((int) (layer.layerConf().getIUpdater().stateSize(numParams)));
        INDArray updaterState = Nd4j.create(1, updaterStateSize);
        updater.setStateViewArray(layer, updaterState, true);
        Gradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        updater.update(layer, gradient, (-1), 0, 1, LayerWorkspaceMgr.noWorkspaces());
        int count = 0;
        for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
            INDArray val = entry.getValue();
            INDArray v = Nd4j.create(val.shape());
            INDArray vPrev = v.dup();
            v = v.mul(mu).subi(val.mul(lr));
            gradExpected = vPrev.muli(mu).addi(v.mul(((-mu) - 1)));
            Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
            count++;
        }
        Assert.assertEquals(mu, getMomentum(), 1.0E-4);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testRMSPropUpdater() {
        double lr = 0.01;
        double rmsDecay = 0.25;
        Map<String, INDArray> lastG = new HashMap<>();
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().updater(new RmsProp(lr, rmsDecay, DEFAULT_RMSPROP_EPSILON)).layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        int updaterStateSize = ((int) (layer.layerConf().getIUpdater().stateSize(numParams)));
        INDArray updaterState = Nd4j.create(1, updaterStateSize);
        updater.setStateViewArray(layer, updaterState, true);
        Gradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        updater.update(layer, gradient, (-1), 0, 1, LayerWorkspaceMgr.noWorkspaces());
        double epsilon = 1.0E-8;
        for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
            key = entry.getKey();
            val = entry.getValue();
            INDArray lastGTmp = lastG.get(key);
            if (lastGTmp == null)
                lastGTmp = Nd4j.zeros(val.shape());

            lastGTmp.muli(rmsDecay).addi(val.mul(val).muli((1 - rmsDecay)));
            gradExpected = val.mul(lr).div(Transforms.sqrt(lastGTmp.add(epsilon)));
            Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
            lastG.put(key, lastGTmp);
        }
        Assert.assertEquals(rmsDecay, getRmsDecay(), 1.0E-4);
    }

    @Test
    public void testSGDUpdater() {
        double lr = 0.05;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(lr)).layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        Gradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        updater.update(layer, gradient, (-1), 0, 1, LayerWorkspaceMgr.noWorkspaces());
        for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
            val = entry.getValue();
            gradExpected = val.mul(lr);
            Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
        }
        Assert.assertEquals(lr, getLearningRate(), 1.0E-4);
    }

    @Test
    public void testNoOpUpdater() {
        Random r = new Random(12345L);
        double lr = 0.5;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).layer(new DenseLayer.Builder().nIn(nIn).nOut(nOut).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        Layer layer = conf.getLayer().instantiate(conf, null, 0, params, true);
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        for (int i = 0; i < (weightGradient.length()); i++)
            weightGradient.putScalar(i, r.nextDouble());

        for (int i = 0; i < (biasGradient.length()); i++)
            biasGradient.putScalar(i, r.nextDouble());

        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        gradient.gradientForVariable().put(WEIGHT_KEY, wg);
        gradient.gradientForVariable().put(BIAS_KEY, bg);
        updater.update(layer, gradient, (-1), 0, 1, LayerWorkspaceMgr.noWorkspaces());
        INDArray weightGradActual = gradient.getGradientFor(WEIGHT_KEY);
        INDArray biasGradActual = gradient.getGradientFor(BIAS_KEY);
        Assert.assertEquals(wg, weightGradActual);
        Assert.assertEquals(bg, biasGradActual);
    }

    @Test
    public void testMultiLayerUpdater() throws Exception {
        Nd4j.getRandom().setSeed(12345L);
        double lr = 0.03;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, new DenseLayer.Builder().nIn(4).nOut(5).updater(new Sgd(lr)).build()).layer(1, new DenseLayer.Builder().nIn(5).nOut(6).updater(new NoOp()).build()).layer(2, new DenseLayer.Builder().nIn(6).nOut(7).updater(new AdaGrad(lr)).build()).layer(3, new OutputLayer.Builder().nIn(7).nOut(8).updater(new Nesterovs(0.6)).activation(TANH).lossFunction(MSE).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.fit(Nd4j.create(1, 4), Nd4j.create(1, 8));
        Updater updater = net.getUpdater();
        Assert.assertNotNull(updater);
        Assert.assertTrue(((updater.getClass()) == (MultiLayerUpdater.class)));
        MultiLayerUpdater mlu = ((MultiLayerUpdater) (updater));
        int count = 0;
        for (UpdaterBlock u : mlu.getUpdaterBlocks()) {
            GradientUpdater gu = u.getGradientUpdater();
            switch (count) {
                case 0 :
                    Assert.assertTrue((gu instanceof SgdUpdater));
                    break;
                case 1 :
                    Assert.assertTrue((gu instanceof NoOpUpdater));
                    break;
                case 2 :
                    Assert.assertTrue((gu instanceof AdaGradUpdater));
                    break;
                case 3 :
                    Assert.assertTrue((gu instanceof NesterovsUpdater));
                    break;
                default :
                    throw new RuntimeException();
            }
            count++;
        }
        GradientUpdater[] uArr = new GradientUpdater[4];
        uArr[0] = new SgdUpdater(new Sgd(lr));
        uArr[1] = new NoOpUpdater(new NoOp());
        uArr[2] = new AdaGradUpdater(new AdaGrad(lr, DEFAULT_ADAGRAD_EPSILON));
        INDArray updaterState = Nd4j.create(1, ((6 * 7) + 7), 'f');
        uArr[2].setStateViewArray(updaterState, new long[]{ 1, (6 * 7) + 7 }, 'f', true);
        uArr[3] = new NesterovsUpdater(new Nesterovs(lr, 0.6));
        // updaterStateSize = uArr[3].stateSizeForLayer(net.getLayer(3));
        updaterState = Nd4j.create(1, ((7 * 8) + 8), 'f');
        uArr[3].setStateViewArray(updaterState, new long[]{ 1, (7 * 8) + 8 }, 'f', true);
        int[] nIns = new int[]{ 4, 5, 6, 7 };
        int[] nOuts = new int[]{ 5, 6, 7, 8 };
        for (int i = 0; i < 5; i++) {
            Gradient gradient = new DefaultGradient();
            Map<String, INDArray> expectedGradient = new LinkedHashMap<>();
            for (int j = 0; j < (net.getnLayers()); j++) {
                // Generate test gradient:
                INDArray wGrad = Nd4j.rand(nIns[j], nOuts[j]);
                INDArray bGrad = Nd4j.rand(1, nOuts[j]);
                String wKey = (j + "_") + (DefaultParamInitializer.WEIGHT_KEY);
                String bKey = (j + "_") + (DefaultParamInitializer.BIAS_KEY);
                gradient.setGradientFor(wKey, wGrad);
                gradient.setGradientFor(bKey, bGrad);
                // Also put copy of gradient through separate layer updaters to compare
                Gradient layerGradient = new DefaultGradient();
                layerGradient.setGradientFor(WEIGHT_KEY, wGrad.dup());
                layerGradient.setGradientFor(BIAS_KEY, bGrad.dup());
                // uArr[j].getConfig().applySchedules(0, net.getLayer(j).conf().getLearningRateByParam("W"));
                for (String s : layerGradient.gradientForVariable().keySet()) {
                    expectedGradient.put(((j + "_") + s), layerGradient.getGradientFor(s));
                }
            }
            updater.update(net, gradient, i, 0, 1, LayerWorkspaceMgr.noWorkspaces());
            Assert.assertEquals(gradient.gradientForVariable(), expectedGradient);
        }
    }

    @Test
    public void testSetGetUpdater() {
        Nd4j.getRandom().setSeed(12345L);
        double lr = 0.03;
        int nIn = 4;
        int nOut = 8;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Nesterovs(lr, 0.6)).list().layer(0, new DenseLayer.Builder().nIn(nIn).nOut(5).updater(SGD).build()).layer(1, new DenseLayer.Builder().nIn(5).nOut(6).updater(new NoOp()).build()).layer(2, new DenseLayer.Builder().nIn(6).nOut(7).updater(ADAGRAD).build()).layer(3, new OutputLayer.Builder().nIn(7).nOut(nOut).activation(SOFTMAX).updater(NESTEROVS).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.fit(Nd4j.rand(5, nIn), Nd4j.rand(5, nOut));// Fit, to initialize optimizer/updater

        Updater updater = net.getUpdater();
        Assert.assertTrue((updater instanceof MultiLayerUpdater));
        Updater newUpdater = UpdaterCreator.getUpdater(net);
        net.setUpdater(newUpdater);
        Assert.assertTrue((newUpdater == (net.getUpdater())));// Should be identical object

    }

    @Test
    public void testSetGetUpdater2() {
        // Same as above test, except that we are doing setUpdater on a new network
        Nd4j.getRandom().setSeed(12345L);
        double lr = 0.03;
        int nIn = 4;
        int nOut = 8;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Nesterovs(lr, 0.6)).list().layer(0, new DenseLayer.Builder().nIn(nIn).nOut(5).updater(SGD).build()).layer(1, new DenseLayer.Builder().nIn(5).nOut(6).updater(new NoOp()).build()).layer(2, new DenseLayer.Builder().nIn(6).nOut(7).updater(ADAGRAD).build()).layer(3, new OutputLayer.Builder().nIn(7).nOut(nOut).activation(SOFTMAX).updater(NESTEROVS).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        Updater newUpdater = UpdaterCreator.getUpdater(net);
        net.setUpdater(newUpdater);
        Assert.assertTrue((newUpdater == (net.getUpdater())));// Should be identical object

    }

    @Test
    public void testPretrain() {
        gradients = Nd4j.ones(1, ((((nIn) * (nOut)) + (nOut)) + (nIn)));
        weightGradient = gradients.get(point(0), interval(0, ((nIn) * (nOut))));
        biasGradient = gradients.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        INDArray vbiasGradient = gradients.get(point(0), interval((((nIn) * (nOut)) + (nOut)), ((((nIn) * (nOut)) + (nOut)) + (nIn))));
        gradient.setFlattenedGradient(gradients);
        // Test with pretrain = true
        double lr = 0.05;
        gradient.setGradientFor(WEIGHT_KEY, weightGradient);
        gradient.setGradientFor(BIAS_KEY, biasGradient);
        gradient.setGradientFor(VISIBLE_BIAS_KEY, vbiasGradient);
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(lr)).seed(42).layer(new AutoEncoder.Builder().lossFunction(LossFunctions.LossFunction.COSINE_PROXIMITY).activation(Activation.IDENTITY).nIn(nIn).nOut(nOut).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        BaseLayer layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        Updater updater = UpdaterCreator.getUpdater(layer);
        DefaultGradient gradientCopyPreUpdate = new DefaultGradient();
        INDArray g = gradients.dup();
        INDArray wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        INDArray bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        INDArray vbg = g.get(point(0), interval((((nIn) * (nOut)) + (nOut)), ((((nIn) * (nOut)) + (nOut)) + (nIn))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        gradientCopyPreUpdate.setGradientFor(VISIBLE_BIAS_KEY, vbg);
        updater.update(layer, gradient, (-1), 0, 1, LayerWorkspaceMgr.noWorkspaces());
        for (Map.Entry<String, INDArray> entry : gradientCopyPreUpdate.gradientForVariable().entrySet()) {
            val = entry.getValue();
            gradExpected = val.mul(lr);
            Assert.assertEquals(gradExpected, gradient.getGradientFor(entry.getKey()));
        }
        Assert.assertEquals(lr, getLearningRate(), 1.0E-4);
        // Test with pretrain == false
        gradients = Nd4j.ones(1, ((((nIn) * (nOut)) + (nOut)) + (nIn)));
        weightGradient = gradients.get(point(0), interval(0, ((nIn) * (nOut))));
        biasGradient = gradients.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        vbiasGradient = gradients.get(point(0), interval((((nIn) * (nOut)) + (nOut)), ((((nIn) * (nOut)) + (nOut)) + (nIn))));
        gradient.setGradientFor(WEIGHT_KEY, weightGradient);
        gradient.setGradientFor(BIAS_KEY, biasGradient);
        gradient.setGradientFor(VISIBLE_BIAS_KEY, vbiasGradient);
        gradient.setFlattenedGradient(gradients);
        gradientCopyPreUpdate = new DefaultGradient();
        g = gradients.dup();
        wg = g.get(point(0), interval(0, ((nIn) * (nOut))));
        bg = g.get(point(0), interval(((nIn) * (nOut)), (((nIn) * (nOut)) + (nOut))));
        vbg = g.get(point(0), interval((((nIn) * (nOut)) + (nOut)), ((((nIn) * (nOut)) + (nOut)) + (nIn))));
        gradientCopyPreUpdate.setGradientFor(WEIGHT_KEY, wg);
        gradientCopyPreUpdate.setGradientFor(BIAS_KEY, bg);
        gradientCopyPreUpdate.setGradientFor(VISIBLE_BIAS_KEY, vbg);
        gradientCopyPreUpdate.setFlattenedGradient(g);
        params = Nd4j.create(1, numParams);
        layer = ((BaseLayer) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        layer.setBackpropGradientsViewArray(gradients);
        updater = UpdaterCreator.getUpdater(layer);
        Assert.assertEquals(lr, getLearningRate(), 1.0E-4);
    }

    @Test
    public void testUpdaterBlockMlnAndCG() {
        for (int i = 0; i < 2; i++) {
            List<UpdaterBlock> blocks;
            if (i == 0) {
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).name("l0").updater(new Adam(0.5)).build()).layer(1, new DenseLayer.Builder().nIn(10).nOut(10).name("l1").updater(new Adam(0.5)).biasUpdater(new Adam(0.25)).build()).layer(2, new DenseLayer.Builder().nIn(10).nOut(10).name("l2").updater(new AdaDelta()).build()).layer(3, new DenseLayer.Builder().nIn(10).nOut(10).name("l3").updater(new AdaGrad(0.5)).build()).layer(4, new OutputLayer.Builder().nIn(10).nOut(10).name("l4").activation(SOFTMAX).updater(new AdaMax(0.5)).build()).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                MultiLayerUpdater u = ((MultiLayerUpdater) (net.getUpdater()));
                blocks = u.getUpdaterBlocks();
            } else {
                ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("l0", new DenseLayer.Builder().nIn(10).nOut(10).updater(new Adam(0.5)).build(), "in").addLayer("l1", new DenseLayer.Builder().nIn(10).nOut(10).updater(new Adam(0.5)).biasUpdater(new Adam(0.25)).build(), "l0").addLayer("l2", new DenseLayer.Builder().nIn(10).nOut(10).updater(new AdaDelta()).build(), "l1").addLayer("l3", new DenseLayer.Builder().nIn(10).nOut(10).updater(new AdaGrad(0.5)).build(), "l2").addLayer("l4", new OutputLayer.Builder().nIn(10).nOut(10).activation(SOFTMAX).updater(new AdaMax(0.5)).build(), "l3").setOutputs("l4").build();
                ComputationGraph net = new ComputationGraph(conf);
                net.init();
                ComputationGraphUpdater u = net.getUpdater();
                blocks = u.getUpdaterBlocks();
            }
            // Expect 4 blocks: (layer0 W, layer0 B, layer 1 W], [layer 1 B], [layer 2 W, layer 2 B],
            // [layer 3 W, layer 3 B], [layer 4 W, layer 4 B]
            Assert.assertEquals(5, blocks.size());
            // Check first updater block:
            UpdaterBlock ub0 = blocks.get(0);
            Assert.assertEquals(3, ub0.getLayersAndVariablesInBlock().size());
            Assert.assertEquals("l0", ub0.getLayersAndVariablesInBlock().get(0).getLayer().getConfig().getLayerName());
            Assert.assertEquals(WEIGHT_KEY, ub0.getLayersAndVariablesInBlock().get(0).getParamName());
            Assert.assertEquals("l0", ub0.getLayersAndVariablesInBlock().get(1).getLayer().getConfig().getLayerName());
            Assert.assertEquals(BIAS_KEY, ub0.getLayersAndVariablesInBlock().get(1).getParamName());
            Assert.assertEquals("l1", ub0.getLayersAndVariablesInBlock().get(2).getLayer().getConfig().getLayerName());
            Assert.assertEquals(WEIGHT_KEY, ub0.getLayersAndVariablesInBlock().get(2).getParamName());
            int nParams0 = ((10 * 10) + 10) + (10 * 10);
            Assert.assertEquals(0, ub0.getParamOffsetStart());
            Assert.assertEquals(nParams0, ub0.getParamOffsetEnd());
            int nUpdaterVals0 = 2 * nParams0;// 2x for Adam

            Assert.assertEquals(0, ub0.getUpdaterViewOffsetStart());
            Assert.assertEquals(nUpdaterVals0, ub0.getUpdaterViewOffsetEnd());
            // Check second updater block:
            UpdaterBlock ub1 = blocks.get(1);
            Assert.assertEquals(1, ub1.getLayersAndVariablesInBlock().size());
            Assert.assertEquals("l1", ub1.getLayersAndVariablesInBlock().get(0).getLayer().getConfig().getLayerName());
            Assert.assertEquals(BIAS_KEY, ub1.getLayersAndVariablesInBlock().get(0).getParamName());
            int nParams1 = 10;
            Assert.assertEquals(nParams0, ub1.getParamOffsetStart());
            Assert.assertEquals((nParams0 + nParams1), ub1.getParamOffsetEnd());
            int nUpdaterVals1 = 2 * nParams1;// 2x for Adam

            Assert.assertEquals(nUpdaterVals0, ub1.getUpdaterViewOffsetStart());
            Assert.assertEquals((nUpdaterVals0 + nUpdaterVals1), ub1.getUpdaterViewOffsetEnd());
            // Check third updater block:
            UpdaterBlock ub2 = blocks.get(2);
            Assert.assertEquals(2, ub2.getLayersAndVariablesInBlock().size());
            Assert.assertEquals("l2", ub2.getLayersAndVariablesInBlock().get(0).getLayer().getConfig().getLayerName());
            Assert.assertEquals(WEIGHT_KEY, ub2.getLayersAndVariablesInBlock().get(0).getParamName());
            Assert.assertEquals("l2", ub2.getLayersAndVariablesInBlock().get(1).getLayer().getConfig().getLayerName());
            Assert.assertEquals(BIAS_KEY, ub2.getLayersAndVariablesInBlock().get(1).getParamName());
            int nParams2 = (10 * 10) + 10;
            Assert.assertEquals((nParams0 + nParams1), ub2.getParamOffsetStart());
            Assert.assertEquals(((nParams0 + nParams1) + nParams2), ub2.getParamOffsetEnd());
            int nUpdaterVals2 = 2 * nParams2;// 2x for Adadelta

            Assert.assertEquals((nUpdaterVals0 + nUpdaterVals1), ub2.getUpdaterViewOffsetStart());
            Assert.assertEquals(((nUpdaterVals0 + nUpdaterVals1) + nUpdaterVals2), ub2.getUpdaterViewOffsetEnd());
            // Check fourth updater block:
            UpdaterBlock ub3 = blocks.get(3);
            Assert.assertEquals(2, ub3.getLayersAndVariablesInBlock().size());
            Assert.assertEquals("l3", ub3.getLayersAndVariablesInBlock().get(0).getLayer().getConfig().getLayerName());
            Assert.assertEquals(WEIGHT_KEY, ub3.getLayersAndVariablesInBlock().get(0).getParamName());
            Assert.assertEquals("l3", ub3.getLayersAndVariablesInBlock().get(1).getLayer().getConfig().getLayerName());
            Assert.assertEquals(BIAS_KEY, ub3.getLayersAndVariablesInBlock().get(1).getParamName());
            int nParams3 = (10 * 10) + 10;
            Assert.assertEquals(((nParams0 + nParams1) + nParams2), ub3.getParamOffsetStart());
            Assert.assertEquals((((nParams0 + nParams1) + nParams2) + nParams3), ub3.getParamOffsetEnd());
            int nUpdaterVals3 = nParams3;// 1x for AdaGrad

            Assert.assertEquals(((nUpdaterVals0 + nUpdaterVals1) + nUpdaterVals2), ub3.getUpdaterViewOffsetStart());
            Assert.assertEquals((((nUpdaterVals0 + nUpdaterVals1) + nUpdaterVals2) + nUpdaterVals3), ub3.getUpdaterViewOffsetEnd());
            // Check fifth updater black
            UpdaterBlock ub4 = blocks.get(4);
            Assert.assertEquals(2, ub4.getLayersAndVariablesInBlock().size());
            Assert.assertEquals("l4", ub4.getLayersAndVariablesInBlock().get(0).getLayer().getConfig().getLayerName());
            Assert.assertEquals(WEIGHT_KEY, ub4.getLayersAndVariablesInBlock().get(0).getParamName());
            Assert.assertEquals("l4", ub4.getLayersAndVariablesInBlock().get(1).getLayer().getConfig().getLayerName());
            Assert.assertEquals(BIAS_KEY, ub4.getLayersAndVariablesInBlock().get(1).getParamName());
            int nParams4 = (10 * 10) + 10;
            Assert.assertEquals((((nParams0 + nParams1) + nParams2) + nParams3), ub4.getParamOffsetStart());
            Assert.assertEquals(((((nParams0 + nParams1) + nParams2) + nParams3) + nParams4), ub4.getParamOffsetEnd());
            int nUpdaterVals4 = 2 * nParams4;// 2x for AdaGrad

            Assert.assertEquals((((nUpdaterVals0 + nUpdaterVals1) + nUpdaterVals2) + nUpdaterVals3), ub4.getUpdaterViewOffsetStart());
            Assert.assertEquals(((((nUpdaterVals0 + nUpdaterVals1) + nUpdaterVals2) + nUpdaterVals3) + nUpdaterVals4), ub4.getUpdaterViewOffsetEnd());
        }
    }

    @Test
    public void testUpdaterBlockVae() {
        List<UpdaterBlock> blocks;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Adam(0.5)).list().layer(0, new org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder.Builder().nIn(8).nOut(12).encoderLayerSizes(10, 11).decoderLayerSizes(13, 14).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        MultiLayerUpdater u = ((MultiLayerUpdater) (net.getUpdater()));
        blocks = u.getUpdaterBlocks();
        // Expect 2 blocks: Standard, and pretrain-only params
        Assert.assertEquals(2, blocks.size());
        // Check first updater block (all backprop-only params)
        UpdaterBlock ub0 = blocks.get(0);
        List<String> expParams = Arrays.asList("e0W", "e0b", "e1W", "e1b", "pZXMeanW", "pZXMeanb");
        List<String> actParams = new ArrayList<>();
        for (UpdaterBlock.ParamState vs : ub0.getLayersAndVariablesInBlock()) {
            actParams.add(vs.getParamName());
        }
        Assert.assertEquals(expParams, actParams);
        // Check second updater block
        UpdaterBlock ub1 = blocks.get(1);
        expParams = Arrays.asList("pZXLogStd2W", "pZXLogStd2b", "d0W", "d0b", "d1W", "d1b", "pXZW", "pXZb");
        actParams = new ArrayList<>();
        for (UpdaterBlock.ParamState vs : ub1.getLayersAndVariablesInBlock()) {
            actParams.add(vs.getParamName());
        }
        Assert.assertEquals(expParams, actParams);
    }

    @Test
    public void testDivisionByMinibatch1() {
        // No batch norm - should be single INDArray equal to flattened gradient view
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(new OutputLayer.Builder().nIn(10).nOut(10).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.fit(Nd4j.create(1, 10), Nd4j.create(1, 10));
        BaseMultiLayerUpdater u = ((BaseMultiLayerUpdater) (net.getUpdater()));
        List<INDArray> l = u.getGradientsForMinibatchDivision();
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        INDArray arr = l.get(0);
        Assert.assertEquals((3 * ((10 * 10) + 10)), arr.length());
        Assert.assertEquals(net.getFlattenedGradients(), arr);
    }

    @Test
    public void testDivisionByMinibatch2() {
        // With batch norm - should be multiple 'division by minibatch' array segments
        // i.e., exclude batch norm mean/variance
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new DenseLayer.Builder().nIn(10).nOut(9).build()).layer(new BatchNormalization.Builder().nOut(9).build()).layer(new DenseLayer.Builder().nIn(9).nOut(8).build()).layer(new BatchNormalization.Builder().nOut(8).build()).layer(new OutputLayer.Builder().nIn(8).nOut(7).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.fit(Nd4j.create(1, 10), Nd4j.create(1, 7));
        BaseMultiLayerUpdater u = ((BaseMultiLayerUpdater) (net.getUpdater()));
        List<INDArray> l = u.getGradientsForMinibatchDivision();
        Assert.assertNotNull(l);
        Assert.assertEquals(3, l.size());// 3 segments

        // First subset: 0_W, 0_b, 1_gamma, 1_beta           Size    10x9 + 9 + 2x9
        // Then excluding 1_mean, 1_var
        // Second subset: 2_W, 2_b, 3_gamma, 3_beta          Size    9x8 + 8 + 2x8
        // Then excluding 3_mean, 3_var
        // Third subset: 4_W, 4_b                            Size    8x7 + 7
        Assert.assertEquals((((10 * 9) + 9) + (2 * 9)), l.get(0).length());
        Assert.assertEquals((((9 * 8) + 8) + (2 * 8)), l.get(1).length());
        Assert.assertEquals(((8 * 7) + 7), l.get(2).length());
        INDArray view = getFlattenedGradientsView();
        view.assign(Nd4j.linspace(1, view.length(), view.length(), Nd4j.dataType()));
        INDArray expView1 = view.get(point(0), interval(0, (((10 * 9) + 9) + (2 * 9))));
        Assert.assertEquals(expView1, l.get(0));
        long start2 = (((10 * 9) + 9) + (2 * 9)) + (2 * 9);
        long length2 = ((9 * 8) + 8) + (2 * 8);
        INDArray expView2 = view.get(point(0), interval(start2, (start2 + length2)));
        Assert.assertEquals(expView2, l.get(1));
        long start3 = (start2 + length2) + (2 * 8);
        long length3 = (8 * 7) + 7;
        INDArray expView3 = view.get(point(0), interval(start3, (start3 + length3)));
        Assert.assertEquals(expView3, l.get(2));
    }

    @Test
    public void testDivisionByMinibatch3() throws Exception {
        // With batch norm - should be multiple 'division by minibatch' array segments
        // i.e., exclude batch norm mean/variance
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new BatchNormalization.Builder().nOut(6).build()).layer(new ConvolutionLayer.Builder().nIn(6).nOut(5).kernelSize(2, 2).build()).layer(new BatchNormalization.Builder().nOut(5).build()).layer(new ConvolutionLayer.Builder().nIn(5).nOut(4).kernelSize(2, 2).build()).layer(new BatchNormalization.Builder().nOut(4).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        BaseMultiLayerUpdater u = ((BaseMultiLayerUpdater) (net.getUpdater()));
        Method m = BaseMultiLayerUpdater.class.getDeclaredMethod("divideByMinibatch", boolean.class, Gradient.class, int.class);
        m.setAccessible(true);
        m.invoke(u, false, null, 32);
        List<INDArray> l = u.getGradientsForMinibatchDivision();
        Assert.assertNotNull(l);
        Assert.assertEquals(3, l.size());// 3 segments

        // First subset: 0_gamma, 0_beta,                    2x6
        // Then excluding 0_mean, 0_var
        // Second subset: 1_b, 1_W, 2_gamma, 2_beta          (6x5x2x2) + 5 + 2x5
        // Then excluding 2_mean, 2_var
        // Third subset: 3_b, 3_W, 4_gamma, 4_beta           (5*4*2*2) + 4 + 2*4
        // Then excluding 4_mean, 4_beta
        Assert.assertEquals((2 * 6), l.get(0).length());
        Assert.assertEquals((((((6 * 5) * 2) * 2) + 5) + (2 * 5)), l.get(1).length());
        Assert.assertEquals((((((5 * 4) * 2) * 2) + 4) + (2 * 4)), l.get(2).length());
        INDArray view = getFlattenedGradientsView();
        view.assign(Nd4j.linspace(1, view.length(), view.length(), Nd4j.dataType()));
        INDArray expView1 = view.get(point(0), interval(0, (2 * 6)));
        Assert.assertEquals(expView1, l.get(0));
        long start2 = (2 * 6) + (2 * 6);
        long length2 = ((((6 * 5) * 2) * 2) + 5) + (2 * 5);
        INDArray expView2 = view.get(point(0), interval(start2, (start2 + length2)));
        Assert.assertEquals(expView2, l.get(1));
        long start3 = (start2 + length2) + (2 * 5);
        long length3 = ((((5 * 4) * 2) * 2) + 4) + (2 * 4);
        INDArray expView3 = view.get(point(0), interval(start3, (start3 + length3)));
        Assert.assertEquals(expView3, l.get(2));
    }
}


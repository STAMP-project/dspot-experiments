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
package org.deeplearning4j.eval;


import Activation.SOFTMAX;
import Activation.TANH;
import LossFunctions.LossFunction.MCXENT;
import WeightInit.XAVIER;
import java.util.HashMap;
import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.evaluation.curves.RocCurve;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by Alex on 04/11/2016.
 */
public class ROCTest extends BaseDL4JTest {
    private static Map<Double, Double> expTPR;

    private static Map<Double, Double> expFPR;

    static {
        ROCTest.expTPR = new HashMap<>();
        double totalPositives = 5.0;
        ROCTest.expTPR.put((0 / 10.0), (5.0 / totalPositives));// All 10 predicted as class 1, of which 5 of 5 are correct

        ROCTest.expTPR.put((1 / 10.0), (5.0 / totalPositives));
        ROCTest.expTPR.put((2 / 10.0), (5.0 / totalPositives));
        ROCTest.expTPR.put((3 / 10.0), (5.0 / totalPositives));
        ROCTest.expTPR.put((4 / 10.0), (5.0 / totalPositives));
        ROCTest.expTPR.put((5 / 10.0), (5.0 / totalPositives));
        ROCTest.expTPR.put((6 / 10.0), (4.0 / totalPositives));// Threshold: 0.4 -> last 4 predicted; last 5 actual

        ROCTest.expTPR.put((7 / 10.0), (3.0 / totalPositives));
        ROCTest.expTPR.put((8 / 10.0), (2.0 / totalPositives));
        ROCTest.expTPR.put((9 / 10.0), (1.0 / totalPositives));
        ROCTest.expTPR.put((10 / 10.0), (0.0 / totalPositives));
        ROCTest.expFPR = new HashMap<>();
        double totalNegatives = 5.0;
        ROCTest.expFPR.put((0 / 10.0), (5.0 / totalNegatives));// All 10 predicted as class 1, but all 5 true negatives are predicted positive

        ROCTest.expFPR.put((1 / 10.0), (4.0 / totalNegatives));// 1 true negative is predicted as negative; 4 false positives

        ROCTest.expFPR.put((2 / 10.0), (3.0 / totalNegatives));// 2 true negatives are predicted as negative; 3 false positives

        ROCTest.expFPR.put((3 / 10.0), (2.0 / totalNegatives));
        ROCTest.expFPR.put((4 / 10.0), (1.0 / totalNegatives));
        ROCTest.expFPR.put((5 / 10.0), (0.0 / totalNegatives));
        ROCTest.expFPR.put((6 / 10.0), (0.0 / totalNegatives));
        ROCTest.expFPR.put((7 / 10.0), (0.0 / totalNegatives));
        ROCTest.expFPR.put((8 / 10.0), (0.0 / totalNegatives));
        ROCTest.expFPR.put((9 / 10.0), (0.0 / totalNegatives));
        ROCTest.expFPR.put((10 / 10.0), (0.0 / totalNegatives));
    }

    @Test
    public void RocEvalSanityCheck() {
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().weightInit(XAVIER).seed(12345).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(4).activation(TANH).build()).layer(1, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        NormalizerStandardize ns = new NormalizerStandardize();
        DataSet ds = iter.next();
        ns.fit(ds);
        ns.transform(ds);
        iter.setPreProcessor(ns);
        for (int i = 0; i < 10; i++) {
            net.fit(ds);
        }
        for (int steps : new int[]{ 32, 0 }) {
            // Steps = 0: exact
            System.out.println(("steps: " + steps));
            iter.reset();
            ds = iter.next();
            INDArray f = ds.getFeatures();
            INDArray l = ds.getLabels();
            INDArray out = net.output(f);
            // System.out.println(f);
            // System.out.println(out);
            ROCMultiClass manual = new ROCMultiClass(steps);
            manual.eval(l, out);
            iter.reset();
            ROCMultiClass roc = net.evaluateROCMultiClass(iter, steps);
            for (int i = 0; i < 3; i++) {
                double rocExp = manual.calculateAUC(i);
                double rocAct = roc.calculateAUC(i);
                Assert.assertEquals(rocExp, rocAct, 1.0E-6);
                RocCurve rc = roc.getRocCurve(i);
                RocCurve rm = manual.getRocCurve(i);
                Assert.assertEquals(rc, rm);
            }
        }
    }
}


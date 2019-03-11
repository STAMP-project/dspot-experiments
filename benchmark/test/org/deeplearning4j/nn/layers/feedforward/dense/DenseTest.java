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
package org.deeplearning4j.nn.layers.feedforward.dense;


import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by nyghtowl on 8/31/15.
 */
public class DenseTest extends BaseDL4JTest {
    private int numSamples = 150;

    private int batchSize = 150;

    private DataSetIterator iter = new IrisDataSetIterator(batchSize, numSamples);

    private DataSet data;

    @Test
    public void testDenseBiasInit() {
        DenseLayer build = new DenseLayer.Builder().nIn(1).nOut(3).biasInit(1).build();
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(build).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        Layer layer = conf.getLayer().instantiate(conf, null, 0, params, true);
        Assert.assertEquals(1, layer.getParam("b").size(0));
    }

    @Test
    public void testMLPMultiLayerPretrain() {
        // Note CNN does not do pretrain
        MultiLayerNetwork model = DenseTest.getDenseMLNConfig(false, true);
        model.fit(iter);
        MultiLayerNetwork model2 = DenseTest.getDenseMLNConfig(false, true);
        model2.fit(iter);
        iter.reset();
        DataSet test = iter.next();
        Assert.assertEquals(model.params(), model2.params());
        Evaluation eval = new Evaluation();
        INDArray output = model.output(test.getFeatures());
        eval.eval(test.getLabels(), output);
        double f1Score = eval.f1();
        Evaluation eval2 = new Evaluation();
        INDArray output2 = model2.output(test.getFeatures());
        eval2.eval(test.getLabels(), output2);
        double f1Score2 = eval2.f1();
        Assert.assertEquals(f1Score, f1Score2, 1.0E-4);
    }

    @Test
    public void testMLPMultiLayerBackprop() {
        MultiLayerNetwork model = DenseTest.getDenseMLNConfig(true, false);
        model.fit(iter);
        MultiLayerNetwork model2 = DenseTest.getDenseMLNConfig(true, false);
        model2.fit(iter);
        iter.reset();
        DataSet test = iter.next();
        Assert.assertEquals(model.params(), model2.params());
        Evaluation eval = new Evaluation();
        INDArray output = model.output(test.getFeatures());
        eval.eval(test.getLabels(), output);
        double f1Score = eval.f1();
        Evaluation eval2 = new Evaluation();
        INDArray output2 = model2.output(test.getFeatures());
        eval2.eval(test.getLabels(), output2);
        double f1Score2 = eval2.f1();
        Assert.assertEquals(f1Score, f1Score2, 1.0E-4);
    }
}


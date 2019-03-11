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
package org.deeplearning4j.nn.layers.ocnn;


import DataType.DOUBLE;
import java.io.File;
import java.util.UUID;
import org.deeplearning4j.gradientcheck.GradientCheckUtil;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


public class OCNNOutputLayerTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testLayer() {
        DataSetIterator dataSetIterator = getNormalizedIterator();
        boolean doLearningFirst = true;
        MultiLayerNetwork network = getGradientCheckNetwork(2);
        DataSet ds = dataSetIterator.next();
        INDArray arr = ds.getFeatures();
        network.setInput(arr);
        if (doLearningFirst) {
            // Run a number of iterations of learning
            network.setInput(arr);
            network.setListeners(new ScoreIterationListener(1));
            network.computeGradientAndScore();
            double scoreBefore = network.score();
            for (int j = 0; j < 10; j++)
                network.fit(ds);

            network.computeGradientAndScore();
            double scoreAfter = network.score();
            // Can't test in 'characteristic mode of operation' if not learning
            String msg = (((((("testLayer() - score did not (sufficiently) decrease during learning - activationFn=" + ((((("relu" + ", lossFn=") + "ocnn") + ", ") + "sigmoid") + ", doLearningFirst=")) + doLearningFirst) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
            // assertTrue(msg, scoreAfter <  scoreBefore);
        }
        if (OCNNOutputLayerTest.PRINT_RESULTS) {
            System.out.println((("testLayer() - activationFn=" + (((("relu" + ", lossFn=") + "ocnn") + "sigmoid") + ", doLearningFirst=")) + doLearningFirst));
            for (int j = 0; j < (network.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (network.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(network, OCNNOutputLayerTest.DEFAULT_EPS, OCNNOutputLayerTest.DEFAULT_MAX_REL_ERROR, OCNNOutputLayerTest.DEFAULT_MIN_ABS_ERROR, OCNNOutputLayerTest.PRINT_RESULTS, OCNNOutputLayerTest.RETURN_ON_FIRST_FAILURE, ds.getFeatures(), ds.getLabels());
        String msg = ("testLayer() - activationFn=" + ((((("relu" + ", lossFn=") + "ocnn") + ",=") + "sigmoid") + ", doLearningFirst=")) + doLearningFirst;
        Assert.assertTrue(msg, gradOK);
    }

    @Test
    public void testLabelProbabilities() throws Exception {
        Nd4j.getRandom().setSeed(42);
        DataSetIterator dataSetIterator = getNormalizedIterator();
        MultiLayerNetwork network = getSingleLayer();
        DataSet next = dataSetIterator.next();
        DataSet filtered = next.filterBy(new int[]{ 0, 1 });
        for (int i = 0; i < 4; i++) {
            network.setEpochCount(i);
            network.getLayerWiseConfigurations().setEpochCount(i);
            network.fit(filtered);
        }
        DataSet anomalies = next.filterBy(new int[]{ 2 });
        INDArray output = network.output(anomalies.getFeatures());
        INDArray normalOutput = network.output(anomalies.getFeatures(), false);
        Assert.assertEquals(output.lt(0.0).castTo(Nd4j.defaultFloatingPointType()).sumNumber().doubleValue(), normalOutput.eq(0.0).castTo(Nd4j.defaultFloatingPointType()).sumNumber().doubleValue(), 0.1);
        System.out.println(("Labels " + (anomalies.getLabels())));
        System.out.println(("Anomaly output " + normalOutput));
        System.out.println(output);
        INDArray normalProbs = network.output(filtered.getFeatures());
        INDArray outputForNormalSamples = network.output(filtered.getFeatures(), false);
        System.out.println(("Normal probabilities " + normalProbs));
        System.out.println(("Normal raw output " + outputForNormalSamples));
        File tmpFile = new File(testDir.getRoot(), ("tmp-file-" + (UUID.randomUUID().toString())));
        ModelSerializer.writeModel(network, tmpFile, true);
        tmpFile.deleteOnExit();
        MultiLayerNetwork multiLayerNetwork = ModelSerializer.restoreMultiLayerNetwork(tmpFile);
        Assert.assertEquals(network.params(), multiLayerNetwork.params());
        Assert.assertEquals(network.numParams(), multiLayerNetwork.numParams());
    }
}


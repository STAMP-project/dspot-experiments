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
package org.deeplearning4j.nn.multilayer;


import Activation.SIGMOID;
import Activation.TANH;
import DefaultParamInitializer.BIAS_KEY;
import DefaultParamInitializer.WEIGHT_KEY;
import java.util.Arrays;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;


public class BackPropMLPTest extends BaseDL4JTest {
    @Test
    public void testMLPTrivial() {
        // Simplest possible case: 1 hidden layer, 1 hidden neuron, batch size of 1.
        MultiLayerNetwork network = new MultiLayerNetwork(BackPropMLPTest.getIrisMLPSimpleConfig(new int[]{ 1 }, SIGMOID));
        network.setListeners(new ScoreIterationListener(1));
        network.init();
        DataSetIterator iter = new IrisDataSetIterator(1, 10);
        while (iter.hasNext())
            network.fit(iter.next());

    }

    @Test
    public void testMLP() {
        // Simple mini-batch test with multiple hidden layers
        MultiLayerConfiguration conf = BackPropMLPTest.getIrisMLPSimpleConfig(new int[]{ 5, 4, 3 }, SIGMOID);
        System.out.println(conf);
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        DataSetIterator iter = new IrisDataSetIterator(10, 100);
        while (iter.hasNext()) {
            network.fit(iter.next());
        } 
    }

    @Test
    public void testMLP2() {
        // Simple mini-batch test with multiple hidden layers
        MultiLayerConfiguration conf = BackPropMLPTest.getIrisMLPSimpleConfig(new int[]{ 5, 15, 3 }, TANH);
        System.out.println(conf);
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        DataSetIterator iter = new IrisDataSetIterator(12, 120);
        while (iter.hasNext()) {
            network.fit(iter.next());
        } 
    }

    @Test
    public void testSingleExampleWeightUpdates() {
        // Simplest possible case: 1 hidden layer, 1 hidden neuron, batch size of 1.
        // Manually calculate weight updates (entirely outside of DL4J and ND4J)
        // and compare expected and actual weights after backprop
        DataSetIterator iris = new IrisDataSetIterator(1, 10);
        MultiLayerNetwork network = new MultiLayerNetwork(BackPropMLPTest.getIrisMLPSimpleConfig(new int[]{ 1 }, SIGMOID));
        network.init();
        Layer[] layers = network.getLayers();
        final boolean printCalculations = true;
        while (iris.hasNext()) {
            DataSet data = iris.next();
            INDArray x = data.getFeatures();
            INDArray y = data.getLabels();
            float[] xFloat = BackPropMLPTest.asFloat(x);
            float[] yFloat = BackPropMLPTest.asFloat(y);
            // Do forward pass:
            INDArray l1Weights = layers[0].getParam(WEIGHT_KEY).dup();// Hidden layer

            INDArray l2Weights = layers[1].getParam(WEIGHT_KEY).dup();// Output layer

            INDArray l1Bias = layers[0].getParam(BIAS_KEY).dup();
            INDArray l2Bias = layers[1].getParam(BIAS_KEY).dup();
            float[] l1WeightsFloat = BackPropMLPTest.asFloat(l1Weights);
            float[] l2WeightsFloat = BackPropMLPTest.asFloat(l2Weights);
            float l1BiasFloat = l1Bias.getFloat(0);
            float[] l2BiasFloatArray = BackPropMLPTest.asFloat(l2Bias);
            float hiddenUnitPreSigmoid = (BackPropMLPTest.dotProduct(l1WeightsFloat, xFloat)) + l1BiasFloat;// z=w*x+b

            float hiddenUnitPostSigmoid = BackPropMLPTest.sigmoid(hiddenUnitPreSigmoid);// a=sigma(z)

            float[] outputPreSoftmax = new float[3];
            // Normally a matrix multiplication here, but only one hidden unit in this trivial example
            for (int i = 0; i < 3; i++) {
                outputPreSoftmax[i] = (hiddenUnitPostSigmoid * (l2WeightsFloat[i])) + (l2BiasFloatArray[i]);
            }
            float[] outputPostSoftmax = BackPropMLPTest.softmax(outputPreSoftmax);
            // Do backward pass:
            float[] deltaOut = BackPropMLPTest.vectorDifference(outputPostSoftmax, yFloat);// out-labels

            // deltaHidden = sigmaPrime(hiddenUnitZ) * sum_k (w_jk * \delta_k); here, only one j
            float deltaHidden = 0.0F;
            for (int i = 0; i < 3; i++)
                deltaHidden += (l2WeightsFloat[i]) * (deltaOut[i]);

            deltaHidden *= BackPropMLPTest.derivOfSigmoid(hiddenUnitPreSigmoid);
            // Calculate weight/bias updates:
            // dL/dW = delta * (activation of prev. layer)
            // dL/db = delta
            float[] dLdwOut = new float[3];
            for (int i = 0; i < (dLdwOut.length); i++)
                dLdwOut[i] = (deltaOut[i]) * hiddenUnitPostSigmoid;

            float[] dLdwHidden = new float[4];
            for (int i = 0; i < (dLdwHidden.length); i++)
                dLdwHidden[i] = deltaHidden * (xFloat[i]);

            float[] dLdbOut = deltaOut;
            float dLdbHidden = deltaHidden;
            if (printCalculations) {
                System.out.println(("deltaOut = " + (Arrays.toString(deltaOut))));
                System.out.println(("deltaHidden = " + deltaHidden));
                System.out.println(("dLdwOut = " + (Arrays.toString(dLdwOut))));
                System.out.println(("dLdbOut = " + (Arrays.toString(dLdbOut))));
                System.out.println(("dLdwHidden = " + (Arrays.toString(dLdwHidden))));
                System.out.println(("dLdbHidden = " + dLdbHidden));
            }
            // Calculate new parameters:
            // w_i = w_i - (learningRate)/(batchSize) * sum_j (dL_j/dw_i)
            // b_i = b_i - (learningRate)/(batchSize) * sum_j (dL_j/db_i)
            // Which for batch size of one (here) is simply:
            // w_i = w_i - learningRate * dL/dW
            // b_i = b_i - learningRate * dL/db
            float[] expectedL1WeightsAfter = new float[4];
            float[] expectedL2WeightsAfter = new float[3];
            float expectedL1BiasAfter = l1BiasFloat - (0.1F * dLdbHidden);
            float[] expectedL2BiasAfter = new float[3];
            for (int i = 0; i < 4; i++)
                expectedL1WeightsAfter[i] = (l1WeightsFloat[i]) - (0.1F * (dLdwHidden[i]));

            for (int i = 0; i < 3; i++)
                expectedL2WeightsAfter[i] = (l2WeightsFloat[i]) - (0.1F * (dLdwOut[i]));

            for (int i = 0; i < 3; i++)
                expectedL2BiasAfter[i] = (l2BiasFloatArray[i]) - (0.1F * (dLdbOut[i]));

            // Finally, do back-prop on network, and compare parameters vs. expected parameters
            network.fit(data);
            /* INDArray l1WeightsAfter = layers[0].getParam(DefaultParamInitializer.WEIGHT_KEY).dup();	//Hidden layer
            INDArray l2WeightsAfter = layers[1].getParam(DefaultParamInitializer.WEIGHT_KEY).dup();	//Output layer
            INDArray l1BiasAfter = layers[0].getParam(DefaultParamInitializer.BIAS_KEY).dup();
            INDArray l2BiasAfter = layers[1].getParam(DefaultParamInitializer.BIAS_KEY).dup();
            float[] l1WeightsFloatAfter = asFloat(l1WeightsAfter);
            float[] l2WeightsFloatAfter = asFloat(l2WeightsAfter);
            float l1BiasFloatAfter = l1BiasAfter.getFloat(0);
            float[] l2BiasFloatAfter = asFloat(l2BiasAfter);

            if( printCalculations) {
            System.out.println("Expected L1 weights = " + Arrays.toString(expectedL1WeightsAfter));
            System.out.println("Actual L1 weights = " + Arrays.toString(asFloat(l1WeightsAfter)));
            System.out.println("Expected L2 weights = " + Arrays.toString(expectedL2WeightsAfter));
            System.out.println("Actual L2 weights = " + Arrays.toString(asFloat(l2WeightsAfter)));
            System.out.println("Expected L1 bias = " + expectedL1BiasAfter);
            System.out.println("Actual L1 bias = " + Arrays.toString(asFloat(l1BiasAfter)));
            System.out.println("Expected L2 bias = " + Arrays.toString(expectedL2BiasAfter));
            System.out.println("Actual L2 bias = " + Arrays.toString(asFloat(l2BiasAfter)));
            }


            float eps = 1e-4f;
            assertArrayEquals(l1WeightsFloatAfter,expectedL1WeightsAfter,eps);
            assertArrayEquals(l2WeightsFloatAfter,expectedL2WeightsAfter,eps);
            assertEquals(l1BiasFloatAfter,expectedL1BiasAfter,eps);
            assertArrayEquals(l2BiasFloatAfter,expectedL2BiasAfter,eps);
             */
            System.out.println("\n\n--------------");
        } 
    }

    @Test
    public void testMLPGradientCalculation() {
        BackPropMLPTest.testIrisMiniBatchGradients(1, new int[]{ 1 }, SIGMOID);
        BackPropMLPTest.testIrisMiniBatchGradients(1, new int[]{ 5 }, SIGMOID);
        BackPropMLPTest.testIrisMiniBatchGradients(12, new int[]{ 15, 25, 10 }, SIGMOID);
        BackPropMLPTest.testIrisMiniBatchGradients(50, new int[]{ 10, 50, 200, 50, 10 }, TANH);
        BackPropMLPTest.testIrisMiniBatchGradients(150, new int[]{ 30, 50, 20 }, TANH);
    }
}


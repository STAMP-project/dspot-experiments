/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 * Modifications copyright (C) 2017 Sam Erickson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.regression;


import NeuralNetwork.ActivationFunction.LOGISTIC_SIGMOID;
import NeuralNetwork.ActivationFunction.TANH;
import org.junit.Test;


/**
 *
 *
 * @author Sam Erickson
 */
public class NeuralNetworkTest {
    public NeuralNetworkTest() {
    }

    /**
     * Test of learn method, of class NeuralNetwork.
     */
    @Test
    public void testLogisticSigmoid() {
        test(LOGISTIC_SIGMOID, "CPU", "weka/cpu.arff", 6);
        // test(NeuralNetwork.ActivationFunction.LOGISTIC_SIGMOID, "2dplanes", "weka/regression/2dplanes.arff", 6);
        test(LOGISTIC_SIGMOID, "abalone", "weka/regression/abalone.arff", 8);
        // test(NeuralNetwork.ActivationFunction.LOGISTIC_SIGMOID, "ailerons", "weka/regression/ailerons.arff", 40);
        // test(NeuralNetwork.ActivationFunction.LOGISTIC_SIGMOID, "bank32nh", "weka/regression/bank32nh.arff", 32);
        test(LOGISTIC_SIGMOID, "cal_housing", "weka/regression/cal_housing.arff", 8);
        // test(NeuralNetworkRegressor.ActivationFunction.LOGISTIC_SIGMOID, "puma8nh", "weka/regression/puma8nh.arff", 8);
        test(LOGISTIC_SIGMOID, "kin8nm", "weka/regression/kin8nm.arff", 8);
    }

    /**
     * Test of learn method, of class NeuralNetwork.
     */
    @Test
    public void testTanh() {
        test(TANH, "CPU", "weka/cpu.arff", 6);
        // test(NeuralNetworkRegressor.ActivationFunction.TANH, "2dplanes", "weka/regression/2dplanes.arff", 6);
        test(TANH, "abalone", "weka/regression/abalone.arff", 8);
        // test(NeuralNetworkRegressor.ActivationFunction.TANH, "ailerons", "weka/regression/ailerons.arff", 40);
        // test(NeuralNetworkRegressor.ActivationFunction.TANH, "bank32nh", "weka/regression/bank32nh.arff", 32);
        test(TANH, "cal_housing", "weka/regression/cal_housing.arff", 8);
        // test(NeuralNetworkRegressor.ActivationFunction.TANH, "puma8nh", "weka/regression/puma8nh.arff", 8);
        test(TANH, "kin8nm", "weka/regression/kin8nm.arff", 8);
    }
}


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
package org.deeplearning4j.spark.parameterserver.modelimport.elephas;


import org.deeplearning4j.spark.impl.graph.SparkComputationGraph;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.deeplearning4j.spark.parameterserver.BaseSparkTest;
import org.deeplearning4j.spark.parameterserver.training.SharedTrainingMaster;
import org.junit.Assert;
import org.junit.Test;


public class TestElephasImport extends BaseSparkTest {
    @Test
    public void testElephasSequentialImport() throws Exception {
        String modelPath = "modelimport/elephas/elephas_sequential.h5";
        SparkDl4jMultiLayer model = importElephasSequential(sc, modelPath);
        // System.out.println(model.getNetwork().summary());
        Assert.assertTrue(((model.getTrainingMaster()) instanceof ParameterAveragingTrainingMaster));
    }

    @Test
    public void testElephasSequentialImportAsync() throws Exception {
        String modelPath = "modelimport/elephas/elephas_sequential_async.h5";
        SparkDl4jMultiLayer model = importElephasSequential(sc, modelPath);
        // System.out.println(model.getNetwork().summary());
        Assert.assertTrue(((model.getTrainingMaster()) instanceof SharedTrainingMaster));
    }

    @Test
    public void testElephasModelImport() throws Exception {
        String modelPath = "modelimport/elephas/elephas_model.h5";
        SparkComputationGraph model = importElephasModel(sc, modelPath);
        // System.out.println(model.getNetwork().summary());
        Assert.assertTrue(((model.getTrainingMaster()) instanceof ParameterAveragingTrainingMaster));
    }

    @Test
    public void testElephasJavaAveragingModelImport() throws Exception {
        String modelPath = "modelimport/elephas/java_param_averaging_model.h5";
        SparkComputationGraph model = importElephasModel(sc, modelPath);
        // System.out.println(model.getNetwork().summary());
        assert (model.getTrainingMaster()) instanceof ParameterAveragingTrainingMaster;
    }

    @Test
    public void testElephasJavaSharingModelImport() throws Exception {
        String modelPath = "modelimport/elephas/java_param_sharing_model.h5";
        SparkComputationGraph model = importElephasModel(sc, modelPath);
        // System.out.println(model.getNetwork().summary());
        assert (model.getTrainingMaster()) instanceof SharedTrainingMaster;
    }

    @Test
    public void testElephasModelImportAsync() throws Exception {
        String modelPath = "modelimport/elephas/elephas_model_async.h5";
        SparkComputationGraph model = importElephasModel(sc, modelPath);
        // System.out.println(model.getNetwork().summary());
        Assert.assertTrue(((model.getTrainingMaster()) instanceof SharedTrainingMaster));
    }
}


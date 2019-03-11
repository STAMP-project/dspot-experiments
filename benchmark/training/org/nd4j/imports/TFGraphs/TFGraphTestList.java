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
package org.nd4j.imports.TFGraphs;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;

import static org.nd4j.imports.TFGraphs.TFGraphTestAllHelper.ExecuteWith.SAMEDIFF;


/**
 * TFGraphTestAll* will run all the checked in TF graphs and
 * compare outputs in nd4j to those generated and checked in from TF.
 * <p>
 * This file is to run a single graph or a list of graphs to aid in debug.
 * Simply change the modelNames String[] to correspond to the directory name the graph lives in
 * - eg. to run the graph for 'bias_add' i.e checked in under tf_graphs/examples/bias_add
 * <p>
 */
// @Ignore
@RunWith(Parameterized.class)
public class TFGraphTestList {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    public static String[] modelNames = new String[]{ "rnn/lstmblockcell/dynamic_b1_n5-3_ts4_noPH_clip-0.3-0.4_fB1_Tanh_noIS_noTM" };

    // change this to SAMEDIFF for samediff
    public static TFGraphTestAllHelper.ExecuteWith executeWith = SAMEDIFF;

    // public static TFGraphTestAllHelper.ExecuteWith executeWith = TFGraphTestAllHelper.ExecuteWith.LIBND4J;
    // public static TFGraphTestAllHelper.ExecuteWith executeWith = TFGraphTestAllHelper.ExecuteWith.JUST_PRINT;
    public static final String MODEL_DIR = "tf_graphs/examples";

    public static final String MODEL_FILENAME = "frozen_model.pb";

    private String modelName;

    public TFGraphTestList(String modelName) {
        this.modelName = modelName;
    }

    @Test
    public void testOutputOnly() throws IOException {
        // Nd4jCpu.Environment.getInstance().setUseMKLDNN(false);
        File dir = testDir.newFolder();
        Map<String, INDArray> inputs = TFGraphTestAllHelper.inputVars(modelName, TFGraphTestList.MODEL_DIR, dir);
        Map<String, INDArray> predictions = TFGraphTestAllHelper.outputVars(modelName, TFGraphTestList.MODEL_DIR, dir);
        Pair<Double, Double> precisionOverride = TFGraphTestAllHelper.testPrecisionOverride(modelName);
        Double maxRE = (precisionOverride == null) ? null : precisionOverride.getFirst();
        Double minAbs = (precisionOverride == null) ? null : precisionOverride.getSecond();
        TFGraphTestAllHelper.checkOnlyOutput(inputs, predictions, modelName, TFGraphTestList.MODEL_DIR, TFGraphTestList.MODEL_FILENAME, TFGraphTestList.executeWith, TFGraphTestAllHelper.LOADER, maxRE, minAbs);
    }
}


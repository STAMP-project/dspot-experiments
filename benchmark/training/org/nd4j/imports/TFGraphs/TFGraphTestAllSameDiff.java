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
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.OpValidationSuite;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;

import static org.nd4j.imports.TFGraphs.TFGraphTestAllHelper.ExecuteWith.SAMEDIFF;


/**
 * Created by susaneraly on 11/29/17.
 */
@Slf4j
@RunWith(Parameterized.class)
public class TFGraphTestAllSameDiff {
    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            log.info(("TFGraphTestAllSameDiff: Starting parameterized test: " + (description.getDisplayName())));
        }
    };

    private Map<String, INDArray> inputs;

    private Map<String, INDArray> predictions;

    private String modelName;

    private File localTestDir;

    private static final TFGraphTestAllHelper.ExecuteWith EXECUTE_WITH = SAMEDIFF;

    private static final String BASE_DIR = "tf_graphs/examples";

    private static final String MODEL_FILENAME = "frozen_model.pb";

    public static final String[] IGNORE_REGEXES = new String[]{ // Still failing: 2019/01/08 - https://github.com/deeplearning4j/deeplearning4j/issues/6322 and https://github.com/deeplearning4j/deeplearning4j/issues/6958 issue 1
    "broadcast_dynamic_shape/.*", // Failing 2019/01/08 - Issue 3 https://github.com/deeplearning4j/deeplearning4j/issues/6958
    "boolean_mask/.*", // Failing 2019/01/15 - Issue 10, https://github.com/deeplearning4j/deeplearning4j/issues/6958
    "slogdet/.*", // Failing 2019/01/15 - Issue 11 - https://github.com/deeplearning4j/deeplearning4j/issues/6958
    "bincount/.*", // Failures as of 2019/01/15: due to bad gather op - Issue 12 https://github.com/deeplearning4j/deeplearning4j/issues/6958
    "embedding_lookup/.*multiple.*", // Failing as of 2019/01/15 - Issue 13 - https://github.com/deeplearning4j/deeplearning4j/issues/6958
    "nth_element/rank1_n0", "nth_element/rank2_n0", // 2019/01/16 - Issue 14 - https://github.com/deeplearning4j/deeplearning4j/issues/6958
    "g_07", // Failing 2019/01/16 - Issue 15 https://github.com/deeplearning4j/deeplearning4j/issues/6958
    "where/cond_only.*", // scatter_nd: a few cases failing as of 2019/01/08
    "scatter_nd/rank2shape_2indices", "scatter_nd/rank3shape_2indices", // TODO floormod and truncatemod behave differently - i.e., "c" vs. "python" semantics. Need to check implementations too
    "truncatemod/.*", // 2019/01/08 - This is simply an order issue - need to account for this in test (TF gives no order guarantees)
    "topk/.*", // Still failing as of 2019/01/08 - https://github.com/deeplearning4j/deeplearning4j/issues/6447
    "cnn1d_layers/channels_first_b2_k2_s1_d2_SAME", "cnn2d_layers/channels_first_b1_k12_s1_d12_SAME", // 2019/01/16 - These have a random component so can't be validated using simple .equals... should still be compared, however to check range is sensible etc
    "alpha_dropout/.*", "layers_dropout/.*", // Still failing as of 2019/02/04 - https://github.com/deeplearning4j/deeplearning4j/issues/6464 - not sure if related to: https://github.com/deeplearning4j/deeplearning4j/issues/6447
    "cnn2d_nn/nhwc_b1_k12_s12_d12_SAME", // 2019/01/08 - No tensorflow op found for SparseTensorDenseAdd
    "confusion/.*", // 2019/01/18 - Issue 18 here: https://github.com/deeplearning4j/deeplearning4j/issues/6958
    "extractImagePatches/.*", // 2019/02/23 - Couple of tests failing (InferenceSession issues)
    "rnn/bstack/d_.*" };

    public TFGraphTestAllSameDiff(Map<String, INDArray> inputs, Map<String, INDArray> predictions, String modelName, File localTestDir) {
        this.inputs = inputs;
        this.predictions = predictions;
        this.modelName = modelName;
        this.localTestDir = localTestDir;
    }

    // (timeout = 25000L)
    @Test
    public void testOutputOnly() throws Exception {
        Nd4j.create(1);
        for (String s : TFGraphTestAllSameDiff.IGNORE_REGEXES) {
            if (modelName.matches(s)) {
                log.info("\n\tIGNORE MODEL ON REGEX: {} - regex {}", modelName, s);
                OpValidationSuite.ignoreFailing();
            }
        }
        Pair<Double, Double> precisionOverride = TFGraphTestAllHelper.testPrecisionOverride(modelName);
        Double maxRE = (precisionOverride == null) ? null : precisionOverride.getFirst();
        Double minAbs = (precisionOverride == null) ? null : precisionOverride.getSecond();
        try {
            TFGraphTestAllHelper.checkOnlyOutput(inputs, predictions, modelName, TFGraphTestAllSameDiff.BASE_DIR, TFGraphTestAllSameDiff.MODEL_FILENAME, TFGraphTestAllSameDiff.EXECUTE_WITH, TFGraphTestAllHelper.LOADER, maxRE, minAbs);
        } catch (Throwable t) {
            log.error("ERROR Executing test: {} - input keys {}", modelName, ((inputs) == null ? null : inputs.keySet()), t);
            t.printStackTrace();
            throw t;
        }
        // TFGraphTestAllHelper.checkIntermediate(inputs, modelName, EXECUTE_WITH);
    }
}


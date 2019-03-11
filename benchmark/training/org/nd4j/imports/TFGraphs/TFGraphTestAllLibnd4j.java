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

import static org.nd4j.imports.TFGraphs.TFGraphTestAllHelper.ExecuteWith.LIBND4J;


/**
 * Created by susaneraly on 11/29/17.
 */
@RunWith(Parameterized.class)
@Slf4j
public class TFGraphTestAllLibnd4j {
    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            log.info(("TFGraphTestAllLibnd4j: Starting parameterized test: " + (description.getDisplayName())));
        }
    };

    private Map<String, INDArray> inputs;

    private Map<String, INDArray> predictions;

    private String modelName;

    private File localTestDir;

    private static final TFGraphTestAllHelper.ExecuteWith EXECUTE_WITH = LIBND4J;

    private static final String BASE_DIR = "tf_graphs/examples";

    private static final String MODEL_FILENAME = "frozen_model.pb";

    private static final String[] SKIP_FOR_LIBND4J_EXEC = new String[]{ // Exceptions - need to look into:
    "alpha_dropout/.*", "layers_dropout/.*", // "losses/.*",
    // These can't pass until this is fixed: https://github.com/deeplearning4j/deeplearning4j/issues/6465#issuecomment-424209155
    // i.e., reduction ops with newFormat/keepDims args
    // "l2_normalize/.*",
    // "norm_tests/.*",
    "g_06", // JVM crashes
    "simpleif.*", "simple_cond.*", // 2019/01/24 - Failing
    "cond/cond_true", "simplewhile_.*", "simple_while", "while1/.*", "while2/a", // 2019/01/24 - TensorArray support missing at libnd4j exec level??
    "tensor_array/.*", // 2019/02/04 - Native execution exception: "Graph wasn't toposorted"
    "primitive_gru_dynamic", // 2019/02/08 - Native execution exception: "Graph wasn't toposorted". Note it's only the dynamic (while loop) RNNs
    "rnn/basiclstmcell/dynamic.*", "rnn/basicrnncell/dynamic.*", "rnn/bidir_basic/dynamic.*", "rnn/fused_adapt_basic/dynamic.*", "rnn/grucell/dynamic.*", "rnn/lstmcell/dynamic.*", "rnn/srucell/dynamic.*", // 2019/02/23 Passing for SameDiff exec, failing for libnd4j exec
    "rnn/grublockcellv2/.*", "rnn/lstmblockcell/.*", "rnn/lstmblockfusedcell/.*" };

    public TFGraphTestAllLibnd4j(Map<String, INDArray> inputs, Map<String, INDArray> predictions, String modelName, File localTestDir) {
        this.inputs = inputs;
        this.predictions = predictions;
        this.modelName = modelName;
        this.localTestDir = localTestDir;
    }

    // (timeout = 25000L)
    @Test
    public void test() throws Exception {
        Nd4j.create(1);
        for (String s : TFGraphTestAllSameDiff.IGNORE_REGEXES) {
            if (modelName.matches(s)) {
                log.info("\n\tIGNORE MODEL ON REGEX: {} - regex {}", modelName, s);
                OpValidationSuite.ignoreFailing();
            }
        }
        for (String s : TFGraphTestAllLibnd4j.SKIP_FOR_LIBND4J_EXEC) {
            if (modelName.matches(s)) {
                log.info("\n\tIGNORE MODEL ON REGEX - SKIP LIBND4J EXEC ONLY: {} - regex {}", modelName, s);
                OpValidationSuite.ignoreFailing();
            }
        }
        log.info("Starting test: {}", this.modelName);
        Pair<Double, Double> precisionOverride = TFGraphTestAllHelper.testPrecisionOverride(modelName);
        Double maxRE = (precisionOverride == null) ? null : precisionOverride.getFirst();
        Double minAbs = (precisionOverride == null) ? null : precisionOverride.getSecond();
        TFGraphTestAllHelper.checkOnlyOutput(inputs, predictions, modelName, TFGraphTestAllLibnd4j.BASE_DIR, TFGraphTestAllLibnd4j.MODEL_FILENAME, TFGraphTestAllLibnd4j.EXECUTE_WITH, TFGraphTestAllHelper.LOADER, maxRE, minAbs);
        // TFGraphTestAllHelper.checkIntermediate(inputs, modelName, EXECUTE_WITH);
    }
}


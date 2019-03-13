package org.nd4j.imports.TFGraphs;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.nd4j.imports.TFGraphs.TFGraphTestAllHelper.ExecuteWith.SAMEDIFF;


/**
 * Created by susaneraly on 12/14/17.
 * Run all the tests found in a subdir - for debug
 * eg. run all tests under tf_graphs/examples/norm_tests
 */
@RunWith(Parameterized.class)
@Slf4j
public class TFGraphTestSubDir {
    private Map<String, INDArray> inputs;

    private Map<String, INDArray> predictions;

    private String modelName;

    public static final TFGraphTestAllHelper.ExecuteWith EXECUTE_WITH = SAMEDIFF;

    // public static final TFGraphTestAllHelper.ExecuteWith EXECUTE_WITH = TFGraphTestAllHelper.ExecuteWith.LIBND4J;
    // public static final TFGraphTestAllHelper.ExecuteWith EXECUTE_WITH = TFGraphTestAllHelper.ExecuteWith.JUST_PRINT;
    private static final String[] SKIP_ARR = new String[]{ // "norm_11",
    "one_hot" };

    public static final Set<String> SKIP_SET = new HashSet<>(Arrays.asList(TFGraphTestSubDir.SKIP_ARR));

    public static String modelDir = "tf_graphs/examples/simple_run";

    public TFGraphTestSubDir(Map<String, INDArray> inputs, Map<String, INDArray> predictions, String modelName) throws IOException {
        this.inputs = inputs;
        this.predictions = predictions;
        this.modelName = modelName;
    }

    @Test
    public void test() throws Exception {
        Nd4j.create(1);
        if (TFGraphTestSubDir.SKIP_SET.contains(modelName)) {
            log.info(("\n\tSKIPPED MODEL: " + (modelName)));
            return;
        }
        TFGraphTestAllHelper.checkOnlyOutput(inputs, predictions, modelName, TFGraphTestSubDir.modelDir, TFGraphTestSubDir.EXECUTE_WITH);
        TFGraphTestAllHelper.checkIntermediate(inputs, modelName, TFGraphTestSubDir.modelDir, TFGraphTestSubDir.EXECUTE_WITH);
    }
}


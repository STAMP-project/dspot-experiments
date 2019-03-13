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

import static org.nd4j.imports.TFGraphs.TFGraphTestAllHelper.ExecuteWith.LIBND4J;


/**
 * Created by susaneraly on 11/29/17.
 */
@RunWith(Parameterized.class)
@Slf4j
public class TFGraphTestAllLibnd4j {
    private Map<String, INDArray> inputs;

    private Map<String, INDArray> predictions;

    private String modelName;

    private static final TFGraphTestAllHelper.ExecuteWith EXECUTE_WITH = LIBND4J;

    private static final String[] SKIP_ARR = new String[]{ "deep_mnist", "deep_mnist_no_dropout", "ssd_mobilenet_v1_coco", "yolov2_608x608", "inception_v3_with_softmax" };

    public static final Set<String> SKIP_SET = new HashSet<>(Arrays.asList(TFGraphTestAllLibnd4j.SKIP_ARR));

    public TFGraphTestAllLibnd4j(Map<String, INDArray> inputs, Map<String, INDArray> predictions, String modelName) throws IOException {
        this.inputs = inputs;
        this.predictions = predictions;
        this.modelName = modelName;
    }

    @Test
    public void test() throws Exception {
        Nd4j.create(1);
        if (TFGraphTestAllLibnd4j.SKIP_SET.contains(modelName)) {
            log.info(("\n\tSKIPPED MODEL: " + (modelName)));
            return;
        }
        TFGraphTestAllHelper.checkOnlyOutput(inputs, predictions, modelName, TFGraphTestAllLibnd4j.EXECUTE_WITH);
    }
}


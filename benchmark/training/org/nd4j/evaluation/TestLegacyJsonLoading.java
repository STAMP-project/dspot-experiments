package org.nd4j.evaluation;


import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.classification.ROCMultiClass;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.io.ClassPathResource;


public class TestLegacyJsonLoading {
    @Test
    public void testEvalLegacyFormat() throws Exception {
        File f = new ClassPathResource("regression_testing/eval_100b/evaluation.json").getFile();
        String s = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        // System.out.println(s);
        Evaluation e = Evaluation.fromJson(s);
        Assert.assertEquals(0.78, e.accuracy(), 1.0E-4);
        Assert.assertEquals(0.8, e.precision(), 1.0E-4);
        Assert.assertEquals(0.7753, e.f1(), 0.001);
        f = new ClassPathResource("regression_testing/eval_100b/regressionEvaluation.json").getFile();
        s = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        RegressionEvaluation re = RegressionEvaluation.fromJson(s);
        Assert.assertEquals(0.0653809, re.meanSquaredError(0), 1.0E-4);
        Assert.assertEquals(0.346236, re.meanAbsoluteError(1), 1.0E-4);
        f = new ClassPathResource("regression_testing/eval_100b/rocMultiClass.json").getFile();
        s = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        ROCMultiClass r = ROCMultiClass.fromJson(s);
        Assert.assertEquals(0.9838, r.calculateAUC(0), 1.0E-4);
        Assert.assertEquals(0.7934, r.calculateAUC(1), 1.0E-4);
    }
}


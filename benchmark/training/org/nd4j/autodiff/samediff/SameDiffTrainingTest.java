package org.nd4j.autodiff.samediff;


import DataType.FLOAT;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.evaluation.IEvaluation;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.dataset.IrisDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.weightinit.impl.XavierInitScheme;


@Slf4j
public class SameDiffTrainingTest extends BaseNd4jTest {
    public SameDiffTrainingTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void irisTrainingSanityCheck() {
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        NormalizerStandardize std = new NormalizerStandardize();
        std.fit(iter);
        iter.setPreProcessor(std);
        // for (String u : new String[]{"sgd", "adam", "nesterov", "adamax", "amsgrad"}) {
        for (String u : new String[]{ "adam", "nesterov", "adamax", "amsgrad" }) {
            Nd4j.getRandom().setSeed(12345);
            log.info(("Starting: " + u));
            SameDiff sd = SameDiff.create();
            SDVariable in = sd.placeHolder("input", FLOAT, (-1), 4);
            SDVariable label = sd.placeHolder("label", FLOAT, (-1), 3);
            SDVariable w0 = sd.var("w0", new XavierInitScheme('c', 4, 10), FLOAT, 4, 10);
            SDVariable b0 = sd.zero("b0", FLOAT, 1, 10);
            SDVariable w1 = sd.var("w1", new XavierInitScheme('c', 10, 3), FLOAT, 10, 3);
            SDVariable b1 = sd.zero("b1", FLOAT, 1, 3);
            SDVariable z0 = in.mmul(w0).add(b0);
            SDVariable a0 = sd.math().tanh(z0);
            SDVariable z1 = a0.mmul(w1).add("prediction", b1);
            SDVariable a1 = sd.nn().softmax(z1);
            SDVariable diff = sd.f().squaredDifference(a1, label);
            SDVariable lossMse = diff.mul(diff).mean();
            IUpdater updater;
            switch (u) {
                case "sgd" :
                    updater = new Sgd(0.3);
                    break;
                case "adam" :
                    updater = new Adam(0.01);
                    break;
                case "nesterov" :
                    updater = new Nesterovs(0.1);
                    break;
                case "adamax" :
                    updater = new AdaMax(0.01);
                    break;
                case "amsgrad" :
                    updater = new AMSGrad(0.01);
                    break;
                default :
                    throw new RuntimeException();
            }
            TrainingConfig conf = new TrainingConfig.Builder().l2(1.0E-4).updater(updater).dataSetFeatureMapping("input").dataSetLabelMapping("label").build();
            sd.setTrainingConfig(conf);
            sd.fit(iter, 100);
            Evaluation e = new Evaluation();
            Map<String, List<IEvaluation>> evalMap = new HashMap<>();
            evalMap.put("prediction", Collections.singletonList(e));
            sd.evaluateMultiple(iter, evalMap);
            System.out.println(e.stats());
            double acc = e.accuracy();
            Assert.assertTrue(((u + " - ") + acc), (acc >= 0.8));
        }
    }
}


package biz.k11i.xgboost.tree;


import biz.k11i.xgboost.Predictor;
import biz.k11i.xgboost.gbm.GBTree;
import biz.k11i.xgboost.util.FVec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import water.util.ArrayUtils;
import water.util.Log;


// this test demonstrates that XGBoost Predictor can be used to calculate feature contributions (Tree SHAP values)
// naive (=slow) algorithm implemented and compared to implementation in XGBoost Predictor
public class XgbPredictContribsTest {
    List<Map<Integer, Float>> trainData;

    DMatrix trainMat;

    DMatrix testMat;

    @Test
    public void testPredictContrib() throws XGBoostError, IOException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("eta", 0.1);
        params.put("max_depth", 5);
        params.put("silent", 1);
        params.put("objective", "binary:logistic");
        HashMap<String, DMatrix> watches = new HashMap<>();
        watches.put("train", trainMat);
        watches.put("test", testMat);
        // 1. Train an XGBoost model & parse using Predictor
        final Booster booster = XGBoost.train(trainMat, params, 10, watches, null, null);
        final Predictor predictor = new Predictor(new ByteArrayInputStream(booster.toByteArray()));
        final double baseMargin = XgbPredictContribsTest.baseMargin(predictor);
        // 2. Sanity check - make sure booster & predictor agree on predictions
        float[][] preds = booster.predict(trainMat, true);
        float[][] ctrbs = booster.predictContrib(trainMat, 0);// these are approximate contributions no TreeSHAP values (included for completeness)

        for (int i = 0; i < (preds.length); ++i) {
            FVec fvec = new XgbPredictContribsTest.MapBackedFVec(trainData.get(i));
            float[] pp = predictor.predict(fvec, true);
            float[] ps = preds[i];
            float[] cs = ctrbs[i];
            if (i < 10) {
                Log.info((((ps[0]) + " = Sum") + (Arrays.toString(cs).replaceAll("0.0, ", ""))));
            }
            Assert.assertEquals(ps[0], ArrayUtils.sum(cs), 1.0E-6);
            Assert.assertEquals(ps[0], pp[0], 1.0E-6);
        }
        // 3. Calculate contributions using naive (and extremely slow) approach and compare with Predictor's result
        GBTree gbTree = ((GBTree) (predictor.getBooster()));
        RegTree[] trees = gbTree.getGroupedTrees()[0];
        for (int i = 0; i < 100; i++) {
            double[] contribsNaive = new double[ctrbs[0].length];// contributions calculated naive approach (exponential complexity)

            float[] contribsPredictor = new float[ctrbs[0].length];// contributions calculated by Predictor

            FVec row = new XgbPredictContribsTest.MapBackedFVec(trainData.get(i));
            float[] predicted = predictor.predict(row, true);
            double pred = 0;
            for (int t = 0; t < (trees.length); t++) {
                final RegTreeImpl tree = ((RegTreeImpl) (trees[t]));
                final TreeSHAP treeSHAP = new TreeSHAP(tree);
                final Set<Integer> usedFeatures = XgbPredictContribsTest.usedFeatures(tree);
                final int M = usedFeatures.size();
                // A) Calculate contributions using Predictor
                treeSHAP.calculateContributions(row, 0, contribsPredictor, 0, (-1));
                // B) Calculate contributions the hard way
                Log.info(((("Tree " + t) + ": ") + usedFeatures));
                // last element is the bias term
                contribsNaive[((contribsNaive.length) - 1)] += (XgbPredictContribsTest.treeMeanValue(tree))/* tree bias */
                 + baseMargin;
                // pre-calculate expValue for each subset
                Map<Set<Integer>, Double> expVals = new HashMap<>();
                for (Set<Integer> subset : XgbPredictContribsTest.allSubsets(usedFeatures)) {
                    expVals.put(subset, XgbPredictContribsTest.expValue(tree, row, subset));
                }
                // calculate contributions using pre-calculated expValues
                for (Integer feature : usedFeatures) {
                    for (Set<Integer> subset : expVals.keySet()) {
                        if (subset.contains(feature)) {
                            Set<Integer> noFeature = new HashSet<>(subset);
                            noFeature.remove(feature);
                            double mult = ((XgbPredictContribsTest.fact(noFeature.size())) * ((long) (XgbPredictContribsTest.fact((M - (subset.size())))))) / ((double) (XgbPredictContribsTest.fact(M)));
                            double contrib = mult * ((expVals.get(subset)) - (expVals.get(noFeature)));
                            contribsNaive[feature] += contrib;
                        }
                    }
                }
                // expValue of a tree with all features marked as used should sum-up to the total prediction
                pred += XgbPredictContribsTest.expValue(tree, row, usedFeatures);
            }
            // sanity check - contributions should sum-up to the prediction
            final double predNaive = ArrayUtils.sum(contribsNaive);
            Assert.assertEquals(predicted[0], predNaive, 1.0E-6);
            Assert.assertEquals(predicted[0], pred, 1.0E-6);
            // contributions should match!
            Assert.assertArrayEquals(contribsNaive, ArrayUtils.toDouble(contribsPredictor), 1.0E-6);
        }
    }

    private static class MapBackedFVec implements FVec {
        private final Map<Integer, Float> _data;

        private MapBackedFVec(Map<Integer, Float> data) {
            _data = data;
        }

        @Override
        public float fvalue(int index) {
            Float val = _data.get(index);
            if (val == null) {
                return Float.NaN;
            }
            return val;
        }
    }
}


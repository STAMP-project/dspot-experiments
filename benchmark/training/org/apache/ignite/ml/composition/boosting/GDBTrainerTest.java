/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.composition.boosting;


import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.ignite.ml.IgniteModel;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.composition.ModelsComposition;
import org.apache.ignite.ml.composition.boosting.convergence.mean.MeanAbsValueConvergenceCheckerFactory;
import org.apache.ignite.ml.composition.predictionsaggregator.WeightedPredictionsAggregator;
import org.apache.ignite.ml.math.functions.IgniteBiFunction;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.apache.ignite.ml.tree.DecisionTreeConditionalNode;
import org.apache.ignite.ml.tree.boosting.GDBBinaryClassifierOnTreesTrainer;
import org.apache.ignite.ml.tree.boosting.GDBRegressionOnTreesTrainer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class GDBTrainerTest extends TrainerTest {
    /**
     *
     */
    @Test
    public void testFitRegression() {
        int size = 100;
        double[] xs = new double[size];
        double[] ys = new double[size];
        double from = -5.0;
        double to = 5.0;
        double step = (Math.abs((from - to))) / size;
        Map<Integer, double[]> learningSample = new HashMap<>();
        for (int i = 0; i < size; i++) {
            xs[i] = from + (step * i);
            ys[i] = 2 * (xs[i]);
            learningSample.put(i, new double[]{ xs[i], ys[i] });
        }
        GDBTrainer trainer = new GDBRegressionOnTreesTrainer(1.0, 2000, 3, 0.0).withUsingIdx(true);
        IgniteModel<Vector, Double> mdl = trainer.fit(learningSample, 1, ( k, v) -> VectorUtils.of(v[0]), ( k, v) -> v[1]);
        double mse = 0.0;
        for (int j = 0; j < size; j++) {
            double x = xs[j];
            double y = ys[j];
            double p = mdl.predict(VectorUtils.of(x));
            mse += Math.pow((y - p), 2);
        }
        mse /= size;
        Assert.assertEquals(0.0, mse, 1.0E-4);
        ModelsComposition composition = ((ModelsComposition) (mdl));
        Assert.assertTrue((!(composition.toString().isEmpty())));
        Assert.assertTrue((!(composition.toString(true).isEmpty())));
        Assert.assertTrue((!(composition.toString(false).isEmpty())));
        composition.getModels().forEach(( m) -> assertTrue((m instanceof DecisionTreeConditionalNode)));
        Assert.assertEquals(2000, composition.getModels().size());
        Assert.assertTrue(((composition.getPredictionsAggregator()) instanceof WeightedPredictionsAggregator));
        trainer = trainer.withCheckConvergenceStgyFactory(new MeanAbsValueConvergenceCheckerFactory(0.1));
        Assert.assertTrue(((trainer.fit(learningSample, 1, ( k, v) -> VectorUtils.of(v[0]), ( k, v) -> v[1]).getModels().size()) < 2000));
    }

    /**
     *
     */
    @Test
    public void testFitClassifier() {
        testClassifier(( trainer, learningSample) -> trainer.fit(learningSample, 1, ( k, v) -> VectorUtils.of(v[0]), ( k, v) -> v[1]));
    }

    /**
     *
     */
    @Test
    public void testFitClassifierWithLearningStrategy() {
        testClassifier(( trainer, learningSample) -> trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(learningSample, 1), ( k, v) -> VectorUtils.of(v[0]), ( k, v) -> v[1]));
    }

    /**
     *
     */
    @Test
    public void testUpdate() {
        int sampleSize = 100;
        double[] xs = new double[sampleSize];
        double[] ys = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            xs[i] = i;
            ys[i] = ((((int) ((xs[i]) / 10.0)) % 2) == 0) ? -1.0 : 1.0;
        }
        Map<Integer, double[]> learningSample = new HashMap<>();
        for (int i = 0; i < sampleSize; i++)
            learningSample.put(i, new double[]{ xs[i], ys[i] });

        IgniteBiFunction<Integer, double[], Vector> fExtr = ( k, v) -> VectorUtils.of(v[0]);
        IgniteBiFunction<Integer, double[], Double> lExtr = ( k, v) -> v[1];
        GDBTrainer classifTrainer = new GDBBinaryClassifierOnTreesTrainer(0.3, 500, 3, 0.0).withUsingIdx(true).withCheckConvergenceStgyFactory(new MeanAbsValueConvergenceCheckerFactory(0.3));
        GDBTrainer regressTrainer = new GDBRegressionOnTreesTrainer(0.3, 500, 3, 0.0).withUsingIdx(true).withCheckConvergenceStgyFactory(new MeanAbsValueConvergenceCheckerFactory(0.3));
        testUpdate(learningSample, fExtr, lExtr, classifTrainer);
        testUpdate(learningSample, fExtr, lExtr, regressTrainer);
    }
}


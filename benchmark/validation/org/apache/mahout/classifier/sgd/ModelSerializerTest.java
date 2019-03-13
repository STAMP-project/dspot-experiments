/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.classifier.sgd;


import Functions.IDENTITY;
import Functions.MAX;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import java.io.IOException;
import java.util.Random;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.stats.GlobalOnlineAuc;
import org.apache.mahout.math.stats.OnlineAuc;
import org.junit.Test;


public final class ModelSerializerTest extends MahoutTestCase {
    @Test
    public void onlineAucRoundtrip() throws IOException {
        RandomUtils.useTestSeed();
        OnlineAuc auc1 = new GlobalOnlineAuc();
        Random gen = RandomUtils.getRandom();
        for (int i = 0; i < 10000; i++) {
            auc1.addSample(0, gen.nextGaussian());
            auc1.addSample(1, ((gen.nextGaussian()) + 1));
        }
        assertEquals(0.76, auc1.auc(), 0.01);
        OnlineAuc auc3 = ModelSerializerTest.roundTrip(auc1, OnlineAuc.class);
        assertEquals(auc1.auc(), auc3.auc(), 0);
        for (int i = 0; i < 1000; i++) {
            auc1.addSample(0, gen.nextGaussian());
            auc1.addSample(1, ((gen.nextGaussian()) + 1));
            auc3.addSample(0, gen.nextGaussian());
            auc3.addSample(1, ((gen.nextGaussian()) + 1));
        }
        assertEquals(auc1.auc(), auc3.auc(), 0.01);
    }

    @Test
    public void onlineLogisticRegressionRoundTrip() throws IOException {
        OnlineLogisticRegression olr = new OnlineLogisticRegression(2, 5, new L1());
        ModelSerializerTest.train(olr, 100);
        OnlineLogisticRegression olr3 = ModelSerializerTest.roundTrip(olr, OnlineLogisticRegression.class);
        assertEquals(0, olr.getBeta().minus(olr3.getBeta()).aggregate(MAX, IDENTITY), 1.0E-6);
        ModelSerializerTest.train(olr, 100);
        ModelSerializerTest.train(olr3, 100);
        assertEquals(0, olr.getBeta().minus(olr3.getBeta()).aggregate(MAX, IDENTITY), 1.0E-6);
        olr.close();
        olr3.close();
    }

    @Test
    public void crossFoldLearnerRoundTrip() throws IOException {
        CrossFoldLearner learner = new CrossFoldLearner(5, 2, 5, new L1());
        ModelSerializerTest.train(learner, 100);
        CrossFoldLearner olr3 = ModelSerializerTest.roundTrip(learner, CrossFoldLearner.class);
        double auc1 = learner.auc();
        assertTrue((auc1 > 0.85));
        assertEquals(auc1, learner.auc(), 1.0E-6);
        assertEquals(auc1, olr3.auc(), 1.0E-6);
        ModelSerializerTest.train(learner, 100);
        ModelSerializerTest.train(learner, 100);
        ModelSerializerTest.train(olr3, 100);
        assertEquals(learner.auc(), learner.auc(), 0.02);
        assertEquals(learner.auc(), olr3.auc(), 0.02);
        double auc2 = learner.auc();
        assertTrue((auc2 > auc1));
        learner.close();
        olr3.close();
    }

    @ThreadLeakLingering(linger = 1000)
    @Test
    public void adaptiveLogisticRegressionRoundTrip() throws IOException {
        AdaptiveLogisticRegression learner = new AdaptiveLogisticRegression(2, 5, new L1());
        learner.setInterval(200);
        ModelSerializerTest.train(learner, 400);
        AdaptiveLogisticRegression olr3 = ModelSerializerTest.roundTrip(learner, AdaptiveLogisticRegression.class);
        double auc1 = learner.auc();
        assertTrue((auc1 > 0.85));
        assertEquals(auc1, learner.auc(), 1.0E-6);
        assertEquals(auc1, olr3.auc(), 1.0E-6);
        ModelSerializerTest.train(learner, 1000);
        ModelSerializerTest.train(learner, 1000);
        ModelSerializerTest.train(olr3, 1000);
        assertEquals(learner.auc(), learner.auc(), 0.005);
        assertEquals(learner.auc(), olr3.auc(), 0.005);
        double auc2 = learner.auc();
        assertTrue(String.format("%.3f > %.3f", auc2, auc1), (auc2 > auc1));
        learner.close();
        olr3.close();
    }
}


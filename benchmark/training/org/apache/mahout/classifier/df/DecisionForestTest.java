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
package org.apache.mahout.classifier.df;


import java.util.Random;
import org.apache.mahout.classifier.df.data.Data;
import org.apache.mahout.classifier.df.data.DataLoader;
import org.apache.mahout.classifier.df.data.Dataset;
import org.apache.mahout.classifier.df.data.DescriptorException;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


@Deprecated
public final class DecisionForestTest extends MahoutTestCase {
    private static final String[] TRAIN_DATA = new String[]{ "sunny,85,85,FALSE,no", "sunny,80,90,TRUE,no", "overcast,83,86,FALSE,yes", "rainy,70,96,FALSE,yes", "rainy,68,80,FALSE,yes", "rainy,65,70,TRUE,no", "overcast,64,65,TRUE,yes", "sunny,72,95,FALSE,no", "sunny,69,70,FALSE,yes", "rainy,75,80,FALSE,yes", "sunny,75,70,TRUE,yes", "overcast,72,90,TRUE,yes", "overcast,81,75,FALSE,yes", "rainy,71,91,TRUE,no" };

    private static final String[] TEST_DATA = new String[]{ "rainy,70,96,TRUE,-", "overcast,64,65,TRUE,-", "sunny,75,90,TRUE,-" };

    private Random rng;

    @Test
    public void testClassify() throws DescriptorException {
        // Training data
        Data[] datas = DecisionForestTest.generateTrainingDataA();
        // Build Forest
        DecisionForest forest = buildForest(datas);
        // Test data
        Dataset dataset = datas[0].getDataset();
        Data testData = DataLoader.loadData(dataset, DecisionForestTest.TEST_DATA);
        double noValue = dataset.valueOf(4, "no");
        double yesValue = dataset.valueOf(4, "yes");
        assertEquals(noValue, forest.classify(testData.getDataset(), rng, testData.get(0)), MahoutTestCase.EPSILON);
        // This one is tie-broken -- 1 is OK too
        // assertEquals(yesValue, forest.classify(testData.getDataset(), rng, testData.get(1)), EPSILON);
        assertEquals(noValue, forest.classify(testData.getDataset(), rng, testData.get(2)), MahoutTestCase.EPSILON);
    }

    @Test
    public void testClassifyData() throws DescriptorException {
        // Training data
        Data[] datas = DecisionForestTest.generateTrainingDataA();
        // Build Forest
        DecisionForest forest = buildForest(datas);
        // Test data
        Dataset dataset = datas[0].getDataset();
        Data testData = DataLoader.loadData(dataset, DecisionForestTest.TEST_DATA);
        double[][] predictions = new double[testData.size()][];
        forest.classify(testData, predictions);
        double noValue = dataset.valueOf(4, "no");
        double yesValue = dataset.valueOf(4, "yes");
        assertArrayEquals(new double[][]{ new double[]{ noValue, Double.NaN, Double.NaN }, new double[]{ noValue, yesValue, Double.NaN }, new double[]{ noValue, noValue, Double.NaN } }, predictions);
    }

    @Test
    public void testRegression() throws DescriptorException {
        Data[] datas = DecisionForestTest.generateTrainingDataB();
        DecisionForest[] forests = new DecisionForest[datas.length];
        for (int i = 0; i < (datas.length); i++) {
            Data[] subDatas = new Data[(datas.length) - 1];
            int k = 0;
            for (int j = 0; j < (datas.length); j++) {
                if (j != i) {
                    subDatas[k] = datas[j];
                    k++;
                }
            }
            forests[i] = buildForest(subDatas);
        }
        double[][] predictions = new double[datas[0].size()][];
        forests[0].classify(datas[0], predictions);
        assertArrayEquals(new double[]{ 20.0, 20.0 }, predictions[0], MahoutTestCase.EPSILON);
        assertArrayEquals(new double[]{ 39.0, 29.0 }, predictions[1], MahoutTestCase.EPSILON);
        assertArrayEquals(new double[]{ Double.NaN, 29.0 }, predictions[2], MahoutTestCase.EPSILON);
        assertArrayEquals(new double[]{ Double.NaN, 23.0 }, predictions[17], MahoutTestCase.EPSILON);
        predictions = new double[datas[1].size()][];
        forests[1].classify(datas[1], predictions);
        assertArrayEquals(new double[]{ 30.0, 29.0 }, predictions[19], MahoutTestCase.EPSILON);
        predictions = new double[datas[2].size()][];
        forests[2].classify(datas[2], predictions);
        assertArrayEquals(new double[]{ 29.0, 28.0 }, predictions[9], MahoutTestCase.EPSILON);
        assertEquals(20.0, forests[0].classify(datas[0].getDataset(), rng, datas[0].get(0)), MahoutTestCase.EPSILON);
        assertEquals(34.0, forests[0].classify(datas[0].getDataset(), rng, datas[0].get(1)), MahoutTestCase.EPSILON);
        assertEquals(29.0, forests[0].classify(datas[0].getDataset(), rng, datas[0].get(2)), MahoutTestCase.EPSILON);
    }
}


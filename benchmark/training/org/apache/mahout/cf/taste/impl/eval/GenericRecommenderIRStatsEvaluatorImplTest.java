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
package org.apache.mahout.cf.taste.impl.eval;


import GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.TasteTestCase;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


public final class GenericRecommenderIRStatsEvaluatorImplTest extends TasteTestCase {
    @Test
    public void testBoolean() throws Exception {
        DataModel model = TasteTestCase.getBooleanDataModel();
        RecommenderBuilder builder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) {
                return new org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender(dataModel, new org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity(dataModel));
            }
        };
        DataModelBuilder dataModelBuilder = new DataModelBuilder() {
            @Override
            public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
                return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
            }
        };
        RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats = evaluator.evaluate(builder, dataModelBuilder, model, null, 1, CHOOSE_THRESHOLD, 1.0);
        assertNotNull(stats);
        assertEquals(0.666666666, stats.getPrecision(), MahoutTestCase.EPSILON);
        assertEquals(0.666666666, stats.getRecall(), MahoutTestCase.EPSILON);
        assertEquals(0.666666666, stats.getF1Measure(), MahoutTestCase.EPSILON);
        assertEquals(0.666666666, stats.getFNMeasure(2.0), MahoutTestCase.EPSILON);
        assertEquals(0.666666666, stats.getNormalizedDiscountedCumulativeGain(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testIRStats() {
        IRStatistics stats = new IRStatisticsImpl(0.3, 0.1, 0.2, 0.05, 0.15);
        assertEquals(0.3, stats.getPrecision(), MahoutTestCase.EPSILON);
        assertEquals(0.1, stats.getRecall(), MahoutTestCase.EPSILON);
        assertEquals(0.15, stats.getF1Measure(), MahoutTestCase.EPSILON);
        assertEquals(0.11538461538462, stats.getFNMeasure(2.0), MahoutTestCase.EPSILON);
        assertEquals(0.05, stats.getNormalizedDiscountedCumulativeGain(), MahoutTestCase.EPSILON);
    }
}


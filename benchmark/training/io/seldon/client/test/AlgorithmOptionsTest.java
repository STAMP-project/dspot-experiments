/**
 * Seldon -- open source prediction engine
 * =======================================
 * Copyright 2011-2015 Seldon Technologies Ltd and Rummble Ltd (http://www.seldon.io/)
 *
 * *********************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * *********************************************************************************************
 */
package io.seldon.client.test;


import CFAlgorithm.CF_ITEM_COMPARATOR.MAHOUT_ITEM;
import CFAlgorithm.CF_PREDICTOR.USER_AVG;
import CFAlgorithm.CF_PREDICTOR.WEIGHTED_MEAN;
import CFAlgorithm.CF_RECOMMENDER.TRUST_ITEMBASED;
import CFAlgorithm.CF_SORTER.DEMOGRAPHICS;
import CFAlgorithm.CF_SORTER.RELEVANCE;
import CFAlgorithm.CF_STRATEGY.FIRST_SUCCESSFUL;
import CFAlgorithm.CF_STRATEGY.WEIGHTED;
import io.seldon.client.algorithm.AlgorithmOptions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by: marc on 25/11/2011 at 10:53
 */
public class AlgorithmOptionsTest extends BaseClientTest {
    @Test
    public void simpleTest() {
        AlgorithmOptions algorithmOptions = new AlgorithmOptions().withSorter(RELEVANCE).withSorter(DEMOGRAPHICS).withItemComparator(MAHOUT_ITEM).withPredictor(WEIGHTED_MEAN).withPredictor(USER_AVG).withRecommender(TRUST_ITEMBASED).withSorterStrategy(FIRST_SUCCESSFUL).withItemComparatorStrategy(WEIGHTED);
        Assert.assertEquals(algorithmOptions.toString(), "sorters:RELEVANCE|DEMOGRAPHICS,item_comparators:MAHOUT_ITEM,recommenders:TRUST_ITEMBASED,predictors:WEIGHTED_MEAN|USER_AVG,sorter_strategy:FIRST_SUCCESSFUL,item_comparator_strategy:WEIGHTED");
        Assert.assertEquals(new AlgorithmOptions().toString(), "");
    }

    @Test
    public void simpleTestWithClusterWeights() {
        AlgorithmOptions algorithmOptions = new AlgorithmOptions().withSorter(RELEVANCE).withSorter(DEMOGRAPHICS).withItemComparator(MAHOUT_ITEM).withPredictor(WEIGHTED_MEAN).withPredictor(USER_AVG).withRecommender(TRUST_ITEMBASED).withSorterStrategy(FIRST_SUCCESSFUL).withItemComparatorStrategy(WEIGHTED).withLongTermClusterWeight(2.5).withShortTermClusterWeight(1.5);
        Assert.assertEquals(algorithmOptions.toString(), "sorters:RELEVANCE|DEMOGRAPHICS,item_comparators:MAHOUT_ITEM,recommenders:TRUST_ITEMBASED,predictors:WEIGHTED_MEAN|USER_AVG,sorter_strategy:FIRST_SUCCESSFUL,item_comparator_strategy:WEIGHTED,long_term_cluster_weight:2.5,short_term_cluster_weight:1.5");
        Assert.assertEquals(new AlgorithmOptions().toString(), "");
    }
}


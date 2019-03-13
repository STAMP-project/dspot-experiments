/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scorecards;


import AggregationStrategy.AGGREGATE_SCORE;
import ScorecardPMMLExtensionNames.SCORECARD_SCORING_STRATEGY;
import org.dmg.pmml.pmml_4_2.descr.Extension;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Assert;
import org.junit.Test;

import static DrlType.INTERNAL_DECLARED_TYPES;


public class ScoringStrategiesTest {
    @Test
    public void testScoringExtension() {
        PMML pmmlDocument;
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_scoring_strategies.xls"))) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            Assert.assertNotNull(pmmlDocument);
            for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
                if (serializable instanceof Scorecard) {
                    Scorecard scorecard = ((Scorecard) (serializable));
                    Assert.assertEquals("Sample Score", scorecard.getModelName());
                    Extension extension = ScorecardPMMLUtils.getExtension(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), SCORECARD_SCORING_STRATEGY);
                    Assert.assertNotNull(extension);
                    Assert.assertEquals(extension.getValue(), AGGREGATE_SCORE.toString());
                    return;
                }
            }
        }
        Assert.fail();
    }

    @Test
    public void testAggregate() throws Exception {
        double finalScore = executeAndFetchScore("scorecards");
        // age==10 (30), validLicense==FALSE (-1)
        Assert.assertEquals(29.0, finalScore, 0.0);
    }

    @Test
    public void testAverage() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_avg");
        // age==10 (30), validLicense==FALSE (-1)
        // count = 2
        Assert.assertEquals(14.5, finalScore, 0.0);
    }

    @Test
    public void testMinimum() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_min");
        // age==10 (30), validLicense==FALSE (-1)
        Assert.assertEquals((-1.0), finalScore, 0.0);
    }

    @Test
    public void testMaximum() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_max");
        // age==10 (30), validLicense==FALSE (-1)
        Assert.assertEquals(30.0, finalScore, 0.0);
    }

    @Test
    public void testWeightedAggregate() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_w_aggregate");
        // age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        Assert.assertEquals(599.0, finalScore, 0.0);
    }

    @Test
    public void testWeightedAverage() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_w_avg");
        // age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        Assert.assertEquals(299.5, finalScore, 0.0);
    }

    @Test
    public void testWeightedMaximum() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_w_max");
        // age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        Assert.assertEquals(600.0, finalScore, 0.0);
    }

    @Test
    public void testWeightedMinimum() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_w_min");
        // age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        Assert.assertEquals((-1.0), finalScore, 0.0);
    }

    /* Tests with Initial Score */
    @Test
    public void testAggregateInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_initial_score");
        // age==10 (30), validLicense==FALSE (-1)
        // initialScore = 100
        Assert.assertEquals(129.0, finalScore, 0.0);
    }

    @Test
    public void testAverageInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_avg_initial_score");
        // age==10 (30), validLicense==FALSE (-1)
        // count = 2
        // initialScore = 100
        Assert.assertEquals(114.5, finalScore, 0.0);
    }

    @Test
    public void testMinimumInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_min_initial_score");
        // age==10 (30), validLicense==FALSE (-1)
        // initialScore = 100
        Assert.assertEquals(99.0, finalScore, 0.0);
    }

    @Test
    public void testMaximumInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_max_initial_score");
        // age==10 (30), validLicense==FALSE (-1)
        // initialScore = 100
        Assert.assertEquals(130.0, finalScore, 0.0);
    }

    @Test
    public void testWeightedAggregateInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_w_aggregate_initial");
        // age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        // initialScore = 100
        Assert.assertEquals(699.0, finalScore, 0.0);
    }

    @Test
    public void testWeightedAverageInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_w_avg_initial");
        // age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        // initialScore = 100
        Assert.assertEquals(399.5, finalScore, 0.0);
    }

    @Test
    public void testWeightedMaximumInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_w_max_initial");
        // age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        // initialScore = 100
        Assert.assertEquals(700.0, finalScore, 0.0);
    }

    @Test
    public void testWeightedMinimumInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_w_min_initial");
        // age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        // initialScore = 100
        Assert.assertEquals(99.0, finalScore, 0.0);
    }
}


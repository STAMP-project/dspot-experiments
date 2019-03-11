/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.score.buildin.bendable;


import InitializingScoreTrendLevel.ONLY_DOWN;
import InitializingScoreTrendLevel.ONLY_UP;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;


public class BendableScoreDefinitionTest {
    @Test
    public void getLevelsSize() {
        Assert.assertEquals(2, new BendableScoreDefinition(1, 1).getLevelsSize());
        Assert.assertEquals(7, new BendableScoreDefinition(3, 4).getLevelsSize());
        Assert.assertEquals(7, new BendableScoreDefinition(4, 3).getLevelsSize());
        Assert.assertEquals(5, new BendableScoreDefinition(0, 5).getLevelsSize());
        Assert.assertEquals(5, new BendableScoreDefinition(5, 0).getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        Assert.assertArrayEquals(new String[]{ "hard 0 score", "soft 0 score" }, new BendableScoreDefinition(1, 1).getLevelLabels());
        Assert.assertArrayEquals(new String[]{ "hard 0 score", "hard 1 score", "hard 2 score", "soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score" }, new BendableScoreDefinition(3, 4).getLevelLabels());
        Assert.assertArrayEquals(new String[]{ "hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "soft 0 score", "soft 1 score", "soft 2 score" }, new BendableScoreDefinition(4, 3).getLevelLabels());
        Assert.assertArrayEquals(new String[]{ "soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score", "soft 4 score" }, new BendableScoreDefinition(0, 5).getLevelLabels());
        Assert.assertArrayEquals(new String[]{ "hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "hard 4 score" }, new BendableScoreDefinition(5, 0).getLevelLabels());
    }

    @Test
    public void getFeasibleLevelsSize() {
        Assert.assertEquals(1, new BendableScoreDefinition(1, 1).getFeasibleLevelsSize());
        Assert.assertEquals(3, new BendableScoreDefinition(3, 4).getFeasibleLevelsSize());
        Assert.assertEquals(4, new BendableScoreDefinition(4, 3).getFeasibleLevelsSize());
        Assert.assertEquals(0, new BendableScoreDefinition(0, 5).getFeasibleLevelsSize());
        Assert.assertEquals(5, new BendableScoreDefinition(5, 0).getFeasibleLevelsSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createScoreWithIllegalArgument() {
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(2, 3);
        bendableScoreDefinition.createScore(1, 2, 3);
    }

    @Test
    public void createScore() {
        int hardLevelSize = 3;
        int softLevelSize = 2;
        int levelSize = hardLevelSize + softLevelSize;
        int[] scores = new int[levelSize];
        for (int i = 0; i < levelSize; i++) {
            scores[i] = i;
        }
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardLevelSize, softLevelSize);
        BendableScore bendableScore = bendableScoreDefinition.createScore(scores);
        Assert.assertEquals(hardLevelSize, bendableScore.getHardLevelsSize());
        Assert.assertEquals(softLevelSize, bendableScore.getSoftLevelsSize());
        for (int i = 0; i < levelSize; i++) {
            if (i < hardLevelSize) {
                Assert.assertEquals(scores[i], bendableScore.getHardScore(i));
            } else {
                Assert.assertEquals(scores[i], bendableScore.getSoftScore((i - hardLevelSize)));
            }
        }
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(2, 3);
        BendableScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 5), scoreDefinition.createScore((-1), (-2), (-3), (-4), (-5)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals(Integer.MAX_VALUE, optimisticBound.getHardScore(0));
        Assert.assertEquals(Integer.MAX_VALUE, optimisticBound.getHardScore(1));
        Assert.assertEquals(Integer.MAX_VALUE, optimisticBound.getSoftScore(0));
        Assert.assertEquals(Integer.MAX_VALUE, optimisticBound.getSoftScore(1));
        Assert.assertEquals(Integer.MAX_VALUE, optimisticBound.getSoftScore(2));
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(2, 3);
        BendableScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 5), scoreDefinition.createScore((-1), (-2), (-3), (-4), (-5)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals((-1), optimisticBound.getHardScore(0));
        Assert.assertEquals((-2), optimisticBound.getHardScore(1));
        Assert.assertEquals((-3), optimisticBound.getSoftScore(0));
        Assert.assertEquals((-4), optimisticBound.getSoftScore(1));
        Assert.assertEquals((-5), optimisticBound.getSoftScore(2));
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(2, 3);
        BendableScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 5), scoreDefinition.createScore((-1), (-2), (-3), (-4), (-5)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals((-1), pessimisticBound.getHardScore(0));
        Assert.assertEquals((-2), pessimisticBound.getHardScore(1));
        Assert.assertEquals((-3), pessimisticBound.getSoftScore(0));
        Assert.assertEquals((-4), pessimisticBound.getSoftScore(1));
        Assert.assertEquals((-5), pessimisticBound.getSoftScore(2));
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(2, 3);
        BendableScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 5), scoreDefinition.createScore((-1), (-2), (-3), (-4), (-5)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals(Integer.MIN_VALUE, pessimisticBound.getHardScore(0));
        Assert.assertEquals(Integer.MIN_VALUE, pessimisticBound.getHardScore(1));
        Assert.assertEquals(Integer.MIN_VALUE, pessimisticBound.getSoftScore(0));
        Assert.assertEquals(Integer.MIN_VALUE, pessimisticBound.getSoftScore(1));
        Assert.assertEquals(Integer.MIN_VALUE, pessimisticBound.getSoftScore(2));
    }
}


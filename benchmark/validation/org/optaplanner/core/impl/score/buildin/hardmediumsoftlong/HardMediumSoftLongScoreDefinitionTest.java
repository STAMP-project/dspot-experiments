/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.buildin.hardmediumsoftlong;


import InitializingScoreTrendLevel.ONLY_DOWN;
import InitializingScoreTrendLevel.ONLY_UP;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;


public class HardMediumSoftLongScoreDefinitionTest {
    @Test
    public void getLevelsSize() {
        Assert.assertEquals(3, new HardMediumSoftLongScoreDefinition().getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        Assert.assertArrayEquals(new String[]{ "hard score", "medium score", "soft score" }, new HardMediumSoftLongScoreDefinition().getLevelLabels());
    }

    @Test
    public void getFeasibleLevelsSize() {
        Assert.assertEquals(1, new HardMediumSoftLongScoreDefinition().getFeasibleLevelsSize());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        HardMediumSoftLongScoreDefinition scoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 3), HardMediumSoftLongScore.of((-1L), (-2L), (-3L)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals(Long.MAX_VALUE, optimisticBound.getHardScore());
        Assert.assertEquals(Long.MAX_VALUE, optimisticBound.getMediumScore());
        Assert.assertEquals(Long.MAX_VALUE, optimisticBound.getSoftScore());
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        HardMediumSoftLongScoreDefinition scoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 3), HardMediumSoftLongScore.of((-1L), (-2L), (-3L)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals((-1L), optimisticBound.getHardScore());
        Assert.assertEquals((-2L), optimisticBound.getMediumScore());
        Assert.assertEquals((-3L), optimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        HardMediumSoftLongScoreDefinition scoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 3), HardMediumSoftLongScore.of((-1L), (-2L), (-3L)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals((-1L), pessimisticBound.getHardScore());
        Assert.assertEquals((-2L), pessimisticBound.getMediumScore());
        Assert.assertEquals((-3L), pessimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        HardMediumSoftLongScoreDefinition scoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 3), HardMediumSoftLongScore.of((-1L), (-2L), (-3L)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals(Long.MIN_VALUE, pessimisticBound.getHardScore());
        Assert.assertEquals(Long.MIN_VALUE, pessimisticBound.getMediumScore());
        Assert.assertEquals(Long.MIN_VALUE, pessimisticBound.getSoftScore());
    }
}


/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.buildin.hardsoft;


import InitializingScoreTrendLevel.ONLY_DOWN;
import InitializingScoreTrendLevel.ONLY_UP;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreDefinitionTest;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;


public class HardSoftScoreDefinitionTest extends AbstractScoreDefinitionTest {
    @Test
    public void getLevelsSize() {
        Assert.assertEquals(2, new HardSoftScoreDefinition().getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        Assert.assertArrayEquals(new String[]{ "hard score", "soft score" }, new HardSoftScoreDefinition().getLevelLabels());
    }

    @Test
    public void getFeasibleLevelsSize() {
        Assert.assertEquals(1, new HardSoftScoreDefinition().getFeasibleLevelsSize());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 2), HardSoftScore.of((-1), (-2)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals(Integer.MAX_VALUE, optimisticBound.getHardScore());
        Assert.assertEquals(Integer.MAX_VALUE, optimisticBound.getSoftScore());
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 2), HardSoftScore.of((-1), (-2)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals((-1), optimisticBound.getHardScore());
        Assert.assertEquals((-2), optimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 2), HardSoftScore.of((-1), (-2)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals((-1), pessimisticBound.getHardScore());
        Assert.assertEquals((-2), pessimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 2), HardSoftScore.of((-1), (-2)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals(Integer.MIN_VALUE, pessimisticBound.getHardScore());
        Assert.assertEquals(Integer.MIN_VALUE, pessimisticBound.getSoftScore());
    }
}


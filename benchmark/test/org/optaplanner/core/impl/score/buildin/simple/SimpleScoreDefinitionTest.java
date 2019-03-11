/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.buildin.simple;


import InitializingScoreTrendLevel.ONLY_DOWN;
import InitializingScoreTrendLevel.ONLY_UP;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;


public class SimpleScoreDefinitionTest {
    @Test
    public void getLevelsSize() {
        Assert.assertEquals(1, new SimpleScoreDefinition().getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        Assert.assertArrayEquals(new String[]{ "score" }, new SimpleScoreDefinition().getLevelLabels());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 1), SimpleScore.of((-1)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals(Integer.MAX_VALUE, optimisticBound.getScore());
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 1), SimpleScore.of((-1)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals((-1), optimisticBound.getScore());
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 1), SimpleScore.of((-1)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals((-1), pessimisticBound.getScore());
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 1), SimpleScore.of((-1)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals(Integer.MIN_VALUE, pessimisticBound.getScore());
    }
}


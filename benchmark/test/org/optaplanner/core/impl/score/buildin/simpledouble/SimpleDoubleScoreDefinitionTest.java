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
package org.optaplanner.core.impl.score.buildin.simpledouble;


import InitializingScoreTrendLevel.ONLY_DOWN;
import InitializingScoreTrendLevel.ONLY_UP;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;


public class SimpleDoubleScoreDefinitionTest {
    @Test
    public void getLevelSize() {
        Assert.assertEquals(1, new SimpleDoubleScoreDefinition().getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        Assert.assertArrayEquals(new String[]{ "score" }, new SimpleDoubleScoreDefinition().getLevelLabels());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 1), SimpleDoubleScore.of((-1.7)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals(Double.POSITIVE_INFINITY, optimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 1), SimpleDoubleScore.of((-1.7)));
        Assert.assertEquals(0, optimisticBound.getInitScore());
        Assert.assertEquals((-1.7), optimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_UP, 1), SimpleDoubleScore.of((-1.7)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals((-1.7), pessimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(InitializingScoreTrend.buildUniformTrend(ONLY_DOWN, 1), SimpleDoubleScore.of((-1.7)));
        Assert.assertEquals(0, pessimisticBound.getInitScore());
        Assert.assertEquals(Double.NEGATIVE_INFINITY, pessimisticBound.getScore(), 0.0);
    }
}


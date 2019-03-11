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
package org.optaplanner.core.impl.constructionheuristic.decider.forager;


import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;


public class DefaultConstructionHeuristicForagerTest<Solution_> {
    @Test
    public void checkPickEarlyNever() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(ConstructionHeuristicPickEarlyType.NEVER);
        ConstructionHeuristicStepScope<Solution_> stepScope = buildStepScope(SimpleScore.ofUninitialized((-8), (-100)));
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized((-7), (-110))));
        Assert.assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized((-7), (-100))));
        Assert.assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized((-7), (-90))));
        Assert.assertEquals(false, forager.isQuitEarly());
    }

    @Test
    public void checkPickEarlyFirstNonDeterioratingScore() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(ConstructionHeuristicPickEarlyType.FIRST_NON_DETERIORATING_SCORE);
        ConstructionHeuristicStepScope<Solution_> stepScope = buildStepScope(SimpleScore.ofUninitialized((-8), (-100)));
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized((-7), (-110))));
        Assert.assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized((-7), (-100))));
        Assert.assertEquals(true, forager.isQuitEarly());
    }

    @Test
    public void checkPickEarlyFirstFeasibleScore() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE);
        ConstructionHeuristicStepScope<Solution_> stepScope = buildStepScope(HardSoftScore.ofUninitialized((-8), 0, (-100)));
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized((-7), (-1), (-110))));
        Assert.assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized((-7), (-1), (-90))));
        Assert.assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized((-7), 0, (-110))));
        Assert.assertEquals(true, forager.isQuitEarly());
    }

    @Test
    public void checkPickEarlyFirstFeasibleScoreOrNonDeterioratingHard() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE_OR_NON_DETERIORATING_HARD);
        ConstructionHeuristicStepScope<Solution_> stepScope = buildStepScope(HardSoftScore.ofUninitialized((-8), (-10), (-100)));
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized((-7), (-11), (-110))));
        Assert.assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized((-7), (-11), (-90))));
        Assert.assertEquals(false, forager.isQuitEarly());
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized((-7), (-10), (-110))));
        Assert.assertEquals(true, forager.isQuitEarly());
    }
}


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
package org.optaplanner.core.impl.localsearch.decider.forager;


import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchPickEarlyType;
import org.optaplanner.core.impl.localsearch.decider.forager.finalist.HighestScoreFinalistPodium;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;


public class AcceptedLocalSearchForagerTest {
    @Test
    public void pickMoveMaxScoreAccepted() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(), LocalSearchPickEarlyType.NEVER, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of((-20)), true);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of((-1)), false);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of((-20)), false);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of((-2)), true);
        LocalSearchMoveScope<TestdataSolution> e = createMoveScope(stepScope, SimpleScore.of((-300)), true);
        // Do stuff
        forager.addMove(a);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(e);
        Assert.assertFalse(forager.isQuitEarly());
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        // Post conditions
        Assert.assertSame(d, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveMaxScoreUnaccepted() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(), LocalSearchPickEarlyType.NEVER, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of((-20)), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of((-1)), false);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of((-20)), false);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of((-2)), false);
        LocalSearchMoveScope<TestdataSolution> e = createMoveScope(stepScope, SimpleScore.of((-300)), false);
        // Do stuff
        forager.addMove(a);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(e);
        Assert.assertFalse(forager.isQuitEarly());
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        // Post conditions
        Assert.assertSame(b, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveFirstBestScoreImproving() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(), LocalSearchPickEarlyType.FIRST_BEST_SCORE_IMPROVING, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of((-1)), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of((-20)), true);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of((-300)), true);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of((-1)), true);
        // Do stuff
        forager.addMove(a);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        Assert.assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        Assert.assertSame(d, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveFirstLastStepScoreImproving() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(), LocalSearchPickEarlyType.FIRST_LAST_STEP_SCORE_IMPROVING, Integer.MAX_VALUE, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of((-1)), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of((-300)), true);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of((-4000)), true);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of((-20)), true);
        // Do stuff
        forager.addMove(a);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        Assert.assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        Assert.assertSame(d, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveAcceptedBreakTieRandomly() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(), LocalSearchPickEarlyType.NEVER, 4, true);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of((-20)), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of((-1)), true);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of((-1)), true);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of((-20)), true);
        LocalSearchMoveScope<TestdataSolution> e = createMoveScope(stepScope, SimpleScore.of((-1)), true);
        // Do stuff
        forager.addMove(a);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(e);
        Assert.assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        Assert.assertSame(c, pickedScope);
        forager.phaseEnded(phaseScope);
    }

    @Test
    public void pickMoveAcceptedBreakTieFirst() {
        // Setup
        LocalSearchForager forager = new AcceptedLocalSearchForager(new HighestScoreFinalistPodium(), LocalSearchPickEarlyType.NEVER, 4, false);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = createPhaseScope();
        forager.phaseStarted(phaseScope);
        LocalSearchStepScope<TestdataSolution> stepScope = new LocalSearchStepScope(phaseScope);
        forager.stepStarted(stepScope);
        // Pre conditions
        LocalSearchMoveScope<TestdataSolution> a = createMoveScope(stepScope, SimpleScore.of((-20)), false);
        LocalSearchMoveScope<TestdataSolution> b = createMoveScope(stepScope, SimpleScore.of((-1)), true);
        LocalSearchMoveScope<TestdataSolution> c = createMoveScope(stepScope, SimpleScore.of((-1)), true);
        LocalSearchMoveScope<TestdataSolution> d = createMoveScope(stepScope, SimpleScore.of((-20)), true);
        LocalSearchMoveScope<TestdataSolution> e = createMoveScope(stepScope, SimpleScore.of((-1)), true);
        // Do stuff
        forager.addMove(a);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(b);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(c);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(d);
        Assert.assertFalse(forager.isQuitEarly());
        forager.addMove(e);
        Assert.assertTrue(forager.isQuitEarly());
        // Post conditions
        LocalSearchMoveScope pickedScope = forager.pickMove(stepScope);
        Assert.assertSame(b, pickedScope);
        forager.phaseEnded(phaseScope);
    }
}


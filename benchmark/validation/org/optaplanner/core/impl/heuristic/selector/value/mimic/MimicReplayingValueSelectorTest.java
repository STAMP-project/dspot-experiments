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
package org.optaplanner.core.impl.heuristic.selector.value.mimic;


import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class MimicReplayingValueSelectorTest {
    @Test
    public void originalSelection() {
        EntityIndependentValueSelector childValueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(TestdataEntity.class, "value", new TestdataValue("v1"), new TestdataValue("v2"), new TestdataValue("v3"));
        MimicRecordingValueSelector recordingValueSelector = new MimicRecordingValueSelector(childValueSelector);
        MimicReplayingValueSelector replayingValueSelector = new MimicReplayingValueSelector(recordingValueSelector);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        recordingValueSelector.solvingStarted(solverScope);
        replayingValueSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        recordingValueSelector.phaseStarted(phaseScopeA);
        replayingValueSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        recordingValueSelector.stepStarted(stepScopeA1);
        replayingValueSelector.stepStarted(stepScopeA1);
        runOriginalAsserts(recordingValueSelector, replayingValueSelector);
        recordingValueSelector.stepEnded(stepScopeA1);
        replayingValueSelector.stepEnded(stepScopeA1);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        recordingValueSelector.stepStarted(stepScopeA2);
        replayingValueSelector.stepStarted(stepScopeA2);
        runOriginalAsserts(recordingValueSelector, replayingValueSelector);
        recordingValueSelector.stepEnded(stepScopeA2);
        replayingValueSelector.stepEnded(stepScopeA2);
        recordingValueSelector.phaseEnded(phaseScopeA);
        replayingValueSelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        recordingValueSelector.phaseStarted(phaseScopeB);
        replayingValueSelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        recordingValueSelector.stepStarted(stepScopeB1);
        replayingValueSelector.stepStarted(stepScopeB1);
        runOriginalAsserts(recordingValueSelector, replayingValueSelector);
        recordingValueSelector.stepEnded(stepScopeB1);
        replayingValueSelector.stepEnded(stepScopeB1);
        recordingValueSelector.phaseEnded(phaseScopeB);
        replayingValueSelector.phaseEnded(phaseScopeB);
        recordingValueSelector.solvingEnded(solverScope);
        replayingValueSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(childValueSelector, 1, 2, 3);
        Mockito.verify(childValueSelector, Mockito.times(3)).iterator();
    }
}


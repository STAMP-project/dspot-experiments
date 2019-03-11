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
package org.optaplanner.core.impl.heuristic.selector.move.decorator;


import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class SelectedCountLimitMoveSelectorTest {
    @Test
    public void selectSizeLimitLowerThanSelectorSize() {
        MoveSelector childMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class, new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3"), new DummyMove("a4"), new DummyMove("a5"));
        MoveSelector moveSelector = new SelectedCountLimitMoveSelector(childMoveSelector, 3L);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeA1);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeA2);
        moveSelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeB2);
        AbstractStepScope stepScopeB3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeB3);
        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(childMoveSelector, 1, 2, 5);
        Mockito.verify(childMoveSelector, Mockito.times(5)).iterator();
        Mockito.verify(childMoveSelector, Mockito.times(5)).getSize();
    }

    @Test
    public void selectSizeLimitHigherThanSelectorSize() {
        MoveSelector childMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class, new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3"));
        MoveSelector moveSelector = new SelectedCountLimitMoveSelector(childMoveSelector, 5L);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeA1);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeA2);
        moveSelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeB2);
        AbstractStepScope stepScopeB3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        moveSelector.stepEnded(stepScopeB3);
        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(childMoveSelector, 1, 2, 5);
        Mockito.verify(childMoveSelector, Mockito.times(5)).iterator();
        Mockito.verify(childMoveSelector, Mockito.times(5)).getSize();
    }
}


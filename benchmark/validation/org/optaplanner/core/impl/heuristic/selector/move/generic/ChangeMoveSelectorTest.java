/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.heuristic.selector.move.generic;


import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class ChangeMoveSelectorTest {
    @Test
    public void original() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class, new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));
        ValueSelector valueSelector = SelectorTestUtils.mockValueSelector(TestdataEntity.class, "value", new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));
        ChangeMoveSelector moveSelector = new ChangeMoveSelector(entitySelector, valueSelector, false);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3");
        moveSelector.stepEnded(stepScopeA1);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3");
        moveSelector.stepEnded(stepScopeA2);
        moveSelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3");
        moveSelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3");
        moveSelector.stepEnded(stepScopeB2);
        AbstractStepScope stepScopeB3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector, "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3");
        moveSelector.stepEnded(stepScopeB3);
        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(entitySelector, 1, 2, 5);
        PlannerAssert.verifyPhaseLifecycle(valueSelector, 1, 2, 5);
    }

    @Test
    public void emptyEntitySelectorOriginal() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        ValueSelector valueSelector = SelectorTestUtils.mockValueSelector(TestdataEntity.class, "value", new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));
        ChangeMoveSelector moveSelector = new ChangeMoveSelector(entitySelector, valueSelector, false);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeA1);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeA2);
        moveSelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeB2);
        AbstractStepScope stepScopeB3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeB3);
        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(entitySelector, 1, 2, 5);
        PlannerAssert.verifyPhaseLifecycle(valueSelector, 1, 2, 5);
    }

    @Test
    public void emptyValueSelectorOriginal() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class, new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));
        ValueSelector valueSelector = SelectorTestUtils.mockValueSelector(TestdataEntity.class, "value");
        ChangeMoveSelector moveSelector = new ChangeMoveSelector(entitySelector, valueSelector, false);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeA1);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeA2);
        moveSelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeB2);
        AbstractStepScope stepScopeB3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        PlannerAssert.assertAllCodesOfMoveSelector(moveSelector);
        moveSelector.stepEnded(stepScopeB3);
        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(entitySelector, 1, 2, 5);
        PlannerAssert.verifyPhaseLifecycle(valueSelector, 1, 2, 5);
    }

    @Test
    public void randomSelection() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class, new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));
        ValueSelector valueSelector = SelectorTestUtils.mockValueSelector(TestdataEntity.class, "value", new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));
        ChangeMoveSelector moveSelector = new ChangeMoveSelector(entitySelector, valueSelector, true);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        PlannerAssert.assertCodesOfNeverEndingMoveSelector(moveSelector, "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1");
        moveSelector.stepEnded(stepScopeA1);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        PlannerAssert.assertCodesOfNeverEndingMoveSelector(moveSelector, "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1");
        moveSelector.stepEnded(stepScopeA2);
        moveSelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        PlannerAssert.assertCodesOfNeverEndingMoveSelector(moveSelector, "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1");
        moveSelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        PlannerAssert.assertCodesOfNeverEndingMoveSelector(moveSelector, "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1");
        moveSelector.stepEnded(stepScopeB2);
        AbstractStepScope stepScopeB3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        PlannerAssert.assertCodesOfNeverEndingMoveSelector(moveSelector, "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1");
        moveSelector.stepEnded(stepScopeB3);
        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(entitySelector, 1, 2, 5);
        PlannerAssert.verifyPhaseLifecycle(valueSelector, 1, 2, 5);
    }
}


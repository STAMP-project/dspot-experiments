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
package org.optaplanner.core.impl.constructionheuristic.placer.entity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedEntityPlacer;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicRecordingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class QueuedEntityPlacerTest extends AbstractEntityPlacerTest {
    @Test
    public void oneMoveSelector() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class, new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"));
        MimicRecordingEntitySelector recordingEntitySelector = new MimicRecordingEntitySelector(entitySelector);
        ValueSelector valueSelector = SelectorTestUtils.mockValueSelector(TestdataEntity.class, "value", new TestdataValue("1"), new TestdataValue("2"));
        MoveSelector moveSelector = new org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector(new org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector(recordingEntitySelector), valueSelector, false);
        QueuedEntityPlacer placer = new QueuedEntityPlacer(recordingEntitySelector, Collections.singletonList(moveSelector));
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        placer.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();
        Assert.assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        AbstractEntityPlacerTest.assertEntityPlacement(placementIterator.next(), "a", "1", "2");
        placer.stepEnded(stepScopeA1);
        Assert.assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        AbstractEntityPlacerTest.assertEntityPlacement(placementIterator.next(), "b", "1", "2");
        placer.stepEnded(stepScopeA2);
        Assert.assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA3.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA3);
        AbstractEntityPlacerTest.assertEntityPlacement(placementIterator.next(), "c", "1", "2");
        placer.stepEnded(stepScopeA3);
        Assert.assertFalse(placementIterator.hasNext());
        placer.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeB);
        placementIterator = placer.iterator();
        Assert.assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        placer.stepStarted(stepScopeB1);
        AbstractEntityPlacerTest.assertEntityPlacement(placementIterator.next(), "a", "1", "2");
        placer.stepEnded(stepScopeB1);
        placer.phaseEnded(phaseScopeB);
        placer.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(entitySelector, 1, 2, 4);
        PlannerAssert.verifyPhaseLifecycle(valueSelector, 1, 2, 4);
    }

    @Test
    public void multiQueuedMoveSelector() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataMultiVarEntity.class, new TestdataMultiVarEntity("a"), new TestdataMultiVarEntity("b"));
        MimicRecordingEntitySelector recordingEntitySelector = new MimicRecordingEntitySelector(entitySelector);
        ValueSelector primaryValueSelector = SelectorTestUtils.mockValueSelector(TestdataMultiVarEntity.class, "primaryValue", new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));
        ValueSelector secondaryValueSelector = SelectorTestUtils.mockValueSelector(TestdataMultiVarEntity.class, "secondaryValue", new TestdataValue("8"), new TestdataValue("9"));
        List<MoveSelector> moveSelectorList = new ArrayList<>(2);
        moveSelectorList.add(new org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector(new org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector(recordingEntitySelector), primaryValueSelector, false));
        moveSelectorList.add(new org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector(new org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector(recordingEntitySelector), secondaryValueSelector, false));
        QueuedEntityPlacer placer = new QueuedEntityPlacer(recordingEntitySelector, moveSelectorList);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        placer.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();
        Assert.assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        AbstractEntityPlacerTest.assertEntityPlacement(placementIterator.next(), "a", "1", "2", "3");
        placer.stepEnded(stepScopeA1);
        Assert.assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        AbstractEntityPlacerTest.assertEntityPlacement(placementIterator.next(), "a", "8", "9");
        placer.stepEnded(stepScopeA2);
        Assert.assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA3.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA3);
        AbstractEntityPlacerTest.assertEntityPlacement(placementIterator.next(), "b", "1", "2", "3");
        placer.stepEnded(stepScopeA3);
        Assert.assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA4 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA4.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA4);
        AbstractEntityPlacerTest.assertEntityPlacement(placementIterator.next(), "b", "8", "9");
        placer.stepEnded(stepScopeA4);
        Assert.assertFalse(placementIterator.hasNext());
        placer.phaseEnded(phaseScopeA);
        placer.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(entitySelector, 1, 1, 4);
        PlannerAssert.verifyPhaseLifecycle(primaryValueSelector, 1, 1, 4);
        PlannerAssert.verifyPhaseLifecycle(secondaryValueSelector, 1, 1, 4);
    }

    @Test
    public void cartesianProductMoveSelector() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataMultiVarEntity.class, new TestdataMultiVarEntity("a"), new TestdataMultiVarEntity("b"));
        MimicRecordingEntitySelector recordingEntitySelector = new MimicRecordingEntitySelector(entitySelector);
        ValueSelector primaryValueSelector = SelectorTestUtils.mockValueSelector(TestdataMultiVarEntity.class, "primaryValue", new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));
        ValueSelector secondaryValueSelector = SelectorTestUtils.mockValueSelector(TestdataMultiVarEntity.class, "secondaryValue", new TestdataValue("8"), new TestdataValue("9"));
        List<MoveSelector> moveSelectorList = new ArrayList<>(2);
        moveSelectorList.add(new org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector(new org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector(recordingEntitySelector), primaryValueSelector, false));
        moveSelectorList.add(new org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector(new org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector(recordingEntitySelector), secondaryValueSelector, false));
        MoveSelector moveSelector = new org.optaplanner.core.impl.heuristic.selector.move.composite.CartesianProductMoveSelector(moveSelectorList, true, false);
        QueuedEntityPlacer placer = new QueuedEntityPlacer(recordingEntitySelector, Collections.singletonList(moveSelector));
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        placer.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();
        Assert.assertEquals(true, placementIterator.hasNext());
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfIterator(placementIterator.next().iterator(), "a->1+a->8", "a->1+a->9", "a->2+a->8", "a->2+a->9", "a->3+a->8", "a->3+a->9");
        placer.stepEnded(stepScopeA1);
        Assert.assertEquals(true, placementIterator.hasNext());
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        PlannerAssert.assertAllCodesOfIterator(placementIterator.next().iterator(), "b->1+b->8", "b->1+b->9", "b->2+b->8", "b->2+b->9", "b->3+b->8", "b->3+b->9");
        placer.stepEnded(stepScopeA2);
        Assert.assertEquals(false, placementIterator.hasNext());
        placer.phaseEnded(phaseScopeA);
        placer.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(entitySelector, 1, 1, 2);
        PlannerAssert.verifyPhaseLifecycle(primaryValueSelector, 1, 1, 2);
        PlannerAssert.verifyPhaseLifecycle(secondaryValueSelector, 1, 1, 2);
    }
}


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
package org.optaplanner.core.impl.heuristic.selector.entity;


import SelectionCacheType.JUST_IN_TIME;
import SelectionCacheType.PHASE;
import SelectionCacheType.STEP;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class FromSolutionEntitySelectorTest {
    @Test
    public void originalWithoutEntityListDirtyAndMinimumCacheTypePhase() {
        runOriginalWithoutEntityListDirtyAndMinimumCacheType(PHASE);
    }

    @Test
    public void originalWithoutEntityListDirtyAndMinimumCacheTypeStep() {
        runOriginalWithoutEntityListDirtyAndMinimumCacheType(STEP);
    }

    @Test
    public void originalWithoutEntityListDirtyAndMinimumCacheTypeJustInTime() {
        runOriginalWithoutEntityListDirtyAndMinimumCacheType(JUST_IN_TIME);
    }

    @Test
    public void originalWithEntityListDirty() {
        TestdataSolution workingSolution = new TestdataSolution();
        EntityDescriptor entityDescriptor = Mockito.mock(EntityDescriptor.class);
        Mockito.when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        InnerScoreDirector scoreDirector = Mockito.mock(InnerScoreDirector.class);
        Mockito.when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        Mockito.when(scoreDirector.getWorkingEntityListRevision()).thenReturn(7L);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(false);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, SelectionCacheType.JUST_IN_TIME, false);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        entitySelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        Mockito.when(phaseScopeA.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        Mockito.when(stepScopeA1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeA1);
        Mockito.when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(new TestdataEntity("f1"), new TestdataEntity("f2"), new TestdataEntity("f3")));
        Mockito.when(scoreDirector.getWorkingEntityListRevision()).thenReturn(8L);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(true);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(8L)).thenReturn(false);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        Mockito.when(stepScopeA2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA2);
        PlannerAssert.assertAllCodesOfEntitySelector(entitySelector, "f1", "f2", "f3");
        entitySelector.stepEnded(stepScopeA2);
        entitySelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        Mockito.when(phaseScopeB.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        Mockito.when(stepScopeB1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB1);
        PlannerAssert.assertAllCodesOfEntitySelector(entitySelector, "f1", "f2", "f3");
        entitySelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        Mockito.when(stepScopeB2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB2);
        PlannerAssert.assertAllCodesOfEntitySelector(entitySelector, "f1", "f2", "f3");
        entitySelector.stepEnded(stepScopeB2);
        Mockito.when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        Mockito.when(scoreDirector.getWorkingEntityListRevision()).thenReturn(9L);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(8L)).thenReturn(true);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(9L)).thenReturn(false);
        AbstractStepScope stepScopeB3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        Mockito.when(stepScopeB3.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB3);
        PlannerAssert.assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeB3);
        entitySelector.phaseEnded(phaseScopeB);
        entitySelector.solvingEnded(solverScope);
        Mockito.verify(entityDescriptor, Mockito.times(4)).extractEntities(workingSolution);
    }

    @Test
    public void randomWithoutEntityListDirtyAndMinimumCacheTypePhase() {
        runRandomWithoutEntityListDirtyAndMinimumCacheType(PHASE);
    }

    @Test
    public void randomWithoutEntityListDirtyAndMinimumCacheTypeStep() {
        runRandomWithoutEntityListDirtyAndMinimumCacheType(STEP);
    }

    @Test
    public void randomWithoutEntityListDirtyAndMinimumCacheTypeJustInTime() {
        runRandomWithoutEntityListDirtyAndMinimumCacheType(JUST_IN_TIME);
    }

    @Test
    public void randomWithEntityListDirty() {
        TestdataSolution workingSolution = new TestdataSolution();
        EntityDescriptor entityDescriptor = Mockito.mock(EntityDescriptor.class);
        Mockito.when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        InnerScoreDirector scoreDirector = Mockito.mock(InnerScoreDirector.class);
        Mockito.when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        Mockito.when(scoreDirector.getWorkingEntityListRevision()).thenReturn(7L);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(false);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, SelectionCacheType.JUST_IN_TIME, true);
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextInt(3)).thenReturn(1, 0, 0, 2, 1, 2, 2, 1, 0);
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        Mockito.when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        entitySelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        Mockito.when(phaseScopeA.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        Mockito.when(stepScopeA1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA1);
        PlannerAssert.assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e2", "e1", "e1", "e3");
        entitySelector.stepEnded(stepScopeA1);
        Mockito.when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(new TestdataEntity("f1"), new TestdataEntity("f2"), new TestdataEntity("f3")));
        Mockito.when(scoreDirector.getWorkingEntityListRevision()).thenReturn(8L);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(true);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(8L)).thenReturn(false);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        Mockito.when(stepScopeA2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA2);
        PlannerAssert.assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "f2", "f3");
        entitySelector.stepEnded(stepScopeA2);
        entitySelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        Mockito.when(phaseScopeB.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        Mockito.when(stepScopeB1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB1);
        PlannerAssert.assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "f3");
        entitySelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        Mockito.when(stepScopeB2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB2);
        PlannerAssert.assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "f2");
        entitySelector.stepEnded(stepScopeB2);
        Mockito.when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        Mockito.when(scoreDirector.getWorkingEntityListRevision()).thenReturn(9L);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(8L)).thenReturn(true);
        Mockito.when(scoreDirector.isWorkingEntityListDirty(9L)).thenReturn(false);
        AbstractStepScope stepScopeB3 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        Mockito.when(stepScopeB3.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB3);
        PlannerAssert.assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e1");
        entitySelector.stepEnded(stepScopeB3);
        entitySelector.phaseEnded(phaseScopeB);
        entitySelector.solvingEnded(solverScope);
        Mockito.verify(entityDescriptor, Mockito.times(4)).extractEntities(workingSolution);
    }

    @Test(expected = IllegalStateException.class)
    public void listIteratorWithRandomSelection() {
        EntityDescriptor entityDescriptor = Mockito.mock(EntityDescriptor.class);
        Mockito.when(entityDescriptor.getEntityClass()).thenReturn(((Class) (TestdataEntity.class)));
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, SelectionCacheType.JUST_IN_TIME, true);
        entitySelector.listIterator();
    }

    @Test(expected = IllegalStateException.class)
    public void indexedListIteratorWithRandomSelection() {
        EntityDescriptor entityDescriptor = Mockito.mock(EntityDescriptor.class);
        Mockito.when(entityDescriptor.getEntityClass()).thenReturn(((Class) (TestdataEntity.class)));
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, SelectionCacheType.JUST_IN_TIME, true);
        entitySelector.listIterator(0);
    }
}


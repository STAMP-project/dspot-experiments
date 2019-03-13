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
package org.optaplanner.core.impl.exhaustivesearch;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.exhaustivesearch.decider.ExhaustiveSearchDecider;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchPhaseScope;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableEntity;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableSolution;
import org.optaplanner.core.impl.testdata.domain.reinitialize.TestdataReinitializeEntity;
import org.optaplanner.core.impl.testdata.domain.reinitialize.TestdataReinitializeSolution;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;


public class DefaultExhaustiveSearchPhaseTest {
    @Test
    public void restoreWorkingSolution() {
        ExhaustiveSearchPhaseScope<TestdataSolution> phaseScope = Mockito.mock(ExhaustiveSearchPhaseScope.class);
        ExhaustiveSearchStepScope<TestdataSolution> lastCompletedStepScope = Mockito.mock(ExhaustiveSearchStepScope.class);
        Mockito.when(phaseScope.getLastCompletedStepScope()).thenReturn(lastCompletedStepScope);
        ExhaustiveSearchStepScope<TestdataSolution> stepScope = Mockito.mock(ExhaustiveSearchStepScope.class);
        Mockito.when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        TestdataSolution workingSolution = new TestdataSolution();
        Mockito.when(phaseScope.getWorkingSolution()).thenReturn(workingSolution);
        InnerScoreDirector<TestdataSolution> scoreDirector = Mockito.mock(InnerScoreDirector.class);
        Mockito.when(phaseScope.getScoreDirector()).thenReturn(scoreDirector);
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        Mockito.when(phaseScope.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        ExhaustiveSearchLayer layer0 = new ExhaustiveSearchLayer(0, Mockito.mock(Object.class));
        ExhaustiveSearchLayer layer1 = new ExhaustiveSearchLayer(1, Mockito.mock(Object.class));
        ExhaustiveSearchLayer layer2 = new ExhaustiveSearchLayer(2, Mockito.mock(Object.class));
        ExhaustiveSearchLayer layer3 = new ExhaustiveSearchLayer(3, Mockito.mock(Object.class));
        ExhaustiveSearchLayer layer4 = new ExhaustiveSearchLayer(4, Mockito.mock(Object.class));
        ExhaustiveSearchNode node0 = new ExhaustiveSearchNode(layer0, null);
        node0.setMove(Mockito.mock(Move.class));
        node0.setUndoMove(Mockito.mock(Move.class));
        ExhaustiveSearchNode node1 = new ExhaustiveSearchNode(layer1, node0);
        node1.setMove(Mockito.mock(Move.class));
        node1.setUndoMove(Mockito.mock(Move.class));
        ExhaustiveSearchNode node2A = new ExhaustiveSearchNode(layer2, node1);
        node2A.setMove(Mockito.mock(Move.class));
        node2A.setUndoMove(Mockito.mock(Move.class));
        ExhaustiveSearchNode node3A = new ExhaustiveSearchNode(layer3, node2A);// oldNode

        node3A.setMove(Mockito.mock(Move.class));
        node3A.setUndoMove(Mockito.mock(Move.class));
        ExhaustiveSearchNode node2B = new ExhaustiveSearchNode(layer2, node1);
        node2B.setMove(Mockito.mock(Move.class));
        node2B.setUndoMove(Mockito.mock(Move.class));
        ExhaustiveSearchNode node3B = new ExhaustiveSearchNode(layer3, node2B);
        node3B.setMove(Mockito.mock(Move.class));
        node3B.setUndoMove(Mockito.mock(Move.class));
        ExhaustiveSearchNode node4B = new ExhaustiveSearchNode(layer4, node3B);// newNode

        node4B.setMove(Mockito.mock(Move.class));
        node4B.setUndoMove(Mockito.mock(Move.class));
        node4B.setScore(SimpleScore.ofUninitialized((-96), 7));
        Mockito.when(lastCompletedStepScope.getExpandingNode()).thenReturn(node3A);
        Mockito.when(stepScope.getExpandingNode()).thenReturn(node4B);
        DefaultExhaustiveSearchPhase<TestdataSolution> phase = new DefaultExhaustiveSearchPhase(0, "", null, null);
        phase.setEntitySelector(Mockito.mock(EntitySelector.class));
        phase.setDecider(Mockito.mock(ExhaustiveSearchDecider.class));
        phase.restoreWorkingSolution(stepScope);
        Mockito.verify(node0.getMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        Mockito.verify(node0.getUndoMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        Mockito.verify(node1.getMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        Mockito.verify(node1.getUndoMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        Mockito.verify(node2A.getMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        Mockito.verify(node2A.getUndoMove(), Mockito.times(1)).doMove(scoreDirector);
        Mockito.verify(node3A.getMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        Mockito.verify(node3A.getUndoMove(), Mockito.times(1)).doMove(scoreDirector);
        Mockito.verify(node2B.getMove(), Mockito.times(1)).doMove(scoreDirector);
        Mockito.verify(node2B.getUndoMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        Mockito.verify(node3B.getMove(), Mockito.times(1)).doMove(scoreDirector);
        Mockito.verify(node3B.getUndoMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        Mockito.verify(node4B.getMove(), Mockito.times(1)).doMove(scoreDirector);
        Mockito.verify(node4B.getUndoMove(), Mockito.times(0)).doMove(ArgumentMatchers.any(ScoreDirector.class));
        // TODO FIXME
        // verify(workingSolution).setScore(newScore);
    }

    @Test
    public void solveWithInitializedEntities() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataSolution.class, TestdataEntity.class);
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(new ExhaustiveSearchPhaseConfig()));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1", null), new TestdataEntity("e2", v2), new TestdataEntity("e3", v1)));
        solution = solver.solve(solution);
        Assert.assertNotNull(solution);
        TestdataEntity solvedE1 = solution.getEntityList().get(0);
        PlannerAssert.assertCode("e1", solvedE1);
        Assert.assertNotNull(solvedE1.getValue());
        TestdataEntity solvedE2 = solution.getEntityList().get(1);
        PlannerAssert.assertCode("e2", solvedE2);
        Assert.assertEquals(v2, solvedE2.getValue());
        TestdataEntity solvedE3 = solution.getEntityList().get(2);
        PlannerAssert.assertCode("e3", solvedE3);
        Assert.assertEquals(v1, solvedE3.getValue());
        Assert.assertEquals(0, solution.getScore().getInitScore());
    }

    @Test
    public void solveWithImmovableEntities() {
        SolverFactory<TestdataImmovableSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataImmovableSolution.class, TestdataImmovableEntity.class);
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(new ExhaustiveSearchPhaseConfig()));
        Solver<TestdataImmovableSolution> solver = solverFactory.buildSolver();
        TestdataImmovableSolution solution = new TestdataImmovableSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(new TestdataImmovableEntity("e1", null, false, false), new TestdataImmovableEntity("e2", v2, true, false), new TestdataImmovableEntity("e3", null, false, true)));
        solution = solver.solve(solution);
        Assert.assertNotNull(solution);
        TestdataImmovableEntity solvedE1 = solution.getEntityList().get(0);
        PlannerAssert.assertCode("e1", solvedE1);
        Assert.assertNotNull(solvedE1.getValue());
        TestdataImmovableEntity solvedE2 = solution.getEntityList().get(1);
        PlannerAssert.assertCode("e2", solvedE2);
        Assert.assertEquals(v2, solvedE2.getValue());
        TestdataImmovableEntity solvedE3 = solution.getEntityList().get(2);
        PlannerAssert.assertCode("e3", solvedE3);
        Assert.assertEquals(null, solvedE3.getValue());
        Assert.assertEquals((-1), solution.getScore().getInitScore());
    }

    @Test
    public void solveWithEmptyEntityList() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataSolution.class, TestdataEntity.class);
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(new ExhaustiveSearchPhaseConfig()));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Collections.emptyList());
        solution = solver.solve(solution);
        Assert.assertNotNull(solution);
        Assert.assertEquals(0, solution.getEntityList().size());
    }

    @Test
    public void solveWithReinitializeVariable() {
        SolverFactory<TestdataReinitializeSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataReinitializeSolution.class, TestdataReinitializeEntity.class);
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(new ExhaustiveSearchPhaseConfig()));
        Solver<TestdataReinitializeSolution> solver = solverFactory.buildSolver();
        TestdataReinitializeSolution solution = new TestdataReinitializeSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(new TestdataReinitializeEntity("e1", null, false), new TestdataReinitializeEntity("e2", v2, false), new TestdataReinitializeEntity("e3", v2, true), new TestdataReinitializeEntity("e4", null, true)));
        solution = solver.solve(solution);
        Assert.assertNotNull(solution);
        TestdataReinitializeEntity solvedE1 = solution.getEntityList().get(0);
        PlannerAssert.assertCode("e1", solvedE1);
        Assert.assertNotNull(solvedE1.getValue());
        TestdataReinitializeEntity solvedE2 = solution.getEntityList().get(1);
        PlannerAssert.assertCode("e2", solvedE2);
        Assert.assertNotNull(solvedE2.getValue());
        TestdataReinitializeEntity solvedE3 = solution.getEntityList().get(2);
        PlannerAssert.assertCode("e3", solvedE3);
        Assert.assertEquals(v2, solvedE3.getValue());
        TestdataReinitializeEntity solvedE4 = solution.getEntityList().get(3);
        PlannerAssert.assertCode("e4", solvedE4);
        Assert.assertEquals(null, solvedE4.getValue());
        Assert.assertEquals((-1), solution.getScore().getInitScore());
    }
}


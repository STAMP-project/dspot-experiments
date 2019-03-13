/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.solver;


import ScoreDefinitionType.SIMPLE;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.phase.custom.NoChangeCustomPhaseCommand;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.legacysolution.TestdataLegacySolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;


public class DefaultSolverTest {
    @Test
    public void solve() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataSolution.class, TestdataEntity.class);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));
        solution = solver.solve(solution);
        Assert.assertNotNull(solution);
        Assert.assertEquals(true, solution.getScore().isSolutionInitialized());
        Assert.assertSame(solution, solver.getBestSolution());
    }

    @Test
    public void solveLegacy() {
        SolverFactory<TestdataLegacySolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataLegacySolution.class, TestdataEntity.class);
        solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().setScoreDefinitionType(SIMPLE);
        Solver<TestdataLegacySolution> solver = solverFactory.buildSolver();
        TestdataLegacySolution solution = new TestdataLegacySolution();
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));
        solution = solver.solve(solution);
        Assert.assertNotNull(solution);
        Assert.assertSame(solution, solver.getBestSolution());
    }

    @Test
    public void solveStopsWhenUninitialized() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataSolution.class, TestdataEntity.class);
        CustomPhaseConfig phaseConfig = new CustomPhaseConfig();
        phaseConfig.setCustomPhaseCommandClassList(Collections.singletonList(NoChangeCustomPhaseCommand.class));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(phaseConfig));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"), new TestdataEntity("e4"), new TestdataEntity("e5")));
        solution = solver.solve(solution);
        Assert.assertNotNull(solution);
        Assert.assertEquals(false, solution.getScore().isSolutionInitialized());
        Assert.assertSame(solution, solver.getBestSolution());
    }

    @Test
    public void solveStopsWhenPartiallyInitialized() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataSolution.class, TestdataEntity.class);
        ConstructionHeuristicPhaseConfig phaseConfig = new ConstructionHeuristicPhaseConfig();
        // Run only 2 steps, although 5 are needed to initialize all entities
        phaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(2));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(phaseConfig));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"), new TestdataEntity("e4"), new TestdataEntity("e5")));
        solution = solver.solve(solution);
        Assert.assertNotNull(solution);
        Assert.assertEquals(false, solution.getScore().isSolutionInitialized());
        Assert.assertSame(solution, solver.getBestSolution());
    }

    @Test(timeout = 600000)
    public void solveThrowsExceptionWhenZeroEntity() {
        SolverFactory<TestdataChainedSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataChainedSolution.class, TestdataChainedEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(1));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(phaseConfig));
        Solver<TestdataChainedSolution> solver = solverFactory.buildSolver();
        TestdataChainedSolution solution = new TestdataChainedSolution("1");
        solution.setChainedEntityList(Collections.EMPTY_LIST);
        solution.setChainedAnchorList(Collections.singletonList(new TestdataChainedAnchor("4")));
        try {
            solver.solve(solution);
            Assert.fail("There was no RuntimeException thrown.");
        } catch (RuntimeException exception) {
            Assert.assertEquals(true, (exception instanceof IllegalStateException));
            Assert.assertEquals(true, exception.getMessage().contains("annotated member"));
            Assert.assertEquals(true, exception.getMessage().contains("must not return"));
        }
    }
}


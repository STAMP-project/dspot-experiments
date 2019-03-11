/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.config.solver;


import SolverConfig.MOVE_THREAD_COUNT_NONE;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.testutil.MockThreadFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;


public class SolverConfigMultiThreadedTest {
    @Test
    public void moveThreadCountAutoIsCorrectlyResolvedWhenCpuCountIsPositive() {
        final int cpuCount = 16;
        Assert.assertEquals(Integer.valueOf((cpuCount - 2)), mockSolverConfigForMoveThreadCountAuto(cpuCount).resolveMoveThreadCount());
    }

    @Test
    public void moveThreadCountAutoIsResolvedToNullWhenCpuCountIsNegative() {
        final int cpuCount = -2;
        Assert.assertNull(mockSolverConfigForMoveThreadCountAuto(cpuCount).resolveMoveThreadCount());
    }

    @Test
    public void moveThreadCountIsCorrectlyResolvedWhenValueIsPositive() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("2");
        Assert.assertEquals(Integer.valueOf(2), solverConfig.resolveMoveThreadCount());
    }

    @Test
    public void moveThreadCountThrowsExceptionWhenValueIsNegative() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("-6");
        try {
            solverConfig.resolveMoveThreadCount();
            Assert.fail("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException expectedException) {
            // expected
        }
    }

    @Test
    public void moveThreadCountIsResolvedToNullWhenValueIsNone() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount(MOVE_THREAD_COUNT_NONE);
        Assert.assertNull(solverConfig.resolveMoveThreadCount());
    }

    @Test(timeout = 5000L)
    public void solvingWithTooHighThreadCountFinishes() {
        runSolvingAndVerifySolution(10, 20, "256");
    }

    @Test(timeout = 5000L)
    public void customThreadFactoryClassIsUsed() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(TestdataSolution.class, TestdataEntity.class);
        solverFactory.getSolverConfig().setThreadFactoryClass(MockThreadFactory.class);
        solverFactory.getSolverConfig().setMoveThreadCount("2");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        TestdataSolution solution = createTestSolution(3, 5);
        solution = solver.solve(solution);
        assertSolution(solver, solution);
        Assert.assertTrue(MockThreadFactory.hasBeenCalled());
    }
}


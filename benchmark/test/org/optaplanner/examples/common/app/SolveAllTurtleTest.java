/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.examples.common.app;


import EnvironmentMode.FAST_ASSERT;
import EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.TestSystemProperties;


/**
 *
 *
 * @param <Solution_>
 * 		the solution type, the class with the {@link PlanningSolution} annotation
 */
@RunWith(Parameterized.class)
public abstract class SolveAllTurtleTest<Solution_> extends AbstractTurtleTest {
    private static final String MOVE_THREAD_COUNT_OVERRIDE = System.getProperty(TestSystemProperties.MOVE_THREAD_COUNT);

    private final String solverConfig;

    public SolveAllTurtleTest(String solverConfig) {
        this.solverConfig = solverConfig;
    }

    @Test
    public void runFastAndFullAssert() {
        AbstractTurtleTest.checkRunTurtleTests();
        SolverFactory<Solution_> solverFactory = buildSolverFactory();
        Solution_ problem = readProblem();
        // Specifically use NON_INTRUSIVE_FULL_ASSERT instead of FULL_ASSERT to flush out bugs hidden by intrusiveness
        // 1) NON_INTRUSIVE_FULL_ASSERT ASSERT to find CH bugs (but covers little ground)
        problem = buildAndSolve(solverFactory, NON_INTRUSIVE_FULL_ASSERT, problem, 2L);
        // 2) FAST_ASSERT to run past CH into LS to find easy bugs (but covers much ground)
        problem = buildAndSolve(solverFactory, FAST_ASSERT, problem, 5L);
        // 3) NON_INTRUSIVE_FULL_ASSERT ASSERT to find LS bugs (but covers little ground)
        problem = buildAndSolve(solverFactory, NON_INTRUSIVE_FULL_ASSERT, problem, 3L);
    }
}


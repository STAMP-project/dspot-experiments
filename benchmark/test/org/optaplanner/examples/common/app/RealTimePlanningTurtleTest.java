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
package org.optaplanner.examples.common.app;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;


/**
 *
 *
 * @param <Solution_>
 * 		the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class RealTimePlanningTurtleTest<Solution_> extends AbstractTurtleTest {
    public static final int FREQUENCY = 300;

    public static final long SPENT_LIMIT = 5000L;

    protected Solver<Solution_> solver;

    @Test
    public void realTimePlanning() throws InterruptedException, ExecutionException {
        AbstractTurtleTest.checkRunTurtleTests();
        final SolverFactory<Solution_> solverFactory = buildSolverFactory();
        final Solution_ problem = readProblem();
        solver = solverFactory.buildSolver();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> solveFuture = executorService.submit(() -> runSolve(solver, problem));
        Future<?> changesFuture = executorService.submit(() -> runChanges());
        solveFuture.get();
        changesFuture.get();
    }
}


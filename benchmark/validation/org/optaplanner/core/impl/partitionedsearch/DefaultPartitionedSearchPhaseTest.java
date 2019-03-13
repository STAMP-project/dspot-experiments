/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.partitionedsearch;


import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;


public class DefaultPartitionedSearchPhaseTest {
    @Test(timeout = 1000)
    public void partCount() {
        final int partSize = 3;
        final int partCount = 7;
        SolverFactory<TestdataSolution> solverFactory = DefaultPartitionedSearchPhaseTest.createSolverFactory(false);
        DefaultPartitionedSearchPhaseTest.setPartSize(solverFactory.getSolverConfig(), partSize);
        DefaultSolver<TestdataSolution> solver = ((DefaultSolver<TestdataSolution>) (solverFactory.buildSolver()));
        PartitionedSearchPhase<TestdataSolution> phase = ((PartitionedSearchPhase<TestdataSolution>) (solver.getPhaseList().get(0)));
        phase.addPhaseLifecycleListener(new org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter<TestdataSolution>() {
            @Override
            public void phaseStarted(AbstractPhaseScope<TestdataSolution> phaseScope) {
                Assert.assertEquals(Integer.valueOf(partCount), getPartCount());
            }
        });
        solver.solve(DefaultPartitionedSearchPhaseTest.createSolution((partCount * partSize), 2));
    }

    @Test(timeout = 1000)
    public void exceptionPropagation() {
        final int partSize = 7;
        final int partCount = 3;
        TestdataSolution solution = DefaultPartitionedSearchPhaseTest.createSolution(((partCount * partSize) - 1), 100);
        solution.getEntityList().add(new TestdataFaultyEntity("XYZ"));
        Assert.assertEquals((partSize * partCount), solution.getEntityList().size());
        SolverFactory<TestdataSolution> solverFactory = DefaultPartitionedSearchPhaseTest.createSolverFactory(false);
        DefaultPartitionedSearchPhaseTest.setPartSize(solverFactory.getSolverConfig(), partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        try {
            solver.solve(solution);
            Assert.fail("The exception was not propagated.");
        } catch (IllegalStateException ex) {
            Assert.assertThat(ex).hasMessageMatching(".*partIndex.*Relayed.*");
            Assert.assertThat(ex).hasRootCauseExactlyInstanceOf(TestdataFaultyEntity.TestException.class);
        }
    }

    @Test(timeout = 5000)
    public void terminateEarly() throws InterruptedException, ExecutionException {
        final int partSize = 1;
        final int partCount = 2;
        TestdataSolution solution = DefaultPartitionedSearchPhaseTest.createSolution((partCount * partSize), 10);
        SolverFactory<TestdataSolution> solverFactory = DefaultPartitionedSearchPhaseTest.createSolverFactory(true);
        DefaultPartitionedSearchPhaseTest.setPartSize(solverFactory.getSolverConfig(), partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        CountDownLatch solvingStarted = new CountDownLatch(1);
        ((DefaultSolver<TestdataSolution>) (solver)).addPhaseLifecycleListener(new org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter<TestdataSolution>() {
            @Override
            public void solvingStarted(DefaultSolverScope<TestdataSolution> solverScope) {
                solvingStarted.countDown();
            }
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TestdataSolution> solutionFuture = executor.submit(() -> {
            return solver.solve(solution);
        });
        // make sure solver has started solving before terminating early
        solvingStarted.await();
        Assert.assertTrue(solver.terminateEarly());
        Assert.assertTrue(solver.isTerminateEarly());
        executor.shutdown();
        Assert.assertTrue(executor.awaitTermination(100, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(solutionFuture.get());
    }

    @Test(timeout = 5000)
    public void shutdownMainThreadAbruptly() throws InterruptedException {
        final int partSize = 5;
        final int partCount = 3;
        TestdataSolution solution = DefaultPartitionedSearchPhaseTest.createSolution(((partCount * partSize) - 1), 10);
        CountDownLatch sleepAnnouncement = new CountDownLatch(1);
        solution.getEntityList().add(new TestdataSleepingEntity("XYZ", sleepAnnouncement));
        SolverFactory<TestdataSolution> solverFactory = DefaultPartitionedSearchPhaseTest.createSolverFactory(true);
        DefaultPartitionedSearchPhaseTest.setPartSize(solverFactory.getSolverConfig(), partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TestdataSolution> solutionFuture = executor.submit(() -> {
            return solver.solve(solution);
        });
        sleepAnnouncement.await();
        // Now we know the sleeping entity is sleeping so we can attempt to shut down.
        // This will initiate an abrupt shutdown that will interrupt the main solver thread.
        executor.shutdownNow();
        // This verifies that PartitionQueue doesn't clear interrupted flag when the main solver thread is interrupted.
        Assert.assertTrue("Executor must terminate successfully when it's shut down abruptly", executor.awaitTermination(100, TimeUnit.MILLISECONDS));
        // This verifies that interruption is propagated to caller (wrapped as an IllegalStateException)
        try {
            solutionFuture.get();
            Assert.fail("InterruptedException should have been propagated to solver thread.");
        } catch (ExecutionException ex) {
            Assert.assertThat(ex).hasCause(new IllegalStateException("Solver thread was interrupted in Partitioned Search."));
            Assert.assertThat(ex).hasRootCauseExactlyInstanceOf(InterruptedException.class);
        }
    }
}


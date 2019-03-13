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
package org.optaplanner.examples.nqueens.solver.tracking;


import NQueensApp.SOLVER_CONFIG;
import SelectionOrder.ORIGINAL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.persistence.NQueensGenerator;


@RunWith(Parameterized.class)
public class NQueensLocalSearchTrackingTest extends NQueensAbstractTrackingTest {
    private static final int N = 6;

    private final AcceptorConfig acceptorConfig;

    private final LocalSearchForagerConfig localSearchForagerConfig;

    private final List<NQueensStepTracking> expectedCoordinates;

    public NQueensLocalSearchTrackingTest(AcceptorConfig acceptorConfig, LocalSearchForagerConfig localSearchForagerConfig, List<NQueensStepTracking> expectedCoordinates) {
        this.expectedCoordinates = expectedCoordinates;
        this.localSearchForagerConfig = localSearchForagerConfig;
        this.acceptorConfig = acceptorConfig;
    }

    @Test
    public void trackLocalSearch() {
        SolverFactory<NQueens> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        NQueensGenerator generator = new NQueensGenerator();
        NQueens problem = NQueensSolutionInitializer.initialize(generator.createNQueens(NQueensLocalSearchTrackingTest.N));
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setAcceptorConfig(acceptorConfig);
        localSearchPhaseConfig.setForagerConfig(localSearchForagerConfig);
        localSearchPhaseConfig.getForagerConfig().setBreakTieRandomly(false);
        localSearchPhaseConfig.setMoveSelectorConfig(new ChangeMoveSelectorConfig());
        localSearchPhaseConfig.getMoveSelectorConfig().setSelectionOrder(ORIGINAL);
        localSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(20));
        solverConfig.getPhaseConfigList().set(1, localSearchPhaseConfig);
        NQueensStepTracker listener = new NQueensStepTracker();
        DefaultSolver<NQueens> solver = ((DefaultSolver<NQueens>) (solverFactory.buildSolver()));
        solver.addPhaseLifecycleListener(listener);
        NQueens bestSolution = solver.solve(problem);
        Assert.assertNotNull(bestSolution);
        assertTrackingList(expectedCoordinates, listener.getTrackingList());
    }
}


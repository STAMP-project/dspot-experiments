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
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.persistence.NQueensGenerator;


@RunWith(Parameterized.class)
public class NQueensConstructionHeuristicTrackingTest extends NQueensAbstractTrackingTest {
    private final ConstructionHeuristicType constructionHeuristicType;

    private final EntitySorterManner entitySorterManner;

    private final ValueSorterManner valueSorterManner;

    private final List<NQueensStepTracking> expectedCoordinates;

    public NQueensConstructionHeuristicTrackingTest(ConstructionHeuristicType constructionHeuristicType, EntitySorterManner entitySorterManner, ValueSorterManner valueSorterManner, List<NQueensStepTracking> expectedCoordinates) {
        this.constructionHeuristicType = constructionHeuristicType;
        this.entitySorterManner = entitySorterManner;
        this.valueSorterManner = valueSorterManner;
        this.expectedCoordinates = expectedCoordinates;
    }

    @Test
    public void trackConstructionHeuristics() {
        SolverFactory<NQueens> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        ConstructionHeuristicPhaseConfig chConfig = new ConstructionHeuristicPhaseConfig();
        chConfig.setValueSorterManner(valueSorterManner);
        chConfig.setEntitySorterManner(entitySorterManner);
        chConfig.setConstructionHeuristicType(constructionHeuristicType);
        solverConfig.setPhaseConfigList(Collections.<PhaseConfig>singletonList(chConfig));
        NQueensGenerator generator = new NQueensGenerator();
        NQueens problem = generator.createNQueens(8);
        NQueensStepTracker listener = new NQueensStepTracker();
        DefaultSolver<NQueens> solver = ((DefaultSolver<NQueens>) (solverFactory.buildSolver()));
        solver.addPhaseLifecycleListener(listener);
        NQueens bestSolution = solver.solve(problem);
        Assert.assertNotNull(bestSolution);
        assertTrackingList(expectedCoordinates, listener.getTrackingList());
    }
}


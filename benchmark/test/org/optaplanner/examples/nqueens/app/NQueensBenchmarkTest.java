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
package org.optaplanner.examples.nqueens.app;


import java.io.File;
import org.junit.Test;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.examples.common.app.PlannerBenchmarkTest;
import org.optaplanner.examples.nqueens.domain.NQueens;


public class NQueensBenchmarkTest extends PlannerBenchmarkTest {
    // ************************************************************************
    // Tests
    // ************************************************************************
    @Test(timeout = 600000)
    public void benchmark64queens() {
        NQueens problem = new org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO<NQueens>(NQueens.class).read(new File("data/nqueens/unsolved/64queens.xml"));
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory();
        addAllStatistics(plannerBenchmarkFactory);
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().setParallelBenchmarkCount("AUTO");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmark();
    }

    @Test(timeout = 600000)
    public void benchmark64queensSingleThread() {
        NQueens problem = new org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO<NQueens>(NQueens.class).read(new File("data/nqueens/unsolved/64queens.xml"));
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory();
        addAllStatistics(plannerBenchmarkFactory);
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().setParallelBenchmarkCount("1");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmark();
    }

    @Test
    public void benchmarkDirectoryNameDuplication() {
        NQueens problem = new org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO<NQueens>(NQueens.class).read(new File("data/nqueens/unsolved/4queens.xml"));
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory();
        DefaultPlannerBenchmark plannerBenchmark = ((DefaultPlannerBenchmark) (plannerBenchmarkFactory.buildPlannerBenchmark(problem)));
        plannerBenchmark.benchmarkingStarted();
        plannerBenchmark.getPlannerBenchmarkResult().initBenchmarkReportDirectory(plannerBenchmarkFactory.getPlannerBenchmarkConfig().getBenchmarkDirectory());
        plannerBenchmark.getPlannerBenchmarkResult().initBenchmarkReportDirectory(plannerBenchmarkFactory.getPlannerBenchmarkConfig().getBenchmarkDirectory());
    }
}


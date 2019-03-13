/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.step;


import ComponentVisitor.Order.POST_ORDER;
import CrawlerDepthLimit.FILE;
import CrawlerDepthLimit.PROJECT;
import LoggerLevel.DEBUG;
import Metric.MetricType;
import Order.PRE_ORDER;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;
import org.sonar.ce.task.ChangeLogLevel;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.PathAwareVisitorAdapter;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.component.TypeAwareVisitorAdapter;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.step.TestComputationStepContext;


public class ExecuteVisitorsStepTest {
    private static final String TEST_METRIC_KEY = "test";

    private static final int ROOT_REF = 1;

    private static final int DIRECTORY_REF = 123;

    private static final int FILE_1_REF = 1231;

    private static final int FILE_2_REF = 1232;

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(1, CoreMetrics.NCLOC).add(new org.sonar.ce.task.projectanalysis.metric.MetricImpl(2, ExecuteVisitorsStepTest.TEST_METRIC_KEY, "name", MetricType.INT));

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void execute_with_type_aware_visitor() {
        ExecuteVisitorsStep underStep = new ExecuteVisitorsStep(treeRootHolder, Collections.singletonList(new ExecuteVisitorsStepTest.TestTypeAwareVisitor()));
        measureRepository.addRawMeasure(ExecuteVisitorsStepTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(1));
        measureRepository.addRawMeasure(ExecuteVisitorsStepTest.FILE_2_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(2));
        measureRepository.addRawMeasure(ExecuteVisitorsStepTest.DIRECTORY_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(3));
        measureRepository.addRawMeasure(ExecuteVisitorsStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(3));
        underStep.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ExecuteVisitorsStepTest.FILE_1_REF, ExecuteVisitorsStepTest.TEST_METRIC_KEY).get().getIntValue()).isEqualTo(2);
        assertThat(measureRepository.getAddedRawMeasure(ExecuteVisitorsStepTest.FILE_2_REF, ExecuteVisitorsStepTest.TEST_METRIC_KEY).get().getIntValue()).isEqualTo(3);
        assertThat(measureRepository.getAddedRawMeasure(ExecuteVisitorsStepTest.DIRECTORY_REF, ExecuteVisitorsStepTest.TEST_METRIC_KEY).get().getIntValue()).isEqualTo(4);
        assertThat(measureRepository.getAddedRawMeasure(ExecuteVisitorsStepTest.ROOT_REF, ExecuteVisitorsStepTest.TEST_METRIC_KEY).get().getIntValue()).isEqualTo(4);
    }

    @Test
    public void execute_with_path_aware_visitor() {
        ExecuteVisitorsStep underTest = new ExecuteVisitorsStep(treeRootHolder, Collections.singletonList(new ExecuteVisitorsStepTest.TestPathAwareVisitor()));
        measureRepository.addRawMeasure(ExecuteVisitorsStepTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(1));
        measureRepository.addRawMeasure(ExecuteVisitorsStepTest.FILE_2_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(1));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ExecuteVisitorsStepTest.FILE_1_REF, ExecuteVisitorsStepTest.TEST_METRIC_KEY).get().getIntValue()).isEqualTo(1);
        assertThat(measureRepository.getAddedRawMeasure(ExecuteVisitorsStepTest.FILE_2_REF, ExecuteVisitorsStepTest.TEST_METRIC_KEY).get().getIntValue()).isEqualTo(1);
        assertThat(measureRepository.getAddedRawMeasure(ExecuteVisitorsStepTest.DIRECTORY_REF, ExecuteVisitorsStepTest.TEST_METRIC_KEY).get().getIntValue()).isEqualTo(2);
        assertThat(measureRepository.getAddedRawMeasure(ExecuteVisitorsStepTest.ROOT_REF, ExecuteVisitorsStepTest.TEST_METRIC_KEY).get().getIntValue()).isEqualTo(2);
    }

    @Test
    public void execute_logs_at_info_level_all_execution_duration_of_all_visitors() {
        try (ChangeLogLevel executor = new ChangeLogLevel(ExecuteVisitorsStep.class, LoggerLevel.DEBUG);ChangeLogLevel step1 = new ChangeLogLevel(ExecuteVisitorsStepTest.VisitorA.class, LoggerLevel.DEBUG);ChangeLogLevel step2 = new ChangeLogLevel(ExecuteVisitorsStepTest.VisitorB.class, LoggerLevel.DEBUG);ChangeLogLevel step3 = new ChangeLogLevel(ExecuteVisitorsStepTest.VisitorB.class, LoggerLevel.DEBUG)) {
            ExecuteVisitorsStep underTest = new ExecuteVisitorsStep(treeRootHolder, Arrays.asList(new ExecuteVisitorsStepTest.VisitorA(), new ExecuteVisitorsStepTest.VisitorB(), new ExecuteVisitorsStepTest.VisitorC()));
            underTest.execute(new TestComputationStepContext());
            List<String> logs = logTester.logs(DEBUG);
            assertThat(logs).hasSize(4);
            assertThat(logs.get(0)).isEqualTo("  Execution time for each component visitor:");
            assertThat(logs.get(1)).startsWith("  - VisitorA | time=");
            assertThat(logs.get(2)).startsWith("  - VisitorB | time=");
            assertThat(logs.get(3)).startsWith("  - VisitorC | time=");
        }
    }

    private static class VisitorA extends TypeAwareVisitorAdapter {
        VisitorA() {
            super(PROJECT, PRE_ORDER);
        }
    }

    private static class VisitorB extends TypeAwareVisitorAdapter {
        VisitorB() {
            super(PROJECT, PRE_ORDER);
        }
    }

    private static class VisitorC extends TypeAwareVisitorAdapter {
        VisitorC() {
            super(PROJECT, PRE_ORDER);
        }
    }

    private class TestTypeAwareVisitor extends TypeAwareVisitorAdapter {
        TestTypeAwareVisitor() {
            super(FILE, POST_ORDER);
        }

        @Override
        public void visitAny(Component any) {
            int ncloc = measureRepository.getRawMeasure(any, metricRepository.getByKey(CoreMetrics.NCLOC_KEY)).get().getIntValue();
            measureRepository.add(any, metricRepository.getByKey(ExecuteVisitorsStepTest.TEST_METRIC_KEY), Measure.newMeasureBuilder().create((ncloc + 1)));
        }
    }

    private class TestPathAwareVisitor extends PathAwareVisitorAdapter<ExecuteVisitorsStepTest.Counter> {
        TestPathAwareVisitor() {
            super(FILE, POST_ORDER, new SimpleStackElementFactory<ExecuteVisitorsStepTest.Counter>() {
                @Override
                public ExecuteVisitorsStepTest.Counter createForAny(Component component) {
                    return new ExecuteVisitorsStepTest.Counter();
                }
            });
        }

        @Override
        public void visitProject(Component project, Path<ExecuteVisitorsStepTest.Counter> path) {
            computeAndSaveMeasures(project, path);
        }

        @Override
        public void visitDirectory(Component directory, Path<ExecuteVisitorsStepTest.Counter> path) {
            computeAndSaveMeasures(directory, path);
        }

        @Override
        public void visitFile(Component file, Path<ExecuteVisitorsStepTest.Counter> path) {
            int ncloc = measureRepository.getRawMeasure(file, metricRepository.getByKey(CoreMetrics.NCLOC_KEY)).get().getIntValue();
            path.current().add(ncloc);
            computeAndSaveMeasures(file, path);
        }

        private void computeAndSaveMeasures(Component component, Path<ExecuteVisitorsStepTest.Counter> path) {
            measureRepository.add(component, metricRepository.getByKey(ExecuteVisitorsStepTest.TEST_METRIC_KEY), Measure.newMeasureBuilder().create(path.current().getValue()));
            increaseParentValue(path);
        }

        private void increaseParentValue(Path<ExecuteVisitorsStepTest.Counter> path) {
            if (!(path.isRoot())) {
                path.parent().add(path.current().getValue());
            }
        }
    }

    public class Counter {
        private int value = 0;

        public void add(int value) {
            this.value += value;
        }

        public int getValue() {
            return value;
        }
    }
}


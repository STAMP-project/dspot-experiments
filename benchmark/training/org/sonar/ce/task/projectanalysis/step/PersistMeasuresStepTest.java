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


import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.measures.Metric;
import org.sonar.ce.task.projectanalysis.analysis.MutableAnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.measure.MeasureDto;


public class PersistMeasuresStepTest extends BaseStepTest {
    private static final Metric STRING_METRIC = create();

    private static final Metric INT_METRIC = create();

    private static final String ANALYSIS_UUID = "a1";

    private static final int REF_1 = 1;

    private static final int REF_2 = 2;

    private static final int REF_3 = 3;

    private static final int REF_4 = 4;

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule();

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    @Rule
    public MutableAnalysisMetadataHolderRule analysisMetadataHolder = new MutableAnalysisMetadataHolderRule();

    private DbClient dbClient = db.getDbClient();

    @Test
    public void persist_measures_of_project_analysis_excluding_directories() {
        prepareProject();
        // the computed measures
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_1, PersistMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("project-value"));
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_3, PersistMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("dir-value"));
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_4, PersistMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("file-value"));
        TestComputationStepContext context = execute();
        // project and dir measures are persisted, but not file measures
        assertThat(db.countRowsOfTable("project_measures")).isEqualTo(1);
        assertThat(selectMeasure("project-uuid", PersistMeasuresStepTest.STRING_METRIC).get().getData()).isEqualTo("project-value");
        assertThatMeasuresAreNotPersisted("dir-uuid");
        assertThatMeasuresAreNotPersisted("file-uuid");
        PersistMeasuresStepTest.assertNbOfInserts(context, 1);
    }

    @Test
    public void measures_without_value_are_not_persisted() {
        prepareProject();
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_1, PersistMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().createNoValue());
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_1, PersistMeasuresStepTest.INT_METRIC.getKey(), Measure.newMeasureBuilder().createNoValue());
        TestComputationStepContext context = execute();
        assertThatMeasureIsNotPersisted("project-uuid", PersistMeasuresStepTest.STRING_METRIC);
        assertThatMeasureIsNotPersisted("project-uuid", PersistMeasuresStepTest.INT_METRIC);
        PersistMeasuresStepTest.assertNbOfInserts(context, 0);
    }

    @Test
    public void measures_on_leak_period_are_persisted() {
        prepareProject();
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_1, PersistMeasuresStepTest.INT_METRIC.getKey(), Measure.newMeasureBuilder().setVariation(42.0).createNoValue());
        TestComputationStepContext context = execute();
        MeasureDto persistedMeasure = selectMeasure("project-uuid", PersistMeasuresStepTest.INT_METRIC).get();
        assertThat(persistedMeasure.getValue()).isNull();
        assertThat(persistedMeasure.getVariation()).isEqualTo(42.0);
        PersistMeasuresStepTest.assertNbOfInserts(context, 1);
    }

    @Test
    public void persist_all_measures_of_portfolio_analysis() {
        preparePortfolio();
        // the computed measures
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_1, PersistMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("view-value"));
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_2, PersistMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("subview-value"));
        measureRepository.addRawMeasure(PersistMeasuresStepTest.REF_3, PersistMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("project-value"));
        TestComputationStepContext context = execute();
        assertThat(db.countRowsOfTable("project_measures")).isEqualTo(2);
        assertThat(selectMeasure("view-uuid", PersistMeasuresStepTest.STRING_METRIC).get().getData()).isEqualTo("view-value");
        assertThat(selectMeasure("subview-uuid", PersistMeasuresStepTest.STRING_METRIC).get().getData()).isEqualTo("subview-value");
        PersistMeasuresStepTest.assertNbOfInserts(context, 2);
    }
}


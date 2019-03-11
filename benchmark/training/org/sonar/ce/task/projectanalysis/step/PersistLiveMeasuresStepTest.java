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
import org.sonar.db.measure.LiveMeasureDto;


public class PersistLiveMeasuresStepTest extends BaseStepTest {
    private static final Metric STRING_METRIC = create();

    private static final Metric INT_METRIC = create();

    private static final Metric METRIC_WITH_BEST_VALUE = create();

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
    public void persist_live_measures_of_project_analysis() {
        prepareProject();
        // the computed measures
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_1, PersistLiveMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("project-value"));
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_3, PersistLiveMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("dir-value"));
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_4, PersistLiveMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("file-value"));
        TestComputationStepContext context = new TestComputationStepContext();
        step().execute(context);
        // all measures are persisted, from project to file
        assertThat(db.countRowsOfTable("live_measures")).isEqualTo(3);
        assertThat(selectMeasure("project-uuid", PersistLiveMeasuresStepTest.STRING_METRIC).get().getDataAsString()).isEqualTo("project-value");
        assertThat(selectMeasure("dir-uuid", PersistLiveMeasuresStepTest.STRING_METRIC).get().getDataAsString()).isEqualTo("dir-value");
        assertThat(selectMeasure("file-uuid", PersistLiveMeasuresStepTest.STRING_METRIC).get().getDataAsString()).isEqualTo("file-value");
        PersistLiveMeasuresStepTest.verifyStatistics(context, 3);
    }

    @Test
    public void measures_without_value_are_not_persisted() {
        prepareProject();
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_1, PersistLiveMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().createNoValue());
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_1, PersistLiveMeasuresStepTest.INT_METRIC.getKey(), Measure.newMeasureBuilder().createNoValue());
        TestComputationStepContext context = new TestComputationStepContext();
        step().execute(context);
        assertThatMeasureIsNotPersisted("project-uuid", PersistLiveMeasuresStepTest.STRING_METRIC);
        assertThatMeasureIsNotPersisted("project-uuid", PersistLiveMeasuresStepTest.INT_METRIC);
        PersistLiveMeasuresStepTest.verifyStatistics(context, 0);
    }

    @Test
    public void measures_on_leak_period_are_persisted() {
        prepareProject();
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_1, PersistLiveMeasuresStepTest.INT_METRIC.getKey(), Measure.newMeasureBuilder().setVariation(42.0).createNoValue());
        TestComputationStepContext context = new TestComputationStepContext();
        step().execute(context);
        LiveMeasureDto persistedMeasure = selectMeasure("project-uuid", PersistLiveMeasuresStepTest.INT_METRIC).get();
        assertThat(persistedMeasure.getValue()).isNull();
        assertThat(persistedMeasure.getVariation()).isEqualTo(42.0);
        PersistLiveMeasuresStepTest.verifyStatistics(context, 1);
    }

    @Test
    public void delete_measures_from_db_if_no_longer_computed() {
        prepareProject();
        // measure to be updated
        LiveMeasureDto measureOnFileInProject = insertMeasure("file-uuid", "project-uuid", PersistLiveMeasuresStepTest.INT_METRIC);
        // measure to be deleted because not computed anymore
        LiveMeasureDto otherMeasureOnFileInProject = insertMeasure("file-uuid", "project-uuid", PersistLiveMeasuresStepTest.STRING_METRIC);
        // measure in another project, not touched
        LiveMeasureDto measureInOtherProject = insertMeasure("other-file-uuid", "other-project-uuid", PersistLiveMeasuresStepTest.INT_METRIC);
        db.commit();
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_4, PersistLiveMeasuresStepTest.INT_METRIC.getKey(), Measure.newMeasureBuilder().create(42));
        TestComputationStepContext context = new TestComputationStepContext();
        step().execute(context);
        assertThatMeasureHasValue(measureOnFileInProject, 42);
        assertThatMeasureDoesNotExist(otherMeasureOnFileInProject);
        assertThatMeasureHasValue(measureInOtherProject, ((int) (measureInOtherProject.getValue().doubleValue())));
        PersistLiveMeasuresStepTest.verifyStatistics(context, 1);
    }

    @Test
    public void do_not_persist_file_measures_with_best_value() {
        prepareProject();
        // measure to be deleted because new value matches the metric best value
        LiveMeasureDto oldMeasure = insertMeasure("file-uuid", "project-uuid", PersistLiveMeasuresStepTest.INT_METRIC);
        db.commit();
        // project measure with metric best value -> persist with value 0
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_1, PersistLiveMeasuresStepTest.METRIC_WITH_BEST_VALUE.getKey(), Measure.newMeasureBuilder().create(0));
        // file measure with metric best value -> do not persist
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_4, PersistLiveMeasuresStepTest.METRIC_WITH_BEST_VALUE.getKey(), Measure.newMeasureBuilder().create(0));
        TestComputationStepContext context = new TestComputationStepContext();
        step().execute(context);
        assertThatMeasureDoesNotExist(oldMeasure);
        assertThatMeasureHasValue("project-uuid", PersistLiveMeasuresStepTest.METRIC_WITH_BEST_VALUE, 0);
        PersistLiveMeasuresStepTest.verifyStatistics(context, 1);
    }

    @Test
    public void persist_live_measures_of_portfolio_analysis() {
        preparePortfolio();
        // the computed measures
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_1, PersistLiveMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("view-value"));
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_2, PersistLiveMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("subview-value"));
        measureRepository.addRawMeasure(PersistLiveMeasuresStepTest.REF_3, PersistLiveMeasuresStepTest.STRING_METRIC.getKey(), Measure.newMeasureBuilder().create("project-value"));
        TestComputationStepContext context = new TestComputationStepContext();
        step().execute(context);
        assertThat(db.countRowsOfTable("live_measures")).isEqualTo(3);
        assertThat(selectMeasure("view-uuid", PersistLiveMeasuresStepTest.STRING_METRIC).get().getDataAsString()).isEqualTo("view-value");
        assertThat(selectMeasure("subview-uuid", PersistLiveMeasuresStepTest.STRING_METRIC).get().getDataAsString()).isEqualTo("subview-value");
        assertThat(selectMeasure("project-uuid", PersistLiveMeasuresStepTest.STRING_METRIC).get().getDataAsString()).isEqualTo("project-value");
        PersistLiveMeasuresStepTest.verifyStatistics(context, 3);
    }
}


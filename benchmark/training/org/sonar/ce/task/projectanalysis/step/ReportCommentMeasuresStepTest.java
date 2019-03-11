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


import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.step.ComputationStep;
import org.sonar.ce.task.step.TestComputationStepContext;


public class ReportCommentMeasuresStepTest {
    private static final int ROOT_REF = 1;

    private static final int DIRECTORY_REF = 1234;

    private static final int FILE_1_REF = 12341;

    private static final int FILE_2_REF = 12342;

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(CoreMetrics.NCLOC).add(CoreMetrics.COMMENT_LINES).add(CoreMetrics.COMMENT_LINES_DENSITY).add(CoreMetrics.PUBLIC_API).add(CoreMetrics.PUBLIC_UNDOCUMENTED_API).add(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    ComputationStep underTest = new CommentMeasuresStep(treeRootHolder, metricRepository, measureRepository);

    @Test
    public void aggregate_comment_lines() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(400));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.COMMENT_LINES_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.COMMENT_LINES_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.COMMENT_LINES_KEY).get().getIntValue()).isEqualTo(500);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.COMMENT_LINES_KEY).get().getIntValue()).isEqualTo(500);
    }

    @Test
    public void compute_comment_density() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(150));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(200));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(50));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(300));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(300));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY).get().getDoubleValue()).isEqualTo(60.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY).get().getDoubleValue()).isEqualTo(20.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY).get().getDoubleValue()).isEqualTo(40.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY).get().getDoubleValue()).isEqualTo(40.0);
    }

    @Test
    public void compute_zero_comment_density_when_zero_comment() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(200));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(300));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(300));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY).get().getDoubleValue()).isEqualTo(0.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY).get().getDoubleValue()).isEqualTo(0.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY).get().getDoubleValue()).isEqualTo(0.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY).get().getDoubleValue()).isEqualTo(0.0);
    }

    @Test
    public void not_compute_comment_density_when_zero_ncloc_and_zero_comment() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(0));
        underTest.execute(new TestComputationStepContext());
        assertNoNewMeasures(CoreMetrics.COMMENT_LINES_DENSITY_KEY);
    }

    @Test
    public void not_compute_comment_density_when_no_ncloc() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(150));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.COMMENT_LINES_KEY, Measure.newMeasureBuilder().create(50));
        underTest.execute(new TestComputationStepContext());
        assertNoNewMeasures(CoreMetrics.COMMENT_LINES_DENSITY_KEY);
    }

    @Test
    public void not_compute_comment_density_when_no_comment() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(200));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(200));
        underTest.execute(new TestComputationStepContext());
        assertNoNewMeasures(CoreMetrics.COMMENT_LINES_DENSITY_KEY);
    }

    @Test
    public void aggregate_public_api() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(400));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_API_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_API_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.PUBLIC_API_KEY).get().getIntValue()).isEqualTo(500);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.PUBLIC_API_KEY).get().getIntValue()).isEqualTo(500);
    }

    @Test
    public void aggregate_public_undocumented_api() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(400));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY).get().getIntValue()).isEqualTo(500);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY).get().getIntValue()).isEqualTo(500);
    }

    @Test
    public void compute_public_documented_api_density() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(50));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(400));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(100));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY).get().getDoubleValue()).isEqualTo(50.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY).get().getDoubleValue()).isEqualTo(75.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY).get().getDoubleValue()).isEqualTo(70.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY).get().getDoubleValue()).isEqualTo(70.0);
    }

    @Test
    public void not_compute_public_documented_api_density_when_no_public_api() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(50));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(100));
        underTest.execute(new TestComputationStepContext());
        assertNoNewMeasures(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY);
    }

    @Test
    public void not_compute_public_documented_api_density_when_no_public_undocumented_api() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(50));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(100));
        underTest.execute(new TestComputationStepContext());
        assertNoNewMeasures(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY);
    }

    @Test
    public void not_compute_public_documented_api_density_when_public_api_is_zero() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(50));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(100));
        underTest.execute(new TestComputationStepContext());
        assertNoNewMeasures(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY);
    }

    @Test
    public void compute_100_percent_public_documented_api_density_when_public_undocumented_api_is_zero() {
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(100));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(0));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_API_KEY, Measure.newMeasureBuilder().create(400));
        measureRepository.addRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, Measure.newMeasureBuilder().create(0));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_1_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY).get().getDoubleValue()).isEqualTo(100.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.FILE_2_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY).get().getDoubleValue()).isEqualTo(100.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.DIRECTORY_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY).get().getDoubleValue()).isEqualTo(100.0);
        assertThat(measureRepository.getAddedRawMeasure(ReportCommentMeasuresStepTest.ROOT_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY).get().getDoubleValue()).isEqualTo(100.0);
    }

    @Test
    public void compute_nothing_when_no_data() {
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasures(ReportCommentMeasuresStepTest.FILE_1_REF)).isEmpty();
        assertThat(measureRepository.getAddedRawMeasures(ReportCommentMeasuresStepTest.FILE_2_REF)).isEmpty();
        assertThat(measureRepository.getAddedRawMeasures(ReportCommentMeasuresStepTest.DIRECTORY_REF)).isEmpty();
        assertThat(measureRepository.getAddedRawMeasures(ReportCommentMeasuresStepTest.ROOT_REF)).isEmpty();
    }
}


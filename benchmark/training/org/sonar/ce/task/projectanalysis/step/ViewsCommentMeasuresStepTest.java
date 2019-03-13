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
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.step.ComputationStep;
import org.sonar.ce.task.step.TestComputationStepContext;


public class ViewsCommentMeasuresStepTest {
    private static final int ROOT_REF = 1;

    private static final int MODULE_REF = 12;

    private static final int SUB_MODULE_REF = 123;

    private static final int PROJECTVIEW_1_REF = 1231;

    private static final int PROJECTVIEW_2_REF = 1232;

    private static final int PROJECTVIEW_3_REF = 1233;

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(CoreMetrics.NCLOC).add(CoreMetrics.COMMENT_LINES).add(CoreMetrics.COMMENT_LINES_DENSITY).add(CoreMetrics.PUBLIC_API).add(CoreMetrics.PUBLIC_UNDOCUMENTED_API).add(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    ComputationStep underTest = new CommentMeasuresStep(treeRootHolder, metricRepository, measureRepository);

    @Test
    public void aggregate_comment_lines() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.COMMENT_LINES_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.COMMENT_LINES_KEY, 400);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_3_REF, CoreMetrics.COMMENT_LINES_KEY, 500);
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.COMMENT_LINES_KEY, 500);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.COMMENT_LINES_KEY, 500);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.COMMENT_LINES_KEY, 1000);
    }

    @Test
    public void compute_comment_density() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.NCLOC_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.COMMENT_LINES_KEY, 150);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.NCLOC_KEY, 200);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.COMMENT_LINES_KEY, 50);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_3_REF, CoreMetrics.NCLOC_KEY, 300);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_3_REF, CoreMetrics.COMMENT_LINES_KEY, 5);
        addRawMeasure(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.NCLOC_KEY, 300);
        addRawMeasure(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.NCLOC_KEY, 300);
        addRawMeasure(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, 300);
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY, 40.0);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY, 40.0);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY, 40.6);
    }

    @Test
    public void compute_zero_comment_density_when_zero_comment() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.NCLOC_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.COMMENT_LINES_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.NCLOC_KEY, 200);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.COMMENT_LINES_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.NCLOC_KEY, 300);
        addRawMeasure(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.NCLOC_KEY, 300);
        addRawMeasure(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, 300);
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY, 0.0);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY, 0.0);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.COMMENT_LINES_DENSITY_KEY, 0.0);
    }

    @Test
    public void not_compute_comment_density_when_zero_ncloc_and_zero_comment() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.NCLOC_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.COMMENT_LINES_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.NCLOC_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.COMMENT_LINES_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.NCLOC_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.NCLOC_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, 0);
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertNoRawMeasures(CoreMetrics.COMMENT_LINES_DENSITY_KEY);
    }

    @Test
    public void not_compute_comment_density_when_no_ncloc() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.COMMENT_LINES_KEY, 150);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.COMMENT_LINES_KEY, 50);
        underTest.execute(new TestComputationStepContext());
        assertNoRawMeasures(CoreMetrics.COMMENT_LINES_DENSITY_KEY);
    }

    @Test
    public void not_compute_comment_density_when_no_comment() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.NCLOC_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.NCLOC_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.NCLOC_KEY, 200);
        addRawMeasure(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.NCLOC_KEY, 200);
        addRawMeasure(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_KEY, 200);
        underTest.execute(new TestComputationStepContext());
        assertNoRawMeasures(CoreMetrics.COMMENT_LINES_DENSITY_KEY);
    }

    @Test
    public void aggregate_public_api() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_API_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_API_KEY, 400);
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.PUBLIC_API_KEY, 500);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.PUBLIC_API_KEY, 500);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.PUBLIC_API_KEY, 500);
    }

    @Test
    public void aggregate_public_undocumented_api() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 400);
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 500);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 500);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 500);
    }

    @Test
    public void compute_public_documented_api_density() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_API_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 50);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_API_KEY, 400);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_3_REF, CoreMetrics.PUBLIC_API_KEY, 300);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_3_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 200);
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY, 70.0);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY, 70.0);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY, 56.3);
    }

    @Test
    public void not_compute_public_documented_api_density_when_no_public_api() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 50);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 100);
        underTest.execute(new TestComputationStepContext());
        assertNoRawMeasures(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY);
    }

    @Test
    public void not_compute_public_documented_api_density_when_no_public_undocumented_api() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_API_KEY, 50);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_API_KEY, 100);
        underTest.execute(new TestComputationStepContext());
        assertNoRawMeasures(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY);
    }

    @Test
    public void not_compute_public_documented_api_density_when_public_api_is_zero() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_API_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 50);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_API_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 100);
        underTest.execute(new TestComputationStepContext());
        assertNoRawMeasures(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY);
    }

    @Test
    public void compute_100_percent_public_documented_api_density_when_public_undocumented_api_is_zero() {
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_API_KEY, 100);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_1_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 0);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_API_KEY, 400);
        addRawMeasure(ViewsCommentMeasuresStepTest.PROJECTVIEW_2_REF, CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY, 0);
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.SUB_MODULE_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY, 100.0);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.MODULE_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY, 100.0);
        assertRawMeasureValue(ViewsCommentMeasuresStepTest.ROOT_REF, CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY, 100.0);
    }

    @Test
    public void compute_nothing_when_no_data() {
        underTest.execute(new TestComputationStepContext());
        assertProjectViewsHasNoNewRawMeasure();
        assertThat(measureRepository.getAddedRawMeasures(ViewsCommentMeasuresStepTest.SUB_MODULE_REF)).isEmpty();
        assertThat(measureRepository.getAddedRawMeasures(ViewsCommentMeasuresStepTest.MODULE_REF)).isEmpty();
        assertThat(measureRepository.getAddedRawMeasures(ViewsCommentMeasuresStepTest.ROOT_REF)).isEmpty();
    }
}


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


public class ReportLanguageDistributionMeasuresStepTest {
    private static final String XOO_LANGUAGE = "xoo";

    private static final String JAVA_LANGUAGE = "java";

    private static final int ROOT_REF = 1;

    private static final int DIRECTORY_REF = 1234;

    private static final int FILE_1_REF = 12341;

    private static final int FILE_2_REF = 12342;

    private static final int FILE_3_REF = 12343;

    private static final int FILE_4_REF = 12344;

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(CoreMetrics.NCLOC).add(CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    ComputationStep underTest = new LanguageDistributionMeasuresStep(treeRootHolder, metricRepository, measureRepository);

    @Test
    public void compute_ncloc_language_distribution() {
        measureRepository.addRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(10));
        measureRepository.addRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_2_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(8));
        measureRepository.addRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_3_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(6));
        measureRepository.addRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_4_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(2));
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_1_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY).get().getStringValue()).isEqualTo("xoo=10");
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_2_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY).get().getStringValue()).isEqualTo("xoo=8");
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_3_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY).get().getStringValue()).isEqualTo("java=6");
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_4_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY).get().getStringValue()).isEqualTo("<null>=2");
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.DIRECTORY_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY).get().getStringValue()).isEqualTo("<null>=2;java=6;xoo=18");
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY).get().getStringValue()).isEqualTo("<null>=2;java=6;xoo=18");
    }

    @Test
    public void do_not_compute_ncloc_language_distribution_when_no_ncloc() {
        underTest.execute(new TestComputationStepContext());
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_1_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_2_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_3_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.FILE_4_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.DIRECTORY_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY)).isNotPresent();
        assertThat(measureRepository.getAddedRawMeasure(ReportLanguageDistributionMeasuresStepTest.ROOT_REF, CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION_KEY)).isNotPresent();
    }
}


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
package org.sonar.ce.task.projectanalysis.api.measurecomputer;


import CoreMetrics.NCLOC;
import Metric.MetricType;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Issue;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.Duration;
import org.sonar.ce.task.projectanalysis.component.Component.Type.FILE;
import org.sonar.ce.task.projectanalysis.component.Component.Type.PROJECT;
import org.sonar.ce.task.projectanalysis.component.ConfigurationRepository;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.issue.ComponentIssuesRepositoryRule;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.core.issue.DefaultIssue;


public class MeasureComputerContextImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String INT_METRIC_KEY = "int_metric_key";

    private static final String DOUBLE_METRIC_KEY = "double_metric_key";

    private static final String LONG_METRIC_KEY = "long_metric_key";

    private static final String STRING_METRIC_KEY = "string_metric_key";

    private static final String BOOLEAN_METRIC_KEY = "boolean_metric_key";

    private static final int PROJECT_REF = 1;

    private static final int FILE_1_REF = 12341;

    private static final String FILE_1_KEY = "fileKey";

    private static final int FILE_2_REF = 12342;

    private static final Component FILE_1 = ReportComponent.builder(FILE, MeasureComputerContextImplTest.FILE_1_REF).setKey(MeasureComputerContextImplTest.FILE_1_KEY).build();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(ReportComponent.builder(PROJECT, MeasureComputerContextImplTest.PROJECT_REF).setKey("project").addChildren(MeasureComputerContextImplTest.FILE_1, ReportComponent.builder(FILE, MeasureComputerContextImplTest.FILE_2_REF).setKey("fileKey2").build()).build());

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(1, NCLOC).add(new org.sonar.ce.task.projectanalysis.metric.MetricImpl(2, MeasureComputerContextImplTest.INT_METRIC_KEY, "int metric", MetricType.INT)).add(new org.sonar.ce.task.projectanalysis.metric.MetricImpl(3, MeasureComputerContextImplTest.DOUBLE_METRIC_KEY, "double metric", MetricType.FLOAT)).add(new org.sonar.ce.task.projectanalysis.metric.MetricImpl(4, MeasureComputerContextImplTest.LONG_METRIC_KEY, "long metric", MetricType.MILLISEC)).add(new org.sonar.ce.task.projectanalysis.metric.MetricImpl(5, MeasureComputerContextImplTest.STRING_METRIC_KEY, "string metric", MetricType.STRING)).add(new org.sonar.ce.task.projectanalysis.metric.MetricImpl(6, MeasureComputerContextImplTest.BOOLEAN_METRIC_KEY, "boolean metric", MetricType.BOOL));

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    @Rule
    public ComponentIssuesRepositoryRule componentIssuesRepository = new ComponentIssuesRepositoryRule(treeRootHolder);

    ConfigurationRepository settingsRepository = Mockito.mock(ConfigurationRepository.class);

    @Test
    public void get_component() {
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.FILE_1_REF);
        assertThat(underTest.getComponent().getType()).isEqualTo(Component.Type.FILE);
    }

    @Test
    public void get_string_settings() {
        MapSettings serverSettings = new MapSettings();
        serverSettings.setProperty("prop", "value");
        Mockito.when(settingsRepository.getConfiguration()).thenReturn(serverSettings.asConfig());
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.FILE_1_REF);
        assertThat(underTest.getSettings().getString("prop")).isEqualTo("value");
        assertThat(underTest.getSettings().getString("unknown")).isNull();
    }

    @Test
    public void get_string_array_settings() {
        MapSettings serverSettings = new MapSettings();
        serverSettings.setProperty("prop", "1,3.4,8,50");
        Mockito.when(settingsRepository.getConfiguration()).thenReturn(serverSettings.asConfig());
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.FILE_1_REF);
        assertThat(underTest.getSettings().getStringArray("prop")).containsExactly("1", "3.4", "8", "50");
        assertThat(underTest.getSettings().getStringArray("unknown")).isEmpty();
    }

    @Test
    public void get_measure() {
        measureRepository.addRawMeasure(MeasureComputerContextImplTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(10));
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, CoreMetrics.COMMENT_LINES_KEY);
        assertThat(underTest.getMeasure(CoreMetrics.NCLOC_KEY).getIntValue()).isEqualTo(10);
    }

    @Test
    public void fail_with_IAE_when_get_measure_is_called_on_metric_not_in_input_list() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only metrics in [another metric] can be used to load measures");
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, "another metric", "debt");
        underTest.getMeasure(CoreMetrics.NCLOC_KEY);
    }

    @Test
    public void get_children_measures() {
        measureRepository.addRawMeasure(MeasureComputerContextImplTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(10));
        measureRepository.addRawMeasure(MeasureComputerContextImplTest.FILE_2_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(12));
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, CoreMetrics.COMMENT_LINES_KEY);
        assertThat(underTest.getChildrenMeasures(CoreMetrics.NCLOC_KEY)).hasSize(2);
        assertThat(underTest.getChildrenMeasures(CoreMetrics.NCLOC_KEY)).extracting("intValue").containsOnly(10, 12);
    }

    @Test
    public void get_children_measures_when_one_child_has_no_value() {
        measureRepository.addRawMeasure(MeasureComputerContextImplTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, Measure.newMeasureBuilder().create(10));
        // No data on file 2
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, CoreMetrics.COMMENT_LINES_KEY);
        assertThat(underTest.getChildrenMeasures(CoreMetrics.NCLOC_KEY)).extracting("intValue").containsOnly(10);
    }

    @Test
    public void not_fail_to_get_children_measures_on_output_metric() {
        measureRepository.addRawMeasure(MeasureComputerContextImplTest.FILE_1_REF, MeasureComputerContextImplTest.INT_METRIC_KEY, Measure.newMeasureBuilder().create(10));
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, MeasureComputerContextImplTest.INT_METRIC_KEY);
        assertThat(underTest.getChildrenMeasures(MeasureComputerContextImplTest.INT_METRIC_KEY)).hasSize(1);
        assertThat(underTest.getChildrenMeasures(MeasureComputerContextImplTest.INT_METRIC_KEY)).extracting("intValue").containsOnly(10);
    }

    @Test
    public void fail_with_IAE_when_get_children_measures_is_called_on_metric_not_in_input_list() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only metrics in [another metric] can be used to load measures");
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, "another metric", "debt");
        underTest.getChildrenMeasures(CoreMetrics.NCLOC_KEY);
    }

    @Test
    public void add_int_measure_create_measure_of_type_int_with_right_value() {
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, MeasureComputerContextImplTest.INT_METRIC_KEY);
        underTest.addMeasure(MeasureComputerContextImplTest.INT_METRIC_KEY, 10);
        Optional<Measure> measure = measureRepository.getAddedRawMeasure(MeasureComputerContextImplTest.PROJECT_REF, MeasureComputerContextImplTest.INT_METRIC_KEY);
        assertThat(measure).isPresent();
        assertThat(measure.get().getIntValue()).isEqualTo(10);
    }

    @Test
    public void add_double_measure_create_measure_of_type_double_with_right_value() {
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, MeasureComputerContextImplTest.DOUBLE_METRIC_KEY);
        underTest.addMeasure(MeasureComputerContextImplTest.DOUBLE_METRIC_KEY, 10.0);
        Optional<Measure> measure = measureRepository.getAddedRawMeasure(MeasureComputerContextImplTest.PROJECT_REF, MeasureComputerContextImplTest.DOUBLE_METRIC_KEY);
        assertThat(measure).isPresent();
        assertThat(measure.get().getDoubleValue()).isEqualTo(10.0);
    }

    @Test
    public void add_long_measure_create_measure_of_type_long_with_right_value() {
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, MeasureComputerContextImplTest.LONG_METRIC_KEY);
        underTest.addMeasure(MeasureComputerContextImplTest.LONG_METRIC_KEY, 10L);
        Optional<Measure> measure = measureRepository.getAddedRawMeasure(MeasureComputerContextImplTest.PROJECT_REF, MeasureComputerContextImplTest.LONG_METRIC_KEY);
        assertThat(measure).isPresent();
        assertThat(measure.get().getLongValue()).isEqualTo(10L);
    }

    @Test
    public void add_string_measure_create_measure_of_type_string_with_right_value() {
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, MeasureComputerContextImplTest.STRING_METRIC_KEY);
        underTest.addMeasure(MeasureComputerContextImplTest.STRING_METRIC_KEY, "data");
        Optional<Measure> measure = measureRepository.getAddedRawMeasure(MeasureComputerContextImplTest.PROJECT_REF, MeasureComputerContextImplTest.STRING_METRIC_KEY);
        assertThat(measure).isPresent();
        assertThat(measure.get().getStringValue()).isEqualTo("data");
    }

    @Test
    public void add_boolean_measure_create_measure_of_type_boolean_with_right_value() {
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, MeasureComputerContextImplTest.BOOLEAN_METRIC_KEY);
        underTest.addMeasure(MeasureComputerContextImplTest.BOOLEAN_METRIC_KEY, true);
        Optional<Measure> measure = measureRepository.getAddedRawMeasure(MeasureComputerContextImplTest.PROJECT_REF, MeasureComputerContextImplTest.BOOLEAN_METRIC_KEY);
        assertThat(measure).isPresent();
        assertThat(measure.get().getBooleanValue()).isTrue();
    }

    @Test
    public void fail_with_IAE_when_add_measure_is_called_on_metric_not_in_output_list() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only metrics in [int_metric_key] can be used to add measures. Metric 'double_metric_key' is not allowed.");
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, CoreMetrics.NCLOC_KEY, MeasureComputerContextImplTest.INT_METRIC_KEY);
        underTest.addMeasure(MeasureComputerContextImplTest.DOUBLE_METRIC_KEY, 10);
    }

    @Test
    public void fail_with_unsupported_operation_when_adding_measure_that_already_exists() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("A measure on metric 'int_metric_key' already exists on component 'fileKey'");
        measureRepository.addRawMeasure(MeasureComputerContextImplTest.FILE_1_REF, MeasureComputerContextImplTest.INT_METRIC_KEY, Measure.newMeasureBuilder().create(20));
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.FILE_1_REF, CoreMetrics.NCLOC_KEY, MeasureComputerContextImplTest.INT_METRIC_KEY);
        underTest.addMeasure(MeasureComputerContextImplTest.INT_METRIC_KEY, 10);
    }

    @Test
    public void get_issues() {
        DefaultIssue issue = new DefaultIssue().setKey("KEY").setRuleKey(RuleKey.of("xoo", "S01")).setSeverity("MAJOR").setStatus("CLOSED").setResolution("FIXED").setEffort(Duration.create(10L));
        MeasureComputerContextImpl underTest = newContext(MeasureComputerContextImplTest.PROJECT_REF, Arrays.asList(issue));
        assertThat(underTest.getIssues()).hasSize(1);
        Issue result = underTest.getIssues().get(0);
        assertThat(result.key()).isEqualTo("KEY");
        assertThat(result.ruleKey()).isEqualTo(RuleKey.of("xoo", "S01"));
        assertThat(result.severity()).isEqualTo("MAJOR");
        assertThat(result.status()).isEqualTo("CLOSED");
        assertThat(result.resolution()).isEqualTo("FIXED");
        assertThat(result.effort()).isEqualTo(Duration.create(10L));
    }
}


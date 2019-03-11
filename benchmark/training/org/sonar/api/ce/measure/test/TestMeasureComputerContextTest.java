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
package org.sonar.api.ce.measure.test;


import Component.Type;
import RuleType.BUG;
import Severity.BLOCKER;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Issue;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.Settings;
import org.sonar.api.issue.Issue.RESOLUTION_FIXED;
import org.sonar.api.issue.Issue.STATUS_RESOLVED;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.Duration;


public class TestMeasureComputerContextTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    static final String INPUT_METRIC = "INPUT_METRIC";

    static final String OUTPUT_METRIC = "OUTPUT_METRIC";

    static final Component PROJECT = new TestComponent("Project", Type.PROJECT, null);

    static final MeasureComputer.MeasureComputerDefinition DEFINITION = new TestMeasureComputerDefinition.MeasureComputerDefinitionBuilderImpl().setInputMetrics(TestMeasureComputerContextTest.INPUT_METRIC).setOutputMetrics(TestMeasureComputerContextTest.OUTPUT_METRIC).build();

    Settings settings = new TestSettings();

    TestMeasureComputerContext underTest = new TestMeasureComputerContext(TestMeasureComputerContextTest.PROJECT, settings, TestMeasureComputerContextTest.DEFINITION);

    @Test
    public void get_component() throws Exception {
        assertThat(underTest.getComponent()).isEqualTo(TestMeasureComputerContextTest.PROJECT);
    }

    @Test
    public void get_settings() throws Exception {
        assertThat(underTest.getSettings()).isEqualTo(settings);
    }

    @Test
    public void get_int_measure() throws Exception {
        underTest.addInputMeasure(TestMeasureComputerContextTest.INPUT_METRIC, 10);
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.INPUT_METRIC).getIntValue()).isEqualTo(10);
    }

    @Test
    public void get_double_measure() throws Exception {
        underTest.addInputMeasure(TestMeasureComputerContextTest.INPUT_METRIC, 10.0);
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.INPUT_METRIC).getDoubleValue()).isEqualTo(10.0);
    }

    @Test
    public void get_long_measure() throws Exception {
        underTest.addInputMeasure(TestMeasureComputerContextTest.INPUT_METRIC, 10L);
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.INPUT_METRIC).getLongValue()).isEqualTo(10L);
    }

    @Test
    public void get_string_measure() throws Exception {
        underTest.addInputMeasure(TestMeasureComputerContextTest.INPUT_METRIC, "text");
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.INPUT_METRIC).getStringValue()).isEqualTo("text");
    }

    @Test
    public void get_boolean_measure() throws Exception {
        underTest.addInputMeasure(TestMeasureComputerContextTest.INPUT_METRIC, true);
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.INPUT_METRIC).getBooleanValue()).isTrue();
    }

    @Test
    public void fail_with_IAE_when_trying_to_get_measure_on_unknown_metric() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only metrics in [INPUT_METRIC] can be used to load measures");
        underTest.getMeasure("unknown");
    }

    @Test
    public void get_int_children_measures() throws Exception {
        underTest.addChildrenMeasures(TestMeasureComputerContextTest.INPUT_METRIC, 10, 20);
        assertThat(underTest.getChildrenMeasures(TestMeasureComputerContextTest.INPUT_METRIC)).hasSize(2);
    }

    @Test
    public void get_doublet_children_measures() throws Exception {
        underTest.addChildrenMeasures(TestMeasureComputerContextTest.INPUT_METRIC, 10.0, 20.0);
        assertThat(underTest.getChildrenMeasures(TestMeasureComputerContextTest.INPUT_METRIC)).hasSize(2);
    }

    @Test
    public void get_long_children_measures() throws Exception {
        underTest.addChildrenMeasures(TestMeasureComputerContextTest.INPUT_METRIC, 10L, 20L);
        assertThat(underTest.getChildrenMeasures(TestMeasureComputerContextTest.INPUT_METRIC)).hasSize(2);
    }

    @Test
    public void get_string_children_measures() throws Exception {
        underTest.addChildrenMeasures(TestMeasureComputerContextTest.INPUT_METRIC, "value1", "value2");
        assertThat(underTest.getChildrenMeasures(TestMeasureComputerContextTest.INPUT_METRIC)).hasSize(2);
    }

    @Test
    public void fail_with_IAE_when_trying_to_get_children_measures_on_unknown_metric() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only metrics in [INPUT_METRIC] can be used to load measures");
        underTest.getChildrenMeasures("unknown");
    }

    @Test
    public void add_int_measure() throws Exception {
        underTest.addMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC, 10);
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC).getIntValue()).isEqualTo(10);
    }

    @Test
    public void add_double_measure() throws Exception {
        underTest.addMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC, 10.0);
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC).getDoubleValue()).isEqualTo(10.0);
    }

    @Test
    public void add_long_measure() throws Exception {
        underTest.addMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC, 10L);
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC).getLongValue()).isEqualTo(10L);
    }

    @Test
    public void add_string_measure() throws Exception {
        underTest.addMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC, "text");
        assertThat(underTest.getMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC).getStringValue()).isEqualTo("text");
    }

    @Test
    public void fail_with_IAE_when_trying_to_add_measure_on_unknown_metric() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only metrics in [OUTPUT_METRIC] can be used to add measures. Metric 'unknown' is not allowed");
        underTest.addMeasure("unknown", 10);
    }

    @Test
    public void fail_with_IAE_when_trying_to_add_measure_on_input_metric() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only metrics in [OUTPUT_METRIC] can be used to add measures. Metric 'INPUT_METRIC' is not allowed");
        underTest.addMeasure(TestMeasureComputerContextTest.INPUT_METRIC, 10);
    }

    @Test
    public void fail_with_UOE_when_trying_to_add_same_measures_twice() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("A measure on metric 'OUTPUT_METRIC' already exists");
        underTest.addMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC, 10);
        underTest.addMeasure(TestMeasureComputerContextTest.OUTPUT_METRIC, 20);
    }

    @Test
    public void get_issues() throws Exception {
        Issue issue = new TestIssue.Builder().setKey("ABCD").setRuleKey(RuleKey.of("xoo", "S01")).setSeverity(BLOCKER).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setEffort(Duration.create(10L)).setType(BUG).build();
        underTest.setIssues(Arrays.asList(issue));
        assertThat(underTest.getIssues()).hasSize(1);
    }
}


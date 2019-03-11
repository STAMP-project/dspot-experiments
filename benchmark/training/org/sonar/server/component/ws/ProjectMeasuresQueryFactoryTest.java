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
package org.sonar.server.component.ws;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.server.component.ws.FilterParser.Criterion;
import org.sonar.server.measure.index.ProjectMeasuresQuery;
import org.sonar.server.tester.UserSessionRule;


public class ProjectMeasuresQueryFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Test
    public void create_query() {
        List<Criterion> criteria = Arrays.asList(Criterion.builder().setKey("ncloc").setOperator(Operator.GT).setValue("10").build(), Criterion.builder().setKey("coverage").setOperator(Operator.LTE).setValue("80").build());
        ProjectMeasuresQuery underTest = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(criteria, Collections.emptySet());
        assertThat(underTest.getMetricCriteria()).extracting(MetricCriterion::getMetricKey, MetricCriterion::getOperator, MetricCriterion::getValue).containsOnly(tuple("ncloc", Operator.GT, 10.0), tuple("coverage", Operator.LTE, 80.0));
    }

    @Test
    public void fail_when_no_value() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Value cannot be null for 'ncloc'");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("ncloc").setOperator(Operator.GT).setValue(null).build()), Collections.emptySet());
    }

    @Test
    public void fail_when_not_double() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Value 'ten' is not a number");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("ncloc").setOperator(Operator.GT).setValue("ten").build()), Collections.emptySet());
    }

    @Test
    public void fail_when_no_operator() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Operator cannot be null for 'ncloc'");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("ncloc").setOperator(null).setValue("ten").build()), Collections.emptySet());
    }

    @Test
    public void create_query_on_quality_gate() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("alert_status").setOperator(Operator.EQ).setValue("OK").build()), Collections.emptySet());
        assertThat(query.getQualityGateStatus().get().name()).isEqualTo("OK");
    }

    @Test
    public void fail_to_create_query_on_quality_gate_when_operator_is_not_equal() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Only equals operator is available for quality gate criteria");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("alert_status").setOperator(Operator.GT).setValue("OK").build()), Collections.emptySet());
    }

    @Test
    public void fail_to_create_query_on_quality_gate_when_value_is_incorrect() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Unknown quality gate status : 'unknown'");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("alert_status").setOperator(Operator.EQ).setValue("unknown").build()), Collections.emptySet());
    }

    @Test
    public void create_query_on_language_using_in_operator() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("languages").setOperator(Operator.IN).setValues(Arrays.asList("java", "js")).build()), Collections.emptySet());
        assertThat(query.getLanguages().get()).containsOnly("java", "js");
    }

    @Test
    public void create_query_on_language_using_equals_operator() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("languages").setOperator(Operator.EQ).setValue("java").build()), Collections.emptySet());
        assertThat(query.getLanguages().get()).containsOnly("java");
    }

    @Test
    public void fail_to_create_query_on_language_using_in_operator_and_value() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Languages should be set either by using 'languages = java' or 'languages IN (java, js)");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("languages").setOperator(Operator.IN).setValue("java").build()), Collections.emptySet());
    }

    @Test
    public void fail_to_create_query_on_language_using_eq_operator_and_values() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Languages should be set either by using 'languages = java' or 'languages IN (java, js)");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("languages").setOperator(Operator.EQ).setValues(Arrays.asList("java")).build()), Collections.emptySet());
    }

    @Test
    public void create_query_on_tag_using_in_operator() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("tags").setOperator(Operator.IN).setValues(Arrays.asList("java", "js")).build()), Collections.emptySet());
        assertThat(query.getTags().get()).containsOnly("java", "js");
    }

    @Test
    public void create_query_on_tag_using_equals_operator() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("tags").setOperator(Operator.EQ).setValue("java").build()), Collections.emptySet());
        assertThat(query.getTags().get()).containsOnly("java");
    }

    @Test
    public void fail_to_create_query_on_tag_using_in_operator_and_value() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Tags should be set either by using 'tags = java' or 'tags IN (finance, platform)");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("tags").setOperator(Operator.IN).setValue("java").build()), Collections.emptySet());
    }

    @Test
    public void fail_to_create_query_on_tag_using_eq_operator_and_values() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Tags should be set either by using 'tags = java' or 'tags IN (finance, platform)");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("tags").setOperator(Operator.EQ).setValues(Arrays.asList("java")).build()), Collections.emptySet());
    }

    @Test
    public void create_query_having_q() {
        List<Criterion> criteria = Collections.singletonList(Criterion.builder().setKey("query").setOperator(Operator.EQ).setValue("Sonar Qube").build());
        ProjectMeasuresQuery underTest = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(criteria, Collections.emptySet());
        assertThat(underTest.getQueryText().get()).isEqualTo("Sonar Qube");
    }

    @Test
    public void create_query_having_q_ignore_case_sensitive() {
        List<Criterion> criteria = Collections.singletonList(Criterion.builder().setKey("query").setOperator(Operator.EQ).setValue("Sonar Qube").build());
        ProjectMeasuresQuery underTest = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(criteria, Collections.emptySet());
        assertThat(underTest.getQueryText().get()).isEqualTo("Sonar Qube");
    }

    @Test
    public void fail_to_create_query_having_q_with_no_value() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Query is invalid");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("query").setOperator(Operator.EQ).build()), Collections.emptySet());
    }

    @Test
    public void fail_to_create_query_having_q_with_other_operator_than_equals() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Query should only be used with equals operator");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("query").setOperator(Operator.LT).setValue("java").build()), Collections.emptySet());
    }

    @Test
    public void do_not_filter_on_projectUuids_if_criteria_non_empty_and_projectUuid_is_null() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("ncloc").setOperator(Operator.EQ).setValue("10").build()), null);
        assertThat(query.getProjectUuids()).isEmpty();
    }

    @Test
    public void filter_on_projectUuids_if_projectUuid_is_empty_and_criteria_non_empty() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("ncloc").setOperator(Operator.GT).setValue("10").build()), Collections.emptySet());
        assertThat(query.getProjectUuids()).isPresent();
    }

    @Test
    public void filter_on_projectUuids_if_projectUuid_is_non_empty_and_criteria_non_empty() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.singletonList(Criterion.builder().setKey("ncloc").setOperator(Operator.GT).setValue("10").build()), Collections.singleton("foo"));
        assertThat(query.getProjectUuids()).isPresent();
    }

    @Test
    public void filter_on_projectUuids_if_projectUuid_is_empty_and_criteria_is_empty() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.emptyList(), Collections.emptySet());
        assertThat(query.getProjectUuids()).isPresent();
    }

    @Test
    public void filter_on_projectUuids_if_projectUuid_is_non_empty_and_criteria_empty() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.emptyList(), Collections.singleton("foo"));
        assertThat(query.getProjectUuids()).isPresent();
    }

    @Test
    public void convert_metric_to_lower_case() {
        ProjectMeasuresQuery query = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Arrays.asList(Criterion.builder().setKey("NCLOC").setOperator(Operator.GT).setValue("10").build(), Criterion.builder().setKey("coVERage").setOperator(Operator.LTE).setValue("80").build()), Collections.emptySet());
        assertThat(query.getMetricCriteria()).extracting(MetricCriterion::getMetricKey, MetricCriterion::getOperator, MetricCriterion::getValue).containsOnly(tuple("ncloc", Operator.GT, 10.0), tuple("coverage", Operator.LTE, 80.0));
    }

    @Test
    public void filter_no_data() {
        List<Criterion> criteria = Collections.singletonList(Criterion.builder().setKey("duplicated_lines_density").setOperator(Operator.EQ).setValue("NO_DATA").build());
        ProjectMeasuresQuery underTest = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(criteria, Collections.emptySet());
        assertThat(underTest.getMetricCriteria()).extracting(MetricCriterion::getMetricKey, MetricCriterion::isNoData).containsOnly(tuple("duplicated_lines_density", true));
    }

    @Test
    public void fail_to_use_no_data_with_operator_lower_than() {
        List<Criterion> criteria = Collections.singletonList(Criterion.builder().setKey("duplicated_lines_density").setOperator(Operator.LT).setValue("NO_DATA").build());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("NO_DATA can only be used with equals operator");
        ProjectMeasuresQueryFactory.newProjectMeasuresQuery(criteria, Collections.emptySet());
    }

    @Test
    public void filter_no_data_with_other_case() {
        List<Criterion> criteria = Collections.singletonList(Criterion.builder().setKey("duplicated_lines_density").setOperator(Operator.EQ).setValue("nO_DaTa").build());
        ProjectMeasuresQuery underTest = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(criteria, Collections.emptySet());
        assertThat(underTest.getMetricCriteria()).extracting(MetricCriterion::getMetricKey, MetricCriterion::isNoData).containsOnly(tuple("duplicated_lines_density", true));
    }

    @Test
    public void accept_empty_query() {
        ProjectMeasuresQuery result = ProjectMeasuresQueryFactory.newProjectMeasuresQuery(Collections.emptyList(), Collections.emptySet());
        assertThat(result.getMetricCriteria()).isEmpty();
    }
}


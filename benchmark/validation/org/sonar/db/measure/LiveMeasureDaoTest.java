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
package org.sonar.db.measure;


import MeasureTreeQuery.Strategy.LEAVES;
import System2.INSTANCE;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.organization.OrganizationDto;


public class LiveMeasureDaoTest {
    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private LiveMeasureDao underTest = db.getDbClient().liveMeasureDao();

    private MetricDto metric;

    @Test
    public void selectByComponentUuidsAndMetricIds() {
        LiveMeasureDto measure1 = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        LiveMeasureDto measure2 = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), measure1);
        underTest.insert(db.getSession(), measure2);
        List<LiveMeasureDto> selected = underTest.selectByComponentUuidsAndMetricIds(db.getSession(), Arrays.asList(measure1.getComponentUuid(), measure2.getComponentUuid()), Collections.singletonList(metric.getId()));
        assertThat(selected).extracting(LiveMeasureDto::getComponentUuid, LiveMeasureDto::getProjectUuid, LiveMeasureDto::getMetricId, LiveMeasureDto::getValue, LiveMeasureDto::getDataAsString).containsExactlyInAnyOrder(tuple(measure1.getComponentUuid(), measure1.getProjectUuid(), measure1.getMetricId(), measure1.getValue(), measure1.getDataAsString()), tuple(measure2.getComponentUuid(), measure2.getProjectUuid(), measure2.getMetricId(), measure2.getValue(), measure2.getDataAsString()));
        assertThat(underTest.selectByComponentUuidsAndMetricIds(db.getSession(), Collections.emptyList(), Collections.singletonList(metric.getId()))).isEmpty();
        assertThat(underTest.selectByComponentUuidsAndMetricIds(db.getSession(), Collections.singletonList(measure1.getComponentUuid()), Collections.emptyList())).isEmpty();
    }

    @Test
    public void selectByComponentUuidsAndMetricIds_returns_empty_list_if_metric_does_not_match() {
        LiveMeasureDto measure = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), measure);
        int otherMetricId = (metric.getId()) + 100;
        List<LiveMeasureDto> selected = underTest.selectByComponentUuidsAndMetricIds(db.getSession(), Collections.singletonList(measure.getComponentUuid()), Collections.singletonList(otherMetricId));
        assertThat(selected).isEmpty();
    }

    @Test
    public void selectByComponentUuidsAndMetricIds_returns_empty_list_if_component_does_not_match() {
        LiveMeasureDto measure = MeasureTesting.newLiveMeasure();
        underTest.insert(db.getSession(), measure);
        List<LiveMeasureDto> selected = underTest.selectByComponentUuidsAndMetricIds(db.getSession(), Collections.singletonList("_missing_"), Collections.singletonList(measure.getMetricId()));
        assertThat(selected).isEmpty();
    }

    @Test
    public void selectByComponentUuidsAndMetricKeys() {
        LiveMeasureDto measure1 = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        LiveMeasureDto measure2 = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), measure1);
        underTest.insert(db.getSession(), measure2);
        List<LiveMeasureDto> selected = underTest.selectByComponentUuidsAndMetricKeys(db.getSession(), Arrays.asList(measure1.getComponentUuid(), measure2.getComponentUuid()), Collections.singletonList(metric.getKey()));
        assertThat(selected).extracting(LiveMeasureDto::getComponentUuid, LiveMeasureDto::getProjectUuid, LiveMeasureDto::getMetricId, LiveMeasureDto::getValue, LiveMeasureDto::getDataAsString).containsExactlyInAnyOrder(tuple(measure1.getComponentUuid(), measure1.getProjectUuid(), measure1.getMetricId(), measure1.getValue(), measure1.getDataAsString()), tuple(measure2.getComponentUuid(), measure2.getProjectUuid(), measure2.getMetricId(), measure2.getValue(), measure2.getDataAsString()));
        assertThat(underTest.selectByComponentUuidsAndMetricKeys(db.getSession(), Collections.emptyList(), Collections.singletonList(metric.getKey()))).isEmpty();
        assertThat(underTest.selectByComponentUuidsAndMetricKeys(db.getSession(), Collections.singletonList(measure1.getComponentUuid()), Collections.emptyList())).isEmpty();
    }

    @Test
    public void selectByComponentUuidsAndMetricKeys_returns_empty_list_if_metric_does_not_match() {
        LiveMeasureDto measure = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), measure);
        List<LiveMeasureDto> selected = underTest.selectByComponentUuidsAndMetricKeys(db.getSession(), Collections.singletonList(measure.getComponentUuid()), Collections.singletonList("_other_"));
        assertThat(selected).isEmpty();
    }

    @Test
    public void selectByComponentUuidsAndMetricKeys_returns_empty_list_if_component_does_not_match() {
        LiveMeasureDto measure = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), measure);
        List<LiveMeasureDto> selected = underTest.selectByComponentUuidsAndMetricKeys(db.getSession(), Collections.singletonList("_missing_"), Collections.singletonList(metric.getKey()));
        assertThat(selected).isEmpty();
    }

    @Test
    public void selectByComponentUuidAndMetricKey() {
        LiveMeasureDto measure = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), measure);
        Optional<LiveMeasureDto> selected = underTest.selectByComponentUuidAndMetricKey(db.getSession(), measure.getComponentUuid(), metric.getKey());
        assertThat(selected).isNotEmpty();
        assertThat(selected.get()).isEqualToComparingFieldByField(measure);
    }

    @Test
    public void selectByComponentUuidAndMetricKey_return_empty_if_component_does_not_match() {
        LiveMeasureDto measure = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), measure);
        assertThat(underTest.selectByComponentUuidAndMetricKey(db.getSession(), "_missing_", metric.getKey())).isEmpty();
    }

    @Test
    public void selectByComponentUuidAndMetricKey_return_empty_if_metric_does_not_match() {
        LiveMeasureDto measure = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), measure);
        assertThat(underTest.selectByComponentUuidAndMetricKey(db.getSession(), measure.getComponentUuid(), "_missing_")).isEmpty();
    }

    @Test
    public void selectMeasure() {
        MetricDto metric = db.measures().insertMetric();
        LiveMeasureDto stored = MeasureTesting.newLiveMeasure().setMetricId(metric.getId());
        underTest.insert(db.getSession(), stored);
        // metric exists but not component
        assertThat(underTest.selectMeasure(db.getSession(), "_missing_", metric.getKey())).isEmpty();
        // component exists but not metric
        assertThat(underTest.selectMeasure(db.getSession(), stored.getComponentUuid(), "_missing_")).isEmpty();
        // component and metric don't match
        assertThat(underTest.selectMeasure(db.getSession(), "_missing_", "_missing_")).isEmpty();
        // matches
        assertThat(underTest.selectMeasure(db.getSession(), stored.getComponentUuid(), metric.getKey()).get()).isEqualToComparingFieldByField(stored);
    }

    @Test
    public void selectTreeByQuery() {
        List<LiveMeasureDto> results = new ArrayList<>();
        MetricDto metric = db.measures().insertMetric();
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        underTest.insert(db.getSession(), MeasureTesting.newLiveMeasure(file, metric).setValue(3.14));
        underTest.selectTreeByQuery(db.getSession(), project, MeasureTreeQuery.builder().setMetricIds(Collections.singleton(metric.getId())).setStrategy(LEAVES).build(), ( context) -> results.add(context.getResultObject()));
        assertThat(results).hasSize(1);
        LiveMeasureDto result = results.get(0);
        assertThat(result.getComponentUuid()).isEqualTo(file.uuid());
        assertThat(result.getMetricId()).isEqualTo(metric.getId());
        assertThat(result.getValue()).isEqualTo(3.14);
    }

    @Test
    public void selectTreeByQuery_with_empty_results() {
        List<LiveMeasureDto> results = new ArrayList<>();
        underTest.selectTreeByQuery(db.getSession(), ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()), MeasureTreeQuery.builder().setStrategy(LEAVES).build(), ( context) -> results.add(context.getResultObject()));
        assertThat(results).isEmpty();
    }

    @Test
    public void selectMeasure_map_fields() {
        MetricDto metric = db.measures().insertMetric();
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        underTest.insert(db.getSession(), MeasureTesting.newLiveMeasure(file, metric).setValue(3.14).setVariation(0.1).setData("text_value"));
        LiveMeasureDto result = underTest.selectMeasure(db.getSession(), file.uuid(), metric.getKey()).orElseThrow(() -> new IllegalArgumentException("Measure not found"));
        assertThat(result).as("Fail to map fields of %s", result.toString()).extracting(LiveMeasureDto::getProjectUuid, LiveMeasureDto::getComponentUuid, LiveMeasureDto::getMetricId, LiveMeasureDto::getValue, LiveMeasureDto::getVariation, LiveMeasureDto::getDataAsString, LiveMeasureDto::getTextValue).contains(project.uuid(), file.uuid(), metric.getId(), 3.14, 0.1, "text_value", "text_value");
    }

    @Test
    public void countNcloc() {
        OrganizationDto organization = db.organizations().insert();
        MetricDto ncloc = db.measures().insertMetric(( m) -> m.setKey("ncloc").setValueType(INT.toString()));
        MetricDto lines = db.measures().insertMetric(( m) -> m.setKey("lines").setValueType(INT.toString()));
        ComponentDto simpleProject = db.components().insertMainBranch(organization);
        db.measures().insertLiveMeasure(simpleProject, ncloc, ( m) -> m.setValue(10.0));
        ComponentDto projectWithBiggerLongLivingBranch = db.components().insertMainBranch(organization);
        ComponentDto bigLongLivingLongBranch = db.components().insertProjectBranch(projectWithBiggerLongLivingBranch, ( b) -> b.setBranchType(BranchType.LONG));
        db.measures().insertLiveMeasure(projectWithBiggerLongLivingBranch, ncloc, ( m) -> m.setValue(100.0));
        db.measures().insertLiveMeasure(bigLongLivingLongBranch, ncloc, ( m) -> m.setValue(200.0));
        ComponentDto projectWithLinesButNoLoc = db.components().insertMainBranch(organization);
        db.measures().insertLiveMeasure(projectWithLinesButNoLoc, lines, ( m) -> m.setValue(365.0));
        db.measures().insertLiveMeasure(projectWithLinesButNoLoc, ncloc, ( m) -> m.setValue(0.0));
        SumNclocDbQuery query = SumNclocDbQuery.builder().setOnlyPrivateProjects(false).setOrganizationUuid(organization.getUuid()).build();
        long result = underTest.sumNclocOfBiggestLongLivingBranch(db.getSession(), query);
        assertThat(result).isEqualTo((10L + 200L));
    }

    @Test
    public void countNcloc_empty() {
        db.measures().insertMetric(( m) -> m.setKey("ncloc").setValueType(INT.toString()));
        db.measures().insertMetric(( m) -> m.setKey("lines").setValueType(INT.toString()));
        SumNclocDbQuery query = SumNclocDbQuery.builder().setOnlyPrivateProjects(false).setOrganizationUuid(db.getDefaultOrganization().getUuid()).build();
        long result = underTest.sumNclocOfBiggestLongLivingBranch(db.getSession(), query);
        assertThat(result).isEqualTo(0L);
    }

    @Test
    public void countNcloc_and_exclude_project() {
        OrganizationDto organization = db.organizations().insert();
        MetricDto ncloc = db.measures().insertMetric(( m) -> m.setKey("ncloc").setValueType(INT.toString()));
        ComponentDto simpleProject = db.components().insertMainBranch(organization);
        db.measures().insertLiveMeasure(simpleProject, ncloc, ( m) -> m.setValue(10.0));
        ComponentDto projectWithBiggerLongLivingBranch = db.components().insertMainBranch(organization);
        ComponentDto bigLongLivingBranch = db.components().insertProjectBranch(projectWithBiggerLongLivingBranch, ( b) -> b.setBranchType(BranchType.LONG));
        db.measures().insertLiveMeasure(projectWithBiggerLongLivingBranch, ncloc, ( m) -> m.setValue(100.0));
        db.measures().insertLiveMeasure(bigLongLivingBranch, ncloc, ( m) -> m.setValue(200.0));
        ComponentDto projectToExclude = db.components().insertMainBranch(organization);
        ComponentDto projectToExcludeBranch = db.components().insertProjectBranch(projectToExclude, ( b) -> b.setBranchType(BranchType.LONG));
        db.measures().insertLiveMeasure(projectToExclude, ncloc, ( m) -> m.setValue(300.0));
        db.measures().insertLiveMeasure(projectToExcludeBranch, ncloc, ( m) -> m.setValue(400.0));
        SumNclocDbQuery query = SumNclocDbQuery.builder().setOrganizationUuid(organization.getUuid()).setProjectUuidToExclude(projectToExclude.uuid()).setOnlyPrivateProjects(false).build();
        long result = underTest.sumNclocOfBiggestLongLivingBranch(db.getSession(), query);
        assertThat(result).isEqualTo((10L + 200L));
    }

    @Test
    public void insert_data() {
        byte[] data = "text_value".getBytes(StandardCharsets.UTF_8);
        MetricDto metric = db.measures().insertMetric();
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        LiveMeasureDto measure = MeasureTesting.newLiveMeasure(file, metric).setData(data);
        underTest.insert(db.getSession(), measure);
        LiveMeasureDto result = underTest.selectMeasure(db.getSession(), file.uuid(), metric.getKey()).orElseThrow(() -> new IllegalArgumentException("Measure not found"));
        assertThat(new String(result.getData(), StandardCharsets.UTF_8)).isEqualTo("text_value");
        assertThat(result.getDataAsString()).isEqualTo("text_value");
    }

    @Test
    public void insertOrUpdate() {
        // insert
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure();
        underTest.insertOrUpdate(db.getSession(), dto);
        verifyPersisted(dto);
        verifyTableSize(1);
        // update
        dto.setValue(((dto.getValue()) + 1));
        dto.setVariation(((dto.getVariation()) + 10));
        dto.setData(((dto.getDataAsString()) + "_new"));
        underTest.insertOrUpdate(db.getSession(), dto);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void deleteByComponentUuidExcludingMetricIds() {
        LiveMeasureDto measure1 = MeasureTesting.newLiveMeasure().setComponentUuid("C1").setMetricId(1);
        LiveMeasureDto measure2 = MeasureTesting.newLiveMeasure().setComponentUuid("C1").setMetricId(2);
        LiveMeasureDto measure3 = MeasureTesting.newLiveMeasure().setComponentUuid("C1").setMetricId(3);
        LiveMeasureDto measureOtherComponent = MeasureTesting.newLiveMeasure().setComponentUuid("C2").setMetricId(3);
        underTest.insertOrUpdate(db.getSession(), measure1);
        underTest.insertOrUpdate(db.getSession(), measure2);
        underTest.insertOrUpdate(db.getSession(), measure3);
        underTest.insertOrUpdate(db.getSession(), measureOtherComponent);
        int count = underTest.deleteByComponentUuidExcludingMetricIds(db.getSession(), "C1", Arrays.asList(1, 2));
        verifyTableSize(3);
        verifyPersisted(measure1);
        verifyPersisted(measure2);
        verifyPersisted(measureOtherComponent);
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void deleteByComponentUuidExcludingMetricIds_with_empty_metrics() {
        LiveMeasureDto measure1 = MeasureTesting.newLiveMeasure().setComponentUuid("C1").setMetricId(1);
        LiveMeasureDto measure2 = MeasureTesting.newLiveMeasure().setComponentUuid("C1").setMetricId(2);
        LiveMeasureDto measureOnOtherComponent = MeasureTesting.newLiveMeasure().setComponentUuid("C2").setMetricId(2);
        underTest.insertOrUpdate(db.getSession(), measure1);
        underTest.insertOrUpdate(db.getSession(), measure2);
        underTest.insertOrUpdate(db.getSession(), measureOnOtherComponent);
        int count = underTest.deleteByComponentUuidExcludingMetricIds(db.getSession(), "C1", Collections.emptyList());
        assertThat(count).isEqualTo(2);
        verifyTableSize(1);
        verifyPersisted(measureOnOtherComponent);
    }

    @Test
    public void upsert_inserts_or_updates_row() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        // insert
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure();
        int count = underTest.upsert(db.getSession(), dto);
        verifyPersisted(dto);
        verifyTableSize(1);
        assertThat(count).isEqualTo(1);
        // update
        dto.setValue(((dto.getValue()) + 1));
        dto.setVariation(((dto.getVariation()) + 10));
        dto.setData(((dto.getDataAsString()) + "_new"));
        count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_does_not_update_row_if_values_are_not_changed() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure();
        underTest.upsert(db.getSession(), dto);
        // update
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(0);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_updates_row_if_lob_data_is_changed() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setData(RandomStringUtils.random(10000));
        underTest.upsert(db.getSession(), dto);
        // update
        dto.setData(RandomStringUtils.random(((dto.getDataAsString().length()) + 10)));
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_does_not_update_row_if_lob_data_is_not_changed() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setData(RandomStringUtils.random(10000));
        underTest.upsert(db.getSession(), dto);
        // update
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(0);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_updates_row_if_lob_data_is_removed() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setData(RandomStringUtils.random(10000));
        underTest.upsert(db.getSession(), dto);
        // update
        dto.setData(((String) (null)));
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_updates_row_if_variation_is_changed() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setVariation(40.0);
        underTest.upsert(db.getSession(), dto);
        // update
        dto.setVariation(50.0);
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_updates_row_if_variation_is_removed() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setVariation(40.0);
        underTest.upsert(db.getSession(), dto);
        // update
        dto.setVariation(null);
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_updates_row_if_variation_is_added() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setVariation(null);
        underTest.upsert(db.getSession(), dto);
        // update
        dto.setVariation(40.0);
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_updates_row_if_value_is_changed() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setValue(40.0);
        underTest.upsert(db.getSession(), dto);
        // update
        dto.setValue(50.0);
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_updates_row_if_value_is_removed() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setValue(40.0);
        underTest.upsert(db.getSession(), dto);
        // update
        dto.setValue(null);
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }

    @Test
    public void upsert_updates_row_if_value_is_added() {
        if (!(db.getDbClient().getDatabase().getDialect().supportsUpsert())) {
            return;
        }
        LiveMeasureDto dto = MeasureTesting.newLiveMeasure().setValue(null);
        underTest.upsert(db.getSession(), dto);
        // update
        dto.setValue(40.0);
        int count = underTest.upsert(db.getSession(), dto);
        assertThat(count).isEqualTo(1);
        verifyPersisted(dto);
        verifyTableSize(1);
    }
}


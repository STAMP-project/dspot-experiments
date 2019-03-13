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
package org.sonar.server.measure.ws;


import CoreMetrics.ALERT_STATUS_KEY;
import Qualifiers.PROJECT;
import ValueType.LEVEL;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.measure.LiveMeasureDto;
import org.sonar.db.metric.MetricDto;

import static Measure.createFromMeasureDto;


public class ComponentTreeSortTest {
    private static final String NUM_METRIC_KEY = "violations";

    private static final String TEXT_METRIC_KEY = "sqale_index";

    private List<MetricDto> metrics;

    private Table<String, MetricDto, ComponentTreeData.Measure> measuresByComponentUuidAndMetric;

    private List<ComponentDto> components;

    @Test
    public void sort_by_names() {
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.NAME_SORT), true, null);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("name").containsExactly("name-1", "name-2", "name-3", "name-4", "name-5", "name-6", "name-7", "name-8", "name-9");
    }

    @Test
    public void sort_by_qualifier() {
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.QUALIFIER_SORT), false, null);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("qualifier").containsExactly("qualifier-9", "qualifier-8", "qualifier-7", "qualifier-6", "qualifier-5", "qualifier-4", "qualifier-3", "qualifier-2", "qualifier-1");
    }

    @Test
    public void sort_by_path() {
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.PATH_SORT), true, null);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-1", "path-2", "path-3", "path-4", "path-5", "path-6", "path-7", "path-8", "path-9");
    }

    @Test
    public void sort_by_numerical_metric_key_ascending() {
        components.add(ComponentTreeSortTest.newComponentWithoutSnapshotId("name-without-measure", "qualifier-without-measure", "path-without-measure"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.METRIC_SORT), true, ComponentTreeSortTest.NUM_METRIC_KEY);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-1", "path-2", "path-3", "path-4", "path-5", "path-6", "path-7", "path-8", "path-9", "path-without-measure");
    }

    @Test
    public void sort_by_numerical_metric_key_descending() {
        components.add(ComponentTreeSortTest.newComponentWithoutSnapshotId("name-without-measure", "qualifier-without-measure", "path-without-measure"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.METRIC_SORT), false, ComponentTreeSortTest.NUM_METRIC_KEY);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-9", "path-8", "path-7", "path-6", "path-5", "path-4", "path-3", "path-2", "path-1", "path-without-measure");
    }

    @Test
    public void sort_by_name_ascending_in_case_of_equality() {
        components = Lists.newArrayList(ComponentTreeSortTest.newComponentWithoutSnapshotId("PROJECT 12", PROJECT, "PROJECT_PATH_1"), ComponentTreeSortTest.newComponentWithoutSnapshotId("PROJECT 11", PROJECT, "PROJECT_PATH_1"), ComponentTreeSortTest.newComponentWithoutSnapshotId("PROJECT 0", PROJECT, "PROJECT_PATH_2"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Lists.newArrayList(ComponentTreeAction.PATH_SORT), false, null);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("name").containsExactly("PROJECT 0", "PROJECT 11", "PROJECT 12");
    }

    @Test
    public void sort_by_alert_status_ascending() {
        components = Lists.newArrayList(ComponentTreeSortTest.newComponentWithoutSnapshotId("PROJECT OK 1", PROJECT, "PROJECT_OK_PATH_1"), ComponentTreeSortTest.newComponentWithoutSnapshotId("PROJECT ERROR 1", PROJECT, "PROJECT_ERROR_PATH_1"), ComponentTreeSortTest.newComponentWithoutSnapshotId("PROJECT OK 2", PROJECT, "PROJECT_OK_PATH_2"), ComponentTreeSortTest.newComponentWithoutSnapshotId("PROJECT ERROR 2", PROJECT, "PROJECT_ERROR_PATH_2"));
        metrics = Collections.singletonList(newMetricDto().setKey(ALERT_STATUS_KEY).setValueType(LEVEL.name()));
        measuresByComponentUuidAndMetric = HashBasedTable.create();
        List<String> statuses = Lists.newArrayList("OK", "ERROR");
        for (int i = 0; i < (components.size()); i++) {
            ComponentDto component = components.get(i);
            String alertStatus = statuses.get((i % 2));
            measuresByComponentUuidAndMetric.put(component.uuid(), metrics.get(0), createFromMeasureDto(new LiveMeasureDto().setData(alertStatus)));
        }
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Lists.newArrayList(ComponentTreeAction.METRIC_SORT, ComponentTreeAction.NAME_SORT), true, ALERT_STATUS_KEY);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("name").containsExactly("PROJECT ERROR 1", "PROJECT ERROR 2", "PROJECT OK 1", "PROJECT OK 2");
    }

    @Test
    public void sort_by_numerical_metric_period_1_key_ascending() {
        components.add(ComponentTreeSortTest.newComponentWithoutSnapshotId("name-without-measure", "qualifier-without-measure", "path-without-measure"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.METRIC_PERIOD_SORT), true, ComponentTreeSortTest.NUM_METRIC_KEY).setMetricPeriodSort(1);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-9", "path-8", "path-7", "path-6", "path-5", "path-4", "path-3", "path-2", "path-1", "path-without-measure");
    }

    @Test
    public void sort_by_numerical_metric_period_1_key_descending() {
        components.add(ComponentTreeSortTest.newComponentWithoutSnapshotId("name-without-measure", "qualifier-without-measure", "path-without-measure"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.METRIC_PERIOD_SORT), false, ComponentTreeSortTest.NUM_METRIC_KEY).setMetricPeriodSort(1);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-1", "path-2", "path-3", "path-4", "path-5", "path-6", "path-7", "path-8", "path-9", "path-without-measure");
    }

    @Test
    public void sort_by_numerical_metric_period_5_key() {
        components.add(ComponentTreeSortTest.newComponentWithoutSnapshotId("name-without-measure", "qualifier-without-measure", "path-without-measure"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.METRIC_SORT), false, ComponentTreeSortTest.NUM_METRIC_KEY).setMetricPeriodSort(5);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-9", "path-8", "path-7", "path-6", "path-5", "path-4", "path-3", "path-2", "path-1", "path-without-measure");
    }

    @Test
    public void sort_by_textual_metric_key_ascending() {
        components.add(ComponentTreeSortTest.newComponentWithoutSnapshotId("name-without-measure", "qualifier-without-measure", "path-without-measure"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.METRIC_SORT), true, ComponentTreeSortTest.TEXT_METRIC_KEY);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-1", "path-2", "path-3", "path-4", "path-5", "path-6", "path-7", "path-8", "path-9", "path-without-measure");
    }

    @Test
    public void sort_by_textual_metric_key_descending() {
        components.add(ComponentTreeSortTest.newComponentWithoutSnapshotId("name-without-measure", "qualifier-without-measure", "path-without-measure"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Collections.singletonList(ComponentTreeAction.METRIC_SORT), false, ComponentTreeSortTest.TEXT_METRIC_KEY);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-9", "path-8", "path-7", "path-6", "path-5", "path-4", "path-3", "path-2", "path-1", "path-without-measure");
    }

    @Test
    public void sort_on_multiple_fields() {
        components = Lists.newArrayList(ComponentTreeSortTest.newComponentWithoutSnapshotId("name-1", "qualifier-1", "path-2"), ComponentTreeSortTest.newComponentWithoutSnapshotId("name-1", "qualifier-1", "path-3"), ComponentTreeSortTest.newComponentWithoutSnapshotId("name-1", "qualifier-1", "path-1"));
        ComponentTreeRequest wsRequest = ComponentTreeSortTest.newRequest(Lists.newArrayList(ComponentTreeAction.NAME_SORT, ComponentTreeAction.QUALIFIER_SORT, ComponentTreeAction.PATH_SORT), true, null);
        List<ComponentDto> result = sortComponents(wsRequest);
        assertThat(result).extracting("path").containsExactly("path-1", "path-2", "path-3");
    }
}


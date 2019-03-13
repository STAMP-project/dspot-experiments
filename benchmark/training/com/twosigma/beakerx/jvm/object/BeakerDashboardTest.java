/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.jvm.object;


import BeakerDashboard.Serializer;
import BeakerDashboard.dashColumn;
import BeakerDashboard.dashRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.twosigma.beakerx.jvm.ObserverObjectTest;
import com.twosigma.beakerx.jvm.serialization.SerializationTestHelper;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class BeakerDashboardTest {
    private BeakerDashboard dashboard;

    private dashRow row;

    private dashColumn column;

    private static Serializer serializer;

    private static SerializationTestHelper<BeakerDashboard.Serializer, BeakerDashboard> helper;

    @Test
    public void createBeakerDashboard_dashboardHasContentNotNull() throws Exception {
        // when
        BeakerDashboard beakerDashboard = new BeakerDashboard();
        // then
        Assertions.assertThat(beakerDashboard.content).isNotNull();
    }

    @Test
    public void createBeakerDashboard_dashboardHasStyleAndClassIsNull() throws Exception {
        // when
        BeakerDashboard beakerDashboard = new BeakerDashboard();
        // then
        Assertions.assertThat(beakerDashboard.getTheClass()).isNull();
        Assertions.assertThat(beakerDashboard.getTheStyle()).isNull();
    }

    @Test
    public void newRow_createNewDashRow() throws Exception {
        // when
        dashRow row = dashboard.newRow();
        // then
        Assertions.assertThat(row).isNotNull();
    }

    @Test
    public void addRow_rowsListIsNotEmpty() throws Exception {
        // when
        dashboard.addRow(row);
        // then
        Assertions.assertThat(dashboard.getRows()).isNotEmpty();
        Assertions.assertThat(dashboard.getRows().size()).isEqualTo(1);
    }

    @Test
    public void newColumn_createNewColumnWithWidth() throws Exception {
        int width = 5;
        // when
        dashColumn dashColumn = dashboard.newColumn(width);
        // then
        Assertions.assertThat(dashColumn).isNotNull();
        Assertions.assertThat(dashColumn.getWidth()).isEqualTo(width);
    }

    @Test
    public void clear_listRowsIsEmpty() throws Exception {
        // given
        dashboard.addRow(row);
        // when
        dashboard.clear();
        // then
        Assertions.assertThat(dashboard.getRows()).isEmpty();
    }

    @Test
    public void redraw_shouldUpdateObservers() throws Exception {
        // given
        ObserverObjectTest observer = new ObserverObjectTest();
        dashboard.addObserver(observer);
        // when
        dashboard.redraw();
        // then
        Assertions.assertThat(observer.getObjectList()).isNotEmpty();
        Assertions.assertThat(observer.getObjectList().get(0)).isEqualTo(dashboard);
    }

    @Test
    public void setTheClass_hasTheClass() throws Exception {
        // when
        dashboard.setTheClass("theClass");
        // then
        Assertions.assertThat(dashboard.getTheClass()).isEqualTo("theClass");
    }

    @Test
    public void setTheStyle_hasTheStyle() throws Exception {
        // when
        dashboard.setTheStyle("theStyle");
        // then
        Assertions.assertThat(dashboard.getTheStyle()).isEqualTo("theStyle");
    }

    @Test
    public void serialize_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = BeakerDashboardTest.helper.serializeObject(dashboard);
        // then
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("BeakerDashboard");
    }

    @Test
    public void serializeTheClass_resultJsonHasTheClass() throws IOException {
        dashboard.setTheClass("test");
        // when
        JsonNode actualObj = BeakerDashboardTest.helper.serializeObject(dashboard);
        // then
        Assertions.assertThat(actualObj.get("theclass").asText()).isEqualTo("test");
    }

    @Test
    public void serializeTheStyle_resultJsonHasTheStyle() throws IOException {
        dashboard.setTheStyle("test");
        // when
        JsonNode actualObj = BeakerDashboardTest.helper.serializeObject(dashboard);
        // then
        Assertions.assertThat(actualObj.get("thestyle").asText()).isEqualTo("test");
    }

    @Test
    public void dashRowSetTheClass_dashRowHasTheClass() throws Exception {
        // when
        row.setTheClass("theClass");
        // then
        Assertions.assertThat(row.getTheClass()).isEqualTo("theClass");
    }

    @Test
    public void dashRowSetTheStyle_dashRowHasTheStyle() throws Exception {
        // when
        row.setTheStyle("theStyle");
        // then
        Assertions.assertThat(row.getTheStyle()).isEqualTo("theStyle");
    }

    @Test
    public void dashRowAddColumn_dashRowColumnListIsNotEmpty() throws Exception {
        // when
        row.addColumn(column);
        // then
        Assertions.assertThat(row.getColumns()).isNotEmpty();
        Assertions.assertThat(row.getColumns().size()).isEqualTo(1);
    }

    @Test
    public void dashRowSerializeTheClass_resultJsonHasTheClass() throws Exception {
        row.setTheClass("test");
        // when
        JsonNode actualObj = serialiseDashRow(row);
        // then
        Assertions.assertThat(actualObj.get("theclass").asText()).isEqualTo("test");
    }

    @Test
    public void dashRowSerializeTheStyle_resultJsonHasTheStyle() throws Exception {
        row.setTheStyle("test");
        // when
        JsonNode actualObj = serialiseDashRow(row);
        // then
        Assertions.assertThat(actualObj.get("thestyle").asText()).isEqualTo("test");
    }

    @Test
    public void dashRowSerializeColumn_resultJsonHasColumnListNotEmpty() throws Exception {
        row.addColumn(column);
        // when
        JsonNode actualObj = serialiseDashRow(row);
        // then
        Assertions.assertThat(((ArrayNode) (actualObj.get("cols")))).isNotEmpty();
    }

    @Test
    public void dashColumnSerializeTheClass_resultJsonHasTheClass() throws Exception {
        column.setTheClass("test");
        // when
        JsonNode actualObj = serialiseDashColumn(column);
        // then
        Assertions.assertThat(actualObj.get("theclass").asText()).isEqualTo("test");
    }

    @Test
    public void dashColumnSerializeTheStyle_resultJsonHasTheStyle() throws Exception {
        column.setTheStyle("test");
        // when
        JsonNode actualObj = serialiseDashColumn(column);
        // then
        Assertions.assertThat(actualObj.get("thestyle").asText()).isEqualTo("test");
    }

    @Test
    public void dashColumnSerializeItem_resultJsonHasPayloadListNotEmpty() throws Exception {
        column.addItem("test");
        // when
        JsonNode actualObj = serialiseDashColumn(column);
        // then
        Assertions.assertThat(((ArrayNode) (actualObj.get("payload")))).isNotEmpty();
    }

    @Test
    public void dashColumnSetTheClass_dashColumnHasTheClass() throws Exception {
        // when
        column.setTheClass("theClass");
        // then
        Assertions.assertThat(column.getTheClass()).isEqualTo("theClass");
    }

    @Test
    public void dashColumnSetTheStyle_dashColumnHasTheStyle() throws Exception {
        // when
        column.setTheStyle("theStyle");
        // then
        Assertions.assertThat(column.getTheStyle()).isEqualTo("theStyle");
    }

    @Test
    public void dashColumnAddItem_dashColumnHasPayload() throws Exception {
        // when
        column.addItem("test");
        // then
        Assertions.assertThat(column.getPayload().get(0)).isEqualTo("test");
    }

    @Test
    public void dashColumnSetWidth_dashColumnHasWidth() throws Exception {
        // when
        column.setWidth(100);
        // then
        Assertions.assertThat(column.getWidth()).isEqualTo(100);
    }
}


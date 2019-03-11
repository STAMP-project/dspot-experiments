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
package org.sonar.ce.task.projectanalysis.measure;


import Component.Type.PROJECT;
import Measure.Level;
import Measure.Level.ERROR;
import Measure.Level.OK;
import Metric.MetricType;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.ce.task.projectanalysis.analysis.MutableAnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.MutableTreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.metric.MetricImpl;
import org.sonar.db.measure.LiveMeasureDto;
import org.sonar.db.measure.MeasureDto;


@RunWith(DataProviderRunner.class)
public class MeasureToMeasureDtoTest {
    private static final MetricImpl SOME_METRIC = new MetricImpl(42, "metric_key", "metric_name", MetricType.STRING);

    private static final String SOME_DATA = "some_data";

    private static final String SOME_STRING = "some_string";

    private static final double SOME_VARIATIONS = 1.0;

    private static final MetricImpl SOME_BOOLEAN_METRIC = new MetricImpl(1, "1", "1", MetricType.BOOL);

    private static final MetricImpl SOME_INT_METRIC = new MetricImpl(2, "2", "2", MetricType.INT);

    private static final MetricImpl SOME_LONG_METRIC = new MetricImpl(3, "3", "3", MetricType.DISTRIB);

    private static final MetricImpl SOME_DOUBLE_METRIC = new MetricImpl(4, "4", "4", MetricType.FLOAT);

    private static final MetricImpl SOME_STRING_METRIC = new MetricImpl(5, "5", "5", MetricType.STRING);

    private static final MetricImpl SOME_LEVEL_METRIC = new MetricImpl(6, "6", "6", MetricType.LEVEL);

    private static final String ANALYSIS_UUID = "a1";

    private static final Component SOME_COMPONENT = ReportComponent.builder(PROJECT, 1).setUuid("uuid_1").build();

    @Rule
    public MutableAnalysisMetadataHolderRule analysisMetadataHolder = new MutableAnalysisMetadataHolderRule();

    @Rule
    public MutableTreeRootHolderRule treeRootHolder = new MutableTreeRootHolderRule();

    private MeasureToMeasureDto underTest = new MeasureToMeasureDto(analysisMetadataHolder, treeRootHolder);

    @Test(expected = NullPointerException.class)
    public void toMeasureDto_throws_NPE_if_Measure_arg_is_null() {
        underTest.toMeasureDto(null, MeasureToMeasureDtoTest.SOME_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
    }

    @Test(expected = NullPointerException.class)
    public void toMeasureDto_throws_NPE_if_Metric_arg_is_null() {
        underTest.toMeasureDto(Measure.newMeasureBuilder().createNoValue(), null, MeasureToMeasureDtoTest.SOME_COMPONENT);
    }

    @Test
    public void toMeasureDto_returns_Dto_with_variation_if_Measure_has_MeasureVariations() {
        MeasureDto measureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().setVariation(MeasureToMeasureDtoTest.SOME_VARIATIONS).create(MeasureToMeasureDtoTest.SOME_STRING), MeasureToMeasureDtoTest.SOME_STRING_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(measureDto.getVariation()).isEqualTo(1.0);
    }

    @Test
    public void toMeasureDto_returns_Dto_with_alertStatus_and_alertText_if_Measure_has_QualityGateStatus() {
        String alertText = "some error";
        MeasureDto measureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().setQualityGateStatus(new QualityGateStatus(Level.ERROR, alertText)).create(MeasureToMeasureDtoTest.SOME_STRING), MeasureToMeasureDtoTest.SOME_STRING_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(measureDto.getAlertStatus()).isEqualTo(ERROR.name());
        assertThat(measureDto.getAlertText()).isEqualTo(alertText);
    }

    @Test
    public void toMeasureDto_maps_value_to_1_or_0_and_data_from_data_field_for_BOOLEAN_metric() {
        MeasureDto trueMeasureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().create(true, MeasureToMeasureDtoTest.SOME_DATA), MeasureToMeasureDtoTest.SOME_BOOLEAN_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(trueMeasureDto.getValue()).isEqualTo(1.0);
        assertThat(trueMeasureDto.getData()).isEqualTo(MeasureToMeasureDtoTest.SOME_DATA);
        MeasureDto falseMeasureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().create(false, MeasureToMeasureDtoTest.SOME_DATA), MeasureToMeasureDtoTest.SOME_BOOLEAN_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(falseMeasureDto.getValue()).isEqualTo(0.0);
        assertThat(falseMeasureDto.getData()).isEqualTo(MeasureToMeasureDtoTest.SOME_DATA);
    }

    @Test
    public void toMeasureDto_maps_value_and_data_from_data_field_for_INT_metric() {
        MeasureDto trueMeasureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().create(123, MeasureToMeasureDtoTest.SOME_DATA), MeasureToMeasureDtoTest.SOME_INT_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(trueMeasureDto.getValue()).isEqualTo(123);
        assertThat(trueMeasureDto.getData()).isEqualTo(MeasureToMeasureDtoTest.SOME_DATA);
    }

    @Test
    public void toMeasureDto_maps_value_and_data_from_data_field_for_LONG_metric() {
        MeasureDto trueMeasureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().create(((long) (456)), MeasureToMeasureDtoTest.SOME_DATA), MeasureToMeasureDtoTest.SOME_LONG_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(trueMeasureDto.getValue()).isEqualTo(456);
        assertThat(trueMeasureDto.getData()).isEqualTo(MeasureToMeasureDtoTest.SOME_DATA);
    }

    @Test
    public void toMeasureDto_maps_value_and_data_from_data_field_for_DOUBLE_metric() {
        MeasureDto trueMeasureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().create(((double) (789)), 1, MeasureToMeasureDtoTest.SOME_DATA), MeasureToMeasureDtoTest.SOME_DOUBLE_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(trueMeasureDto.getValue()).isEqualTo(789);
        assertThat(trueMeasureDto.getData()).isEqualTo(MeasureToMeasureDtoTest.SOME_DATA);
    }

    @Test
    public void toMeasureDto_maps_to_only_data_for_STRING_metric() {
        MeasureDto trueMeasureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().create(MeasureToMeasureDtoTest.SOME_STRING), MeasureToMeasureDtoTest.SOME_STRING_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(trueMeasureDto.getValue()).isNull();
        assertThat(trueMeasureDto.getData()).isEqualTo(MeasureToMeasureDtoTest.SOME_STRING);
    }

    @Test
    public void toMeasureDto_maps_name_of_Level_to_data_and_has_no_value_for_LEVEL_metric() {
        MeasureDto trueMeasureDto = underTest.toMeasureDto(Measure.newMeasureBuilder().create(OK), MeasureToMeasureDtoTest.SOME_LEVEL_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(trueMeasureDto.getValue()).isNull();
        assertThat(trueMeasureDto.getData()).isEqualTo(OK.name());
    }

    @Test
    public void toLiveMeasureDto() {
        treeRootHolder.setRoot(MeasureToMeasureDtoTest.SOME_COMPONENT);
        LiveMeasureDto liveMeasureDto = underTest.toLiveMeasureDto(Measure.newMeasureBuilder().create(OK), MeasureToMeasureDtoTest.SOME_LEVEL_METRIC, MeasureToMeasureDtoTest.SOME_COMPONENT);
        assertThat(liveMeasureDto.getTextValue()).isEqualTo(OK.name());
    }
}


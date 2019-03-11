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


import Level.ERROR;
import Level.OK;
import Measure.ValueType.BOOLEAN;
import Measure.ValueType.DOUBLE;
import Measure.ValueType.INT;
import Measure.ValueType.LEVEL;
import Measure.ValueType.LONG;
import Measure.ValueType.NO_VALUE;
import Measure.ValueType.STRING;
import Metric.MetricType;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Optional;
import org.assertj.core.data.Offset;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.ce.task.projectanalysis.measure.Measure.Level;
import org.sonar.ce.task.projectanalysis.metric.Metric;
import org.sonar.ce.task.projectanalysis.metric.MetricImpl;
import org.sonar.db.measure.MeasureDto;


@RunWith(DataProviderRunner.class)
public class MeasureDtoToMeasureTest {
    private static final Metric SOME_INT_METRIC = new MetricImpl(42, "int", "name", MetricType.INT);

    private static final Metric SOME_LONG_METRIC = new MetricImpl(42, "long", "name", MetricType.WORK_DUR);

    private static final Metric SOME_DOUBLE_METRIC = new MetricImpl(42, "double", "name", MetricType.FLOAT);

    private static final Metric SOME_STRING_METRIC = new MetricImpl(42, "string", "name", MetricType.STRING);

    private static final Metric SOME_BOOLEAN_METRIC = new MetricImpl(42, "boolean", "name", MetricType.BOOL);

    private static final Metric SOME_LEVEL_METRIC = new MetricImpl(42, "level", "name", MetricType.LEVEL);

    private static final String SOME_DATA = "some_data man!";

    private static final String SOME_ALERT_TEXT = "some alert text_be_careFul!";

    private static final MeasureDto EMPTY_MEASURE_DTO = new MeasureDto();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private MeasureDtoToMeasure underTest = new MeasureDtoToMeasure();

    @Test
    public void toMeasure_returns_absent_for_null_argument() {
        assertThat(underTest.toMeasure(null, MeasureDtoToMeasureTest.SOME_INT_METRIC)).isNotPresent();
    }

    @Test(expected = NullPointerException.class)
    public void toMeasure_throws_NPE_if_metric_argument_is_null() {
        underTest.toMeasure(MeasureDtoToMeasureTest.EMPTY_MEASURE_DTO, null);
    }

    @Test(expected = NullPointerException.class)
    public void toMeasure_throws_NPE_if_both_arguments_are_null() {
        underTest.toMeasure(null, null);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_data_for_Level_Metric() {
        Optional<Measure> measure = underTest.toMeasure(MeasureDtoToMeasureTest.EMPTY_MEASURE_DTO, MeasureDtoToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_invalid_data_for_Level_Metric() {
        Optional<Measure> measure = underTest.toMeasure(new MeasureDto().setData("trololo"), MeasureDtoToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_no_value_if_dta_has_data_in_wrong_case_for_Level_Metric() {
        Optional<Measure> measure = underTest.toMeasure(new MeasureDto().setData("waRn"), MeasureDtoToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_no_QualityGateStatus_if_dto_has_no_alertStatus_for_Level_Metric() {
        Optional<Measure> measure = underTest.toMeasure(MeasureDtoToMeasureTest.EMPTY_MEASURE_DTO, MeasureDtoToMeasureTest.SOME_STRING_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().hasQualityGateStatus()).isFalse();
    }

    @Test
    public void toMeasure_returns_no_QualityGateStatus_if_alertStatus_has_invalid_data_for_Level_Metric() {
        Optional<Measure> measure = underTest.toMeasure(new MeasureDto().setData("trololo"), MeasureDtoToMeasureTest.SOME_STRING_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().hasQualityGateStatus()).isFalse();
    }

    @Test
    public void toMeasure_returns_no_QualityGateStatus_if_alertStatus_has_data_in_wrong_case_for_Level_Metric() {
        Optional<Measure> measure = underTest.toMeasure(new MeasureDto().setData("waRn"), MeasureDtoToMeasureTest.SOME_STRING_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().hasQualityGateStatus()).isFalse();
    }

    @Test
    public void toMeasure_returns_value_for_LEVEL_Metric() {
        for (Level level : Level.values()) {
            verify_toMeasure_returns_value_for_LEVEL_Metric(level);
        }
    }

    @Test
    public void toMeasure_for_LEVEL_Metric_can_have_an_qualityGateStatus() {
        MeasureDto measureDto = new MeasureDto().setData(OK.name()).setAlertStatus(ERROR.name()).setAlertText(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
        Optional<Measure> measure = underTest.toMeasure(measureDto, MeasureDtoToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().getValueType()).isEqualTo(LEVEL);
        assertThat(measure.get().getLevelValue()).isEqualTo(OK);
        assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(ERROR);
        assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
    }

    @Test(expected = IllegalStateException.class)
    public void toMeasure_for_LEVEL_Metric_ignores_data() {
        MeasureDto measureDto = new MeasureDto().setAlertStatus(ERROR.name()).setData(MeasureDtoToMeasureTest.SOME_DATA);
        Optional<Measure> measure = underTest.toMeasure(measureDto, MeasureDtoToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        measure.get().getStringValue();
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_Int_Metric() {
        Optional<Measure> measure = underTest.toMeasure(MeasureDtoToMeasureTest.EMPTY_MEASURE_DTO, MeasureDtoToMeasureTest.SOME_INT_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_int_part_of_value_in_dto_for_Int_Metric() {
        Optional<Measure> measure = underTest.toMeasure(new MeasureDto().setValue(1.5), MeasureDtoToMeasureTest.SOME_INT_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(INT);
        assertThat(measure.get().getIntValue()).isEqualTo(1);
    }

    @Test
    public void toMeasure_maps_data_and_alert_properties_in_dto_for_Int_Metric() {
        MeasureDto measureDto = new MeasureDto().setValue(10.0).setData(MeasureDtoToMeasureTest.SOME_DATA).setAlertStatus(OK.name()).setAlertText(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
        Optional<Measure> measure = underTest.toMeasure(measureDto, MeasureDtoToMeasureTest.SOME_INT_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(INT);
        assertThat(measure.get().getIntValue()).isEqualTo(10);
        assertThat(measure.get().getData()).isEqualTo(MeasureDtoToMeasureTest.SOME_DATA);
        assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(OK);
        assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_Long_Metric() {
        Optional<Measure> measure = underTest.toMeasure(MeasureDtoToMeasureTest.EMPTY_MEASURE_DTO, MeasureDtoToMeasureTest.SOME_LONG_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_long_part_of_value_in_dto_for_Long_Metric() {
        Optional<Measure> measure = underTest.toMeasure(new MeasureDto().setValue(1.5), MeasureDtoToMeasureTest.SOME_LONG_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(LONG);
        assertThat(measure.get().getLongValue()).isEqualTo(1);
    }

    @Test
    public void toMeasure_maps_data_and_alert_properties_in_dto_for_Long_Metric() {
        MeasureDto measureDto = new MeasureDto().setValue(10.0).setData(MeasureDtoToMeasureTest.SOME_DATA).setAlertStatus(OK.name()).setAlertText(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
        Optional<Measure> measure = underTest.toMeasure(measureDto, MeasureDtoToMeasureTest.SOME_LONG_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(LONG);
        assertThat(measure.get().getLongValue()).isEqualTo(10);
        assertThat(measure.get().getData()).isEqualTo(MeasureDtoToMeasureTest.SOME_DATA);
        assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(OK);
        assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_Double_Metric() {
        Optional<Measure> measure = underTest.toMeasure(MeasureDtoToMeasureTest.EMPTY_MEASURE_DTO, MeasureDtoToMeasureTest.SOME_DOUBLE_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_maps_data_and_alert_properties_in_dto_for_Double_Metric() {
        MeasureDto measureDto = new MeasureDto().setValue(10.6395).setData(MeasureDtoToMeasureTest.SOME_DATA).setAlertStatus(OK.name()).setAlertText(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
        Optional<Measure> measure = underTest.toMeasure(measureDto, MeasureDtoToMeasureTest.SOME_DOUBLE_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(DOUBLE);
        assertThat(measure.get().getDoubleValue()).isEqualTo(10.6395);
        assertThat(measure.get().getData()).isEqualTo(MeasureDtoToMeasureTest.SOME_DATA);
        assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(OK);
        assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_Boolean_metric() {
        Optional<Measure> measure = underTest.toMeasure(MeasureDtoToMeasureTest.EMPTY_MEASURE_DTO, MeasureDtoToMeasureTest.SOME_BOOLEAN_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_false_value_if_dto_has_invalid_value_for_Boolean_metric() {
        Optional<Measure> measure = underTest.toMeasure(new MeasureDto().setValue(1.987), MeasureDtoToMeasureTest.SOME_BOOLEAN_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(BOOLEAN);
        assertThat(measure.get().getBooleanValue()).isFalse();
    }

    @Test
    public void toMeasure_maps_data_and_alert_properties_in_dto_for_Boolean_metric() {
        MeasureDto measureDto = new MeasureDto().setValue(1.0).setData(MeasureDtoToMeasureTest.SOME_DATA).setAlertStatus(OK.name()).setAlertText(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
        Optional<Measure> measure = underTest.toMeasure(measureDto, MeasureDtoToMeasureTest.SOME_BOOLEAN_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(BOOLEAN);
        assertThat(measure.get().getBooleanValue()).isTrue();
        assertThat(measure.get().getData()).isEqualTo(MeasureDtoToMeasureTest.SOME_DATA);
        assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(OK);
        assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_String_Metric() {
        Optional<Measure> measure = underTest.toMeasure(MeasureDtoToMeasureTest.EMPTY_MEASURE_DTO, MeasureDtoToMeasureTest.SOME_STRING_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_maps_alert_properties_in_dto_for_String_Metric() {
        MeasureDto measureDto = new MeasureDto().setData(MeasureDtoToMeasureTest.SOME_DATA).setAlertStatus(OK.name()).setAlertText(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
        Optional<Measure> measure = underTest.toMeasure(measureDto, MeasureDtoToMeasureTest.SOME_STRING_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(STRING);
        assertThat(measure.get().getStringValue()).isEqualTo(MeasureDtoToMeasureTest.SOME_DATA);
        assertThat(measure.get().getData()).isEqualTo(MeasureDtoToMeasureTest.SOME_DATA);
        assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(OK);
        assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(MeasureDtoToMeasureTest.SOME_ALERT_TEXT);
    }

    @Test
    public void toMeasure_creates_MeasureVariation_and_maps_the_right_one() {
        MeasureDto measureDto = new MeasureDto().setData("1").setVariation(2.0);
        Optional<Measure> measure = underTest.toMeasure(measureDto, MeasureDtoToMeasureTest.SOME_STRING_METRIC);
        assertThat(measure.get().getVariation()).isEqualTo(2);
    }

    @Test
    public void toMeasure_should_not_loose_decimals_of_float_values() {
        MetricImpl metric = new MetricImpl(42, "double", "name", MetricType.FLOAT, 5, null, false);
        MeasureDto measureDto = new MeasureDto().setValue(0.12345);
        Optional<Measure> measure = underTest.toMeasure(measureDto, metric);
        assertThat(measure.get().getDoubleValue()).isEqualTo(0.12345, Offset.offset(1.0E-6));
    }
}


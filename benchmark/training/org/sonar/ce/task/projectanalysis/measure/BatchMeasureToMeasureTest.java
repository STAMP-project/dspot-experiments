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


import Measure.Level;
import Measure.Level.OK;
import Measure.ValueType.BOOLEAN;
import Measure.ValueType.DOUBLE;
import Measure.ValueType.INT;
import Measure.ValueType.LEVEL;
import Measure.ValueType.LONG;
import Measure.ValueType.NO_VALUE;
import Measure.ValueType.STRING;
import Metric.MetricType;
import ScannerReport.Measure;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.ce.task.projectanalysis.metric.Metric;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.Measure.BoolValue;
import org.sonar.scanner.protocol.output.ScannerReport.Measure.DoubleValue;
import org.sonar.scanner.protocol.output.ScannerReport.Measure.IntValue;
import org.sonar.scanner.protocol.output.ScannerReport.Measure.LongValue;
import org.sonar.scanner.protocol.output.ScannerReport.Measure.StringValue;


@RunWith(DataProviderRunner.class)
public class BatchMeasureToMeasureTest {
    private static final Metric SOME_INT_METRIC = new org.sonar.ce.task.projectanalysis.metric.MetricImpl(42, "int", "name", MetricType.INT);

    private static final Metric SOME_LONG_METRIC = new org.sonar.ce.task.projectanalysis.metric.MetricImpl(42, "long", "name", MetricType.WORK_DUR);

    private static final Metric SOME_DOUBLE_METRIC = new org.sonar.ce.task.projectanalysis.metric.MetricImpl(42, "double", "name", MetricType.FLOAT);

    private static final Metric SOME_STRING_METRIC = new org.sonar.ce.task.projectanalysis.metric.MetricImpl(42, "string", "name", MetricType.STRING);

    private static final Metric SOME_BOOLEAN_METRIC = new org.sonar.ce.task.projectanalysis.metric.MetricImpl(42, "boolean", "name", MetricType.BOOL);

    private static final Metric SOME_LEVEL_METRIC = new org.sonar.ce.task.projectanalysis.metric.MetricImpl(42, "level", "name", MetricType.LEVEL);

    private static final String SOME_DATA = "some_data man!";

    private static final Measure EMPTY_BATCH_MEASURE = Measure.newBuilder().build();

    private BatchMeasureToMeasure underTest = new BatchMeasureToMeasure();

    @Test
    public void toMeasure_returns_absent_for_null_argument() {
        assertThat(underTest.toMeasure(null, BatchMeasureToMeasureTest.SOME_INT_METRIC)).isNotPresent();
    }

    @Test(expected = NullPointerException.class)
    public void toMeasure_throws_NPE_if_metric_argument_is_null() {
        underTest.toMeasure(BatchMeasureToMeasureTest.EMPTY_BATCH_MEASURE, null);
    }

    @Test(expected = NullPointerException.class)
    public void toMeasure_throws_NPE_if_both_arguments_are_null() {
        underTest.toMeasure(null, null);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_string_value_for_LEVEL_Metric() {
        Optional<Measure> measure = underTest.toMeasure(BatchMeasureToMeasureTest.EMPTY_BATCH_MEASURE, BatchMeasureToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_invalid_string_value_for_LEVEL_Metric() {
        Optional<Measure> measure = underTest.toMeasure(Measure.newBuilder().setStringValue(StringValue.newBuilder().setValue("trololo")).build(), BatchMeasureToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_value_in_wrong_case_for_LEVEL_Metric() {
        Optional<Measure> measure = underTest.toMeasure(Measure.newBuilder().setStringValue(StringValue.newBuilder().setValue("waRn")).build(), BatchMeasureToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_value_for_LEVEL_Metric() {
        for (Measure.Level alertStatus : Level.values()) {
            verify_toMeasure_returns_value_for_LEVEL_Metric(alertStatus);
        }
    }

    @Test
    public void toMeasure_for_LEVEL_Metric_maps_QualityGateStatus() {
        ScannerReport.Measure batchMeasure = Measure.newBuilder().setStringValue(StringValue.newBuilder().setValue(OK.name())).build();
        Optional<Measure> measure = underTest.toMeasure(batchMeasure, BatchMeasureToMeasureTest.SOME_LEVEL_METRIC);
        assertThat(measure).isPresent();
        assertThat(measure.get().getValueType()).isEqualTo(LEVEL);
        assertThat(measure.get().getLevelValue()).isEqualTo(OK);
    }

    @Test
    public void toMeasure_for_LEVEL_Metric_parses_level_from_data() {
        for (Measure.Level level : Level.values()) {
            verify_toMeasure_for_LEVEL_Metric_parses_level_from_data(level);
        }
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_Int_Metric() {
        Optional<Measure> measure = underTest.toMeasure(BatchMeasureToMeasureTest.EMPTY_BATCH_MEASURE, BatchMeasureToMeasureTest.SOME_INT_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_maps_data_and_alert_properties_in_dto_for_Int_Metric() {
        ScannerReport.Measure batchMeasure = Measure.newBuilder().setIntValue(IntValue.newBuilder().setValue(10).setData(BatchMeasureToMeasureTest.SOME_DATA)).build();
        Optional<Measure> measure = underTest.toMeasure(batchMeasure, BatchMeasureToMeasureTest.SOME_INT_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(INT);
        assertThat(measure.get().getIntValue()).isEqualTo(10);
        assertThat(measure.get().getData()).isEqualTo(BatchMeasureToMeasureTest.SOME_DATA);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_Long_Metric() {
        Optional<Measure> measure = underTest.toMeasure(BatchMeasureToMeasureTest.EMPTY_BATCH_MEASURE, BatchMeasureToMeasureTest.SOME_LONG_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_long_part_of_value_in_dto_for_Long_Metric() {
        Optional<Measure> measure = underTest.toMeasure(Measure.newBuilder().setLongValue(LongValue.newBuilder().setValue(15L)).build(), BatchMeasureToMeasureTest.SOME_LONG_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(LONG);
        assertThat(measure.get().getLongValue()).isEqualTo(15);
    }

    @Test
    public void toMeasure_maps_data_and_alert_properties_in_dto_for_Long_Metric() {
        ScannerReport.Measure batchMeasure = Measure.newBuilder().setLongValue(LongValue.newBuilder().setValue(10L).setData(BatchMeasureToMeasureTest.SOME_DATA)).build();
        Optional<Measure> measure = underTest.toMeasure(batchMeasure, BatchMeasureToMeasureTest.SOME_LONG_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(LONG);
        assertThat(measure.get().getLongValue()).isEqualTo(10);
        assertThat(measure.get().getData()).isEqualTo(BatchMeasureToMeasureTest.SOME_DATA);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_Double_Metric() {
        Optional<Measure> measure = underTest.toMeasure(BatchMeasureToMeasureTest.EMPTY_BATCH_MEASURE, BatchMeasureToMeasureTest.SOME_DOUBLE_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_maps_data_and_alert_properties_in_dto_for_Double_Metric() {
        ScannerReport.Measure batchMeasure = Measure.newBuilder().setDoubleValue(DoubleValue.newBuilder().setValue(10.6395).setData(BatchMeasureToMeasureTest.SOME_DATA)).build();
        Optional<Measure> measure = underTest.toMeasure(batchMeasure, BatchMeasureToMeasureTest.SOME_DOUBLE_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(DOUBLE);
        assertThat(measure.get().getDoubleValue()).isEqualTo(10.6395);
        assertThat(measure.get().getData()).isEqualTo(BatchMeasureToMeasureTest.SOME_DATA);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_Boolean_metric() {
        Optional<Measure> measure = underTest.toMeasure(BatchMeasureToMeasureTest.EMPTY_BATCH_MEASURE, BatchMeasureToMeasureTest.SOME_BOOLEAN_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_returns_false_value_if_dto_has_invalid_value_for_Boolean_metric() {
        verify_toMeasure_returns_false_value_if_dto_has_invalid_value_for_Boolean_metric(true);
        verify_toMeasure_returns_false_value_if_dto_has_invalid_value_for_Boolean_metric(false);
    }

    @Test
    public void toMeasure_maps_data_and_alert_properties_in_dto_for_Boolean_metric() {
        ScannerReport.Measure batchMeasure = Measure.newBuilder().setBooleanValue(BoolValue.newBuilder().setValue(true).setData(BatchMeasureToMeasureTest.SOME_DATA)).build();
        Optional<Measure> measure = underTest.toMeasure(batchMeasure, BatchMeasureToMeasureTest.SOME_BOOLEAN_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(BOOLEAN);
        assertThat(measure.get().getBooleanValue()).isTrue();
        assertThat(measure.get().getData()).isEqualTo(BatchMeasureToMeasureTest.SOME_DATA);
    }

    @Test
    public void toMeasure_returns_no_value_if_dto_has_no_value_for_String_Metric() {
        Optional<Measure> measure = underTest.toMeasure(BatchMeasureToMeasureTest.EMPTY_BATCH_MEASURE, BatchMeasureToMeasureTest.SOME_STRING_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(NO_VALUE);
    }

    @Test
    public void toMeasure_maps_alert_properties_in_dto_for_String_Metric() {
        ScannerReport.Measure batchMeasure = Measure.newBuilder().setStringValue(StringValue.newBuilder().setValue(BatchMeasureToMeasureTest.SOME_DATA)).build();
        Optional<Measure> measure = underTest.toMeasure(batchMeasure, BatchMeasureToMeasureTest.SOME_STRING_METRIC);
        assertThat(measure.isPresent()).isTrue();
        assertThat(measure.get().getValueType()).isEqualTo(STRING);
        assertThat(measure.get().getStringValue()).isEqualTo(BatchMeasureToMeasureTest.SOME_DATA);
        assertThat(measure.get().getData()).isEqualTo(BatchMeasureToMeasureTest.SOME_DATA);
    }
}


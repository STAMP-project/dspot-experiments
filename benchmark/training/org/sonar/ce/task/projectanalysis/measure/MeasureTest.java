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
import ValueType.BOOLEAN;
import ValueType.DOUBLE;
import ValueType.INT;
import ValueType.LONG;
import ValueType.STRING;
import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.ce.task.projectanalysis.component.Developer;
import org.sonar.ce.task.projectanalysis.component.DumbDeveloper;


@RunWith(DataProviderRunner.class)
public class MeasureTest {
    private static final Measure INT_MEASURE = Measure.newMeasureBuilder().create(1);

    private static final Measure LONG_MEASURE = Measure.newMeasureBuilder().create(1L);

    private static final Measure DOUBLE_MEASURE = Measure.newMeasureBuilder().create(1.0, 1);

    private static final Measure STRING_MEASURE = Measure.newMeasureBuilder().create("some_sT ring");

    private static final Measure TRUE_MEASURE = Measure.newMeasureBuilder().create(true);

    private static final Measure FALSE_MEASURE = Measure.newMeasureBuilder().create(false);

    private static final Measure LEVEL_MEASURE = Measure.newMeasureBuilder().create(OK);

    private static final Measure NO_VALUE_MEASURE = Measure.newMeasureBuilder().createNoValue();

    private static final List<Measure> MEASURES = ImmutableList.of(MeasureTest.INT_MEASURE, MeasureTest.LONG_MEASURE, MeasureTest.DOUBLE_MEASURE, MeasureTest.STRING_MEASURE, MeasureTest.TRUE_MEASURE, MeasureTest.FALSE_MEASURE, MeasureTest.NO_VALUE_MEASURE, MeasureTest.LEVEL_MEASURE);

    private static final Developer SOME_DEVELOPER = new DumbDeveloper("DEV1");

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getDeveloper_returns_dev_set_in_builder() {
        assertThat(Measure.newMeasureBuilder().forDeveloper(MeasureTest.SOME_DEVELOPER).createNoValue().getDeveloper()).isEqualTo(MeasureTest.SOME_DEVELOPER);
    }

    @Test
    public void create_measure_for_dev() {
        Measure measure = Measure.newMeasureBuilder().forDeveloper(MeasureTest.SOME_DEVELOPER).createNoValue();
        assertThat(measure.getDeveloper()).isEqualTo(MeasureTest.SOME_DEVELOPER);
    }

    @Test(expected = NullPointerException.class)
    public void create_from_String_throws_NPE_if_arg_is_null() {
        Measure.newMeasureBuilder().create(((String) (null)));
    }

    @Test
    public void create_from_int_has_INT_value_type() {
        assertThat(MeasureTest.INT_MEASURE.getValueType()).isEqualTo(INT);
    }

    @Test
    public void create_from_long_has_LONG_value_type() {
        assertThat(MeasureTest.LONG_MEASURE.getValueType()).isEqualTo(LONG);
    }

    @Test
    public void create_from_double_has_DOUBLE_value_type() {
        assertThat(MeasureTest.DOUBLE_MEASURE.getValueType()).isEqualTo(DOUBLE);
    }

    @Test
    public void create_from_boolean_has_BOOLEAN_value_type() {
        assertThat(MeasureTest.TRUE_MEASURE.getValueType()).isEqualTo(BOOLEAN);
        assertThat(MeasureTest.FALSE_MEASURE.getValueType()).isEqualTo(BOOLEAN);
    }

    @Test
    public void create_from_String_has_STRING_value_type() {
        assertThat(MeasureTest.STRING_MEASURE.getValueType()).isEqualTo(STRING);
    }

    @Test
    public void getIntValue_returns_value_for_INT_value_type() {
        assertThat(MeasureTest.INT_MEASURE.getIntValue()).isEqualTo(1);
    }

    @Test
    public void getLongValue_returns_value_for_LONG_value_type() {
        assertThat(MeasureTest.LONG_MEASURE.getLongValue()).isEqualTo(1);
    }

    @Test
    public void getDoubleValue_returns_value_for_DOUBLE_value_type() {
        assertThat(MeasureTest.DOUBLE_MEASURE.getDoubleValue()).isEqualTo(1.0);
    }

    @Test
    public void getBooleanValue_returns_value_for_BOOLEAN_value_type() {
        assertThat(MeasureTest.TRUE_MEASURE.getBooleanValue()).isTrue();
        assertThat(MeasureTest.FALSE_MEASURE.getBooleanValue()).isFalse();
    }

    @Test
    public void getData_returns_null_for_NO_VALUE_value_type() {
        assertThat(MeasureTest.NO_VALUE_MEASURE.getData()).isNull();
    }

    @Test
    public void getData_returns_value_for_STRING_value_type() {
        assertThat(MeasureTest.STRING_MEASURE.getData()).isEqualTo(MeasureTest.STRING_MEASURE.getStringValue());
    }

    @Test
    public void getAlertStatus_returns_argument_from_setQualityGateStatus() {
        QualityGateStatus someStatus = new QualityGateStatus(Level.OK);
        assertThat(Measure.newMeasureBuilder().setQualityGateStatus(someStatus).create(true, null).getQualityGateStatus()).isEqualTo(someStatus);
        assertThat(Measure.newMeasureBuilder().setQualityGateStatus(someStatus).create(false, null).getQualityGateStatus()).isEqualTo(someStatus);
        assertThat(Measure.newMeasureBuilder().setQualityGateStatus(someStatus).create(1, null).getQualityGateStatus()).isEqualTo(someStatus);
        assertThat(Measure.newMeasureBuilder().setQualityGateStatus(someStatus).create(((long) (1)), null).getQualityGateStatus()).isEqualTo(someStatus);
        assertThat(Measure.newMeasureBuilder().setQualityGateStatus(someStatus).create(((double) (1)), 1, null).getQualityGateStatus()).isEqualTo(someStatus);
        assertThat(Measure.newMeasureBuilder().setQualityGateStatus(someStatus).create("str").getQualityGateStatus()).isEqualTo(someStatus);
        assertThat(Measure.newMeasureBuilder().setQualityGateStatus(someStatus).create(OK).getQualityGateStatus()).isEqualTo(someStatus);
    }

    @Test(expected = NullPointerException.class)
    public void newMeasureBuilder_setQualityGateStatus_throws_NPE_if_arg_is_null() {
        Measure.newMeasureBuilder().setQualityGateStatus(null);
    }

    @Test(expected = NullPointerException.class)
    public void updateMeasureBuilder_setQualityGateStatus_throws_NPE_if_arg_is_null() {
        Measure.updatedMeasureBuilder(Measure.newMeasureBuilder().createNoValue()).setQualityGateStatus(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void updateMeasureBuilder_setQualityGateStatus_throws_USO_if_measure_already_has_a_QualityGateStatus() {
        QualityGateStatus qualityGateStatus = new QualityGateStatus(Level.ERROR);
        Measure.updatedMeasureBuilder(Measure.newMeasureBuilder().setQualityGateStatus(qualityGateStatus).createNoValue()).setQualityGateStatus(qualityGateStatus);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void updateMeasureBuilder_setVariations_throws_USO_if_measure_already_has_Variations() {
        Measure.updatedMeasureBuilder(Measure.newMeasureBuilder().setVariation(1.0).createNoValue()).setVariation(2.0);
    }

    @Test
    public void getData_returns_argument_from_factory_method() {
        String someData = "lololool";
        assertThat(Measure.newMeasureBuilder().create(true, someData).getData()).isEqualTo(someData);
        assertThat(Measure.newMeasureBuilder().create(false, someData).getData()).isEqualTo(someData);
        assertThat(Measure.newMeasureBuilder().create(1, someData).getData()).isEqualTo(someData);
        assertThat(Measure.newMeasureBuilder().create(((long) (1)), someData).getData()).isEqualTo(someData);
        assertThat(Measure.newMeasureBuilder().create(((double) (1)), 1, someData).getData()).isEqualTo(someData);
    }

    @Test
    public void measure_of_value_type_LEVEL_has_no_data() {
        assertThat(MeasureTest.LEVEL_MEASURE.getData()).isNull();
    }

    @Test
    public void double_values_are_scaled_to_1_digit_and_round() {
        assertThat(Measure.newMeasureBuilder().create(30.27777, 1).getDoubleValue()).isEqualTo(30.3);
        assertThat(Measure.newMeasureBuilder().create(30.0, 1).getDoubleValue()).isEqualTo(30.0);
        assertThat(Measure.newMeasureBuilder().create(30.01, 1).getDoubleValue()).isEqualTo(30.0);
        assertThat(Measure.newMeasureBuilder().create(30.1, 1).getDoubleValue()).isEqualTo(30.1);
    }

    @Test
    public void create_with_double_value_throws_IAE_if_value_is_NaN() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("NaN is not allowed as a Measure value");
        Measure.newMeasureBuilder().create(Double.NaN, 1);
    }

    @Test
    public void create_with_double_value_data_throws_IAE_if_value_is_NaN() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("NaN is not allowed as a Measure value");
        Measure.newMeasureBuilder().create(Double.NaN, 1, "some data");
    }
}


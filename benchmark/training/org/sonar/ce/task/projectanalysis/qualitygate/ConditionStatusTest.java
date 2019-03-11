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
package org.sonar.ce.task.projectanalysis.qualitygate;


import ConditionStatus.NO_VALUE_STATUS;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class ConditionStatusTest {
    private static final String SOME_VALUE = "value";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void create_throws_NPE_if_status_argument_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("status can not be null");
        ConditionStatus.create(null, ConditionStatusTest.SOME_VALUE);
    }

    @Test
    public void create_throws_IAE_if_status_argument_is_NO_VALUE() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("EvaluationStatus 'NO_VALUE' can not be used with this method, use constant ConditionStatus.NO_VALUE_STATUS instead.");
        ConditionStatus.create(EvaluationStatus.NO_VALUE, ConditionStatusTest.SOME_VALUE);
    }

    @Test
    public void verify_getters() {
        ConditionStatus underTest = ConditionStatus.create(EvaluationStatus.OK, ConditionStatusTest.SOME_VALUE);
        assertThat(underTest.getStatus()).isEqualTo(EvaluationStatus.OK);
        assertThat(underTest.getValue()).isEqualTo(ConditionStatusTest.SOME_VALUE);
    }

    @Test
    public void verify_toString() {
        assertThat(ConditionStatus.create(EvaluationStatus.OK, ConditionStatusTest.SOME_VALUE).toString()).isEqualTo("ConditionStatus{status=OK, value='value'}");
        assertThat(NO_VALUE_STATUS.toString()).isEqualTo("ConditionStatus{status=NO_VALUE, value='null'}");
    }

    @Test
    public void constant_NO_VALUE_STATUS_has_status_NO_VALUE_and_null_value() {
        assertThat(NO_VALUE_STATUS.getStatus()).isEqualTo(EvaluationStatus.NO_VALUE);
        assertThat(NO_VALUE_STATUS.getValue()).isNull();
    }
}


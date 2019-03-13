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


import ConditionStatus.EvaluationStatus.OK;
import QualityGateStatus.ERROR;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Collections;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(DataProviderRunner.class)
public class QualityGateStatusHolderImplTest {
    private static final Map<Condition, ConditionStatus> SOME_STATUS_PER_CONDITION = Collections.singletonMap(Mockito.mock(Condition.class), ConditionStatus.create(OK, "val"));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private QualityGateStatusHolderImpl underTest = new QualityGateStatusHolderImpl();

    @Test
    public void setStatus_throws_NPE_if_globalStatus_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("global status can not be null");
        underTest.setStatus(null, QualityGateStatusHolderImplTest.SOME_STATUS_PER_CONDITION);
    }

    @Test
    public void setStatus_throws_NPE_if_statusPerCondition_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("status per condition can not be null");
        underTest.setStatus(QualityGateStatus.OK, null);
    }

    @Test
    public void setStatus_throws_ISE_if_called_twice() {
        underTest.setStatus(QualityGateStatus.OK, QualityGateStatusHolderImplTest.SOME_STATUS_PER_CONDITION);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Quality gate status has already been set in the holder");
        underTest.setStatus(null, null);
    }

    @Test
    public void getStatus_throws_ISE_if_setStatus_not_called_yet() {
        expectQGNotSetYetISE();
        underTest.getStatus();
    }

    @Test
    public void getStatusPerConditions_throws_ISE_if_setStatus_not_called_yet() {
        expectQGNotSetYetISE();
        underTest.getStatusPerConditions();
    }

    @Test
    public void getStatusPerConditions_returns_statusPerCondition_argument_from_setStatus() {
        underTest.setStatus(ERROR, QualityGateStatusHolderImplTest.SOME_STATUS_PER_CONDITION);
        assertThat(underTest.getStatusPerConditions()).isEqualTo(QualityGateStatusHolderImplTest.SOME_STATUS_PER_CONDITION);
        // a copy is made to be immutable
        assertThat(underTest.getStatusPerConditions()).isNotSameAs(QualityGateStatusHolderImplTest.SOME_STATUS_PER_CONDITION);
    }
}


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
package org.sonar.db.qualitygate;


import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;


public class QualityGateConditionDaoTest {
    private static final String[] COLUMNS_WITHOUT_TIMESTAMPS = new String[]{ "id", "qgate_id", "metric_id", "operator", "value_error" };

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private DbSession dbSession = dbTester.getSession();

    private QualityGateConditionDao underTest = dbTester.getDbClient().gateConditionDao();

    @Test
    public void testInsert() {
        prepareDbUnit(getClass(), "insert.xml");
        QualityGateConditionDto newCondition = new QualityGateConditionDto().setQualityGateId(1L).setMetricId(2L).setOperator("GT").setErrorThreshold("20");
        underTest.insert(newCondition, dbTester.getSession());
        dbTester.commit();
        dbTester.assertDbUnitTable(getClass(), "insert-result.xml", "quality_gate_conditions", "metric_id", "operator", "error_value");
        assertThat(newCondition.getId()).isNotNull();
    }

    @Test
    public void testSelectForQualityGate() {
        prepareDbUnit(getClass(), "selectForQualityGate.xml");
        assertThat(underTest.selectForQualityGate(dbSession, 1L)).hasSize(3);
        assertThat(underTest.selectForQualityGate(dbSession, 2L)).hasSize(2);
    }

    @Test
    public void testSelectById() {
        prepareDbUnit(getClass(), "selectForQualityGate.xml");
        QualityGateConditionDto selectById = underTest.selectById(1L, dbSession);
        assertThat(selectById).isNotNull();
        assertThat(selectById.getId()).isNotNull().isNotEqualTo(0L);
        assertThat(selectById.getMetricId()).isEqualTo(2L);
        assertThat(selectById.getOperator()).isEqualTo("<");
        assertThat(selectById.getQualityGateId()).isEqualTo(1L);
        assertThat(selectById.getErrorThreshold()).isEqualTo("20");
        assertThat(underTest.selectById(42L, dbSession)).isNull();
    }

    @Test
    public void testDelete() {
        prepareDbUnit(getClass(), "selectForQualityGate.xml");
        underTest.delete(new QualityGateConditionDto().setId(1L), dbSession);
        dbSession.commit();
        assertDbUnitTable(getClass(), "delete-result.xml", "quality_gate_conditions", QualityGateConditionDaoTest.COLUMNS_WITHOUT_TIMESTAMPS);
    }

    @Test
    public void testUpdate() {
        prepareDbUnit(getClass(), "selectForQualityGate.xml");
        underTest.update(new QualityGateConditionDto().setId(1L).setMetricId(7L).setOperator(">").setErrorThreshold("80"), dbSession);
        dbSession.commit();
        assertDbUnitTable(getClass(), "update-result.xml", "quality_gate_conditions", QualityGateConditionDaoTest.COLUMNS_WITHOUT_TIMESTAMPS);
    }

    @Test
    public void shouldCleanConditions() {
        prepareDbUnit(getClass(), "shouldCleanConditions.xml");
        underTest.deleteConditionsWithInvalidMetrics(dbTester.getSession());
        dbTester.commit();
        assertDbUnit(getClass(), "shouldCleanConditions-result.xml", new String[]{ "created_at", "updated_at" }, "quality_gate_conditions");
    }
}


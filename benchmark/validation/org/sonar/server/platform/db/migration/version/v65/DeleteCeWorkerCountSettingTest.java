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
package org.sonar.server.platform.db.migration.version.v65;


import java.sql.SQLException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class DeleteCeWorkerCountSettingTest {
    private static final String TABLE_PROPERTIES = "properties";

    private static final String PROPERTY_SONAR_CE_WORKER_COUNT = "sonar.ce.workerCount";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeleteCeWorkerCountSettingTest.class, "properties.sql");

    private DeleteCeWorkerCountSetting underTest = new DeleteCeWorkerCountSetting(db.database());

    @Test
    public void execute_does_not_fail_when_table_is_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void execute_deletes_ce_worker_count_property() throws SQLException {
        insertProperty(DeleteCeWorkerCountSettingTest.PROPERTY_SONAR_CE_WORKER_COUNT);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteCeWorkerCountSettingTest.TABLE_PROPERTIES)).isZero();
    }

    @Test
    public void execute_is_case_sensitive() throws SQLException {
        insertProperty(DeleteCeWorkerCountSettingTest.PROPERTY_SONAR_CE_WORKER_COUNT.toUpperCase());
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteCeWorkerCountSettingTest.TABLE_PROPERTIES)).isEqualTo(1);
    }

    @Test
    public void execute_does_not_delete_other_property() throws SQLException {
        insertProperty(RandomStringUtils.randomAlphanumeric(3));
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteCeWorkerCountSettingTest.TABLE_PROPERTIES)).isEqualTo(1);
    }
}


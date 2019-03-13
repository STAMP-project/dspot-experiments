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
package org.sonar.server.platform.db.migration.version.v70;


import java.sql.SQLException;
import java.sql.Types;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.version.v67.CreateTableAnalysisPropertiesTest;


public class CreateOrgQualityGatesTableTest {
    private static final String TABLE = "org_quality_gates";

    @Rule
    public final CoreDbTester db = CoreDbTester.createForSchema(CreateTableAnalysisPropertiesTest.class, "empty.sql");

    private CreateOrgQualityGatesTable underTest = new CreateOrgQualityGatesTable(db.database());

    @Test
    public void creates_table_on_empty_db() throws SQLException {
        underTest.execute();
        db.assertColumnDefinition(CreateOrgQualityGatesTableTest.TABLE, "uuid", Types.VARCHAR, 40, false);
        db.assertColumnDefinition(CreateOrgQualityGatesTableTest.TABLE, "organization_uuid", Types.VARCHAR, 40, false);
        db.assertColumnDefinition(CreateOrgQualityGatesTableTest.TABLE, "quality_gate_uuid", Types.VARCHAR, 40, false);
        db.assertUniqueIndex(CreateOrgQualityGatesTableTest.TABLE, "uniq_org_quality_gates", "organization_uuid", "quality_gate_uuid");
        assertThat(db.countRowsOfTable(CreateOrgQualityGatesTableTest.TABLE)).isEqualTo(0);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        underTest.execute();
        underTest.execute();
        db.assertColumnDefinition(CreateOrgQualityGatesTableTest.TABLE, "uuid", Types.VARCHAR, 40, false);
        db.assertColumnDefinition(CreateOrgQualityGatesTableTest.TABLE, "organization_uuid", Types.VARCHAR, 40, false);
        db.assertColumnDefinition(CreateOrgQualityGatesTableTest.TABLE, "quality_gate_uuid", Types.VARCHAR, 40, false);
        db.assertUniqueIndex(CreateOrgQualityGatesTableTest.TABLE, "uniq_org_quality_gates", "organization_uuid", "quality_gate_uuid");
        assertThat(db.countRowsOfTable(CreateOrgQualityGatesTableTest.TABLE)).isEqualTo(0);
    }
}


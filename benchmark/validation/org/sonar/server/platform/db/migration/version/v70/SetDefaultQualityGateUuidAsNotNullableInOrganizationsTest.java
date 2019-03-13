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
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.step.DdlChange;


public class SetDefaultQualityGateUuidAsNotNullableInOrganizationsTest {
    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(SetDefaultQualityGateUuidAsNotNullableInOrganizationsTest.class, "organizations.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DdlChange underTest = new SetDefaultQualityGateUuidAsNotNullableInOrganizations(dbTester.database());

    @Test
    public void default_quality_gate_uuid_is_set_as_not_nullable() throws SQLException {
        underTest.execute();
        dbTester.assertColumnDefinition("organizations", "default_quality_gate_uuid", Types.VARCHAR, 40, false);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        underTest.execute();
        dbTester.assertColumnDefinition("organizations", "default_quality_gate_uuid", Types.VARCHAR, 40, false);
        underTest.execute();
        dbTester.assertColumnDefinition("organizations", "default_quality_gate_uuid", Types.VARCHAR, 40, false);
    }
}


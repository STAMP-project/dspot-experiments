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
package org.sonar.server.platform.db.migration.version.v63;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class MakeDefaultOrganizationGuardedTest {
    private static final String TABLE_ORGANIZATIONS = "organizations";

    private static final String DEFAULT_ORGANIZATION_UUID = "def-org";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(MakeDefaultOrganizationGuardedTest.class, "organizations_and_internal_properties.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MakeDefaultOrganizationGuarded underTest = new MakeDefaultOrganizationGuarded(db.database(), new DefaultOrganizationUuidProviderImpl());

    @Test
    public void fails_with_ISE_when_no_default_organization_is_set() throws SQLException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization uuid is missing");
        underTest.execute();
    }

    @Test
    public void fails_with_ISE_when_default_organization_does_not_exist_in_table_ORGANIZATIONS() throws SQLException {
        insertDefaultOrganizationUuid("blabla");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization with uuid 'blabla' does not exist in table ORGANIZATIONS");
        underTest.execute();
    }

    @Test
    public void execute_sets_guarded_of_non_guarded_default_organization_to_true() throws Exception {
        setupDefaultOrganization();
        assertThat(isDefaultOrganizationGuarded()).isFalse();
        underTest.execute();
        assertThat(isDefaultOrganizationGuarded()).isTrue();
    }

    @Test
    public void execute_is_reentrant() throws Exception {
        setupDefaultOrganization();
        underTest.execute();
        underTest.execute();
        assertThat(isDefaultOrganizationGuarded()).isTrue();
    }
}


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
package org.sonar.server.platform.db.migration.version.v62;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.CoreDbTester;


public class CreateDefaultOrganizationTest {
    private static final String DEFAULT_ORGANIZATION_KEY = "default-organization";

    private static final String DEFAULT_ORGANIZATION_NAME = "Default Organization";

    private static final String INTERNAL_PROPERTY_ORGANIZATION_DEFAULT = "organization.default";

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(CreateDefaultOrganizationTest.class, "organizations_and_internal_properties.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UuidFactory uuidFactory = Mockito.mock(UuidFactory.class);

    private CreateDefaultOrganization underTest = new CreateDefaultOrganization(dbTester.database(), system2, uuidFactory);

    @Test
    public void execute_insert_data_in_organizations_and_internal_properties_when_it_does_not_exist() throws Exception {
        long now = 1222999L;
        String uuid = "a uuid";
        Mockito.when(system2.now()).thenReturn(now);
        Mockito.when(uuidFactory.create()).thenReturn(uuid);
        underTest.execute();
        try (Connection connection = dbTester.openConnection();PreparedStatement preparedStatement = createSelectStatementByKey(connection, CreateDefaultOrganizationTest.DEFAULT_ORGANIZATION_KEY);ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                assertThat(resultSet.getString(1)).isEqualTo(uuid);
                assertThat(resultSet.getString(2)).isEqualTo(CreateDefaultOrganizationTest.DEFAULT_ORGANIZATION_KEY);
                assertThat(resultSet.getString(3)).isEqualTo(CreateDefaultOrganizationTest.DEFAULT_ORGANIZATION_NAME);
                assertThat(resultSet.getString(4)).isNull();
                assertThat(resultSet.getString(5)).isNull();
                assertThat(resultSet.getString(6)).isNull();
                assertThat(resultSet.getLong(7)).isEqualTo(now);
                assertThat(resultSet.getLong(8)).isEqualTo(now);
                assertThat(resultSet.next()).isFalse();
            } else {
                fail(("Can't retrieve organization " + uuid));
            }
        }
        verifyInternalProperty(uuid);
    }

    @Test
    public void execute_inserts_internal_property_if_default_organization_already_exists() throws Exception {
        long past = 2999033L;
        String uuid = "uuidAAAA";
        insertExistingOrganization(uuid, past);
        verifyExistingOrganization(uuid, past);
        underTest.execute();
        // existing organization is unchanged
        verifyExistingOrganization(uuid, past);
        // internal property created
        verifyInternalProperty(uuid);
    }

    @Test
    public void execute_has_no_effect_if_organization_and_internal_property_already_exist() throws Exception {
        long past = 2999033L;
        String uuid = "uuidAAAA";
        insertExistingOrganization(uuid, past);
        insertExistingInternalProperty(uuid);
        verifyExistingOrganization(uuid, past);
        verifyInternalProperty(uuid);
        underTest.execute();
        verifyExistingOrganization(uuid, past);
        verifyInternalProperty(uuid);
    }

    @Test
    public void execute_is_reentrant() throws SQLException {
        Mockito.when(system2.now()).thenReturn(1222999L);
        Mockito.when(uuidFactory.create()).thenReturn("a uuid");
        underTest.execute();
        underTest.execute();
    }
}


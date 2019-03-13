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
package org.sonar.server.platform.db.migration.version.v73;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.Uuids;
import org.sonar.db.CoreDbTester;


public class PopulateSubscriptionOnOrganizationsTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateSubscriptionOnOrganizationsTest.class, "schema.sql");

    private MapSettings settings = new MapSettings();

    private System2 system2 = new TestSystem2().setNow(PopulateSubscriptionOnOrganizationsTest.NOW);

    private PopulateSubscriptionOnOrganizations underTest = new PopulateSubscriptionOnOrganizations(db.database(), system2, settings.asConfig());

    @Test
    public void set_organization_as_paid_when_containing_only_private_projects_on_sonarcloud() throws SQLException {
        setSonarCloud();
        String organization1 = insertOrganization(Uuids.createFast(), null);
        insertProject(organization1, true);
        insertProject(organization1, true);
        String organization2 = insertOrganization(Uuids.createFast(), null);
        insertProject(organization2, true);
        underTest.execute();
        assertOrganizations(tuple(organization1, "PAID", PopulateSubscriptionOnOrganizationsTest.NOW), tuple(organization2, "PAID", PopulateSubscriptionOnOrganizationsTest.NOW));
    }

    @Test
    public void set_organization_as_free_when_containing_no_private_projects_on_sonarcloud() throws SQLException {
        setSonarCloud();
        String organization1 = insertOrganization(Uuids.createFast(), null);
        insertProject(organization1, false);
        insertProject(organization1, false);
        String organization2 = insertOrganization(Uuids.createFast(), null);
        underTest.execute();
        assertOrganizations(tuple(organization1, "FREE", PopulateSubscriptionOnOrganizationsTest.NOW), tuple(organization2, "FREE", PopulateSubscriptionOnOrganizationsTest.NOW));
    }

    @Test
    public void does_nothing_when_subscription_is_already_set_on_sonarcloud() throws SQLException {
        setSonarCloud();
        String organization1 = insertOrganization(Uuids.createFast(), "PAID");
        insertProject(organization1, true);
        insertProject(organization1, true);
        String organization2 = insertOrganization(Uuids.createFast(), "FREE");
        insertProject(organization2, false);
        underTest.execute();
        assertOrganizations(tuple(organization1, "PAID", PopulateSubscriptionOnOrganizationsTest.PAST), tuple(organization2, "FREE", PopulateSubscriptionOnOrganizationsTest.PAST));
    }

    @Test
    public void migration_is_reentrant_on_sonarcloud() throws SQLException {
        setSonarCloud();
        String organization1 = insertOrganization(Uuids.createFast(), null);
        insertProject(organization1, true);
        insertProject(organization1, true);
        String organization2 = insertOrganization(Uuids.createFast(), null);
        insertProject(organization2, false);
        underTest.execute();
        underTest.execute();
        assertOrganizations(tuple(organization1, "PAID", PopulateSubscriptionOnOrganizationsTest.NOW), tuple(organization2, "FREE", PopulateSubscriptionOnOrganizationsTest.NOW));
    }

    @Test
    public void set_organization_as_sonarqube() throws SQLException {
        String defaultOrganization = insertOrganization(Uuids.createFast(), null);
        underTest.execute();
        assertOrganizations(tuple(defaultOrganization, "SONARQUBE", PopulateSubscriptionOnOrganizationsTest.NOW));
    }

    @Test
    public void does_nothing_when_subscription_is_already_set_on_sonarqube() throws SQLException {
        String defaultOrganization = insertOrganization(Uuids.createFast(), "SONARQUBE");
        underTest.execute();
        assertOrganizations(tuple(defaultOrganization, "SONARQUBE", PopulateSubscriptionOnOrganizationsTest.PAST));
    }

    @Test
    public void migration_is_reentrant_on_sonarqube() throws SQLException {
        String defaultOrganization = insertOrganization(Uuids.createFast(), null);
        underTest.execute();
        underTest.execute();
        assertOrganizations(tuple(defaultOrganization, "SONARQUBE", PopulateSubscriptionOnOrganizationsTest.NOW));
    }
}


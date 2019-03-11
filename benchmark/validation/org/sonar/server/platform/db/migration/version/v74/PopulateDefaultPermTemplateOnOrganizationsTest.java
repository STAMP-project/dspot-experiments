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
package org.sonar.server.platform.db.migration.version.v74;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class PopulateDefaultPermTemplateOnOrganizationsTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateDefaultPermTemplateOnOrganizationsTest.class, "organizations.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateDefaultPermTemplateOnOrganizationsTest.NOW);

    private PopulateDefaultPermTemplateOnOrganizations underTest = new PopulateDefaultPermTemplateOnOrganizations(db.database(), system2);

    @Test
    public void test_is_reentrant() throws SQLException {
        insertOrganization("aa", "aa-1");
        insertOrganization("bb", null);
        underTest.execute();
        underTest.execute();
        assertOrganizations(tuple("aa", "aa-1", "aa-1", "aa-1", PopulateDefaultPermTemplateOnOrganizationsTest.NOW), tuple("bb", null, null, null, PopulateDefaultPermTemplateOnOrganizationsTest.PAST));
    }

    @Test
    public void test_with_organizations() throws SQLException {
        insertOrganization("aa", "aa-1");
        insertOrganization("bb", "bb-1");
        insertOrganization("cc", null);
        underTest.execute();
        assertOrganizations(tuple("aa", "aa-1", "aa-1", "aa-1", PopulateDefaultPermTemplateOnOrganizationsTest.NOW), tuple("bb", "bb-1", "bb-1", "bb-1", PopulateDefaultPermTemplateOnOrganizationsTest.NOW), tuple("cc", null, null, null, PopulateDefaultPermTemplateOnOrganizationsTest.PAST));
    }

    @Test
    public void without_governance_no_modifications() throws SQLException {
        insertOrganization("default-organization", null);
        underTest.execute();
        assertOrganizations(tuple("default-organization", null, null, null, PopulateDefaultPermTemplateOnOrganizationsTest.PAST));
    }
}


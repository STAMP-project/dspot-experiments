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


public class PopulateNullValuesOfIsExternalOnRulesTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateNullValuesOfIsExternalOnRulesTest.class, "rules.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateNullValuesOfIsExternalOnRulesTest.NOW);

    private PopulateNullValuesOfIsExternalOnRules underTest = new PopulateNullValuesOfIsExternalOnRules(db.database(), system2);

    @Test
    public void set_is_external_to_false() throws SQLException {
        insertRule(1, null);
        insertRule(2, null);
        underTest.execute();
        assertRules(tuple(1L, false, PopulateNullValuesOfIsExternalOnRulesTest.NOW), tuple(2L, false, PopulateNullValuesOfIsExternalOnRulesTest.NOW));
    }

    @Test
    public void does_nothing_when_is_external_is_already_set() throws SQLException {
        insertRule(1, true);
        insertRule(2, false);
        underTest.execute();
        assertRules(tuple(1L, true, PopulateNullValuesOfIsExternalOnRulesTest.PAST), tuple(2L, false, PopulateNullValuesOfIsExternalOnRulesTest.PAST));
    }

    @Test
    public void migration_is_re_entrant() throws SQLException {
        insertRule(1, null);
        underTest.execute();
        underTest.execute();
        assertRules(tuple(1L, false, PopulateNullValuesOfIsExternalOnRulesTest.NOW));
    }
}


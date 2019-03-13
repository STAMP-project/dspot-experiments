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


public class PopulateIsAdHocOnRulesTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateIsAdHocOnRulesTest.class, "rules.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateIsAdHocOnRulesTest.NOW);

    private PopulateIsAdHocOnRules underTest = new PopulateIsAdHocOnRules(db.database(), system2);

    @Test
    public void set_is_ad_hoc_to_true_on_external_rules() throws SQLException {
        insertRule(1, true, null);
        insertRule(2, true, null);
        underTest.execute();
        assertRules(tuple(1L, true, true, PopulateIsAdHocOnRulesTest.NOW), tuple(2L, true, true, PopulateIsAdHocOnRulesTest.NOW));
    }

    @Test
    public void set_is_ad_hoc_to_false_on_none_external_rules() throws SQLException {
        insertRule(1, false, null);
        insertRule(2, false, null);
        underTest.execute();
        assertRules(tuple(1L, false, false, PopulateIsAdHocOnRulesTest.NOW), tuple(2L, false, false, PopulateIsAdHocOnRulesTest.NOW));
    }

    @Test
    public void does_nothing_when_is_ad_hoc_is_already_set() throws SQLException {
        insertRule(1, true, true);
        insertRule(2, false, false);
        underTest.execute();
        assertRules(tuple(1L, true, true, PopulateIsAdHocOnRulesTest.PAST), tuple(2L, false, false, PopulateIsAdHocOnRulesTest.PAST));
    }

    @Test
    public void migration_is_re_entrant() throws SQLException {
        insertRule(1, true, null);
        insertRule(2, false, null);
        underTest.execute();
        underTest.execute();
        assertRules(tuple(1L, true, true, PopulateIsAdHocOnRulesTest.NOW), tuple(2L, false, false, PopulateIsAdHocOnRulesTest.NOW));
    }
}


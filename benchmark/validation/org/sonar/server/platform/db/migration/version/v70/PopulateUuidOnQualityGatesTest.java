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
import java.util.Date;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.CoreDbTester;


public class PopulateUuidOnQualityGatesTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateUuidOnQualityGatesTest.class, "quality_gates.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateUuidOnQualityGatesTest.NOW);

    private PopulateUuidOnQualityGates underTest = new PopulateUuidOnQualityGates(db.database(), system2, UuidFactoryFast.getInstance());

    @Test
    public void has_no_effect_if_table_is_empty() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("quality_gates")).isEqualTo(0);
    }

    @Test
    public void updates_uuid_when_uuid_is_null() throws SQLException {
        insertQualityGate("Test 1", null);
        insertQualityGate("Test 2", null);
        underTest.execute();
        List<Tuple> tuples = selectAllQualityGates("NAME", "IS_BUILT_IN", "CREATED_AT", "UPDATED_AT");
        assertThat(selectAllQualityGates("NAME", "IS_BUILT_IN", "CREATED_AT", "UPDATED_AT")).containsExactlyInAnyOrder(tuple("Test 1", false, new Date(PopulateUuidOnQualityGatesTest.PAST), new Date(PopulateUuidOnQualityGatesTest.NOW)), tuple("Test 2", false, new Date(PopulateUuidOnQualityGatesTest.PAST), new Date(PopulateUuidOnQualityGatesTest.NOW)));
        selectAllQualityGates("UUID").forEach(( c) -> assertThat(c).isNotNull());
    }

    @Test
    public void does_not_update_uuid_when_uuid_is_not_null() throws SQLException {
        insertQualityGate("Test 1", "1");
        insertQualityGate("Test 2", "2");
        underTest.execute();
        assertThat(selectAllQualityGates("UUID", "NAME", "IS_BUILT_IN", "CREATED_AT", "UPDATED_AT")).containsExactlyInAnyOrder(tuple("1", "Test 1", false, new Date(PopulateUuidOnQualityGatesTest.PAST), new Date(PopulateUuidOnQualityGatesTest.PAST)), tuple("2", "Test 2", false, new Date(PopulateUuidOnQualityGatesTest.PAST), new Date(PopulateUuidOnQualityGatesTest.PAST)));
    }

    @Test
    public void execute_is_reentreant() throws SQLException {
        insertQualityGate("Test 1", null);
        insertQualityGate("Test 2", null);
        underTest.execute();
        underTest.execute();
        assertThat(selectAllQualityGates("NAME", "IS_BUILT_IN", "CREATED_AT", "UPDATED_AT")).containsExactlyInAnyOrder(tuple("Test 1", false, new Date(PopulateUuidOnQualityGatesTest.PAST), new Date(PopulateUuidOnQualityGatesTest.NOW)), tuple("Test 2", false, new Date(PopulateUuidOnQualityGatesTest.PAST), new Date(PopulateUuidOnQualityGatesTest.NOW)));
        selectAllQualityGates("UUID").forEach(( c) -> assertThat(c).isNotNull());
    }
}


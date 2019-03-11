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
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.CoreDbTester;


public class CreateBuiltInQualityGateTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    private System2 system2 = new TestSystem2().setNow(CreateBuiltInQualityGateTest.NOW);

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CreateBuiltInQualityGateTest.class, "quality_gates.sql");

    private CreateBuiltInQualityGate underTest = new CreateBuiltInQualityGate(db.database(), system2, UuidFactoryFast.getInstance());

    @Test
    public void should_create_builtin_quality_gate() throws SQLException {
        underTest.execute();
        assertThat(selectAllQualityGates()).extracting(( map) -> map.get("NAME"), ( map) -> map.get("IS_BUILT_IN"), ( map) -> map.get("CREATED_AT")).containsExactlyInAnyOrder(tuple("Sonar way", true, new Date(CreateBuiltInQualityGateTest.NOW)));
    }

    @Test
    public void should_not_create_builtin_quality_gate_if_existing() throws SQLException {
        insertQualityGate("Whatever", true);
        underTest.execute();
        assertThat(selectAllQualityGates()).extracting(( map) -> map.get("NAME"), ( map) -> map.get("IS_BUILT_IN"), ( map) -> map.get("CREATED_AT")).containsExactlyInAnyOrder(tuple("Whatever", true, new Date(CreateBuiltInQualityGateTest.PAST)));
    }
}


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
package org.sonar.server.platform.db.migration.version.v61;


import System2.INSTANCE;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;


public class RemoveViewsDefinitionFromPropertiesTest {
    private static final long NOW = 1500000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(RemoveViewsDefinitionFromPropertiesTest.class, "properties_and_internal_properties.sql");

    private System2 system2 = Mockito.spy(INSTANCE);

    private RemoveViewsDefinitionFromProperties underTest = new RemoveViewsDefinitionFromProperties(db.database(), system2);

    @Test
    public void ignore_missing_views_definition() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("internal_properties")).isEqualTo(0);
    }

    @Test
    public void move_views_definition_from_properties_to_text_value_of_internal_properties_table_when_less_than_4000() throws SQLException {
        executeAndVerify("views content", false);
    }

    @Test
    public void move_views_definition_from_properties_to_text_value_of_internal_properties_table_when_is_4000() throws SQLException {
        executeAndVerify(String.format("%1$4000.4000s", "*"), false);
    }

    @Test
    public void move_views_definition_from_properties_to_clob_value_of_internal_properties_table_when_is_more_than_4000() throws SQLException {
        executeAndVerify(((String.format("%1$4000.4000s", "*")) + "abc"), true);
    }
}


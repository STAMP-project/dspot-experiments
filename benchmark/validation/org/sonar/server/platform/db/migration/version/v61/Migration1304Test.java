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


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.db.Database;


public class Migration1304Test {
    private Database database = Mockito.mock(Database.class);

    private enum Call {

        CALL_1,
        CALL_2;}

    private List<Migration1304Test.Call> calls = new ArrayList<>();

    private ShrinkModuleUuidPathOfProjects shrinkModuleUuidPathOfProjects = new ShrinkModuleUuidPathOfProjects(database) {
        @Override
        public void execute(Context context) {
            calls.add(Migration1304Test.Call.CALL_1);
        }
    };

    private AddBUuidPathToProjects addBUuidPathToProjects = new AddBUuidPathToProjects(database) {
        @Override
        public void execute(Context context) {
            calls.add(Migration1304Test.Call.CALL_2);
        }
    };

    private Migration1304 underTest = new Migration1304(shrinkModuleUuidPathOfProjects, addBUuidPathToProjects);

    @Test
    public void execute_calls_2_delegates_in_order() throws SQLException {
        underTest.execute();
        assertThat(calls).containsExactly(Migration1304Test.Call.CALL_1, Migration1304Test.Call.CALL_2);
    }
}


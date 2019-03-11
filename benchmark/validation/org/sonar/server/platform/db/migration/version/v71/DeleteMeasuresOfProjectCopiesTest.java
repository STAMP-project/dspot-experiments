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
package org.sonar.server.platform.db.migration.version.v71;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.core.util.SequenceUuidFactory;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.step.DataChange;


public class DeleteMeasuresOfProjectCopiesTest {
    @Rule
    public final CoreDbTester db = CoreDbTester.createForSchema(DeleteMeasuresOfProjectCopiesTest.class, "initial.sql");

    private UuidFactory uuidFactory = new SequenceUuidFactory();

    private DataChange underTest = new DeleteMeasuresOfProjectCopies(db.database());

    @Test
    public void has_no_effect_if_table_is_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void delete_measures_of_project_copies_only() throws SQLException {
        String project1 = insertComponent("PRJ", "TRK", null);
        String project1Copy = insertComponent("FIL", "TRK", project1);
        String fileInProject1 = insertComponent("FIL", "FIL", null);
        String project2 = insertComponent("PRJ", "TRK", null);
        String project2Copy = insertComponent("FIL", "TRK", project2);
        String project3 = insertComponent("PRJ", "TRK", null);
        insertMeasures(project1, 3);
        insertMeasures(project1Copy, 3);
        insertMeasures(project2, 5);
        insertMeasures(project2Copy, 5);
        insertMeasures(project3, 4);
        insertMeasures(fileInProject1, 3);
        underTest.execute();
        verifyMeasures(project1, 3);
        verifyMeasures(project1Copy, 0);
        verifyMeasures(fileInProject1, 3);
        verifyMeasures(project2, 5);
        verifyMeasures(project2Copy, 0);
        verifyMeasures(project3, 4);
    }
}


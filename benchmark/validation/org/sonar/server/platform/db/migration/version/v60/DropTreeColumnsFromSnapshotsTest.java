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
package org.sonar.server.platform.db.migration.version.v60;


import DdlChange.Context;
import java.sql.SQLException;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.db.Database;
import org.sonar.db.dialect.PostgreSql;
import org.sonar.server.platform.db.migration.step.DdlChange;


public class DropTreeColumnsFromSnapshotsTest {
    private Database database = Mockito.mock(Database.class);

    private DropTreeColumnsFromSnapshots underTest = new DropTreeColumnsFromSnapshots(database);

    @Test
    public void verify_generated_sql_on_postgresql() throws SQLException {
        Mockito.when(database.getDialect()).thenReturn(new PostgreSql());
        DdlChange.Context context = Mockito.mock(Context.class);
        underTest.execute(context);
        Mockito.verify(context).execute(Collections.singletonList("ALTER TABLE snapshots DROP COLUMN parent_snapshot_id, DROP COLUMN scope, DROP COLUMN qualifier, DROP COLUMN root_snapshot_id, DROP COLUMN path, DROP COLUMN depth, DROP COLUMN root_component_uuid"));
    }
}


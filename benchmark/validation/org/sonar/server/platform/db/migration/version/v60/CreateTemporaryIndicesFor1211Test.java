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
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.db.Database;


public class CreateTemporaryIndicesFor1211Test {
    private Database db = Mockito.mock(Database.class, Mockito.RETURNS_DEEP_STUBS);

    private Context context = Mockito.mock(Context.class);

    @Test
    public void create_two_indices() throws Exception {
        CreateTemporaryIndicesFor1211 underTest = new CreateTemporaryIndicesFor1211(db);
        underTest.execute(context);
        Mockito.verify(context).execute(Arrays.asList("CREATE INDEX ce_activity_snapshot_id ON ce_activity (snapshot_id)"));
        Mockito.verify(context).execute(Arrays.asList("CREATE INDEX dup_index_psid ON duplications_index (project_snapshot_id)"));
        Mockito.verifyNoMoreInteractions(context);
    }
}


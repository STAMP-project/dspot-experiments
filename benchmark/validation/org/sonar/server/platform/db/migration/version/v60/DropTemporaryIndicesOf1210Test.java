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


public class DropTemporaryIndicesOf1210Test {
    private Database db = Mockito.mock(Database.class, Mockito.RETURNS_DEEP_STUBS);

    private Context context = Mockito.mock(Context.class);

    @Test
    public void drop_two_indices() throws Exception {
        DropTemporaryIndicesOf1210 underTest = new DropTemporaryIndicesOf1210(db);
        underTest.execute(context);
        Mockito.verify(context).execute(Arrays.asList("DROP INDEX ce_activity_snapshot_id ON ce_activity"));
        Mockito.verify(context).execute(Arrays.asList("DROP INDEX dup_index_psid ON duplications_index"));
        Mockito.verifyNoMoreInteractions(context);
    }
}


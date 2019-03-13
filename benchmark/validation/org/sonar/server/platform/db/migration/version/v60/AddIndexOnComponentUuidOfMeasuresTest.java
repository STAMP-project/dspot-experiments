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
import org.sonar.db.dialect.H2;


public class AddIndexOnComponentUuidOfMeasuresTest {
    private Database db = Mockito.mock(Database.class, Mockito.RETURNS_DEEP_STUBS);

    private Context context = Mockito.mock(Context.class);

    @Test
    public void add_index_to_measures() throws Exception {
        Mockito.when(db.getDialect()).thenReturn(new H2());
        AddIndexOnComponentUuidOfMeasures underTest = new AddIndexOnComponentUuidOfMeasures(db);
        underTest.execute(context);
        Mockito.verify(context).execute(Arrays.asList("CREATE INDEX measures_component_uuid ON project_measures (component_uuid)"));
        Mockito.verifyNoMoreInteractions(context);
    }
}


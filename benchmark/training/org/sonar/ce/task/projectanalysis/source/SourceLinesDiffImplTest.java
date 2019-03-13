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
package org.sonar.ce.task.projectanalysis.source;


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDao;
import org.sonar.db.source.FileSourceDao;


public class SourceLinesDiffImplTest {
    private DbClient dbClient = Mockito.mock(DbClient.class);

    private DbSession dbSession = Mockito.mock(DbSession.class);

    private ComponentDao componentDao = Mockito.mock(ComponentDao.class);

    private FileSourceDao fileSourceDao = Mockito.mock(FileSourceDao.class);

    private SourceLinesHashRepository sourceLinesHash = Mockito.mock(SourceLinesHashRepository.class);

    private SourceLinesDiffImpl underTest = new SourceLinesDiffImpl(dbClient, fileSourceDao, sourceLinesHash);

    private static final int FILE_REF = 1;

    private static final String FILE_KEY = String.valueOf(SourceLinesDiffImplTest.FILE_REF);

    private static final String[] CONTENT = new String[]{ "package org.sonar.ce.task.projectanalysis.source_diff;", "", "public class Foo {", "  public String bar() {", "    return \"Doh!\";", "  }", "}" };

    @Test
    public void should_find_no_diff_when_report_and_db_content_are_identical() {
        Component component = SourceLinesDiffImplTest.fileComponent(SourceLinesDiffImplTest.FILE_REF);
        mockLineHashesInDb(("" + (SourceLinesDiffImplTest.FILE_KEY)), SourceLinesDiffImplTest.CONTENT);
        setLineHashesInReport(component, SourceLinesDiffImplTest.CONTENT);
        assertThat(underTest.computeMatchingLines(component)).containsExactly(1, 2, 3, 4, 5, 6, 7);
    }
}


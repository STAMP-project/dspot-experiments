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
package org.sonar.ce.task.projectanalysis.dbmigration;


import System2.INSTANCE;
import java.sql.SQLException;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.ce.task.CeTask;
import org.sonar.db.DbTester;
import org.sonar.db.source.FileSourceDto;


public class PopulateFileSourceLineCountTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.createForSchema(INSTANCE, PopulateFileSourceLineCountTest.class, "file_sources.sql");

    private Random random = new Random();

    private CeTask ceTask = Mockito.mock(CeTask.class);

    private PopulateFileSourceLineCount underTest = new PopulateFileSourceLineCount(db.database(), ceTask);

    @Test
    public void execute_has_no_effect_on_empty_table() throws SQLException {
        String projectUuid = randomAlphanumeric(4);
        Mockito.when(ceTask.getComponent()).thenReturn(PopulateFileSourceLineCountTest.newComponent(projectUuid));
        underTest.execute();
    }

    @Test
    public void execute_populates_line_count_of_any_type() throws SQLException {
        String projectUuid = randomAlphanumeric(4);
        String fileUuid = randomAlphanumeric(5);
        Mockito.when(ceTask.getComponent()).thenReturn(PopulateFileSourceLineCountTest.newComponent(projectUuid));
        int lineCount = 1 + (random.nextInt(15));
        insertUnpopulatedFileSource(projectUuid, fileUuid, lineCount);
        assertThat(getLineCountByFileUuid(fileUuid)).isEqualTo(FileSourceDto.LINE_COUNT_NOT_POPULATED);
        underTest.execute();
        assertThat(getLineCountByFileUuid(fileUuid)).isEqualTo(lineCount);
    }

    @Test
    public void execute_changes_only_file_source_with_LINE_COUNT_NOT_POPULATED_value() throws SQLException {
        String projectUuid = randomAlphanumeric(4);
        String fileUuid1 = randomAlphanumeric(5);
        String fileUuid2 = randomAlphanumeric(6);
        String fileUuid3 = randomAlphanumeric(7);
        int lineCountFile1 = 100 + (random.nextInt(15));
        int lineCountFile2 = 50 + (random.nextInt(15));
        int lineCountFile3 = 150 + (random.nextInt(15));
        Mockito.when(ceTask.getComponent()).thenReturn(PopulateFileSourceLineCountTest.newComponent(projectUuid));
        insertPopulatedFileSource(projectUuid, fileUuid1, lineCountFile1);
        int badLineCountFile2 = insertInconsistentPopulatedFileSource(projectUuid, fileUuid2, lineCountFile2);
        insertUnpopulatedFileSource(projectUuid, fileUuid3, lineCountFile3);
        assertThat(getLineCountByFileUuid(fileUuid1)).isEqualTo(lineCountFile1);
        assertThat(getLineCountByFileUuid(fileUuid2)).isEqualTo(badLineCountFile2);
        assertThat(getLineCountByFileUuid(fileUuid3)).isEqualTo(FileSourceDto.LINE_COUNT_NOT_POPULATED);
        underTest.execute();
        assertThat(getLineCountByFileUuid(fileUuid1)).isEqualTo(lineCountFile1);
        assertThat(getLineCountByFileUuid(fileUuid2)).isEqualTo(badLineCountFile2);
        assertThat(getLineCountByFileUuid(fileUuid3)).isEqualTo(lineCountFile3);
    }

    @Test
    public void execute_changes_only_file_source_of_CeTask_component_uuid() throws SQLException {
        String projectUuid1 = randomAlphanumeric(4);
        String projectUuid2 = randomAlphanumeric(5);
        String fileUuid1 = randomAlphanumeric(6);
        String fileUuid2 = randomAlphanumeric(7);
        int lineCountFile1 = 100 + (random.nextInt(15));
        int lineCountFile2 = 30 + (random.nextInt(15));
        Mockito.when(ceTask.getComponent()).thenReturn(PopulateFileSourceLineCountTest.newComponent(projectUuid1));
        insertUnpopulatedFileSource(projectUuid1, fileUuid1, lineCountFile1);
        insertUnpopulatedFileSource(projectUuid2, fileUuid2, lineCountFile2);
        underTest.execute();
        assertThat(getLineCountByFileUuid(fileUuid1)).isEqualTo(lineCountFile1);
        assertThat(getLineCountByFileUuid(fileUuid2)).isEqualTo(FileSourceDto.LINE_COUNT_NOT_POPULATED);
    }

    @Test
    public void execute_set_line_count_to_zero_when_file_source_has_no_line_hashes() throws SQLException {
        String projectUuid = randomAlphanumeric(4);
        String fileUuid1 = randomAlphanumeric(5);
        Mockito.when(ceTask.getComponent()).thenReturn(PopulateFileSourceLineCountTest.newComponent(projectUuid));
        insertFileSource(projectUuid, fileUuid1, null, FileSourceDto.LINE_COUNT_NOT_POPULATED);
        underTest.execute();
        assertThat(getLineCountByFileUuid(fileUuid1)).isZero();
    }

    @Test
    public void execute_set_line_count_to_1_when_file_source_has_empty_line_hashes() throws SQLException {
        String projectUuid = randomAlphanumeric(4);
        String fileUuid1 = randomAlphanumeric(5);
        Mockito.when(ceTask.getComponent()).thenReturn(PopulateFileSourceLineCountTest.newComponent(projectUuid));
        insertFileSource(projectUuid, fileUuid1, "", FileSourceDto.LINE_COUNT_NOT_POPULATED);
        underTest.execute();
        assertThat(getLineCountByFileUuid(fileUuid1)).isEqualTo(1);
    }
}


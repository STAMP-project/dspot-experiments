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
package org.sonar.ce.task.projectanalysis.component;


import ScannerReport.Component;
import ScannerReport.Component.ComponentType.DIRECTORY;
import ScannerReport.Component.ComponentType.MODULE;
import ScannerReport.Component.ComponentType.PROJECT;
import ScannerReport.Metadata;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReader;


public class ReportModulesPathTest {
    private BatchReportReader reader = Mockito.mock(BatchReportReader.class);

    private ReportModulesPath reportModulesPath = new ReportModulesPath(reader);

    private Component root = addComponent(1, "project", PROJECT, null, 2);

    @Test
    public void should_not_read_hierarchy_if_metadata_available() {
        Mockito.when(reader.readMetadata()).thenReturn(Metadata.newBuilder().putModulesProjectRelativePathByKey("module1", "path1").setRootComponentRef(1).build());
        Map<String, String> pathByModuleKey = reportModulesPath.get();
        assertThat(pathByModuleKey).containsExactly(entry("module1", "path1"));
        Mockito.verify(reader).readMetadata();
        Mockito.verifyNoMoreInteractions(reader);
    }

    @Test
    public void should_read_hierarchy_if_metadata_not_available() {
        Mockito.when(reader.readMetadata()).thenReturn(Metadata.newBuilder().setRootComponentRef(1).build());
        addComponent(2, "project:module1", MODULE, "path1", 3);
        addComponent(3, "project:module1:module2", MODULE, "path1/path2", 4);
        addComponent(4, "project:module1:module2:dir", DIRECTORY, "path1/path2/dir");
        Map<String, String> pathByModuleKey = reportModulesPath.get();
        assertThat(pathByModuleKey).containsOnly(entry("project:module1", "path1"), entry("project:module1:module2", "path1/path2"));
        Mockito.verify(reader).readMetadata();
        Mockito.verify(reader).readComponent(1);
        Mockito.verify(reader).readComponent(2);
        Mockito.verify(reader).readComponent(3);
        Mockito.verify(reader).readComponent(4);
        Mockito.verifyNoMoreInteractions(reader);
    }
}


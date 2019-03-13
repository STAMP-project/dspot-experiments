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
package org.sonar.scanner.scan.filesystem;


import InputFile.Status.ADDED;
import InputFile.Status.CHANGED;
import InputFile.Status.SAME;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.Test;
import org.sonar.scanner.repository.SingleProjectRepository;
import org.sonar.scanner.scm.ScmChangedFiles;


public class StatusDetectionTest {
    @Test
    public void detect_status() {
        SingleProjectRepository ref = new SingleProjectRepository(StatusDetectionTest.createFileDataPerPathMap());
        ScmChangedFiles changedFiles = new ScmChangedFiles(null);
        StatusDetection statusDetection = new StatusDetection(ref, changedFiles);
        assertThat(statusDetection.status("foo", StatusDetectionTest.createFile("src/Foo.java"), "ABCDE")).isEqualTo(SAME);
        assertThat(statusDetection.status("foo", StatusDetectionTest.createFile("src/Foo.java"), "XXXXX")).isEqualTo(CHANGED);
        assertThat(statusDetection.status("foo", StatusDetectionTest.createFile("src/Other.java"), "QWERT")).isEqualTo(ADDED);
    }

    @Test
    public void detect_status_branches_exclude() {
        SingleProjectRepository ref = new SingleProjectRepository(StatusDetectionTest.createFileDataPerPathMap());
        ScmChangedFiles changedFiles = new ScmChangedFiles(Collections.emptyList());
        StatusDetection statusDetection = new StatusDetection(ref, changedFiles);
        // normally changed
        assertThat(statusDetection.status("foo", StatusDetectionTest.createFile("src/Foo.java"), "XXXXX")).isEqualTo(SAME);
        // normally added
        assertThat(statusDetection.status("foo", StatusDetectionTest.createFile("src/Other.java"), "QWERT")).isEqualTo(SAME);
    }

    @Test
    public void detect_status_branches_confirm() {
        SingleProjectRepository ref = new SingleProjectRepository(StatusDetectionTest.createFileDataPerPathMap());
        ScmChangedFiles changedFiles = new ScmChangedFiles(Collections.singletonList(Paths.get("module", "src", "Foo.java")));
        StatusDetection statusDetection = new StatusDetection(ref, changedFiles);
        assertThat(statusDetection.status("foo", StatusDetectionTest.createFile("src/Foo.java"), "XXXXX")).isEqualTo(CHANGED);
    }
}


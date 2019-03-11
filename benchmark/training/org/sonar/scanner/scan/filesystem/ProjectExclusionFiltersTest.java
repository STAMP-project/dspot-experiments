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


import CoreProperties.PROJECT_EXCLUSIONS_PROPERTY;
import CoreProperties.PROJECT_INCLUSIONS_PROPERTY;
import CoreProperties.PROJECT_TEST_INCLUSIONS_PROPERTY;
import InputFile.Type.MAIN;
import InputFile.Type.TEST;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.IndexedFile;
import org.sonar.api.batch.fs.internal.DefaultIndexedFile;
import org.sonar.api.config.internal.MapSettings;


public class ProjectExclusionFiltersTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private Path moduleBaseDir;

    private MapSettings settings;

    @Test
    public void no_inclusions_nor_exclusions() throws IOException {
        ProjectExclusionFilters filter = new ProjectExclusionFilters(settings.asConfig());
        IndexedFile indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/com/mycompany/FooDao.java", null);
        assertThat(filter.isExcluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isFalse();
        assertThat(filter.isExcluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), TEST)).isFalse();
        assertThat(filter.isIncluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isTrue();
        assertThat(filter.isIncluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), TEST)).isTrue();
    }

    @Test
    public void match_inclusion() throws IOException {
        settings.setProperty(PROJECT_INCLUSIONS_PROPERTY, "**/*Dao.java");
        ProjectExclusionFilters filter = new ProjectExclusionFilters(settings.asConfig());
        IndexedFile indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/com/mycompany/FooDao.java", null);
        assertThat(filter.isIncluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isTrue();
        indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/com/mycompany/Foo.java", null);
        assertThat(filter.isIncluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isFalse();
    }

    @Test
    public void match_at_least_one_inclusion() throws IOException {
        settings.setProperty(PROJECT_INCLUSIONS_PROPERTY, "**/*Dao.java,**/*Dto.java");
        ProjectExclusionFilters filter = new ProjectExclusionFilters(settings.asConfig());
        IndexedFile indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/com/mycompany/Foo.java", null);
        assertThat(filter.isIncluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isFalse();
        indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/com/mycompany/FooDto.java", null);
        assertThat(filter.isIncluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isTrue();
    }

    @Test
    public void match_exclusions() throws IOException {
        settings.setProperty(PROJECT_INCLUSIONS_PROPERTY, "src/main/java/**/*");
        settings.setProperty(PROJECT_TEST_INCLUSIONS_PROPERTY, "src/test/java/**/*");
        settings.setProperty(PROJECT_EXCLUSIONS_PROPERTY, "**/*Dao.java");
        ProjectExclusionFilters filter = new ProjectExclusionFilters(settings.asConfig());
        IndexedFile indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/com/mycompany/FooDao.java", null);
        assertThat(filter.isExcluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isTrue();
        indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/com/mycompany/Foo.java", null);
        assertThat(filter.isExcluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isFalse();
        // source exclusions do not apply to tests
        indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/test/java/com/mycompany/FooDao.java", null);
        assertThat(filter.isExcluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), TEST)).isFalse();
    }

    @Test
    public void match_exclusion_by_absolute_path() throws IOException {
        File excludedFile = new File(moduleBaseDir.toString(), "src/main/java/org/bar/Bar.java");
        settings.setProperty(PROJECT_INCLUSIONS_PROPERTY, "src/main/java/**/*");
        settings.setProperty(PROJECT_EXCLUSIONS_PROPERTY, ("file:" + (excludedFile.getAbsolutePath())));
        ProjectExclusionFilters filter = new ProjectExclusionFilters(settings.asConfig());
        IndexedFile indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/org/bar/Foo.java", null);
        assertThat(filter.isExcluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isFalse();
        indexedFile = new DefaultIndexedFile("foo", moduleBaseDir, "src/main/java/org/bar/Bar.java", null);
        assertThat(filter.isExcluded(indexedFile.path(), Paths.get(indexedFile.relativePath()), MAIN)).isTrue();
    }

    @Test
    public void trim_pattern() {
        ProjectExclusionFilters filter = new ProjectExclusionFilters(settings.asConfig());
        assertThat(filter.prepareMainExclusions(new String[]{ "   **/*Dao.java   " }, new String[0])[0].toString()).isEqualTo("**/*Dao.java");
    }
}


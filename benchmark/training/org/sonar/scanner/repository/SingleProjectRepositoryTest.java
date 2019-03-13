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
package org.sonar.scanner.repository;


import org.junit.Assert;
import org.junit.Test;


public class SingleProjectRepositoryTest {
    private SingleProjectRepository repository;

    @Test
    public void test_file_data_when_file_exists() {
        FileData fileData = repository.fileData("/Abc.java");
        Assert.assertNotNull(fileData);
        assertThat(fileData.hash()).isEqualTo("123");
        assertThat(fileData.revision()).isEqualTo("456");
    }

    @Test
    public void test_file_data_when_file_does_not_exist() {
        FileData fileData = repository.fileData("/Def.java");
        assertThat(fileData).isNull();
    }
}


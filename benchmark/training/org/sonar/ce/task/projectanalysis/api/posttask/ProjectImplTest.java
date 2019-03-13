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
package org.sonar.ce.task.projectanalysis.api.posttask;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ProjectImplTest {
    private static final String SOME_UUID = "some uuid";

    private static final String SOME_KEY = "some key";

    private static final String SOME_NAME = "some name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor_throws_NPE_if_uuid_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("uuid can not be null");
        new ProjectImpl(null, ProjectImplTest.SOME_KEY, ProjectImplTest.SOME_NAME);
    }

    @Test
    public void constructor_throws_NPE_if_key_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("key can not be null");
        new ProjectImpl(ProjectImplTest.SOME_UUID, null, ProjectImplTest.SOME_NAME);
    }

    @Test
    public void constructor_throws_NPE_if_name_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("name can not be null");
        new ProjectImpl(ProjectImplTest.SOME_UUID, ProjectImplTest.SOME_KEY, null);
    }

    @Test
    public void verify_getters() {
        ProjectImpl underTest = new ProjectImpl(ProjectImplTest.SOME_UUID, ProjectImplTest.SOME_KEY, ProjectImplTest.SOME_NAME);
        assertThat(underTest.getUuid()).isEqualTo(ProjectImplTest.SOME_UUID);
        assertThat(underTest.getKey()).isEqualTo(ProjectImplTest.SOME_KEY);
        assertThat(underTest.getName()).isEqualTo(ProjectImplTest.SOME_NAME);
    }

    @Test
    public void verify_toString() {
        assertThat(new ProjectImpl(ProjectImplTest.SOME_UUID, ProjectImplTest.SOME_KEY, ProjectImplTest.SOME_NAME).toString()).isEqualTo("ProjectImpl{uuid='some uuid', key='some key', name='some name'}");
    }
}


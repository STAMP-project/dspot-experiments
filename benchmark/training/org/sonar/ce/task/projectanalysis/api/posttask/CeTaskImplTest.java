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


import CeTask.Status;
import CeTask.Status.FAILED;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CeTaskImplTest {
    private static final String SOME_ID = "some id";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor_throws_NPE_if_id_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("id can not be null");
        new CeTaskImpl(null, Status.SUCCESS);
    }

    @Test
    public void constructor_throws_NPE_if_status_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("status can not be null");
        new CeTaskImpl(CeTaskImplTest.SOME_ID, null);
    }

    @Test
    public void verify_getters() {
        CeTaskImpl underTest = new CeTaskImpl(CeTaskImplTest.SOME_ID, Status.FAILED);
        assertThat(underTest.getId()).isEqualTo(CeTaskImplTest.SOME_ID);
        assertThat(underTest.getStatus()).isEqualTo(FAILED);
    }

    @Test
    public void verify_toString() {
        assertThat(new CeTaskImpl(CeTaskImplTest.SOME_ID, Status.SUCCESS).toString()).isEqualTo("CeTaskImpl{id='some id', status=SUCCESS}");
    }
}


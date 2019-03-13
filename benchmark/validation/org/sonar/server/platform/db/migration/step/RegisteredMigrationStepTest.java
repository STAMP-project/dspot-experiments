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
package org.sonar.server.platform.db.migration.step;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class RegisteredMigrationStepTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor_throws_NPE_if_description_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("description can't be null");
        new RegisteredMigrationStep(1, null, MigrationStep.class);
    }

    @Test
    public void constructor_throws_NPE_if_MigrationStep_class_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("MigrationStep class can't be null");
        new RegisteredMigrationStep(1, "", null);
    }

    @Test
    public void verify_getters() {
        RegisteredMigrationStep underTest = new RegisteredMigrationStep(3, "foo", RegisteredMigrationStepTest.MyMigrationStep.class);
        assertThat(underTest.getMigrationNumber()).isEqualTo(3L);
        assertThat(underTest.getDescription()).isEqualTo("foo");
        assertThat(underTest.getStepClass()).isEqualTo(RegisteredMigrationStepTest.MyMigrationStep.class);
    }

    private abstract static class MyMigrationStep implements MigrationStep {}
}


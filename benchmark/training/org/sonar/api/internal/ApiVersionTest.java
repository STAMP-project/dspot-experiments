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
package org.sonar.api.internal;


import System2.INSTANCE;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.Version;


public class ApiVersionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void load_version_from_file_in_classpath() {
        Version version = ApiVersion.load(INSTANCE);
        assertThat(version).isNotNull();
        assertThat(version.major()).isGreaterThanOrEqualTo(5);
    }

    @Test
    public void throw_ISE_if_fail_to_load_version() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Can not load /sonar-api-version.txt from classpath");
        System2 system = Mockito.spy(System2.class);
        Mockito.when(system.getResource(ArgumentMatchers.anyString())).thenReturn(new File("target/unknown").toURI().toURL());
        ApiVersion.load(system);
    }
}


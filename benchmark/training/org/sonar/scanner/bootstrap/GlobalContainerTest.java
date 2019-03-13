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
package org.sonar.scanner.bootstrap;


import com.google.common.collect.Lists;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.utils.TempFolder;
import org.sonar.batch.bootstrapper.EnvironmentInformation;
import org.sonar.core.util.UuidFactory;


public class GlobalContainerTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void should_add_components() {
        GlobalContainer container = createContainer(Collections.singletonList(new EnvironmentInformation("maven", "3.1.0")));
        assertThat(container.getComponentByType(UuidFactory.class)).isNotNull();
        assertThat(container.getComponentByType(TempFolder.class)).isNotNull();
    }

    @Test
    public void should_add_bootstrap_extensions() {
        GlobalContainer container = createContainer(Lists.newArrayList(GlobalContainerTest.Foo.class, new GlobalContainerTest.Bar()));
        assertThat(container.getComponentByType(GlobalContainerTest.Foo.class)).isNotNull();
        assertThat(container.getComponentByType(GlobalContainerTest.Bar.class)).isNotNull();
    }

    @Test
    public void shouldFormatTime() {
        assertThat(GlobalContainer.formatTime(((((((1 * 60) * 60) * 1000) + ((2 * 60) * 1000)) + (3 * 1000)) + 400))).isEqualTo("1:02:03.400 s");
        assertThat(GlobalContainer.formatTime(((((2 * 60) * 1000) + (3 * 1000)) + 400))).isEqualTo("2:03.400 s");
        assertThat(GlobalContainer.formatTime(((3 * 1000) + 400))).isEqualTo("3.400 s");
        assertThat(GlobalContainer.formatTime(400)).isEqualTo("0.400 s");
    }

    @ScannerSide
    public static class Foo {}

    @ScannerSide
    public static class Bar {}
}


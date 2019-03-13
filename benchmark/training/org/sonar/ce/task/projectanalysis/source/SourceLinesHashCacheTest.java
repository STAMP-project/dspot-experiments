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
package org.sonar.ce.task.projectanalysis.source;


import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.internal.JUnitTempFolder;
import org.sonar.ce.task.projectanalysis.component.Component;


public class SourceLinesHashCacheTest {
    private static final String FILE_UUID = "FILE_UUID";

    private static final String FILE_KEY = "FILE_KEY";

    @Rule
    public JUnitTempFolder tempFolder = new JUnitTempFolder();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private SourceLinesHashCache underTest;

    @Test
    public void should_computeIfAbsent() {
        Component component = SourceLinesHashCacheTest.createComponent(1);
        Function<Component, List<String>> f = Mockito.mock(Function.class);
        List<String> list = Collections.singletonList("hash1");
        Mockito.when(f.apply(component)).thenReturn(list);
        assertThat(underTest.contains(component)).isFalse();
        List<String> returned = underTest.computeIfAbsent(component, f);
        assertThat(returned).isEqualTo(list);
        assertThat(underTest.contains(component)).isTrue();
        returned = underTest.computeIfAbsent(component, f);
        assertThat(returned).isEqualTo(list);
        Mockito.verify(f).apply(component);
    }

    @Test
    public void get_throws_ISE_if_not_cached() {
        Component component = SourceLinesHashCacheTest.createComponent(1);
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Source line hashes for component ReportComponent{ref=1, key='FILE_KEY', type=FILE} not cached");
        underTest.get(component);
    }

    @Test
    public void get_returns_value_if_cached() {
        List<String> list = Collections.singletonList("hash1");
        Component component = SourceLinesHashCacheTest.createComponent(1);
        underTest.computeIfAbsent(component, ( c) -> list);
        assertThat(underTest.get(component)).isEqualTo(list);
    }
}


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
package org.sonar.core.extension;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class CoreExtensionRepositoryImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CoreExtensionRepositoryImpl underTest = new CoreExtensionRepositoryImpl();

    @Test
    public void loadedCoreExtensions_fails_with_ISE_if_called_before_setLoadedCoreExtensions() {
        expectRepositoryNotInitializedISE();
        underTest.loadedCoreExtensions();
    }

    @Test
    public void setLoadedCoreExtensions_fails_with_NPE_if_argument_is_null() {
        expectedException.expect(NullPointerException.class);
        underTest.setLoadedCoreExtensions(null);
    }

    @Test
    public void installed_fails_with_ISE_if_called_before_setLoadedCoreExtensions() {
        CoreExtension coreExtension = CoreExtensionRepositoryImplTest.newCoreExtension();
        expectRepositoryNotInitializedISE();
        underTest.installed(coreExtension);
    }

    @Test
    public void isInstalled_fails_with_ISE_if_called_before_setLoadedCoreExtensions() {
        expectRepositoryNotInitializedISE();
        underTest.isInstalled("foo");
    }

    @Test
    public void isInstalled_returns_false_for_loaded_but_not_installed_CoreExtension() {
        CoreExtension coreExtension = CoreExtensionRepositoryImplTest.newCoreExtension();
        underTest.setLoadedCoreExtensions(Collections.singleton(coreExtension));
        assertThat(underTest.isInstalled(coreExtension.getName())).isFalse();
    }

    @Test
    public void isInstalled_returns_true_for_loaded_and_installed_CoreExtension() {
        CoreExtension coreExtension = CoreExtensionRepositoryImplTest.newCoreExtension();
        underTest.setLoadedCoreExtensions(Collections.singleton(coreExtension));
        underTest.installed(coreExtension);
        assertThat(underTest.isInstalled(coreExtension.getName())).isTrue();
    }

    private static int nameCounter = 0;
}


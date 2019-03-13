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
package org.sonar.ce.task.projectanalysis.organization;


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.server.organization.DefaultOrganizationCache;


public class DefaultOrganizationLoaderTest {
    private DefaultOrganizationCache defaultOrganizationCache = Mockito.mock(DefaultOrganizationCache.class);

    private DefaultOrganizationLoader underTest = new DefaultOrganizationLoader(defaultOrganizationCache);

    @Test
    public void start_calls_cache_load_method() {
        underTest.start();
        Mockito.verify(defaultOrganizationCache).load();
        Mockito.verifyNoMoreInteractions(defaultOrganizationCache);
    }

    @Test
    public void stop_calls_cache_unload_method() {
        underTest.stop();
        Mockito.verify(defaultOrganizationCache).unload();
        Mockito.verifyNoMoreInteractions(defaultOrganizationCache);
    }
}


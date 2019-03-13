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


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.scanner.bootstrap.ProcessedScannerProperties;
import org.sonar.scanner.scan.branch.BranchConfiguration;


public class ProjectRepositoriesProviderTest {
    private ProjectRepositoriesProvider provider;

    private ProjectRepositories project;

    @Mock
    private ProjectRepositoriesLoader loader;

    @Mock
    private ProcessedScannerProperties props;

    @Mock
    private BranchConfiguration branchConfiguration;

    @Test
    public void testValidation() {
        Mockito.when(loader.load(ArgumentMatchers.eq("key"), ArgumentMatchers.any())).thenReturn(project);
        provider.provide(loader, props, branchConfiguration);
    }

    @Test
    public void testAssociated() {
        Mockito.when(loader.load(ArgumentMatchers.eq("key"), ArgumentMatchers.any())).thenReturn(project);
        ProjectRepositories repo = provider.provide(loader, props, branchConfiguration);
        assertThat(repo.exists()).isEqualTo(true);
        Mockito.verify(props).getKeyWithBranch();
        Mockito.verify(loader).load(ArgumentMatchers.eq("key"), ArgumentMatchers.eq(null));
        Mockito.verifyNoMoreInteractions(loader, props);
    }
}


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
package org.sonar.scanner.scan.branch;


import BranchType.LONG;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.bootstrap.ProjectReactor;
import org.sonar.scanner.bootstrap.GlobalConfiguration;
import org.sonar.scanner.bootstrap.GlobalServerSettings;
import org.sonar.scanner.scan.ProjectServerSettings;


public class BranchConfigurationProviderTest {
    private BranchConfigurationProvider provider = new BranchConfigurationProvider();

    private GlobalConfiguration globalConfiguration = Mockito.mock(GlobalConfiguration.class);

    private BranchConfigurationLoader loader = Mockito.mock(BranchConfigurationLoader.class);

    private BranchConfiguration config = Mockito.mock(BranchConfiguration.class);

    private ProjectBranches branches = Mockito.mock(ProjectBranches.class);

    private ProjectPullRequests pullRequests = Mockito.mock(ProjectPullRequests.class);

    private ProjectReactor reactor = Mockito.mock(ProjectReactor.class);

    private Map<String, String> globalPropertiesMap = new HashMap<>();

    private ProjectDefinition root = Mockito.mock(ProjectDefinition.class);

    private GlobalServerSettings globalServerSettings = Mockito.mock(GlobalServerSettings.class);

    private ProjectServerSettings projectServerSettings = Mockito.mock(ProjectServerSettings.class);

    @Captor
    private ArgumentCaptor<Supplier<Map<String, String>>> settingsCaptor;

    @Test
    public void should_cache_config() {
        BranchConfiguration configuration = provider.provide(null, globalConfiguration, reactor, globalServerSettings, projectServerSettings, branches, pullRequests);
        assertThat(provider.provide(null, globalConfiguration, reactor, globalServerSettings, projectServerSettings, branches, pullRequests)).isSameAs(configuration);
    }

    @Test
    public void should_use_loader() {
        Mockito.when(loader.load(ArgumentMatchers.eq(globalPropertiesMap), ArgumentMatchers.any(Supplier.class), ArgumentMatchers.eq(branches), ArgumentMatchers.eq(pullRequests))).thenReturn(config);
        BranchConfiguration result = provider.provide(loader, globalConfiguration, reactor, globalServerSettings, projectServerSettings, branches, pullRequests);
        assertThat(result).isSameAs(config);
    }

    @Test
    public void settings_should_include_command_line_args_with_highest_priority() {
        Mockito.when(globalConfiguration.getProperties()).thenReturn(Collections.singletonMap("key", "global"));
        Mockito.when(projectServerSettings.properties()).thenReturn(Collections.singletonMap("key", "settings"));
        Mockito.when(root.properties()).thenReturn(Collections.singletonMap("key", "root"));
        provider.provide(loader, globalConfiguration, reactor, globalServerSettings, projectServerSettings, branches, pullRequests);
        Mockito.verify(loader).load(ArgumentMatchers.anyMap(), settingsCaptor.capture(), ArgumentMatchers.any(ProjectBranches.class), ArgumentMatchers.any(ProjectPullRequests.class));
        Map<String, String> map = settingsCaptor.getValue().get();
        assertThat(map.get("key")).isEqualTo("root");
    }

    @Test
    public void should_return_default_if_no_loader() {
        BranchConfiguration result = provider.provide(null, globalConfiguration, reactor, globalServerSettings, projectServerSettings, branches, pullRequests);
        assertThat(result.targetScmBranch()).isNull();
        assertThat(result.branchType()).isEqualTo(LONG);
    }
}


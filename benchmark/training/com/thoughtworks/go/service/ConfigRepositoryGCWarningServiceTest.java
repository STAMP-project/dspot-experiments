/**
 * Copyright 2015 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.service;


import HealthStateLevel.WARNING;
import SystemEnvironment.GO_CONFIG_REPO_GC_LOOSE_OBJECT_WARNING_THRESHOLD;
import com.thoughtworks.go.CurrentGoCDVersion;
import com.thoughtworks.go.util.SystemEnvironment;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ConfigRepositoryGCWarningServiceTest {
    private ConfigRepository configRepository;

    private ServerHealthService serverHealthService;

    private SystemEnvironment systemEnvironment;

    private ConfigRepositoryGCWarningService service;

    @Test
    public void shouldAddWarningWhenConfigRepoLooseObjectCountGoesBeyondTheConfiguredThreshold() throws Exception {
        Mockito.when(systemEnvironment.get(GO_CONFIG_REPO_GC_LOOSE_OBJECT_WARNING_THRESHOLD)).thenReturn(10L);
        Mockito.when(configRepository.getLooseObjectCount()).thenReturn(20L);
        service.checkRepoAndAddWarningIfRequired();
        List<ServerHealthState> healthStates = serverHealthService.filterByScope(HealthStateScope.forConfigRepo("GC"));
        String message = "Action required: Run 'git gc' on config.git repo";
        String description = (("Number of loose objects in your Configuration repository(config.git) has grown beyond " + ((("the configured threshold. As the size of config repo increases, the config save operations tend to slow down " + "drastically. It is recommended that you run 'git gc' from ") + "'&lt;go server installation directory&gt;/db/config.git/' to address this problem. Go can do this ") + "automatically on a periodic basis if you enable automatic GC. <a target='_blank' href='")) + (CurrentGoCDVersion.docsUrl("/advanced_usage/config_repo.html"))) + "'>read more...</a>";
        Assert.assertThat(healthStates.get(0).getDescription(), Matchers.is(description));
        Assert.assertThat(healthStates.get(0).getLogLevel(), Matchers.is(WARNING));
        Assert.assertThat(healthStates.get(0).getMessage(), Matchers.is(message));
    }

    @Test
    public void shouldNotAddWarningWhenConfigRepoLooseObjectCountIsBelowTheConfiguredThreshold() throws Exception {
        Mockito.when(systemEnvironment.get(GO_CONFIG_REPO_GC_LOOSE_OBJECT_WARNING_THRESHOLD)).thenReturn(10L);
        Mockito.when(configRepository.getLooseObjectCount()).thenReturn(1L);
        service.checkRepoAndAddWarningIfRequired();
        List<ServerHealthState> healthStates = serverHealthService.filterByScope(HealthStateScope.forConfigRepo("GC"));
        Assert.assertThat(healthStates.isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldRemoteExistingWarningAboutGCIfLooseObjectCountGoesBelowTheSetThreshold() throws Exception {
        serverHealthService.update(ServerHealthState.warning("message", "description", HealthStateType.general(HealthStateScope.forConfigRepo("GC"))));
        Assert.assertThat(serverHealthService.filterByScope(HealthStateScope.forConfigRepo("GC")).isEmpty(), Matchers.is(false));
        Mockito.when(systemEnvironment.get(GO_CONFIG_REPO_GC_LOOSE_OBJECT_WARNING_THRESHOLD)).thenReturn(10L);
        Mockito.when(configRepository.getLooseObjectCount()).thenReturn(1L);
        service.checkRepoAndAddWarningIfRequired();
        Assert.assertThat(serverHealthService.filterByScope(HealthStateScope.forConfigRepo("GC")).isEmpty(), Matchers.is(true));
    }
}


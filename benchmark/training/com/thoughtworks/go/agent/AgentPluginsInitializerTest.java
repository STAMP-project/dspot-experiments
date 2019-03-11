/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.agent;


import DownloadableFile.AGENT_PLUGINS;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.monitor.DefaultPluginJarLocationMonitor;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.util.ZipUtil;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AgentPluginsInitializerTest {
    @Mock
    private ZipUtil zipUtil;

    @Mock
    private PluginManager pluginManager;

    @Mock
    private DefaultPluginJarLocationMonitor pluginJarLocationMonitor;

    @Mock
    private SystemEnvironment systemEnvironment;

    private AgentPluginsInitializer agentPluginsInitializer;

    @Test
    public void shouldExtractPluginZip() throws Exception {
        agentPluginsInitializer.onApplicationEvent(null);
        Mockito.verify(zipUtil).unzip(AGENT_PLUGINS.getLocalFile(), new File(SystemEnvironment.PLUGINS_PATH));
    }

    @Test
    public void shouldInitializePluginJarLocationMonitorAndStartPluginInfrastructureAfterPluginZipExtracted() throws Exception {
        InOrder inOrder = Mockito.inOrder(zipUtil, pluginManager, pluginJarLocationMonitor);
        agentPluginsInitializer.onApplicationEvent(null);
        inOrder.verify(zipUtil).unzip(AGENT_PLUGINS.getLocalFile(), new File(SystemEnvironment.PLUGINS_PATH));
        inOrder.verify(pluginJarLocationMonitor).initialize();
        inOrder.verify(pluginManager).startInfrastructure(false);
    }

    @Test
    public void shouldHandleIOExceptionQuietly() throws Exception {
        Mockito.doThrow(new IOException()).when(zipUtil).unzip(AGENT_PLUGINS.getLocalFile(), new File(SystemEnvironment.PLUGINS_PATH));
        try {
            agentPluginsInitializer.onApplicationEvent(null);
        } catch (Exception e) {
            Assert.fail("should have handled IOException");
        }
    }

    @Test
    public void shouldAllExceptionsExceptionQuietly() throws Exception {
        Mockito.doThrow(new IOException()).when(zipUtil).unzip(AGENT_PLUGINS.getLocalFile(), new File(SystemEnvironment.PLUGINS_PATH));
        try {
            Mockito.doThrow(new RuntimeException("message")).when(pluginJarLocationMonitor).initialize();
            agentPluginsInitializer.onApplicationEvent(null);
        } catch (Exception e) {
            Assert.fail("should have handled IOException");
        }
    }
}


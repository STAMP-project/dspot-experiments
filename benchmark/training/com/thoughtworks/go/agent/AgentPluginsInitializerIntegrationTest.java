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


import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.monitor.DefaultPluginJarLocationMonitor;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.util.ZipBuilder;
import com.thoughtworks.go.util.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/* Some parts are mocked, as in AgentPluginsInitializerTest, but the file system (through ZipUtil) is not. */
@RunWith(MockitoJUnitRunner.class)
public class AgentPluginsInitializerIntegrationTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private PluginManager pluginManager;

    @Mock
    private DefaultPluginJarLocationMonitor pluginJarLocationMonitor;

    @Mock
    private SystemEnvironment systemEnvironment;

    private File directoryForUnzippedPlugins;

    private AgentPluginsInitializer agentPluginsInitializer;

    @Test
    public void shouldRemoveExistingBundledPluginsBeforeInitializingNewPlugins() throws Exception {
        File existingBundledPlugin = new File(directoryForUnzippedPlugins, "bundled/existing-plugin-1.jar");
        setupAgentsPluginFile().withBundledPlugin("new-plugin-1.jar", "SOME-PLUGIN-CONTENT").done();
        FileUtils.writeStringToFile(existingBundledPlugin, "OLD-CONTENT", StandardCharsets.UTF_8);
        agentPluginsInitializer.onApplicationEvent(null);
        Assert.assertThat(existingBundledPlugin.exists(), Matchers.is(false));
        Assert.assertThat(new File(directoryForUnzippedPlugins, "bundled/new-plugin-1.jar").exists(), Matchers.is(true));
    }

    @Test
    public void shouldReplaceExistingBundledPluginsWithNewPluginsOfSameName() throws Exception {
        File bundledPlugin = new File(directoryForUnzippedPlugins, "bundled/plugin-1.jar");
        setupAgentsPluginFile().withBundledPlugin("plugin-1.jar", "SOME-NEW-CONTENT").done();
        FileUtils.writeStringToFile(bundledPlugin, "OLD-CONTENT", StandardCharsets.UTF_8);
        agentPluginsInitializer.onApplicationEvent(null);
        Assert.assertThat(bundledPlugin.exists(), Matchers.is(true));
        Assert.assertThat(FileUtils.readFileToString(bundledPlugin, StandardCharsets.UTF_8), Matchers.is("SOME-NEW-CONTENT"));
    }

    @Test
    public void shouldRemoveExistingExternalPluginsBeforeInitializingNewPlugins() throws Exception {
        File existingExternalPlugin = new File(directoryForUnzippedPlugins, "external/existing-plugin-1.jar");
        setupAgentsPluginFile().withExternalPlugin("new-plugin-1.jar", "SOME-PLUGIN-CONTENT").done();
        FileUtils.writeStringToFile(existingExternalPlugin, "OLD-CONTENT", StandardCharsets.UTF_8);
        agentPluginsInitializer.onApplicationEvent(null);
        Assert.assertThat(existingExternalPlugin.exists(), Matchers.is(false));
        Assert.assertThat(new File(directoryForUnzippedPlugins, "external/new-plugin-1.jar").exists(), Matchers.is(true));
    }

    @Test
    public void shouldReplaceExistingExternalPluginsWithNewPluginsOfSameName() throws Exception {
        File externalPlugin = new File(directoryForUnzippedPlugins, "external/plugin-1.jar");
        setupAgentsPluginFile().withExternalPlugin("plugin-1.jar", "SOME-NEW-CONTENT").done();
        FileUtils.writeStringToFile(externalPlugin, "OLD-CONTENT", StandardCharsets.UTF_8);
        agentPluginsInitializer.onApplicationEvent(null);
        Assert.assertThat(externalPlugin.exists(), Matchers.is(true));
        Assert.assertThat(FileUtils.readFileToString(externalPlugin, StandardCharsets.UTF_8), Matchers.is("SOME-NEW-CONTENT"));
    }

    @Test
    public void shouldRemoveAnExistingPluginWhenItHasBeenRemovedFromTheServerSide() throws Exception {
        File existingExternalPlugin = new File(directoryForUnzippedPlugins, "external/plugin-1.jar");
        setupAgentsPluginFile().done();
        FileUtils.writeStringToFile(existingExternalPlugin, "OLD-CONTENT", StandardCharsets.UTF_8);
        agentPluginsInitializer.onApplicationEvent(null);
        Assert.assertThat(existingExternalPlugin.exists(), Matchers.is(false));
    }

    private class SetupOfAgentPluginsFile {
        private final File bundledPluginsDir;

        private final File externalPluginsDir;

        private final ZipUtil zipUtil;

        private final File dummyFileSoZipFileIsNotEmpty;

        private File pluginsZipFile;

        public SetupOfAgentPluginsFile(File pluginsZipFile) throws IOException {
            this.pluginsZipFile = pluginsZipFile;
            this.bundledPluginsDir = temporaryFolder.newFolder("bundled");
            this.externalPluginsDir = temporaryFolder.newFolder("external");
            this.dummyFileSoZipFileIsNotEmpty = temporaryFolder.newFile("dummy.txt");
            this.zipUtil = new ZipUtil();
        }

        public AgentPluginsInitializerIntegrationTest.SetupOfAgentPluginsFile withBundledPlugin(String pluginFileName, String pluginFileContent) throws IOException {
            FileUtils.writeStringToFile(new File(bundledPluginsDir, pluginFileName), pluginFileContent, StandardCharsets.UTF_8);
            return this;
        }

        public AgentPluginsInitializerIntegrationTest.SetupOfAgentPluginsFile withExternalPlugin(String pluginFileName, String pluginFileContent) throws IOException {
            FileUtils.writeStringToFile(new File(externalPluginsDir, pluginFileName), pluginFileContent, StandardCharsets.UTF_8);
            return this;
        }

        public File done() throws IOException {
            ZipBuilder zipBuilder = zipUtil.zipContentsOfMultipleFolders(pluginsZipFile, true);
            zipBuilder.add("bundled", bundledPluginsDir).add("external", externalPluginsDir).add("dummy.txt", dummyFileSoZipFileIsNotEmpty).done();
            return pluginsZipFile;
        }
    }
}


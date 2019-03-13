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
package com.thoughtworks.go.plugin.infra.commons;


import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class PluginsZipTest {
    private SystemEnvironment systemEnvironment;

    private PluginsZip pluginsZip;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private String expectedZipPath;

    private File externalPluginsDir;

    private PluginManager pluginManager;

    private GoPluginDescriptor bundledTaskPlugin;

    private GoPluginDescriptor bundledAuthPlugin;

    private GoPluginDescriptor bundledSCMPlugin;

    private GoPluginDescriptor externalTaskPlugin;

    private GoPluginDescriptor externalElasticAgentPlugin;

    private GoPluginDescriptor externalSCMPlugin;

    private GoPluginDescriptor bundledPackageMaterialPlugin;

    private GoPluginDescriptor externalPackageMaterialPlugin;

    @Test
    public void shouldZipTaskPluginsIntoOneZipEveryTime() throws Exception {
        pluginsZip.create();
        Assert.assertThat(((expectedZipPath) + " should exist"), new File(expectedZipPath).exists(), Matchers.is(true));
        Assert.assertThat(new ZipFile(expectedZipPath).getEntry("bundled/bundled-task-1.jar"), Matchers.is(notNullValue()));
        Assert.assertThat(new ZipFile(expectedZipPath).getEntry("bundled/bundled-scm-3.jar"), Matchers.is(notNullValue()));
        Assert.assertThat(new ZipFile(expectedZipPath).getEntry("bundled/bundled-package-material-4.jar"), Matchers.is(notNullValue()));
        Assert.assertThat(new ZipFile(expectedZipPath).getEntry("external/external-task-1.jar"), Matchers.is(notNullValue()));
        Assert.assertThat(new ZipFile(expectedZipPath).getEntry("external/external-scm-3.jar"), Matchers.is(notNullValue()));
        Assert.assertThat(new ZipFile(expectedZipPath).getEntry("external/external-package-material-4.jar"), Matchers.is(notNullValue()));
        Assert.assertThat(new ZipFile(expectedZipPath).getEntry("bundled/bundled-auth-2.jar"), Matchers.is(nullValue()));
        Assert.assertThat(new ZipFile(expectedZipPath).getEntry("external/external-elastic-agent-2.jar"), Matchers.is(nullValue()));
    }

    @Test
    public void shouldGetChecksumIfFileWasCreated() {
        pluginsZip.create();
        String md5 = pluginsZip.md5();
        Assert.assertThat(md5, Matchers.is(notNullValue()));
    }

    @Test
    public void shouldUpdateChecksumIfFileIsReCreated() throws Exception {
        pluginsZip.create();
        String oldMd5 = pluginsZip.md5();
        FileUtils.writeStringToFile(new File(externalPluginsDir, "external-task-1.jar"), UUID.randomUUID().toString(), StandardCharsets.UTF_8);
        pluginsZip.create();
        Assert.assertThat(pluginsZip.md5(), Matchers.is(not(oldMd5)));
    }

    @Test(expected = FileAccessRightsCheckException.class)
    public void shouldFailGracefullyWhenExternalFileCannotBeRead() throws Exception {
        File bundledPluginsDir = temporaryFolder.newFolder("plugins-bundled-ext");
        SystemEnvironment systemEnvironmentFail = Mockito.mock(SystemEnvironment.class);
        Mockito.when(systemEnvironmentFail.get(PLUGIN_GO_PROVIDED_PATH)).thenReturn(bundledPluginsDir.getAbsolutePath());
        Mockito.when(systemEnvironmentFail.get(PLUGIN_EXTERNAL_PROVIDED_PATH)).thenReturn("");
        Mockito.when(systemEnvironmentFail.get(ALL_PLUGINS_ZIP_PATH)).thenReturn("");
        FileUtils.writeStringToFile(new File(bundledPluginsDir, "bundled-task-1.jar"), "Bundled1", StandardCharsets.UTF_8);
        PluginsZip pluginsZipFail = new PluginsZip(systemEnvironmentFail, pluginManager);
        pluginsZipFail.create();
    }

    @Test(expected = FileAccessRightsCheckException.class)
    public void shouldFailGracefullyWhenBundledFileCannotBeRead() throws Exception {
        SystemEnvironment systemEnvironmentFail = Mockito.mock(SystemEnvironment.class);
        Mockito.when(systemEnvironmentFail.get(PLUGIN_GO_PROVIDED_PATH)).thenReturn("");
        Mockito.when(systemEnvironmentFail.get(PLUGIN_EXTERNAL_PROVIDED_PATH)).thenReturn(externalPluginsDir.getAbsolutePath());
        Mockito.when(systemEnvironmentFail.get(ALL_PLUGINS_ZIP_PATH)).thenReturn("");
        FileUtils.writeStringToFile(new File(externalPluginsDir, "external-task-1.jar"), "External1", StandardCharsets.UTF_8);
        PluginsZip pluginsZipFail = new PluginsZip(systemEnvironmentFail, pluginManager);
        pluginsZipFail.create();
    }

    @Test
    public void fileAccessErrorShouldContainPathToTheFolderInWhichTheErrorOccurred() throws Exception {
        SystemEnvironment systemEnvironmentFail = Mockito.mock(SystemEnvironment.class);
        Mockito.when(systemEnvironmentFail.get(PLUGIN_GO_PROVIDED_PATH)).thenReturn("/dummy");
        Mockito.when(systemEnvironmentFail.get(PLUGIN_EXTERNAL_PROVIDED_PATH)).thenReturn(externalPluginsDir.getAbsolutePath());
        Mockito.when(systemEnvironmentFail.get(ALL_PLUGINS_ZIP_PATH)).thenReturn("");
        FileUtils.writeStringToFile(new File(externalPluginsDir, "external-task-1.jar"), "External1", StandardCharsets.UTF_8);
        expectedException.expect(FileAccessRightsCheckException.class);
        expectedException.expectMessage("dummy");
        PluginsZip pluginsZipFail = new PluginsZip(systemEnvironmentFail, pluginManager);
        pluginsZipFail.create();
    }

    @Test
    public void shouldCreatePluginsWhenTaskPluginsAreAdded() {
        GoPluginDescriptor plugin = new GoPluginDescriptor("curl-task-plugin", null, null, null, null, false);
        Mockito.when(pluginManager.isPluginOfType("task", plugin.id())).thenReturn(true);
        pluginsZip.pluginLoaded(plugin);
        Mockito.verify(pluginsZip, Mockito.times(1)).create();
    }

    @Test
    public void shouldCreatePluginsWhenTaskPluginsAreRemoved() {
        pluginsZip.pluginUnLoaded(externalTaskPlugin);
        Mockito.verify(pluginsZip, Mockito.times(1)).create();
    }

    @Test
    public void shouldDoNothingWhenAPluginThatIsNotATaskOrScmOrPackageMaterialPluginPluginIsAdded() {
        pluginsZip.pluginLoaded(externalElasticAgentPlugin);
        Mockito.verify(pluginsZip, Mockito.never()).create();
    }

    @Test
    public void shouldDoNothingWhenAPluginThatIsNotATaskOrScmOrPackageMaterialPluginPluginIsRemoved() {
        pluginsZip.pluginUnLoaded(externalElasticAgentPlugin);
        Mockito.verify(pluginsZip, Mockito.never()).create();
    }
}


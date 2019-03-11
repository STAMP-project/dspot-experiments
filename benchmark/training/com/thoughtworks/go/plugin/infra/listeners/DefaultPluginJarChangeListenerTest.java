/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.infra.listeners;


import com.thoughtworks.go.CurrentGoCDVersion;
import com.thoughtworks.go.plugin.infra.GoPluginOSGiFramework;
import com.thoughtworks.go.plugin.infra.PluginExtensionsAndVersionValidator;
import com.thoughtworks.go.plugin.infra.monitor.PluginFileDetails;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;


public class DefaultPluginJarChangeListenerTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String PLUGIN_JAR_FILE_NAME = "descriptor-aware-test-plugin.jar";

    private File pluginDir;

    private File bundleDir;

    private DefaultPluginRegistry registry;

    private GoPluginOSGiManifestGenerator osgiManifestGenerator;

    private DefaultPluginJarChangeListener listener;

    private GoPluginOSGiFramework osgiFramework;

    private SystemEnvironment systemEnvironment;

    private GoPluginDescriptorBuilder goPluginDescriptorBuilder;

    private PluginExtensionsAndVersionValidator pluginExtensionsAndVersionValidator;

    @Test
    public void shouldCopyPluginToBundlePathAndInformRegistryAndUpdateTheOSGiManifestWhenAPluginIsAdded() throws Exception {
        String pluginId = "testplugin.descriptorValidator";
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        File expectedBundleDirectory = new File(bundleDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId(pluginId, pluginJarFile.getAbsolutePath(), expectedBundleDirectory, true);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
        Mockito.when(registry.getPluginByIdOrFileName(pluginId, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME)).thenReturn(null);
        Mockito.doNothing().when(registry).loadPlugin(descriptor);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Assert.assertThat(expectedBundleDirectory.exists(), Matchers.is(true));
        Mockito.verify(registry).getPluginByIdOrFileName(pluginId, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        Mockito.verify(registry).loadPlugin(descriptor);
        Mockito.verify(osgiManifestGenerator).updateManifestOf(descriptor);
        Mockito.verify(osgiFramework).loadPlugin(descriptor);
        Mockito.verifyNoMoreInteractions(osgiManifestGenerator);
        Mockito.verifyNoMoreInteractions(registry);
        Assert.assertThat(new File(expectedBundleDirectory, "lib/go-plugin-activator.jar").exists(), Matchers.is(true));
    }

    @Test
    public void shouldOverwriteAFileCalledGoPluginActivatorInLibWithOurOwnGoPluginActivatorEvenIfItExists() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        File expectedBundleDirectory = new File(bundleDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        File activatorFileLocation = new File(expectedBundleDirectory, "lib/go-plugin-activator.jar");
        FileUtils.writeStringToFile(activatorFileLocation, "SOME-DATA", StandardCharsets.UTF_8);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("testplugin.descriptorValidator", pluginJarFile.getAbsolutePath(), expectedBundleDirectory, true);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
        Mockito.doNothing().when(registry).loadPlugin(descriptor);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Assert.assertThat(new File(expectedBundleDirectory, "lib/go-plugin-activator.jar").exists(), Matchers.is(true));
        Assert.assertThat(FileUtils.readFileToString(activatorFileLocation, StandardCharsets.UTF_8), Matchers.is(Matchers.not("SOME-DATA")));
    }

    @Test
    public void shouldCopyPluginToBundlePathAndInformRegistryAndUpdateTheOSGiManifestWhenAPluginIsUpdated() throws Exception {
        DefaultPluginJarChangeListener spy = Mockito.spy(listener);
        String pluginId = "plugin-id";
        String pluginJarFileName = "jarName";
        File pluginJarFile = Mockito.mock(File.class);
        File oldPluginBundleDirectory = temporaryFolder.newFolder("bundleDir", "old-bundle");
        final File explodedDirectory = Mockito.mock(File.class);
        Mockito.doNothing().when(spy).explodePluginJarToBundleDir(pluginJarFile, explodedDirectory);
        Mockito.doNothing().when(spy).installActivatorJarToBundleDir(explodedDirectory);
        GoPluginDescriptor oldDescriptor = Mockito.mock(GoPluginDescriptor.class);
        Bundle oldBundle = Mockito.mock(Bundle.class);
        Mockito.when(oldDescriptor.bundle()).thenReturn(oldBundle);
        Mockito.when(oldDescriptor.fileName()).thenReturn(pluginJarFileName);
        Mockito.when(oldDescriptor.bundleLocation()).thenReturn(oldPluginBundleDirectory);
        GoPluginDescriptor newDescriptor = Mockito.mock(GoPluginDescriptor.class);
        Mockito.when(newDescriptor.id()).thenReturn(pluginId);
        Mockito.when(newDescriptor.isInvalid()).thenReturn(false);
        Mockito.when(newDescriptor.bundleLocation()).thenReturn(explodedDirectory);
        Mockito.when(newDescriptor.fileName()).thenReturn(pluginJarFileName);
        Mockito.when(newDescriptor.isCurrentOSValidForThisPlugin(systemEnvironment.getOperatingSystemFamilyName())).thenReturn(true);
        Mockito.when(newDescriptor.isCurrentGocdVersionValidForThisPlugin()).thenReturn(true);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(newDescriptor);
        Mockito.when(registry.getPluginByIdOrFileName(pluginId, pluginJarFileName)).thenReturn(oldDescriptor);
        Mockito.when(registry.unloadPlugin(newDescriptor)).thenReturn(oldDescriptor);
        Mockito.doNothing().when(registry).loadPlugin(newDescriptor);
        spy.pluginJarUpdated(new PluginFileDetails(pluginJarFile, true));
        Assert.assertThat(oldPluginBundleDirectory.exists(), Matchers.is(false));
        Mockito.verify(registry).getPluginByIdOrFileName(pluginId, pluginJarFileName);
        Mockito.verify(registry).unloadPlugin(newDescriptor);
        Mockito.verify(registry).loadPlugin(newDescriptor);
        Mockito.verify(osgiManifestGenerator).updateManifestOf(newDescriptor);
        Mockito.verify(osgiFramework).unloadPlugin(oldDescriptor);
        Mockito.verify(osgiFramework).loadPlugin(newDescriptor);
        Mockito.verifyNoMoreInteractions(osgiManifestGenerator);
        Mockito.verifyNoMoreInteractions(registry);
    }

    @Test
    public void shouldRemovePluginFromBundlePathAndInformRegistryWhenAPluginIsRemoved() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        File removedBundleDirectory = new File(bundleDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        Bundle bundle = Mockito.mock(Bundle.class);
        GoPluginDescriptor descriptorOfThePluginWhichWillBeRemoved = GoPluginDescriptor.usingId("testplugin.descriptorValidator", pluginJarFile.getAbsolutePath(), removedBundleDirectory, true);
        descriptorOfThePluginWhichWillBeRemoved.setBundle(bundle);
        Mockito.when(registry.getPluginByIdOrFileName(null, descriptorOfThePluginWhichWillBeRemoved.fileName())).thenReturn(descriptorOfThePluginWhichWillBeRemoved);
        Mockito.when(registry.unloadPlugin(descriptorOfThePluginWhichWillBeRemoved)).thenReturn(descriptorOfThePluginWhichWillBeRemoved);
        copyPluginToTheDirectory(bundleDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        listener.pluginJarRemoved(new PluginFileDetails(pluginJarFile, true));
        Mockito.verify(registry).unloadPlugin(descriptorOfThePluginWhichWillBeRemoved);
        Mockito.verify(osgiFramework).unloadPlugin(descriptorOfThePluginWhichWillBeRemoved);
        Assert.assertThat(removedBundleDirectory.exists(), Matchers.is(false));
    }

    @Test
    public void shouldNotTryAndUpdateManifestOfAnAddedInvalidPlugin() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        File expectedBundleDirectory = new File(bundleDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptorForInvalidPlugin = GoPluginDescriptor.usingId("testplugin.descriptorValidator", pluginJarFile.getAbsolutePath(), expectedBundleDirectory, true).markAsInvalid(Arrays.asList("For a test"), null);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptorForInvalidPlugin);
        Mockito.doNothing().when(registry).loadPlugin(descriptorForInvalidPlugin);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Assert.assertThat(expectedBundleDirectory.exists(), Matchers.is(true));
        Mockito.verify(registry).loadPlugin(descriptorForInvalidPlugin);
        Mockito.verifyNoMoreInteractions(osgiManifestGenerator);
    }

    @Test
    public void shouldNotTryAndUpdateManifestOfAnUpdatedInvalidPlugin() throws Exception {
        DefaultPluginJarChangeListener spy = Mockito.spy(listener);
        String pluginId = "plugin-id";
        File pluginFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        File expectedBundleDirectoryForInvalidPlugin = new File(bundleDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        File bundleDirectoryForOldPlugin = new File(bundleDir, "descriptor-aware-test-plugin-old.jar");
        FileUtils.forceMkdir(bundleDirectoryForOldPlugin);
        GoPluginDescriptor descriptorForInvalidPlugin = GoPluginDescriptor.usingId("testplugin.descriptorValidator", pluginFile.getAbsolutePath(), expectedBundleDirectoryForInvalidPlugin, true).markAsInvalid(Arrays.asList("For a test"), null);
        Bundle oldBundle = Mockito.mock(Bundle.class);
        GoPluginDescriptor oldPluginDescriptor = GoPluginDescriptor.usingId("some.old.id", "some/path/to/plugin.jar", bundleDirectoryForOldPlugin, true).setBundle(oldBundle);
        Mockito.when(goPluginDescriptorBuilder.build(pluginFile, true)).thenReturn(descriptorForInvalidPlugin);
        Mockito.when(registry.getPlugin(pluginId)).thenReturn(oldPluginDescriptor);
        Mockito.when(registry.unloadPlugin(descriptorForInvalidPlugin)).thenReturn(oldPluginDescriptor);
        Mockito.doNothing().when(registry).loadPlugin(descriptorForInvalidPlugin);
        spy.pluginJarUpdated(new PluginFileDetails(pluginFile, true));
        Assert.assertThat(expectedBundleDirectoryForInvalidPlugin.exists(), Matchers.is(true));
        Assert.assertThat(bundleDirectoryForOldPlugin.exists(), Matchers.is(false));
        Mockito.verify(registry).unloadPlugin(descriptorForInvalidPlugin);
        Mockito.verify(osgiFramework).unloadPlugin(oldPluginDescriptor);
        Mockito.verify(registry).loadPlugin(descriptorForInvalidPlugin);
        Mockito.verifyNoMoreInteractions(osgiManifestGenerator);
        Mockito.verifyNoMoreInteractions(osgiFramework);
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailToLoadAPluginWhenActivatorJarIsNotAvailable() throws Exception {
        systemEnvironment = Mockito.mock(SystemEnvironment.class);
        Mockito.when(systemEnvironment.get(PLUGIN_ACTIVATOR_JAR_PATH)).thenReturn("some-path-which-does-not-exist.jar");
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        File bundleDirectory = new File(bundleDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("some.old.id", pluginJarFile.getAbsolutePath(), bundleDirectory, true);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
        listener = new DefaultPluginJarChangeListener(registry, osgiManifestGenerator, osgiFramework, goPluginDescriptorBuilder, systemEnvironment);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
    }

    @Test
    public void shouldNotReplaceBundledPluginWhenExternalPluginIsAdded() {
        String pluginId = "external";
        String pluginJarFileName = "plugin-file-name";
        File pluginJarFile = Mockito.mock(File.class);
        Mockito.when(pluginJarFile.getName()).thenReturn(pluginJarFileName);
        GoPluginDescriptor externalPluginDescriptor = new GoPluginDescriptor(pluginId, "1.0", null, pluginJarFile.getAbsolutePath(), new File(pluginJarFileName), false);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, false)).thenReturn(externalPluginDescriptor);
        GoPluginDescriptor bundledPluginDescriptor = new GoPluginDescriptor("bundled", "1.0", null, null, null, true);
        Mockito.when(registry.getPluginByIdOrFileName(pluginId, pluginJarFileName)).thenReturn(bundledPluginDescriptor);
        DefaultPluginJarChangeListener spy = Mockito.spy(listener);
        try {
            listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, false));
            Assert.fail("should have failed as external plugin cannot replace bundled plugin");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Found bundled plugin with ID: [bundled], external plugin could not be loaded"));
        }
        Mockito.verify(spy, Mockito.never()).explodePluginJarToBundleDir(pluginJarFile, externalPluginDescriptor.bundleLocation());
    }

    @Test
    public void shouldNotUpdatePluginWhenThereIsExistingPluginWithSameId() throws Exception {
        String pluginId = "plugin-id";
        String pluginJarFileName = "plugin-file-name";
        File pluginJarFile = Mockito.mock(File.class);
        Mockito.when(pluginJarFile.getName()).thenReturn(pluginJarFileName);
        GoPluginDescriptor newPluginDescriptor = new GoPluginDescriptor(pluginId, "1.0", null, pluginJarFile.getAbsolutePath(), new File(pluginJarFileName), true);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, false)).thenReturn(newPluginDescriptor);
        GoPluginDescriptor oldPluginDescriptor = new GoPluginDescriptor(pluginId, "1.0", null, "location-old", new File("location-old"), true);
        Mockito.when(registry.getPluginByIdOrFileName(pluginId, pluginJarFileName)).thenReturn(oldPluginDescriptor);
        DefaultPluginJarChangeListener spy = Mockito.spy(listener);
        try {
            spy.pluginJarUpdated(new PluginFileDetails(pluginJarFile, false));
            Assert.fail("should have failed as external plugin cannot replace bundled plugin");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Found another plugin with ID: plugin-id"));
        }
        Mockito.verify(spy, Mockito.never()).explodePluginJarToBundleDir(pluginJarFile, newPluginDescriptor.bundleLocation());
    }

    @Test
    public void shouldNotUpdateBundledPluginWithExternalPlugin() {
        String pluginId = "plugin-id";
        String pluginJarFileName = "plugin-file-name";
        File pluginJarFile = Mockito.mock(File.class);
        Mockito.when(pluginJarFile.getName()).thenReturn(pluginJarFileName);
        GoPluginDescriptor newPluginDescriptor = new GoPluginDescriptor(pluginId, "1.0", null, null, new File(pluginJarFileName), false);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, false)).thenReturn(newPluginDescriptor);
        GoPluginDescriptor oldPluginDescriptor = new GoPluginDescriptor(pluginId, "1.0", null, null, null, true);
        Mockito.when(registry.getPluginByIdOrFileName(pluginId, pluginJarFileName)).thenReturn(oldPluginDescriptor);
        DefaultPluginJarChangeListener spy = Mockito.spy(listener);
        try {
            spy.pluginJarUpdated(new PluginFileDetails(pluginJarFile, false));
            Assert.fail("should have failed as external plugin cannot replace bundled plugin");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Found bundled plugin with ID: [plugin-id], external plugin could not be loaded"));
        }
        Mockito.verify(spy, Mockito.never()).explodePluginJarToBundleDir(pluginJarFile, newPluginDescriptor.bundleLocation());
    }

    @Test
    public void shouldNotRemoveBundledPluginExternalPluginJarRemovedWithSameId() throws Exception {
        String pluginId = "plugin-id";
        String pluginJarFileName = "plugin-file-name";
        File pluginJarFile = Mockito.mock(File.class);
        Mockito.when(pluginJarFile.getName()).thenReturn(pluginJarFileName);
        GoPluginDescriptor oldPluginDescriptor = new GoPluginDescriptor(pluginId, "1.0", null, null, null, true);
        Mockito.when(registry.getPluginByIdOrFileName(null, pluginJarFileName)).thenReturn(oldPluginDescriptor);
        DefaultPluginJarChangeListener spy = Mockito.spy(listener);
        spy.pluginJarRemoved(new PluginFileDetails(pluginJarFile, false));
        Mockito.verify(registry, Mockito.never()).unloadPlugin(oldPluginDescriptor);
        Mockito.verify(osgiFramework, Mockito.never()).unloadPlugin(oldPluginDescriptor);
    }

    @Test
    public void shouldNotLoadAPluginWhenCurrentOSIsNotAmongTheListOfTargetOSesAsDeclaredByThePluginInItsXML() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = new GoPluginDescriptor("some.old.id", "1.0", new GoPluginDescriptor.About(null, null, null, null, null, Arrays.asList("Linux", "Mac OS X")), null, new File(DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME), false);
        Mockito.when(systemEnvironment.getOperatingSystemFamilyName()).thenReturn("Windows");
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
        listener = new DefaultPluginJarChangeListener(registry, osgiManifestGenerator, osgiFramework, goPluginDescriptorBuilder, systemEnvironment);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Mockito.verify(registry, Mockito.times(1)).loadPlugin(descriptor);
        Mockito.verifyZeroInteractions(osgiFramework);
        Assert.assertThat(descriptor.getStatus().getMessages().size(), Matchers.is(1));
        Assert.assertThat(descriptor.getStatus().getMessages().get(0), Matchers.is("Plugin with ID (some.old.id) is not valid: Incompatible with current operating system 'Windows'. Valid operating systems are: [Linux, Mac OS X]."));
    }

    @Test
    public void shouldNotLoadAPluginWhenCurrentOSIsNotAmongTheListOfTargetOSesAsDeclaredByThePluginInItsXMLForUpdatePath() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        String pluginID = "some.id";
        GoPluginDescriptor newPluginDescriptor = new GoPluginDescriptor(pluginID, "1.0", new GoPluginDescriptor.About(null, null, null, null, null, Arrays.asList("Mac OS X")), null, new File(DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME), true);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, false)).thenReturn(newPluginDescriptor);
        GoPluginDescriptor oldPluginDescriptor = new GoPluginDescriptor(pluginID, "1.0", new GoPluginDescriptor.About(null, null, null, null, null, Arrays.asList("Linux", "Mac OS X")), null, new File(DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME), true);
        Mockito.when(registry.getPluginByIdOrFileName(pluginID, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME)).thenReturn(oldPluginDescriptor);
        Mockito.when(systemEnvironment.getOperatingSystemFamilyName()).thenReturn("Linux");
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(newPluginDescriptor);
        Mockito.when(registry.unloadPlugin(newPluginDescriptor)).thenReturn(oldPluginDescriptor);
        listener = new DefaultPluginJarChangeListener(registry, osgiManifestGenerator, osgiFramework, goPluginDescriptorBuilder, systemEnvironment);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Mockito.verify(registry, Mockito.times(1)).loadPlugin(newPluginDescriptor);
        Assert.assertThat(newPluginDescriptor.getStatus().getMessages().size(), Matchers.is(1));
        Assert.assertThat(newPluginDescriptor.getStatus().getMessages().get(0), Matchers.is("Plugin with ID (some.id) is not valid: Incompatible with current operating system 'Linux'. Valid operating systems are: [Mac OS X]."));
    }

    @Test
    public void shouldLoadAPluginWhenCurrentOSIsAmongTheListOfTargetOSesAsDeclaredByThePluginInItsXML() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = new GoPluginDescriptor("some.old.id", "1.0", new GoPluginDescriptor.About(null, null, null, null, null, Arrays.asList("Windows", "Linux")), null, new File(DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME), false);
        Mockito.when(systemEnvironment.getOperatingSystemFamilyName()).thenReturn("Windows");
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
        listener = new DefaultPluginJarChangeListener(registry, osgiManifestGenerator, osgiFramework, goPluginDescriptorBuilder, systemEnvironment);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Mockito.verify(registry, Mockito.times(1)).loadPlugin(descriptor);
        Mockito.verify(osgiFramework, Mockito.times(1)).loadPlugin(descriptor);
    }

    @Test
    public void shouldLoadAPluginWhenAListOfTargetOSesIsNotDeclaredByThePluginInItsXML() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = new GoPluginDescriptor("some.old.id", "1.0", new GoPluginDescriptor.About(null, null, null, null, null, null), null, new File(DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME), false);
        Mockito.when(systemEnvironment.getOperatingSystemFamilyName()).thenReturn("Windows");
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
        listener = new DefaultPluginJarChangeListener(registry, osgiManifestGenerator, osgiFramework, goPluginDescriptorBuilder, systemEnvironment);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Mockito.verify(registry, Mockito.times(1)).loadPlugin(descriptor);
        Mockito.verify(osgiFramework, Mockito.times(1)).loadPlugin(descriptor);
    }

    @Test
    public void shouldNotLoadAPluginWhenTargetedGocdVersionIsGreaterThanCurrentGocdVersion() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = new GoPluginDescriptor("some.old.id", "1.0", new GoPluginDescriptor.About(null, null, "9999.0.0", null, null, Arrays.asList("Linux", "Mac OS X")), null, new File(DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME), false);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
        listener = new DefaultPluginJarChangeListener(registry, osgiManifestGenerator, osgiFramework, goPluginDescriptorBuilder, systemEnvironment);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Mockito.verify(registry, Mockito.times(1)).loadPlugin(descriptor);
        Mockito.verifyZeroInteractions(osgiFramework);
        Assert.assertThat(descriptor.getStatus().getMessages().size(), Matchers.is(1));
        Assert.assertThat(descriptor.getStatus().getMessages().get(0), Matchers.is((("Plugin with ID (some.old.id) is not valid: Incompatible with GoCD version '" + (CurrentGoCDVersion.getInstance().goVersion())) + "'. Compatible version is: 9999.0.0.")));
    }

    @Test
    public void shouldNotLoadAPluginWhenTargetedGocdVersionIsIncorrect() throws Exception {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = new GoPluginDescriptor("some.old.id", "1.0", new GoPluginDescriptor.About(null, null, "9999.0.0.1.2", null, null, Arrays.asList("Linux", "Mac OS X")), null, new File(DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME), false);
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
        listener = new DefaultPluginJarChangeListener(registry, osgiManifestGenerator, osgiFramework, goPluginDescriptorBuilder, systemEnvironment);
        listener.pluginJarAdded(new PluginFileDetails(pluginJarFile, true));
        Mockito.verify(registry, Mockito.times(1)).loadPlugin(descriptor);
        Mockito.verifyZeroInteractions(osgiFramework);
        Assert.assertThat(descriptor.getStatus().getMessages().size(), Matchers.is(1));
        Assert.assertThat(descriptor.getStatus().getMessages().get(0), Matchers.is("Plugin with ID (some.old.id) is not valid: Incorrect target gocd version(9999.0.0.1.2) specified."));
    }

    @Test
    public void shouldNotLoadAPluginWhenProvidedExtensionVersionByThePluginIsNotSupportedByCurrentGoCDVersion() throws IOException, URISyntaxException {
        File pluginJarFile = new File(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        copyPluginToTheDirectory(pluginDir, DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME);
        GoPluginDescriptor descriptor = new GoPluginDescriptor("some.old.id", "1.0", new GoPluginDescriptor.About(null, null, null, null, null, Arrays.asList("Windows", "Linux")), null, new File(DefaultPluginJarChangeListenerTest.PLUGIN_JAR_FILE_NAME), false);
        Mockito.when(systemEnvironment.getOperatingSystemFamilyName()).thenReturn("Windows");
        Mockito.when(goPluginDescriptorBuilder.build(pluginJarFile, true)).thenReturn(descriptor);
    }
}


/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.infra.monitor;


import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;


@RunWith(JunitExtRunner.class)
@RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
public class DefaultExternalPluginJarLocationMonitorTest extends AbstractDefaultPluginJarLocationMonitorTest {
    private File pluginBundledDir;

    private File pluginExternalDir;

    private DefaultPluginJarLocationMonitor monitor;

    private PluginJarChangeListener changeListener;

    private SystemEnvironment systemEnvironment;

    @Test
    public void shouldCreateExternalPluginDirectoryIfItDoesNotExist() throws Exception {
        pluginExternalDir.delete();
        initialize();
        Assert.assertThat(pluginExternalDir.exists(), Matchers.is(true));
    }

    @Test
    public void shouldThrowUpWhenExternalPluginDirectoryCreationFails() throws Exception {
        Mockito.when(systemEnvironment.get(PLUGIN_EXTERNAL_PROVIDED_PATH)).thenReturn("/xyz");
        try {
            initialize();
            Assert.fail("should have failed for missing external plugin folder");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Failed to create external plugins folder in location /xyz"));
        }
    }

    @Test
    public void shouldDetectNewlyAddedPluginJar() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-plugin-2.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-plugin-2.jar", false));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldDetectOnlyJarsAsNewPlugins() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-plugin.something-other-than-jar.zip");
        waitUntilNextRun(monitor);
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldDetectRemovedPluginJar() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-plugin-2.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-plugin-2.jar", false));
        FileUtils.deleteQuietly(new File(pluginExternalDir, "descriptor-aware-test-plugin-2.jar"));
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarRemoved(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-plugin-2.jar", false));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotifyListenerOfMultiplePluginFilesAdded() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar", false));
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-2.jar");
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-3.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-2.jar", false));
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-3.jar", false));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotifyListenerOfMultiplePluginFilesRemoved() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar");
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-2.jar");
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-3.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar", false));
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-2.jar", false));
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-3.jar", false));
        FileUtils.deleteQuietly(new File(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar"));
        FileUtils.deleteQuietly(new File(pluginExternalDir, "descriptor-aware-test-external-plugin-2.jar"));
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarRemoved(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar", false));
        Mockito.verify(changeListener).pluginJarRemoved(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-2.jar", false));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotifyRemoveEventBeforeAddEventInCaseOfFileRename() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar");
        waitUntilNextRun(monitor);
        PluginFileDetails orgExternalFile = pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar", false);
        Mockito.verify(changeListener).pluginJarAdded(orgExternalFile);
        PluginFileDetails newExternalFile = pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-1-new.jar", false);
        FileUtils.moveFile(orgExternalFile.file(), newExternalFile.file());
        waitUntilNextRun(monitor);
        InOrder inOrder = Mockito.inOrder(changeListener);
        inOrder.verify(changeListener).pluginJarRemoved(orgExternalFile);
        inOrder.verify(changeListener).pluginJarAdded(newExternalFile);
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotifyListenersOfUpdatesToPluginJars() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin.jar", false));
        updateFileContents(new File(pluginExternalDir, "descriptor-aware-test-external-plugin.jar"));
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarUpdated(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin.jar", false));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldAlwaysHandleBundledPluginsAheadOfExternalPlugins() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginBundledDir, "descriptor-aware-test-bundled-plugin-1.jar");
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar");
        waitUntilNextRun(monitor);
        InOrder jarAddedOrder = Mockito.inOrder(changeListener);
        jarAddedOrder.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginBundledDir, "descriptor-aware-test-bundled-plugin-1.jar", true));
        jarAddedOrder.verify(changeListener).pluginJarAdded(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar", false));
        updateFileContents(new File(pluginBundledDir, "descriptor-aware-test-bundled-plugin-1.jar"));
        updateFileContents(new File(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar"));
        waitUntilNextRun(monitor);
        InOrder jarUpdatedOrder = Mockito.inOrder(changeListener);
        jarUpdatedOrder.verify(changeListener).pluginJarUpdated(pluginFileDetails(pluginBundledDir, "descriptor-aware-test-bundled-plugin-1.jar", true));
        jarUpdatedOrder.verify(changeListener).pluginJarUpdated(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar", false));
        FileUtils.deleteQuietly(new File(pluginBundledDir, "descriptor-aware-test-bundled-plugin-1.jar"));
        FileUtils.deleteQuietly(new File(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar"));
        waitUntilNextRun(monitor);
        InOrder jarRemovedOrder = Mockito.inOrder(changeListener);
        jarRemovedOrder.verify(changeListener).pluginJarRemoved(pluginFileDetails(pluginBundledDir, "descriptor-aware-test-bundled-plugin-1.jar", true));
        jarRemovedOrder.verify(changeListener).pluginJarRemoved(pluginFileDetails(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar", false));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldSpecifyIfPluginIsBundledOrExternalWhenAdded() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(pluginBundledDir, "descriptor-aware-test-bundled-plugin-1.jar");
        copyPluginToThePluginDirectory(pluginExternalDir, "descriptor-aware-test-external-plugin-1.jar");
        ArgumentCaptor<PluginFileDetails> pluginFileDetailsArgumentCaptor = ArgumentCaptor.forClass(PluginFileDetails.class);
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener, Mockito.times(2)).pluginJarAdded(pluginFileDetailsArgumentCaptor.capture());
        Assert.assertThat(pluginFileDetailsArgumentCaptor.getAllValues().get(0).isBundledPlugin(), Matchers.is(true));
        Assert.assertThat(pluginFileDetailsArgumentCaptor.getAllValues().get(1).isBundledPlugin(), Matchers.is(false));
        Mockito.verifyNoMoreInteractions(changeListener);
    }
}


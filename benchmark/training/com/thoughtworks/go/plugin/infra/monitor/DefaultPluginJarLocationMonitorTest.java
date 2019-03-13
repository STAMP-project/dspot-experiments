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
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JunitExtRunner.class)
@RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
public class DefaultPluginJarLocationMonitorTest extends AbstractDefaultPluginJarLocationMonitorTest {
    private DefaultPluginJarLocationMonitor monitor;

    private File bundledPluginDir;

    private File pluginExternalDir;

    @Mock
    private SystemEnvironment systemEnvironment;

    @Mock
    private PluginJarChangeListener changeListener;

    @Test
    public void shouldCreatePluginDirectoryIfItDoesNotExist() throws Exception {
        bundledPluginDir.delete();
        initialize();
        Assert.assertThat(bundledPluginDir.exists(), Matchers.is(true));
    }

    @Test
    public void shouldNotFailIfNoListenerIsPresentWhenAPluginJarIsAdded() throws Exception {
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin.jar");
        monitor.start();
        waitUntilNextRun(monitor);
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldDetectNewlyAddedPluginJar() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin.jar", true));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldDetectOnlyJarsAsNewPlugins() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin.something-other-than-jar.zip");
        waitUntilNextRun(monitor);
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldRunOnlyOnceWhenIntervalIsLessThanZero() throws Exception {
        Mockito.when(systemEnvironment.get(PLUGIN_LOCATION_MONITOR_INTERVAL_IN_SECONDS)).thenReturn((-1));
        monitor.addPluginJarChangeListener(changeListener);
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin.jar");
        monitor.start();
        waitAMoment();
        FileUtils.deleteQuietly(new File(bundledPluginDir, "descriptor-aware-test-plugin.jar"));
        waitAMoment();
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin.jar", true));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldDetectRemovedPluginJar() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin.jar", true));
        FileUtils.deleteQuietly(new File(bundledPluginDir, "descriptor-aware-test-plugin.jar"));
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarRemoved(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin.jar", true));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotifyListenerOfMultiplePluginFilesAdded() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-1.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true));
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-2.jar");
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-3.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-2.jar", true));
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-3.jar", true));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotifyListenerOfMultiplePluginFilesAddedEvenIfOneListenerThrowsAnException() throws Exception {
        PluginJarChangeListener exceptionRasingListener = Mockito.mock(PluginJarChangeListener.class);
        Mockito.doThrow(new RuntimeException("Dummy Listener Exception")).when(exceptionRasingListener).pluginJarAdded(ArgumentMatchers.any(PluginFileDetails.class));
        monitor.addPluginJarChangeListener(exceptionRasingListener);
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-1.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true));
        Mockito.verify(exceptionRasingListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true));
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-2.jar");
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-3.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-2.jar", true));
        Mockito.verify(exceptionRasingListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-2.jar", true));
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-3.jar", true));
        Mockito.verify(exceptionRasingListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-3.jar", true));
        Mockito.verifyNoMoreInteractions(changeListener);
        Mockito.verifyNoMoreInteractions(exceptionRasingListener);
    }

    @Test
    public void shouldNotifyListenerOfMultiplePluginFilesRemoved() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-1.jar");
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-2.jar");
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-3.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true));
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-2.jar", true));
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-3.jar", true));
        FileUtils.deleteQuietly(new File(bundledPluginDir, "descriptor-aware-test-plugin-1.jar"));
        FileUtils.deleteQuietly(new File(bundledPluginDir, "descriptor-aware-test-plugin-2.jar"));
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener, Mockito.atMost(1)).pluginJarUpdated(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true));
        Mockito.verify(changeListener, Mockito.atMost(1)).pluginJarUpdated(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-2.jar", true));
        Mockito.verify(changeListener).pluginJarRemoved(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true));
        Mockito.verify(changeListener).pluginJarRemoved(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-2.jar", true));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotifyRemoveEventBeforeAddEventInCaseOfFileRename() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-1.jar");
        waitUntilNextRun(monitor);
        PluginFileDetails orgFile = pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true);
        Mockito.verify(changeListener).pluginJarAdded(orgFile);
        PluginFileDetails newFile = pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1-new.jar", true);
        FileUtils.moveFile(orgFile.file(), newFile.file());
        waitUntilNextRun(monitor);
        InOrder inOrder = Mockito.inOrder(changeListener);
        inOrder.verify(changeListener).pluginJarRemoved(orgFile);
        inOrder.verify(changeListener).pluginJarAdded(newFile);
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotifyListenersOfUpdatesToPluginJars() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin.jar", true));
        updateFileContents(new File(bundledPluginDir, "descriptor-aware-test-plugin.jar"));
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarUpdated(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin.jar", true));
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldCreatePluginZipIfPluginJarIsAdded() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin.jar", true));
    }

    @Test
    public void shouldCreatePluginZipIfPluginJarIsRemoved() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        String pluginJar = "descriptor-aware-test-plugin.jar";
        copyPluginToThePluginDirectory(bundledPluginDir, pluginJar);
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, pluginJar, true));
        FileUtils.deleteQuietly(new File(bundledPluginDir, pluginJar));
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarRemoved(pluginFileDetails(bundledPluginDir, pluginJar, true));
    }

    @Test
    public void shouldCreatePluginZipIfPluginJarIsUpdated() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-1.jar");
        waitUntilNextRun(monitor);
        PluginFileDetails orgFile = pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true);
        Mockito.verify(changeListener).pluginJarAdded(orgFile);
        PluginFileDetails newFile = pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1-new.jar", true);
        FileUtils.moveFile(orgFile.file(), newFile.file());
        waitUntilNextRun(monitor);
    }

    @Test
    public void shouldNotCreatePluginZipIfPluginJarIsNeitherUpdatedNorAddedOrRemoved() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        waitUntilNextRun(monitor);
    }

    @Test
    public void shouldNotSendAnyNotificationsToAListenerWhichHasBeenRemoved() throws Exception {
        monitor.addPluginJarChangeListener(changeListener);
        monitor.start();
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-1.jar");
        waitUntilNextRun(monitor);
        Mockito.verify(changeListener).pluginJarAdded(pluginFileDetails(bundledPluginDir, "descriptor-aware-test-plugin-1.jar", true));
        monitor.removePluginJarChangeListener(changeListener);
        copyPluginToThePluginDirectory(bundledPluginDir, "descriptor-aware-test-plugin-2.jar");
        waitUntilNextRun(monitor);
        Mockito.verifyNoMoreInteractions(changeListener);
    }

    @Test
    public void shouldNotAllowMonitorToBeStartedMultipleTimes() throws Exception {
        try {
            monitor.start();
            monitor.start();
            Assert.fail("Expected an IllegalStateException.");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Cannot start the monitor multiple times.", e.getMessage());
        }
    }
}


/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.infra;


import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.infra.plugininfo.DefaultPluginRegistry;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;


public class FelixGoPluginOSGiFrameworkIntegrationTest {
    @ClassRule
    public static final RestoreSystemProperties RESTORE_SYSTEM_PROPERTIES = new RestoreSystemProperties();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private FelixGoPluginOSGiFramework pluginOSGiFramework;

    private File descriptorBundleDir;

    private File errorGeneratingDescriptorBundleDir;

    private File exceptionThrowingAtLoadDescriptorBundleDir;

    private File validMultipleExtensionPluginBundleDir;

    private File pluginToTestClassloadPluginBundleDir;

    private DefaultPluginRegistry registry;

    private SystemEnvironment systemEnvironment;

    private static final String PLUGIN_ID = "some-plugin";

    @Test
    public void shouldLoadAValidGoPluginOSGiBundle() throws Exception {
        Bundle bundle = pluginOSGiFramework.loadPlugin(new GoPluginDescriptor(FelixGoPluginOSGiFrameworkIntegrationTest.PLUGIN_ID, null, null, null, descriptorBundleDir, true));
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.ACTIVE));
        BundleContext context = bundle.getBundleContext();
        ServiceReference<?>[] allServiceReferences = context.getServiceReferences(GoPlugin.class.getCanonicalName(), null);
        Assert.assertThat(allServiceReferences.length, Matchers.is(1));
        try {
            GoPlugin service = ((GoPlugin) (context.getService(allServiceReferences[0])));
            service.pluginIdentifier();
            Assert.assertThat("@Load should have been called", getIntField(service, "loadCalled"), Matchers.is(1));
        } catch (Exception e) {
            Assert.fail(String.format("pluginIdentifier should have been called. Exception: %s", e.getMessage()));
        }
    }

    @Test
    public void shouldNotifyListenersWhenPluginLoaded() {
        PluginChangeListener pluginChangeListener = Mockito.mock(PluginChangeListener.class);
        pluginOSGiFramework.addPluginChangeListener(pluginChangeListener);
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(FelixGoPluginOSGiFrameworkIntegrationTest.PLUGIN_ID, null, null, null, descriptorBundleDir, true);
        pluginOSGiFramework.loadPlugin(pluginDescriptor);
        Mockito.verify(pluginChangeListener).pluginLoaded(pluginDescriptor);
    }

    @Test
    public void shouldNotifyListenersWhenPluginUnLoaded() {
        PluginChangeListener pluginChangeListener = Mockito.mock(PluginChangeListener.class);
        pluginOSGiFramework.addPluginChangeListener(pluginChangeListener);
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(FelixGoPluginOSGiFrameworkIntegrationTest.PLUGIN_ID, null, null, null, descriptorBundleDir, true);
        Bundle bundle = pluginOSGiFramework.loadPlugin(pluginDescriptor);
        pluginDescriptor.setBundle(bundle);
        pluginOSGiFramework.unloadPlugin(pluginDescriptor);
        Mockito.verify(pluginChangeListener).pluginUnLoaded(pluginDescriptor);
    }

    @Test
    public void shouldLoadAValidGoPluginOSGiBundleAndShouldBeDiscoverableThroughSymbolicNameFilter() throws Exception {
        Bundle bundle = pluginOSGiFramework.loadPlugin(new GoPluginDescriptor(FelixGoPluginOSGiFrameworkIntegrationTest.PLUGIN_ID, null, null, null, descriptorBundleDir, true));
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.ACTIVE));
        String filterBySymbolicName = String.format("(%s=%s)", Constants.BUNDLE_SYMBOLICNAME, "testplugin.descriptorValidator");
        BundleContext context = bundle.getBundleContext();
        ServiceReference<?>[] allServiceReferences = context.getServiceReferences(GoPlugin.class.getCanonicalName(), filterBySymbolicName);
        Assert.assertThat(allServiceReferences.length, Matchers.is(1));
        try {
            GoPlugin service = ((GoPlugin) (context.getService(allServiceReferences[0])));
            service.pluginIdentifier();
        } catch (Exception e) {
            Assert.fail(String.format("pluginIdentifier should have been called. Exception: %s", e.getMessage()));
        }
    }

    @Test
    public void shouldHandleErrorGeneratedByAValidGoPluginOSGiBundleAtUsageTime() {
        Bundle bundle = pluginOSGiFramework.loadPlugin(new GoPluginDescriptor(FelixGoPluginOSGiFrameworkIntegrationTest.PLUGIN_ID, null, null, null, errorGeneratingDescriptorBundleDir, true));
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.ACTIVE));
        ActionWithReturn<GoPlugin, Object> action = new ActionWithReturn<GoPlugin, Object>() {
            @Override
            public Object execute(GoPlugin goPlugin, GoPluginDescriptor goPluginDescriptor) {
                goPlugin.pluginIdentifier();
                return null;
            }
        };
        try {
            pluginOSGiFramework.doOn(GoPlugin.class, "testplugin.descriptorValidator", "CANNOT_FIND_EXTENSION_TYPE", action);
            Assert.fail("Should Throw An Exception");
        } catch (Exception ex) {
            Assert.assertThat(((ex.getCause()) instanceof AbstractMethodError), Matchers.is(true));
        }
    }

    @Test
    public void shouldPassInCorrectDescriptorToAction() {
        final GoPluginDescriptor descriptor = new GoPluginDescriptor("testplugin.descriptorValidator", null, null, null, descriptorBundleDir, true);
        Bundle bundle = pluginOSGiFramework.loadPlugin(descriptor);
        registry.loadPlugin(descriptor);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.ACTIVE));
        ActionWithReturn<GoPlugin, Object> action = new ActionWithReturn<GoPlugin, Object>() {
            @Override
            public Object execute(GoPlugin plugin, GoPluginDescriptor pluginDescriptor) {
                Assert.assertThat(pluginDescriptor, Matchers.is(descriptor));
                plugin.pluginIdentifier();
                return null;
            }
        };
        pluginOSGiFramework.doOn(GoPlugin.class, "testplugin.descriptorValidator", "notification", action);
    }

    @Test
    public void shouldUnloadALoadedPlugin() throws Exception {
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(FelixGoPluginOSGiFrameworkIntegrationTest.PLUGIN_ID, null, null, null, descriptorBundleDir, true);
        Bundle bundle = pluginOSGiFramework.loadPlugin(pluginDescriptor);
        BundleContext context = bundle.getBundleContext();
        ServiceReference<?>[] allServiceReferences = context.getServiceReferences(GoPlugin.class.getCanonicalName(), null);
        Assert.assertThat(allServiceReferences.length, Matchers.is(1));
        GoPlugin service = ((GoPlugin) (context.getService(allServiceReferences[0])));
        Assert.assertThat("@Load should have been called", getIntField(service, "loadCalled"), Matchers.is(1));
        pluginDescriptor.setBundle(bundle);
        pluginOSGiFramework.unloadPlugin(pluginDescriptor);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        Assert.assertThat("@UnLoad should have been called", getIntField(service, "unloadCalled"), Matchers.is(1));
    }

    @Test
    public void shouldMarkAPluginInvalidAnUnloadPluginIfAtLoadOfAnyExtensionPointInItFails() throws Exception {
        String id = "com.tw.go.exception.throwing.at.loadplugin";
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(id, null, null, null, exceptionThrowingAtLoadDescriptorBundleDir, true);
        registry.loadPlugin(pluginDescriptor);
        Assert.assertThat(pluginDescriptor.isInvalid(), Matchers.is(false));
        Bundle bundle = pluginOSGiFramework.loadPlugin(pluginDescriptor);
        Assert.assertThat(pluginDescriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
    }

    @Test
    public void shouldLoadAValidPluginWithMultipleExtensions_ImplementingDifferentExtensions() throws Exception {
        Bundle bundle = pluginOSGiFramework.loadPlugin(new GoPluginDescriptor(FelixGoPluginOSGiFrameworkIntegrationTest.PLUGIN_ID, null, null, null, validMultipleExtensionPluginBundleDir, true));
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.ACTIVE));
        BundleContext context = bundle.getBundleContext();
        String taskExtensionFilter = String.format("(&(%s=%s)(%s=%s))", Constants.BUNDLE_SYMBOLICNAME, "valid-plugin-with-multiple-extensions", Constants.BUNDLE_CATEGORY, "task");
        String analyticsExtensionFilter = String.format("(&(%s=%s)(%s=%s))", Constants.BUNDLE_SYMBOLICNAME, "valid-plugin-with-multiple-extensions", Constants.BUNDLE_CATEGORY, "analytics");
        ServiceReference<?>[] taskExtensionServiceReferences = context.getServiceReferences(GoPlugin.class.getCanonicalName(), taskExtensionFilter);
        Assert.assertThat(taskExtensionServiceReferences.length, Matchers.is(1));
        Assert.assertThat(pluginIdentifier().getExtension(), Matchers.is("task"));
        ServiceReference<?>[] analyticsExtensionServiceReferences = context.getServiceReferences(GoPlugin.class.getCanonicalName(), analyticsExtensionFilter);
        Assert.assertThat(analyticsExtensionServiceReferences.length, Matchers.is(1));
        Assert.assertThat(pluginIdentifier().getExtension(), Matchers.is("analytics"));
    }

    @Test
    public void shouldSetCurrentThreadContextClassLoaderToBundleClassLoaderToAvoidDependenciesFromApplicationClassloaderMessingAroundWithThePluginBehavior() {
        systemEnvironment.setProperty("gocd.plugins.classloader.old", "false");
        final GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin.to.test.classloader", null, null, null, pluginToTestClassloadPluginBundleDir, true);
        Bundle bundle = pluginOSGiFramework.loadPlugin(descriptor);
        registry.loadPlugin(descriptor);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.ACTIVE));
        ActionWithReturn<GoPlugin, Object> action = ( plugin, pluginDescriptor) -> {
            assertThat(pluginDescriptor, is(descriptor));
            assertThat(Thread.currentThread().getContextClassLoader().getClass().getCanonicalName(), is(.class.getCanonicalName()));
            plugin.pluginIdentifier();
            return null;
        };
        pluginOSGiFramework.doOn(GoPlugin.class, "plugin.to.test.classloader", "notification", action);
    }

    @Test
    public void shouldUseOldClassLoaderBehaviourWhenSystemPropertyIsSet() {
        systemEnvironment.setProperty("gocd.plugins.classloader.old", "true");
        final GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin.to.test.classloader", null, null, null, pluginToTestClassloadPluginBundleDir, true);
        Bundle bundle = pluginOSGiFramework.loadPlugin(descriptor);
        registry.loadPlugin(descriptor);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.ACTIVE));
        ActionWithReturn<GoPlugin, Object> action = ( plugin, pluginDescriptor) -> {
            assertThat(pluginDescriptor, is(descriptor));
            assertThat(Thread.currentThread().getContextClassLoader().getClass().getCanonicalName(), not(.class.getCanonicalName()));
            return null;
        };
        pluginOSGiFramework.doOn(GoPlugin.class, "plugin.to.test.classloader", "notification", action);
    }
}


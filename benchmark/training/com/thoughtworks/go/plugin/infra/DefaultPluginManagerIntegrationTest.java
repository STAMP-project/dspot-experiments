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
package com.thoughtworks.go.plugin.infra;


import com.thoughtworks.go.plugin.activation.DefaultGoPluginActivator;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.infra.listeners.DefaultPluginJarChangeListener;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import org.apache.felix.framework.BundleWiringImpl.BundleClassLoader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


// TODO: Write Test to handle OSGIFWK and PLugin Manager Interaction.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext-plugin-infra.xml" })
@DirtiesContext
public class DefaultPluginManagerIntegrationTest {
    @ClassRule
    public static final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @ClassRule
    public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

    public static final String PLUGIN_DESC_PROPERTY_SET_BY_TEST_PLUGIN_1 = "testplugin.descriptorValidator.setPluginDescriptor.invoked";

    private static final String PLUGIN_ID_1 = "testplugin.descriptorValidator";

    private static final String PLUGIN_TO_TEST_CLASSLOADER = "plugin.to.test.classloader";

    private static File bundleDir;

    @Autowired
    DefaultPluginManager pluginManager;

    @Autowired
    DefaultPluginJarChangeListener jarChangeListener;

    @Autowired
    SystemEnvironment systemEnvironment;

    @Test
    public void shouldRegisterTheExtensionClassesOfAPluginAndInitializeAccessorUponFirstCall() throws Exception {
        GoPluginDescriptor plugin = pluginManager.getPluginDescriptorFor(DefaultPluginManagerIntegrationTest.PLUGIN_ID_1);
        Assert.assertThat(plugin.id(), Matchers.is(DefaultPluginManagerIntegrationTest.PLUGIN_ID_1));
        Assert.assertThat(plugin.bundleSymbolicName(), Matchers.is(DefaultPluginManagerIntegrationTest.PLUGIN_ID_1));
        Assert.assertThat(plugin.bundleClassPath(), Matchers.is("lib/go-plugin-activator.jar,.,lib/dependency.jar"));
        Assert.assertThat(plugin.bundleActivator(), Matchers.is(DefaultGoPluginActivator.class.getCanonicalName()));
        Assert.assertThat(plugin.isInvalid(), Matchers.is(false));
        String extensionType = "notification";
        pluginManager.submitTo(DefaultPluginManagerIntegrationTest.PLUGIN_ID_1, extensionType, new DefaultGoPluginApiRequest(extensionType, "2.0", "test-request"));
        Assert.assertThat(System.getProperty(DefaultPluginManagerIntegrationTest.PLUGIN_DESC_PROPERTY_SET_BY_TEST_PLUGIN_1), Matchers.is("PluginLoad: 1, InitAccessor"));
    }

    @Test
    public void shouldSetCurrentThreadContextClassLoaderToBundleClassLoaderToAvoidDependenciesFromWebappClassloaderMessingAroundWithThePluginBehavior() {
        GoPluginDescriptor plugin = pluginManager.getPluginDescriptorFor(DefaultPluginManagerIntegrationTest.PLUGIN_TO_TEST_CLASSLOADER);
        Assert.assertThat(plugin.id(), Matchers.is(DefaultPluginManagerIntegrationTest.PLUGIN_TO_TEST_CLASSLOADER));
        String extensionType = "notification";
        GoPluginApiResponse goPluginApiResponse = pluginManager.submitTo(DefaultPluginManagerIntegrationTest.PLUGIN_TO_TEST_CLASSLOADER, extensionType, new DefaultGoPluginApiRequest(extensionType, "2.0", "Thread.currentThread.getContextClassLoader"));
        Assert.assertThat(goPluginApiResponse.responseBody(), Matchers.is(BundleClassLoader.class.getCanonicalName()));
        goPluginApiResponse = pluginManager.submitTo(DefaultPluginManagerIntegrationTest.PLUGIN_TO_TEST_CLASSLOADER, extensionType, new DefaultGoPluginApiRequest(extensionType, "2.0", "this.getClass.getClassLoader"));
        Assert.assertThat(goPluginApiResponse.responseBody(), Matchers.is(BundleClassLoader.class.getCanonicalName()));
    }
}


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
import com.thoughtworks.go.plugin.infra.listeners.DefaultPluginJarChangeListener;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
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


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext-plugin-infra.xml" })
@DirtiesContext
public class MultipleExtensionPluginWithPluginManagerIntegrationTest {
    @ClassRule
    public static final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @ClassRule
    public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String EXTENSION_1_PROPERTY_PREFIX = "valid-plugin-with-multiple-extensions.task_extension.";

    private static final String EXTENSION_2_PROPERTY_PREFIX = "valid-plugin-with-multiple-extensions.analytics_extension.";

    private static final String PLUGIN_ID = "valid-plugin-with-multiple-extensions";

    @Autowired
    DefaultPluginManager pluginManager;

    @Autowired
    DefaultPluginJarChangeListener jarChangeListener;

    @Autowired
    SystemEnvironment systemEnvironment;

    private static File bundleDir;

    private static File pluginDir;

    @Test
    public void shouldInitializeAccessorForEveryExtensionJustBeforeSendingTheFirstEverRequest() {
        GoPluginDescriptor plugin = pluginManager.getPluginDescriptorFor(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID);
        Assert.assertThat(plugin.id(), Matchers.is(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID));
        Assert.assertThat(plugin.bundleSymbolicName(), Matchers.is(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID));
        Assert.assertThat(plugin.bundleClassPath(), Matchers.is("lib/go-plugin-activator.jar,."));
        Assert.assertThat(plugin.bundleActivator(), Matchers.is(DefaultGoPluginActivator.class.getCanonicalName()));
        Assert.assertThat(plugin.isInvalid(), Matchers.is(false));
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("task", "1.0", "request1");
        request.setRequestBody("{ \"abc\": 1 }");
        pluginManager.submitTo(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID, "task", request);
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "initialize_accessor.count")), Matchers.is("1"));
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_2_PROPERTY_PREFIX) + "initialize_accessor.count")), Matchers.is(Matchers.nullValue()));
        request = new DefaultGoPluginApiRequest("analytics", "1.0", "request1");
        request.setRequestBody("{ \"abc\": 2 }");
        pluginManager.submitTo(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID, "analytics", request);
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "initialize_accessor.count")), Matchers.is("1"));
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_2_PROPERTY_PREFIX) + "initialize_accessor.count")), Matchers.is("1"));
    }

    @Test
    public void shouldNotReinitializeWithAccessorIfAlreadyInitialized() {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("task", "1.0", "request1");
        request.setRequestBody("{ \"abc\": 1 }");
        pluginManager.submitTo(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID, "task", request);
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "initialize_accessor.count")), Matchers.is("1"));
        pluginManager.submitTo(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID, "task", request);
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "initialize_accessor.count")), Matchers.is("1"));
    }

    @Test
    public void shouldSubmitRequestToTheRightExtension() {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("task", "1.0", "request1");
        request.setRequestBody("{ \"abc\": 1 }");
        pluginManager.submitTo(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID, "task", request);
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "request.count")), Matchers.is("1"));
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "request.name")), Matchers.is("request1"));
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "request.body")), Matchers.is("{ \"abc\": 1 }"));
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_2_PROPERTY_PREFIX) + "request.count")), Matchers.is(Matchers.nullValue()));
        request = new DefaultGoPluginApiRequest("task", "1.0", "request2");
        request.setRequestBody("{ \"abc\": 2 }");
        pluginManager.submitTo(MultipleExtensionPluginWithPluginManagerIntegrationTest.PLUGIN_ID, "task", request);
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "request.count")), Matchers.is("2"));
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "request.name")), Matchers.is("request2"));
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_1_PROPERTY_PREFIX) + "request.body")), Matchers.is("{ \"abc\": 2 }"));
        Assert.assertThat(System.getProperty(((MultipleExtensionPluginWithPluginManagerIntegrationTest.EXTENSION_2_PROPERTY_PREFIX) + "request.count")), Matchers.is(Matchers.nullValue()));
    }
}


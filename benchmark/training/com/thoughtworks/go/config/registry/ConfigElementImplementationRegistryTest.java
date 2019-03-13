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
package com.thoughtworks.go.config.registry;


import PluginNamespace.XSD_NAMESPACE_PREFIX;
import PluginNamespace.XSD_NAMESPACE_URI;
import com.thoughtworks.go.config.BuildTask;
import com.thoughtworks.go.domain.Task;
import com.thoughtworks.go.plugins.PluginExtensions;
import com.thoughtworks.go.plugins.PluginTestUtil;
import com.thoughtworks.go.plugins.presentation.PluggableViewModelFactory;
import com.thoughtworks.go.presentation.TaskViewModel;
import com.thoughtworks.go.util.DataStructureUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;


public class ConfigElementImplementationRegistryTest {
    private PluginExtensions pluginExtns;

    @Test
    public void shouldProvideTheNamespaceUriAndTheLocation() throws MalformedURLException {
        ConfigElementImplementationRegistry registry = new ConfigElementImplementationRegistry(pluginExtns);
        URL resource = new File("file:///tmp/foo").toURI().toURL();
        URL resource1 = new File("file:///tmp/bar").toURI().toURL();
        registry.xsdFor(PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "uri-1", XSD_NAMESPACE_URI, "uri1")), resource);
        registry.xsdFor(PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "uri-2", XSD_NAMESPACE_URI, "uri2")), resource);
        registry.xsdFor(PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "uri-3", XSD_NAMESPACE_URI, "uri3")), resource1);
        Assert.assertThat(registry.xsds(), Matchers.containsString(String.format("uri1 %s", resource.toString())));
        Assert.assertThat(registry.xsds(), Matchers.containsString(String.format("uri2 %s", resource.toString())));
        Assert.assertThat(registry.xsds(), Matchers.containsString(String.format("uri3 %s", resource1.toString())));
    }

    @Test
    public void shouldAddPluginNamespaceToPassedInElement() throws MalformedURLException {
        ConfigElementImplementationRegistry registry = new ConfigElementImplementationRegistry(pluginExtns);
        registry.xsdFor(PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "something", XSD_NAMESPACE_URI, "uri")), new File("file:///tmp/foo").toURI().toURL());
        registry.xsdFor(PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "second", XSD_NAMESPACE_URI, "uri-1")), new File("file:///tmp/foo1").toURI().toURL());
        Element foo = new Element("foo");
        registry.registerNamespacesInto(foo);
        Assert.assertThat(foo.getNamespace("something"), Matchers.is(Namespace.getNamespace("something", "uri")));
        Assert.assertThat(foo.getNamespace("second"), Matchers.is(Namespace.getNamespace("second", "uri-1")));
    }

    private static final class TestTaskConfigTypeExtension<T extends Task> implements ConfigTypeExtension<Task> {
        private Class<T> implType;

        private PluggableViewModelFactory<T> factory;

        private TestTaskConfigTypeExtension(Class<T> implType, PluggableViewModelFactory<T> factory) {
            this.implType = implType;
            this.factory = factory;
        }

        public Class<Task> getType() {
            return Task.class;
        }

        public Class<? extends Task> getImplementation() {
            return implType;
        }

        public PluggableViewModelFactory<? extends Task> getFactory() {
            return factory;
        }
    }

    @Test
    public void shouldCreateTaskViewModelForPlugins() throws MalformedURLException {
        BundleContext execCtx = PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "exec", XSD_NAMESPACE_URI, "uri-exec"));
        PluggableViewModelFactory<ConfigElementImplementationRegistryTest.PluginExec> factory = Mockito.mock(PluggableViewModelFactory.class);
        ConfigTypeExtension exec = new ConfigElementImplementationRegistryTest.TestTaskConfigTypeExtension(ConfigElementImplementationRegistryTest.PluginExec.class, factory);
        ConfigElementImplementationRegistryTest.PluginExec execInstance = new ConfigElementImplementationRegistryTest.PluginExec();
        TaskViewModel stubbedViewModel = new TaskViewModel(execInstance, "my/view");
        Mockito.when(factory.viewModelFor(execInstance, "new")).thenReturn(stubbedViewModel);
        ConfigurationExtension execTask = new ConfigurationExtension(new PluginNamespace(execCtx, new URL("file:///exec")), exec);
        Mockito.when(pluginExtns.configTagImplementations()).thenReturn(Arrays.asList(execTask));
        ConfigElementImplementationRegistry registry = new ConfigElementImplementationRegistry(pluginExtns);
        Assert.assertThat(registry.getViewModelFor(execInstance, "new"), Matchers.is(stubbedViewModel));
    }

    @Test
    public void shouldNotThrowUpIfPluginHasNotRegisteredViewTemplates() throws Exception {
        BundleContext execCtx = PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "exec", XSD_NAMESPACE_URI, "uri-exec"));
        ConfigTypeExtension exec = new ConfigElementImplementationRegistryTest.TestTaskConfigTypeExtension(ConfigElementImplementationRegistryTest.PluginExec.class, PluggableViewModelFactory.DOES_NOT_APPLY);
        ConfigElementImplementationRegistryTest.PluginExec execInstance = new ConfigElementImplementationRegistryTest.PluginExec();
        ConfigurationExtension execTask = new ConfigurationExtension(new PluginNamespace(execCtx, new URL("file:///exec")), exec);
        Mockito.when(pluginExtns.configTagImplementations()).thenReturn(Arrays.asList(execTask));
        ConfigElementImplementationRegistry registry = new ConfigElementImplementationRegistry(pluginExtns);
        try {
            registry.getViewModelFor(execInstance, "new");
            Assert.fail("Should not have a view model when the plugin factory does not exist");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is(String.format("This component does not support rendering '%s' for action 'new'", execInstance)));
        }
    }

    @Test
    public void registerAllConfigTagImplementationsProvidedByPlugins() throws MalformedURLException {
        BundleContext execCtx = PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "exec", XSD_NAMESPACE_URI, "uri-exec"));
        PluggableViewModelFactory<ConfigElementImplementationRegistryTest.PluginExec> factory = Mockito.mock(PluggableViewModelFactory.class);
        ConfigTypeExtension exec = new ConfigElementImplementationRegistryTest.TestTaskConfigTypeExtension(ConfigElementImplementationRegistryTest.PluginExec.class, factory);
        ConfigurationExtension execTag = new ConfigurationExtension(new PluginNamespace(execCtx, new URL("file:///exec")), exec);
        BundleContext antCtx = PluginTestUtil.bundleCtxWithHeaders(DataStructureUtils.m(XSD_NAMESPACE_PREFIX, "ant", XSD_NAMESPACE_URI, "uri-ant"));
        ConfigTypeExtension ant = new ConfigElementImplementationRegistryTest.TestTaskConfigTypeExtension(ConfigElementImplementationRegistryTest.PluginAnt.class, Mockito.mock(PluggableViewModelFactory.class));
        ConfigurationExtension antTag = new ConfigurationExtension(new PluginNamespace(antCtx, new URL("file:///ant")), ant);
        Mockito.when(pluginExtns.configTagImplementations()).thenReturn(Arrays.asList(execTag, antTag));
        ConfigElementImplementationRegistry registry = new ConfigElementImplementationRegistry(pluginExtns);
        Assert.assertThat(registry.xsds(), Matchers.containsString("uri-exec file:/exec"));
        Assert.assertThat(registry.xsds(), Matchers.containsString("uri-ant file:/ant"));
        List<Class<? extends Task>> implementationTypes = registry.implementersOf(Task.class);
        Assert.assertThat(implementationTypes.contains(ConfigElementImplementationRegistryTest.PluginExec.class), Matchers.is(true));
        Assert.assertThat(implementationTypes.contains(ConfigElementImplementationRegistryTest.PluginAnt.class), Matchers.is(true));
        Element mock = Mockito.mock(Element.class);
        registry.registerNamespacesInto(mock);
        Mockito.verify(mock).addNamespaceDeclaration(Namespace.getNamespace("exec", "uri-exec"));
        Mockito.verify(mock).addNamespaceDeclaration(Namespace.getNamespace("ant", "uri-ant"));
    }

    class PluginAnt extends BuildTask {
        @Override
        public String getTaskType() {
            return "build";
        }

        public String getTypeForDisplay() {
            return null;
        }

        @Override
        public String command() {
            return null;
        }

        @Override
        public String arguments() {
            return null;
        }
    }

    class PluginExec extends BuildTask {
        @Override
        public String getTaskType() {
            return "build";
        }

        public String getTypeForDisplay() {
            return null;
        }

        @Override
        public String command() {
            return null;
        }

        @Override
        public String arguments() {
            return null;
        }
    }
}


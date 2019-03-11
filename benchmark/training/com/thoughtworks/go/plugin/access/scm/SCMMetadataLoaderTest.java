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
package com.thoughtworks.go.plugin.access.scm;


import SCMProperty.PART_OF_IDENTITY;
import SCMProperty.REQUIRED;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SCMMetadataLoaderTest {
    private SCMMetadataLoader metadataLoader;

    private GoPluginDescriptor pluginDescriptor;

    private SCMExtension scmExtension;

    private PluginManager pluginManager;

    @Test
    public void shouldFetchSCMMetadataForPluginsWhichImplementSCMExtensionPoint() {
        SCMPropertyConfiguration scmPropertyConfiguration = new SCMPropertyConfiguration();
        scmPropertyConfiguration.add(new SCMProperty("k1").with(REQUIRED, true).with(PART_OF_IDENTITY, false));
        Mockito.when(scmExtension.getSCMConfiguration(pluginDescriptor.id())).thenReturn(scmPropertyConfiguration);
        Mockito.when(scmExtension.getSCMView(pluginDescriptor.id())).thenReturn(createSCMView("display-value", "template"));
        metadataLoader.fetchSCMMetaData(pluginDescriptor);
        SCMConfigurations configurationMetadata = SCMMetadataStore.getInstance().getConfigurationMetadata(pluginDescriptor.id());
        Assert.assertThat(configurationMetadata.size(), Matchers.is(1));
        SCMConfiguration scmConfiguration = configurationMetadata.get("k1");
        Assert.assertThat(scmConfiguration.getKey(), Matchers.is("k1"));
        Assert.assertThat(scmConfiguration.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(scmConfiguration.getOption(PART_OF_IDENTITY), Matchers.is(false));
        SCMView viewMetadata = SCMMetadataStore.getInstance().getViewMetadata(pluginDescriptor.id());
        Assert.assertThat(viewMetadata.displayValue(), Matchers.is("display-value"));
        Assert.assertThat(viewMetadata.template(), Matchers.is("template"));
    }

    @Test
    public void shouldThrowExceptionWhenNullSCMConfigurationReturned() {
        Mockito.when(scmExtension.getSCMConfiguration(pluginDescriptor.id())).thenReturn(null);
        try {
            metadataLoader.fetchSCMMetaData(pluginDescriptor);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Plugin[plugin-id] returned null SCM configuration"));
        }
        Assert.assertThat(SCMMetadataStore.getInstance().getConfigurationMetadata(pluginDescriptor.id()), Matchers.nullValue());
    }

    @Test
    public void shouldThrowExceptionWhenNullSCMViewReturned() {
        Mockito.when(scmExtension.getSCMConfiguration(pluginDescriptor.id())).thenReturn(new SCMPropertyConfiguration());
        Mockito.when(scmExtension.getSCMView(pluginDescriptor.id())).thenReturn(null);
        try {
            metadataLoader.fetchSCMMetaData(pluginDescriptor);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Plugin[plugin-id] returned null SCM view"));
        }
        Assert.assertThat(SCMMetadataStore.getInstance().getConfigurationMetadata(pluginDescriptor.id()), Matchers.nullValue());
    }

    @Test
    public void shouldRegisterAsPluginFrameworkStartListener() throws Exception {
        metadataLoader = new SCMMetadataLoader(scmExtension, pluginManager);
        Mockito.verify(pluginManager).addPluginChangeListener(metadataLoader);
    }

    @Test
    public void shouldFetchMetadataOnPluginLoadedCallback() throws Exception {
        SCMMetadataLoader spy = Mockito.spy(metadataLoader);
        Mockito.doNothing().when(spy).fetchSCMMetaData(pluginDescriptor);
        Mockito.when(scmExtension.canHandlePlugin(pluginDescriptor.id())).thenReturn(true);
        spy.pluginLoaded(pluginDescriptor);
        Mockito.verify(spy).fetchSCMMetaData(pluginDescriptor);
    }

    @Test
    public void shouldNotTryToFetchMetadataOnPluginLoadedCallback() throws Exception {
        SCMMetadataLoader spy = Mockito.spy(metadataLoader);
        Mockito.when(scmExtension.canHandlePlugin(pluginDescriptor.id())).thenReturn(false);
        spy.pluginLoaded(pluginDescriptor);
        Mockito.verify(spy, Mockito.never()).fetchSCMMetaData(pluginDescriptor);
    }

    @Test
    public void shouldRemoveMetadataOnPluginUnLoadedCallback() throws Exception {
        SCMMetadataStore.getInstance().addMetadataFor(pluginDescriptor.id(), new SCMConfigurations(), createSCMView(null, null));
        Mockito.when(scmExtension.canHandlePlugin(pluginDescriptor.id())).thenReturn(true);
        metadataLoader.pluginUnLoaded(pluginDescriptor);
        Assert.assertThat(SCMMetadataStore.getInstance().getConfigurationMetadata(pluginDescriptor.id()), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(SCMMetadataStore.getInstance().getViewMetadata(pluginDescriptor.id()), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotTryRemoveMetadataOnPluginUnLoadedCallback() throws Exception {
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        SCMView scmView = createSCMView(null, null);
        SCMMetadataStore.getInstance().addMetadataFor(pluginDescriptor.id(), scmConfigurations, scmView);
        Mockito.when(scmExtension.canHandlePlugin(pluginDescriptor.id())).thenReturn(false);
        metadataLoader.pluginUnLoaded(pluginDescriptor);
        Assert.assertThat(SCMMetadataStore.getInstance().getConfigurationMetadata(pluginDescriptor.id()), Matchers.is(scmConfigurations));
        Assert.assertThat(SCMMetadataStore.getInstance().getViewMetadata(pluginDescriptor.id()), Matchers.is(scmView));
    }
}


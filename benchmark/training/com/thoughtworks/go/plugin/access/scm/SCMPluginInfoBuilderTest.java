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
package com.thoughtworks.go.plugin.access.scm;


import com.thoughtworks.go.plugin.domain.scm.SCMPluginInfo;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class SCMPluginInfoBuilderTest {
    private SCMExtension extension;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldBuildPluginInfo() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        SCMPluginInfo pluginInfo = new SCMPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        List<PluginConfiguration> scmConfigurations = Arrays.asList(new PluginConfiguration("username", new MetadataWithPartOfIdentity(true, false, true)), new PluginConfiguration("password", new MetadataWithPartOfIdentity(true, true, false)));
        PluginView pluginView = new PluginView("some html");
        List<PluginConfiguration> pluginSettings = Arrays.asList(new PluginConfiguration("k1", new Metadata(true, false)));
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("scm"));
        Assert.assertThat(pluginInfo.getDisplayName(), Matchers.is("some scm plugin"));
        Assert.assertThat(pluginInfo.getScmSettings(), Matchers.is(new PluggableInstanceSettings(scmConfigurations, pluginView)));
        Assert.assertThat(pluginInfo.getPluginSettings(), Matchers.is(new PluggableInstanceSettings(pluginSettings, new PluginView("settings view"))));
    }

    @Test
    public void shouldThrowAnExceptionIfScmConfigReturnedByPluginIsNull() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.when(extension.getSCMConfiguration("plugin1")).thenReturn(null);
        thrown.expectMessage("Plugin[plugin1] returned null scm configuration");
        new SCMPluginInfoBuilder(extension).pluginInfoFor(descriptor);
    }

    @Test
    public void shouldThrowAnExceptionIfScmViewReturnedByPluginIsNull() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.when(extension.getSCMView("plugin1")).thenReturn(null);
        thrown.expectMessage("Plugin[plugin1] returned null scm view");
        new SCMPluginInfoBuilder(extension).pluginInfoFor(descriptor);
    }
}


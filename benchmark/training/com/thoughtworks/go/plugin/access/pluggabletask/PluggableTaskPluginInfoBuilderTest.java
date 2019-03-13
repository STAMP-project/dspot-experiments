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
package com.thoughtworks.go.plugin.access.pluggabletask;


import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluginConfiguration;
import com.thoughtworks.go.plugin.domain.common.PluginView;
import com.thoughtworks.go.plugin.domain.pluggabletask.PluggableTaskPluginInfo;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PluggableTaskPluginInfoBuilderTest {
    private TaskExtension extension;

    @Test
    public void shouldBuildPluginInfo() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        PluggableTaskPluginInfo pluginInfo = new PluggableTaskPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("username", new Metadata(true, false)), new PluginConfiguration("password", new Metadata(true, true)));
        PluginView pluginView = new PluginView("some html");
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("task"));
        Assert.assertThat(pluginInfo.getDisplayName(), Matchers.is("my task plugin"));
        Assert.assertThat(pluginInfo.getTaskSettings(), Matchers.is(new com.thoughtworks.go.plugin.domain.common.PluggableInstanceSettings(pluginConfigurations, pluginView)));
        Assert.assertNull(pluginInfo.getPluginSettings());
    }
}


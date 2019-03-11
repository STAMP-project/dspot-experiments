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
package com.thoughtworks.go.plugin.infra.plugininfo;


import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class GoPluginDescriptorBuilderTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String TESTPLUGIN_ID = "testplugin.descriptorValidator";

    private GoPluginDescriptorBuilder goPluginDescriptorBuilder;

    private File pluginDirectory;

    private File bundleDirectory;

    @Test
    public void shouldCreateThePluginDescriptorFromGivenPluginJarWithPluginXML() throws Exception {
        String pluginJarName = "descriptor-aware-test-plugin.jar";
        copyPluginToThePluginDirectory(pluginDirectory, pluginJarName);
        File pluginJarFile = new File(pluginDirectory, pluginJarName);
        GoPluginDescriptor descriptor = goPluginDescriptorBuilder.build(pluginJarFile, true);
        GoPluginDescriptor expectedDescriptor = buildExpectedDescriptor(pluginJarName, pluginJarFile.getAbsolutePath());
        Assert.assertThat(descriptor, Matchers.is(expectedDescriptor));
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(false));
        Assert.assertThat(descriptor.isBundledPlugin(), Matchers.is(true));
    }

    @Test
    public void shouldCreateInvalidPluginDescriptorBecausePluginXMLDoesNotConformToXSD() throws Exception {
        String pluginJarName = "invalid-descriptor-plugin.jar";
        copyPluginToThePluginDirectory(pluginDirectory, pluginJarName);
        File pluginJarFile = new File(pluginDirectory, pluginJarName);
        GoPluginDescriptor descriptor = goPluginDescriptorBuilder.build(pluginJarFile, true);
        GoPluginDescriptor expectedDescriptor = buildXMLSchemaErrorDescriptor(pluginJarName);
        Assert.assertThat(descriptor, Matchers.is(expectedDescriptor));
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.isBundledPlugin(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages(), Matchers.is(expectedDescriptor.getStatus().getMessages()));
    }

    @Test
    public void shouldCreatePluginDescriptorEvenIfPluginXMLIsNotFound() throws Exception {
        String pluginJarName = "descriptor-aware-test-plugin-with-no-plugin-xml.jar";
        copyPluginToThePluginDirectory(pluginDirectory, pluginJarName);
        File pluginJarFile = new File(pluginDirectory, pluginJarName);
        GoPluginDescriptor descriptor = goPluginDescriptorBuilder.build(pluginJarFile, false);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(false));
        Assert.assertThat(descriptor.id(), Matchers.is(pluginJarName));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionForInvalidPluginIfThePluginJarDoesNotExist() throws Exception {
        goPluginDescriptorBuilder.build(new File(pluginDirectory, "invalid"), true);
    }
}


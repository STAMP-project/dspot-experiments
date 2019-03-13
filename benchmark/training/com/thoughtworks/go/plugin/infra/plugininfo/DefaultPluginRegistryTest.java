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
package com.thoughtworks.go.plugin.infra.plugininfo;


import java.io.File;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultPluginRegistryTest {
    private DefaultPluginRegistry registry;

    @Test
    public void shouldMarkPluginAsInvalidWithMessage() throws Exception {
        String pluginId = "plugin-id";
        File pluginFile = Mockito.mock(File.class);
        String message = "random failure";
        DefaultPluginRegistry spy = Mockito.spy(registry);
        GoPluginDescriptor descriptor = new GoPluginDescriptor(pluginId, "1.0", null, null, pluginFile, true);
        spy.loadPlugin(descriptor);
        spy.markPluginInvalid(pluginId, Arrays.asList(message));
        GoPluginDescriptor loadedDescriptor = spy.plugins().get(0);
        Assert.assertThat(loadedDescriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(loadedDescriptor.getStatus().getMessages(), Matchers.hasItem(message));
    }

    @Test
    public void testThrowExceptionWhenPluginNotFound() throws Exception {
        try {
            registry.markPluginInvalid("invalid-plugin-id", Arrays.asList("some message"));
            Assert.fail("should have thrown exception for plugin not found ");
        } catch (Exception e) {
            Assert.assertThat((e instanceof RuntimeException), Matchers.is(true));
            Assert.assertThat(e.getMessage(), Matchers.is("Invalid plugin identifier 'invalid-plugin-id'"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenPluginIdIsNull() throws Exception {
        try {
            registry.markPluginInvalid(null, Arrays.asList("some message"));
            Assert.fail("should have thrown exception for plugin not found ");
        } catch (Exception e) {
            Assert.assertThat((e instanceof RuntimeException), Matchers.is(true));
            Assert.assertThat(e.getMessage(), Matchers.is("Invalid plugin identifier 'null'"));
        }
    }

    @Test
    public void shouldListAllLoadedPlugins() throws Exception {
        GoPluginDescriptor descriptor1 = GoPluginDescriptor.usingId("id1", null, null, true);
        registry.loadPlugin(descriptor1);
        GoPluginDescriptor descriptor2 = GoPluginDescriptor.usingId("id2", null, null, true);
        registry.loadPlugin(descriptor2);
        Assert.assertThat(registry.plugins().size(), Matchers.is(2));
        Assert.assertThat(registry.plugins(), Matchers.hasItems(descriptor1, descriptor2));
    }

    @Test
    public void shouldReturnThePluginWithGivenId() throws Exception {
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("id", null, null, true);
        registry.loadPlugin(descriptor);
        Assert.assertThat(registry.getPlugin("id"), Matchers.is(descriptor));
    }

    @Test
    public void shouldUnloadPluginFromRegistry() throws Exception {
        GoPluginDescriptor descriptor1 = GoPluginDescriptor.usingId("id1", "location-one.jar", new File("location-one"), true);
        registry.loadPlugin(descriptor1);
        GoPluginDescriptor descriptor2 = GoPluginDescriptor.usingId("id2", "location-two.jar", new File("location-two"), true);
        registry.loadPlugin(descriptor2);
        Assert.assertThat(registry.plugins().size(), Matchers.is(2));
        Assert.assertThat(registry.plugins(), Matchers.hasItems(descriptor1, descriptor2));
        registry.unloadPlugin(descriptor2);
        Assert.assertThat(registry.plugins().size(), Matchers.is(1));
        Assert.assertThat(registry.plugins(), Matchers.hasItems(descriptor1));
    }

    @Test
    public void shouldBeAbleToUnloadThePluginBasedOnFileNameEvenIfTheIDHasBeenChanged() throws Exception {
        File bundleLocation = Mockito.mock(File.class);
        Mockito.when(bundleLocation.getName()).thenReturn("plugin-id");
        GoPluginDescriptor descriptor1 = GoPluginDescriptor.usingId("id", "some-plugin.jar", bundleLocation, true);
        registry.loadPlugin(descriptor1);
        Assert.assertThat(descriptor1.id(), Matchers.is("id"));
        GoPluginDescriptor descriptorOfPluginToBeUnloaded = GoPluginDescriptor.usingId("plugin-id", "some-plugin.jar", bundleLocation, true);
        GoPluginDescriptor descriptorOfUnloadedPlugin = registry.unloadPlugin(descriptorOfPluginToBeUnloaded);
        Assert.assertThat(descriptorOfUnloadedPlugin.id(), Matchers.is("id"));
        Assert.assertThat(registry.plugins().size(), Matchers.is(0));
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotUnloadAPluginIfItWasNotLoadedBefore() throws Exception {
        registry.unloadPlugin(GoPluginDescriptor.usingId("id1", null, null, true));
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotLoadPluginIfThereIsOneMorePluginWithTheSameID() throws Exception {
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("id1", null, null, true);
        registry.loadPlugin(descriptor);
        GoPluginDescriptor secondPluginBundleLocation = GoPluginDescriptor.usingId("id1", null, null, true);
        registry.loadPlugin(secondPluginBundleLocation);
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotLoadPluginIfThereIsOneMorePluginWithTheSameIDAndDifferentCase() throws Exception {
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("id1", null, null, true);
        registry.loadPlugin(descriptor);
        GoPluginDescriptor secondPluginBundleLocation = GoPluginDescriptor.usingId("iD1", null, null, true);
        registry.loadPlugin(secondPluginBundleLocation);
    }
}


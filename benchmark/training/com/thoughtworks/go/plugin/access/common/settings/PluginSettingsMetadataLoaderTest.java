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
package com.thoughtworks.go.plugin.access.common.settings;


import PluginConstants.NOTIFICATION_EXTENSION;
import Property.REQUIRED;
import Property.SECURE;
import com.thoughtworks.go.plugin.access.common.AbstractExtension;
import com.thoughtworks.go.plugin.access.configrepo.ConfigRepoExtension;
import com.thoughtworks.go.plugin.access.notification.NotificationExtension;
import com.thoughtworks.go.plugin.access.packagematerial.PackageRepositoryExtension;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskExtension;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PluginSettingsMetadataLoaderTest {
    @Mock
    private PackageRepositoryExtension packageRepositoryExtension;

    @Mock
    private SCMExtension scmExtension;

    @Mock
    private TaskExtension taskExtension;

    @Mock
    private NotificationExtension notificationExtension;

    @Mock
    private ConfigRepoExtension configRepoExtension;

    @Mock
    private PluginManager pluginManager;

    private PluginSettingsMetadataLoader metadataLoader;

    @Test
    public void shouldFetchPluginSettingsMetadataForPluginBasedOnPluginId() throws Exception {
        List<AbstractExtension> everyExtensionExceptTask = Arrays.asList(packageRepositoryExtension, scmExtension, notificationExtension, configRepoExtension);
        for (GoPluginExtension extension : everyExtensionExceptTask) {
            PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
            configuration.add(new PluginSettingsProperty("k1").with(REQUIRED, true).with(SECURE, false));
            GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(UUID.randomUUID().toString(), "1.0", null, null, null, true);
            setupSettingsResponses(extension, pluginDescriptor.id(), configuration, "template");
            metadataLoader.fetchPluginSettingsMetaData(pluginDescriptor);
            verifyMetadataForPlugin(pluginDescriptor.id());
        }
    }

    @Test
    public void shouldNotFetchPluginSettingsMetadataForTaskPlugin() throws Exception {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        configuration.add(new PluginSettingsProperty("k1").with(REQUIRED, true).with(SECURE, false));
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(UUID.randomUUID().toString(), "1.0", null, null, null, true);
        setupSettingsResponses(taskExtension, pluginDescriptor.id(), configuration, "template");
        metadataLoader.fetchPluginSettingsMetaData(pluginDescriptor);
        Mockito.verify(taskExtension, Mockito.never()).getPluginSettingsConfiguration(pluginDescriptor.id());
        Mockito.verify(taskExtension, Mockito.never()).getPluginSettingsView(pluginDescriptor.id());
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().configuration(pluginDescriptor.id()), is(nullValue()));
    }

    @Test
    public void shouldNotStoreMetadataIfConfigurationIsMissing() {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        configuration.add(new PluginSettingsProperty("k1").with(REQUIRED, true).with(SECURE, false));
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor("plugin-id", "1.0", null, null, null, true);
        setupSettingsResponses(packageRepositoryExtension, pluginDescriptor.id(), null, "template");
        metadataLoader.fetchPluginSettingsMetaData(pluginDescriptor);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginDescriptor.id()), is(false));
    }

    @Test
    public void shouldNotStoreMetadataIfViewTemplateIsMissing() {
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor("plugin-id", "1.0", null, null, null, true);
        setupSettingsResponses(packageRepositoryExtension, pluginDescriptor.id(), null, null);
        metadataLoader.fetchPluginSettingsMetaData(pluginDescriptor);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginDescriptor.id()), is(false));
    }

    @Test
    public void shouldRegisterAsPluginFrameworkStartListener() throws Exception {
        Mockito.verify(pluginManager).addPluginChangeListener(metadataLoader);
    }

    @Test
    public void shouldRemoveMetadataOnPluginUnLoadedCallback() throws Exception {
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor("plugin-id", "1.0", null, null, null, true);
        PluginSettingsMetadataStore.getInstance().addMetadataFor(pluginDescriptor.id(), NOTIFICATION_EXTENSION, new PluginSettingsConfiguration(), "template");
        metadataLoader.pluginUnLoaded(pluginDescriptor);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginDescriptor.id()), is(false));
    }

    @Test
    public void shouldFailWhenAPluginWithMultipleExtensionsHasMoreThanOneExtensionRespondingWithSettings() throws Exception {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        configuration.add(new PluginSettingsProperty("k1").with(REQUIRED, true).with(SECURE, false));
        String pluginID = "plugin-id";
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(pluginID, "1.0", null, null, null, true);
        setupSettingsResponses(notificationExtension, pluginID, configuration, "view");
        setupSettingsResponses(packageRepositoryExtension, pluginID, configuration, "view");
        try {
            metadataLoader.fetchPluginSettingsMetaData(pluginDescriptor);
            Assert.fail("Should have failed since multiple extensions support plugin settings.");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), containsString("Plugin with ID: plugin-id has more than one extension which supports plugin settings"));
            Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginDescriptor.id()), is(false));
        }
    }

    @Test
    public void shouldNotFailWhenAPluginWithMultipleExtensionsHasMoreThanOneExtensionRespondingWithSettings_BUT_OnlyOneIsValid() throws Exception {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        configuration.add(new PluginSettingsProperty("k1").with(REQUIRED, true).with(SECURE, false));
        String pluginID = "plugin-id";
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(pluginID, "1.0", null, null, null, true);
        setupSettingsResponses(notificationExtension, pluginID, configuration, null);
        setupSettingsResponses(packageRepositoryExtension, pluginID, configuration, "view");
        metadataLoader.fetchPluginSettingsMetaData(pluginDescriptor);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginID), is(true));
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().configuration(pluginID), is(configuration));
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().template(pluginID), is("view"));
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().extensionWhichCanHandleSettings(pluginID), is(PluginConstants.PACKAGE_MATERIAL_EXTENSION));
    }

    @Test
    public void shouldNotFailWhenAPluginWithMultipleExtensionsHasMoreThanOneExtensionRespondingWithSettings_BUT_NoneIsValid() throws Exception {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        configuration.add(new PluginSettingsProperty("k1").with(REQUIRED, true).with(SECURE, false));
        String pluginID = "plugin-id";
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(pluginID, "1.0", null, null, null, true);
        setupSettingsResponses(notificationExtension, pluginID, configuration, null);
        setupSettingsResponses(packageRepositoryExtension, pluginID, null, "view");
        metadataLoader.fetchPluginSettingsMetaData(pluginDescriptor);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginID), is(false));
    }

    @Test
    public void shouldNotFailWhenAPluginWithMultipleExtensionsHasMoreThanOneExtensionRespondingWithSettings_BUT_OneIsValidAndOtherThrowsException() throws Exception {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        configuration.add(new PluginSettingsProperty("k1").with(REQUIRED, true).with(SECURE, false));
        String pluginID = "plugin-id";
        GoPluginDescriptor pluginDescriptor = new GoPluginDescriptor(pluginID, "1.0", null, null, null, true);
        setupSettingsResponses(notificationExtension, pluginID, configuration, "view");
        Mockito.when(packageRepositoryExtension.canHandlePlugin(pluginID)).thenReturn(false);
        Mockito.when(scmExtension.canHandlePlugin(pluginID)).thenReturn(true);
        Mockito.when(scmExtension.getPluginSettingsConfiguration(pluginID)).thenThrow(new RuntimeException("Ouch!"));
        Mockito.when(scmExtension.getPluginSettingsView(pluginID)).thenReturn("view");
        metadataLoader.fetchPluginSettingsMetaData(pluginDescriptor);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginID), is(true));
        Mockito.verify(packageRepositoryExtension, Mockito.never()).getPluginSettingsConfiguration(pluginID);
        Mockito.verify(packageRepositoryExtension, Mockito.never()).getPluginSettingsView(pluginID);
    }

    @Test
    public void shouldReturnNullForExtensionWhichCanHandleSettingsIfPluginDoesNotExist() throws Exception {
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().extensionWhichCanHandleSettings("INVALID-PLUGIN"), is(nullValue()));
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().extensionWhichCanHandleSettings(""), is(nullValue()));
    }
}


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
package com.thoughtworks.go.plugin.access.common;


import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.PluginRequestHelper;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsConstants;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsJsonMessageHandler1_0;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsJsonMessageHandler2_0;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.infra.PluginManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AbstractExtensionTest {
    private AbstractExtension extension;

    @Mock
    private PluginManager pluginManager;

    @Mock
    ExtensionsRegistry extensionsRegistry;

    private String extensionName;

    private static List<String> goSupportedVersions = Arrays.asList("1.0", "2.0");

    private String pluginId;

    private static class TestExtension extends AbstractExtension {
        protected TestExtension(PluginManager pluginManager, ExtensionsRegistry extensionsRegistry, PluginRequestHelper pluginRequestHelper, String extensionName) {
            super(pluginManager, extensionsRegistry, pluginRequestHelper, extensionName);
        }

        @Override
        public List<String> goSupportedVersions() {
            return AbstractExtensionTest.goSupportedVersions;
        }
    }

    @Test
    public void shouldNotifySettingsChangeForPluginWhichSupportsNotification() throws Exception {
        String supportedVersion = "2.0";
        Map<String, String> settings = Collections.singletonMap("foo", "bar");
        ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor = ArgumentCaptor.forClass(GoPluginApiRequest.class);
        extension.registerHandler(supportedVersion, new PluginSettingsJsonMessageHandler2_0());
        Mockito.when(pluginManager.resolveExtensionVersion(pluginId, extensionName, AbstractExtensionTest.goSupportedVersions)).thenReturn(supportedVersion);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, ""));
        extension.notifyPluginSettingsChange(pluginId, settings);
        assertRequest(requestArgumentCaptor.getValue(), extensionName, supportedVersion, PluginSettingsConstants.REQUEST_NOTIFY_PLUGIN_SETTINGS_CHANGE, "{\"foo\":\"bar\"}");
    }

    @Test
    public void shouldIgnoreSettingsChangeNotificationIfPluginDoesNotSupportsNotification() throws Exception {
        String supportedVersion = "1.0";
        Map<String, String> settings = Collections.singletonMap("foo", "bar");
        extension.registerHandler(supportedVersion, new PluginSettingsJsonMessageHandler1_0());
        Mockito.when(pluginManager.resolveExtensionVersion(pluginId, extensionName, AbstractExtensionTest.goSupportedVersions)).thenReturn(supportedVersion);
        extension.notifyPluginSettingsChange(pluginId, settings);
        Mockito.verify(pluginManager, Mockito.times(0)).submitTo(ArgumentMatchers.anyString(), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class));
    }
}


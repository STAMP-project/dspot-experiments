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
package com.thoughtworks.go.plugin.access.notification;


import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsConstants;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsJsonMessageHandler2_0;
import com.thoughtworks.go.plugin.access.notification.v4.JsonMessageHandler4_0;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class NotificationExtensionTestForV4 extends NotificationExtensionTestBase {
    @Mock
    private PluginSettingsJsonMessageHandler2_0 pluginSettingsJSONMessageHandlerv2;

    @Mock
    private JsonMessageHandler4_0 jsonMessageHandlerv4;

    @Test
    public void shouldNotifyPluginSettingsChange() throws Exception {
        String supportedVersion = "4.0";
        Map<String, String> settings = Collections.singletonMap("foo", "bar");
        ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor = ArgumentCaptor.forClass(GoPluginApiRequest.class);
        Mockito.when(pluginManager.resolveExtensionVersion(ArgumentMatchers.eq("pluginId"), ArgumentMatchers.eq(PluginConstants.NOTIFICATION_EXTENSION), ArgumentMatchers.anyList())).thenReturn(supportedVersion);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.NOTIFICATION_EXTENSION, "pluginId")).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq("pluginId"), ArgumentMatchers.eq(PluginConstants.NOTIFICATION_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, ""));
        NotificationExtension extension = new NotificationExtension(pluginManager, extensionsRegistry);
        extension.notifyPluginSettingsChange("pluginId", settings);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.NOTIFICATION_EXTENSION, supportedVersion, PluginSettingsConstants.REQUEST_NOTIFY_PLUGIN_SETTINGS_CHANGE, "{\"foo\":\"bar\"}");
    }
}


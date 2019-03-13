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


import NotificationExtension.REQUEST_NOTIFICATIONS_INTERESTED_IN;
import PluginSettingsConstants.REQUEST_PLUGIN_SETTINGS_CONFIGURATION;
import PluginSettingsConstants.REQUEST_PLUGIN_SETTINGS_VIEW;
import PluginSettingsConstants.REQUEST_VALIDATE_PLUGIN_SETTINGS;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.domain.buildcause.BuildCause;
import com.thoughtworks.go.domain.notificationdata.StageNotificationData;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.common.AbstractExtension;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsConfiguration;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;


public abstract class NotificationExtensionTestBase {
    private static final String PLUGIN_ID = "plugin-id";

    private static final String RESPONSE_BODY = "expected-response";

    private NotificationExtension notificationExtension;

    private PluginSettingsConfiguration pluginSettingsConfiguration;

    private ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    @Mock
    protected PluginManager pluginManager;

    @Mock
    protected ExtensionsRegistry extensionsRegistry;

    @Test
    public void shouldExtendAbstractExtension() {
        Assert.assertTrue(((notificationExtension) instanceof AbstractExtension));
    }

    @Test
    public void shouldTalkToPluginToGetPluginSettingsConfiguration() {
        PluginSettingsConfiguration deserializedResponse = new PluginSettingsConfiguration();
        Mockito.when(pluginSettingsJSONMessageHandler().responseMessageForPluginSettingsConfiguration(NotificationExtensionTestBase.RESPONSE_BODY)).thenReturn(deserializedResponse);
        PluginSettingsConfiguration response = notificationExtension.getPluginSettingsConfiguration(NotificationExtensionTestBase.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.NOTIFICATION_EXTENSION, apiVersion(), REQUEST_PLUGIN_SETTINGS_CONFIGURATION, null);
        Mockito.verify(pluginSettingsJSONMessageHandler()).responseMessageForPluginSettingsConfiguration(NotificationExtensionTestBase.RESPONSE_BODY);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetPluginSettingsView() throws Exception {
        String deserializedResponse = "";
        Mockito.when(pluginSettingsJSONMessageHandler().responseMessageForPluginSettingsView(NotificationExtensionTestBase.RESPONSE_BODY)).thenReturn(deserializedResponse);
        String response = notificationExtension.getPluginSettingsView(NotificationExtensionTestBase.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.NOTIFICATION_EXTENSION, apiVersion(), REQUEST_PLUGIN_SETTINGS_VIEW, null);
        Mockito.verify(pluginSettingsJSONMessageHandler()).responseMessageForPluginSettingsView(NotificationExtensionTestBase.RESPONSE_BODY);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToValidatePluginSettings() throws Exception {
        String requestBody = "expected-request";
        Mockito.when(pluginSettingsJSONMessageHandler().requestMessageForPluginSettingsValidation(pluginSettingsConfiguration)).thenReturn(requestBody);
        ValidationResult deserializedResponse = new ValidationResult();
        Mockito.when(pluginSettingsJSONMessageHandler().responseMessageForPluginSettingsValidation(NotificationExtensionTestBase.RESPONSE_BODY)).thenReturn(deserializedResponse);
        ValidationResult response = notificationExtension.validatePluginSettings(NotificationExtensionTestBase.PLUGIN_ID, pluginSettingsConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.NOTIFICATION_EXTENSION, apiVersion(), REQUEST_VALIDATE_PLUGIN_SETTINGS, requestBody);
        Mockito.verify(pluginSettingsJSONMessageHandler()).responseMessageForPluginSettingsValidation(NotificationExtensionTestBase.RESPONSE_BODY);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetNotificationsInterestedIn() throws Exception {
        List<String> response = Arrays.asList("pipeline-status", "stage-status");
        Mockito.when(jsonMessageHandler().responseMessageForNotificationsInterestedIn(NotificationExtensionTestBase.RESPONSE_BODY)).thenReturn(response);
        List<String> deserializedResponse = notificationExtension.getNotificationsOfInterestFor(NotificationExtensionTestBase.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.NOTIFICATION_EXTENSION, apiVersion(), REQUEST_NOTIFICATIONS_INTERESTED_IN, null);
        Mockito.verify(jsonMessageHandler()).responseMessageForNotificationsInterestedIn(NotificationExtensionTestBase.RESPONSE_BODY);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToNotify() throws Exception {
        Result response = new Result();
        String notificationName = "notification-name";
        String jsonResponse = "json-response";
        StageNotificationData stageNotificationData = new StageNotificationData(new Stage(), BuildCause.createWithEmptyModifications(), "group");
        Mockito.when(jsonMessageHandler().requestMessageForNotify(stageNotificationData)).thenReturn(jsonResponse);
        Mockito.when(jsonMessageHandler().responseMessageForNotify(NotificationExtensionTestBase.RESPONSE_BODY)).thenReturn(response);
        Result deserializedResponse = notificationExtension.notify(NotificationExtensionTestBase.PLUGIN_ID, notificationName, stageNotificationData);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.NOTIFICATION_EXTENSION, apiVersion(), notificationName, jsonResponse);
        Mockito.verify(jsonMessageHandler()).responseMessageForNotify(NotificationExtensionTestBase.RESPONSE_BODY);
        Assert.assertSame(response, deserializedResponse);
    }
}


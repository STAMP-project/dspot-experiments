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


import PluginSettingsConstants.REQUEST_PLUGIN_SETTINGS_CONFIGURATION;
import PluginSettingsConstants.REQUEST_PLUGIN_SETTINGS_VIEW;
import PluginSettingsConstants.REQUEST_VALIDATE_PLUGIN_SETTINGS;
import SCMExtension.REQUEST_CHECKOUT;
import SCMExtension.REQUEST_CHECK_SCM_CONNECTION;
import SCMExtension.REQUEST_LATEST_REVISION;
import SCMExtension.REQUEST_LATEST_REVISIONS_SINCE;
import SCMExtension.REQUEST_SCM_CONFIGURATION;
import SCMExtension.REQUEST_SCM_VIEW;
import SCMExtension.REQUEST_VALIDATE_SCM_CONFIGURATION;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.common.AbstractExtension;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsConfiguration;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsJsonMessageHandler1_0;
import com.thoughtworks.go.plugin.access.scm.material.MaterialPollResult;
import com.thoughtworks.go.plugin.access.scm.revision.SCMRevision;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class SCMExtensionTest {
    public static final String PLUGIN_ID = "plugin-id";

    @Mock
    private PluginManager pluginManager;

    @Mock
    private ExtensionsRegistry extensionsRegistry;

    @Mock
    private PluginSettingsJsonMessageHandler1_0 pluginSettingsJSONMessageHandler;

    @Mock
    private JsonMessageHandler1_0 jsonMessageHandler;

    private SCMExtension scmExtension;

    private String requestBody = "expected-request";

    private String responseBody = "expected-response";

    private PluginSettingsConfiguration pluginSettingsConfiguration;

    private SCMPropertyConfiguration scmPropertyConfiguration;

    private Map<String, String> materialData;

    private ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldExtendAbstractExtension() throws Exception {
        Assert.assertTrue(((scmExtension) instanceof AbstractExtension));
    }

    @Test
    public void shouldTalkToPluginToGetPluginSettingsConfiguration() throws Exception {
        PluginSettingsConfiguration deserializedResponse = new PluginSettingsConfiguration();
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsConfiguration(responseBody)).thenReturn(deserializedResponse);
        PluginSettingsConfiguration response = scmExtension.getPluginSettingsConfiguration(SCMExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_PLUGIN_SETTINGS_CONFIGURATION, null);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsConfiguration(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetPluginSettingsView() throws Exception {
        String deserializedResponse = "";
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsView(responseBody)).thenReturn(deserializedResponse);
        String response = scmExtension.getPluginSettingsView(SCMExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_PLUGIN_SETTINGS_VIEW, null);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsView(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetValidatePluginSettings() throws Exception {
        Mockito.when(pluginSettingsJSONMessageHandler.requestMessageForPluginSettingsValidation(pluginSettingsConfiguration)).thenReturn(requestBody);
        ValidationResult deserializedResponse = new ValidationResult();
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsValidation(responseBody)).thenReturn(deserializedResponse);
        ValidationResult response = scmExtension.validatePluginSettings(SCMExtensionTest.PLUGIN_ID, pluginSettingsConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_VALIDATE_PLUGIN_SETTINGS, requestBody);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsValidation(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetSCMConfiguration() throws Exception {
        SCMPropertyConfiguration deserializedResponse = new SCMPropertyConfiguration();
        Mockito.when(jsonMessageHandler.responseMessageForSCMConfiguration(responseBody)).thenReturn(deserializedResponse);
        SCMPropertyConfiguration response = scmExtension.getSCMConfiguration(SCMExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_SCM_CONFIGURATION, null);
        Mockito.verify(jsonMessageHandler).responseMessageForSCMConfiguration(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetSCMView() throws Exception {
        SCMView deserializedResponse = new SCMView() {
            @Override
            public String displayValue() {
                return null;
            }

            @Override
            public String template() {
                return null;
            }
        };
        Mockito.when(jsonMessageHandler.responseMessageForSCMView(responseBody)).thenReturn(deserializedResponse);
        SCMView response = scmExtension.getSCMView(SCMExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_SCM_VIEW, null);
        Mockito.verify(jsonMessageHandler).responseMessageForSCMView(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToCheckIfSCMConfigurationIsValid() throws Exception {
        Mockito.when(jsonMessageHandler.requestMessageForIsSCMConfigurationValid(scmPropertyConfiguration)).thenReturn(requestBody);
        ValidationResult deserializedResponse = new ValidationResult();
        Mockito.when(jsonMessageHandler.responseMessageForIsSCMConfigurationValid(responseBody)).thenReturn(deserializedResponse);
        ValidationResult response = scmExtension.isSCMConfigurationValid(SCMExtensionTest.PLUGIN_ID, scmPropertyConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_VALIDATE_SCM_CONFIGURATION, requestBody);
        Mockito.verify(jsonMessageHandler).requestMessageForIsSCMConfigurationValid(scmPropertyConfiguration);
        Mockito.verify(jsonMessageHandler).responseMessageForIsSCMConfigurationValid(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToCheckSCMConnectionSuccessful() throws Exception {
        Mockito.when(jsonMessageHandler.requestMessageForCheckConnectionToSCM(scmPropertyConfiguration)).thenReturn(requestBody);
        Result deserializedResponse = new Result();
        Mockito.when(jsonMessageHandler.responseMessageForCheckConnectionToSCM(responseBody)).thenReturn(deserializedResponse);
        Result response = scmExtension.checkConnectionToSCM(SCMExtensionTest.PLUGIN_ID, scmPropertyConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_CHECK_SCM_CONNECTION, requestBody);
        Mockito.verify(jsonMessageHandler).requestMessageForCheckConnectionToSCM(scmPropertyConfiguration);
        Mockito.verify(jsonMessageHandler).responseMessageForCheckConnectionToSCM(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetLatestModification() throws Exception {
        String flyweight = "flyweight";
        Mockito.when(jsonMessageHandler.requestMessageForLatestRevision(scmPropertyConfiguration, materialData, flyweight)).thenReturn(requestBody);
        MaterialPollResult deserializedResponse = new MaterialPollResult();
        Mockito.when(jsonMessageHandler.responseMessageForLatestRevision(responseBody)).thenReturn(deserializedResponse);
        MaterialPollResult response = scmExtension.getLatestRevision(SCMExtensionTest.PLUGIN_ID, scmPropertyConfiguration, materialData, flyweight);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_LATEST_REVISION, requestBody);
        Mockito.verify(jsonMessageHandler).requestMessageForLatestRevision(scmPropertyConfiguration, materialData, flyweight);
        Mockito.verify(jsonMessageHandler).responseMessageForLatestRevision(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetLatestModificationSinceLastRevision() throws Exception {
        String flyweight = "flyweight";
        SCMRevision previouslyKnownRevision = new SCMRevision();
        Mockito.when(jsonMessageHandler.requestMessageForLatestRevisionsSince(scmPropertyConfiguration, materialData, flyweight, previouslyKnownRevision)).thenReturn(requestBody);
        MaterialPollResult deserializedResponse = new MaterialPollResult();
        Mockito.when(jsonMessageHandler.responseMessageForLatestRevisionsSince(responseBody)).thenReturn(deserializedResponse);
        MaterialPollResult response = scmExtension.latestModificationSince(SCMExtensionTest.PLUGIN_ID, scmPropertyConfiguration, materialData, flyweight, previouslyKnownRevision);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_LATEST_REVISIONS_SINCE, requestBody);
        Mockito.verify(jsonMessageHandler).requestMessageForLatestRevisionsSince(scmPropertyConfiguration, materialData, flyweight, previouslyKnownRevision);
        Mockito.verify(jsonMessageHandler).responseMessageForLatestRevisionsSince(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToCheckout() throws Exception {
        String destination = "destination";
        SCMRevision revision = new SCMRevision();
        Mockito.when(jsonMessageHandler.requestMessageForCheckout(scmPropertyConfiguration, destination, revision)).thenReturn(requestBody);
        Result deserializedResponse = new Result();
        Mockito.when(jsonMessageHandler.responseMessageForCheckout(responseBody)).thenReturn(deserializedResponse);
        Result response = scmExtension.checkout(SCMExtensionTest.PLUGIN_ID, scmPropertyConfiguration, destination, revision);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.SCM_EXTENSION, "1.0", REQUEST_CHECKOUT, requestBody);
        Mockito.verify(jsonMessageHandler).requestMessageForCheckout(scmPropertyConfiguration, destination, revision);
        Mockito.verify(jsonMessageHandler).responseMessageForCheckout(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldHandleExceptionDuringPluginInteraction() throws Exception {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(SCMExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.SCM_EXTENSION), requestArgumentCaptor.capture())).thenThrow(new RuntimeException("exception-from-plugin"));
        try {
            scmExtension.checkConnectionToSCM(SCMExtensionTest.PLUGIN_ID, scmPropertyConfiguration);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Interaction with plugin with id 'plugin-id' implementing 'scm' extension failed while requesting for 'check-scm-connection'. Reason: [exception-from-plugin]"));
        }
    }
}


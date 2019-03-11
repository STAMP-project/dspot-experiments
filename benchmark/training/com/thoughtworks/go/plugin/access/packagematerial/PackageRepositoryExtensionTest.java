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
package com.thoughtworks.go.plugin.access.packagematerial;


import PackageRepositoryExtension.REQUEST_CHECK_PACKAGE_CONNECTION;
import PackageRepositoryExtension.REQUEST_CHECK_REPOSITORY_CONNECTION;
import PackageRepositoryExtension.REQUEST_LATEST_REVISION;
import PackageRepositoryExtension.REQUEST_LATEST_REVISION_SINCE;
import PackageRepositoryExtension.REQUEST_PACKAGE_CONFIGURATION;
import PackageRepositoryExtension.REQUEST_REPOSITORY_CONFIGURATION;
import PackageRepositoryExtension.REQUEST_VALIDATE_PACKAGE_CONFIGURATION;
import PackageRepositoryExtension.REQUEST_VALIDATE_REPOSITORY_CONFIGURATION;
import PluginSettingsConstants.REQUEST_PLUGIN_SETTINGS_CONFIGURATION;
import PluginSettingsConstants.REQUEST_PLUGIN_SETTINGS_VIEW;
import PluginSettingsConstants.REQUEST_VALIDATE_PLUGIN_SETTINGS;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.common.AbstractExtension;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsConfiguration;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsJsonMessageHandler1_0;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProperty;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
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


public class PackageRepositoryExtensionTest {
    public static final String PLUGIN_ID = "plugin-id";

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Mock
    private PluginManager pluginManager;

    @Mock
    private ExtensionsRegistry extensionsRegistry;

    @Mock
    private PluginSettingsJsonMessageHandler1_0 pluginSettingsJSONMessageHandler;

    @Mock
    private JsonMessageHandler1_0 jsonMessageHandler;

    private PackageRepositoryExtension extension;

    private PluginSettingsConfiguration pluginSettingsConfiguration;

    private RepositoryConfiguration repositoryConfiguration;

    private PackageConfiguration packageConfiguration;

    private ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldExtendAbstractExtension() throws Exception {
        Assert.assertTrue(((extension) instanceof AbstractExtension));
    }

    @Test
    public void shouldTalkToPluginToGetPluginSettingsConfiguration() throws Exception {
        extension.registerHandler("1.0", pluginSettingsJSONMessageHandler);
        extension.messageHandlerMap.put("1.0", jsonMessageHandler);
        String responseBody = "expected-response";
        PluginSettingsConfiguration deserializedResponse = new PluginSettingsConfiguration();
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsConfiguration(responseBody)).thenReturn(deserializedResponse);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        PluginSettingsConfiguration response = extension.getPluginSettingsConfiguration(PackageRepositoryExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_PLUGIN_SETTINGS_CONFIGURATION, null);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsConfiguration(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetPluginSettingsView() throws Exception {
        extension.registerHandler("1.0", pluginSettingsJSONMessageHandler);
        extension.messageHandlerMap.put("1.0", jsonMessageHandler);
        String responseBody = "expected-response";
        String deserializedResponse = "";
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsView(responseBody)).thenReturn(deserializedResponse);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        String response = extension.getPluginSettingsView(PackageRepositoryExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_PLUGIN_SETTINGS_VIEW, null);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsView(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToValidatePluginSettings() throws Exception {
        extension.registerHandler("1.0", pluginSettingsJSONMessageHandler);
        extension.messageHandlerMap.put("1.0", jsonMessageHandler);
        String requestBody = "expected-request";
        Mockito.when(pluginSettingsJSONMessageHandler.requestMessageForPluginSettingsValidation(pluginSettingsConfiguration)).thenReturn(requestBody);
        String responseBody = "expected-response";
        ValidationResult deserializedResponse = new ValidationResult();
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsValidation(responseBody)).thenReturn(deserializedResponse);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        ValidationResult response = extension.validatePluginSettings(PackageRepositoryExtensionTest.PLUGIN_ID, pluginSettingsConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_VALIDATE_PLUGIN_SETTINGS, requestBody);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsValidation(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetRepositoryConfiguration() throws Exception {
        String expectedRequestBody = null;
        String expectedResponseBody = "{" + ((("\"key-one\":{}," + "\"key-two\":{\"default-value\":\"two\",\"part-of-identity\":true,\"secure\":true,\"required\":true,\"display-name\":\"display-two\",\"display-order\":\"1\"},") + "\"key-three\":{\"default-value\":\"three\",\"part-of-identity\":false,\"secure\":false,\"required\":false,\"display-name\":\"display-three\",\"display-order\":\"2\"}") + "}");
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        RepositoryConfiguration repositoryConfiguration = extension.getRepositoryConfiguration(PackageRepositoryExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_REPOSITORY_CONFIGURATION, expectedRequestBody);
        assertPropertyConfiguration(((PackageMaterialProperty) (repositoryConfiguration.get("key-one"))), "key-one", null, true, true, false, "", 0);
        assertPropertyConfiguration(((PackageMaterialProperty) (repositoryConfiguration.get("key-two"))), "key-two", "two", true, true, true, "display-two", 1);
        assertPropertyConfiguration(((PackageMaterialProperty) (repositoryConfiguration.get("key-three"))), "key-three", "three", false, false, false, "display-three", 2);
    }

    @Test
    public void shouldTalkToPluginToGetPackageConfiguration() throws Exception {
        String expectedRequestBody = null;
        String expectedResponseBody = "{" + ((("\"key-one\":{}," + "\"key-two\":{\"default-value\":\"two\",\"part-of-identity\":true,\"secure\":true,\"required\":true,\"display-name\":\"display-two\",\"display-order\":\"1\"},") + "\"key-three\":{\"default-value\":\"three\",\"part-of-identity\":false,\"secure\":false,\"required\":false,\"display-name\":\"display-three\",\"display-order\":\"2\"}") + "}");
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        PackageConfiguration packageConfiguration = extension.getPackageConfiguration(PackageRepositoryExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_PACKAGE_CONFIGURATION, expectedRequestBody);
        assertPropertyConfiguration(((PackageMaterialProperty) (packageConfiguration.get("key-one"))), "key-one", null, true, true, false, "", 0);
        assertPropertyConfiguration(((PackageMaterialProperty) (packageConfiguration.get("key-two"))), "key-two", "two", true, true, true, "display-two", 1);
        assertPropertyConfiguration(((PackageMaterialProperty) (packageConfiguration.get("key-three"))), "key-three", "three", false, false, false, "display-three", 2);
    }

    @Test
    public void shouldTalkToPluginToCheckIfRepositoryConfigurationIsValid() throws Exception {
        String expectedRequestBody = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}";
        String expectedResponseBody = "[{\"key\":\"key-one\",\"message\":\"incorrect value\"},{\"message\":\"general error\"}]";
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        ValidationResult validationResult = extension.isRepositoryConfigurationValid(PackageRepositoryExtensionTest.PLUGIN_ID, repositoryConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_VALIDATE_REPOSITORY_CONFIGURATION, expectedRequestBody);
        assertValidationError(validationResult.getErrors().get(0), "key-one", "incorrect value");
        assertValidationError(validationResult.getErrors().get(1), "", "general error");
    }

    @Test
    public void shouldTalkToPluginToCheckIfPackageConfigurationIsValid() throws Exception {
        String expectedRequestBody = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}," + "\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}}";
        String expectedResponseBody = "[{\"key\":\"key-one\",\"message\":\"incorrect value\"},{\"message\":\"general error\"}]";
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        ValidationResult validationResult = extension.isPackageConfigurationValid(PackageRepositoryExtensionTest.PLUGIN_ID, packageConfiguration, repositoryConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_VALIDATE_PACKAGE_CONFIGURATION, expectedRequestBody);
        assertValidationError(validationResult.getErrors().get(0), "key-one", "incorrect value");
        assertValidationError(validationResult.getErrors().get(1), "", "general error");
    }

    @Test
    public void shouldTalkToPluginToGetLatestModification() throws Exception {
        String expectedRequestBody = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}," + "\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}}";
        String expectedResponseBody = "{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"user\":\"some-user\",\"revisionComment\":\"comment\"," + "\"trackbackUrl\":\"http:\\\\localhost:9999\",\"data\":{\"dataKeyOne\":\"data-value-one\",\"dataKeyTwo\":\"data-value-two\"}}";
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        PackageRevision packageRevision = extension.getLatestRevision(PackageRepositoryExtensionTest.PLUGIN_ID, packageConfiguration, repositoryConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_LATEST_REVISION, expectedRequestBody);
        assertPackageRevision(packageRevision, "abc.rpm", "some-user", "2011-07-14T19:43:37.100Z", "comment", "http:\\localhost:9999");
    }

    @Test
    public void shouldTalkToPluginToGetLatestModificationSinceLastRevision() throws Exception {
        String expectedRequestBody = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}," + ("\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}," + "\"previous-revision\":{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-13T19:43:37.100Z\",\"data\":{\"dataKeyOne\":\"data-value-one\",\"dataKeyTwo\":\"data-value-two\"}}}");
        String expectedResponseBody = "{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"user\":\"some-user\",\"revisionComment\":\"comment\"," + "\"trackbackUrl\":\"http:\\\\localhost:9999\",\"data\":{\"dataKeyOne\":\"data-value-one\",\"dataKeyTwo\":\"data-value-two\"}}";
        Date timestamp = new SimpleDateFormat(PackageRepositoryExtensionTest.DATE_FORMAT).parse("2011-07-13T19:43:37.100Z");
        Map data = new LinkedHashMap();
        data.put("dataKeyOne", "data-value-one");
        data.put("dataKeyTwo", "data-value-two");
        PackageRevision previouslyKnownRevision = new PackageRevision("abc.rpm", timestamp, "someuser", "comment", null, data);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        PackageRevision packageRevision = extension.latestModificationSince(PackageRepositoryExtensionTest.PLUGIN_ID, packageConfiguration, repositoryConfiguration, previouslyKnownRevision);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_LATEST_REVISION_SINCE, expectedRequestBody);
        assertPackageRevision(packageRevision, "abc.rpm", "some-user", "2011-07-14T19:43:37.100Z", "comment", "http:\\localhost:9999");
    }

    @Test
    public void shouldTalkToPluginToCheckRepositoryConnectionSuccessful() throws Exception {
        String expectedRequestBody = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}";
        String expectedResponseBody = "{\"status\":\"success\",messages=[\"message-one\",\"message-two\"]}";
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        Result result = extension.checkConnectionToRepository(PackageRepositoryExtensionTest.PLUGIN_ID, repositoryConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_CHECK_REPOSITORY_CONNECTION, expectedRequestBody);
        assertSuccessResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldTalkToPluginToCheckRepositoryConnectionFailure() throws Exception {
        String expectedRequestBody = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}";
        String expectedResponseBody = "{\"status\":\"failed\",messages=[\"message-one\",\"message-two\"]}";
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        Result result = extension.checkConnectionToRepository(PackageRepositoryExtensionTest.PLUGIN_ID, repositoryConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_CHECK_REPOSITORY_CONNECTION, expectedRequestBody);
        assertFailureResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldTalkToPluginToCheckPackageConnectionSuccessful() throws Exception {
        String expectedRequestBody = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}," + "\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}}";
        String expectedResponseBody = "{\"status\":\"success\",messages=[\"message-one\",\"message-two\"]}";
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        Result result = extension.checkConnectionToPackage(PackageRepositoryExtensionTest.PLUGIN_ID, packageConfiguration, repositoryConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_CHECK_PACKAGE_CONNECTION, expectedRequestBody);
        assertSuccessResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldTalkToPluginToCheckPackageConnectionFailure() throws Exception {
        String expectedRequestBody = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}," + "\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}}";
        String expectedResponseBody = "{\"status\":\"failure\",messages=[\"message-one\",\"message-two\"]}";
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(expectedResponseBody));
        Result result = extension.checkConnectionToPackage(PackageRepositoryExtensionTest.PLUGIN_ID, packageConfiguration, repositoryConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PACKAGE_MATERIAL_EXTENSION, "1.0", REQUEST_CHECK_PACKAGE_CONNECTION, expectedRequestBody);
        assertFailureResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldHandleExceptionDuringPluginInteraction() throws Exception {
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PACKAGE_MATERIAL_EXTENSION, PackageRepositoryExtensionTest.PLUGIN_ID)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(PackageRepositoryExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.PACKAGE_MATERIAL_EXTENSION), requestArgumentCaptor.capture())).thenThrow(new RuntimeException("exception-from-plugin"));
        try {
            extension.checkConnectionToPackage(PackageRepositoryExtensionTest.PLUGIN_ID, packageConfiguration, repositoryConfiguration);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Interaction with plugin with id 'plugin-id' implementing 'package-repository' extension failed while requesting for 'check-package-connection'. Reason: [exception-from-plugin]"));
        }
    }
}


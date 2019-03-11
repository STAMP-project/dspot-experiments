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
package com.thoughtworks.go.plugin.access.common.settings;


import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class PluginSettingsJsonMessageHandlerTestBase {
    protected PluginSettingsJsonMessageHandlerBase messageHandler;

    @Test
    public void shouldBuildPluginSettingsConfigurationFromResponseBody() throws Exception {
        String responseBody = "{" + ((("\"key-one\":{}," + "\"key-two\":{\"default-value\":\"two\",\"part-of-identity\":true,\"secure\":true,\"required\":true,\"display-name\":\"display-two\",\"display-order\":\"1\"},") + "\"key-three\":{\"default-value\":\"three\",\"part-of-identity\":false,\"secure\":false,\"required\":false,\"display-name\":\"display-three\",\"display-order\":\"2\"}") + "}");
        PluginSettingsConfiguration configuration = messageHandler.responseMessageForPluginSettingsConfiguration(responseBody);
        assertPropertyConfiguration(((PluginSettingsProperty) (configuration.get("key-one"))), "key-one", null, true, false, "key-one", 0);
        assertPropertyConfiguration(((PluginSettingsProperty) (configuration.get("key-two"))), "key-two", "two", true, true, "display-two", 1);
        assertPropertyConfiguration(((PluginSettingsProperty) (configuration.get("key-three"))), "key-three", "three", false, false, "display-three", 2);
    }

    @Test
    public void shouldBuildPluginSettingsViewFromResponse() {
        String jsonResponse = "{\"template\":\"<html>junk</html>\"}";
        String view = messageHandler.responseMessageForPluginSettingsView(jsonResponse);
        Assert.assertThat(view, Matchers.is("<html>junk</html>"));
    }

    @Test
    public void shouldValidateIncorrectJsonResponseForPluginSettingsConfiguration() {
        Assert.assertThat(errorMessageForPluginSettingsConfiguration(""), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration(null), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("[{\"key-one\":\"value\"},{\"key-two\":\"value\"}]"), Matchers.is("Unable to de-serialize json response. Plugin Settings Configuration should be returned as a map"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"\":{}}"), Matchers.is("Unable to de-serialize json response. Plugin Settings Configuration key cannot be empty"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":[{}]}"), Matchers.is("Unable to de-serialize json response. Plugin Settings Configuration properties for key 'key' should be represented as a Map"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"secure\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"secure\":100}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"secure\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"required\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"required\":100}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"required\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"display-name\":true}}"), Matchers.is("Unable to de-serialize json response. 'display-name' property for key 'key' should be of type string"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"display-name\":100}}"), Matchers.is("Unable to de-serialize json response. 'display-name' property for key 'key' should be of type string"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"display-order\":true}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"display-order\":10.0}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
        Assert.assertThat(errorMessageForPluginSettingsConfiguration("{\"key\":{\"display-order\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
    }

    @Test
    public void shouldValidateIncorrectJsonResponseForPluginSettingsView() {
        Assert.assertThat(errorMessageForPluginSettingsView("{\"template\":null}"), Matchers.is("Unable to de-serialize json response. Error: Plugin Settings View's 'template' is a required field."));
        Assert.assertThat(errorMessageForPluginSettingsView("{\"template\":true}"), Matchers.is("Unable to de-serialize json response. Error: Plugin Settings View's 'template' should be of type string."));
    }

    @Test
    public void shouldBuildRequestBodyForCheckSCMConfigurationValidRequest() throws Exception {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        configuration.add(new PluginSettingsProperty("key-one", "value-one"));
        configuration.add(new PluginSettingsProperty("key-two", "value-two"));
        String requestMessage = messageHandler.requestMessageForPluginSettingsValidation(configuration);
        Assert.assertThat(requestMessage, Matchers.is("{\"plugin-settings\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}"));
    }

    @Test
    public void shouldBuildValidationResultFromCheckSCMConfigurationValidResponse() throws Exception {
        String responseBody = "[{\"key\":\"key-one\",\"message\":\"incorrect value\"},{\"message\":\"general error\"}]";
        ValidationResult validationResult = messageHandler.responseMessageForPluginSettingsValidation(responseBody);
        assertValidationError(validationResult.getErrors().get(0), "key-one", "incorrect value");
        assertValidationError(validationResult.getErrors().get(1), "", "general error");
    }
}


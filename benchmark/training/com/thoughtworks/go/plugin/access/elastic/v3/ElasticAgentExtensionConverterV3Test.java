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
package com.thoughtworks.go.plugin.access.elastic.v3;


import com.google.gson.Gson;
import com.thoughtworks.go.domain.JobIdentifier;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.domain.common.Image;
import com.thoughtworks.go.plugin.domain.elastic.Capabilities;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ElasticAgentExtensionConverterV3Test {
    private JobIdentifier jobIdentifier;

    @Test
    public void shouldUnJSONizeCanHandleResponseBody() {
        Assert.assertTrue(new Gson().fromJson("true", Boolean.class));
        Assert.assertFalse(new Gson().fromJson("false", Boolean.class));
    }

    @Test
    public void shouldUnJSONizeShouldAssignWorkResponseFromBody() {
        Assert.assertTrue(new ElasticAgentExtensionConverterV3().shouldAssignWorkResponseFromBody("true"));
        Assert.assertFalse(new ElasticAgentExtensionConverterV3().shouldAssignWorkResponseFromBody("false"));
    }

    @Test
    public void shouldJSONizeCreateAgentRequestBody() throws Exception {
        Map<String, String> configuration = new HashMap<>();
        configuration.put("key1", "value1");
        configuration.put("key2", "value2");
        String json = new ElasticAgentExtensionConverterV3().createAgentRequestBody("secret-key", "prod", configuration, jobIdentifier);
        assertThatJson(json).isEqualTo(("{" + ((((((((((((((("  \"auto_register_key\":\"secret-key\"," + "  \"properties\":{") + "    \"key1\":\"value1\",") + "    \"key2\":\"value2\"") + "    },") + "  \"environment\":\"prod\",") + "  \"job_identifier\": {\n") + "    \"pipeline_name\": \"test-pipeline\",\n") + "    \"pipeline_counter\": 1,\n") + "    \"pipeline_label\": \"Test Pipeline\",\n") + "    \"stage_name\": \"test-stage\",\n") + "    \"stage_counter\": \"1\",\n") + "    \"job_name\": \"test-job\",\n") + "    \"job_id\": 100\n") + "  }\n") + "}")));
    }

    @Test
    public void shouldJSONizeShouldAssignWorkRequestBody() throws Exception {
        HashMap<String, String> configuration = new HashMap<>();
        configuration.put("property_name", "property_value");
        String actual = new ElasticAgentExtensionConverterV3().shouldAssignWorkRequestBody(elasticAgent(), "prod", configuration, jobIdentifier);
        String expected = "{" + ((((((((((((((((((("  \"environment\":\"prod\"," + "  \"agent\":{") + "    \"agent_id\":\"42\",") + "    \"agent_state\":\"Idle\",") + "    \"build_state\":\"Idle\",") + "    \"config_state\":\"Enabled\"") + "  },") + "  \"properties\":{") + "    \"property_name\":\"property_value\"") + "  },") + "  \"job_identifier\": {\n") + "    \"pipeline_name\": \"test-pipeline\",\n") + "    \"pipeline_counter\": 1,\n") + "    \"pipeline_label\": \"Test Pipeline\",\n") + "    \"stage_name\": \"test-stage\",\n") + "    \"stage_counter\": \"1\",\n") + "    \"job_name\": \"test-job\",\n") + "    \"job_id\": 100\n") + "  }\n") + "}");
        assertThatJson(expected).isEqualTo(actual);
    }

    @Test
    public void shouldJSONizeElasticAgentStatusReportRequestBodyWhenElasticAgentIdIsProvided() throws Exception {
        String elasticAgentId = "my-fancy-elastic-agent-id";
        String actual = new ElasticAgentExtensionConverterV3().getAgentStatusReportRequestBody(null, elasticAgentId);
        String expected = String.format(("{" + ("  \"elastic_agent_id\": \"%s\"" + "}")), elasticAgentId);
        assertThatJson(expected).isEqualTo(actual);
    }

    @Test
    public void shouldJSONizeElasticAgentStatusReportRequestBodyWhenJobIdentifierIsProvided() throws Exception {
        String actual = new ElasticAgentExtensionConverterV3().getAgentStatusReportRequestBody(jobIdentifier, null);
        String expected = "{" + ((((((((("  \"job_identifier\": {\n" + "    \"pipeline_name\": \"test-pipeline\",\n") + "    \"pipeline_counter\": 1,\n") + "    \"pipeline_label\": \"Test Pipeline\",\n") + "    \"stage_name\": \"test-stage\",\n") + "    \"stage_counter\": \"1\",\n") + "    \"job_name\": \"test-job\",\n") + "    \"job_id\": 100\n") + "  }\n") + "}");
        assertThatJson(expected).isEqualTo(actual);
    }

    @Test
    public void shouldConstructValidationRequest() {
        HashMap<String, String> configuration = new HashMap<>();
        configuration.put("key1", "value1");
        configuration.put("key2", "value2");
        configuration.put("key3", null);
        String requestBody = new ElasticAgentExtensionConverterV3().validateElasticProfileRequestBody(configuration);
        assertThatJson(requestBody).isEqualTo("{\"key3\":null,\"key2\":\"value2\",\"key1\":\"value1\"}");
    }

    @Test
    public void shouldHandleValidationResponse() {
        String responseBody = "[{\"key\":\"key-one\",\"message\":\"error on key one\"}, {\"key\":\"key-two\",\"message\":\"error on key two\"}]";
        ValidationResult result = new ElasticAgentExtensionConverterV3().getElasticProfileValidationResultResponseFromBody(responseBody);
        Assert.assertThat(result.isSuccessful(), Matchers.is(false));
        Assert.assertThat(result.getErrors().size(), Matchers.is(2));
        Assert.assertThat(result.getErrors().get(0).getKey(), Matchers.is("key-one"));
        Assert.assertThat(result.getErrors().get(0).getMessage(), Matchers.is("error on key one"));
        Assert.assertThat(result.getErrors().get(1).getKey(), Matchers.is("key-two"));
        Assert.assertThat(result.getErrors().get(1).getMessage(), Matchers.is("error on key two"));
    }

    @Test
    public void shouldUnJSONizeGetProfileViewResponseFromBody() {
        String template = new ElasticAgentExtensionConverterV3().getProfileViewResponseFromBody("{\"template\":\"foo\"}");
        Assert.assertThat(template, Matchers.is("foo"));
    }

    @Test
    public void shouldUnJSONizeGetImageResponseFromBody() {
        Image image = new ElasticAgentExtensionConverterV3().getImageResponseFromBody("{\"content_type\":\"foo\", \"data\":\"bar\"}");
        Assert.assertThat(image.getContentType(), Matchers.is("foo"));
        Assert.assertThat(image.getData(), Matchers.is("bar"));
    }

    @Test
    public void shouldGetStatusReportViewFromResponseBody() {
        String template = new ElasticAgentExtensionConverterV3().getStatusReportView("{\"view\":\"foo\"}");
        Assert.assertThat(template, Matchers.is("foo"));
    }

    @Test
    public void shouldGetCapabilitiesFromResponseBody() {
        String responseBody = "{\"supports_status_report\":\"true\",\"supports_agent_status_report\":\"true\"}";
        Capabilities capabilities = new ElasticAgentExtensionConverterV3().getCapabilitiesFromResponseBody(responseBody);
        Assert.assertTrue(capabilities.supportsStatusReport());
        Assert.assertTrue(capabilities.supportsAgentStatusReport());
    }
}


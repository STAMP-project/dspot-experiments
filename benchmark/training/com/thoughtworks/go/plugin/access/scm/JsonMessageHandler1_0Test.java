/**
 * Copyright 2017 ThoughtWorks, Inc.
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


import com.thoughtworks.go.plugin.access.scm.material.MaterialPollResult;
import com.thoughtworks.go.plugin.access.scm.revision.ModifiedAction;
import com.thoughtworks.go.plugin.access.scm.revision.SCMRevision;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonMessageHandler1_0Test {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private JsonMessageHandler1_0 messageHandler;

    private SCMPropertyConfiguration scmPropertyConfiguration;

    private Map<String, String> materialData;

    @Test
    public void shouldBuildSCMConfigurationFromResponseBody() throws Exception {
        String responseBody = "{" + ((("\"key-one\":{}," + "\"key-two\":{\"default-value\":\"two\",\"part-of-identity\":true,\"secure\":true,\"required\":true,\"display-name\":\"display-two\",\"display-order\":\"1\"},") + "\"key-three\":{\"default-value\":\"three\",\"part-of-identity\":false,\"secure\":false,\"required\":false,\"display-name\":\"display-three\",\"display-order\":\"2\"}") + "}");
        SCMPropertyConfiguration scmConfiguration = messageHandler.responseMessageForSCMConfiguration(responseBody);
        assertPropertyConfiguration(((SCMProperty) (scmConfiguration.get("key-one"))), "key-one", null, true, true, false, "", 0);
        assertPropertyConfiguration(((SCMProperty) (scmConfiguration.get("key-two"))), "key-two", "two", true, true, true, "display-two", 1);
        assertPropertyConfiguration(((SCMProperty) (scmConfiguration.get("key-three"))), "key-three", "three", false, false, false, "display-three", 2);
    }

    @Test
    public void shouldBuildSCMViewFromResponse() {
        String jsonResponse = "{\"displayValue\":\"MySCMPlugin\", \"template\":\"<html>junk</html>\"}";
        SCMView view = messageHandler.responseMessageForSCMView(jsonResponse);
        Assert.assertThat(view.displayValue(), Matchers.is("MySCMPlugin"));
        Assert.assertThat(view.template(), Matchers.is("<html>junk</html>"));
    }

    @Test
    public void shouldBuildRequestBodyForCheckSCMConfigurationValidRequest() throws Exception {
        String requestMessage = messageHandler.requestMessageForIsSCMConfigurationValid(scmPropertyConfiguration);
        Assert.assertThat(requestMessage, Matchers.is("{\"scm-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}"));
    }

    @Test
    public void shouldBuildValidationResultFromCheckSCMConfigurationValidResponse() throws Exception {
        String responseBody = "[{\"key\":\"key-one\",\"message\":\"incorrect value\"},{\"message\":\"general error\"}]";
        ValidationResult validationResult = messageHandler.responseMessageForIsSCMConfigurationValid(responseBody);
        assertValidationError(validationResult.getErrors().get(0), "key-one", "incorrect value");
        assertValidationError(validationResult.getErrors().get(1), "", "general error");
    }

    @Test
    public void shouldBuildSuccessValidationResultFromCheckSCMConfigurationValidResponse() throws Exception {
        Assert.assertThat(messageHandler.responseMessageForIsSCMConfigurationValid("").isSuccessful(), Matchers.is(true));
        Assert.assertThat(messageHandler.responseMessageForIsSCMConfigurationValid(null).isSuccessful(), Matchers.is(true));
    }

    @Test
    public void shouldBuildRequestBodyForCheckSCMConnectionRequest() throws Exception {
        String requestMessage = messageHandler.requestMessageForCheckConnectionToSCM(scmPropertyConfiguration);
        Assert.assertThat(requestMessage, Matchers.is("{\"scm-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}"));
    }

    @Test
    public void shouldBuildSuccessResultFromCheckSCMConnectionResponse() throws Exception {
        String responseBody = "{\"status\":\"success\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForCheckConnectionToSCM(responseBody);
        assertSuccessResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldBuildFailureResultFromCheckSCMConnectionResponse() throws Exception {
        String responseBody = "{\"status\":\"failure\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForCheckConnectionToSCM(responseBody);
        assertFailureResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldHandleNullMessagesForCheckSCMConnectionResponse() throws Exception {
        assertSuccessResult(messageHandler.responseMessageForCheckConnectionToSCM("{\"status\":\"success\"}"), new ArrayList());
        assertFailureResult(messageHandler.responseMessageForCheckConnectionToSCM("{\"status\":\"failure\"}"), new ArrayList());
    }

    @Test
    public void shouldBuildRequestBodyForLatestRevisionRequest() throws Exception {
        String requestBody = messageHandler.requestMessageForLatestRevision(scmPropertyConfiguration, materialData, "flyweight");
        Assert.assertThat(requestBody, Matchers.is("{\"scm-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}},\"scm-data\":{\"key-one\":\"value-one\"},\"flyweight-folder\":\"flyweight\"}"));
    }

    @Test
    public void shouldBuildSCMRevisionFromLatestRevisionResponse() throws Exception {
        String revisionJSON = "{\"revision\":\"r1\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"user\":\"some-user\",\"revisionComment\":\"comment\",\"data\":{\"dataKeyTwo\":\"data-value-two\",\"dataKeyOne\":\"data-value-one\"}," + "\"modifiedFiles\":[{\"fileName\":\"f1\",\"action\":\"added\"},{\"fileName\":\"f2\",\"action\":\"modified\"},{\"fileName\":\"f3\",\"action\":\"deleted\"}]}";
        String responseBody = ("{\"revision\": " + revisionJSON) + "}";
        MaterialPollResult pollResult = messageHandler.responseMessageForLatestRevision(responseBody);
        Assert.assertThat(pollResult.getMaterialData(), Matchers.is(Matchers.nullValue()));
        assertSCMRevision(pollResult.getLatestRevision(), "r1", "some-user", "2011-07-14T19:43:37.100Z", "comment", Arrays.asList(new com.thoughtworks.go.plugin.access.scm.revision.ModifiedFile("f1", ModifiedAction.added), new com.thoughtworks.go.plugin.access.scm.revision.ModifiedFile("f2", ModifiedAction.modified), new com.thoughtworks.go.plugin.access.scm.revision.ModifiedFile("f3", ModifiedAction.deleted)));
    }

    @Test
    public void shouldBuildSCMDataFromLatestRevisionResponse() throws Exception {
        String responseBodyWithSCMData = "{\"revision\":{\"revision\":\"r1\",\"timestamp\":\"2011-07-14T19:43:37.100Z\"},\"scm-data\":{\"key-one\":\"value-one\"}}";
        MaterialPollResult pollResult = messageHandler.responseMessageForLatestRevision(responseBodyWithSCMData);
        Map<String, String> scmData = new HashMap<>();
        scmData.put("key-one", "value-one");
        Assert.assertThat(pollResult.getMaterialData(), Matchers.is(scmData));
        Assert.assertThat(pollResult.getRevisions().get(0).getRevision(), Matchers.is("r1"));
    }

    @Test
    public void shouldBuildRequestBodyForLatestRevisionsSinceRequest() throws Exception {
        Date timestamp = new SimpleDateFormat(JsonMessageHandler1_0Test.DATE_FORMAT).parse("2011-07-13T19:43:37.100Z");
        Map data = new LinkedHashMap();
        data.put("dataKeyOne", "data-value-one");
        data.put("dataKeyTwo", "data-value-two");
        SCMRevision previouslyKnownRevision = new SCMRevision("abc.rpm", timestamp, "someuser", "comment", data, null);
        String requestBody = messageHandler.requestMessageForLatestRevisionsSince(scmPropertyConfiguration, materialData, "flyweight", previouslyKnownRevision);
        String expectedValue = "{\"scm-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}},\"scm-data\":{\"key-one\":\"value-one\"},\"flyweight-folder\":\"flyweight\"," + "\"previous-revision\":{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-13T19:43:37.100Z\",\"data\":{\"dataKeyOne\":\"data-value-one\",\"dataKeyTwo\":\"data-value-two\"}}}";
        Assert.assertThat(requestBody, Matchers.is(expectedValue));
    }

    @Test
    public void shouldBuildSCMRevisionsFromLatestRevisionsSinceResponse() throws Exception {
        String r1 = "{\"revision\":\"r1\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"user\":\"some-user\",\"revisionComment\":\"comment\",\"data\":{\"dataKeyTwo\":\"data-value-two\",\"dataKeyOne\":\"data-value-one\"}," + "\"modifiedFiles\":[{\"fileName\":\"f1\",\"action\":\"added\"},{\"fileName\":\"f2\",\"action\":\"modified\"},{\"fileName\":\"f3\",\"action\":\"deleted\"}]}";
        String r2 = "{\"revision\":\"r2\",\"timestamp\":\"2011-07-14T19:43:37.101Z\",\"user\":\"new-user\",\"revisionComment\":\"comment\",\"data\":{\"dataKeyTwo\":\"data-value-two\",\"dataKeyOne\":\"data-value-one\"}," + "\"modifiedFiles\":[{\"fileName\":\"f1\",\"action\":\"added\"}]}";
        String responseBody = ((("{\"revisions\":[" + r1) + ",") + r2) + "]}";
        MaterialPollResult pollResult = messageHandler.responseMessageForLatestRevisionsSince(responseBody);
        Assert.assertThat(pollResult.getMaterialData(), Matchers.is(Matchers.nullValue()));
        List<SCMRevision> scmRevisions = pollResult.getRevisions();
        Assert.assertThat(scmRevisions.size(), Matchers.is(2));
        assertSCMRevision(scmRevisions.get(0), "r1", "some-user", "2011-07-14T19:43:37.100Z", "comment", Arrays.asList(new com.thoughtworks.go.plugin.access.scm.revision.ModifiedFile("f1", ModifiedAction.added), new com.thoughtworks.go.plugin.access.scm.revision.ModifiedFile("f2", ModifiedAction.modified), new com.thoughtworks.go.plugin.access.scm.revision.ModifiedFile("f3", ModifiedAction.deleted)));
        assertSCMRevision(scmRevisions.get(1), "r2", "new-user", "2011-07-14T19:43:37.101Z", "comment", Arrays.asList(new com.thoughtworks.go.plugin.access.scm.revision.ModifiedFile("f1", ModifiedAction.added)));
    }

    @Test
    public void shouldBuildSCMDataFromLatestRevisionsSinceResponse() throws Exception {
        String responseBodyWithSCMData = "{\"revisions\":[],\"scm-data\":{\"key-one\":\"value-one\"}}";
        MaterialPollResult pollResult = messageHandler.responseMessageForLatestRevisionsSince(responseBodyWithSCMData);
        Map<String, String> scmData = new HashMap<>();
        scmData.put("key-one", "value-one");
        Assert.assertThat(pollResult.getMaterialData(), Matchers.is(scmData));
        Assert.assertThat(pollResult.getRevisions().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldBuildNullSCMRevisionFromLatestRevisionsSinceWhenEmptyResponse() throws Exception {
        MaterialPollResult pollResult = messageHandler.responseMessageForLatestRevisionsSince("");
        Assert.assertThat(pollResult.getRevisions(), Matchers.nullValue());
        Assert.assertThat(pollResult.getMaterialData(), Matchers.nullValue());
        pollResult = messageHandler.responseMessageForLatestRevisionsSince(null);
        Assert.assertThat(pollResult.getRevisions(), Matchers.nullValue());
        Assert.assertThat(pollResult.getMaterialData(), Matchers.nullValue());
    }

    @Test
    public void shouldBuildRequestBodyForCheckoutRequest() throws Exception {
        Date timestamp = new SimpleDateFormat(JsonMessageHandler1_0Test.DATE_FORMAT).parse("2011-07-13T19:43:37.100Z");
        Map data = new LinkedHashMap();
        data.put("dataKeyOne", "data-value-one");
        data.put("dataKeyTwo", "data-value-two");
        SCMRevision revision = new SCMRevision("abc.rpm", timestamp, "someuser", "comment", data, null);
        String requestBody = messageHandler.requestMessageForCheckout(scmPropertyConfiguration, "destination", revision);
        String expectedValue = "{\"scm-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}},\"destination-folder\":\"destination\"," + "\"revision\":{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-13T19:43:37.100Z\",\"data\":{\"dataKeyOne\":\"data-value-one\",\"dataKeyTwo\":\"data-value-two\"}}}";
        Assert.assertThat(requestBody, Matchers.is(expectedValue));
    }

    @Test
    public void shouldBuildResultFromCheckoutResponse() throws Exception {
        String responseBody = "{\"status\":\"failure\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForCheckout(responseBody);
        assertFailureResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldValidateIncorrectJsonResponseForSCMConfiguration() {
        Assert.assertThat(errorMessageForSCMConfiguration(""), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForSCMConfiguration(null), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForSCMConfiguration("[{\"key-one\":\"value\"},{\"key-two\":\"value\"}]"), Matchers.is("Unable to de-serialize json response. SCM configuration should be returned as a map"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"\":{}}"), Matchers.is("Unable to de-serialize json response. SCM configuration key cannot be empty"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":[{}]}"), Matchers.is("Unable to de-serialize json response. SCM configuration properties for key 'key' should be represented as a Map"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"part-of-identity\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"part-of-identity\":100}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"part-of-identity\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"secure\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"secure\":100}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"secure\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"required\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"required\":100}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"required\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"display-name\":true}}"), Matchers.is("Unable to de-serialize json response. 'display-name' property for key 'key' should be of type string"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"display-name\":100}}"), Matchers.is("Unable to de-serialize json response. 'display-name' property for key 'key' should be of type string"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"display-order\":true}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"display-order\":10.0}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
        Assert.assertThat(errorMessageForSCMConfiguration("{\"key\":{\"display-order\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
    }

    @Test
    public void shouldValidateIncorrectJsonResponseForSCMView() {
        Assert.assertThat(errorMessageForSCMView("{\"template\":\"<html>junk</html>\"}"), Matchers.is("Unable to de-serialize json response. Error: SCM View's 'displayValue' is a required field."));
        Assert.assertThat(errorMessageForSCMView("{\"displayValue\":\"MySCMPlugin\"}"), Matchers.is("Unable to de-serialize json response. Error: SCM View's 'template' is a required field."));
        Assert.assertThat(errorMessageForSCMView("{\"displayValue\":null, \"template\":\"<html>junk</html>\"}"), Matchers.is("Unable to de-serialize json response. Error: SCM View's 'displayValue' is a required field."));
        Assert.assertThat(errorMessageForSCMView("{\"displayValue\":true, \"template\":null}"), Matchers.is("Unable to de-serialize json response. Error: SCM View's 'displayValue' should be of type string."));
        Assert.assertThat(errorMessageForSCMView("{\"displayValue\":\"MySCMPlugin\", \"template\":true}"), Matchers.is("Unable to de-serialize json response. Error: SCM View's 'template' should be of type string."));
    }

    @Test
    public void shouldValidateIncorrectJsonForSCMRevisions() {
        Assert.assertThat(errorMessageForSCMRevisions("{\"revisions\":{}}"), Matchers.is("Unable to de-serialize json response. 'revisions' should be of type list of map"));
        Assert.assertThat(errorMessageForSCMRevisions("{\"revisions\":[\"crap\"]}"), Matchers.is("Unable to de-serialize json response. SCM revision should be of type map"));
    }

    @Test
    public void shouldValidateIncorrectJsonForSCMRevision() {
        Assert.assertThat(errorMessageForSCMRevision(""), Matchers.is("Unable to de-serialize json response. SCM revision cannot be empty"));
        Assert.assertThat(errorMessageForSCMRevision("{\"revision\":[]}"), Matchers.is("Unable to de-serialize json response. SCM revision should be of type map"));
        Assert.assertThat(errorMessageForSCMRevision("{\"crap\":{}}"), Matchers.is("Unable to de-serialize json response. SCM revision cannot be empty"));
    }

    @Test
    public void shouldValidateIncorrectJsonForEachRevision() {
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":{}}"), Matchers.is("SCM revision should be of type string"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"\"}"), Matchers.is("SCM revision's 'revision' is a required field"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":{}}"), Matchers.is("SCM revision timestamp should be of type string with format yyyy-MM-dd'T'HH:mm:ss.SSS'Z' and cannot be empty"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"\"}"), Matchers.is("SCM revision timestamp should be of type string with format yyyy-MM-dd'T'HH:mm:ss.SSS'Z' and cannot be empty"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"12-01-2014\"}"), Matchers.is("SCM revision timestamp should be of type string with format yyyy-MM-dd'T'HH:mm:ss.SSS'Z' and cannot be empty"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"revisionComment\":{}}"), Matchers.is("SCM revision comment should be of type string"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"user\":{}}"), Matchers.is("SCM revision user should be of type string"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"modifiedFiles\":{}}"), Matchers.is("SCM revision 'modifiedFiles' should be of type list of map"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"modifiedFiles\":[\"crap\"]}"), Matchers.is("SCM revision 'modified file' should be of type map"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"modifiedFiles\":[{\"fileName\":{}}]}"), Matchers.is("modified file 'fileName' should be of type string"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"modifiedFiles\":[{\"fileName\":\"\"}]}"), Matchers.is("modified file 'fileName' is a required field"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"modifiedFiles\":[{\"fileName\":\"f1\",\"action\":{}}]}"), Matchers.is("modified file 'action' should be of type string"));
        Assert.assertThat(errorMessageForEachRevision("{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"modifiedFiles\":[{\"fileName\":\"f1\",\"action\":\"crap\"}]}"), Matchers.is("modified file 'action' can only be added, modified, deleted"));
    }

    @Test
    public void shouldValidateIncorrectJsonForSCMData() {
        Assert.assertThat(errorMessageForSCMData("{\"scm-data\":[]}"), Matchers.is("Unable to de-serialize json response. SCM data should be of type map"));
    }
}


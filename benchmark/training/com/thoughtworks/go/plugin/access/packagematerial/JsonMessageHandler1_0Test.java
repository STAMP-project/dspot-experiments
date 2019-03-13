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
package com.thoughtworks.go.plugin.access.packagematerial;


import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProperty;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonMessageHandler1_0Test {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private JsonMessageHandler1_0 messageHandler;

    private RepositoryConfiguration repositoryConfiguration;

    private PackageConfiguration packageConfiguration;

    @Test
    public void shouldBuildRepositoryConfigurationFromResponseBody() throws Exception {
        String responseBody = "{" + ((("\"key-one\":{}," + "\"key-two\":{\"default-value\":\"two\",\"part-of-identity\":true,\"secure\":true,\"required\":true,\"display-name\":\"display-two\",\"display-order\":\"1\"},") + "\"key-three\":{\"default-value\":\"three\",\"part-of-identity\":false,\"secure\":false,\"required\":false,\"display-name\":\"display-three\",\"display-order\":\"2\"}") + "}");
        RepositoryConfiguration repositoryConfiguration = messageHandler.responseMessageForRepositoryConfiguration(responseBody);
        assertPropertyConfiguration(((PackageMaterialProperty) (repositoryConfiguration.get("key-one"))), "key-one", null, true, true, false, "", 0);
        assertPropertyConfiguration(((PackageMaterialProperty) (repositoryConfiguration.get("key-two"))), "key-two", "two", true, true, true, "display-two", 1);
        assertPropertyConfiguration(((PackageMaterialProperty) (repositoryConfiguration.get("key-three"))), "key-three", "three", false, false, false, "display-three", 2);
    }

    @Test
    public void shouldBuildPackageConfigurationFromResponseBody() throws Exception {
        String responseBody = "{" + ((("\"key-one\":{}," + "\"key-two\":{\"default-value\":\"two\",\"part-of-identity\":true,\"secure\":true,\"required\":true,\"display-name\":\"display-two\",\"display-order\":\"1\"},") + "\"key-three\":{\"default-value\":\"three\",\"part-of-identity\":false,\"secure\":false,\"required\":false,\"display-name\":\"display-three\",\"display-order\":\"2\"}") + "}");
        PackageConfiguration packageConfiguration = messageHandler.responseMessageForPackageConfiguration(responseBody);
        assertPropertyConfiguration(((PackageMaterialProperty) (packageConfiguration.get("key-one"))), "key-one", null, true, true, false, "", 0);
        assertPropertyConfiguration(((PackageMaterialProperty) (packageConfiguration.get("key-two"))), "key-two", "two", true, true, true, "display-two", 1);
        assertPropertyConfiguration(((PackageMaterialProperty) (packageConfiguration.get("key-three"))), "key-three", "three", false, false, false, "display-three", 2);
    }

    @Test
    public void shouldBuildRequestBodyForCheckRepositoryConfigurationValidRequest() throws Exception {
        String requestMessage = messageHandler.requestMessageForIsRepositoryConfigurationValid(repositoryConfiguration);
        Assert.assertThat(requestMessage, Matchers.is("{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}"));
    }

    @Test
    public void shouldBuildValidationResultFromCheckRepositoryConfigurationValidResponse() throws Exception {
        String responseBody = "[{\"key\":\"key-one\",\"message\":\"incorrect value\"},{\"message\":\"general error\"}]";
        ValidationResult validationResult = messageHandler.responseMessageForIsRepositoryConfigurationValid(responseBody);
        assertValidationError(validationResult.getErrors().get(0), "key-one", "incorrect value");
        assertValidationError(validationResult.getErrors().get(1), "", "general error");
    }

    @Test
    public void shouldBuildSuccessValidationResultFromCheckRepositoryConfigurationValidResponse() throws Exception {
        Assert.assertThat(messageHandler.responseMessageForIsRepositoryConfigurationValid("").isSuccessful(), Matchers.is(true));
        Assert.assertThat(messageHandler.responseMessageForIsRepositoryConfigurationValid(null).isSuccessful(), Matchers.is(true));
    }

    @Test
    public void shouldBuildRequestBodyForCheckPackageConfigurationValidRequest() throws Exception {
        String requestMessage = messageHandler.requestMessageForIsPackageConfigurationValid(packageConfiguration, repositoryConfiguration);
        Assert.assertThat(requestMessage, Matchers.is("{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}},\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}}"));
    }

    @Test
    public void shouldBuildValidationResultForCheckRepositoryConfigurationValidResponse() throws Exception {
        String responseBody = "[{\"key\":\"key-one\",\"message\":\"incorrect value\"},{\"message\":\"general error\"}]";
        ValidationResult validationResult = messageHandler.responseMessageForIsPackageConfigurationValid(responseBody);
        assertValidationError(validationResult.getErrors().get(0), "key-one", "incorrect value");
        assertValidationError(validationResult.getErrors().get(1), "", "general error");
    }

    @Test
    public void shouldBuildRequestBodyForCheckRepositoryConnectionRequest() throws Exception {
        String requestMessage = messageHandler.requestMessageForCheckConnectionToRepository(repositoryConfiguration);
        Assert.assertThat(requestMessage, Matchers.is("{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}"));
    }

    @Test
    public void shouldBuildSuccessResultFromCheckRepositoryConnectionResponse() throws Exception {
        String responseBody = "{\"status\":\"success\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForCheckConnectionToRepository(responseBody);
        assertSuccessResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldBuildFailureResultFromCheckRepositoryConnectionResponse() throws Exception {
        String responseBody = "{\"status\":\"failure\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForCheckConnectionToRepository(responseBody);
        assertFailureResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldHandleNullMessagesForCheckRepositoryConnectionResponse() throws Exception {
        assertSuccessResult(messageHandler.responseMessageForCheckConnectionToRepository("{\"status\":\"success\"}"), new ArrayList());
        assertFailureResult(messageHandler.responseMessageForCheckConnectionToRepository("{\"status\":\"failure\"}"), new ArrayList());
    }

    @Test
    public void shouldBuildRequestBodyForCheckPackageConnectionRequest() throws Exception {
        String requestMessage = messageHandler.requestMessageForCheckConnectionToPackage(packageConfiguration, repositoryConfiguration);
        Assert.assertThat(requestMessage, Matchers.is("{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}},\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}}"));
    }

    @Test
    public void shouldBuildSuccessResultFromCheckPackageConnectionResponse() throws Exception {
        String responseBody = "{\"status\":\"success\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForCheckConnectionToPackage(responseBody);
        assertSuccessResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldBuildFailureResultFromCheckPackageConnectionResponse() throws Exception {
        String responseBody = "{\"status\":\"failure\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForCheckConnectionToPackage(responseBody);
        assertFailureResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldHandleNullMessagesForCheckPackageConnectionResponse() throws Exception {
        assertSuccessResult(messageHandler.responseMessageForCheckConnectionToPackage("{\"status\":\"success\"}"), new ArrayList());
        assertFailureResult(messageHandler.responseMessageForCheckConnectionToPackage("{\"status\":\"failure\"}"), new ArrayList());
    }

    @Test
    public void shouldBuildRequestBodyForLatestRevisionRequest() throws Exception {
        String requestBody = messageHandler.requestMessageForLatestRevision(packageConfiguration, repositoryConfiguration);
        Assert.assertThat(requestBody, Matchers.is("{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}},\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}}"));
    }

    @Test
    public void shouldBuildPackageRevisionFromLatestRevisionResponse() throws Exception {
        String responseBody = "{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"user\":\"some-user\",\"revisionComment\":\"comment\"," + "\"trackbackUrl\":\"http:\\\\localhost:9999\",\"data\":{\"dataKeyOne\":\"data-value-one\",\"dataKeyTwo\":\"data-value-two\"}}";
        PackageRevision packageRevision = messageHandler.responseMessageForLatestRevision(responseBody);
        assertPackageRevision(packageRevision, "abc.rpm", "some-user", "2011-07-14T19:43:37.100Z", "comment", "http:\\localhost:9999");
    }

    @Test
    public void shouldThrowExceptionWhenAttemptingToGetLatestRevisionFromEmptyResponse() {
        Assert.assertThat(getErrorMessageFromLatestRevision(""), Matchers.is("Empty response body"));
        Assert.assertThat(getErrorMessageFromLatestRevision("{}"), Matchers.is("Empty response body"));
        Assert.assertThat(getErrorMessageFromLatestRevision(null), Matchers.is("Empty response body"));
    }

    @Test
    public void shouldBuildRequestBodyForLatestRevisionSinceRequest() throws Exception {
        Date timestamp = new SimpleDateFormat(JsonMessageHandler1_0Test.DATE_FORMAT).parse("2011-07-13T19:43:37.100Z");
        Map data = new LinkedHashMap();
        data.put("dataKeyOne", "data-value-one");
        data.put("dataKeyTwo", "data-value-two");
        PackageRevision previouslyKnownRevision = new PackageRevision("abc.rpm", timestamp, "someuser", "comment", null, data);
        String requestBody = messageHandler.requestMessageForLatestRevisionSince(packageConfiguration, repositoryConfiguration, previouslyKnownRevision);
        String expectedValue = "{\"repository-configuration\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}," + ("\"package-configuration\":{\"key-three\":{\"value\":\"value-three\"},\"key-four\":{\"value\":\"value-four\"}}," + "\"previous-revision\":{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-13T19:43:37.100Z\",\"data\":{\"dataKeyOne\":\"data-value-one\",\"dataKeyTwo\":\"data-value-two\"}}}");
        Assert.assertThat(requestBody, Matchers.is(expectedValue));
    }

    @Test
    public void shouldBuildPackageRevisionFromLatestRevisionSinceResponse() throws Exception {
        String responseBody = "{\"revision\":\"abc.rpm\",\"timestamp\":\"2011-07-14T19:43:37.100Z\",\"user\":\"some-user\",\"revisionComment\":\"comment\"," + "\"trackbackUrl\":\"http:\\\\localhost:9999\",\"data\":{\"dataKeyOne\":\"data-value-one\",\"dataKeyTwo\":\"data-value-two\"}}";
        PackageRevision packageRevision = messageHandler.responseMessageForLatestRevisionSince(responseBody);
        assertPackageRevision(packageRevision, "abc.rpm", "some-user", "2011-07-14T19:43:37.100Z", "comment", "http:\\localhost:9999");
    }

    @Test
    public void shouldBuildNullPackageRevisionFromLatestRevisionSinceWhenEmptyResponse() throws Exception {
        Assert.assertThat(messageHandler.responseMessageForLatestRevisionSince(""), Matchers.nullValue());
        Assert.assertThat(messageHandler.responseMessageForLatestRevisionSince(null), Matchers.nullValue());
        Assert.assertThat(messageHandler.responseMessageForLatestRevisionSince("{}"), Matchers.nullValue());
    }

    @Test
    public void shouldValidateIncorrectJsonResponseForRepositoryConfiguration() {
        Assert.assertThat(errorMessageForRepositoryConfiguration(""), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForRepositoryConfiguration(null), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("[{\"key-one\":\"value\"},{\"key-two\":\"value\"}]"), Matchers.is("Unable to de-serialize json response. Repository configuration should be returned as a map"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"\":{}}"), Matchers.is("Unable to de-serialize json response. Repository configuration key cannot be empty"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":[{}]}"), Matchers.is("Unable to de-serialize json response. Repository configuration properties for key 'key' should be represented as a Map"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"part-of-identity\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"part-of-identity\":100}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"part-of-identity\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"secure\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"secure\":100}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"secure\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"required\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"required\":100}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"required\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-name\":true}}"), Matchers.is("Unable to de-serialize json response. 'display-name' property for key 'key' should be of type string"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-name\":100}}"), Matchers.is("Unable to de-serialize json response. 'display-name' property for key 'key' should be of type string"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-order\":true}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-order\":10.0}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-order\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
    }

    @Test
    public void shouldValidateIncorrectJsonResponseForPackageConfiguration() {
        Assert.assertThat(errorMessageForPackageConfiguration(""), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForPackageConfiguration(null), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForPackageConfiguration("[{\"key-one\":\"value\"},{\"key-two\":\"value\"}]"), Matchers.is("Unable to de-serialize json response. Package configuration should be returned as a map"));
        Assert.assertThat(errorMessageForPackageConfiguration("{\"\":{}}"), Matchers.is("Unable to de-serialize json response. Package configuration key cannot be empty"));
        Assert.assertThat(errorMessageForPackageConfiguration("{\"key\":[{}]}"), Matchers.is("Unable to de-serialize json response. Package configuration properties for key 'key' should be represented as a Map"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"part-of-identity\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"part-of-identity\":100}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"part-of-identity\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'part-of-identity' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"secure\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"secure\":100}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"secure\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'secure' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"required\":\"true\"}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"required\":100}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"required\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'required' property for key 'key' should be of type boolean"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-name\":true}}"), Matchers.is("Unable to de-serialize json response. 'display-name' property for key 'key' should be of type string"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-name\":100}}"), Matchers.is("Unable to de-serialize json response. 'display-name' property for key 'key' should be of type string"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-order\":true}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-order\":10.0}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
        Assert.assertThat(errorMessageForRepositoryConfiguration("{\"key\":{\"display-order\":\"\"}}"), Matchers.is("Unable to de-serialize json response. 'display-order' property for key 'key' should be of type integer"));
    }

    @Test
    public void shouldValidateIncorrectJsonForPackageRevision() {
        Assert.assertThat(errorMessageForPackageRevision("[{\"revision\":\"abc.rpm\"}]"), Matchers.is("Unable to de-serialize json response. Package revision should be returned as a map"));
        Assert.assertThat(errorMessageForPackageRevision("{\"revision\":{}}"), Matchers.is("Unable to de-serialize json response. Package revision should be of type string"));
        Assert.assertThat(errorMessageForPackageRevision("{\"revisionComment\":{}}"), Matchers.is("Unable to de-serialize json response. Package revision comment should be of type string"));
        Assert.assertThat(errorMessageForPackageRevision("{\"user\":{}}"), Matchers.is("Unable to de-serialize json response. Package revision user should be of type string"));
        Assert.assertThat(errorMessageForPackageRevision("{\"timestamp\":{}}"), Matchers.is("Unable to de-serialize json response. Package revision timestamp should be of type string with format yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        Assert.assertThat(errorMessageForPackageRevision("{\"timestamp\":\"12-01-2014\"}"), Matchers.is("Unable to de-serialize json response. Package revision timestamp should be of type string with format yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }
}


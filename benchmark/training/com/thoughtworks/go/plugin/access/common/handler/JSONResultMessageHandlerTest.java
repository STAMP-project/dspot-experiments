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
package com.thoughtworks.go.plugin.access.common.handler;


import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JSONResultMessageHandlerTest {
    private JSONResultMessageHandler messageHandler;

    @Test
    public void shouldBuildValidationResultFromResponseBody() throws Exception {
        String responseBody = "[{\"key\":\"key-one\",\"message\":\"incorrect value\"},{\"message\":\"general error\"}]";
        ValidationResult validationResult = messageHandler.toValidationResult(responseBody);
        assertValidationError(validationResult.getErrors().get(0), "key-one", "incorrect value");
        assertValidationError(validationResult.getErrors().get(1), "", "general error");
    }

    @Test
    public void shouldBuildSuccessResultFromResponseBody() throws Exception {
        String responseBody = "{\"status\":\"success\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.toResult(responseBody);
        assertSuccessResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldBuildFailureResultFromResponseBody() throws Exception {
        String responseBody = "{\"status\":\"failure\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.toResult(responseBody);
        assertFailureResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldValidateIncorrectJsonForValidationResult() {
        Assert.assertThat(errorMessageForValidationResult("{{\"key\":\"abc\",\"message\":\"msg\"}}"), Matchers.is("Unable to de-serialize json response. Validation errors should be returned as list or errors, with each error represented as a map"));
        Assert.assertThat(errorMessageForValidationResult("[[{\"key\":\"abc\",\"message\":\"msg\"}]]"), Matchers.is("Unable to de-serialize json response. Each validation error should be represented as a map"));
        Assert.assertThat(errorMessageForValidationResult("[{\"key\":true,\"message\":\"msg\"}]"), Matchers.is("Unable to de-serialize json response. Validation error key should be of type string"));
        Assert.assertThat(errorMessageForValidationResult("[{\"key\":\"abc\",\"message\":{}}]"), Matchers.is("Unable to de-serialize json response. Validation message should be of type string"));
        Assert.assertThat(errorMessageForValidationResult("[{\"key\":\"abc\",\"message\":[]}]"), Matchers.is("Unable to de-serialize json response. Validation message should be of type string"));
    }

    @Test
    public void shouldValidateIncorrectJsonForCheckConnectionResult() {
        Assert.assertThat(errorMessageForCheckConnectionResult(""), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForCheckConnectionResult("[{\"result\":\"success\"}]"), Matchers.is("Unable to de-serialize json response. Check connection result should be returned as map, with key represented as string and messages represented as list"));
        Assert.assertThat(errorMessageForCheckConnectionResult("{\"status\":true}"), Matchers.is("Unable to de-serialize json response. Check connection 'status' should be of type string"));
        Assert.assertThat(errorMessageForCheckConnectionResult("{\"result\":true}"), Matchers.is("Unable to de-serialize json response. Check connection 'status' is a required field"));
        Assert.assertThat(errorMessageForCheckConnectionResult("{\"status\":\"success\",\"messages\":[{},{}]}"), Matchers.is("Unable to de-serialize json response. Check connection 'message' should be of type string"));
    }
}


/**
 * Copyright 2019 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.access.authorization.v1;


import com.thoughtworks.go.plugin.domain.common.ValidationError;
import com.thoughtworks.go.plugin.domain.common.VerifyConnectionResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class VerifyConnectionResponseDTOTest {
    @Test
    public void shouldDeserializeSuccessResponseFromJSON() throws Exception {
        String json = "{\n" + (("  \"status\": \"success\",\n" + "  \"message\": \"Connection check passed\"\n") + "}");
        VerifyConnectionResponse response = VerifyConnectionResponseDTO.fromJSON(json).response();
        Assert.assertThat(response.getStatus(), Matchers.is("success"));
        Assert.assertThat(response.getMessage(), Matchers.is("Connection check passed"));
        Assert.assertNull(response.getValidationResult());
    }

    @Test
    public void shouldDeserializeFailureResponseFromJSON() throws Exception {
        String json = "{\n" + (("  \"status\": \"failure\",\n" + "  \"message\": \"Connection check failed\"\n") + "}");
        VerifyConnectionResponse response = VerifyConnectionResponseDTO.fromJSON(json).response();
        Assert.assertThat(response.getStatus(), Matchers.is("failure"));
        Assert.assertThat(response.getMessage(), Matchers.is("Connection check failed"));
        Assert.assertNull(response.getValidationResult());
    }

    @Test
    public void shouldDeserializeValidationFailedResponseFromJSON() throws Exception {
        String json = "{\n" + (((((((((((("  \"status\": \"validation-failed\",\n" + "  \"message\": \"Validation failed\",\n") + "  \"errors\": [\n") + "    {") + "      \"key\": \"url\",\n") + "      \"message\": \"URL cannot be blank\"\n") + "    },\n") + "    {") + "      \"key\": \"password\",\n") + "      \"message\": \"Password cannot be blank\"\n") + "    }\n") + "  ]\n") + "}");
        VerifyConnectionResponse response = VerifyConnectionResponseDTO.fromJSON(json).response();
        Assert.assertThat(response.getStatus(), Matchers.is("validation-failed"));
        Assert.assertThat(response.getMessage(), Matchers.is("Validation failed"));
        Assert.assertFalse(response.getValidationResult().isSuccessful());
        Assert.assertThat(response.getValidationResult().getErrors().get(0), Matchers.is(new ValidationError("url", "URL cannot be blank")));
        Assert.assertThat(response.getValidationResult().getErrors().get(1), Matchers.is(new ValidationError("password", "Password cannot be blank")));
    }
}


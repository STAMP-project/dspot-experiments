/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.common.exception;


import OAuth2Exception.ACCESS_DENIED;
import OAuth2Exception.INVALID_CLIENT;
import OAuth2Exception.INVALID_GRANT;
import OAuth2Exception.INVALID_REQUEST;
import OAuth2Exception.INVALID_SCOPE;
import OAuth2Exception.INVALID_TOKEN;
import OAuth2Exception.UNAUTHORIZED_CLIENT;
import OAuth2Exception.UNSUPPORTED_GRANT_TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Winch
 * @author Dave Syer
 */
public class OAuth2ExceptionJackson2DeserializerTests {
    private static final String DETAILS = "some detail";

    private static ObjectMapper mapper;

    @Test
    public void readValueInvalidGrant() throws Exception {
        String accessToken = createResponse(INVALID_GRANT);
        InvalidGrantException result = ((InvalidGrantException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueInvalidRequest() throws Exception {
        String accessToken = createResponse(INVALID_REQUEST);
        InvalidRequestException result = ((InvalidRequestException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueInvalidScope() throws Exception {
        String accessToken = createResponse(INVALID_SCOPE);
        InvalidScopeException result = ((InvalidScopeException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueIsufficientScope() throws Exception {
        String accessToken = "{\"error\": \"insufficient_scope\", \"error_description\": \"insufficient scope\", \"scope\": \"bar foo\"}";
        InsufficientScopeException result = ((InsufficientScopeException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals("insufficient scope", result.getMessage());
        Assert.assertEquals("bar foo", result.getAdditionalInformation().get("scope").toString());
    }

    @Test
    public void readValueUnsupportedGrantType() throws Exception {
        String accessToken = createResponse(UNSUPPORTED_GRANT_TYPE);
        UnsupportedGrantTypeException result = ((UnsupportedGrantTypeException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueUnauthorizedClient() throws Exception {
        String accessToken = createResponse(UNAUTHORIZED_CLIENT);
        UnauthorizedClientException result = ((UnauthorizedClientException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueAccessDenied() throws Exception {
        String accessToken = createResponse(ACCESS_DENIED);
        UserDeniedAuthorizationException result = ((UserDeniedAuthorizationException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueRedirectUriMismatch() throws Exception {
        String accessToken = createResponse(INVALID_GRANT, "Redirect URI mismatch.");
        RedirectMismatchException result = ((RedirectMismatchException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals("Redirect URI mismatch.", result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueInvalidToken() throws Exception {
        String accessToken = createResponse(INVALID_TOKEN);
        InvalidTokenException result = ((InvalidTokenException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueUndefinedException() throws Exception {
        String accessToken = createResponse("notdefinedcode");
        OAuth2Exception result = OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class);
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueInvalidClient() throws Exception {
        String accessToken = createResponse(INVALID_CLIENT);
        InvalidClientException result = ((InvalidClientException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals(null, result.getAdditionalInformation());
    }

    @Test
    public void readValueWithAdditionalDetails() throws Exception {
        String accessToken = "{\"error\": \"invalid_client\", \"error_description\": \"some detail\", \"foo\": \"bar\"}";
        InvalidClientException result = ((InvalidClientException) (OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class)));
        Assert.assertEquals(OAuth2ExceptionJackson2DeserializerTests.DETAILS, result.getMessage());
        Assert.assertEquals("{foo=bar}", result.getAdditionalInformation().toString());
    }

    @Test
    public void readValueWithObjects() throws Exception {
        String accessToken = "{\"error\": [\"invalid\",\"client\"], \"error_description\": {\"some\":\"detail\"}, \"foo\": [\"bar\"]}";
        OAuth2Exception result = OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(accessToken, OAuth2Exception.class);
        Assert.assertEquals("{some=detail}", result.getMessage());
        Assert.assertEquals("{foo=[bar]}", result.getAdditionalInformation().toString());
    }

    // gh-594
    @Test
    public void readValueWithNullErrorDescription() throws Exception {
        OAuth2Exception ex = new OAuth2Exception(null);
        OAuth2Exception result = OAuth2ExceptionJackson2DeserializerTests.mapper.readValue(OAuth2ExceptionJackson2DeserializerTests.mapper.writeValueAsString(ex), OAuth2Exception.class);
        // Null error description defaults to error code when deserialized
        Assert.assertEquals(ex.getOAuth2ErrorCode(), result.getMessage());
    }
}


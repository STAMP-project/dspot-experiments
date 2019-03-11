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


import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Winch
 */
public class OAuth2ExceptionSerializerTests {
    private static final String DETAILS = "some detail";

    private static ObjectMapper mapper;

    private OAuth2Exception oauthException;

    @Test
    public void writeValueAsStringInvalidClient() throws Exception {
        oauthException = new InvalidClientException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringInvalidGrant() throws Exception {
        oauthException = new InvalidGrantException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringInvalidRequest() throws Exception {
        oauthException = new InvalidRequestException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringInvalidScope() throws Exception {
        oauthException = new InvalidScopeException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringUnsupportedGrantType() throws Exception {
        oauthException = new UnsupportedGrantTypeException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringUnauthorizedClient() throws Exception {
        oauthException = new UnauthorizedClientException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringAccessDenied() throws Exception {
        oauthException = new UserDeniedAuthorizationException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringRedirectUriMismatch() throws Exception {
        oauthException = new RedirectMismatchException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringInvalidToken() throws Exception {
        oauthException = new InvalidTokenException(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringOAuth2Exception() throws Exception {
        oauthException = new OAuth2Exception(OAuth2ExceptionSerializerTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }

    @Test
    public void writeValueAsStringWithAdditionalDetails() throws Exception {
        oauthException = new InvalidClientException(OAuth2ExceptionSerializerTests.DETAILS);
        oauthException.addAdditionalInformation("foo", "bar");
        String expected = "{\"error\":\"invalid_client\",\"error_description\":\"some detail\",\"foo\":\"bar\"}";
        Assert.assertEquals(expected, OAuth2ExceptionSerializerTests.mapper.writeValueAsString(oauthException));
    }
}


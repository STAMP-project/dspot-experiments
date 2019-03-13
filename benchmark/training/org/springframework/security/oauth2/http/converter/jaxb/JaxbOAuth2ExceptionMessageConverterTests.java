/**
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.security.oauth2.http.converter.jaxb;


import OAuth2Exception.ACCESS_DENIED;
import OAuth2Exception.INVALID_CLIENT;
import OAuth2Exception.INVALID_GRANT;
import OAuth2Exception.INVALID_REQUEST;
import OAuth2Exception.INVALID_SCOPE;
import OAuth2Exception.INVALID_TOKEN;
import OAuth2Exception.REDIRECT_URI_MISMATCH;
import OAuth2Exception.UNAUTHORIZED_CLIENT;
import OAuth2Exception.UNSUPPORTED_GRANT_TYPE;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ System.class, JaxbOAuth2AccessToken.class })
public class JaxbOAuth2ExceptionMessageConverterTests extends BaseJaxbMessageConverterTest {
    private JaxbOAuth2ExceptionMessageConverter converter;

    private static String DETAILS = "some detail";

    @Test
    public void writeInvalidClient() throws IOException {
        OAuth2Exception oauthException = new InvalidClientException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeInvalidGrant() throws Exception {
        OAuth2Exception oauthException = new InvalidGrantException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeInvalidRequest() throws Exception {
        OAuth2Exception oauthException = new InvalidRequestException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeInvalidScope() throws Exception {
        OAuth2Exception oauthException = new InvalidScopeException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeUnsupportedGrantType() throws Exception {
        OAuth2Exception oauthException = new UnsupportedGrantTypeException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeUnauthorizedClient() throws Exception {
        OAuth2Exception oauthException = new UnauthorizedClientException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeAccessDenied() throws Exception {
        OAuth2Exception oauthException = new UserDeniedAuthorizationException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeRedirectUriMismatch() throws Exception {
        OAuth2Exception oauthException = new RedirectMismatchException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeInvalidToken() throws Exception {
        OAuth2Exception oauthException = new InvalidTokenException(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    @Test
    public void writeOAuth2Exception() throws Exception {
        OAuth2Exception oauthException = new OAuth2Exception(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        String expected = createResponse(oauthException.getOAuth2ErrorCode());
        converter.write(oauthException, contentType, outputMessage);
        Assert.assertEquals(expected, getOutput());
    }

    // SECOAUTH-311
    @Test
    public void writeCreatesNewUnmarshaller() throws Exception {
        useMockJAXBContext(converter, JaxbOAuth2Exception.class);
        OAuth2Exception oauthException = new OAuth2Exception(JaxbOAuth2ExceptionMessageConverterTests.DETAILS);
        converter.write(oauthException, contentType, outputMessage);
        Mockito.verify(context).createMarshaller();
        converter.write(oauthException, contentType, outputMessage);
        Mockito.verify(context, Mockito.times(2)).createMarshaller();
    }

    @Test
    public void readInvalidGrant() throws Exception {
        String accessToken = createResponse(INVALID_GRANT);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        InvalidGrantException result = ((InvalidGrantException) (converter.read(OAuth2Exception.class, inputMessage)));
    }

    @Test
    public void readInvalidRequest() throws Exception {
        String accessToken = createResponse(INVALID_REQUEST);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        InvalidRequestException result = ((InvalidRequestException) (converter.read(OAuth2Exception.class, inputMessage)));
    }

    @Test
    public void readInvalidScope() throws Exception {
        String accessToken = createResponse(INVALID_SCOPE);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        InvalidScopeException result = ((InvalidScopeException) (converter.read(OAuth2Exception.class, inputMessage)));
    }

    @Test
    public void readUnsupportedGrantType() throws Exception {
        String accessToken = createResponse(UNSUPPORTED_GRANT_TYPE);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        UnsupportedGrantTypeException result = ((UnsupportedGrantTypeException) (converter.read(OAuth2Exception.class, inputMessage)));
    }

    @Test
    public void readUnauthorizedClient() throws Exception {
        String accessToken = createResponse(UNAUTHORIZED_CLIENT);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        UnauthorizedClientException result = ((UnauthorizedClientException) (converter.read(OAuth2Exception.class, inputMessage)));
    }

    @Test
    public void readAccessDenied() throws Exception {
        String accessToken = createResponse(ACCESS_DENIED);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        UserDeniedAuthorizationException result = ((UserDeniedAuthorizationException) (converter.read(OAuth2Exception.class, inputMessage)));
    }

    @Test
    public void readRedirectUriMismatch() throws Exception {
        String accessToken = createResponse(REDIRECT_URI_MISMATCH);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        RedirectMismatchException result = ((RedirectMismatchException) (converter.read(OAuth2Exception.class, inputMessage)));
    }

    @Test
    public void readInvalidToken() throws Exception {
        String accessToken = createResponse(INVALID_TOKEN);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        InvalidTokenException result = ((InvalidTokenException) (converter.read(OAuth2Exception.class, inputMessage)));
    }

    @Test
    public void readUndefinedException() throws Exception {
        String accessToken = createResponse("notdefinedcode");
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        OAuth2Exception result = converter.read(OAuth2Exception.class, inputMessage);
    }

    @Test
    public void readInvalidClient() throws IOException {
        String accessToken = createResponse(INVALID_CLIENT);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        @SuppressWarnings("unused")
        InvalidClientException result = ((InvalidClientException) (converter.read(InvalidClientException.class, inputMessage)));
    }

    // SECOAUTH-311
    @Test
    public void readCreatesNewUnmarshaller() throws Exception {
        useMockJAXBContext(converter, JaxbOAuth2Exception.class);
        String accessToken = createResponse(ACCESS_DENIED);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        converter.read(OAuth2Exception.class, inputMessage);
        Mockito.verify(context).createUnmarshaller();
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(accessToken));
        converter.read(OAuth2Exception.class, inputMessage);
        Mockito.verify(context, Mockito.times(2)).createUnmarshaller();
    }
}


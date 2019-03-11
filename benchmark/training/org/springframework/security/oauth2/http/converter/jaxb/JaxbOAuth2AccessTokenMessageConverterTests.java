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


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;


/**
 *
 *
 * @author Rob Winch
 */
@PrepareForTest(JaxbOAuth2AccessToken.class)
public class JaxbOAuth2AccessTokenMessageConverterTests extends BaseJaxbMessageConverterTest {
    private JaxbOAuth2AccessTokenMessageConverter converter;

    private DefaultOAuth2AccessToken accessToken;

    @Test
    public void writeAccessToken() throws IOException {
        converter.write(accessToken, contentType, outputMessage);
        Assert.assertEquals(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN, getOutput());
    }

    @Test
    public void writeAccessTokenNoRefresh() throws IOException {
        accessToken.setRefreshToken(null);
        converter.write(accessToken, contentType, outputMessage);
        Assert.assertEquals(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN_NOREFRESH, getOutput());
    }

    @Test
    public void writeAccessTokenNoExpires() throws IOException {
        accessToken.setRefreshToken(null);
        accessToken.setExpiration(null);
        converter.write(accessToken, contentType, outputMessage);
        Assert.assertEquals(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN_NOEXPIRES, getOutput());
    }

    // SECOAUTH-311
    @Test
    public void writeCreatesNewMarshaller() throws Exception {
        useMockJAXBContext(converter, JaxbOAuth2AccessToken.class);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN));
        converter.write(accessToken, contentType, outputMessage);
        Mockito.verify(context).createMarshaller();
        converter.write(accessToken, contentType, outputMessage);
        Mockito.verify(context, Mockito.times(2)).createMarshaller();
    }

    @Test
    public void readAccessToken() throws IOException {
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN));
        OAuth2AccessToken token = converter.read(OAuth2AccessToken.class, inputMessage);
        JaxbOAuth2AccessTokenMessageConverterTests.assertTokenEquals(accessToken, token);
    }

    @Test
    public void readAccessTokenNoRefresh() throws IOException {
        accessToken.setRefreshToken(null);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN_NOREFRESH));
        OAuth2AccessToken token = converter.read(OAuth2AccessToken.class, inputMessage);
        JaxbOAuth2AccessTokenMessageConverterTests.assertTokenEquals(accessToken, token);
    }

    @Test
    public void readAccessTokenNoExpires() throws IOException {
        accessToken.setRefreshToken(null);
        accessToken.setExpiration(null);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN_NOEXPIRES));
        OAuth2AccessToken token = converter.read(OAuth2AccessToken.class, inputMessage);
        JaxbOAuth2AccessTokenMessageConverterTests.assertTokenEquals(accessToken, token);
    }

    // SECOAUTH-311
    @Test
    public void readCreatesNewUnmarshaller() throws Exception {
        useMockJAXBContext(converter, JaxbOAuth2AccessToken.class);
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN));
        converter.read(OAuth2AccessToken.class, inputMessage);
        Mockito.verify(context).createUnmarshaller();
        Mockito.when(inputMessage.getBody()).thenReturn(createInputStream(BaseJaxbMessageConverterTest.OAUTH_ACCESSTOKEN));
        converter.read(OAuth2AccessToken.class, inputMessage);
        Mockito.verify(context, Mockito.times(2)).createUnmarshaller();
    }
}


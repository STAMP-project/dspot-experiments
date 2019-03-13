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
package org.springframework.security.oauth2.common;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashSet;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


/**
 * Tests deserialization of an {@link org.springframework.security.oauth2.common.OAuth2AccessToken} using jackson.
 *
 * @author Rob Winch
 */
@PrepareForTest(OAuth2AccessTokenJackson2Deserializer.class)
public class OAuth2AccessTokenJackson2DeserializerTests extends BaseOAuth2AccessTokenJacksonTest {
    protected ObjectMapper mapper;

    @Test
    public void readValueNoRefresh() throws JsonGenerationException, JsonMappingException, IOException {
        accessToken.setRefreshToken(null);
        accessToken.setScope(null);
        OAuth2AccessToken actual = mapper.readValue(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_NOREFRESH, OAuth2AccessToken.class);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }

    @Test
    public void readValueWithRefresh() throws JsonGenerationException, JsonMappingException, IOException {
        accessToken.setScope(null);
        OAuth2AccessToken actual = mapper.readValue(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_NOSCOPE, OAuth2AccessToken.class);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }

    @Test
    public void readValueWithSingleScopes() throws JsonGenerationException, JsonMappingException, IOException {
        accessToken.getScope().remove(accessToken.getScope().iterator().next());
        OAuth2AccessToken actual = mapper.readValue(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_SINGLESCOPE, OAuth2AccessToken.class);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }

    @Test
    public void readValueWithEmptyStringScope() throws JsonGenerationException, JsonMappingException, IOException {
        accessToken.setScope(new HashSet<String>());
        OAuth2AccessToken actual = mapper.readValue(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_EMPTYSCOPE, OAuth2AccessToken.class);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }

    @Test
    public void readValueWithBrokenExpiresIn() throws JsonGenerationException, JsonMappingException, IOException {
        accessToken.setScope(new HashSet<String>());
        OAuth2AccessToken actual = mapper.readValue(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_BROKENEXPIRES, OAuth2AccessToken.class);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }

    @Test
    public void readValueWithMultiScopes() throws Exception {
        OAuth2AccessToken actual = mapper.readValue(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_MULTISCOPE, OAuth2AccessToken.class);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }

    @Test
    public void readValueWithArrayScopes() throws Exception {
        OAuth2AccessToken actual = mapper.readValue(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_ARRAYSCOPE, OAuth2AccessToken.class);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }

    @Test
    public void readValueWithMac() throws Exception {
        accessToken.setTokenType("mac");
        String encodedToken = BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_MULTISCOPE.replace("bearer", accessToken.getTokenType());
        OAuth2AccessToken actual = mapper.readValue(encodedToken, OAuth2AccessToken.class);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }

    @Test
    public void readValueWithAdditionalInformation() throws Exception {
        OAuth2AccessToken actual = mapper.readValue(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_ADDITIONAL_INFO, OAuth2AccessToken.class);
        accessToken.setAdditionalInformation(additionalInformation);
        accessToken.setRefreshToken(null);
        accessToken.setScope(null);
        accessToken.setExpiration(null);
        OAuth2AccessTokenJackson2DeserializerTests.assertTokenEquals(accessToken, actual);
    }
}


/**
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.provider.token;


import java.util.LinkedHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.provider.OAuth2Authentication;


@RunWith(MockitoJUnitRunner.class)
public class DefaultAuthenticationKeyGeneratorTest {
    private static final String USERNAME = "name";

    private static final String CLIENT_ID = "client-id";

    private static final String CHECKSUM = "checksum";

    @Mock
    private OAuth2Authentication auth;

    @Spy
    private DefaultAuthenticationKeyGenerator generator;

    @Test
    public void shouldUseTheChecksumGeneratedByTheDigest() {
        Mockito.when(auth.getOAuth2Request()).thenReturn(createRequest(DefaultAuthenticationKeyGeneratorTest.CLIENT_ID));
        Mockito.when(generator.generateKey(ArgumentMatchers.anyMap())).thenReturn(DefaultAuthenticationKeyGeneratorTest.CHECKSUM);
        Assert.assertEquals(DefaultAuthenticationKeyGeneratorTest.CHECKSUM, generator.extractKey(auth));
    }

    @Test
    public void shouldOnlyUseTheClientIdAsPartOfTheDigestIfTheAuthIsClientOnly() {
        Mockito.when(auth.isClientOnly()).thenReturn(true);
        Mockito.when(auth.getOAuth2Request()).thenReturn(createRequest(DefaultAuthenticationKeyGeneratorTest.CLIENT_ID));
        generator.extractKey(auth);
        LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
        expectedValues.put("client_id", DefaultAuthenticationKeyGeneratorTest.CLIENT_ID);
        expectedValues.put("scope", "");
        Mockito.verify(generator).generateKey(expectedValues);
    }

    @Test
    public void shouldNotUseScopesIfNoneAreProvided() {
        Mockito.when(auth.getOAuth2Request()).thenReturn(createRequest(DefaultAuthenticationKeyGeneratorTest.CLIENT_ID));
        generator.extractKey(auth);
        LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
        expectedValues.put("username", DefaultAuthenticationKeyGeneratorTest.USERNAME);
        expectedValues.put("client_id", DefaultAuthenticationKeyGeneratorTest.CLIENT_ID);
        expectedValues.put("scope", "");
        Mockito.verify(generator).generateKey(expectedValues);
    }

    @Test
    public void shouldSortTheScopesBeforeDigesting() {
        Mockito.when(auth.getOAuth2Request()).thenReturn(createRequest(DefaultAuthenticationKeyGeneratorTest.CLIENT_ID, "3", "1", "2"));
        generator.extractKey(auth);
        LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
        expectedValues.put("username", DefaultAuthenticationKeyGeneratorTest.USERNAME);
        expectedValues.put("client_id", DefaultAuthenticationKeyGeneratorTest.CLIENT_ID);
        expectedValues.put("scope", "1 2 3");
        Mockito.verify(generator).generateKey(expectedValues);
    }
}


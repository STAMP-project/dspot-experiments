/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.util;


import io.helidon.common.CollectionsHelper;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit test for {@link TokenHandler}.
 */
public class TokenHandlerTest {
    private static final String TOKEN_VALUE = "abdasf5as4df35as4dfas3f4as35d21afd3";

    @Test
    public void testMissingHeader() {
        TokenHandler tp = TokenHandler.builder().tokenHeader("Other").tokenPrefix("bearer ").build();
        Optional<String> optToken = tp.extractToken(bearerRequest());
        MatcherAssert.assertThat(optToken, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(optToken.isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void testWrongPrefix() {
        TokenHandler tp = TokenHandler.builder().tokenHeader("Authorization").tokenPrefix("hearer ").build();
        try {
            testBearer(tp, TokenHandlerTest.TOKEN_VALUE);
        } catch (SecurityException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.startsWith("Header does not start"));
        }
    }

    @Test
    public void testWrongPattern() {
        TokenHandler tp = TokenHandler.builder().tokenHeader("Authorization").tokenPattern(Pattern.compile("not matching")).build();
        try {
            testBearer(tp, TokenHandlerTest.TOKEN_VALUE);
        } catch (SecurityException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.startsWith("Header does not match expected pattern"));
        }
    }

    @Test
    public void testPrefixConfig() {
        Config config = Config.builder().sources(ConfigSources.classpath("token_provider.conf")).build();
        TokenHandler tp = TokenHandler.create(config.get("token-1"));
        testBearer(tp, TokenHandlerTest.TOKEN_VALUE);
    }

    @Test
    public void testPrefixBuilder() {
        TokenHandler tp = TokenHandler.builder().tokenHeader("Authorization").tokenPrefix("bearer ").build();
        testBearer(tp, TokenHandlerTest.TOKEN_VALUE);
        testCreateHeader(tp);
    }

    @Test
    public void testRegexpConfig() {
        Config config = Config.builder().sources(ConfigSources.classpath("token_provider.conf")).build();
        TokenHandler tp = TokenHandler.create(config.get("token-2"));
        testBearer(tp, TokenHandlerTest.TOKEN_VALUE);
        testCreateHeader(tp);
    }

    @Test
    public void testAddHeader() {
        TokenHandler tp = TokenHandler.forHeader("Authorization");
        Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.put("Authorization", Collections.singletonList("firstToken"));
        tp.addHeader(headers, "secondToken");
        List<String> authorization = headers.get("Authorization");
        MatcherAssert.assertThat(authorization.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(authorization, CoreMatchers.is(CollectionsHelper.listOf("firstToken", "secondToken")));
        MatcherAssert.assertThat(tp.tokenHeader(), CoreMatchers.is("Authorization"));
    }

    @Test
    public void testAddNewHeader() {
        TokenHandler tp = TokenHandler.forHeader("Authorization");
        Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        tp.addHeader(headers, "secondToken");
        List<String> authorization = headers.get("Authorization");
        MatcherAssert.assertThat(authorization.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(authorization, CoreMatchers.is(Collections.singletonList("secondToken")));
        MatcherAssert.assertThat(tp.tokenHeader(), CoreMatchers.is("Authorization"));
    }

    @Test
    public void testExtractHeader() {
        String tokenValue = "token_asdfas?dfasdlkjfsad?lflsd";
        TokenHandler tokenHandler = TokenHandler.forHeader("Test");
        String value = tokenHandler.extractToken(tokenValue);
        MatcherAssert.assertThat(value, CoreMatchers.is(tokenValue));
    }

    @Test
    public void testRegexpBuilder() {
        TokenHandler tp = TokenHandler.builder().tokenHeader("Authorization").tokenPattern(Pattern.compile("bearer (.*)", Pattern.CASE_INSENSITIVE)).build();
        testBearer(tp, TokenHandlerTest.TOKEN_VALUE);
    }

    @Test
    public void testConfig() {
        Config config = Config.builder().sources(ConfigSources.classpath("token_provider.conf")).build();
        TokenHandler tp = TokenHandler.create(config.get("token-4"));
        testBearer(tp, ("bearer " + (TokenHandlerTest.TOKEN_VALUE)));
    }

    @Test
    public void testBuilder() {
        TokenHandler tp = TokenHandler.builder().tokenHeader("Authorization").build();
        testBearer(tp, ("bearer " + (TokenHandlerTest.TOKEN_VALUE)));
    }

    @Test
    public void testWrongConfig() {
        Config config = Config.builder().sources(ConfigSources.classpath("token_provider.conf")).build();
        Assertions.assertThrows(NullPointerException.class, () -> TokenHandler.create(config.get("token-3")));
    }

    @Test
    public void testWrongBuilder() {
        Assertions.assertThrows(NullPointerException.class, () -> TokenHandler.builder().tokenPattern(Pattern.compile("bearer (.*)", Pattern.CASE_INSENSITIVE)).build());
    }
}


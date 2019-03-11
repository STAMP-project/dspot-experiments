/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.web.server.util.matcher;


import ServerWebExchangeMatcher.MatchResult;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.server.ServerWebExchange;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class OrServerWebExchangeMatcherTests {
    @Mock
    ServerWebExchange exchange;

    @Mock
    ServerWebExchangeMatcher matcher1;

    @Mock
    ServerWebExchangeMatcher matcher2;

    OrServerWebExchangeMatcher matcher;

    @Test
    public void matchesWhenFalseFalseThenFalse() throws Exception {
        Mockito.when(matcher1.matches(exchange)).thenReturn(MatchResult.notMatch());
        Mockito.when(matcher2.matches(exchange)).thenReturn(MatchResult.notMatch());
        ServerWebExchangeMatcher.MatchResult matches = matcher.matches(exchange).block();
        assertThat(matches.isMatch()).isFalse();
        assertThat(matches.getVariables()).isEmpty();
        Mockito.verify(matcher1).matches(exchange);
        Mockito.verify(matcher2).matches(exchange);
    }

    @Test
    public void matchesWhenTrueFalseThenTrueAndMatcher2NotInvoked() throws Exception {
        Map<String, Object> params = Collections.singletonMap("foo", "bar");
        Mockito.when(matcher1.matches(exchange)).thenReturn(MatchResult.match(params));
        ServerWebExchangeMatcher.MatchResult matches = matcher.matches(exchange).block();
        assertThat(matches.isMatch()).isTrue();
        assertThat(matches.getVariables()).isEqualTo(params);
        Mockito.verify(matcher1).matches(exchange);
        Mockito.verify(matcher2, Mockito.never()).matches(exchange);
    }

    @Test
    public void matchesWhenFalseTrueThenTrue() throws Exception {
        Map<String, Object> params = Collections.singletonMap("foo", "bar");
        Mockito.when(matcher1.matches(exchange)).thenReturn(MatchResult.notMatch());
        Mockito.when(matcher2.matches(exchange)).thenReturn(MatchResult.match(params));
        ServerWebExchangeMatcher.MatchResult matches = matcher.matches(exchange).block();
        assertThat(matches.isMatch()).isTrue();
        assertThat(matches.getVariables()).isEqualTo(params);
        Mockito.verify(matcher1).matches(exchange);
        Mockito.verify(matcher2).matches(exchange);
    }
}


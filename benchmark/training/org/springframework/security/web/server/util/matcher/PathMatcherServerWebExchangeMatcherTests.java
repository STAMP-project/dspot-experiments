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


import PathPattern.PathMatchInfo;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PathMatcherServerWebExchangeMatcherTests {
    @Mock
    PathPattern pattern;

    @Mock
    PathMatchInfo pathMatchInfo;

    MockServerWebExchange exchange;

    PathPatternParserServerWebExchangeMatcher matcher;

    String path;

    @Test(expected = IllegalArgumentException.class)
    public void constructorPatternWhenPatternNullThenThrowsException() {
        new PathPatternParserServerWebExchangeMatcher(((PathPattern) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorPatternAndMethodWhenPatternNullThenThrowsException() {
        new PathPatternParserServerWebExchangeMatcher(((PathPattern) (null)), HttpMethod.GET);
    }

    @Test
    public void matchesWhenPathMatcherTrueThenReturnTrue() {
        Mockito.when(pattern.matches(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(pattern.matchAndExtract(ArgumentMatchers.any())).thenReturn(pathMatchInfo);
        Mockito.when(pathMatchInfo.getUriVariables()).thenReturn(new HashMap());
        assertThat(matcher.matches(exchange).block().isMatch()).isTrue();
    }

    @Test
    public void matchesWhenPathMatcherFalseThenReturnFalse() {
        Mockito.when(pattern.matches(ArgumentMatchers.any())).thenReturn(false);
        assertThat(matcher.matches(exchange).block().isMatch()).isFalse();
    }

    @Test
    public void matchesWhenPathMatcherTrueAndMethodTrueThenReturnTrue() {
        matcher = new PathPatternParserServerWebExchangeMatcher(pattern, exchange.getRequest().getMethod());
        Mockito.when(pattern.matches(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(pattern.matchAndExtract(ArgumentMatchers.any())).thenReturn(pathMatchInfo);
        Mockito.when(pathMatchInfo.getUriVariables()).thenReturn(new HashMap());
        assertThat(matcher.matches(exchange).block().isMatch()).isTrue();
    }

    @Test
    public void matchesWhenPathMatcherTrueAndMethodFalseThenReturnFalse() {
        HttpMethod method = HttpMethod.OPTIONS;
        assertThat(exchange.getRequest().getMethod()).isNotEqualTo(method);
        matcher = new PathPatternParserServerWebExchangeMatcher(pattern, method);
        assertThat(matcher.matches(exchange).block().isMatch()).isFalse();
        Mockito.verifyZeroInteractions(pattern);
    }
}


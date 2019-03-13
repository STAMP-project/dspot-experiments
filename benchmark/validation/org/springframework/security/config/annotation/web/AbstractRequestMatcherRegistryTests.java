/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.config.annotation.web;


import HttpMethod.GET;
import java.util.List;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * Tests for {@link AbstractRequestMatcherRegistry}.
 *
 * @author Joe Grandja
 */
public class AbstractRequestMatcherRegistryTests {
    private AbstractRequestMatcherRegistryTests.TestRequestMatcherRegistry matcherRegistry;

    @Test
    public void regexMatchersWhenHttpMethodAndPatternParamsThenReturnRegexRequestMatcherType() {
        List<RequestMatcher> requestMatchers = this.matcherRegistry.regexMatchers(GET, "/a.*");
        assertThat(requestMatchers).isNotEmpty();
        assertThat(requestMatchers.size()).isEqualTo(1);
        assertThat(requestMatchers.get(0)).isExactlyInstanceOf(RegexRequestMatcher.class);
    }

    @Test
    public void regexMatchersWhenPatternParamThenReturnRegexRequestMatcherType() {
        List<RequestMatcher> requestMatchers = regexMatchers("/a.*");
        assertThat(requestMatchers).isNotEmpty();
        assertThat(requestMatchers.size()).isEqualTo(1);
        assertThat(requestMatchers.get(0)).isExactlyInstanceOf(RegexRequestMatcher.class);
    }

    @Test
    public void antMatchersWhenHttpMethodAndPatternParamsThenReturnAntPathRequestMatcherType() {
        List<RequestMatcher> requestMatchers = this.matcherRegistry.antMatchers(GET, "/a.*");
        assertThat(requestMatchers).isNotEmpty();
        assertThat(requestMatchers.size()).isEqualTo(1);
        assertThat(requestMatchers.get(0)).isExactlyInstanceOf(AntPathRequestMatcher.class);
    }

    @Test
    public void antMatchersWhenPatternParamThenReturnAntPathRequestMatcherType() {
        List<RequestMatcher> requestMatchers = antMatchers("/a.*");
        assertThat(requestMatchers).isNotEmpty();
        assertThat(requestMatchers.size()).isEqualTo(1);
        assertThat(requestMatchers.get(0)).isExactlyInstanceOf(AntPathRequestMatcher.class);
    }

    private static class TestRequestMatcherRegistry extends AbstractRequestMatcherRegistry<List<RequestMatcher>> {
        @Override
        public List<RequestMatcher> mvcMatchers(String... mvcPatterns) {
            return null;
        }

        @Override
        public List<RequestMatcher> mvcMatchers(HttpMethod method, String... mvcPatterns) {
            return null;
        }

        @Override
        protected List<RequestMatcher> chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            return requestMatchers;
        }
    }
}


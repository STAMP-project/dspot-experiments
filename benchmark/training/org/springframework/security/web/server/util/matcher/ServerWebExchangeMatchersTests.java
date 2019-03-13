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


import HttpMethod.GET;
import HttpMethod.POST;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
public class ServerWebExchangeMatchersTests {
    ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());

    @Test
    public void pathMatchersWhenSingleAndSamePatternThenMatches() throws Exception {
        assertThat(ServerWebExchangeMatchers.pathMatchers("/").matches(exchange).block().isMatch()).isTrue();
    }

    @Test
    public void pathMatchersWhenSingleAndSamePatternAndMethodThenMatches() throws Exception {
        assertThat(ServerWebExchangeMatchers.pathMatchers(GET, "/").matches(exchange).block().isMatch()).isTrue();
    }

    @Test
    public void pathMatchersWhenSingleAndSamePatternAndDiffMethodThenDoesNotMatch() throws Exception {
        assertThat(ServerWebExchangeMatchers.pathMatchers(POST, "/").matches(exchange).block().isMatch()).isFalse();
    }

    @Test
    public void pathMatchersWhenSingleAndDifferentPatternThenDoesNotMatch() throws Exception {
        assertThat(ServerWebExchangeMatchers.pathMatchers("/foobar").matches(exchange).block().isMatch()).isFalse();
    }

    @Test
    public void pathMatchersWhenMultiThenMatches() throws Exception {
        assertThat(ServerWebExchangeMatchers.pathMatchers("/foobar", "/").matches(exchange).block().isMatch()).isTrue();
    }

    @Test
    public void anyExchangeWhenMockThenMatches() {
        ServerWebExchange mockExchange = Mockito.mock(ServerWebExchange.class);
        assertThat(ServerWebExchangeMatchers.anyExchange().matches(mockExchange).block().isMatch()).isTrue();
        Mockito.verifyZeroInteractions(mockExchange);
    }

    /**
     * If a LinkedMap is used and anyRequest equals anyRequest then the following is added:
     * anyRequest() -> authenticated()
     * pathMatchers("/admin/**") -> hasRole("ADMIN")
     * anyRequest() -> permitAll
     *
     * will result in the first entry being overridden
     */
    @Test
    public void anyExchangeWhenTwoCreatedThenDifferentToPreventIssuesInMap() {
        assertThat(ServerWebExchangeMatchers.anyExchange()).isNotEqualTo(ServerWebExchangeMatchers.anyExchange());
    }
}


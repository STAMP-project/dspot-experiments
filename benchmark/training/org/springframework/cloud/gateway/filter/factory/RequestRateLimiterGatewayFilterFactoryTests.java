/**
 * Copyright 2017-2019 the original author or authors.
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
package org.springframework.cloud.gateway.filter.factory;


import HttpStatus.FORBIDDEN;
import HttpStatus.OK;
import HttpStatus.TOO_MANY_REQUESTS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;


/**
 * see
 * https://gist.github.com/ptarjan/e38f45f2dfe601419ca3af937fff574d#file-1-check_request_rate_limiter-rb-L36-L62
 *
 * @author Spencer Gibb
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class RequestRateLimiterGatewayFilterFactoryTests extends BaseWebClientTests {
    @Autowired
    @Qualifier("resolver2")
    KeyResolver resolver2;

    @Autowired
    private ApplicationContext context;

    @MockBean
    private RateLimiter rateLimiter;

    @MockBean
    private GatewayFilterChain filterChain;

    @Test
    public void allowedWorks() {
        // tests that auto wired as default works
        assertFilterFactory(null, "allowedkey", true, OK);
    }

    @Test
    public void notAllowedWorks() {
        assertFilterFactory(resolver2, "notallowedkey", false, TOO_MANY_REQUESTS);
    }

    @Test
    public void emptyKeyDenied() {
        assertFilterFactory(( exchange) -> Mono.empty(), null, true, FORBIDDEN);
    }

    @Test
    public void emptyKeyAllowed() {
        assertFilterFactory(( exchange) -> Mono.empty(), null, true, OK, false);
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        @Bean
        @Primary
        KeyResolver resolver1() {
            return ( exchange) -> Mono.just("allowedkey");
        }

        @Bean
        KeyResolver resolver2() {
            return ( exchange) -> Mono.just("notallowedkey");
        }
    }
}


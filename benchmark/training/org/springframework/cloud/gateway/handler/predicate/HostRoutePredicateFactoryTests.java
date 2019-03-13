/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.handler.predicate;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class HostRoutePredicateFactoryTests extends BaseWebClientTests {
    @Test
    public void hostRouteWorks() {
        expectHostRoute("www.example.org", "host_example_to_httpbin");
    }

    @Test
    public void hostRouteBackwardsCompatiblePatternWorks() {
        expectHostRoute("www.hostpatternarg.org", "host_backwards_compatible_test");
    }

    @Test
    public void hostRouteBackwardsCompatibleShortcutWorks() {
        expectHostRoute("www.hostpatternshortcut.org", "host_backwards_compatible_shortcut_test");
    }

    @Test
    public void mulitHostRouteWorks() {
        expectHostRoute("www.hostmulti1.org", "host_multi_test");
        expectHostRoute("www.hostmulti2.org", "host_multi_test");
    }

    @Test
    public void mulitHostRouteDslWorks() {
        expectHostRoute("www.hostmultidsl1.org", "host_multi_dsl");
        expectHostRoute("www.hostmultidsl2.org", "host_multi_dsl");
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        @Value("${test.uri}")
        String uri;

        @Bean
        public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route("host_multi_dsl", ( r) -> r.host("**.hostmultidsl1.org", "**.hostmultidsl2.org").filters(( f) -> f.prefixPath("/httpbin")).uri(uri)).build();
        }
    }
}


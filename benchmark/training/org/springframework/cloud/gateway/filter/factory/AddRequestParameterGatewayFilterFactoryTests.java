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
package org.springframework.cloud.gateway.filter.factory;


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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
@ActiveProfiles(profiles = "request-parameter-web-filter")
public class AddRequestParameterGatewayFilterFactoryTests extends BaseWebClientTests {
    @Test
    public void addRequestParameterFilterWorksBlankQuery() {
        testRequestParameterFilter(null, null);
    }

    @Test
    public void addRequestParameterFilterWorksNonBlankQuery() {
        testRequestParameterFilter("baz", "bam");
    }

    @Test
    public void addRequestParameterFilterWorksEncodedQuery() {
        testRequestParameterFilter("name", "%E6%89%8E%E6%A0%B9");
    }

    @Test
    public void addRequestParameterFilterWorksEncodedQueryJavaDsl() {
        testRequestParameterFilter("www.addreqparamjava.org", "ValueB", "javaname", "%E6%89%8E%E6%A0%B9");
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        @Value("${test.uri}")
        String uri;

        @Bean
        public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route("add_request_param_java_test", ( r) -> r.path("/get").and().host("**.addreqparamjava.org").filters(( f) -> f.prefixPath("/httpbin").addRequestParameter("example", "ValueB")).uri(uri)).build();
        }
    }
}


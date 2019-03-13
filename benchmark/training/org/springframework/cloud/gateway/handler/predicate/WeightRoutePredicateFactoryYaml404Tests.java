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


import HttpHeaders.HOST;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.WeightCalculatorWebFilter;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("weights-404")
@DirtiesContext
public class WeightRoutePredicateFactoryYaml404Tests extends BaseWebClientTests {
    @Autowired
    private WeightCalculatorWebFilter filter;

    @Test
    public void weightsFromYamlNot404() {
        filter.setRandom(WeightRoutePredicateFactoryYaml404Tests.getRandom(0.5));
        testClient.get().uri("/get").header(HOST, "www.weight4041.org").exchange().expectStatus().isOk().expectHeader().valueEquals(BaseWebClientTests.ROUTE_ID_HEADER, "weight_first_404_test_1");
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        public TestConfig(WeightCalculatorWebFilter filter) {
            Random random = WeightRoutePredicateFactoryYaml404Tests.getRandom(0.4);
            filter.setRandom(random);
        }
    }
}


/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.test.autoconfigure.data.neo4j;


import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.testsupport.testcontainers.SkippableContainer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.Neo4jContainer;


/**
 * Integration test with custom include filter for {@link DataNeo4jTest}.
 *
 * @author Edd? Mel?ndez
 * @author Michael Simons
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = DataNeo4jTestWithIncludeFilterIntegrationTests.Initializer.class)
@DataNeo4jTest(includeFilters = @Filter(Service.class))
public class DataNeo4jTestWithIncludeFilterIntegrationTests {
    @ClassRule
    public static SkippableContainer<Neo4jContainer<?>> neo4j = new SkippableContainer<Neo4jContainer<?>>(() -> new Neo4jContainer<>().withAdminPassword(null));

    @Autowired
    private ExampleService service;

    @Test
    public void testService() {
        assertThat(this.service.hasNode(ExampleGraph.class)).isFalse();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(("spring.data.neo4j.uri=" + (DataNeo4jTestWithIncludeFilterIntegrationTests.neo4j.getContainer().getBoltUrl()))).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}


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
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.testsupport.testcontainers.SkippableContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.Neo4jContainer;


/**
 * Integration test for {@link DataNeo4jTest}.
 *
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 * @author Michael Simons
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = DataNeo4jTestIntegrationTests.Initializer.class)
@DataNeo4jTest
public class DataNeo4jTestIntegrationTests {
    @ClassRule
    public static SkippableContainer<Neo4jContainer<?>> neo4j = new SkippableContainer<Neo4jContainer<?>>(() -> new Neo4jContainer<>().withAdminPassword(null));

    @Autowired
    private Session session;

    @Autowired
    private ExampleRepository exampleRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testRepository() {
        ExampleGraph exampleGraph = new ExampleGraph();
        exampleGraph.setDescription("Look, new @DataNeo4jTest!");
        assertThat(exampleGraph.getId()).isNull();
        ExampleGraph savedGraph = save(exampleGraph);
        assertThat(savedGraph.getId()).isNotNull();
        assertThat(this.session.countEntitiesOfType(ExampleGraph.class)).isEqualTo(1);
    }

    @Test
    public void didNotInjectExampleService() {
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> this.applicationContext.getBean(.class));
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(("spring.data.neo4j.uri=" + (DataNeo4jTestIntegrationTests.neo4j.getContainer().getBoltUrl()))).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}


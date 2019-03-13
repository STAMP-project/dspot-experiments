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
package org.springframework.boot.autoconfigure.data.neo4j;


import AutoIndexMode.NONE;
import AutoIndexMode.VALIDATE;
import Neo4jProperties.BOLT_DRIVER;
import Neo4jProperties.DEFAULT_BOLT_URI;
import Neo4jProperties.EMBEDDED_DRIVER;
import Neo4jProperties.HTTP_DRIVER;
import org.junit.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Tests for {@link Neo4jProperties}.
 *
 * @author Stephane Nicoll
 * @author Michael Simons
 */
public class Neo4jPropertiesTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void defaultUseEmbeddedInMemoryIfAvailable() {
        Neo4jProperties properties = load(true);
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, EMBEDDED_DRIVER, null);
    }

    @Test
    public void defaultUseBoltDriverIfEmbeddedDriverIsNotAvailable() {
        Neo4jProperties properties = load(false);
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, BOLT_DRIVER, DEFAULT_BOLT_URI);
    }

    @Test
    public void httpUriUseHttpDriver() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.uri=http://localhost:7474");
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, HTTP_DRIVER, "http://localhost:7474");
    }

    @Test
    public void httpsUriUseHttpDriver() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.uri=https://localhost:7474");
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, HTTP_DRIVER, "https://localhost:7474");
    }

    @Test
    public void boltUriUseBoltDriver() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.uri=bolt://localhost:7687");
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, BOLT_DRIVER, "bolt://localhost:7687");
    }

    @Test
    public void fileUriUseEmbeddedServer() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.uri=file://var/tmp/graph.db");
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, EMBEDDED_DRIVER, "file://var/tmp/graph.db");
    }

    @Test
    public void credentialsAreSet() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.uri=http://localhost:7474", "spring.data.neo4j.username=user", "spring.data.neo4j.password=secret");
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, HTTP_DRIVER, "http://localhost:7474");
        Neo4jPropertiesTests.assertCredentials(configuration, "user", "secret");
    }

    @Test
    public void credentialsAreSetFromUri() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.uri=http://user:secret@my-server:7474");
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, HTTP_DRIVER, "http://my-server:7474");
        Neo4jPropertiesTests.assertCredentials(configuration, "user", "secret");
    }

    @Test
    public void autoIndexNoneByDefault() {
        Neo4jProperties properties = load(true);
        Configuration configuration = properties.createConfiguration();
        assertThat(configuration.getAutoIndex()).isEqualTo(NONE);
    }

    @Test
    public void autoIndexCanBeConfigured() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.auto-index=validate");
        Configuration configuration = properties.createConfiguration();
        assertThat(configuration.getAutoIndex()).isEqualTo(VALIDATE);
    }

    @Test
    public void embeddedModeDisabledUseBoltUri() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.embedded.enabled=false");
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, BOLT_DRIVER, DEFAULT_BOLT_URI);
    }

    @Test
    public void embeddedModeWithRelativeLocation() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.uri=file:relative/path/to/my.db");
        Configuration configuration = properties.createConfiguration();
        Neo4jPropertiesTests.assertDriver(configuration, EMBEDDED_DRIVER, "file:relative/path/to/my.db");
    }

    @Test
    public void nativeTypesAreSetToFalseByDefault() {
        Neo4jProperties properties = load(true);
        Configuration configuration = properties.createConfiguration();
        assertThat(configuration.getUseNativeTypes()).isFalse();
    }

    @Test
    public void nativeTypesCanBeConfigured() {
        Neo4jProperties properties = load(true, "spring.data.neo4j.use-native-types=true");
        Configuration configuration = properties.createConfiguration();
        assertThat(configuration.getUseNativeTypes()).isTrue();
    }

    @org.springframework.context.annotation.Configuration
    @EnableConfigurationProperties(Neo4jProperties.class)
    static class TestConfiguration {}
}


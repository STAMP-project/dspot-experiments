/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.mongo;


import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link MongoClientFactory}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Mark Paluch
 */
public class MongoClientFactoryTests {
    private MockEnvironment environment = new MockEnvironment();

    @Test
    public void portCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setPort(12345);
        MongoClient client = createMongoClient(properties);
        List<ServerAddress> allAddresses = client.getAllAddress();
        assertThat(allAddresses).hasSize(1);
        assertServerAddress(allAddresses.get(0), "localhost", 12345);
    }

    @Test
    public void hostCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setHost("mongo.example.com");
        MongoClient client = createMongoClient(properties);
        List<ServerAddress> allAddresses = client.getAllAddress();
        assertThat(allAddresses).hasSize(1);
        assertServerAddress(allAddresses.get(0), "mongo.example.com", 27017);
    }

    @Test
    public void credentialsCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setUsername("user");
        properties.setPassword("secret".toCharArray());
        MongoClient client = createMongoClient(properties);
        assertMongoCredential(client.getCredentialsList().get(0), "user", "secret", "test");
    }

    @Test
    public void databaseCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setDatabase("foo");
        properties.setUsername("user");
        properties.setPassword("secret".toCharArray());
        MongoClient client = createMongoClient(properties);
        assertMongoCredential(client.getCredentialsList().get(0), "user", "secret", "foo");
    }

    @Test
    public void authenticationDatabaseCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setAuthenticationDatabase("foo");
        properties.setUsername("user");
        properties.setPassword("secret".toCharArray());
        MongoClient client = createMongoClient(properties);
        assertMongoCredential(client.getCredentialsList().get(0), "user", "secret", "foo");
    }

    @Test
    public void uriCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setUri(("mongodb://user:secret@mongo1.example.com:12345," + "mongo2.example.com:23456/test"));
        MongoClient client = createMongoClient(properties);
        List<ServerAddress> allAddresses = client.getAllAddress();
        assertThat(allAddresses).hasSize(2);
        assertServerAddress(allAddresses.get(0), "mongo1.example.com", 12345);
        assertServerAddress(allAddresses.get(1), "mongo2.example.com", 23456);
        List<MongoCredential> credentialsList = client.getCredentialsList();
        assertThat(credentialsList).hasSize(1);
        assertMongoCredential(credentialsList.get(0), "user", "secret", "test");
    }

    @Test
    public void uriIsIgnoredInEmbeddedMode() {
        MongoProperties properties = new MongoProperties();
        properties.setUri("mongodb://mongo.example.com:1234/mydb");
        this.environment.setProperty("local.mongo.port", "4000");
        MongoClient client = createMongoClient(properties, this.environment);
        List<ServerAddress> allAddresses = client.getAllAddress();
        assertThat(allAddresses).hasSize(1);
        assertServerAddress(allAddresses.get(0), "localhost", 4000);
    }

    @Configuration
    @EnableConfigurationProperties(MongoProperties.class)
    static class Config {}
}


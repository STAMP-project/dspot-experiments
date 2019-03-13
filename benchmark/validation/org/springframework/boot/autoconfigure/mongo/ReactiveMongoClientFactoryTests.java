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


import MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link ReactiveMongoClientFactory}.
 *
 * @author Mark Paluch
 * @author Stephane Nicoll
 */
public class ReactiveMongoClientFactoryTests {
    private MockEnvironment environment = new MockEnvironment();

    @Test
    public void portCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setPort(12345);
        MongoClient client = createMongoClient(properties);
        List<ServerAddress> allAddresses = extractServerAddresses(client);
        assertThat(allAddresses).hasSize(1);
        assertServerAddress(allAddresses.get(0), "localhost", 12345);
    }

    @Test
    public void hostCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setHost("mongo.example.com");
        MongoClient client = createMongoClient(properties);
        List<ServerAddress> allAddresses = extractServerAddresses(client);
        assertThat(allAddresses).hasSize(1);
        assertServerAddress(allAddresses.get(0), "mongo.example.com", 27017);
    }

    @Test
    public void credentialsCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setUsername("user");
        properties.setPassword("secret".toCharArray());
        MongoClient client = createMongoClient(properties);
        assertMongoCredential(extractMongoCredentials(client), "user", "secret", "test");
    }

    @Test
    public void databaseCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setDatabase("foo");
        properties.setUsername("user");
        properties.setPassword("secret".toCharArray());
        MongoClient client = createMongoClient(properties);
        assertMongoCredential(extractMongoCredentials(client), "user", "secret", "foo");
    }

    @Test
    public void authenticationDatabaseCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setAuthenticationDatabase("foo");
        properties.setUsername("user");
        properties.setPassword("secret".toCharArray());
        MongoClient client = createMongoClient(properties);
        assertMongoCredential(extractMongoCredentials(client), "user", "secret", "foo");
    }

    @Test
    public void uriCanBeCustomized() {
        MongoProperties properties = new MongoProperties();
        properties.setUri(("mongodb://user:secret@mongo1.example.com:12345," + "mongo2.example.com:23456/test"));
        MongoClient client = createMongoClient(properties);
        List<ServerAddress> allAddresses = extractServerAddresses(client);
        assertThat(allAddresses).hasSize(2);
        assertServerAddress(allAddresses.get(0), "mongo1.example.com", 12345);
        assertServerAddress(allAddresses.get(1), "mongo2.example.com", 23456);
        MongoCredential credential = extractMongoCredentials(client);
        assertMongoCredential(credential, "user", "secret", "test");
    }

    @Test
    public void retryWritesIsPropagatedFromUri() {
        MongoProperties properties = new MongoProperties();
        properties.setUri("mongodb://localhost/test?retryWrites=true");
        MongoClient client = createMongoClient(properties);
        assertThat(getSettings(client).getRetryWrites()).isTrue();
    }

    @Test
    public void uriCannotBeSetWithCredentials() {
        MongoProperties properties = new MongoProperties();
        properties.setUri("mongodb://127.0.0.1:1234/mydb");
        properties.setUsername("user");
        properties.setPassword("secret".toCharArray());
        assertThatIllegalStateException().isThrownBy(() -> createMongoClient(properties)).withMessageContaining(("Invalid mongo configuration, " + "either uri or host/port/credentials must be specified"));
    }

    @Test
    public void uriCannotBeSetWithHostPort() {
        MongoProperties properties = new MongoProperties();
        properties.setUri("mongodb://127.0.0.1:1234/mydb");
        properties.setHost("localhost");
        properties.setPort(4567);
        assertThatIllegalStateException().isThrownBy(() -> createMongoClient(properties)).withMessageContaining(("Invalid mongo configuration, " + "either uri or host/port/credentials must be specified"));
    }

    @Test
    public void uriIsIgnoredInEmbeddedMode() {
        MongoProperties properties = new MongoProperties();
        properties.setUri("mongodb://mongo.example.com:1234/mydb");
        this.environment.setProperty("local.mongo.port", "4000");
        MongoClient client = createMongoClient(properties, this.environment);
        List<ServerAddress> allAddresses = extractServerAddresses(client);
        assertThat(allAddresses).hasSize(1);
        assertServerAddress(allAddresses.get(0), "localhost", 4000);
    }

    @Test
    public void customizerIsInvoked() {
        MongoProperties properties = new MongoProperties();
        MongoClientSettingsBuilderCustomizer customizer = Mockito.mock(MongoClientSettingsBuilderCustomizer.class);
        createMongoClient(properties, this.environment, customizer);
        Mockito.verify(customizer).customize(ArgumentMatchers.any(Builder.class));
    }

    @Test
    public void customizerIsInvokedWhenHostIsSet() {
        MongoProperties properties = new MongoProperties();
        properties.setHost("localhost");
        MongoClientSettingsBuilderCustomizer customizer = Mockito.mock(MongoClientSettingsBuilderCustomizer.class);
        createMongoClient(properties, this.environment, customizer);
        Mockito.verify(customizer).customize(ArgumentMatchers.any(Builder.class));
    }

    @Test
    public void customizerIsInvokedForEmbeddedMongo() {
        MongoProperties properties = new MongoProperties();
        this.environment.setProperty("local.mongo.port", "27017");
        MongoClientSettingsBuilderCustomizer customizer = Mockito.mock(MongoClientSettingsBuilderCustomizer.class);
        createMongoClient(properties, this.environment, customizer);
        Mockito.verify(customizer).customize(ArgumentMatchers.any(Builder.class));
    }
}


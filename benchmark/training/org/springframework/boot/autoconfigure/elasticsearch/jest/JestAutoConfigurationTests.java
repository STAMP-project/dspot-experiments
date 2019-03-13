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
package org.springframework.boot.autoconfigure.elasticsearch.jest;


import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.testcontainers.ElasticsearchContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link JestAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
public class JestAutoConfigurationTests {
    @ClassRule
    public static ElasticsearchContainer elasticsearch = new ElasticsearchContainer();

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(GsonAutoConfiguration.class, JestAutoConfiguration.class));

    @Test
    public void jestClientOnLocalhostByDefault() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void customJestClient() {
        this.contextRunner.withUserConfiguration(JestAutoConfigurationTests.CustomJestClient.class).withPropertyValues("spring.elasticsearch.jest.uris[0]=http://localhost:9200").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void customGson() {
        this.contextRunner.withUserConfiguration(JestAutoConfigurationTests.CustomGson.class).withPropertyValues("spring.elasticsearch.jest.uris=http://localhost:9200").run(( context) -> {
            JestHttpClient client = ((JestHttpClient) (context.getBean(.class)));
            assertThat(client.getGson()).isSameAs(context.getBean("customGson"));
        });
    }

    @Test
    public void customizerOverridesAutoConfig() {
        this.contextRunner.withUserConfiguration(JestAutoConfigurationTests.BuilderCustomizer.class).withPropertyValues("spring.elasticsearch.jest.uris=http://localhost:9200").run(( context) -> {
            JestHttpClient client = ((JestHttpClient) (context.getBean(.class)));
            assertThat(client.getGson()).isSameAs(context.getBean(.class).getGson());
        });
    }

    @Test
    public void proxyHostWithoutPort() {
        this.contextRunner.withPropertyValues("spring.elasticsearch.jest.uris=http://localhost:9200", "spring.elasticsearch.jest.proxy.host=proxy.example.com").run(( context) -> assertThat(context.getStartupFailure()).isInstanceOf(.class).hasMessageContaining("Proxy port must not be null"));
    }

    @Test
    public void jestCanCommunicateWithElasticsearchInstance() {
        this.contextRunner.withPropertyValues(("spring.elasticsearch.jest.uris=http://localhost:" + (JestAutoConfigurationTests.elasticsearch.getMappedPort()))).run(( context) -> {
            JestClient client = context.getBean(.class);
            Map<String, String> source = new HashMap<>();
            source.put("a", "alpha");
            source.put("b", "bravo");
            Index index = new Index.Builder(source).index("foo").type("bar").id("1").build();
            execute(client, index);
            Get getRequest = new Get.Builder("foo", "1").build();
            assertThat(execute(client, getRequest).getResponseCode()).isEqualTo(200);
        });
    }

    @Configuration
    static class CustomJestClient {
        @Bean
        public JestClient customJestClient() {
            return Mockito.mock(JestClient.class);
        }
    }

    @Configuration
    static class CustomGson {
        @Bean
        public Gson customGson() {
            return new Gson();
        }
    }

    @Configuration
    @Import(JestAutoConfigurationTests.CustomGson.class)
    static class BuilderCustomizer {
        private final Gson gson = new Gson();

        @Bean
        public HttpClientConfigBuilderCustomizer customizer() {
            return ( builder) -> builder.gson(org.springframework.boot.autoconfigure.elasticsearch.jest.BuilderCustomizer.this.gson);
        }

        Gson getGson() {
            return this.gson;
        }
    }
}


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
package org.springframework.boot.autoconfigure.web.servlet;


import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.Filter;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.testsupport.web.servlet.MockServletWebServer.RegisteredFilter;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.SessionRepositoryFilter;


/**
 * Integration tests that verify the ordering of various filters that are auto-configured.
 *
 * @author Andy Wilkinson
 * @author Edd? Mel?ndez
 */
public class FilterOrderingIntegrationTests {
    private AnnotationConfigServletWebServerApplicationContext context;

    @Test
    public void testFilterOrdering() {
        load();
        List<RegisteredFilter> registeredFilters = this.context.getBean(MockServletWebServerFactory.class).getWebServer().getRegisteredFilters();
        List<Filter> filters = new java.util.ArrayList(registeredFilters.size());
        for (RegisteredFilter registeredFilter : registeredFilters) {
            filters.add(registeredFilter.getFilter());
        }
        Iterator<Filter> iterator = filters.iterator();
        assertThat(iterator.next()).isInstanceOf(OrderedCharacterEncodingFilter.class);
        assertThat(iterator.next()).isInstanceOf(SessionRepositoryFilter.class);
        assertThat(iterator.next()).isInstanceOf(Filter.class);
        assertThat(iterator.next()).isInstanceOf(Filter.class);
        assertThat(iterator.next()).isInstanceOf(OrderedRequestContextFilter.class);
        assertThat(iterator.next()).isInstanceOf(FilterChainProxy.class);
    }

    @Configuration
    static class MockWebServerConfiguration {
        @Bean
        public MockServletWebServerFactory webServerFactory() {
            return new MockServletWebServerFactory();
        }

        @Bean
        public WebServerFactoryCustomizerBeanPostProcessor ServletWebServerCustomizerBeanPostProcessor() {
            return new WebServerFactoryCustomizerBeanPostProcessor();
        }
    }

    @Configuration
    @EnableSpringHttpSession
    static class TestSessionConfiguration {
        @Bean
        public MapSessionRepository mapSessionRepository() {
            return new MapSessionRepository(new ConcurrentHashMap());
        }
    }

    @Configuration
    static class TestRedisConfiguration {
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisConnectionFactory connectionFactory = Mockito.mock(RedisConnectionFactory.class);
            RedisConnection connection = Mockito.mock(RedisConnection.class);
            BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
            return connectionFactory;
        }
    }
}


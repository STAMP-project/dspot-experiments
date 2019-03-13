/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.cache.config;


import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class ExpressionCachingIntegrationTests {
    // SPR-11692
    @Test
    @SuppressWarnings("unchecked")
    public void expressionIsCacheBasedOnActualMethod() {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(ExpressionCachingIntegrationTests.SharedConfig.class, ExpressionCachingIntegrationTests.Spr11692Config.class);
        ExpressionCachingIntegrationTests.BaseDao<ExpressionCachingIntegrationTests.User> userDao = ((ExpressionCachingIntegrationTests.BaseDao<ExpressionCachingIntegrationTests.User>) (context.getBean("userDao")));
        ExpressionCachingIntegrationTests.BaseDao<ExpressionCachingIntegrationTests.Order> orderDao = ((ExpressionCachingIntegrationTests.BaseDao<ExpressionCachingIntegrationTests.Order>) (context.getBean("orderDao")));
        userDao.persist(new ExpressionCachingIntegrationTests.User("1"));
        orderDao.persist(new ExpressionCachingIntegrationTests.Order("2"));
        context.close();
    }

    @Configuration
    static class Spr11692Config {
        @Bean
        public ExpressionCachingIntegrationTests.BaseDao<ExpressionCachingIntegrationTests.User> userDao() {
            return new ExpressionCachingIntegrationTests.UserDaoImpl();
        }

        @Bean
        public ExpressionCachingIntegrationTests.BaseDao<ExpressionCachingIntegrationTests.Order> orderDao() {
            return new ExpressionCachingIntegrationTests.OrderDaoImpl();
        }
    }

    private interface BaseDao<T> {
        T persist(T t);
    }

    private static class UserDaoImpl implements ExpressionCachingIntegrationTests.BaseDao<ExpressionCachingIntegrationTests.User> {
        @Override
        @CachePut(value = "users", key = "#user.id")
        public ExpressionCachingIntegrationTests.User persist(ExpressionCachingIntegrationTests.User user) {
            return user;
        }
    }

    private static class OrderDaoImpl implements ExpressionCachingIntegrationTests.BaseDao<ExpressionCachingIntegrationTests.Order> {
        @Override
        @CachePut(value = "orders", key = "#order.id")
        public ExpressionCachingIntegrationTests.Order persist(ExpressionCachingIntegrationTests.Order order) {
            return order;
        }
    }

    private static class User {
        private final String id;

        public User(String id) {
            this.id = id;
        }

        @SuppressWarnings("unused")
        public String getId() {
            return this.id;
        }
    }

    private static class Order {
        private final String id;

        public Order(String id) {
            this.id = id;
        }

        @SuppressWarnings("unused")
        public String getId() {
            return this.id;
        }
    }

    @Configuration
    @EnableCaching
    static class SharedConfig extends CachingConfigurerSupport {
        @Override
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }
}


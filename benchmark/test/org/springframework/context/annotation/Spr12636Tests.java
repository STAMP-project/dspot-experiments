/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.context.annotation;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class Spr12636Tests {
    private ConfigurableApplicationContext context;

    @Test
    public void orderOnImplementation() {
        this.context = new AnnotationConfigApplicationContext(Spr12636Tests.UserServiceTwo.class, Spr12636Tests.UserServiceOne.class, Spr12636Tests.UserServiceCollector.class);
        Spr12636Tests.UserServiceCollector bean = this.context.getBean(Spr12636Tests.UserServiceCollector.class);
        Assert.assertSame(context.getBean("serviceOne", Spr12636Tests.UserService.class), bean.userServices.get(0));
        Assert.assertSame(context.getBean("serviceTwo", Spr12636Tests.UserService.class), bean.userServices.get(1));
    }

    @Test
    public void orderOnImplementationWithProxy() {
        this.context = new AnnotationConfigApplicationContext(Spr12636Tests.UserServiceTwo.class, Spr12636Tests.UserServiceOne.class, Spr12636Tests.UserServiceCollector.class, Spr12636Tests.AsyncConfig.class);
        // Validate those beans are indeed wrapped by a proxy
        Spr12636Tests.UserService serviceOne = this.context.getBean("serviceOne", Spr12636Tests.UserService.class);
        Spr12636Tests.UserService serviceTwo = this.context.getBean("serviceTwo", Spr12636Tests.UserService.class);
        Assert.assertTrue(AopUtils.isAopProxy(serviceOne));
        Assert.assertTrue(AopUtils.isAopProxy(serviceTwo));
        Spr12636Tests.UserServiceCollector bean = this.context.getBean(Spr12636Tests.UserServiceCollector.class);
        Assert.assertSame(serviceOne, bean.userServices.get(0));
        Assert.assertSame(serviceTwo, bean.userServices.get(1));
    }

    @Configuration
    @EnableAsync
    static class AsyncConfig {}

    @Component
    static class UserServiceCollector {
        public final List<Spr12636Tests.UserService> userServices;

        @Autowired
        UserServiceCollector(List<Spr12636Tests.UserService> userServices) {
            this.userServices = userServices;
        }
    }

    interface UserService {
        void doIt();
    }

    @Component("serviceOne")
    @Order(1)
    static class UserServiceOne implements Spr12636Tests.UserService {
        @Async
        @Override
        public void doIt() {
        }
    }

    @Component("serviceTwo")
    @Order(2)
    static class UserServiceTwo implements Spr12636Tests.UserService {
        @Async
        @Override
        public void doIt() {
        }
    }
}


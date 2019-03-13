/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.context.annotation.configuration;


import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 *
 *
 * @author Marcin Piela
 * @author Juergen Hoeller
 */
public class Spr12526Tests {
    @Test
    public void testInjection() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Spr12526Tests.TestContext.class);
        Spr12526Tests.CustomCondition condition = ctx.getBean(Spr12526Tests.CustomCondition.class);
        condition.setCondition(true);
        Spr12526Tests.FirstService firstService = ((Spr12526Tests.FirstService) (ctx.getBean(Spr12526Tests.Service.class)));
        Assert.assertNotNull("FirstService.dependency is null", firstService.getDependency());
        condition.setCondition(false);
        Spr12526Tests.SecondService secondService = ((Spr12526Tests.SecondService) (ctx.getBean(Spr12526Tests.Service.class)));
        Assert.assertNotNull("SecondService.dependency is null", secondService.getDependency());
    }

    @Configuration
    public static class TestContext {
        @Bean
        @Scope(SCOPE_SINGLETON)
        public Spr12526Tests.CustomCondition condition() {
            return new Spr12526Tests.CustomCondition();
        }

        @Bean
        @Scope(SCOPE_PROTOTYPE)
        public Spr12526Tests.Service service(Spr12526Tests.CustomCondition condition) {
            return condition.check() ? new Spr12526Tests.FirstService() : new Spr12526Tests.SecondService();
        }

        @Bean
        public Spr12526Tests.DependencyOne dependencyOne() {
            return new Spr12526Tests.DependencyOne();
        }

        @Bean
        public Spr12526Tests.DependencyTwo dependencyTwo() {
            return new Spr12526Tests.DependencyTwo();
        }
    }

    public static class CustomCondition {
        private boolean condition;

        public boolean check() {
            return condition;
        }

        public void setCondition(boolean value) {
            this.condition = value;
        }
    }

    public interface Service {
        void doStuff();
    }

    public static class FirstService implements Spr12526Tests.Service {
        private Spr12526Tests.DependencyOne dependency;

        @Override
        public void doStuff() {
            if ((dependency) == null) {
                throw new IllegalStateException("FirstService: dependency is null");
            }
        }

        @Resource(name = "dependencyOne")
        public void setDependency(Spr12526Tests.DependencyOne dependency) {
            this.dependency = dependency;
        }

        public Spr12526Tests.DependencyOne getDependency() {
            return dependency;
        }
    }

    public static class SecondService implements Spr12526Tests.Service {
        private Spr12526Tests.DependencyTwo dependency;

        @Override
        public void doStuff() {
            if ((dependency) == null) {
                throw new IllegalStateException("SecondService: dependency is null");
            }
        }

        @Resource(name = "dependencyTwo")
        public void setDependency(Spr12526Tests.DependencyTwo dependency) {
            this.dependency = dependency;
        }

        public Spr12526Tests.DependencyTwo getDependency() {
            return dependency;
        }
    }

    public static class DependencyOne {}

    public static class DependencyTwo {}
}


/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.test.context.junit4.spr9051;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration tests that verify proper scoping of beans created in
 * <em>{@code @Bean} Lite Mode</em>.
 *
 * @author Sam Brannen
 * @since 3.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AtBeanLiteModeScopeTests.LiteBeans.class)
public class AtBeanLiteModeScopeTests {
    /**
     * This is intentionally <b>not</b> annotated with {@code @Configuration}.
     */
    static class LiteBeans {
        @Bean
        public LifecycleBean singleton() {
            LifecycleBean bean = new LifecycleBean("singleton");
            Assert.assertFalse(bean.isInitialized());
            return bean;
        }

        @Bean
        @Scope("prototype")
        public LifecycleBean prototype() {
            LifecycleBean bean = new LifecycleBean("prototype");
            Assert.assertFalse(bean.isInitialized());
            return bean;
        }
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("singleton")
    private LifecycleBean injectedSingletonBean;

    @Autowired
    @Qualifier("prototype")
    private LifecycleBean injectedPrototypeBean;

    @Test
    public void singletonLiteBean() {
        Assert.assertNotNull(injectedSingletonBean);
        Assert.assertTrue(injectedSingletonBean.isInitialized());
        LifecycleBean retrievedSingletonBean = applicationContext.getBean("singleton", LifecycleBean.class);
        Assert.assertNotNull(retrievedSingletonBean);
        Assert.assertTrue(retrievedSingletonBean.isInitialized());
        Assert.assertSame(injectedSingletonBean, retrievedSingletonBean);
    }

    @Test
    public void prototypeLiteBean() {
        Assert.assertNotNull(injectedPrototypeBean);
        Assert.assertTrue(injectedPrototypeBean.isInitialized());
        LifecycleBean retrievedPrototypeBean = applicationContext.getBean("prototype", LifecycleBean.class);
        Assert.assertNotNull(retrievedPrototypeBean);
        Assert.assertTrue(retrievedPrototypeBean.isInitialized());
        Assert.assertNotSame(injectedPrototypeBean, retrievedPrototypeBean);
    }
}


/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.transaction.annotation;


import TransactionManagementConfigUtils.TRANSACTIONAL_EVENT_LISTENER_FACTORY_BEAN_NAME;
import java.util.Collection;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Service;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.event.TransactionalEventListenerFactory;

import static ConfigurationPhase.REGISTER_BEAN;


/**
 * Tests demonstrating use of @EnableTransactionManagement @Configuration classes.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @since 3.1
 */
public class EnableTransactionManagementTests {
    @Test
    public void transactionProxyIsCreated() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.EnableTxConfig.class, EnableTransactionManagementTests.TxManagerConfig.class);
        EnableTransactionManagementTests.TransactionalTestBean bean = ctx.getBean(EnableTransactionManagementTests.TransactionalTestBean.class);
        Assert.assertTrue("testBean is not a proxy", AopUtils.isAopProxy(bean));
        Map<?, ?> services = ctx.getBeansWithAnnotation(Service.class);
        Assert.assertTrue("Stereotype annotation not visible", services.containsKey("testBean"));
        ctx.close();
    }

    @Test
    public void transactionProxyIsCreatedWithEnableOnSuperclass() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.InheritedEnableTxConfig.class, EnableTransactionManagementTests.TxManagerConfig.class);
        EnableTransactionManagementTests.TransactionalTestBean bean = ctx.getBean(EnableTransactionManagementTests.TransactionalTestBean.class);
        Assert.assertTrue("testBean is not a proxy", AopUtils.isAopProxy(bean));
        Map<?, ?> services = ctx.getBeansWithAnnotation(Service.class);
        Assert.assertTrue("Stereotype annotation not visible", services.containsKey("testBean"));
        ctx.close();
    }

    @Test
    public void transactionProxyIsCreatedWithEnableOnExcludedSuperclass() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.ParentEnableTxConfig.class, EnableTransactionManagementTests.ChildEnableTxConfig.class, EnableTransactionManagementTests.TxManagerConfig.class);
        EnableTransactionManagementTests.TransactionalTestBean bean = ctx.getBean(EnableTransactionManagementTests.TransactionalTestBean.class);
        Assert.assertTrue("testBean is not a proxy", AopUtils.isAopProxy(bean));
        Map<?, ?> services = ctx.getBeansWithAnnotation(Service.class);
        Assert.assertTrue("Stereotype annotation not visible", services.containsKey("testBean"));
        ctx.close();
    }

    @Test
    public void txManagerIsResolvedOnInvocationOfTransactionalMethod() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.EnableTxConfig.class, EnableTransactionManagementTests.TxManagerConfig.class);
        EnableTransactionManagementTests.TransactionalTestBean bean = ctx.getBean(EnableTransactionManagementTests.TransactionalTestBean.class);
        // invoke a transactional method, causing the PlatformTransactionManager bean to be resolved.
        bean.findAllFoos();
        ctx.close();
    }

    @Test
    public void txManagerIsResolvedCorrectlyWhenMultipleManagersArePresent() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.EnableTxConfig.class, EnableTransactionManagementTests.MultiTxManagerConfig.class);
        EnableTransactionManagementTests.TransactionalTestBean bean = ctx.getBean(EnableTransactionManagementTests.TransactionalTestBean.class);
        // invoke a transactional method, causing the PlatformTransactionManager bean to be resolved.
        bean.findAllFoos();
        ctx.close();
    }

    /**
     * A cheap test just to prove that in ASPECTJ mode, the AnnotationTransactionAspect does indeed
     * get loaded -- or in this case, attempted to be loaded at which point the test fails.
     */
    @Test
    @SuppressWarnings("resource")
    public void proxyTypeAspectJCausesRegistrationOfAnnotationTransactionAspect() {
        try {
            new AnnotationConfigApplicationContext(EnableTransactionManagementTests.EnableAspectjTxConfig.class, EnableTransactionManagementTests.TxManagerConfig.class);
            Assert.fail(("should have thrown CNFE when trying to load AnnotationTransactionAspect. " + "Do you actually have org.springframework.aspects on the classpath?"));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("AspectJJtaTransactionManagementConfiguration"));
        }
    }

    @Test
    public void transactionalEventListenerRegisteredProperly() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.EnableTxConfig.class);
        Assert.assertTrue(ctx.containsBean(TRANSACTIONAL_EVENT_LISTENER_FACTORY_BEAN_NAME));
        Assert.assertEquals(1, ctx.getBeansOfType(TransactionalEventListenerFactory.class).size());
        ctx.close();
    }

    @Test
    public void spr11915TransactionManagerAsManualSingleton() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.Spr11915Config.class);
        EnableTransactionManagementTests.TransactionalTestBean bean = ctx.getBean(EnableTransactionManagementTests.TransactionalTestBean.class);
        CallCountingTransactionManager txManager = ctx.getBean("qualifiedTransactionManager", CallCountingTransactionManager.class);
        bean.saveQualifiedFoo();
        Assert.assertThat(txManager.begun, CoreMatchers.equalTo(1));
        Assert.assertThat(txManager.commits, CoreMatchers.equalTo(1));
        Assert.assertThat(txManager.rollbacks, CoreMatchers.equalTo(0));
        bean.saveQualifiedFooWithAttributeAlias();
        Assert.assertThat(txManager.begun, CoreMatchers.equalTo(2));
        Assert.assertThat(txManager.commits, CoreMatchers.equalTo(2));
        Assert.assertThat(txManager.rollbacks, CoreMatchers.equalTo(0));
        ctx.close();
    }

    @Test
    public void spr14322FindsOnInterfaceWithInterfaceProxy() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.Spr14322ConfigA.class);
        EnableTransactionManagementTests.TransactionalTestInterface bean = ctx.getBean(EnableTransactionManagementTests.TransactionalTestInterface.class);
        CallCountingTransactionManager txManager = ctx.getBean(CallCountingTransactionManager.class);
        bean.saveFoo();
        bean.saveBar();
        Assert.assertThat(txManager.begun, CoreMatchers.equalTo(2));
        Assert.assertThat(txManager.commits, CoreMatchers.equalTo(2));
        Assert.assertThat(txManager.rollbacks, CoreMatchers.equalTo(0));
        ctx.close();
    }

    @Test
    public void spr14322FindsOnInterfaceWithCglibProxy() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableTransactionManagementTests.Spr14322ConfigB.class);
        EnableTransactionManagementTests.TransactionalTestInterface bean = ctx.getBean(EnableTransactionManagementTests.TransactionalTestInterface.class);
        CallCountingTransactionManager txManager = ctx.getBean(CallCountingTransactionManager.class);
        bean.saveFoo();
        bean.saveBar();
        Assert.assertThat(txManager.begun, CoreMatchers.equalTo(2));
        Assert.assertThat(txManager.commits, CoreMatchers.equalTo(2));
        Assert.assertThat(txManager.rollbacks, CoreMatchers.equalTo(0));
        ctx.close();
    }

    @Service
    public static class TransactionalTestBean {
        @Transactional(readOnly = true)
        public Collection<?> findAllFoos() {
            return null;
        }

        @Transactional("qualifiedTransactionManager")
        public void saveQualifiedFoo() {
        }

        @Transactional(transactionManager = "qualifiedTransactionManager")
        public void saveQualifiedFooWithAttributeAlias() {
        }
    }

    @Configuration
    @EnableTransactionManagement
    static class EnableTxConfig {}

    @Configuration
    static class InheritedEnableTxConfig extends EnableTransactionManagementTests.EnableTxConfig {}

    @Configuration
    @EnableTransactionManagement
    @Conditional(EnableTransactionManagementTests.NeverCondition.class)
    static class ParentEnableTxConfig {
        @Bean
        Object someBean() {
            return new Object();
        }
    }

    @Configuration
    static class ChildEnableTxConfig extends EnableTransactionManagementTests.ParentEnableTxConfig {
        @Override
        Object someBean() {
            return "X";
        }
    }

    private static class NeverCondition implements ConfigurationCondition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return false;
        }

        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return REGISTER_BEAN;
        }
    }

    @Configuration
    @EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
    static class EnableAspectjTxConfig {}

    @Configuration
    static class TxManagerConfig {
        @Bean
        public EnableTransactionManagementTests.TransactionalTestBean testBean() {
            return new EnableTransactionManagementTests.TransactionalTestBean();
        }

        @Bean
        public PlatformTransactionManager txManager() {
            return new CallCountingTransactionManager();
        }
    }

    @Configuration
    static class MultiTxManagerConfig extends EnableTransactionManagementTests.TxManagerConfig implements TransactionManagementConfigurer {
        @Bean
        public PlatformTransactionManager txManager2() {
            return new CallCountingTransactionManager();
        }

        @Override
        public PlatformTransactionManager annotationDrivenTransactionManager() {
            return txManager2();
        }
    }

    @Configuration
    @EnableTransactionManagement
    static class Spr11915Config {
        @Autowired
        public void initializeApp(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().registerSingleton("qualifiedTransactionManager", new CallCountingTransactionManager());
        }

        @Bean
        public EnableTransactionManagementTests.TransactionalTestBean testBean() {
            return new EnableTransactionManagementTests.TransactionalTestBean();
        }
    }

    public interface BaseTransactionalInterface {
        @Transactional
        default void saveBar() {
        }
    }

    public interface TransactionalTestInterface extends EnableTransactionManagementTests.BaseTransactionalInterface {
        @Transactional
        void saveFoo();
    }

    @Service
    public static class TransactionalTestService implements EnableTransactionManagementTests.TransactionalTestInterface {
        @Override
        public void saveFoo() {
        }
    }

    @Configuration
    @EnableTransactionManagement
    static class Spr14322ConfigA {
        @Bean
        public EnableTransactionManagementTests.TransactionalTestInterface testBean() {
            return new EnableTransactionManagementTests.TransactionalTestService();
        }

        @Bean
        public PlatformTransactionManager txManager() {
            return new CallCountingTransactionManager();
        }
    }

    @Configuration
    @EnableTransactionManagement(proxyTargetClass = true)
    static class Spr14322ConfigB {
        @Bean
        public EnableTransactionManagementTests.TransactionalTestInterface testBean() {
            return new EnableTransactionManagementTests.TransactionalTestService();
        }

        @Bean
        public PlatformTransactionManager txManager() {
            return new CallCountingTransactionManager();
        }
    }
}


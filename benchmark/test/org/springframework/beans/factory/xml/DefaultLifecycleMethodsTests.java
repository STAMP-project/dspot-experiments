/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.beans.factory.xml;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class DefaultLifecycleMethodsTests {
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void lifecycleMethodsInvoked() {
        DefaultLifecycleMethodsTests.LifecycleAwareBean bean = ((DefaultLifecycleMethodsTests.LifecycleAwareBean) (this.beanFactory.getBean("lifecycleAware")));
        Assert.assertTrue("Bean not initialized", bean.isInitCalled());
        Assert.assertFalse("Custom init method called incorrectly", bean.isCustomInitCalled());
        Assert.assertFalse("Bean destroyed too early", bean.isDestroyCalled());
        this.beanFactory.destroySingletons();
        Assert.assertTrue("Bean not destroyed", bean.isDestroyCalled());
        Assert.assertFalse("Custom destroy method called incorrectly", bean.isCustomDestroyCalled());
    }

    @Test
    public void lifecycleMethodsDisabled() throws Exception {
        DefaultLifecycleMethodsTests.LifecycleAwareBean bean = ((DefaultLifecycleMethodsTests.LifecycleAwareBean) (this.beanFactory.getBean("lifecycleMethodsDisabled")));
        Assert.assertFalse("Bean init method called incorrectly", bean.isInitCalled());
        Assert.assertFalse("Custom init method called incorrectly", bean.isCustomInitCalled());
        this.beanFactory.destroySingletons();
        Assert.assertFalse("Bean destroy method called incorrectly", bean.isDestroyCalled());
        Assert.assertFalse("Custom destroy method called incorrectly", bean.isCustomDestroyCalled());
    }

    @Test
    public void ignoreDefaultLifecycleMethods() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("ignoreDefaultLifecycleMethods.xml", getClass()));
        bf.preInstantiateSingletons();
        bf.destroySingletons();
    }

    @Test
    public void overrideDefaultLifecycleMethods() throws Exception {
        DefaultLifecycleMethodsTests.LifecycleAwareBean bean = ((DefaultLifecycleMethodsTests.LifecycleAwareBean) (this.beanFactory.getBean("overrideLifecycleMethods")));
        Assert.assertFalse("Default init method called incorrectly", bean.isInitCalled());
        Assert.assertTrue("Custom init method not called", bean.isCustomInitCalled());
        this.beanFactory.destroySingletons();
        Assert.assertFalse("Default destroy method called incorrectly", bean.isDestroyCalled());
        Assert.assertTrue("Custom destroy method not called", bean.isCustomDestroyCalled());
    }

    @Test
    public void childWithDefaultLifecycleMethods() throws Exception {
        DefaultLifecycleMethodsTests.LifecycleAwareBean bean = ((DefaultLifecycleMethodsTests.LifecycleAwareBean) (this.beanFactory.getBean("childWithDefaultLifecycleMethods")));
        Assert.assertTrue("Bean not initialized", bean.isInitCalled());
        Assert.assertFalse("Custom init method called incorrectly", bean.isCustomInitCalled());
        Assert.assertFalse("Bean destroyed too early", bean.isDestroyCalled());
        this.beanFactory.destroySingletons();
        Assert.assertTrue("Bean not destroyed", bean.isDestroyCalled());
        Assert.assertFalse("Custom destroy method called incorrectly", bean.isCustomDestroyCalled());
    }

    @Test
    public void childWithLifecycleMethodsDisabled() throws Exception {
        DefaultLifecycleMethodsTests.LifecycleAwareBean bean = ((DefaultLifecycleMethodsTests.LifecycleAwareBean) (this.beanFactory.getBean("childWithLifecycleMethodsDisabled")));
        Assert.assertFalse("Bean init method called incorrectly", bean.isInitCalled());
        Assert.assertFalse("Custom init method called incorrectly", bean.isCustomInitCalled());
        this.beanFactory.destroySingletons();
        Assert.assertFalse("Bean destroy method called incorrectly", bean.isDestroyCalled());
        Assert.assertFalse("Custom destroy method called incorrectly", bean.isCustomDestroyCalled());
    }

    public static class LifecycleAwareBean {
        private boolean initCalled;

        private boolean destroyCalled;

        private boolean customInitCalled;

        private boolean customDestroyCalled;

        public void init() {
            this.initCalled = true;
        }

        public void destroy() {
            this.destroyCalled = true;
        }

        public void customInit() {
            this.customInitCalled = true;
        }

        public void customDestroy() {
            this.customDestroyCalled = true;
        }

        public boolean isInitCalled() {
            return initCalled;
        }

        public boolean isDestroyCalled() {
            return destroyCalled;
        }

        public boolean isCustomInitCalled() {
            return customInitCalled;
        }

        public boolean isCustomDestroyCalled() {
            return customDestroyCalled;
        }
    }
}


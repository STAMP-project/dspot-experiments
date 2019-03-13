/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.beans.factory.support.security;


import ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.beans.factory.support.security.support.ConstructorBean;
import org.springframework.beans.factory.support.security.support.CustomCallbackBean;


/**
 * Security test case. Checks whether the container uses its privileges for its
 * internal work but does not leak them when touching/calling user code.
 *
 * t The first half of the test case checks that permissions are downgraded when
 * calling user code while the second half that the caller code permission get
 * through and Spring doesn't override the permission stack.
 *
 * @author Costin Leau
 */
public class CallbacksSecurityTests {
    private DefaultListableBeanFactory beanFactory;

    private SecurityContextProvider provider;

    @SuppressWarnings("unused")
    private static class NonPrivilegedBean {
        private String expectedName;

        public static boolean destroyed = false;

        public NonPrivilegedBean(String expected) {
            this.expectedName = expected;
            checkCurrentContext();
        }

        public void init() {
            checkCurrentContext();
        }

        public void destroy() {
            checkCurrentContext();
            CallbacksSecurityTests.NonPrivilegedBean.destroyed = true;
        }

        public void setProperty(Object value) {
            checkCurrentContext();
        }

        public Object getProperty() {
            checkCurrentContext();
            return null;
        }

        public void setListProperty(Object value) {
            checkCurrentContext();
        }

        public Object getListProperty() {
            checkCurrentContext();
            return null;
        }

        private void checkCurrentContext() {
            Assert.assertEquals(expectedName, CallbacksSecurityTests.getCurrentSubjectName());
        }
    }

    @SuppressWarnings("unused")
    private static class NonPrivilegedSpringCallbacksBean implements BeanClassLoaderAware , BeanFactoryAware , BeanNameAware , DisposableBean , InitializingBean {
        private String expectedName;

        public static boolean destroyed = false;

        public NonPrivilegedSpringCallbacksBean(String expected) {
            this.expectedName = expected;
            checkCurrentContext();
        }

        @Override
        public void afterPropertiesSet() {
            checkCurrentContext();
        }

        @Override
        public void destroy() {
            checkCurrentContext();
            CallbacksSecurityTests.NonPrivilegedSpringCallbacksBean.destroyed = true;
        }

        @Override
        public void setBeanName(String name) {
            checkCurrentContext();
        }

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            checkCurrentContext();
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            checkCurrentContext();
        }

        private void checkCurrentContext() {
            Assert.assertEquals(expectedName, CallbacksSecurityTests.getCurrentSubjectName());
        }
    }

    @SuppressWarnings("unused")
    private static class NonPrivilegedFactoryBean implements SmartFactoryBean {
        private String expectedName;

        public NonPrivilegedFactoryBean(String expected) {
            this.expectedName = expected;
            checkCurrentContext();
        }

        @Override
        public boolean isEagerInit() {
            checkCurrentContext();
            return false;
        }

        @Override
        public boolean isPrototype() {
            checkCurrentContext();
            return true;
        }

        @Override
        public Object getObject() throws Exception {
            checkCurrentContext();
            return new Object();
        }

        @Override
        public Class getObjectType() {
            checkCurrentContext();
            return Object.class;
        }

        @Override
        public boolean isSingleton() {
            checkCurrentContext();
            return false;
        }

        private void checkCurrentContext() {
            Assert.assertEquals(expectedName, CallbacksSecurityTests.getCurrentSubjectName());
        }
    }

    @SuppressWarnings("unused")
    private static class NonPrivilegedFactory {
        private final String expectedName;

        public NonPrivilegedFactory(String expected) {
            this.expectedName = expected;
            Assert.assertEquals(expectedName, CallbacksSecurityTests.getCurrentSubjectName());
        }

        public static Object makeStaticInstance(String expectedName) {
            Assert.assertEquals(expectedName, CallbacksSecurityTests.getCurrentSubjectName());
            return new Object();
        }

        public Object makeInstance() {
            Assert.assertEquals(expectedName, CallbacksSecurityTests.getCurrentSubjectName());
            return new Object();
        }
    }

    private static class TestPrincipal implements Principal {
        private String name;

        public TestPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == (this)) {
                return true;
            }
            if (!(obj instanceof CallbacksSecurityTests.TestPrincipal)) {
                return false;
            }
            CallbacksSecurityTests.TestPrincipal p = ((CallbacksSecurityTests.TestPrincipal) (obj));
            return this.name.equals(p.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }

    public CallbacksSecurityTests() {
        // setup security
        if ((System.getSecurityManager()) == null) {
            Policy policy = Policy.getPolicy();
            URL policyURL = getClass().getResource("/org/springframework/beans/factory/support/security/policy.all");
            System.setProperty("java.security.policy", policyURL.toString());
            System.setProperty("policy.allowSystemProperty", "true");
            policy.refresh();
            System.setSecurityManager(new SecurityManager());
        }
    }

    @Test
    public void testSecuritySanity() throws Exception {
        AccessControlContext acc = provider.getAccessControlContext();
        try {
            acc.checkPermission(new PropertyPermission("*", "read"));
            Assert.fail("Acc should not have any permissions");
        } catch (SecurityException se) {
            // expected
        }
        final CustomCallbackBean bean = new CustomCallbackBean();
        final Method method = bean.getClass().getMethod("destroy");
        method.setAccessible(true);
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    method.invoke(bean);
                    return null;
                }
            }, acc);
            Assert.fail("expected security exception");
        } catch (Exception ex) {
        }
        final Class<ConstructorBean> cl = ConstructorBean.class;
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    return cl.newInstance();
                }
            }, acc);
            Assert.fail("expected security exception");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testSpringInitBean() throws Exception {
        try {
            beanFactory.getBean("spring-init");
            Assert.fail("expected security exception");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof SecurityException));
        }
    }

    @Test
    public void testCustomInitBean() throws Exception {
        try {
            beanFactory.getBean("custom-init");
            Assert.fail("expected security exception");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof SecurityException));
        }
    }

    @Test
    public void testSpringDestroyBean() throws Exception {
        beanFactory.getBean("spring-destroy");
        beanFactory.destroySingletons();
        Assert.assertNull(System.getProperty("security.destroy"));
    }

    @Test
    public void testCustomDestroyBean() throws Exception {
        beanFactory.getBean("custom-destroy");
        beanFactory.destroySingletons();
        Assert.assertNull(System.getProperty("security.destroy"));
    }

    @Test
    public void testCustomFactoryObject() throws Exception {
        try {
            beanFactory.getBean("spring-factory");
            Assert.fail("expected security exception");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof SecurityException));
        }
    }

    @Test
    public void testCustomFactoryType() throws Exception {
        Assert.assertNull(beanFactory.getType("spring-factory"));
        Assert.assertNull(System.getProperty("factory.object.type"));
    }

    @Test
    public void testCustomStaticFactoryMethod() throws Exception {
        try {
            beanFactory.getBean("custom-static-factory-method");
            Assert.fail("expected security exception");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getMostSpecificCause()) instanceof SecurityException));
        }
    }

    @Test
    public void testCustomInstanceFactoryMethod() throws Exception {
        try {
            beanFactory.getBean("custom-factory-method");
            Assert.fail("expected security exception");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getMostSpecificCause()) instanceof SecurityException));
        }
    }

    @Test
    public void testTrustedFactoryMethod() throws Exception {
        try {
            beanFactory.getBean("privileged-static-factory-method");
            Assert.fail("expected security exception");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getMostSpecificCause()) instanceof SecurityException));
        }
    }

    @Test
    public void testConstructor() throws Exception {
        try {
            beanFactory.getBean("constructor");
            Assert.fail("expected security exception");
        } catch (BeanCreationException ex) {
            // expected
            Assert.assertTrue(((ex.getMostSpecificCause()) instanceof SecurityException));
        }
    }

    @Test
    public void testContainerPrivileges() throws Exception {
        AccessControlContext acc = provider.getAccessControlContext();
        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                beanFactory.getBean("working-factory-method");
                beanFactory.getBean("container-execution");
                return null;
            }
        }, acc);
    }

    @Test
    public void testPropertyInjection() throws Exception {
        try {
            beanFactory.getBean("property-injection");
            Assert.fail("expected security exception");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.getMessage().contains("security"));
        }
        beanFactory.getBean("working-property-injection");
    }

    @Test
    public void testInitSecurityAwarePrototypeBean() {
        final DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(CallbacksSecurityTests.NonPrivilegedBean.class).setScope(SCOPE_PROTOTYPE).setInitMethodName("init").setDestroyMethodName("destroy").addConstructorArgValue("user1");
        lbf.registerBeanDefinition("test", bdb.getBeanDefinition());
        final Subject subject = new Subject();
        subject.getPrincipals().add(new CallbacksSecurityTests.TestPrincipal("user1"));
        CallbacksSecurityTests.NonPrivilegedBean bean = Subject.doAsPrivileged(subject, new PrivilegedAction<CallbacksSecurityTests.NonPrivilegedBean>() {
            @Override
            public CallbacksSecurityTests.NonPrivilegedBean run() {
                return lbf.getBean("test", CallbacksSecurityTests.NonPrivilegedBean.class);
            }
        }, null);
        Assert.assertNotNull(bean);
    }

    @Test
    public void testTrustedExecution() throws Exception {
        beanFactory.setSecurityContextProvider(null);
        Permissions perms = new Permissions();
        perms.add(new AuthPermission("getSubject"));
        ProtectionDomain pd = new ProtectionDomain(null, perms);
        new AccessControlContext(new ProtectionDomain[]{ pd });
        final Subject subject = new Subject();
        subject.getPrincipals().add(new CallbacksSecurityTests.TestPrincipal("user1"));
        // request the beans from non-privileged code
        Subject.doAsPrivileged(subject, new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                // sanity check
                Assert.assertEquals("user1", CallbacksSecurityTests.getCurrentSubjectName());
                Assert.assertEquals(false, CallbacksSecurityTests.NonPrivilegedBean.destroyed);
                beanFactory.getBean("trusted-spring-callbacks");
                beanFactory.getBean("trusted-custom-init-destroy");
                // the factory is a prototype - ask for multiple instances
                beanFactory.getBean("trusted-spring-factory");
                beanFactory.getBean("trusted-spring-factory");
                beanFactory.getBean("trusted-spring-factory");
                beanFactory.getBean("trusted-factory-bean");
                beanFactory.getBean("trusted-static-factory-method");
                beanFactory.getBean("trusted-factory-method");
                beanFactory.getBean("trusted-property-injection");
                beanFactory.getBean("trusted-working-property-injection");
                beanFactory.destroySingletons();
                Assert.assertEquals(true, CallbacksSecurityTests.NonPrivilegedBean.destroyed);
                return null;
            }
        }, provider.getAccessControlContext());
    }
}


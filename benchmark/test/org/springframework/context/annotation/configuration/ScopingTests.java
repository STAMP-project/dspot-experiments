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
package org.springframework.context.annotation.configuration;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Tests that scopes are properly supported by using a custom Scope implementations
 * and scoped proxy {@link Bean} declarations.
 *
 * @author Costin Leau
 * @author Chris Beams
 */
public class ScopingTests {
    public static String flag = "1";

    private static final String SCOPE = "my scope";

    private ScopingTests.CustomScope customScope;

    private GenericApplicationContext ctx;

    @Test
    public void testScopeOnClasses() throws Exception {
        genericTestScope("scopedClass");
    }

    @Test
    public void testScopeOnInterfaces() throws Exception {
        genericTestScope("scopedInterface");
    }

    @Test
    public void testSameScopeOnDifferentBeans() throws Exception {
        Object beanAInScope = ctx.getBean("scopedClass");
        Object beanBInScope = ctx.getBean("scopedInterface");
        Assert.assertNotSame(beanAInScope, beanBInScope);
        customScope.createNewScope = true;
        Object newBeanAInScope = ctx.getBean("scopedClass");
        Object newBeanBInScope = ctx.getBean("scopedInterface");
        Assert.assertNotSame(newBeanAInScope, newBeanBInScope);
        Assert.assertNotSame(newBeanAInScope, beanAInScope);
        Assert.assertNotSame(newBeanBInScope, beanBInScope);
    }

    @Test
    public void testRawScopes() throws Exception {
        String beanName = "scopedProxyInterface";
        // get hidden bean
        Object bean = ctx.getBean(("scopedTarget." + beanName));
        Assert.assertFalse((bean instanceof ScopedObject));
    }

    @Test
    public void testScopedProxyConfiguration() throws Exception {
        TestBean singleton = ((TestBean) (ctx.getBean("singletonWithScopedInterfaceDep")));
        ITestBean spouse = singleton.getSpouse();
        Assert.assertTrue("scoped bean is not wrapped by the scoped-proxy", (spouse instanceof ScopedObject));
        String beanName = "scopedProxyInterface";
        String scopedBeanName = "scopedTarget." + beanName;
        // get hidden bean
        Assert.assertEquals(ScopingTests.flag, spouse.getName());
        ITestBean spouseFromBF = ((ITestBean) (ctx.getBean(scopedBeanName)));
        Assert.assertEquals(spouse.getName(), spouseFromBF.getName());
        // the scope proxy has kicked in
        Assert.assertNotSame(spouse, spouseFromBF);
        // create a new bean
        customScope.createNewScope = true;
        // get the bean again from the BF
        spouseFromBF = ((ITestBean) (ctx.getBean(scopedBeanName)));
        // make sure the name has been updated
        Assert.assertSame(spouse.getName(), spouseFromBF.getName());
        Assert.assertNotSame(spouse, spouseFromBF);
        // get the bean again
        spouseFromBF = ((ITestBean) (ctx.getBean(scopedBeanName)));
        Assert.assertSame(spouse.getName(), spouseFromBF.getName());
    }

    @Test
    public void testScopedProxyConfigurationWithClasses() throws Exception {
        TestBean singleton = ((TestBean) (ctx.getBean("singletonWithScopedClassDep")));
        ITestBean spouse = singleton.getSpouse();
        Assert.assertTrue("scoped bean is not wrapped by the scoped-proxy", (spouse instanceof ScopedObject));
        String beanName = "scopedProxyClass";
        String scopedBeanName = "scopedTarget." + beanName;
        // get hidden bean
        Assert.assertEquals(ScopingTests.flag, spouse.getName());
        TestBean spouseFromBF = ((TestBean) (ctx.getBean(scopedBeanName)));
        Assert.assertEquals(spouse.getName(), spouseFromBF.getName());
        // the scope proxy has kicked in
        Assert.assertNotSame(spouse, spouseFromBF);
        // create a new bean
        customScope.createNewScope = true;
        ScopingTests.flag = "boo";
        // get the bean again from the BF
        spouseFromBF = ((TestBean) (ctx.getBean(scopedBeanName)));
        // make sure the name has been updated
        Assert.assertSame(spouse.getName(), spouseFromBF.getName());
        Assert.assertNotSame(spouse, spouseFromBF);
        // get the bean again
        spouseFromBF = ((TestBean) (ctx.getBean(scopedBeanName)));
        Assert.assertSame(spouse.getName(), spouseFromBF.getName());
    }

    static class Foo {
        public Foo() {
        }

        public void doSomething() {
        }
    }

    static class Bar {
        private final ScopingTests.Foo foo;

        public Bar(ScopingTests.Foo foo) {
            this.foo = foo;
        }

        public ScopingTests.Foo getFoo() {
            return foo;
        }
    }

    @Configuration
    public static class InvalidProxyOnPredefinedScopesConfiguration {
        @Bean
        @Scope(proxyMode = ScopedProxyMode.INTERFACES)
        public Object invalidProxyOnPredefinedScopes() {
            return new Object();
        }
    }

    @Configuration
    public static class ScopedConfigurationClass {
        @Bean
        @ScopingTests.MyScope
        public TestBean scopedClass() {
            TestBean tb = new TestBean();
            tb.setName(ScopingTests.flag);
            return tb;
        }

        @Bean
        @ScopingTests.MyScope
        public ITestBean scopedInterface() {
            TestBean tb = new TestBean();
            tb.setName(ScopingTests.flag);
            return tb;
        }

        @Bean
        @ScopingTests.MyProxiedScope
        public ITestBean scopedProxyInterface() {
            TestBean tb = new TestBean();
            tb.setName(ScopingTests.flag);
            return tb;
        }

        @ScopingTests.MyProxiedScope
        public TestBean scopedProxyClass() {
            TestBean tb = new TestBean();
            tb.setName(ScopingTests.flag);
            return tb;
        }

        @Bean
        public TestBean singletonWithScopedClassDep() {
            TestBean singleton = new TestBean();
            singleton.setSpouse(scopedProxyClass());
            return singleton;
        }

        @Bean
        public TestBean singletonWithScopedInterfaceDep() {
            TestBean singleton = new TestBean();
            singleton.setSpouse(scopedProxyInterface());
            return singleton;
        }
    }

    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Scope(ScopingTests.SCOPE)
    @interface MyScope {}

    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Bean
    @Scope(value = ScopingTests.SCOPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @interface MyProxiedScope {}

    /**
     * Simple scope implementation which creates object based on a flag.
     *
     * @author Costin Leau
     * @author Chris Beams
     */
    static class CustomScope implements org.springframework.beans.factory.config.Scope {
        public boolean createNewScope = true;

        private Map<String, Object> beans = new HashMap<>();

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            if (createNewScope) {
                beans.clear();
                // reset the flag back
                createNewScope = false;
            }
            Object bean = beans.get(name);
            // if a new object is requested or none exists under the current
            // name, create one
            if (bean == null) {
                beans.put(name, objectFactory.getObject());
            }
            return beans.get(name);
        }

        @Override
        public String getConversationId() {
            return null;
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
            throw new IllegalStateException("Not supposed to be called");
        }

        @Override
        public Object remove(String name) {
            return beans.remove(name);
        }

        @Override
        public Object resolveContextualObject(String key) {
            return null;
        }
    }
}


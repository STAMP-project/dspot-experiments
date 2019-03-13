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
package org.springframework.transaction.annotation;


import TransactionAttribute.ISOLATION_REPEATABLE_READ;
import TransactionAttribute.PROPAGATION_REQUIRED;
import TransactionAttribute.PROPAGATION_REQUIRES_NEW;
import TransactionAttribute.PROPAGATION_SUPPORTS;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Transactional.TxType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.SerializationTestUtils;

import static Isolation.REPEATABLE_READ;
import static Propagation.REQUIRES_NEW;


/**
 *
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class AnnotationTransactionAttributeSourceTests {
    @Test
    public void serializable() throws Exception {
        AnnotationTransactionAttributeSourceTests.TestBean1 tb = new AnnotationTransactionAttributeSourceTests.TestBean1();
        CallCountingTransactionManager ptm = new CallCountingTransactionManager();
        AnnotationTransactionAttributeSource tas = new AnnotationTransactionAttributeSource();
        TransactionInterceptor ti = new TransactionInterceptor(ptm, tas);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(AnnotationTransactionAttributeSourceTests.ITestBean1.class);
        proxyFactory.addAdvice(ti);
        proxyFactory.setTarget(tb);
        AnnotationTransactionAttributeSourceTests.ITestBean1 proxy = ((AnnotationTransactionAttributeSourceTests.ITestBean1) (proxyFactory.getProxy()));
        proxy.getAge();
        Assert.assertEquals(1, ptm.commits);
        AnnotationTransactionAttributeSourceTests.ITestBean1 serializedProxy = ((AnnotationTransactionAttributeSourceTests.ITestBean1) (SerializationTestUtils.serializeAndDeserialize(proxy)));
        serializedProxy.getAge();
        Advised advised = ((Advised) (serializedProxy));
        TransactionInterceptor serializedTi = ((TransactionInterceptor) (advised.getAdvisors()[0].getAdvice()));
        CallCountingTransactionManager serializedPtm = ((CallCountingTransactionManager) (serializedTi.getTransactionManager()));
        Assert.assertEquals(2, serializedPtm.commits);
    }

    @Test
    public void nullOrEmpty() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.Empty.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        Assert.assertNull(atas.getTransactionAttribute(method, null));
        // Try again in case of caching
        Assert.assertNull(atas.getTransactionAttribute(method, null));
    }

    /**
     * Test the important case where the invocation is on a proxied interface method
     * but the attribute is defined on the target class.
     */
    @Test
    public void transactionAttributeDeclaredOnClassMethod() throws Exception {
        Method classMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(classMethod, AnnotationTransactionAttributeSourceTests.TestBean1.class);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
    }

    /**
     * Test the important case where the invocation is on a proxied interface method
     * but the attribute is defined on the target class.
     */
    @Test
    public void transactionAttributeDeclaredOnCglibClassMethod() throws Exception {
        Method classMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getAge");
        AnnotationTransactionAttributeSourceTests.TestBean1 tb = new AnnotationTransactionAttributeSourceTests.TestBean1();
        ProxyFactory pf = new ProxyFactory(tb);
        pf.setProxyTargetClass(true);
        Object proxy = pf.getProxy();
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(classMethod, proxy.getClass());
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
    }

    /**
     * Test case where attribute is on the interface method.
     */
    @Test
    public void transactionAttributeDeclaredOnInterfaceMethodOnly() throws Exception {
        Method interfaceMethod = AnnotationTransactionAttributeSourceTests.ITestBean2.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(interfaceMethod, AnnotationTransactionAttributeSourceTests.TestBean2.class);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
    }

    /**
     * Test that when an attribute exists on both class and interface, class takes precedence.
     */
    @Test
    public void transactionAttributeOnTargetClassMethodOverridesAttributeOnInterfaceMethod() throws Exception {
        Method interfaceMethod = AnnotationTransactionAttributeSourceTests.ITestBean3.class.getMethod("getAge");
        Method interfaceMethod2 = AnnotationTransactionAttributeSourceTests.ITestBean3.class.getMethod("getName");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(interfaceMethod, AnnotationTransactionAttributeSourceTests.TestBean3.class);
        Assert.assertEquals(PROPAGATION_REQUIRES_NEW, actual.getPropagationBehavior());
        Assert.assertEquals(ISOLATION_REPEATABLE_READ, actual.getIsolationLevel());
        Assert.assertEquals(5, actual.getTimeout());
        Assert.assertTrue(actual.isReadOnly());
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
        TransactionAttribute actual2 = atas.getTransactionAttribute(interfaceMethod2, AnnotationTransactionAttributeSourceTests.TestBean3.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, actual2.getPropagationBehavior());
    }

    @Test
    public void rollbackRulesAreApplied() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.TestBean3.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(method, AnnotationTransactionAttributeSourceTests.TestBean3.class);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute("java.lang.Exception"));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
        Assert.assertTrue(actual.rollbackOn(new Exception()));
        Assert.assertFalse(actual.rollbackOn(new IOException()));
        actual = atas.getTransactionAttribute(method, method.getDeclaringClass());
        rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute("java.lang.Exception"));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
        Assert.assertTrue(actual.rollbackOn(new Exception()));
        Assert.assertFalse(actual.rollbackOn(new IOException()));
    }

    /**
     * Test that transaction attribute is inherited from class
     * if not specified on method.
     */
    @Test
    public void defaultsToClassTransactionAttribute() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.TestBean4.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(method, AnnotationTransactionAttributeSourceTests.TestBean4.class);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
    }

    @Test
    public void customClassAttributeDetected() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.TestBean5.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(method, AnnotationTransactionAttributeSourceTests.TestBean5.class);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
    }

    @Test
    public void customMethodAttributeDetected() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.TestBean6.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(method, AnnotationTransactionAttributeSourceTests.TestBean6.class);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
    }

    @Test
    public void customClassAttributeWithReadOnlyOverrideDetected() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.TestBean7.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(method, AnnotationTransactionAttributeSourceTests.TestBean7.class);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
        Assert.assertTrue(actual.isReadOnly());
    }

    @Test
    public void customMethodAttributeWithReadOnlyOverrideDetected() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.TestBean8.class.getMethod("getAge");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(method, AnnotationTransactionAttributeSourceTests.TestBean8.class);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
        Assert.assertTrue(actual.isReadOnly());
    }

    @Test
    public void customClassAttributeWithReadOnlyOverrideOnInterface() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.TestInterface9.class.getMethod("getAge");
        Transactional annotation = AnnotationUtils.findAnnotation(method, Transactional.class);
        Assert.assertNull("AnnotationUtils.findAnnotation should not find @Transactional for TestBean9.getAge()", annotation);
        annotation = AnnotationUtils.findAnnotation(AnnotationTransactionAttributeSourceTests.TestBean9.class, Transactional.class);
        Assert.assertNotNull("AnnotationUtils.findAnnotation failed to find @Transactional for TestBean9", annotation);
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(method, AnnotationTransactionAttributeSourceTests.TestBean9.class);
        Assert.assertNotNull("Failed to retrieve TransactionAttribute for TestBean9.getAge()", actual);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
        Assert.assertTrue(actual.isReadOnly());
    }

    @Test
    public void customMethodAttributeWithReadOnlyOverrideOnInterface() throws Exception {
        Method method = AnnotationTransactionAttributeSourceTests.TestInterface10.class.getMethod("getAge");
        Transactional annotation = AnnotationUtils.findAnnotation(method, Transactional.class);
        Assert.assertNotNull("AnnotationUtils.findAnnotation failed to find @Transactional for TestBean10.getAge()", annotation);
        annotation = AnnotationUtils.findAnnotation(AnnotationTransactionAttributeSourceTests.TestBean10.class, Transactional.class);
        Assert.assertNull("AnnotationUtils.findAnnotation should not find @Transactional for TestBean10", annotation);
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute actual = atas.getTransactionAttribute(method, AnnotationTransactionAttributeSourceTests.TestBean10.class);
        Assert.assertNotNull("Failed to retrieve TransactionAttribute for TestBean10.getAge()", actual);
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        rbta.getRollbackRules().add(new NoRollbackRuleAttribute(IOException.class));
        Assert.assertEquals(rbta.getRollbackRules(), getRollbackRules());
        Assert.assertTrue(actual.isReadOnly());
    }

    @Test
    public void transactionAttributeDeclaredOnClassMethodWithEjb3() throws Exception {
        Method getAgeMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getAge");
        Method getNameMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getName");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute getAgeAttr = atas.getTransactionAttribute(getAgeMethod, AnnotationTransactionAttributeSourceTests.Ejb3AnnotatedBean1.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, getAgeAttr.getPropagationBehavior());
        TransactionAttribute getNameAttr = atas.getTransactionAttribute(getNameMethod, AnnotationTransactionAttributeSourceTests.Ejb3AnnotatedBean1.class);
        Assert.assertEquals(PROPAGATION_SUPPORTS, getNameAttr.getPropagationBehavior());
    }

    @Test
    public void transactionAttributeDeclaredOnClassWithEjb3() throws Exception {
        Method getAgeMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getAge");
        Method getNameMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getName");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute getAgeAttr = atas.getTransactionAttribute(getAgeMethod, AnnotationTransactionAttributeSourceTests.Ejb3AnnotatedBean2.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, getAgeAttr.getPropagationBehavior());
        TransactionAttribute getNameAttr = atas.getTransactionAttribute(getNameMethod, AnnotationTransactionAttributeSourceTests.Ejb3AnnotatedBean2.class);
        Assert.assertEquals(PROPAGATION_SUPPORTS, getNameAttr.getPropagationBehavior());
    }

    @Test
    public void transactionAttributeDeclaredOnInterfaceWithEjb3() throws Exception {
        Method getAgeMethod = AnnotationTransactionAttributeSourceTests.ITestEjb.class.getMethod("getAge");
        Method getNameMethod = AnnotationTransactionAttributeSourceTests.ITestEjb.class.getMethod("getName");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute getAgeAttr = atas.getTransactionAttribute(getAgeMethod, AnnotationTransactionAttributeSourceTests.Ejb3AnnotatedBean3.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, getAgeAttr.getPropagationBehavior());
        TransactionAttribute getNameAttr = atas.getTransactionAttribute(getNameMethod, AnnotationTransactionAttributeSourceTests.Ejb3AnnotatedBean3.class);
        Assert.assertEquals(PROPAGATION_SUPPORTS, getNameAttr.getPropagationBehavior());
    }

    @Test
    public void transactionAttributeDeclaredOnClassMethodWithJta() throws Exception {
        Method getAgeMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getAge");
        Method getNameMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getName");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute getAgeAttr = atas.getTransactionAttribute(getAgeMethod, AnnotationTransactionAttributeSourceTests.JtaAnnotatedBean1.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, getAgeAttr.getPropagationBehavior());
        TransactionAttribute getNameAttr = atas.getTransactionAttribute(getNameMethod, AnnotationTransactionAttributeSourceTests.JtaAnnotatedBean1.class);
        Assert.assertEquals(PROPAGATION_SUPPORTS, getNameAttr.getPropagationBehavior());
    }

    @Test
    public void transactionAttributeDeclaredOnClassWithJta() throws Exception {
        Method getAgeMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getAge");
        Method getNameMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getName");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute getAgeAttr = atas.getTransactionAttribute(getAgeMethod, AnnotationTransactionAttributeSourceTests.JtaAnnotatedBean2.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, getAgeAttr.getPropagationBehavior());
        TransactionAttribute getNameAttr = atas.getTransactionAttribute(getNameMethod, AnnotationTransactionAttributeSourceTests.JtaAnnotatedBean2.class);
        Assert.assertEquals(PROPAGATION_SUPPORTS, getNameAttr.getPropagationBehavior());
    }

    @Test
    public void transactionAttributeDeclaredOnInterfaceWithJta() throws Exception {
        Method getAgeMethod = AnnotationTransactionAttributeSourceTests.ITestEjb.class.getMethod("getAge");
        Method getNameMethod = AnnotationTransactionAttributeSourceTests.ITestEjb.class.getMethod("getName");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute getAgeAttr = atas.getTransactionAttribute(getAgeMethod, AnnotationTransactionAttributeSourceTests.JtaAnnotatedBean3.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, getAgeAttr.getPropagationBehavior());
        TransactionAttribute getNameAttr = atas.getTransactionAttribute(getNameMethod, AnnotationTransactionAttributeSourceTests.JtaAnnotatedBean3.class);
        Assert.assertEquals(PROPAGATION_SUPPORTS, getNameAttr.getPropagationBehavior());
    }

    @Test
    public void transactionAttributeDeclaredOnGroovyClass() throws Exception {
        Method getAgeMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getAge");
        Method getNameMethod = AnnotationTransactionAttributeSourceTests.ITestBean1.class.getMethod("getName");
        Method getMetaClassMethod = GroovyObject.class.getMethod("getMetaClass");
        AnnotationTransactionAttributeSource atas = new AnnotationTransactionAttributeSource();
        TransactionAttribute getAgeAttr = atas.getTransactionAttribute(getAgeMethod, AnnotationTransactionAttributeSourceTests.GroovyTestBean.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, getAgeAttr.getPropagationBehavior());
        TransactionAttribute getNameAttr = atas.getTransactionAttribute(getNameMethod, AnnotationTransactionAttributeSourceTests.GroovyTestBean.class);
        Assert.assertEquals(PROPAGATION_REQUIRED, getNameAttr.getPropagationBehavior());
        Assert.assertNull(atas.getTransactionAttribute(getMetaClassMethod, AnnotationTransactionAttributeSourceTests.GroovyTestBean.class));
    }

    interface ITestBean1 {
        int getAge();

        void setAge(int age);

        String getName();

        void setName(String name);
    }

    interface ITestBean2 {
        @Transactional
        int getAge();

        void setAge(int age);
    }

    interface ITestBean2X extends AnnotationTransactionAttributeSourceTests.ITestBean2 {
        String getName();

        void setName(String name);
    }

    @Transactional
    interface ITestBean3 {
        int getAge();

        void setAge(int age);

        String getName();

        void setName(String name);
    }

    static class Empty implements AnnotationTransactionAttributeSourceTests.ITestBean1 {
        private String name;

        private int age;

        public Empty() {
        }

        public Empty(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    @SuppressWarnings("serial")
    static class TestBean1 implements Serializable , AnnotationTransactionAttributeSourceTests.ITestBean1 {
        private String name;

        private int age;

        public TestBean1() {
        }

        public TestBean1(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    static class TestBean2 implements AnnotationTransactionAttributeSourceTests.ITestBean2X {
        private String name;

        private int age;

        public TestBean2() {
        }

        public TestBean2(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    static class TestBean3 implements AnnotationTransactionAttributeSourceTests.ITestBean3 {
        private String name;

        private int age;

        public TestBean3() {
        }

        public TestBean3(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        @Transactional(propagation = REQUIRES_NEW, isolation = REPEATABLE_READ, timeout = 5, readOnly = true, rollbackFor = Exception.class, noRollbackFor = IOException.class)
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    @Transactional(rollbackFor = Exception.class, noRollbackFor = IOException.class)
    static class TestBean4 implements AnnotationTransactionAttributeSourceTests.ITestBean3 {
        private String name;

        private int age;

        public TestBean4() {
        }

        public TestBean4(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Transactional(rollbackFor = Exception.class, noRollbackFor = IOException.class)
    @interface Tx {}

    @AnnotationTransactionAttributeSourceTests.Tx
    static class TestBean5 {
        public int getAge() {
            return 10;
        }
    }

    static class TestBean6 {
        @AnnotationTransactionAttributeSourceTests.Tx
        public int getAge() {
            return 10;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Transactional(rollbackFor = Exception.class, noRollbackFor = IOException.class)
    @interface TxWithAttribute {
        boolean readOnly();
    }

    @AnnotationTransactionAttributeSourceTests.TxWithAttribute(readOnly = true)
    static class TestBean7 {
        public int getAge() {
            return 10;
        }
    }

    static class TestBean8 {
        @AnnotationTransactionAttributeSourceTests.TxWithAttribute(readOnly = true)
        public int getAge() {
            return 10;
        }
    }

    @AnnotationTransactionAttributeSourceTests.TxWithAttribute(readOnly = true)
    interface TestInterface9 {
        int getAge();
    }

    static class TestBean9 implements AnnotationTransactionAttributeSourceTests.TestInterface9 {
        @Override
        public int getAge() {
            return 10;
        }
    }

    interface TestInterface10 {
        @AnnotationTransactionAttributeSourceTests.TxWithAttribute(readOnly = true)
        int getAge();
    }

    static class TestBean10 implements AnnotationTransactionAttributeSourceTests.TestInterface10 {
        @Override
        public int getAge() {
            return 10;
        }
    }

    static class Ejb3AnnotatedBean1 implements AnnotationTransactionAttributeSourceTests.ITestBean1 {
        private String name;

        private int age;

        @Override
        @javax.ejb.TransactionAttribute(TransactionAttributeType.SUPPORTS)
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        @javax.ejb.TransactionAttribute
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    @javax.ejb.TransactionAttribute(TransactionAttributeType.SUPPORTS)
    static class Ejb3AnnotatedBean2 implements AnnotationTransactionAttributeSourceTests.ITestBean1 {
        private String name;

        private int age;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        @javax.ejb.TransactionAttribute
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    @javax.ejb.TransactionAttribute(TransactionAttributeType.SUPPORTS)
    interface ITestEjb {
        @javax.ejb.TransactionAttribute
        int getAge();

        void setAge(int age);

        String getName();

        void setName(String name);
    }

    static class Ejb3AnnotatedBean3 implements AnnotationTransactionAttributeSourceTests.ITestEjb {
        private String name;

        private int age;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    static class JtaAnnotatedBean1 implements AnnotationTransactionAttributeSourceTests.ITestBean1 {
        private String name;

        private int age;

        @Override
        @javax.transaction.Transactional(TxType.SUPPORTS)
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        @javax.transaction.Transactional
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    @javax.transaction.Transactional(TxType.SUPPORTS)
    static class JtaAnnotatedBean2 implements AnnotationTransactionAttributeSourceTests.ITestBean1 {
        private String name;

        private int age;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        @javax.transaction.Transactional
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    @javax.transaction.Transactional(TxType.SUPPORTS)
    interface ITestJta {
        @javax.transaction.Transactional
        int getAge();

        void setAge(int age);

        String getName();

        void setName(String name);
    }

    static class JtaAnnotatedBean3 implements AnnotationTransactionAttributeSourceTests.ITestEjb {
        private String name;

        private int age;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }
    }

    @Transactional
    static class GroovyTestBean implements GroovyObject , AnnotationTransactionAttributeSourceTests.ITestBean1 {
        private String name;

        private int age;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int getAge() {
            return age;
        }

        @Override
        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public Object invokeMethod(String name, Object args) {
            return null;
        }

        @Override
        public Object getProperty(String propertyName) {
            return null;
        }

        @Override
        public void setProperty(String propertyName, Object newValue) {
        }

        @Override
        public MetaClass getMetaClass() {
            return null;
        }

        @Override
        public void setMetaClass(MetaClass metaClass) {
        }
    }
}


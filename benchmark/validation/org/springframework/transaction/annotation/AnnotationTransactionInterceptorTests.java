/**
 * Copyright 2002-2019 the original author or authors.
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


import io.vavr.control.Try;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class AnnotationTransactionInterceptorTests {
    private final CallCountingTransactionManager ptm = new CallCountingTransactionManager();

    private final AnnotationTransactionAttributeSource source = new AnnotationTransactionAttributeSource();

    private final TransactionInterceptor ti = new TransactionInterceptor(this.ptm, this.source);

    @Test
    public void classLevelOnly() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestClassLevelOnly());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestClassLevelOnly proxy = ((AnnotationTransactionInterceptorTests.TestClassLevelOnly) (proxyFactory.getProxy()));
        proxy.doSomething();
        assertGetTransactionAndCommitCount(1);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(2);
        proxy.doSomething();
        assertGetTransactionAndCommitCount(3);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(4);
    }

    @Test
    public void withSingleMethodOverride() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithSingleMethodOverride());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithSingleMethodOverride proxy = ((AnnotationTransactionInterceptorTests.TestWithSingleMethodOverride) (proxyFactory.getProxy()));
        proxy.doSomething();
        assertGetTransactionAndCommitCount(1);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(2);
        proxy.doSomethingCompletelyElse();
        assertGetTransactionAndCommitCount(3);
        proxy.doSomething();
        assertGetTransactionAndCommitCount(4);
    }

    @Test
    public void withSingleMethodOverrideInverted() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithSingleMethodOverrideInverted());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithSingleMethodOverrideInverted proxy = ((AnnotationTransactionInterceptorTests.TestWithSingleMethodOverrideInverted) (proxyFactory.getProxy()));
        proxy.doSomething();
        assertGetTransactionAndCommitCount(1);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(2);
        proxy.doSomethingCompletelyElse();
        assertGetTransactionAndCommitCount(3);
        proxy.doSomething();
        assertGetTransactionAndCommitCount(4);
    }

    @Test
    public void withMultiMethodOverride() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithMultiMethodOverride());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithMultiMethodOverride proxy = ((AnnotationTransactionInterceptorTests.TestWithMultiMethodOverride) (proxyFactory.getProxy()));
        proxy.doSomething();
        assertGetTransactionAndCommitCount(1);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(2);
        proxy.doSomethingCompletelyElse();
        assertGetTransactionAndCommitCount(3);
        proxy.doSomething();
        assertGetTransactionAndCommitCount(4);
    }

    @Test
    public void withRollbackOnRuntimeException() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithExceptions());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithExceptions proxy = ((AnnotationTransactionInterceptorTests.TestWithExceptions) (proxyFactory.getProxy()));
        try {
            proxy.doSomethingErroneous();
            Assert.fail("Should throw IllegalStateException");
        } catch (IllegalStateException ex) {
            assertGetTransactionAndRollbackCount(1);
        }
        try {
            proxy.doSomethingElseErroneous();
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertGetTransactionAndRollbackCount(2);
        }
    }

    @Test
    public void withCommitOnCheckedException() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithExceptions());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithExceptions proxy = ((AnnotationTransactionInterceptorTests.TestWithExceptions) (proxyFactory.getProxy()));
        try {
            proxy.doSomethingElseWithCheckedException();
            Assert.fail("Should throw Exception");
        } catch (Exception ex) {
            assertGetTransactionAndCommitCount(1);
        }
    }

    @Test
    public void withRollbackOnCheckedExceptionAndRollbackRule() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithExceptions());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithExceptions proxy = ((AnnotationTransactionInterceptorTests.TestWithExceptions) (proxyFactory.getProxy()));
        try {
            proxy.doSomethingElseWithCheckedExceptionAndRollbackRule();
            Assert.fail("Should throw Exception");
        } catch (Exception ex) {
            assertGetTransactionAndRollbackCount(1);
        }
    }

    @Test
    public void withVavrTrySuccess() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithVavrTry());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithVavrTry proxy = ((AnnotationTransactionInterceptorTests.TestWithVavrTry) (proxyFactory.getProxy()));
        proxy.doSomething();
        assertGetTransactionAndCommitCount(1);
    }

    @Test
    public void withVavrTryRuntimeException() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithVavrTry());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithVavrTry proxy = ((AnnotationTransactionInterceptorTests.TestWithVavrTry) (proxyFactory.getProxy()));
        proxy.doSomethingErroneous();
        assertGetTransactionAndRollbackCount(1);
    }

    @Test
    public void withVavrTryCheckedException() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithVavrTry());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithVavrTry proxy = ((AnnotationTransactionInterceptorTests.TestWithVavrTry) (proxyFactory.getProxy()));
        proxy.doSomethingErroneousWithCheckedException();
        assertGetTransactionAndCommitCount(1);
    }

    @Test
    public void withVavrTryCheckedExceptionAndRollbackRule() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithVavrTry());
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithVavrTry proxy = ((AnnotationTransactionInterceptorTests.TestWithVavrTry) (proxyFactory.getProxy()));
        proxy.doSomethingErroneousWithCheckedExceptionAndRollbackRule();
        assertGetTransactionAndRollbackCount(1);
    }

    @Test
    public void withInterface() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithInterfaceImpl());
        proxyFactory.addInterface(AnnotationTransactionInterceptorTests.TestWithInterface.class);
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithInterface proxy = ((AnnotationTransactionInterceptorTests.TestWithInterface) (proxyFactory.getProxy()));
        proxy.doSomething();
        assertGetTransactionAndCommitCount(1);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(2);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(3);
        proxy.doSomething();
        assertGetTransactionAndCommitCount(4);
        proxy.doSomethingDefault();
        assertGetTransactionAndCommitCount(5);
    }

    @Test
    public void crossClassInterfaceMethodLevelOnJdkProxy() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.SomeServiceImpl());
        proxyFactory.addInterface(AnnotationTransactionInterceptorTests.SomeService.class);
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.SomeService someService = ((AnnotationTransactionInterceptorTests.SomeService) (proxyFactory.getProxy()));
        someService.bar();
        assertGetTransactionAndCommitCount(1);
        someService.foo();
        assertGetTransactionAndCommitCount(2);
        someService.fooBar();
        assertGetTransactionAndCommitCount(3);
    }

    @Test
    public void crossClassInterfaceOnJdkProxy() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new AnnotationTransactionInterceptorTests.OtherServiceImpl());
        proxyFactory.addInterface(AnnotationTransactionInterceptorTests.OtherService.class);
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.OtherService otherService = ((AnnotationTransactionInterceptorTests.OtherService) (proxyFactory.getProxy()));
        otherService.foo();
        assertGetTransactionAndCommitCount(1);
    }

    @Test
    public void withInterfaceOnTargetJdkProxy() {
        ProxyFactory targetFactory = new ProxyFactory();
        targetFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithInterfaceImpl());
        targetFactory.addInterface(AnnotationTransactionInterceptorTests.TestWithInterface.class);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(targetFactory.getProxy());
        proxyFactory.addInterface(AnnotationTransactionInterceptorTests.TestWithInterface.class);
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithInterface proxy = ((AnnotationTransactionInterceptorTests.TestWithInterface) (proxyFactory.getProxy()));
        proxy.doSomething();
        assertGetTransactionAndCommitCount(1);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(2);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(3);
        proxy.doSomething();
        assertGetTransactionAndCommitCount(4);
        proxy.doSomethingDefault();
        assertGetTransactionAndCommitCount(5);
    }

    @Test
    public void withInterfaceOnTargetCglibProxy() {
        ProxyFactory targetFactory = new ProxyFactory();
        targetFactory.setTarget(new AnnotationTransactionInterceptorTests.TestWithInterfaceImpl());
        targetFactory.setProxyTargetClass(true);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(targetFactory.getProxy());
        proxyFactory.addInterface(AnnotationTransactionInterceptorTests.TestWithInterface.class);
        proxyFactory.addAdvice(this.ti);
        AnnotationTransactionInterceptorTests.TestWithInterface proxy = ((AnnotationTransactionInterceptorTests.TestWithInterface) (proxyFactory.getProxy()));
        proxy.doSomething();
        assertGetTransactionAndCommitCount(1);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(2);
        proxy.doSomethingElse();
        assertGetTransactionAndCommitCount(3);
        proxy.doSomething();
        assertGetTransactionAndCommitCount(4);
        proxy.doSomethingDefault();
        assertGetTransactionAndCommitCount(5);
    }

    @Transactional
    public static class TestClassLevelOnly {
        public void doSomething() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }

        public void doSomethingElse() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }
    }

    @Transactional
    public static class TestWithSingleMethodOverride {
        public void doSomething() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }

        @Transactional(readOnly = true)
        public void doSomethingElse() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }

        public void doSomethingCompletelyElse() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }
    }

    @Transactional(readOnly = true)
    public static class TestWithSingleMethodOverrideInverted {
        @Transactional
        public void doSomething() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }

        public void doSomethingElse() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }

        public void doSomethingCompletelyElse() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }
    }

    @Transactional
    public static class TestWithMultiMethodOverride {
        @Transactional(readOnly = true)
        public void doSomething() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }

        @Transactional(readOnly = true)
        public void doSomethingElse() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }

        public void doSomethingCompletelyElse() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }
    }

    @Transactional
    public static class TestWithExceptions {
        public void doSomethingErroneous() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            throw new IllegalStateException();
        }

        public void doSomethingElseErroneous() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            throw new IllegalArgumentException();
        }

        @Transactional
        public void doSomethingElseWithCheckedException() throws Exception {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            throw new Exception();
        }

        @Transactional(rollbackFor = Exception.class)
        public void doSomethingElseWithCheckedExceptionAndRollbackRule() throws Exception {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            throw new Exception();
        }
    }

    @Transactional
    public static class TestWithVavrTry {
        public Try<String> doSomething() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            return Try.success("ok");
        }

        public Try<String> doSomethingErroneous() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            return Try.failure(new IllegalStateException());
        }

        public Try<String> doSomethingErroneousWithCheckedException() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            return Try.failure(new Exception());
        }

        @Transactional(rollbackFor = Exception.class)
        public Try<String> doSomethingErroneousWithCheckedExceptionAndRollbackRule() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            return Try.failure(new Exception());
        }
    }

    public interface BaseInterface {
        void doSomething();
    }

    @Transactional
    public interface TestWithInterface extends AnnotationTransactionInterceptorTests.BaseInterface {
        @Transactional(readOnly = true)
        void doSomethingElse();

        default void doSomethingDefault() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }
    }

    public static class TestWithInterfaceImpl implements AnnotationTransactionInterceptorTests.TestWithInterface {
        @Override
        public void doSomething() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }

        @Override
        public void doSomethingElse() {
            Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }
    }

    public interface SomeService {
        void foo();

        @Transactional
        void bar();

        @Transactional(readOnly = true)
        void fooBar();
    }

    public static class SomeServiceImpl implements AnnotationTransactionInterceptorTests.SomeService {
        @Override
        public void bar() {
        }

        @Override
        @Transactional
        public void foo() {
        }

        @Override
        @Transactional(readOnly = false)
        public void fooBar() {
        }
    }

    public interface OtherService {
        void foo();
    }

    @Transactional
    public static class OtherServiceImpl implements AnnotationTransactionInterceptorTests.OtherService {
        @Override
        public void foo() {
        }
    }
}


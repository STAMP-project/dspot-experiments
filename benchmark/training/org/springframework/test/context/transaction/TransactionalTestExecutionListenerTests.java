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
package org.springframework.test.context.transaction;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Unit tests for {@link TransactionalTestExecutionListener}.
 *
 * @author Sam Brannen
 * @since 4.0
 */
public class TransactionalTestExecutionListenerTests {
    private final PlatformTransactionManager tm = Mockito.mock(PlatformTransactionManager.class);

    private final TransactionalTestExecutionListener listener = new TransactionalTestExecutionListener() {
        @Override
        protected PlatformTransactionManager getTransactionManager(TestContext testContext, String qualifier) {
            return tm;
        }
    };

    private final TestContext testContext = Mockito.mock(TestContext.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    // SPR-13895
    @Test
    public void transactionalTestWithoutTransactionManager() throws Exception {
        TransactionalTestExecutionListener listener = new TransactionalTestExecutionListener() {
            protected PlatformTransactionManager getTransactionManager(TestContext testContext, String qualifier) {
                return null;
            }
        };
        Class<? extends TransactionalTestExecutionListenerTests.Invocable> clazz = TransactionalTestExecutionListenerTests.TransactionalDeclaredOnClassLocallyTestCase.class;
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(clazz);
        TransactionalTestExecutionListenerTests.Invocable instance = BeanUtils.instantiateClass(clazz);
        BDDMockito.given(testContext.getTestInstance()).willReturn(instance);
        BDDMockito.given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("transactionalTest"));
        Assert.assertFalse("callback should not have been invoked", instance.invoked());
        TransactionContextHolder.removeCurrentTransactionContext();
        try {
            listener.beforeTestMethod(testContext);
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().startsWith("Failed to retrieve PlatformTransactionManager for @Transactional test"));
        }
    }

    @Test
    public void beforeTestMethodWithTransactionalDeclaredOnClassLocally() throws Exception {
        assertBeforeTestMethodWithTransactionalTestMethod(TransactionalTestExecutionListenerTests.TransactionalDeclaredOnClassLocallyTestCase.class);
    }

    @Test
    public void beforeTestMethodWithTransactionalDeclaredOnClassViaMetaAnnotation() throws Exception {
        assertBeforeTestMethodWithTransactionalTestMethod(TransactionalTestExecutionListenerTests.TransactionalDeclaredOnClassViaMetaAnnotationTestCase.class);
    }

    @Test
    public void beforeTestMethodWithTransactionalDeclaredOnClassViaMetaAnnotationWithOverride() throws Exception {
        // Note: not actually invoked within a transaction since the test class is
        // annotated with @MetaTxWithOverride(propagation = NOT_SUPPORTED)
        assertBeforeTestMethodWithTransactionalTestMethod(TransactionalTestExecutionListenerTests.TransactionalDeclaredOnClassViaMetaAnnotationWithOverrideTestCase.class, false);
    }

    @Test
    public void beforeTestMethodWithTransactionalDeclaredOnMethodViaMetaAnnotationWithOverride() throws Exception {
        // Note: not actually invoked within a transaction since the method is
        // annotated with @MetaTxWithOverride(propagation = NOT_SUPPORTED)
        assertBeforeTestMethodWithTransactionalTestMethod(TransactionalTestExecutionListenerTests.TransactionalDeclaredOnMethodViaMetaAnnotationWithOverrideTestCase.class, false);
        assertBeforeTestMethodWithNonTransactionalTestMethod(TransactionalTestExecutionListenerTests.TransactionalDeclaredOnMethodViaMetaAnnotationWithOverrideTestCase.class);
    }

    @Test
    public void beforeTestMethodWithTransactionalDeclaredOnMethodLocally() throws Exception {
        assertBeforeTestMethod(TransactionalTestExecutionListenerTests.TransactionalDeclaredOnMethodLocallyTestCase.class);
    }

    @Test
    public void beforeTestMethodWithTransactionalDeclaredOnMethodViaMetaAnnotation() throws Exception {
        assertBeforeTestMethod(TransactionalTestExecutionListenerTests.TransactionalDeclaredOnMethodViaMetaAnnotationTestCase.class);
    }

    @Test
    public void beforeTestMethodWithBeforeTransactionDeclaredLocally() throws Exception {
        assertBeforeTestMethod(TransactionalTestExecutionListenerTests.BeforeTransactionDeclaredLocallyTestCase.class);
    }

    @Test
    public void beforeTestMethodWithBeforeTransactionDeclaredViaMetaAnnotation() throws Exception {
        assertBeforeTestMethod(TransactionalTestExecutionListenerTests.BeforeTransactionDeclaredViaMetaAnnotationTestCase.class);
    }

    @Test
    public void afterTestMethodWithAfterTransactionDeclaredLocally() throws Exception {
        assertAfterTestMethod(TransactionalTestExecutionListenerTests.AfterTransactionDeclaredLocallyTestCase.class);
    }

    @Test
    public void afterTestMethodWithAfterTransactionDeclaredViaMetaAnnotation() throws Exception {
        assertAfterTestMethod(TransactionalTestExecutionListenerTests.AfterTransactionDeclaredViaMetaAnnotationTestCase.class);
    }

    @Test
    public void beforeTestMethodWithBeforeTransactionDeclaredAsInterfaceDefaultMethod() throws Exception {
        assertBeforeTestMethod(TransactionalTestExecutionListenerTests.BeforeTransactionDeclaredAsInterfaceDefaultMethodTestCase.class);
    }

    @Test
    public void afterTestMethodWithAfterTransactionDeclaredAsInterfaceDefaultMethod() throws Exception {
        assertAfterTestMethod(TransactionalTestExecutionListenerTests.AfterTransactionDeclaredAsInterfaceDefaultMethodTestCase.class);
    }

    @Test
    public void isRollbackWithMissingRollback() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.MissingRollbackTestCase.class, true);
    }

    @Test
    public void isRollbackWithEmptyMethodLevelRollback() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.EmptyMethodLevelRollbackTestCase.class, true);
    }

    @Test
    public void isRollbackWithMethodLevelRollbackWithExplicitValue() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.MethodLevelRollbackWithExplicitValueTestCase.class, false);
    }

    @Test
    public void isRollbackWithMethodLevelRollbackViaMetaAnnotation() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.MethodLevelRollbackViaMetaAnnotationTestCase.class, false);
    }

    @Test
    public void isRollbackWithEmptyClassLevelRollback() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.EmptyClassLevelRollbackTestCase.class, true);
    }

    @Test
    public void isRollbackWithClassLevelRollbackWithExplicitValue() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.ClassLevelRollbackWithExplicitValueTestCase.class, false);
    }

    @Test
    public void isRollbackWithClassLevelRollbackViaMetaAnnotation() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.ClassLevelRollbackViaMetaAnnotationTestCase.class, false);
    }

    @Test
    public void isRollbackWithClassLevelRollbackWithExplicitValueOnTestInterface() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.ClassLevelRollbackWithExplicitValueOnTestInterfaceTestCase.class, false);
    }

    @Test
    public void isRollbackWithClassLevelRollbackViaMetaAnnotationOnTestInterface() throws Exception {
        assertIsRollback(TransactionalTestExecutionListenerTests.ClassLevelRollbackViaMetaAnnotationOnTestInterfaceTestCase.class, false);
    }

    @Transactional
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MetaTransactional {}

    @Transactional
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface MetaTxWithOverride {
        @AliasFor(annotation = Transactional.class, attribute = "value")
        String transactionManager() default "";

        Propagation propagation() default REQUIRED;
    }

    @BeforeTransaction
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MetaBeforeTransaction {}

    @AfterTransaction
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MetaAfterTransaction {}

    private interface Invocable {
        void invoked(boolean invoked);

        boolean invoked();
    }

    private static class AbstractInvocable implements TransactionalTestExecutionListenerTests.Invocable {
        boolean invoked = false;

        @Override
        public void invoked(boolean invoked) {
            this.invoked = invoked;
        }

        @Override
        public boolean invoked() {
            return this.invoked;
        }
    }

    @Transactional
    static class TransactionalDeclaredOnClassLocallyTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @BeforeTransaction
        public void beforeTransaction() {
            invoked(true);
        }

        public void transactionalTest() {
        }
    }

    static class TransactionalDeclaredOnMethodLocallyTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @BeforeTransaction
        public void beforeTransaction() {
            invoked(true);
        }

        @Transactional
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    @TransactionalTestExecutionListenerTests.MetaTransactional
    static class TransactionalDeclaredOnClassViaMetaAnnotationTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @BeforeTransaction
        public void beforeTransaction() {
            invoked(true);
        }

        public void transactionalTest() {
        }
    }

    static class TransactionalDeclaredOnMethodViaMetaAnnotationTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @BeforeTransaction
        public void beforeTransaction() {
            invoked(true);
        }

        @TransactionalTestExecutionListenerTests.MetaTransactional
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    @TransactionalTestExecutionListenerTests.MetaTxWithOverride(propagation = NOT_SUPPORTED)
    static class TransactionalDeclaredOnClassViaMetaAnnotationWithOverrideTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @BeforeTransaction
        public void beforeTransaction() {
            invoked(true);
        }

        public void transactionalTest() {
        }
    }

    static class TransactionalDeclaredOnMethodViaMetaAnnotationWithOverrideTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @BeforeTransaction
        public void beforeTransaction() {
            invoked(true);
        }

        @TransactionalTestExecutionListenerTests.MetaTxWithOverride(propagation = NOT_SUPPORTED)
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    static class BeforeTransactionDeclaredLocallyTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @BeforeTransaction
        public void beforeTransaction() {
            invoked(true);
        }

        @Transactional
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    static class BeforeTransactionDeclaredViaMetaAnnotationTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @TransactionalTestExecutionListenerTests.MetaBeforeTransaction
        public void beforeTransaction() {
            invoked(true);
        }

        @Transactional
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    static class AfterTransactionDeclaredLocallyTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @AfterTransaction
        public void afterTransaction() {
            invoked(true);
        }

        @Transactional
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    static class AfterTransactionDeclaredViaMetaAnnotationTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable {
        @TransactionalTestExecutionListenerTests.MetaAfterTransaction
        public void afterTransaction() {
            invoked(true);
        }

        @Transactional
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    interface BeforeTransactionInterface extends TransactionalTestExecutionListenerTests.Invocable {
        @BeforeTransaction
        default void beforeTransaction() {
            invoked(true);
        }
    }

    interface AfterTransactionInterface extends TransactionalTestExecutionListenerTests.Invocable {
        @AfterTransaction
        default void afterTransaction() {
            invoked(true);
        }
    }

    static class BeforeTransactionDeclaredAsInterfaceDefaultMethodTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable implements TransactionalTestExecutionListenerTests.BeforeTransactionInterface {
        @Transactional
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    static class AfterTransactionDeclaredAsInterfaceDefaultMethodTestCase extends TransactionalTestExecutionListenerTests.AbstractInvocable implements TransactionalTestExecutionListenerTests.AfterTransactionInterface {
        @Transactional
        public void transactionalTest() {
        }

        public void nonTransactionalTest() {
        }
    }

    static class MissingRollbackTestCase {
        public void test() {
        }
    }

    static class EmptyMethodLevelRollbackTestCase {
        @Rollback
        public void test() {
        }
    }

    static class MethodLevelRollbackWithExplicitValueTestCase {
        @Rollback(false)
        public void test() {
        }
    }

    static class MethodLevelRollbackViaMetaAnnotationTestCase {
        @Commit
        public void test() {
        }
    }

    @Rollback
    static class EmptyClassLevelRollbackTestCase {
        public void test() {
        }
    }

    @Rollback(false)
    static class ClassLevelRollbackWithExplicitValueTestCase {
        public void test() {
        }
    }

    @Commit
    static class ClassLevelRollbackViaMetaAnnotationTestCase {
        public void test() {
        }
    }

    @Rollback(false)
    interface RollbackFalseTestInterface {}

    static class ClassLevelRollbackWithExplicitValueOnTestInterfaceTestCase implements TransactionalTestExecutionListenerTests.RollbackFalseTestInterface {
        public void test() {
        }
    }

    @Commit
    interface RollbackFalseViaMetaAnnotationTestInterface {}

    static class ClassLevelRollbackViaMetaAnnotationOnTestInterfaceTestCase implements TransactionalTestExecutionListenerTests.RollbackFalseViaMetaAnnotationTestInterface {
        public void test() {
        }
    }
}


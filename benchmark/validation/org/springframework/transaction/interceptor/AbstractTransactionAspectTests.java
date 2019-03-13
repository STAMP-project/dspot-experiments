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
package org.springframework.transaction.interceptor;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.MockCallbackPreferringTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.TransactionAspectSupport.TransactionInfo;


/**
 * Mock object based tests for transaction aspects.
 * True unit test in that it tests how the transaction aspect uses
 * the PlatformTransactionManager helper, rather than indirectly
 * testing the helper implementation.
 *
 * This is a superclass to allow testing both the AOP Alliance MethodInterceptor
 * and the AspectJ aspect.
 *
 * @author Rod Johnson
 * @since 16.03.2003
 */
public abstract class AbstractTransactionAspectTests {
    protected Method exceptionalMethod;

    protected Method getNameMethod;

    protected Method setNameMethod;

    @Test
    public void noTransaction() throws Exception {
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        TestBean tb = new TestBean();
        TransactionAttributeSource tas = new MapTransactionAttributeSource();
        // All the methods in this class use the advised() template method
        // to obtain a transaction object, configured with the given PlatformTransactionManager
        // and transaction attribute source
        ITestBean itb = ((ITestBean) (advised(tb, ptm, tas)));
        checkTransactionStatus(false);
        itb.getName();
        checkTransactionStatus(false);
        // expect no calls
        Mockito.verifyZeroInteractions(ptm);
    }

    /**
     * Check that a transaction is created and committed.
     */
    @Test
    public void transactionShouldSucceed() throws Exception {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(getNameMethod, txatt);
        TransactionStatus status = Mockito.mock(TransactionStatus.class);
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        // expect a transaction
        BDDMockito.given(ptm.getTransaction(txatt)).willReturn(status);
        TestBean tb = new TestBean();
        ITestBean itb = ((ITestBean) (advised(tb, ptm, tas)));
        checkTransactionStatus(false);
        itb.getName();
        checkTransactionStatus(false);
        Mockito.verify(ptm).commit(status);
    }

    /**
     * Check that a transaction is created and committed using
     * CallbackPreferringPlatformTransactionManager.
     */
    @Test
    public void transactionShouldSucceedWithCallbackPreference() throws Exception {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(getNameMethod, txatt);
        MockCallbackPreferringTransactionManager ptm = new MockCallbackPreferringTransactionManager();
        TestBean tb = new TestBean();
        ITestBean itb = ((ITestBean) (advised(tb, ptm, tas)));
        checkTransactionStatus(false);
        itb.getName();
        checkTransactionStatus(false);
        Assert.assertSame(txatt, ptm.getDefinition());
        Assert.assertFalse(ptm.getStatus().isRollbackOnly());
    }

    @Test
    public void transactionExceptionPropagatedWithCallbackPreference() throws Throwable {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(exceptionalMethod, txatt);
        MockCallbackPreferringTransactionManager ptm = new MockCallbackPreferringTransactionManager();
        TestBean tb = new TestBean();
        ITestBean itb = ((ITestBean) (advised(tb, ptm, tas)));
        checkTransactionStatus(false);
        try {
            itb.exceptional(new OptimisticLockingFailureException(""));
            Assert.fail("Should have thrown OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException ex) {
            // expected
        }
        checkTransactionStatus(false);
        Assert.assertSame(txatt, ptm.getDefinition());
        Assert.assertFalse(ptm.getStatus().isRollbackOnly());
    }

    /**
     * Check that two transactions are created and committed.
     */
    @Test
    public void twoTransactionsShouldSucceed() throws Exception {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        MapTransactionAttributeSource tas1 = new MapTransactionAttributeSource();
        tas1.register(getNameMethod, txatt);
        MapTransactionAttributeSource tas2 = new MapTransactionAttributeSource();
        tas2.register(setNameMethod, txatt);
        TransactionStatus status = Mockito.mock(TransactionStatus.class);
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        // expect a transaction
        BDDMockito.given(ptm.getTransaction(txatt)).willReturn(status);
        TestBean tb = new TestBean();
        ITestBean itb = ((ITestBean) (advised(tb, ptm, new TransactionAttributeSource[]{ tas1, tas2 })));
        checkTransactionStatus(false);
        itb.getName();
        checkTransactionStatus(false);
        itb.setName("myName");
        checkTransactionStatus(false);
        Mockito.verify(ptm, Mockito.times(2)).commit(status);
    }

    /**
     * Check that a transaction is created and committed.
     */
    @Test
    public void transactionShouldSucceedWithNotNew() throws Exception {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(getNameMethod, txatt);
        TransactionStatus status = Mockito.mock(TransactionStatus.class);
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        // expect a transaction
        BDDMockito.given(ptm.getTransaction(txatt)).willReturn(status);
        TestBean tb = new TestBean();
        ITestBean itb = ((ITestBean) (advised(tb, ptm, tas)));
        checkTransactionStatus(false);
        // verification!?
        itb.getName();
        checkTransactionStatus(false);
        Mockito.verify(ptm).commit(status);
    }

    @Test
    public void enclosingTransactionWithNonTransactionMethodOnAdvisedInside() throws Throwable {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(exceptionalMethod, txatt);
        TransactionStatus status = Mockito.mock(TransactionStatus.class);
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        // Expect a transaction
        BDDMockito.given(ptm.getTransaction(txatt)).willReturn(status);
        final String spouseName = "innerName";
        TestBean outer = new TestBean() {
            @Override
            public void exceptional(Throwable t) throws Throwable {
                TransactionInfo ti = TransactionAspectSupport.currentTransactionInfo();
                Assert.assertTrue(ti.hasTransaction());
                Assert.assertEquals(spouseName, getSpouse().getName());
            }
        };
        TestBean inner = new TestBean() {
            @Override
            public String getName() {
                // Assert that we're in the inner proxy
                TransactionInfo ti = TransactionAspectSupport.currentTransactionInfo();
                Assert.assertFalse(ti.hasTransaction());
                return spouseName;
            }
        };
        ITestBean outerProxy = ((ITestBean) (advised(outer, ptm, tas)));
        ITestBean innerProxy = ((ITestBean) (advised(inner, ptm, tas)));
        outer.setSpouse(innerProxy);
        checkTransactionStatus(false);
        // Will invoke inner.getName, which is non-transactional
        outerProxy.exceptional(null);
        checkTransactionStatus(false);
        Mockito.verify(ptm).commit(status);
    }

    @Test
    public void enclosingTransactionWithNestedTransactionOnAdvisedInside() throws Throwable {
        final TransactionAttribute outerTxatt = new DefaultTransactionAttribute();
        final TransactionAttribute innerTxatt = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_NESTED);
        Method outerMethod = exceptionalMethod;
        Method innerMethod = getNameMethod;
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(outerMethod, outerTxatt);
        tas.register(innerMethod, innerTxatt);
        TransactionStatus outerStatus = Mockito.mock(TransactionStatus.class);
        TransactionStatus innerStatus = Mockito.mock(TransactionStatus.class);
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        // Expect a transaction
        BDDMockito.given(ptm.getTransaction(outerTxatt)).willReturn(outerStatus);
        BDDMockito.given(ptm.getTransaction(innerTxatt)).willReturn(innerStatus);
        final String spouseName = "innerName";
        TestBean outer = new TestBean() {
            @Override
            public void exceptional(Throwable t) throws Throwable {
                TransactionInfo ti = TransactionAspectSupport.currentTransactionInfo();
                Assert.assertTrue(ti.hasTransaction());
                Assert.assertEquals(outerTxatt, ti.getTransactionAttribute());
                Assert.assertEquals(spouseName, getSpouse().getName());
            }
        };
        TestBean inner = new TestBean() {
            @Override
            public String getName() {
                // Assert that we're in the inner proxy
                TransactionInfo ti = TransactionAspectSupport.currentTransactionInfo();
                // Has nested transaction
                Assert.assertTrue(ti.hasTransaction());
                Assert.assertEquals(innerTxatt, ti.getTransactionAttribute());
                return spouseName;
            }
        };
        ITestBean outerProxy = ((ITestBean) (advised(outer, ptm, tas)));
        ITestBean innerProxy = ((ITestBean) (advised(inner, ptm, tas)));
        outer.setSpouse(innerProxy);
        checkTransactionStatus(false);
        // Will invoke inner.getName, which is non-transactional
        outerProxy.exceptional(null);
        checkTransactionStatus(false);
        Mockito.verify(ptm).commit(innerStatus);
        Mockito.verify(ptm).commit(outerStatus);
    }

    @Test
    public void rollbackOnCheckedException() throws Throwable {
        doTestRollbackOnException(new Exception(), true, false);
    }

    @Test
    public void noRollbackOnCheckedException() throws Throwable {
        doTestRollbackOnException(new Exception(), false, false);
    }

    @Test
    public void rollbackOnUncheckedException() throws Throwable {
        doTestRollbackOnException(new RuntimeException(), true, false);
    }

    @Test
    public void noRollbackOnUncheckedException() throws Throwable {
        doTestRollbackOnException(new RuntimeException(), false, false);
    }

    @Test
    public void rollbackOnCheckedExceptionWithRollbackException() throws Throwable {
        doTestRollbackOnException(new Exception(), true, true);
    }

    @Test
    public void noRollbackOnCheckedExceptionWithRollbackException() throws Throwable {
        doTestRollbackOnException(new Exception(), false, true);
    }

    @Test
    public void rollbackOnUncheckedExceptionWithRollbackException() throws Throwable {
        doTestRollbackOnException(new RuntimeException(), true, true);
    }

    @Test
    public void noRollbackOnUncheckedExceptionWithRollbackException() throws Throwable {
        doTestRollbackOnException(new RuntimeException(), false, true);
    }

    /**
     * Test that TransactionStatus.setRollbackOnly works.
     */
    @Test
    public void programmaticRollback() throws Exception {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        Method m = getNameMethod;
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(m, txatt);
        TransactionStatus status = Mockito.mock(TransactionStatus.class);
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        BDDMockito.given(ptm.getTransaction(txatt)).willReturn(status);
        final String name = "jenny";
        TestBean tb = new TestBean() {
            @Override
            public String getName() {
                TransactionStatus txStatus = TransactionInterceptor.currentTransactionStatus();
                txStatus.setRollbackOnly();
                return name;
            }
        };
        ITestBean itb = ((ITestBean) (advised(tb, ptm, tas)));
        // verification!?
        Assert.assertTrue(name.equals(itb.getName()));
        Mockito.verify(ptm).commit(status);
    }

    /**
     * Simulate a transaction infrastructure failure.
     * Shouldn't invoke target method.
     */
    @Test
    public void cannotCreateTransaction() throws Exception {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        Method m = getNameMethod;
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(m, txatt);
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        // Expect a transaction
        CannotCreateTransactionException ex = new CannotCreateTransactionException("foobar", null);
        BDDMockito.given(ptm.getTransaction(txatt)).willThrow(ex);
        TestBean tb = new TestBean() {
            @Override
            public String getName() {
                throw new UnsupportedOperationException("Shouldn't have invoked target method when couldn't create transaction for transactional method");
            }
        };
        ITestBean itb = ((ITestBean) (advised(tb, ptm, tas)));
        try {
            itb.getName();
            Assert.fail("Shouldn't have invoked method");
        } catch (CannotCreateTransactionException thrown) {
            Assert.assertTrue((thrown == ex));
        }
    }

    /**
     * Simulate failure of the underlying transaction infrastructure to commit.
     * Check that the target method was invoked, but that the transaction
     * infrastructure exception was thrown to the client
     */
    @Test
    public void cannotCommitTransaction() throws Exception {
        TransactionAttribute txatt = new DefaultTransactionAttribute();
        Method m = setNameMethod;
        MapTransactionAttributeSource tas = new MapTransactionAttributeSource();
        tas.register(m, txatt);
        // Method m2 = getNameMethod;
        // No attributes for m2
        PlatformTransactionManager ptm = Mockito.mock(PlatformTransactionManager.class);
        TransactionStatus status = Mockito.mock(TransactionStatus.class);
        BDDMockito.given(ptm.getTransaction(txatt)).willReturn(status);
        UnexpectedRollbackException ex = new UnexpectedRollbackException("foobar", null);
        BDDMockito.willThrow(ex).given(ptm).commit(status);
        TestBean tb = new TestBean();
        ITestBean itb = ((ITestBean) (advised(tb, ptm, tas)));
        String name = "new name";
        try {
            itb.setName(name);
            Assert.fail("Shouldn't have succeeded");
        } catch (UnexpectedRollbackException thrown) {
            Assert.assertTrue((thrown == ex));
        }
        // Should have invoked target and changed name
        Assert.assertTrue(((itb.getName()) == name));
    }
}


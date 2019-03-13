/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.cache.test.util;


import Propagation.MANDATORY;
import Propagation.NESTED;
import Propagation.NEVER;
import Propagation.NOT_SUPPORTED;
import Propagation.REQUIRED;
import Propagation.REQUIRES_NEW;
import Propagation.SUPPORTS;
import TransactionAttribute.Builder;
import com.liferay.portal.cache.TransactionalPortalCache;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.transactional.TransactionalPortalCacheHelper;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.transaction.TransactionAttribute;
import com.liferay.portal.kernel.transaction.TransactionLifecycleListener;
import com.liferay.portal.kernel.transaction.TransactionStatus;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class TransactionalPortalCacheTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = new CodeCoverageAssertor() {
        @Override
        public void appendAssertClasses(List<Class<?>> assertClasses) {
            assertClasses.add(TransactionalPortalCache.class);
            Class<TransactionalPortalCacheHelper> clazz = TransactionalPortalCacheHelper.class;
            assertClasses.add(clazz);
            Collections.addAll(assertClasses, clazz.getDeclaredClasses());
            TransactionLifecycleListener transactionLifecycleListener = TransactionalPortalCacheHelper.TRANSACTION_LIFECYCLE_LISTENER;
            assertClasses.add(transactionLifecycleListener.getClass());
        }
    };

    @Test
    public void testConcurrentTransactionForMVCCPortalCache() throws Exception {
        _setEnableTransactionalCache(true);
        TransactionalPortalCache<String, String> transactionalPortalCache = new TransactionalPortalCache(_portalCache, true);
        // Two read only transactions do put
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1, true, TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2, true, false);
        _testCacheListener.assertPut(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertPut(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertPut(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        _testCacheReplicator.assertPut(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2);
        _testCacheReplicator.assertActionsCount(2);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        _testCacheListener.reset();
        _testCacheReplicator.reset();
        // Two read only transactions do remove
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, null, true, TransactionalPortalCacheTest._KEY_2, null, true, false);
        _testCacheListener.assertActionsCount(0);
        _testCacheReplicator.assertActionsCount(0);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        // One read only transaction and one write transaction do put
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2, true, TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1, false, false);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheReplicator.assertActionsCount(2);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        _testCacheListener.reset();
        _testCacheReplicator.reset();
        // One write transaction and one read only transaction do put
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1, false, TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2, true, false);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2);
        _testCacheReplicator.assertActionsCount(2);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        _testCacheListener.reset();
        _testCacheReplicator.reset();
        // Two write transactions do put
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2, false, TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1, false, false);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheReplicator.assertActionsCount(2);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        _testCacheListener.reset();
        _testCacheReplicator.reset();
        // Two write transactions do remove without replicator
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, null, false, TransactionalPortalCacheTest._KEY_2, null, false, true);
        _testCacheListener.assertRemoved(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertRemoved(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertActionsCount(0);
        Assert.assertNull(_portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertNull(_portalCache.get(TransactionalPortalCacheTest._KEY_2));
    }

    @Test
    public void testConcurrentTransactionForNonmvccPortalCache() throws Exception {
        _setEnableTransactionalCache(true);
        TransactionalPortalCache<String, String> transactionalPortalCache = new TransactionalPortalCache(_portalCache, false);
        // Two read only transactions do put
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1, true, TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2, true, false);
        _testCacheListener.assertPut(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertPut(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertPut(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        _testCacheReplicator.assertPut(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2);
        _testCacheReplicator.assertActionsCount(2);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        _testCacheListener.reset();
        _testCacheReplicator.reset();
        // Two read only transactions do remove
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, null, true, TransactionalPortalCacheTest._KEY_2, null, true, false);
        _testCacheListener.assertActionsCount(0);
        _testCacheReplicator.assertActionsCount(0);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        // One read only transaction and one write transaction do put
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2, true, TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1, false, false);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheReplicator.assertActionsCount(2);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        _testCacheListener.reset();
        _testCacheReplicator.reset();
        // One write transaction and one read only transaction do put
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1, false, TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2, true, false);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertActionsCount(1);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        _testCacheReplicator.assertActionsCount(1);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_2));
        _testCacheListener.reset();
        _testCacheReplicator.reset();
        // Two write transactions do put
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2, false, TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_2, false, false);
        _testCacheListener.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertRemoved(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertUpdated(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheReplicator.assertRemoved(TransactionalPortalCacheTest._KEY_2, TransactionalPortalCacheTest._VALUE_1);
        _testCacheReplicator.assertActionsCount(2);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertNull(_portalCache.get(TransactionalPortalCacheTest._KEY_2));
        _testCacheListener.reset();
        _testCacheReplicator.reset();
        // Two write transactions do remove without replicator
        _invokeTransactionalPortalCacheConcurrently(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, null, false, TransactionalPortalCacheTest._KEY_2, null, false, true);
        _testCacheListener.assertRemoved(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        _testCacheListener.assertRemoved(TransactionalPortalCacheTest._KEY_2, null);
        _testCacheListener.assertActionsCount(2);
        _testCacheReplicator.assertActionsCount(0);
        Assert.assertNull(_portalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertNull(_portalCache.get(TransactionalPortalCacheTest._KEY_2));
    }

    @Test
    public void testMisc() {
        // For code coverage
        new TransactionalPortalCacheHelper();
        _setEnableTransactionalCache(true);
        TransactionalPortalCacheHelper.begin();
        TransactionalPortalCache<String, String> transactionalPortalCache = new TransactionalPortalCache(_portalCache);
        TransactionalPortalCacheHelper.put(transactionalPortalCache, TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1, 0);
        TransactionalPortalCacheHelper.removeAll(transactionalPortalCache);
        TransactionalPortalCacheHelper.commit();
        TransactionLifecycleListener transactionLifecycleListener = TransactionalPortalCacheHelper.TRANSACTION_LIFECYCLE_LISTENER;
        _setEnableTransactionalCache(false);
        transactionLifecycleListener.created(null, null);
        transactionLifecycleListener.committed(null, null);
        transactionLifecycleListener.rollbacked(null, null, null);
    }

    @Test
    public void testNoneTransactionalCache() {
        _setEnableTransactionalCache(false);
        Assert.assertFalse("TransactionalPortalCacheHelper should be disabled", TransactionalPortalCacheHelper.isEnabled());
        // MVCC portal cache when transactional cache is disabled
        _testNoneTransactionalPortalCache(new TransactionalPortalCache(_portalCache, true));
        // Non MVCC portal cache when transactional cache is disabled
        _testNoneTransactionalPortalCache(new TransactionalPortalCache(_portalCache, false));
        // MVCC portal cache when not used in transaction
        _setEnableTransactionalCache(true);
        Assert.assertFalse("TransactionalPortalCacheHelper should be disabled", TransactionalPortalCacheHelper.isEnabled());
        _testNoneTransactionalPortalCache(new TransactionalPortalCache(_portalCache, true));
        // Non MVCC portal cache when not used in transaction
        _testNoneTransactionalPortalCache(new TransactionalPortalCache(_portalCache, false));
    }

    @Test
    public void testTransactionalCache() {
        _setEnableTransactionalCache(true);
        // MVCC portal cache without ttl
        _testTransactionalPortalCache(new TransactionalPortalCache(_portalCache, true), false, true);
        // Non MVCC portal cache without ttl
        _testTransactionalPortalCache(new TransactionalPortalCache(_portalCache, false), false, false);
        // MVCC portal cache with ttl
        _testTransactionalPortalCache(new TransactionalPortalCache(_portalCache, true), true, true);
        // Non MVCC portal cache with ttl
        _testTransactionalPortalCache(new TransactionalPortalCache(_portalCache, false), true, false);
    }

    @Test
    public void testTransactionalCacheWithParameterValidation() {
        _setEnableTransactionalCache(true);
        TransactionalPortalCache<String, String> transactionalPortalCache = new TransactionalPortalCache(_portalCache, true);
        _portalCache.put(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1);
        TransactionalPortalCacheHelper.begin();
        // Get
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, transactionalPortalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        // Get with null key
        try {
            transactionalPortalCache.get(null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        // Put
        transactionalPortalCache.put(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_2);
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_2, transactionalPortalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        // Put with null key
        try {
            transactionalPortalCache.put(null, TransactionalPortalCacheTest._VALUE_1);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        // Put with null value
        try {
            transactionalPortalCache.put(TransactionalPortalCacheTest._KEY_1, null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Value is null", npe.getMessage());
        }
        // Put with negative ttl
        try {
            transactionalPortalCache.put(TransactionalPortalCacheTest._KEY_1, TransactionalPortalCacheTest._VALUE_1, (-1));
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Time to live is negative", iae.getMessage());
        }
        // Remove
        transactionalPortalCache.remove(TransactionalPortalCacheTest._KEY_1);
        Assert.assertNull(transactionalPortalCache.get(TransactionalPortalCacheTest._KEY_1));
        Assert.assertEquals(TransactionalPortalCacheTest._VALUE_1, _portalCache.get(TransactionalPortalCacheTest._KEY_1));
        // Remove with null key
        try {
            transactionalPortalCache.remove(null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        TransactionalPortalCacheHelper.commit(false);
    }

    @Test
    public void testTransactionalPortalCacheHelperEnabled() {
        _setEnableTransactionalCache(false);
        Assert.assertFalse("TransactionalPortalCacheHelper should be disabled", TransactionalPortalCacheHelper.isEnabled());
        _setEnableTransactionalCache(true);
        Assert.assertFalse("TransactionalPortalCacheHelper should be disabled", TransactionalPortalCacheHelper.isEnabled());
        TransactionalPortalCacheHelper.begin();
        Assert.assertTrue("TransactionalPortalCacheHelper should be enabled", TransactionalPortalCacheHelper.isEnabled());
        TransactionalPortalCacheHelper.commit(false);
        ReflectionTestUtil.setFieldValue(TransactionalPortalCacheHelper.class, "_transactionalCacheEnabled", null);
        PropsTestUtil.setProps(Collections.emptyMap());
        Assert.assertFalse("TransactionalPortalCacheHelper should be disabled", TransactionalPortalCacheHelper.isEnabled());
    }

    @Test
    public void testTransactionalPortalCacheWithRealMVCCPortalCache() {
        _setEnableTransactionalCache(true);
        TransactionalPortalCache<String, MVCCModel> transactionalPortalCache = new TransactionalPortalCache(new com.liferay.portal.cache.MVCCPortalCache(new TestPortalCache("Test MVCC Portal Cache")), true);
        // Put real value and commit
        TransactionalPortalCacheHelper.begin();
        MockMVCCModel mockMVCCModel = new MockMVCCModel(0);
        transactionalPortalCache.put(TransactionalPortalCacheTest._KEY_1, mockMVCCModel);
        TransactionalPortalCacheHelper.commit(false);
        Assert.assertSame(mockMVCCModel, transactionalPortalCache.get(TransactionalPortalCacheTest._KEY_1));
        // Remove, put NullModel and commit
        TransactionalPortalCacheHelper.begin();
        transactionalPortalCache.remove(TransactionalPortalCacheTest._KEY_1);
        MVCCModel nullMVCCModel = ReflectionTestUtil.getFieldValue(BasePersistenceImpl.class, "nullModel");
        transactionalPortalCache.put(TransactionalPortalCacheTest._KEY_1, nullMVCCModel);
        TransactionalPortalCacheHelper.commit(false);
        Assert.assertSame(nullMVCCModel, transactionalPortalCache.get(TransactionalPortalCacheTest._KEY_1));
    }

    @Test
    public void testTransactionLifecycleListenerEnabledWithBarrier() {
        _setEnableTransactionalCache(true);
        _testTransactionLifecycleListenerEnabledWithBarrier(NOT_SUPPORTED);
        _testTransactionLifecycleListenerEnabledWithBarrier(NEVER);
        _testTransactionLifecycleListenerEnabledWithBarrier(NESTED);
    }

    @Test
    public void testTransactionLifecycleListenerEnabledWithExistTransaction() {
        _setEnableTransactionalCache(true);
        Assert.assertEquals(0, _getTransactionStackSize());
        TransactionLifecycleListener transactionLifecycleListener = TransactionalPortalCacheHelper.TRANSACTION_LIFECYCLE_LISTENER;
        TransactionAttribute.Builder builder = new TransactionAttribute.Builder();
        TransactionAttribute transactionAttribute = builder.build();
        TransactionStatus transactionStatus = new TransactionalPortalCacheTest.TestTrasactionStatus(false, false, false);
        transactionLifecycleListener.created(transactionAttribute, transactionStatus);
        Assert.assertEquals(0, _getTransactionStackSize());
        transactionLifecycleListener.committed(transactionAttribute, transactionStatus);
        Assert.assertEquals(0, _getTransactionStackSize());
        transactionLifecycleListener.created(transactionAttribute, transactionStatus);
        Assert.assertEquals(0, _getTransactionStackSize());
        transactionLifecycleListener.rollbacked(transactionAttribute, transactionStatus, null);
        Assert.assertEquals(0, _getTransactionStackSize());
    }

    @Test
    public void testTransactionLifecycleListenerEnabledWithoutBarrier() {
        _setEnableTransactionalCache(true);
        _testTransactionLifecycleListenerEnabledWithoutBarrier(REQUIRED);
        _testTransactionLifecycleListenerEnabledWithoutBarrier(SUPPORTS);
        _testTransactionLifecycleListenerEnabledWithoutBarrier(MANDATORY);
        _testTransactionLifecycleListenerEnabledWithoutBarrier(REQUIRES_NEW);
    }

    private static final String _KEY_1 = "KEY_1";

    private static final String _KEY_2 = "KEY_2";

    private static final String _VALUE_1 = "VALUE_1";

    private static final String _VALUE_2 = "VALUE_2";

    private PortalCache<String, String> _portalCache;

    private TestPortalCacheListener<String, String> _testCacheListener;

    private TestPortalCacheReplicator<String, String> _testCacheReplicator;

    private static class TestCallable implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            TransactionalPortalCacheHelper.begin();
            if (_skipReplicator) {
                if ((_value) == null) {
                    PortalCacheHelperUtil.removeWithoutReplicator(_transactionalPortalCache, _key);
                } else {
                    PortalCacheHelperUtil.putWithoutReplicator(_transactionalPortalCache, _key, _value);
                }
            } else {
                if ((_value) == null) {
                    _transactionalPortalCache.remove(_key);
                } else {
                    _transactionalPortalCache.put(_key, _value);
                }
            }
            _waitCountDownLatch.countDown();
            _blockCountDownLatch.await();
            TransactionalPortalCacheHelper.commit(_readOnly);
            return null;
        }

        public void unblock() {
            _blockCountDownLatch.countDown();
        }

        public void waitUntilBlock() throws InterruptedException {
            _waitCountDownLatch.await();
        }

        private TestCallable(TransactionalPortalCache<String, String> transactionalPortalCache, String key, String value, boolean readOnly, boolean skipReplicator) {
            _transactionalPortalCache = transactionalPortalCache;
            _key = key;
            _value = value;
            _readOnly = readOnly;
            _skipReplicator = skipReplicator;
        }

        private final CountDownLatch _blockCountDownLatch = new CountDownLatch(1);

        private final String _key;

        private final boolean _readOnly;

        private final boolean _skipReplicator;

        private final TransactionalPortalCache<String, String> _transactionalPortalCache;

        private final String _value;

        private final CountDownLatch _waitCountDownLatch = new CountDownLatch(1);
    }

    private static class TestTrasactionStatus implements TransactionStatus {
        @Override
        public Object getPlatformTransactionManager() {
            return null;
        }

        @Override
        public boolean isCompleted() {
            return _completed;
        }

        @Override
        public boolean isNewTransaction() {
            return _newTransaction;
        }

        @Override
        public boolean isRollbackOnly() {
            return _rollbackOnly;
        }

        private TestTrasactionStatus(boolean newTransaction, boolean rollbackOnly, boolean completed) {
            _newTransaction = newTransaction;
            _rollbackOnly = rollbackOnly;
            _completed = completed;
        }

        private final boolean _completed;

        private final boolean _newTransaction;

        private final boolean _rollbackOnly;
    }
}


/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.datasource;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for FirstAvailableDataSourceSupplier
 */
@RunWith(RobolectricTestRunner.class)
public class FirstAvailableDataSourceSupplierTest extends DataSourceTestUtils.AbstractDataSourceSupplier {
    /**
     * All data sources failed, no intermediate results.
     */
    @Test
    public void testLifecycle_F1_F2_F3_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber1.onFailure(mSrc1);
        mInOrder.verify(mSrc1).close();
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribe(mDataSourceSupplier2, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber2.onFailure(mSrc2);
        mInOrder.verify(mSrc2).close();
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        Throwable throwable = Mockito.mock(Throwable.class);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        subscriber3.onFailure(mSrc3);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.ON_FAILURE);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        testClose(dataSource);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    /**
     * All data sources failed, second data source produced multiple intermediate results.
     */
    @Test
    public void testLifecycle_F1_I2_I2_F2_F3_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber1.onFailure(mSrc1);
        mInOrder.verify(mSrc1).close();
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribe(mDataSourceSupplier2, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        Object val2a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        Object val2b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber2.onFailure(mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        Throwable throwable = Mockito.mock(Throwable.class);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        subscriber3.onFailure(mSrc3);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.ON_FAILURE);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.FAILED, throwable);
        testClose(dataSource, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    /**
     * All data sources failed, first two data sources produced intermediate results. Only first kept.
     */
    @Test
    public void testLifecycle_I1_F1_I2_F2_F3_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        Object val1 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber1.onFailure(mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribe(mDataSourceSupplier2, mSrc2);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        // I2 gets ignored because we already have I1
        Object val2 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber2.onFailure(mSrc2);
        mInOrder.verify(mSrc2).close();
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        Throwable throwable = Mockito.mock(Throwable.class);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        subscriber3.onFailure(mSrc3);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.ON_FAILURE);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.FAILED, throwable);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    /**
     * First data source failed, second succeeded, no intermediate results.
     */
    @Test
    public void testLifecycle_F1_S2_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber1.onFailure(mSrc1);
        mInOrder.verify(mSrc1).close();
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribe(mDataSourceSupplier2, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * First data source succeeded, no intermediate results.
     */
    @Test
    public void testLifecycle_S1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * First data source succeeded, with multiple intermediate results.
     */
    @Test
    public void testLifecycle_I1_I1_S1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        Object val1 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        Object val2 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * First data source failed with intermediate results, second succeeded with intermediate results.
     */
    @Test
    public void testLifecycle_I1_F1_I2_S2_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        Object val1 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber1.onFailure(mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribe(mDataSourceSupplier2, mSrc2);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        // I2 gets ignored because we already have I1
        Object val2 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        mInOrder.verify(mSrc1).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * First data source failed with intermediate results, second had intermediate results but closed.
     */
    @Test
    public void testLifecycle_I1_F1_I2_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        Object val1 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber1.onFailure(mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribe(mDataSourceSupplier2, mSrc2);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        // I2 gets ignored because we already have I1
        Object val2 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1, mSrc2);
        verifySubscriber(dataSource, null, DataSourceTestUtils.ON_CANCELLATION);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Early close with no results.
     */
    @Test
    public void testLifecycle_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        testClose(dataSource, mSrc1);
        verifySubscriber(dataSource, null, DataSourceTestUtils.ON_CANCELLATION);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Ignore callbacks after closed.
     */
    @Test
    public void testLifecycle_I1_C_S1() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        Object val1 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifySubscriber(dataSource, null, DataSourceTestUtils.ON_CANCELLATION);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Test data source without result
     */
    @Test
    public void testLifecycle_WithoutResult_NI1_NS1_I2_S2_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribe(mDataSourceSupplier1, mSrc1);
        // I1 gets ignored because there is no result
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        // S1 gets ignored because there is no result
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        mInOrder.verify(mSrc1).close();
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribe(mDataSourceSupplier2, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        Object val2a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        Object val2b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }
}


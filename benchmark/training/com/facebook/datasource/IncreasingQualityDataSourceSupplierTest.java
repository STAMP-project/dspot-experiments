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
 * Tests for IncreasingQualityDataSourceSupplier
 */
@RunWith(RobolectricTestRunner.class)
public class IncreasingQualityDataSourceSupplierTest extends DataSourceTestUtils.AbstractDataSourceSupplier {
    /**
     * All data sources failed, highest-quality failed last, no intermediate results.
     */
    @Test
    public void testLifecycle_F2_F3_F1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber2.onFailure(mSrc2);
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber3.onFailure(mSrc3);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        Throwable throwable = Mockito.mock(Throwable.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        subscriber1.onFailure(mSrc1);
        mInOrder.verify(mSrc1).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_FAILURE);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        testClose(dataSource);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    /**
     * Highest-quality data source failed second, result of the third data source is ignored.
     */
    @Test
    public void testLifecycle_F2_F1_S3_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribeM(mDataSourceSupplier3, mSrc3);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber2.onFailure(mSrc2);
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        // src1 is closed and failure cause stored, but dataSource is not failed
        Throwable throwable = Mockito.mock(Throwable.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        subscriber1.onFailure(mSrc1);
        mInOrder.verify(mSrc1).close();
        mInOrder.verify(mSrc1).getFailureCause();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        // src3's result is used but datasource is marked failed with the original cause from src1
        final Object result = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, result, DataSourceTestUtils.NOT_FAILED, null);
        subscriber3.onNewResult(mSrc3);
        mInOrder.verify(mDataSubscriber).onNewResult(dataSource);
        mInOrder.verify(mDataSubscriber).onFailure(dataSource);
        verifyState(dataSource, mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, result, DataSourceTestUtils.FAILED, throwable);
        testClose(dataSource, mSrc3);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    /**
     * Highest-quality data source failed, result of the third data source is ignored.
     * Second data source produced intermediate result first, the result is preserved until closed.
     */
    @Test
    public void testLifecycle_I2_F2_F1_S3_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        Object val2 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber2.onFailure(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        Throwable throwable = Mockito.mock(Throwable.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        subscriber1.onFailure(mSrc1);
        mInOrder.verify(mSrc1).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_FAILURE);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.FAILED, throwable);
        // gets ignored because DS1 failed
        // besides, this data source shouldn't have finished as it was supposed to be closed!
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, Mockito.mock(Object.class), DataSourceTestUtils.NOT_FAILED, null);
        subscriber3.onFailure(mSrc3);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.FAILED, throwable);
        testClose(dataSource, mSrc2);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    /**
     * Second data source produced multiple intermediate results first, intermediate result of
     * highest-quality data source gets ignored afterwards. Second data source fails and first data
     * source produced another intermediate result, but it gets ignored again. Finally, first data
     * source produced its final result which is set.
     */
    @Test
    public void testLifecycle_I2_I2_I1_F2_I1_S1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        Object val2a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        Object val2b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        // gets ignored because DS2 was first to produce result
        Object val1a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.FAILED, Mockito.mock(Throwable.class));
        subscriber2.onFailure(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        // gets ignored because DS2 was first to produce result
        Object val1b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        Object val1c = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1c, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1c, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Interleaved results.
     */
    @Test
    public void testLifecycle_I3_I2_I3_S2_I1_S1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        Object val3a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val3a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber3.onNewResult(mSrc3);
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val3a, DataSourceTestUtils.NOT_FAILED, null);
        // gets ignored because DS3 was first
        Object val2a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val3a, DataSourceTestUtils.NOT_FAILED, null);
        Object val3b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val3b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber3.onNewResult(mSrc3);
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val3b, DataSourceTestUtils.NOT_FAILED, null);
        Object val2b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        // gets ignored because DS2 was first
        Object val1a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        Object val1b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1b, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Second data source produced its final result, followed by the first data source.
     */
    @Test
    public void testLifecycle_S2_S1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        Object val2 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2, DataSourceTestUtils.NOT_FAILED, null);
        Object val1 = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Highest-quality data source was first to produce result, other data sources got closed.
     */
    @Test
    public void testLifecycle_I1_S1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        Object val1a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        mInOrder.verify(mSrc3).close();
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val1a, DataSourceTestUtils.NOT_FAILED, null);
        Object val1b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1b, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Highest-quality data source was first to produce result, other data sources got closed.
     */
    @Test
    public void testLifecycle_S1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        Object val1b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        mInOrder.verify(mSrc3).close();
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val1b, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Early close with intermediate result.
     */
    @Test
    public void testLifecycle_I2_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        Object val2a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
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
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        testClose(dataSource, mSrc1, mSrc2, mSrc3);
        verifySubscriber(dataSource, null, DataSourceTestUtils.ON_CANCELLATION);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Ignore callbacks after closed.
     */
    @Test
    public void testLifecycle_I2_C_S1() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        Object val2a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1, mSrc2);
        verifySubscriber(dataSource, null, DataSourceTestUtils.ON_CANCELLATION);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Test data source without result
     */
    @Test
    public void testLifecycle_WithoutResult_NI2_NS2_I3_S3_S1_C() {
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        DataSubscriber<Object> subscriber3 = verifyGetAndSubscribe(mDataSourceSupplier3, mSrc3);
        // I2 gets ignored because there is no result
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        // S2 gets ignored because there is no result
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.NO_INTERACTIONS);
        verifyState(dataSource, null, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        Object val3a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val3a, DataSourceTestUtils.NOT_FAILED, null);
        subscriber3.onNewResult(mSrc3);
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val3a, DataSourceTestUtils.NOT_FAILED, null);
        Object val3b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val3b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber3.onNewResult(mSrc3);
        verifySubscriber(dataSource, mSrc3, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc3, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val3b, DataSourceTestUtils.NOT_FAILED, null);
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        mInOrder.verify(mSrc3).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Immediate result of low-res data source followed by delayed result of the first data source.
     */
    @Test
    public void testLifecycle_ImmediateLowRes() {
        Object val2a = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.AbstractDataSourceSupplier.respondOnSubscribe(mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        DataSubscriber<Object> subscriber2 = verifyGetAndSubscribeM(mDataSourceSupplier2, mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2a, DataSourceTestUtils.NOT_FAILED, null);
        Object val2b = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        subscriber2.onNewResult(mSrc2);
        verifySubscriber(dataSource, mSrc2, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc2, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, val2b, DataSourceTestUtils.NOT_FAILED, null);
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        subscriber1.onNewResult(mSrc1);
        mInOrder.verify(mSrc2).close();
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    /**
     * Immediate finish of the first data source.
     */
    @Test
    public void testLifecycle_ImmediateFinish() {
        Object val = Mockito.mock(Object.class);
        DataSourceTestUtils.setState(mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        DataSourceTestUtils.AbstractDataSourceSupplier.respondOnSubscribe(mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        DataSource<Object> dataSource = getAndSubscribe();
        DataSubscriber<Object> subscriber1 = verifyGetAndSubscribeM(mDataSourceSupplier1, mSrc1);
        verifySubscriber(dataSource, mSrc1, DataSourceTestUtils.ON_NEW_RESULT);
        verifyState(dataSource, mSrc1, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, val, DataSourceTestUtils.NOT_FAILED, null);
        testClose(dataSource, mSrc1);
        verifyState(dataSource, null, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }
}


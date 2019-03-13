/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.datasource;


import Consumer.IS_LAST;
import Consumer.NO_FLAGS;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.SettableProducerContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(RobolectricTestRunner.class)
public class ProducerToDataSourceAdapterTest {
    @Mock
    public RequestListener mRequestListener;

    private static final boolean FINISHED = true;

    private static final boolean NOT_FINISHED = false;

    private static final boolean WITH_RESULT = true;

    private static final boolean WITHOUT_RESULT = false;

    private static final boolean FAILED = true;

    private static final boolean NOT_FAILED = false;

    private static final boolean LAST = true;

    private static final boolean INTERMEDIATE = false;

    private static final int NO_INTERACTIONS = 0;

    private static final int ON_NEW_RESULT = 1;

    private static final int ON_FAILURE = 2;

    private static final Exception NPE = new NullPointerException();

    private static final String mRequestId = "requestId";

    private Object mResult1;

    private Object mResult2;

    private Object mResult3;

    private Exception mException;

    private DataSubscriber<Object> mDataSubscriber1;

    private DataSubscriber<Object> mDataSubscriber2;

    private SettableProducerContext mSettableProducerContext;

    private Producer<Object> mProducer;

    private Consumer<Object> mInternalConsumer;

    private DataSource<Object> mDataSource;

    @Test
    public void testInitialState() {
        verifyInitial();
    }

    @Test
    public void test_C_a() {
        testClose(ProducerToDataSourceAdapterTest.NOT_FINISHED, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.NO_INTERACTIONS);
    }

    @Test
    public void test_C_I_a() {
        testClose(ProducerToDataSourceAdapterTest.NOT_FINISHED, 1);
        mInternalConsumer.onNewResult(mResult2, NO_FLAGS);
        verifyClosed(ProducerToDataSourceAdapterTest.NOT_FINISHED, null);
        testSubscribe(ProducerToDataSourceAdapterTest.NO_INTERACTIONS);
    }

    @Test
    public void test_C_L_a() {
        testClose(ProducerToDataSourceAdapterTest.NOT_FINISHED, 1);
        mInternalConsumer.onNewResult(mResult2, IS_LAST);
        verifyClosed(ProducerToDataSourceAdapterTest.NOT_FINISHED, null);
        testSubscribe(ProducerToDataSourceAdapterTest.NO_INTERACTIONS);
    }

    @Test
    public void testC_F_a() {
        testClose(ProducerToDataSourceAdapterTest.NOT_FINISHED, 1);
        mInternalConsumer.onFailure(mException);
        verifyClosed(ProducerToDataSourceAdapterTest.NOT_FINISHED, null);
        testSubscribe(ProducerToDataSourceAdapterTest.NO_INTERACTIONS);
    }

    @Test
    public void test_I_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.NOT_FINISHED, 2);
    }

    @Test
    public void test_I_I_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResult2, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.NOT_FINISHED, 2);
    }

    @Test
    public void test_I_I_L_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResult2, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResult3, ProducerToDataSourceAdapterTest.LAST, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_I_I_F_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResult2, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testFailure(mResult2, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_I_L_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResult2, ProducerToDataSourceAdapterTest.LAST, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_I_F_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testFailure(mResult1, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_L_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.LAST, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_L_I_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.LAST, 1);
        mInternalConsumer.onNewResult(mResult2, NO_FLAGS);
        verifyWithResult(mResult1, ProducerToDataSourceAdapterTest.LAST);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_L_L_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.LAST, 1);
        mInternalConsumer.onNewResult(mResult2, IS_LAST);
        verifyWithResult(mResult1, ProducerToDataSourceAdapterTest.LAST);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_L_F_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.LAST, 1);
        mInternalConsumer.onFailure(mException);
        verifyWithResult(mResult1, ProducerToDataSourceAdapterTest.LAST);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_F_a_C() {
        testFailure(null, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_F_I_a_C() {
        testFailure(null, 1);
        mInternalConsumer.onNewResult(mResult1, NO_FLAGS);
        verifyFailed(null, mException);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_F_L_a_C() {
        testFailure(null, 1);
        mInternalConsumer.onNewResult(mResult1, IS_LAST);
        verifyFailed(null, mException);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_F_F_a_C() {
        testFailure(null, 1);
        mInternalConsumer.onFailure(Mockito.mock(Throwable.class));
        verifyFailed(null, mException);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_NI_S_a_C() {
        mInternalConsumer.onNewResult(null, NO_FLAGS);
        Mockito.verify(mDataSubscriber1).onNewResult(mDataSource);
        verifyWithResult(null, ProducerToDataSourceAdapterTest.INTERMEDIATE);
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.LAST, 1);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_NI_a_NL_C() {
        mInternalConsumer.onNewResult(null, NO_FLAGS);
        Mockito.verify(mDataSubscriber1).onNewResult(mDataSource);
        verifyWithResult(null, ProducerToDataSourceAdapterTest.INTERMEDIATE);
        testSubscribe(ProducerToDataSourceAdapterTest.NO_INTERACTIONS);
        mInternalConsumer.onNewResult(null, IS_LAST);
        Mockito.verify(mRequestListener).onRequestSuccess(mSettableProducerContext.getImageRequest(), ProducerToDataSourceAdapterTest.mRequestId, mSettableProducerContext.isPrefetch());
        Mockito.verify(mDataSubscriber1).onNewResult(mDataSource);
        Mockito.verify(mDataSubscriber2).onNewResult(mDataSource);
        verifyWithResult(null, ProducerToDataSourceAdapterTest.LAST);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_I_NL_a_C() {
        testNewResult(mResult1, ProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        mInternalConsumer.onNewResult(null, IS_LAST);
        Mockito.verify(mRequestListener).onRequestSuccess(mSettableProducerContext.getImageRequest(), ProducerToDataSourceAdapterTest.mRequestId, mSettableProducerContext.isPrefetch());
        Mockito.verify(mDataSubscriber1).onNewResult(mDataSource);
        verifyWithResult(null, ProducerToDataSourceAdapterTest.LAST);
        testSubscribe(ProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(ProducerToDataSourceAdapterTest.FINISHED, 2);
    }
}


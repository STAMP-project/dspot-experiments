/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.datasource;


import Consumer.IS_LAST;
import Consumer.NO_FLAGS;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
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
public class CloseableProducerToDataSourceAdapterTest {
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

    private ResourceReleaser mResourceReleaser;

    private CloseableReference<Object> mResultRef1;

    private CloseableReference<Object> mResultRef2;

    private CloseableReference<Object> mResultRef3;

    private Exception mException;

    private DataSubscriber<CloseableReference<Object>> mDataSubscriber1;

    private DataSubscriber<CloseableReference<Object>> mDataSubscriber2;

    private SettableProducerContext mSettableProducerContext;

    private Producer<CloseableReference<Object>> mProducer;

    private Consumer<CloseableReference<Object>> mInternalConsumer;

    private DataSource<CloseableReference<Object>> mDataSource;

    @Test
    public void testInitialState() {
        verifyInitial();
    }

    @Test
    public void test_C_a() {
        testClose(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.NO_INTERACTIONS);
    }

    @Test
    public void test_C_I_a() {
        testClose(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, 1);
        mInternalConsumer.onNewResult(mResultRef2, NO_FLAGS);
        verifyClosed(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, null);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.NO_INTERACTIONS);
    }

    @Test
    public void test_C_L_a() {
        testClose(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, 1);
        mInternalConsumer.onNewResult(mResultRef2, IS_LAST);
        verifyClosed(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, null);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.NO_INTERACTIONS);
    }

    @Test
    public void testC_F_a() {
        testClose(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, 1);
        mInternalConsumer.onFailure(mException);
        verifyClosed(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, null);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.NO_INTERACTIONS);
    }

    @Test
    public void test_I_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, 2);
    }

    @Test
    public void test_I_I_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResultRef2, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.NOT_FINISHED, 2);
    }

    @Test
    public void test_I_I_L_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResultRef2, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResultRef3, CloseableProducerToDataSourceAdapterTest.LAST, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_I_I_F_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResultRef2, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testFailure(mResultRef2, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_I_L_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testNewResult(mResultRef2, CloseableProducerToDataSourceAdapterTest.LAST, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_I_F_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        testFailure(mResultRef1, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_L_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.LAST, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_L_I_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.LAST, 1);
        mInternalConsumer.onNewResult(mResultRef2, NO_FLAGS);
        verifyWithResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.LAST);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_L_L_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.LAST, 1);
        mInternalConsumer.onNewResult(mResultRef2, IS_LAST);
        verifyWithResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.LAST);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_L_F_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.LAST, 1);
        mInternalConsumer.onFailure(mException);
        verifyWithResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.LAST);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_F_a_C() {
        testFailure(null, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_F_I_a_C() {
        testFailure(null, 1);
        mInternalConsumer.onNewResult(mResultRef1, NO_FLAGS);
        verifyFailed(null, mException);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_F_L_a_C() {
        testFailure(null, 1);
        mInternalConsumer.onNewResult(mResultRef1, IS_LAST);
        verifyFailed(null, mException);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_F_F_a_C() {
        testFailure(null, 1);
        mInternalConsumer.onFailure(Mockito.mock(Throwable.class));
        verifyFailed(null, mException);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_FAILURE);
        testClose(mException);
    }

    @Test
    public void test_NI_S_a_C() {
        mInternalConsumer.onNewResult(null, NO_FLAGS);
        Mockito.verify(mDataSubscriber1).onNewResult(mDataSource);
        verifyWithResult(null, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE);
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.LAST, 1);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_NI_a_NL_C() {
        mInternalConsumer.onNewResult(null, NO_FLAGS);
        Mockito.verify(mDataSubscriber1).onNewResult(mDataSource);
        verifyWithResult(null, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.NO_INTERACTIONS);
        mInternalConsumer.onNewResult(null, IS_LAST);
        Mockito.verify(mRequestListener).onRequestSuccess(mSettableProducerContext.getImageRequest(), CloseableProducerToDataSourceAdapterTest.mRequestId, mSettableProducerContext.isPrefetch());
        Mockito.verify(mDataSubscriber1).onNewResult(mDataSource);
        Mockito.verify(mDataSubscriber2).onNewResult(mDataSource);
        verifyWithResult(null, CloseableProducerToDataSourceAdapterTest.LAST);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }

    @Test
    public void test_I_NL_a_C() {
        testNewResult(mResultRef1, CloseableProducerToDataSourceAdapterTest.INTERMEDIATE, 1);
        mInternalConsumer.onNewResult(null, IS_LAST);
        Mockito.verify(mRequestListener).onRequestSuccess(mSettableProducerContext.getImageRequest(), CloseableProducerToDataSourceAdapterTest.mRequestId, mSettableProducerContext.isPrefetch());
        Mockito.verify(mDataSubscriber1).onNewResult(mDataSource);
        verifyWithResult(null, CloseableProducerToDataSourceAdapterTest.LAST);
        testSubscribe(CloseableProducerToDataSourceAdapterTest.ON_NEW_RESULT);
        testClose(CloseableProducerToDataSourceAdapterTest.FINISHED, 2);
    }
}


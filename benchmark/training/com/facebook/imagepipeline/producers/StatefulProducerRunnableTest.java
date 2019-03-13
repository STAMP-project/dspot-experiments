/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import com.facebook.common.internal.Supplier;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class StatefulProducerRunnableTest {
    private static final String REQUEST_ID = "awesomeRequestId";

    private static final String PRODUCER_NAME = "aBitLessAwesomeButStillAwesomeProducerName";

    @Mock
    public Consumer<Closeable> mConsumer;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Supplier<Closeable> mResultSupplier;

    @Mock
    public Closeable mResult;

    private RuntimeException mException;

    private Map<String, String> mSuccessMap;

    private Map<String, String> mFailureMap;

    private Map<String, String> mCancellationMap;

    private StatefulProducerRunnable<Closeable> mStatefulProducerRunnable;

    @Test
    public void testOnSuccess_extraMap() throws IOException {
        Mockito.doReturn(true).when(mProducerListener).requiresExtraMap(StatefulProducerRunnableTest.REQUEST_ID);
        Mockito.doReturn(mResult).when(mResultSupplier).get();
        mStatefulProducerRunnable.run();
        Mockito.verify(mConsumer).onNewResult(mResult, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME, mSuccessMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.verify(mResult).close();
    }

    @Test
    public void testOnSuccess_noExtraMap() throws IOException {
        Mockito.doReturn(mResult).when(mResultSupplier).get();
        mStatefulProducerRunnable.run();
        Mockito.verify(mConsumer).onNewResult(mResult, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.verify(mResult).close();
    }

    @Test
    public void testOnCancellation_extraMap() {
        Mockito.doReturn(true).when(mProducerListener).requiresExtraMap(StatefulProducerRunnableTest.REQUEST_ID);
        mStatefulProducerRunnable.cancel();
        Mockito.verify(mConsumer).onCancellation();
        Mockito.verify(mProducerListener).onProducerStart(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithCancellation(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME, mCancellationMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testOnCancellation_noExtraMap() {
        mStatefulProducerRunnable.cancel();
        Mockito.verify(mConsumer).onCancellation();
        Mockito.verify(mProducerListener).onProducerStart(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithCancellation(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testOnFailure_extraMap() {
        Mockito.doReturn(true).when(mProducerListener).requiresExtraMap(StatefulProducerRunnableTest.REQUEST_ID);
        Mockito.doThrow(mException).when(mResultSupplier).get();
        mStatefulProducerRunnable.run();
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME, mException, mFailureMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testOnFailure_noExtraMap() {
        Mockito.doThrow(mException).when(mResultSupplier).get();
        mStatefulProducerRunnable.run();
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(StatefulProducerRunnableTest.REQUEST_ID, StatefulProducerRunnableTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }
}


/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import ThreadHandoffProducer.PRODUCER_NAME;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.testing.TestExecutorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class ThreadHandoffProducerTest {
    @Mock
    public Producer mInputProducer;

    @Mock
    public Consumer mConsumer;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerListener mProducerListener;

    private final String mRequestId = "mRequestId";

    private SettableProducerContext mProducerContext;

    private ThreadHandoffProducer mThreadHandoffProducer;

    private TestExecutorService mTestExecutorService;

    @Test
    public void testSuccess() {
        mThreadHandoffProducer.produceResults(mConsumer, mProducerContext);
        mTestExecutorService.runUntilIdle();
        Mockito.verify(mInputProducer).produceResults(mConsumer, mProducerContext);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, PRODUCER_NAME, null);
        Mockito.verifyNoMoreInteractions(mProducerListener);
    }

    @Test
    public void testCancellation() {
        mThreadHandoffProducer.produceResults(mConsumer, mProducerContext);
        mProducerContext.cancel();
        mTestExecutorService.runUntilIdle();
        Mockito.verify(mInputProducer, Mockito.never()).produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onCancellation();
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, PRODUCER_NAME);
        Mockito.verify(mProducerListener).requiresExtraMap(mRequestId);
        Mockito.verify(mProducerListener).onProducerFinishWithCancellation(mRequestId, PRODUCER_NAME, null);
        Mockito.verifyNoMoreInteractions(mProducerListener);
    }
}


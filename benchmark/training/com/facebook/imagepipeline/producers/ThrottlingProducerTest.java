/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.NO_FLAGS;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;
import static ThrottlingProducer.PRODUCER_NAME;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class ThrottlingProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final int MAX_SIMULTANEOUS_REQUESTS = 2;

    @Mock
    public Producer<Object> mInputProducer;

    @Mock
    public Exception mException;

    private final Consumer<Object>[] mConsumers = new Consumer[5];

    private final ProducerContext[] mProducerContexts = new ProducerContext[5];

    private final ProducerListener[] mProducerListeners = new ProducerListener[5];

    private final String[] mRequestIds = new String[5];

    private final Consumer<Object>[] mThrottlerConsumers = new Consumer[5];

    private final Object[] mResults = new Object[5];

    private ThrottlingProducer<Object> mThrottlingProducer;

    @Test
    public void testThrottling() {
        // First two requests are passed on immediately
        mThrottlingProducer.produceResults(mConsumers[0], mProducerContexts[0]);
        Assert.assertNotNull(mThrottlerConsumers[0]);
        Mockito.verify(mProducerListeners[0]).onProducerStart(mRequestIds[0], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[0]).onProducerFinishWithSuccess(mRequestIds[0], ThrottlingProducerTest.PRODUCER_NAME, null);
        mThrottlingProducer.produceResults(mConsumers[1], mProducerContexts[1]);
        Assert.assertNotNull(mThrottlerConsumers[1]);
        Mockito.verify(mProducerListeners[1]).onProducerStart(mRequestIds[1], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[1]).onProducerFinishWithSuccess(mRequestIds[1], ThrottlingProducerTest.PRODUCER_NAME, null);
        // Third and fourth requests are queued up
        mThrottlingProducer.produceResults(mConsumers[2], mProducerContexts[2]);
        Assert.assertNull(mThrottlerConsumers[2]);
        Mockito.verify(mProducerListeners[2]).onProducerStart(mRequestIds[2], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[2], Mockito.never()).onProducerFinishWithSuccess(mRequestIds[2], ThrottlingProducerTest.PRODUCER_NAME, null);
        mThrottlingProducer.produceResults(mConsumers[3], mProducerContexts[3]);
        Assert.assertNull(mThrottlerConsumers[3]);
        Mockito.verify(mProducerListeners[3]).onProducerStart(mRequestIds[3], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[3], Mockito.never()).onProducerFinishWithSuccess(mRequestIds[3], ThrottlingProducerTest.PRODUCER_NAME, null);
        // First request fails, third request is kicked off, fourth request remains in queue
        mThrottlerConsumers[0].onFailure(mException);
        Mockito.verify(mConsumers[0]).onFailure(mException);
        Assert.assertNotNull(mThrottlerConsumers[2]);
        Mockito.verify(mProducerListeners[2]).onProducerFinishWithSuccess(mRequestIds[2], ThrottlingProducerTest.PRODUCER_NAME, null);
        Assert.assertNull(mThrottlerConsumers[3]);
        Mockito.verify(mProducerListeners[3], Mockito.never()).onProducerFinishWithSuccess(mRequestIds[3], ThrottlingProducerTest.PRODUCER_NAME, null);
        // Fifth request is queued up
        mThrottlingProducer.produceResults(mConsumers[4], mProducerContexts[4]);
        Assert.assertNull(mThrottlerConsumers[4]);
        Mockito.verify(mProducerListeners[4]).onProducerStart(mRequestIds[4], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[4], Mockito.never()).onProducerFinishWithSuccess(mRequestIds[4], ThrottlingProducerTest.PRODUCER_NAME, null);
        // Second request gives intermediate result, no new request is kicked off
        Object intermediateResult = Mockito.mock(Object.class);
        mThrottlerConsumers[1].onNewResult(intermediateResult, NO_FLAGS);
        Mockito.verify(mConsumers[1]).onNewResult(intermediateResult, NO_FLAGS);
        Assert.assertNull(mThrottlerConsumers[3]);
        Assert.assertNull(mThrottlerConsumers[4]);
        // Third request finishes, fourth request is kicked off
        mThrottlerConsumers[2].onNewResult(mResults[2], IS_LAST);
        Mockito.verify(mConsumers[2]).onNewResult(mResults[2], IS_LAST);
        Assert.assertNotNull(mThrottlerConsumers[3]);
        Mockito.verify(mProducerListeners[3]).onProducerFinishWithSuccess(mRequestIds[3], ThrottlingProducerTest.PRODUCER_NAME, null);
        Assert.assertNull(mThrottlerConsumers[4]);
        // Second request is cancelled, fifth request is kicked off
        mThrottlerConsumers[1].onCancellation();
        Mockito.verify(mConsumers[1]).onCancellation();
        Assert.assertNotNull(mThrottlerConsumers[4]);
        Mockito.verify(mProducerListeners[4]).onProducerFinishWithSuccess(mRequestIds[4], ThrottlingProducerTest.PRODUCER_NAME, null);
        // Fourth and fifth requests finish
        mThrottlerConsumers[3].onNewResult(mResults[3], IS_LAST);
        mThrottlerConsumers[4].onNewResult(mResults[4], IS_LAST);
    }

    @Test
    public void testNoThrottlingAfterRequestsFinish() {
        // First two requests are passed on immediately
        mThrottlingProducer.produceResults(mConsumers[0], mProducerContexts[0]);
        Assert.assertNotNull(mThrottlerConsumers[0]);
        Mockito.verify(mProducerListeners[0]).onProducerStart(mRequestIds[0], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[0]).onProducerFinishWithSuccess(mRequestIds[0], ThrottlingProducerTest.PRODUCER_NAME, null);
        mThrottlingProducer.produceResults(mConsumers[1], mProducerContexts[1]);
        Assert.assertNotNull(mThrottlerConsumers[1]);
        Mockito.verify(mProducerListeners[1]).onProducerStart(mRequestIds[1], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[1]).onProducerFinishWithSuccess(mRequestIds[1], ThrottlingProducerTest.PRODUCER_NAME, null);
        // First two requests finish
        mThrottlerConsumers[0].onNewResult(mResults[3], IS_LAST);
        mThrottlerConsumers[1].onNewResult(mResults[4], IS_LAST);
        // Next two requests are passed on immediately
        mThrottlingProducer.produceResults(mConsumers[2], mProducerContexts[2]);
        Assert.assertNotNull(mThrottlerConsumers[2]);
        Mockito.verify(mProducerListeners[2]).onProducerStart(mRequestIds[2], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[2]).onProducerFinishWithSuccess(mRequestIds[2], ThrottlingProducerTest.PRODUCER_NAME, null);
        mThrottlingProducer.produceResults(mConsumers[3], mProducerContexts[3]);
        Assert.assertNotNull(mThrottlerConsumers[3]);
        Mockito.verify(mProducerListeners[3]).onProducerStart(mRequestIds[3], ThrottlingProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListeners[3]).onProducerFinishWithSuccess(mRequestIds[3], ThrottlingProducerTest.PRODUCER_NAME, null);
        // Next two requests finish
        mThrottlerConsumers[2].onNewResult(mResults[3], IS_LAST);
        mThrottlerConsumers[3].onNewResult(mResults[4], IS_LAST);
    }
}


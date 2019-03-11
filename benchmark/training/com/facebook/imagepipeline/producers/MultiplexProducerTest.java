/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.NO_FLAGS;
import Consumer.Status;
import Priority.HIGH;
import Priority.LOW;
import Priority.MEDIUM;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheKey;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Checks basic properties of the multiplex producer, that is:
 *   - identical keys should be combined into the same request.
 *   - non-identical keys get their own request.
 *   - requests should only be cancelled if all underlying requests are cancelled.
 *   - requests should be cleared when they finish.
 *
 * <p>This test happens to use {@link BitmapMemoryCacheKeyMultiplexProducer}. The subclasses are so
 * similar that it's not worth doing a separate test for each one.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MultiplexProducerTest {
    /**
     * An extra flag to check we maintain other flags than just whether this is the last result
     */
    @Consumer.Status
    private final int TEST_FLAG = 1 << 11;

    @Mock
    public CacheKeyFactory mCacheKeyFactory;

    @Mock
    public Producer mInputProducer;

    @Mock
    public Exception mException;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Object mCallerContext;

    private SettableProducerContext mProducerContext1;

    private SettableProducerContext mProducerContext2;

    private SettableProducerContext mProducerContext3;

    private ImageRequest mImageRequest1;

    private ImageRequest mImageRequest2;

    private BitmapMemoryCacheKey mBitmapMemoryCacheKey1;

    private BitmapMemoryCacheKey mBitmapMemoryCacheKey2;

    private Consumer<CloseableReference<CloseableImage>> mConsumer1;

    private Consumer<CloseableReference<CloseableImage>> mConsumer2;

    private Consumer<CloseableReference<CloseableImage>> mConsumer3;

    private Consumer<CloseableReference<CloseableImage>> mForwardingConsumer1;

    private Consumer<CloseableReference<CloseableImage>> mForwardingConsumer2;

    private BaseProducerContext mMultiplexedContext1;

    private BaseProducerContext mMultiplexedContext2;

    private CloseableImage mFinalCloseableImage1;

    private CloseableImage mFinalCloseableImage2;

    private CloseableImage mIntermediateCloseableImage1;

    private CloseableImage mIntermediateCloseableImage2;

    private CloseableReference<CloseableImage> mFinalImageReference1;

    private CloseableReference<CloseableImage> mFinalImageReference2;

    private CloseableReference<CloseableImage> mIntermediateImageReference1;

    private CloseableReference<CloseableImage> mIntermediateImageReference2;

    private BitmapMemoryCacheKeyMultiplexProducer mMultiplexProducer;

    @Test
    public void testSingleRequest() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mForwardingConsumer1.onNewResult(mIntermediateImageReference1, NO_FLAGS);
        Mockito.verify(mConsumer1).onNewResult(mIntermediateImageReference1, NO_FLAGS);
        mForwardingConsumer1.onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Mockito.verify(mConsumer1).onNewResult(mIntermediateImageReference2, NO_FLAGS);
        mForwardingConsumer1.onNewResult(mFinalImageReference1, IS_LAST);
        Mockito.verify(mConsumer1).onNewResult(mFinalImageReference1, IS_LAST);
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
    }

    @Test
    public void testNewRequestGetsIntermediateResult() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mForwardingConsumer1.onNewResult(mIntermediateImageReference1, NO_FLAGS);
        mForwardingConsumer1.onNewResult(mIntermediateImageReference2, TEST_FLAG);
        ArgumentCaptor<CloseableReference> imageReferenceCaptor = ArgumentCaptor.forClass(CloseableReference.class);
        mMultiplexProducer.produceResults(mConsumer2, mProducerContext2);
        Mockito.verify(mConsumer2).onNewResult(imageReferenceCaptor.capture(), ArgumentMatchers.eq(TEST_FLAG));
        Assert.assertEquals(imageReferenceCaptor.getValue().getUnderlyingReferenceTestOnly(), mIntermediateImageReference2.getUnderlyingReferenceTestOnly());
    }

    @Test
    public void testTwoIdenticalRequestAndOneDifferent() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer2, mProducerContext2);
        mForwardingConsumer1.onNewResult(mIntermediateImageReference1, NO_FLAGS);
        mMultiplexProducer.produceResults(mConsumer3, mProducerContext3);
        Mockito.verify(mConsumer3, Mockito.never()).onNewResult(mIntermediateImageReference1, NO_FLAGS);
        mForwardingConsumer2.onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Mockito.verify(mConsumer3).onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Mockito.verify(mConsumer1, Mockito.never()).onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Mockito.verify(mConsumer2, Mockito.never()).onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 2));
        mForwardingConsumer1.onNewResult(mFinalImageReference1, IS_LAST);
        Mockito.verify(mConsumer1).onNewResult(mFinalImageReference1, IS_LAST);
        Mockito.verify(mConsumer2).onNewResult(mFinalImageReference1, IS_LAST);
        Mockito.verify(mConsumer3, Mockito.never()).onNewResult(mFinalImageReference1, IS_LAST);
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 1));
        mForwardingConsumer2.onNewResult(mFinalImageReference2, IS_LAST);
        Mockito.verify(mConsumer3).onNewResult(mFinalImageReference2, IS_LAST);
        Mockito.verify(mConsumer1, Mockito.never()).onNewResult(mFinalImageReference2, IS_LAST);
        Mockito.verify(mConsumer2, Mockito.never()).onNewResult(mFinalImageReference2, IS_LAST);
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
    }

    @Test
    public void testMultiplexRequestFailure() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer2, mProducerContext1);
        mForwardingConsumer1.onFailure(mException);
        Mockito.verify(mConsumer1).onFailure(mException);
        Mockito.verify(mConsumer2).onFailure(mException);
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
    }

    @Test
    public void testTwoIdenticalInSequence() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mForwardingConsumer1.onNewResult(mFinalImageReference1, IS_LAST);
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
        mMultiplexProducer.produceResults(mConsumer2, mProducerContext2);
        mForwardingConsumer2.onNewResult(mFinalImageReference2, IS_LAST);
        Mockito.verify(mConsumer2).onNewResult(mFinalImageReference2, IS_LAST);
        Mockito.verify(mConsumer1, Mockito.never()).onNewResult(mFinalImageReference2, IS_LAST);
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
    }

    @Test
    public void testCancelSingleRequest() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mProducerContext1.cancel();
        Assert.assertTrue(mMultiplexedContext1.isCancelled());
        mForwardingConsumer1.onCancellation();
        Mockito.verify(mConsumer1).onCancellation();
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
        Mockito.verifyNoMoreInteractions(mConsumer1);
    }

    @Test
    public void testCancelMultiplexRequest() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer2, mProducerContext2);
        mMultiplexProducer.produceResults(mConsumer3, mProducerContext3);
        mProducerContext1.cancel();
        mForwardingConsumer1.onNewResult(mIntermediateImageReference1, TEST_FLAG);
        Mockito.verify(mConsumer1, Mockito.never()).onNewResult(mIntermediateImageReference1, TEST_FLAG);
        Mockito.verify(mConsumer2).onNewResult(mIntermediateImageReference1, TEST_FLAG);
        Mockito.verify(mConsumer3, Mockito.never()).onNewResult(mIntermediateImageReference1, TEST_FLAG);
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 2));
        mForwardingConsumer2.onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Mockito.verify(mConsumer3).onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Mockito.verify(mConsumer1, Mockito.never()).onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Mockito.verify(mConsumer2, Mockito.never()).onNewResult(mIntermediateImageReference2, NO_FLAGS);
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 2));
        mProducerContext3.cancel();
        mForwardingConsumer2.onCancellation();
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 1));
        mProducerContext2.cancel();
        mForwardingConsumer1.onCancellation();
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 0));
    }

    @Test
    public void testOnFailureThenCancel() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mForwardingConsumer1.onFailure(mException);
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
        mMultiplexProducer.produceResults(mConsumer2, mProducerContext2);
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 1));
        mProducerContext1.cancel();
        mForwardingConsumer1.onCancellation();
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 1));
    }

    @Test
    public void testCancelThenOnLastResult() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mProducerContext1.cancel();
        Assert.assertFalse(mMultiplexProducer.mMultiplexers.isEmpty());
        mForwardingConsumer1.onCancellation();
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
        mMultiplexProducer.produceResults(mConsumer2, mProducerContext2);
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 1));
        mForwardingConsumer1.onNewResult(mFinalImageReference1, IS_LAST);
        Assert.assertTrue(((mMultiplexProducer.mMultiplexers.size()) == 1));
    }

    @Test
    public void testRestartProducerOnLateCancellationCallback() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Mockito.verify(mInputProducer).produceResults(mForwardingConsumer1, mMultiplexedContext1);
        mProducerContext1.cancel();
        Mockito.verify(mConsumer1).onCancellation();
        mMultiplexProducer.produceResults(mConsumer2, mProducerContext2);
        Mockito.verify(mInputProducer).produceResults(ArgumentMatchers.any(Consumer.class), ArgumentMatchers.any(ProducerContext.class));
        mForwardingConsumer1.onCancellation();
        Assert.assertEquals(1, mMultiplexProducer.mMultiplexers.size());
        Mockito.verify(mInputProducer).produceResults(mForwardingConsumer2, mMultiplexedContext2);
        mForwardingConsumer2.onNewResult(mFinalImageReference1, IS_LAST);
        Mockito.verify(mConsumer2).onNewResult(mFinalImageReference1, IS_LAST);
        Assert.assertTrue(mMultiplexProducer.mMultiplexers.isEmpty());
    }

    @Test
    public void testIsPrefetchTrue() {
        mProducerContext1.setIsPrefetch(true);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testIsPrefetchFalse() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testCancelChangesIsPrefetchIfNoMoreNonPrefetch() {
        mProducerContext1.setIsPrefetch(true);
        mProducerContext2.setIsPrefetch(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
        ProducerContextCallbacks callbacks = Mockito.mock(ProducerContextCallbacks.class);
        mMultiplexedContext1.addCallbacks(callbacks);
        mProducerContext2.cancel();
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
        Mockito.verify(callbacks).onIsPrefetchChanged();
    }

    @Test
    public void testCancelDoesNotChangeIsPrefetchIfOtherIsNotPrefetch() {
        mProducerContext1.setIsPrefetch(false);
        mProducerContext2.setIsPrefetch(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
        mProducerContext2.cancel();
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testCancelDoesNotChangeIsPrefetchOtherIfAlreadyPrefetch() {
        mProducerContext1.setIsPrefetch(true);
        mProducerContext2.setIsPrefetch(true);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
        mProducerContext2.cancel();
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testAddConsumerChangesIsPrefetch() {
        mProducerContext1.setIsPrefetch(true);
        mProducerContext2.setIsPrefetch(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
        ProducerContextCallbacks callbacks = Mockito.mock(ProducerContextCallbacks.class);
        mMultiplexedContext1.addCallbacks(callbacks);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
        Mockito.verify(callbacks).onIsPrefetchChanged();
    }

    @Test
    public void testAddConsumerDoesNotChangeIsPrefetchIfAlreadyPrefetch() {
        mProducerContext1.setIsPrefetch(true);
        mProducerContext2.setIsPrefetch(true);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testAddConsumerDoesNotChangeIsPrefetchIfPrefetch() {
        mProducerContext1.setIsPrefetch(false);
        mProducerContext2.setIsPrefetch(true);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testNoLongerPrefetchWhenCurrentlyPrefetch() {
        mProducerContext1.setIsPrefetch(true);
        mProducerContext2.setIsPrefetch(true);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
        mProducerContext1.setIsPrefetch(false);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testNowPrefetchWhenNotPrefetchBefore() {
        mProducerContext1.setIsPrefetch(true);
        mProducerContext2.setIsPrefetch(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
        mProducerContext2.setIsPrefetch(true);
        Assert.assertTrue(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testBothNotPrefetchThenOneBecomesPrefetch() {
        mProducerContext1.setIsPrefetch(false);
        mProducerContext2.setIsPrefetch(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
        mProducerContext2.setIsPrefetch(true);
        Assert.assertFalse(mMultiplexedContext1.isPrefetch());
    }

    @Test
    public void testIsIntermediateResultExpectedTrue() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testIsIntermediateResultExpectedFalse() {
        mProducerContext1.setIsIntermediateResultExpected(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testCancelChangesIsIntermediateResultExpectedIfNeeded() {
        mProducerContext1.setIsIntermediateResultExpected(true);
        mProducerContext2.setIsIntermediateResultExpected(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
        ProducerContextCallbacks callbacks = Mockito.mock(ProducerContextCallbacks.class);
        mMultiplexedContext1.addCallbacks(callbacks);
        mProducerContext1.cancel();
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
        Mockito.verify(callbacks).onIsIntermediateResultExpectedChanged();
    }

    @Test
    public void testCancelDoesNotChangeIsIntermediateResultExpectedIfNotNeeded() {
        mProducerContext1.setIsIntermediateResultExpected(false);
        mProducerContext2.setIsIntermediateResultExpected(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
        mProducerContext2.cancel();
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testCancelDoesNotChangeIsIntermediateResultExpectedIfNotNeeded2() {
        mProducerContext1.setIsIntermediateResultExpected(true);
        mProducerContext2.setIsIntermediateResultExpected(true);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
        mProducerContext2.cancel();
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testAddConsumerChangesIsIntermediateResultExpectedIfNeeded() {
        mProducerContext1.setIsIntermediateResultExpected(false);
        mProducerContext2.setIsIntermediateResultExpected(true);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
        ProducerContextCallbacks callbacks = Mockito.mock(ProducerContextCallbacks.class);
        mMultiplexedContext1.addCallbacks(callbacks);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
        Mockito.verify(callbacks).onIsIntermediateResultExpectedChanged();
    }

    @Test
    public void testAddConsumerDoesNotChangeIsIntermediateResultExpectedIfNotNeeded() {
        mProducerContext1.setIsIntermediateResultExpected(false);
        mProducerContext2.setIsIntermediateResultExpected(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testAddConsumerDoesNotChangeIsIntermediateResultExpectedIfNotNeeded2() {
        mProducerContext1.setIsIntermediateResultExpected(true);
        mProducerContext2.setIsIntermediateResultExpected(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testPropagatesIsIntermediateResultExpectedChangeIfNeeded() {
        mProducerContext1.setIsIntermediateResultExpected(false);
        mProducerContext2.setIsIntermediateResultExpected(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
        mProducerContext1.setIsIntermediateResultExpected(true);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testPropagatesIsIntermediateResultExpectedChangeIfNeeded2() {
        mProducerContext1.setIsIntermediateResultExpected(true);
        mProducerContext2.setIsIntermediateResultExpected(false);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
        mProducerContext1.setIsIntermediateResultExpected(false);
        Assert.assertFalse(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testDoesNotPropagatesIsIntermediateResultExpectedChangeIfNotNeeded() {
        mProducerContext1.setIsIntermediateResultExpected(true);
        mProducerContext2.setIsIntermediateResultExpected(true);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
        mProducerContext2.setIsIntermediateResultExpected(false);
        Assert.assertTrue(mMultiplexedContext1.isIntermediateResultExpected());
    }

    @Test
    public void testGetPriority() {
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertEquals(MEDIUM, mMultiplexedContext1.getPriority());
    }

    @Test
    public void testAddHigherPriorityIncreasesPriority() {
        mProducerContext1.setPriority(MEDIUM);
        mProducerContext2.setPriority(HIGH);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertEquals(MEDIUM, mMultiplexedContext1.getPriority());
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
    }

    @Test
    public void testAddLowerPriorityDoesNotChangePriority() {
        mProducerContext1.setPriority(MEDIUM);
        mProducerContext2.setPriority(LOW);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        Assert.assertEquals(MEDIUM, mMultiplexedContext1.getPriority());
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertEquals(MEDIUM, mMultiplexedContext1.getPriority());
    }

    @Test
    public void testCancelHighestPriorityLowersPriority() {
        mProducerContext1.setPriority(MEDIUM);
        mProducerContext2.setPriority(HIGH);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
        mProducerContext2.cancel();
        Assert.assertEquals(MEDIUM, mMultiplexedContext1.getPriority());
    }

    @Test
    public void testCancelLowerPriorityDoesNotChangePriority() {
        mProducerContext1.setPriority(MEDIUM);
        mProducerContext2.setPriority(HIGH);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
        mProducerContext1.cancel();
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
    }

    @Test
    public void testChangeHighestPriorityLowersPriority() {
        mProducerContext1.setPriority(HIGH);
        mProducerContext2.setPriority(MEDIUM);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
        mProducerContext1.setPriority(LOW);
        Assert.assertEquals(MEDIUM, mMultiplexedContext1.getPriority());
    }

    @Test
    public void testChangeToHighestPriorityHighersPriority() {
        mProducerContext1.setPriority(LOW);
        mProducerContext2.setPriority(MEDIUM);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertEquals(MEDIUM, mMultiplexedContext1.getPriority());
        mProducerContext1.setPriority(HIGH);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
    }

    @Test
    public void testChangeToNonHighestPriorityDoesNotChangePriority() {
        mProducerContext1.setPriority(LOW);
        mProducerContext2.setPriority(HIGH);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
        mProducerContext1.setPriority(MEDIUM);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
    }

    @Test
    public void testChangeNonHighestToLowerPriorityDoesNotChangePriority() {
        mProducerContext1.setPriority(HIGH);
        mProducerContext2.setPriority(HIGH);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext1);
        mMultiplexProducer.produceResults(mConsumer1, mProducerContext2);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
        mProducerContext1.setPriority(MEDIUM);
        Assert.assertEquals(HIGH, mMultiplexedContext1.getPriority());
    }
}


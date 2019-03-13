/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import com.facebook.imagepipeline.request.ImageRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;


/**
 * Checks basic properties of NullProducer, that is that it always returns null.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class SettableProducerContextTest {
    @Mock
    public ImageRequest mImageRequest;

    private final String mRequestId = "mRequestId";

    private ProducerContextCallbacks mCallbacks1;

    private ProducerContextCallbacks mCallbacks2;

    private SettableProducerContext mSettableProducerContext;

    @Test
    public void testGetters() {
        Assert.assertEquals(mImageRequest, mSettableProducerContext.getImageRequest());
        Assert.assertEquals(mRequestId, mSettableProducerContext.getId());
    }

    @Test
    public void testIsPrefetch() {
        Assert.assertFalse(mSettableProducerContext.isPrefetch());
    }

    @Test
    public void testCancellation() {
        mSettableProducerContext.addCallbacks(mCallbacks1);
        Mockito.verify(mCallbacks1, Mockito.never()).onCancellationRequested();
        mSettableProducerContext.cancel();
        Mockito.verify(mCallbacks1).onCancellationRequested();
        Mockito.verify(mCallbacks1, Mockito.never()).onIsPrefetchChanged();
        mSettableProducerContext.addCallbacks(mCallbacks2);
        Mockito.verify(mCallbacks2).onCancellationRequested();
        Mockito.verify(mCallbacks2, Mockito.never()).onIsPrefetchChanged();
    }

    @Test
    public void testSetPrefetch() {
        mSettableProducerContext.addCallbacks(mCallbacks1);
        Assert.assertFalse(mSettableProducerContext.isPrefetch());
        mSettableProducerContext.setIsPrefetch(true);
        Assert.assertTrue(mSettableProducerContext.isPrefetch());
        Mockito.verify(mCallbacks1).onIsPrefetchChanged();
        mSettableProducerContext.setIsPrefetch(true);
        // only one callback is expected
        Mockito.verify(mCallbacks1).onIsPrefetchChanged();
    }

    @Test
    public void testSetIsIntermediateResultExpected() {
        mSettableProducerContext.addCallbacks(mCallbacks1);
        Assert.assertTrue(mSettableProducerContext.isIntermediateResultExpected());
        mSettableProducerContext.setIsIntermediateResultExpected(false);
        Assert.assertFalse(mSettableProducerContext.isIntermediateResultExpected());
        Mockito.verify(mCallbacks1).onIsIntermediateResultExpectedChanged();
        mSettableProducerContext.setIsIntermediateResultExpected(false);
        // only one callback is expected
        Mockito.verify(mCallbacks1).onIsIntermediateResultExpectedChanged();
    }

    @Test
    public void testNoCallbackCalledWhenIsPrefetchDoesNotChange() {
        Assert.assertFalse(mSettableProducerContext.isPrefetch());
        mSettableProducerContext.addCallbacks(mCallbacks1);
        mSettableProducerContext.setIsPrefetch(false);
        Mockito.verify(mCallbacks1, Mockito.never()).onIsPrefetchChanged();
    }

    @Test
    public void testCallbackCalledWhenIsPrefetchChanges() {
        Assert.assertFalse(mSettableProducerContext.isPrefetch());
        mSettableProducerContext.addCallbacks(mCallbacks1);
        mSettableProducerContext.addCallbacks(mCallbacks2);
        mSettableProducerContext.setIsPrefetch(true);
        Assert.assertTrue(mSettableProducerContext.isPrefetch());
        Mockito.verify(mCallbacks1).onIsPrefetchChanged();
        Mockito.verify(mCallbacks1, Mockito.never()).onCancellationRequested();
        Mockito.verify(mCallbacks2).onIsPrefetchChanged();
        Mockito.verify(mCallbacks2, Mockito.never()).onCancellationRequested();
    }
}


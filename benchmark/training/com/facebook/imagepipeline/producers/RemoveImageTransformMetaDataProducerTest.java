/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.NO_FLAGS;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class RemoveImageTransformMetaDataProducerTest {
    @Mock
    public Producer mInputProducer;

    @Mock
    public Consumer<CloseableReference<PooledByteBuffer>> mConsumer;

    @Mock
    public EncodedImage mEncodedImage;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public Exception mException;

    private RemoveImageTransformMetaDataProducer mRemoveMetaDataProducer;

    private Consumer<EncodedImage> mRemoveMetaDataConsumer;

    private PooledByteBuffer mIntermediateByteBuffer;

    private PooledByteBuffer mFinalByteBuffer;

    private CloseableReference<PooledByteBuffer> mIntermediateResult;

    private CloseableReference<PooledByteBuffer> mFinalResult;

    @Test
    public void testOnNewResult() {
        Mockito.when(mEncodedImage.getByteBufferRef()).thenReturn(mIntermediateResult);
        Mockito.when(mEncodedImage.isValid()).thenReturn(true);
        mRemoveMetaDataConsumer.onNewResult(mEncodedImage, NO_FLAGS);
        ArgumentCaptor<CloseableReference> argumentCaptor = ArgumentCaptor.forClass(CloseableReference.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(NO_FLAGS));
        CloseableReference intermediateResult = argumentCaptor.getValue();
        Assert.assertEquals(mIntermediateResult.getUnderlyingReferenceTestOnly().getRefCountTestOnly(), intermediateResult.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Mockito.when(mEncodedImage.getByteBufferRef()).thenReturn(mFinalResult);
        mRemoveMetaDataConsumer.onNewResult(mEncodedImage, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(NO_FLAGS));
        CloseableReference finalResult = argumentCaptor.getValue();
        Assert.assertEquals(mFinalResult.getUnderlyingReferenceTestOnly().getRefCountTestOnly(), finalResult.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
    }

    @Test
    public void testOnNullResult() {
        mRemoveMetaDataConsumer.onNewResult(null, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(null, NO_FLAGS);
    }

    @Test
    public void testOnFailure() {
        mRemoveMetaDataConsumer.onFailure(mException);
        Mockito.verify(mConsumer).onFailure(mException);
    }

    @Test
    public void testOnCancellation() {
        mRemoveMetaDataConsumer.onCancellation();
        Mockito.verify(mConsumer).onCancellation();
    }
}


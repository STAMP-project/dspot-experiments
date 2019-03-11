/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableImage;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static Config.NONE;


/**
 * Checks basic properties of swallow result producer, that is:
 *   - it swallows all results.
 *   - it notifies previous consumer of last result.
 *   - it notifies previous consumer of a failure.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class SwallowResultProducerTest {
    @Mock
    public Producer<CloseableReference<CloseableImage>> mInputProducer;

    @Mock
    public Consumer<Void> mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public Exception mException;

    private CloseableReference<CloseableImage> mFinalImageReference;

    private CloseableReference<CloseableImage> mIntermediateImageReference;

    private SwallowResultProducer<CloseableReference<CloseableImage>> mSwallowResultProducer;

    @Test
    public void testSwallowResults() {
        setupInputProducerStreamingSuccess();
        mSwallowResultProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verifyNoMoreInteractions(mConsumer);
    }

    @Test
    public void testPassOnNullResult() {
        setupInputProducerNotFound();
        mSwallowResultProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verifyNoMoreInteractions(mConsumer);
    }

    @Test
    public void testPassOnFailure() {
        setupInputProducerFailure();
        mSwallowResultProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verifyNoMoreInteractions(mConsumer);
    }

    @Test
    public void testPassOnCancellation() {
        setupInputProducerCancellation();
        mSwallowResultProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onCancellation();
        Mockito.verifyNoMoreInteractions(mConsumer);
    }

    private static class ProduceResultsNewResultAnswer implements Answer<Void> {
        private final List<CloseableReference<CloseableImage>> mResults;

        private ProduceResultsNewResultAnswer(List<CloseableReference<CloseableImage>> results) {
            mResults = results;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Consumer consumer = ((Consumer) (invocation.getArguments()[0]));
            Iterator<CloseableReference<CloseableImage>> iterator = mResults.iterator();
            while (iterator.hasNext()) {
                CloseableReference<CloseableImage> result = iterator.next();
                consumer.onNewResult(result, BaseConsumer.simpleStatusForIsLast((!(iterator.hasNext()))));
            } 
            return null;
        }
    }

    private class ProduceResultsFailureAnswer implements Answer<Void> {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Consumer consumer = ((Consumer) (invocation.getArguments()[0]));
            consumer.onFailure(mException);
            return null;
        }
    }

    private class ProduceResultsCancellationAnswer implements Answer<Void> {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Consumer consumer = ((Consumer) (invocation.getArguments()[0]));
            consumer.onCancellation();
            return null;
        }
    }
}


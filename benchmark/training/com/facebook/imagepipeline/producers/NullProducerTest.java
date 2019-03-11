/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
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
public class NullProducerTest {
    @Mock
    public Consumer mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public ProducerListener mProducerListener;

    private NullProducer mNullProducer;

    @Test
    public void testNullProducerReturnsNull() {
        mNullProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
    }
}


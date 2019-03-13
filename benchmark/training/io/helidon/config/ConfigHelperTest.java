/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config;


import Flow.Subscription;
import io.helidon.common.reactive.Flow;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.function.Function;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link ConfigHelper}.
 */
public class ConfigHelperTest {
    @Test
    public void testCreateReaderFromReader() throws IOException {
        StringReader readable = new StringReader("test-value");
        Assert.assertThat(ConfigHelper.createReader(readable), Matchers.is(readable));
    }

    @Test
    public void testCreateReaderFromCharBuffer() throws IOException {
        Assert.assertThat(ConfigHelperTest.readerToString(ConfigHelper.createReader(CharBuffer.wrap("test-value"))), Matchers.is("test-value"));
    }

    @Test
    public void testSubscriber() {
        // mocks
        Function<Long, Boolean> onNextFunction = Mockito.mock(Function.class);
        Flow.Subscription subscription = Mockito.mock(Subscription.class);
        // create Subscriber
        Flow.Subscriber<Long> subscriber = ConfigHelper.subscriber(onNextFunction);
        // onSubscribe
        subscriber.onSubscribe(subscription);
        // request(Long.MAX_VALUE) has been invoked
        Mockito.verify(subscription).request(Long.MAX_VALUE);
        // MOCK onNext
        Mockito.when(onNextFunction.apply(1L)).thenReturn(true);
        Mockito.when(onNextFunction.apply(2L)).thenReturn(true);
        Mockito.when(onNextFunction.apply(3L)).thenReturn(false);
        // 2x onNext -> true
        subscriber.onNext(1L);
        subscriber.onNext(2L);
        // function invoked 2x, cancel never
        Mockito.verify(onNextFunction, Mockito.times(2)).apply(ArgumentMatchers.any());
        Mockito.verify(subscription, Mockito.never()).cancel();
        // 1x onNext -> false
        subscriber.onNext(3L);
        // function invoked 2+1x, cancel 1x
        Mockito.verify(onNextFunction, Mockito.times((2 + 1))).apply(ArgumentMatchers.any());
        Mockito.verify(subscription, Mockito.times(1)).cancel();
    }
}


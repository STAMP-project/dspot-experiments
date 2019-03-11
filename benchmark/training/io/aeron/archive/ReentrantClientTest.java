/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.archive;


import io.aeron.driver.MediaDriver;
import io.aeron.exceptions.AeronException;
import org.agrona.ErrorHandler;
import org.agrona.collections.MutableReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static CommonContext.IPC_CHANNEL;


public class ReentrantClientTest {
    final MediaDriver mediaDriver = MediaDriver.launch(new MediaDriver.Context().dirDeleteOnStart(true));

    @Test
    public void shouldThrowWhenReentering() {
        final MutableReference<Throwable> expectedException = new MutableReference();
        final ErrorHandler errorHandler = expectedException::set;
        try (Aeron aeron = Aeron.connect(new Aeron.Context().errorHandler(errorHandler))) {
            final String channel = IPC_CHANNEL;
            final AvailableImageHandler mockHandler = Mockito.mock(AvailableImageHandler.class);
            Mockito.doAnswer(( invocation) -> aeron.addSubscription(channel, 3)).when(mockHandler).onAvailableImage(ArgumentMatchers.any(Image.class));
            final Subscription sub = aeron.addSubscription(channel, 1, mockHandler, null);
            final Publication pub = aeron.addPublication(channel, 1);
            Mockito.verify(mockHandler, Mockito.timeout(5000)).onAvailableImage(ArgumentMatchers.any(Image.class));
            pub.close();
            sub.close();
            Assert.assertThat(expectedException.get(), CoreMatchers.instanceOf(AeronException.class));
        }
    }
}


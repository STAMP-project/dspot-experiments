/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.poison.pill;


import Message.Headers.DATE;
import Message.Headers.SENDER;
import Message.POISON_PILL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Date: 12/27/15 - 10:32 PM
 *
 * @author Jeroen Meulemeester
 */
public class ProducerTest {
    @Test
    public void testSend() throws Exception {
        final MqPublishPoint publishPoint = Mockito.mock(MqPublishPoint.class);
        final Producer producer = new Producer("producer", publishPoint);
        Mockito.verifyZeroInteractions(publishPoint);
        producer.send("Hello!");
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(publishPoint).put(messageCaptor.capture());
        final Message message = messageCaptor.getValue();
        Assertions.assertNotNull(message);
        Assertions.assertEquals("producer", message.getHeader(SENDER));
        Assertions.assertNotNull(message.getHeader(DATE));
        Assertions.assertEquals("Hello!", message.getBody());
        Mockito.verifyNoMoreInteractions(publishPoint);
    }

    @Test
    public void testStop() throws Exception {
        final MqPublishPoint publishPoint = Mockito.mock(MqPublishPoint.class);
        final Producer producer = new Producer("producer", publishPoint);
        Mockito.verifyZeroInteractions(publishPoint);
        producer.stop();
        Mockito.verify(publishPoint).put(ArgumentMatchers.eq(POISON_PILL));
        try {
            producer.send("Hello!");
            Assertions.fail("Expected 'IllegalStateException' at this point, since the producer has stopped!");
        } catch (IllegalStateException e) {
            Assertions.assertNotNull(e);
            Assertions.assertNotNull(e.getMessage());
            Assertions.assertEquals("Producer Hello! was stopped and fail to deliver requested message [producer].", e.getMessage());
        }
        Mockito.verifyNoMoreInteractions(publishPoint);
    }
}


/**
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.item.amqp.builder;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.batch.item.amqp.AmqpItemReader;


/**
 *
 *
 * @author Glenn Renfro
 */
public class AmqpItemReaderBuilderTests {
    @Mock
    AmqpTemplate amqpTemplate;

    @Test
    public void testNoItemType() {
        Mockito.when(this.amqpTemplate.receiveAndConvert()).thenReturn("foo");
        final AmqpItemReader<String> amqpItemReader = new AmqpItemReaderBuilder<String>().amqpTemplate(this.amqpTemplate).build();
        Assert.assertEquals("foo", amqpItemReader.read());
    }

    @Test
    public void testNonMessageItemType() {
        Mockito.when(this.amqpTemplate.receiveAndConvert()).thenReturn("foo");
        final AmqpItemReader<String> amqpItemReader = new AmqpItemReaderBuilder<String>().amqpTemplate(this.amqpTemplate).itemType(String.class).build();
        Assert.assertEquals("foo", amqpItemReader.read());
    }

    @Test
    public void testMessageItemType() {
        final Message message = Mockito.mock(Message.class);
        Mockito.when(this.amqpTemplate.receive()).thenReturn(message);
        final AmqpItemReader<Message> amqpItemReader = new AmqpItemReaderBuilder<Message>().amqpTemplate(this.amqpTemplate).itemType(Message.class).build();
        Assert.assertEquals(message, amqpItemReader.read());
    }

    @Test
    public void testNullAmqpTemplate() {
        try {
            new AmqpItemReaderBuilder<Message>().build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "amqpTemplate is required.", iae.getMessage());
        }
    }
}


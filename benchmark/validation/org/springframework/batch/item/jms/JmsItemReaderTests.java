/**
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.jms;


import java.util.Date;
import javax.jms.Message;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;


public class JmsItemReaderTests {
    JmsItemReader<String> itemReader = new JmsItemReader();

    @Test
    public void testNoItemTypeSunnyDay() {
        JmsOperations jmsTemplate = Mockito.mock(JmsOperations.class);
        Mockito.when(jmsTemplate.receiveAndConvert()).thenReturn("foo");
        itemReader.setJmsTemplate(jmsTemplate);
        Assert.assertEquals("foo", itemReader.read());
    }

    @Test
    public void testSetItemTypeSunnyDay() {
        JmsOperations jmsTemplate = Mockito.mock(JmsOperations.class);
        Mockito.when(jmsTemplate.receiveAndConvert()).thenReturn("foo");
        itemReader.setJmsTemplate(jmsTemplate);
        itemReader.setItemType(String.class);
        Assert.assertEquals("foo", itemReader.read());
    }

    @Test
    public void testSetItemSubclassTypeSunnyDay() {
        JmsOperations jmsTemplate = Mockito.mock(JmsOperations.class);
        Date date = new java.sql.Date(0L);
        Mockito.when(jmsTemplate.receiveAndConvert()).thenReturn(date);
        JmsItemReader<Date> itemReader = new JmsItemReader();
        itemReader.setJmsTemplate(jmsTemplate);
        itemReader.setItemType(Date.class);
        Assert.assertEquals(date, itemReader.read());
    }

    @Test
    public void testSetItemTypeMismatch() {
        JmsOperations jmsTemplate = Mockito.mock(JmsOperations.class);
        Mockito.when(jmsTemplate.receiveAndConvert()).thenReturn("foo");
        JmsItemReader<Date> itemReader = new JmsItemReader();
        itemReader.setJmsTemplate(jmsTemplate);
        itemReader.setItemType(Date.class);
        try {
            itemReader.read();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
            Assert.assertTrue(((e.getMessage().indexOf("wrong type")) >= 0));
        }
    }

    @Test
    public void testNextMessageSunnyDay() {
        JmsOperations jmsTemplate = Mockito.mock(JmsOperations.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(jmsTemplate.receive()).thenReturn(message);
        JmsItemReader<Message> itemReader = new JmsItemReader();
        itemReader.setJmsTemplate(jmsTemplate);
        itemReader.setItemType(Message.class);
        Assert.assertEquals(message, itemReader.read());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTemplateWithNoDefaultDestination() throws Exception {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setReceiveTimeout(100L);
        itemReader.setJmsTemplate(jmsTemplate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTemplateWithNoTimeout() throws Exception {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestinationName("foo");
        itemReader.setJmsTemplate(jmsTemplate);
    }
}


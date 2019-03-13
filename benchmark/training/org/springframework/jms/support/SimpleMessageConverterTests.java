/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.jms.support;


import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.SimpleMessageConverter;


/**
 * Unit tests for the {@link SimpleMessageConverter} class.
 *
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 18.09.2004
 */
public class SimpleMessageConverterTests {
    @Test
    public void testStringConversion() throws JMSException {
        Session session = Mockito.mock(Session.class);
        TextMessage message = Mockito.mock(TextMessage.class);
        String content = "test";
        BDDMockito.given(session.createTextMessage(content)).willReturn(message);
        BDDMockito.given(message.getText()).willReturn(content);
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(content, session);
        Assert.assertEquals(content, converter.fromMessage(msg));
    }

    @Test
    public void testByteArrayConversion() throws JMSException {
        Session session = Mockito.mock(Session.class);
        BytesMessage message = Mockito.mock(BytesMessage.class);
        byte[] content = "test".getBytes();
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        BDDMockito.given(session.createBytesMessage()).willReturn(message);
        BDDMockito.given(message.getBodyLength()).willReturn(((long) (content.length)));
        BDDMockito.given(message.readBytes(ArgumentMatchers.any(byte[].class))).willAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return byteArrayInputStream.read(((byte[]) (invocation.getArguments()[0])));
            }
        });
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(content, session);
        Assert.assertEquals(content.length, ((byte[]) (converter.fromMessage(msg))).length);
        Mockito.verify(message).writeBytes(content);
    }

    @Test
    public void testMapConversion() throws JMSException {
        Session session = Mockito.mock(Session.class);
        MapMessage message = Mockito.mock(MapMessage.class);
        Map<String, String> content = new HashMap<>(2);
        content.put("key1", "value1");
        content.put("key2", "value2");
        BDDMockito.given(session.createMapMessage()).willReturn(message);
        BDDMockito.given(message.getMapNames()).willReturn(Collections.enumeration(content.keySet()));
        BDDMockito.given(message.getObject("key1")).willReturn("value1");
        BDDMockito.given(message.getObject("key2")).willReturn("value2");
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(content, session);
        Assert.assertEquals(content, converter.fromMessage(msg));
        Mockito.verify(message).setObject("key1", "value1");
        Mockito.verify(message).setObject("key2", "value2");
    }

    @Test
    public void testSerializableConversion() throws JMSException {
        Session session = Mockito.mock(Session.class);
        ObjectMessage message = Mockito.mock(ObjectMessage.class);
        Integer content = new Integer(5);
        BDDMockito.given(session.createObjectMessage(content)).willReturn(message);
        BDDMockito.given(message.getObject()).willReturn(content);
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(content, session);
        Assert.assertEquals(content, converter.fromMessage(msg));
    }

    @Test(expected = MessageConversionException.class)
    public void testToMessageThrowsExceptionIfGivenNullObjectToConvert() throws Exception {
        new SimpleMessageConverter().toMessage(null, null);
    }

    @Test(expected = MessageConversionException.class)
    public void testToMessageThrowsExceptionIfGivenIncompatibleObjectToConvert() throws Exception {
        new SimpleMessageConverter().toMessage(new Object(), null);
    }

    @Test
    public void testToMessageSimplyReturnsMessageAsIsIfSuppliedWithMessage() throws JMSException {
        Session session = Mockito.mock(Session.class);
        ObjectMessage message = Mockito.mock(ObjectMessage.class);
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(message, session);
        Assert.assertSame(message, msg);
    }

    @Test
    public void testFromMessageSimplyReturnsMessageAsIsIfSuppliedWithMessage() throws JMSException {
        Message message = Mockito.mock(Message.class);
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Object msg = converter.fromMessage(message);
        Assert.assertSame(message, msg);
    }

    @Test
    public void testMapConversionWhereMapHasNonStringTypesForKeys() throws JMSException {
        MapMessage message = Mockito.mock(MapMessage.class);
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createMapMessage()).willReturn(message);
        Map<Integer, String> content = new HashMap<>(1);
        content.put(1, "value1");
        SimpleMessageConverter converter = new SimpleMessageConverter();
        try {
            converter.toMessage(content, session);
            Assert.fail("expected MessageConversionException");
        } catch (MessageConversionException ex) {
            /* expected */
        }
    }

    @Test
    public void testMapConversionWhereMapHasNNullForKey() throws JMSException {
        MapMessage message = Mockito.mock(MapMessage.class);
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createMapMessage()).willReturn(message);
        Map<Object, String> content = new HashMap<>(1);
        content.put(null, "value1");
        SimpleMessageConverter converter = new SimpleMessageConverter();
        try {
            converter.toMessage(content, session);
            Assert.fail("expected MessageConversionException");
        } catch (MessageConversionException ex) {
            /* expected */
        }
    }
}


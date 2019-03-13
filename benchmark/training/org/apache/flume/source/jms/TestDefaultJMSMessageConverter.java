/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.source.jms;


import com.google.common.base.Charsets;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestDefaultJMSMessageConverter {
    private static final String TEXT = "text";

    private static final byte[] BYTES = TestDefaultJMSMessageConverter.TEXT.getBytes(Charsets.UTF_8);

    private Context context;

    private Message message;

    private Map<String, String> headers;

    private JMSMessageConverter converter;

    @Test
    public void testTextMessage() throws Exception {
        createTextMessage();
        headers.put("key1", "value1");
        headers.put("key2", "value2");
        createHeaders();
        Event event = converter.convert(message).iterator().next();
        Assert.assertEquals(headers, event.getHeaders());
        Assert.assertEquals(TestDefaultJMSMessageConverter.TEXT, new String(event.getBody(), Charsets.UTF_8));
    }

    @Test
    public void testNullTextMessage() throws Exception {
        createNullTextMessage();
        headers.put("key1", "value1");
        headers.put("key2", "value2");
        createHeaders();
        Event event = converter.convert(message).iterator().next();
        Assert.assertEquals(headers, event.getHeaders());
        // In case of a null text message, the event's body will be empty due to
        // SimpleEvent's body not updated with a valid text message.
        Assert.assertEquals(event.getBody().length, 0);
    }

    @Test
    public void testBytesMessage() throws Exception {
        createBytesMessage();
        headers.put("key1", "value1");
        headers.put("key2", "value2");
        createHeaders();
        Event event = converter.convert(message).iterator().next();
        Assert.assertEquals(headers, event.getHeaders());
        Assert.assertArrayEquals(TestDefaultJMSMessageConverter.BYTES, event.getBody());
    }

    @Test(expected = JMSException.class)
    public void testBytesMessageTooLarge() throws Exception {
        createBytesMessage();
        Mockito.when(getBodyLength()).thenReturn(Long.MAX_VALUE);
        createHeaders();
        converter.convert(message);
    }

    @Test(expected = JMSException.class)
    public void testBytesMessagePartialReturn() throws Exception {
        createBytesMessage();
        Mockito.when(readBytes(ArgumentMatchers.any(byte[].class))).thenReturn(((TestDefaultJMSMessageConverter.BYTES.length) + 1));
        createHeaders();
        converter.convert(message);
    }

    @Test
    public void testObjectMessage() throws Exception {
        createObjectMessage();
        headers.put("key1", "value1");
        headers.put("key2", "value2");
        createHeaders();
        Event event = converter.convert(message).iterator().next();
        Assert.assertEquals(headers, event.getHeaders());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(TestDefaultJMSMessageConverter.TEXT);
        Assert.assertArrayEquals(bos.toByteArray(), event.getBody());
    }

    @Test
    public void testNoHeaders() throws Exception {
        createTextMessage();
        createHeaders();
        Event event = converter.convert(message).iterator().next();
        Assert.assertEquals(Collections.EMPTY_MAP, event.getHeaders());
        Assert.assertEquals(TestDefaultJMSMessageConverter.TEXT, new String(event.getBody(), Charsets.UTF_8));
    }
}


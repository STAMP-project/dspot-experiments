/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.jms.support.converter;


import MessageType.TEXT;
import javax.jms.BytesMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class MarshallingMessageConverterTests {
    private MarshallingMessageConverter converter;

    private Marshaller marshallerMock;

    private Unmarshaller unmarshallerMock;

    private Session sessionMock;

    @Test
    public void toBytesMessage() throws Exception {
        BytesMessage bytesMessageMock = Mockito.mock(BytesMessage.class);
        Object toBeMarshalled = new Object();
        BDDMockito.given(sessionMock.createBytesMessage()).willReturn(bytesMessageMock);
        converter.toMessage(toBeMarshalled, sessionMock);
        Mockito.verify(marshallerMock).marshal(ArgumentMatchers.eq(toBeMarshalled), ArgumentMatchers.isA(Result.class));
        Mockito.verify(bytesMessageMock).writeBytes(ArgumentMatchers.isA(byte[].class));
    }

    @Test
    public void fromBytesMessage() throws Exception {
        BytesMessage bytesMessageMock = Mockito.mock(BytesMessage.class);
        Object unmarshalled = new Object();
        BDDMockito.given(bytesMessageMock.getBodyLength()).willReturn(10L);
        BDDMockito.given(bytesMessageMock.readBytes(ArgumentMatchers.isA(byte[].class))).willReturn(0);
        BDDMockito.given(unmarshallerMock.unmarshal(ArgumentMatchers.isA(Source.class))).willReturn(unmarshalled);
        Object result = converter.fromMessage(bytesMessageMock);
        Assert.assertEquals("Invalid result", result, unmarshalled);
    }

    @Test
    public void toTextMessage() throws Exception {
        converter.setTargetType(TEXT);
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        Object toBeMarshalled = new Object();
        BDDMockito.given(sessionMock.createTextMessage(ArgumentMatchers.isA(String.class))).willReturn(textMessageMock);
        converter.toMessage(toBeMarshalled, sessionMock);
        Mockito.verify(marshallerMock).marshal(ArgumentMatchers.eq(toBeMarshalled), ArgumentMatchers.isA(Result.class));
    }

    @Test
    public void fromTextMessage() throws Exception {
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        Object unmarshalled = new Object();
        String text = "foo";
        BDDMockito.given(textMessageMock.getText()).willReturn(text);
        BDDMockito.given(unmarshallerMock.unmarshal(ArgumentMatchers.isA(Source.class))).willReturn(unmarshalled);
        Object result = converter.fromMessage(textMessageMock);
        Assert.assertEquals("Invalid result", result, unmarshalled);
    }
}


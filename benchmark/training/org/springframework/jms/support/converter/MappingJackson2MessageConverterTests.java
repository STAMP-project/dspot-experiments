/**
 * Copyright 2002-2019 the original author or authors.
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
import com.fasterxml.jackson.annotation.JsonView;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.MethodParameter;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class MappingJackson2MessageConverterTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private MappingJackson2MessageConverter converter;

    private Session sessionMock;

    @Test
    public void toBytesMessage() throws Exception {
        BytesMessage bytesMessageMock = Mockito.mock(BytesMessage.class);
        Date toBeMarshalled = new Date();
        BDDMockito.given(sessionMock.createBytesMessage()).willReturn(bytesMessageMock);
        converter.toMessage(toBeMarshalled, sessionMock);
        Mockito.verify(bytesMessageMock).setStringProperty("__encoding__", "UTF-8");
        Mockito.verify(bytesMessageMock).setStringProperty("__typeid__", Date.class.getName());
        Mockito.verify(bytesMessageMock).writeBytes(ArgumentMatchers.isA(byte[].class));
    }

    @Test
    public void fromBytesMessage() throws Exception {
        BytesMessage bytesMessageMock = Mockito.mock(BytesMessage.class);
        Map<String, String> unmarshalled = Collections.singletonMap("foo", "bar");
        byte[] bytes = "{\"foo\":\"bar\"}".getBytes();
        final ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        BDDMockito.given(bytesMessageMock.getStringProperty("__typeid__")).willReturn(Object.class.getName());
        BDDMockito.given(bytesMessageMock.propertyExists("__encoding__")).willReturn(false);
        BDDMockito.given(bytesMessageMock.getBodyLength()).willReturn(new Long(bytes.length));
        BDDMockito.given(bytesMessageMock.readBytes(ArgumentMatchers.any(byte[].class))).willAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return byteStream.read(((byte[]) (invocation.getArguments()[0])));
            }
        });
        Object result = converter.fromMessage(bytesMessageMock);
        Assert.assertEquals("Invalid result", result, unmarshalled);
    }

    @Test
    public void toTextMessageWithObject() throws Exception {
        converter.setTargetType(TEXT);
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        Date toBeMarshalled = new Date();
        BDDMockito.given(sessionMock.createTextMessage(ArgumentMatchers.isA(String.class))).willReturn(textMessageMock);
        converter.toMessage(toBeMarshalled, sessionMock);
        Mockito.verify(textMessageMock).setStringProperty("__typeid__", Date.class.getName());
    }

    @Test
    public void toTextMessageWithMap() throws Exception {
        converter.setTargetType(TEXT);
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        Map<String, String> toBeMarshalled = new HashMap<>();
        toBeMarshalled.put("foo", "bar");
        BDDMockito.given(sessionMock.createTextMessage(ArgumentMatchers.isA(String.class))).willReturn(textMessageMock);
        converter.toMessage(toBeMarshalled, sessionMock);
        Mockito.verify(textMessageMock).setStringProperty("__typeid__", HashMap.class.getName());
    }

    @Test
    public void fromTextMessage() throws Exception {
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        MappingJackson2MessageConverterTests.MyBean unmarshalled = new MappingJackson2MessageConverterTests.MyBean("bar");
        String text = "{\"foo\":\"bar\"}";
        BDDMockito.given(textMessageMock.getStringProperty("__typeid__")).willReturn(MappingJackson2MessageConverterTests.MyBean.class.getName());
        BDDMockito.given(textMessageMock.getText()).willReturn(text);
        MappingJackson2MessageConverterTests.MyBean result = ((MappingJackson2MessageConverterTests.MyBean) (converter.fromMessage(textMessageMock)));
        Assert.assertEquals("Invalid result", result, unmarshalled);
    }

    @Test
    public void fromTextMessageWithUnknownProperty() throws Exception {
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        MappingJackson2MessageConverterTests.MyBean unmarshalled = new MappingJackson2MessageConverterTests.MyBean("bar");
        String text = "{\"foo\":\"bar\", \"unknownProperty\":\"value\"}";
        BDDMockito.given(textMessageMock.getStringProperty("__typeid__")).willReturn(MappingJackson2MessageConverterTests.MyBean.class.getName());
        BDDMockito.given(textMessageMock.getText()).willReturn(text);
        MappingJackson2MessageConverterTests.MyBean result = ((MappingJackson2MessageConverterTests.MyBean) (converter.fromMessage(textMessageMock)));
        Assert.assertEquals("Invalid result", result, unmarshalled);
    }

    @Test
    public void fromTextMessageAsObject() throws Exception {
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        Map<String, String> unmarshalled = Collections.singletonMap("foo", "bar");
        String text = "{\"foo\":\"bar\"}";
        BDDMockito.given(textMessageMock.getStringProperty("__typeid__")).willReturn(Object.class.getName());
        BDDMockito.given(textMessageMock.getText()).willReturn(text);
        Object result = converter.fromMessage(textMessageMock);
        Assert.assertEquals("Invalid result", result, unmarshalled);
    }

    @Test
    public void fromTextMessageAsMap() throws Exception {
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        Map<String, String> unmarshalled = Collections.singletonMap("foo", "bar");
        String text = "{\"foo\":\"bar\"}";
        BDDMockito.given(textMessageMock.getStringProperty("__typeid__")).willReturn(HashMap.class.getName());
        BDDMockito.given(textMessageMock.getText()).willReturn(text);
        Object result = converter.fromMessage(textMessageMock);
        Assert.assertEquals("Invalid result", result, unmarshalled);
    }

    @Test
    public void toTextMessageWithReturnType() throws NoSuchMethodException, JMSException {
        Method method = this.getClass().getDeclaredMethod("summary");
        MethodParameter returnType = new MethodParameter(method, (-1));
        testToTextMessageWithReturnType(returnType);
        Mockito.verify(sessionMock).createTextMessage("{\"name\":\"test\"}");
    }

    @Test
    public void toTextMessageWithNullReturnType() throws NoSuchMethodException, JMSException {
        testToTextMessageWithReturnType(null);
        Mockito.verify(sessionMock).createTextMessage("{\"name\":\"test\",\"description\":\"lengthy description\"}");
    }

    @Test
    public void toTextMessageWithReturnTypeAndNoJsonView() throws NoSuchMethodException, JMSException {
        Method method = this.getClass().getDeclaredMethod("none");
        MethodParameter returnType = new MethodParameter(method, (-1));
        testToTextMessageWithReturnType(returnType);
        Mockito.verify(sessionMock).createTextMessage("{\"name\":\"test\",\"description\":\"lengthy description\"}");
    }

    @Test
    public void toTextMessageWithReturnTypeAndMultipleJsonViews() throws NoSuchMethodException, JMSException {
        Method method = this.getClass().getDeclaredMethod("invalid");
        MethodParameter returnType = new MethodParameter(method, (-1));
        thrown.expect(IllegalArgumentException.class);
        testToTextMessageWithReturnType(returnType);
    }

    @Test
    public void toTextMessageWithJsonViewClass() throws JMSException {
        converter.setTargetType(TEXT);
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        MappingJackson2MessageConverterTests.MyAnotherBean bean = new MappingJackson2MessageConverterTests.MyAnotherBean("test", "lengthy description");
        BDDMockito.given(sessionMock.createTextMessage(ArgumentMatchers.isA(String.class))).willReturn(textMessageMock);
        converter.toMessage(bean, sessionMock, MappingJackson2MessageConverterTests.Summary.class);
        Mockito.verify(textMessageMock).setStringProperty("__typeid__", MappingJackson2MessageConverterTests.MyAnotherBean.class.getName());
        Mockito.verify(sessionMock).createTextMessage("{\"name\":\"test\"}");
    }

    @Test
    public void toTextMessageWithAnotherJsonViewClass() throws JMSException {
        converter.setTargetType(TEXT);
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        MappingJackson2MessageConverterTests.MyAnotherBean bean = new MappingJackson2MessageConverterTests.MyAnotherBean("test", "lengthy description");
        BDDMockito.given(sessionMock.createTextMessage(ArgumentMatchers.isA(String.class))).willReturn(textMessageMock);
        converter.toMessage(bean, sessionMock, MappingJackson2MessageConverterTests.Full.class);
        Mockito.verify(textMessageMock).setStringProperty("__typeid__", MappingJackson2MessageConverterTests.MyAnotherBean.class.getName());
        Mockito.verify(sessionMock).createTextMessage("{\"name\":\"test\",\"description\":\"lengthy description\"}");
    }

    public static class MyBean {
        private String foo;

        public MyBean() {
        }

        public MyBean(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MappingJackson2MessageConverterTests.MyBean bean = ((MappingJackson2MessageConverterTests.MyBean) (o));
            if ((foo) != null ? !(foo.equals(bean.foo)) : (bean.foo) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return (foo) != null ? foo.hashCode() : 0;
        }
    }

    private interface Summary {}

    private interface Full extends MappingJackson2MessageConverterTests.Summary {}

    private static class MyAnotherBean {
        @JsonView(MappingJackson2MessageConverterTests.Summary.class)
        private String name;

        @JsonView(MappingJackson2MessageConverterTests.Full.class)
        private String description;

        private MyAnotherBean() {
        }

        public MyAnotherBean(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}


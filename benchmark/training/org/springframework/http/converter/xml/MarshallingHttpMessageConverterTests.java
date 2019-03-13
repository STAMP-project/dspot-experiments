/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.http.converter.xml;


import MediaType.TEXT_PLAIN;
import MediaType.TEXT_XML;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;


/**
 * Tests for {@link MarshallingHttpMessageConverter}.
 *
 * @author Arjen Poutsma
 */
public class MarshallingHttpMessageConverterTests {
    @Test
    public void canRead() {
        Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        BDDMockito.given(unmarshaller.supports(Integer.class)).willReturn(false);
        BDDMockito.given(unmarshaller.supports(String.class)).willReturn(true);
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter();
        converter.setUnmarshaller(unmarshaller);
        Assert.assertFalse(converter.canRead(Boolean.class, TEXT_PLAIN));
        Assert.assertFalse(converter.canRead(Integer.class, TEXT_XML));
        Assert.assertTrue(converter.canRead(String.class, TEXT_XML));
    }

    @Test
    public void canWrite() {
        Marshaller marshaller = Mockito.mock(Marshaller.class);
        BDDMockito.given(marshaller.supports(Integer.class)).willReturn(false);
        BDDMockito.given(marshaller.supports(String.class)).willReturn(true);
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter();
        converter.setMarshaller(marshaller);
        Assert.assertFalse(converter.canWrite(Boolean.class, TEXT_PLAIN));
        Assert.assertFalse(converter.canWrite(Integer.class, TEXT_XML));
        Assert.assertTrue(converter.canWrite(String.class, TEXT_XML));
    }

    @Test
    public void read() throws Exception {
        String body = "<root>Hello World</root>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        BDDMockito.given(unmarshaller.unmarshal(ArgumentMatchers.isA(StreamSource.class))).willReturn(body);
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter();
        converter.setUnmarshaller(unmarshaller);
        String result = ((String) (converter.read(Object.class, inputMessage)));
        Assert.assertEquals("Invalid result", body, result);
    }

    @Test
    public void readWithTypeMismatchException() throws Exception {
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(new byte[0]);
        Marshaller marshaller = Mockito.mock(Marshaller.class);
        Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        BDDMockito.given(unmarshaller.unmarshal(ArgumentMatchers.isA(StreamSource.class))).willReturn(Integer.valueOf(3));
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller, unmarshaller);
        try {
            converter.read(String.class, inputMessage);
            Assert.fail("Should have thrown HttpMessageNotReadableException");
        } catch (HttpMessageNotReadableException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof TypeMismatchException));
        }
    }

    @Test
    public void readWithMarshallingFailureException() throws Exception {
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(new byte[0]);
        UnmarshallingFailureException ex = new UnmarshallingFailureException("forced");
        Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        BDDMockito.given(unmarshaller.unmarshal(ArgumentMatchers.isA(StreamSource.class))).willThrow(ex);
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter();
        converter.setUnmarshaller(unmarshaller);
        try {
            converter.read(Object.class, inputMessage);
            Assert.fail("HttpMessageNotReadableException should be thrown");
        } catch (HttpMessageNotReadableException e) {
            Assert.assertTrue("Invalid exception hierarchy", ((e.getCause()) == ex));
        }
    }

    @Test
    public void write() throws Exception {
        String body = "<root>Hello World</root>";
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        Marshaller marshaller = Mockito.mock(Marshaller.class);
        BDDMockito.willDoNothing().given(marshaller).marshal(ArgumentMatchers.eq(body), ArgumentMatchers.isA(Result.class));
        MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller);
        converter.write(body, null, outputMessage);
        Assert.assertEquals("Invalid content-type", new MediaType("application", "xml"), outputMessage.getHeaders().getContentType());
    }

    @Test
    public void writeWithMarshallingFailureException() throws Exception {
        String body = "<root>Hello World</root>";
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MarshallingFailureException ex = new MarshallingFailureException("forced");
        Marshaller marshaller = Mockito.mock(Marshaller.class);
        BDDMockito.willThrow(ex).given(marshaller).marshal(ArgumentMatchers.eq(body), ArgumentMatchers.isA(Result.class));
        try {
            MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(marshaller);
            converter.write(body, null, outputMessage);
            Assert.fail("HttpMessageNotWritableException should be thrown");
        } catch (HttpMessageNotWritableException e) {
            Assert.assertTrue("Invalid exception hierarchy", ((e.getCause()) == ex));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void supports() throws Exception {
        new MarshallingHttpMessageConverter().supports(Object.class);
    }
}


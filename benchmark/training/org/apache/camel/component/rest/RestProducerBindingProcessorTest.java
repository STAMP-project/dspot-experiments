/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.rest;


import java.io.InputStream;
import java.io.OutputStream;
import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.DataFormat;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RestProducerBindingProcessorTest {
    public static class RequestPojo {}

    public static class ResponsePojo {}

    final AsyncCallback callback = Mockito.mock(AsyncCallback.class);

    final CamelContext context = new DefaultCamelContext();

    final DataFormat jsonDataFormat = Mockito.mock(DataFormat.class);

    final DataFormat outJsonDataFormat = Mockito.mock(DataFormat.class);

    final DataFormat outXmlDataFormat = Mockito.mock(DataFormat.class);

    final AsyncProcessor processor = Mockito.mock(AsyncProcessor.class);

    final DataFormat xmlDataFormat = Mockito.mock(DataFormat.class);

    @Test
    public void shouldMarshalAndUnmarshalJson() throws Exception {
        final String outType = RestProducerBindingProcessorTest.ResponsePojo.class.getName();
        final RestProducerBindingProcessor bindingProcessor = new RestProducerBindingProcessor(processor, context, jsonDataFormat, xmlDataFormat, outJsonDataFormat, outXmlDataFormat, "json", true, outType);
        final Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        final Message input = new org.apache.camel.support.DefaultMessage(context);
        final RestProducerBindingProcessorTest.RequestPojo request = new RestProducerBindingProcessorTest.RequestPojo();
        input.setBody(request);
        exchange.setIn(input);
        final RestProducerBindingProcessorTest.ResponsePojo response = new RestProducerBindingProcessorTest.ResponsePojo();
        Mockito.when(outJsonDataFormat.unmarshal(ArgumentMatchers.same(exchange), ArgumentMatchers.any(InputStream.class))).thenReturn(response);
        final ArgumentCaptor<AsyncCallback> bindingCallback = ArgumentCaptor.forClass(AsyncCallback.class);
        Mockito.when(processor.process(ArgumentMatchers.same(exchange), bindingCallback.capture())).thenReturn(false);
        bindingProcessor.process(exchange, callback);
        Mockito.verify(jsonDataFormat).marshal(ArgumentMatchers.same(exchange), ArgumentMatchers.same(request), ArgumentMatchers.any(OutputStream.class));
        Assert.assertNotNull(bindingCallback.getValue());
        final AsyncCallback that = bindingCallback.getValue();
        that.done(false);
        Assert.assertSame(response, exchange.getOut().getBody());
    }

    @Test
    public void shouldMarshalAndUnmarshalXml() throws Exception {
        final String outType = RestProducerBindingProcessorTest.ResponsePojo.class.getName();
        final RestProducerBindingProcessor bindingProcessor = new RestProducerBindingProcessor(processor, context, jsonDataFormat, xmlDataFormat, outJsonDataFormat, outXmlDataFormat, "xml", true, outType);
        final Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        final Message input = new org.apache.camel.support.DefaultMessage(context);
        final RestProducerBindingProcessorTest.RequestPojo request = new RestProducerBindingProcessorTest.RequestPojo();
        input.setBody(request);
        exchange.setIn(input);
        final RestProducerBindingProcessorTest.ResponsePojo response = new RestProducerBindingProcessorTest.ResponsePojo();
        Mockito.when(outXmlDataFormat.unmarshal(ArgumentMatchers.same(exchange), ArgumentMatchers.any(InputStream.class))).thenReturn(response);
        final ArgumentCaptor<AsyncCallback> bindingCallback = ArgumentCaptor.forClass(AsyncCallback.class);
        Mockito.when(processor.process(ArgumentMatchers.same(exchange), bindingCallback.capture())).thenReturn(false);
        bindingProcessor.process(exchange, callback);
        Mockito.verify(xmlDataFormat).marshal(ArgumentMatchers.same(exchange), ArgumentMatchers.same(request), ArgumentMatchers.any(OutputStream.class));
        Assert.assertNotNull(bindingCallback.getValue());
        final AsyncCallback that = bindingCallback.getValue();
        that.done(false);
        Assert.assertSame(response, exchange.getOut().getBody());
    }

    @Test
    public void shouldNotMarshalAndUnmarshalByDefault() throws Exception {
        final String outType = RestProducerBindingProcessorTest.ResponsePojo.class.getName();
        final RestProducerBindingProcessor bindingProcessor = new RestProducerBindingProcessor(processor, context, jsonDataFormat, xmlDataFormat, outJsonDataFormat, outXmlDataFormat, "off", true, outType);
        final Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        final Message input = new org.apache.camel.support.DefaultMessage(context);
        final RestProducerBindingProcessorTest.RequestPojo request = new RestProducerBindingProcessorTest.RequestPojo();
        input.setBody(request);
        exchange.setIn(input);
        final ArgumentCaptor<AsyncCallback> bindingCallback = ArgumentCaptor.forClass(AsyncCallback.class);
        Mockito.when(processor.process(ArgumentMatchers.same(exchange), bindingCallback.capture())).thenReturn(false);
        bindingProcessor.process(exchange, callback);
        Assert.assertNotNull(bindingCallback.getValue());
        final AsyncCallback that = bindingCallback.getValue();
        that.done(false);
    }
}


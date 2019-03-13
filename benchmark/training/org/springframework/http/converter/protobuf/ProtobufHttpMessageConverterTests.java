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
package org.springframework.http.converter.protobuf;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN;
import ProtobufHttpMessageConverter.PROTOBUF;
import ProtobufHttpMessageConverter.X_PROTOBUF_MESSAGE_HEADER;
import ProtobufHttpMessageConverter.X_PROTOBUF_SCHEMA_HEADER;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.protobuf.Msg;

import static ProtobufHttpMessageConverter.PROTOBUF;


/**
 * Test suite for {@link ProtobufHttpMessageConverter}.
 *
 * @author Alex Antonov
 * @author Juergen Hoeller
 * @author Andreas Ahlenstorf
 * @author Sebastien Deleuze
 */
@SuppressWarnings("deprecation")
public class ProtobufHttpMessageConverterTests {
    private ProtobufHttpMessageConverter converter;

    private ExtensionRegistry extensionRegistry;

    private ExtensionRegistryInitializer registryInitializer;

    private Msg testMsg;

    @Test
    public void extensionRegistryInitialized() {
        Mockito.verify(this.registryInitializer, Mockito.times(1)).initializeExtensionRegistry(ArgumentMatchers.any());
    }

    @Test
    public void extensionRegistryInitializerNull() {
        ProtobufHttpMessageConverter converter = new ProtobufHttpMessageConverter(((ExtensionRegistryInitializer) (null)));
        Assert.assertNotNull(converter.extensionRegistry);
    }

    @Test
    public void extensionRegistryNull() {
        ProtobufHttpMessageConverter converter = new ProtobufHttpMessageConverter(((ExtensionRegistry) (null)));
        Assert.assertNotNull(converter.extensionRegistry);
    }

    @Test
    public void canRead() {
        Assert.assertTrue(this.converter.canRead(Msg.class, null));
        Assert.assertTrue(this.converter.canRead(Msg.class, PROTOBUF));
        Assert.assertTrue(this.converter.canRead(Msg.class, APPLICATION_JSON));
        Assert.assertTrue(this.converter.canRead(Msg.class, APPLICATION_XML));
        Assert.assertTrue(this.converter.canRead(Msg.class, TEXT_PLAIN));
        // only supported as an output format
        Assert.assertFalse(this.converter.canRead(Msg.class, TEXT_HTML));
    }

    @Test
    public void canWrite() {
        Assert.assertTrue(this.converter.canWrite(Msg.class, null));
        Assert.assertTrue(this.converter.canWrite(Msg.class, PROTOBUF));
        Assert.assertTrue(this.converter.canWrite(Msg.class, APPLICATION_JSON));
        Assert.assertTrue(this.converter.canWrite(Msg.class, APPLICATION_XML));
        Assert.assertTrue(this.converter.canWrite(Msg.class, TEXT_PLAIN));
        Assert.assertTrue(this.converter.canWrite(Msg.class, TEXT_HTML));
    }

    @Test
    public void read() throws IOException {
        byte[] body = toByteArray();
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body);
        inputMessage.getHeaders().setContentType(PROTOBUF);
        Message result = this.converter.read(Msg.class, inputMessage);
        Assert.assertEquals(this.testMsg, result);
    }

    @Test
    public void readNoContentType() throws IOException {
        byte[] body = toByteArray();
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body);
        Message result = this.converter.read(Msg.class, inputMessage);
        Assert.assertEquals(this.testMsg, result);
    }

    @Test
    public void writeProtobuf() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MediaType contentType = PROTOBUF;
        this.converter.write(this.testMsg, contentType, outputMessage);
        Assert.assertEquals(contentType, outputMessage.getHeaders().getContentType());
        Assert.assertTrue(((outputMessage.getBodyAsBytes().length) > 0));
        Message result = Msg.parseFrom(outputMessage.getBodyAsBytes());
        Assert.assertEquals(this.testMsg, result);
        String messageHeader = outputMessage.getHeaders().getFirst(X_PROTOBUF_MESSAGE_HEADER);
        Assert.assertEquals("Msg", messageHeader);
        String schemaHeader = outputMessage.getHeaders().getFirst(X_PROTOBUF_SCHEMA_HEADER);
        Assert.assertEquals("sample.proto", schemaHeader);
    }

    @Test
    public void writeJsonWithGoogleProtobuf() throws IOException {
        this.converter = new ProtobufHttpMessageConverter(new ProtobufHttpMessageConverter.ProtobufJavaUtilSupport(null, null), this.extensionRegistry);
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MediaType contentType = MediaType.APPLICATION_JSON_UTF8;
        this.converter.write(this.testMsg, contentType, outputMessage);
        Assert.assertEquals(contentType, outputMessage.getHeaders().getContentType());
        final String body = outputMessage.getBodyAsString(Charset.forName("UTF-8"));
        Assert.assertFalse("body is empty", body.isEmpty());
        Msg.Builder builder = Msg.newBuilder();
        JsonFormat.parser().merge(body, builder);
        Assert.assertEquals(this.testMsg, builder.build());
        Assert.assertNull(outputMessage.getHeaders().getFirst(X_PROTOBUF_MESSAGE_HEADER));
        Assert.assertNull(outputMessage.getHeaders().getFirst(X_PROTOBUF_SCHEMA_HEADER));
    }

    @Test
    public void writeJsonWithJavaFormat() throws IOException {
        this.converter = new ProtobufHttpMessageConverter(new ProtobufHttpMessageConverter.ProtobufJavaFormatSupport(), this.extensionRegistry);
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MediaType contentType = MediaType.APPLICATION_JSON_UTF8;
        this.converter.write(this.testMsg, contentType, outputMessage);
        Assert.assertEquals(contentType, outputMessage.getHeaders().getContentType());
        final String body = outputMessage.getBodyAsString(Charset.forName("UTF-8"));
        Assert.assertFalse("body is empty", body.isEmpty());
        Msg.Builder builder = Msg.newBuilder();
        JsonFormat.parser().merge(body, builder);
        Assert.assertEquals(this.testMsg, builder.build());
        Assert.assertNull(outputMessage.getHeaders().getFirst(X_PROTOBUF_MESSAGE_HEADER));
        Assert.assertNull(outputMessage.getHeaders().getFirst(X_PROTOBUF_SCHEMA_HEADER));
    }

    @Test
    public void defaultContentType() throws Exception {
        Assert.assertEquals(PROTOBUF, this.converter.getDefaultContentType(this.testMsg));
    }

    @Test
    public void getContentLength() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MediaType contentType = PROTOBUF;
        this.converter.write(this.testMsg, contentType, outputMessage);
        Assert.assertEquals((-1), outputMessage.getHeaders().getContentLength());
    }
}


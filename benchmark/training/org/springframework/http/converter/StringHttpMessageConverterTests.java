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
package org.springframework.http.converter;


import MediaType.ALL;
import MediaType.TEXT_PLAIN;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class StringHttpMessageConverterTests {
    public static final MediaType TEXT_PLAIN_UTF_8 = new MediaType("text", "plain", StandardCharsets.UTF_8);

    private StringHttpMessageConverter converter;

    private MockHttpOutputMessage outputMessage;

    @Test
    public void canRead() {
        Assert.assertTrue(this.converter.canRead(String.class, TEXT_PLAIN));
    }

    @Test
    public void canWrite() {
        Assert.assertTrue(this.converter.canWrite(String.class, TEXT_PLAIN));
        Assert.assertTrue(this.converter.canWrite(String.class, ALL));
    }

    @Test
    public void read() throws IOException {
        String body = "Hello World";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes(StandardCharsets.UTF_8));
        inputMessage.getHeaders().setContentType(StringHttpMessageConverterTests.TEXT_PLAIN_UTF_8);
        String result = this.converter.read(String.class, inputMessage);
        Assert.assertEquals("Invalid result", body, result);
    }

    @Test
    public void writeDefaultCharset() throws IOException {
        String body = "H\u00e9llo W\u00f6rld";
        this.converter.write(body, null, this.outputMessage);
        org.springframework.http.HttpHeaders headers = this.outputMessage.getHeaders();
        Assert.assertEquals(body, this.outputMessage.getBodyAsString(StandardCharsets.ISO_8859_1));
        Assert.assertEquals(new MediaType("text", "plain", StandardCharsets.ISO_8859_1), headers.getContentType());
        Assert.assertEquals(body.getBytes(StandardCharsets.ISO_8859_1).length, headers.getContentLength());
        Assert.assertFalse(headers.getAcceptCharset().isEmpty());
    }

    @Test
    public void writeUTF8() throws IOException {
        String body = "H\u00e9llo W\u00f6rld";
        this.converter.write(body, StringHttpMessageConverterTests.TEXT_PLAIN_UTF_8, this.outputMessage);
        org.springframework.http.HttpHeaders headers = this.outputMessage.getHeaders();
        Assert.assertEquals(body, this.outputMessage.getBodyAsString(StandardCharsets.UTF_8));
        Assert.assertEquals(StringHttpMessageConverterTests.TEXT_PLAIN_UTF_8, headers.getContentType());
        Assert.assertEquals(body.getBytes(StandardCharsets.UTF_8).length, headers.getContentLength());
        Assert.assertFalse(headers.getAcceptCharset().isEmpty());
    }

    // SPR-8867
    @Test
    public void writeOverrideRequestedContentType() throws IOException {
        String body = "H\u00e9llo W\u00f6rld";
        MediaType requestedContentType = new MediaType("text", "html");
        org.springframework.http.HttpHeaders headers = this.outputMessage.getHeaders();
        headers.setContentType(StringHttpMessageConverterTests.TEXT_PLAIN_UTF_8);
        this.converter.write(body, requestedContentType, this.outputMessage);
        Assert.assertEquals(body, this.outputMessage.getBodyAsString(StandardCharsets.UTF_8));
        Assert.assertEquals(StringHttpMessageConverterTests.TEXT_PLAIN_UTF_8, headers.getContentType());
        Assert.assertEquals(body.getBytes(StandardCharsets.UTF_8).length, headers.getContentLength());
        Assert.assertFalse(headers.getAcceptCharset().isEmpty());
    }
}


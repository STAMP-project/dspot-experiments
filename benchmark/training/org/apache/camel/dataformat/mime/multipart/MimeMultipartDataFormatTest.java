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
package org.apache.camel.dataformat.mime.multipart;


import Exchange.CONTENT_ENCODING;
import Exchange.CONTENT_TYPE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import org.apache.camel.Attachment;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.IOHelper;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;


public class MimeMultipartDataFormatTest extends CamelTestSupport {
    private Exchange exchange;

    private Message in;

    @Test
    public void roundtripWithTextAttachments() throws IOException {
        String attContentType = "text/plain";
        String attText = "Attachment Text";
        String attFileName = "Attachment File Name";
        in.setBody("Body text");
        in.setHeader(CONTENT_TYPE, "text/plain;charset=iso8859-1;other-parameter=true");
        in.setHeader(CONTENT_ENCODING, "UTF8");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Description", "Sample Attachment Data");
        headers.put("X-AdditionalData", "additional data");
        addAttachment(attContentType, attText, attFileName, headers);
        Exchange result = template.send("direct:roundtrip", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertThat(out.getHeader(CONTENT_TYPE, String.class), StringStartsWith.startsWith("text/plain"));
        assertEquals("UTF8", out.getHeader(CONTENT_ENCODING));
        assertTrue(out.hasAttachments());
        assertEquals(1, out.getAttachmentNames().size());
        assertThat(out.getAttachmentNames(), IsCollectionContaining.hasItem(attFileName));
        Attachment att = out.getAttachmentObject(attFileName);
        DataHandler dh = att.getDataHandler();
        assertNotNull(dh);
        assertEquals(attContentType, dh.getContentType());
        InputStream is = dh.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOHelper.copyAndCloseInput(is, os);
        assertEquals(attText, new String(os.toByteArray()));
        assertEquals("Sample Attachment Data", att.getHeader("content-description"));
        assertEquals("additional data", att.getHeader("X-AdditionalData"));
    }

    @Test
    public void roundtripWithTextAttachmentsHeadersInline() throws IOException {
        String attContentType = "text/plain";
        String attText = "Attachment Text";
        String attFileName = "Attachment File Name";
        in.setBody("Body text");
        in.setHeader(CONTENT_TYPE, "text/plain;charset=iso8859-1;other-parameter=true");
        in.setHeader(CONTENT_ENCODING, "UTF8");
        addAttachment(attContentType, attText, attFileName);
        Exchange result = template.send("direct:roundtripinlineheaders", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertThat(out.getHeader(CONTENT_TYPE, String.class), StringStartsWith.startsWith("text/plain"));
        assertEquals("UTF8", out.getHeader(CONTENT_ENCODING));
        assertTrue(out.hasAttachments());
        assertEquals(1, out.getAttachmentNames().size());
        assertThat(out.getAttachmentNames(), IsCollectionContaining.hasItem(attFileName));
        DataHandler dh = out.getAttachment(attFileName);
        assertNotNull(dh);
        assertEquals(attContentType, dh.getContentType());
        InputStream is = dh.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOHelper.copyAndCloseInput(is, os);
        assertEquals(attText, new String(os.toByteArray()));
    }

    @Test
    public void roundtripWithTextAttachmentsAndBinaryContent() throws IOException {
        String attContentType = "text/plain";
        String attText = "Attachment Text";
        String attFileName = "Attachment File Name";
        in.setBody("Body text");
        in.setHeader(CONTENT_TYPE, "text/plain;charset=iso8859-1;other-parameter=true");
        addAttachment(attContentType, attText, attFileName);
        Exchange result = template.send("direct:roundtripbinarycontent", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertThat(out.getHeader(CONTENT_TYPE, String.class), StringStartsWith.startsWith("text/plain"));
        assertEquals("iso8859-1", out.getHeader(CONTENT_ENCODING));
        assertTrue(out.hasAttachments());
        assertEquals(1, out.getAttachmentNames().size());
        assertThat(out.getAttachmentNames(), IsCollectionContaining.hasItem(attFileName));
        DataHandler dh = out.getAttachment(attFileName);
        assertNotNull(dh);
        assertEquals(attContentType, dh.getContentType());
        InputStream is = dh.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOHelper.copyAndCloseInput(is, os);
        assertEquals(attText, new String(os.toByteArray()));
    }

    @Test
    public void roundtripWithBinaryAttachments() throws IOException {
        String attContentType = "application/binary";
        byte[] attText = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7 };
        String attFileName = "Attachment File Name";
        in.setBody("Body text");
        DataSource ds = new ByteArrayDataSource(attText, attContentType);
        in.addAttachment(attFileName, new DataHandler(ds));
        Exchange result = template.send("direct:roundtrip", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertTrue(out.hasAttachments());
        assertEquals(1, out.getAttachmentNames().size());
        assertThat(out.getAttachmentNames(), IsCollectionContaining.hasItem(attFileName));
        DataHandler dh = out.getAttachment(attFileName);
        assertNotNull(dh);
        assertEquals(attContentType, dh.getContentType());
        InputStream is = dh.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOHelper.copyAndCloseInput(is, os);
        assertArrayEquals(attText, os.toByteArray());
    }

    @Test
    public void roundtripWithBinaryAttachmentsAndBinaryContent() throws IOException {
        String attContentType = "application/binary";
        byte[] attText = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7 };
        String attFileName = "Attachment File Name";
        in.setBody("Body text");
        DataSource ds = new ByteArrayDataSource(attText, attContentType);
        in.addAttachment(attFileName, new DataHandler(ds));
        Exchange result = template.send("direct:roundtripbinarycontent", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertTrue(out.hasAttachments());
        assertEquals(1, out.getAttachmentNames().size());
        assertThat(out.getAttachmentNames(), IsCollectionContaining.hasItem(attFileName));
        DataHandler dh = out.getAttachment(attFileName);
        assertNotNull(dh);
        assertEquals(attContentType, dh.getContentType());
        InputStream is = dh.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOHelper.copyAndCloseInput(is, os);
        assertArrayEquals(attText, os.toByteArray());
    }

    @Test
    public void roundtripWithoutAttachments() throws IOException {
        in.setBody("Body text");
        Exchange result = template.send("direct:roundtrip", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertFalse(out.hasAttachments());
    }

    @Test
    public void roundtripWithoutAttachmentsToMultipart() throws IOException {
        in.setBody("Body text");
        Exchange result = template.send("direct:roundtripmultipart", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertFalse(out.hasAttachments());
    }

    @Test
    public void roundtripWithoutAttachmentsAndContentType() throws IOException {
        in.setBody("Body text");
        in.setHeader("Content-Type", "text/plain");
        Exchange result = template.send("direct:roundtrip", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertFalse(out.hasAttachments());
    }

    @Test
    public void roundtripWithoutAttachmentsAndInvalidContentType() throws IOException {
        in.setBody("Body text");
        in.setHeader("Content-Type", "text?plain");
        Exchange result = template.send("direct:roundtrip", exchange);
        Message out = result.getOut();
        assertEquals("Body text", out.getBody(String.class));
        assertFalse(out.hasAttachments());
    }

    @Test
    public void marhsalOnlyMixed() throws IOException {
        in.setBody("Body text");
        in.setHeader("Content-Type", "text/plain");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Description", "Sample Attachment Data");
        headers.put("X-AdditionalData", "additional data");
        addAttachment("application/octet-stream", "foobar", "attachment.bin", headers);
        Exchange result = template.send("direct:marshalonlymixed", exchange);
        assertThat(result.getOut().getHeader("Content-Type", String.class), StringStartsWith.startsWith("multipart/mixed"));
        String resultBody = result.getOut().getBody(String.class);
        assertThat(resultBody, StringContains.containsString("Content-Description: Sample Attachment Data"));
    }

    @Test
    public void marhsalOnlyRelated() throws IOException {
        in.setBody("Body text");
        in.setHeader("Content-Type", "text/plain");
        addAttachment("application/octet-stream", "foobar", "attachment.bin");
        Exchange result = template.send("direct:marshalonlyrelated", exchange);
        assertThat(result.getOut().getHeader("Content-Type", String.class), StringStartsWith.startsWith("multipart/related"));
    }

    @Test
    public void marhsalUnmarshalInlineHeaders() throws IOException {
        in.setBody("Body text");
        in.setHeader("Content-Type", "text/plain");
        in.setHeader("included", "must be included");
        in.setHeader("excluded", "should not be there");
        in.setHeader("x-foo", "any value");
        in.setHeader("x-bar", "also there");
        in.setHeader("xbar", "should not be there");
        addAttachment("application/octet-stream", "foobar", "attachment.bin");
        Exchange intermediate = template.send("direct:marshalonlyinlineheaders", exchange);
        String bodyStr = intermediate.getOut().getBody(String.class);
        assertThat(bodyStr, StringContains.containsString("must be included"));
        assertThat(bodyStr, IsNot.not(StringContains.containsString("should not be there")));
        assertThat(bodyStr, StringContains.containsString("x-foo:"));
        assertThat(bodyStr, StringContains.containsString("x-bar:"));
        assertThat(bodyStr, IsNot.not(StringContains.containsString("xbar")));
        intermediate.setIn(intermediate.getOut());
        intermediate.setOut(null);
        intermediate.getIn().removeHeaders(".*");
        intermediate.getIn().setHeader("included", "should be replaced");
        Exchange out = template.send("direct:unmarshalonlyinlineheaders", intermediate);
        assertEquals("Body text", out.getOut().getBody(String.class));
        assertEquals("must be included", out.getOut().getHeader("included"));
        assertNull(out.getOut().getHeader("excluded"));
        assertEquals("any value", out.getOut().getHeader("x-foo"));
        assertEquals("also there", out.getOut().getHeader("x-bar"));
    }

    @Test
    public void unmarshalRelated() throws IOException {
        in.setBody(new File("src/test/resources/multipart-related.txt"));
        Attachment dh = unmarshalAndCheckAttachmentName("950120.aaCB@XIson.com");
        assertNotNull(dh);
        assertEquals("The fixed length records", dh.getHeader("Content-Description"));
        assertEquals("header value1,header value2", dh.getHeader("X-Additional-Header"));
        assertEquals(2, dh.getHeaderAsList("X-Additional-Header").size());
    }

    @Test
    public void unmarshalWithoutId() throws IOException {
        in.setBody(new File("src/test/resources/multipart-without-id.txt"));
        unmarshalAndCheckAttachmentName("@camel.apache.org");
    }

    @Test
    public void unmarshalNonMimeBody() {
        in.setBody("This is not a MIME-Multipart");
        Exchange out = template.send("direct:unmarshalonly", exchange);
        assertNotNull(out.getOut());
        String bodyStr = out.getOut().getBody(String.class);
        assertEquals("This is not a MIME-Multipart", bodyStr);
    }

    @Test
    public void unmarshalInlineHeadersNonMimeBody() {
        in.setBody("This is not a MIME-Multipart");
        Exchange out = template.send("direct:unmarshalonlyinlineheaders", exchange);
        assertNotNull(out.getOut());
        String bodyStr = out.getOut().getBody(String.class);
        assertEquals("This is not a MIME-Multipart", bodyStr);
    }

    /* This test will only work of stream caching is enabled on the route. In order to find out whether the body
    is a multipart or not the stream has to be read, and if the underlying data is a stream (but not a stream cache)
    there is no way back
     */
    @Test
    public void unmarshalInlineHeadersNonMimeBodyStream() throws UnsupportedEncodingException {
        in.setBody(new ByteArrayInputStream("This is not a MIME-Multipart".getBytes("UTF-8")));
        Exchange out = template.send("direct:unmarshalonlyinlineheaders", exchange);
        assertNotNull(out.getOut());
        String bodyStr = out.getOut().getBody(String.class);
        assertEquals("This is not a MIME-Multipart", bodyStr);
    }
}


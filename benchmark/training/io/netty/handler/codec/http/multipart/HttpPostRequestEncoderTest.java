/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http.multipart;


import HttpMethod.CONNECT;
import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.PATCH;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpMethod.TRACE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder.EncoderMode;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder.ErrorDataEncoderException;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import static DefaultHttpDataFactory.MINSIZE;


/**
 * {@link HttpPostRequestEncoder} test case.
 */
public class HttpPostRequestEncoderTest {
    @Test
    public void testAllowedMethods() throws Exception {
        shouldThrowExceptionIfNotAllowed(CONNECT);
        shouldThrowExceptionIfNotAllowed(PUT);
        shouldThrowExceptionIfNotAllowed(POST);
        shouldThrowExceptionIfNotAllowed(PATCH);
        shouldThrowExceptionIfNotAllowed(DELETE);
        shouldThrowExceptionIfNotAllowed(GET);
        shouldThrowExceptionIfNotAllowed(HEAD);
        shouldThrowExceptionIfNotAllowed(OPTIONS);
        try {
            shouldThrowExceptionIfNotAllowed(TRACE);
            Assert.fail("Should raised an exception with TRACE method");
        } catch (ErrorDataEncoderException e) {
            // Exception is willing
        }
    }

    @Test
    public void testSingleFileUploadNoName() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(request, true);
        File file1 = new File(getClass().getResource("/file-01.txt").toURI());
        encoder.addBodyAttribute("foo", "bar");
        encoder.addBodyFileUpload("quux", "", file1, "text/plain", false);
        String multipartDataBoundary = encoder.multipartDataBoundary;
        String content = HttpPostRequestEncoderTest.getRequestBody(encoder);
        String expected = (((((((((((((((((((((((((((((((((((("--" + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"foo\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": 3") + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain; charset=UTF-8") + "\r\n") + "\r\n") + "bar") + "\r\n") + "--") + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"quux\"\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": ") + (file1.length())) + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain") + "\r\n") + (HttpHeaderNames.CONTENT_TRANSFER_ENCODING)) + ": binary") + "\r\n") + "\r\n") + "File 01") + (StringUtil.NEWLINE)) + "\r\n") + "--") + multipartDataBoundary) + "--") + "\r\n";
        Assert.assertEquals(expected, content);
    }

    @Test
    public void testMultiFileUploadInMixedMode() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(request, true);
        File file1 = new File(getClass().getResource("/file-01.txt").toURI());
        File file2 = new File(getClass().getResource("/file-02.txt").toURI());
        encoder.addBodyAttribute("foo", "bar");
        encoder.addBodyFileUpload("quux", file1, "text/plain", false);
        encoder.addBodyFileUpload("quux", file2, "text/plain", false);
        // We have to query the value of these two fields before finalizing
        // the request, which unsets one of them.
        String multipartDataBoundary = encoder.multipartDataBoundary;
        String multipartMixedBoundary = encoder.multipartMixedBoundary;
        String content = HttpPostRequestEncoderTest.getRequestBody(encoder);
        String expected = (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("--" + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"foo\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": 3") + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain; charset=UTF-8") + "\r\n") + "\r\n") + "bar") + "\r\n") + "--") + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"quux\"") + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": multipart/mixed; boundary=") + multipartMixedBoundary) + "\r\n") + "\r\n") + "--") + multipartMixedBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": attachment; filename=\"file-02.txt\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": ") + (file1.length())) + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain") + "\r\n") + (HttpHeaderNames.CONTENT_TRANSFER_ENCODING)) + ": binary") + "\r\n") + "\r\n") + "File 01") + (StringUtil.NEWLINE)) + "\r\n") + "--") + multipartMixedBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": attachment; filename=\"file-02.txt\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": ") + (file2.length())) + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain") + "\r\n") + (HttpHeaderNames.CONTENT_TRANSFER_ENCODING)) + ": binary") + "\r\n") + "\r\n") + "File 02") + (StringUtil.NEWLINE)) + "\r\n") + "--") + multipartMixedBoundary) + "--") + "\r\n") + "--") + multipartDataBoundary) + "--") + "\r\n";
        Assert.assertEquals(expected, content);
    }

    @Test
    public void testMultiFileUploadInMixedModeNoName() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(request, true);
        File file1 = new File(getClass().getResource("/file-01.txt").toURI());
        File file2 = new File(getClass().getResource("/file-02.txt").toURI());
        encoder.addBodyAttribute("foo", "bar");
        encoder.addBodyFileUpload("quux", "", file1, "text/plain", false);
        encoder.addBodyFileUpload("quux", "", file2, "text/plain", false);
        // We have to query the value of these two fields before finalizing
        // the request, which unsets one of them.
        String multipartDataBoundary = encoder.multipartDataBoundary;
        String multipartMixedBoundary = encoder.multipartMixedBoundary;
        String content = HttpPostRequestEncoderTest.getRequestBody(encoder);
        String expected = (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("--" + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"foo\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": 3") + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain; charset=UTF-8") + "\r\n") + "\r\n") + "bar") + "\r\n") + "--") + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"quux\"") + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": multipart/mixed; boundary=") + multipartMixedBoundary) + "\r\n") + "\r\n") + "--") + multipartMixedBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": attachment\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": ") + (file1.length())) + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain") + "\r\n") + (HttpHeaderNames.CONTENT_TRANSFER_ENCODING)) + ": binary") + "\r\n") + "\r\n") + "File 01") + (StringUtil.NEWLINE)) + "\r\n") + "--") + multipartMixedBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": attachment\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": ") + (file2.length())) + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain") + "\r\n") + (HttpHeaderNames.CONTENT_TRANSFER_ENCODING)) + ": binary") + "\r\n") + "\r\n") + "File 02") + (StringUtil.NEWLINE)) + "\r\n") + "--") + multipartMixedBoundary) + "--") + "\r\n") + "--") + multipartDataBoundary) + "--") + "\r\n";
        Assert.assertEquals(expected, content);
    }

    @Test
    public void testSingleFileUploadInHtml5Mode() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        DefaultHttpDataFactory factory = new DefaultHttpDataFactory(MINSIZE);
        HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(factory, request, true, CharsetUtil.UTF_8, EncoderMode.HTML5);
        File file1 = new File(getClass().getResource("/file-01.txt").toURI());
        File file2 = new File(getClass().getResource("/file-02.txt").toURI());
        encoder.addBodyAttribute("foo", "bar");
        encoder.addBodyFileUpload("quux", file1, "text/plain", false);
        encoder.addBodyFileUpload("quux", file2, "text/plain", false);
        String multipartDataBoundary = encoder.multipartDataBoundary;
        String content = HttpPostRequestEncoderTest.getRequestBody(encoder);
        String expected = ((((((((((((((((((((((((((((((((((((((((((((((((((((((((("--" + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"foo\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": 3") + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain; charset=UTF-8") + "\r\n") + "\r\n") + "bar") + "\r\n") + "--") + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"quux\"; filename=\"file-01.txt\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": ") + (file1.length())) + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain") + "\r\n") + (HttpHeaderNames.CONTENT_TRANSFER_ENCODING)) + ": binary") + "\r\n") + "\r\n") + "File 01") + (StringUtil.NEWLINE)) + "\r\n") + "--") + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"quux\"; filename=\"file-02.txt\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": ") + (file2.length())) + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain") + "\r\n") + (HttpHeaderNames.CONTENT_TRANSFER_ENCODING)) + ": binary") + "\r\n") + "\r\n") + "File 02") + (StringUtil.NEWLINE)) + "\r\n") + "--") + multipartDataBoundary) + "--") + "\r\n";
        Assert.assertEquals(expected, content);
    }

    @Test
    public void testMultiFileUploadInHtml5Mode() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        DefaultHttpDataFactory factory = new DefaultHttpDataFactory(MINSIZE);
        HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(factory, request, true, CharsetUtil.UTF_8, EncoderMode.HTML5);
        File file1 = new File(getClass().getResource("/file-01.txt").toURI());
        encoder.addBodyAttribute("foo", "bar");
        encoder.addBodyFileUpload("quux", file1, "text/plain", false);
        String multipartDataBoundary = encoder.multipartDataBoundary;
        String content = HttpPostRequestEncoderTest.getRequestBody(encoder);
        String expected = ((((((((((((((((((((((((((((((((((((("--" + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"foo\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": 3") + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain; charset=UTF-8") + "\r\n") + "\r\n") + "bar") + "\r\n") + "--") + multipartDataBoundary) + "\r\n") + (HttpHeaderNames.CONTENT_DISPOSITION)) + ": form-data; name=\"quux\"; filename=\"file-01.txt\"") + "\r\n") + (HttpHeaderNames.CONTENT_LENGTH)) + ": ") + (file1.length())) + "\r\n") + (HttpHeaderNames.CONTENT_TYPE)) + ": text/plain") + "\r\n") + (HttpHeaderNames.CONTENT_TRANSFER_ENCODING)) + ": binary") + "\r\n") + "\r\n") + "File 01") + (StringUtil.NEWLINE)) + "\r\n") + "--") + multipartDataBoundary) + "--") + "\r\n";
        Assert.assertEquals(expected, content);
    }

    @Test
    public void testHttpPostRequestEncoderSlicedBuffer() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(request, true);
        // add Form attribute
        encoder.addBodyAttribute("getform", "POST");
        encoder.addBodyAttribute("info", "first value");
        encoder.addBodyAttribute("secondinfo", "secondvalue a&");
        encoder.addBodyAttribute("thirdinfo", "short text");
        int length = 100000;
        char[] array = new char[length];
        Arrays.fill(array, 'a');
        String longText = new String(array);
        encoder.addBodyAttribute("fourthinfo", longText.substring(0, 7470));
        File file1 = new File(getClass().getResource("/file-01.txt").toURI());
        encoder.addBodyFileUpload("myfile", file1, "application/x-zip-compressed", false);
        encoder.finalizeRequest();
        while (!(encoder.isEndOfInput())) {
            HttpContent httpContent = encoder.readChunk(((ByteBufAllocator) (null)));
            ByteBuf content = httpContent.content();
            int refCnt = content.refCnt();
            Assert.assertTrue(((((("content: " + content) + " content.unwrap(): ") + (content.unwrap())) + " refCnt: ") + refCnt), (((((content.unwrap()) == content) || ((content.unwrap()) == null)) && (refCnt == 1)) || (((content.unwrap()) != content) && (refCnt == 2))));
            httpContent.release();
        } 
        encoder.cleanFiles();
        encoder.close();
    }

    @Test
    public void testDataIsMultipleOfChunkSize1() throws Exception {
        DefaultHttpDataFactory factory = new DefaultHttpDataFactory(MINSIZE);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(factory, request, true, HttpConstants.DEFAULT_CHARSET, EncoderMode.RFC1738);
        MemoryFileUpload first = new MemoryFileUpload("resources", "", "application/json", null, CharsetUtil.UTF_8, (-1));
        first.setMaxSize((-1));
        first.setContent(new ByteArrayInputStream(new byte[7955]));
        encoder.addBodyHttpData(first);
        MemoryFileUpload second = new MemoryFileUpload("resources2", "", "application/json", null, CharsetUtil.UTF_8, (-1));
        second.setMaxSize((-1));
        second.setContent(new ByteArrayInputStream(new byte[7928]));
        encoder.addBodyHttpData(second);
        Assert.assertNotNull(encoder.finalizeRequest());
        HttpPostRequestEncoderTest.checkNextChunkSize(encoder, 8080);
        HttpPostRequestEncoderTest.checkNextChunkSize(encoder, 8080);
        HttpContent httpContent = encoder.readChunk(((ByteBufAllocator) (null)));
        Assert.assertTrue("Expected LastHttpContent is not received", (httpContent instanceof LastHttpContent));
        httpContent.release();
        Assert.assertTrue("Expected end of input is not receive", encoder.isEndOfInput());
    }

    @Test
    public void testDataIsMultipleOfChunkSize2() throws Exception {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(request, true);
        int length = 7943;
        char[] array = new char[length];
        Arrays.fill(array, 'a');
        String longText = new String(array);
        encoder.addBodyAttribute("foo", longText);
        Assert.assertNotNull(encoder.finalizeRequest());
        HttpPostRequestEncoderTest.checkNextChunkSize(encoder, 8080);
        HttpContent httpContent = encoder.readChunk(((ByteBufAllocator) (null)));
        Assert.assertTrue("Expected LastHttpContent is not received", (httpContent instanceof LastHttpContent));
        httpContent.release();
        Assert.assertTrue("Expected end of input is not receive", encoder.isEndOfInput());
    }
}


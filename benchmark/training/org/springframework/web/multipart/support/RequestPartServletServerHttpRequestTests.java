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
package org.springframework.web.multipart.support;


import HttpMethod.POST;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_OCTET_STREAM;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.mock.web.test.MockMultipartFile;
import org.springframework.mock.web.test.MockMultipartHttpServletRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 *
 * @author Rossen Stoyanchev
 */
public class RequestPartServletServerHttpRequestTests {
    private final MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();

    @Test
    public void getMethod() throws Exception {
        this.mockRequest.addFile(new MockMultipartFile("part", "", "", "content".getBytes("UTF-8")));
        ServerHttpRequest request = new RequestPartServletServerHttpRequest(this.mockRequest, "part");
        this.mockRequest.setMethod("POST");
        Assert.assertEquals(POST, request.getMethod());
    }

    @Test
    public void getURI() throws Exception {
        this.mockRequest.addFile(new MockMultipartFile("part", "", "application/json", "content".getBytes("UTF-8")));
        ServerHttpRequest request = new RequestPartServletServerHttpRequest(this.mockRequest, "part");
        URI uri = new URI("http://example.com/path?query");
        this.mockRequest.setServerName(uri.getHost());
        this.mockRequest.setServerPort(uri.getPort());
        this.mockRequest.setRequestURI(uri.getPath());
        this.mockRequest.setQueryString(uri.getQuery());
        Assert.assertEquals(uri, request.getURI());
    }

    @Test
    public void getContentType() throws Exception {
        MultipartFile part = new MockMultipartFile("part", "", "application/json", "content".getBytes("UTF-8"));
        this.mockRequest.addFile(part);
        ServerHttpRequest request = new RequestPartServletServerHttpRequest(this.mockRequest, "part");
        org.springframework.http.HttpHeaders headers = request.getHeaders();
        Assert.assertNotNull(headers);
        Assert.assertEquals(APPLICATION_JSON, headers.getContentType());
    }

    @Test
    public void getBody() throws Exception {
        byte[] bytes = "content".getBytes("UTF-8");
        MultipartFile part = new MockMultipartFile("part", "", "application/json", bytes);
        this.mockRequest.addFile(part);
        ServerHttpRequest request = new RequestPartServletServerHttpRequest(this.mockRequest, "part");
        byte[] result = FileCopyUtils.copyToByteArray(request.getBody());
        Assert.assertArrayEquals(bytes, result);
    }

    // SPR-13317
    @Test
    public void getBodyWithWrappedRequest() throws Exception {
        byte[] bytes = "content".getBytes("UTF-8");
        MultipartFile part = new MockMultipartFile("part", "", "application/json", bytes);
        this.mockRequest.addFile(part);
        HttpServletRequest wrapped = new HttpServletRequestWrapper(this.mockRequest);
        ServerHttpRequest request = new RequestPartServletServerHttpRequest(wrapped, "part");
        byte[] result = FileCopyUtils.copyToByteArray(request.getBody());
        Assert.assertArrayEquals(bytes, result);
    }

    // SPR-13096
    @Test
    public void getBodyViaRequestParameter() throws Exception {
        MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest() {
            @Override
            public org.springframework.http.HttpHeaders getMultipartHeaders(String paramOrFileName) {
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(new MediaType("application", "octet-stream", StandardCharsets.ISO_8859_1));
                return headers;
            }
        };
        byte[] bytes = new byte[]{ ((byte) (196)) };
        mockRequest.setParameter("part", new String(bytes, StandardCharsets.ISO_8859_1));
        ServerHttpRequest request = new RequestPartServletServerHttpRequest(mockRequest, "part");
        byte[] result = FileCopyUtils.copyToByteArray(request.getBody());
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void getBodyViaRequestParameterWithRequestEncoding() throws Exception {
        MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest() {
            @Override
            public org.springframework.http.HttpHeaders getMultipartHeaders(String paramOrFileName) {
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(APPLICATION_OCTET_STREAM);
                return headers;
            }
        };
        byte[] bytes = new byte[]{ ((byte) (196)) };
        mockRequest.setParameter("part", new String(bytes, StandardCharsets.ISO_8859_1));
        mockRequest.setCharacterEncoding("iso-8859-1");
        ServerHttpRequest request = new RequestPartServletServerHttpRequest(mockRequest, "part");
        byte[] result = FileCopyUtils.copyToByteArray(request.getBody());
        Assert.assertArrayEquals(bytes, result);
    }
}


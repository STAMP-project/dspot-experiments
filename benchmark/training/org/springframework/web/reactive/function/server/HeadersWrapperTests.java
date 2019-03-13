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
package org.springframework.web.reactive.function.server;


import MediaType.APPLICATION_JSON;
import ServerRequest.Headers;
import ServerRequestWrapper.HeadersWrapper;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class HeadersWrapperTests {
    private Headers mockHeaders;

    private HeadersWrapper wrapper;

    @Test
    public void accept() {
        List<MediaType> accept = Collections.singletonList(APPLICATION_JSON);
        Mockito.when(mockHeaders.accept()).thenReturn(accept);
        Assert.assertSame(accept, wrapper.accept());
    }

    @Test
    public void acceptCharset() {
        List<Charset> acceptCharset = Collections.singletonList(StandardCharsets.UTF_8);
        Mockito.when(mockHeaders.acceptCharset()).thenReturn(acceptCharset);
        Assert.assertSame(acceptCharset, wrapper.acceptCharset());
    }

    @Test
    public void contentLength() {
        OptionalLong contentLength = OptionalLong.of(42L);
        Mockito.when(mockHeaders.contentLength()).thenReturn(contentLength);
        Assert.assertSame(contentLength, wrapper.contentLength());
    }

    @Test
    public void contentType() {
        Optional<MediaType> contentType = Optional.of(APPLICATION_JSON);
        Mockito.when(mockHeaders.contentType()).thenReturn(contentType);
        Assert.assertSame(contentType, wrapper.contentType());
    }

    @Test
    public void host() {
        InetSocketAddress host = InetSocketAddress.createUnresolved("example.com", 42);
        Mockito.when(mockHeaders.host()).thenReturn(host);
        Assert.assertSame(host, wrapper.host());
    }

    @Test
    public void range() {
        List<HttpRange> range = Collections.singletonList(HttpRange.createByteRange(42));
        Mockito.when(mockHeaders.range()).thenReturn(range);
        Assert.assertSame(range, wrapper.range());
    }

    @Test
    public void header() {
        String name = "foo";
        List<String> value = Collections.singletonList("bar");
        Mockito.when(mockHeaders.header(name)).thenReturn(value);
        Assert.assertSame(value, wrapper.header(name));
    }

    @Test
    public void asHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        Mockito.when(mockHeaders.asHttpHeaders()).thenReturn(httpHeaders);
        Assert.assertSame(httpHeaders, wrapper.asHttpHeaders());
    }
}


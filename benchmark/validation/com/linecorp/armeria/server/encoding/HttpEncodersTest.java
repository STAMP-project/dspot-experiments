/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.encoding;


import HttpEncodingType.DEFLATE;
import HttpEncodingType.GZIP;
import HttpHeaderNames.ACCEPT_ENCODING;
import HttpHeaders.EMPTY_HEADERS;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class HttpEncodersTest {
    @Rule
    public MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private HttpRequest request;

    @Test
    public void noAcceptEncoding() {
        Mockito.when(request.headers()).thenReturn(EMPTY_HEADERS);
        assertThat(HttpEncoders.getWrapperForRequest(request)).isNull();
    }

    @Test
    public void acceptEncodingGzip() {
        Mockito.when(request.headers()).thenReturn(HttpHeaders.of(ACCEPT_ENCODING, "gzip"));
        assertThat(HttpEncoders.getWrapperForRequest(request)).isEqualTo(GZIP);
    }

    @Test
    public void acceptEncodingDeflate() {
        Mockito.when(request.headers()).thenReturn(HttpHeaders.of(ACCEPT_ENCODING, "deflate"));
        assertThat(HttpEncoders.getWrapperForRequest(request)).isEqualTo(DEFLATE);
    }

    @Test
    public void acceptEncodingBoth() {
        Mockito.when(request.headers()).thenReturn(HttpHeaders.of(ACCEPT_ENCODING, "gzip, deflate"));
        assertThat(HttpEncoders.getWrapperForRequest(request)).isEqualTo(GZIP);
    }

    @Test
    public void acceptEncodingUnknown() {
        Mockito.when(request.headers()).thenReturn(HttpHeaders.of(ACCEPT_ENCODING, "piedpiper"));
        assertThat(HttpEncoders.getWrapperForRequest(request)).isNull();
    }
}


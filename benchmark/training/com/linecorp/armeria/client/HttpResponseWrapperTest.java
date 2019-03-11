/**
 * Copyright 2019 LINE Corporation
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
package com.linecorp.armeria.client;


import HttpHeaderNames.CONTENT_LENGTH;
import com.linecorp.armeria.client.HttpResponseDecoder.HttpResponseWrapper;
import com.linecorp.armeria.common.CommonPools;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpObject;
import com.linecorp.armeria.internal.InboundTrafficController;
import io.netty.channel.Channel;
import java.util.List;
import org.junit.Test;


public class HttpResponseWrapperTest {
    @Test
    public void headersAndData() throws Exception {
        final DecodedHttpResponse res = new DecodedHttpResponse(CommonPools.workerGroup().next());
        final HttpResponseWrapper wrapper = HttpResponseWrapperTest.httpResponseWrapper(res);
        assertThat(wrapper.tryWrite(HttpHeaders.of(200).addInt(CONTENT_LENGTH, "foo".length()))).isTrue();
        assertThat(wrapper.tryWrite(HttpData.ofUtf8("foo"))).isTrue();
        wrapper.close();
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(200).addInt(CONTENT_LENGTH, 3), HttpData.ofUtf8("foo"));
    }

    @Test
    public void headersAndTrailers() throws Exception {
        final DecodedHttpResponse res = new DecodedHttpResponse(CommonPools.workerGroup().next());
        final HttpResponseWrapper wrapper = HttpResponseWrapperTest.httpResponseWrapper(res);
        assertThat(wrapper.tryWrite(HttpHeaders.of(200))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();
        wrapper.close();
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(200), HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    @Test
    public void dataIsIgnoreAfterSecondHeaders() throws Exception {
        final DecodedHttpResponse res = new DecodedHttpResponse(CommonPools.workerGroup().next());
        final HttpResponseWrapper wrapper = HttpResponseWrapperTest.httpResponseWrapper(res);
        assertThat(wrapper.tryWrite(HttpHeaders.of(200))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();// Second header is trailers.

        assertThat(wrapper.tryWrite(HttpData.ofUtf8("foo"))).isFalse();
        wrapper.close();
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(200), HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    @Test
    public void splitTrailersIsIgnored() throws Exception {
        final DecodedHttpResponse res = new DecodedHttpResponse(CommonPools.workerGroup().next());
        final HttpResponseWrapper wrapper = HttpResponseWrapperTest.httpResponseWrapper(res);
        assertThat(wrapper.tryWrite(HttpHeaders.of(200))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("qux"), "quux"))).isFalse();
        wrapper.close();
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(200), HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    @Test
    public void splitTrailersAfterDataIsIgnored() throws Exception {
        final DecodedHttpResponse res = new DecodedHttpResponse(CommonPools.workerGroup().next());
        final HttpResponseWrapper wrapper = HttpResponseWrapperTest.httpResponseWrapper(res);
        assertThat(wrapper.tryWrite(HttpHeaders.of(200).addInt(CONTENT_LENGTH, "foo".length()))).isTrue();
        assertThat(wrapper.tryWrite(HttpData.ofUtf8("foo"))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("qux"), "quux"))).isFalse();
        wrapper.close();
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(200).addInt(CONTENT_LENGTH, 3), HttpData.ofUtf8("foo"), HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    @Test
    public void infromationalHeadersHeadersDataAndTrailers() throws Exception {
        final DecodedHttpResponse res = new DecodedHttpResponse(CommonPools.workerGroup().next());
        final HttpResponseWrapper wrapper = HttpResponseWrapperTest.httpResponseWrapper(res);
        assertThat(wrapper.tryWrite(HttpHeaders.of(100))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("a"), "b"))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(200).addInt(CONTENT_LENGTH, "foo".length()))).isTrue();
        assertThat(wrapper.tryWrite(HttpData.ofUtf8("foo"))).isTrue();
        assertThat(wrapper.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();
        wrapper.close();
        final List<HttpObject> drained = res.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(100), HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("a"), "b"), HttpHeaders.of(200).addInt(CONTENT_LENGTH, "foo".length()), HttpData.ofUtf8("foo"), HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    private static class TestHttpResponseDecoder extends HttpResponseDecoder {
        TestHttpResponseDecoder(Channel channel, InboundTrafficController inboundTrafficController) {
            super(channel, inboundTrafficController);
        }
    }
}


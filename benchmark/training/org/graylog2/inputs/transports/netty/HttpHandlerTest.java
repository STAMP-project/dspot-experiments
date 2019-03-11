/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs.transports.netty;


import HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS;
import HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN;
import HttpHeaderNames.CONNECTION;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.CONTENT_TYPE;
import HttpHeaderNames.HOST;
import HttpHeaderNames.ORIGIN;
import HttpHeaderValues.APPLICATION_JSON;
import HttpHeaderValues.CLOSE;
import HttpHeaderValues.KEEP_ALIVE;
import HttpResponseStatus.ACCEPTED;
import HttpResponseStatus.METHOD_NOT_ALLOWED;
import HttpResponseStatus.NOT_FOUND;
import HttpResponseStatus.OK;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import org.junit.Test;


public class HttpHandlerTest {
    private static final byte[] GELF_MESSAGE = "{\"version\":\"1.1\",\"short_message\":\"Foo\",\"host\":\"localhost\"}".getBytes(StandardCharsets.UTF_8);

    private EmbeddedChannel channel;

    @Test
    public void messageReceivedSuccessfullyProcessesPOSTRequest() {
        final FullHttpRequest httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/gelf");
        httpRequest.headers().add(HOST, "localhost");
        httpRequest.headers().add(ORIGIN, "http://example.com");
        httpRequest.headers().add(CONNECTION, CLOSE);
        httpRequest.content().writeBytes(HttpHandlerTest.GELF_MESSAGE);
        channel.writeInbound(httpRequest);
        channel.finish();
        final HttpResponse httpResponse = channel.readOutbound();
        assertThat(httpResponse.status()).isEqualTo(ACCEPTED);
        final HttpHeaders headers = httpResponse.headers();
        assertThat(headers.get(CONTENT_LENGTH)).isEqualTo("0");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo("http://example.com");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_HEADERS)).isEqualTo("Authorization, Content-Type");
        assertThat(headers.get(CONNECTION)).isEqualTo(CLOSE.toString());
    }

    @Test
    public void withKeepalive() {
        final FullHttpRequest httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/gelf");
        httpRequest.headers().add(HOST, "localhost");
        httpRequest.headers().add(CONNECTION, KEEP_ALIVE);
        httpRequest.content().writeBytes(HttpHandlerTest.GELF_MESSAGE);
        channel.writeInbound(httpRequest);
        channel.finish();
        final HttpResponse httpResponse = channel.readOutbound();
        assertThat(httpResponse.status()).isEqualTo(ACCEPTED);
        final HttpHeaders headers = httpResponse.headers();
        assertThat(headers.get(CONTENT_LENGTH)).isEqualTo("0");
        assertThat(headers.get(CONNECTION)).isEqualTo(KEEP_ALIVE.toString());
    }

    @Test
    public void withJSONContentType() {
        final FullHttpRequest httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/gelf");
        httpRequest.headers().add(HOST, "localhost");
        httpRequest.headers().add(CONTENT_TYPE, APPLICATION_JSON);
        httpRequest.headers().add(CONNECTION, CLOSE);
        httpRequest.content().writeBytes(HttpHandlerTest.GELF_MESSAGE);
        channel.writeInbound(httpRequest);
        channel.finish();
        final HttpResponse httpResponse = channel.readOutbound();
        assertThat(httpResponse.status()).isEqualTo(ACCEPTED);
        final HttpHeaders headers = httpResponse.headers();
        assertThat(headers.get(CONTENT_LENGTH)).isEqualTo("0");
        assertThat(headers.get(CONNECTION)).isEqualTo(CLOSE.toString());
    }

    @Test
    public void withCustomContentType() {
        final FullHttpRequest httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/gelf");
        httpRequest.headers().add(HOST, "localhost");
        httpRequest.headers().add(CONTENT_TYPE, "foo/bar");
        httpRequest.headers().add(CONNECTION, CLOSE);
        httpRequest.content().writeBytes(HttpHandlerTest.GELF_MESSAGE);
        channel.writeInbound(httpRequest);
        channel.finish();
        final HttpResponse httpResponse = channel.readOutbound();
        assertThat(httpResponse.status()).isEqualTo(ACCEPTED);
        final HttpHeaders headers = httpResponse.headers();
        assertThat(headers.get(CONTENT_LENGTH)).isEqualTo("0");
        assertThat(headers.get(CONNECTION)).isEqualTo(CLOSE.toString());
    }

    @Test
    public void successfullyProcessOPTIONSRequest() {
        final HttpRequest httpRequest = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.OPTIONS, "/gelf");
        httpRequest.headers().add(HOST, "localhost");
        httpRequest.headers().add(ORIGIN, "http://example.com");
        httpRequest.headers().add(CONNECTION, CLOSE);
        channel.writeInbound(httpRequest);
        channel.finish();
        final HttpResponse httpResponse = channel.readOutbound();
        assertThat(httpResponse.status()).isEqualTo(OK);
        final HttpHeaders headers = httpResponse.headers();
        assertThat(headers.get(CONTENT_LENGTH)).isEqualTo("0");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo("http://example.com");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_HEADERS)).isEqualTo("Authorization, Content-Type");
    }

    @Test
    public void successfullyProcessOPTIONSRequestWithoutOrigin() {
        final HttpRequest httpRequest = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.OPTIONS, "/gelf");
        httpRequest.headers().add(HOST, "localhost");
        httpRequest.headers().add(CONNECTION, CLOSE);
        channel.writeInbound(httpRequest);
        channel.finish();
        final HttpResponse httpResponse = channel.readOutbound();
        assertThat(httpResponse.status()).isEqualTo(OK);
        final HttpHeaders headers = httpResponse.headers();
        assertThat(headers.get(CONTENT_LENGTH)).isEqualTo("0");
        assertThat(headers.contains(ACCESS_CONTROL_ALLOW_ORIGIN)).isFalse();
        assertThat(headers.contains(ACCESS_CONTROL_ALLOW_CREDENTIALS)).isFalse();
        assertThat(headers.contains(ACCESS_CONTROL_ALLOW_HEADERS)).isFalse();
    }

    @Test
    public void return404ForWrongPath() {
        final HttpRequest httpRequest = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/");
        httpRequest.headers().add(HOST, "localhost");
        httpRequest.headers().add(ORIGIN, "http://example.com");
        httpRequest.headers().add(CONNECTION, CLOSE);
        channel.writeInbound(httpRequest);
        channel.finish();
        final HttpResponse httpResponse = channel.readOutbound();
        assertThat(httpResponse.status()).isEqualTo(NOT_FOUND);
        final HttpHeaders headers = httpResponse.headers();
        assertThat(headers.get(CONTENT_LENGTH)).isEqualTo("0");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo("http://example.com");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_HEADERS)).isEqualTo("Authorization, Content-Type");
    }

    @Test
    public void messageReceivedReturns405ForInvalidMethod() {
        final HttpRequest httpRequest = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
        httpRequest.headers().add(HOST, "localhost");
        httpRequest.headers().add(ORIGIN, "http://example.com");
        httpRequest.headers().add(CONNECTION, CLOSE);
        channel.writeInbound(httpRequest);
        channel.finish();
        final HttpResponse httpResponse = channel.readOutbound();
        assertThat(httpResponse.status()).isEqualTo(METHOD_NOT_ALLOWED);
        final HttpHeaders headers = httpResponse.headers();
        assertThat(headers.get(CONTENT_LENGTH)).isEqualTo("0");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo("http://example.com");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
        assertThat(headers.get(ACCESS_CONTROL_ALLOW_HEADERS)).isEqualTo("Authorization, Content-Type");
    }
}


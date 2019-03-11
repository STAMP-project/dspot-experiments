/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http.cors;


import HttpMethod.POST;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeadersTestUtils;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


public class CorsHandlerTest {
    @Test
    public void nonCorsRequest() {
        final HttpResponse response = CorsHandlerTest.simpleRequest(forAnyOrigin().build(), null);
        MatcherAssert.assertThat(response.headers().contains(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(false));
    }

    @Test
    public void simpleRequestWithAnyOrigin() {
        final HttpResponse response = CorsHandlerTest.simpleRequest(forAnyOrigin().build(), "http://localhost:7777");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is("*"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_HEADERS), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void simpleRequestWithNullOrigin() {
        final HttpResponse response = CorsHandlerTest.simpleRequest(forOrigin("http://test.com").allowNullOrigin().allowCredentials().build(), "null");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is("null"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_CREDENTIALS), CoreMatchers.is(IsEqual.equalTo("true")));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_HEADERS), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void simpleRequestWithOrigin() {
        final String origin = "http://localhost:8888";
        final HttpResponse response = CorsHandlerTest.simpleRequest(forOrigin(origin).build(), origin);
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(origin));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_HEADERS), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void simpleRequestWithOrigins() {
        final String origin1 = "http://localhost:8888";
        final String origin2 = "https://localhost:8888";
        final String[] origins = new String[]{ origin1, origin2 };
        final HttpResponse response1 = CorsHandlerTest.simpleRequest(forOrigins(origins).build(), origin1);
        MatcherAssert.assertThat(response1.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(origin1));
        MatcherAssert.assertThat(response1.headers().get(ACCESS_CONTROL_ALLOW_HEADERS), CoreMatchers.is(CoreMatchers.nullValue()));
        final HttpResponse response2 = CorsHandlerTest.simpleRequest(forOrigins(origins).build(), origin2);
        MatcherAssert.assertThat(response2.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(origin2));
        MatcherAssert.assertThat(response2.headers().get(ACCESS_CONTROL_ALLOW_HEADERS), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void simpleRequestWithNoMatchingOrigin() {
        final String origin = "http://localhost:8888";
        final HttpResponse response = CorsHandlerTest.simpleRequest(forOrigins("https://localhost:8888").build(), origin);
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_HEADERS), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void preflightDeleteRequestWithCustomHeaders() {
        final CorsConfig config = forOrigin("http://localhost:8888").allowedRequestMethods(GET, DELETE).build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, "http://localhost:8888", "content-type, xheader1");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is("http://localhost:8888"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_METHODS), CoreMatchers.containsString("GET"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_METHODS), CoreMatchers.containsString("DELETE"));
        MatcherAssert.assertThat(response.headers().get(VARY), CoreMatchers.equalTo(ORIGIN.toString()));
    }

    @Test
    public void preflightGetRequestWithCustomHeaders() {
        final CorsConfig config = forOrigin("http://localhost:8888").allowedRequestMethods(OPTIONS, GET, DELETE).allowedRequestHeaders("content-type", "xheader1").build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, "http://localhost:8888", "content-type, xheader1");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is("http://localhost:8888"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_METHODS), CoreMatchers.containsString("OPTIONS"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_METHODS), CoreMatchers.containsString("GET"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_HEADERS), CoreMatchers.containsString("content-type"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_HEADERS), CoreMatchers.containsString("xheader1"));
        MatcherAssert.assertThat(response.headers().get(VARY), CoreMatchers.equalTo(ORIGIN.toString()));
    }

    @Test
    public void preflightRequestWithDefaultHeaders() {
        final CorsConfig config = forOrigin("http://localhost:8888").build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, "http://localhost:8888", "content-type, xheader1");
        MatcherAssert.assertThat(response.headers().get(CONTENT_LENGTH), CoreMatchers.is("0"));
        MatcherAssert.assertThat(response.headers().get(DATE), CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(response.headers().get(VARY), CoreMatchers.equalTo(ORIGIN.toString()));
    }

    @Test
    public void preflightRequestWithCustomHeader() {
        final CorsConfig config = forOrigin("http://localhost:8888").preflightResponseHeader("CustomHeader", "somevalue").build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, "http://localhost:8888", "content-type, xheader1");
        MatcherAssert.assertThat(response.headers().get(HttpHeadersTestUtils.of("CustomHeader")), IsEqual.equalTo("somevalue"));
        MatcherAssert.assertThat(response.headers().get(VARY), CoreMatchers.equalTo(ORIGIN.toString()));
        MatcherAssert.assertThat(response.headers().get(CONTENT_LENGTH), CoreMatchers.is("0"));
    }

    @Test
    public void preflightRequestWithUnauthorizedOrigin() {
        final String origin = "http://host";
        final CorsConfig config = forOrigin("http://localhost").build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, origin, "xheader1");
        MatcherAssert.assertThat(response.headers().contains(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(false));
    }

    @Test
    public void preflightRequestWithCustomHeaders() {
        final String headerName = "CustomHeader";
        final String value1 = "value1";
        final String value2 = "value2";
        final CorsConfig config = forOrigin("http://localhost:8888").preflightResponseHeader(headerName, value1, value2).build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, "http://localhost:8888", "content-type, xheader1");
        CorsHandlerTest.assertValues(response, headerName, value1, value2);
        MatcherAssert.assertThat(response.headers().get(VARY), CoreMatchers.equalTo(ORIGIN.toString()));
    }

    @Test
    public void preflightRequestWithCustomHeadersIterable() {
        final String headerName = "CustomHeader";
        final String value1 = "value1";
        final String value2 = "value2";
        final CorsConfig config = forOrigin("http://localhost:8888").preflightResponseHeader(headerName, Arrays.asList(value1, value2)).build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, "http://localhost:8888", "content-type, xheader1");
        CorsHandlerTest.assertValues(response, headerName, value1, value2);
        MatcherAssert.assertThat(response.headers().get(VARY), CoreMatchers.equalTo(ORIGIN.toString()));
    }

    @Test
    public void preflightRequestWithValueGenerator() {
        final CorsConfig config = forOrigin("http://localhost:8888").preflightResponseHeader("GenHeader", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "generatedValue";
            }
        }).build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, "http://localhost:8888", "content-type, xheader1");
        MatcherAssert.assertThat(response.headers().get(HttpHeadersTestUtils.of("GenHeader")), IsEqual.equalTo("generatedValue"));
        MatcherAssert.assertThat(response.headers().get(VARY), CoreMatchers.equalTo(ORIGIN.toString()));
    }

    @Test
    public void preflightRequestWithNullOrigin() {
        final String origin = "null";
        final CorsConfig config = forOrigin(origin).allowNullOrigin().allowCredentials().build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, origin, "content-type, xheader1");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(IsEqual.equalTo("null")));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_CREDENTIALS), CoreMatchers.is(IsEqual.equalTo("true")));
    }

    @Test
    public void preflightRequestAllowCredentials() {
        final String origin = "null";
        final CorsConfig config = forOrigin(origin).allowCredentials().build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, origin, "content-type, xheader1");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_CREDENTIALS), CoreMatchers.is(IsEqual.equalTo("true")));
    }

    @Test
    public void preflightRequestDoNotAllowCredentials() {
        final CorsConfig config = forOrigin("http://localhost:8888").build();
        final HttpResponse response = CorsHandlerTest.preflightRequest(config, "http://localhost:8888", "");
        // the only valid value for Access-Control-Allow-Credentials is true.
        MatcherAssert.assertThat(response.headers().contains(ACCESS_CONTROL_ALLOW_CREDENTIALS), CoreMatchers.is(false));
    }

    @Test
    public void simpleRequestCustomHeaders() {
        final CorsConfig config = forAnyOrigin().exposeHeaders("custom1", "custom2").build();
        final HttpResponse response = CorsHandlerTest.simpleRequest(config, "http://localhost:7777");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), IsEqual.equalTo("*"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_EXPOSE_HEADERS), CoreMatchers.containsString("custom1"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_EXPOSE_HEADERS), CoreMatchers.containsString("custom2"));
    }

    @Test
    public void simpleRequestAllowCredentials() {
        final CorsConfig config = forAnyOrigin().allowCredentials().build();
        final HttpResponse response = CorsHandlerTest.simpleRequest(config, "http://localhost:7777");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_CREDENTIALS), IsEqual.equalTo("true"));
    }

    @Test
    public void simpleRequestDoNotAllowCredentials() {
        final CorsConfig config = forAnyOrigin().build();
        final HttpResponse response = CorsHandlerTest.simpleRequest(config, "http://localhost:7777");
        MatcherAssert.assertThat(response.headers().contains(ACCESS_CONTROL_ALLOW_CREDENTIALS), CoreMatchers.is(false));
    }

    @Test
    public void anyOriginAndAllowCredentialsShouldEchoRequestOrigin() {
        final CorsConfig config = forAnyOrigin().allowCredentials().build();
        final HttpResponse response = CorsHandlerTest.simpleRequest(config, "http://localhost:7777");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_CREDENTIALS), IsEqual.equalTo("true"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), IsEqual.equalTo("http://localhost:7777"));
        MatcherAssert.assertThat(response.headers().get(VARY), CoreMatchers.equalTo(ORIGIN.toString()));
    }

    @Test
    public void simpleRequestExposeHeaders() {
        final CorsConfig config = forAnyOrigin().exposeHeaders("one", "two").build();
        final HttpResponse response = CorsHandlerTest.simpleRequest(config, "http://localhost:7777");
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_EXPOSE_HEADERS), CoreMatchers.containsString("one"));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_EXPOSE_HEADERS), CoreMatchers.containsString("two"));
    }

    @Test
    public void simpleRequestShortCircuit() {
        final CorsConfig config = forOrigin("http://localhost:8080").shortCircuit().build();
        final HttpResponse response = CorsHandlerTest.simpleRequest(config, "http://localhost:7777");
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.FORBIDDEN));
        MatcherAssert.assertThat(response.headers().get(CONTENT_LENGTH), CoreMatchers.is("0"));
    }

    @Test
    public void simpleRequestNoShortCircuit() {
        final CorsConfig config = forOrigin("http://localhost:8080").build();
        final HttpResponse response = CorsHandlerTest.simpleRequest(config, "http://localhost:7777");
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.OK));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shortCircuitNonCorsRequest() {
        final CorsConfig config = forOrigin("https://localhost").shortCircuit().build();
        final HttpResponse response = CorsHandlerTest.simpleRequest(config, null);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.OK));
        MatcherAssert.assertThat(response.headers().get(ACCESS_CONTROL_ALLOW_ORIGIN), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shortCircuitWithConnectionKeepAliveShouldStayOpen() {
        final CorsConfig config = forOrigin("http://localhost:8080").shortCircuit().build();
        final EmbeddedChannel channel = new EmbeddedChannel(new CorsHandler(config));
        final FullHttpRequest request = CorsHandlerTest.createHttpRequest(GET);
        request.headers().set(ORIGIN, "http://localhost:8888");
        request.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        MatcherAssert.assertThat(channel.writeInbound(request), CoreMatchers.is(false));
        final HttpResponse response = channel.readOutbound();
        MatcherAssert.assertThat(HttpUtil.isKeepAlive(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.isOpen(), CoreMatchers.is(true));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.FORBIDDEN));
        MatcherAssert.assertThat(ReferenceCountUtil.release(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void shortCircuitWithoutConnectionShouldStayOpen() {
        final CorsConfig config = forOrigin("http://localhost:8080").shortCircuit().build();
        final EmbeddedChannel channel = new EmbeddedChannel(new CorsHandler(config));
        final FullHttpRequest request = CorsHandlerTest.createHttpRequest(GET);
        request.headers().set(ORIGIN, "http://localhost:8888");
        MatcherAssert.assertThat(channel.writeInbound(request), CoreMatchers.is(false));
        final HttpResponse response = channel.readOutbound();
        MatcherAssert.assertThat(HttpUtil.isKeepAlive(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.isOpen(), CoreMatchers.is(true));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.FORBIDDEN));
        MatcherAssert.assertThat(ReferenceCountUtil.release(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void shortCircuitWithConnectionCloseShouldClose() {
        final CorsConfig config = forOrigin("http://localhost:8080").shortCircuit().build();
        final EmbeddedChannel channel = new EmbeddedChannel(new CorsHandler(config));
        final FullHttpRequest request = CorsHandlerTest.createHttpRequest(GET);
        request.headers().set(ORIGIN, "http://localhost:8888");
        request.headers().set(CONNECTION, HttpHeaderValues.CLOSE);
        MatcherAssert.assertThat(channel.writeInbound(request), CoreMatchers.is(false));
        final HttpResponse response = channel.readOutbound();
        MatcherAssert.assertThat(HttpUtil.isKeepAlive(response), CoreMatchers.is(false));
        MatcherAssert.assertThat(channel.isOpen(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.FORBIDDEN));
        MatcherAssert.assertThat(ReferenceCountUtil.release(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void preflightRequestShouldReleaseRequest() {
        final CorsConfig config = forOrigin("http://localhost:8888").preflightResponseHeader("CustomHeader", Arrays.asList("value1", "value2")).build();
        final EmbeddedChannel channel = new EmbeddedChannel(new CorsHandler(config));
        final FullHttpRequest request = CorsHandlerTest.optionsRequest("http://localhost:8888", "content-type, xheader1", null);
        MatcherAssert.assertThat(channel.writeInbound(request), CoreMatchers.is(false));
        MatcherAssert.assertThat(request.refCnt(), CoreMatchers.is(0));
        MatcherAssert.assertThat(ReferenceCountUtil.release(channel.readOutbound()), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void preflightRequestWithConnectionKeepAliveShouldStayOpen() throws Exception {
        final CorsConfig config = forOrigin("http://localhost:8888").build();
        final EmbeddedChannel channel = new EmbeddedChannel(new CorsHandler(config));
        final FullHttpRequest request = CorsHandlerTest.optionsRequest("http://localhost:8888", "", HttpHeaderValues.KEEP_ALIVE);
        MatcherAssert.assertThat(channel.writeInbound(request), CoreMatchers.is(false));
        final HttpResponse response = channel.readOutbound();
        MatcherAssert.assertThat(HttpUtil.isKeepAlive(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.isOpen(), CoreMatchers.is(true));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.OK));
        MatcherAssert.assertThat(ReferenceCountUtil.release(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void preflightRequestWithoutConnectionShouldStayOpen() throws Exception {
        final CorsConfig config = forOrigin("http://localhost:8888").build();
        final EmbeddedChannel channel = new EmbeddedChannel(new CorsHandler(config));
        final FullHttpRequest request = CorsHandlerTest.optionsRequest("http://localhost:8888", "", null);
        MatcherAssert.assertThat(channel.writeInbound(request), CoreMatchers.is(false));
        final HttpResponse response = channel.readOutbound();
        MatcherAssert.assertThat(HttpUtil.isKeepAlive(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.isOpen(), CoreMatchers.is(true));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.OK));
        MatcherAssert.assertThat(ReferenceCountUtil.release(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void preflightRequestWithConnectionCloseShouldClose() throws Exception {
        final CorsConfig config = forOrigin("http://localhost:8888").build();
        final EmbeddedChannel channel = new EmbeddedChannel(new CorsHandler(config));
        final FullHttpRequest request = CorsHandlerTest.optionsRequest("http://localhost:8888", "", HttpHeaderValues.CLOSE);
        MatcherAssert.assertThat(channel.writeInbound(request), CoreMatchers.is(false));
        final HttpResponse response = channel.readOutbound();
        MatcherAssert.assertThat(HttpUtil.isKeepAlive(response), CoreMatchers.is(false));
        MatcherAssert.assertThat(channel.isOpen(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(HttpResponseStatus.OK));
        MatcherAssert.assertThat(ReferenceCountUtil.release(response), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void forbiddenShouldReleaseRequest() {
        final CorsConfig config = forOrigin("https://localhost").shortCircuit().build();
        final EmbeddedChannel channel = new EmbeddedChannel(new CorsHandler(config), new CorsHandlerTest.EchoHandler());
        final FullHttpRequest request = CorsHandlerTest.createHttpRequest(GET);
        request.headers().set(ORIGIN, "http://localhost:8888");
        MatcherAssert.assertThat(channel.writeInbound(request), CoreMatchers.is(false));
        MatcherAssert.assertThat(request.refCnt(), CoreMatchers.is(0));
        MatcherAssert.assertThat(ReferenceCountUtil.release(channel.readOutbound()), CoreMatchers.is(true));
        MatcherAssert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void differentConfigsPerOrigin() {
        String host1 = "http://host1:80";
        String host2 = "http://host2";
        CorsConfig rule1 = forOrigin(host1).allowedRequestMethods(HttpMethod.GET).build();
        CorsConfig rule2 = forOrigin(host2).allowedRequestMethods(HttpMethod.GET, POST).allowCredentials().build();
        List<CorsConfig> corsConfigs = Arrays.asList(rule1, rule2);
        final HttpResponse preFlightHost1 = CorsHandlerTest.preflightRequest(corsConfigs, host1, "", false);
        MatcherAssert.assertThat(preFlightHost1.headers().get(ACCESS_CONTROL_ALLOW_METHODS), CoreMatchers.is("GET"));
        MatcherAssert.assertThat(preFlightHost1.headers().getAsString(ACCESS_CONTROL_ALLOW_CREDENTIALS), CoreMatchers.is(CoreMatchers.nullValue()));
        final HttpResponse preFlightHost2 = CorsHandlerTest.preflightRequest(corsConfigs, host2, "", false);
        CorsHandlerTest.assertValues(preFlightHost2, ACCESS_CONTROL_ALLOW_METHODS.toString(), "GET", "POST");
        MatcherAssert.assertThat(preFlightHost2.headers().getAsString(ACCESS_CONTROL_ALLOW_CREDENTIALS), IsEqual.equalTo("true"));
    }

    @Test
    public void specificConfigPrecedenceOverGeneric() {
        String host1 = "http://host1";
        String host2 = "http://host2";
        CorsConfig forHost1 = forOrigin(host1).allowedRequestMethods(HttpMethod.GET).maxAge(3600L).build();
        CorsConfig allowAll = forAnyOrigin().allowedRequestMethods(POST, HttpMethod.GET, HttpMethod.OPTIONS).maxAge(1800).build();
        List<CorsConfig> rules = Arrays.asList(forHost1, allowAll);
        final HttpResponse host1Response = CorsHandlerTest.preflightRequest(rules, host1, "", false);
        MatcherAssert.assertThat(host1Response.headers().get(ACCESS_CONTROL_ALLOW_METHODS), CoreMatchers.is("GET"));
        MatcherAssert.assertThat(host1Response.headers().getAsString(ACCESS_CONTROL_MAX_AGE), IsEqual.equalTo("3600"));
        final HttpResponse host2Response = CorsHandlerTest.preflightRequest(rules, host2, "", false);
        CorsHandlerTest.assertValues(host2Response, ACCESS_CONTROL_ALLOW_METHODS.toString(), "POST", "GET", "OPTIONS");
        MatcherAssert.assertThat(host2Response.headers().getAsString(ACCESS_CONTROL_ALLOW_ORIGIN), IsEqual.equalTo("*"));
        MatcherAssert.assertThat(host2Response.headers().getAsString(ACCESS_CONTROL_MAX_AGE), IsEqual.equalTo("1800"));
    }

    private static class EchoHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.writeAndFlush(new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, true, true));
        }
    }
}


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


import EmptyHttpHeaders.INSTANCE;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.DATE;
import HttpMethod.GET;
import HttpMethod.POST;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeadersTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class CorsConfigTest {
    @Test
    public void disabled() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().disable().build();
        MatcherAssert.assertThat(cors.isCorsSupportEnabled(), CoreMatchers.is(false));
    }

    @Test
    public void anyOrigin() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().build();
        MatcherAssert.assertThat(cors.isAnyOriginSupported(), CoreMatchers.is(true));
        MatcherAssert.assertThat(cors.origin(), CoreMatchers.is("*"));
        MatcherAssert.assertThat(cors.origins().isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void wildcardOrigin() {
        final CorsConfig cors = CorsConfigBuilder.forOrigin("*").build();
        MatcherAssert.assertThat(cors.isAnyOriginSupported(), CoreMatchers.is(true));
        MatcherAssert.assertThat(cors.origin(), CoreMatchers.equalTo("*"));
        MatcherAssert.assertThat(cors.origins().isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void origin() {
        final CorsConfig cors = CorsConfigBuilder.forOrigin("http://localhost:7888").build();
        MatcherAssert.assertThat(cors.origin(), CoreMatchers.is(CoreMatchers.equalTo("http://localhost:7888")));
        MatcherAssert.assertThat(cors.isAnyOriginSupported(), CoreMatchers.is(false));
    }

    @Test
    public void origins() {
        final String[] origins = new String[]{ "http://localhost:7888", "https://localhost:7888" };
        final CorsConfig cors = CorsConfigBuilder.forOrigins(origins).build();
        MatcherAssert.assertThat(cors.origins(), CoreMatchers.hasItems(origins));
        MatcherAssert.assertThat(cors.isAnyOriginSupported(), CoreMatchers.is(false));
    }

    @Test
    public void exposeHeaders() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().exposeHeaders("custom-header1", "custom-header2").build();
        MatcherAssert.assertThat(cors.exposedHeaders(), CoreMatchers.hasItems("custom-header1", "custom-header2"));
    }

    @Test
    public void allowCredentials() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().allowCredentials().build();
        MatcherAssert.assertThat(cors.isCredentialsAllowed(), CoreMatchers.is(true));
    }

    @Test
    public void maxAge() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().maxAge(3000).build();
        MatcherAssert.assertThat(cors.maxAge(), CoreMatchers.is(3000L));
    }

    @Test
    public void requestMethods() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().allowedRequestMethods(POST, GET).build();
        MatcherAssert.assertThat(cors.allowedRequestMethods(), CoreMatchers.hasItems(POST, GET));
    }

    @Test
    public void requestHeaders() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().allowedRequestHeaders("preflight-header1", "preflight-header2").build();
        MatcherAssert.assertThat(cors.allowedRequestHeaders(), CoreMatchers.hasItems("preflight-header1", "preflight-header2"));
    }

    @Test
    public void preflightResponseHeadersSingleValue() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().preflightResponseHeader("SingleValue", "value").build();
        MatcherAssert.assertThat(cors.preflightResponseHeaders().get(HttpHeadersTestUtils.of("SingleValue")), CoreMatchers.equalTo("value"));
    }

    @Test
    public void preflightResponseHeadersMultipleValues() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().preflightResponseHeader("MultipleValues", "value1", "value2").build();
        MatcherAssert.assertThat(cors.preflightResponseHeaders().getAll(HttpHeadersTestUtils.of("MultipleValues")), CoreMatchers.hasItems("value1", "value2"));
    }

    @Test
    public void defaultPreflightResponseHeaders() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().build();
        MatcherAssert.assertThat(cors.preflightResponseHeaders().get(DATE), CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(cors.preflightResponseHeaders().get(CONTENT_LENGTH), CoreMatchers.is("0"));
    }

    @Test
    public void emptyPreflightResponseHeaders() {
        final CorsConfig cors = CorsConfigBuilder.forAnyOrigin().noPreflightResponseHeaders().build();
        MatcherAssert.assertThat(cors.preflightResponseHeaders(), CoreMatchers.equalTo(((HttpHeaders) (INSTANCE))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfValueIsNull() {
        CorsConfigBuilder.forOrigin("*").preflightResponseHeader("HeaderName", new Object[]{ null }).build();
    }

    @Test
    public void shortCircuit() {
        final CorsConfig cors = CorsConfigBuilder.forOrigin("http://localhost:8080").shortCircuit().build();
        MatcherAssert.assertThat(cors.isShortCircuit(), CoreMatchers.is(true));
    }
}


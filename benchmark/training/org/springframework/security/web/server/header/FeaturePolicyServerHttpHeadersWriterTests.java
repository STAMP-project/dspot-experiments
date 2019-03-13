/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.web.server.header;


import FeaturePolicyServerHttpHeadersWriter.FEATURE_POLICY;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;


/**
 * Tests for {@link FeaturePolicyServerHttpHeadersWriter}.
 *
 * @author Vedran Pavic
 */
public class FeaturePolicyServerHttpHeadersWriterTests {
    private static final String DEFAULT_POLICY_DIRECTIVES = "geolocation 'self'";

    private ServerWebExchange exchange;

    private FeaturePolicyServerHttpHeadersWriter writer;

    @Test
    public void writeHeadersWhenUsingDefaultsThenDoesNotWrite() {
        this.writer.writeHttpHeaders(this.exchange);
        HttpHeaders headers = this.exchange.getResponse().getHeaders();
        assertThat(headers).isEmpty();
    }

    @Test
    public void writeHeadersWhenUsingPolicyThenWritesPolicy() {
        this.writer.setPolicyDirectives(FeaturePolicyServerHttpHeadersWriterTests.DEFAULT_POLICY_DIRECTIVES);
        this.writer.writeHttpHeaders(this.exchange);
        HttpHeaders headers = this.exchange.getResponse().getHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers.get(FEATURE_POLICY)).containsOnly(FeaturePolicyServerHttpHeadersWriterTests.DEFAULT_POLICY_DIRECTIVES);
    }

    @Test
    public void writeHeadersWhenAlreadyWrittenThenWritesHeader() {
        this.writer.setPolicyDirectives(FeaturePolicyServerHttpHeadersWriterTests.DEFAULT_POLICY_DIRECTIVES);
        String headerValue = "camera: 'self'";
        this.exchange.getResponse().getHeaders().set(FEATURE_POLICY, headerValue);
        this.writer.writeHttpHeaders(this.exchange);
        HttpHeaders headers = this.exchange.getResponse().getHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers.get(FEATURE_POLICY)).containsOnly(headerValue);
    }
}


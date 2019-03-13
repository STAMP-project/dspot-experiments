/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.security.web.header.writers;


import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN;


/**
 *
 *
 * @author Edd? Mel?ndez
 * @author Ankur Pathak
 */
public class ReferrerPolicyHeaderWriterTests {
    private final String DEFAULT_REFERRER_POLICY = "no-referrer";

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private ReferrerPolicyHeaderWriter writer;

    private static final String REFERRER_POLICY_HEADER = "Referrer-Policy";

    @Test
    public void writeHeadersReferrerPolicyDefault() {
        this.writer.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeaderNames()).hasSize(1);
        assertThat(this.response.getHeader("Referrer-Policy")).isEqualTo(DEFAULT_REFERRER_POLICY);
    }

    @Test
    public void writeHeadersReferrerPolicyCustom() {
        this.writer = new ReferrerPolicyHeaderWriter(SAME_ORIGIN);
        this.writer.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeaderNames()).hasSize(1);
        assertThat(this.response.getHeader("Referrer-Policy")).isEqualTo("same-origin");
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeHeaderReferrerPolicyInvalid() {
        this.writer = new ReferrerPolicyHeaderWriter(null);
    }

    @Test
    public void writeHeaderWhenNotPresent() {
        String value = new String("value");
        this.response.setHeader(ReferrerPolicyHeaderWriterTests.REFERRER_POLICY_HEADER, value);
        this.writer.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeader(ReferrerPolicyHeaderWriterTests.REFERRER_POLICY_HEADER)).isSameAs(value);
    }
}


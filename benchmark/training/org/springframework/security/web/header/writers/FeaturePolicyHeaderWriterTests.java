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


/**
 * Tests for {@link FeaturePolicyHeaderWriter}.
 *
 * @author Vedran Pavic
 * @author Ankur Pathak
 */
public class FeaturePolicyHeaderWriterTests {
    private static final String DEFAULT_POLICY_DIRECTIVES = "geolocation 'self'";

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private FeaturePolicyHeaderWriter writer;

    private static final String FEATURE_POLICY_HEADER = "Feature-Policy";

    @Test
    public void writeHeadersFeaturePolicyDefault() {
        writer.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeaderNames()).hasSize(1);
        assertThat(this.response.getHeader("Feature-Policy")).isEqualTo(FeaturePolicyHeaderWriterTests.DEFAULT_POLICY_DIRECTIVES);
    }

    @Test
    public void createWriterWithNullDirectivesShouldThrowException() {
        assertThatThrownBy(() -> new FeaturePolicyHeaderWriter(null)).isInstanceOf(IllegalArgumentException.class).hasMessage("policyDirectives must not be null or empty");
    }

    @Test
    public void createWriterWithEmptyDirectivesShouldThrowException() {
        assertThatThrownBy(() -> new FeaturePolicyHeaderWriter("")).isInstanceOf(IllegalArgumentException.class).hasMessage("policyDirectives must not be null or empty");
    }

    @Test
    public void writeHeaderOnlyIfNotPresent() {
        String value = new String("value");
        this.response.setHeader(FeaturePolicyHeaderWriterTests.FEATURE_POLICY_HEADER, value);
        this.writer.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeader(FeaturePolicyHeaderWriterTests.FEATURE_POLICY_HEADER)).isSameAs(value);
    }
}


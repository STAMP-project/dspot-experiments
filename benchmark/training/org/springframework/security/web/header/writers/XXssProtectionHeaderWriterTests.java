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
 *
 *
 * @author Rob Winch
 * @author Ankur Pathak
 */
public class XXssProtectionHeaderWriterTests {
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private XXssProtectionHeaderWriter writer;

    private static final String XSS_PROTECTION_HEADER = "X-XSS-Protection";

    @Test
    public void writeHeaders() {
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeaderValues("X-XSS-Protection")).containsOnly("1; mode=block");
    }

    @Test
    public void writeHeadersNoBlock() {
        writer.setBlock(false);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeaderValues("X-XSS-Protection")).containsOnly("1");
    }

    @Test
    public void writeHeadersDisabled() {
        writer.setBlock(false);
        writer.setEnabled(false);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeaderValues("X-XSS-Protection")).containsOnly("0");
    }

    @Test
    public void setEnabledFalseWithBlockTrue() {
        writer.setEnabled(false);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeaderValues("X-XSS-Protection")).containsOnly("0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBlockTrueWithEnabledFalse() {
        writer.setBlock(false);
        writer.setEnabled(false);
        writer.setBlock(true);
    }

    @Test
    public void writeHeaderWhenNotPresent() {
        String value = new String("value");
        this.response.setHeader(XXssProtectionHeaderWriterTests.XSS_PROTECTION_HEADER, value);
        this.writer.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeader(XXssProtectionHeaderWriterTests.XSS_PROTECTION_HEADER)).isSameAs(value);
    }
}


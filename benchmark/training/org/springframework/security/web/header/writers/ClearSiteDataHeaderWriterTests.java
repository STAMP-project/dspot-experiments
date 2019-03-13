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


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Rafiullah Hamedy
 * @author Josh Cummings
 * @see {@link ClearSiteDataHeaderWriter}
 */
public class ClearSiteDataHeaderWriterTests {
    private static final String HEADER_NAME = "Clear-Site-Data";

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createInstanceWhenMissingSourceThenThrowsException() {
        this.thrown.expect(Exception.class);
        this.thrown.expectMessage("sources cannot be empty or null");
        new ClearSiteDataHeaderWriter();
    }

    @Test
    public void writeHeaderWhenRequestNotSecureThenHeaderIsNotPresent() {
        this.request.setSecure(false);
        ClearSiteDataHeaderWriter headerWriter = new ClearSiteDataHeaderWriter("cache");
        headerWriter.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeader(ClearSiteDataHeaderWriterTests.HEADER_NAME)).isNull();
    }

    @Test
    public void writeHeaderWhenRequestIsSecureThenHeaderValueMatchesPassedSource() {
        ClearSiteDataHeaderWriter headerWriter = new ClearSiteDataHeaderWriter("storage");
        headerWriter.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeader(ClearSiteDataHeaderWriterTests.HEADER_NAME)).isEqualTo("\"storage\"");
    }

    @Test
    public void writeHeaderWhenRequestIsSecureThenHeaderValueMatchesPassedSources() {
        ClearSiteDataHeaderWriter headerWriter = new ClearSiteDataHeaderWriter("cache", "cookies", "storage", "executionContexts");
        headerWriter.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeader(ClearSiteDataHeaderWriterTests.HEADER_NAME)).isEqualTo("\"cache\", \"cookies\", \"storage\", \"executionContexts\"");
    }
}


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


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Tim Ysewyn
 * @author Ankur Pathak
 */
public class HpkpHeaderWriterTests {
    private static final Map<String, String> DEFAULT_PINS;

    static {
        Map<String, String> defaultPins = new LinkedHashMap<>();
        defaultPins.put("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "sha256");
        DEFAULT_PINS = Collections.unmodifiableMap(defaultPins);
    }

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private HpkpHeaderWriter writer;

    private static final String HPKP_HEADER_NAME = "Public-Key-Pins";

    private static final String HPKP_RO_HEADER_NAME = "Public-Key-Pins-Report-Only";

    @Test
    public void writeHeadersDefaultValues() {
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo("max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"");
    }

    @Test
    public void maxAgeCustomConstructorWriteHeaders() {
        writer = new HpkpHeaderWriter(2592000);
        writer.setPins(HpkpHeaderWriterTests.DEFAULT_PINS);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo("max-age=2592000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"");
    }

    @Test
    public void maxAgeAndIncludeSubdomainsCustomConstructorWriteHeaders() {
        writer = new HpkpHeaderWriter(2592000, true);
        writer.setPins(HpkpHeaderWriterTests.DEFAULT_PINS);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo("max-age=2592000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; includeSubDomains");
    }

    @Test
    public void allArgsCustomConstructorWriteHeaders() {
        writer = new HpkpHeaderWriter(2592000, true, false);
        writer.setPins(HpkpHeaderWriterTests.DEFAULT_PINS);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins")).isEqualTo("max-age=2592000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; includeSubDomains");
    }

    @Test
    public void writeHeadersCustomMaxAgeInSeconds() {
        writer.setMaxAgeInSeconds(2592000);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo("max-age=2592000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"");
    }

    @Test
    public void writeHeadersIncludeSubDomains() {
        writer.setIncludeSubDomains(true);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo("max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; includeSubDomains");
    }

    @Test
    public void writeHeadersTerminateConnection() {
        writer.setReportOnly(false);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins")).isEqualTo("max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"");
    }

    @Test
    public void writeHeadersTerminateConnectionWithURI() throws URISyntaxException {
        writer.setReportOnly(false);
        writer.setReportUri(new URI("http://example.com/pkp-report"));
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins")).isEqualTo("max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; report-uri=\"http://example.com/pkp-report\"");
    }

    @Test
    public void writeHeadersTerminateConnectionWithURIAsString() throws URISyntaxException {
        writer.setReportOnly(false);
        writer.setReportUri("http://example.com/pkp-report");
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins")).isEqualTo("max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; report-uri=\"http://example.com/pkp-report\"");
    }

    @Test
    public void writeHeadersAddSha256Pins() {
        writer.addSha256Pins("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "E9CZ9INDbd+2eRQozYqqbQ2yXLVKB9+xcprMF+44U1g=");
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(1);
        assertThat(response.getHeader("Public-Key-Pins-Report-Only")).isEqualTo("max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; pin-sha256=\"E9CZ9INDbd+2eRQozYqqbQ2yXLVKB9+xcprMF+44U1g=\"");
    }

    @Test
    public void writeHeadersInsecureRequestDoesNotWriteHeader() {
        request.setSecure(false);
        writer.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMaxAgeInSecondsToNegative() {
        writer.setMaxAgeInSeconds((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSha256PinsWithNullPin() {
        writer.addSha256Pins("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIncorrectReportUri() {
        writer.setReportUri("some url here...");
    }

    @Test
    public void writePublicKeyPinsHeaderOnlyWhenNotPresent() {
        String value = new String("value");
        this.response.setHeader(HpkpHeaderWriterTests.HPKP_HEADER_NAME, value);
        this.writer.setReportOnly(false);
        this.writer.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeader(HpkpHeaderWriterTests.HPKP_HEADER_NAME)).isSameAs(value);
    }

    @Test
    public void writePublicKeyPinsReportOnlyHeaderWhenNotPresent() {
        String value = new String("value");
        this.response.setHeader(HpkpHeaderWriterTests.HPKP_RO_HEADER_NAME, value);
        this.writer.setReportOnly(false);
        this.writer.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeader(HpkpHeaderWriterTests.HPKP_RO_HEADER_NAME)).isSameAs(value);
    }
}


/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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
package org.springframework.security.web.access.channel;


import javax.servlet.FilterChain;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;


/**
 * Tests {@link InsecureChannelProcessor}.
 *
 * @author Ben Alex
 */
public class InsecureChannelProcessorTests {
    @Test
    public void testDecideDetectsAcceptableChannel() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString("info=true");
        request.setServerName("localhost");
        request.setContextPath("/bigapp");
        request.setServletPath("/servlet");
        request.setScheme("http");
        request.setServerPort(8080);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterInvocation fi = new FilterInvocation(request, response, Mockito.mock(FilterChain.class));
        InsecureChannelProcessor processor = new InsecureChannelProcessor();
        processor.decide(fi, SecurityConfig.createList("SOME_IGNORED_ATTRIBUTE", "REQUIRES_INSECURE_CHANNEL"));
        assertThat(fi.getResponse().isCommitted()).isFalse();
    }

    @Test
    public void testDecideDetectsUnacceptableChannel() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString("info=true");
        request.setServerName("localhost");
        request.setContextPath("/bigapp");
        request.setServletPath("/servlet");
        request.setScheme("https");
        request.setSecure(true);
        request.setServerPort(8443);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterInvocation fi = new FilterInvocation(request, response, Mockito.mock(FilterChain.class));
        InsecureChannelProcessor processor = new InsecureChannelProcessor();
        processor.decide(fi, SecurityConfig.createList(new String[]{ "SOME_IGNORED_ATTRIBUTE", "REQUIRES_INSECURE_CHANNEL" }));
        assertThat(fi.getResponse().isCommitted()).isTrue();
    }

    @Test
    public void testDecideRejectsNulls() throws Exception {
        InsecureChannelProcessor processor = new InsecureChannelProcessor();
        processor.afterPropertiesSet();
        try {
            processor.decide(null, null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGettersSetters() {
        InsecureChannelProcessor processor = new InsecureChannelProcessor();
        assertThat(processor.getInsecureKeyword()).isEqualTo("REQUIRES_INSECURE_CHANNEL");
        processor.setInsecureKeyword("X");
        assertThat(processor.getInsecureKeyword()).isEqualTo("X");
        assertThat(((processor.getEntryPoint()) != null)).isTrue();
        processor.setEntryPoint(null);
        assertThat(((processor.getEntryPoint()) == null)).isTrue();
    }

    @Test
    public void testMissingEntryPoint() throws Exception {
        InsecureChannelProcessor processor = new InsecureChannelProcessor();
        processor.setEntryPoint(null);
        try {
            processor.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage()).isEqualTo("entryPoint required");
        }
    }

    @Test
    public void testMissingSecureChannelKeyword() throws Exception {
        InsecureChannelProcessor processor = new InsecureChannelProcessor();
        processor.setInsecureKeyword(null);
        try {
            processor.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage()).isEqualTo("insecureKeyword required");
        }
        processor.setInsecureKeyword("");
        try {
            processor.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage()).isEqualTo("insecureKeyword required");
        }
    }

    @Test
    public void testSupports() {
        InsecureChannelProcessor processor = new InsecureChannelProcessor();
        assertThat(processor.supports(new SecurityConfig("REQUIRES_INSECURE_CHANNEL"))).isTrue();
        assertThat(processor.supports(null)).isFalse();
        assertThat(processor.supports(new SecurityConfig("NOT_SUPPORTED"))).isFalse();
    }
}


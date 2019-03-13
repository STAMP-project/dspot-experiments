/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.openid;


import java.net.URI;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class OpenIDAuthenticationFilterTests {
    OpenIDAuthenticationFilter filter;

    private static final String REDIRECT_URL = "http://www.example.com/redirect";

    private static final String CLAIMED_IDENTITY_URL = "http://www.example.com/identity";

    private static final String REQUEST_PATH = "/login/openid";

    private static final String FILTER_PROCESS_URL = "http://localhost:8080" + (OpenIDAuthenticationFilterTests.REQUEST_PATH);

    private static final String DEFAULT_TARGET_URL = OpenIDAuthenticationFilterTests.FILTER_PROCESS_URL;

    @Test
    public void testFilterOperation() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath(OpenIDAuthenticationFilterTests.REQUEST_PATH);
        req.setRequestURI(OpenIDAuthenticationFilterTests.REQUEST_PATH);
        req.setServerPort(8080);
        MockHttpServletResponse response = new MockHttpServletResponse();
        req.setParameter("openid_identifier", (" " + (OpenIDAuthenticationFilterTests.CLAIMED_IDENTITY_URL)));
        req.setRemoteHost("www.example.com");
        filter.setConsumer(new MockOpenIDConsumer() {
            public String beginConsumption(HttpServletRequest req, String claimedIdentity, String returnToUrl, String realm) throws OpenIDConsumerException {
                assertThat(claimedIdentity).isEqualTo(OpenIDAuthenticationFilterTests.CLAIMED_IDENTITY_URL);
                assertThat(returnToUrl).isEqualTo(OpenIDAuthenticationFilterTests.DEFAULT_TARGET_URL);
                assertThat(realm).isEqualTo("http://localhost:8080/");
                return OpenIDAuthenticationFilterTests.REDIRECT_URL;
            }
        });
        FilterChain fc = Mockito.mock(FilterChain.class);
        filter.doFilter(req, response, fc);
        assertThat(response.getRedirectedUrl()).isEqualTo(OpenIDAuthenticationFilterTests.REDIRECT_URL);
        // Filter chain shouldn't proceed
        Mockito.verify(fc, Mockito.never()).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    /**
     * Tests that the filter encodes any query parameters on the return_to URL.
     */
    @Test
    public void encodesUrlParameters() throws Exception {
        // Arbitrary parameter name and value that will both need to be encoded:
        String paramName = "foo&bar";
        String paramValue = "http://example.com/path?a=b&c=d";
        MockHttpServletRequest req = new MockHttpServletRequest("GET", OpenIDAuthenticationFilterTests.REQUEST_PATH);
        req.addParameter(paramName, paramValue);
        filter.setReturnToUrlParameters(Collections.singleton(paramName));
        URI returnTo = new URI(filter.buildReturnToUrl(req));
        String query = returnTo.getRawQuery();
        assertThat(OpenIDAuthenticationFilterTests.count(query, '=')).isEqualTo(1);
        assertThat(OpenIDAuthenticationFilterTests.count(query, '&')).isZero();
    }
}


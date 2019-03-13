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
package org.springframework.security.web.server.csrf;


import java.time.Duration;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static CookieServerCsrfTokenRepository.DEFAULT_CSRF_COOKIE_NAME;
import static CookieServerCsrfTokenRepository.DEFAULT_CSRF_HEADER_NAME;
import static CookieServerCsrfTokenRepository.DEFAULT_CSRF_PARAMETER_NAME;


/**
 *
 *
 * @author Eric Deandrea
 * @since 5.1
 */
public class CookieServerCsrfTokenRepositoryTests {
    private MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/someUri"));

    private CookieServerCsrfTokenRepository csrfTokenRepository = new CookieServerCsrfTokenRepository();

    private String expectedHeaderName = DEFAULT_CSRF_HEADER_NAME;

    private String expectedParameterName = DEFAULT_CSRF_PARAMETER_NAME;

    private Duration expectedMaxAge = Duration.ofSeconds((-1));

    private String expectedDomain = null;

    private String expectedPath = "/";

    private boolean expectedSecure = false;

    private boolean expectedHttpOnly = true;

    private String expectedCookieName = DEFAULT_CSRF_COOKIE_NAME;

    private String expectedCookieValue = "csrfToken";

    @Test
    public void generateTokenWhenDefaultThenDefaults() {
        generateTokenAndAssertExpectedValues();
    }

    @Test
    public void generateTokenWhenCustomHeaderThenCustomHeader() {
        setExpectedHeaderName("someHeader");
        generateTokenAndAssertExpectedValues();
    }

    @Test
    public void generateTokenWhenCustomParameterThenCustomParameter() {
        setExpectedParameterName("someParam");
        generateTokenAndAssertExpectedValues();
    }

    @Test
    public void generateTokenWhenCustomHeaderAndParameterThenCustomHeaderAndParameter() {
        setExpectedHeaderName("someHeader");
        setExpectedParameterName("someParam");
        generateTokenAndAssertExpectedValues();
    }

    @Test
    public void saveTokenWhenNoSubscriptionThenNotWritten() {
        this.csrfTokenRepository.saveToken(this.exchange, createToken());
        assertThat(this.exchange.getResponse().getCookies().getFirst(this.expectedCookieName)).isNull();
    }

    @Test
    public void saveTokenWhenDefaultThenDefaults() {
        saveAndAssertExpectedValues(createToken());
    }

    @Test
    public void saveTokenWhenNullThenDeletes() {
        saveAndAssertExpectedValues(null);
    }

    @Test
    public void saveTokenWhenHttpOnlyFalseThenHttpOnlyFalse() {
        setExpectedHttpOnly(false);
        saveAndAssertExpectedValues(createToken());
    }

    @Test
    public void saveTokenWhenCustomPropertiesThenCustomProperties() {
        setExpectedDomain(".spring.io");
        setExpectedCookieName("csrfCookie");
        setExpectedPath("/some/path");
        setExpectedHeaderName("headerName");
        setExpectedParameterName("paramName");
        saveAndAssertExpectedValues(createToken());
    }

    @Test
    public void loadTokenWhenCookieExistThenTokenFound() {
        loadAndAssertExpectedValues();
    }

    @Test
    public void loadTokenWhenCustomThenTokenFound() {
        setExpectedParameterName("paramName");
        setExpectedHeaderName("headerName");
        setExpectedCookieName("csrfCookie");
        saveAndAssertExpectedValues(createToken());
    }

    @Test
    public void loadTokenWhenNoCookiesThenNullToken() {
        CsrfToken csrfToken = this.csrfTokenRepository.loadToken(this.exchange).block();
        assertThat(csrfToken).isNull();
    }

    @Test
    public void loadTokenWhenCookieExistsWithNoValue() {
        setExpectedCookieValue("");
        loadAndAssertExpectedValues();
    }

    @Test
    public void loadTokenWhenCookieExistsWithNullValue() {
        setExpectedCookieValue(null);
        loadAndAssertExpectedValues();
    }
}


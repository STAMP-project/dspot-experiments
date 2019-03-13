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
package org.springframework.security.config.web.server;


import ContentSecurityPolicyServerHttpHeadersWriter.CONTENT_SECURITY_POLICY;
import ContentTypeOptionsServerHttpHeadersWriter.X_CONTENT_OPTIONS;
import FeaturePolicyServerHttpHeadersWriter.FEATURE_POLICY;
import HttpHeaders.CACHE_CONTROL;
import HttpHeaders.EXPIRES;
import HttpHeaders.PRAGMA;
import ReferrerPolicy.NO_REFERRER;
import ReferrerPolicy.NO_REFERRER_WHEN_DOWNGRADE;
import ReferrerPolicyServerHttpHeadersWriter.REFERRER_POLICY;
import ServerHttpSecurity.HeaderSpec;
import StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY;
import XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN;
import XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.springframework.http.HttpHeaders;


/**
 * Tests for {@link ServerHttpSecurity.HeaderSpec}.
 *
 * @author Rob Winch
 * @author Vedran Pavic
 * @since 5.0
 */
public class HeaderSpecTests {
    private HeaderSpec headers = ServerHttpSecurity.http().headers();

    private HttpHeaders expectedHeaders = new HttpHeaders();

    private Set<String> headerNamesNotPresent = new HashSet<>();

    @Test
    public void headersWhenDisableThenNoSecurityHeaders() {
        new HashSet(this.expectedHeaders.keySet()).forEach(this::expectHeaderNamesNotPresent);
        this.headers.disable();
        assertHeaders();
    }

    @Test
    public void headersWhenDisableAndInvokedExplicitlyThenDefautsUsed() {
        this.headers.disable().headers();
        assertHeaders();
    }

    @Test
    public void headersWhenDefaultsThenAllDefaultsWritten() {
        assertHeaders();
    }

    @Test
    public void headersWhenCacheDisableThenCacheNotWritten() {
        expectHeaderNamesNotPresent(CACHE_CONTROL, PRAGMA, EXPIRES);
        this.headers.cache().disable();
        assertHeaders();
    }

    @Test
    public void headersWhenContentOptionsDisableThenContentTypeOptionsNotWritten() {
        expectHeaderNamesNotPresent(X_CONTENT_OPTIONS);
        this.headers.contentTypeOptions().disable();
        assertHeaders();
    }

    @Test
    public void headersWhenHstsDisableThenHstsNotWritten() {
        expectHeaderNamesNotPresent(STRICT_TRANSPORT_SECURITY);
        this.headers.hsts().disable();
        assertHeaders();
    }

    @Test
    public void headersWhenHstsCustomThenCustomHstsWritten() {
        this.expectedHeaders.remove(STRICT_TRANSPORT_SECURITY);
        this.expectedHeaders.add(STRICT_TRANSPORT_SECURITY, "max-age=60");
        this.headers.hsts().maxAge(Duration.ofSeconds(60)).includeSubdomains(false);
        assertHeaders();
    }

    @Test
    public void headersWhenHstsCustomWithPreloadThenCustomHstsWritten() {
        this.expectedHeaders.remove(STRICT_TRANSPORT_SECURITY);
        this.expectedHeaders.add(STRICT_TRANSPORT_SECURITY, "max-age=60 ; includeSubDomains ; preload");
        this.headers.hsts().maxAge(Duration.ofSeconds(60)).preload(true);
        assertHeaders();
    }

    @Test
    public void headersWhenFrameOptionsDisableThenFrameOptionsNotWritten() {
        expectHeaderNamesNotPresent(X_FRAME_OPTIONS);
        this.headers.frameOptions().disable();
        assertHeaders();
    }

    @Test
    public void headersWhenFrameOptionsModeThenFrameOptionsCustomMode() {
        this.expectedHeaders.set(X_FRAME_OPTIONS, "SAMEORIGIN");
        this.headers.frameOptions().mode(SAMEORIGIN);
        assertHeaders();
    }

    @Test
    public void headersWhenXssProtectionDisableThenXssProtectionNotWritten() {
        expectHeaderNamesNotPresent("X-Xss-Protection");
        this.headers.xssProtection().disable();
        assertHeaders();
    }

    @Test
    public void headersWhenFeaturePolicyEnabledThenFeaturePolicyWritten() {
        String policyDirectives = "Feature-Policy";
        this.expectedHeaders.add(FEATURE_POLICY, policyDirectives);
        this.headers.featurePolicy(policyDirectives);
        assertHeaders();
    }

    @Test
    public void headersWhenContentSecurityPolicyEnabledThenFeaturePolicyWritten() {
        String policyDirectives = "default-src 'self'";
        this.expectedHeaders.add(CONTENT_SECURITY_POLICY, policyDirectives);
        this.headers.contentSecurityPolicy(policyDirectives);
        assertHeaders();
    }

    @Test
    public void headersWhenReferrerPolicyEnabledThenFeaturePolicyWritten() {
        this.expectedHeaders.add(REFERRER_POLICY, NO_REFERRER.getPolicy());
        this.headers.referrerPolicy();
        assertHeaders();
    }

    @Test
    public void headersWhenReferrerPolicyCustomEnabledThenFeaturePolicyCustomWritten() {
        this.expectedHeaders.add(REFERRER_POLICY, NO_REFERRER_WHEN_DOWNGRADE.getPolicy());
        this.headers.referrerPolicy(NO_REFERRER_WHEN_DOWNGRADE);
        assertHeaders();
    }
}


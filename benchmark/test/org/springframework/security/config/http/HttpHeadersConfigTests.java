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
package org.springframework.security.config.http;


import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionStoreException;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 *
 * @author Rob Winch
 * @author Tim Ysewyn
 * @author Josh Cummings
 */
public class HttpHeadersConfigTests {
    private static final String CONFIG_LOCATION_PREFIX = "classpath:org/springframework/security/config/http/HttpHeadersConfigTests";

    static final Map<String, String> defaultHeaders = ImmutableMap.<String, String>builder().put("X-Content-Type-Options", "nosniff").put("X-Frame-Options", "DENY").put("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains").put("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate").put("Expires", "0").put("Pragma", "no-cache").put("X-XSS-Protection", "1; mode=block").build();

    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    MockMvc mvc;

    @Test
    public void requestWhenHeadersDisabledThenResponseExcludesAllSecureHeaders() throws Exception {
        this.spring.configLocations(this.xml("HeadersDisabled")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void configureWhenHeadersDisabledHavingChildElementThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("HeadersDisabledHavingChildElement")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("Cannot specify <headers disabled=\"true\"> with child elements");
    }

    @Test
    public void requestWhenHeadersEnabledThenResponseContainsAllSecureHeaders() throws Exception {
        this.spring.configLocations(this.xml("DefaultConfig")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includesDefaults());
    }

    @Test
    public void requestWhenHeadersElementUsedThenResponseContainsAllSecureHeaders() throws Exception {
        this.spring.configLocations(this.xml("HeadersEnabled")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includesDefaults());
    }

    @Test
    public void requestWhenFrameOptionsConfiguredThenIncludesHeader() throws Exception {
        Map<String, String> headers = new HashMap(HttpHeadersConfigTests.defaultHeaders);
        headers.put("X-Frame-Options", "SAMEORIGIN");
        this.spring.configLocations(this.xml("WithFrameOptions")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(headers));
    }

    // -- defaults disabled
    /**
     * gh-3986
     */
    @Test
    public void requestWhenDefaultsDisabledWithNoOverrideThenExcludesAllSecureHeaders() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithNoOverride")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void requestWhenUsingContentTypeOptionsThenDefaultsToNoSniff() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-Content-Type-Options");
        this.spring.configLocations(this.xml("DefaultsDisabledWithContentTypeOptions")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-Content-Type-Options", "nosniff")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void requestWhenUsingFrameOptionsThenDefaultsToDeny() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-Frame-Options");
        this.spring.configLocations(this.xml("DefaultsDisabledWithFrameOptions")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-Frame-Options", "DENY")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void requestWhenUsingFrameOptionsDenyThenRespondsWithDeny() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-Frame-Options");
        this.spring.configLocations(this.xml("DefaultsDisabledWithFrameOptionsDeny")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-Frame-Options", "DENY")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void requestWhenUsingFrameOptionsSameOriginThenRespondsWithSameOrigin() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-Frame-Options");
        this.spring.configLocations(this.xml("DefaultsDisabledWithFrameOptionsSameOrigin")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-Frame-Options", "SAMEORIGIN")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void configureWhenUsingFrameOptionsAllowFromNoOriginThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("DefaultsDisabledWithFrameOptionsAllowFromNoOrigin")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("Strategy requires a 'value' to be set.");// FIXME better error message?

    }

    @Test
    public void configureWhenUsingFrameOptionsAllowFromBlankOriginThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("DefaultsDisabledWithFrameOptionsAllowFromBlankOrigin")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("Strategy requires a 'value' to be set.");// FIXME better error message?

    }

    @Test
    public void requestWhenUsingFrameOptionsAllowFromThenRespondsWithAllowFrom() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-Frame-Options");
        this.spring.configLocations(this.xml("DefaultsDisabledWithFrameOptionsAllowFrom")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-Frame-Options", "ALLOW-FROM https://example.org")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void requestWhenUsingFrameOptionsAllowFromWhitelistThenRespondsWithAllowFrom() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-Frame-Options");
        this.spring.configLocations(this.xml("DefaultsDisabledWithFrameOptionsAllowFromWhitelist")).autowire();
        this.mvc.perform(get("/").param("from", "https://example.org")).andExpect(status().isOk()).andExpect(header().string("X-Frame-Options", "ALLOW-FROM https://example.org")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-Frame-Options", "DENY")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void requestWhenUsingCustomHeaderThenRespondsWithThatHeader() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithCustomHeader")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("a", "b")).andExpect(header().string("c", "d")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void requestWhenUsingCustomHeaderWriterThenRespondsWithThatHeader() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithCustomHeaderWriter")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("abc", "def")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void configureWhenUsingCustomHeaderNameOnlyThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("DefaultsDisabledWithOnlyHeaderName")).autowire()).isInstanceOf(BeanCreationException.class);
    }

    @Test
    public void configureWhenUsingCustomHeaderValueOnlyThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("DefaultsDisabledWithOnlyHeaderValue")).autowire()).isInstanceOf(BeanCreationException.class);
    }

    @Test
    public void requestWhenUsingXssProtectionThenDefaultsToModeBlock() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-XSS-Protection");
        this.spring.configLocations(this.xml("DefaultsDisabledWithXssProtection")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-XSS-Protection", "1; mode=block")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void requestWhenEnablingXssProtectionThenDefaultsToModeBlock() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-XSS-Protection");
        this.spring.configLocations(this.xml("DefaultsDisabledWithXssProtectionEnabled")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-XSS-Protection", "1; mode=block")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void requestWhenDisablingXssProtectionThenDefaultsToZero() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("X-XSS-Protection");
        this.spring.configLocations(this.xml("DefaultsDisabledWithXssProtectionDisabled")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("X-XSS-Protection", "0")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void configureWhenXssProtectionDisabledAndBlockSetThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("DefaultsDisabledWithXssProtectionDisabledAndBlockSet")).autowire()).isInstanceOf(BeanCreationException.class).hasMessageContaining("Cannot set block to true with enabled false");
    }

    @Test
    public void requestWhenUsingCacheControlThenRespondsWithCorrespondingHeaders() throws Exception {
        Map<String, String> includedHeaders = ImmutableMap.<String, String>builder().put("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate").put("Expires", "0").put("Pragma", "no-cache").build();
        this.spring.configLocations(this.xml("DefaultsDisabledWithCacheControl")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(includedHeaders));
    }

    @Test
    public void requestWhenUsingHstsThenRespondsWithHstsHeader() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("Strict-Transport-Security");
        this.spring.configLocations(this.xml("DefaultsDisabledWithHsts")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(header().string("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void insecureRequestWhenUsingHstsThenExcludesHstsHeader() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithHsts")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void insecureRequestWhenUsingCustomHstsRequestMatcherThenIncludesHstsHeader() throws Exception {
        Set<String> excludedHeaders = new HashSet<>(HttpHeadersConfigTests.defaultHeaders.keySet());
        excludedHeaders.remove("Strict-Transport-Security");
        this.spring.configLocations(this.xml("DefaultsDisabledWithCustomHstsRequestMatcher")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().string("Strict-Transport-Security", "max-age=1")).andExpect(HttpHeadersConfigTests.excludes(excludedHeaders));
    }

    @Test
    public void configureWhenUsingHpkpWithoutPinsThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("DefaultsDisabledWithEmptyHpkp")).autowire()).isInstanceOf(XmlBeanDefinitionStoreException.class).hasMessageContaining("The content of element 'hpkp' is not complete");
    }

    @Test
    public void configureWhenUsingHpkpWithEmptyPinsThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("DefaultsDisabledWithEmptyPins")).autowire()).isInstanceOf(XmlBeanDefinitionStoreException.class).hasMessageContaining("The content of element 'pins' is not complete");
    }

    @Test
    public void requestWhenUsingHpkpThenIncludesHpkpHeader() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithHpkp")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(header().string("Public-Key-Pins-Report-Only", "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void requestWhenUsingHpkpDefaultsThenIncludesHpkpHeaderUsingSha256() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithHpkpDefaults")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(header().string("Public-Key-Pins-Report-Only", "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void insecureRequestWhenUsingHpkpThenExcludesHpkpHeader() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithHpkpDefaults")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().doesNotExist("Public-Key-Pins-Report-Only")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void requestWhenUsingHpkpCustomMaxAgeThenIncludesHpkpHeaderAccordingly() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithHpkpMaxAge")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(header().string("Public-Key-Pins-Report-Only", "max-age=604800 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void requestWhenUsingHpkpReportThenIncludesHpkpHeaderAccordingly() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithHpkpReport")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(header().string("Public-Key-Pins", "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void requestWhenUsingHpkpIncludeSubdomainsThenIncludesHpkpHeaderAccordingly() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithHpkpIncludeSubdomains")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(header().string("Public-Key-Pins-Report-Only", "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; includeSubDomains")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    @Test
    public void requestWhenUsingHpkpReportUriThenIncludesHpkpHeaderAccordingly() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithHpkpReportUri")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(header().string("Public-Key-Pins-Report-Only", "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; report-uri=\"http://example.net/pkp-report\"")).andExpect(HttpHeadersConfigTests.excludesDefaults());
    }

    // -- single-header disabled
    @Test
    public void requestWhenCacheControlDisabledThenExcludesHeader() throws Exception {
        Collection<String> cacheControl = Arrays.asList("Cache-Control", "Expires", "Pragma");
        Map<String, String> allButCacheControl = HttpHeadersConfigTests.remove(HttpHeadersConfigTests.defaultHeaders, cacheControl);
        this.spring.configLocations(this.xml("CacheControlDisabled")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(allButCacheControl)).andExpect(HttpHeadersConfigTests.excludes(cacheControl));
    }

    @Test
    public void requestWhenContentTypeOptionsDisabledThenExcludesHeader() throws Exception {
        Collection<String> contentTypeOptions = Arrays.asList("X-Content-Type-Options");
        Map<String, String> allButContentTypeOptions = HttpHeadersConfigTests.remove(HttpHeadersConfigTests.defaultHeaders, contentTypeOptions);
        this.spring.configLocations(this.xml("ContentTypeOptionsDisabled")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(allButContentTypeOptions)).andExpect(HttpHeadersConfigTests.excludes(contentTypeOptions));
    }

    @Test
    public void requestWhenHstsDisabledThenExcludesHeader() throws Exception {
        Collection<String> hsts = Arrays.asList("Strict-Transport-Security");
        Map<String, String> allButHsts = HttpHeadersConfigTests.remove(HttpHeadersConfigTests.defaultHeaders, hsts);
        this.spring.configLocations(this.xml("HstsDisabled")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(allButHsts)).andExpect(HttpHeadersConfigTests.excludes(hsts));
    }

    @Test
    public void requestWhenHpkpDisabledThenExcludesHeader() throws Exception {
        this.spring.configLocations(this.xml("HpkpDisabled")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includesDefaults());
    }

    @Test
    public void requestWhenFrameOptionsDisabledThenExcludesHeader() throws Exception {
        Collection<String> frameOptions = Arrays.asList("X-Frame-Options");
        Map<String, String> allButFrameOptions = HttpHeadersConfigTests.remove(HttpHeadersConfigTests.defaultHeaders, frameOptions);
        this.spring.configLocations(this.xml("FrameOptionsDisabled")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(allButFrameOptions)).andExpect(HttpHeadersConfigTests.excludes(frameOptions));
    }

    @Test
    public void requestWhenXssProtectionDisabledThenExcludesHeader() throws Exception {
        Collection<String> xssProtection = Arrays.asList("X-XSS-Protection");
        Map<String, String> allButXssProtection = HttpHeadersConfigTests.remove(HttpHeadersConfigTests.defaultHeaders, xssProtection);
        this.spring.configLocations(this.xml("XssProtectionDisabled")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(allButXssProtection)).andExpect(HttpHeadersConfigTests.excludes(xssProtection));
    }

    // --- disable error handling ---
    @Test
    public void configureWhenHstsDisabledAndIncludeSubdomainsSpecifiedThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("HstsDisabledSpecifyingIncludeSubdomains")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("include-subdomains");
    }

    @Test
    public void configureWhenHstsDisabledAndMaxAgeSpecifiedThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("HstsDisabledSpecifyingMaxAge")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("max-age");
    }

    @Test
    public void configureWhenHstsDisabledAndRequestMatcherSpecifiedThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("HstsDisabledSpecifyingRequestMatcher")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("request-matcher-ref");
    }

    @Test
    public void configureWhenXssProtectionDisabledAndEnabledThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("XssProtectionDisabledAndEnabled")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("enabled");
    }

    @Test
    public void configureWhenXssProtectionDisabledAndBlockSpecifiedThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("XssProtectionDisabledSpecifyingBlock")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("block");
    }

    @Test
    public void configureWhenFrameOptionsDisabledAndPolicySpecifiedThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("FrameOptionsDisabledSpecifyingPolicy")).autowire()).isInstanceOf(BeanDefinitionParsingException.class).hasMessageContaining("policy");
    }

    @Test
    public void requestWhenContentSecurityPolicyDirectivesConfiguredThenIncludesDirectives() throws Exception {
        Map<String, String> includedHeaders = new HashMap<>(HttpHeadersConfigTests.defaultHeaders);
        includedHeaders.put("Content-Security-Policy", "default-src 'self'");
        this.spring.configLocations(this.xml("ContentSecurityPolicyWithPolicyDirectives")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(includedHeaders));
    }

    @Test
    public void requestWhenHeadersDisabledAndContentSecurityPolicyConfiguredThenExcludesHeader() throws Exception {
        this.spring.configLocations(this.xml("HeadersDisabledWithContentSecurityPolicy")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.excludesDefaults()).andExpect(HttpHeadersConfigTests.excludes("Content-Security-Policy"));
    }

    @Test
    public void requestWhenDefaultsDisabledAndContentSecurityPolicyConfiguredThenIncludesHeader() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithContentSecurityPolicy")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.excludesDefaults()).andExpect(header().string("Content-Security-Policy", "default-src 'self'"));
    }

    @Test
    public void configureWhenContentSecurityPolicyConfiguredWithEmptyDirectivesThenAutowireFails() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("ContentSecurityPolicyWithEmptyDirectives")).autowire()).isInstanceOf(BeanDefinitionParsingException.class);
    }

    @Test
    public void requestWhenContentSecurityPolicyConfiguredWithReportOnlyThenIncludesReportOnlyHeader() throws Exception {
        Map<String, String> includedHeaders = new HashMap<>(HttpHeadersConfigTests.defaultHeaders);
        includedHeaders.put("Content-Security-Policy-Report-Only", "default-src https:; report-uri https://example.org/");
        this.spring.configLocations(this.xml("ContentSecurityPolicyWithReportOnly")).autowire();
        this.mvc.perform(get("/").secure(true)).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.includes(includedHeaders));
    }

    @Test
    public void requestWhenReferrerPolicyConfiguredThenResponseDefaultsToNoReferrer() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithReferrerPolicy")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.excludesDefaults()).andExpect(header().string("Referrer-Policy", "no-referrer"));
    }

    @Test
    public void requestWhenReferrerPolicyConfiguredWithSameOriginThenRespondsWithSameOrigin() throws Exception {
        this.spring.configLocations(this.xml("DefaultsDisabledWithReferrerPolicySameOrigin")).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(HttpHeadersConfigTests.excludesDefaults()).andExpect(header().string("Referrer-Policy", "same-origin"));
    }

    @RestController
    public static class SimpleController {
        @GetMapping("/")
        public String ok() {
            return "ok";
        }
    }
}


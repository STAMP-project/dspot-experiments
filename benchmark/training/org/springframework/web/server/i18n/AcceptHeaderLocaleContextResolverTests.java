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
package org.springframework.web.server.i18n;


import HttpHeaders.ACCEPT_LANGUAGE;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;


/**
 * Unit tests for {@link AcceptHeaderLocaleContextResolver}.
 *
 * @author Sebastien Deleuze
 * @author Juergen Hoeller
 */
public class AcceptHeaderLocaleContextResolverTests {
    private final AcceptHeaderLocaleContextResolver resolver = new AcceptHeaderLocaleContextResolver();

    @Test
    public void resolve() {
        Assert.assertEquals(Locale.CANADA, this.resolver.resolveLocaleContext(exchange(Locale.CANADA)).getLocale());
        Assert.assertEquals(Locale.US, this.resolver.resolveLocaleContext(exchange(Locale.US, Locale.CANADA)).getLocale());
    }

    @Test
    public void resolvePreferredSupported() {
        this.resolver.setSupportedLocales(Collections.singletonList(Locale.CANADA));
        Assert.assertEquals(Locale.CANADA, this.resolver.resolveLocaleContext(exchange(Locale.US, Locale.CANADA)).getLocale());
    }

    @Test
    public void resolvePreferredNotSupported() {
        this.resolver.setSupportedLocales(Collections.singletonList(Locale.CANADA));
        Assert.assertEquals(Locale.US, this.resolver.resolveLocaleContext(exchange(Locale.US, Locale.UK)).getLocale());
    }

    @Test
    public void resolvePreferredNotSupportedWithDefault() {
        this.resolver.setSupportedLocales(Arrays.asList(Locale.US, Locale.JAPAN));
        this.resolver.setDefaultLocale(Locale.JAPAN);
        Assert.assertEquals(Locale.JAPAN, this.resolver.resolveLocaleContext(exchange(Locale.KOREA)).getLocale());
    }

    @Test
    public void resolvePreferredAgainstLanguageOnly() {
        this.resolver.setSupportedLocales(Collections.singletonList(Locale.ENGLISH));
        Assert.assertEquals(Locale.ENGLISH, this.resolver.resolveLocaleContext(exchange(Locale.GERMANY, Locale.US, Locale.UK)).getLocale());
    }

    @Test
    public void resolvePreferredAgainstCountryIfPossible() {
        this.resolver.setSupportedLocales(Arrays.asList(Locale.ENGLISH, Locale.UK));
        Assert.assertEquals(Locale.UK, this.resolver.resolveLocaleContext(exchange(Locale.GERMANY, Locale.US, Locale.UK)).getLocale());
    }

    @Test
    public void resolvePreferredAgainstLanguageWithMultipleSupportedLocales() {
        this.resolver.setSupportedLocales(Arrays.asList(Locale.GERMAN, Locale.US));
        Assert.assertEquals(Locale.GERMAN, this.resolver.resolveLocaleContext(exchange(Locale.GERMANY, Locale.US, Locale.UK)).getLocale());
    }

    @Test
    public void resolveMissingAcceptLanguageHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertNull(this.resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    public void resolveMissingAcceptLanguageHeaderWithDefault() {
        this.resolver.setDefaultLocale(Locale.US);
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertEquals(Locale.US, this.resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    public void resolveEmptyAcceptLanguageHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header(ACCEPT_LANGUAGE, "").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertNull(this.resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    public void resolveEmptyAcceptLanguageHeaderWithDefault() {
        this.resolver.setDefaultLocale(Locale.US);
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header(ACCEPT_LANGUAGE, "").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertEquals(Locale.US, this.resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    public void resolveInvalidAcceptLanguageHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header(ACCEPT_LANGUAGE, "en_US").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertNull(this.resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    public void resolveInvalidAcceptLanguageHeaderWithDefault() {
        this.resolver.setDefaultLocale(Locale.US);
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header(ACCEPT_LANGUAGE, "en_US").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertEquals(Locale.US, this.resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    public void defaultLocale() {
        this.resolver.setDefaultLocale(Locale.JAPANESE);
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertEquals(Locale.JAPANESE, this.resolver.resolveLocaleContext(exchange).getLocale());
        request = MockServerHttpRequest.get("/").acceptLanguageAsLocales(Locale.US).build();
        exchange = MockServerWebExchange.from(request);
        Assert.assertEquals(Locale.US, this.resolver.resolveLocaleContext(exchange).getLocale());
    }
}


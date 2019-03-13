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
package org.springframework.web.servlet.i18n;


import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;


/**
 * Unit tests for {@link AcceptHeaderLocaleResolver}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class AcceptHeaderLocaleResolverTests {
    private final AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();

    @Test
    public void resolve() {
        Assert.assertEquals(Locale.CANADA, this.resolver.resolveLocale(request(Locale.CANADA)));
        Assert.assertEquals(Locale.US, this.resolver.resolveLocale(request(Locale.US, Locale.CANADA)));
    }

    @Test
    public void resolvePreferredSupported() {
        this.resolver.setSupportedLocales(Collections.singletonList(Locale.CANADA));
        Assert.assertEquals(Locale.CANADA, this.resolver.resolveLocale(request(Locale.US, Locale.CANADA)));
    }

    @Test
    public void resolvePreferredNotSupported() {
        this.resolver.setSupportedLocales(Collections.singletonList(Locale.CANADA));
        Assert.assertEquals(Locale.US, this.resolver.resolveLocale(request(Locale.US, Locale.UK)));
    }

    @Test
    public void resolvePreferredAgainstLanguageOnly() {
        this.resolver.setSupportedLocales(Collections.singletonList(Locale.ENGLISH));
        Assert.assertEquals(Locale.ENGLISH, this.resolver.resolveLocale(request(Locale.GERMANY, Locale.US, Locale.UK)));
    }

    @Test
    public void resolvePreferredAgainstCountryIfPossible() {
        this.resolver.setSupportedLocales(Arrays.asList(Locale.ENGLISH, Locale.UK));
        Assert.assertEquals(Locale.UK, this.resolver.resolveLocale(request(Locale.GERMANY, Locale.US, Locale.UK)));
    }

    @Test
    public void resolvePreferredAgainstLanguageWithMultipleSupportedLocales() {
        this.resolver.setSupportedLocales(Arrays.asList(Locale.GERMAN, Locale.US));
        Assert.assertEquals(Locale.GERMAN, this.resolver.resolveLocale(request(Locale.GERMANY, Locale.US, Locale.UK)));
    }

    @Test
    public void resolvePreferredNotSupportedWithDefault() {
        this.resolver.setSupportedLocales(Arrays.asList(Locale.US, Locale.JAPAN));
        this.resolver.setDefaultLocale(Locale.JAPAN);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", Locale.KOREA.toLanguageTag());
        request.setPreferredLocales(Collections.singletonList(Locale.KOREA));
        Assert.assertEquals(Locale.JAPAN, this.resolver.resolveLocale(request));
    }

    @Test
    public void defaultLocale() {
        this.resolver.setDefaultLocale(Locale.JAPANESE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Assert.assertEquals(Locale.JAPANESE, this.resolver.resolveLocale(request));
        request.addHeader("Accept-Language", Locale.US.toLanguageTag());
        request.setPreferredLocales(Collections.singletonList(Locale.US));
        Assert.assertEquals(Locale.US, this.resolver.resolveLocale(request));
    }
}


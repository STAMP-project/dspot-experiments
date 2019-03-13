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


import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;


/**
 * Unit tests for {@link FixedLocaleContextResolver}.
 *
 * @author Sebastien Deleuze
 */
public class FixedLocaleContextResolverTests {
    @Test
    public void resolveDefaultLocale() {
        FixedLocaleContextResolver resolver = new FixedLocaleContextResolver();
        Assert.assertEquals(Locale.US, resolver.resolveLocaleContext(exchange()).getLocale());
        Assert.assertEquals(Locale.US, resolver.resolveLocaleContext(exchange(Locale.CANADA)).getLocale());
    }

    @Test
    public void resolveCustomizedLocale() {
        FixedLocaleContextResolver resolver = new FixedLocaleContextResolver(Locale.FRANCE);
        Assert.assertEquals(Locale.FRANCE, resolver.resolveLocaleContext(exchange()).getLocale());
        Assert.assertEquals(Locale.FRANCE, resolver.resolveLocaleContext(exchange(Locale.CANADA)).getLocale());
    }

    @Test
    public void resolveCustomizedAndTimeZoneLocale() {
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of("UTC"));
        FixedLocaleContextResolver resolver = new FixedLocaleContextResolver(Locale.FRANCE, timeZone);
        TimeZoneAwareLocaleContext context = ((TimeZoneAwareLocaleContext) (resolver.resolveLocaleContext(exchange())));
        Assert.assertEquals(Locale.FRANCE, context.getLocale());
        Assert.assertEquals(timeZone, context.getTimeZone());
    }
}


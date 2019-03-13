/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.context.i18n;


import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class LocaleContextHolderTests {
    @Test
    public void testSetLocaleContext() {
        LocaleContext lc = new SimpleLocaleContext(Locale.GERMAN);
        LocaleContextHolder.setLocaleContext(lc);
        Assert.assertSame(lc, LocaleContextHolder.getLocaleContext());
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        lc = new SimpleLocaleContext(Locale.GERMANY);
        LocaleContextHolder.setLocaleContext(lc);
        Assert.assertSame(lc, LocaleContextHolder.getLocaleContext());
        Assert.assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        LocaleContextHolder.resetLocaleContext();
        Assert.assertNull(LocaleContextHolder.getLocaleContext());
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
    }

    @Test
    public void testSetTimeZoneAwareLocaleContext() {
        LocaleContext lc = new SimpleTimeZoneAwareLocaleContext(Locale.GERMANY, TimeZone.getTimeZone("GMT+1"));
        LocaleContextHolder.setLocaleContext(lc);
        Assert.assertSame(lc, LocaleContextHolder.getLocaleContext());
        Assert.assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
        LocaleContextHolder.resetLocaleContext();
        Assert.assertNull(LocaleContextHolder.getLocaleContext());
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
    }

    @Test
    public void testSetLocale() {
        LocaleContextHolder.setLocale(Locale.GERMAN);
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        Assert.assertFalse(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocaleContext().getLocale());
        LocaleContextHolder.setLocale(Locale.GERMANY);
        Assert.assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        Assert.assertFalse(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(Locale.GERMANY, LocaleContextHolder.getLocaleContext().getLocale());
        LocaleContextHolder.setLocale(null);
        Assert.assertNull(LocaleContextHolder.getLocaleContext());
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        LocaleContextHolder.setDefaultLocale(Locale.GERMAN);
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
        LocaleContextHolder.setDefaultLocale(null);
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
    }

    @Test
    public void testSetTimeZone() {
        LocaleContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
        Assert.assertTrue(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertNull(LocaleContextHolder.getLocaleContext().getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), getTimeZone());
        LocaleContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+2"), LocaleContextHolder.getTimeZone());
        Assert.assertTrue(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertNull(LocaleContextHolder.getLocaleContext().getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+2"), getTimeZone());
        LocaleContextHolder.setTimeZone(null);
        Assert.assertNull(LocaleContextHolder.getLocaleContext());
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        LocaleContextHolder.setDefaultTimeZone(TimeZone.getTimeZone("GMT+1"));
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
        LocaleContextHolder.setDefaultTimeZone(null);
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
    }

    @Test
    public void testSetLocaleAndSetTimeZoneMixed() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        Assert.assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        Assert.assertFalse(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(Locale.GERMANY, LocaleContextHolder.getLocaleContext().getLocale());
        LocaleContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Assert.assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
        Assert.assertTrue(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(Locale.GERMANY, LocaleContextHolder.getLocaleContext().getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), getTimeZone());
        LocaleContextHolder.setLocale(Locale.GERMAN);
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
        Assert.assertTrue(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocaleContext().getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), getTimeZone());
        LocaleContextHolder.setTimeZone(null);
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        Assert.assertFalse(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocaleContext().getLocale());
        LocaleContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+2"), LocaleContextHolder.getTimeZone());
        Assert.assertTrue(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(Locale.GERMAN, LocaleContextHolder.getLocaleContext().getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+2"), getTimeZone());
        LocaleContextHolder.setLocale(null);
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+2"), LocaleContextHolder.getTimeZone());
        Assert.assertTrue(((LocaleContextHolder.getLocaleContext()) instanceof TimeZoneAwareLocaleContext));
        Assert.assertNull(LocaleContextHolder.getLocaleContext().getLocale());
        Assert.assertEquals(TimeZone.getTimeZone("GMT+2"), getTimeZone());
        LocaleContextHolder.setTimeZone(null);
        Assert.assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
        Assert.assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
        Assert.assertNull(LocaleContextHolder.getLocaleContext());
    }
}


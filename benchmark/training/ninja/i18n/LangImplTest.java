/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.i18n;


import NinjaConstant.applicationCookiePrefix;
import NinjaConstant.applicationLanguages;
import Result.SC_204_NO_CONTENT;
import java.util.Locale;
import java.util.Optional;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LangImplTest {
    @Mock
    private NinjaProperties ninjaProperties;

    @Mock
    private Context context;

    @Captor
    ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

    @Test
    public void testGetLanguage() {
        Cookie cookie = Cookie.builder(("NINJA_TEST" + (NinjaConstant.LANG_COOKIE_SUFFIX)), "de").build();
        Mockito.when(ninjaProperties.getOrDie(applicationCookiePrefix)).thenReturn("NINJA_TEST");
        Mockito.when(context.getCookie(("NINJA_TEST" + (NinjaConstant.LANG_COOKIE_SUFFIX)))).thenReturn(cookie);
        Lang lang = new LangImpl(ninjaProperties);
        // 1) with context and result => but result does not have a default lang
        Result result = Results.ok();
        Optional<String> language = lang.getLanguage(context, Optional.of(result));
        Assert.assertEquals("de", language.get());
        // 2) with context and result => result has already new lang set...
        result = Results.ok();
        cookie = Cookie.builder(("NINJA_TEST" + (NinjaConstant.LANG_COOKIE_SUFFIX)), "en").build();
        result.addCookie(cookie);
        language = lang.getLanguage(context, Optional.of(result));
        Assert.assertEquals("en", language.get());
    }

    @Test
    public void testChangeLanguage() {
        Cookie cookie = Cookie.builder(("NINJA_TEST" + (NinjaConstant.LANG_COOKIE_SUFFIX)), "de").build();
        Mockito.when(ninjaProperties.getOrDie(applicationCookiePrefix)).thenReturn("NINJA_TEST");
        Lang lang = new LangImpl(ninjaProperties);
        // test with result
        Result result = Results.noContent();
        result = lang.setLanguage("to", result);
        Assert.assertEquals("to", result.getCookie(cookie.getName()).getValue());
        Assert.assertEquals(SC_204_NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void testClearLanguage() {
        Cookie cookie = Cookie.builder(("NINJA_TEST" + (NinjaConstant.LANG_COOKIE_SUFFIX)), "de").build();
        Mockito.when(ninjaProperties.getOrDie(applicationCookiePrefix)).thenReturn("NINJA_TEST");
        Lang lang = new LangImpl(ninjaProperties);
        Result result = Results.ok();
        lang.clearLanguage(result);
        Cookie returnCookie = result.getCookie(cookie.getName());
        Assert.assertEquals("", returnCookie.getValue());
        Assert.assertEquals(0, returnCookie.getMaxAge());
    }

    @Test
    public void testIsLanguageDirectlySupportedByThisApplication() {
        Mockito.when(ninjaProperties.getOrDie(applicationCookiePrefix)).thenReturn("NINJA_TEST");
        Mockito.when(ninjaProperties.get(applicationLanguages)).thenReturn("en");
        Lang lang = new LangImpl(ninjaProperties);
        Assert.assertTrue(lang.isLanguageDirectlySupportedByThisApplication("en"));
        Assert.assertFalse(lang.isLanguageDirectlySupportedByThisApplication("de"));
        Mockito.when(ninjaProperties.get(applicationLanguages)).thenReturn("en, de, se");
        Assert.assertTrue(lang.isLanguageDirectlySupportedByThisApplication("en"));
        Assert.assertTrue(lang.isLanguageDirectlySupportedByThisApplication("de"));
        Assert.assertTrue(lang.isLanguageDirectlySupportedByThisApplication("se"));
        Assert.assertFalse(lang.isLanguageDirectlySupportedByThisApplication("tk"));
    }

    @Test
    public void testGetLocaleFromStringOrDefault() {
        // ONE DEFAULT LOCALE
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en" });
        Lang lang = new LangImpl(ninjaProperties);
        Optional<String> language = Optional.empty();
        Locale locale = lang.getLocaleFromStringOrDefault(language);
        Assert.assertEquals(Locale.ENGLISH, locale);
        // GERMAN LOCALE
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "de", "en" });
        lang = new LangImpl(ninjaProperties);
        language = Optional.empty();
        locale = lang.getLocaleFromStringOrDefault(language);
        Assert.assertEquals(Locale.GERMAN, locale);
        // GERMANY LOCALE
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "de-DE", "en" });
        lang = new LangImpl(ninjaProperties);
        language = Optional.empty();
        locale = lang.getLocaleFromStringOrDefault(language);
        Assert.assertEquals(Locale.GERMANY, locale);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetLocaleFromStringOrDefaultISEWhenNoApplicationLanguageDefined() {
        // ONE DEFAULT LOCALE
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{  });
        Lang lang = new LangImpl(ninjaProperties);
        Optional<String> language = Optional.empty();
        lang.getLocaleFromStringOrDefault(language);
        // ISE expected
    }
}


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


import NinjaConstant.applicationLanguages;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MessagesImplTest {
    @Mock
    private NinjaProperties ninjaProperties;

    @Mock
    Context context;

    Result result;

    @Test
    public void testGetWithLanguage() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        // that will refer to messages_en.properties:
        Assert.assertEquals("english", messages.get("language", Optional.of("en-US")).get());
        Assert.assertEquals("english", messages.get("language", Optional.of("en-CA")).get());
        Assert.assertEquals("english", messages.get("language", Optional.of("en-UK")).get());
        // that will refer to messages_de.properties:
        Assert.assertEquals("deutsch", messages.get("language", Optional.of("de")).get());
        Assert.assertEquals("deutsch", messages.get("language", Optional.of("de-DE")).get());
        // that will refer to messages_fr-FR.properties:
        Assert.assertEquals("fran?ais", messages.get("language", Optional.of("fr-FR")).get());
        // that will refer to messages_fr-FR.properties:
        Assert.assertEquals("fran?ais", messages.get("language", Optional.of("da,fr-FR;q=0.8")).get());
        Assert.assertEquals("fran?ais", messages.get("language", Optional.of("da;q=0.9, fr-FR; q=0.8")).get());
        // that will refer to messages_de.properties:
        Assert.assertEquals("deutsch", messages.get("language", Optional.of("de,fr-FR;q=0.8")).get());
        Assert.assertEquals("deutsch", messages.get("language", Optional.of("de;q=0.9, fr-FR; q=0.8")).get());
        Assert.assertEquals("defaultlanguage", messages.get("language", Optional.of("fr")).get());
        Assert.assertEquals(Optional.empty(), messages.get("a_non_existing_key", Optional.of("fr")));
    }

    @Test
    public void testGetWithContextAndResult() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        result = Results.ok();
        // test with context Accept Header
        Mockito.when(context.getAcceptLanguage()).thenReturn("en-US");
        Assert.assertEquals("english", messages.get("language", context, Optional.of(result)).get());
        Mockito.when(context.getAcceptLanguage()).thenReturn("en-CA");
        Assert.assertEquals("english", messages.get("language", context, Optional.of(result)).get());
        Mockito.when(context.getAcceptLanguage()).thenReturn("en-UK");
        Assert.assertEquals("english", messages.get("language", context, Optional.of(result)).get());
        // test that result overwrites context AcceptHeader
        lang.setLanguage("de", result);
        Assert.assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        Assert.assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());
        // that forced language from context works with empty result
        result = Results.ok();
        Mockito.when(context.getCookie(Mockito.anyString())).thenReturn(Cookie.builder("name", "fr-FR").build());
        Assert.assertEquals("fran?ais", messages.get("language", context, Optional.of(result)).get());
        // and the result overwrites it again...
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        Assert.assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());
    }

    @Test
    public void testGetWithLanguageAndParameters() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        // that will refer to messages_en.properties:
        Assert.assertEquals("this is the placeholder: test_parameter", messages.get("message_with_placeholder", Optional.of("en-US"), "test_parameter").get());
        Assert.assertEquals("this is the placeholder: test_parameter", messages.get("message_with_placeholder", Optional.of("en-CA"), "test_parameter").get());
        Assert.assertEquals("this is the placeholder: test_parameter", messages.get("message_with_placeholder", Optional.of("en-UK"), "test_parameter").get());
        // that will refer to messages_de.properties:
        Assert.assertEquals("Tor???? - das ist der platzhalter: test_parameter", messages.get("message_with_placeholder", Optional.of("de"), "test_parameter").get());
        Assert.assertEquals("Tor???? - das ist der platzhalter: test_parameter", messages.get("message_with_placeholder", Optional.of("de-DE"), "test_parameter").get());
    }

    @Test
    public void testGetWithContextAndResultAndParameters() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        result = Results.ok();
        Mockito.when(context.getAcceptLanguage()).thenReturn("en-US");
        Assert.assertEquals("this is the placeholder: test_parameter", messages.getWithDefault("message_with_placeholder", "default value", context, Optional.of(result), "test_parameter"));
        Mockito.when(context.getAcceptLanguage()).thenReturn("fr-FR");
        Assert.assertEquals("c'est le placeholder: test_parameter", messages.getWithDefault("message_with_placeholder", "default value", context, Optional.of(result), "test_parameter"));
        Mockito.when(context.getAcceptLanguage()).thenReturn("fr-FR");
        Assert.assertEquals("c'est le message default: test_parameter", messages.getWithDefault("i_do_not_exist", "c''est le message default: {0}", context, Optional.of(result), "test_parameter"));
    }

    @Test
    public void testGetWithDefaultAndLanguage() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        Assert.assertEquals("this is the placeholder: test_parameter", messages.getWithDefault("message_with_placeholder", "default value", Optional.of("en-US"), "test_parameter"));
        Assert.assertEquals("c'est le placeholder: test_parameter", messages.getWithDefault("message_with_placeholder", "default value", Optional.of("fr-FR"), "test_parameter"));
        Assert.assertEquals("c'est le message default: test_parameter", messages.getWithDefault("i_do_not_exist", "c''est le message default: {0}", Optional.of("fr-FR"), "test_parameter"));
    }

    @Test
    public void testGetWithDefaultAndContextAndResult() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        result = Results.ok();
        // test with context Accept Header
        Mockito.when(context.getAcceptLanguage()).thenReturn("en-US");
        // that will refer to messages_en.properties:
        Assert.assertEquals("this is the placeholder: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        Mockito.when(context.getAcceptLanguage()).thenReturn("en-CA");
        Assert.assertEquals("this is the placeholder: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        Mockito.when(context.getAcceptLanguage()).thenReturn("en-UK");
        Assert.assertEquals("this is the placeholder: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        // that will refer to messages_de.properties:
        lang.setLanguage("de", result);
        Assert.assertEquals("Tor???? - das ist der platzhalter: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        lang.setLanguage("de-DE", result);
        Assert.assertEquals("Tor???? - das ist der platzhalter: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        // that forced language from context works with empty result
        result = Results.ok();
        Mockito.when(context.getCookie(Mockito.anyString())).thenReturn(Cookie.builder("name", "fr-FR").build());
        Assert.assertEquals("c'est le placeholder: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        // and the result overwrites it again...
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        Assert.assertEquals("Tor???? - das ist der platzhalter: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
    }

    @Test
    public void testGetWithSpecialI18nPlaceholder() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Date DATE_1970_JAN = new DateTime(1970, 1, 1, 1, 1).toDate();
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        // test fallback to default (english in that case)
        Optional<String> language = Optional.empty();
        Optional<String> result = messages.get("message_with_placeholder_date", language, DATE_1970_JAN);
        Assert.assertEquals("that's a date: Jan 1, 1970", result.get());
        // de as language
        language = Optional.of("de");
        result = messages.get("message_with_placeholder_date", language, DATE_1970_JAN);
        Assert.assertEquals("das ist ein datum: 01.01.1970", result.get());
        // fr as language
        language = Optional.of("fr-FR");
        result = messages.get("message_with_placeholder_date", language, DATE_1970_JAN);
        Assert.assertEquals("c'est la date: 1 janv. 1970", result.get());
        // en as language
        language = Optional.of("en");
        result = messages.get("message_with_placeholder_date", language, DATE_1970_JAN);
        Assert.assertEquals("that's a date: Jan 1, 1970", result.get());
    }

    @Test
    public void testGetAllWithLanguage() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Messages lang = new MessagesImpl(ninjaProperties, null);
        // US locale testing:
        Map<Object, Object> map = lang.getAll(Optional.of("en-US"));
        Assert.assertEquals(5, map.keySet().size());
        Assert.assertTrue(map.containsKey("language"));
        Assert.assertTrue(map.containsKey("message_with_placeholder"));
        Assert.assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        Assert.assertTrue(map.containsKey("a_propert_with_commas"));
        Assert.assertEquals("english", map.get("language"));
        // GERMAN locale testing:
        map = lang.getAll(Optional.of("de"));
        Assert.assertEquals(5, map.keySet().size());
        Assert.assertTrue(map.containsKey("language"));
        Assert.assertTrue(map.containsKey("message_with_placeholder"));
        Assert.assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        Assert.assertTrue(map.containsKey("a_propert_with_commas"));
        Assert.assertEquals("deutsch", map.get("language"));
        Assert.assertEquals("Tor???? - das ist der platzhalter: {0}", map.get("message_with_placeholder"));
    }

    @Test
    public void testGetAllWithContextAndResult() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        result = Results.ok();
        Mockito.when(context.getAcceptLanguage()).thenReturn("en-US");
        // US locale testing:
        Map<Object, Object> map = messages.getAll(context, Optional.of(result));
        Assert.assertEquals(5, map.keySet().size());
        Assert.assertTrue(map.containsKey("language"));
        Assert.assertTrue(map.containsKey("message_with_placeholder"));
        Assert.assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        Assert.assertTrue(map.containsKey("a_propert_with_commas"));
        Assert.assertEquals("english", map.get("language"));
        // GERMAN locale testing:
        lang.setLanguage("de", result);
        map = messages.getAll(context, Optional.of(result));
        Assert.assertEquals(5, map.keySet().size());
        Assert.assertTrue(map.containsKey("language"));
        Assert.assertTrue(map.containsKey("message_with_placeholder"));
        Assert.assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        Assert.assertTrue(map.containsKey("a_propert_with_commas"));
        Assert.assertEquals("deutsch", map.get("language"));
        Assert.assertEquals("Tor???? - das ist der platzhalter: {0}", map.get("message_with_placeholder"));
        // reset result and set context cookie:
        result = Results.ok();
        Mockito.when(context.getCookie(Mockito.anyString())).thenReturn(Cookie.builder("name", "en").build());
        map = messages.getAll(context, Optional.of(result));
        Assert.assertEquals(5, map.keySet().size());
        Assert.assertTrue(map.containsKey("language"));
        Assert.assertTrue(map.containsKey("message_with_placeholder"));
        Assert.assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        Assert.assertTrue(map.containsKey("a_propert_with_commas"));
        Assert.assertEquals("english", map.get("language"));
    }

    @Test
    public void testCorrectParsingOfDelimitersInPropertiesFiles() {
        Mockito.when(ninjaProperties.getStringArray(applicationLanguages)).thenReturn(new String[]{ "en", "de", "fr-FR" });
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        Assert.assertEquals("prop1, prop2, prop3", messages.get("a_propert_with_commas", Optional.of("en-US")).get());
    }
}


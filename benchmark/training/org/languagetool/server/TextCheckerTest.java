/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2016 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.server;


import com.auth0.jwt.exceptions.SignatureVerificationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.markup.AnnotatedTextBuilder;


public class TextCheckerTest {
    private final String english = "This is clearly an English text, should be easy to detect.";

    private final TextChecker checker = new V2TextChecker(new HTTPServerConfig(), false, null, new RequestCounter());

    private final String unsupportedCzech = "V sou?asn? dob? je ozna?en?m Linux m?n?no nejen j?dro opera?n?ho syst?mu, ale zahrnuje do n?j t?? ve?ker? programov? vybaven?";

    @Test
    public void testJSONP() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("text", "not used");
        params.put("language", "en");
        params.put("callback", "myCallback");
        HTTPServerConfig config1 = new HTTPServerConfig(HTTPTools.getDefaultPort());
        TextChecker checker = new V2TextChecker(config1, false, null, new RequestCounter());
        FakeHttpExchange httpExchange = new FakeHttpExchange();
        checker.checkText(new AnnotatedTextBuilder().addText("some random text").build(), httpExchange, params, null, null);
        Assert.assertTrue(httpExchange.getOutput().startsWith("myCallback("));
        Assert.assertTrue(httpExchange.getOutput().endsWith(");"));
    }

    @Test
    public void testMaxTextLength() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("text", "not used");
        params.put("language", "en");
        HTTPServerConfig config1 = new HTTPServerConfig(HTTPTools.getDefaultPort());
        config1.setMaxTextLength(10);
        TextChecker checker = new V2TextChecker(config1, false, null, new RequestCounter());
        try {
            checker.checkText(new AnnotatedTextBuilder().addText("longer than 10 chars").build(), new FakeHttpExchange(), params, null, null);
            Assert.fail();
        } catch (TextTooLongException ignore) {
        }
        try {
            params.put("token", "invalid");
            checker.checkText(new AnnotatedTextBuilder().addText("longer than 10 chars").build(), new FakeHttpExchange(), params, null, null);
            Assert.fail();
        } catch (RuntimeException ignore) {
        }
        String validToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvbGFuZ3VhZ2V0b29scGx1cy5jb20iLCJpYXQiOjE1MDQ4NTY4NTQsInVpZCI6MSwibWF4VGV4dExlbmd0aCI6MTAwfQ._-8qpa99IWJiP_Zx5o-yVU11neW8lrxmLym1DdwPtIc";
        try {
            params.put("token", validToken);
            checker.checkText(new AnnotatedTextBuilder().addText("longer than 10 chars").build(), new FakeHttpExchange(), params, null, null);
            Assert.fail();
        } catch (RuntimeException expected) {
            // server not configured to accept tokens
        }
        try {
            config1.secretTokenKey = "foobar";
            checker.checkText(new AnnotatedTextBuilder().addText("longer than 10 chars").build(), new FakeHttpExchange(), params, null, null);
            Assert.fail();
        } catch (SignatureVerificationException ignore) {
        }
        config1.secretTokenKey = "foobar";
        // see test below for how to create a token:
        params.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vZm9vYmFyIiwiaWF0IjoxNTA0ODU3NzAzLCJtYXhUZXh0TGVuZ3RoIjozMH0.2ijjMEhSyJPEc0fv91UtOJdQe8CMfo2U9dbXgHOkzr0");
        checker.checkText(new AnnotatedTextBuilder().addText("longer than 10 chars").build(), new FakeHttpExchange(), params, null, null);
        try {
            config1.secretTokenKey = "foobar";
            checker.checkText(new AnnotatedTextBuilder().addText("now it's even longer than 30 chars").build(), new FakeHttpExchange(), params, null, null);
            Assert.fail();
        } catch (TextTooLongException expected) {
            // too long even with claim from token, which allows 30 characters
        }
    }

    @Test
    public void testInvalidAltLanguages() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("text", "not used");
        params.put("language", "en");
        HTTPServerConfig config1 = new HTTPServerConfig(HTTPTools.getDefaultPort());
        TextChecker checker = new V2TextChecker(config1, false, null, new RequestCounter());
        try {
            params.put("altLanguages", "en");
            checker.checkText(new AnnotatedTextBuilder().addText("something").build(), new FakeHttpExchange(), params, null, null);
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            params.put("altLanguages", "xy");
            checker.checkText(new AnnotatedTextBuilder().addText("something").build(), new FakeHttpExchange(), params, null, null);
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
        params.put("language", "en");
        params.put("altLanguages", "de-DE");
        checker.checkText(new AnnotatedTextBuilder().addText("something").build(), new FakeHttpExchange(), params, null, null);
        params.put("language", "en-US");
        params.put("altLanguages", "en-US");// not useful, but not forbidden

        checker.checkText(new AnnotatedTextBuilder().addText("something").build(), new FakeHttpExchange(), params, null, null);
    }

    @Test
    public void testDetectLanguageOfString() {
        List<String> e = Collections.emptyList();
        List<String> preferredLangs = Collections.emptyList();
        Assert.assertThat(checker.detectLanguageOfString("", "en", Arrays.asList("en-GB"), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("en-GB"));
        // fallback language does not work anymore, now detected as ca-ES, ensure that at least the probability is low
        // assertThat(checker.detectLanguageOfString("X", "en", Arrays.asList("en-GB"), e)
        // .getDetectedLanguage().getShortCodeWithCountryAndVariant(), is("en-GB"));
        // assertThat(checker.detectLanguageOfString("X", "en", Arrays.asList("en-ZA"), e)
        // .getDetectedLanguage().getShortCodeWithCountryAndVariant(), is("en-ZA"));
        Assert.assertThat(checker.detectLanguageOfString("X", "en", Arrays.asList("en-GB"), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("ca-ES"));
        Assert.assertTrue(((checker.detectLanguageOfString("X", "en", Arrays.asList("en-GB"), e, preferredLangs).getDetectionConfidence()) < 0.5));
        Assert.assertThat(checker.detectLanguageOfString(english, "de", Arrays.asList("en-GB", "de-AT"), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("en-GB"));
        Assert.assertThat(checker.detectLanguageOfString(english, "de", Arrays.asList(), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("en-US"));
        Assert.assertThat(checker.detectLanguageOfString(english, "de", Arrays.asList("de-AT", "en-ZA"), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("en-ZA"));
        String german = "Das hier ist klar ein deutscher Text, sollte gut zu erkennen sein.";
        Assert.assertThat(checker.detectLanguageOfString(german, "fr", Arrays.asList("de-AT", "en-ZA"), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("de-AT"));
        Assert.assertThat(checker.detectLanguageOfString(german, "fr", Arrays.asList("de-at", "en-ZA"), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("de-AT"));
        Assert.assertThat(checker.detectLanguageOfString(german, "fr", Arrays.asList(), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("de-DE"));
        Assert.assertThat(checker.detectLanguageOfString(unsupportedCzech, "en", Arrays.asList(), e, preferredLangs).getDetectedLanguage().getShortCodeWithCountryAndVariant(), Is.is("sk-SK"));// misdetected because it's not supported

    }

    @Test(expected = RuntimeException.class)
    public void testInvalidPreferredVariant() {
        checker.detectLanguageOfString(english, "de", Arrays.asList("en"), Collections.emptyList(), Collections.emptyList());// that's not a variant

    }

    @Test(expected = RuntimeException.class)
    public void testInvalidPreferredVariant2() {
        checker.detectLanguageOfString(english, "de", Arrays.asList("en-YY"), Collections.emptyList(), Collections.emptyList());// variant doesn't exist

    }
}


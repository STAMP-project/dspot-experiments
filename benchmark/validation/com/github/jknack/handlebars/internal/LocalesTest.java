package com.github.jknack.handlebars.internal;


import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link Locales}.
 */
public class LocalesTest {
    private static List<Locale> COMMON_LOCALES = Arrays.asList(Locale.CANADA, Locale.CANADA_FRENCH, Locale.CHINA, Locale.ENGLISH, Locale.GERMANY, Locale.FRANCE);

    @Test
    public void testUnderscore() {
        for (Locale l : LocalesTest.COMMON_LOCALES) {
            // l.toString() returns format de_DE
            Assert.assertEquals(l, Locales.fromString(l.toString()));
        }
    }

    @Test
    public void testHyphen() {
        for (Locale l : LocalesTest.COMMON_LOCALES) {
            // l.toLanguageTag() returns format de-DE
            Assert.assertEquals(l, Locales.fromString(l.toLanguageTag()));
        }
    }

    @Test
    public void testNull() {
        Assert.assertNull(Locales.fromString(null));
    }
}


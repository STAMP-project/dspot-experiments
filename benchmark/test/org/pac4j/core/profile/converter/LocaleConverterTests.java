package org.pac4j.core.profile.converter;


import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the {@link org.pac4j.core.profile.converter.LocaleConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class LocaleConverterTests {
    private final LocaleConverter converter = new LocaleConverter();

    @Test
    public void testNull() {
        Assert.assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        Assert.assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testLanguage() {
        final Locale locale = this.converter.convert("fr");
        Assert.assertEquals("fr", locale.getLanguage());
    }

    @Test
    public void testLanguageCountry() {
        final Locale locale = this.converter.convert(Locale.FRANCE.toString());
        Assert.assertEquals(Locale.FRANCE.getLanguage(), locale.getLanguage());
        Assert.assertEquals(Locale.FRANCE.getCountry(), locale.getCountry());
    }

    @Test
    public void testBadLocale() {
        Assert.assertNull(this.converter.convert("1_2_3"));
    }
}


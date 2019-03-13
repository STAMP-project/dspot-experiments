package dev.morphia.converters;


import dev.morphia.TestBase;
import java.util.Locale;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LocaleConverterTest extends TestBase {
    @Test
    public void shouldEncodeAndDecodeBuiltInLocale() throws Exception {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = Locale.CANADA_FRENCH;
        // when
        Locale decodedLocale = ((Locale) (converter.decode(Locale.class, converter.encode(expectedLocale))));
        // then
        Assert.assertThat(decodedLocale, CoreMatchers.is(expectedLocale));
    }

    @Test
    public void shouldEncodeAndDecodeCountryOnlyLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("", "FI");
        // when
        Locale decodedLocale = ((Locale) (converter.decode(Locale.class, converter.encode(expectedLocale))));
        // then
        Assert.assertThat(decodedLocale, CoreMatchers.is(expectedLocale));
    }

    @Test
    public void shouldEncodeAndDecodeCustomLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("de", "DE", "bavarian");
        // when
        Locale decodedLocale = ((Locale) (converter.decode(Locale.class, converter.encode(expectedLocale))));
        // then
        Assert.assertThat(decodedLocale, CoreMatchers.is(expectedLocale));
        Assert.assertThat(decodedLocale.getLanguage(), CoreMatchers.is("de"));
        Assert.assertThat(decodedLocale.getCountry(), CoreMatchers.is("DE"));
        Assert.assertThat(decodedLocale.getVariant(), CoreMatchers.is("bavarian"));
    }

    @Test
    public void shouldEncodeAndDecodeNoCountryLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("fi", "", "VAR");
        // when
        Locale decodedLocale = ((Locale) (converter.decode(Locale.class, converter.encode(expectedLocale))));
        // then
        Assert.assertThat(decodedLocale, CoreMatchers.is(expectedLocale));
    }

    @Test
    public void shouldEncodeAndDecodeNoLanguageLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("", "FI", "VAR");
        // when
        Locale decodedLocale = ((Locale) (converter.decode(Locale.class, converter.encode(expectedLocale))));
        // then
        Assert.assertThat(decodedLocale, CoreMatchers.is(expectedLocale));
    }

    @Test
    public void shouldEncodeAndDecodeSpecialVariantLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("fi", "FI", "VAR_SPECIAL");
        // when
        Locale decodedLocale = ((Locale) (converter.decode(Locale.class, converter.encode(expectedLocale))));
        // then
        Assert.assertThat(decodedLocale, CoreMatchers.is(expectedLocale));
    }
}

